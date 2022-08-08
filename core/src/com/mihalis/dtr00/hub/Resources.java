package com.mihalis.dtr00.hub;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mihalis.dtr00.utils.SpriteBatchSuper;

public class Resources {
    private static ImageHub imageHub;
    private static FontHub fontHub;
    private static LocaleHub localeHub;
    private static StyleHub styleHub;

    private static Camera camera;
    private static Viewport viewport;
    private static SpriteBatchSuper spriteBatch;
    private static Stage stage;

    public static void setProviders(ImageHub image, FontHub font, LocaleHub locales, StyleHub styles) {
        imageHub = image;
        fontHub = font;
        localeHub = locales;
        styleHub = styles;
    }

    public static void setFrontendUtils(Viewport viewport, SpriteBatchSuper spriteBatch, Camera camera) {
        Resources.viewport = viewport;
        Resources.spriteBatch = spriteBatch;
        Resources.camera = camera;
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

    public static SpriteBatchSuper getSpriteBatch() {
        return spriteBatch;
    }

    public static StyleHub getStyles() {
        return styleHub;
    }

    public static Camera getCamera() {
        return camera;
    }

    public static Stage getStage() {
        return stage;
    }

    public static void setStage(Stage stage) {
        Resources.stage = stage;
    }
}
