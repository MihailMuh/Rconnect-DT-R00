package ru.warfare.esp8266.activity;

import static ru.warfare.esp8266.Constants.DELAY;
import static ru.warfare.esp8266.Constants.PRESS;
import static ru.warfare.esp8266.services.ClientServer.IP;
import static ru.warfare.esp8266.services.ClientServer.postToServer;
import static ru.warfare.esp8266.services.Service.post;
import static ru.warfare.esp8266.services.Service.readFromFile;
import static ru.warfare.esp8266.services.Service.sleep;
import static ru.warfare.esp8266.services.Service.vibrate;
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

import ru.warfare.esp8266.R;
import ru.warfare.esp8266.services.ClientServer;
import ru.warfare.esp8266.services.Service;

public class MainActivity extends BaseActivity {
    private static final Button[] buttons = new Button[8];
    private static final ImageView[] imageViews = new ImageView[8];

    private Drawable onImg, offImg;

    private Runnable onError;

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

        StringBuilder stringBuilder = new StringBuilder();

        Service.init(this);

        confirmLanguage();
        post(() -> {
            onError = () -> {
                if (isOnline()) {
                    makeToast(s_cant_connect_server);
                } else {
                    noWiFi();
                }
            };

            IP = readFromFile("IP");
            if (IP == null) {
                runOnUiThread(() -> setIP(false));
            }

            offImg = AppCompatResources.getDrawable(this, R.drawable.off);
            onImg = AppCompatResources.getDrawable(this, R.drawable.on);
            runOnUiThread(() -> {
                for (int i = 0; i < buttons.length; i++) {
                    imageViews[i] = findViewById(id(stringBuilder.append("light").append(i), "id"));
                    imageViews[i].setImageDrawable(offImg);
                }
            });
        });

        for (int i = 0; i < buttons.length; i++) {
            final int finalI = i;

            buttons[i] = findViewById(id(stringBuilder.append("buttonRelay").append(i), "id"));
            buttons[i].setOnClickListener(view -> {
                vibrate(55);
                if (buttons[finalI].getText().equals(s_on)) {
                    postToServer(PRESS, finalI, 1, 0, () -> {
                        imageViews[finalI].setImageDrawable(onImg);
                        buttons[finalI].setText(s_off);

                        wrapperToast(s_enable, 500);
                    }, onError);
                } else {
                    postToServer(PRESS, finalI, 0, 0, () -> {
                        imageViews[finalI].setImageDrawable(offImg);
                        buttons[finalI].setText(s_on);
                        wrapperToast(s_disable, 600);
                    }, onError);
                }
            });

            findViewById(id(stringBuilder.append("buttonDelay").append(i), "id")).setOnClickListener(view -> {
                int secs = 5;
//                                = Integer.parseInt(((EditText) findViewById(R.id.input_ip)).getText().toString());
                postToServer(DELAY, finalI, 1, secs, () -> {
                    imageViews[finalI].setImageDrawable(onImg);
                    buttons[finalI].setText(s_off);
                    makeToast(s_enable_on_time_1 + secs + s_enable_on_time_2);

                    post(() -> {
                        sleep(secs);
                        updateRelays();
                    });
                }, onError);

                vibrate(55);
            });
        }

        Button button = findViewById(R.id.buttonSettings);
        button.setText(s_settings);
        button.setOnClickListener(view -> {
            startActivity(new Intent(this, SettingsActivity.class));
            vibrate(55);
        });
    }

    private boolean updateRelays() {
        String string = ClientServer.getRelaysStatus();
        final int len = string.length() - 1;

        if (len > 0) {
            runOnUiThread(() -> {
                int j = 0;
                for (int i = 5; i < len; i += 2) {
                    if (Integer.parseInt(String.valueOf(string.charAt(i))) == 1) {
                        buttons[j].setText(s_off);
                        imageViews[j].setImageDrawable(onImg);
                    } else {
                        buttons[j].setText(s_on);
                        imageViews[j].setImageDrawable(offImg);
                    }
                    j++;
                }
            });
            return true;
        }

        return false;
    }

    public void setIP(boolean cancelable) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_ip, null);

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(cancelable)
                .create();

        ((TextView) view.findViewById(R.id.text_example)).setText(s_example);

        EditText editText = view.findViewById(R.id.input_ip);
        editText.setHint(s_address);

        Button button = view.findViewById(R.id.button_save);
        button.setText(s_save);
        button.setOnClickListener(view1 -> {
            IP = editText.getText().toString();
            post(() -> {
                if (updateRelays()) {
                    alertDialog.dismiss();

                    writeToFile("IP", IP);

                    makeToast(s_saved);
                } else {
                    makeToast(s_incorrect_IP);
                }
            });
            vibrate(55);
        });

        alertDialog.show();
    }

    private void noWiFi() {
        noWiFi(s_no_internet, s_exit, s_i_enable_wifi);
    }

    public void confirmLanguage() {
        String[] strings = getResources().getStringArray(R.array.ru);

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
        if (isOnline()) {
            post(this::updateRelays);
        } else {
            noWiFi();
        }
    }
}
