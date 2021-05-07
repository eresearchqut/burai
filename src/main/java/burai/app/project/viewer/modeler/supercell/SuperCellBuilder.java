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

package burai.app.project.viewer.modeler.supercell;

import burai.app.project.viewer.modeler.ModelerBase;
import burai.atoms.model.Atom;
import burai.atoms.model.Cell;
import burai.atoms.model.exception.ZeroVolumCellException;
import burai.atoms.model.property.AtomProperty;
import burai.atoms.model.property.CellProperty;
import burai.com.env.Environments;
import burai.com.math.Matrix3D;
import burai.com.parallel.Parallel;

public class SuperCellBuilder {

    private static final int NUM_THREADS = Math.max(1, Environments.getNumCUPs() - 1);

    private Cell cell;

    public SuperCellBuilder(Cell cell) {
        if (cell == null) {
            throw new IllegalArgumentException("cell is null.");
        }

        this.cell = cell;
    }

    public boolean build(int na, int nb, int nc) {
        if (na < 1 || nb < 1 || nc < 1) {
            return false;
        }

        int nt = na * nb * nc;
        int natom = this.cell.numAtoms(true);
        if ((nt * natom) >= ModelerBase.maxNumAtoms()) {
            return false;
        }

        Atom[] atoms = this.cell.listAtoms(true);
        this.cell.removeAllAtoms();
        this.cell.stopResolving();

        try {

            // expand lattice
            double[][] lattice = this.cell.copyLattice();
            if (lattice == null || lattice.length < 3) {
                return false;
            }

            double[][] lattice_ = Matrix3D.copy(lattice);
            if (lattice_ == null || lattice_.length < 3) {
                return false;
            }

            for (int i = 0; i < 3; i++) {
                lattice_[0][i] *= (double) na;
                lattice_[1][i] *= (double) nb;
                lattice_[2][i] *= (double) nc;
            }

            try {
                this.cell.moveLattice(lattice_);
            } catch (ZeroVolumCellException e) {
                e.printStackTrace();
                return false;
            }

            if (this.cell.hasProperty(CellProperty.AXIS)) {
                String axis = this.cell.stringProperty(CellProperty.AXIS);
                if ("x".equalsIgnoreCase(axis)) {
                    if (na > 1) {
                        this.cell.removeProperty(CellProperty.AXIS);
                    }
                } else if ("y".equalsIgnoreCase(axis)) {
                    if (nb > 1) {
                        this.cell.removeProperty(CellProperty.AXIS);
                    }
                } else if ("z".equalsIgnoreCase(axis)) {
                    if (nc > 1) {
                        this.cell.removeProperty(CellProperty.AXIS);
                    }
                }
            }

            // fill with atoms
            if (atoms == null || atoms.length < natom) {
                return true;
            }

            Atom[][] atomsBuffer = new Atom[nt][];

            Integer[] indexes = new Integer[nt];
            for (int it = 0; it < indexes.length; it++) {
                indexes[it] = it;
            }

            Parallel<Integer, Object> parallel = new Parallel<Integer, Object>(indexes);
            parallel.setNumThreads(NUM_THREADS);
            parallel.forEach(it -> {

                int it0 = it;
                int ia = it0 / (nb * nc);
                it0 -= ia * (nb * nc);
                int ib = it0 / nc;
                it0 -= ib * nc;
                int ic = it0;

                double ra = (double) ia;
                double rb = (double) ib;
                double rc = (double) ic;
                double tx = ra * lattice[0][0] + rb * lattice[1][0] + rc * lattice[2][0];
                double ty = ra * lattice[0][1] + rb * lattice[1][1] + rc * lattice[2][1];
                double tz = ra * lattice[0][2] + rb * lattice[1][2] + rc * lattice[2][2];

                Atom[] atoms_ = new Atom[natom];
                for (int i = 0; i < natom; i++) {
                    Atom atom = atoms[i];
                    if (atom == null) {
                        atoms_[i] = null;
                    } else {
                        String name = atom.getName();
                        double x = atom.getX() + tx;
                        double y = atom.getY() + ty;
                        double z = atom.getZ() + tz;
                        boolean xFix = atom.booleanProperty(AtomProperty.FIXED_X);
                        boolean yFix = atom.booleanProperty(AtomProperty.FIXED_Y);
                        boolean zFix = atom.booleanProperty(AtomProperty.FIXED_Z);
                        atoms_[i] = new Atom(name, x, y, z);
                        atoms_[i].setProperty(AtomProperty.FIXED_X, xFix);
                        atoms_[i].setProperty(AtomProperty.FIXED_Y, yFix);
                        atoms_[i].setProperty(AtomProperty.FIXED_Z, zFix);
                    }
                }

                synchronized (atomsBuffer) {
                    atomsBuffer[it] = atoms_;
                }

                return null;
            });

            for (int it = 0; it < nt; it++) {
                if (atomsBuffer[it] == null || atomsBuffer[it].length < natom) {
                    continue;
                }
                for (int i = 0; i < natom; i++) {
                    Atom atom = atomsBuffer[it][i];
                    if (atom != null) {
                        this.cell.addAtom(atom);
                    }
                }
            }

            return true;

        } finally {
            this.cell.restartResolving();
        }
    }
}
