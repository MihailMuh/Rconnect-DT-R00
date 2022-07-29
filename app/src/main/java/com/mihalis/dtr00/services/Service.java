package com.mihalis.dtr00.services;

import static android.content.Context.VIBRATOR_SERVICE;
import static android.os.VibrationEffect.createOneShot;

import android.content.Context;
import android.os.Vibrator;
import android.util.Log;

import com.mihalis.dtr00.activity.BaseActivity;
import com.mihalis.dtr00.utils.JSON;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class Service {
    private static final ExecutorService threadPool = Executors.newWorkStealingPool(8);
    private static Vibrator vibrator;

    public static void init(BaseActivity activity) {
        vibrator = (Vibrator) activity.getSystemService(VIBRATOR_SERVICE);
    }

    public static void post(Runnable runnable) {
        threadPool.execute(runnable);
    }

    private static void print(String s) {
        Log.e("DT-R00", s);
    }

    public synchronized static void print(Object... objects) {
        var stringBuilder = new StringBuilder();
        try {
            if (objects.length == 1) {
                print(objects[0].toString());
                return;
            }
            for (Object o : objects) {
                stringBuilder.append(o).append(" ");
            }
            print(stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString());
        } catch (Exception exception) {
            print("Error When Log:", exception);
        }
    }

    public static void sleep(int secs) {
        sleepMillis(secs * 1000);
    }

    public static void sleepMillis(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            print("Sleep:", e);
            sleepMillis(millis);
        }
    }

    public static void vibrate(int millis) {
        vibrator.vibrate(createOneShot(millis, 255));
    }

    public static void writeToFile(BaseActivity activity, String fileName, JSON jsonObject) {
        try {
            OutputStreamWriter writer_str = new OutputStreamWriter(activity.openFileOutput(fileName, Context.MODE_PRIVATE));
            writer_str.write(jsonObject.toString());
            writer_str.close();
        } catch (Exception e) {
            print("Can't save", fileName, e);
        }
    }

    public static JSON readFromFile(BaseActivity activity, String fileName) {
        try {
            InputStreamReader reader_cooler = new InputStreamReader(activity.openFileInput(fileName));

            String string = new BufferedReader(reader_cooler).readLine();

            reader_cooler.close();

            return new JSON(string);
        } catch (Exception e) {
            var jsonObject = new JSON();

            print("Can't recovery", fileName, e);
            print("Creating new file...");
            writeToFile(activity, fileName, jsonObject);
            print("Successful");
            return jsonObject;
        }
    }
}
