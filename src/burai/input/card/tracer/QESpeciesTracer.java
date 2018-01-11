/*
 * Copyright (C) 2017 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.input.card.tracer;

import java.util.HashMap;
import java.util.Map;

import burai.com.consts.ConstantAtoms;
import burai.input.QEInput;
import burai.input.card.QEAtomicSpecies;
import burai.input.card.QECardEvent;
import burai.input.card.QECardListener;
import burai.input.namelist.QENamelist;
import burai.input.namelist.QEValue;
import burai.input.namelist.QEValueBuffer;

public class QESpeciesTracer implements QECardListener {

    private static int MAX_NUM_ELEMS = ConstantAtoms.MAX_NUM_ELEMS;

    private static class AtomicValue {
        public Double startMag;
        public Double angle1;
        public Double angle2;
        public Double hubbard;

        public AtomicValue() {
            this.startMag = null;
            this.angle1 = null;
            this.angle2 = null;
            this.hubbard = null;
        }
    }

    private boolean stopped;

    private boolean busySpecies;

    private QENamelist nmlSystem;

    private QEAtomicSpecies atomicSpecies;

    private Map<String, AtomicValue> atomicValues;

    public QESpeciesTracer(QENamelist nmlSystem, QEAtomicSpecies atomicSpecies) {
        if (nmlSystem == null || !QEInput.NAMELIST_SYSTEM.equals(nmlSystem.getName())) {
            throw new IllegalArgumentException("nmlSystem is incorrect.");
        }

        if (atomicSpecies == null) {
            throw new IllegalArgumentException("atomicSpecies is null.");
        }

        this.stopped = false;
        this.busySpecies = false;

        this.nmlSystem = nmlSystem;
        this.atomicSpecies = atomicSpecies;
        this.atomicValues = null;
    }

    public void stopTracer() {
        this.stopped = true;
    }

    public void restartTracer() {
        this.stopped = false;

        int numSpecs = this.atomicSpecies.numSpecies();
        for (int i = 0; i < numSpecs; i++) {
            this.storeAtomicValues(i);
        }
    }

    public void traceAtomicSpecies() {
        int numSpecs = this.atomicSpecies.numSpecies();
        for (int i = 0; i < numSpecs; i++) {
            this.storeAtomicValues(i);
        }

        for (int i = 0; i < MAX_NUM_ELEMS; i++) {
            QEValueBuffer startMagBuffer = this.nmlSystem.getValueBuffer("starting_magnetization(" + (i + 1) + ")");
            QEValueBuffer angle1Buffer = this.nmlSystem.getValueBuffer("angle1(" + (i + 1) + ")");
            QEValueBuffer angle2Buffer = this.nmlSystem.getValueBuffer("angle2(" + (i + 1) + ")");
            QEValueBuffer hubbardBuffer = this.nmlSystem.getValueBuffer("hubbard_u(" + (i + 1) + ")");

            int i_ = i;
            startMagBuffer.addListener(value -> this.storeAtomicValues(i_));
            angle1Buffer.addListener(value -> this.storeAtomicValues(i_));
            angle2Buffer.addListener(value -> this.storeAtomicValues(i_));
            hubbardBuffer.addListener(value -> this.storeAtomicValues(i_));
        }

        this.atomicSpecies.addListener(this);
    }

    @Override
    public void onCardChanged(QECardEvent event) {
        if (this.stopped) {
            return;
        }

        this.busySpecies = true;
        this.restoreAtomicValues();
        this.busySpecies = false;
    }

    private void storeAtomicValues(int index) {
        if (this.stopped) {
            return;
        }

        if (this.busySpecies) {
            return;
        }

        if (this.nmlSystem.isClearing()) {
            return;
        }

        if (this.atomicValues == null) {
            this.atomicValues = new HashMap<String, AtomicValue>();
        }

        if (index < 0) {
            return;
        }

        int numSpecs = this.atomicSpecies.numSpecies();
        if (index >= numSpecs) {
            return;
        }

        String label = this.atomicSpecies.getLabel(index);
        AtomicValue aValue = this.createAtomicValue(index);
        this.atomicValues.put(label, aValue);
    }

    private void restoreAtomicValues() {
        if (this.atomicValues == null || this.atomicValues.isEmpty()) {
            return;
        }

        int numSpecs = this.atomicSpecies.numSpecies();
        for (int i = 0; i < numSpecs; i++) {
            String label = this.atomicSpecies.getLabel(i);
            AtomicValue aValue = this.atomicValues.get(label);
            if (aValue != null) {
                this.setupAtomicValue(i, aValue);
            } else {
                this.removeAtomicValue(i);
            }
        }

        for (int i = numSpecs; i < MAX_NUM_ELEMS; i++) {
            this.removeAtomicValue(i);
        }
    }

    private AtomicValue createAtomicValue(int index) {
        if (index < 0) {
            return null;
        }

        QEValue value = null;
        AtomicValue aValue = new AtomicValue();

        int index1 = index + 1;

        value = this.nmlSystem.getValue("starting_magnetization(" + index1 + ")");
        if (value != null) {
            aValue.startMag = value.getRealValue();
        }

        value = this.nmlSystem.getValue("angle1(" + index1 + ")");
        if (value != null) {
            aValue.angle1 = value.getRealValue();
        }

        value = this.nmlSystem.getValue("angle2(" + index1 + ")");
        if (value != null) {
            aValue.angle2 = value.getRealValue();
        }

        value = this.nmlSystem.getValue("hubbard_u(" + index1 + ")");
        if (value != null) {
            aValue.hubbard = value.getRealValue();
        }

        return aValue;
    }

    private void setupAtomicValue(int index, AtomicValue aValue) {
        if (index < 0) {
            return;
        }

        if (aValue == null) {
            return;
        }

        int index1 = index + 1;

        if (aValue.startMag != null) {
            this.nmlSystem.setValue("starting_magnetization(" + index1 + ") = " + aValue.startMag);
        } else {
            this.nmlSystem.removeValue("starting_magnetization(" + index1 + ")");
        }

        if (aValue.angle1 != null) {
            this.nmlSystem.setValue("angle1(" + index1 + ") = " + aValue.angle1);
        } else {
            this.nmlSystem.removeValue("angle1(" + index1 + ")");
        }

        if (aValue.angle2 != null) {
            this.nmlSystem.setValue("angle2(" + index1 + ") = " + aValue.angle2);
        } else {
            this.nmlSystem.removeValue("angle2(" + index1 + ")");
        }

        if (aValue.hubbard != null) {
            this.nmlSystem.setValue("hubbard_u(" + index1 + ") = " + aValue.hubbard);
        } else {
            this.nmlSystem.removeValue("hubbard_u(" + index1 + ")");
        }
    }

    private void removeAtomicValue(int index) {
        if (index < 0) {
            return;
        }

        int index1 = index + 1;

        this.nmlSystem.removeValue("starting_magnetization(" + index1 + ")");
        this.nmlSystem.removeValue("angle1(" + index1 + ")");
        this.nmlSystem.removeValue("angle2(" + index1 + ")");
        this.nmlSystem.removeValue("hubbard_u(" + index1 + ")");
    }
}
