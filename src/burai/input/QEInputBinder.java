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

package burai.input;

import burai.atoms.model.Atom;
import burai.atoms.model.Cell;
import burai.atoms.model.event.AtomEvent;
import burai.atoms.model.event.AtomEventListener;
import burai.atoms.model.event.CellEvent;
import burai.atoms.model.event.CellEventListener;
import burai.atoms.model.event.ModelEvent;
import burai.atoms.model.property.AtomProperty;
import burai.com.math.Lattice;
import burai.com.math.Matrix3D;
import burai.input.card.QEAtomicPositions;
import burai.input.card.QECard;
import burai.input.card.QECellParameters;
import burai.input.namelist.QENamelist;

public class QEInputBinder implements AtomEventListener, CellEventListener {

    private static final double LATTICE_DELTA = 1.0e-6;

    private static final double ATOMPOS_DELTA = 1.0e-6;
    private static final double ATOMPOS_DELTA2 = ATOMPOS_DELTA * ATOMPOS_DELTA;

    private QEGeometryInput input;

    public QEInputBinder(QEGeometryInput input) {
        if (input == null) {
            throw new IllegalArgumentException("input is null.");
        }

        this.input = input;
    }

    public void bindBy(Cell cell) {
        if (cell == null) {
            return;
        }

        cell.addListenerFirst(this);

        Atom[] atoms = cell.listAtoms(true);
        if (atoms != null) {
            for (Atom atom : atoms) {
                if (atom != null) {
                    atom.addListenerFirst(this);
                    atom.addPropertyListener(AtomProperty.FIXED_X, o -> this.onAtomFixedChanged(atom));
                    atom.addPropertyListener(AtomProperty.FIXED_Y, o -> this.onAtomFixedChanged(atom));
                    atom.addPropertyListener(AtomProperty.FIXED_Z, o -> this.onAtomFixedChanged(atom));
                }
            }
        }
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

    private boolean toUpdateLattice(double[][] lattice) {
        if (lattice == null || lattice.length < 3) {
            return false;
        }
        for (int i = 0; i < 3; i++) {
            if (lattice[i] == null || lattice[i].length < 3) {
                return false;
            }
        }

        double[][] inpLattice = this.input.getLattice();
        boolean availInpLattice = true;
        if (inpLattice == null || inpLattice.length < 3) {
            availInpLattice = false;
        }
        if (availInpLattice) {
            for (int i = 0; i < 3; i++) {
                if (inpLattice[i] == null || inpLattice[i].length < 3) {
                    availInpLattice = false;
                }
            }
        }

        if (availInpLattice) {
            double delta = 0.0;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    delta = Math.max(delta, Math.abs(lattice[i][j] - inpLattice[i][j]));
                }
            }
            if (delta < LATTICE_DELTA) {
                return false;
            }
        }

