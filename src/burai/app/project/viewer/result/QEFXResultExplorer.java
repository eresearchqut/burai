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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.TilePane;
import burai.app.project.QEFXProjectController;
import burai.app.project.viewer.result.band.QEFXBandButton;
import burai.app.project.viewer.result.graph.EnergyType;
import burai.app.project.viewer.result.graph.LatticeViewerType;
import burai.app.project.viewer.result.graph.QEFXDosButton;
import burai.app.project.viewer.result.graph.QEFXMdEnergyButton;
import burai.app.project.viewer.result.graph.QEFXMdLatticeButton;
import burai.app.project.viewer.result.graph.QEFXOptEnergyButton;
import burai.app.project.viewer.result.graph.QEFXOptForceButton;
import burai.app.project.viewer.result.graph.QEFXOptLatticeButton;
import burai.app.project.viewer.result.graph.QEFXOptStressButton;
import burai.app.project.viewer.result.graph.QEFXScfButton;
import burai.app.project.viewer.result.log.QEFXCrashButton;
import burai.app.project.viewer.result.log.QEFXErrorButton;
import burai.app.project.viewer.result.log.QEFXInputButton;
import burai.app.project.viewer.result.log.QEFXOutputButton;
import burai.app.project.viewer.result.movie.QEFXMdMovieButton;
import burai.app.project.viewer.result.movie.QEFXOptMovieButton;
import burai.com.keys.PriorKeyEvent;
import burai.project.Project;
import burai.run.RunningManager;

public class QEFXResultExplorer {

    private static final double PANE_HEIGHT = 100.0;
    private static final double PANE_WIDTH = 100.0;

    private static final String SCROLL_CLASS = "result-expr-scroll";
    private static final String TILE_CLASS = "result-expr-tile";

    private static final long AUTORELOADING_TIME = 2000L;

    private Project project;

    private QEFXProjectController projectController;

    private List<QEFXResultButton<?, ?>> buttonList;
    private Map<String, QEFXResultButton<?, ?>> buttonMap;

    private boolean autoReloading;

    private ScrollPane scrollPane;

    private TilePane tilePane;

    public QEFXResultExplorer(QEFXProjectController projectController, Project project) {
        if (projectController == null) {
            throw new IllegalArgumentException("projectController is null.");
        }

        if (project == null) {
            throw new IllegalArgumentException("project is null.");
        }

        this.projectController = projectController;
        this.project = project;

        this.buttonList = null;
        this.buttonMap = null;

        this.autoReloading = false;

        this.createScrollPane();
        this.createTilePane();

        this.reload();
    }

    public Node getNode() {
        return this.scrollPane;
    }

    private void createScrollPane() {
        this.scrollPane = new ScrollPane();
        this.scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
        this.scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        this.scrollPane.setFitToHeight(true);
        this.scrollPane.setFitToWidth(true);
        this.scrollPane.setPrefHeight(PANE_HEIGHT);
        this.scrollPane.setPrefWidth(PANE_WIDTH);
        this.scrollPane.setPannable(false);
        this.scrollPane.getStyleClass().add(SCROLL_CLASS);
        this.setupKeys(this.scrollPane);
    }

    private void createTilePane() {
        this.tilePane = new TilePane();
        this.tilePane.getStyleClass().add(TILE_CLASS);
        this.tilePane.setFocusTraversable(false);
        this.tilePane.setOnMouseClicked(event -> this.tilePane.requestFocus());
        this.scrollPane.setContent(this.tilePane);
    }

    private void setupKeys(Node node) {
        if (node == null) {
            return;
        }

        node.setOnKeyPressed(event -> {
            if (event == null || PriorKeyEvent.isPriorKeyEvent(event)) {
                return;
            }

            if (KeyCode.F5.equals(event.getCode())) {
                // F5
                this.reload();
            }
        });
    }

    public void reload() {
        if (this.buttonList != null) {
            this.buttonList.clear();
        }

        this.updateLogButtons();
        this.updateScfButtons();
        this.updateOptButtons();
        this.updateMdButtons();
        this.updateDosButtons();
        this.updateBandButtons();

        int numNode1 = this.buttonList == null ? 0 : this.buttonList.size();
        int numNode2 = this.tilePane.getChildren().size();
        boolean changed = (numNode1 != numNode2);

        if (!changed) {
            for (int i = 0; i < numNode1; i++) {
                QEFXResultButton<?, ?> button = this.buttonList.get(i);
                Node node1 = button == null ? null : button.getNode();
                Node node2 = this.tilePane.getChildren().get(i);
                if (node1 != node2) {
                    changed = true;
                    break;
                }
            }
        }

        if (changed && this.buttonList != null) {
            this.tilePane.getChildren().clear();
            for (QEFXResultButton<?, ?> button : this.buttonList) {
                Node node = button == null ? null : button.getNode();
                if (node != null) {
                    this.tilePane.getChildren().add(node);
                }
            }
        }

        synchronized (this) {
            if (!this.autoReloading) {
                this.autoReloading = true;
                this.autoReload();
            }
        }
    }

