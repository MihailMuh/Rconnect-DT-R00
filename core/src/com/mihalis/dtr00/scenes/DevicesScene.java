package com.mihalis.dtr00.scenes;

import static com.badlogic.gdx.Input.Keys.BACK;
import static com.badlogic.gdx.utils.Align.center;
import static com.badlogic.gdx.utils.Align.top;
import static com.mihalis.dtr00.hub.Resources.getImages;
import static com.mihalis.dtr00.hub.Resources.getLocales;
import static com.mihalis.dtr00.hub.Resources.getStyles;
import static com.mihalis.dtr00.systemd.service.Windows.HALF_SCREEN_WIDTH;
import static com.mihalis.dtr00.systemd.service.Windows.SCREEN_HEIGHT;
import static com.mihalis.dtr00.systemd.service.Windows.SCREEN_WIDTH;
import static com.mihalis.dtr00.utils.Intersector.underFinger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.mihalis.dtr00.scenes.register.RegisterScene;
import com.mihalis.dtr00.systemd.MainAppManager;
import com.mihalis.dtr00.systemd.service.FileManager;
import com.mihalis.dtr00.systemd.service.Networking;
import com.mihalis.dtr00.utils.Scene;
import com.mihalis.dtr00.utils.UserDevice;
import com.mihalis.dtr00.widgets.Button;
import com.mihalis.dtr00.widgets.EditText;

import java.util.HashMap;
import java.util.Map.Entry;

public class DevicesScene extends Scene {
    private Array<EditText> editTexts;

    private int numDevices;

    public DevicesScene(MainAppManager mainAppManager) {
        super(mainAppManager);
    }

    @Override
    public void create() {
        super.create();

        FileManager.readFromJsonFile();
    }

    @Override
    public void resume() {
        super.resume();

        stage.clear();
        editTexts = new Array<>(true, 4, EditText.class);

        placeButtonAddDevice();
        placeTextYourDevices();
        readAllUserDeviceData();
        setStageListener();
    }

    private void readAllUserDeviceData() {
        final HashMap<String, UserDevice> allUserDevices = FileManager.getUserDevicesData();
        float widgetsY = SCREEN_HEIGHT - 450;

        for (Entry<String, UserDevice> pair : allUserDevices.entrySet()) {
            placeButtonEnter(widgetsY, pair);
            placeEditDeviceName(pair.getValue().deviceName, widgetsY);
            placeTextHintOverEditText(stage.getActors().peek().getX(center), stage.getActors().peek().getY(top));

            widgetsY -= 350;
        }
    }

    private void placeButtonEnter(float y, Entry<String, UserDevice> pair) {
        Button buttonEnter = new Button(getLocales().enter) {
            @Override
            public void onClick() {
                Networking.setIpAddress(pair.getKey());
                mainAppManager.startScene(new MainScene(mainAppManager, pair.getValue()));
            }
        };
        buttonEnter.setSize(getImages().buttonWidth, getImages().buttonHeight);
        buttonEnter.setX(0);
        buttonEnter.setY(y, center);
        buttonEnter.setFontScale(1.2f);
        buttonEnter.setBottomPod(1);

        stage.addActor(buttonEnter);
    }

    private void placeEditDeviceName(String currentDeviceName, float y) {
        float x = getImages().buttonWidth + 40 + (SCREEN_WIDTH - (getImages().buttonWidth + 40)) / 2f;
        y -= 13; // кнопка почему-то ниже отрисовывается, отнимаем, чтобы все по одной линии были

        EditText editDeviceName = new EditText(currentDeviceName, getStyles().editTextStyle);
        editDeviceName.setPosition(x, y, center);
        editDeviceName.setName("editDevice");
        editDeviceName.setMaxWidth(735); // методом тыка

        stage.addActor(editDeviceName);
        editTexts.add(editDeviceName);
    }

    private void placeTextHintOverEditText(float x, float y) {
        Label textHint = new Label(getLocales().deviceName, getStyles().hintStyle);
        textHint.setPosition(x, y + 45, center);
        textHint.setAlignment(center);

        stage.addActor(textHint);
    }

    private void placeTextYourDevices() {
        Label yourDevicesText = new Label(getLocales().yourDevices, getStyles().labelStyle);
        yourDevicesText.setPosition(HALF_SCREEN_WIDTH, SCREEN_HEIGHT - 110, center);
        yourDevicesText.setAlignment(center);
        yourDevicesText.setFontScale(1.2f);

        stage.addActor(yourDevicesText);
    }

    private void placeButtonAddDevice() {
        Button buttonAddDevice = new Button(getLocales().addDevice) {
            @Override
            public void onClick() {
                if (++numDevices >= 4) {
                    activate(false);
                } else {
                    mainAppManager.startScene(new RegisterScene(mainAppManager));
                }
            }
        };
        buttonAddDevice.setSize(getImages().buttonWidth * 1.7f, getImages().buttonHeight * 1.9f);
        buttonAddDevice.setPosition(HALF_SCREEN_WIDTH, getImages().buttonHeight * 1.9f, center);
        buttonAddDevice.setFontScale(1.2f);
        buttonAddDevice.setBottomPod(-13);

        stage.addActor(buttonAddDevice);
    }

    private void updateDeviceNameInJSONFile() {
        final HashMap<String, UserDevice> allUserDevices = FileManager.getUserDevicesData();
        int i = 0;

        for (UserDevice userDevice : allUserDevices.values()) {
            userDevice.deviceName = editTexts.get(i++).getText();
        }

        FileManager.saveJsonFile();
    }

    private void setStageListener() {
        stage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                for (Actor actor : stage.getActors()) {
                    if ("editDevice".equals(actor.getName())) {
                        if (underFinger(actor, x, y)) return;
                    }
                }

                updateDeviceNameInJSONFile();
                Gdx.input.setOnscreenKeyboardVisible(false);
                stage.setKeyboardFocus(null);
            }

            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == BACK) {
                    Gdx.app.exit();
                }
                return true;
            }
        });
    }
}
