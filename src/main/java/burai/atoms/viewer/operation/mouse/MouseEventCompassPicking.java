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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import burai.atoms.model.Atom;
import burai.atoms.viewer.AtomsViewer;
import burai.atoms.viewer.operation.ViewerEventCompassPicking;
import burai.atoms.viewer.operation.ViewerEventManager;
import burai.atoms.visible.VisibleAtom;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;

public class MouseEventCompassPicking extends ViewerEventCompassPicking<MouseEvent> implements MouseEventKernel {

    private MouseEventProxy proxy;

    private Set<Atom> mobileAtoms;

    public MouseEventCompassPicking(MouseEventHandler handler) {
        super();
        this.proxy = new MouseEventProxy(handler, this);
        this.mobileAtoms = null;
    }

    @Override
    public void perform(ViewerEventManager manager, MouseEvent event) {
        this.proxy.perform(manager, event);
    }

    @Override
    public void performOnMousePressed(MouseEvent event) {
        // NOP
    }

    @Override
    public void performOnMouseDragged(MouseEvent event) {
        if (this.mobileAtoms == null) {
            this.createMobileAtoms();
        }

        this.moveSelectedAtoms();
    }

    @Override
    public void performOnMouseReleased(MouseEvent event) {
        ViewerEventManager manager = this.proxy.getManager();
        if (manager == null) {
            return;
        }

        this.mobileAtoms = null;
        manager.setCompassPicking(false);
        manager.exitCompassMode();
    }

    private void createMobileAtoms() {
        this.mobileAtoms = new HashSet<Atom>();

        ViewerEventManager manager = this.proxy.getManager();
        if (manager == null) {
            return;
        }

        AtomsViewer atomsViewer = manager.getAtomsViewer();
        if (atomsViewer == null) {
            return;
        }

        List<VisibleAtom> visibleAtoms = atomsViewer.getVisibleAtoms();
        for (VisibleAtom visibleAtom : visibleAtoms) {
            if (visibleAtom != null && visibleAtom.isSelected()) {
                Atom atom = visibleAtom.getModel();
                if (atom != null) {
                    atom = atom.getMasterAtom();
                }
                if (atom != null) {
                    this.mobileAtoms.add(atom);
                }
            }
        }
    }

    private void moveSelectedAtoms() {
        ViewerEventManager manager = this.proxy.getManager();
        if (manager == null) {
            return;
        }

        MouseEventHandler handler = this.proxy.getHandler();
        if (handler == null) {
            return;
        }

        AtomsViewer atomsViewer = manager.getAtomsViewer();
        if (atomsViewer == null) {
            return;
        }

        double sceneX1 = handler.getMouseX1();
        double sceneY1 = handler.getMouseY1();
        double sceneZ1 = atomsViewer.getSceneZOnCompass(sceneX1, sceneY1);
        Point3D point1 = atomsViewer.sceneToCell(sceneX1, sceneY1, sceneZ1);
        if (point1 == null) {
            return;
        }
        double x1 = point1.getX();
        double y1 = point1.getY();
        double z1 = point1.getZ();

        double sceneX2 = handler.getMouseX2();
        double sceneY2 = handler.getMouseY2();
        double sceneZ2 = atomsViewer.getSceneZOnCompass(sceneX2, sceneY2);
        Point3D point2 = atomsViewer.sceneToCell(sceneX2, sceneY2, sceneZ2);
        if (point2 == null) {
            return;
        }
        double x2 = point2.getX();
        double y2 = point2.getY();
        double z2 = point2.getZ();

        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;

        for (Atom atom : this.mobileAtoms) {
            atom.moveBy(dx, dy, dz);
        }
    }
}
