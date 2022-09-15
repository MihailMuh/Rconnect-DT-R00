package com.mihalis.dtr00.utils;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class ScenesArray extends Array<Scene> implements Disposable {
    public ScenesArray() {
        super(true, 10, Scene.class);
    }

    public void pauseScene() {
        if (size > 0) peek().pause();
    }

    public void resumeScene() {
        if (size > 0) peek().resume();
    }

    public void updateScene() {
        if (size > 0) peek().update();
    }

    public void renderScene() {
        if (size > 0) peek().render();
    }

    @Override
    public void dispose() {
        pauseScene();

        for (Scene scene : this) {
            scene.dispose();
        }

        clear();
    }
}
