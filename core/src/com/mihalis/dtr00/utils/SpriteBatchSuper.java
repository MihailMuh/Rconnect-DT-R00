package com.mihalis.dtr00.utils;

import com.badlogic.gdx.graphics.g2d.CpuSpriteBatch;

public class SpriteBatchSuper extends CpuSpriteBatch {
    public SpriteBatchSuper(int size) {
        super(size);
    }

    public void endSolidScreen() {
        end();
        enableBlending();
        begin();
    }
}
