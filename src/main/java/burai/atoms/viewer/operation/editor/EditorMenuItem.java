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

import burai.atoms.viewer.operation.ViewerEventManager;
import javafx.scene.control.MenuItem;

public abstract class EditorMenuItem extends MenuItem {

    protected EditorMenu editorMenu;

    protected ViewerEventManager manager;

    protected EditorMenuItem(String text, ViewerEventManager manager) {
        this(text, null, manager);
    }

    protected EditorMenuItem(String text, EditorMenu editorMenu) {
        this(text, editorMenu, editorMenu == null ? null : editorMenu.getManager());
    }

    protected EditorMenuItem(String text, EditorMenu editorMenu, ViewerEventManager manager) {
        super(text);

        if (manager == null) {
            throw new IllegalArgumentException("manager is null.");
        }

        this.editorMenu = editorMenu;
        this.manager = manager;

        this.setOnAction(event -> {
            if (this.editorMenu != null) {
                this.editorMenu.setItemInAction(true);
            }

            this.editAtoms();

            this.manager.setPrincipleAtom(null);
            this.manager.removeEditorMenu();

            if (this.editorMenu != null) {
                this.editorMenu.setItemInAction(false);
            }
        });
    }

    protected abstract void editAtoms();

    public void performAction() {
        this.editAtoms();
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }

        EditorMenuItem item = (EditorMenuItem) obj;
        return this.manager == item.manager;
    }
}
