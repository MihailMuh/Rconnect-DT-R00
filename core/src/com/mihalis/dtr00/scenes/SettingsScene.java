package com.mihalis.dtr00.scenes;

import static com.badlogic.gdx.Input.Keys.BACK;
import static com.badlogic.gdx.utils.Align.center;
import static com.badlogic.gdx.utils.Align.left;
import static com.badlogic.gdx.utils.Align.right;
import static com.badlogic.gdx.utils.Align.top;
import static com.mihalis.dtr00.constants.Constant.WIDGETS_PAD;
import static com.mihalis.dtr00.constants.Constant.WIDGETS_START_X;
import static com.mihalis.dtr00.constants.Constant.WIDGETS_START_Y;
import static com.mihalis.dtr00.hub.Resources.getImages;
import static com.mihalis.dtr00.hub.Resources.getLocales;
import static com.mihalis.dtr00.hub.Resources.getStyles;
import static com.mihalis.dtr00.systemd.service.Networking.getIpAddress;
import static com.mihalis.dtr00.systemd.service.Windows.HALF_SCREEN_HEIGHT;
import static com.mihalis.dtr00.systemd.service.Windows.HALF_SCREEN_WIDTH;
import static com.mihalis.dtr00.systemd.service.Windows.SCREEN_HEIGHT;
import static com.mihalis.dtr00.utils.Intersector.underFinger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.mihalis.dtr00.hub.FontHub;
import com.mihalis.dtr00.systemd.MainAppManager;
import com.mihalis.dtr00.systemd.service.FileManager;
import com.mihalis.dtr00.systemd.service.Networking;
import com.mihalis.dtr00.systemd.service.Toast;
import com.mihalis.dtr00.utils.AsyncRequestHandler;
import com.mihalis.dtr00.utils.Scene;
import com.mihalis.dtr00.utils.UserDevice;
import com.mihalis.dtr00.widgets.Button;
import com.mihalis.dtr00.widgets.EditText;

import java.util.HashMap;

public class SettingsScene extends Scene {
    private final String oldIP = Networking.getIpAddress();
    private final UserDevice userDevice;
    private final Array<EditText> editRelayNames;
    private final Array<EditText> editRelayDelays;
    private EditText editIP;

    private final float maxYForWidget = SCREEN_HEIGHT - 40;
    private float lastTapY;

    public SettingsScene(MainAppManager mainAppManager, UserDevice userDevice) {
        super(mainAppManager);
        this.userDevice = userDevice;

        editRelayDelays = new Array<>(true, userDevice.countOfRelayChannels, EditText.class);
        editRelayNames = new Array<>(true, userDevice.countOfRelayChannels, EditText.class);
    }

    @Override
    public void create() {
        super.create();

        placeEditIP();
        placeCurrentIpHint();

        float x = WIDGETS_START_X;
        float y = WIDGETS_START_Y - 50;
        for (int i = 0; i < userDevice.countOfRelayChannels; i++) {
            placeEditText(userDevice.userSettings.relayNames[i], x, y, 450);
            placeDelayText(y + 20);
            placeEditText(String.valueOf(userDevice.userSettings.relayDelays[i]),
                    x + stage.getActors().peek().getX(right) + 20, y, 150);

            y -= 180;
        }

        placeSaveButton();
        placeCancelButton();
        setStageListener();
    }

    private void placeCancelButton() {
        Button buttonCancel = new Button(getLocales().cancel) {
            @Override
            public void onClick() {
                Networking.setIpAddress(oldIP);
                back();
            }
        };
        buttonCancel.setSize(getImages().buttonWidth * 1.4f, getImages().buttonHeight * 1.2f);
        buttonCancel.setX(HALF_SCREEN_WIDTH + WIDGETS_PAD, left);
        buttonCancel.setY(WIDGETS_PAD * 2);
        buttonCancel.setFontScale(1.2f);
        buttonCancel.setBottomPod(-5);

        stage.addActor(buttonCancel);
    }

