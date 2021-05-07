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

package burai.app.project.editor.result.movie;

import java.io.IOException;

import burai.app.project.QEFXProjectController;
import burai.app.project.editor.result.QEFXResultEditor;
import burai.app.project.viewer.result.movie.QEFXMovieViewer;
import burai.project.Project;

public class QEFXMovieEditor extends QEFXResultEditor<QEFXMovieEditorController> {

    public QEFXMovieEditor(QEFXProjectController projectController, Project project, QEFXMovieViewer viewer) throws IOException {
        super("QEFXMovieEditor.fxml",
                new QEFXMovieEditorController(projectController, viewer == null ? null : viewer.getController(), project));
    }

}
