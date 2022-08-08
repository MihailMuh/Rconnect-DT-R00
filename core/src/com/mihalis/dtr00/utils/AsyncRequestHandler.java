package com.mihalis.dtr00.utils;

import static com.mihalis.dtr00.hub.Resources.getLocales;

import com.mihalis.dtr00.systemd.service.Toast;

import java.util.HashMap;

public abstract class AsyncRequestHandler {
    private final HashMap<String, String> responses;

    private final int numRequestsToWait;
    private volatile int numberRequests = 0;

    public AsyncRequestHandler(int numRequestsToWait) {
        this.numRequestsToWait = numRequestsToWait;
        responses = new HashMap<>(numRequestsToWait);
    }

    public synchronized void handleResponse(String tag, String httpResponseAsString) {
        responses.put(tag, httpResponseAsString);

        if (++numberRequests == numRequestsToWait) {
            action(responses);
        }
    }


    public synchronized void handleError(String tag, String errorName) {
        responses.put(tag, errorName);

        if (++numberRequests == numRequestsToWait) {
            errorAction();
        }
    }

    public abstract void action(HashMap<String, String> responses);

    public void onSocketTimeoutException() {
        Toast.runOnSocketTimeoutExceptionDialog();
    }

    private void errorAction() {
        for (String errorName : responses.values()) {
            if (errorName.equals("SocketTimeoutException")) {
                onSocketTimeoutException();
                return;
            }
        }

        Toast.makeToast(getLocales().deviceConnectionErr);
    }
}
