package com.mihalis.dtr00.scenes;

import static com.badlogic.gdx.Input.Keys.BACK;
import static com.badlogic.gdx.utils.Align.center;
import static com.badlogic.gdx.utils.Align.top;
import static com.mihalis.dtr00.hub.FontHub.getTextHeight;
import static com.mihalis.dtr00.hub.FontHub.resizeFont;
import static com.mihalis.dtr00.hub.Resources.getFonts;
import static com.mihalis.dtr00.hub.Resources.getImages;
import static com.mihalis.dtr00.hub.Resources.getLocales;
import static com.mihalis.dtr00.hub.Resources.getStyles;
import static com.mihalis.dtr00.systemd.service.Windows.HALF_SCREEN_WIDTH;
import static com.mihalis.dtr00.systemd.service.Windows.SCREEN_HEIGHT;
import static com.mihalis.dtr00.systemd.service.Windows.SCREEN_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.StringBuilder;
import com.mihalis.dtr00.systemd.MainAppManager;
import com.mihalis.dtr00.systemd.service.networking.NetworkManager;
import com.mihalis.dtr00.systemd.service.Toast;
import com.mihalis.dtr00.utils.Scene;
import com.mihalis.dtr00.utils.ScenesArray;
import com.mihalis.dtr00.widgets.Button;

public class ErrorScene extends Scene {
    private final StringBuilder reportStringBuilder = new StringBuilder();

    public volatile boolean running = false;

    public ErrorScene(MainAppManager mainAppManager) {
        super(mainAppManager);
    }

    public void startWithoutMainAppManager(ScenesArray scenesArray) {
        try {
            scenesArray.pauseScene();
        } catch (Exception ignored) {
        }
        scenesArray.clear();
        scenesArray.add(this);
    }

    public synchronized void prepare(Throwable throwable) {
        running = true;

        reportStringBuilder.appendLine("--------- Stack Trace ---------");
        reportStringBuilder.append(throwable.getClass().getName()).append(": ").appendLine(throwable.getMessage());

        for (StackTraceElement traceElement : throwable.getStackTrace()) {
            reportStringBuilder.appendLine(traceElement.toString());
        }

        reportStringBuilder.append("------ End Of Stack Trace ------");

        placeTitleText();
        placeReportText(reportStringBuilder);
        placeSendToDevText();
        placeButtonSendToDev();

        setStageListener();
    }

    private void placeTitleText() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.appendLine(getLocales().unexpectedError).appendLine(getLocales().pleaseReEnter);

        Label reportLabel = new Label(stringBuilder, getStyles().labelStyle);
        reportLabel.setX(HALF_SCREEN_WIDTH, center);
        reportLabel.setY(SCREEN_HEIGHT - 50, top);
        reportLabel.setAlignment(center);
        reportLabel.setWrap(true);
        reportLabel.getStyle().fontColor = Color.SCARLET;
        reportLabel.setFontScale(resizeFont(getFonts().timesNewRoman, SCREEN_WIDTH,
                (getLocales().unexpectedError + "\n" + getLocales().pleaseReEnter).split("\n")));

        stage.addActor(reportLabel);
    }

    private void placeReportText(StringBuilder report) {
        Label reportText = new Label(report, getStyles().labelStyle);
        reportText.setFontScale(resizeFont(getFonts().timesNewRoman, SCREEN_WIDTH, report.toString().split("\n")));
        reportText.setHeight(getTextHeight(reportText));
        reportText.setX(HALF_SCREEN_WIDTH, center);
        reportText.setY(stage.getActors().peek().getY(), top);
        reportText.setAlignment(center);
        reportText.setWrap(true);
        reportText.getStyle().fontColor = Color.SCARLET;

        stage.addActor(reportText);
    }

    private void placeSendToDevText() {
        Label sendToDevText = new Label(getLocales().sendToDev, getStyles().labelStyle);
        sendToDevText.setX(HALF_SCREEN_WIDTH, center);
        sendToDevText.setY(stage.getActors().peek().getY(), top);
        sendToDevText.setAlignment(center);
        sendToDevText.setWrap(true);
        sendToDevText.setFontScale(resizeFont(getFonts().timesNewRoman, SCREEN_WIDTH,
                getLocales().sendToDev.split("\n")));

        stage.addActor(sendToDevText);
    }

    private void placeButtonSendToDev() {
        Button buttonSend = new Button(getLocales().sendError) {
            @Override
            public void onClick() {
                NetworkManager.postErrorReport(reportStringBuilder.toString(), () -> {
                    activate(false);
                    Toast.makeToast(getLocales().thankYou);
                });
            }
        };
        buttonSend.setFontScale(1.15f);
        buttonSend.setSize(getImages().buttonWidth * 1.4f, getImages().buttonHeight * 1.5f);
        buttonSend.setX(HALF_SCREEN_WIDTH, center);
        buttonSend.setY(stage.getActors().peek().getY() + 50, top);
        buttonSend.setWrap(true);
        buttonSend.setBottomPod(-5);

        stage.addActor(buttonSend);
    }

    private void setStageListener() {
        stage.addListener(new ClickListener() {
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
