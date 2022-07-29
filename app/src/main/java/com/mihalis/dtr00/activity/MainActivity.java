package com.mihalis.dtr00.activity;

import static com.mihalis.dtr00.constants.Constants.DELAY;
import static com.mihalis.dtr00.constants.Constants.PRESS;
import static com.mihalis.dtr00.constants.Strings.CHANGE_LAYOUT;
import static com.mihalis.dtr00.constants.Strings.DISABLE;
import static com.mihalis.dtr00.constants.Strings.ENABLE;
import static com.mihalis.dtr00.constants.Strings.ENABLE_AT;
import static com.mihalis.dtr00.constants.Strings.OFF;
import static com.mihalis.dtr00.constants.Strings.ON;
import static com.mihalis.dtr00.constants.Strings.PULSE;
import static com.mihalis.dtr00.constants.Strings.SAVE;
import static com.mihalis.dtr00.constants.Strings.SECONDS;
import static com.mihalis.dtr00.constants.Strings.SETTINGS;
import static com.mihalis.dtr00.constants.Strings.SUCCESSFULLY_SAVED;
import static com.mihalis.dtr00.constants.Strings.TAP_TO_HIDE_WIDGET;
import static com.mihalis.dtr00.services.ClientServer.IP;
import static com.mihalis.dtr00.services.ClientServer.postToServer;
import static com.mihalis.dtr00.services.Service.post;
import static com.mihalis.dtr00.services.Service.print;
import static com.mihalis.dtr00.services.Service.sleep;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;

import com.mihalis.dtr00.R;
import com.mihalis.dtr00.services.ClientServer;
import com.mihalis.dtr00.services.Service;
import com.mihalis.dtr00.utils.ArrayFiller;
import com.mihalis.dtr00.utils.ClickListener;
import com.mihalis.dtr00.utils.JSON;

public class MainActivity extends BaseActivity {
    private boolean hideMode = false;
    static volatile boolean afterRegister = false;

    private final Button[] buttonsOnOff = new Button[8];
    private final Button[] buttonsDelay = new Button[8];
    private final ImageView[] imageViews = new ImageView[8];
    private final TextView[] textRelays = new TextView[8];
    private final View[][] allViews = new View[][]{textRelays, buttonsOnOff, imageViews, buttonsDelay};

    private final int[] delays = new int[8];
    private final String[] names = new String[8];
    private final boolean[][] disabledMask = new boolean[8][4];

    private Drawable onImg, offImg;
    private JSON jsonDeviceData;
    private Button buttonChangeLayout; // сохраняем, чтобы в pause менять ее текст

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        offImg = AppCompatResources.getDrawable(this, R.drawable.off);
        onImg = AppCompatResources.getDrawable(this, R.drawable.on);

        setListenersToWidgets();

        Button buttonSettings = findViewById(R.id.buttonSettings);
        buttonSettings.setText(SETTINGS);
        buttonSettings.setOnClickListener((ClickListener) () -> startActivity(new Intent(this, SettingsActivity.class)));

        buttonChangeLayout = findViewById(R.id.buttonChangeLayout);
        buttonChangeLayout.setText(CHANGE_LAYOUT);
        buttonChangeLayout.setOnClickListener((ClickListener) () -> {
            if (hideMode) {
                saveDisabledMaskToJson();
                updateUIForVisibility();
            } else {
                buttonChangeLayout.setText(SAVE);
                alert(TAP_TO_HIDE_WIDGET);
                for (int i = 0; i < disabledMask.length; i++) {
                    for (int j = 0; j < disabledMask[i].length; j++) {
                        allViews[j][i].setVisibility(View.VISIBLE);
                        setWidgetState(allViews[j][i], disabledMask[i][j]);
                    }
                }
            }
            hideMode = !hideMode;
        });

