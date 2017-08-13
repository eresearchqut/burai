/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.viewer.atoms;

import java.io.File;

import burai.app.project.QEFXProjectController;
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
        String directoryPath = project == null ? null : project.getDirectoryPath();
        if (directoryPath == null || directoryPath.isEmpty()) {
            return null;
        }

        return new File(directoryPath, ATOMS_DESIGN_FILE_NAME);
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
