package com.mihalis.dtr00.systemd.service;

import com.badlogic.gdx.Gdx;

public final class Windows {
    public static int SCREEN_HEIGHT = 2225;
    public static int HALF_SCREEN_HEIGHT = SCREEN_HEIGHT / 2;

    public static int SCREEN_WIDTH = 1027;
    public static int HALF_SCREEN_WIDTH = SCREEN_WIDTH / 2;

    public static int DEVICE_WIDTH;
    public static int DEVICE_HEIGHT;

    public static void refresh() {
        DEVICE_WIDTH = Gdx.graphics.getWidth();
        DEVICE_HEIGHT = Gdx.graphics.getHeight();
    }
}
