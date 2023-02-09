package com.mihalis.dtr00.systemd.service.networking;

import static com.mihalis.dtr00.Settings.WEAK_WIFI_TIME;
import static com.mihalis.dtr00.hub.Resources.getLocales;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.mihalis.dtr00.systemd.service.Processor;
import com.mihalis.dtr00.systemd.service.Toast;

public abstract class SendRequestHandler implements Net.HttpResponseListener {
    private final Net.HttpRequest httpRequest;
    private final Processor.Future future;
    private static volatile boolean requestSent; // чтобы нескольких одинаковых тостов подряд не было

    public SendRequestHandler(HttpRequestBuilder requestBuilder) {
        httpRequest = requestBuilder.build();

        if (!requestSent) {
            future = Processor.postDelayed(() -> Toast.makeToast(getLocales().weakWifi), WEAK_WIFI_TIME);
            requestSent = true;
        } else {
            future = new Processor.Future();
        }

        sendRequest();
    }

    private void sendRequest() {
        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                SendRequestHandler.this.handleHttpResponse(httpResponse);
                future.cancel();
                requestSent = false;
            }

            @Override
            public void failed(Throwable throwable) {
                SendRequestHandler.this.failed(throwable);
                future.cancel();
                requestSent = false;
            }

            @Override
            public void cancelled() {
                SendRequestHandler.this.cancelled();
                future.cancel();
                requestSent = false;
            }
        });
    }

    @Override
    public void cancelled() {

    }
}
