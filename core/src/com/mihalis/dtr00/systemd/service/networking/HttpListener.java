package com.mihalis.dtr00.systemd.service.networking;

import com.badlogic.gdx.Net;

public interface HttpListener extends Net.HttpResponseListener {
    @Override
    default void cancelled() {

    }
}