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
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

public abstract class QEFXMenu<K> extends Group {

    private K selectedKey;

    private MenuShowing onMenuShowing;

    private MenuItemSelected<K> onMenuItemSelected;

    protected QEFXMenu() {
        this.selectedKey = null;
        this.onMenuItemSelected = null;
        this.setupMouseClickedAction();
    }

    public abstract void clearItem();

    public abstract void addItem(K key);

    protected abstract void playShowingAnimation();

    protected void setSelectedKey(K key) {
        this.selectedKey = key;
    }

    public void setOnMenuShowing(MenuShowing onMenuShowing) {
        this.onMenuShowing = onMenuShowing;
    }

    public void setOnMenuItemSelected(MenuItemSelected<K> onMenuItemSelected) {
        this.onMenuItemSelected = onMenuItemSelected;
    }

    public void showMenu() {
        this.playShowingAnimation();

        if (this.onMenuShowing != null) {
            this.onMenuShowing.onMenuShowing();
        }
    }

    private void setupMouseClickedAction() {
        this.setOnMouseClicked(event -> {
            Parent parent = this.getParent();
            if (parent != null && parent instanceof Pane) {
                ((Pane) parent).getChildren().remove(this);
            }

            if (this.onMenuItemSelected != null) {
                this.onMenuItemSelected.onMenuItemSelected(this.selectedKey);
            }
        });
    }
}
