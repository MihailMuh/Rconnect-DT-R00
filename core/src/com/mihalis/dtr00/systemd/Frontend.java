package com.mihalis.dtr00.systemd;

import static com.mihalis.dtr00.systemd.service.Windows.SCREEN_HEIGHT;
import static com.mihalis.dtr00.systemd.service.Windows.SCREEN_WIDTH;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mihalis.dtr00.hub.Resources;
import com.mihalis.dtr00.utils.ScenesStack;
import com.mihalis.dtr00.utils.SpriteBatchSuper;

public class Frontend implements Disposable {
    private final ScenesStack scenesStack;

    private final OrthographicCamera camera = new OrthographicCamera(SCREEN_WIDTH, SCREEN_HEIGHT);
    private final FitViewport viewport = new FitViewport(SCREEN_WIDTH, SCREEN_HEIGHT, camera);
    private final SpriteBatchSuper spriteBatch = new SpriteBatchSuper(100);

    Frontend(ScenesStack scenesStack) {
        this.scenesStack = scenesStack;
        Resources.setFrontendUtils(viewport, spriteBatch, camera);
    }

    void resize(int width, int height) {
        viewport.update(width, height, true);
        spriteBatch.setProjectionMatrix(camera.combined);
    }

    public void render() {
        ScreenUtils.clear(Color.WHITE);

        scenesStack.lastScene.render();
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
    }
}
