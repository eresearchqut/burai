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

package burai.atoms.viewer;

import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import burai.atoms.visible.CompassPlane;
import burai.atoms.visible.VisibleAtom;

public class ViewerCompass extends ViewerComponent<CompassPlane> {

    private static final double Z_MIN = 1.0e-6;

    private ViewerCell parent;

    private VisibleAtom targetAtom;

    private boolean keepOperation;

    public ViewerCompass(ViewerCell parent) {
        super(parent == null ? null : parent.atomsViewer);

        if (parent == null) {
            throw new IllegalArgumentException("parent is null.");
        }

        this.parent = parent;
        this.targetAtom = null;
        this.keepOperation = false;
    }

    public void setTargetAtom(VisibleAtom targetAtom) {
        this.targetAtom = targetAtom;
    }

    public void initialize(boolean keepOperation) {
        this.keepOperation = keepOperation;
        this.initialize();
        this.keepOperation = false;
    }

    @Override
    public void initialize() {
        Node parentNode = this.parent.getNode();

        double scaleOld = this.scale;
        double centerXOld = this.centerX;
        double centerYOld = this.centerY;
        double centerZOld = this.centerZ;

        double width = this.atomsViewer.getSceneWidth();
        double height = this.atomsViewer.getSceneHeight();
        double rangeScene = Math.min(width, height);

        double rangeCompass = CompassPlane.getHeight();
        if (rangeCompass <= 0.0) {
            rangeCompass = 1.0;
        }

        this.scale = 0.6 * rangeScene / rangeCompass;
        this.centerX = this.parent.centerX;
        this.centerY = this.parent.centerY;
        this.centerZ = this.parent.centerZ;

        if (this.targetAtom != null && parentNode != null) {
            double x = this.targetAtom.getX();
            double y = this.targetAtom.getY();
            double z = this.targetAtom.getZ();
            Point3D point3d = parentNode.localToScene(x, y, z);
            this.centerX = point3d.getX();
            this.centerY = point3d.getY();
            this.centerZ = point3d.getZ();
        }

        if (this.affine == null) {
            this.affine = new Affine();
        }

        if (this.keepOperation) {
            this.affine.prependTranslation(-centerXOld, -centerYOld, -centerZOld);
            this.affine.prependScale(1.0 / scaleOld, 1.0 / scaleOld, 1.0 / scaleOld);
        } else {
            this.affine.setToIdentity();
            this.affine.prependRotation(180.0, Point3D.ZERO, Rotate.Y_AXIS);
            this.affine.prependRotation(180.0, Point3D.ZERO, Rotate.Z_AXIS);
            // bend the plane
            this.affine.prependRotation(120.0, Point3D.ZERO, Rotate.X_AXIS);
            this.affine.prependRotation(30.0, Point3D.ZERO, Rotate.Y_AXIS);
        }

        this.affine.prependScale(this.scale, this.scale, this.scale);
        this.affine.prependTranslation(this.centerX, this.centerY, this.centerZ);
    }

    @Override
    protected CompassPlane createNode() {
        CompassPlane compassPlane = new CompassPlane();
        compassPlane.setVisible(false);

        return compassPlane;
    }

    @Override
    public void appendTranslation(double x, double y, double z) {
        super.appendTranslation(x, y, z);

        this.centerX += x;
        this.centerY += y;
        this.centerZ += z;
    }

    public double getSceneZ(double sceneX, double sceneY) {
        CompassPlane compassPlane = this.getNode();
        if (compassPlane == null) {
            return this.centerZ;
        }

        double sceneZ1 = -5.0;
        double sceneZ2 = +5.0;

        Point3D point1 = compassPlane.sceneToLocal(sceneX, sceneY, sceneZ1);
        if (point1 == null) {
            return this.centerZ;
        }

        Point3D point2 = compassPlane.sceneToLocal(sceneX, sceneY, sceneZ2);
        if (point2 == null) {
            return this.centerZ;
        }

        double z1 = point1.getZ();
        double z2 = point2.getZ();
        if (Math.abs(z2 - z1) < Z_MIN) {
            return this.centerZ;
        }

        double rate = (sceneZ2 - sceneZ1) / (z2 - z1);
        double sceneZ = sceneZ1 - rate * z1;

        return sceneZ;
    }
}
