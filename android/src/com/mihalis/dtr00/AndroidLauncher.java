package com.mihalis.dtr00;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;
import static com.mihalis.dtr00.systemd.service.Toast.subscribe;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.mihalis.dtr00.systemd.MainApp;
import com.mihalis.dtr00.utils.Subscriber;

public class AndroidLauncher extends AndroidApplication implements Subscriber<String> {
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

        subscribe(this);

        initialize(new MainApp(), config);
    }

    @Override
    public void onEvent(String text) {
        runOnUiThread(() -> makeText(this, text, LENGTH_SHORT).show());
    }
}
