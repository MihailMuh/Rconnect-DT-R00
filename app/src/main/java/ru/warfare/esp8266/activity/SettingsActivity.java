package ru.warfare.esp8266.activity;

import static ru.warfare.esp8266.services.ClientServer.IP;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import ru.warfare.esp8266.services.Clerk;
import ru.warfare.esp8266.services.ClientServer;
import ru.warfare.esp8266.R;
import ru.warfare.esp8266.services.ThreadPool;
import ru.warfare.esp8266.services.Vibrator;

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
        editText.setText(Clerk.recoveryIP());

        ((TextView) findViewById(R.id.current_ip_text)).setText(s_current);

        Button btnSettings = findViewById(R.id.button_save_settings);
        btnSettings.setText(s_save);
        btnSettings.setOnClickListener(view -> {
            wrapperToast(s_wait, 700);
            IP = editText.getText().toString();
            ThreadPool.post(() -> {
                Vibrator.vibrate(70);
                try {
                    ClientServer.getStatistics();

                    runOnUiThread(this::finish);

                    Clerk.saveIP(IP);

                    makeToast(getIntent().getStringExtra("Successfully saved"));
                } catch (Exception e) {
                    makeToast(getIntent().getStringExtra("Incorrect IP"));
                }
            });
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

        ThreadPool.post(() -> IP = Clerk.recoveryIP());
    }
}
