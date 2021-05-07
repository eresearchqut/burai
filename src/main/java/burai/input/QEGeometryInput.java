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

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import burai.atoms.element.ElementUtil;
import burai.atoms.model.Atom;
import burai.atoms.model.Cell;
import burai.atoms.model.property.AtomProperty;
import burai.com.math.Matrix3D;
import burai.input.card.QEAtomicPositions;
import burai.input.card.QEAtomicSpecies;
import burai.input.card.QECard;
import burai.input.card.QECellParameters;
import burai.input.card.tracer.QEAtomicTracer;
import burai.input.correcter.QEGeometryInputCorrecter;
import burai.input.correcter.QEInputCorrecter;
import burai.input.namelist.QENamelist;
import burai.pseudo.PseudoLibrary;
import burai.pseudo.PseudoPotential;

public class QEGeometryInput extends QEInput {

    protected static final String MODEL_BUSY = "busyWithActions";

    private CellBinder cellBinder;

    private QEInputBinder inputBinder;

    private boolean busyWithActions;

    public QEGeometryInput() {
        super();
        this.cellBinder = null;
        this.inputBinder = null;
        this.busyWithActions = false;
    }

    public QEGeometryInput(Cell cell) {
        super();

        if (cell == null) {
            throw new IllegalArgumentException("cell is null.");
        }

        this.setupLattice(cell);
        this.setupAtomicSpecies(cell);
        this.setupAtomicPositions(cell);

        QEInputCorrecter inputCorrecter = this.getInputCorrector();
        if (inputCorrecter != null) {
            inputCorrecter.correctInput();
        }

        this.cellBinder = null;
        this.inputBinder = null;
        this.busyWithActions = false;

        this.getCellBinder().bindCell(cell);
        this.getInputBinder().bindBy(cell);
    }

    public QEGeometryInput(String fileName) throws IOException {
        super(fileName);
        this.cellBinder = null;
        this.inputBinder = null;
        this.busyWithActions = false;
    }

    public QEGeometryInput(File file) throws IOException {
        super(file);
        this.cellBinder = null;
        this.inputBinder = null;
        this.busyWithActions = false;
    }

    private void setupLattice(Cell cell) {
        double[][] lattice = cell.copyLattice();
        if (lattice == null || lattice.length < 3) {
            lattice = Matrix3D.unit();
        }

        this.getInputBinder().setLattice(lattice);
    }

    private void setupAtomicSpecies(Cell cell) {
        Atom[] atoms = cell.listAtoms(true);
        if (atoms == null || atoms.length < 1) {
            return;
        }
        Set<String> names = new LinkedHashSet<String>();
        for (Atom atom : atoms) {
            names.add(atom.getName());
        }

        QECard card = this.cards.get(QEAtomicSpecies.CARD_NAME);
        if (!(card instanceof QEAtomicSpecies)) {
            return;
        }

        QEAtomicSpecies atomicSpecies = (QEAtomicSpecies) card;

        atomicSpecies.clear();
        for (String name : names) {
            String element = name == null ? null : ElementUtil.toElementName(name);
            double mass = ElementUtil.getMass(element);
            PseudoPotential pseudoPot =
                    element == null ? null : PseudoLibrary.getInstance().getPseudoPotential(element);
            atomicSpecies.addSpecies(name, mass, pseudoPot == null ? null : pseudoPot.getName());
        }
    }

    private void setupAtomicPositions(Cell cell) {
        Atom[] atoms = cell.listAtoms(true);
        if (atoms == null || atoms.length < 1) {
            return;
        }

        QECard card = this.cards.get(QEAtomicPositions.CARD_NAME);
        if (!(card instanceof QEAtomicPositions)) {
            return;
        }

        QEAtomicPositions atomicPositions = (QEAtomicPositions) card;

        atomicPositions.clear();
        atomicPositions.setAngstrom();
        for (int i = 0; i < atoms.length; i++) {
            Atom atom = atoms[i];
            if (atom == null) {
                continue;
            }

            String label = atom.getName();
            double[] position = { atom.getX(), atom.getY(), atom.getZ() };
            boolean[] mobile = { true, true, true };
            atomicPositions.addPosition(label, position, mobile);

            atom.setProperty(AtomProperty.FIXED_X, !mobile[0]);
            atom.setProperty(AtomProperty.FIXED_Y, !mobile[1]);
            atom.setProperty(AtomProperty.FIXED_Z, !mobile[2]);
            atom.setProperty(AtomProperty.INPUT_INDEX, i);
        }
    }

