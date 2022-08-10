package com.mihalis.dtr00.systemd;

import com.badlogic.gdx.Gdx;
import com.mihalis.dtr00.utils.Scene;
import com.mihalis.dtr00.utils.ScenesStack;

public class MainAppManager {
    private final ScenesStack scenesStack;

    public MainAppManager(ScenesStack scenesStack) {
        this.scenesStack = scenesStack;
    }

    public void finishScene() {
        Gdx.app.postRunnable(() -> {
            Gdx.input.setOnscreenKeyboardVisible(false);

            Scene lastScene = scenesStack.pop();

            scenesStack.resumeScene();

            lastScene.pause();
            lastScene.dispose();
        });
    }

    public void replaceCurrentScene(Scene newScene) {
        Gdx.app.postRunnable(() -> {
            Gdx.input.setOnscreenKeyboardVisible(false);

            newScene.create();

            Scene lastScene = scenesStack.pop();

            scenesStack.push(newScene);
            newScene.resume();

            lastScene.pause();
            lastScene.dispose();
        });
    }

    public void startScene(Scene scene) {
        Gdx.app.postRunnable(() -> {
            scene.create();

            scenesStack.pauseScene();
            scenesStack.push(scene);

            scene.resume();
        });
    }
}
