package com.mihalis.dtr00.hub;

import static com.mihalis.dtr00.hub.Resources.getFonts;
import static com.mihalis.dtr00.hub.Resources.getImages;
import static com.mihalis.dtr00.systemd.service.Windows.SCREEN_HEIGHT;
import static com.mihalis.dtr00.systemd.service.Windows.SCREEN_WIDTH;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class StyleHub extends BaseHub {
    public TextFieldStyle textFieldStyle = new TextFieldStyle();
    public LabelStyle labelStyle = new LabelStyle();
    public CheckBoxStyle checkBoxStyle = new CheckBoxStyle();
    public LabelStyle buttonStyle = new LabelStyle();
    public WindowStyle dialogStyle = new WindowStyle();
    public TextButtonStyle textButtonStyle = new TextButtonStyle();
    public LabelStyle hintStyle = new LabelStyle();

    public StyleHub(AssetManagerSuper assetManager) {
        super(assetManager);
    }

    @Override
    public void boot() {
        textFieldStyle.font = getFonts().timesNewRoman;
        textFieldStyle.fontColor = Color.BLACK;
        textFieldStyle.disabledFontColor = Color.valueOf("505050");
        textFieldStyle.background = new TextureRegionDrawable(getImages().editTextNonFocused);
        textFieldStyle.focusedBackground = new TextureRegionDrawable(getImages().editTextFocused);
        textFieldStyle.disabledBackground = new TextureRegionDrawable(getImages().editTextNonFocused);
        textFieldStyle.cursor = new TextureRegionDrawable(getImages().cursor);
        textFieldStyle.messageFont = getFonts().timesNewRomanHint;
        textFieldStyle.messageFontColor = Color.GRAY;

        labelStyle.font = getFonts().timesNewRoman;
        labelStyle.fontColor = Color.BLACK;

        checkBoxStyle.font = getFonts().timesNewRoman;
        checkBoxStyle.fontColor = Color.BLACK;
        checkBoxStyle.checkboxOn = new TextureRegionDrawable(getImages().checkBoxFocused);
        checkBoxStyle.checkboxOff = new TextureRegionDrawable(getImages().checkBoxNonFocused);

        buttonStyle.font = getFonts().timeNewRomanForButtons;
        buttonStyle.fontColor = Color.WHITE;

        dialogStyle.titleFont = getFonts().timesNewRomanHint;
        dialogStyle.titleFontColor = Color.BLACK;
        dialogStyle.background = new TextureRegionDrawable(getImages().alert);
        Sprite sprite = new Sprite(getImages().dark);
        sprite.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        dialogStyle.stageBackground = new SpriteDrawable(sprite);

        textButtonStyle.font = getFonts().timeNewRomanForButtons;
        textButtonStyle.fontColor = Color.WHITE;
        textButtonStyle.up = new TextureRegionDrawable(getImages().buttonNotPressed);
        textButtonStyle.over = new TextureRegionDrawable(getImages().buttonPressed);
        textButtonStyle.down = new TextureRegionDrawable(getImages().buttonPressed);

        hintStyle.font = getFonts().timesNewRomanHint;
        hintStyle.fontColor = Color.GRAY;
    }
}
