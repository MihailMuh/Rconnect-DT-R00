package com.mihalis.dtr00.activity;

import static com.mihalis.dtr00.Strings.ADD_DEVICE;
import static com.mihalis.dtr00.Strings.ENTER;
import static com.mihalis.dtr00.services.ClientServer.IP;
import static com.mihalis.dtr00.services.Service.post;
import static com.mihalis.dtr00.services.Service.print;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.MobileAds;
import com.mihalis.dtr00.ClickListener;
import com.mihalis.dtr00.R;
import com.mihalis.dtr00.Strings;
import com.mihalis.dtr00.services.JSON;
import com.mihalis.dtr00.services.Service;

import java.util.Iterator;

public class DevicesActivity extends BaseActivity {
    private InputMethodManager inputMethodManager;

    private ColorStateList greenTint;
    private ColorStateList whiteTint;

    private Button buttonAddDevice;

    private JSON jsonDevices;
    private int numDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_device);

        Strings.init(this);
        post(() -> {
            Service.init(this);
            inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            MobileAds.initialize(this, initializationStatus -> print("MobileAds has initialized"));
        });

        greenTint = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.green));
        whiteTint = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white));

        buttonAddDevice = findViewById(R.id.button_add_device);
        buttonAddDevice.setText(ADD_DEVICE);
        buttonAddDevice.setOnClickListener((ClickListener) () ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        post(() -> {
            jsonDevices = getJSONDevices();
            parseJsonDevices();
        });

        setButtonState(buttonAddDevice, numDevices < 4);
    }

    private void parseJsonDevices() {
        numDevices = 1;

        for (Iterator<String> it = jsonDevices.keys(); it.hasNext(); numDevices++) {
            String ip = it.next();
            String deviceName = jsonDevices.getJSON(ip).optString("deviceName");

            View deviceContainer = findViewById("deviceContainer", numDevices);
            EditText device = findViewById("device", numDevices);
            Button buttonEnter = findViewById("buttonDevice", numDevices);

            runOnUiThread(() -> {
                device.setText(deviceName);
                buttonEnter.setText(ENTER);
                setEditTestState(device, false);

                deviceContainer.setVisibility(View.VISIBLE);
            });

            findViewById("imageEditDevice", numDevices).setOnClickListener((ClickListener) () -> {
                boolean isEnabled = device.isEnabled();
                setEditTestState(device, !isEnabled);

                if (isEnabled) {
                    post(() -> newNameForDevice(device.getText().toString(), ip));
                }
            });

            buttonEnter.setOnClickListener((ClickListener) () -> {
                if (!device.isEnabled()) {
                    IP = ip;
                    startActivity(new Intent(this, MainActivity.class));
                }
            });
        }
    }

    private void newNameForDevice(String name, String ip) {
        jsonDevices.put(ip, jsonDevices.getJSON(ip).put("deviceName", name));
        updateJSONDevices(jsonDevices);
    }

    private void setEditTestState(EditText editText, boolean isEnable) {
        editText.setEnabled(isEnable);
        if (isEnable) {
            editText.setBackgroundTintList(greenTint);
            editText.requestFocus();
            editText.setSelection(editText.getText().length());
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        } else {
            editText.setBackgroundTintList(whiteTint);
        }
    }
}