package com.mihalis.dtr00.activity;

import static com.mihalis.dtr00.Constants.DELAY;
import static com.mihalis.dtr00.Constants.PRESS;
import static com.mihalis.dtr00.Strings.DISABLE;
import static com.mihalis.dtr00.Strings.ENABLE;
import static com.mihalis.dtr00.Strings.ENABLE_AT;
import static com.mihalis.dtr00.Strings.ERROR_ACCESSING_THE_RELAY;
import static com.mihalis.dtr00.Strings.OFF;
import static com.mihalis.dtr00.Strings.ON;
import static com.mihalis.dtr00.Strings.PULSE;
import static com.mihalis.dtr00.Strings.SECONDS;
import static com.mihalis.dtr00.Strings.SETTINGS;
import static com.mihalis.dtr00.services.ClientServer.IP;
import static com.mihalis.dtr00.services.ClientServer.postToServer;
import static com.mihalis.dtr00.services.Service.post;
import static com.mihalis.dtr00.services.Service.sleep;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;

import com.mihalis.dtr00.ClickListener;
import com.mihalis.dtr00.R;
import com.mihalis.dtr00.services.ClientServer;
import com.mihalis.dtr00.services.JSON;

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

    private final Runnable onError = () -> checkWIFI(() -> toast(ERROR_ACCESSING_THE_RELAY));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        offImg = AppCompatResources.getDrawable(this, R.drawable.off);
        onImg = AppCompatResources.getDrawable(this, R.drawable.on);

        for (int i = 0; i < buttonsOnOff.length; i++) {
            final int I = i;

            buttonsOnOff[i] = findViewById("buttonRelay", i);
            buttonsDelay[i] = findViewById("buttonDelay", i);
            textRelays[i] = findViewById("relay", i);
            imageViews[i] = findViewById("light", i);

            buttonsOnOff[i].setOnClickListener((ClickListener) () -> {
                if (buttonsOnOff[I].getText().equals(ON)) {
                    postToServer(this, PRESS, I, 1, 0, () -> {
                        imageViews[I].setImageDrawable(onImg);
                        buttonsOnOff[I].setText(OFF);

                        toast(ENABLE, 500);
                    }, onError);
                } else {
                    postToServer(this, PRESS, I, 0, 0, () -> {
                        imageViews[I].setImageDrawable(offImg);
                        buttonsOnOff[I].setText(ON);

                        toast(DISABLE, 500);
                    }, onError);
                }
            });

            buttonsDelay[i].setText(PULSE);
            buttonsDelay[i].setOnClickListener((ClickListener) () ->
                    postToServer(this, DELAY, I, 1, delays[I], () -> {
                        imageViews[I].setImageDrawable(onImg);
                        buttonsOnOff[I].setText(OFF);
                        toast(ENABLE_AT + " " + delays[I] + " " + SECONDS);

                        post(() -> {
                            sleep(delays[I]);
                            updateRelays();
                        });
                    }, onError));
        }

        Button button = findViewById(R.id.buttonSettings);
        button.setText(SETTINGS);
        button.setOnClickListener((ClickListener) () -> startActivity(new Intent(this, SettingsActivity.class)));
    }

    private void updateUI() {
        for (int i = 0; i < 8; i++) {
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
        if (onPause) {
            return;
        }
        String relayStatus = ClientServer.getRelaysStatus();
        int startIndex = relayStatus.indexOf(getCountOfRelayChannels(relayStatus)) + 2;

        runOnUiThread(() -> {
            int j = 0;
            for (int i = startIndex; i < relayStatus.length() - 1; i += 2) {
                if (show[j]) {
                    if (relayStatus.charAt(i) == '1') {
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

    private String getCountOfRelayChannels(String relayStatus) {
        int relayChannels = 2;

        while (!relayStatus.contains(String.valueOf(relayChannels))) {
            relayChannels *= 2;
        }
        return String.valueOf(relayChannels);
    }

    @Override
    protected void onResume() {
        super.onResume();

        post(() -> {
            var jsonDeviceData = getJSONDevices().getJSON(IP);
            if (!afterRegister && !jsonDeviceData.optBoolean("remember")) {
                runOnUiThread(() -> startActivity(new Intent(this, RegisterActivity.class)));
                return;
            }

            parseSettings(jsonDeviceData.getJSON("settings"));

            runOnUiThread(this::updateUI);
            checkWIFI(this::updateRelays);
        });
    }

    private void parseSettings(JSON jsonSettings) {
        var nameArray = jsonSettings.getJSONArray("name");
        var showArray = jsonSettings.getJSONArray("show");
        var delayArray = jsonSettings.getJSONArray("delay");

        for (int i = 0; i < 8; i++) {
            names[i] = nameArray.optString(i);
            delays[i] = delayArray.optInt(i);
            show[i] = showArray.optBoolean(i);
        }
    }
}
