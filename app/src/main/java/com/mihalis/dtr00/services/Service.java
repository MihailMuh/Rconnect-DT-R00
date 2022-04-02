package com.mihalis.dtr00.services;

import static android.content.Context.VIBRATOR_SERVICE;
import static android.os.VibrationEffect.createOneShot;

import android.content.Context;
import android.os.Vibrator;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.mihalis.dtr00.activity.MainActivity;

public final class Service {
    private static final ExecutorService threadPool = Executors.newCachedThreadPool();
    private static Vibrator vibrator;

    public static MainActivity activity;

    public static volatile int numRegisterActivity  = 0;
    public static volatile int numMainActivity  = 0;
    public static volatile int numSettingsActivity  = 0;

    public static void init(MainActivity mainActivity) {
        activity = mainActivity;
        vibrator = (Vibrator) mainActivity.getSystemService(VIBRATOR_SERVICE);
    }

    public static void post(Runnable runnable) {
        threadPool.execute(runnable);
    }

    public static void print(Object object) {
        try {
            Log.e("ESP8266", object.toString());
        } catch (Exception e) {
            print(e);
        }
    }

    public static void sleep(int secs) {
        sleepMillis(secs * 1000);
    }

    public static void sleepMillis(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            print("Sleep " + e);
            sleepMillis(millis);
        }
    }

    public static void vibrate(int millis) {
        vibrator.vibrate(createOneShot(millis, 255));
    }

    public static void writeToFile(String fileName, Object content) {
        try {
            OutputStreamWriter writer_str = new OutputStreamWriter(
                    activity.openFileOutput(fileName, Context.MODE_PRIVATE));

            writer_str.write(content.toString());
            writer_str.close();
        } catch (Exception e) {
            print("Can't save " + fileName + " " + e);
        }
    }

    public static String readFromFile(String fileName) {
        try {
            InputStreamReader reader_cooler = new InputStreamReader(activity.openFileInput(fileName));

            String string = new BufferedReader(reader_cooler).readLine();

            reader_cooler.close();

            return string;
        } catch (Exception e) {
            print("Can't recovery " + fileName + " " + e);
            print("Creating new file...");
            writeToFile(fileName, "");
            print("Successful");
            return null;
        }
    }
}
