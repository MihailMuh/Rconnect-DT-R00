package com.mihalis.dtr00.services;

import static com.mihalis.dtr00.services.Service.post;
import static com.mihalis.dtr00.services.Service.print;

import com.mihalis.dtr00.activity.BaseActivity;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public final class ClientServer {
    private static final OkHttpClient client = new OkHttpClient();
    public static volatile String IP;

    private static void postToServer(String message) throws IOException {
        client.newCall(new Request.Builder().url("http://" + IP + "/relay_cgi.cgi?" + message + "&pwd=0&").build()).execute().close();
    }

    public static String getRelaysStatus() {
        try {
            Response response = client.newCall(new Request.Builder().url("http://" + IP + "/relay_cgi_load.cgi?").build()).execute();
            String s = response.body().string();
            response.close();
            return s;
        } catch (Exception e) {
            print("Can't get statistics " + e);
        }
        return "";
    }

    public static int login(String user, String password) {
        try {
            Response response = client.newCall(new Request.Builder().url("http://" + IP + "/login.cgi?user=" + user + "&passwd=" + password + "&").build()).execute();
            int answer = Integer.parseInt(response.body().string().split("&")[1]);
            response.close();
            return answer;
        } catch (Exception e) {
            print("Can't login " + e);
        }
        return 44_44_44;
    }

    public static void postToServer(BaseActivity activity, byte type, int relay, int on, int time, Runnable onSucceeded, Runnable onError) {
        post(() -> {
            try {
                postToServer("type=" + type + "&relay=" + relay + "&on=" + on + "&time=" + time);
                activity.runOnUiThread(onSucceeded);
            } catch (Exception e) {
                activity.runOnUiThread(onError);
                print(e);
            }
        });
    }
}
