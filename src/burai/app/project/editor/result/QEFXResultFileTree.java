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

package burai.app.project.editor.result;

import java.io.IOException;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import burai.app.QEFXAppComponent;
import burai.app.project.QEFXProjectController;
import burai.app.project.viewer.result.QEFXResultExplorer;
import burai.com.keys.PriorKeyEvent;
import burai.project.Project;

public class QEFXResultFileTree extends QEFXAppComponent<QEFXResultFileTreeController> {

    public QEFXResultFileTree(QEFXProjectController projectController, Project project) throws IOException {
        super("QEFXResultFileTree.fxml", new QEFXResultFileTreeController(projectController, project));

        if (this.node != null) {
            this.node.setOnMouseReleased(event -> this.node.requestFocus());
            this.setupKey(this.node);
        }
    }

    public void setResultExplorer(QEFXResultExplorer explorer) {
        this.controller.setResultExplorer(explorer);
    }

    public void reload() {
        this.controller.reload();
    }

    private void setupKey(Node node) {
        if (node == null) {
            return;
        }

        node.setOnKeyPressed(event -> {
            if (event == null) {
                return;
            }

            if (PriorKeyEvent.isPriorKeyEvent(event)) {
                return;
            }

            if (KeyCode.F5.equals(event.getCode())) {
                // F5
                this.controller.reloadAll();
            }
        });
    }
}
