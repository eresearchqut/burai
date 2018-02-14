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

package burai.app.project.viewer.result.graph.tools;

import java.net.URL;
import java.util.ResourceBundle;

import burai.app.QEFXAppController;
import burai.app.project.QEFXProjectController;
import burai.com.graphic.svg.SVGLibrary;
import burai.com.graphic.svg.SVGLibrary.SVGData;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

public class QEFXGraphNoteController extends QEFXAppController {

    private static final double INSETS_SIZE = 6.0;

    private static final double GRAPHIC_SIZE = 16.0;
    private static final String GRAPHIC_CLASS = "piclight-button";

    private static final String NOTE_CLASS = "result-graph-note";

    private QEFXProjectController projectController;

    private Node content;

    @FXML
    private Group baseGroup;

    @FXML
    private BorderPane basePane;

    @FXML
    private Button screenButton;

    @FXML
    private Button minButton;

    private Button maxButton;

    private boolean maximized;

    private boolean initMaximized;

    private NoteMaximized onNoteMaximized;

    public QEFXGraphNoteController(QEFXProjectController projectController, Node content, boolean initMaximized) {
        super(projectController == null ? null : projectController.getMainController());

        if (projectController == null) {
            throw new IllegalArgumentException("projectController is null.");
        }

        if (content == null) {
            throw new IllegalArgumentException("content is null.");
        }

        this.projectController = projectController;
        this.content = content;

        this.maxButton = null;
        this.maximized = true;
        this.initMaximized = initMaximized;
        this.onNoteMaximized = null;
    }

    public void setOnNoteMaximized(NoteMaximized onNoteMaximized) {
        this.onNoteMaximized = onNoteMaximized;
    }

    public void minimize() {
        if (!this.maximized) {
            return;
        }

        if (this.baseGroup != null && this.maxButton != null) {
            ToolBar toolBar = new ToolBar(this.maxButton);
            toolBar.getStyleClass().add(NOTE_CLASS);
            this.baseGroup.getChildren().clear();
            this.baseGroup.getChildren().add(toolBar);

            this.maximized = false;

            if (this.onNoteMaximized != null) {
                this.onNoteMaximized.onNoteMaximized(false);
            }
        }
    }

    public void maximize() {
        if (this.maximized) {
            return;
        }

        if (this.baseGroup != null && this.basePane != null) {
            this.baseGroup.getChildren().clear();
            this.baseGroup.getChildren().add(this.basePane);

            this.maximized = true;

            if (this.onNoteMaximized != null) {
                this.onNoteMaximized.onNoteMaximized(true);
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setupBaseGroup();
        this.setupBasePane();
        this.setupScreenButton();
        this.setupMinButton();
        this.setupMaxButton();

        if (this.initMaximized) {
            this.maximize();
        } else {
            this.minimize();
        }
    }

    private void setupBaseGroup() {
        if (this.baseGroup == null) {
            return;
        }

        StackPane.setMargin(this.baseGroup, new Insets(INSETS_SIZE));
    }

    private void setupBasePane() {
        if (this.basePane == null) {
            return;
        }

        this.basePane.setCenter(this.content);
    }

    private void setupScreenButton() {
        if (this.screenButton == null) {
            return;
        }

        this.screenButton.setText("");
        this.screenButton.setGraphic(
                SVGLibrary.getGraphic(SVGData.CAMERA, GRAPHIC_SIZE, null, GRAPHIC_CLASS));

        this.screenButton.setTooltip(new Tooltip("screen-shot"));

        this.screenButton.setOnAction(event -> {
            if (this.content != null) {
                this.projectController.sceenShot(this.content);
            }
        });
    }

    private void setupMinButton() {
        if (this.minButton == null) {
            return;
        }

        this.minButton.setText("");
        this.minButton.setGraphic(
                SVGLibrary.getGraphic(SVGData.MINIMIZE, GRAPHIC_SIZE, null, GRAPHIC_CLASS));

        this.minButton.setTooltip(new Tooltip("minimize"));

        this.minButton.setOnAction(event -> this.minimize());
    }

    private void setupMaxButton() {
        this.maxButton = new Button();
        this.maxButton.getStyleClass().add(GRAPHIC_CLASS);

        this.maxButton.setText("");
        this.maxButton.setGraphic(
                SVGLibrary.getGraphic(SVGData.MAXIMIZE, GRAPHIC_SIZE, null, GRAPHIC_CLASS));

        this.maxButton.setTooltip(new Tooltip("maximize"));

        this.maxButton.setOnAction(event -> this.maximize());
    }
}
