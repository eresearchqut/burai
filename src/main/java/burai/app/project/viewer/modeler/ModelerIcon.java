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

package burai.app.project.viewer.modeler;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import burai.com.graphic.svg.SVGLibrary;
import burai.com.graphic.svg.SVGLibrary.SVGData;

public class ModelerIcon extends Group {

    private static final double INSETS_SIZE = 6.0;
    private static final double GRAPHIC_SIZE = 72.0;
    private static final String GRAPHIC_CLASS = "icon-modeler";

    public ModelerIcon(String text) {
        super();

        String text2 = "";
        if (text != null) {
            text2 = text;
        }

        Node figure = SVGLibrary.getGraphic(SVGData.TOOL, GRAPHIC_SIZE, null, GRAPHIC_CLASS);
        StackPane.setMargin(figure, new Insets(INSETS_SIZE));

        Label label = new Label(text2);
        label.setWrapText(true);
        label.getStyleClass().add(GRAPHIC_CLASS);

        StackPane pane = new StackPane();
        pane.getChildren().add(figure);
        pane.getChildren().add(label);

        this.getChildren().add(pane);
        StackPane.setAlignment(this, Pos.BOTTOM_LEFT);
    }
}
