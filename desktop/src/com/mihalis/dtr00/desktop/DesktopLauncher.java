package com.mihalis.dtr00.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mihalis.dtr00.systemd.MainApp;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 300;
        config.height = 900;
//        config.fullscreen = true;
        config.useGL30 = true;
        config.foregroundFPS = 0;
        config.backgroundFPS = 0;

        new LwjglApplication(new MainApp(), config);
    }
}
