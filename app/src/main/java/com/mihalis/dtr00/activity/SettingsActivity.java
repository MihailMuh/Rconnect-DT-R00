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
import static com.mihalis.dtr00.services.Service.post;
import static com.mihalis.dtr00.services.Service.print;
import static com.mihalis.dtr00.services.Service.readFromFile;
import static com.mihalis.dtr00.services.Service.writeToFile;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.mihalis.dtr00.ClickListener;
import com.mihalis.dtr00.R;
import com.mihalis.dtr00.services.ClientServer;
import com.mihalis.dtr00.services.Service;

import org.json.JSONArray;
import org.json.JSONObject;

public class SettingsActivity extends BaseActivity {
    private final EditText[] editRelays = new EditText[8];
    private final EditText[] editDelays = new EditText[8];
    private final Button[] buttons = new Button[8];

    private final int[] delays = new int[8];
    private final boolean[] show = new boolean[8];
    private final String[] names = new String[8];

    private JSONObject jsonSettings;
    private JSONObject jsonIP;

    private final String oldIP = IP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        try {
            jsonSettings = new JSONObject(readFromFile("SETTINGS.json"));
            jsonIP = new JSONObject(readFromFile("IP.json"));

            JSONArray nameArray = jsonSettings.getJSONArray("name");
            JSONArray showArray = jsonSettings.getJSONArray("show");
            JSONArray delayArray = jsonSettings.getJSONArray("delay");

            for (int i = 0; i < 8; i++) {
                names[i] = nameArray.getString(i);
                delays[i] = delayArray.getInt(i);
                show[i] = showArray.getBoolean(i);
            }
        } catch (Exception e) {
            print(e);
        }

        EditText editText = findViewById(R.id.edit_ip);
        editText.setText(IP);

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
                if (buttons[finalI].getText().equals(SHOW)) {
                    buttons[finalI].setText(HIDE);
                    show[finalI] = true;
                    color(true, finalI);
                } else {
                    buttons[finalI].setText(SHOW);
                    show[finalI] = false;
                    color(false, finalI);
                }
            });
            text(i, show[i]);
        }

        Button btnSettings = findViewById(R.id.button_save_settings);
        btnSettings.setText(SAVE);
        btnSettings.setOnClickListener((ClickListener) () -> checkWIFI(() -> {
            toast(WAIT, 500);

            post(() -> {
                IP = editText.getText().toString();

                for (int i = 0; i < 8; i++) {
                    delays[i] = Integer.parseInt(editDelays[i].getText().toString());
                    names[i] = editRelays[i].getText().toString();
                }
                try {
                    jsonSettings.put("name", new JSONArray(names));
                    jsonSettings.put("delay", new JSONArray(delays));
                    jsonSettings.put("show", new JSONArray(show));

                    jsonIP.put("IP", IP);
                } catch (Exception e) {
                    print(e);
                }

                if (ClientServer.getRelaysStatus().length() > 1) {
                    runOnUiThread(this::finish);

                    writeToFile("IP.json", jsonIP);
                    writeToFile("SETTINGS.json", jsonSettings);

                    toast(SUCCESSFULLY_SAVED);
                } else {
                    toast(INCORRECT_IP);
                }
            });
        }));
    }

    private void text(int i, boolean show) {
        if (show) {
            buttons[i].setText(HIDE);
        } else {
            buttons[i].setText(SHOW);
        }

        color(show, i);
    }

    private void color(boolean green, int i) {
        if (green) {
            buttons[i].setBackgroundColor(Color.parseColor("#004524"));
            buttons[i].setTextColor(ContextCompat.getColor(this, R.color.white));
        } else {
            buttons[i].setBackgroundColor(Color.parseColor("#DCDCDC"));
            buttons[i].setTextColor(ContextCompat.getColor(this, R.color.black));
        }
    }

    @Override
    public void onBackPressed() {
        IP = oldIP;
        finish();
    }
}
