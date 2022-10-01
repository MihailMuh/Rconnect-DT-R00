package com.mihalis.dtr00.systemd.service;

import com.badlogic.gdx.Gdx;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class Processor {
    private static final ExecutorService pool = Executors.newWorkStealingPool();
    private static final ScheduledThreadPoolExecutor scheduledPool = new ScheduledThreadPoolExecutor(16);

    public static Thread UIThread;

    public static Future postDelayed(Runnable runnable, int millis) {
        return new Future(scheduledPool.schedule(runnable, millis, TimeUnit.MILLISECONDS));
    }

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

    public static class Future {
        private final ScheduledFuture<?> scheduledFuture;

        public Future(ScheduledFuture<?> scheduledFuture) {
            this.scheduledFuture = scheduledFuture;
        }

        public Future() {
            scheduledFuture = null;
        }

        public void cancel() {
            if (scheduledFuture != null && !scheduledFuture.isDone()) {
                scheduledFuture.cancel(false);
            }
        }
    }
}
