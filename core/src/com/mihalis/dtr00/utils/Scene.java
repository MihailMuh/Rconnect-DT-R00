package com.mihalis.dtr00.utils;

import static com.mihalis.dtr00.hub.Resources.getSpriteBatch;
import static com.mihalis.dtr00.hub.Resources.getViewport;
import static com.mihalis.dtr00.systemd.service.Watch.delta;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mihalis.dtr00.systemd.MainAppManager;

public class Scene implements ApplicationListener {
    protected final MainAppManager mainAppManager;
    public final Stage stage;

    public Scene(MainAppManager mainAppManager) {
        this.mainAppManager = mainAppManager;

        stage = new Stage(getViewport(), getSpriteBatch());
    }

    @Override
    public void create() {
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render() {
        stage.draw();
    }

    public void update() {
        stage.act(delta);
    }

    @Override
    public void dispose() {
        stage.clear();
    }
}
