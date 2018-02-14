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

import java.util.ArrayList;
import java.util.List;

import burai.atoms.model.event.AtomEvent;
import burai.atoms.model.event.AtomEventListener;
import burai.atoms.model.event.CellEvent;
import burai.atoms.model.event.CellEventListener;
import burai.atoms.model.event.ModelEvent;
import burai.com.env.Environments;
import burai.com.parallel.Parallel;
import javafx.application.Platform;

public class BondsResolver implements AtomEventListener, CellEventListener {

    private static final double THR_DENSITY = 0.50;

    private static final double BOND_SCALE1 = 0.50;

    private static final double BOND_SCALE2 = 1.50;

    private static final double THR_ATOM_MOTION = 1.0e-3;

    private static final double THR_ATOM_MOTION2 = THR_ATOM_MOTION * THR_ATOM_MOTION;

    private static final int NUM_THREADS = Math.max(1, Environments.getNumCUPs() - 1);

    private static final int NUM_ATOMS_TO_ASYNC = 16;

    private static final int NUM_ATOMS_TO_PARALLEL = 64;

    private static final int DIM_BONDS = 6;

    private Cell cell;

    boolean auto;

    protected BondsResolver(Cell cell) {
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
        int natom = this.cell.numAtoms();
        if (natom <= NUM_ATOMS_TO_ASYNC) {
            this.resolveAll();

        } else {
            Platform.runLater(() -> {
                this.resolveAll();
            });
        }
    }

    private void resolveAll() {

        this.removeNotUsedBonds();

        List<Atom> atoms = this.cell.getAtoms();
        if (atoms == null || atoms.isEmpty()) {
            return;
        }

        if (!this.isAbleToResolve(true)) {
            return;
        }

        int natom = atoms.size();
        int nbond = this.cell.numBonds();

        List<Bond> bondsToAdd = new ArrayList<Bond>();
        List<Bond> bondsToRemove = new ArrayList<Bond>();

        if (NUM_THREADS < 2 || natom <= NUM_ATOMS_TO_PARALLEL) {
            // serial calculation
            for (int i = 0; i < natom; i++) {
                Atom atom = atoms.get(i);
                Bond[][] bondsBuffer = this.resolve(atom, i, atoms, nbond == 0);
                if (bondsBuffer == null || bondsBuffer.length < 2) {
                    continue;
                }

                Bond[] bondsToAdd_ = bondsBuffer[0];
                if (bondsToAdd_ != null && bondsToAdd_.length > 0) {
                    for (Bond bond : bondsToAdd_) {
                        if (bond == null) {
                            break;
                        }
                        bondsToAdd.add(bond);
                    }
                }

                Bond[] bondsToRemove_ = bondsBuffer[1];
                if (bondsToRemove_ != null && bondsToRemove_.length > 0) {
                    for (Bond bond : bondsToRemove_) {
                        if (bond == null) {
                            break;
                        }
                        bondsToRemove.add(bond);
                    }
                }
            }

        } else {
            // parallel calculation
            Integer[] iatom = new Integer[natom];
            for (int i = 0; i < iatom.length; i++) {
                iatom[i] = i;
            }

            Parallel<Integer, Object> parallel = new Parallel<Integer, Object>(iatom);
            parallel.setNumThreads(NUM_THREADS);
            parallel.forEach(i -> {

                Atom atom = atoms.get(i);
                Bond[][] bondsBuffer = this.resolve(atom, i, atoms, nbond == 0);
                if (bondsBuffer == null || bondsBuffer.length < 2) {
                    return null;
                }

                Bond[] bondsToAdd_ = bondsBuffer[0];
                if (bondsToAdd_ != null && bondsToAdd_.length > 0) {
                    synchronized (bondsToAdd) {
                        for (Bond bond : bondsToAdd_) {
                            if (bond == null) {
                                break;
                            }
                            bondsToAdd.add(bond);
                        }
                    }
                }

                Bond[] bondsToRemove_ = bondsBuffer[1];
                if (bondsToRemove_ != null && bondsToRemove_.length > 0) {
                    synchronized (bondsToRemove) {
                        for (Bond bond : bondsToRemove_) {
                            if (bond == null) {
                                break;
                            }
                            bondsToRemove.add(bond);
                        }
                    }
                }

                return null;
            });
        }

        for (Bond bond : bondsToAdd) {
            this.cell.addBond(bond);
        }

        for (Bond bond : bondsToRemove) {
            this.cell.removeBond(bond);
        }
    }

    protected void resolve(Atom atom) {
        List<Atom> atoms = this.cell.getAtoms();
        if (atoms == null || atoms.isEmpty()) {
            return;
        }

        if (!this.isAbleToResolve(false)) {
            return;
        }

        Bond[][] bondsBuffer = this.resolve(atom, atoms.size(), atoms, false);
        if (bondsBuffer == null || bondsBuffer.length < 2) {
            return;
        }

        Bond[] bondsToAdd = bondsBuffer[0];
        if (bondsToAdd != null && bondsToAdd.length > 0) {
            for (Bond bond : bondsToAdd) {
                if (bond == null) {
                    break;
                }
                this.cell.addBond(bond);
            }
        }

        Bond[] bondsToRemove = bondsBuffer[1];
        if (bondsToRemove != null && bondsToRemove.length > 0) {
            for (Bond bond : bondsToRemove) {
                if (bond == null) {
                    break;
                }
                this.cell.removeBond(bond);
            }
        }
    }

