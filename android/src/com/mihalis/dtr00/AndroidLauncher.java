package com.mihalis.dtr00;

import static android.widget.Toast.LENGTH_SHORT;
import static com.mihalis.dtr00.systemd.service.Service.setLogger;
import static com.mihalis.dtr00.systemd.service.Toast.setToastManager;

import android.os.Bundle;
import android.os.Process;
import android.widget.Toast;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.mihalis.dtr00.systemd.MainApp;
import com.mihalis.dtr00.systemd.service.Processor;
import com.mihalis.dtr00.systemd.service.Service;
import com.mihalis.dtr00.utils.DTR00Exception;
import com.mihalis.dtr00.utils.LogManager;
import com.mihalis.dtr00.utils.ToastManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class AndroidLauncher extends AndroidApplication implements ToastManager, LogManager {
    private static final String processId = Integer.toString(android.os.Process.myPid());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useAccelerometer = false;
        config.useCompass = false;
        config.useGyroscope = false;
        config.useImmersiveMode = false;
        config.hideStatusBar = true;
        config.numSamples = 2;
        config.disableAudio = true;
        config.useGL30 = true;

        setToastManager(this);
        setLogger(this);

        initialize(new MainApp(), config);
    }

    @Override
    public void makeToast(String text) {
        runOnUiThread(() -> Toast.makeText(this, text, LENGTH_SHORT).show());
    }

    @Override
    public void makeToast(String text, int millis) {
        runOnUiThread(() -> {
            Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
            toast.show();
            Processor.postTask(() -> {
                Service.sleep(millis);

                toast.cancel();
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Process.killProcess(Process.myPid());
    }

    @Override
    public String getLog() {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            String[] cmd = new String[]{"logcat", "-d", "-v", "threadtime"};

            java.lang.Process process = Runtime.getRuntime().exec(cmd);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains(processId)) {
                    stringBuilder.append(line).append("\n");
                }
            }
        } catch (Exception exception) {
            throw new DTR00Exception(exception.getMessage(), exception, true, true);
        }

        return stringBuilder.toString();
    }
}
