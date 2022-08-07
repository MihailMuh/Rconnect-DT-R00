package com.mihalis.dtr00.systemd.service;

import com.mihalis.dtr00.utils.Observable;
import com.mihalis.dtr00.utils.Subscriber;

public final class Toast {
    private static final Observable<String> toastObservable = new Observable<>();

    public static void subscribe(Subscriber<String> subscriber) {
        toastObservable.subscribe(subscriber);
    }

    public static void makeToast(String text) {
        toastObservable.runEvent(text);
    }
}
