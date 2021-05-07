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

package burai.app.project.editor.input.geom;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import burai.app.QEFXMainController;
import burai.app.project.editor.QEFXEditorController;

public class QEFXGeomController extends QEFXEditorController {

    @FXML
    private ScrollPane cellPane;

    @FXML
    private ScrollPane elementsPane;

    @FXML
    private ScrollPane atomsPane;

    public QEFXGeomController(QEFXMainController mainController) {
        super(mainController);
    }

    public void setCellPane(Node node) {
        if (node == null) {
            return;
        }

        if (this.cellPane != null) {
            this.cellPane.setContent(node);
        }
    }

    public void setElementsPane(Node node) {
        if (node == null) {
            return;
        }

        if (this.elementsPane != null) {
            this.elementsPane.setContent(node);
        }
    }

    public void setAtomsPane(Node node) {
        if (node == null) {
            return;
        }

        if (this.atomsPane != null) {
            this.atomsPane.setContent(node);
        }
    }
}
