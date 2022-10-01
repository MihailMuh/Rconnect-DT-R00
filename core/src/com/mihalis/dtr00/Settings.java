package com.mihalis.dtr00;

import com.badlogic.gdx.utils.Collections;

public final class Settings {
    public static final boolean VIBRATE = true;

    public static final boolean SHOW_ASSET_MANAGER_LOGS = false;

    public static final int TIMEOUT_MILLIS = 7000;
    public static final int WEAK_WIFI_TIME = 3000;

    static {
        Collections.allocateIterators = false;
    }
}
