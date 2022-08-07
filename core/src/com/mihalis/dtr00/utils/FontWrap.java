package com.mihalis.dtr00.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.BitmapFontData;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.mihalis.dtr00.hub.FontHub;
import com.mihalis.dtr00.systemd.service.Processor;

public class FontWrap {
    private final BitmapFont bitmapFont;
    private final BitmapFontData bitmapFontData;
    private final GlyphLayout glyph = new GlyphLayout();
    private float scale;

    private Color color = Color.WHITE;

    public FontWrap(BitmapFont font, float scale) {
        checkUIThread();

        this.scale = scale;
        bitmapFont = font;
        bitmapFontData = bitmapFont.getData();
    }

    public static FontWrap scaledFontWrap(BitmapFont font, float limit, String... strings) {
        return new FontWrap(font, FontHub.resizeFont(font, limit, strings));
    }

    private void checkUIThread() {
        if (!Processor.isUIThread()) throw new GdxRuntimeException("No OpenGL context found!");
    }

    private synchronized void setText(String text) {
        checkUIThread();

        bitmapFontData.setScale(scale);
        glyph.setText(bitmapFont, text);
        bitmapFontData.setScale(1);
    }

    public float getTextWidth(String text) {
        setText(text);
        return glyph.width;
    }

    public float getHalfTextWidth(String string) {
        return getTextWidth(string) / 2f;
    }

    public float getTextHeight(String text) {
        setText(text);
        return glyph.height;
    }

    public float getHalfTextHeight(String string) {
        return getTextHeight(string) / 2f;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void resetColor() {
        color = Color.WHITE;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void draw(Batch batch, float x, float y, String text) {
        final BitmapFont bitmapFont = this.bitmapFont;
        final BitmapFontData bitmapFontData = this.bitmapFontData;

        bitmapFontData.setScale(scale);
        bitmapFont.setColor(color);

        bitmapFont.draw(batch, text, x, y);

        bitmapFont.setColor(Color.WHITE);
        bitmapFontData.setScale(1);
    }
}