    private void placeSaveButton() {
        Button buttonSave = new Button(getLocales().save) {
            @Override
            public void onClick() {
                if (!parseEditNames() || !parseEditDelays()) return;

                Networking.setIpAddress(editIP.getText());
                Networking.getRelayStatus(new AsyncRequestHandler(1) {
                    @Override
                    public void action(HashMap<String, String> responses) {
                        Toast.makeToast(getLocales().successfullySaved);
                        FileManager.writeToJsonFile(Networking.getIpAddress(), userDevice);
                        back();
                    }
                });
            }
        };
        buttonSave.setSize(getImages().buttonWidth * 1.4f, getImages().buttonHeight * 1.2f);
        buttonSave.setX(HALF_SCREEN_WIDTH - WIDGETS_PAD, right);
        buttonSave.setY(WIDGETS_PAD * 2);
        buttonSave.setFontScale(1.2f);
        buttonSave.setBottomPod(-5);

        stage.addActor(buttonSave);
    }

    private boolean parseEditNames() {
        int i = 0;
        for (EditText editRelayName : editRelayNames) {
            String text = editRelayName.getText();
            if (text.length() == 0) {
                Toast.makeToast(getLocales().filledAllFields);
                return false;
            }
            userDevice.userSettings.relayNames[i++] = text;
        }
        return true;
    }

    private boolean parseEditDelays() {
        int i = 0;
        for (EditText editRelayDelay : editRelayDelays) {
            String text = editRelayDelay.getText();
            if (text.length() == 0) {
                Toast.makeToast(getLocales().filledAllFields);
                return false;
            }
            userDevice.userSettings.relayDelays[i++] = Integer.parseInt(text);
        }
        return true;
    }

    private void placeDelayText(float y) {
        Label delayText = new Label(getLocales().delay, getStyles().labelStyle);
        delayText.setPosition(HALF_SCREEN_WIDTH + 50, y);
        delayText.setAlignment(center);
        delayText.setFontScale(0.8f);
        delayText.setWidth(FontHub.getTextWidth(delayText));

        stage.addActor(delayText);
    }

    private void placeEditText(String name, float x, float y, float maxWidth) {
        EditText editText = new EditText(name, getStyles().editTextStyle);
        editText.setPosition(x, y);
        editText.setGrowLeft(true);
        editText.setMaxWidth(maxWidth);

        if (maxWidth > 300) {
            editRelayNames.add(editText);
        } else {
            editRelayDelays.add(editText);
        }
        stage.addActor(editText);
    }

    private void placeCurrentIpHint() {
        Label deviceNameHint = new Label(getLocales().currentIP, getStyles().hintStyle);
        deviceNameHint.setPosition(HALF_SCREEN_WIDTH, stage.getActors().peek().getY(top) + 45, center);
        deviceNameHint.setAlignment(center);

        stage.addActor(deviceNameHint);
    }

    private void placeEditIP() {
        editIP = new EditText(getIpAddress(), getStyles().editTextStyle);
        editIP.setPosition(HALF_SCREEN_WIDTH, SCREEN_HEIGHT - 180, center);

        stage.addActor(editIP);
    }

    private void back() {
        mainAppManager.replaceCurrentScene(new MainScene(mainAppManager, userDevice));
    }

    private void setStageListener() {
        stage.addListener(new ClickListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == BACK) {
                    Networking.setIpAddress(oldIP);
                    back();
                }
                return true;
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                for (EditText editText : editRelayNames) {
                    if (underFinger(editText, x, y)) return;
                }
                for (EditText editText : editRelayDelays) {
                    if (underFinger(editText, x, y)) return;
                }

                if (!underFinger(editIP, x, y)) {
                    Gdx.input.setOnscreenKeyboardVisible(false);
                    stage.setKeyboardFocus(null);

                    // возвращаем всех на свои позиции
                    float firstWidgetY = stage.getActors().get(1).getY(center);
                    for (Actor actor : stage.getActors()) {
                        actor.setY(actor.getY(center) - (firstWidgetY - maxYForWidget), center);
                    }
                }
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                SettingsScene.this.lastTapY = y;
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                super.touchDragged(event, x, y, pointer);
                if (stage.getKeyboardFocus() == null) return;

                float deltaY = y - SettingsScene.this.lastTapY;
                float firstWidgetY = stage.getActors().get(1).getY(center);
                float lastWidgetY = stage.getActors().peek().getY(center);

                if (firstWidgetY + deltaY < maxYForWidget || lastWidgetY + deltaY > HALF_SCREEN_HEIGHT) {
                    return;
                }

                for (Actor actor : stage.getActors()) {
                    actor.setY(actor.getY(center) + deltaY, center);
                }
                SettingsScene.this.lastTapY = y;
            }
        });
    }
}
