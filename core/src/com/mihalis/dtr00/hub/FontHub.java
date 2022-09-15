package com.mihalis.dtr00.hub;

import static com.mihalis.dtr00.hub.Resources.getStyles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.mihalis.dtr00.systemd.service.Processor;
import com.mihalis.dtr00.utils.CollectionManipulator;

public class FontHub extends BaseHub {
    private static final String RUSSIAN = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя";

    public BitmapFont timesNewRoman, timesNewRomanHint, timeNewRomanForButtons;

    public FontHub(AssetManagerSuper assetManager) {
        super(assetManager);

        assetManager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(assetManager.resolver));
        assetManager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(assetManager.resolver));

        FreeTypeFontLoaderParameter params = new FreeTypeFontLoaderParameter();
        params.fontFileName = "fonts/times_new_roman.ttf";
        params.fontParameters.size = 90;
        params.fontParameters.minFilter = Texture.TextureFilter.MipMapLinearLinear;
        params.fontParameters.magFilter = Texture.TextureFilter.Linear;
        params.fontParameters.hinting = FreeTypeFontGenerator.Hinting.AutoFull;
        params.fontParameters.gamma = 0.5f;
        params.fontParameters.spaceX = -3;
        params.fontParameters.incremental = true;
        params.fontParameters.genMipMaps = true;
        params.fontParameters.characters += RUSSIAN;

        assetManager.load("times_new_roman.ttf", BitmapFont.class, params);

        loadTimesNewRomanHint();
        loadTimesNewRomanForButtons();
    }

    private void loadTimesNewRomanHint() {
        FreeTypeFontLoaderParameter params = new FreeTypeFontLoaderParameter();
        params.fontFileName = "fonts/times_new_roman.ttf";
        params.fontParameters.size = 60;
        params.fontParameters.minFilter = Texture.TextureFilter.MipMapLinearLinear;
        params.fontParameters.magFilter = Texture.TextureFilter.Linear;
        params.fontParameters.hinting = FreeTypeFontGenerator.Hinting.AutoFull;
        params.fontParameters.gamma = 0.5f;
        params.fontParameters.spaceX = -2;
        params.fontParameters.padTop = 7;
        params.fontParameters.incremental = true;
        params.fontParameters.genMipMaps = true;
        params.fontParameters.characters += RUSSIAN;

        assetManager.load("times_new_roman_hint.ttf", BitmapFont.class, params);
    }

    private void loadTimesNewRomanForButtons() {
        FreeTypeFontLoaderParameter params = new FreeTypeFontLoaderParameter();
        params.fontFileName = "fonts/times_new_roman.ttf";
        params.fontParameters.size = 55;
        params.fontParameters.minFilter = Texture.TextureFilter.MipMapLinearLinear;
        params.fontParameters.magFilter = Texture.TextureFilter.Linear;
        params.fontParameters.hinting = FreeTypeFontGenerator.Hinting.AutoFull;
        params.fontParameters.gamma = 0.5f;
        params.fontParameters.spaceX = -2;
        params.fontParameters.shadowOffsetX = 7;
        params.fontParameters.shadowOffsetY = 7;
        params.fontParameters.incremental = true;
        params.fontParameters.genMipMaps = true;
        params.fontParameters.characters += RUSSIAN;

        assetManager.load("times_new_roman_button.ttf", BitmapFont.class, params);
    }

    @Override
    public void boot() {
        timesNewRoman = assetManager.get("times_new_roman.ttf");
        timesNewRomanHint = assetManager.get("times_new_roman_hint.ttf");
        timeNewRomanForButtons = assetManager.get("times_new_roman_button.ttf");
    }

    public static float resizeFont(BitmapFont font, float maxWidth, String... texts) {
        if (!Processor.isUIThread()) throw new GdxRuntimeException("No OpenGL context found!");

        GlyphLayout glyph = new GlyphLayout();
        glyph.setText(font, CollectionManipulator.getLongestString(texts));

        return maxWidth / glyph.width;
    }

    private static GlyphLayout getGlyphLayout(Label label) {
        if (!Processor.isUIThread()) throw new GdxRuntimeException("No OpenGL context found!");

        GlyphLayout glyph = new GlyphLayout();
        glyph.setText(label.getStyle().font, label.getText());

        return glyph;
    }

    public static float getTextWidth(Label label) {
        return getGlyphLayout(label).width * label.getFontScaleX();
    }

    public static float getTextWidth(String text) {
        return getTextWidth(new Label(text, getStyles().labelStyle));
    }

    public static float getTextWidth(String text, BitmapFont font) {
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        return getTextWidth(new Label(text, labelStyle));
    }

    public static float getTextHeight(Label label) {
        return getGlyphLayout(label).height * label.getFontScaleY();
    }

    public static float getTextHeight(String text) {
        return getTextHeight(new Label(text, getStyles().labelStyle));
    }
}
