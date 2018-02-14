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

package burai.app.project.viewer.modeler.slabmodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import burai.app.project.viewer.modeler.ModelerBase;
import burai.atoms.model.Atom;
import burai.atoms.model.Cell;
import burai.atoms.model.exception.ZeroVolumCellException;
import burai.atoms.model.property.AtomProperty;
import burai.atoms.model.property.CellProperty;
import burai.com.math.Lattice;
import burai.com.math.Matrix3D;

public class SlabModelStem extends SlabModel {

    private static final double DET_THR = 1.0e-8;
    private static final double PACK_THR = 1.0e-6; // internal coordinate
    private static final double OFFSET_THR = 1.0e-12; // angstrom
    private static final double THICK_THR = 1.0e-12; // angstrom
    private static final double POSIT_THR = 1.0e-4; // angstrom
    private static final double VALUE_THR = 1.0e-12;
    private static final double STOIX_THR = 1.0e-6;

    private static final String STOIX_NATOM = "NAtom";

    private static final double STEP_FOR_GENOMS = 0.50; // angstrom
    private static final double STEP_FOR_CTHICK = 0.05; // internal coordinate
    private static final int MAX_FOR_CTHICK = 20;

    private static final double SLAB_FIX_THR = 0.1; // angstrom
    private static final double SLAB_FIX_RATE = 0.5; // internal coordinate

    private int miller1;
    private int miller2;
    private int miller3;

    private int intercept1;
    private int intercept2;
    private int intercept3;
    private int numIntercept;
    private boolean hasIntercept1;
    private boolean hasIntercept2;
    private boolean hasIntercept3;

    private int[] vector1;
    private int[] vector2;
    private int[] vector3;

    private int[][] boundBox;

    private double[] lattConst;
    private double[][] lattUnit;
    private double[][] lattAuxi;
    private double[][] lattSlab;

    private List<AtomEntry> entryUnit;
    private List<AtomEntry> entryAuxi;
    private List<AtomEntry> entrySlab;

    private Map<String, Double> stoixUnit;

    private String codeAuxi;
    private String codeSlab;

    private Double currOffset;
    private Double currThickness;
    private Double currVacuum;
    private Integer currScaleA;
    private Integer currScaleB;

    /**
     * cell is not changed.
     */
    public SlabModelStem(Cell cell, int h, int k, int l) throws MillerIndexException {
        super();

        if (cell == null) {
            throw new IllegalArgumentException("cell is null.");
        }

        if (!this.setupMillers(cell, h, k, l)) {
            throw new MillerIndexException();
        }

        if (!this.setupUnitAtomsInCell(cell)) {
            throw new MillerIndexException();
        }

        if (!this.setupUnitAtomsInSlab()) {
            throw new MillerIndexException();
        }

        this.lattAuxi = null;
        this.lattSlab = null;
        this.entryAuxi = null;
        this.entrySlab = null;
        this.codeAuxi = null;
        this.codeSlab = null;

        this.currOffset = null;
        this.currThickness = null;
        this.currVacuum = null;
        this.currScaleA = null;
        this.currScaleB = null;
    }

    @Override
    public SlabModel[] getSlabModels() {
        if (this.lattUnit == null || this.lattUnit.length < 3) {
            return new SlabModel[] { new SlabModelLeaf(this, this.offset) };
        }
        if (this.lattUnit[2] == null || this.lattUnit[2].length < 3) {
            return new SlabModel[] { new SlabModelLeaf(this, this.offset) };
        }

        int nstep = (int) (this.lattUnit[2][2] / STEP_FOR_GENOMS);
        if (nstep < 2) {
            return new SlabModel[] { new SlabModelLeaf(this, this.offset) };
        }

        Map<SlabGenom, Double> slabGenoms = new LinkedHashMap<SlabGenom, Double>();

        for (int i = 0; i < nstep; i++) {
            double offset = ((double) i) / ((double) nstep);
            SlabGenom slabGenom = this.getSlabGenom(offset);
            if (slabGenom != null && !(slabGenoms.containsKey(slabGenom))) {
                slabGenoms.put(slabGenom, offset);
            }
        }

        if (slabGenoms.isEmpty()) {
            return new SlabModel[] { new SlabModelLeaf(this, this.offset) };
        }

        int index = 0;
        SlabModel[] slabModels = new SlabModel[slabGenoms.size()];
        for (double offset : slabGenoms.values()) {
            slabModels[index] = new SlabModelLeaf(this, offset);
            index++;
        }

        return slabModels;
    }

