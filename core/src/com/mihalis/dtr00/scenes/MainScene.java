package com.mihalis.dtr00.scenes;

import static com.badlogic.gdx.Input.Keys.BACK;
import static com.badlogic.gdx.utils.Align.center;
import static com.badlogic.gdx.utils.Align.left;
import static com.badlogic.gdx.utils.Align.right;
import static com.mihalis.dtr00.hub.Resources.getImages;
import static com.mihalis.dtr00.hub.Resources.getLocales;
import static com.mihalis.dtr00.hub.Resources.getStyles;
import static com.mihalis.dtr00.systemd.service.Windows.HALF_SCREEN_WIDTH;
import static com.mihalis.dtr00.systemd.service.Windows.SCREEN_HEIGHT;
import static com.mihalis.dtr00.systemd.service.Windows.SCREEN_WIDTH;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.mihalis.dtr00.systemd.MainAppManager;
import com.mihalis.dtr00.utils.Scene;
import com.mihalis.dtr00.utils.UserDevice;
import com.mihalis.dtr00.widgets.Button;
import com.mihalis.dtr00.widgets.ImageView;

import java.util.Arrays;

public class MainScene extends Scene {
    private static final int WIDGETS_START_X = 50;
    private static final int WIDGETS_PAD = 40;

    private final UserDevice userDevice;
    private final Array<Array<Actor>> widgetMatrix;

    public MainScene(MainAppManager mainAppManager, UserDevice userDevice) {
        super(mainAppManager);

        this.userDevice = userDevice;
        widgetMatrix = new Array<>(true, userDevice.countOfRelayChannels, Array.class);
    }

    @Override
    public void create() {
        super.create();

        for (int i = 0; i < widgetMatrix.items.length; i++) {
            Array<Actor> widgetLine = new Array<>(true, 4, Actor.class);
            widgetLine.add(getRelayNameText(userDevice.userSettings.relayNames[i]));
            widgetLine.add(getButton(getLocales().on, () -> {
            }));
            widgetLine.add(getImage());
            widgetLine.add(getButton(getLocales().pulse, () -> {
            }));

            widgetMatrix.add(widgetLine);
        }

        placeDeviceNameView();
        placeButtonChangeVisibility();
        placeButtonSettings();
        setStageListener();
    }

    private void placeDeviceNameView() {
        Label deviceName = new Label(userDevice.deviceName, getStyles().labelStyle);
        deviceName.setPosition(HALF_SCREEN_WIDTH, SCREEN_HEIGHT - 110, center);
        deviceName.setAlignment(center);
        deviceName.setFontScale(1.2f);

        stage.addActor(deviceName);
    }

    private void placeButtonChangeVisibility() {
        Button buttonChangeVisibility = new Button(getLocales().changeLayout) {
            @Override
            public void onClick() {
            }
        };
        buttonChangeVisibility.setSize(getImages().buttonWidth * 1.75f, getImages().buttonHeight * 1.2f);
        buttonChangeVisibility.setX(WIDGETS_PAD, left);
        buttonChangeVisibility.setY(WIDGETS_PAD * 2);
        buttonChangeVisibility.setFontScale(1.2f);
        buttonChangeVisibility.setBottomPod(-5);

        stage.addActor(buttonChangeVisibility);
    }

    private void placeButtonSettings() {
        Button buttonSettings = new Button(getLocales().settings) {
            @Override
            public void onClick() {
            }
        };
        buttonSettings.setSize(getImages().buttonWidth * 1.4f, getImages().buttonHeight * 1.2f);
        buttonSettings.setX(SCREEN_WIDTH - WIDGETS_PAD, right);
        buttonSettings.setY(WIDGETS_PAD * 2);
        buttonSettings.setFontScale(1.2f);
        buttonSettings.setBottomPod(-5);

        stage.addActor(buttonSettings);
    }

    private Label getRelayNameText(String name) {
        Label relayName = new Label(name, getStyles().hintStyle);
        relayName.setColor(Color.BLACK);
        relayName.setAlignment(center);
        relayName.setFontScale(1.3f);
        stage.addActor(relayName);

        return relayName;
    }

    private ImageView getImage() {
        ImageView image = new ImageView();
        stage.addActor(image);

        return image;
    }

    private Button getButton(String text, Runnable runnable) {
        Button button = new Button(text) {
            @Override
            public void onClick() {
                runnable.run();
            }
        };
        stage.addActor(button);

        return button;
    }

    @Override
    public void update() {
        super.update();

        float y = SCREEN_HEIGHT - 450;
        for (Array<Actor> lineWidgets : widgetMatrix) {
            if (Arrays.stream(lineWidgets.items).noneMatch(Actor::isVisible)) continue;

            Actor relayName = lineWidgets.items[0];
            relayName.setPosition(WIDGETS_START_X, y + 35);

            Actor buttonOnOff = lineWidgets.items[1];
            buttonOnOff.setPosition(relayName.getX(right) + WIDGETS_PAD + 35, y);

            Actor image = lineWidgets.items[2];
            image.setPosition(buttonOnOff.getX(right) + WIDGETS_PAD, y);

            Actor buttonPulse = lineWidgets.items[3];
            buttonPulse.setPosition(image.getX(right) + WIDGETS_PAD, y);

            y = image.getY() - image.getWidth() - WIDGETS_PAD - 20;
        }
    }

    private void setStageListener() {
        stage.addListener(new ClickListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == BACK) {
                    mainAppManager.finishScene();
                }
                return true;
            }
        });
    }
}
