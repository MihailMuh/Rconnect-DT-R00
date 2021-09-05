package ru.warfare.esp8266.activity;

import static ru.warfare.esp8266.services.ClientServer.postToServer;
import static ru.warfare.esp8266.Constants.DELAY;
import static ru.warfare.esp8266.Constants.PRESS;
import static ru.warfare.esp8266.services.ClientServer.IP;
import static ru.warfare.esp8266.services.Py.print;

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

import ru.warfare.esp8266.services.Clerk;
import ru.warfare.esp8266.services.ClientServer;
import ru.warfare.esp8266.R;
import ru.warfare.esp8266.services.ThreadPool;
import ru.warfare.esp8266.services.Time;
import ru.warfare.esp8266.services.Vibrator;

public class MainActivity extends BaseActivity {
    private static final StringBuilder stringBuilder = new StringBuilder();

    private static final Button[] buttons = new Button[8];
    private static final ImageView[] imageViews = new ImageView[8];

    private Drawable onImg;
    private Drawable offImg;

    private String s_on;
    private String s_off;
    private String s_enable;
    private String s_disable;
    private String s_exit;
    private String s_example;
    private String s_save;
    private String s_address;
    private String s_saved;
    private String s_incorrect;
    private String s_settings;
    private String s_no_internet;
    private String s_enable_on_time_1;
    private String s_enable_on_time_2;
    private String s_incorrect_value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ThreadPool.post(() -> {
            offImg = AppCompatResources.getDrawable(this, R.drawable.off);
            onImg = AppCompatResources.getDrawable(this, R.drawable.on);

            Vibrator.init(this);
        });
        confirmLanguage();

        if (isOnline()) {
            ThreadPool.post(() -> {
                Clerk.init(this);
                IP = Clerk.recoveryIP();

                if (IP == null) {
                    runOnUiThread(() -> {
                        setIP(false);

                        for (int i = 0; i < buttons.length; i++) {
                            ((ImageView) findViewById(id(stringBuilder.append("light").append(i), "id"))).setImageDrawable(offImg);
                        }
                    });
                }
            });

            for (int i = 0; i < buttons.length; i++) {
                final int finalI = i;

                imageViews[i] = findViewById(id(stringBuilder.append("light").append(i), "id"));

                buttons[i] = findViewById(id(stringBuilder.append("buttonRelay").append(i), "id"));
                buttons[i].setOnClickListener(view -> {
                    Vibrator.vibrate(70);
                    if (buttons[finalI].getText().equals(s_on)) {
                        ThreadPool.post(() -> postToServer(PRESS, finalI, 1, 0, () -> makeToast(s_incorrect)));
                        buttons[finalI].setText(s_off);
                        imageViews[finalI].setImageDrawable(onImg);
                        wrapperToast(s_enable, 700);
                    } else {
                        ThreadPool.post(() -> postToServer(PRESS, finalI, 0, 0, () -> makeToast(s_incorrect)));
                        buttons[finalI].setText(s_on);
                        imageViews[finalI].setImageDrawable(offImg);
                        wrapperToast(s_disable, 700);
                    }
                });

                findViewById(id(stringBuilder.append("buttonDelay").append(i), "id")).setOnClickListener(view -> {
                    try {
                        int secs = 5;
//                                = Integer.parseInt(((EditText) findViewById(R.id.input_ip)).getText().toString());
                        ThreadPool.post(() -> {
                            postToServer(DELAY, finalI, 1, secs, () -> makeToast(s_incorrect));

                            Time.sleep(secs);

                            getStatusWrap();
                        });

                        Vibrator.vibrate(70);
                        makeToast(s_enable_on_time_1 + secs + s_enable_on_time_2);
                        buttons[finalI].setText(s_off);
                        imageViews[finalI].setImageDrawable(onImg);
                    } catch (Exception e) {
                        makeToast(s_incorrect_value);
                    }
                });
            }
        } else {
            noWiFi();
        }

        Button button = findViewById(R.id.buttonSettings);
        button.setText(s_settings);
        button.setOnClickListener(view -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.putExtra("Incorrect IP", s_incorrect);
            intent.putExtra("Successfully saved", s_saved);
            startActivity(intent);
            Vibrator.vibrate(70);
        });
    }

    public void update() {
        if (isOnline()) {
            if (IP != null) {
                ThreadPool.post(this::getStatusWrap);
            }
        } else {
            noWiFi();
        }
    }

    private void getStatusWrap() {
        try {
            getStatus();
        } catch (Exception e) {
            print(e);
        }
    }

    private void getStatus() throws Exception {
        String string = ClientServer.getStatistics();
        StringBuilder localStringBuilder = new StringBuilder();
        final int len = string.length() - 1;

        runOnUiThread(() -> {
            int j = 0;
            for (int i = 5; i < len; i += 2) {
                if (Integer.parseInt(localStringBuilder.append(string.charAt(i)).toString()) == 1) {
                    buttons[j].setText(s_off);
                    imageViews[j].setImageDrawable(onImg);
                } else {
                    buttons[j].setText(s_on);
                    imageViews[j].setImageDrawable(offImg);
                }
                localStringBuilder.setLength(0);
                j++;
            }
        });
    }

    public void setIP(boolean cancelable) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_ip, null);

        ((TextView) view.findViewById(R.id.text_example)).setText(s_example);

        EditText editText = view.findViewById(R.id.input_ip);
        editText.setHint(s_address);

        Button button = view.findViewById(R.id.button_save);
        button.setText(s_save);

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(cancelable)
                .create();

        alertDialog.setOnShowListener(dialog ->
                button.setOnClickListener(view1 -> {
                    IP = editText.getText().toString();
                    ThreadPool.post(() -> {
                        try {
                            getStatus();

                            alertDialog.dismiss();

                            Clerk.saveIP(IP);

                            makeToast(s_saved);
                        } catch (Exception e) {
                            makeToast(s_incorrect);
                        }
                    });
                }));
        alertDialog.show();
    }

    private void noWiFi() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_no_internet, null);

        Button button = view.findViewById(R.id.button_exit);
        button.setOnClickListener(view1 -> Exit());
        button.setText(s_exit);

        ((TextView) view.findViewById(R.id.text_no_internet)).setText(s_no_internet);

        new AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(false)
                .create()
                .show();
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
        s_incorrect = strings[12] + " " + s_address;
        s_no_internet = strings[14];
        s_saved = strings[15];
        s_incorrect_value = strings[16];

        s_enable_on_time_1 = strings[17] + " ";
        s_enable_on_time_2 = " " + strings[18];

        s_settings = strings[19];
    }

    @Override
    protected void onResume() {
        super.onResume();
        update();
    }
}