    private SlabGenom getSlabGenom(double offset) {
        if (this.lattUnit == null || this.lattUnit.length < 3) {
            return null;
        }
        if (this.lattUnit[2] == null || this.lattUnit[2].length < 3) {
            return null;
        }

        if (this.entryUnit == null || this.entryUnit.isEmpty()) {
            return null;
        }

        int natom = this.entryUnit.size();
        int iatom = natom;

        String[] names = new String[natom];
        double[] coords = new double[natom];

        for (int i = 0; i < natom; i++) {
            AtomEntry entry = this.entryUnit.get(i);
            if (entry == null) {
                return null;
            }

            double c1 = entry.c + offset;
            double c2 = c1;
            c2 -= Math.floor(c2);

            double dc = Math.abs(c2 - 1.0);
            double dz = dc * this.lattUnit[2][2];
            if (dz < POSIT_THR) {
                c2 -= 1.0;
            }

            dz = Math.abs(c1 - c2) * this.lattUnit[2][2];
            if (iatom >= natom && dz < POSIT_THR) {
                iatom = i;
            }

            names[i] = entry.name;
            coords[i] = c2 * this.lattUnit[2][2];
        }

        String[] names2 = new String[natom];
        double[] coords2 = new double[natom];

        int index = 0;
        for (int i = iatom; i < natom; i++) {
            names2[index] = names[i];
            coords2[index] = coords[i];
            index++;
        }

        for (int i = 0; i < iatom; i++) {
            names2[index] = names[i];
            coords2[index] = coords[i];
            index++;
        }

        return new SlabGenom(names2, coords2);
    }

    @Override
    protected boolean updateCell(Cell cell) {
        return this.updateCell(cell, this);
    }

    protected boolean updateCell(Cell cell, SlabModel slabModel) {
        if (cell == null) {
            return false;
        }

        if (slabModel == null) {
            return false;
        }

        if (!this.setupAuxiAtoms(slabModel)) {
            return false;
        }

        if (!this.setupSlabAtoms(slabModel)) {
            return false;
        }

        if (this.lattSlab == null || this.lattSlab.length < 3) {
            return false;
        }

        if (this.entrySlab == null || this.entrySlab.isEmpty()) {
            return false;
        }

        cell.stopResolving();

        try {
            cell.moveLattice(this.lattSlab);
        } catch (ZeroVolumCellException e) {
            cell.restartResolving();
            return false;
        }

        cell.setProperty(CellProperty.AXIS, "z");

        int natom = this.entrySlab.size();
        int natom2 = cell.numAtoms(true);

        Atom[] refAtoms = null;
        if (natom == natom2) {
            refAtoms = cell.listAtoms(true);
        }

        double zMax = -Double.MAX_VALUE;
        double zMin = +Double.MAX_VALUE;
        for (AtomEntry entry : this.entrySlab) {
            if (entry != null) {
                zMax = Math.max(zMax, entry.z);
                zMin = Math.min(zMin, entry.z);
            }
        }

        if (refAtoms != null && refAtoms.length == natom) {
            for (int i = 0; i < natom; i++) {
                AtomEntry entry = this.entrySlab.get(i);
                if (entry == null) {
                    continue;
                }

                String name = entry.name;
                if (name == null || name.isEmpty()) {
                    continue;
                }

                Atom atom = refAtoms[i];
                if (atom == null) {
                    atom = new Atom(name, entry.x, entry.y, entry.z);
                    cell.addAtom(atom);

                } else {
                    String name2 = atom.getName();
                    if (!name.equals(name2)) {
                        atom.setName(name);
                    }

                    double x2 = atom.getX();
                    double y2 = atom.getY();
                    double z2 = atom.getZ();
                    if (Math.abs(entry.x - x2) > 0.0 || Math.abs(entry.y - y2) > 0.0 || Math.abs(entry.z - z2) > 0.0) {
                        atom.moveTo(entry.x, entry.y, entry.z);
                    }
                }

                boolean toFix = false;
                if ((zMax - zMin) < SLAB_FIX_THR) {
                    toFix = true;
                } else if ((entry.z - zMin - SLAB_FIX_THR) / (zMax - zMin) < SLAB_FIX_RATE) {
                    toFix = true;
                }

                boolean xFix = atom.booleanProperty(AtomProperty.FIXED_X);
                boolean yFix = atom.booleanProperty(AtomProperty.FIXED_Y);
                boolean zFix = atom.booleanProperty(AtomProperty.FIXED_Z);

                if (xFix != toFix) {
                    atom.setProperty(AtomProperty.FIXED_X, toFix);
                }
                if (yFix != toFix) {
                    atom.setProperty(AtomProperty.FIXED_Y, toFix);
                }
                if (zFix != toFix) {
                    atom.setProperty(AtomProperty.FIXED_Z, toFix);
                }
            }

        } else {
            cell.removeAllAtoms();

            for (AtomEntry entry : this.entrySlab) {
                if (entry == null) {
                    continue;
                }

                String name = entry.name;
                if (name == null || name.isEmpty()) {
                    continue;
                }

                boolean toFix = false;
                if ((zMax - zMin) < SLAB_FIX_THR) {
                    toFix = true;
                } else if ((entry.z - zMin - SLAB_FIX_THR) / (zMax - zMin) < SLAB_FIX_RATE) {
                    toFix = true;
                }

                Atom atom = new Atom(name, entry.x, entry.y, entry.z);
                atom.setProperty(AtomProperty.FIXED_X, toFix);
                atom.setProperty(AtomProperty.FIXED_Y, toFix);
                atom.setProperty(AtomProperty.FIXED_Z, toFix);

                cell.addAtom(atom);
            }
        }

        cell.restartResolving();
        return true;
    }

