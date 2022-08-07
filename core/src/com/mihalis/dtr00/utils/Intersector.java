package com.mihalis.dtr00.utils;

import com.badlogic.gdx.scenes.scene2d.Actor;

public class Intersector {
    public static boolean underFinger(Actor actor, float x, float y) {
        return actor.getX() <= x && x <= actor.getRight() && actor.getY() <= y && y <= actor.getTop();
    }
}
