package com.mihalis.dtr00.widgets;

import static com.mihalis.dtr00.hub.Resources.getImages;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class ImageView extends Actor {
    private final Sprite sprite = new Sprite();

    public ImageView() {
        setEnabled(false);
        setSize(getImages().off.originalWidth, getImages().off.originalHeight);
    }

    public void setEnabled(boolean enabled) {
        if (enabled) {
            sprite.setRegion(getImages().on);
        } else {
            sprite.setRegion(getImages().off);
        }
    }

    @Override
    protected void positionChanged() {
        sprite.setPosition(getX(), getY() - 13);
    }

    @Override
    protected void sizeChanged() {
        sprite.setSize(getWidth(), getHeight());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        sprite.draw(batch);
    }
}
