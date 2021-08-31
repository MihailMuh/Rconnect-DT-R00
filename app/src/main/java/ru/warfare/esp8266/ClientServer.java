package ru.warfare.esp8266;

import static ru.warfare.esp8266.Py.print;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public final class ClientServer {
    private static final OkHttpClient client = new OkHttpClient();
    public static String IP;

    private static void postToServer(String message) throws IOException {
        client.newCall(new Request.Builder().url("http://" + IP + "/relay_cgi.cgi?" + message + "&pwd=0&").build()).execute().close();
    }

    public static String getStatistics() {
        String s = null;
        try {
            Response response = client.newCall(new Request.Builder().url("http://" + IP + "/relay_cgi_load.cgi?").build()).execute();
            s = response.body().string();
            response.close();
        } catch (Exception e) {
            print(e);
        }
        return s;
    }

    public static void postBestScore(byte type, int relay, int on, int time) {
        try {
            postToServer("type=" + type + "&relay=" + relay + "&on=" + on + "&time=" + time);
        } catch (Exception e) {
            print(e);
        }
    }
}
