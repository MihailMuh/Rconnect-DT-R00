package ru.warfare.esp8266.services;

import android.util.Log;

public final class Py {
    public static void print(Object object) {
        try {
            Log.e("ESP8266", object.toString());
        } catch (Exception e) {
            print(e);
        }
    }
}
