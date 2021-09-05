package ru.warfare.esp8266.services;

import static ru.warfare.esp8266.services.Py.print;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public final class ClientServer {
    private static final OkHttpClient client = new OkHttpClient.Builder().connectTimeout(5000, TimeUnit.MILLISECONDS).build();
    public static String IP;

    private static void postToServer(String message) throws IOException {
        client.newCall(new Request.Builder().url("http://" + IP + "/relay_cgi.cgi?" + message + "&pwd=0&").build()).execute().close();
    }

    public static String getStatistics() throws Exception {
        Response response = client.newCall(new Request.Builder().url("http://" + IP + "/relay_cgi_load.cgi?").build()).execute();
        String s = response.body().string();
        response.close();
        return s;
    }

    public static void postToServer(byte type, int relay, int on, int time, Runnable onError) {
        try {
            postToServer("type=" + type + "&relay=" + relay + "&on=" + on + "&time=" + time);
        } catch (Exception e) {
            if (onError != null) {
                ThreadPool.postUI(onError);
            }
            print(e);
        }
    }
}
