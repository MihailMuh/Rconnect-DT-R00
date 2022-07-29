package com.mihalis.dtr00.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSON extends JSONObject {
    public JSON(String string) throws JSONException {
        super(string);
    }

    public JSON() {
    }

    @NonNull
    @Override
    public JSON put(@NonNull String name, boolean value) {
        try {
            super.put(name, value);
            return this;
        } catch (JSONException jsonException) {
            throw new RuntimeException(jsonException);
        }
    }

    @NonNull
    public JSON put(@NonNull String name, @Nullable Object value) {
        try {
            super.put(name, value);
            return this;
        } catch (JSONException jsonException) {
            throw new RuntimeException(jsonException);
        }
    }

    public JSON getJSON(@NonNull String name) {
        try {
            return new JSON(getJSONObject(name).toString());
        } catch (JSONException jsonException) {
            throw new RuntimeException(jsonException);
        }
    }

    public static JSONArray createJSONArray(Object array) {
        try {
            return new JSONArray(array);
        } catch (JSONException jsonException) {
            throw new RuntimeException(jsonException);
        }
    }
}
