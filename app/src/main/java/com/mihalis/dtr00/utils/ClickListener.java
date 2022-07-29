package com.mihalis.dtr00.utils;

import static com.mihalis.dtr00.services.Service.vibrate;

import android.view.View;
import android.view.View.OnClickListener;

public interface ClickListener extends OnClickListener {
    void click();

    @Override
    default void onClick(View v) {
        vibrate(55);
        click();
    }
}
