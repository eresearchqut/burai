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

package burai.app.project.viewer.result.log;

import java.io.File;
import java.io.IOException;

import burai.app.project.QEFXProjectController;
import burai.app.project.editor.result.log.QEFXLogEditor;
import burai.app.project.viewer.result.QEFXResultButton;

public abstract class QEFXLogButton extends QEFXResultButton<QEFXLogViewer, QEFXLogEditor> {

    private File file;

    protected QEFXLogButton(QEFXProjectController projectController, String title, String subTitle, File file) {
        super(projectController, title, subTitle);

        if (file == null) {
            throw new IllegalArgumentException("file is null.");
        }

        this.file = file;
    }

    @Override
    protected QEFXLogViewer createResultViewer() throws IOException {
        if (this.projectController == null) {
            return null;
        }

        return new QEFXLogViewer(this.projectController, this.file);
    }

    @Override
    protected QEFXLogEditor createResultEditor(QEFXLogViewer resultViewer) throws IOException {
        if (resultViewer == null) {
            return null;
        }

        if (this.projectController == null) {
            return null;
        }

        return new QEFXLogEditor(this.projectController, resultViewer);
    }
}
