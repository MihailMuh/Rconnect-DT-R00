package ru.warfare.esp8266.services;

import static android.content.Context.VIBRATOR_SERVICE;
import static android.os.VibrationEffect.createOneShot;

import ru.warfare.esp8266.activity.MainActivity;

public final class Vibrator {
    private static android.os.Vibrator vibrator;

    public static void init(MainActivity activity) {
        vibrator = (android.os.Vibrator) activity.getSystemService(VIBRATOR_SERVICE);
    }

    public static void vibrate(int millis) {
        vibrator.vibrate(createOneShot(millis, 255));
    }
}
