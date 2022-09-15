package com.mihalis.dtr00.systemd;

import static com.mihalis.dtr00.systemd.service.Windows.SCREEN_HEIGHT;
import static com.mihalis.dtr00.systemd.service.Windows.SCREEN_WIDTH;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mihalis.dtr00.hub.Resources;
import com.mihalis.dtr00.utils.ScenesArray;

public class Frontend implements Disposable {
    private final ScenesArray scenesArray;

    private final OrthographicCamera camera = new OrthographicCamera(SCREEN_WIDTH, SCREEN_HEIGHT);
    private final FitViewport viewport = new FitViewport(SCREEN_WIDTH, SCREEN_HEIGHT, camera);
    private final SpriteBatch spriteBatch = new SpriteBatch(100);

    Frontend(ScenesArray scenesArray) {
        this.scenesArray = scenesArray;

        ExtendViewport extendViewport = new ExtendViewport(SCREEN_WIDTH, SCREEN_HEIGHT, camera);
        extendViewport.update(SCREEN_WIDTH, SCREEN_HEIGHT, true);
        Resources.setFrontendUtils(viewport, spriteBatch, extendViewport);
    }

    void resize(int width, int height) {
        viewport.update(width, height, true);
        spriteBatch.setProjectionMatrix(camera.combined);
    }

    public void render() {
        ScreenUtils.clear(Color.WHITE);

        scenesArray.renderScene();
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
    }
}
