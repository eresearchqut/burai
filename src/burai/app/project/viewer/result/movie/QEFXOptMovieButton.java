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
import burai.app.project.viewer.result.QEFXResultButtonWrapper;
import burai.project.Project;
import burai.project.property.ProjectGeometryList;
import burai.project.property.ProjectProperty;

public class QEFXOptMovieButton extends QEFXMovieButton {

    private static final String BUTTON_TITLE = "OPT";
    private static final String BUTTON_SUBTITLE = ".movie";
    private static final String BUTTON_FONT_COLOR = "-fx-text-fill: snow";
    private static final String BUTTON_BACKGROUND = "-fx-background-color: mediumorchid";

    public static QEFXResultButtonWrapper<QEFXOptMovieButton> getWrapper(QEFXProjectController projectController, Project project) {
        if (projectController == null) {
            return null;
        }

        if (project == null) {
            return null;
        }

        ProjectProperty projectProperty = project.getProperty();
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

        return () -> new QEFXOptMovieButton(projectController, project, projectProperty);
    }

    private QEFXOptMovieButton(QEFXProjectController projectController, Project project, ProjectProperty projectProperty) {
        super(projectController, project, projectProperty, BUTTON_TITLE, BUTTON_SUBTITLE, false);

        this.setIconStyle(BUTTON_BACKGROUND);
        this.setLabelStyle(BUTTON_FONT_COLOR);
    }
}
