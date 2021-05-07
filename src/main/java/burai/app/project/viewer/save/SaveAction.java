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

package burai.app.project.viewer.save;

import java.io.File;

import burai.app.QEFXMain;
import burai.app.QEFXMainController;
import burai.app.project.QEFXProjectController;
import burai.com.env.Environments;
import burai.project.Project;
import burai.run.RunningManager;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class SaveAction {

    private Project project;

    private QEFXProjectController controller;

    public SaveAction(Project project, QEFXProjectController controller) {
        if (project == null) {
            throw new IllegalArgumentException("project is null.");
        }

        if (controller == null) {
            throw new IllegalArgumentException("controller is null.");
        }

        this.project = project;
        this.controller = controller;
    }

    public boolean saveProject() {
        if (this.isThereDirectory()) {
            this.project.saveQEInputs();
            SaveText saveText = new SaveText(this.controller);
            saveText.playDecayingAnimation();
            return true;

        } else {
            return this.saveProjectAsNew();
        }
    }

    private boolean isThereDirectory() {
        String dirPath = this.project.getDirectoryPath();
        if (dirPath == null || dirPath.isEmpty()) {
            return false;
        }

        try {
            File dirFile = new File(dirPath);
            if (!dirFile.isDirectory()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public boolean saveProjectAsNew() {
        if (RunningManager.getInstance().getNode(this.project) != null) {
            this.showDialogProjectRunning();
            return false;
        }

        File directory = null;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save project");

        String projPath = Environments.getProjectsPath();
        if (projPath != null) {
            File projDir = new File(projPath);
            try {
                if (projDir.isDirectory()) {
                    fileChooser.setInitialDirectory(projDir);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Stage stage = this.controller.getStage();
        if (stage != null) {
            directory = fileChooser.showSaveDialog(stage);
        }

        if (directory == null) {
            return false;
        }

        try {
            if (directory.exists()) {
                this.showDialogDirectoryBeing(directory);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        QEFXMainController mainController = this.controller.getMainController();
        if (mainController != null) {
            mainController.offerOnHomeTabSelected(explorerFacade -> {
                if (explorerFacade != null) {
                    explorerFacade.refreshProject(this.project);
                }
            });
        }

        this.project.saveQEInputs(directory.getPath());
        Environments.addRecentFilePath(this.project.getRelatedFilePath());

        if (mainController != null) {
            mainController.offerOnHomeTabSelected(explorerFacade -> {
                if (explorerFacade != null && explorerFacade.isRecentlyUsedMode()) {
                    explorerFacade.reloadLocation();
                }
            });
        }

        File parentFile = directory.getParentFile();
        if (mainController != null && parentFile != null) {
            mainController.offerOnHomeTabSelected(explorerFacade -> {
                String location = null;
                if (explorerFacade != null) {
                    location = explorerFacade.getLocation();
                }

                File locationFile = null;
                if (location != null && (!location.isEmpty())) {
                    locationFile = new File(location);
                }

                if (locationFile != null && locationFile.equals(parentFile)) {
                    explorerFacade.reloadLocation();
                }
            });
        }

        SaveText saveText = new SaveText(this.controller);
        saveText.playDecayingAnimation();
        return true;
    }

    private void showDialogProjectRunning() {
        Alert alert = new Alert(AlertType.ERROR);
        QEFXMain.initializeDialogOwner(alert);
        alert.setHeaderText("Cannot save as new project, while running.");
        alert.showAndWait();
    }

    private void showDialogDirectoryBeing(File directory) {
        if (directory == null) {
            return;
        }

        Alert alert = new Alert(AlertType.ERROR);
        QEFXMain.initializeDialogOwner(alert);
        alert.setHeaderText(directory.getName() + " already exists.");
        alert.showAndWait();
    }
}
