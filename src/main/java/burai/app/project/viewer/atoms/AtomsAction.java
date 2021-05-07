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

package burai.app.project.viewer.atoms;

import java.io.File;

import burai.app.project.QEFXProjectController;
import burai.atoms.design.Design;
import burai.atoms.model.Cell;
import burai.atoms.viewer.AtomsViewer;
import burai.project.Project;
import javafx.scene.layout.BorderPane;

public class AtomsAction {

    private static final double ATOMS_VIEWER_SIZE = 400.0;

    public static double getAtomsViewerSize() {
        return ATOMS_VIEWER_SIZE;
    }

    private static final String ATOMS_DESIGN_FILE_NAME = ".design";

    public static File getAtomsDesignFile(Project project) {
        File directory = project == null ? null : project.getDirectory();
        if (directory != null) {
            return new File(directory, ATOMS_DESIGN_FILE_NAME);
        }

        File rootFile = project == null ? null : project.getRootFile();
        File rootDir = rootFile == null ? null : rootFile.getParentFile();
        String rootName = rootFile == null ? null : rootFile.getName();
        if (rootName != null && !rootName.isEmpty()) {
            return new File(rootDir, "." + rootName + ATOMS_DESIGN_FILE_NAME);
        }

        return null;
    }

    private Project project;

    private QEFXProjectController controller;

    private AtomsViewer atomsViewer;

    public AtomsAction(Project project, QEFXProjectController controller) {
        if (project == null) {
            throw new IllegalArgumentException("project is null.");
        }

        if (controller == null) {
            throw new IllegalArgumentException("controller is null.");
        }

        this.project = project;
        this.controller = controller;

        this.atomsViewer = null;
    }

    public QEFXProjectController getController() {
        return this.controller;
    }

    public void showAtoms() {
        if (this.atomsViewer == null) {
            this.initializeAtomsViewer();
        }

        if (this.atomsViewer != null) {
            this.controller.setViewerPane(this.atomsViewer);
        }
    }

    private void initializeAtomsViewer() {
        Cell cell = this.project.getCell();
        if (cell == null) {
            return;
        }

        this.atomsViewer = new AtomsViewer(cell, getAtomsViewerSize());

        File designFile = getAtomsDesignFile(this.project);
        if (designFile != null) {
            this.atomsViewer.setDesign(designFile);
        }

        this.project.addOnFilePathChanged(path -> {
            File designFile_ = getAtomsDesignFile(this.project);
            String designPath = designFile_ == null ? null : designFile_.getPath();
            if (designPath == null || designPath.isEmpty()) {
                return;
            }

            Design design = this.atomsViewer.getDesign();
            if (design != null) {
                design.writeDesign(designPath);
            }
        });

        final BorderPane projectPane;
        if (this.controller != null) {
            projectPane = this.controller.getProjectPane();
        } else {
            projectPane = null;
        }

        if (projectPane != null) {
            this.atomsViewer.addExclusiveNode(() -> {
                return projectPane.getRight();
            });
            this.atomsViewer.addExclusiveNode(() -> {
                return projectPane.getBottom();
            });
        }
    }
}
