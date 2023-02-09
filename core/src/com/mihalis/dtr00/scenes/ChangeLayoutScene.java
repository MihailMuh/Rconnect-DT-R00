package com.mihalis.dtr00.scenes;

import static com.badlogic.gdx.Input.Keys.BACK;
import static com.badlogic.gdx.utils.Align.left;
import static com.badlogic.gdx.utils.Align.right;
import static com.mihalis.dtr00.constants.Constant.WIDGETS_PAD;
import static com.mihalis.dtr00.hub.Resources.getImages;
import static com.mihalis.dtr00.hub.Resources.getLocales;
import static com.mihalis.dtr00.hub.Resources.getStage;
import static com.mihalis.dtr00.hub.Resources.getStyles;
import static com.mihalis.dtr00.systemd.service.Service.vibrate;
import static com.mihalis.dtr00.systemd.service.Windows.HALF_SCREEN_WIDTH;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mihalis.dtr00.systemd.MainAppManager;
import com.mihalis.dtr00.systemd.service.FileManager;
import com.mihalis.dtr00.systemd.service.Toast;
import com.mihalis.dtr00.utils.jsonTypes.UserDevice;
import com.mihalis.dtr00.widgets.WidgetsLine;
import com.mihalis.dtr00.widgets.AlertDialog;
import com.mihalis.dtr00.widgets.Button;

public class ChangeLayoutScene extends MainScene {
    public ChangeLayoutScene(MainAppManager mainAppManager, UserDevice userDevice) {
        super(mainAppManager, userDevice);
    }

    @Override
    public void create() {
        super.create();

        int i = 0;
        for (WidgetsLine widgetsLine : widgetMatrix) {
            widgetsLine.getOnOffButton().getListeners().clear();
            widgetsLine.getPulseButton().getListeners().clear();

            int j = 0;
            for (Actor widget : widgetsLine) {
                int finalI = i, finalJ = j++;

                if (!widget.isVisible()) {
                    widget.setVisible(true);
                    changeWidgetVisibility(widget);
                }

                widget.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        vibrate(50);

                        userDevice.userSettings.disabledMask[finalI][finalJ] = widget.getColor().a != 1;

                        changeWidgetVisibility(widget);
                    }
                });
            }
            i++;
        }
        updateIndicatorsAndButtons();

        AlertDialog alertDialog = new AlertDialog(getLocales().changingLayout, getStyles().dialogStyle);
        alertDialog.text(getLocales().tapToHide, 0.6f);
        alertDialog.button("OK", null, getStyles().textButtonStyle);
        alertDialog.hideAfterOutsideClick(stage);
        alertDialog.show(getStage());
    }

    private void changeWidgetVisibility(Actor widget) {
        if (widget.getColor().a == 1) {
            widget.setColor(1, 1, 1, 0.2f);
        } else {
            if ("label".equals(widget.getName())) {
                widget.setColor(0, 0, 0, 1);
            } else {
                widget.setColor(1, 1, 1, 1);
            }
        }
    }

    @Override
    protected void placeButtonChangeLayout() {
        Button buttonSaveDisabledMask = new Button(getLocales().save) {
            @Override
            public void onClick() {
                Toast.makeToast(getLocales().successfullySaved);
                FileManager.saveJsonFile();
                back();
            }
        };
        buttonSaveDisabledMask.setSize(getImages().buttonWidth * 1.4f, getImages().buttonHeight * 1.2f);
        buttonSaveDisabledMask.setX(HALF_SCREEN_WIDTH - WIDGETS_PAD, right);
        buttonSaveDisabledMask.setY(WIDGETS_PAD * 2);
        buttonSaveDisabledMask.setFontScale(1.2f);
        buttonSaveDisabledMask.setBottomPod(-5);

        stage.addActor(buttonSaveDisabledMask);
    }

    @Override
    protected void placeButtonSettings() {
        Button buttonCancel = new Button(getLocales().cancel) {
            @Override
            public void onClick() {
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

    private void back() {
        mainAppManager.replaceCurrentScene(new MainScene(mainAppManager, userDevice));
    }

    @Override
    protected void setStageListener() {
        stage.addListener(new ClickListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == BACK) back();
                return true;
            }
        });
    }

    @Override
    protected void updateIndicatorsAndButtons() {

    }
}