    private boolean setupSlabAtoms(SlabModel slabModel) {
        if (slabModel == null) {
            return false;
        }

        // check status
        boolean sameCondition = true;
        boolean vacuumOnlyChanged = true;

        if (this.codeSlab == null || !this.codeSlab.equals(this.codeAuxi)) {
            sameCondition = false;
            vacuumOnlyChanged = false;
        }

        if (this.currVacuum == null || Math.abs(slabModel.vacuum - this.currVacuum) > VALUE_THR) {
            sameCondition = false;
        }

        if (this.currScaleA == null || slabModel.scaleA != this.currScaleA.intValue()) {
            sameCondition = false;
            vacuumOnlyChanged = false;
        }

        if (this.currScaleB == null || slabModel.scaleB != this.currScaleB.intValue()) {
            sameCondition = false;
            vacuumOnlyChanged = false;
        }

        if (sameCondition) {
            return true;
        }

        this.codeSlab = null;
        this.currVacuum = null;
        this.currScaleA = null;
        this.currScaleB = null;

        // lattice
        if (this.lattUnit == null) {
            return false;
        }
        if (this.lattAuxi == null || this.lattAuxi.length < 3) {
            return false;
        }
        if (this.lattAuxi[2] == null || this.lattAuxi[2].length < 3) {
            return false;
        }

        double[][] lattSlab2 = null;
        if (vacuumOnlyChanged) {
            lattSlab2 = this.lattSlab == null ? null : Matrix3D.copy(this.lattSlab);
        }

        this.lattSlab = Matrix3D.copy(this.lattUnit);
        if (this.lattSlab == null || this.lattSlab.length < 3) {
            return false;
        }

        int aScale = Math.max(1, slabModel.scaleA);
        this.lattSlab[0] = Matrix3D.mult((double) aScale, this.lattSlab[0]);
        if (this.lattSlab[0] == null || lattSlab[0].length < 3) {
            return false;
        }

        int bScale = Math.max(1, slabModel.scaleB);
        this.lattSlab[1] = Matrix3D.mult((double) bScale, this.lattSlab[1]);
        if (this.lattSlab[1] == null || lattSlab[1].length < 3) {
            return false;
        }

        double zSlab = this.lattSlab[2][2];
        double zTotal = this.lattAuxi[2][2] + 2.0 * Math.max(0.0, slabModel.vacuum);
        double zScale = zSlab == 0.0 ? 1.0 : (zTotal / zSlab);
        this.lattSlab[2] = Matrix3D.mult(zScale, this.lattSlab[2]);
        if (this.lattSlab[2] == null || lattSlab[2].length < 3) {
            return false;
        }

        // atoms
        if (vacuumOnlyChanged) {
            // shift atoms
            if (this.entrySlab == null || this.entrySlab.isEmpty()) {
                return false;
            }

            if (lattSlab2 == null || lattSlab2.length < 3) {
                return false;
            }
            if (lattSlab2[2] == null || lattSlab2[2].length < 3) {
                return false;
            }

            double tx = 0.5 * (this.lattSlab[2][0] - lattSlab2[2][0]);
            double ty = 0.5 * (this.lattSlab[2][1] - lattSlab2[2][1]);
            double tz = 0.5 * (this.lattSlab[2][2] - lattSlab2[2][2]);

            for (AtomEntry entry : this.entrySlab) {
                if (entry == null) {
                    continue;
                }

                entry.x += tx;
                entry.y += ty;
                entry.z += tz;
            }

        } else {
            // create atoms
            int natom = this.entryAuxi.size();
            if (this.entryAuxi == null || natom < 1) {
                return false;
            }

            if ((aScale * bScale * natom) >= ModelerBase.maxNumAtoms()) {
                return false;
            }

            this.entrySlab = new ArrayList<AtomEntry>();

            double tx = 0.5 * (this.lattSlab[2][0] - this.lattAuxi[2][0]);
            double ty = 0.5 * (this.lattSlab[2][1] - this.lattAuxi[2][1]);
            double tz = 0.5 * (this.lattSlab[2][2] - this.lattAuxi[2][2]);

            for (int ia = 0; ia < aScale; ia++) {
                double ra = ((double) ia) / ((double) aScale);
                for (int ib = 0; ib < bScale; ib++) {
                    double rb = ((double) ib) / ((double) bScale);

                    double vx = ra * this.lattSlab[0][0] + rb * this.lattSlab[1][0];
                    double vy = ra * this.lattSlab[0][1] + rb * this.lattSlab[1][1];

                    for (AtomEntry entry : this.entryAuxi) {
                        if (entry == null) {
                            continue;
                        }

                        AtomEntry entry2 = new AtomEntry(this.lattSlab);
                        entry2.name = entry.name;
                        entry2.x = entry.x + tx + vx;
                        entry2.y = entry.y + ty + vy;
                        entry2.z = entry.z + tz;

                        this.entrySlab.add(entry2);
                    }
                }
            }
        }

        // store status
        this.codeSlab = this.codeAuxi;
        this.currVacuum = slabModel.vacuum;
        this.currScaleA = slabModel.scaleA;
        this.currScaleB = slabModel.scaleB;

        return true;
    }

