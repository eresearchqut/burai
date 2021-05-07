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

import java.io.IOException;

import burai.app.QEFXMainController;
import burai.app.project.editor.QEFXEditorComponent;
import burai.atoms.model.Cell;
import burai.input.QEInput;

public class QEFXGeom extends QEFXEditorComponent<QEFXGeomController> {

    private QEFXCell cellComponent;
    private QEFXElements elementsComponent;
    private QEFXAtoms atomsComponent;

    public QEFXGeom(QEFXMainController mainController, QEInput input, Cell cell) throws IOException {
        super("QEFXGeom.fxml", new QEFXGeomController(mainController));

        if (input == null) {
            throw new IllegalArgumentException("input is null.");
        }

        if (cell == null) {
            throw new IllegalArgumentException("cell is null.");
        }

        this.createComponents(input, cell);
    }

    private void createComponents(QEInput input, Cell cell) throws IOException {
        QEFXMainController mainController = null;
        if (this.controller != null) {
            mainController = this.controller.getMainController();
        }

        this.cellComponent = new QEFXCell(mainController, input, cell);
        this.elementsComponent = new QEFXElements(mainController, input, cell);
        this.atomsComponent = new QEFXAtoms(mainController, input, cell);

        if (this.controller != null) {
            this.controller.setCellPane(this.cellComponent.getNode());
            this.controller.setElementsPane(this.elementsComponent.getNode());
            this.controller.setAtomsPane(this.atomsComponent.getNode());
        }
    }

    @Override
    public void notifyEditorOpened() {
        this.cellComponent.notifyEditorOpened();
        this.elementsComponent.notifyEditorOpened();
        this.atomsComponent.notifyEditorOpened();
    }

}
