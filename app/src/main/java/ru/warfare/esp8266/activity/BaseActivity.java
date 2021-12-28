package ru.warfare.esp8266.activity;

import static ru.warfare.esp8266.Strings.I_ENABLE_WIFI;
import static ru.warfare.esp8266.Strings.NO_WIFI;
import static ru.warfare.esp8266.Strings.QUIT;
import static ru.warfare.esp8266.services.Service.post;
import static ru.warfare.esp8266.services.Service.sleepMillis;

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

import ru.warfare.esp8266.ButtonListener;
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
        finishAffinity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fullscreen();
    }

    public void fullscreen() {
        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LOW_PROFILE
        );
    }

    public void checkWIFI(Runnable runnable) {
        if (connectivityManager.getActiveNetworkInfo() != null) {
            post(runnable);
        } else {
            noWiFi();
        }
    }

    public void makeToast(String text) {
        runOnUiThread(() -> Toast.makeText(this, text, Toast.LENGTH_SHORT).show());
    }

    public void wrapperToast(String text, int millis) {
        runOnUiThread(() -> {
            Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
            toast.show();
            post(() -> {
                sleepMillis(millis);

                toast.cancel();
            });
        });
    }

    public synchronized <T extends View> T findViewById(String name, int index) {
        stringBuilder.setLength(0);
        return super.findViewById(resources.getIdentifier(
                stringBuilder.append(name).append(index).toString(), "id", packageName));
    }

    public void noWiFi() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_no_internet, null);
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(view)
                .setTitle(NO_WIFI)
                .setCancelable(false)
                .create();

        Button button = view.findViewById(R.id.button_exit);
        button.setOnClickListener((ButtonListener) this::finishAffinity);
        button.setText(QUIT);

        button = view.findViewById(R.id.button_i_enable_wifi);
        button.setOnClickListener((ButtonListener) () -> {
            alertDialog.dismiss();
            checkWIFI(() -> {
            });
        });
        button.setText(I_ENABLE_WIFI);

        alertDialog.show();
    }
}
