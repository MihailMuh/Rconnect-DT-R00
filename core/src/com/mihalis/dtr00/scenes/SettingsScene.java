package com.mihalis.dtr00.scenes;

import static com.badlogic.gdx.Input.Keys.BACK;
import static com.badlogic.gdx.utils.Align.center;
import static com.badlogic.gdx.utils.Align.left;
import static com.badlogic.gdx.utils.Align.right;
import static com.badlogic.gdx.utils.Align.top;
import static com.mihalis.dtr00.constants.Constant.WIDGETS_PAD;
import static com.mihalis.dtr00.constants.Constant.WIDGETS_START_X;
import static com.mihalis.dtr00.constants.Constant.WIDGETS_START_Y;
import static com.mihalis.dtr00.hub.FontHub.getTextHeight;
import static com.mihalis.dtr00.hub.FontHub.getTextWidth;
import static com.mihalis.dtr00.hub.FontHub.resizeFont;
import static com.mihalis.dtr00.hub.Resources.getImages;
import static com.mihalis.dtr00.hub.Resources.getLocales;
import static com.mihalis.dtr00.hub.Resources.getStyles;
import static com.mihalis.dtr00.systemd.service.networking.NetworkManager.getIpAddress;
import static com.mihalis.dtr00.systemd.service.Service.vibrate;
import static com.mihalis.dtr00.systemd.service.Windows.HALF_SCREEN_HEIGHT;
import static com.mihalis.dtr00.systemd.service.Windows.HALF_SCREEN_WIDTH;
import static com.mihalis.dtr00.systemd.service.Windows.SCREEN_HEIGHT;
import static com.mihalis.dtr00.systemd.service.Windows.SCREEN_WIDTH;
import static com.mihalis.dtr00.utils.CollectionsManipulator.getLongestString;
import static com.mihalis.dtr00.utils.Intersector.underFinger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.mihalis.dtr00.systemd.MainAppManager;
import com.mihalis.dtr00.systemd.service.FileManager;
import com.mihalis.dtr00.systemd.service.networking.NetworkManager;
import com.mihalis.dtr00.systemd.service.Processor;
import com.mihalis.dtr00.systemd.service.Service;
import com.mihalis.dtr00.systemd.service.Toast;
import com.mihalis.dtr00.utils.AsyncRequestHandler;
import com.mihalis.dtr00.utils.Scene;
import com.mihalis.dtr00.utils.jsonTypes.UserDevice;
import com.mihalis.dtr00.widgets.AlertDialog;
import com.mihalis.dtr00.widgets.Button;
import com.mihalis.dtr00.widgets.EditText;

import java.util.HashMap;
import java.util.Map;

public class SettingsScene extends Scene {
    private static boolean logHarvested = false;

    private final String oldIP = NetworkManager.getIpAddress();
    private final UserDevice userDevice;
    private final Array<EditText> editRelayNames;
    private final Array<EditText> editRelayDelays;
    private final HashMap<String, String> relayNamesAndIPs = getRelaysNamesAsMap();
    private final String[] relayNames = relayNamesAndIPs.keySet().toArray(new String[0]);
    private EditText editIP;

    private Button buttonPostLogs, buttonBack;
    private CheckBox boxHideDevicesScene;

    private final float maxYForWidget = 2157.5f;
    private float lastTapY;

    private boolean isAlertToSetDeviceForAutoEnter = false;

    public SettingsScene(MainAppManager mainAppManager, UserDevice userDevice) {
        super(mainAppManager);
        this.userDevice = userDevice;

        editRelayDelays = new Array<>(true, userDevice.countOfRelayChannels, EditText.class);
        editRelayNames = new Array<>(true, userDevice.countOfRelayChannels, EditText.class);
    }

    private HashMap<String, String> getRelaysNamesAsMap() {
        final HashMap<String, UserDevice> allUserDevices = FileManager.getUserDevicesData();
        final HashMap<String, String> relaysNames = new HashMap<>(allUserDevices.size());

        for (Map.Entry<String, UserDevice> pair : allUserDevices.entrySet()) {
            relaysNames.put(pair.getValue().deviceName, pair.getKey());
        }

        return relaysNames;
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
        placeFurtherButton();
        setStageListener();
    }

