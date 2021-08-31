package ru.warfare.esp8266;

import static ru.warfare.esp8266.ClientServer.postBestScore;
import static ru.warfare.esp8266.Constants.DELAY;
import static ru.warfare.esp8266.Constants.PRESS;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends BaseActivity {
    private static final ExecutorService threadPool = Executors.newCachedThreadPool();

    private static final Button[] buttons = new Button[8];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Clerk.init(this);
        String ip = Clerk.recoveryIP();

        if (ip == null) {
            setIP(false);
        } else {
            ClientServer.IP = ip;
        }

        if (isOnline()) {
            buttons[0] = findViewById(R.id.buttonRelay1);
            buttons[0].setOnClickListener(view -> {
                if (buttons[0].getText().equals("Вкл")) {
                    threadPool.execute(() -> postBestScore(PRESS, 0, 1, 0));
                    buttons[0].setText("Выкл");
                    makeToast("Включено");
                } else {
                    threadPool.execute(() -> postBestScore(PRESS, 0, 0, 0));
                    buttons[0].setText("Вкл");
                    makeToast("Выключено");
                }
            });

            findViewById(R.id.buttonDelay1).setOnClickListener(view -> {
                try {
                    int secs = Integer.parseInt(((EditText) findViewById(R.id.input_ip)).getText().toString());
                    threadPool.execute(() -> postBestScore(DELAY, 0, 1, secs));
                    makeToast("Включено на " + secs + " секунд");
                    buttons[0].setText("Выкл");
                } catch (Exception e) {
                    makeToast("Некорректное значение!");
                }
            });

            buttons[1] = findViewById(R.id.buttonRelay2);
            buttons[1].setOnClickListener(view -> {
                if (buttons[1].getText().equals("Вкл")) {
                    threadPool.execute(() -> postBestScore(PRESS, 1, 1, 0));
                    buttons[1].setText("Выкл");
                    makeToast("Включено");
                } else {
                    threadPool.execute(() -> postBestScore(PRESS, 1, 0, 0));
                    buttons[1].setText("Вкл");
                    makeToast("Выключено");
                }
            });

            findViewById(R.id.buttonDelay2).setOnClickListener(view -> {
                try {
                    int secs = Integer.parseInt(((EditText) findViewById(R.id.input2)).getText().toString());
                    threadPool.execute(() -> postBestScore(DELAY, 1, 1, secs));
                    makeToast("Включено на " + secs + " секунд");
                    buttons[1].setText("Выкл");
                } catch (Exception e) {
                    makeToast("Некорректное значение!");
                }
            });

            buttons[2] = findViewById(R.id.buttonRelay3);
            buttons[2].setOnClickListener(view -> {
                if (buttons[2].getText().equals("Вкл")) {
                    threadPool.execute(() -> postBestScore(PRESS, 2, 1, 0));
                    buttons[2].setText("Выкл");
                    makeToast("Включено");
                } else {
                    threadPool.execute(() -> postBestScore(PRESS, 2, 0, 0));
                    buttons[2].setText("Вкл");
                    makeToast("Выключено");
                }
            });

            findViewById(R.id.buttonDelay3).setOnClickListener(view -> {
                try {
                    int secs = Integer.parseInt(((EditText) findViewById(R.id.input3)).getText().toString());
                    threadPool.execute(() -> postBestScore(DELAY, 2, 1, secs));
                    makeToast("Включено на " + secs + " секунд");
                    buttons[2].setText("Выкл");
                } catch (Exception e) {
                    makeToast("Некорректное значение!");
                }
            });

            buttons[3] = findViewById(R.id.buttonRelay4);
            buttons[3].setOnClickListener(view -> {
                if (buttons[3].getText().equals("Вкл")) {
                    threadPool.execute(() -> postBestScore(PRESS, 3, 1, 0));
                    buttons[3].setText("Выкл");
                    makeToast("Включено");
                } else {
                    threadPool.execute(() -> postBestScore(PRESS, 3, 0, 0));
                    buttons[3].setText("Вкл");
                    makeToast("Выключено");
                }
            });

            findViewById(R.id.buttonDelay4).setOnClickListener(view -> {
                try {
                    int secs = Integer.parseInt(((EditText) findViewById(R.id.input4)).getText().toString());
                    threadPool.execute(() -> postBestScore(DELAY, 3, 1, secs));
                    makeToast("Включено на " + secs + " секунд");
                    buttons[3].setText("Выкл");
                } catch (Exception e) {
                    makeToast("Некорректное значение!");
                }
            });

            buttons[4] = findViewById(R.id.buttonRelay5);
            buttons[4].setOnClickListener(view -> {
                if (buttons[4].getText().equals("Вкл")) {
                    threadPool.execute(() -> postBestScore(PRESS, 4, 1, 0));
                    buttons[4].setText("Выкл");
                    makeToast("Включено");
                } else {
                    threadPool.execute(() -> postBestScore(PRESS, 4, 0, 0));
                    buttons[4].setText("Вкл");
                    makeToast("Выключено");
                }
            });

            findViewById(R.id.buttonDelay5).setOnClickListener(view -> {
                try {
                    int secs = Integer.parseInt(((EditText) findViewById(R.id.input5)).getText().toString());
                    threadPool.execute(() -> postBestScore(DELAY, 4, 1, secs));
                    makeToast("Включено на " + secs + " секунд");
                    buttons[4].setText("Выкл");
                } catch (Exception e) {
                    makeToast("Некорректное значение!");
                }
            });

            buttons[5] = findViewById(R.id.buttonRelay6);
            buttons[5].setOnClickListener(view -> {
                if (buttons[5].getText().equals("Вкл")) {
                    threadPool.execute(() -> postBestScore(PRESS, 5, 1, 0));
                    buttons[5].setText("Выкл");
                    makeToast("Включено");
                } else {
                    threadPool.execute(() -> postBestScore(PRESS, 5, 0, 0));
                    buttons[5].setText("Вкл");
                    makeToast("Выключено");
                }
            });

            findViewById(R.id.buttonDelay6).setOnClickListener(view -> {
                try {
                    int secs = Integer.parseInt(((EditText) findViewById(R.id.input6)).getText().toString());
                    threadPool.execute(() -> postBestScore(DELAY, 5, 1, secs));
                    makeToast("Включено на " + secs + " секунд");
                    buttons[5].setText("Выкл");
                } catch (Exception e) {
                    makeToast("Некорректное значение!");
                }
            });

            buttons[6] = findViewById(R.id.buttonRelay7);
            buttons[6].setOnClickListener(view -> {
                if (buttons[6].getText().equals("Вкл")) {
                    threadPool.execute(() -> postBestScore(PRESS, 6, 1, 0));
                    buttons[6].setText("Выкл");
                    makeToast("Включено");
                } else {
                    threadPool.execute(() -> postBestScore(PRESS, 6, 0, 0));
                    buttons[6].setText("Вкл");
                    makeToast("Выключено");
                }
            });

            findViewById(R.id.buttonDelay7).setOnClickListener(view -> {
                try {
                    int secs = Integer.parseInt(((EditText) findViewById(R.id.input7)).getText().toString());
                    threadPool.execute(() -> postBestScore(DELAY, 6, 1, secs));
                    makeToast("Включено на " + secs + " секунд");
                    buttons[6].setText("Выкл");
                } catch (Exception e) {
                    makeToast("Некорректное значение!");
                }
            });

            buttons[7] = findViewById(R.id.buttonRelay8);
            buttons[7].setOnClickListener(view -> {
                if (buttons[7].getText().equals("Вкл")) {
                    threadPool.execute(() -> postBestScore(PRESS, 7, 1, 0));
                    buttons[7].setText("Выкл");
                    makeToast("Включено");
                } else {
                    threadPool.execute(() -> postBestScore(PRESS, 7, 0, 0));
                    buttons[7].setText("Вкл");
                    makeToast("Выключено");
                }
            });

            findViewById(R.id.buttonDelay8).setOnClickListener(view -> {
                try {
                    int secs = Integer.parseInt(((EditText) findViewById(R.id.input8)).getText().toString());
                    threadPool.execute(() -> postBestScore(DELAY, 7, 1, secs));
                    makeToast("Включено на " + secs + " секунд");
                    buttons[7].setText("Выкл");
                } catch (Exception e) {
                    makeToast("Некорректное значение!");
                }
            });
        } else {
            makeLongToast("Отсутствует интернет соединение!");
        }

        findViewById(R.id.ip).setOnClickListener(view -> setIP(true));
        ((TextView) findViewById(R.id.current_ip)).setText(("Текущий IP адрес: " + ClientServer.IP));
    }

    public void update() {
        if (isOnline() && ClientServer.IP != null) {
            threadPool.execute(this::getStatus);
        }
    }

    private void getStatus() {
        String string = ClientServer.getStatistics();
        StringBuilder stringBuilder = new StringBuilder();
        int len = string.length() - 1;
        int j = 0;

        for (int i = 5; i < len; i += 2) {
            if (Integer.parseInt(stringBuilder.append(string.charAt(i)).toString()) == 1) {
                int finalJ = j;
                runOnUiThread(() -> buttons[finalJ].setText("Выкл"));
            }
            stringBuilder.setLength(0);
            j++;
        }
    }

    public void setIP(boolean cancelable) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_ip, null);

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(cancelable)
                .create();

        alertDialog.setOnShowListener(dialog ->
                view.findViewById(R.id.button_ip).setOnClickListener(view1 -> {
                    String ip = ((EditText) view.findViewById(R.id.input_ip)).getText().toString();
                    threadPool.execute(() -> {
                        try {
                            ClientServer.IP = ip;
                            getStatus();

                            Thread.sleep(1_000);

                            Clerk.saveIP(ip);

                            runOnUiThread(() -> ((TextView) findViewById(R.id.current_ip)).setText(("Текущий IP адрес: " + ClientServer.IP)));

                            makeLongToast("Успешно сохранено!");

                            alertDialog.dismiss();
                        } catch (Exception e) {
                            makeLongToast("Некорректный IP адрес");
                        }
                    });
                }));
        alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        update();
    }
}