    private boolean setupAuxiAtoms(SlabModel slabModel) {
        if (slabModel == null) {
            return false;
        }

        // check status
        boolean sameCondition = true;

        if (this.currOffset == null || Math.abs(slabModel.offset - this.currOffset) > VALUE_THR) {
            sameCondition = false;
        }

        if (this.currThickness == null || Math.abs(slabModel.thickness - this.currThickness) > VALUE_THR) {
            sameCondition = false;
        }

        if (sameCondition) {
            return true;
        }

        this.codeAuxi = null;
        this.currOffset = null;
        this.currThickness = null;

        // init lattice
        if (this.lattUnit == null) {
            return false;
        }

        this.lattAuxi = Matrix3D.copy(this.lattUnit);
        if (this.lattAuxi == null || this.lattAuxi.length < 3) {
            return false;
        }
        if (this.lattAuxi[0] == null || this.lattAuxi[0].length < 3) {
            return false;
        }
        if (this.lattAuxi[1] == null || this.lattAuxi[1].length < 3) {
            return false;
        }
        if (this.lattAuxi[2] == null || this.lattAuxi[2].length < 3) {
            return false;
        }

        // init atoms
        if (this.entryUnit == null || this.entryUnit.isEmpty()) {
            return false;
        }

        double cOffset = slabModel.offset - Math.floor(slabModel.offset);
        double cThick = Math.max(0.0, slabModel.thickness);

        for (int istep = 0; istep < MAX_FOR_CTHICK; istep++) {

            boolean hasOffset = this.lattAuxi[2][2] * Math.abs(Math.min(cOffset, 1.0 - cOffset)) > OFFSET_THR;
            boolean hasThick = this.lattAuxi[2][2] * Math.abs(cThick - 1.0) > THICK_THR;
            if (hasOffset || hasThick) {
                this.entryAuxi = new ArrayList<AtomEntry>();
            } else {
                this.entryAuxi = this.entryUnit;
            }

            int nThick = (int) (Math.ceil(cThick) + 0.1);

            for (int iThick = 0; iThick < nThick; iThick++) {
                List<AtomEntry> entryBuffer = null;
                Map<String, Double> stoixBuffer = null;
                if (iThick == (nThick - 1)) {
                    entryBuffer = new ArrayList<AtomEntry>();
                    stoixBuffer = new HashMap<String, Double>();
                }

                for (boolean phase : new boolean[] { true, false }) {

                    for (AtomEntry entry : this.entryUnit) {
                        if (entry == null) {
                            continue;
                        }

                        String name = entry.name;
                        if (name == null || name.isEmpty()) {
                            continue;
                        }

                        double a = entry.a;
                        double b = entry.b;
                        double c = entry.c + cOffset;
                        double c_ = c;
                        c -= Math.floor(c);

                        double dc = Math.abs(c - 1.0);
                        double dz = dc * this.lattAuxi[2][2];
                        if (dz < POSIT_THR) {
                            c -= 1.0;
                        }

                        double zshift = Math.abs(c - c_) * this.lattAuxi[2][2];
                        if (zshift < POSIT_THR) {
                            if (!phase) {
                                continue;
                            }
                        } else {
                            if (phase) {
                                continue;
                            }
                        }

                        c -= (double) iThick;

                        dc = c - (1.0 - cThick);
                        dz = dc * this.lattAuxi[2][2];
                        if (dz < (-2.0 * POSIT_THR)) {
                            continue;
                        }

                        AtomEntry entry2 = null;
                        if (this.entryAuxi != this.entryUnit) {
                            entry2 = new AtomEntry(this.lattAuxi);
                        } else {
                            entry2 = entry;
                        }

                        entry2.name = name;
                        entry2.x = a * this.lattAuxi[0][0] + b * this.lattAuxi[1][0] + c * this.lattAuxi[2][0];
                        entry2.y = a * this.lattAuxi[0][1] + b * this.lattAuxi[1][1] + c * this.lattAuxi[2][1];
                        entry2.z = a * this.lattAuxi[0][2] + b * this.lattAuxi[1][2] + c * this.lattAuxi[2][2];

                        if (this.entryAuxi != this.entryUnit) {
                            if (iThick < (nThick - 1)) {
                                this.entryAuxi.add(entry2);

                            } else {
                                entryBuffer.add(entry2);

                                stoixBuffer.put(STOIX_NATOM, (double) entryBuffer.size());
                                if (stoixBuffer.containsKey(name)) {
                                    double value = stoixBuffer.get(name);
                                    stoixBuffer.put(name, value + 1.0);
                                } else {
                                    stoixBuffer.put(name, 1.0);
                                }

                                if (this.stoixUnit != null && this.equalsStoichiometry(this.stoixUnit, stoixBuffer)) {
                                    this.entryAuxi.addAll(entryBuffer);
                                    entryBuffer.clear();
                                    stoixBuffer.clear();
                                }
                            }
                        }
                    }
                }
            }

            if (!this.entryAuxi.isEmpty()) {
                break;
            }

            cThick += STEP_FOR_CTHICK;
        }

        if (this.entryAuxi.size() >= ModelerBase.maxNumAtoms()) {
            return false;
        }

        // modify lattice
        double zMax = 0.0;
        double zMin = 0.0;
        boolean zFirst = true;
        for (AtomEntry entry : this.entryAuxi) {
            if (entry == null) {
                continue;
            }

            if (zFirst) {
                zFirst = false;
                zMax = entry.z;
                zMin = entry.z;
            } else {
                zMax = Math.max(zMax, entry.z);
                zMin = Math.min(zMin, entry.z);
            }
        }

        double zSlab = this.lattAuxi[2][2];
        double zDelta = Math.max(zMax - zMin, 0.0);
        double zScale = zSlab == 0.0 ? 1.0 : (zDelta / zSlab);
        this.lattAuxi[2] = Matrix3D.mult(zScale, this.lattAuxi[2]);
        if (this.lattAuxi[2] == null || lattAuxi[2].length < 3) {
            return false;
        }

        // modify atoms
        double xMin = 0.0;
        double yMin = 0.0;
        if (this.lattAuxi[2][2] > THICK_THR) {
            xMin = zMin * this.lattAuxi[2][0] / this.lattAuxi[2][2];
            yMin = zMin * this.lattAuxi[2][1] / this.lattAuxi[2][2];
        }

        for (AtomEntry entry : this.entryAuxi) {
            if (entry == null) {
                continue;
            }

            entry.x -= xMin;
            entry.y -= yMin;
            entry.z -= zMin;
        }

        // store status
        UUID uuid = UUID.randomUUID();
        if (uuid != null) {
            this.codeAuxi = uuid.toString();
        }

        this.currOffset = slabModel.offset;
        this.currThickness = slabModel.thickness;

        return true;
    }

