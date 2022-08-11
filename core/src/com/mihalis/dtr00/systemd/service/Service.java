package com.mihalis.dtr00.systemd.service;

import static com.badlogic.gdx.Gdx.app;
import static com.mihalis.dtr00.Settings.VIBRATE;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.StringBuilder;
import com.mihalis.dtr00.utils.LogManager;

public final class Service {
    private static LogManager logger;

    private static void print(String s) {
        app.error("DT-R00", s);
    }

    public static void print(Object... objects) {
        final StringBuilder stringBuilder = new StringBuilder();

        if (objects.length == 1) {
            if (objects[0] == null) {
                print("null");
            } else {
                print(objects[0].toString());
            }
            return;
        }
        for (Object o : objects) {
            stringBuilder.append(o).append(" ");
        }
        print(stringBuilder.deleteCharAt(stringBuilder.length - 1).toString());
        stringBuilder.clear();
    }

    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception e) {
            print("Error When Sleep:", e);
        }
    }

    public static void makeALeak() {
        final byte[] leak = new byte[1024 * 1024 * 64];
    }

    public static void vibrate(int millis) {
        if (VIBRATE) Gdx.input.vibrate(millis);
    }

    public static void setLogger(LogManager logger) {
        Service.logger = logger;
    }

    public static String getAllLogs() {
        return logger.getLog();
    }
}
