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

package burai.atoms.viewer.operation.editor;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import burai.app.QEFXMain;
import burai.atoms.viewer.AtomsViewer;
import burai.atoms.viewer.operation.ViewerEventManager;
import burai.atoms.viewer.operation.mouse.MouseEventHandler;
import burai.com.periodic.ElementButton;
import burai.com.periodic.PeriodicTable;

public class PutMenuItem extends EditorMenuItem {

    private static final String ITEM_LABEL = "Put an atom";

    public PutMenuItem(ViewerEventManager manager) {
        super(ITEM_LABEL, manager);
    }

    public PutMenuItem(EditorMenu editorMenu) {
        super(ITEM_LABEL, editorMenu);
    }

    @Override
    protected void editAtoms() {
        if (this.manager == null) {
            return;
        }

        AtomsViewer atomsViewer = this.manager.getAtomsViewer();
        if (atomsViewer == null) {
            return;
        }

        MouseEventHandler hander = this.manager.getMouseEventHandler();
        if (hander == null) {
            return;
        }

        double sceneX = hander.getMouseX0();
        double sceneY = hander.getMouseY0();
        double sceneZ = 0.0;

        if (!atomsViewer.isInCell(sceneX, sceneY, sceneZ)) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            QEFXMain.initializeDialogOwner(alert);
            String message = "";
            message = message + "Specified coordinate is out of the cell." + System.lineSeparator();
            message = message + "Putted atom will be packed into the cell.";
            alert.setHeaderText(message);
            Optional<ButtonType> optButtonType = alert.showAndWait();
            if (optButtonType == null || !optButtonType.isPresent()) {
                return;
            }
            if (optButtonType.get() != ButtonType.OK) {
                return;
            }
        }

        PeriodicTable periodicTable = new PeriodicTable();
        Optional<ElementButton> optElementButton = periodicTable.showAndWait();
        if (optElementButton == null || !optElementButton.isPresent()) {
            return;
        }

        atomsViewer.storeCell();

        ElementButton elementButton = optElementButton.get();
        String elementName = elementButton.getText();
        atomsViewer.putAtom(elementName, sceneX, sceneY, sceneZ);
    }
}
