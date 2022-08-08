package com.mihalis.dtr00.scenes.register;

import static com.badlogic.gdx.Input.Keys.BACK;
import static com.badlogic.gdx.utils.Align.center;
import static com.mihalis.dtr00.hub.FontHub.getTextHeight;
import static com.mihalis.dtr00.hub.FontHub.getTextWidth;
import static com.mihalis.dtr00.hub.Resources.getImages;
import static com.mihalis.dtr00.hub.Resources.getLocales;
import static com.mihalis.dtr00.hub.Resources.getStyles;
import static com.mihalis.dtr00.systemd.service.Service.vibrate;
import static com.mihalis.dtr00.systemd.service.Windows.HALF_SCREEN_HEIGHT;
import static com.mihalis.dtr00.systemd.service.Windows.HALF_SCREEN_WIDTH;
import static com.mihalis.dtr00.systemd.service.Windows.SCREEN_HEIGHT;
import static com.mihalis.dtr00.utils.Intersector.underFinger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mihalis.dtr00.systemd.MainAppManager;
import com.mihalis.dtr00.systemd.service.Networking;
import com.mihalis.dtr00.systemd.service.Toast;
import com.mihalis.dtr00.utils.Scene;
import com.mihalis.dtr00.widgets.Button;
import com.mihalis.dtr00.widgets.EditText;

public class RegisterScene extends Scene {
    private EditText editIP, editLogin, editPassword;
    private CheckBox rememberMeBox;
    private Button saveButton;

    private final float maxYForWidget = SCREEN_HEIGHT - 110;
    private float lastTapY;

    public RegisterScene(MainAppManager mainAppManager) {
        super(mainAppManager);
    }

    @Override
    public void create() {
        super.create();

        placeAuthorizationText();
        placeEnterIpDeviceText();
        placeEditIp();
        placeLoginText();
        placeEditLogin();
        placePasswordText();
        placeEditPassword();
        placeCheckBoxRemember();
        placeButtonSave();

        setStageListener();
    }

    private void placeButtonSave() {
        saveButton = new Button(getLocales().apply) {
            @Override
            public void onClick() {
                Networking.setIpAddress(editIP.getText().replace(" ", ""));

                Registration registration = new Registration() {
                    @Override
                    void onIncorrect() {
                        Toast.makeToast(getLocales().incorrectLoginPasswd);
                    }

                    @Override
                    void onCorrect() {
                        Toast.makeToast(getLocales().successfullySaved);
                        mainAppManager.finishScene();
                    }
                };
                registration.login(editLogin.getText(), editPassword.getText(), rememberMeBox.isChecked());
            }
        };
        saveButton.setSize(getImages().buttonWidth * 1.6f, getImages().buttonHeight * 1.4f);
        saveButton.setPosition(HALF_SCREEN_WIDTH, 200, center);
        saveButton.setAlignment(center);
        saveButton.setFontScale(1.3f);
        saveButton.setBottomPod(-8);

        stage.addActor(saveButton);
    }

    private void placeCheckBoxRemember() {
        rememberMeBox = new CheckBox(getLocales().rememberMe, getStyles().checkBoxStyle);
        rememberMeBox.setSize(getImages().checkBoxWidth + getTextWidth(rememberMeBox.getLabel()),
                getTextHeight(rememberMeBox.getLabel()));
        rememberMeBox.setPosition(HALF_SCREEN_WIDTH, 415, center);
        rememberMeBox.setChecked(true);
        rememberMeBox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                vibrate(50);
            }
        });

        stage.addActor(rememberMeBox);
    }

    private void placeEditPassword() {
        editPassword = new EditText(getStyles().editTextStyle);
        editPassword.setMessageText(getLocales().password);
        editPassword.setPosition(HALF_SCREEN_WIDTH, SCREEN_HEIGHT - 1300, center);
        editPassword.setPasswordMode(true);
        editPassword.setPasswordCharacter('*');

        stage.addActor(editPassword);
    }

    private void placePasswordText() {
        Label passwordText = new Label(getLocales().password, getStyles().labelStyle);
        passwordText.setX(HALF_SCREEN_WIDTH, center);
        passwordText.setY(SCREEN_HEIGHT - 1170, center);
        passwordText.setAlignment(center);
        passwordText.setFontScale(0.8f);
        stage.addActor(passwordText);
    }

    private void placeEditLogin() {
        editLogin = new EditText(getStyles().editTextStyle);
        editLogin.setMessageText(getLocales().login);
        editLogin.setPosition(HALF_SCREEN_WIDTH, SCREEN_HEIGHT - 940, center);

        stage.addActor(editLogin);
    }

    private void placeLoginText() {
        Label loginText = new Label(getLocales().login, getStyles().labelStyle);
        loginText.setX(HALF_SCREEN_WIDTH, center);
        loginText.setY(SCREEN_HEIGHT - 810, center);
        loginText.setAlignment(center);
        loginText.setFontScale(0.8f);
        stage.addActor(loginText);
    }

    private void placeEnterIpDeviceText() {
        Label enterIpDeviceText = new Label(getLocales().enterIpDevice, getStyles().labelStyle);
        enterIpDeviceText.setX(HALF_SCREEN_WIDTH, center);
        enterIpDeviceText.setY(SCREEN_HEIGHT - 400, center);
        enterIpDeviceText.setWrap(true);
        enterIpDeviceText.setAlignment(center);
        enterIpDeviceText.setFontScale(0.8f);
        stage.addActor(enterIpDeviceText);
    }

    private void placeEditIp() {
        editIP = new EditText(getStyles().editTextStyle);
        editIP.setMessageText(getLocales().ipAddress);
        editIP.setPosition(HALF_SCREEN_WIDTH, SCREEN_HEIGHT - 580, center);

        stage.addActor(editIP);
    }

    private void placeAuthorizationText() {
        Label authorizationText = new Label(getLocales().authorization, getStyles().labelStyle);
        authorizationText.setPosition(HALF_SCREEN_WIDTH, SCREEN_HEIGHT - 110, center);
        authorizationText.setAlignment(center);
        authorizationText.setFontScale(1.2f);
        stage.addActor(authorizationText);
    }

    private void setStageListener() {
        stage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean someoneHasFocus = underFinger(editIP, x, y);
                someoneHasFocus |= underFinger(editLogin, x, y);
                someoneHasFocus |= underFinger(editPassword, x, y);
                someoneHasFocus |= underFinger(rememberMeBox, x, y);
                someoneHasFocus |= underFinger(saveButton, x, y);

                if (!someoneHasFocus) {
                    Gdx.input.setOnscreenKeyboardVisible(false);
                    stage.setKeyboardFocus(null);

                    // возвращаем всех на свои позиции
                    float firstWidgetY = stage.getActors().get(0).getY(center);
                    for (Actor actor : stage.getActors()) {
                        actor.setY(actor.getY(center) - (firstWidgetY - maxYForWidget), center);
                    }
                }
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                RegisterScene.this.lastTapY = y;
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                super.touchDragged(event, x, y, pointer);
                if (stage.getKeyboardFocus() == null) return;

                float deltaY = y - RegisterScene.this.lastTapY;
                float firstWidgetY = stage.getActors().get(0).getY(center);
                float lastWidgetY = stage.getActors().peek().getY(center);

                if (firstWidgetY + deltaY < maxYForWidget || lastWidgetY + deltaY > HALF_SCREEN_HEIGHT) {
                    return;
                }

                for (Actor actor : stage.getActors()) {
                    actor.setY(actor.getY(center) + deltaY, center);
                }
                RegisterScene.this.lastTapY = y;
            }

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
