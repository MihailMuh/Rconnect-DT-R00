package com.mihalis.dtr00.systemd;

import com.badlogic.gdx.Gdx;
import com.mihalis.dtr00.systemd.service.Processor;
import com.mihalis.dtr00.utils.Scene;
import com.mihalis.dtr00.utils.ScenesArray;

public class MainAppManager {
    private final ScenesArray scenesArray;

    public MainAppManager(ScenesArray scenesArray) {
        this.scenesArray = scenesArray;
    }

    public void finishAppIfOneSceneInStack() {
        if (scenesArray.size == 1) {
            Gdx.app.exit();
        } else {
            finishScene();
        }
    }

    public void finishScene() {
        Processor.postToGDX(() -> {
            Gdx.input.setOnscreenKeyboardVisible(false);

            Scene lastScene = scenesArray.pop();

            scenesArray.resumeScene();

            lastScene.pause();
            lastScene.dispose();
        });
    }

    public void replaceCurrentScene(Scene newScene) {
        Processor.postToGDX(() -> {
            Gdx.input.setOnscreenKeyboardVisible(false);

            newScene.create();

            Scene lastScene = scenesArray.pop();

            scenesArray.add(newScene);
            newScene.resume();

            lastScene.pause();
            lastScene.dispose();
        });
    }

    public void startScene(Scene scene) {
        Processor.postToGDX(() -> {
            scene.create();

            scenesArray.pauseScene();
            scenesArray.add(scene);

            scene.resume();
        });
    }
}
