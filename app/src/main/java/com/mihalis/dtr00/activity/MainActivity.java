package com.mihalis.dtr00.activity;

import static com.mihalis.dtr00.Constants.DELAY;
import static com.mihalis.dtr00.Constants.PRESS;
import static com.mihalis.dtr00.Strings.DISABLE;
import static com.mihalis.dtr00.Strings.ENABLE;
import static com.mihalis.dtr00.Strings.ENABLE_AT;
import static com.mihalis.dtr00.Strings.OFF;
import static com.mihalis.dtr00.Strings.ON;
import static com.mihalis.dtr00.Strings.PULSE;
import static com.mihalis.dtr00.Strings.RELAY;
import static com.mihalis.dtr00.Strings.SECONDS;
import static com.mihalis.dtr00.Strings.SETTINGS;
import static com.mihalis.dtr00.Strings.UNEXPECTED_ERROR;
import static com.mihalis.dtr00.services.ClientServer.IP;
import static com.mihalis.dtr00.services.ClientServer.postToServer;
import static com.mihalis.dtr00.services.Service.post;
import static com.mihalis.dtr00.services.Service.print;
import static com.mihalis.dtr00.services.Service.readFromFile;
import static com.mihalis.dtr00.services.Service.sleep;
import static com.mihalis.dtr00.services.Service.writeToFile;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;

import com.google.android.gms.ads.MobileAds;
import com.mihalis.dtr00.ClickListener;
import com.mihalis.dtr00.R;
import com.mihalis.dtr00.services.ClientServer;
import com.mihalis.dtr00.services.Service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends BaseActivity {
    static volatile boolean afterRegister = false;

    private final Button[] buttonsOnOff = new Button[8];
    private final Button[] buttonsDelay = new Button[8];
    private final ImageView[] imageViews = new ImageView[8];
    private final TextView[] textRelays = new TextView[8];

    private final int[] delays = new int[8];
    private final boolean[] show = new boolean[8];
    private final String[] names = new String[8];

    private Drawable onImg, offImg;

    private final Runnable onError = () -> checkWIFI(() -> toast(UNEXPECTED_ERROR));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Service.init(this);
        post(() -> MobileAds.initialize(this, initializationStatus -> print("MobileAds has initialized")));

        offImg = AppCompatResources.getDrawable(this, R.drawable.off);
        onImg = AppCompatResources.getDrawable(this, R.drawable.on);

        for (int i = 0; i < buttonsOnOff.length; i++) {
            final int finalI = i;

            buttonsOnOff[i] = findViewById("buttonRelay", i);
            buttonsDelay[i] = findViewById("buttonDelay", i);
            textRelays[i] = findViewById("relay", i);
            imageViews[i] = findViewById("light", i);

            buttonsOnOff[i].setOnClickListener((ClickListener) () -> {
                if (buttonsOnOff[finalI].getText().equals(ON)) {
                    postToServer(PRESS, finalI, 1, 0, () -> {
                        imageViews[finalI].setImageDrawable(onImg);
                        buttonsOnOff[finalI].setText(OFF);

                        toast(ENABLE, 500);
                    }, onError);
                } else {
                    postToServer(PRESS, finalI, 0, 0, () -> {
                        imageViews[finalI].setImageDrawable(offImg);
                        buttonsOnOff[finalI].setText(ON);

                        toast(DISABLE, 500);
                    }, onError);
                }
            });

            buttonsDelay[i].setText(PULSE);
            buttonsDelay[i].setOnClickListener((ClickListener) () ->
                    postToServer(DELAY, finalI, 1, delays[finalI], () -> {
                        imageViews[finalI].setImageDrawable(onImg);
                        buttonsOnOff[finalI].setText(OFF);
                        toast(ENABLE_AT + " " + delays[finalI] + " " + SECONDS);

                        post(() -> {
                            sleep(delays[finalI]);
                            updateRelays();
                        });
                    }, onError));
        }

        Button button = findViewById(R.id.buttonSettings);
        button.setText(SETTINGS);
        button.setOnClickListener((ClickListener) () ->
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

    private void register() {
        runOnUiThread(() -> startActivity(new Intent(this, RegisterActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();

        post(() -> {
            String settings = readFromFile("SETTINGS.json");
            String ip = readFromFile("IP.json");

            try {
                if (settings == null || ip == null) {
                    register();
                    writePrimarySettings();
                    return;
                }

                var jsonIP = new JSONObject(ip);
                if (!afterRegister && !jsonIP.getBoolean("remember")) {
                    register();
                    return;
                }

                parseSettings(settings);
                IP = jsonIP.getString("IP");

                checkWIFI(this::updateRelays);
                runOnUiThread(this::updateUI);
            } catch (Exception e) {
                print("Error working with JSON " + e);
            }
        });
    }

    private void parseSettings(String settings) throws JSONException {
        var jsonSettings = new JSONObject(settings);

        var nameArray = jsonSettings.getJSONArray("name");
        var showArray = jsonSettings.getJSONArray("show");
        var delayArray = jsonSettings.getJSONArray("delay");

        for (int i = 0; i < 8; i++) {
            names[i] = nameArray.getString(i);
            delays[i] = delayArray.getInt(i);
            show[i] = showArray.getBoolean(i);
        }
    }

    private void writePrimarySettings() throws JSONException {
        var jsonSettings = new JSONObject();
        var stringBuilder = new StringBuilder();

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
