package com.mihalis.dtr00.widgets;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static com.badlogic.gdx.utils.Align.center;
import static com.badlogic.gdx.utils.Align.top;
import static com.mihalis.dtr00.hub.Resources.getCamera;
import static com.mihalis.dtr00.hub.Resources.getStyles;
import static com.mihalis.dtr00.hub.Resources.getViewport;
import static com.mihalis.dtr00.systemd.service.Service.vibrate;
import static com.mihalis.dtr00.systemd.service.Windows.SCREEN_HEIGHT;
import static com.mihalis.dtr00.systemd.service.Windows.SCREEN_WIDTH;

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
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mihalis.dtr00.hub.Resources;
import com.mihalis.dtr00.systemd.service.Processor;
import com.mihalis.dtr00.utils.Intersector;

public class AlertDialog extends Dialog {
    public AlertDialog(String title, WindowStyle windowStyle) {
        super(title, windowStyle);
        init();
    }

    private void init() {
        pad(50);
        padTop(175);
        getTitleLabel().setFontScale(1.2f);
        getTitleLabel().setAlignment(center);
        getContentTable().pad(70);
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

    @Override
    public Dialog show(Stage stage) {
        Processor.postToGDX(() -> {
            show(stage, sequence(Actions.alpha(0), Actions.fadeIn(0.2f, Interpolation.fade)));
            setPosition(Math.round((stage.getWidth() - getWidth()) / 2), Math.round((stage.getHeight() - getHeight()) / 2));
        });
        return this;
    }

    @Override
    public void hide() {
        hide(sequence(fadeOut(0.2f, Interpolation.fade), getDeleteAction()));
    }

    public void hideAfterOutsideClick(Stage stage) {
        stage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!Intersector.underFinger(AlertDialog.this, x, y)) {
                    hide();
                    stage.removeListener(this);
                }
            }
        });
    }

    private RunnableAction getDeleteAction() {
        RunnableAction runnableAction = new RunnableAction();
        runnableAction.setRunnable(() -> Resources.getStage().getActors().removeValue(AlertDialog.this, true));
        return runnableAction;
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
