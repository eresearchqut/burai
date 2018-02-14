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

package burai.app.project.viewer.result.graph;

import java.io.File;
import java.io.IOException;

import burai.app.project.QEFXProjectController;
import burai.app.project.viewer.result.QEFXResultButtonWrapper;
import burai.project.Project;
import burai.project.property.ProjectGeometryList;
import burai.project.property.ProjectProperty;

public class QEFXMdLatticeButton extends QEFXGraphButton<QEFXLatticeViewer> {

    private static final String FILE_NAME = ".burai.graph.md.latt";

    private static final String BUTTON_TITLE = "MD";
    private static final String BUTTON_SUBTITLE = ".latt";
    private static final String BUTTON_FONT_COLOR = "-fx-text-fill: derive(limegreen, -20.0%)";
    private static final String BUTTON_BACKGROUND = "-fx-background-color: snow";

    public static QEFXResultButtonWrapper<QEFXMdLatticeButton> getWrapper(
            QEFXProjectController projectController, Project project, LatticeViewerType lattVType) {

        if (projectController == null) {
            return null;
        }

        ProjectProperty projectProperty = project == null ? null : project.getProperty();
        if (projectProperty == null) {
            return null;
        }

        if (lattVType == null) {
            return null;
        }

        ProjectGeometryList projectGeometryList = projectProperty.getMdList();
        if (projectGeometryList == null || projectGeometryList.numGeometries() < 2) {
            return null;
        }

        if (!(new GeometryChecker(projectGeometryList).isAvailableLattice(lattVType))) {
            return null;
        }

        return () -> {
            QEFXMdLatticeButton button = new QEFXMdLatticeButton(projectController, projectProperty, lattVType);

            String propPath = project == null ? null : project.getDirectoryPath();
            File propFile = propPath == null ? null : new File(propPath, FILE_NAME + "." + lattVType.toString());
            if (propFile != null) {
                button.setPropertyFile(propFile);
            }

            return button;
        };
    }

    private LatticeViewerType lattVType;

    private ProjectProperty projectProperty;

    private QEFXMdLatticeButton(QEFXProjectController projectController,
            ProjectProperty projectProperty, LatticeViewerType lattVType) {

        super(projectController,
                BUTTON_TITLE, BUTTON_SUBTITLE + "." + (lattVType == null ? "" : lattVType.name()));

        if (projectProperty == null) {
            throw new IllegalArgumentException("projectProperty is null.");
        }

        if (lattVType == null) {
            throw new IllegalArgumentException("lattVType is null.");
        }

        this.projectProperty = projectProperty;
        this.lattVType = lattVType;

        this.setIconStyle(BUTTON_BACKGROUND);
        this.setLabelStyle(BUTTON_FONT_COLOR);
    }

    @Override
    protected QEFXLatticeViewer createGraphViewer() throws IOException {
        if (this.projectController == null) {
            return null;
        }

        return new QEFXLatticeViewer(this.projectController, this.projectProperty, this.lattVType, true);
    }
}
