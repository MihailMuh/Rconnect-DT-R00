package com.mihalis.dtr00.systemd.service;

import static com.badlogic.gdx.Net.HttpMethods.GET;
import static com.mihalis.dtr00.systemd.service.Service.print;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.mihalis.dtr00.utils.AsyncRequestHandler;

public final class Networking {
    private static String IP_ADDRESS;

    private static void baseResponse(String url, String tag, AsyncRequestHandler asyncRequestHandler) {
        HttpRequest request = new HttpRequestBuilder()
                .newRequest()
                .method(GET)
                .url(url)
                .build();
        request.setTimeOut(5000);
        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(HttpResponse httpResponse) {
                asyncRequestHandler.handleResponse(tag, httpResponse.getResultAsString());
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
        baseResponse("http://" + IP_ADDRESS + "/login.cgi?user=" + login + "&passwd=" + password + "&",
                password, asyncRequestHandler);
    }

    public static void getRelayStatus(AsyncRequestHandler asyncRequestHandler) {
        baseResponse("http://" + IP_ADDRESS + "/relay_cgi_load.cgi?", "relayStatus", asyncRequestHandler);
    }

    public static String getIpAddress() {
        return IP_ADDRESS;
    }

    public static void setIpAddress(String ipAddress) {
        IP_ADDRESS = ipAddress;
    }
}
