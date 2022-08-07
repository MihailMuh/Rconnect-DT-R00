package com.mihalis.dtr00.systemd;

import static com.mihalis.dtr00.hub.Resources.getFonts;
import static com.mihalis.dtr00.hub.Resources.getImages;
import static com.mihalis.dtr00.hub.Resources.getLocales;
import static com.mihalis.dtr00.hub.Resources.getStyles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.mihalis.dtr00.hub.AssetManagerSuper;
import com.mihalis.dtr00.hub.FontHub;
import com.mihalis.dtr00.hub.ImageHub;
import com.mihalis.dtr00.hub.LocaleHub;
import com.mihalis.dtr00.hub.Resources;
import com.mihalis.dtr00.hub.StyleHub;
import com.mihalis.dtr00.scenes.DevicesScene;
import com.mihalis.dtr00.systemd.service.Networking;
import com.mihalis.dtr00.systemd.service.Processor;
import com.mihalis.dtr00.systemd.service.Watch;
import com.mihalis.dtr00.systemd.service.Windows;
import com.mihalis.dtr00.utils.BaseApp;
import com.mihalis.dtr00.utils.ScenesStack;

public class MainApp extends BaseApp {
    private final ScenesStack scenesStack = new ScenesStack();
    private final MainAppManager mainAppManager = new MainAppManager(scenesStack);
    private Frontend frontend;

    private Dialog dialog;

    AssetManagerSuper assetManager;

    @Override
    public void create() {
        super.create();
        assetManager = new AssetManagerSuper();

        Resources.setProviders(new ImageHub(assetManager), new FontHub(assetManager),
                new LocaleHub(assetManager), new StyleHub(assetManager));
        assetManager.finishLoading();
        getFonts().boot(); // frontend-у нужен canis для fps

        frontend = new Frontend(scenesStack);
        DevicesScene devicesScene = new DevicesScene(mainAppManager);

        assetManager.finishLoading();
        getImages().boot();
        getLocales().boot();
        getStyles().boot();

        devicesScene.create();
        scenesStack.push(devicesScene);

        getImages().lazyLoading();
        getFonts().lazyLoading();
        getLocales().lazyLoading();

        resume();
    }

    @Override
    public void render() {
        Watch.update();

        scenesStack.lastScene.update();

        frontend.render();
    }

    @Override
    public void resume() {
        Texture.setAssetManager(assetManager);
        scenesStack.resumeScene();
    }

    @Override
    public void pause() {
        scenesStack.pauseScene();
    }

    @Override
    public void resize(int width, int height) {
        Windows.refresh();
        frontend.resize(width, height);
    }

    @Override
    public void dispose() {
        frontend.dispose();
        assetManager.dispose();
        Processor.dispose();
    }
}
