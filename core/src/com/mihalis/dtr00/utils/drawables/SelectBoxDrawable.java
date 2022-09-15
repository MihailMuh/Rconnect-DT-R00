package com.mihalis.dtr00.utils.drawables;

import static com.mihalis.dtr00.constants.Constant.SELECT_BOX_BOTTOM_HEIGHT;
import static com.mihalis.dtr00.constants.Constant.SELECT_BOX_LEFT_WIDTH;
import static com.mihalis.dtr00.hub.Resources.getImages;

public class SelectBoxDrawable extends EditTextDrawable {
    public SelectBoxDrawable(boolean focused) {
        super(focused);

        if (focused) {
            editTextRight = getImages().selectBoxFocusedRight;
            editTextLeft = getImages().selectBoxFocusedLeft;
        } else {
            editTextRight = getImages().selectBoxNonFocusedRight;
            editTextLeft = getImages().selectBoxNonFocusedLeft;
        }
    }

    @Override
    public float getLeftWidth() {
        return SELECT_BOX_LEFT_WIDTH;
    }

    @Override
    public float getBottomHeight() {
        return SELECT_BOX_BOTTOM_HEIGHT - 8;
    }
}
