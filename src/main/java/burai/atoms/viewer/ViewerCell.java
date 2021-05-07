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

import burai.atoms.model.Cell;
import burai.atoms.visible.VisibleCell;
import burai.com.math.Lattice;
import burai.com.math.Matrix3D;
import javafx.geometry.Point3D;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;

public class ViewerCell extends ViewerComponent<VisibleCell> {

    private Cell cell;

    private boolean silent;

    private boolean keepOperation;

    public ViewerCell(AtomsViewer atomsViewer, Cell cell) {
        this(atomsViewer, cell, false);
    }

    public ViewerCell(AtomsViewer atomsViewer, Cell cell, boolean silent) {
        super(atomsViewer);

        if (cell == null) {
            throw new IllegalArgumentException("cell is null.");
        }

        this.cell = cell;
        this.silent = silent;
        this.keepOperation = false;
    }

    public void initialize(boolean keepOperation) {
        this.keepOperation = keepOperation;
        this.initialize();
        this.keepOperation = false;
    }

    @Override
    public void initialize() {
        double[][] lattice = this.cell.copyLattice();
        double[] center = { 0.5, 0.5, 0.5 };
        double[] latticeCenter = Matrix3D.mult(center, lattice);

        double xMax = Lattice.getXMax(lattice);
        double xMin = Lattice.getXMin(lattice);
        double yMax = Lattice.getYMax(lattice);
        double yMin = Lattice.getYMin(lattice);
        double zMax = Lattice.getZMax(lattice);
        double zMin = Lattice.getZMin(lattice);
        double rangeLattice = Math.max(Math.max(xMax - xMin, yMax - yMin), zMax - zMin);
        if (rangeLattice <= 0.0) {
            rangeLattice = 1.0;
        }

        double width = this.atomsViewer.getSceneWidth();
        double height = this.atomsViewer.getSceneHeight();
        double rangeScene = Math.min(width, height);

        double scaleOld = this.scale;
        double centerXOld = this.centerX;
        double centerYOld = this.centerY;
        double centerZOld = this.centerZ;

        this.scale = 0.6 * rangeScene / rangeLattice;
        this.centerX = 0.5 * width;
        this.centerY = 0.5 * height;
        this.centerZ = 0.0;

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
            this.affine.prependTranslation(-latticeCenter[0], latticeCenter[1], latticeCenter[2]);
        }

        this.affine.prependScale(this.scale, this.scale, this.scale);
        this.affine.prependTranslation(this.centerX, this.centerY, this.centerZ);
    }

    @Override
    protected VisibleCell createNode() {
        return new VisibleCell(this.cell, this.atomsViewer.getDesign(), this.silent);
    }

    public boolean isInCell(double sceneX, double sceneY, double sceneZ) {
        VisibleCell visibleCell = this.getNode();
        if (visibleCell == null) {
            return false;
        }

        Point3D point3d = visibleCell.sceneToLocal(sceneX, sceneY, sceneZ);
        double x = point3d.getX();
        double y = point3d.getY();
        double z = point3d.getZ();

        return visibleCell.getModel().isInCell(x, y, z);
    }
}
