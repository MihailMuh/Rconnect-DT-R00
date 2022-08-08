package com.mihalis.dtr00.systemd.service;

import static com.mihalis.dtr00.hub.Resources.getLocales;
import static com.mihalis.dtr00.hub.Resources.getStage;
import static com.mihalis.dtr00.hub.Resources.getStyles;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mihalis.dtr00.utils.Observable;
import com.mihalis.dtr00.utils.Subscriber;
import com.mihalis.dtr00.utils.ToastManager;
import com.mihalis.dtr00.widgets.AlertDialog;

public final class Toast {
    private static ToastManager toastManager;

    public static void makeToast(String text) {
        toastManager.makeToast(text);
    }

    public static void makeToast(String text, int millis) {
        toastManager.makeToast(text, millis);
    }

    public static void runOnSocketTimeoutExceptionDialog() {
        AlertDialog dialog = new AlertDialog(getLocales().socketTimeout, getStyles().dialogStyle);
        dialog.text(getLocales().boostInternet, 0.6f);
        dialog.button("OK", null, getStyles().textButtonStyle);
        dialog.show(getStage());
    }

    public static void setToastManager(ToastManager toastManager) {
        Toast.toastManager = toastManager;
    }
}
