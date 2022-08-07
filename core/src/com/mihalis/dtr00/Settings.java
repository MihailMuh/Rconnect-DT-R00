package com.mihalis.dtr00;

import com.badlogic.gdx.utils.Collections;

public final class Settings {
    public static final boolean VIBRATE = true;

    public static final boolean SHOW_ASSET_MANAGER_LOGS = false;

    static {
        Collections.allocateIterators = false;
    }
}
