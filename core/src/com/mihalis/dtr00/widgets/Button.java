package com.mihalis.dtr00.widgets;

import static com.badlogic.gdx.utils.Align.center;
import static com.mihalis.dtr00.hub.Resources.getImages;
import static com.mihalis.dtr00.hub.Resources.getStyles;
import static com.mihalis.dtr00.systemd.service.Service.vibrate;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public abstract class Button extends Label {
    private final Sprite sprite = new Sprite();
    private AtlasRegion buttonPressed, buttonNotPressed;

    private boolean pressed = false, deactivated = false;
    private float pod;

    public Button() {
        this("");
    }

    public Button(String text) {
        super(text, getStyles().buttonStyle);
        setSize(getImages().buttonWidth, getImages().buttonHeight);
        activate(true);
        setAlignment(center);
        setListeners();
    }

    private void setListeners() {
        addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.exit(event, x, y, pointer, fromActor);
                pressed = true;
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                pressed = false;
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (deactivated) return;

                vibrate(50);
                onClick();
            }
        });
    }

    public abstract void onClick();

    public void activate(boolean activated) {
        deactivated = !activated;

        if (activated) {
            buttonPressed = getImages().buttonPressed;
            buttonNotPressed = getImages().buttonNotPressed;
        } else {
            buttonPressed = getImages().buttonNotPressedDeactivated;
            buttonNotPressed = getImages().buttonPressedDeactivated;
        }
    }

    public void changeActivated() {
        activate(deactivated);
    }

    public void setBottomPod(float pod) {
        this.pod = pod;
    }

    @Override
    protected void positionChanged() {
        sprite.setPosition(getX(), getY() - 13);
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        if (sprite != null) {
            sprite.setSize(getWidth(), getHeight());
        }
    }

    @Override
    public void setColor(float r, float g, float b, float a) {
        super.setColor(r, g, b, a);
        sprite.setColor(r, g, b, a);
    }

    public String getString() {
        return getText().toString();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (pressed) {
            sprite.setRegion(buttonPressed);
        } else {
            sprite.setRegion(buttonNotPressed);
        }
        sprite.draw(batch);

        setY(getY() - pod); // понизить текста на pod px
        if (pressed) {
            float y = getY();

            setY(y - 10);
            super.draw(batch, parentAlpha); // рисуем текст
            setY(y);
        } else {
            super.draw(batch, parentAlpha); // рисуем текст
        }
        setY(getY() + pod);
    }
}
