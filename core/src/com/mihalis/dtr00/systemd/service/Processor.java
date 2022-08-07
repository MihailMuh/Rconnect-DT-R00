package com.mihalis.dtr00.systemd.service;

import static com.badlogic.gdx.Application.ApplicationType.Desktop;

import com.badlogic.gdx.Gdx;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class Processor {
    private static final ExecutorService pool = Executors.newWorkStealingPool();

    private static final ExecutorService looper = Executors.newSingleThreadExecutor();
    private static final ExecutorService looperTouches = Executors.newSingleThreadExecutor();

    public static Thread UIThread;

    public static void postTask(Runnable runnable) {
        pool.execute(runnable);
    }

    public static void postToLooper(Runnable runnable) {
        looper.execute(runnable);
    }

    public static void postToLooperTouches(Runnable runnable) {
        looperTouches.execute(runnable);
    }

    public static void runForDifferentOS(Runnable function) {
        if (Gdx.app.getType() == Desktop) {
            Gdx.app.postRunnable(function);
        } else {
            function.run();
        }
    }

    public static boolean isUIThread() {
        return Thread.currentThread() == UIThread;
    }

    public static void dispose() {
        pool.shutdown();
        looper.shutdown();
        looperTouches.shutdown();
    }
}
