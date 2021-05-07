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

package burai.app.project.viewer.result.band;

import java.io.File;
import java.io.IOException;

import burai.app.project.QEFXProjectController;
import burai.app.project.editor.result.band.QEFXBandEditor;
import burai.app.project.viewer.result.QEFXResultButton;
import burai.app.project.viewer.result.QEFXResultButtonWrapper;
import burai.project.Project;
import burai.project.property.BandData;
import burai.project.property.ProjectBand;
import burai.project.property.ProjectBandPaths;
import burai.project.property.ProjectEnergies;
import burai.project.property.ProjectProperty;
import burai.project.property.ProjectStatus;

public class QEFXBandButton extends QEFXResultButton<QEFXBandViewer, QEFXBandEditor> {

    private static final String FILE_NAME = ".burai.graph.band";

    private static final String BUTTON_TITLE = "BAND";
    private static final String BUTTON_FONT_COLOR = "-fx-text-fill: ivory";
    private static final String BUTTON_BACKGROUND = "-fx-background-color: derive(lightslategrey, -55.0%)";

    public static QEFXResultButtonWrapper<QEFXBandButton> getWrapper(QEFXProjectController projectController, Project project) {
        if (projectController == null) {
            return null;
        }

        ProjectProperty projectProperty = project == null ? null : project.getProperty();
        if (projectProperty == null) {
            return null;
        }

        ProjectStatus projectStatus = projectProperty.getStatus();
        if (projectStatus == null || (!projectStatus.isBandDone())) {
            return null;
        }

        ProjectEnergies projectEnergies = projectProperty.getFermiEnergies();
        if (projectEnergies == null || projectEnergies.numEnergies() < 1) {
            return null;
        }

        ProjectBand projectBand = projectProperty.getBand();
        if (projectBand == null) {
            return null;
        }

        ProjectBandPaths projectBandPaths = projectProperty.getBandPaths();
        if (projectBandPaths == null || projectBandPaths.numPoints() < 1) {
            return null;
        }

        BandData bandData = projectBand.getBandData();
        if (bandData == null) {
            return null;
        }

        String dirPath = project == null ? null : project.getDirectoryPath();
        String fileName = project == null ? null : (project.getPrefixName() + ".band1.gnu");

        File file = null;
        if (dirPath != null && fileName != null) {
            file = new File(dirPath, fileName);
        }

        try {
            if (file == null || (!file.isFile()) || (file.length() <= 0L)) {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return () -> {
            QEFXBandButton button = new QEFXBandButton(projectController, projectProperty);

            String propPath = project == null ? null : project.getDirectoryPath();
            File propFile = propPath == null ? null : new File(propPath, FILE_NAME);
            if (propFile != null) {
                button.propertyFile = propFile;
            }

            return button;
        };
    }

    private File propertyFile;

    private ProjectProperty projectProperty;

    private QEFXBandButton(QEFXProjectController projectController, ProjectProperty projectProperty) {
        super(projectController, BUTTON_TITLE, null);

        if (projectProperty == null) {
            throw new IllegalArgumentException("projectProperty is null.");
        }

        this.propertyFile = null;
        this.projectProperty = projectProperty;

        this.setIconStyle(BUTTON_BACKGROUND);
        this.setLabelStyle(BUTTON_FONT_COLOR);
    }

    @Override
    protected final QEFXBandViewer createResultViewer() throws IOException {
        if (this.projectController == null) {
            return null;
        }

        QEFXBandViewer viewer = new QEFXBandViewer(this.projectController, this.projectProperty);

        if (viewer != null) {
            QEFXBandViewerController controller = viewer.getController();
            if (controller != null) {
                controller.setPropertyFile(this.propertyFile);
            }
        }

        return viewer;
    }

    @Override
    protected final QEFXBandEditor createResultEditor(QEFXBandViewer resultViewer) throws IOException {
        if (resultViewer == null) {
            return null;
        }

        if (this.projectController == null) {
            return null;
        }

        return new QEFXBandEditor(this.projectController, resultViewer);
    }
}
