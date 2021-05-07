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

import burai.atoms.viewer.AtomsViewer;
import burai.atoms.viewer.operation.ViewerEventCompass;
import burai.atoms.viewer.operation.ViewerEventManager;
import burai.atoms.visible.VisibleAtom;
import javafx.scene.input.MouseEvent;

public class MouseEventCompass extends ViewerEventCompass<MouseEvent> implements MouseEventKernel {

    private MouseEventProxy proxy;

    public MouseEventCompass(MouseEventHandler handler) {
        super();
        this.proxy = new MouseEventProxy(handler, this);
    }

    @Override
    public void perform(ViewerEventManager manager, MouseEvent event) {
        this.proxy.perform(manager, event);
    }

    @Override
    public void performOnMousePressed(MouseEvent event) {
        if (event == null) {
            return;
        }

        ViewerEventManager manager = this.proxy.getManager();
        if (manager == null) {
            return;
        }

        MouseEventHandler handler = this.proxy.getHandler();
        if (handler == null) {
            return;
        }

        VisibleAtom pickedAtom = handler.getPickedAtom();

        if (pickedAtom != null && pickedAtom == manager.getPrincipleAtom()) {
            AtomsViewer atomsViewer = manager.getAtomsViewer();
            if (atomsViewer != null) {
                atomsViewer.storeCell();
            }

            manager.setCompassPicking(true);

        } else if (event.getClickCount() >= 2) {
            manager.exitCompassMode();
        }
    }

    @Override
    public void performOnMouseDragged(MouseEvent event) {
        if (event == null) {
            return;
        }

        this.rotateCompass();
    }

    @Override
    public void performOnMouseReleased(MouseEvent event) {
        // NOP
    }

    private void rotateCompass() {
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
            manager.getAtomsViewer().appendCompassRotation(rho, dy, -dx, 0.0);
        }
    }
}
