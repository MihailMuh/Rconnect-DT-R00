package com.mihalis.dtr00.scenes;

import static com.badlogic.gdx.utils.Align.center;
import static com.mihalis.dtr00.hub.Resources.getLocales;
import static com.mihalis.dtr00.hub.Resources.getStyles;
import static com.mihalis.dtr00.systemd.service.Windows.HALF_SCREEN_WIDTH;
import static com.mihalis.dtr00.systemd.service.Windows.SCREEN_HEIGHT;
import static com.mihalis.dtr00.systemd.service.Windows.SCREEN_WIDTH;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.mihalis.dtr00.hub.FontHub;
import com.mihalis.dtr00.scenes.register.RegisterScene;
import com.mihalis.dtr00.systemd.MainAppManager;
import com.mihalis.dtr00.systemd.service.FileManager;
import com.mihalis.dtr00.systemd.service.networking.NetworkManager;

public class ChangePasswordScene extends RegisterScene {
    public ChangePasswordScene(MainAppManager mainAppManager) {
        super(mainAppManager);
    }

    @Override
    protected void placeEnterIpDeviceText() {
        Label enterIpDeviceText = new Label(getLocales().currentIP, getStyles().labelStyle);
        enterIpDeviceText.setPosition(HALF_SCREEN_WIDTH, SCREEN_HEIGHT - 450, center);
        enterIpDeviceText.setAlignment(center);
        enterIpDeviceText.setFontScale(0.8f);
        stage.addActor(enterIpDeviceText);
    }

    @Override
    protected void placeAuthorizationText() {
        Label maybeYouChangePasswdText = new Label(getLocales().maybeYouChangePasswd, getStyles().labelStyle);
        maybeYouChangePasswdText.setX(HALF_SCREEN_WIDTH, center);
        maybeYouChangePasswdText.setY(SCREEN_HEIGHT - 320);
        maybeYouChangePasswdText.setWrap(true);
        maybeYouChangePasswdText.setAlignment(center);
        maybeYouChangePasswdText.setFontScale(FontHub.resizeFont(getStyles().labelStyle.font,
                SCREEN_WIDTH - 20, getLocales().maybeYouChangePasswd.split("\n")));
        stage.addActor(maybeYouChangePasswdText);
    }

    @Override
    protected void placeEditIp() {
        super.placeEditIp();

        editIP.setText(NetworkManager.getIpAddress());
        editIP.setWidth(editIP.getTextWidth() + 40);
    }

    @Override
    protected void onSuccessfulLogin() {
        super.onSuccessfulLogin();

        mainAppManager.startScene(new MainScene(mainAppManager,
                FileManager.getUserDevicesData().get(NetworkManager.getIpAddress())));
    }
}