    private boolean setupUnitAtomsInCell(Cell cell) {
        if (cell == null) {
            return false;
        }

        Atom[] atoms = cell.listAtoms(true);
        if (atoms == null || atoms.length < 1) {
            return false;
        }

        this.entryUnit = new ArrayList<AtomEntry>();

        double[][] lattice = cell.copyLattice();

        for (Atom atom : atoms) {
            if (atom == null) {
                return false;
            }

            String name = atom.getName();
            if (name == null || name.isEmpty()) {
                return false;
            }

            double x = atom.getX();
            double y = atom.getY();
            double z = atom.getZ();
            double[] coord = cell.convertToLatticePosition(x, y, z);
            if (coord == null || coord.length < 3) {
                return false;
            }

            if (lattice != null) {
                AtomEntry entry = new AtomEntry(lattice);
                entry.name = name;
                entry.a = coord[0];
                entry.b = coord[1];
                entry.c = coord[2];
                this.entryUnit.add(entry);
            }
        }

        return true;
    }

    private boolean setupUnitAtomsInSlab() {
        List<AtomEntry> entryUnit_ = this.entryUnit;
        int natom = entryUnit_.size();
        if (natom < 1) {
            return false;
        }

        double[][] lattInt = new double[3][];
        lattInt[0] = new double[] { (double) this.vector1[0], (double) this.vector1[1], (double) this.vector1[2] };
        lattInt[1] = new double[] { (double) this.vector2[0], (double) this.vector2[1], (double) this.vector2[2] };
        lattInt[2] = new double[] { (double) this.vector3[0], (double) this.vector3[1], (double) this.vector3[2] };

        double detLatt = Matrix3D.determinant(lattInt);
        if (Math.abs(detLatt) < DET_THR) {
            System.err.println("volume is zero.");
            return false;
        }
        if (detLatt < 0.0) {
            System.err.println("parity is incorrect.");
            return false;
        }

        double detLatt2 = Math.rint(detLatt);
        if (Math.abs(detLatt - detLatt2) >= DET_THR) {
            System.err.println("not integer volume of lattice.");
            return false;
        }

        int nsize = (int) (detLatt2 + 0.1);
        if ((nsize * natom) >= ModelerBase.maxNumAtoms()) {
            return false;
        }

        double[][] invLatt = Matrix3D.inverse(lattInt);
        if (invLatt == null || invLatt.length < 3) {
            return false;
        }
        if (invLatt[0] == null || invLatt[0].length < 3) {
            return false;
        }
        if (invLatt[1] == null || invLatt[1].length < 3) {
            return false;
        }
        if (invLatt[2] == null || invLatt[2].length < 3) {
            return false;
        }

        this.entryUnit = new ArrayList<AtomEntry>();

        for (int ia = this.boundBox[0][0]; ia <= this.boundBox[0][1]; ia++) {
            double ta = (double) ia;
            for (int ib = this.boundBox[1][0]; ib <= this.boundBox[1][1]; ib++) {
                double tb = (double) ib;
                for (int ic = this.boundBox[2][0]; ic <= this.boundBox[2][1]; ic++) {
                    double tc = (double) ic;

                    for (AtomEntry entry : entryUnit_) {
                        double a = entry.a + ta;
                        double b = entry.b + tb;
                        double c = entry.c + tc;
                        double a2 = a * invLatt[0][0] + b * invLatt[1][0] + c * invLatt[2][0];
                        double b2 = a * invLatt[0][1] + b * invLatt[1][1] + c * invLatt[2][1];
                        double c2 = a * invLatt[0][2] + b * invLatt[1][2] + c * invLatt[2][2];

                        if (-PACK_THR <= a2 && a2 < (1.0 + PACK_THR) &&
                                -PACK_THR <= b2 && b2 < (1.0 + PACK_THR) &&
                                -PACK_THR <= c2 && c2 < (1.0 + PACK_THR)) {

                            AtomEntry entry2 = new AtomEntry(this.lattUnit);
                            entry2.name = entry.name;
                            entry2.a = a2;
                            entry2.b = b2;
                            entry2.c = c2;
                            if (!this.entryUnit.contains(entry2)) {
                                this.entryUnit.add(entry2);
                            }
                        }
                    }
                }
            }
        }

        if (this.entryUnit.isEmpty()) {
            return false;
        }

        for (AtomEntry entry : this.entryUnit) {
            entry.a -= Math.floor(entry.a);
            entry.b -= Math.floor(entry.b);
            entry.c -= Math.floor(entry.c);

            double dc = Math.abs(entry.c - 1.0);
            double dz = dc * this.lattUnit[2][2];
            if (dz < POSIT_THR) {
                entry.c -= 1.0;
            }
        }

        Collections.sort(this.entryUnit);

        // keep stoichiometry
        this.stoixUnit = new HashMap<String, Double>();
        if (!this.setupStoichiometry(this.entryUnit, this.stoixUnit)) {
            return false;
        }

        return true;
    }

