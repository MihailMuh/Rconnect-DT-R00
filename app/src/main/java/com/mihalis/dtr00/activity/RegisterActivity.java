package com.mihalis.dtr00.activity;

import static com.mihalis.dtr00.constants.Strings.ADDRESS;
import static com.mihalis.dtr00.constants.Strings.APPLY;
import static com.mihalis.dtr00.constants.Strings.INCORRECT_USER_PASSWD;
import static com.mihalis.dtr00.constants.Strings.INPUT_IP;
import static com.mihalis.dtr00.constants.Strings.LOGIN;
import static com.mihalis.dtr00.constants.Strings.PASSWORD;
import static com.mihalis.dtr00.constants.Strings.RELAY;
import static com.mihalis.dtr00.constants.Strings.REMEMBER_ME;
import static com.mihalis.dtr00.constants.Strings.UNEXPECTED_ERROR;
import static com.mihalis.dtr00.constants.Strings.USER_NAME;
import static com.mihalis.dtr00.services.ClientServer.IP;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.mihalis.dtr00.utils.ArrayFiller;
import com.mihalis.dtr00.utils.ClickListener;
import com.mihalis.dtr00.constants.Constants;
import com.mihalis.dtr00.R;
import com.mihalis.dtr00.services.ClientServer;
import com.mihalis.dtr00.utils.JSON;

import java.util.Arrays;

public class RegisterActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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
        var json = new JSON();
        json.put("remember", remember);
        json.put("settings", getPrimarySettings());
        json.put("deviceName", IP);

        updateJSONDevices(getJSONDevices().put(IP, json));

        MainActivity.afterRegister = true;
        runOnUiThread(this::finish);
    }

    private JSON getPrimarySettings() {
        var jsonSettings = new JSON();
        var delays = new int[8];
        var disabledMask = new boolean[8][4];
        var names = new String[8];

        Arrays.fill(delays, 5);
        ArrayFiller.fill(disabledMask, true);
        ArrayFiller.fill(names, (int index) -> RELAY + " " + (index + 1));

        jsonSettings.put("name", JSON.createJSONArray(names));
        jsonSettings.put("delay", JSON.createJSONArray(delays));
        jsonSettings.put("disabledMask", JSON.createJSONArray(disabledMask));

        return jsonSettings;
    }

    private String getRandomPassword() {
        var stringBuilder = new StringBuilder(20);
        int numSymbols = Constants.symbols.length();

        for (int i = 0; i < 20; i++) {
            stringBuilder.append(Constants.symbols.charAt(randInt(numSymbols)));
        }
        return stringBuilder.toString();
    }

    private int randInt(int max) {
        return (int) (Math.random() * max);
    }
}
