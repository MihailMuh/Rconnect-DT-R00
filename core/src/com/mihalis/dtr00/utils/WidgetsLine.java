package com.mihalis.dtr00.utils;

import static com.badlogic.gdx.utils.Align.left;
import static com.badlogic.gdx.utils.Align.right;
import static com.mihalis.dtr00.constants.Constant.WIDGETS_PAD;
import static com.mihalis.dtr00.constants.Constant.WIDGETS_START_X;
import static com.mihalis.dtr00.hub.Resources.getImages;
import static com.mihalis.dtr00.hub.Resources.getLocales;
import static com.mihalis.dtr00.hub.Resources.getStage;
import static com.mihalis.dtr00.hub.Resources.getStyles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.mihalis.dtr00.hub.FontHub;
import com.mihalis.dtr00.systemd.service.Networking;
import com.mihalis.dtr00.systemd.service.Processor;
import com.mihalis.dtr00.systemd.service.Service;
import com.mihalis.dtr00.systemd.service.Toast;
import com.mihalis.dtr00.utils.jsonTypes.UserSettings;
import com.mihalis.dtr00.widgets.Button;
import com.mihalis.dtr00.widgets.ImageView;

public class WidgetsLine extends Array<Actor> {
    private final Label relayName;
    private final Button onOffButton;
    private final ImageView imageIndicator;
    private final Button pulseButton;

    public WidgetsLine(UserSettings userSettings, int index, Runnable updateIndicatorsAndButtons) {
        super(true, 4, Actor.class);

        relayName = new Label(userSettings.relayNames[index], getStyles().hintStyle);
        relayName.setColor(Color.BLACK);
        relayName.setAlignment(left);
        relayName.setFontScale(1.3f);
        relayName.setWidth(FontHub.getTextWidth(relayName));
        relayName.setName("label");

        imageIndicator = new ImageView();

        String on = getLocales().on;
        String off = getLocales().off;
        onOffButton = new Button(on) {
            @Override
            public void onClick() {
                if (getText().toString().equals(on)) {
                    Networking.onRelay(index, () -> {
                        setText(off);
                        imageIndicator.setEnabled(true);

                        Toast.makeToast(getLocales().enabled, 500);
                    });
                } else {
                    Networking.offRelay(index, () -> {
                        setText(on);
                        imageIndicator.setEnabled(false);

                        Toast.makeToast(getLocales().enabled, 500);
                    });
                }
            }
        };
        onOffButton.setWidth(getImages().buttonWidth * 0.7f);

        int delaySeconds = userSettings.relayDelays[index];
        pulseButton = new Button(getLocales().pulse) {
            @Override
            public void onClick() {
                Networking.delayRelay(index, delaySeconds, () -> {
                    imageIndicator.setEnabled(true);
                    onOffButton.setText(off);

                    Toast.makeToast(getLocales().enabledAt + " " + delaySeconds + " " + getLocales().seconds);

                    Processor.postTask(() -> {
                        Service.sleep(delaySeconds * 1000);
                        updateIndicatorsAndButtons.run();
                    });
                });
            }
        };
        pulseButton.setWidth(getImages().buttonWidth * 0.9f);

        add(relayName, onOffButton, imageIndicator, pulseButton);

        getStage().addActor(relayName);
        getStage().addActor(onOffButton);
        getStage().addActor(imageIndicator);
        getStage().addActor(pulseButton);

        for (int i = 0; i < size; i++) {
            get(i).setVisible(userSettings.disabledMask[index][i]);
        }
    }

    public void updatePositions(float y) {
        relayName.setPosition(WIDGETS_START_X, y + 35);
        onOffButton.setPosition(relayName.getX(right) + WIDGETS_PAD + 20, y);
        imageIndicator.setPosition(onOffButton.getX(right) + 20, y);
        pulseButton.setPosition(imageIndicator.getX(right) + 20, y);
    }

    public Button getOnOffButton() {
        return onOffButton;
    }

    public ImageView getImageIndicator() {
        return imageIndicator;
    }

    public Button getPulseButton() {
        return pulseButton;
    }
}
