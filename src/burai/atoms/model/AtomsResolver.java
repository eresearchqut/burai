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

package burai.atoms.model;

import burai.atoms.model.event.AtomEvent;
import burai.atoms.model.event.AtomEventListener;
import burai.atoms.model.event.CellEvent;
import burai.atoms.model.event.CellEventListener;
import burai.atoms.model.event.ModelEvent;

public class AtomsResolver implements AtomEventListener, CellEventListener {

    private static final double THR_DENSITY = 0.50;

    private static final double THR_NORM_LATTICE = 1.00;

    private static final double DELTA_ON_LATTICE = 0.25;

    private Cell cell;

    boolean auto;

    protected AtomsResolver(Cell cell) {
        if (cell == null) {
            throw new IllegalArgumentException("cell is null.");
        }

        this.cell = cell;
        this.cell.addListenerFirst(this);

        Atom[] atoms = this.cell.listAtoms();
        if (atoms != null) {
            for (Atom atom : atoms) {
                if (atom != null) {
                    atom.addListenerFirst(this);
                }
            }
        }

        this.auto = true;
    }

    protected void setAuto(boolean auto) {
        this.auto = auto;
    }

    protected boolean isAuto() {
        return this.auto;
    }

    protected void resolve() {
        Atom[] atoms = this.cell.listAtoms();
        if (atoms == null || atoms.length < 1) {
            return;
        }

        for (Atom atom : atoms) {
            double x = atom.getX();
            double y = atom.getY();
            double z = atom.getZ();
            atom.moveTo(x, y, z);
        }
    }

    protected void packAtomIntoCell(Atom atom) {
        boolean orgAuto = this.auto;
        this.auto = false;

        double[] position = null;

        double x = atom.getX();
        double y = atom.getY();
        double z = atom.getZ();

        position = this.cell.convertToLatticePosition(x, y, z);
        double a = position[0];
        double b = position[1];
        double c = position[2];

        boolean anyShift = false;
        while (a < 0.0) {
            a += 1.0;
            anyShift = true;
        }
        while (a >= 1.0) {
            a -= 1.0;
            anyShift = true;
        }
        while (b < 0.0) {
            b += 1.0;
            anyShift = true;
        }
        while (b >= 1.0) {
            b -= 1.0;
            anyShift = true;
        }
        while (c < 0.0) {
            c += 1.0;
            anyShift = true;
        }
        while (c >= 1.0) {
            c -= 1.0;
            anyShift = true;
        }

        if (anyShift) {
            position = this.cell.convertToCartesianPosition(a, b, c);
            x = position[0];
            y = position[1];
            z = position[2];
            atom.moveTo(x, y, z);
        }

        this.auto = orgAuto;
    }

    private Atom[] listAtomsOnCell(Atom atom) {
        int natom = this.cell.numAtoms();
        if (natom < 1) {
            return null;
        }

        double volume = this.cell.getVolume();
        if (volume <= 0.0) {
            return null;
        }

        double density = ((double) natom) / volume;
        if (density > THR_DENSITY) {
            return null;
        }

        double[] position = null;
        double[] normLattice = this.cell.getNormLattice();

        String name = atom.getName();
        int atomNum = atom.getAtomNum();
        double radius = atom.getRadius();
        double x = atom.getX();
        double y = atom.getY();
        double z = atom.getZ();

        position = this.cell.convertToLatticePosition(x, y, z);
        double a = position[0];
        double b = position[1];
        double c = position[2];

        double[] deltaA = null;
        if (normLattice[0] > THR_NORM_LATTICE) {
            if (normLattice[0] * Math.abs(a) < DELTA_ON_LATTICE) {
                deltaA = new double[] { 0.0, 1.0 };
            } else if (normLattice[0] * Math.abs(a - 1.0) < DELTA_ON_LATTICE) {
                deltaA = new double[] { 0.0, -1.0 };
            } else {
                deltaA = new double[] { 0.0 };
            }
        }

        double[] deltaB = null;
        if (normLattice[1] > THR_NORM_LATTICE) {
            if (normLattice[1] * Math.abs(b) < DELTA_ON_LATTICE) {
                deltaB = new double[] { 0.0, 1.0 };
            } else if (normLattice[1] * Math.abs(b - 1.0) < DELTA_ON_LATTICE) {
                deltaB = new double[] { 0.0, -1.0 };
            } else {
                deltaB = new double[] { 0.0 };
            }
        }

        double[] deltaC = null;
        if (normLattice[2] > THR_NORM_LATTICE) {
            if (normLattice[2] * Math.abs(c) < DELTA_ON_LATTICE) {
                deltaC = new double[] { 0.0, 1.0 };
            } else if (normLattice[2] * Math.abs(c - 1.0) < DELTA_ON_LATTICE) {
                deltaC = new double[] { 0.0, -1.0 };
            } else {
                deltaC = new double[] { 0.0 };
            }
        }

        if (deltaA == null || deltaB == null || deltaC == null) {
            return null;
        }

        int numArray = deltaA.length * deltaB.length * deltaC.length - 1;
        if (numArray < 1) {
            return null;
        }

        Atom[] atomArray = new Atom[numArray];
        int iAtom = 0;
        for (int ia = 0; ia < deltaA.length; ia++) {
            double a2 = a + deltaA[ia];
            for (int ib = 0; ib < deltaB.length; ib++) {
                double b2 = b + deltaB[ib];
                for (int ic = 0; ic < deltaC.length; ic++) {
                    double c2 = c + deltaC[ic];
                    if ((ia + ib + ic) == 0) {
                        continue;
                    }
                    position = this.cell.convertToCartesianPosition(a2, b2, c2);
                    x = position[0];
                    y = position[1];
                    z = position[2];
                    atomArray[iAtom] = new Atom(name, atomNum, radius, x, y, z);
                    iAtom++;
                }
            }
        }

        return atomArray;
    }

