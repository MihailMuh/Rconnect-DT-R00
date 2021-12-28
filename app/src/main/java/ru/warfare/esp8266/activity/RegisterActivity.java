package ru.warfare.esp8266.activity;

import static ru.warfare.esp8266.Strings.ADDRESS;
import static ru.warfare.esp8266.Strings.APPLY;
import static ru.warfare.esp8266.Strings.INCORRECT_IP;
import static ru.warfare.esp8266.Strings.INCORRECT_USER_PASSWD;
import static ru.warfare.esp8266.Strings.INPUT_IP;
import static ru.warfare.esp8266.Strings.LOGIN;
import static ru.warfare.esp8266.Strings.PASSWORD;
import static ru.warfare.esp8266.Strings.REMEMBER_ME;
import static ru.warfare.esp8266.Strings.USER_NAME;
import static ru.warfare.esp8266.services.ClientServer.IP;
import static ru.warfare.esp8266.services.Service.print;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import ru.warfare.esp8266.ButtonListener;
import ru.warfare.esp8266.R;
import ru.warfare.esp8266.services.ClientServer;
import ru.warfare.esp8266.services.Service;

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
        button.setOnClickListener((ButtonListener) () ->
                checkWIFI(() -> {
                    IP = editIP.getText().toString();

                    switch (ClientServer.login(((EditText) findViewById(R.id.input_user_name)).getText().toString(),
                            ((EditText) findViewById(R.id.input_password)).getText().toString())) {
                        case 0:
                            try {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("IP", IP);
                                jsonObject.put("remember", checkBox.isChecked());
                                Service.writeToFile("IP.json", jsonObject);
                            } catch (Exception e) {
                                print("Error in JSON while register " + e);
                            }
                            runOnUiThread(this::finish);
                            break;
                        case -7:
                            makeToast(INCORRECT_USER_PASSWD);
                            break;
                        case 404:
                            makeToast(INCORRECT_IP);
                            break;
                    }
                }));

        ((TextView) findViewById(R.id.text_auth)).setText(LOGIN);
        ((TextView) findViewById(R.id.text_enter_ip)).setText(INPUT_IP);
        ((TextView) findViewById(R.id.text_enter_user_name)).setText(USER_NAME);
        ((TextView) findViewById(R.id.text_enter_password)).setText(PASSWORD);
    }
}
