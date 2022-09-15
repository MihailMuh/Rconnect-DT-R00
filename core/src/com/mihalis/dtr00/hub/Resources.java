package com.mihalis.dtr00.hub;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Resources {
    private static ImageHub imageHub;
    private static FontHub fontHub;
    private static LocaleHub localeHub;
    private static StyleHub styleHub;

    private static Viewport viewport;
    private static ExtendViewport extendViewport;
    private static SpriteBatch spriteBatch;
    private static Stage stage;

    public static void setProviders(ImageHub image, FontHub font, LocaleHub locales, StyleHub styles) {
        imageHub = image;
        fontHub = font;
        localeHub = locales;
        styleHub = styles;
    }

    public static void setFrontendUtils(Viewport viewport, SpriteBatch spriteBatch, ExtendViewport extendViewport) {
        Resources.viewport = viewport;
        Resources.spriteBatch = spriteBatch;
        Resources.extendViewport = extendViewport;
    }

    public static ImageHub getImages() {
        return imageHub;
    }

    public static FontHub getFonts() {
        return fontHub;
    }

    public static LocaleHub getLocales() {
        return localeHub;
    }

    public static Viewport getViewport() {
        return viewport;
    }

    public static SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    public static StyleHub getStyles() {
        return styleHub;
    }

    public static Stage getStage() {
        return stage;
    }

    public static void setStage(Stage stage) {
        Resources.stage = stage;
    }

    public static ExtendViewport getExtendViewport() {
        return extendViewport;
    }
}
