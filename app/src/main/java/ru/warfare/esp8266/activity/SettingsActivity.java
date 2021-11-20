package ru.warfare.esp8266.activity;

import static ru.warfare.esp8266.services.ClientServer.IP;
import static ru.warfare.esp8266.services.Service.activity;
import static ru.warfare.esp8266.services.Service.post;
import static ru.warfare.esp8266.services.Service.readFromFile;
import static ru.warfare.esp8266.services.Service.vibrate;
import static ru.warfare.esp8266.services.Service.writeToFile;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import ru.warfare.esp8266.R;
import ru.warfare.esp8266.services.ClientServer;

public class SettingsActivity extends BaseActivity {
    private String s_current;
    private String s_save;
    private String s_wait;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        confirmLanguage();

        EditText editText = findViewById(R.id.edit_ip);
        editText.setText(readFromFile("IP"));

        ((TextView) findViewById(R.id.current_ip_text)).setText(s_current);

        Button btnSettings = findViewById(R.id.button_save_settings);
        btnSettings.setText(s_save);
        btnSettings.setOnClickListener(view -> {
            if (isOnline()) {
                wrapperToast(s_wait, 600);
                IP = editText.getText().toString();
                post(() -> {
                    vibrate(55);
                    if (ClientServer.getRelaysStatus().length() > 1) {
                        runOnUiThread(this::finish);

                        writeToFile("IP", IP);

                        makeToast(activity.s_saved);
                    } else {
                        makeToast(activity.s_incorrect_IP);
                    }
                });
            } else {
                noWiFi(activity.s_no_internet, activity.s_exit, activity.s_i_enable_wifi);
            }
        });
    }

    public void confirmLanguage() {
        String[] strings = getResources().getStringArray(R.array.ru);

        s_current = strings[13] + " IP:";
        s_save = strings[9];
        s_wait = strings[20] + "...";
    }

    @Override
    public void onBackPressed() {
        finish();

        post(() -> IP = readFromFile("IP"));
    }
}