    private void autoReload() {
        Thread thread = new Thread(() -> {
            synchronized (this) {
                while (RunningManager.getInstance().getNode(this.project) != null) {
                    try {
                        this.wait(AUTORELOADING_TIME);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Platform.runLater(() -> this.reload());
                }

                this.autoReloading = false;
            }
        });

        thread.start();
    }

    private void updateLogButtons() {
        this.updateButton("QEFXCrashButton", () -> {
            return QEFXCrashButton.getWrapper(this.projectController, this.project);
        });

        this.updateButton("QEFXInputButton", () -> {
            return QEFXInputButton.getWrapper(this.projectController, this.project);
        });

        File directory = this.project.getDirectory();
        if (directory == null) {
            return;
        }

        try {
            if (!directory.isDirectory()) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        Comparator<File> fileComparator = (file1, file2) -> {
            long time1 = 0L;
            try {
                time1 = file1 == null ? Long.MAX_VALUE : file1.lastModified();
            } catch (Exception e) {
                time1 = Long.MAX_VALUE;
            }

            long time2 = 0L;
            try {
                time2 = file2 == null ? Long.MAX_VALUE : file2.lastModified();
            } catch (Exception e) {
                time2 = Long.MAX_VALUE;
            }

            if (time1 < time2) {
                return -1;
            } else if (time1 > time2) {
                return 1;
            } else {
                return 0;
            }
        };

        String logName0 = this.project.getLogFileName("#");
        if (logName0 != null && !(logName0.isEmpty())) {
            logName0 = logName0.substring(0, logName0.length() - 1);
        }

        File[] logFiles = null;
        if (logName0 != null && !(logName0.isEmpty())) {
            String logName1 = logName0;
            logFiles = directory.listFiles((dir, name) -> {
                return (name != null && name.startsWith(logName1));
            });
        }

        if (logFiles != null) {
            Arrays.sort(logFiles, fileComparator);

            int nstem = logName0 == null ? 0 : logName0.length();
            for (File logFile : logFiles) {
                String logName = logFile == null ? null : logFile.getName();
                if (logName == null || logName.isEmpty()) {
                    continue;
                }

                String ext = logName.length() <= nstem ? null : logName.substring(nstem);
                if (ext == null || ext.isEmpty()) {
                    continue;
                }

                this.updateButton("QEFXOutputButton#" + ext, () -> {
                    return QEFXOutputButton.getWrapper(this.projectController, this.project, ext);
                });
            }
        }

        String errName0 = this.project.getErrFileName("#");
        if (errName0 != null && !(errName0.isEmpty())) {
            errName0 = errName0.substring(0, errName0.length() - 1);
        }

        File[] errFiles = null;
        if (errName0 != null && !(errName0.isEmpty())) {
            String errName1 = errName0;
            errFiles = directory.listFiles((dir, name) -> {
                return (name != null && name.startsWith(errName1));
            });
        }

        if (errFiles != null) {
            Arrays.sort(errFiles, fileComparator);

            int nstem = errName0 == null ? 0 : errName0.length();
            for (File errFile : errFiles) {
                String errName = errFile == null ? null : errFile.getName();
                if (errName == null || errName.isEmpty()) {
                    continue;
                }

                String ext = errName.length() <= nstem ? null : errName.substring(nstem);
                if (ext == null || ext.isEmpty()) {
                    continue;
                }

                this.updateButton("QEFXErrorButton#" + ext, () -> {
                    return QEFXErrorButton.getWrapper(this.projectController, this.project, ext);
                });
            }
        }
    }

    private void updateScfButtons() {
        this.updateButton("QEFXScfButton", () -> {
            return QEFXScfButton.getWrapper(this.projectController, this.project);
        });
    }

    private void updateOptButtons() {
        this.updateButton("QEFXOptEnergyButton", () -> {
            return QEFXOptEnergyButton.getWrapper(this.projectController, this.project);
        });

        this.updateButton("QEFXOptForceButton", () -> {
            return QEFXOptForceButton.getWrapper(this.projectController, this.project);
        });

        this.updateButton("QEFXOptStressButton", () -> {
            return QEFXOptStressButton.getWrapper(this.projectController, this.project);
        });

        this.updateButton("QEFXOptLatticeButton#A", () -> {
            return QEFXOptLatticeButton.getWrapper(this.projectController, this.project, LatticeViewerType.A);
        });

        this.updateButton("QEFXOptLatticeButton#B", () -> {
            return QEFXOptLatticeButton.getWrapper(this.projectController, this.project, LatticeViewerType.B);
        });

        this.updateButton("QEFXOptLatticeButton#C", () -> {
            return QEFXOptLatticeButton.getWrapper(this.projectController, this.project, LatticeViewerType.C);
        });

        this.updateButton("QEFXOptLatticeButton#ANGLE", () -> {
            return QEFXOptLatticeButton.getWrapper(this.projectController, this.project, LatticeViewerType.ANGLE);
        });

        this.updateButton("QEFXOptMovieButton", () -> {
            return QEFXOptMovieButton.getWrapper(this.projectController, this.project);
        });
    }

    private void updateMdButtons() {
        this.updateButton("QEFXMdEnergyButton#TOTAL", () -> {
            return QEFXMdEnergyButton.getWrapper(this.projectController, this.project, EnergyType.TOTAL);
        });

        this.updateButton("QEFXMdEnergyButton#KINETIC", () -> {
            return QEFXMdEnergyButton.getWrapper(this.projectController, this.project, EnergyType.KINETIC);
        });

        this.updateButton("QEFXMdEnergyButton#CONSTANT", () -> {
            return QEFXMdEnergyButton.getWrapper(this.projectController, this.project, EnergyType.CONSTANT);
        });

        this.updateButton("QEFXMdEnergyButton#TEMPERATURE", () -> {
            return QEFXMdEnergyButton.getWrapper(this.projectController, this.project, EnergyType.TEMPERATURE);
        });

        this.updateButton("QEFXMdLatticeButton#A", () -> {
            return QEFXMdLatticeButton.getWrapper(this.projectController, this.project, LatticeViewerType.A);
        });

        this.updateButton("QEFXMdLatticeButton#B", () -> {
            return QEFXMdLatticeButton.getWrapper(this.projectController, this.project, LatticeViewerType.B);
        });

        this.updateButton("QEFXMdLatticeButton#C", () -> {
            return QEFXMdLatticeButton.getWrapper(this.projectController, this.project, LatticeViewerType.C);
        });

        this.updateButton("QEFXMdLatticeButton#ANGLE", () -> {
            return QEFXMdLatticeButton.getWrapper(this.projectController, this.project, LatticeViewerType.ANGLE);
        });

        this.updateButton("QEFXMdMovieButton", () -> {
            return QEFXMdMovieButton.getWrapper(this.projectController, this.project);
        });
    }

    private void updateDosButtons() {
        this.updateButton("QEFXDosButton", () -> {
            return QEFXDosButton.getWrapper(this.projectController, this.project);
        });
    }

    private void updateBandButtons() {
        this.updateButton("QEFXBandButton", () -> {
            return QEFXBandButton.getWrapper(this.projectController, this.project);
        });
    }

    private <T extends QEFXResultButton<?, ?>> boolean updateButton(String key, ButtonGetter<T> buttonGetter) {
        if (key == null) {
            return false;
        }
        if (buttonGetter == null) {
            return false;
        }

        QEFXResultButton<?, ?> button = null;
        QEFXResultButtonWrapper<T> wrapper = buttonGetter.getWrapper();
        if (wrapper != null) {
            if (this.buttonMap != null && this.buttonMap.containsKey(key)) {
                button = this.buttonMap.get(key);
            } else {
                button = wrapper.getInstance();
            }
        }

        if (button != null) {
            if (this.buttonMap == null) {
                this.buttonMap = new HashMap<String, QEFXResultButton<?, ?>>();
            }
            this.buttonMap.put(key, button);

            if (this.buttonList == null) {
                this.buttonList = new ArrayList<QEFXResultButton<?, ?>>();
            }

            this.buttonList.add(button);
            return true;
        }

        return false;
    }

    @FunctionalInterface
    private static interface ButtonGetter<T extends QEFXResultButton<?, ?>> {

        public abstract QEFXResultButtonWrapper<T> getWrapper();

    }
}
