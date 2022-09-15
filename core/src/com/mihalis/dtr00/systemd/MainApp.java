package com.mihalis.dtr00.systemd;

import static com.mihalis.dtr00.hub.Resources.getFonts;
import static com.mihalis.dtr00.hub.Resources.getImages;
import static com.mihalis.dtr00.hub.Resources.getLocales;
import static com.mihalis.dtr00.hub.Resources.getSpriteBatch;
import static com.mihalis.dtr00.hub.Resources.getStyles;
import static com.mihalis.dtr00.systemd.service.FileManager.getJson;
import static com.mihalis.dtr00.systemd.service.Service.print;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.mihalis.dtr00.hub.AssetManagerSuper;
import com.mihalis.dtr00.hub.FontHub;
import com.mihalis.dtr00.hub.ImageHub;
import com.mihalis.dtr00.hub.LocaleHub;
import com.mihalis.dtr00.hub.Resources;
import com.mihalis.dtr00.hub.StyleHub;
import com.mihalis.dtr00.scenes.DevicesScene;
import com.mihalis.dtr00.scenes.ErrorScene;
import com.mihalis.dtr00.scenes.MainScene;
import com.mihalis.dtr00.systemd.service.Networking;
import com.mihalis.dtr00.systemd.service.Processor;
import com.mihalis.dtr00.systemd.service.Watch;
import com.mihalis.dtr00.systemd.service.Windows;
import com.mihalis.dtr00.utils.BaseApp;
import com.mihalis.dtr00.utils.ScenesArray;

public class MainApp extends BaseApp {
    private final ScenesArray scenesArray = new ScenesArray();
    private final MainAppManager mainAppManager = new MainAppManager(scenesArray);
    private Frontend frontend;
    private ErrorScene errorScene;

    AssetManagerSuper assetManager;

    @Override
    public void create() {
        super.create();
        assetManager = new AssetManagerSuper();
        frontend = new Frontend(scenesArray);

        Resources.setProviders(new ImageHub(assetManager), new FontHub(assetManager),
                new LocaleHub(assetManager), new StyleHub(assetManager));

        assetManager.finishLoading();
        getImages().boot();
        getLocales().boot();
        getFonts().boot();
        getStyles().boot();

        if (getJson().relayIPToAutoEnter != null) {
            Networking.setIpAddress(getJson().relayIPToAutoEnter);
            mainAppManager.startScene(new MainScene(mainAppManager, getJson().allUserDevices.get(getJson().relayIPToAutoEnter)));
        } else {
            mainAppManager.startScene(new DevicesScene(mainAppManager));
        }

        createErrorScene();
    }

    private void createErrorScene() {
        errorScene = new ErrorScene(mainAppManager);

        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            print("Error in thread:", thread);
            Gdx.app.postRunnable(() -> onError(throwable));
        });
    }

    @Override
    public void render() {
        try {
            Watch.update();

            scenesArray.updateScene();

            frontend.render();
        } catch (IllegalStateException exception) {
            try {
                getSpriteBatch().end();
            } catch (Exception e) {
                exception.printStackTrace();
                onError(exception);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            onError(exception);
        }
    }

    private void onError(Throwable throwable) {
        if (errorScene.running) return;

        errorScene.prepare(throwable);
        try {
            mainAppManager.startScene(errorScene);
        } catch (Exception exception) {
            errorScene.startWithoutMainAppManager(scenesArray);
        }
    }

    @Override
    public void resume() {
        Texture.setAssetManager(assetManager);
        scenesArray.resumeScene();
    }

    @Override
    public void pause() {
        scenesArray.pauseScene();
    }

    @Override
    public void resize(int width, int height) {
        Windows.refresh();
        frontend.resize(width, height);
    }

    @Override
    public void dispose() {
        scenesArray.dispose();
        frontend.dispose();
        assetManager.dispose();
        Processor.dispose();
    }
}
