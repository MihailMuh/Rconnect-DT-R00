package ru.warfare.esp8266.services;

public final class Time {
    public static void sleep(int secs) {
        sleepMillis(secs * 1000);
    }

    public static void sleepMillis(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Py.print("Sleep " + e.toString());
        }
    }
}
