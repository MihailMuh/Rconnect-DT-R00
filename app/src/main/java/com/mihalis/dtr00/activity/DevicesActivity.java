package com.mihalis.dtr00.activity;

import static com.mihalis.dtr00.services.ClientServer.IP;
import static com.mihalis.dtr00.services.Service.print;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.constraintlayout.utils.widget.ImageFilterButton;
import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.MobileAds;
import com.mihalis.dtr00.ClickListener;
import com.mihalis.dtr00.R;
import com.mihalis.dtr00.Strings;
import com.mihalis.dtr00.services.Service;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class DevicesActivity extends BaseActivity {
    private JSONObject jsonDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_device);
        Service.post(() -> {
            Strings.init(this);
            Service.init(this);
            MobileAds.initialize(this, initializationStatus -> print("MobileAds has initialized"));
        });

        Button button = findViewById(R.id.button_add_device);
        button.setOnClickListener((ClickListener) () -> startActivity(new Intent(this, RegisterActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            jsonDevices = getJSONDevices();

            if (jsonDevices.length() > 0) {
                parseJsonDevices();
            }
        } catch (Exception e) {
            print("Error in DEVICES Activity " + e);
        }
    }

    private void parseJsonDevices() throws JSONException {
        var greenTint = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.green));
        var whiteTint = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white));
        int numDevices = 0;

        for (Iterator<String> it = jsonDevices.keys(); it.hasNext(); ) {
            String ip = it.next();
            numDevices++;

            findViewById("deviceContainer", numDevices).setVisibility(View.VISIBLE);

            EditText device = findViewById("device", numDevices);
            device.setText(jsonDevices.getJSONObject(ip).getString("deviceName"));

            ImageFilterButton imageEdit = findViewById("imageEditDevice", numDevices);
            imageEdit.setOnClickListener((ClickListener) () -> {
                if (device.isEnabled()) {
                    Service.post(() -> newNameForDevice(device.getText().toString(), ip));

                    device.setEnabled(false);
                    device.setBackgroundTintList(whiteTint);
                } else {
                    device.setEnabled(true);
                    device.setBackgroundTintList(greenTint);
                }
            });

            findViewById("buttonDevice", numDevices).setOnClickListener((ClickListener) () -> {
                if (!device.isEnabled()) {
                    IP = ip;
                    startActivity(new Intent(this, MainActivity.class));
                }
            });
        }
    }

    private void newNameForDevice(String name, String ip) {
        try {
            jsonDevices.getJSONObject(ip).put("deviceName", name);
            updateJSONDevices(jsonDevices);
        } catch (Exception e) {
            print("Error newNameForDevice " + e);
        }
    }
}
