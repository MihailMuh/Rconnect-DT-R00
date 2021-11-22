package ru.warfare.esp8266.activity;

import static ru.warfare.esp8266.Constants.DELAY;
import static ru.warfare.esp8266.Constants.PRESS;
import static ru.warfare.esp8266.services.ClientServer.IP;
import static ru.warfare.esp8266.services.ClientServer.postToServer;
import static ru.warfare.esp8266.services.Service.post;
import static ru.warfare.esp8266.services.Service.print;
import static ru.warfare.esp8266.services.Service.readFromFile;
import static ru.warfare.esp8266.services.Service.sleep;
import static ru.warfare.esp8266.services.Service.writeToFile;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import ru.warfare.esp8266.ButtonListener;
import ru.warfare.esp8266.R;
import ru.warfare.esp8266.services.ClientServer;
import ru.warfare.esp8266.services.Service;

public class MainActivity extends BaseActivity {
    private final Button[] buttonsOnOff = new Button[8];
    private final Button[] buttonsDelay = new Button[8];
    private final ImageView[] imageViews = new ImageView[8];
    private final TextView[] textRelays = new TextView[8];

    private final int[] delays = new int[8];
    private final boolean[] show = new boolean[8];
    private final String[] names = new String[8];

    private Drawable onImg, offImg;

    private Runnable onError;

    String s_relay;
    String s_on, s_off;
    String s_enable, s_disable;
    String s_enable_on_time_1, s_enable_on_time_2;
    String s_exit;
    String s_example;
    String s_save;
    String s_address;
    String s_saved;
    String s_incorrect_IP;
    String s_settings;
    String s_no_internet;
    String s_cant_connect_server;
    String s_i_enable_wifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Service.init(this);

        confirmLanguage();
        post(() -> {
            offImg = AppCompatResources.getDrawable(this, R.drawable.off);
            onImg = AppCompatResources.getDrawable(this, R.drawable.on);

            onError = () -> {
                if (isOnline()) {
                    makeToast(s_cant_connect_server);
                } else {
                    noWiFi();
                }
            };

            IP = readFromFile("IP.txt");
            if (IP == null) {
                runOnUiThread(this::setIP);
            }
        });

        for (int i = 0; i < buttonsOnOff.length; i++) {
            final int finalI = i;

            buttonsDelay[i] = findViewById("buttonDelay", i);
            textRelays[i] = findViewById("relay", i);
            imageViews[i] = findViewById("light", i);

            buttonsOnOff[i] = findViewById("buttonRelay", i);
            buttonsOnOff[i].setOnClickListener((ButtonListener) () -> {
                if (buttonsOnOff[finalI].getText().equals(s_on)) {
                    postToServer(PRESS, finalI, 1, 0, () -> {
                        imageViews[finalI].setImageDrawable(onImg);
                        buttonsOnOff[finalI].setText(s_off);

                        wrapperToast(s_enable, 500);
                    }, onError);
                } else {
                    postToServer(PRESS, finalI, 0, 0, () -> {
                        imageViews[finalI].setImageDrawable(offImg);
                        buttonsOnOff[finalI].setText(s_on);
                        wrapperToast(s_disable, 500);
                    }, onError);
                }
            });

            findViewById("buttonDelay", i).setOnClickListener((ButtonListener) () ->
                    postToServer(DELAY, finalI, 1, delays[finalI], () -> {
                        imageViews[finalI].setImageDrawable(onImg);
                        buttonsOnOff[finalI].setText(s_off);
                        makeToast(s_enable_on_time_1 + delays[finalI] + s_enable_on_time_2);

                        post(() -> {
                            sleep(delays[finalI]);
                            updateRelays();
                        });
                    }, onError));
        }

