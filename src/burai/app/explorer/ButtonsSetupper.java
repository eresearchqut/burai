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

package burai.app.explorer;

import java.io.File;

import javafx.scene.control.Button;
import burai.app.explorer.body.QEFXExplorerBody;
import burai.com.env.Environments;
import burai.com.graphic.svg.SVGLibrary;
import burai.com.graphic.svg.SVGLibrary.SVGData;
import burai.matapi.MaterialsAPILoader;

public class ButtonsSetupper extends ExplorerSetupper {

    private static final double GRAPHIC_SIZE = 20.0;

    private boolean hasSearched;

    protected ButtonsSetupper(QEFXExplorerController controller) {
        super(controller);
        this.hasSearched = false;
    }

    protected void toBeSearched() {
        this.hasSearched = true;
    }

    protected void setupRecentButton(Button recentButton) {
        if (recentButton == null) {
            return;
        }

        recentButton.setOnAction(event -> {
            this.controller.getLocationField().setText(QEFXExplorerBody.CODE_RECENTLY_USED);
            this.controller.updateExplorerBody();
        });
    }

    protected void setupComputerButton(Button computerButton) {
        if (computerButton == null) {
            return;
        }

        computerButton.setOnAction(event -> {
            String rootPath = Environments.getRootPath();
            this.controller.getLocationField().setText(rootPath == null ? "" : rootPath);
            this.controller.updateExplorerBody();
        });
    }

    protected void setupProjectsButton(Button projectsButton) {
        if (projectsButton == null) {
            return;
        }

        projectsButton.setOnAction(event -> {
            String projPath = Environments.getProjectsPath();
            this.controller.getLocationField().setText(projPath == null ? "" : projPath);
            this.controller.updateExplorerBody();
        });
    }

    protected void setupCalculatingButton(Button calculatingButton) {
        if (calculatingButton == null) {
            return;
        }

        calculatingButton.setOnAction(event -> {
            this.controller.getLocationField().setText(QEFXExplorerBody.CODE_CALCULATING);
            this.controller.updateExplorerBody();
        });
    }

    protected void setupSearchedButton(Button searchedButton) {
        if (searchedButton == null) {
            return;
        }

        searchedButton.setOnAction(event -> {
            String searchedPath = null;
            if (!this.hasSearched) {
                searchedPath = MaterialsAPILoader.getLastDirectory();
                searchedPath = searchedPath == null ? null : searchedPath.trim();
            }

            boolean existsSearched = false;
            if (searchedPath != null && (!searchedPath.isEmpty())) {
                try {
                    if (new File(searchedPath).isDirectory()) {
                        existsSearched = true;
                    }
                } catch (Exception e) {
                    existsSearched = false;
                }
            }

            if (existsSearched) {
                this.controller.getLocationField().setText(searchedPath);
            } else {
                this.controller.getLocationField().setText(QEFXExplorerBody.CODE_SEARCHED);
            }

            this.controller.updateExplorerBody();
        });
    }

    protected void setupWebButton(Button webButton) {
        if (webButton == null) {
            return;
        }

        webButton.setText(" " + webButton.getText());
        webButton.setGraphic(SVGLibrary.getGraphic(SVGData.EARTH, GRAPHIC_SIZE));

        webButton.setOnAction(event -> {
            this.controller.getLocationField().setText(QEFXExplorerBody.CODE_WEB);
            this.controller.updateExplorerBody();
        });
    }

    protected void setupDownloadsButton(Button downloadsButton) {
        if (downloadsButton == null) {
            return;
        }

        downloadsButton.setOnAction(event -> {
            String downloadsPath = Environments.getDownloadsPath();
            this.controller.getLocationField().setText(downloadsPath == null ? "" : downloadsPath);
            this.controller.updateExplorerBody();
        });
    }
}
