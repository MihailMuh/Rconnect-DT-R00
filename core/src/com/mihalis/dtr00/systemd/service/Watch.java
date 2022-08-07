package com.mihalis.dtr00.systemd.service;

import com.badlogic.gdx.Gdx;

public final class Watch {
    private static boolean stoppedTime = false;

    public static float delta, time, timeOnPause;
    public static int frameCount;

    public static void update() {
        delta = Gdx.graphics.getDeltaTime();

        if (!stoppedTime) {
            time += delta;
            frameCount++;
        } else {
            timeOnPause += delta;
        }
    }

    public static void stopTime() {
        stoppedTime = true;
        timeOnPause = 0;
        frameCount = 0;
    }

    public static void resetTime() {
        stoppedTime = false;
    }
}