    private boolean setupStoichiometry(List<AtomEntry> entryList, Map<String, Double> stoixMap) {
        if (entryList == null || entryList.isEmpty()) {
            return false;
        }

        if (stoixMap == null) {
            return false;
        }
        if (!stoixMap.isEmpty()) {
            stoixMap.clear();
        }

        int natom = 0;

        for (AtomEntry entry : entryList) {
            String name = entry == null ? null : entry.name;
            if (name == null || name.isEmpty()) {
                continue;
            }

            natom++;

            if (stoixMap.containsKey(name)) {
                double value = stoixMap.get(name);
                stoixMap.put(name, value + 1.0);
            } else {
                stoixMap.put(name, 1.0);
            }
        }

        if (natom < 1) {
            return false;
        }

        stoixMap.put(STOIX_NATOM, (double) natom);

        return true;
    }

    private boolean equalsStoichiometry(Map<String, Double> stoixMap1, Map<String, Double> stoixMap2) {
        if (stoixMap1 == null || stoixMap1.isEmpty()) {
            return false;
        }

        if (stoixMap2 == null || stoixMap2.isEmpty()) {
            return false;
        }

        Set<String> names1 = stoixMap1.keySet();
        if (names1 == null) {
            return false;
        }

        Set<String> names2 = stoixMap2.keySet();
        if (names2 == null) {
            return false;
        }

        if (names1.size() != names2.size()) {
            return false;
        }

        double natom1 = 0.0;
        if (stoixMap1.containsKey(STOIX_NATOM)) {
            natom1 = stoixMap1.get(STOIX_NATOM);
        }

        if (natom1 <= 0.0) {
            return false;
        }

        double natom2 = 0.0;
        if (stoixMap2.containsKey(STOIX_NATOM)) {
            natom2 = stoixMap2.get(STOIX_NATOM);
        }

        if (natom2 <= 0.0) {
            return false;
        }

        for (String name : names1) {
            if (!stoixMap2.containsKey(name)) {
                return false;
            }

            double value1 = stoixMap1.get(name) / natom1;
            double value2 = stoixMap2.get(name) / natom2;
            if (Math.abs(value1 - value2) > STOIX_THR) {
                return false;
            }
        }

        return true;
    }

    private boolean setupMillers(Cell cell, int h, int k, int l) {
        if (cell == null) {
            return false;
        }

        if (h == 0 && k == 0 && l == 0) {
            return false;
        }

        this.miller1 = h;
        this.miller2 = k;
        this.miller3 = l;

        if (!this.setupIntercepts()) {
            return false;
        }

        if (!this.setupVectors()) {
            return false;
        }

        if (!this.setupBoundaryBox()) {
            return false;
        }

        if (!this.setupLattice(cell)) {
            return false;
        }

        return true;
    }

    private boolean setupIntercepts() {
        int scaleMin = 1;
        int scaleMax = 1;
        this.numIntercept = 0;

        if (this.miller1 != 0) {
            scaleMin = Math.max(scaleMin, Math.abs(this.miller1));
            scaleMax *= Math.abs(this.miller1);
            this.numIntercept++;
            this.hasIntercept1 = true;
        } else {
            this.hasIntercept1 = false;
        }

        if (this.miller2 != 0) {
            scaleMin = Math.max(scaleMin, Math.abs(this.miller2));
            scaleMax *= Math.abs(this.miller2);
            this.numIntercept++;
            this.hasIntercept2 = true;
        } else {
            this.hasIntercept2 = false;
        }

        if (this.miller3 != 0) {
            scaleMin = Math.max(scaleMin, Math.abs(this.miller3));
            scaleMax *= Math.abs(this.miller3);
            this.numIntercept++;
            this.hasIntercept3 = true;
        } else {
            this.hasIntercept3 = false;
        }

        if (scaleMin < 1) {
            System.err.println("scaleMin is not positive.");
            return false;
        }

        if (scaleMax < scaleMin) {
            System.err.println("scaleMax < scaleMin.");
            return false;
        }

        if (this.numIntercept < 1) {
            System.err.println("there are no intercepts.");
            return false;
        }

        int scale = 0;
        for (int i = scaleMin; i <= scaleMax; i++) {
            if (this.hasIntercept1 && (i % this.miller1) != 0) {
                continue;
            }
            if (this.hasIntercept2 && (i % this.miller2) != 0) {
                continue;
            }
            if (this.hasIntercept3 && (i % this.miller3) != 0) {
                continue;
            }

            scale = i;
            break;
        }

        if (scale < 1) {
            System.err.println("cannot detect scale.");
            return false;
        }

        this.intercept1 = this.hasIntercept1 ? (scale / this.miller1) : 0;
        this.intercept2 = this.hasIntercept2 ? (scale / this.miller2) : 0;
        this.intercept3 = this.hasIntercept3 ? (scale / this.miller3) : 0;

        return true;
    }

