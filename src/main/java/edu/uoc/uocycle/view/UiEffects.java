package edu.uoc.uocycle.view;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class UiEffects {

    public static void applyHoverTranslate(Button target, StackPane hoverArea, double dx, double dy, Duration dur) {
        hoverArea.setPickOnBounds(true);

        Insets p = hoverArea.getPadding();
        double top    = p.getTop()    + Math.max(0, dy);
        double right  = p.getRight()  + Math.max(0, dx);
        double bottom = p.getBottom() + Math.max(0, -dy);
        double left   = p.getLeft()   + Math.max(0, -dx);
        hoverArea.setPadding(new Insets(top, right, bottom, left));

        target.setMouseTransparent(true);

        TranslateTransition in = new TranslateTransition(dur, target);
        in.setInterpolator(Interpolator.EASE_BOTH);
        in.setToX(dx);
        in.setToY(-dy);

        TranslateTransition out = new TranslateTransition(dur, target);
        out.setInterpolator(Interpolator.EASE_BOTH);
        out.setToX(0);
        out.setToY(0);

        hoverArea.setOnMouseEntered(e -> {
            out.stop();
            in.playFromStart();
            hoverArea.setCursor(Cursor.HAND);
        });

        hoverArea.setOnMouseExited(e -> {
            in.stop();
            out.playFromStart();
            hoverArea.setCursor(Cursor.DEFAULT);
        });

        hoverArea.setOnMouseClicked(e -> target.fire());
        hoverArea.setFocusTraversable(true);
        hoverArea.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case ENTER:
                case SPACE:
                    target.fire();
                    break;
                default:
            }
        });
    }

}
