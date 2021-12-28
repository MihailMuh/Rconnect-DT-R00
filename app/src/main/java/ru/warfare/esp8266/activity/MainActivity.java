package ru.warfare.esp8266.activity;

import static ru.warfare.esp8266.Constants.DELAY;
import static ru.warfare.esp8266.Constants.PRESS;
import static ru.warfare.esp8266.Strings.*;
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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
    private static boolean firstRun = true;

    private final Button[] buttonsOnOff = new Button[8];
    private final Button[] buttonsDelay = new Button[8];
    private final ImageView[] imageViews = new ImageView[8];
    private final TextView[] textRelays = new TextView[8];

    private final int[] delays = new int[8];
    private final boolean[] show = new boolean[8];
    private final String[] names = new String[8];

    private Drawable onImg, offImg;

    private Runnable onError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Service.init(this);

        post(() -> {
            offImg = AppCompatResources.getDrawable(this, R.drawable.off);
            onImg = AppCompatResources.getDrawable(this, R.drawable.on);

            onError = () -> checkWIFI(() -> makeToast(CANT_CONNECT_SERVER));

            for (int i = 0; i < buttonsOnOff.length; i++) {
                final int finalI = i;

                buttonsDelay[i] = findViewById("buttonDelay", i);
                textRelays[i] = findViewById("relay", i);
                imageViews[i] = findViewById("light", i);

                buttonsOnOff[i] = findViewById("buttonRelay", i);
                buttonsOnOff[i].setOnClickListener((ButtonListener) () -> {
                    if (buttonsOnOff[finalI].getText().equals(ON)) {
                        postToServer(PRESS, finalI, 1, 0, () -> {
                            imageViews[finalI].setImageDrawable(onImg);
                            buttonsOnOff[finalI].setText(OFF);

                            wrapperToast(ENABLE, 500);
                        }, onError);
                    } else {
                        postToServer(PRESS, finalI, 0, 0, () -> {
                            imageViews[finalI].setImageDrawable(offImg);
                            buttonsOnOff[finalI].setText(ON);
                            wrapperToast(DISABLE, 500);
                        }, onError);
                    }
                });

                findViewById("buttonDelay", i).setOnClickListener((ButtonListener) () ->
                        postToServer(DELAY, finalI, 1, delays[finalI], () -> {
                            imageViews[finalI].setImageDrawable(onImg);
                            buttonsOnOff[finalI].setText(OFF);
                            makeToast(ENABLE_AT + " " + delays[finalI] + " " + SECONDS);

                            post(() -> {
                                sleep(delays[finalI]);
                                updateRelays();
                            });
                        }, onError));
            }
        });

        Button button = findViewById(R.id.buttonSettings);
        button.setText(SETTINGS);
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

    private void updateRelays() {
        String string = ClientServer.getRelaysStatus();
        final int len = string.length() - 1;

        if (len > 0) {
            runOnUiThread(() -> {
                int j = 0;
                for (int i = 5; i < len; i += 2) {
                    if (show[j]) {
                        if (Integer.parseInt(String.valueOf(string.charAt(i))) == 1) {
                            buttonsOnOff[j].setText(OFF);
                            imageViews[j].setImageDrawable(onImg);
                        } else {
                            buttonsOnOff[j].setText(ON);
                            imageViews[j].setImageDrawable(offImg);
                        }
                    }
                    j++;
                }
            });
        }
    }

    private void login() {
        runOnUiThread(() -> startActivity(new Intent(this, RegisterActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();

        post(() -> {
            String data = readFromFile("SETTINGS.json");
            String ip = readFromFile("IP.json");

            try {
                if (data == null || ip == null) {
                    login();
                    writeJSONAtFirst();
                } else {
                    JSONObject jsonSettings = new JSONObject(data);

                    JSONArray nameArray = jsonSettings.getJSONArray("name");
                    JSONArray showArray = jsonSettings.getJSONArray("show");
                    JSONArray delayArray = jsonSettings.getJSONArray("delay");

                    JSONObject jsonIP = new JSONObject(ip);
                    if (firstRun && !jsonIP.getBoolean("remember")) {
                        login();
                    } else {
                        IP = jsonIP.getString("IP");
                    }

                    for (int i = 0; i < 8; i++) {
                        names[i] = nameArray.getString(i);
                        delays[i] = delayArray.getInt(i);
                        show[i] = showArray.getBoolean(i);
                    }

                    checkWIFI(this::updateRelays);
                    runOnUiThread(this::updateUI);
                }
            } catch (Exception e) {
                print("Error working with JSON " + e);
            }
            firstRun = false;
        });
    }

    private void writeJSONAtFirst() throws JSONException {
        JSONObject jsonSettings = new JSONObject();
        StringBuilder stringBuilder = new StringBuilder();

        Arrays.fill(delays, 5);
        Arrays.fill(show, true);
        for (int i = 0; i < 8; i++) {
            names[i] = stringBuilder.append(RELAY).append(" ").append(i + 1).toString();
            stringBuilder.setLength(0);
        }

        jsonSettings.put("name", new JSONArray(names));
        jsonSettings.put("delay", new JSONArray(delays));
        jsonSettings.put("show", new JSONArray(show));

        writeToFile("SETTINGS.json", jsonSettings);
    }
}
