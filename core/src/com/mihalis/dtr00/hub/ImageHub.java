package com.mihalis.dtr00.hub;

import static com.mihalis.dtr00.constants.Assets.IMAGES;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class ImageHub extends BaseHub {
    public AtlasRegion buttonPressed, buttonNotPressed, buttonPressedDeactivated, buttonNotPressedDeactivated;
    public int buttonWidth, buttonHeight;

    public AtlasRegion editTextRightFocused, editTextCenterFocused, editTextLeftFocused;
    public AtlasRegion editTextRightNonFocused, editTextCenterNonFocused, editTextLeftNonFocused;
    public int editTextWidth, editTextHeight;

    public AtlasRegion cursor;

    public AtlasRegion checkBoxFocused, checkBoxNonFocused;
    public int checkBoxWidth, checkBoxHeight;

    public AtlasRegion alert, dark;
    public int alertWidth, alertHeight;

    public AtlasRegion on, off;

    public ImageHub(AssetManagerSuper assetManager) {
        super(assetManager);

        assetManager.loadAtlas(IMAGES);
    }

    @Override
    public void boot() {
        TextureAtlas atlasWidgets = assetManager.get(IMAGES);

        buttonNotPressed = atlasWidgets.findRegion("button_not_pressed");
        buttonPressed = atlasWidgets.findRegion("button_pressed");
        buttonPressedDeactivated = atlasWidgets.findRegion("button_not_pressed_deactivated");
        buttonNotPressedDeactivated = atlasWidgets.findRegion("button_pressed_deactivated");

        buttonWidth = buttonNotPressed.originalWidth;
        buttonHeight = buttonNotPressed.originalHeight;

        cursor = atlasWidgets.findRegion("cursor");

        checkBoxFocused = atlasWidgets.findRegion("check_box_focused");
        checkBoxNonFocused = atlasWidgets.findRegion("check_box_non_focused");

        checkBoxWidth = checkBoxFocused.originalWidth;
        checkBoxHeight = checkBoxFocused.originalHeight;

        alert = atlasWidgets.findRegion("alert");
        dark = atlasWidgets.findRegion("dark");

        alertWidth = alert.originalWidth;
        alertHeight = alert.originalHeight;

        on = atlasWidgets.findRegion("on_img");
        off = atlasWidgets.findRegion("off_img");

        editTextLeftFocused = atlasWidgets.findRegion("edit_text_focused", 0);
        editTextCenterFocused = atlasWidgets.findRegion("edit_text_focused", 1);
        editTextRightFocused = atlasWidgets.findRegion("edit_text_focused", 2);

        editTextLeftNonFocused = atlasWidgets.findRegion("edit_text_non_focused", 0);
        editTextCenterNonFocused = atlasWidgets.findRegion("edit_text_non_focused", 1);
        editTextRightNonFocused = atlasWidgets.findRegion("edit_text_non_focused", 2);

        editTextWidth = editTextCenterFocused.originalWidth;
        editTextHeight = editTextCenterFocused.originalHeight;
    }
}
