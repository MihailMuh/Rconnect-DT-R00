package ru.warfare.esp8266;

import static ru.warfare.esp8266.services.Service.vibrate;

import android.view.View;
import android.view.View.OnClickListener;

public interface ButtonListener extends OnClickListener {
    void click();

    @Override
    default void onClick(View v) {
        vibrate(55);
        click();
    }
}
