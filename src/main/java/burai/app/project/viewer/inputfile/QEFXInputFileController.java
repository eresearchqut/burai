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

package burai.app.project.viewer.inputfile;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import burai.app.QEFXAppController;
import burai.app.project.QEFXProjectController;
import burai.com.graphic.svg.SVGLibrary;
import burai.com.graphic.svg.SVGLibrary.SVGData;
import burai.input.QEInput;
import burai.project.InputModeChanged;
import burai.project.Project;

public class QEFXInputFileController extends QEFXAppController implements InputModeChanged {

    private static final double INSETS_SIZE = 2.0;

    private static final double GRAPHIC_SIZE = 18.0;
    private static final String GRAPHIC_CLASS = "piclight-button";

    private Project project;

    private QEFXProjectController projectController;

    @FXML
    private Group baseGroup;

    @FXML
    private Button closeButton;

    @FXML
    private Button reloadButton;

    @FXML
    private Button uploadButton;

    @FXML
    private TextArea inputArea;

    public QEFXInputFileController(QEFXProjectController projectController, Project project) {
        super(projectController == null ? null : projectController.getMainController());

        if (project == null) {
            throw new IllegalArgumentException("project is null.");
        }

        if (projectController == null) {
            throw new IllegalArgumentException("projectController is null.");
        }

        this.project = project;
        this.project.addOnInputModeChanged(this);

        this.projectController = projectController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setupBaseGroup();
        this.setupCloseButton();
        this.setupReloadButton();
        this.setupUploadButton();
        this.setupInputArea();
    }

    public void close() {
        EventHandler<ActionEvent> handler = null;
        if (this.closeButton != null) {
            handler = this.closeButton.getOnAction();
        }

        if (handler != null) {
            handler.handle(new ActionEvent());
        }
    }

    private void setupBaseGroup() {
        if (this.baseGroup == null) {
            return;
        }

        StackPane.setMargin(this.baseGroup, new Insets(INSETS_SIZE));
        StackPane.setAlignment(this.baseGroup, Pos.BOTTOM_LEFT);
    }

    private void setupCloseButton() {
        if (this.closeButton == null) {
            return;
        }

        this.closeButton.setText("");
        this.closeButton.setGraphic(
                SVGLibrary.getGraphic(SVGData.CLOSE, GRAPHIC_SIZE, null, GRAPHIC_CLASS));

        this.closeButton.setTooltip(new Tooltip("close"));

        this.closeButton.setOnAction(event -> {
            this.project.removeOnInputModeChanged(this);
            this.projectController.clearStackedsOnViewerPane();
        });
    }

    private void setupReloadButton() {
        if (this.reloadButton == null) {
            return;
        }

        this.reloadButton.setText("");
        this.reloadButton.setGraphic(
                SVGLibrary.getGraphic(SVGData.ARROW_ROUND, GRAPHIC_SIZE, null, GRAPHIC_CLASS));

        this.reloadButton.setTooltip(new Tooltip("reload"));

        this.reloadButton.setOnAction(event -> {
            this.actionOnReloadButton();
        });
    }

    private void actionOnReloadButton() {
        if (this.inputArea == null) {
            return;
        }

        QEInput qeInput = this.project.getQEInputCurrent();
        if (qeInput != null) {
            this.project.resolveQEInputs();
            this.inputArea.setText(qeInput.toString());
        }
    }

    private void setupUploadButton() {
        if (this.uploadButton == null) {
            return;
        }

        this.uploadButton.setText("");
        this.uploadButton.setGraphic(
                SVGLibrary.getGraphic(SVGData.ARROW_UP, GRAPHIC_SIZE, null, GRAPHIC_CLASS));

        this.uploadButton.setTooltip(new Tooltip("upload"));

        this.uploadButton.setOnAction(event -> {
            if (this.inputArea == null) {
                return;
            }

            QEInput qeInput = this.project.getQEInputCurrent();
            if (qeInput == null) {
                return;
            }

            String text = null;
            text = this.inputArea.getText();
            if (text != null) {
                try {
                    qeInput.updateInputData(text);
                    this.project.resolveQEInputs();
                    this.inputArea.setText(qeInput.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setupInputArea() {
        if (this.inputArea == null) {
            return;
        }

        this.inputArea.setWrapText(false);

        QEInput qeInput = this.project.getQEInputCurrent();
        if (qeInput != null) {
            this.inputArea.setText(qeInput.toString());
        }
    }

    @Override
    public void onInputModeChanged(int inputMode) {
        this.actionOnReloadButton();
    }
}
