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

package burai.app.project.editor.modeler.slabmodel;

import java.io.IOException;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import burai.app.QEFXAppComponent;
import burai.app.project.QEFXProjectController;
import burai.app.project.viewer.modeler.slabmodel.SlabModel;
import burai.app.project.viewer.modeler.slabmodel.SlabModeler;
import burai.com.keys.PriorKeyEvent;

public class QEFXSlabEditor extends QEFXAppComponent<QEFXSlabEditorController> {

    public QEFXSlabEditor(QEFXProjectController projectController, SlabModeler modeler) throws IOException {
        super("QEFXSlabEditor.fxml", new QEFXSlabEditorController(projectController, modeler));

        if (this.node != null) {
            this.node.setOnMouseReleased(event -> this.node.requestFocus());
        }

        if (this.node != null) {
            this.setupKeys(this.node, modeler);
        }
    }

    private void setupKeys(Node node, SlabModeler modeler) {
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

            if (event.isShortcutDown() && KeyCode.C.equals(event.getCode())) {
                // Shortcut + C
                if (modeler != null) {
                    modeler.center();
                }
            }
        });
    }

    public void setSlabModels(SlabModel[] slabModels) {
        if (this.controller != null) {
            this.controller.setSlabModels(slabModels);
        }
    }

    public void cleanSlabModels() {
        if (this.controller != null) {
            this.controller.cleanSlabModels();
        }
    }
}
