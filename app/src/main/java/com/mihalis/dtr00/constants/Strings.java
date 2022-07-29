package com.mihalis.dtr00.constants;

import com.mihalis.dtr00.R;
import com.mihalis.dtr00.activity.BaseActivity;

import java.util.Locale;

public final class Strings {
    public static String RELAY;
    public static String ON, OFF;
    public static String ENABLE, DISABLE;
    public static String PULSE;
    public static String ADDRESS;
    public static String SAVE;
    public static String EXAMPLE;
    public static String QUIT;
    public static String INCORRECT_IP;
    public static String CURRENT_IP;
    public static String NO_WIFI;
    public static String SUCCESSFULLY_SAVED;
    public static String INCORRECT_VALUE;
    public static String ENABLE_AT;
    public static String SECONDS;
    public static String SETTINGS;
    public static String WAIT;
    public static String UNEXPECTED_ERROR;
    public static String I_ENABLE_WIFI;
    public static String DELAY;
    public static String HIDE;
    public static String SHOW;
    public static String INPUT_IP;
    public static String USER_NAME;
    public static String PASSWORD;
    public static String APPLY;
    public static String LOGIN;
    public static String INCORRECT_USER_PASSWD;
    public static String REMEMBER_ME;
    public static String ADD_DEVICE;
    public static String ENTER;
    public static String ERROR_ACCESSING_THE_RELAY;
    public static String CHANGE_LAYOUT;
    public static String TAP_TO_HIDE_WIDGET;

    public static void init(BaseActivity activity) {
        String[] strings;
        if (Locale.getDefault().getLanguage().equals("ru")) {
            strings = activity.resources.getStringArray(R.array.ru);
        } else {
            strings = activity.resources.getStringArray(R.array.en);
        }

        RELAY = strings[0];
        ON = strings[1];
        OFF = strings[2];
        ENABLE = strings[3];
        DISABLE = strings[4];
        PULSE = strings[5];
        ADDRESS = "IP " + strings[6];
        SAVE = strings[7];
        EXAMPLE = strings[8];
        QUIT = strings[9];
        INCORRECT_IP = strings[10] + " " + ADDRESS;
        CURRENT_IP = strings[11] + " IP:";
        NO_WIFI = strings[12];
        SUCCESSFULLY_SAVED = strings[13];
        INCORRECT_VALUE = strings[14];
        ENABLE_AT = strings[15];
        SECONDS = strings[16];
        SETTINGS = strings[17];
        WAIT = strings[18] + "...";
        UNEXPECTED_ERROR = strings[19];
        I_ENABLE_WIFI = strings[20];
        DELAY = strings[21] + ":";
        HIDE = strings[22];
        SHOW = strings[23];
        INPUT_IP = strings[24] + " 192.168.1.100:80";
        USER_NAME = strings[25];
        PASSWORD = strings[26];
        APPLY = strings[27];
        LOGIN = strings[28];
        INCORRECT_USER_PASSWD = strings[29];
        REMEMBER_ME = strings[30];
        ADD_DEVICE = strings[31];
        ENTER = strings[32];
        ERROR_ACCESSING_THE_RELAY = strings[33];
        CHANGE_LAYOUT = strings[34];
        TAP_TO_HIDE_WIDGET = strings[35];
    }
}
