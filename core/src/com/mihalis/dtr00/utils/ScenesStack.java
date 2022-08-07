package com.mihalis.dtr00.utils;

import com.badlogic.gdx.utils.Array;
import com.mihalis.dtr00.systemd.service.Processor;

import java.util.Iterator;

public class ScenesStack implements Iterable<Scene> {
    private final Array<Scene> scenes = new Array<>(true, 10, Scene.class);

    public Scene lastScene;
    public int size;

    public Scene[] toArray() {
        return scenes.toArray();
    }

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

    public void clear() {
        lastScene.pause();

        for (Iterator<Scene> iterator = iterator(); iterator.hasNext(); ) {
            Scene scene = iterator.next();
            Processor.postTask(scene::dispose);
            iterator.remove();
        }

        lastScene = null;
        size = 0;
    }

    public void pauseScene() {
        if (lastScene != null) lastScene.pause();
    }

    public void resumeScene() {
        if (lastScene != null) lastScene.resume();
    }

    @Override
    public Iterator<Scene> iterator() {
        return scenes.iterator();
    }
}