        Button button = findViewById(R.id.buttonSettings);
        button.setText(s_settings);
        button.setOnClickListener((ButtonListener) () ->
                startActivity(new Intent(this, SettingsActivity.class)));
    }

    private void updateUI() {
        for (int i = 0; i < 8; i++) {
            textRelays[i].setText(names[i]);

            if (!show[i]) {
                textRelays[i].setVisibility(View.GONE);
                buttonsDelay[i].setVisibility(View.GONE);
                imageViews[i].setVisibility(View.GONE);
                buttonsOnOff[i].setVisibility(View.GONE);
            } else {
                textRelays[i].setVisibility(View.VISIBLE);
                buttonsDelay[i].setVisibility(View.VISIBLE);
                imageViews[i].setVisibility(View.VISIBLE);
                buttonsOnOff[i].setVisibility(View.VISIBLE);
                textRelays[i].setText(names[i]);
            }
        }
    }

    private boolean updateRelays() {
        String string = ClientServer.getRelaysStatus();
        final int len = string.length() - 1;

        if (len > 0) {
            runOnUiThread(() -> {
                int j = 0;
                for (int i = 5; i < len; i += 2) {
                    if (show[j]) {
                        if (Integer.parseInt(String.valueOf(string.charAt(i))) == 1) {
                            buttonsOnOff[j].setText(s_off);
                            imageViews[j].setImageDrawable(onImg);
                        } else {
                            buttonsOnOff[j].setText(s_on);
                            imageViews[j].setImageDrawable(offImg);
                        }
                    }
                    j++;
                }
            });
            return true;
        }

        return false;
    }

    public void setIP() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_ip, null);

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(false)
                .create();

        ((TextView) view.findViewById(R.id.text_example)).setText(s_example);

        EditText editText = view.findViewById(R.id.input_ip);
        editText.setHint(s_address);

        Button button = view.findViewById(R.id.button_save);
        button.setText(s_save);
        button.setOnClickListener((ButtonListener) () -> {
            IP = editText.getText().toString();
            post(() -> {
                if (updateRelays()) {
                    alertDialog.dismiss();

                    writeToFile("IP.txt", IP);

                    makeToast(s_saved);
                } else {
                    makeToast(s_incorrect_IP);
                }
            });
        });

        alertDialog.show();
    }

    private void noWiFi() {
        noWiFi(s_no_internet, s_exit, s_i_enable_wifi);
    }

    public void confirmLanguage() {
        String[] strings = getResources().getStringArray(R.array.ru);

        s_relay = strings[0];
        s_on = strings[1];
        s_off = strings[2];
        s_enable = strings[3];
        s_disable = strings[4];
        s_address = "IP " + strings[8];
        s_save = strings[9];
        s_example = strings[10] + ": 192.168.60.60:8000";
        s_exit = strings[11];
        s_incorrect_IP = strings[12] + " " + s_address;
        s_no_internet = strings[14];
        s_saved = strings[15];

        s_enable_on_time_1 = strings[17] + " ";
        s_enable_on_time_2 = " " + strings[18];

        s_settings = strings[19];

        s_cant_connect_server = strings[21];
        s_i_enable_wifi = strings[22];
    }

    @Override
    protected void onResume() {
        super.onResume();

        post(() -> {
            String data = readFromFile("SETTINGS.json");

            try {
                if (data == null) {
                    writeJSONAtFirst();
                } else {
                    JSONObject jsonSettings = new JSONObject(data);

                    JSONArray nameArray = jsonSettings.getJSONArray("name");
                    JSONArray showArray = jsonSettings.getJSONArray("show");
                    JSONArray delayArray = jsonSettings.getJSONArray("delay");

                    for (int i = 0; i < 8; i++) {
                        names[i] = nameArray.getString(i);
                        delays[i] = delayArray.getInt(i);
                        show[i] = showArray.getBoolean(i);
                    }

                    runOnUiThread(() -> {
                        updateUI();

                        if (isOnline()) {
                            post(this::updateRelays);
                        } else {
                            noWiFi();
                        }
                    });
                }
            } catch (Exception e) {
                print("Error working with JSON " + e);
            }
        });
    }

    private void writeJSONAtFirst() throws JSONException {
        JSONObject jsonSettings = new JSONObject();
        StringBuilder stringBuilder = new StringBuilder();

        Arrays.fill(delays, 5);
        Arrays.fill(show, true);
        for (int i = 0; i < 8; i++) {
            names[i] = stringBuilder.append(s_relay).append(" ").append(i + 1).toString();
            stringBuilder.setLength(0);
        }

        jsonSettings.put("name", new JSONArray(names));
        jsonSettings.put("delay", new JSONArray(delays));
        jsonSettings.put("show", new JSONArray(show));

        writeToFile("SETTINGS.json", jsonSettings);
    }
}
