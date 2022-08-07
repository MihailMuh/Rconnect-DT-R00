package com.mihalis.dtr00.widgets;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static com.badlogic.gdx.utils.Align.bottom;
import static com.badlogic.gdx.utils.Align.center;
import static com.badlogic.gdx.utils.Align.top;
import static com.mihalis.dtr00.hub.Resources.getCamera;
import static com.mihalis.dtr00.hub.Resources.getStyles;
import static com.mihalis.dtr00.hub.Resources.getViewport;
import static com.mihalis.dtr00.systemd.service.Service.vibrate;
import static com.mihalis.dtr00.systemd.service.Windows.SCREEN_HEIGHT;
import static com.mihalis.dtr00.systemd.service.Windows.SCREEN_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.HdpiUtils;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mihalis.dtr00.utils.Intersector;

public class AlertDialog extends Dialog {
    public AlertDialog(String title, Skin skin) {
        super(title, skin);
        init();
    }

    public AlertDialog(String title, Skin skin, String windowStyleName) {
        super(title, skin, windowStyleName);
        init();
    }

    public AlertDialog(String title, WindowStyle windowStyle) {
        super(title, windowStyle);
        init();
    }

    private void init() {
        pad(50);
        padTop(175);
        getTitleLabel().setFontScale(1.2f);
        getTitleLabel().setAlignment(center, bottom);
        getButtonTable().padTop(70);
        getButtonTable().padBottom(10);
        setMovable(false);
    }

    public Dialog text(String text, float fontScale) {
        Label label = new Label(text, getStyles().labelStyle);
        label.setWrap(true);
        label.setFontScale(fontScale);
        label.setAlignment(center, top);
        return text(label);
    }

    @Override
    public Dialog text(String text) {
        return text(text, 1);
    }

    @Override
    public Dialog button(Button button, Object object) {
        button.padBottom(15);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                vibrate(50);
            }
        });
        return super.button(button, object);
    }

    public void hideAfterOutsideClick(Stage stage) {
        stage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!Intersector.underFinger(AlertDialog.this, x, y)) {
                    killDialog(stage);
                    stage.removeListener(this);
                }
            }
        });
    }

    @Override
    public Dialog show(Stage stage) {
        Gdx.app.postRunnable(() -> {
            show(stage, sequence(Actions.alpha(0), Actions.fadeIn(0.2f, Interpolation.fade)));
            setPosition(Math.round((stage.getWidth() - getWidth()) / 2), Math.round((stage.getHeight() - getHeight()) / 2));
        });
        return this;
    }

    private RunnableAction getDeleteAction(Stage stage) {
        RunnableAction runnableAction = new RunnableAction();
        runnableAction.setRunnable(() -> stage.getActors().removeValue(AlertDialog.this, true));
        return runnableAction;
    }

    public void killDialog(Stage stage) {
        hide(sequence(fadeOut(0.2f, Interpolation.fade), getDeleteAction(stage)));
    }

    // Переопределяю метод, чтобы текстура на заднем фоне обходила ограничение FitViewport (черные/белые полосы по краям)
    @Override
    protected void drawStageBackground(Batch batch, float parentAlpha, float x, float y, float width, float height) {
        batch.end();

        HdpiUtils.glViewport(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        batch.getProjectionMatrix().idt().setToOrtho2D(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        batch.getTransformMatrix().idt();

        batch.begin();
        super.drawStageBackground(batch, parentAlpha, x, y, width, height);
        batch.end();

        getViewport().apply(true);
        batch.setProjectionMatrix(getCamera().combined);
        batch.setTransformMatrix(getCamera().view);
        batch.begin();
    }
}
