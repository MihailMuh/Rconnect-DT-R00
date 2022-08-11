package com.mihalis.dtr00.systemd.service;

import com.badlogic.gdx.Gdx;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class Processor {
    private static final ExecutorService pool = Executors.newWorkStealingPool();

    public static Thread UIThread;

    public static void postTask(Runnable runnable) {
        pool.execute(runnable);
    }

    public static void postToGDX(Runnable runnable) {
        Gdx.app.postRunnable(() -> {
            try {
                runnable.run();
            } catch (Exception exception) {
                postTask(() -> {
                    throw exception;
                });
            }
        });
    }

    public static boolean isUIThread() {
        return Thread.currentThread() == UIThread;
    }

    public static void dispose() {
        pool.shutdown();
    }
}
