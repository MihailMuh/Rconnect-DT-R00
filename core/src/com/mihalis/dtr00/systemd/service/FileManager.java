package com.mihalis.dtr00.systemd.service;

import static com.mihalis.dtr00.constants.Constant.DTR00_JSON;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.mihalis.dtr00.utils.jsonTypes.JsonFile;
import com.mihalis.dtr00.utils.jsonTypes.UserDevice;

import java.util.HashMap;

public final class FileManager {
    private static JsonFile jsonFile;

    public static void saveJsonFile() {
        String stringJson = new Json().toJson(jsonFile, JsonFile.class);

        Gdx.files.local(DTR00_JSON).writeString(stringJson, false);
    }

    public static void writeToJsonFile(String ip, UserDevice userDevice) {
        jsonFile.allUserDevices.put(ip, userDevice);

        saveJsonFile();
    }

    public static void readFromJsonFile() {
        if (!Gdx.files.local(DTR00_JSON).exists()) {
            jsonFile = new JsonFile();
            jsonFile.allUserDevices = new HashMap<>();
            saveJsonFile();
            return;
        }

        jsonFile = new Json().fromJson(JsonFile.class, Gdx.files.local(DTR00_JSON).readString());
    }

    public static JsonFile getJson() {
        return jsonFile;
    }

    public static HashMap<String, UserDevice> getUserDevicesData() {
        return jsonFile.allUserDevices;
    }
}