    @Override
    protected void setupNamelists(QEInputReader reader) throws IOException {
        boolean hasNmlSystem = this.namelists.containsKey(NAMELIST_SYSTEM);

        this.setupNamelist(NAMELIST_CONTROL, reader);
        this.setupNamelist(NAMELIST_SYSTEM, reader);

        if (!hasNmlSystem) {
            QENamelist nmlSystem = this.namelists.get(NAMELIST_SYSTEM);
            nmlSystem.addDeletingValue("celldm(1)");
            nmlSystem.addDeletingValue("celldm(2)");
            nmlSystem.addDeletingValue("celldm(3)");
            nmlSystem.addDeletingValue("celldm(4)");
            nmlSystem.addDeletingValue("celldm(5)");
            nmlSystem.addDeletingValue("celldm(6)");
            nmlSystem.addBindingValue("a");
            nmlSystem.addBindingValue("b");
            nmlSystem.addBindingValue("c");
            nmlSystem.addBindingValue("cosab");
            nmlSystem.addBindingValue("cosac");
            nmlSystem.addBindingValue("cosbc");
        }
    }

    @Override
    protected void setupCards(QEInputReader reader) throws IOException {
        QECard card1 = this.cards.get(QEAtomicSpecies.CARD_NAME);
        QECard card2 = this.cards.get(QEAtomicPositions.CARD_NAME);

        boolean hasAtomicCards = true;
        hasAtomicCards = hasAtomicCards && (card1 != null) && (card1 instanceof QEAtomicSpecies);
        hasAtomicCards = hasAtomicCards && (card2 != null) && (card2 instanceof QEAtomicPositions);

        this.setupCard(new QECellParameters(), reader);
        this.setupCard(new QEAtomicSpecies(), reader);
        this.setupCard(new QEAtomicPositions(), reader);

        if (!hasAtomicCards) {
            QENamelist nmlSystem = this.namelists.get(NAMELIST_SYSTEM);
            QEAtomicSpecies atomicSpecies = (QEAtomicSpecies) this.cards.get(QEAtomicSpecies.CARD_NAME);
            QEAtomicPositions atomicPositions = (QEAtomicPositions) this.cards.get(QEAtomicPositions.CARD_NAME);
            QEAtomicTracer atomicTracer = new QEAtomicTracer(nmlSystem, atomicSpecies);
            atomicTracer.traceAtomicPositions(atomicPositions);
        }
    }

    private CellBinder getCellBinder() {
        if (this.cellBinder == null) {
            this.cellBinder = new CellBinder(this);
        }

        return this.cellBinder;
    }

    private QEInputBinder getInputBinder() {
        if (this.inputBinder == null) {
            this.inputBinder = new QEInputBinder(this);
        }

        return this.inputBinder;
    }

    public Cell getCell() {
        Cell cell = this.getCellBuilder().buildCell();
        if (cell != null) {
            this.getCellBinder().bindCell(cell);
            this.getInputBinder().bindBy(cell);
        }

        return cell;
    }

    @Override
    public void reload() {
        QEInputCorrecter inputCorrecter = this.getInputCorrector();
        if (inputCorrecter != null) {
            inputCorrecter.correctInput();
        }
    }

    @Override
    public QEGeometryInput copy() {
        Cell cell = this.getCell();

        QEGeometryInput input = null;
        if (cell == null) {
            input = new QEGeometryInput();
        } else {
            input = new QEGeometryInput(cell);
        }

        QEInputCopier copier = new QEInputCopier(this);
        copier.copyTo(input, false);
        return input;
    }

    @Override
    protected QEInputCorrecter createInputCorrector() {
        return new QEGeometryInputCorrecter(this);
    }

    protected void setBusyWithActions(boolean busyWithActions) {
        this.busyWithActions = busyWithActions;
    }

    protected boolean isBusyWithActions() {
        return this.busyWithActions;
    }
}