    private void placePostLogsButton(float y) {
        buttonPostLogs = new Button(getLocales().postLogs) {
            @Override
            public void onClick() {
                NetworkManager.postErrorReport(Service.getAllLogs(), () -> {
                    logHarvested = true;
                    buttonPostLogs.activate(false);
                    Toast.makeToast(getLocales().thankYou);
                });
            }
        };
        buttonPostLogs.setSize(getImages().buttonWidth * 1.7f, getImages().buttonHeight * 1.6f);
        buttonPostLogs.setX(SCREEN_WIDTH + HALF_SCREEN_WIDTH, center);
        buttonPostLogs.setY(y, top);
        buttonPostLogs.setWrap(true);
        buttonPostLogs.setBottomPod(-8);
        buttonPostLogs.activate(!logHarvested);

        stage.addActor(buttonPostLogs);
    }

    private void placeHideDevicesSceneBox() {
        boxHideDevicesScene = new CheckBox(getLocales().hideDevicesScene, getStyles().checkBoxStyle);
        boxHideDevicesScene.getLabel().setFontScale(resizeFont(boxHideDevicesScene.getLabel().getStyle().font,
                SCREEN_WIDTH - 200, getLocales().hideDevicesScene));
        boxHideDevicesScene.setSize(SCREEN_WIDTH, getTextHeight(boxHideDevicesScene.getLabel()));
        boxHideDevicesScene.setPosition(SCREEN_WIDTH + HALF_SCREEN_WIDTH, SCREEN_HEIGHT - 180, center);
        boxHideDevicesScene.setChecked(FileManager.getJson().relayIPToAutoEnter != null);
        boxHideDevicesScene.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                vibrate(50);
                if (boxHideDevicesScene.isChecked()) {
                    showAlertChooseRelayToAutoEnter(boxHideDevicesScene);
                } else {
                    FileManager.getJson().relayIPToAutoEnter = null;
                }
            }
        });

        stage.addActor(boxHideDevicesScene);
    }

    private void showAlertChooseRelayToAutoEnter(CheckBox checkBox) {
        SelectBox<String> selectBox = getSelectRelayBox();
        isAlertToSetDeviceForAutoEnter = true;

        AlertDialog dialog = new AlertDialog(getLocales().deviceToAutomatic, getStyles().dialogStyle) {
            @Override
            protected void result(Object object) {
                if ("OK".equals(object)) {
                    FileManager.getJson().relayIPToAutoEnter = relayNamesAndIPs.get(selectBox.getSelected());
                    FileManager.saveJsonFile();
                } else if ("CANCEL".equals(object)) {
                    checkBox.setChecked(false);
                }
                isAlertToSetDeviceForAutoEnter = false;
            }
        };
        dialog.getTitleLabel().setFontScale(1.5f);
        dialog.button(getLocales().cancel, "CANCEL", getStyles().textButtonStyle);
        dialog.button("OK", "OK", getStyles().textButtonStyle);
        dialog.getTitleTable().padBottom(-100);
        dialog.getContentTable().add(selectBox);
        dialog.getContentTable().padTop(150);

        dialog.show(stage);
    }

    private SelectBox<String> getSelectRelayBox() {
        float selectBoxWidth = getTextWidth(getLongestString(relayNames), getStyles().selectBoxStyle.font) + 150;

        SelectBox<String> selectBox = new SelectBox<String>(getStyles().selectBoxStyle) {
            @Override
            public float getPrefWidth() {
                super.getPrefWidth();
                return selectBoxWidth;
            }

            @Override
            public float getPrefHeight() {
                super.getPrefHeight();
                return 135;
            }
        };
        selectBox.setItems(relayNames);

        return selectBox;
    }

    private void placeBackButton() {
        buttonBack = new Button(getLocales().back) {
            @Override
            public void onClick() {
                Gdx.input.setInputProcessor(null);

                Processor.postTask(() -> {
                    moveWidgetsEachFrameOn(-61);

                    Processor.postToGDX(() -> {
                        stage.getActors().removeValue(buttonBack, true);
                        stage.getActors().removeValue(buttonPostLogs, true);
                        stage.getActors().removeValue(boxHideDevicesScene, true);
                        resetWidgetPositionsAfterKeyboard();

                        Gdx.input.setInputProcessor(stage);
                    });
                });
            }
        };
        buttonBack.setSize(getImages().buttonWidth * 1.4f, getImages().buttonHeight * 1.2f);
        buttonBack.setX(SCREEN_WIDTH + HALF_SCREEN_WIDTH, center);
        buttonBack.setY(WIDGETS_PAD * 3.5f);
        buttonBack.setFontScale(1.2f);

        stage.addActor(buttonBack);
    }

    private void placeFurtherButton() {
        Button buttonFurther = new Button(getLocales().further) {
            @Override
            public void onClick() {
                resetWidgetPositionsAfterKeyboard();
                placeHideDevicesSceneBox();
                placePostLogsButton(stage.getActors().peek().getY() - 150);
                placeBackButton();

                Gdx.input.setInputProcessor(null);
                Processor.postTask(() -> {
                    moveWidgetsEachFrameOn(61);

                    Gdx.input.setInputProcessor(stage);
                });
            }
        };
        buttonFurther.setSize(getImages().buttonWidth * 1.4f, getImages().buttonHeight * 1.2f);
        buttonFurther.setX(HALF_SCREEN_WIDTH + WIDGETS_PAD, left);
        buttonFurther.setY(WIDGETS_PAD * 2);
        buttonFurther.setFontScale(1.2f);

        stage.addActor(buttonFurther);
    }

    private void moveWidgetsEachFrameOn(int deltaX) {
        for (int i = 0; i < SCREEN_WIDTH / 60; i++) {
            Service.sleep(17);
            Processor.postToGDX(() -> {
                for (Actor actor : stage.getActors()) {
                    actor.setX(actor.getX() - deltaX);
                }
            });
        }
    }

    private void placeSaveButton() {
        Button buttonSave = new Button(getLocales().save) {
            @Override
            public void onClick() {
                if (!parseEditNames() || !parseEditDelays()) return;

                NetworkManager.setIpAddress(editIP.getText());
                NetworkManager.getRelayStatus(new AsyncRequestHandler(1) {
                    @Override
                    public void action(HashMap<String, String> responses) {
                        Toast.makeToast(getLocales().successfullySaved);
                        FileManager.writeToJsonFile(NetworkManager.getIpAddress(), userDevice);
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
        for (int i = 0; i < editRelayNames.size; i++) {
            String text = editRelayNames.get(i).getText();
            if (text.length() == 0) {
                Toast.makeToast(getLocales().filledAllFields);
                return false;
            }
            userDevice.userSettings.relayNames[i] = text;
        }
        return true;
    }

    private boolean parseEditDelays() {
        for (int i = 0; i < editRelayDelays.size; i++) {
            String text = editRelayDelays.get(i).getText();
            if (text.length() == 0) {
                Toast.makeToast(getLocales().filledAllFields);
                return false;
            }
            userDevice.userSettings.relayDelays[i] = Integer.parseInt(text);
        }
        return true;
    }

    private void placeDelayText(float y) {
        Label delayText = new Label(getLocales().delay, getStyles().labelStyle);
        delayText.setPosition(HALF_SCREEN_WIDTH + 50, y);
        delayText.setAlignment(center);
        delayText.setFontScale(0.8f);
        delayText.setWidth(getTextWidth(delayText));

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
                    NetworkManager.setIpAddress(oldIP);
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
                    resetWidgetPositionsAfterKeyboard();
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
                if (stage.getKeyboardFocus() == null || isAlertToSetDeviceForAutoEnter) return;

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

    private void resetWidgetPositionsAfterKeyboard() {
        Gdx.input.setOnscreenKeyboardVisible(false);
        stage.setKeyboardFocus(null);

        float firstWidgetY = stage.getActors().get(1).getY(center);
        for (Actor actor : stage.getActors()) {
            actor.setY(actor.getY(center) - (firstWidgetY - maxYForWidget), center);
        }
    }
}
