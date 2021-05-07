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

package burai.app.project.menu;

import javafx.scene.Group;

public class QEFXMenuItem<K> extends Group {

    protected K key;

    private QEFXMenu<K> parent;

    protected boolean selected;

    private MenuItemSelected<K> onMenuItemSelected;

    public QEFXMenuItem(K key) {
        this.key = key;
        this.parent = null;
        this.selected = false;
        this.onMenuItemSelected = null;

        this.setupHovering();
    }

    public void setParent(QEFXMenu<K> parent) {
        this.parent = parent;
    }

    protected void setOnMenuItemSelected(MenuItemSelected<K> onMenuItemSelected) {
        this.onMenuItemSelected = onMenuItemSelected;
    }

    private void setupHovering() {
        this.hoverProperty().addListener(o -> {
            this.selected = this.isHover();

            if (this.onMenuItemSelected != null) {
                this.onMenuItemSelected.onMenuItemSelected(this.key);
            }

            if (this.parent != null && this.selected) {
                this.parent.setSelectedKey(this.key);
            }
        });
    }

    @Override
    public String toString() {
        return this.key == null ? super.toString() : this.key.toString();
    }

    @Override
    public int hashCode() {
        return this.key == null ? super.hashCode() : this.key.hashCode();
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

        @SuppressWarnings("unchecked")
        QEFXMenuItem<K> other = (QEFXMenuItem<K>) obj;
        if (this.key == null) {
            if (other.key != null) {
                return false;
            }
        } else if (!this.key.equals(other.key)) {
            return false;
        }

        return true;
    }
}
