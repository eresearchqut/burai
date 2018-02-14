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

package burai.atoms.vlight;

import burai.atoms.model.Cell;
import burai.atoms.visible.VisibleCell;
import burai.com.math.Lattice;
import burai.com.math.Matrix3D;
import javafx.geometry.Point3D;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;

public class VLightCell extends VLightComponent<VisibleCell> {

    private Cell cell;

    public VLightCell(AtomsVLight atomsVLight, Cell cell) {
        super(atomsVLight);

        if (cell == null) {
            throw new IllegalArgumentException("cell is null.");
        }

        this.cell = cell;
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

        double size = this.atomsViewer.getSize();
        double rangeScene = size;

        this.scale = 0.42 * rangeScene / rangeLattice;
        this.centerX = 0.5 * size;
        this.centerY = 0.5 * size;
        this.centerZ = 0.0;

        if (this.affine == null) {
            this.affine = new Affine();
        }

        this.affine.setToIdentity();
        this.affine.prependRotation(180.0, Point3D.ZERO, Rotate.Y_AXIS);
        this.affine.prependRotation(180.0, Point3D.ZERO, Rotate.Z_AXIS);
        this.affine.prependTranslation(-latticeCenter[0], latticeCenter[1], latticeCenter[2]);
        this.affine.prependScale(this.scale, this.scale, this.scale);
        this.affine.prependTranslation(this.centerX, this.centerY, this.centerZ);
    }

    @Override
    protected VisibleCell createNode() {
        return new VisibleCell(this.cell, null, true, true);
    }

    public void detachFromCell() {
        this.getNode().setToBeFlushed(true);
        this.cell.flushListeners();
    }
}
