package ru.warfare.esp8266.services;

import android.os.Handler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ThreadPool {
    private static final ExecutorService threadPool = Executors.newCachedThreadPool();
    private static final Handler handler = new Handler();

    public static void post(Runnable runnable) {
        threadPool.execute(runnable);
    }

    public static void postUI(Runnable runnable) {
        handler.post(runnable);
    }
}
