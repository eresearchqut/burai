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

package burai.atoms.viewer.operation.mouse;

import javafx.scene.input.MouseEvent;
import burai.atoms.viewer.operation.ViewerEventManager;
import burai.atoms.viewer.operation.ViewerEventRegular;
import burai.atoms.viewer.operation.editor.EditorMenu;
import burai.atoms.visible.VisibleAtom;

public class MouseEventRegular extends ViewerEventRegular<MouseEvent> implements MouseEventKernel {

    private boolean silent;

    private MouseEventProxy proxy;

    public MouseEventRegular(MouseEventHandler handler, boolean silent) {
        super();
        this.silent = silent;
        this.proxy = new MouseEventProxy(handler, this);
    }

    @Override
    public void perform(ViewerEventManager manager, MouseEvent event) {
        this.proxy.perform(manager, event);
    }

    @Override
    public void performOnMousePressed(MouseEvent event) {
        if (this.silent) {
            return;
        }

        if (event == null) {
            return;
        }

        if (event.isShortcutDown()) {
            this.startScoping(event);

        } else if (event.isSecondaryButtonDown()) {
            this.showEditorMenu(event);

        } else if (event.getClickCount() >= 2) {
            MouseEventHandler handler = this.proxy.getHandler();
            VisibleAtom visibleAtom = null;
            if (handler != null) {
                visibleAtom = handler.getPickedAtom();
            }

            if (visibleAtom != null) {
                visibleAtom.setSelected(!visibleAtom.isSelected());
            } else {
                this.startScoping(event);
            }
        }
    }

    @Override
    public void performOnMouseDragged(MouseEvent event) {
        if (event == null) {
            return;
        }

        if (event.isAltDown()) {
            this.scaleCell();

        } else if (event.isShiftDown()) {
            this.translateCell();

        } else if (event.isMiddleButtonDown()) {
            this.translateCell();

        } else {
            this.rotateCell();
        }
    }

    @Override
    public void performOnMouseReleased(MouseEvent event) {
        // NOP
    }

    private void showEditorMenu(MouseEvent event) {
        ViewerEventManager manager = this.proxy.getManager();
        if (manager == null) {
            return;
        }

        MouseEventHandler handler = this.proxy.getHandler();
        if (handler == null) {
            return;
        }

        manager.setPrincipleAtom(handler.getPickedAtom());

        manager.removeEditorMenu();
        EditorMenu editorMenu = manager.getEditorMenu();
        editorMenu.show(event);
    }

    private void startScoping(MouseEvent event) {
        ViewerEventManager manager = this.proxy.getManager();
        if (manager == null) {
            return;
        }

        manager.removeScopeRectangle();

        if (event.isPrimaryButtonDown()) {
            manager.getScopeRectangle(true);
        } else {
            manager.getScopeRectangle(false);
        }
    }

    private void scaleCell() {
        ViewerEventManager manager = this.proxy.getManager();
        if (manager == null) {
            return;
        }

        MouseEventHandler handler = this.proxy.getHandler();
        if (handler == null) {
            return;
        }

        double y1 = handler.getMouseY1();
        double y2 = handler.getMouseY2();
        double dy = y2 - y1;

        if (dy != 0.0) {
            double eta = 1.0 + Math.tanh(MOUSE_SCALE_SPEED * dy);
            manager.getAtomsViewer().appendCellScale(eta);
        }
    }

    private void rotateCell() {
        ViewerEventManager manager = this.proxy.getManager();
        if (manager == null) {
            return;
        }

        MouseEventHandler handler = this.proxy.getHandler();
        if (handler == null) {
            return;
        }

        double x1 = handler.getMouseX1();
        double x2 = handler.getMouseX2();
        double y1 = handler.getMouseY1();
        double y2 = handler.getMouseY2();
        double dx = x2 - x1;
        double dy = y2 - y1;

        double rr = dx * dx + dy * dy;
        if (rr > 0.0) {
            double rho = MOUSE_ROTATE_SPEED * Math.sqrt(rr);
            manager.getAtomsViewer().appendCellRotation(rho, dy, -dx, 0.0);
        }
    }

    private void translateCell() {
        ViewerEventManager manager = this.proxy.getManager();
        if (manager == null) {
            return;
        }

        MouseEventHandler handler = this.proxy.getHandler();
        if (handler == null) {
            return;
        }

        double x1 = handler.getMouseX1();
        double x2 = handler.getMouseX2();
        double y1 = handler.getMouseY1();
        double y2 = handler.getMouseY2();
        double dx = x2 - x1;
        double dy = y2 - y1;

        double xi = MOUSE_TRANS_SPEED * dx;
        double eta = MOUSE_TRANS_SPEED * dy;
        manager.getAtomsViewer().appendCellTranslation(xi, eta, 0.0);
    }
}
