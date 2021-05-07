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
