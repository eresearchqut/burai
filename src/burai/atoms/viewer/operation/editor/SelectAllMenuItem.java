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

import javafx.scene.input.KeyCode;
import burai.atoms.viewer.AtomsViewer;
import burai.atoms.viewer.operation.ViewerEventManager;
import burai.atoms.visible.VisibleAtom;
import burai.com.keys.KeyNames;

public class SelectAllMenuItem extends EditorMenuItem {

    private static final String ITEM_LABEL = "Select all atoms [" + KeyNames.getShortcut(KeyCode.A) + "]";

    public SelectAllMenuItem(ViewerEventManager manager) {
        super(ITEM_LABEL, manager);
    }

    public SelectAllMenuItem(EditorMenu editorMenu) {
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
        for (VisibleAtom visibleAtom : visibleAtoms) {
            if (visibleAtom != null) {
                visibleAtom.setSelected(true);
            }
        }

        this.manager.clearPrincipleAtom();
    }
}
