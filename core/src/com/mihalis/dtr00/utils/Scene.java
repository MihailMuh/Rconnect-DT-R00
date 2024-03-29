package com.mihalis.dtr00.utils;

import static com.mihalis.dtr00.hub.Resources.getSpriteBatch;
import static com.mihalis.dtr00.hub.Resources.getViewport;
import static com.mihalis.dtr00.systemd.service.Watch.delta;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mihalis.dtr00.hub.Resources;
import com.mihalis.dtr00.systemd.MainAppManager;

public class Scene implements ApplicationListener {
    protected final MainAppManager mainAppManager;
    public final Stage stage;

    public volatile boolean onPause = false;

    public Scene(MainAppManager mainAppManager) {
        this.mainAppManager = mainAppManager;

        stage = new Stage(getViewport(), getSpriteBatch());
    }

    @Override
    public void create() {
        Resources.setStage(stage);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {
        onPause = true;
    }

    @Override
    public void resume() {
        onPause = false;

        Gdx.input.setInputProcessor(stage);
        Resources.setStage(stage);
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
        stage.dispose();
    }
}
