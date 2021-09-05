package ru.warfare.esp8266.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import ru.warfare.esp8266.R;
import ru.warfare.esp8266.services.ThreadPool;
import ru.warfare.esp8266.services.Time;

public abstract class BaseActivity extends AppCompatActivity {
    public Resources resources;
    public String packageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        resources = getResources();
        packageName = getPackageName();
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
        return ((ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }

    public void makeToast(String text) {
        runOnUiThread(() -> Toast.makeText(this, text, Toast.LENGTH_SHORT).show());
    }

    public void wrapperToast(String text, int millis) {
        runOnUiThread(() -> {
            Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
            ThreadPool.post(() -> {
                Time.sleepMillis(millis);

                runOnUiThread(toast::cancel);
            });
            toast.show();
        });
    }

    public int id(String name, String type) {
        return resources.getIdentifier(name, type, packageName);
    }

    public int id(StringBuilder stringBuilder, String type) {
        int id =  resources.getIdentifier(stringBuilder.toString(), type, packageName);
        stringBuilder.setLength(0);
        return id;
    }
}