    private boolean setupVectors() {
        this.vector1 = new int[] { 0, 0, 0 };
        this.vector2 = new int[] { 0, 0, 0 };
        this.vector3 = new int[] { 0, 0, 0 };

        if (this.numIntercept <= 1) {
            this.setupVectors1();
        } else if (this.numIntercept <= 2) {
            this.setupVectors2();
        } else {
            this.setupVectors3();
        }

        return true;
    }

    private void setupVectors1() {
        if (this.hasIntercept1) {
            if (this.intercept1 > 0) {
                this.vector1[1] = 1;
                this.vector2[2] = 1;
                this.vector3[0] = 1;
            } else {
                this.vector1[2] = 1;
                this.vector2[1] = 1;
                this.vector3[0] = -1;
            }

        } else if (this.hasIntercept2) {
            if (this.intercept2 > 0) {
                this.vector1[2] = 1;
                this.vector2[0] = 1;
                this.vector3[1] = 1;
            } else {
                this.vector1[0] = 1;
                this.vector2[2] = 1;
                this.vector3[1] = -1;
            }

        } else if (this.hasIntercept3) {
            if (this.intercept3 > 0) {
                this.vector1[0] = 1;
                this.vector2[1] = 1;
                this.vector3[2] = 1;
            } else {
                this.vector1[1] = 1;
                this.vector2[0] = 1;
                this.vector3[2] = -1;
            }
        }
    }

    private void setupVectors2() {
        if (!this.hasIntercept3) { // cat in A-B plane
            int sign1 = Integer.signum(this.intercept1);
            int sign2 = Integer.signum(this.intercept2);
            this.vector1[2] = sign1 * sign2;
            this.vector2[0] = +this.intercept1;
            this.vector2[1] = -this.intercept2;
            this.vector3[0] = sign1;
            this.vector3[1] = sign2;

        } else if (!this.hasIntercept2) { // cat in A-C plane
            int sign1 = Integer.signum(this.intercept1);
            int sign3 = Integer.signum(this.intercept3);
            this.vector1[1] = sign1 * sign3;
            this.vector2[0] = -this.intercept1;
            this.vector2[2] = +this.intercept3;
            this.vector3[0] = sign1;
            this.vector3[2] = sign3;

        } else if (!this.hasIntercept1) { // cat in B-C plane
            int sign2 = Integer.signum(this.intercept2);
            int sign3 = Integer.signum(this.intercept3);
            this.vector1[0] = sign2 * sign3;
            this.vector2[1] = +this.intercept2;
            this.vector2[2] = -this.intercept3;
            this.vector3[1] = sign2;
            this.vector3[2] = sign3;
        }
    }

    private void setupVectors3() {
        int sign1 = Integer.signum(this.intercept1);
        int sign2 = Integer.signum(this.intercept2);
        int sign3 = Integer.signum(this.intercept3);
        if (sign3 > 0) {
            this.vector1[1] = +sign1 * this.intercept2;
            this.vector1[2] = -sign1 * this.intercept3;
            this.vector2[0] = -sign2 * this.intercept1;
            this.vector2[2] = +sign2 * this.intercept3;
        } else {
            this.vector1[0] = -sign1 * this.intercept1;
            this.vector1[2] = +sign1 * this.intercept3;
            this.vector2[1] = +sign2 * this.intercept2;
            this.vector2[2] = -sign2 * this.intercept3;
        }
        this.vector3[0] = sign1;
        this.vector3[1] = sign2;
        this.vector3[2] = sign3;
    }

    private boolean setupBoundaryBox() {
        this.boundBox = new int[3][2];

        for (int i = 0; i < 3; i++) {
            this.boundBox[i][0] = 0;
            this.boundBox[i][1] = 0;

            if (this.vector1[i] < 0) {
                this.boundBox[i][0] += this.vector1[i];
            } else {
                this.boundBox[i][1] += this.vector1[i];
            }

            if (this.vector2[i] < 0) {
                this.boundBox[i][0] += this.vector2[i];
            } else {
                this.boundBox[i][1] += this.vector2[i];
            }

            if (this.vector3[i] < 0) {
                this.boundBox[i][0] += this.vector3[i];
            } else {
                this.boundBox[i][1] += this.vector3[i];
            }
        }

        return true;
    }

