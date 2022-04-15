package com.mihalis.dtr00.activity;

import static com.mihalis.dtr00.Strings.CURRENT_IP;
import static com.mihalis.dtr00.Strings.DELAY;
import static com.mihalis.dtr00.Strings.HIDE;
import static com.mihalis.dtr00.Strings.INCORRECT_IP;
import static com.mihalis.dtr00.Strings.SAVE;
import static com.mihalis.dtr00.Strings.SHOW;
import static com.mihalis.dtr00.Strings.SUCCESSFULLY_SAVED;
import static com.mihalis.dtr00.Strings.WAIT;
import static com.mihalis.dtr00.services.ClientServer.IP;
import static com.mihalis.dtr00.services.JSON.createJSONArray;
import static com.mihalis.dtr00.services.Service.post;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mihalis.dtr00.ClickListener;
import com.mihalis.dtr00.R;
import com.mihalis.dtr00.services.ClientServer;
import com.mihalis.dtr00.services.JSON;

public class SettingsActivity extends BaseActivity {
    private final EditText[] editRelays = new EditText[8];
    private final EditText[] editDelays = new EditText[8];
    private final Button[] buttons = new Button[8];

    private final int[] delays = new int[8];
    private final boolean[] show = new boolean[8];
    private final String[] names = new String[8];

    private JSON jsonSettings, jsonDeviceData, jsonDevices;

    private final String oldIP = IP.concat("");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        parseALLJSONes();

        EditText editIP = findViewById(R.id.edit_ip);
        editIP.setText(IP);

        ((TextView) findViewById(R.id.current_ip_text)).setText(CURRENT_IP);

        for (int i = 0; i < editDelays.length; i++) {
            final int finalI = i;

            ((TextView) findViewById("text_delay", i)).setText(DELAY);

            editRelays[i] = findViewById("edit_relay", i);
            editRelays[i].setText(names[i]);

            editDelays[i] = findViewById("edit_delay", i);
            editDelays[i].setText(String.valueOf(delays[i]));

            buttons[i] = findViewById("button_hide", i);
            buttons[i].setOnClickListener((ClickListener) () -> {
                Button button = buttons[finalI];
                boolean enabled = button.getText().equals(SHOW);

                if (enabled) {
                    button.setText(HIDE);
                } else {
                    button.setText(SHOW);
                }
                show[finalI] = enabled;
                setButtonState(button, enabled);
            });
            text(i, show[i]);
        }

        Button btnSettings = findViewById(R.id.button_save_settings);
        btnSettings.setText(SAVE);
        btnSettings.setOnClickListener((ClickListener) () -> checkWIFI(() -> {
            toast(WAIT, 500);

            post(() -> {
                IP = editIP.getText().toString();

                for (int i = 0; i < 8; i++) {
                    delays[i] = Integer.parseInt(editDelays[i].getText().toString());
                    names[i] = editRelays[i].getText().toString();
                }
                jsonSettings.put("name", createJSONArray(names));
                jsonSettings.put("delay", createJSONArray(delays));
                jsonSettings.put("show", createJSONArray(show));
                jsonDeviceData.put("settings", jsonSettings);

                if (ClientServer.getRelaysStatus().length() > 1) {
                    runOnUiThread(this::finish);

                    if (!oldIP.equals(IP)) {
                        jsonDevices.remove(oldIP);
                    }
                    updateJSONDevices(jsonDevices.put(IP, jsonDeviceData));

                    toast(SUCCESSFULLY_SAVED);
                } else {
                    toast(INCORRECT_IP);
                }
            });
        }));
    }

    private void parseALLJSONes() {
        jsonDevices = getJSONDevices();
        jsonDeviceData = jsonDevices.getJSON(IP);
        jsonSettings = jsonDeviceData.getJSON("settings");

        var nameArray = jsonSettings.getJSONArray("name");
        var showArray = jsonSettings.getJSONArray("show");
        var delayArray = jsonSettings.getJSONArray("delay");

        for (int i = 0; i < 8; i++) {
            names[i] = nameArray.optString(i);
            delays[i] = delayArray.optInt(i);
            show[i] = showArray.optBoolean(i);
        }
    }

    private void text(int i, boolean show) {
        if (show) {
            buttons[i].setText(HIDE);
        } else {
            buttons[i].setText(SHOW);
        }

        setButtonState(buttons[i], show);
    }

    @Override
    public void onBackPressed() {
        IP = oldIP;
        finish();
    }
}