        TextView deviceName = findViewById(R.id.deviceName);
        deviceName.setText(getJSONDevices().getJSON(IP).optString("deviceName"));
    }

    private void setListenersToWidgets() {
        for (int i = 0; i < buttonsOnOff.length; i++) {
            final int I = i;

            buttonsOnOff[i] = findViewById("buttonRelay", i);
            buttonsDelay[i] = findViewById("buttonDelay", i);
            textRelays[i] = findViewById("relay", i);
            imageViews[i] = findViewById("light", i);

            buttonsOnOff[i].setOnClickListener((ClickListener) () -> {
                if (!hideMode) {
                    checkWIFI(() -> {
                        if (buttonsOnOff[I].getText().equals(ON)) {
                            postToServer(this, PRESS, I, 1, 0, () -> {
                                imageViews[I].setImageDrawable(onImg);
                                buttonsOnOff[I].setText(OFF);

                                toast(ENABLE, 500);
                            });
                        } else {
                            postToServer(this, PRESS, I, 0, 0, () -> {
                                imageViews[I].setImageDrawable(offImg);
                                buttonsOnOff[I].setText(ON);

                                toast(DISABLE, 500);
                            });
                        }
                    });
                } else {
                    boolean enabled = disabledMask[I][1];
                    setWidgetState(buttonsOnOff[I], !enabled);
                    disabledMask[I][1] = !enabled;
                }
            });

            buttonsDelay[i].setText(PULSE);
            buttonsDelay[i].setOnClickListener((ClickListener) () -> {
                if (!hideMode) {
                    checkWIFI(() ->
                            postToServer(this, DELAY, I, 1, delays[I], () -> {
                                imageViews[I].setImageDrawable(onImg);
                                buttonsOnOff[I].setText(OFF);
                                toast(ENABLE_AT + " " + delays[I] + " " + SECONDS);

                                post(() -> {
                                    sleep(delays[I]);
                                    updateRelaysImages();
                                });
                            })
                    );
                } else {
                    boolean enabled = disabledMask[I][3];
                    setWidgetState(buttonsDelay[I], !enabled);
                    disabledMask[I][3] = !enabled;
                }
            });

            textRelays[i].setOnClickListener((ClickListener) () -> {
                if (!hideMode) return;
                boolean enabled = disabledMask[I][0];
                setWidgetState(textRelays[I], !enabled);
                disabledMask[I][0] = !enabled;
            });
            imageViews[i].setOnClickListener((ClickListener) () -> {
                if (!hideMode) return;
                boolean enabled = disabledMask[I][2];
                setWidgetState(imageViews[I], !enabled);
                disabledMask[I][2] = !enabled;
            });
        }

    }

    private void updateUIForVisibility() {
        for (int i = 0; i < disabledMask.length; i++) {
            for (int j = 0; j < disabledMask[i].length; j++) {
                if (disabledMask[i][j]) {
                    allViews[j][i].setVisibility(View.VISIBLE);
                } else {
                    allViews[j][i].setVisibility(View.GONE);
                }
            }
            textRelays[i].setText(names[i]);
        }
    }

    private void updateRelaysImages() {
        if (onPause) {
            return;
        }
        String relayStatus = ClientServer.getRelaysStatus();
        int startIndex = relayStatus.indexOf(getCountOfRelayChannels(relayStatus)) + 2;

        runOnUiThread(() -> {
            int j = 0;
            for (int i = startIndex; i < relayStatus.length() - 1; i += 2) {
                if (relayStatus.charAt(i) == '1') {
                    buttonsOnOff[j].setText(OFF);
                    imageViews[j].setImageDrawable(onImg);
                } else {
                    buttonsOnOff[j].setText(ON);
                    imageViews[j].setImageDrawable(offImg);
                }
                j++;
            }
        });
    }

    // channels - кубики на реле (2, 4, 8 шт.)
    private String getCountOfRelayChannels(String relayStatus) {
        int relayChannels = 2;

        while (!relayStatus.contains(String.valueOf(relayChannels))) {
            relayChannels *= 2;
        }
        return String.valueOf(relayChannels);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (hideMode) {
            saveDisabledMaskToJson();
            hideMode = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

//          For example: {"remember":true,
//                      "settings":{
//                          "name":["Реле 1","Реле 2","Реле 3","Реле 4","Реле 5","Реле 6","Реле 7","Реле 8"],
//                          "disabledMask":[[false,false,false,false],[true,true,true,true],[true,true,true,true],[true,true,true,true],[true,true,true,true],[true,true,true,true],[true,true,true,true],[false,false,false,false]],
//                          "show":[true,true,true,true,true,true,true,true]
//                      },
//                      "deviceName":"Hello World!"}
        jsonDeviceData = getJSONDevices().getJSON(IP);
        print("Device data:", jsonDeviceData);

        if (!afterRegister && !jsonDeviceData.optBoolean("remember")) {
            startActivity(new Intent(this, RegisterActivity.class));
            return;
        }

        parseSettings(jsonDeviceData.getJSON("settings"));

        updateUIForVisibility();
        if (online()) post(this::updateRelaysImages);
    }

    private void parseSettings(JSON jsonSettings) {
        var nameArray = jsonSettings.optJSONArray("name");
        var delayArray = jsonSettings.optJSONArray("delay");
        var disabledMaskArray = jsonSettings.optJSONArray("disabledMask");

        for (int i = 0; i < 8; i++) {
            names[i] = nameArray.optString(i);
            delays[i] = delayArray.optInt(i);
        }

        // проверка на тех, у кого в файлах было еще записано "show":[true,true,true,true,true,true,true,true] (для старых обновлений)
        if (disabledMaskArray != null) {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 4; j++) {
                    disabledMask[i][j] = disabledMaskArray.optJSONArray(i).optBoolean(j);
                }
            }
        } else {
            ArrayFiller.fill(disabledMask, true);
        }
    }

    private void saveDisabledMaskToJson() {
        buttonChangeLayout.setText(CHANGE_LAYOUT);
        toast(SUCCESSFULLY_SAVED);

        Service.post(() -> {
            var jsonSettings = jsonDeviceData.getJSON("settings");
            jsonSettings.put("disabledMask", JSON.createJSONArray(disabledMask));
            updateJSONDevices(getJSONDevices().put(IP, jsonDeviceData.put("settings", jsonSettings)));
        });
    }
}