    private boolean setupLattice(Cell cell) {
        if (cell == null) {
            return false;
        }

        double[][] lattInt = new double[3][];
        lattInt[0] = new double[] { (double) this.vector1[0], (double) this.vector1[1], (double) this.vector1[2] };
        lattInt[1] = new double[] { (double) this.vector2[0], (double) this.vector2[1], (double) this.vector2[2] };
        lattInt[2] = new double[] { (double) this.vector3[0], (double) this.vector3[1], (double) this.vector3[2] };

        double[][] lattUnit0 = Matrix3D.mult(lattInt, cell.copyLattice());
        this.lattConst = lattUnit0 == null ? null : Lattice.getCellDm(14, lattUnit0);
        if (this.lattConst == null || this.lattConst.length < 6) {
            return false;
        }

        this.lattUnit = Lattice.getCell(14, this.lattConst);
        if (this.lattUnit == null || this.lattUnit.length < 3) {
            return false;
        }
        if (this.lattUnit[0] == null || this.lattUnit[0].length < 3) {
            return false;
        }
        if (this.lattUnit[1] == null || this.lattUnit[1].length < 3) {
            return false;
        }
        if (this.lattUnit[2] == null || this.lattUnit[2].length < 3) {
            return false;
        }

        return true;
    }

    private static class AtomEntry implements Comparable<AtomEntry> {
        private double[][] lattice;

        public String name;
        public double a;
        public double b;
        public double c;
        public double x;
        public double y;
        public double z;

        public AtomEntry(double[][] lattice) {
            if (lattice == null) {
                throw new IllegalArgumentException("lattice is null.");
            }

            this.lattice = lattice;
            this.name = null;
            this.a = 0.0;
            this.b = 0.0;
            this.c = 0.0;
            this.x = 0.0;
            this.y = 0.0;
            this.z = 0.0;
        }

        @Override
        public int compareTo(AtomEntry other) {
            return compareToStatic(this, other);
        }

        private static int compareToStatic(AtomEntry entry1, AtomEntry entry2) {
            if (entry2 == null) {
                return -1;
            }

            double dx;
            double dy;
            double dz;
            double rr;

            double dc = entry1.c - entry2.c;
            dx = dc * entry1.lattice[2][0];
            dy = dc * entry1.lattice[2][1];
            dz = dc * entry1.lattice[2][2];
            rr = dx * dx + dy * dy + dz * dz;
            if (rr > POSIT_THR * POSIT_THR) {
                if (dc > 0.0) {
                    return -1;
                } else {
                    return 1;
                }
            }

            double db = entry1.b - entry2.b;
            dx = db * entry1.lattice[1][0];
            dy = db * entry1.lattice[1][1];
            dz = db * entry1.lattice[1][2];
            rr = dx * dx + dy * dy + dz * dz;
            if (rr > POSIT_THR * POSIT_THR) {
                if (db > 0.0) {
                    return 1;
                } else {
                    return -1;
                }
            }

            double da = entry1.a - entry2.a;
            dx = da * entry1.lattice[0][0];
            dy = da * entry1.lattice[0][1];
            dz = da * entry1.lattice[0][2];
            rr = dx * dx + dy * dy + dz * dz;
            if (rr > POSIT_THR * POSIT_THR) {
                if (da > 0.0) {
                    return 1;
                } else {
                    return -1;
                }
            }

            if (entry1.name == null) {
                if (entry2.name == null) {
                    return 0;
                } else {
                    return 1;
                }
            }

            return entry1.name.compareTo(entry2.name);
        }

        @Override
        public int hashCode() {
            return this.name == null ? 0 : this.name.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return equalsStatic(this, obj);
        }

        private static boolean equalsStatic(AtomEntry entry, Object obj) {
            if (entry == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (entry.getClass() != obj.getClass()) {
                return false;
            }

            AtomEntry other = (AtomEntry) obj;
            if (entry.name == null) {
                if (entry.name != other.name) {
                    return false;
                }
            } else {
                if (!entry.name.equals(other.name)) {
                    return false;
                }
            }

            double da = Math.abs(entry.a - other.a);
            da = Math.min(da, Math.abs(entry.a - other.a + Math.signum(0.5 - entry.a)));

            double db = Math.abs(entry.b - other.b);
            db = Math.min(db, Math.abs(entry.b - other.b + Math.signum(0.5 - entry.b)));

            double dc = Math.abs(entry.c - other.c);
            dc = Math.min(dc, Math.abs(entry.c - other.c + Math.signum(0.5 - entry.c)));

            double dx = da * entry.lattice[0][0] + db * entry.lattice[1][0] + dc * entry.lattice[2][0];
            double dy = da * entry.lattice[0][1] + db * entry.lattice[1][1] + dc * entry.lattice[2][1];
            double dz = da * entry.lattice[0][2] + db * entry.lattice[1][2] + dc * entry.lattice[2][2];
            double rr = dx * dx + dy * dy + dz * dz;
            if (rr > POSIT_THR * POSIT_THR) {
                return false;
            }

            return true;
        }
    }
}
