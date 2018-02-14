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

public class BondEvent extends ModelEvent {

    public static final int LINKED_ATOM_NULL = 0;
    public static final int LINKED_ATOM1 = 1;
    public static final int LINKED_ATOM2 = 2;

    private AtomEvent atomEvent;

    private int linkedAtom;

    public BondEvent(Object source) {
        super(source);
        this.atomEvent = null;
        this.linkedAtom = LINKED_ATOM_NULL;
    }

    public void setAtomEvent(AtomEvent atomEvent) {
        this.atomEvent = atomEvent;
    }

    public AtomEvent getAtomEvent() {
        return this.atomEvent;
    }

    public void setLinkedAtom(int linkedAtom) {
        this.linkedAtom = linkedAtom;
    }

    public int getLinkedAtom() {
        return this.linkedAtom;
    }

    public Atom getAtom() {
        if (this.source instanceof Atom) {
            return (Atom) this.source;
        }

        return null;
    }
}
