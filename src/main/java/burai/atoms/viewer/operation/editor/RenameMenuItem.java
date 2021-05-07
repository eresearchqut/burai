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

import java.util.List;
import java.util.Optional;

import javafx.scene.input.KeyCode;
import burai.atoms.model.Atom;
import burai.atoms.viewer.AtomsViewer;
import burai.atoms.viewer.operation.ViewerEventManager;
import burai.atoms.visible.VisibleAtom;
import burai.com.keys.KeyNames;
import burai.com.periodic.ElementButton;
import burai.com.periodic.PeriodicTable;

public class RenameMenuItem extends EditorMenuItem {

    private static final String ITEM_LABEL = "Rename selected atoms [" + KeyNames.getShortcut(KeyCode.R) + "]";

    public RenameMenuItem(ViewerEventManager manager) {
        super(ITEM_LABEL, manager);
    }

    public RenameMenuItem(EditorMenu editorMenu) {
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

        List<VisibleAtom> visibleAtoms = atomsViewer.getVisibleAtoms();

        int numSelected = 0;
        for (VisibleAtom visibleAtom : visibleAtoms) {
            if (visibleAtom != null && visibleAtom.isSelected()) {
                numSelected++;
            }
        }
        if (numSelected < 1) {
            return;
        }

        PeriodicTable periodicTable = new PeriodicTable();
        Optional<ElementButton> optElementButton = periodicTable.showAndWait();
        if (optElementButton == null || !optElementButton.isPresent()) {
            return;
        }

        ElementButton elementButton = optElementButton.get();
        String elementName = elementButton.getText();
        if (elementName == null || elementName.isEmpty()) {
            return;
        }

        atomsViewer.storeCell();

        for (VisibleAtom visibleAtom : visibleAtoms) {
            if (visibleAtom != null && visibleAtom.isSelected()) {
                Atom atom = visibleAtom.getModel();
                if (atom != null) {
                    atom = atom.getMasterAtom();
                }
                if (atom != null) {
                    atom.setName(elementName);
                }
            }
        }
    }
}
