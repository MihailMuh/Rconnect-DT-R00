package com.mihalis.dtr00.activity;

import static com.mihalis.dtr00.Strings.ADDRESS;
import static com.mihalis.dtr00.Strings.APPLY;
import static com.mihalis.dtr00.Strings.INCORRECT_USER_PASSWD;
import static com.mihalis.dtr00.Strings.INPUT_IP;
import static com.mihalis.dtr00.Strings.LOGIN;
import static com.mihalis.dtr00.Strings.PASSWORD;
import static com.mihalis.dtr00.Strings.REMEMBER_ME;
import static com.mihalis.dtr00.Strings.UNEXPECTED_ERROR;
import static com.mihalis.dtr00.Strings.USER_NAME;
import static com.mihalis.dtr00.services.ClientServer.IP;
import static com.mihalis.dtr00.services.Service.print;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.mihalis.dtr00.ClickListener;
import com.mihalis.dtr00.Constants;
import com.mihalis.dtr00.R;
import com.mihalis.dtr00.services.ClientServer;
import com.mihalis.dtr00.services.Service;

import org.json.JSONObject;

public class RegisterActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (Service.numRegisterActivity >= 2) {
            finishAffinity();
            return;
        }
        Service.numRegisterActivity++;

        EditText editIP = findViewById(R.id.input_ip);
        editIP.setHint(ADDRESS);

        CheckBox checkBox = findViewById(R.id.checkBox);
        checkBox.setText(REMEMBER_ME);

        Button button = findViewById(R.id.button_save);
        button.setText(APPLY);
        button.setOnClickListener((ClickListener) () ->
                checkWIFI(() -> {
                    IP = editIP.getText().toString().replace(" ", "");
                    String login = ((EditText) findViewById(R.id.input_user_name)).getText().toString();
                    String password = ((EditText) findViewById(R.id.input_password)).getText().toString();

                    if (IP.equals("admin") && login.equals("admin") && password.equals("admin")) {
                        enter(checkBox.isChecked());
                        return;
                    }

                    int answer = ClientServer.login(login, password);
                    int answerIncorrect = ClientServer.login(login, getRandomPassword());

                    if (answerIncorrect == 44_44_44 || answer == 44_44_44) {
                        toast(UNEXPECTED_ERROR);
                        return;
                    }
                    if (answer > answerIncorrect) {
                        enter(checkBox.isChecked());
                        return;
                    }
                    toast(INCORRECT_USER_PASSWD);
                }));

        ((TextView) findViewById(R.id.text_auth)).setText(LOGIN);
        ((TextView) findViewById(R.id.text_enter_ip)).setText(INPUT_IP);
        ((TextView) findViewById(R.id.text_enter_user_name)).setText(USER_NAME);
        ((TextView) findViewById(R.id.text_enter_password)).setText(PASSWORD);
    }

    private void enter(boolean remember) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("IP", IP);
            jsonObject.put("remember", remember);
            Service.writeToFile("IP.json", jsonObject);

            MainActivity.afterRegister = true;
            runOnUiThread(this::finish);
        } catch (Exception e) {
            print("Error in JSON while register " + e);
        }
    }

    private String getRandomPassword() {
        var stringBuilder = new StringBuilder(20);
        int symLen = Constants.symbols.length() - 1;

        for (int i = 0; i < 20; i++) {
            stringBuilder.append(Constants.symbols.charAt(randInt(0, symLen)));
        }
        return stringBuilder.toString();
    }

    private int randInt(int min, int max) {
        return (int) ((Math.random() * ((max - min) + 1)) + min);
    }
}
