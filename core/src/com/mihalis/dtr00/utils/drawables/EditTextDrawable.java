package com.mihalis.dtr00.utils.drawables;

import static com.mihalis.dtr00.hub.Resources.getImages;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class EditTextDrawable extends TextureRegionDrawable {
    protected final float editTextCenterWidth = getImages().editTextWidth;
    protected final float editTextCenterWidthDoubled = editTextCenterWidth * 2;
    protected final AtlasRegion editTextCenter;
    protected AtlasRegion editTextLeft;
    protected AtlasRegion editTextRight;

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
        float centerLength = width - editTextCenterWidthDoubled;
        batch.draw(editTextLeft, x, y, editTextCenterWidth, height);
        batch.draw(editTextCenter, x + editTextCenterWidth, y, centerLength, height);
        batch.draw(editTextRight, x + editTextCenterWidth + centerLength, y, editTextCenterWidth, height);
    }
}
