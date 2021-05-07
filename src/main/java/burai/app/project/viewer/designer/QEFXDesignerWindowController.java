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

package burai.app.project.viewer.designer;

import java.net.URL;
import java.util.ResourceBundle;

import burai.app.QEFXAppController;
import burai.app.project.QEFXProjectController;
import burai.atoms.design.Design;
import burai.atoms.viewer.AtomsViewer;
import burai.com.graphic.svg.SVGLibrary;
import burai.com.graphic.svg.SVGLibrary.SVGData;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class QEFXDesignerWindowController extends QEFXAppController {

    private static final double INSETS_SIZE = 4.0;

    private static final double GRAPHIC_SIZE = 20.0;
    private static final String GRAPHIC_CLASS = "designer-button";

    private AtomsViewer atomsViewer;

    private boolean maximized;
    private WindowMaximized onWindowMaximized;

    @FXML
    private Group baseGroup;

    @FXML
    private Pane mainPane;

    @FXML
    private Label headLabel;

    @FXML
    private Button scaleButton;

    public QEFXDesignerWindowController(QEFXProjectController projectController, AtomsViewer atomsViewer) {
        super(projectController == null ? null : projectController.getMainController());

        if (atomsViewer == null) {
            throw new IllegalArgumentException("atomsViewer is null.");
        }

        this.atomsViewer = atomsViewer;

        this.maximized = false;
        this.onWindowMaximized = null;
    }

    public void setWidth(double width) {
        if (width <= 0.0) {
            return;
        }

        if (this.mainPane != null) {
            this.mainPane.setPrefWidth(width);
        }
    }

    public void setHeight(double height) {
        if (height <= 0.0) {
            return;
        }

        if (this.mainPane != null) {
            this.mainPane.setPrefHeight(height);
        }
    }

    public void setOnWindowMaximized(WindowMaximized onWindowMaximized) {
        this.onWindowMaximized = onWindowMaximized;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setupBaseGroup();
        this.setupMainPane();
        this.setupHeadLabel();
        this.setupScaleButton();
    }

    private void setupBaseGroup() {
        if (this.baseGroup == null) {
            return;
        }

        StackPane.setMargin(this.baseGroup, new Insets(INSETS_SIZE));
        StackPane.setAlignment(this.baseGroup, Pos.BOTTOM_LEFT);
    }

    private void setupMainPane() {
        if (this.mainPane == null) {
            return;
        }

        this.mainPane.setPrefWidth(0.0);
        this.mainPane.setPrefHeight(0.0);
        this.mainPane.getChildren().clear();

        if (this.atomsViewer != null) {
            this.atomsViewer.bindSceneTo(this.mainPane);
            this.mainPane.getChildren().add(this.atomsViewer);
        }
    }

    private void setupHeadLabel() {
        if (this.headLabel == null) {
            return;
        }

        Design design = this.atomsViewer == null ? null : this.atomsViewer.getDesign();
        if (design == null) {
            return;
        }

        Color color = design.getFontColor();
        if (color != null) {
            this.headLabel.setTextFill(color);
        }

        design.addOnFontColorChanged(color_ -> {
            if (color_ != null) {
                this.headLabel.setTextFill(color_);
            }
        });
    }

    private void setupScaleButton() {
        if (this.scaleButton == null) {
            return;
        }

        this.scaleButton.setText("");
        this.scaleButton.getStyleClass().add(GRAPHIC_CLASS);
        this.updateScaleButton(!this.maximized);

        this.scaleButton.setOnAction(event -> {
            this.maximized = !this.maximized;

            if (this.onWindowMaximized != null) {
                this.onWindowMaximized.onWindowMaximized(this.maximized);
            }

            this.updateScaleButton(!this.maximized);
        });
    }

    private void updateScaleButton(boolean toMaximize) {
        if (this.scaleButton == null) {
            return;
        }

        if (toMaximize) {
            this.scaleButton.setTooltip(new Tooltip("maximize"));
            this.scaleButton.setGraphic(
                    SVGLibrary.getGraphic(SVGData.MAXIMIZE, GRAPHIC_SIZE, null, GRAPHIC_CLASS));

        } else {
            this.scaleButton.setTooltip(new Tooltip("minimize"));
            this.scaleButton.setGraphic(
                    SVGLibrary.getGraphic(SVGData.MINIMIZE, GRAPHIC_SIZE, null, GRAPHIC_CLASS));
        }
    }
}
