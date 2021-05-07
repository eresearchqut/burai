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

import java.io.IOException;

import burai.app.project.QEFXProjectController;
import burai.app.project.editor.result.movie.QEFXMovieEditor;
import burai.app.project.viewer.result.QEFXResultButton;
import burai.atoms.design.Design;
import burai.atoms.model.Cell;
import burai.atoms.model.property.CellProperty;
import burai.atoms.viewer.AtomsViewer;
import burai.atoms.viewer.AtomsViewerInterface;
import burai.com.consts.Constants;
import burai.com.math.Matrix3D;
import burai.project.Project;
import burai.project.property.ProjectGeometry;
import burai.project.property.ProjectGeometryList;
import burai.project.property.ProjectProperty;

public abstract class QEFXMovieButton extends QEFXResultButton<QEFXMovieViewer, QEFXMovieEditor> {

    private boolean mdMode;

    private Project project;

    private ProjectProperty projectProperty;

    protected QEFXMovieButton(QEFXProjectController projectController,
            Project project, ProjectProperty projectProperty, String title, String subTitle, boolean mdMode) {

        super(projectController, title, subTitle);

        if (project == null) {
            throw new IllegalArgumentException("project is null.");
        }

        if (projectProperty == null) {
            throw new IllegalArgumentException("projectProperty is null.");
        }

        this.project = project;
        this.projectProperty = projectProperty;
        this.mdMode = mdMode;
    }

    private Design createDesign() {
        if (this.projectController == null) {
            return null;
        }

        AtomsViewerInterface atomsViewer = this.projectController.getAtomsViewer();
        if (atomsViewer != null && atomsViewer instanceof AtomsViewer) {
            return ((AtomsViewer) atomsViewer).getDesign();
        }

        return null;
    }

    @Override
    protected QEFXMovieViewer createResultViewer() throws IOException {
        if (this.projectController == null) {
            return null;
        }

        ProjectGeometryList projectGeometryList = null;
        if (this.mdMode) {
            projectGeometryList = this.projectProperty.getMdList();
        } else {
            projectGeometryList = this.projectProperty.getOptList();
        }

        if (projectGeometryList == null || projectGeometryList.numGeometries() < 1) {
            return null;
        }

        Cell cell = null;

        try {
            ProjectGeometry projectGeometry = projectGeometryList.getGeometry(0);
            if (projectGeometry == null) {
                return null;
            }

            double[][] lattice = projectGeometry.getCell();
            lattice = Matrix3D.mult(Constants.BOHR_RADIUS_ANGS, lattice);

            if (lattice == null || lattice.length < 3) {
                return null;
            }
            if (lattice[0] == null || lattice[0].length < 3) {
                return null;
            }
            if (lattice[1] == null || lattice[1].length < 3) {
                return null;
            }
            if (lattice[2] == null || lattice[2].length < 3) {
                return null;
            }

            cell = new Cell(lattice);

            String axis = projectGeometryList.getCellAxis();
            if (axis != null) {
                cell.setProperty(CellProperty.AXIS, axis);
            } else {
                cell.removeProperty(CellProperty.AXIS);
            }

            boolean molecule = projectGeometryList.isMolecule();
            cell.setProperty(CellProperty.MOLECULE, molecule);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        QEFXMovieViewer movieViewer = new QEFXMovieViewer(
                this.projectController, this.projectProperty, cell, this.mdMode);

        Design design = this.createDesign();
        if (design != null) {
            movieViewer.setDesign(design);
        }

        return movieViewer;
    }

    @Override
    protected QEFXMovieEditor createResultEditor(QEFXMovieViewer resultViewer) throws IOException {
        if (resultViewer == null) {
            return null;
        }

        if (this.project == null) {
            return null;
        }

        if (this.projectController == null) {
            return null;
        }

        return new QEFXMovieEditor(this.projectController, project, resultViewer);
    }
}
