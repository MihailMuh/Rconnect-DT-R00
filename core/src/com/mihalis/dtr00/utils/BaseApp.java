package com.mihalis.dtr00.utils;

import static com.badlogic.gdx.Input.Keys.BACK;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.mihalis.dtr00.systemd.service.Processor;
import com.mihalis.dtr00.systemd.service.Windows;

public abstract class BaseApp extends ApplicationAdapter {
    @Override
    public void create() {
        Gdx.input.setCatchKey(BACK, true);

        Windows.refresh();
        Processor.UIThread = Thread.currentThread();
    }
}
