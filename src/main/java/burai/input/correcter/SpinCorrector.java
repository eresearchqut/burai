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
import burai.input.card.QEAtomicSpecies;
import burai.pseudo.PseudoData;
import burai.pseudo.PseudoPotential;

public class SpinCorrector {

    public enum SpinType {
        NON_POLARIZED,
        COLINEAR,
        NON_COLINEAR;
    }

    private QEInput input;

    private QEAtomicSpecies cardSpecies;

    public SpinCorrector(QEInput input) {
        if (input == null) {
            throw new IllegalArgumentException("input is null.");
        }

        this.input = input;

        this.cardSpecies = this.input.getCard(QEAtomicSpecies.class);
    }

    public boolean isAvailable() {
        if (this.cardSpecies == null || this.cardSpecies.numSpecies() < 1) {
            return false;
        }

        return true;
    }

    public SpinType getSpinType() {
        if (this.cardSpecies == null) {
            return SpinType.NON_POLARIZED;
        }

        int numSpecies = this.cardSpecies.numSpecies();

        boolean hasRelative = false;
        boolean hasTransMetal = false;

        for (int i = 0; i < numSpecies; i++) {
            PseudoPotential pseudoPot = this.cardSpecies.getPseudoPotential(i);
            if (pseudoPot != null) {
                int relative = pseudoPot.getData().getRelativistic();
                if (relative == PseudoData.RELATIVISTIC_FULL) {
                    hasRelative = true;
                }
            }

            String label = this.cardSpecies.getLabel(i);
            label = label == null ? null : label.trim();
            if (label != null && !label.isEmpty()) {
                if (ElementUtil.isTransitionMetal(label)) {
                    hasTransMetal = true;
                }
            }
        }

        if (hasRelative) {
            return SpinType.NON_COLINEAR;

        } else if (hasTransMetal) {
            return SpinType.COLINEAR;

        } else {
            return SpinType.NON_POLARIZED;
        }
    }
}
