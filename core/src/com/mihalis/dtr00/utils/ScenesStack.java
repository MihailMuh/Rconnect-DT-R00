package com.mihalis.dtr00.utils;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import java.util.Iterator;

public class ScenesStack implements Iterable<Scene>, Disposable {
    private final Array<Scene> scenes = new Array<>(true, 10, Scene.class);
    private Scene lastScene;
    private int size;

    public void push(Scene scene) {
        lastScene = scene;
        scenes.add(scene);
        size = scenes.size;
    }

    public Scene pop() {
        final Array<Scene> scenes = this.scenes;

        if (size < 2) lastScene = null;
        else lastScene = scenes.items[size - 2];

        Scene removed = scenes.pop();
        size = scenes.size;

        return removed;
    }

    public void pauseScene() {
        if (lastScene != null) lastScene.pause();
    }

    public void resumeScene() {
        if (lastScene != null) lastScene.resume();
    }

    public void updateScene() {
        if (lastScene != null) lastScene.update();
    }

    public void renderScene() {
        if (lastScene != null) lastScene.render();
    }

    public void clear() {
        scenes.clear();
        lastScene = null;
        size = 0;
    }

    @Override
    public Iterator<Scene> iterator() {
        return scenes.iterator();
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
