package com.mihalis.dtr00.systemd.service;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.mihalis.dtr00.utils.UserDevice;

import java.util.HashMap;

public final class FileManager {
    private static HashMap<String, UserDevice> allUserDevices;

    public static void saveJsonFile() {
        String stringJson = new Json().toJson(allUserDevices);

        Gdx.files.local("DT-R00.json").writeString(stringJson, false);
    }

    public static void writeToJsonFile(String ip, UserDevice userDevice) {
        allUserDevices.put(ip, userDevice);

        saveJsonFile();
    }

    public static void readFromJsonFile() {
        if (!Gdx.files.local("DT-R00.json").exists()) {
            Gdx.files.local("DT-R00.json").writeString("", false);
            return;
        }
        allUserDevices = new Json().fromJson(HashMap.class, Gdx.files.local("DT-R00.json").readString());
    }

    public static HashMap<String, UserDevice> getUserDevicesData() {
        if (allUserDevices == null) {
            allUserDevices = new HashMap<>();
        }
        return allUserDevices;
    }
}
