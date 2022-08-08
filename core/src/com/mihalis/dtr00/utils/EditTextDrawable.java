package com.mihalis.dtr00.utils;

import static com.mihalis.dtr00.hub.Resources.getImages;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class EditTextDrawable extends TextureRegionDrawable {
    private final float editTextWidth = getImages().editTextWidth;
    private final float editTextWidthDoubled = editTextWidth * 2;
    private final AtlasRegion editTextLeft;
    private final AtlasRegion editTextCenter;
    private final AtlasRegion editTextRight;

    public EditTextDrawable(boolean focused) {
        if (focused) {
            editTextLeft = getImages().editTextLeftFocused;
            editTextCenter = getImages().editTextCenterFocused;
            editTextRight = getImages().editTextRightFocused;
        } else {
            editTextLeft = getImages().editTextLeftNonFocused;
            editTextCenter = getImages().editTextCenterNonFocused;
            editTextRight = getImages().editTextRightNonFocused;
        }
    }

    @Override
    public void draw(Batch batch, float x, float y, float width, float height) {
        float centerLength = width - editTextWidthDoubled;
        batch.draw(editTextLeft, x, y, editTextWidth, height);
        batch.draw(editTextCenter, x + editTextWidth, y, centerLength, height);
        batch.draw(editTextRight, x + editTextWidth + centerLength, y, editTextWidth, height);
    }
}