        return true;
    }

    protected void setLattice(double[][] lattice) {
        if (lattice == null || lattice.length < 3) {
            return;
        }
        for (int i = 0; i < 3; i++) {
            if (lattice[i] == null || lattice[i].length < 3) {
                return;
            }
        }

        QENamelist nmlSystem = this.input.getNamelist(QEInput.NAMELIST_SYSTEM);
        if (nmlSystem != null) {
            nmlSystem.removeValue("a");
            nmlSystem.removeValue("b");
            nmlSystem.removeValue("c");
            nmlSystem.removeValue("cosbc");
            nmlSystem.removeValue("cosac");
            nmlSystem.removeValue("cosab");
            nmlSystem.removeValue("celldm(1)");
            nmlSystem.removeValue("celldm(2)");
            nmlSystem.removeValue("celldm(3)");
            nmlSystem.removeValue("celldm(4)");
            nmlSystem.removeValue("celldm(5)");
            nmlSystem.removeValue("celldm(6)");
        }

        int ibrav = Lattice.getBravais(lattice);

        if (ibrav == 0) {
            if (nmlSystem != null) {
                nmlSystem.setValue("ibrav = 0");
            }
        } else {
            if (nmlSystem != null) {
                this.setBravaisParameters(ibrav, lattice, nmlSystem);
            }
        }

        QECard card = this.input.getCard(QECellParameters.CARD_NAME);
        if (card != null && card instanceof QECellParameters) {
            QECellParameters cellParameters = (QECellParameters) card;
            cellParameters.setAngstrom();
            cellParameters.setVector(1, lattice[0]);
            cellParameters.setVector(2, lattice[1]);
            cellParameters.setVector(3, lattice[2]);
        }
    }

    private void setBravaisParameters(int ibrav, double[][] lattice, QENamelist nmlSystem) {
        double[] lattConst = Lattice.getLatticeConstants(ibrav, lattice, true);
        double a = lattConst[0];
        double b = lattConst[1];
        double c = lattConst[2];
        double cosbc = lattConst[3];
        double cosac = lattConst[4];
        double cosab = lattConst[5];

        nmlSystem.setValue("ibrav = " + ibrav);

        switch (ibrav) {
        case 1:
            nmlSystem.setValue("a = " + a);
            break;

        case 2:
            nmlSystem.setValue("a = " + a);
            break;

        case 3:
            nmlSystem.setValue("a = " + a);
            break;

        case -3:
            nmlSystem.setValue("a = " + a);
            break;

        case 4:
            nmlSystem.setValue("a = " + a);
            nmlSystem.setValue("c = " + c);
            break;

        case 5:
            nmlSystem.setValue("a = " + a);
            nmlSystem.setValue("cosab = " + cosab);
            break;

        case -5:
            nmlSystem.setValue("a = " + a);
            nmlSystem.setValue("cosab = " + cosab);
            break;

        case 6:
            nmlSystem.setValue("a = " + a);
            nmlSystem.setValue("c = " + c);
            break;

        case 7:
            nmlSystem.setValue("a = " + a);
            nmlSystem.setValue("c = " + c);
            break;

        case 8:
            nmlSystem.setValue("a = " + a);
            nmlSystem.setValue("b = " + b);
            nmlSystem.setValue("c = " + c);
            break;

        case 9:
            nmlSystem.setValue("a = " + a);
            nmlSystem.setValue("b = " + b);
            nmlSystem.setValue("c = " + c);
            break;

        case -9:
            nmlSystem.setValue("a = " + a);
            nmlSystem.setValue("b = " + b);
            nmlSystem.setValue("c = " + c);
            break;

        case 91:
            nmlSystem.setValue("a = " + a);
            nmlSystem.setValue("b = " + b);
            nmlSystem.setValue("c = " + c);
            break;

        case 10:
            nmlSystem.setValue("a = " + a);
            nmlSystem.setValue("b = " + b);
            nmlSystem.setValue("c = " + c);
            break;

        case 11:
            nmlSystem.setValue("a = " + a);
            nmlSystem.setValue("b = " + b);
            nmlSystem.setValue("c = " + c);
            break;

        case 12:
            nmlSystem.setValue("a = " + a);
            nmlSystem.setValue("b = " + b);
            nmlSystem.setValue("c = " + c);
            nmlSystem.setValue("cosab = " + cosab);
            break;

        case -12:
            nmlSystem.setValue("a = " + a);
            nmlSystem.setValue("b = " + b);
            nmlSystem.setValue("c = " + c);
            nmlSystem.setValue("cosac = " + cosac);
            break;

        case 13:
            nmlSystem.setValue("a = " + a);
            nmlSystem.setValue("b = " + b);
            nmlSystem.setValue("c = " + c);
            nmlSystem.setValue("cosab = " + cosab);
            break;

        case -13:
            nmlSystem.setValue("a = " + a);
            nmlSystem.setValue("b = " + b);
            nmlSystem.setValue("c = " + c);
            nmlSystem.setValue("cosac = " + cosac);
            break;

        case 14:
            nmlSystem.setValue("a = " + a);
            nmlSystem.setValue("b = " + b);
            nmlSystem.setValue("c = " + c);
            nmlSystem.setValue("cosbc = " + cosbc);
            nmlSystem.setValue("cosac = " + cosac);
            nmlSystem.setValue("cosab = " + cosab);
            break;

        default:
            nmlSystem.setValue("a = " + a);
            nmlSystem.setValue("b = " + b);
            nmlSystem.setValue("c = " + c);
            nmlSystem.setValue("cosbc = " + cosbc);
            nmlSystem.setValue("cosac = " + cosac);
            nmlSystem.setValue("cosab = " + cosab);
            break;
        }
    }

    @Override
    public void onLatticeMoved(CellEvent event) {
        if (this.input.isBusyWithActions()) {
            return;
        }

        if (event == null) {
            return;
        }

        Object source = event.getSource();
        if (source == null || !(source instanceof Cell)) {
            return;
        }
        Cell cell = (Cell) source;

        double[][] lattice = event.getLattice();
        if (!this.toUpdateLattice(lattice)) {
            return;
        }

        cell.setProperty(QEGeometryInput.MODEL_BUSY, true);

        this.setLattice(lattice);

        cell.setProperty(QEGeometryInput.MODEL_BUSY, false);
    }

    @Override
    public void onAtomAdded(CellEvent event) {
        if (event == null) {
            return;
        }

        Object source = event.getSource();
        if (source == null || !(source instanceof Cell)) {
            return;
        }
        Cell cell = (Cell) source;

        Atom atom = event.getAtom();
        if (atom == null) {
            return;
        }

        if (atom.isSlaveAtom()) {
            return;
        }

        if (!atom.hasProperty(AtomProperty.FIXED_X)) {
            atom.setProperty(AtomProperty.FIXED_X, false);
        }

        if (!atom.hasProperty(AtomProperty.FIXED_Y)) {
            atom.setProperty(AtomProperty.FIXED_Y, false);
        }

        if (!atom.hasProperty(AtomProperty.FIXED_Z)) {
            atom.setProperty(AtomProperty.FIXED_Z, false);
        }

        if (!atom.hasProperty(AtomProperty.INPUT_INDEX)) {
            int index = 0;
            Atom[] atoms = cell.listAtoms(true);
            for (int i = 0; i < atoms.length; i++) {
                if (atoms[i] != null && atoms[i].hasProperty(AtomProperty.INPUT_INDEX)) {
                    index = Math.max(index, atoms[i].intProperty(AtomProperty.INPUT_INDEX) + 1);
                }
            }
            atom.setProperty(AtomProperty.INPUT_INDEX, index);
        }

        atom.addListenerFirst(this);
        atom.addPropertyListener(AtomProperty.FIXED_X, o -> this.onAtomFixedChanged(atom));
        atom.addPropertyListener(AtomProperty.FIXED_Y, o -> this.onAtomFixedChanged(atom));
        atom.addPropertyListener(AtomProperty.FIXED_Z, o -> this.onAtomFixedChanged(atom));

        if (this.input.isBusyWithActions()) {
            return;
        }

        cell.setProperty(QEGeometryInput.MODEL_BUSY, true);

        QECard card = this.input.getCard(QEAtomicPositions.CARD_NAME);
        if (card != null && card instanceof QEAtomicPositions) {
            QEAtomicPositions atomicPositions = (QEAtomicPositions) card;

            String label = atom.getName();

            double x = atom.getX();
            double y = atom.getY();
            double z = atom.getZ();
            double[][] matrix = this.input.getAngstromInverse();
            double[] position = null;
            if (matrix != null) {
                position = Matrix3D.mult(matrix, new double[] { x, y, z });
            }

            boolean mobileX = !atom.booleanProperty(AtomProperty.FIXED_X);
            boolean mobileY = !atom.booleanProperty(AtomProperty.FIXED_Y);
            boolean mobileZ = !atom.booleanProperty(AtomProperty.FIXED_Z);
            boolean[] mobile = { mobileX, mobileY, mobileZ };

            if (label != null && (!label.trim().isEmpty()) && position != null) {
                atomicPositions.addPosition(label, position, mobile);
            }
        }

        cell.setProperty(QEGeometryInput.MODEL_BUSY, false);
    }

    @Override
    public void onAtomRemoved(CellEvent event) {
        if (this.input.isBusyWithActions()) {
            return;
        }

        if (event == null) {
            return;
        }

        Object source = event.getSource();
        if (source == null || !(source instanceof Cell)) {
            return;
        }
        Cell cell = (Cell) source;

        Atom atom = event.getAtom();
        if (atom == null) {
            return;
        }

        if (atom.isSlaveAtom()) {
            return;
        }

        cell.setProperty(QEGeometryInput.MODEL_BUSY, true);

        int index = atom.intProperty(AtomProperty.INPUT_INDEX);

        QECard card = this.input.getCard(QEAtomicPositions.CARD_NAME);
        if (card != null && card instanceof QEAtomicPositions) {
            QEAtomicPositions atomicPositions = (QEAtomicPositions) card;
            if (0 <= index || index < atomicPositions.numPositions()) {
                atomicPositions.removePosition(index);
            }
        }

        Atom[] atoms = cell.listAtoms(true);
        for (int i = 0; i < atoms.length; i++) {
            if (atoms[i] == null) {
                continue;
            }
            int myIndex = atoms[i].intProperty(AtomProperty.INPUT_INDEX);
            if (myIndex >= index) {
                atoms[i].setProperty(AtomProperty.INPUT_INDEX, myIndex - 1);
            }
        }

        cell.setProperty(QEGeometryInput.MODEL_BUSY, false);
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
        if (this.input.isBusyWithActions()) {
            return;
        }

        if (event == null) {
            return;
        }

        Object source = event.getSource();
        if (source == null || !(source instanceof Atom)) {
            return;
        }
        Atom atom = (Atom) source;

        if (atom.isSlaveAtom()) {
            return;
        }

        String name1 = event.getOldName();
        String name2 = event.getName();
        if (name1 != null && name1.equals(name2)) {
            return;
        }

        atom.setProperty(QEGeometryInput.MODEL_BUSY, true);

        QECard card = this.input.getCard(QEAtomicPositions.CARD_NAME);
        if (card != null && card instanceof QEAtomicPositions) {
            QEAtomicPositions atomicPositions = (QEAtomicPositions) card;
            int index = atom.intProperty(AtomProperty.INPUT_INDEX);
            if (0 <= index || index < atomicPositions.numPositions()) {
                atomicPositions.setLabel(index, atom.getName());
            }
        }

        atom.setProperty(QEGeometryInput.MODEL_BUSY, false);
    }

    @Override
    public void onAtomMoved(AtomEvent event) {
        if (this.input.isBusyWithActions()) {
            return;
        }

        if (event == null) {
            return;
        }

        Object source = event.getSource();
        if (source == null || !(source instanceof Atom)) {
            return;
        }
        Atom atom = (Atom) source;

        if (atom.isSlaveAtom()) {
            return;
        }

        double dx = event.getDeltaX();
        double dy = event.getDeltaY();
        double dz = event.getDeltaZ();
        double rr = dx * dx + dy * dy + dz * dz;
        if (rr < ATOMPOS_DELTA2) {
            return;
        }

        atom.setProperty(QEGeometryInput.MODEL_BUSY, true);

        QECard card = this.input.getCard(QEAtomicPositions.CARD_NAME);
        if (card != null && card instanceof QEAtomicPositions) {
            QEAtomicPositions atomicPositions = (QEAtomicPositions) card;
            int index = atom.intProperty(AtomProperty.INPUT_INDEX);
            if (0 <= index && index < atomicPositions.numPositions()) {
                double x = atom.getX();
                double y = atom.getY();
                double z = atom.getZ();
                double[][] matrix = this.input.getAngstromInverse();
                double[] position = null;
                if (matrix != null) {
                    position = Matrix3D.mult(matrix, new double[] { x, y, z });
                }
                if (position != null) {
                    atomicPositions.setPosition(index, position);
                }
            }
        }

        atom.setProperty(QEGeometryInput.MODEL_BUSY, false);
    }

    private void onAtomFixedChanged(Atom atom) {
        if (this.input.isBusyWithActions()) {
            return;
        }

        if (atom == null) {
            return;
        }

        if (atom.isSlaveAtom()) {
            return;
        }

        atom.setProperty(QEGeometryInput.MODEL_BUSY, true);

        QECard card = this.input.getCard(QEAtomicPositions.CARD_NAME);
        if (card != null && card instanceof QEAtomicPositions) {
            QEAtomicPositions atomicPositions = (QEAtomicPositions) card;
            int index = atom.intProperty(AtomProperty.INPUT_INDEX);
            if (0 <= index || index < atomicPositions.numPositions()) {
                boolean[] mobile1 = atomicPositions.getMobile(index);
                if (mobile1 == null || mobile1.length < 3) {
                    mobile1 = new boolean[] { true, true, true };
                }

                boolean mobileX = !atom.booleanProperty(AtomProperty.FIXED_X);
                boolean mobileY = !atom.booleanProperty(AtomProperty.FIXED_Y);
                boolean mobileZ = !atom.booleanProperty(AtomProperty.FIXED_Z);
                boolean[] mobile2 = { mobileX, mobileY, mobileZ };

                if (mobile1[0] != mobile2[0] || mobile1[1] != mobile2[1] || mobile1[2] != mobile2[2]) {
                    atomicPositions.setMobile(index, mobile2);
                }
            }
        }

        atom.setProperty(QEGeometryInput.MODEL_BUSY, false);
    }
}
