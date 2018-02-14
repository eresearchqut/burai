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

package burai.app.project.viewer.result.graph.tools;

import java.io.IOException;

import burai.app.QEFXAppComponent;
import burai.app.project.QEFXProjectController;
import javafx.scene.Node;

public class QEFXGraphNote extends QEFXAppComponent<QEFXGraphNoteController> {

    public QEFXGraphNote(QEFXProjectController projectController, Node content, boolean initMaximized) throws IOException {
        super("QEFXGraphNote.fxml", new QEFXGraphNoteController(projectController, content, initMaximized));
    }

    public void setOnNoteMaximized(NoteMaximized onNoteMaximized) {
        if (this.controller != null) {
            this.controller.setOnNoteMaximized(onNoteMaximized);
        }
    }

    public void minimize() {
        if (this.controller != null) {
            this.controller.minimize();
        }
    }

    public void maximize() {
        if (this.controller != null) {
            this.controller.maximize();
        }
    }

}
