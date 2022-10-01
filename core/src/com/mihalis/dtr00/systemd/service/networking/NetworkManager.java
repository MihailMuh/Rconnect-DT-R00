package com.mihalis.dtr00.systemd.service.networking;

import static com.badlogic.gdx.Net.HttpMethods.GET;
import static com.badlogic.gdx.Net.HttpMethods.POST;
import static com.mihalis.dtr00.Settings.TIMEOUT_MILLIS;
import static com.mihalis.dtr00.systemd.service.Service.print;

import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.mihalis.dtr00.utils.AsyncRequestHandler;

import java.util.HashMap;

public final class NetworkManager {
    private static String IP_ADDRESS;

    private static HttpRequestBuilder getRequestBuilder(String url) {
        return new HttpRequestBuilder()
                .newRequest()
                .method(GET)
                .url(url)
                .timeout(TIMEOUT_MILLIS);
    }

    private static void responseWithRunnable(String url, Runnable runnable) {
        responseWithAsyncHandler(url, "action", new AsyncRequestHandler(1) {
            @Override
            public void action(HashMap<String, String> responses) {
                runnable.run();
            }
        });
    }

    private static void responseWithAsyncHandler(String url, String tag, AsyncRequestHandler asyncRequestHandler) {
        new SendRequestHandler(getRequestBuilder(url)) {
            @Override
            public void handleHttpResponse(HttpResponse httpResponse) {
                if (httpResponse.getStatus().getStatusCode() == -1) {
                    asyncRequestHandler.handleError(tag, "TimeOutIn_handleHttpResponse");
                } else {
                    asyncRequestHandler.handleResponse(tag, httpResponse.getResultAsString());
                }
            }

            @Override
            public void failed(Throwable throwable) {
                print("Networking Error");
                throwable.printStackTrace();

                asyncRequestHandler.handleError(tag, throwable.getClass().getSimpleName());
            }
        };
    }

    public static void login(String login, String password, AsyncRequestHandler asyncRequestHandler) {
        responseWithAsyncHandler("http://" + IP_ADDRESS + "/login.cgi?user=" + login + "&passwd=" + password + "&",
                password, asyncRequestHandler);
    }

    public static void getRelayStatus(AsyncRequestHandler asyncRequestHandler) {
        responseWithAsyncHandler("http://" + IP_ADDRESS + "/relay_cgi_load.cgi?", "relayStatus", asyncRequestHandler);
    }

    private static void postToDevice(String primaryUrl, Runnable runnable) {
        responseWithRunnable("http://" + IP_ADDRESS + "/relay_cgi.cgi?" + primaryUrl + "&pwd=0&", runnable);
    }

    public static void onRelay(int relayIndex, Runnable runnable) {
        postToDevice("type=0&relay=" + relayIndex + "&on=1&time=0", runnable);
    }

    public static void offRelay(int relayIndex, Runnable runnable) {
        postToDevice("type=0&relay=" + relayIndex + "&on=0&time=0", runnable);
    }

    public static void delayRelay(int relayIndex, int seconds, Runnable runnable) {
        postToDevice("type=2&relay=" + relayIndex + "&on=1&time=" + seconds, runnable);
    }

    public static String getIpAddress() {
        return IP_ADDRESS;
    }

    public static void setIpAddress(String ipAddress) {
        IP_ADDRESS = ipAddress;
    }

    public static void postErrorReport(String report, Runnable action) {
        new SendRequestHandler(getRequestBuilder("http://78.29.33.173:49144/email")
                .content("{\"report\":\"" + report.replace("\n", "*") + "\"}")
                .header("Content-Type", "application/json")
                .method(POST)) {
            @Override
            public void handleHttpResponse(HttpResponse httpResponse) {
                action.run();
            }

            @Override
            public void failed(Throwable throwable) {
                print("Networking Error");
                throwable.printStackTrace();

                new AsyncRequestHandler(1) {
                    @Override
                    public void action(HashMap<String, String> responses) {
                    }
                }.handleError("sendMail", throwable.getClass().getSimpleName());
            }
        };
    }
}
