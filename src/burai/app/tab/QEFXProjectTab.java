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

package burai.app.tab;

import java.util.Optional;

import javafx.event.Event;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tooltip;
import burai.app.QEFXMain;
import burai.app.QEFXMainController;
import burai.app.project.QEFXProjectController;
import burai.project.Project;
import burai.run.RunningManager;
import burai.run.RunningNode;

public class QEFXProjectTab extends QEFXTab<Project> {

    private QEFXProjectController projectController;

    public QEFXProjectTab(Project project) {
        super(project);

        this.projectController = null;

        this.setupOnCloseRequest();
        this.setupOnClosed();
        this.setupOnSelected();
        this.setupTabTitle();
    }

    public void setProjectController(QEFXProjectController projectController) {
        this.projectController = projectController;
    }

    private void setupOnCloseRequest() {
        this.setOnCloseRequest(event -> {
            if (event != null && (!event.isConsumed())) {
                this.checkProjectWithModeler(event);
            }
            if (event != null && (!event.isConsumed())) {
                this.checkProjectToBeSaved(event);
            }
            if (event != null && (!event.isConsumed())) {
                this.checkProjectInRunning(event);
            }
        });
    }

    private void setupOnClosed() {
        this.setOnClosed(event -> {
            QEFXMainController mainController = null;
            if (this.projectController != null) {
                mainController = this.projectController.getMainController();
            }

            if (mainController != null) {
                mainController.refreshProjectOnExplorer(this.body);
            }

            if (this.projectController != null) {
                this.projectController.detach();
            }
        });
    }

    private void setupOnSelected() {
        this.setOnSelectionChanged(event -> {
            if (this.isSelected()) {
                if (this.projectController != null) {
                    this.projectController.toBeShown();
                }
            }
        });
    }

    private void checkProjectWithModeler(Event event) {
        if (this.projectController == null) {
            return;
        }

        if (this.projectController.isModelerMode() || this.projectController.isModelerSlabMode()) {
            Alert alert = new Alert(AlertType.WARNING);
            QEFXMain.initializeDialogOwner(alert);
            alert.setHeaderText("This project is in Modeler-mode."
                    + System.lineSeparator() + "Do you discard the model ?");
            alert.getButtonTypes().clear();
            alert.getButtonTypes().add(ButtonType.YES);
            alert.getButtonTypes().add(ButtonType.NO);

            Optional<ButtonType> optButtonType = alert.showAndWait();
            if (optButtonType == null || (!optButtonType.isPresent())) {
                return;
            }

            ButtonType buttonType = optButtonType.get();
            if (!ButtonType.YES.equals(buttonType)) {
                if (event != null) {
                    event.consume();
                }
            }
        }
    }

    private void checkProjectToBeSaved(Event event) {
        if (!this.body.isQEInputChanged()) {
            return;
        }

        Alert alert = new Alert(AlertType.WARNING);
        QEFXMain.initializeDialogOwner(alert);
        alert.setHeaderText("This project is changed. Do you save it ?");
        alert.getButtonTypes().clear();
        alert.getButtonTypes().add(ButtonType.YES);
        alert.getButtonTypes().add(ButtonType.NO);
        alert.getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> optButtonType = alert.showAndWait();
        if (optButtonType == null || (!optButtonType.isPresent())) {
            return;
        }

        ButtonType buttonType = optButtonType.get();
        if (ButtonType.YES.equals(buttonType)) {
            if (this.projectController != null) {
                boolean saved = this.projectController.saveFile();
                if (!saved) {
                    if (event != null) {
                        event.consume();
                    }
                }
            }

        } else if (ButtonType.NO.equals(buttonType)) {
            // NOP

        } else {
            if (event != null) {
                event.consume();
            }
        }
    }

    private void checkProjectInRunning(Event event) {
        if (RunningManager.getInstance().getNode(this.body) == null) {
            return;
        }

        Alert alert = new Alert(AlertType.WARNING);
        QEFXMain.initializeDialogOwner(alert);
        alert.setHeaderText("This project is running. Do you delete its calculations ?");
        alert.getButtonTypes().clear();
        alert.getButtonTypes().add(ButtonType.YES);
        alert.getButtonTypes().add(ButtonType.NO);

        Optional<ButtonType> optButtonType = alert.showAndWait();
        if (optButtonType == null || (!optButtonType.isPresent())) {
            return;
        }

        ButtonType buttonType = optButtonType.get();
        if (ButtonType.YES.equals(buttonType)) {
            RunningNode runningNode = null;
            while ((runningNode = RunningManager.getInstance().getNode(this.body)) != null) {
                RunningManager.getInstance().removeNode(runningNode);
            }

        } else {
            if (event != null) {
                event.consume();
            }
        }
    }

    private void setupTabTitle() {
        updateTabTitle();

        this.body.addOnFilePathChanged(path -> this.updateTabTitle());
    }

    private void updateTabTitle() {
        String rootFilePath = this.body.getRootFilePath();

        if (rootFilePath != null) {
            this.setTabTitle(this.body.getRootFileName());
            this.setTooltip(new Tooltip(rootFilePath));

        } else {
            this.setTabTitle(this.body.getDirectoryName());
            this.setTooltip(new Tooltip(this.body.getDirectoryPath()));
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }

        QEFXProjectTab other = (QEFXProjectTab) obj;

        String rootPath1 = this.body.getRootFilePath();
        String rootPath2 = other.body.getRootFilePath();
        if (rootPath1 != null && rootPath2 != null && rootPath1.equals(rootPath2)) {
            return true;
        }

        String dirPath1 = this.body.getDirectoryPath();
        String dirPath2 = other.body.getDirectoryPath();
        if (dirPath1 != null && dirPath2 != null && dirPath1.equals(dirPath2)) {
            return true;
        }

        return false;
    }
}
