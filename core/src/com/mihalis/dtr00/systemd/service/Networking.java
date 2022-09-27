package com.mihalis.dtr00.systemd.service;

import static com.badlogic.gdx.Net.HttpMethods.GET;
import static com.badlogic.gdx.Net.HttpMethods.POST;
import static com.mihalis.dtr00.systemd.service.Service.print;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.mihalis.dtr00.utils.AsyncRequestHandler;

import java.util.HashMap;

public final class Networking {
    private static String IP_ADDRESS;

    private static HttpRequest getRequest(String url) {
        HttpRequest request = new HttpRequestBuilder()
                .newRequest()
                .method(GET)
                .url(url)
                .build();
        request.setTimeOut(50);

        return request;
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
        Gdx.net.sendHttpRequest(getRequest(url), new Net.HttpResponseListener() {
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

            @Override
            public void cancelled() {

            }
        });
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
        HttpRequest request = new HttpRequestBuilder()
                .newRequest()
                .content("{\"report\":\"" + report.replace("\n", "*") + "\"}")
                .header("Content-Type", "application/json")
                .method(POST)
                .url("http://78.29.33.173:49144/email")
                .build();
        request.setTimeOut(5000);

        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
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

            @Override
            public void cancelled() {

            }
        });
    }
}
