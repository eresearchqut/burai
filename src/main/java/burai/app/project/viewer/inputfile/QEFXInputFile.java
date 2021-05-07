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

package burai.app.project.viewer.inputfile;

import java.io.IOException;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import burai.app.QEFXAppComponent;
import burai.app.project.QEFXProjectController;
import burai.project.Project;

public class QEFXInputFile extends QEFXAppComponent<QEFXInputFileController> {

    public QEFXInputFile(QEFXProjectController projectController, Project project) throws IOException {
        super("QEFXInputFile.fxml", new QEFXInputFileController(projectController, project));

        if (this.node != null) {
            this.setupKeys(this.node);
        }
    }

    private void setupKeys(Node node) {
        if (node == null) {
            return;
        }

        node.setOnKeyPressed(event -> {
            if (event == null) {
                return;
            }

            if (event.isShortcutDown() && KeyCode.W.equals(event.getCode())) {
                // Shortcut + W
                this.controller.close();
                event.consume();
            }
        });
    }
}
