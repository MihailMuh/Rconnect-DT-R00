package ru.warfare.esp8266.activity;

import static ru.warfare.esp8266.services.ClientServer.IP;
import static ru.warfare.esp8266.services.Service.activity;
import static ru.warfare.esp8266.services.Service.post;
import static ru.warfare.esp8266.services.Service.print;
import static ru.warfare.esp8266.services.Service.readFromFile;
import static ru.warfare.esp8266.services.Service.writeToFile;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import ru.warfare.esp8266.ButtonListener;
import ru.warfare.esp8266.R;
import ru.warfare.esp8266.services.ClientServer;

public class SettingsActivity extends BaseActivity {
    private final EditText[] editRelays = new EditText[8];
    private final EditText[] editDelays = new EditText[8];
    private final Button[] buttons = new Button[8];

    private JSONObject jsonSettings = new JSONObject();
    private final int[] delays = new int[8];
    private final boolean[] show = new boolean[8];
    private final String[] names = new String[8];

    private String s_current;
    private String s_save;
    private String s_wait;
    private String s_delay;
    private String s_show, s_hide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        try {
            jsonSettings = new JSONObject(readFromFile("SETTINGS.json"));

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

        confirmLanguage();

        EditText editText = findViewById(R.id.edit_ip);
        editText.setText(IP);

        ((TextView) findViewById(R.id.current_ip_text)).setText(s_current);

        for (int i = 0; i < editDelays.length; i++) {
            final int finalI = i;

            ((TextView) findViewById("text_delay", i)).setText(s_delay);

            editRelays[i] = findViewById("edit_relay", i);
            editRelays[i].setText(names[i]);

            editDelays[i] = findViewById("edit_delay", i);
            editDelays[i].setText(String.valueOf(delays[i]));

            buttons[i] = findViewById("button_hide", i);
            buttons[i].setOnClickListener((ButtonListener) () -> {
                if (buttons[finalI].getText().equals(s_show)) {
                    buttons[finalI].setText(s_hide);
                    show[finalI] = true;
                    color(true, finalI);
                } else {
                    buttons[finalI].setText(s_show);
                    show[finalI] = false;
                    color(false, finalI);
                }
            });
            text(i, show[i]);
        }

        Button btnSettings = findViewById(R.id.button_save_settings);
        btnSettings.setText(s_save);
        btnSettings.setOnClickListener((ButtonListener) () -> {
            if (isOnline()) {
                wrapperToast(s_wait, 500);

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
                    } catch (Exception e) {
                        print(e);
                    }

                    if (ClientServer.getRelaysStatus().length() > 1) {
                        runOnUiThread(this::finish);

                        writeToFile("IP.txt", IP);
                        writeToFile("SETTINGS.json", jsonSettings);

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

    private void text(int i, boolean show) {
        if (show) {
            buttons[i].setText(s_hide);
        } else {
            buttons[i].setText(s_show);
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

    public void confirmLanguage() {
        String[] strings = getResources().getStringArray(R.array.ru);

        s_current = strings[13] + " IP:";
        s_save = strings[9];
        s_wait = strings[20] + "...";
        s_delay = strings[23] + ":";
        s_hide = strings[24];
        s_show = strings[25];
    }

    @Override
    public void onBackPressed() {
        finish();

        post(() -> IP = readFromFile("IP.txt"));
    }
}
