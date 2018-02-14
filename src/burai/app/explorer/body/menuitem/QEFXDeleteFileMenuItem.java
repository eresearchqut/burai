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

package burai.app.explorer.body.menuitem;

import java.util.Optional;

import burai.app.QEFXMain;
import burai.app.explorer.body.QEFXExplorerBody;
import burai.app.icon.QEFXIcon;
import burai.app.icon.QEFXRunningIcon;
import burai.project.Project;
import burai.run.RunningManager;
import burai.run.RunningNode;
import burai.run.RunningType;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class QEFXDeleteFileMenuItem extends QEFXMenuItem {

    public QEFXDeleteFileMenuItem(QEFXIcon icon, QEFXExplorerBody body) {
        super("Delete file");

        if (icon == null) {
            throw new IllegalArgumentException("icon is null.");
        }

        if (body == null) {
            throw new IllegalArgumentException("body is null.");
        }

        if (body.isExplorerMode()) {
            if (icon instanceof QEFXRunningIcon) {
                this.setDisable(true);

            } else {
                this.setOnAction(event -> {
                    body.deleteIcon(icon);
                });
            }

        } else if (body.isRecentlyUsedMode()) {
            this.setOnAction(event -> {
                body.deleteIcon(icon);
            });

        } else if (body.isCalculatingMode()) {
            final RunningNode runningNode;
            if (icon instanceof QEFXRunningIcon) {
                runningNode = ((QEFXRunningIcon) icon).getRunningNode();
            } else {
                runningNode = null;
            }

            if (runningNode != null) {
                this.setOnAction(event -> {
                    this.deleteRunningNode(runningNode);
                });

            } else {
                this.setDisable(true);
            }

        } else if (body.isSearchedMode()) {
            this.setDisable(true);

        } else if (body.isWebMode()) {
            this.setDisable(true);
        }
    }

    private void deleteRunningNode(RunningNode runningNode) {
        if (runningNode == null) {
            return;
        }

        String contentText = "";

        Project project = runningNode.getProject();
        if (project != null) {
            contentText = contentText + "Project: " + project.getRelatedFilePath();
        }

        RunningType runningType = runningNode.getType();
        if (runningType != null) {
            if (!contentText.isEmpty()) {
                contentText = contentText + System.lineSeparator();
            }
            contentText = contentText + "Job: " + runningType.toString();
        }

        int numProcess = runningNode.getNumProcesses();
        if (runningType != null && numProcess > 0) {
            if (!contentText.isEmpty()) {
                contentText = contentText + " ,  ";
            }
            contentText = contentText + "#process: " + numProcess;
        }

        int numThread = runningNode.getNumThreads();
        if (runningType != null && numThread > 0) {
            if (!contentText.isEmpty()) {
                contentText = contentText + " ,  ";
            }
            contentText = contentText + "#thread: " + numThread;
        }

        contentText = contentText + System.lineSeparator();

        Alert alert = new Alert(AlertType.CONFIRMATION);
        QEFXMain.initializeDialogOwner(alert);
        alert.setHeaderText("Calculation will be deleted.");
        alert.setContentText(contentText);

        Optional<ButtonType> optButtonType = alert.showAndWait();
        if (optButtonType == null || (!optButtonType.isPresent())) {
            return;
        }
        if (!ButtonType.OK.equals(optButtonType.get())) {
            return;
        }

        RunningManager.getInstance().removeNode(runningNode);
    }
}
