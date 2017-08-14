/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.viewer.result.movie;

import burai.app.project.QEFXProjectController;
import burai.app.project.viewer.atoms.AtomsAction;
import burai.app.project.viewer.result.QEFXResultViewer;
import burai.atoms.design.Design;
import burai.atoms.model.Cell;
import burai.atoms.viewer.AtomsViewer;
import burai.atoms.viewer.AtomsViewerInterface;
import burai.project.property.ProjectProperty;
import javafx.scene.layout.BorderPane;

public class QEFXMovieViewer extends QEFXResultViewer<QEFXMovieViewerController> {

    private Design design;

    public QEFXMovieViewer(QEFXProjectController projectController, ProjectProperty projectProperty, Cell cell,
            boolean mdMode) {

        super(cell == null ? null : new AtomsViewer(cell, AtomsAction.getAtomsViewerSize(), true),
                new QEFXMovieViewerController(projectController, projectProperty, cell, mdMode));

        if (this.node != null && (this.node instanceof AtomsViewerInterface)) {
            this.setupAtomsViewer((AtomsViewerInterface) this.node, projectController);
        }

        this.design = null;
    }

    private void setupAtomsViewer(AtomsViewerInterface atomsViewer, QEFXProjectController projectController) {
        if (atomsViewer == null) {
            return;
        }

        final BorderPane projectPane;
        if (projectController != null) {
            projectPane = projectController.getProjectPane();
        } else {
            projectPane = null;
        }

        if (projectPane != null) {
            atomsViewer.addExclusiveNode(() -> {
                return projectPane.getRight();
            });
            atomsViewer.addExclusiveNode(() -> {
                return projectPane.getBottom();
            });
        }
    }

    @Override
    public void reload() {
        this.setDesign(this.design);
        super.reload();
    }

    public void setDesign(Design design) {
        if (design == null) {
            return;
        }

        this.design = design;

        if (this.node != null && (this.node instanceof AtomsViewer)) {
            ((AtomsViewer) this.node).setDesign(this.design);
        }
    }
}
