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

package burai.app.project.viewer.result;

import java.io.IOException;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import burai.app.QEFXMain;
import burai.app.project.QEFXProjectController;
import burai.app.project.editor.result.QEFXResultFileTree;
import burai.project.Project;

public class ResultAction {

    private Project project;

    private QEFXProjectController controller;

    private QEFXResultExplorer explorer;

    private QEFXResultFileTree fileTree;

    public ResultAction(Project project, QEFXProjectController controller) {
        if (project == null) {
            throw new IllegalArgumentException("project is null.");
        }

        if (controller == null) {
            throw new IllegalArgumentException("controller is null.");
        }

        this.project = project;
        this.controller = controller;

        this.explorer = null;
        this.fileTree = null;
    }

    public QEFXProjectController getController() {
        return this.controller;
    }

    public void showResult() {
        if (this.project.getDirectoryPath() == null) {
            this.showErrorDialog();
            return;
        }

        if (this.explorer == null || this.fileTree == null) {
            this.initializeResult();
            return;
        }

        this.controller.setResultExplorerMode();
    }

    private void showErrorDialog() {
        Alert alert = new Alert(AlertType.ERROR);
        QEFXMain.initializeDialogOwner(alert);
        alert.setHeaderText("This project has not been saved nor run.");
        alert.showAndWait();
    }

    private void initializeResult() {
        this.explorer = new QEFXResultExplorer(this.controller, this.project);

        try {
            this.fileTree = new QEFXResultFileTree(this.controller, this.project);
            this.fileTree.setResultExplorer(this.explorer);
        } catch (IOException e) {
            this.fileTree = null;
            e.printStackTrace();
        }

        if (this.explorer != null && this.fileTree != null) {
            this.controller.setResultExplorerMode(controller2 -> {
                this.explorer.reload();
                this.fileTree.reload();
            });

            this.controller.clearStackedsOnViewerPane();

            Node explorerNode = this.explorer.getNode();
            if (explorerNode != null) {
                this.controller.stackOnViewerPane(explorerNode);
            }

            Node fileTreeNode = this.fileTree.getNode();
            if (fileTreeNode != null) {
                this.controller.setEditorPane(fileTreeNode);
            }
        }
    }
}
