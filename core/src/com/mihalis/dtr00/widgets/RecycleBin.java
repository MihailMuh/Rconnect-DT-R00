package com.mihalis.dtr00.widgets;

import static com.mihalis.dtr00.hub.Resources.getImages;
import static com.mihalis.dtr00.hub.Resources.getLocales;
import static com.mihalis.dtr00.hub.Resources.getStyles;
import static com.mihalis.dtr00.systemd.service.Service.vibrate;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mihalis.dtr00.utils.Clicker;

public abstract class RecycleBin extends ImageView implements Clicker {
    public RecycleBin() {
        sprite.setRegion(getImages().bin);
        sprite.setSize(getImages().bin.originalWidth, getImages().bin.originalHeight);

        setListeners();
    }

    private void showAlert() {
        AlertDialog dialog = new AlertDialog(getLocales().areYouSure, getStyles().dialogStyle) {
            @Override
            protected void result(Object object) {
                if ("yes".equals(object)) {
                    onClick();
                }
            }
        };
        dialog.button(getLocales().no, null, getStyles().textButtonStyle);
        dialog.button(getLocales().yes, "yes", getStyles().textButtonRedStyle);
        dialog.getTitleTable().padBottom(-100);
        dialog.hideAfterOutsideClick(getStage());
        dialog.show(getStage());
    }

    private void setListeners() {
        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                vibrate(50);
                showAlert();
            }
        });
    }
}
