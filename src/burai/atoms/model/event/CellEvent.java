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

package burai.atoms.model.event;

import burai.atoms.model.Atom;
import burai.atoms.model.Bond;

public class CellEvent extends ModelEvent {

    private double[][] lattice;

    private Atom atom;

    private Bond bond;

    public CellEvent(Object source) {
        super(source);
        this.lattice = null;
        this.atom = null;
        this.bond = null;
    }

    public void setLattice(double[][] lattice) {
        this.lattice = lattice;
    }

    public double[][] getLattice() {
        return this.lattice;
    }

    public void setAtom(Atom atom) {
        this.atom = atom;
    }

    public Atom getAtom() {
        return this.atom;
    }

    public void setBond(Bond bond) {
        this.bond = bond;
    }

    public Bond getBond() {
        return this.bond;
    }
}
