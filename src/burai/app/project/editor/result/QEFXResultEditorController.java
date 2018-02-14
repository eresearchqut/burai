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

package burai.app.project.editor.result;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import burai.app.QEFXAppController;
import burai.app.project.QEFXProjectController;
import burai.app.project.viewer.result.QEFXResultViewerController;
import burai.com.graphic.svg.SVGLibrary;
import burai.com.graphic.svg.SVGLibrary.SVGData;

public abstract class QEFXResultEditorController<V extends QEFXResultViewerController> extends QEFXAppController {

    private static final double GRAPHIC_SIZE = 20.0;
    private static final String GRAPHIC_CLASS = "piclight-button";

    protected QEFXProjectController projectController;

    protected V viewerController;

    @FXML
    private Button reloadButton;

    @FXML
    private Button screenButton;

    public QEFXResultEditorController(QEFXProjectController projectController, V viewerController) {
        super(projectController == null ? null : projectController.getMainController());

        if (projectController == null) {
            throw new IllegalArgumentException("projectController is null.");
        }

        if (viewerController == null) {
            throw new IllegalArgumentException("viewerController is null.");
        }

        this.projectController = projectController;
        this.viewerController = viewerController;
    }

    public void reload() {
        if (this.viewerController != null) {
            this.viewerController.reloadSafely();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setupReloadButton();
        this.setupScreenButton();
        this.setupFXComponents();
    }

    protected abstract void setupFXComponents();

    private void setupReloadButton() {
        if (this.reloadButton == null) {
            return;
        }

        this.reloadButton.setText("");
        this.reloadButton.setGraphic(SVGLibrary.getGraphic(SVGData.ARROW_ROUND, GRAPHIC_SIZE, null, GRAPHIC_CLASS));

        this.reloadButton.setOnAction(event -> {
            if (this.viewerController != null) {
                this.viewerController.reloadSafely();
            }
        });
    }

    private void setupScreenButton() {
        if (this.screenButton == null) {
            return;
        }

        this.screenButton.setText("");
        this.screenButton.setGraphic(SVGLibrary.getGraphic(SVGData.CAMERA, GRAPHIC_SIZE, null, GRAPHIC_CLASS));

        this.screenButton.setOnAction(event -> {
            if (this.projectController != null) {
                this.projectController.sceenShot();
            }
        });
    }
}
