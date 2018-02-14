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

package burai.atoms.viewer.operation;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import burai.atoms.viewer.AtomsViewer;
import burai.atoms.viewer.operation.editor.EditorMenu;
import burai.atoms.viewer.operation.key.KeyPressedHandler;
import burai.atoms.viewer.operation.mouse.MouseEventHandler;
import burai.atoms.viewer.operation.scroll.ScrollHandler;
import burai.atoms.visible.VisibleAtom;

public class ViewerEventManager {

    private AtomsViewer atomsViewer;

    private boolean principleSelected;
    private VisibleAtom principleAtom;

    private boolean compassPicking;

    private EditorMenu editorMenu;

    private boolean regularScope;
    private Rectangle scopeRectangle;

    private KeyPressedHandler keyPressedHandler;
    private MouseEventHandler mouseEventHandler;
    private ScrollHandler scrollHandler;

    public ViewerEventManager(AtomsViewer atomsViewer, boolean silent) {
        if (atomsViewer == null) {
            throw new IllegalArgumentException("atomsViewer is null.");
        }

        this.atomsViewer = atomsViewer;

        this.principleSelected = false;
        this.principleAtom = null;

        this.compassPicking = false;

        this.editorMenu = null;

        this.regularScope = false;
        this.scopeRectangle = null;

        this.createHandlers(silent);
    }

    private void createHandlers(boolean silent) {
        this.keyPressedHandler = new KeyPressedHandler(this, silent);
        this.mouseEventHandler = new MouseEventHandler(this, silent);
        this.scrollHandler = new ScrollHandler(this, silent);
    }

    public AtomsViewer getAtomsViewer() {
        return this.atomsViewer;
    }

    public void exitCompassMode() {
        this.atomsViewer.setCompassMode(null);
        this.setPrincipleAtom(null);
    }

    public VisibleAtom getPrincipleAtom() {
        return this.principleAtom;
    }

    public void clearPrincipleAtom() {
        if (this.atomsViewer.isCompassMode()) {
            return;
        }

        this.principleSelected = false;
        this.principleAtom = null;
    }

    public void setPrincipleAtom(VisibleAtom principleAtom) {
        if (this.atomsViewer.isCompassMode()) {
            return;
        }

        if (principleAtom == null && this.principleAtom != null) {
            this.principleAtom.setSelected(this.principleSelected);
        }

        this.principleAtom = principleAtom;
        if (this.principleAtom != null) {
            this.principleSelected = this.principleAtom.isSelected();
            this.principleAtom.setSelected(true);
        } else {
            this.principleSelected = false;
        }
    }

    public boolean isCompassPicking() {
        return this.compassPicking;
    }

    public void setCompassPicking(boolean compassPicking) {
        if (!this.atomsViewer.isCompassMode()) {
            return;
        }

        this.compassPicking = compassPicking;
    }

    public boolean isEditorMenuBusy() {
        return this.editorMenu != null;
    }

    public EditorMenu getEditorMenu() {
        if (this.editorMenu != null) {
            return this.editorMenu;
        }

        this.editorMenu = new EditorMenu(this);

        return this.editorMenu;
    }

    public void removeEditorMenu() {
        if (this.editorMenu == null) {
            return;
        }

        this.editorMenu = null;
    }

    public boolean isRegularScope() {
        return this.regularScope;
    }

    public boolean isScopeRectangleBusy() {
        return this.scopeRectangle != null;
    }

    public Rectangle getScopeRectangle(boolean regularScope) {
        if (this.scopeRectangle != null) {
            return this.scopeRectangle;
        }

        this.regularScope = regularScope;

        return this.getScopeRectangle();
    }

    public Rectangle getScopeRectangle() {
        if (this.scopeRectangle != null) {
            return this.scopeRectangle;
        }

        Rectangle rectangle = new Rectangle();
        rectangle.setFill(Color.rgb(0, 0, 127, 0.1));
        double width = this.atomsViewer.getSceneWidth();
        double height = this.atomsViewer.getSceneHeight();
        rectangle.setTranslateZ(-0.5 * Math.min(width, height));

        if (!this.atomsViewer.hasChild(rectangle)) {
            if (this.atomsViewer.addChild(rectangle)) {
                this.scopeRectangle = rectangle;
            }
        }

        return this.scopeRectangle;
    }

    public void removeScopeRectangle() {
        if (this.scopeRectangle == null) {
            return;
        }

        this.regularScope = false;

        if (this.atomsViewer.hasChild(this.scopeRectangle)) {
            if (this.atomsViewer.removeChild(this.scopeRectangle)) {
                this.scopeRectangle = null;
            }
        }
    }

    public MouseEventHandler getMouseEventHandler() {
        return this.mouseEventHandler;
    }

    public EventHandler<KeyEvent> getKeyPressedHandler() {
        return (EventHandler<KeyEvent>) this.keyPressedHandler;
    }

    public EventHandler<MouseEvent> getMousePressedHandler() {
        return (EventHandler<MouseEvent>) this.mouseEventHandler;
    }

    public EventHandler<MouseEvent> getMouseDraggedHandler() {
        return (EventHandler<MouseEvent>) this.mouseEventHandler;
    }

    public EventHandler<MouseEvent> getMouseReleasedHandler() {
        return (EventHandler<MouseEvent>) this.mouseEventHandler;
    }

    public EventHandler<ScrollEvent> getScrollHandler() {
        return (EventHandler<ScrollEvent>) this.scrollHandler;
    }
}
