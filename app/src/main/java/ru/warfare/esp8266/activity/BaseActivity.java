package ru.warfare.esp8266.activity;

import static ru.warfare.esp8266.services.Service.post;
import static ru.warfare.esp8266.services.Service.sleepMillis;
import static ru.warfare.esp8266.services.Service.vibrate;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import ru.warfare.esp8266.R;

public abstract class BaseActivity extends AppCompatActivity {
    private final StringBuilder stringBuilder = new StringBuilder();
    private ConnectivityManager connectivityManager;

    public Resources resources;
    public String packageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        resources = getResources();
        packageName = getPackageName();
        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        fullscreen();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_main);
        fullscreen();
    }

    @Override
    public void onBackPressed() {
        Exit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fullscreen();
    }

    public void Exit() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.killBackgroundProcesses(getApplication().getPackageName());
        finishAndRemoveTask();
        System.exit(0);
    }

    public void fullscreen() {
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LOW_PROFILE
        );
    }

    public boolean isOnline() {
        return connectivityManager.getActiveNetworkInfo() != null;
    }

    public void makeToast(String text) {
        runOnUiThread(() -> Toast.makeText(this, text, Toast.LENGTH_SHORT).show());
    }

    public void wrapperToast(String text, int millis) {
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
        toast.show();
        post(() -> {
            sleepMillis(millis);

            toast.cancel();
        });
    }

    public synchronized <T extends View> T findViewById(String name, int index) {
        stringBuilder.setLength(0);
        return super.findViewById(resources.getIdentifier(
                stringBuilder.append(name).append(index).toString(), "id", packageName));
    }

    public synchronized int id(String name, String index) {
        stringBuilder.setLength(0);
        return resources.getIdentifier(
                stringBuilder.append(name).append(index).toString(), "id", packageName);
    }

    public void noWiFi(String title, String button1, String button2) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_no_internet, null);
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(view)
                .setTitle(title)
                .setCancelable(false)
                .create();

        Button button = view.findViewById(R.id.button_exit);
        button.setOnClickListener(view1 -> {
            vibrate(55);
            Exit();
        });
        button.setText(button1);

        button = view.findViewById(R.id.button_i_enable_wifi);
        button.setOnClickListener(view1 -> {
            vibrate(55);
            alertDialog.dismiss();
            if (!isOnline()) {
                noWiFi(title, button1, button2);
            }
        });
        button.setText(button2);

        alertDialog.show();
    }
}
