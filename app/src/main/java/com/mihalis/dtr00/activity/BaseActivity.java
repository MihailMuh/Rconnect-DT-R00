package com.mihalis.dtr00.activity;

import static com.mihalis.dtr00.constants.Strings.I_ENABLE_WIFI;
import static com.mihalis.dtr00.constants.Strings.NO_WIFI;
import static com.mihalis.dtr00.services.Service.post;
import static com.mihalis.dtr00.services.Service.readFromFile;
import static com.mihalis.dtr00.services.Service.sleepMillis;

import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.mihalis.dtr00.R;
import com.mihalis.dtr00.services.Service;
import com.mihalis.dtr00.utils.ClickListener;
import com.mihalis.dtr00.utils.JSON;

import java.util.Objects;

public abstract class BaseActivity extends AppCompatActivity {
    private ConnectivityManager connectivityManager;

    protected volatile boolean onPause = false;

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
    protected void onResume() {
        super.onResume();
        fullscreen();
        onPause = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        onPause = true;
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

    public boolean online() {
        return connectivityManager.getActiveNetworkInfo() != null;
    }

    public void checkWIFI(Runnable runnable) {
        if (online()) {
            post(runnable);
        } else {
            alert(NO_WIFI, I_ENABLE_WIFI);
        }
    }

    public void toast(String text) {
        runOnUiThread(() -> Toast.makeText(this, text, Toast.LENGTH_SHORT).show());
    }

    public void toast(String text, int millis) {
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
        return super.findViewById(resources.getIdentifier(name + index, "id", packageName));
    }

    public void alert(String alertText) {
        alert(alertText, "Ok");
    }

    public void alert(String alertText, String buttonText) {
        View view = LayoutInflater.from(this).inflate(R.layout.alert, null);
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(view)
                .setTitle(alertText)
                .setCancelable(true)
                .create();

        Button button = view.findViewById(R.id.buttonOk);
        button.setOnClickListener((ClickListener) alertDialog::dismiss);
        button.setText(buttonText);

        alertDialog.show();
    }

    public JSON getJSONDevices() {
        return readFromFile(this, "DEVICES.json");
    }

    public void updateJSONDevices(JSON jsonDevices) {
        Service.writeToFile(this, "DEVICES.json", jsonDevices);
    }

    public void setWidgetState(View widget, boolean enabled) {
        if (enabled) {
            widget.setAlpha(1);
        } else {
            widget.setAlpha(0.3f);
        }
    }
}