    private boolean isAbleToResolve(boolean fullCheck) {
        double ratom = (double) this.cell.numAtoms();

        double volume0 = this.cell.getVolume();
        if (volume0 <= 0.0) {
            return false;
        }

        double density0 = ratom / volume0;
        if (density0 > THR_DENSITY) {
            return false;
        }

        if (!fullCheck) {
            return true;
        }

        double volume1 = this.cell.getBoundaryVolume();
        if (volume1 <= 0.0) {
            return false;
        }

        double density1 = ratom / volume1;
        if (density1 > THR_DENSITY) {
            return false;
        }

        return true;
    }

    private Bond[][] resolve(Atom atom1, int maxAtom, List<Atom> atoms, boolean fromBeginning) {
        if (atom1 == null) {
            return null;
        }

        if (maxAtom < 1) {
            return null;
        }

        if (atoms == null || atoms.size() < maxAtom) {
            return null;
        }

        double x1 = atom1.getX();
        double y1 = atom1.getY();
        double z1 = atom1.getZ();
        double rcov1 = atom1.getRadius();

        List<Bond> bonds = null;
        if (!fromBeginning) {
            bonds = this.cell.pickBonds(atom1);
        }

        int numToAdd = 0;
        int numToRemove = 0;
        Bond[] bondsToAdd = null;
        Bond[] bondsToRemove = null;

        for (int i = 0; i < maxAtom; i++) {
            Atom atom2 = atoms.get(i);
            if (atom1 == atom2) {
                continue;
            }

            double x2 = atom2.getX();
            double y2 = atom2.getY();
            double z2 = atom2.getZ();
            double rcov2 = atom2.getRadius();

            double rr = (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2) + (z1 - z2) * (z1 - z2);
            double rcov = rcov1 + rcov2;
            double rrcov = rcov * rcov;
            double rrmin = BOND_SCALE1 * BOND_SCALE1 * rrcov;
            double rrmax = BOND_SCALE2 * BOND_SCALE2 * rrcov;

            Bond bond = null;
            if (!fromBeginning) {
                bond = this.cell.pickBond(atom1, atom2, bonds);
            }

            if (rrmin <= rr && rr <= rrmax) {
                if (bond == null) {
                    if (bondsToAdd == null || numToAdd >= bondsToAdd.length) {
                        Bond[] bondsTmp = new Bond[numToAdd + DIM_BONDS];
                        for (int j = 0; j < numToAdd; j++) {
                            bondsTmp[j] = bondsToAdd[j];
                        }
                        bondsToAdd = bondsTmp;
                    }

                    bondsToAdd[numToAdd] = new Bond(atom1, atom2);
                    numToAdd++;
                }

            } else {
                if (bond != null) {
                    if (bondsToRemove == null || numToRemove >= bondsToRemove.length) {
                        Bond[] bondsTmp = new Bond[numToRemove + DIM_BONDS];
                        for (int j = 0; j < numToRemove; j++) {
                            bondsTmp[j] = bondsToRemove[j];
                        }
                        bondsToRemove = bondsTmp;
                    }

                    bondsToRemove[numToRemove] = bond;
                    numToRemove++;
                }
            }
        }

        Bond[][] bondsBuffer = new Bond[2][];
        bondsBuffer[0] = bondsToAdd;
        bondsBuffer[1] = bondsToRemove;
        return bondsBuffer;
    }

    private void removeAllBondsLinkedWith(Atom atom) {
        if (atom == null) {
            throw new IllegalArgumentException("atom is null.");
        }

        Bond[] bonds = this.cell.listBonds();
        if (bonds == null || bonds.length < 1) {
            return;
        }

        for (Bond bond : bonds) {
            Atom atom1 = bond.getAtom1();
            Atom atom2 = bond.getAtom2();
            if (atom == atom1 || atom == atom2) {
                this.cell.removeBond(bond);
            }
        }
    }

    private void removeNotUsedBonds() {
        Bond[] bonds = this.cell.listBonds();
        if (bonds == null || bonds.length < 1) {
            return;
        }

        List<Atom> atoms = this.cell.getAtoms();
        if (atoms == null || atoms.isEmpty()) {
            for (Bond bond : bonds) {
                this.cell.removeBond(bond);
            }
            return;
        }

        for (Bond bond : bonds) {
            Atom atom1 = bond.getAtom1();
            Atom atom2 = bond.getAtom2();
            boolean hasAtom1 = false;
            boolean hasAtom2 = false;
            for (Atom atom : atoms) {
                if (hasAtom1 && hasAtom2) {
                    break;
                } else if (!hasAtom1) {
                    hasAtom1 = (atom == atom1);
                } else if (!hasAtom2) {
                    hasAtom2 = (atom == atom2);
                }
            }
            if (!(hasAtom1 && hasAtom2)) {
                this.cell.removeBond(bond);
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

        this.resolve(atom);
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

        this.removeAllBondsLinkedWith(atom);
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
        if (event == null) {
            return;
        }

        if (!this.auto) {
            return;
        }

        String name1 = event.getOldName();
        String name2 = event.getName();
        if (name1 != null && name1.equals(name2)) {
            return;
        }

        Object obj = event.getSource();
        if (obj == null || !(obj instanceof Atom)) {
            return;
        }

        Atom atom = (Atom) obj;
        this.resolve(atom);
    }

    @Override
    public void onAtomMoved(AtomEvent event) {
        if (event == null) {
            return;
        }

        if (!this.auto) {
            return;
        }

        double dx = event.getDeltaX();
        double dy = event.getDeltaY();
        double dz = event.getDeltaZ();
        double rr = dx * dx + dy * dy + dz * dz;
        if (rr < THR_ATOM_MOTION2) {
            return;
        }

        Object obj = event.getSource();
        if (obj == null || !(obj instanceof Atom)) {
            return;
        }

        Atom atom = (Atom) obj;
        this.resolve(atom);
    }
}
