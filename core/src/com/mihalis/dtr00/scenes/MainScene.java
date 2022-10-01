package com.mihalis.dtr00.scenes;

import static com.badlogic.gdx.Input.Keys.BACK;
import static com.badlogic.gdx.utils.Align.center;
import static com.badlogic.gdx.utils.Align.left;
import static com.badlogic.gdx.utils.Align.right;
import static com.mihalis.dtr00.constants.Constant.WIDGETS_PAD;
import static com.mihalis.dtr00.constants.Constant.WIDGETS_START_Y;
import static com.mihalis.dtr00.hub.Resources.getImages;
import static com.mihalis.dtr00.hub.Resources.getLocales;
import static com.mihalis.dtr00.hub.Resources.getStyles;
import static com.mihalis.dtr00.systemd.service.Windows.HALF_SCREEN_WIDTH;
import static com.mihalis.dtr00.systemd.service.Windows.SCREEN_HEIGHT;
import static com.mihalis.dtr00.systemd.service.Windows.SCREEN_WIDTH;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.mihalis.dtr00.scenes.register.Registration;
import com.mihalis.dtr00.systemd.MainAppManager;
import com.mihalis.dtr00.systemd.service.networking.NetworkManager;
import com.mihalis.dtr00.systemd.service.Processor;
import com.mihalis.dtr00.utils.AsyncRequestHandler;
import com.mihalis.dtr00.utils.Scene;
import com.mihalis.dtr00.utils.WidgetsLine;
import com.mihalis.dtr00.utils.jsonTypes.UserDevice;
import com.mihalis.dtr00.widgets.Button;

import java.util.Arrays;
import java.util.HashMap;

public class MainScene extends Scene {
    protected final Array<WidgetsLine> widgetMatrix;
    protected final UserDevice userDevice;

    public MainScene(MainAppManager mainAppManager, UserDevice userDevice) {
        super(mainAppManager);

        this.userDevice = userDevice;
        widgetMatrix = new Array<>(true, userDevice.countOfRelayChannels, WidgetsLine.class);
    }

    @Override
    public void create() {
        super.create();

        for (int i = 0; i < widgetMatrix.items.length; i++) {
            widgetMatrix.add(new WidgetsLine(userDevice.userSettings, i, this::updateIndicatorsAndButtons));
        }

        placeDeviceNameView();
        placeButtonChangeLayout();
        placeButtonSettings();
        setStageListener();
    }

    @Override
    public void resume() {
        Processor.postTask(() -> {
            super.resume();

            checkRegistration();
            updateIndicatorsAndButtons();
        });
    }

    // если пароль реле изменили через браузер, то в приложении нужно заново зарегестрироваться
    private void checkRegistration() {
        Registration registration = new Registration() {
            @Override
            public void onIncorrect() {
                mainAppManager.finishScene();
                mainAppManager.startScene(new ChangePasswordScene(mainAppManager));
            }

            @Override
            public void onCorrect() {
            }
        };
        registration.login(userDevice.login, userDevice.password, userDevice.rememberRegistration, false);
    }

    private void placeDeviceNameView() {
        Label deviceName = new Label(userDevice.deviceName, getStyles().labelStyle);
        deviceName.setPosition(HALF_SCREEN_WIDTH, SCREEN_HEIGHT - 110, center);
        deviceName.setAlignment(center);
        deviceName.setFontScale(1.2f);

        stage.addActor(deviceName);
    }

    protected void placeButtonChangeLayout() {
        Button buttonChangeLayout = new Button(getLocales().changeLayout) {
            @Override
            public void onClick() {
                mainAppManager.replaceCurrentScene(new ChangeLayoutScene(mainAppManager, userDevice));
            }
        };
        buttonChangeLayout.setSize(getImages().buttonWidth * 1.75f, getImages().buttonHeight * 1.2f);
        buttonChangeLayout.setX(WIDGETS_PAD, left);
        buttonChangeLayout.setY(WIDGETS_PAD * 2);
        buttonChangeLayout.setFontScale(1.2f);
        buttonChangeLayout.setBottomPod(-5);

        stage.addActor(buttonChangeLayout);
    }

    protected void placeButtonSettings() {
        Button buttonSettings = new Button(getLocales().settings) {
            @Override
            public void onClick() {
                mainAppManager.replaceCurrentScene(new SettingsScene(mainAppManager, userDevice));
            }
        };
        buttonSettings.setSize(getImages().buttonWidth * 1.4f, getImages().buttonHeight * 1.2f);
        buttonSettings.setX(SCREEN_WIDTH - WIDGETS_PAD, right);
        buttonSettings.setY(WIDGETS_PAD * 2);
        buttonSettings.setFontScale(1.2f);
        buttonSettings.setBottomPod(-5);

        stage.addActor(buttonSettings);
    }

    protected void updateIndicatorsAndButtons() {
        if (onPause) return;

        NetworkManager.getRelayStatus(new AsyncRequestHandler(1) {
            @Override
            public void action(HashMap<String, String> responses) {
                // такой ответ &0&8&1&1&1&1&1&1&1&1&
                final String relayStatus = responses.get("relayStatus").replace("&", "");
                int startIndex = relayStatus.indexOf(userDevice.countOfRelayChannels + "") + 1;
                int index = 0;

                for (int i = startIndex; i < relayStatus.length(); i++) {
                    boolean relayEnabled = relayStatus.charAt(i) == '1';
                    if (relayEnabled) {
                        widgetMatrix.get(index).getOnOffButton().setText(getLocales().off);
                    } else {
                        widgetMatrix.get(index).getOnOffButton().setText(getLocales().on);
                    }

                    widgetMatrix.get(index++).getImageIndicator().setEnabled(relayStatus.charAt(i) == '1');
                }
            }
        });
    }

    @Override
    public void update() {
        super.update();

        float y = WIDGETS_START_Y;
        for (WidgetsLine widgetsLine : widgetMatrix) {
            if (Arrays.stream(widgetsLine.items).noneMatch(Actor::isVisible)) continue;

            widgetsLine.updatePositions(y);
            y -= 200;
        }
    }

    protected void setStageListener() {
        stage.addListener(new ClickListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == BACK) {
                    mainAppManager.finishAppIfOneSceneInStack();
                }
                return true;
            }
        });
    }
}
