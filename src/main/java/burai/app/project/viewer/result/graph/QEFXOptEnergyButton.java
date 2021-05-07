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

public class QEFXOptEnergyButton extends QEFXGraphButton<QEFXEnergyViewer> {

    private static final String FILE_NAME = ".burai.graph.opt.ene";

    private static final String BUTTON_TITLE = "OPT";
    private static final String BUTTON_SUBTITLE = ".ene";
    private static final String BUTTON_FONT_COLOR = "-fx-text-fill: mediumorchid";
    private static final String BUTTON_BACKGROUND = "-fx-background-color: snow";

    public static QEFXResultButtonWrapper<QEFXOptEnergyButton> getWrapper(QEFXProjectController projectController, Project project) {
        if (projectController == null) {
            return null;
        }

        ProjectProperty projectProperty = project == null ? null : project.getProperty();
        if (projectProperty == null) {
            return null;
        }

        ProjectGeometryList projectGeometryList = projectProperty.getOptList();
        if (projectGeometryList == null || projectGeometryList.numGeometries() < 1) {
            return null;
        }

        if (!projectGeometryList.hasAnyConvergedGeometries()) {
            return null;
        }

        return () -> {
            QEFXOptEnergyButton button = new QEFXOptEnergyButton(projectController, projectProperty);

            String propPath = project == null ? null : project.getDirectoryPath();
            File propFile = propPath == null ? null : new File(propPath, FILE_NAME);
            if (propFile != null) {
                button.setPropertyFile(propFile);
            }

            return button;
        };
    }

    private ProjectProperty projectProperty;

    private QEFXOptEnergyButton(QEFXProjectController projectController, ProjectProperty projectProperty) {
        super(projectController, BUTTON_TITLE, BUTTON_SUBTITLE);

        if (projectProperty == null) {
            throw new IllegalArgumentException("projectProperty is null.");
        }

        this.projectProperty = projectProperty;

        this.setIconStyle(BUTTON_BACKGROUND);
        this.setLabelStyle(BUTTON_FONT_COLOR);
    }

    @Override
    protected QEFXEnergyViewer createGraphViewer() throws IOException {
        if (this.projectController == null) {
            return null;
        }

        return new QEFXEnergyViewer(this.projectController, this.projectProperty, EnergyType.TOTAL, false);
    }
}
