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

package burai.app.explorer.body;

import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import burai.app.explorer.body.contextmenu.QEFXContextMenu;
import burai.app.icon.QEFXIcon;

public class QEFXTileCell extends BorderPane {

    private static final int MAX_LEN_CAPTION = 20;

    private QEFXExplorerTileView root;

    private QEFXIcon icon;

    private ContextMenu contextMenu;

    public QEFXTileCell(QEFXExplorerTileView root, QEFXIcon icon, double size) {
        if (root == null) {
            throw new IllegalArgumentException("root is null.");
        }

        if (icon == null) {
            throw new IllegalArgumentException("icon is null.");
        }

        if (size <= 0.0) {
            throw new IllegalArgumentException("size is not positive.");
        }

        this.root = root;
        this.icon = icon;
        this.contextMenu = QEFXContextMenu.getContextMenu(this.icon, this.root);
        this.initialize(size);
    }

    public QEFXIcon getIcon() {
        return this.icon;
    }

    private void initialize(double size) {
        this.getStyleClass().add("tile-icon");
        this.setCenter(this.createFigure(icon, size));
        this.setBottom(this.createCaption(icon));

        this.setOnMouseClicked(event -> {
            if (event != null && MouseButton.PRIMARY.equals(event.getButton()) && event.getClickCount() >= 2) {
                this.root.selectIcon(icon);
            }
        });

        this.setOnMousePressed(event -> {
            if (event != null && event.isSecondaryButtonDown()) {
                this.contextMenu.show(this, event.getScreenX(), event.getScreenY());
            } else {
                this.contextMenu.hide();
            }
        });
    }

    private Node createFigure(QEFXIcon icon, double size) {
        Node figure = icon.getFigure(size);
        if (figure == null) {
            figure = new Label("No Figure");
        }

        BorderPane.setAlignment(figure, Pos.BOTTOM_CENTER);

        return figure;
    }

    private Node createCaption(QEFXIcon icon) {
        Label caption = new Label();
        StringProperty captionProp = icon.captionProperty();
        this.updateCaption(caption, captionProp.get());
        BorderPane.setAlignment(caption, Pos.TOP_CENTER);

        captionProp.addListener(o -> {
            this.updateCaption(caption, captionProp.get());
        });

        return caption;
    }

    private void updateCaption(Label caption, String captionStr) {
        Tooltip captionTip = null;

        String captionStr2 = captionStr;
        if (captionStr2 == null || captionStr2.isEmpty()) {
            captionStr2 = "No Caption";
        }

        if (captionStr2.length() > (MAX_LEN_CAPTION + 3)) {
            captionTip = new Tooltip(captionStr2);
            captionStr2 = captionStr2.substring(0, MAX_LEN_CAPTION) + "...";
        }

        caption.setText(captionStr2);
        caption.setTooltip(captionTip);
    }
}
