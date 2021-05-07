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
import burai.app.project.editor.result.graph.QEFXGraphEditor;
import burai.app.project.viewer.result.QEFXResultButton;

public abstract class QEFXGraphButton<V extends QEFXGraphViewer<?>> extends QEFXResultButton<V, QEFXGraphEditor> {

    private File propertyFile;

    protected QEFXGraphButton(QEFXProjectController projectController, String title, String subTitle) {
        super(projectController, title, subTitle);
        this.propertyFile = null;
    }

    protected void setPropertyFile(File propertyFile) {
        this.propertyFile = propertyFile;
    }

    protected abstract V createGraphViewer() throws IOException;

    @Override
    protected final V createResultViewer() throws IOException {
        V viewer = this.createGraphViewer();
        if (viewer != null) {
            QEFXGraphViewerController controller = viewer.getController();
            if (controller != null) {
                controller.setPropertyFile(this.propertyFile);
            }
        }

        return viewer;
    }

    @Override
    protected final QEFXGraphEditor createResultEditor(V resultViewer) throws IOException {
        if (resultViewer == null) {
            return null;
        }

        if (this.projectController == null) {
            return null;
        }

        return new QEFXGraphEditor(this.projectController, resultViewer);
    }
}
