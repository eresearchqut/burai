/*
 * Copyright (C) 2018 Satomichi Nishihara
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package burai.app.project.menu.roll;

import java.util.List;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.util.Duration;
import burai.app.project.menu.QEFXMenu;

public class QEFXRollMenu extends QEFXMenu<Node> {

    private static final double TIME_TO_SHOW_ITEMS = 250.0;

    private double radius;
    private double width;

    private Animation animation;

    public QEFXRollMenu(double radius, double width) {
        super();

        if (radius < 0.0) {
            throw new IllegalArgumentException("radius is negative.");
        }

        if (width <= 0.0) {
            throw new IllegalArgumentException("width is not positive.");
        }

        this.radius = radius;
        this.width = width;

        this.animation = null;
    }

    private void refreshItems(QEFXRollMenuItem newItem) {
        int numItems = newItem == null ? 0 : 1;
        List<Node> children = this.getChildren();

        for (Node child : children) {
            if (child != null && (child instanceof QEFXRollMenuItem)) {
                numItems++;
            }
        }

        double angleWidth = 360.0 / numItems;

        int indexItems = 0;
        for (Node child : children) {
            if (child != null && (child instanceof QEFXRollMenuItem)) {
                QEFXRollMenuItem item = (QEFXRollMenuItem) child;
                item.setStartAngle(indexItems * angleWidth);
                item.setEndAngle((indexItems + 1) * angleWidth);
                indexItems++;
            }
        }

        if (newItem != null) {
            newItem.setStartAngle((numItems - 1) * angleWidth);
            newItem.setEndAngle(numItems * angleWidth);
            this.getChildren().add(newItem);
        }
    }

    private void createAnimation() {
        DoubleProperty angle = new SimpleDoubleProperty(0.0);
        angle.addListener(o -> this.setRotate(-angle.get()));
        KeyValue angleKeyValue = new KeyValue(angle, 360.0);
        KeyFrame angleKeyFrame = new KeyFrame(Duration.millis(TIME_TO_SHOW_ITEMS), angleKeyValue);

        Timeline timeline = new Timeline();
        timeline.setCycleCount(1);
        timeline.getKeyFrames().add(angleKeyFrame);

        timeline.setOnFinished(event -> {
            angle.set(0.0);

            List<Node> children = this.getChildren();
            for (Node child : children) {
                if (child != null && (child instanceof QEFXRollMenuItem)) {
                    QEFXRollMenuItem item = (QEFXRollMenuItem) child;
                    item.showGraphic();
                }
            }
        });

        this.animation = timeline;
    }

    private void addToAnimation(QEFXRollMenuItem item) {
        if (item == null) {
            return;
        }

        if (this.animation == null || !(this.animation instanceof Timeline)) {
            this.createAnimation();
        }

        KeyValue radiusKeyValue = new KeyValue(item.radiusProperty(), this.radius);
        KeyValue widthKeyValue = new KeyValue(item.widthProperty(), this.width);
        KeyFrame radiusKeyFrame = new KeyFrame(Duration.millis(TIME_TO_SHOW_ITEMS), radiusKeyValue);
        KeyFrame widthKeyFrame = new KeyFrame(Duration.millis(TIME_TO_SHOW_ITEMS), widthKeyValue);

        Timeline timeline = (Timeline) this.animation;
        timeline.getKeyFrames().addAll(radiusKeyFrame, widthKeyFrame);
    }

    @Override
    public void clearItem() {
        this.getChildren().clear();
        this.animation = null;
    }

    @Override
    public void addItem(Node graphic) {
        QEFXRollMenuItem item = new QEFXRollMenuItem(graphic);
        item.setParent(this);
        this.addToAnimation(item);
        this.refreshItems(item);
    }

    @Override
    protected void playShowingAnimation() {
        List<Node> children = this.getChildren();
        for (Node child : children) {
            if (child != null && (child instanceof QEFXRollMenuItem)) {
                QEFXRollMenuItem item = (QEFXRollMenuItem) child;
                item.setRadius(0.0);
                item.setWidth(0.0);
                item.hideGraphic();
            }
        }

        if (this.animation != null) {
            this.animation.playFromStart();
        }
    }
}
