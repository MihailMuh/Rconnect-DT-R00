package com.mihalis.dtr00.widgets;

import static com.badlogic.gdx.utils.Align.center;
import static com.mihalis.dtr00.hub.Resources.getImages;
import static com.mihalis.dtr00.systemd.service.Windows.SCREEN_WIDTH;
import static java.lang.Math.max;
import static java.lang.Math.min;

import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.mihalis.dtr00.hub.FontHub;

public class EditText extends TextField {
    private final int minWidth = getImages().editTextWidth * 2;
    private boolean growLeft;
    private float maxWidth;

    public EditText(TextFieldStyle style) {
        super(null, style);
        init();
    }

    public EditText(String text, TextFieldStyle style) {
        super(text, style);
        init();
    }

    private void init() {
        setAlignment(center);
        setGrowLeft(false);
        setMaxWidth(SCREEN_WIDTH);
        setSize(getTextWidth(), getImages().editTextHeight);
        setTextFieldListener((textField, character) -> setWidth(getTextWidth()));
    }

    private float getTextWidth() {
        return min(maxWidth, max(FontHub.getTextWidth(getText()), FontHub.getTextWidth(getMessageText())) + minWidth);
    }

    public void setGrowLeft(boolean growLeft) {
        this.growLeft = growLeft;
    }

    public void setMaxWidth(float maxWidth) {
        this.maxWidth = maxWidth;
    }

    @Override
    public void setMessageText(String messageText) {
        super.setMessageText(messageText);
        setWidth(getTextWidth());
    }

    @Override
    public void setWidth(float width) {
        float oldWidth = getWidth();
        super.setWidth(width);

        if (!growLeft) {
            setX(getX() - (width - oldWidth) / 2f);
        }
    }
}
