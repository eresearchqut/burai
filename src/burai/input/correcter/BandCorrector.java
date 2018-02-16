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

package burai.input.correcter;

import burai.atoms.element.ElementUtil;
import burai.input.QEInput;
import burai.input.card.QEAtomicPositions;
import burai.input.card.QEAtomicSpecies;
import burai.input.namelist.QENamelist;
import burai.input.namelist.QEValue;
import burai.pseudo.PseudoPotential;

public class BandCorrector {

    private QEInput input;

    private QENamelist nmlSystem;

    private QEAtomicSpecies cardSpecies;

    private QEAtomicPositions cardPositions;

    public BandCorrector(QEInput input) {
        if (input == null) {
            throw new IllegalArgumentException("input is null.");
        }

        this.input = input;

        this.nmlSystem = this.input.getNamelist(QEInput.NAMELIST_SYSTEM);

        this.cardSpecies = this.input.getCard(QEAtomicSpecies.class);

        this.cardPositions = this.input.getCard(QEAtomicPositions.class);
    }

    public boolean isAvailable() {
        if (this.cardSpecies == null || this.cardSpecies.numSpecies() < 1) {
            return false;
        }

        if (this.cardPositions == null || this.cardPositions.numPositions() < 1) {
            return false;
        }

        return true;
    }

    public int getNumBands() {
        if (this.cardSpecies == null || this.cardPositions == null) {
            return 0;
        }

        int numElems = this.cardSpecies.numSpecies();
        int[] nbandList = new int[numElems];

        for (int i = 0; i < numElems; i++) {
            String name = this.cardSpecies.getLabel(i);
            if (name == null || name.trim().isEmpty()) {
                nbandList[i] = 0;
                continue;
            }

            int valence = Math.max(0, ElementUtil.getValence(name));

            int numOrb = 0;
            if (ElementUtil.isLanthanoid(name) || ElementUtil.isActinoid(name)) {
                numOrb = 1 + 3 + 5 + 7;
            } else if (ElementUtil.isTransitionMetal(name)) {
                numOrb = 1 + 3 + 5;
            } else {
                numOrb = 1 + 3;
            }

            PseudoPotential pseudoPot = this.cardSpecies.getPseudoPotential(i);
            if (pseudoPot == null) {
                nbandList[i] = numOrb;
                continue;
            }

            double zValence = pseudoPot.getData().getZValence();
            double dValence = zValence - (double) valence;
            double occCore = 0.5 * dValence;
            int numCore = Math.max(0, (int) (occCore + 1.0 - 1.0e-6));
            nbandList[i] = numOrb + numCore;
        }

        int nbands = 0;
        int numAtoms = this.cardPositions.numPositions();
        for (int i = 0; i < numAtoms; i++) {
            String name = this.cardPositions.getLabel(i);
            int index = this.cardSpecies.indexOfSpecies(name);
            nbands += nbandList[index];
        }

        if (this.nmlSystem != null) {
            QEValue value = this.nmlSystem.getValue("noncolin");
            if (value != null && value.getLogicalValue()) {
                nbands *= 2;
            }
        }

        return nbands;
    }
}