    @Override
    public boolean isToBeFlushed() {
        return false;
    }

    @Override
    public void onModelDisplayed(ModelEvent event) {
        // NOP
    }

    @Override
    public void onModelNotDisplayed(ModelEvent event) {
        // NOP
    }

    @Override
    public void onLatticeMoved(CellEvent event) {
        if (event == null) {
            return;
        }

        if (this.cell != event.getSource()) {
            return;
        }

        if (!this.auto) {
            return;
        }

        this.resolve();
    }

    @Override
    public void onAtomAdded(CellEvent event) {
        if (event == null) {
            return;
        }

        if (this.cell != event.getSource()) {
            return;
        }

        Atom atom = event.getAtom();
        if (atom == null) {
            return;
        }

        atom.addListenerFirst(this);

        if (!this.auto) {
            return;
        }

        if (atom.isSlaveAtom()) {
            return;
        }

        Atom[] subAtoms = this.listAtomsOnCell(atom);
        if (subAtoms != null && subAtoms.length > 0) {
            for (Atom subAtom : subAtoms) {
                subAtom.setMasterAtom(atom);
                this.cell.addAtom(subAtom);
            }
        }
    }

    @Override
    public void onAtomRemoved(CellEvent event) {
        if (event == null) {
            return;
        }

        if (this.cell != event.getSource()) {
            return;
        }

        Atom atom = event.getAtom();
        if (atom == null) {
            return;
        }

        if (!this.auto) {
            return;
        }

        Atom[] slaveAtoms = atom.listSlaveAtoms();
        if (slaveAtoms != null && slaveAtoms.length > 0) {
            for (Atom slaveAtom : slaveAtoms) {
                this.cell.removeAtom(slaveAtom);
            }
        }
    }

    @Override
    public void onBondAdded(CellEvent event) {
        // NOP
    }

    @Override
    public void onBondRemoved(CellEvent event) {
        // NOP
    }

    @Override
    public void onAtomRenamed(AtomEvent event) {
        // NOP
    }

    @Override
    public void onAtomMoved(AtomEvent event) {
        if (event == null) {
            return;
        }

        if (!this.auto) {
            return;
        }

        Object obj = event.getSource();
        if (obj == null || !(obj instanceof Atom)) {
            return;
        }

        Atom atom = (Atom) obj;
        if (atom.isSlaveAtom()) {
            return;
        }

        Atom[] slaveAtoms = atom.listSlaveAtoms();
        if (slaveAtoms != null && slaveAtoms.length > 0) {
            for (Atom slaveAtom : slaveAtoms) {
                this.cell.removeAtom(slaveAtom);
            }
        }

        this.packAtomIntoCell(atom);

        Atom[] subAtoms = this.listAtomsOnCell(atom);
        if (subAtoms != null && subAtoms.length > 0) {
            double deltaX = event.getDeltaX();
            double deltaY = event.getDeltaY();
            double deltaZ = event.getDeltaZ();
            for (Atom subAtom : subAtoms) {
                subAtom.moveBy(-deltaX, -deltaY, -deltaZ);
                subAtom.setMasterAtom(atom);
                this.cell.addAtom(subAtom);
            }
        }
    }
}
