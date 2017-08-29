/*
 * Copyright (C) 2017 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.viewer;

import java.util.ArrayList;
import java.util.List;

import burai.atoms.design.Design;
import burai.atoms.model.Atom;
import burai.atoms.model.Cell;
import burai.atoms.model.property.CellProperty;
import burai.com.math.Lattice;

public class InitialOperations {

    private InitialOperations() {
        // NOP
    }

    public static List<double[]> getOperations(Cell cell) {
        return getOperations(cell, null);
    }

    public static List<double[]> getOperations(Cell cell, Design design) {
        if (cell == null) {
            return null;
        }

        List<double[]> operations = new ArrayList<double[]>();

        // get length of lattice
        double[][] lattice = cell.copyLattice();
        if (lattice == null || lattice.length < 3) {
            return null;
        }
        for (int i = 0; i < 3; i++) {
            if (lattice[i] == null || lattice[i].length < 3) {
                return null;
            }
        }

        double xLatt = Lattice.getXMax(lattice) - Lattice.getXMin(lattice);
        if (xLatt <= 0.0) {
            return null;
        }

        double yLatt = Lattice.getYMax(lattice) - Lattice.getYMin(lattice);
        if (yLatt <= 0.0) {
            return null;
        }

        double zLatt = Lattice.getZMax(lattice) - Lattice.getZMin(lattice);
        if (zLatt <= 0.0) {
            return null;
        }

        double xBox = xLatt;
        double yBox = yLatt;
        double zBox = zLatt;

        // is this molecule ?
        boolean isMolecule = false;
        if (design != null) {
            isMolecule = !design.isShowingCell();

        } else {
            if (cell.hasProperty(CellProperty.MOLECULE)) {
                isMolecule = cell.booleanProperty(CellProperty.MOLECULE);
            }
        }

        if (isMolecule) {
            Atom[] atoms = cell.listAtoms();
            if (atoms != null && atoms.length > 0) {
                // scale molecule
                Atom atom = atoms[0];
                double r = atom == null ? 1.0 : Math.max(1.0, atom.getRadius());
                double x = atom == null ? 0.0 : atom.getX();
                double y = atom == null ? 0.0 : atom.getY();
                double z = atom == null ? 0.0 : atom.getZ();
                double xMax = x + r;
                double yMax = y + r;
                double zMax = z + r;
                double xMin = x - r;
                double yMin = y - r;
                double zMin = z - r;

                for (int i = 1; i < atoms.length; i++) {
                    atom = atoms[i];
                    if (atom == null) {
                        continue;
                    }

                    r = Math.max(1.0, atom.getRadius());
                    x = atom.getX();
                    y = atom.getY();
                    z = atom.getZ();
                    xMax = Math.max(xMax, x + r);
                    yMax = Math.max(yMax, y + r);
                    zMax = Math.max(zMax, z + r);
                    xMin = Math.min(xMin, x - r);
                    yMin = Math.min(yMin, y - r);
                    zMin = Math.min(zMin, z - r);
                }

                double xMole = xMax - xMin;
                double yMole = yMax - yMin;
                double zMole = zMax - zMin;

                xBox = xMole;
                yBox = yMole;
                zBox = zMole;

                double rLatt = Math.max(Math.max(xLatt, yLatt), zLatt);
                double rMole = Math.max(Math.max(xMole, yMole), zMole);
                double scale = Math.max(1.0, rLatt / rMole);
                operations.add(new double[] { scale });
            }
        }

        // has specified axis ?
        String axis = null;
        if (cell.hasProperty(CellProperty.AXIS)) {
            axis = cell.stringProperty(CellProperty.AXIS);
        }

        if ("x".equalsIgnoreCase(axis)) {
            xBox = Double.MAX_VALUE;
        } else if ("y".equalsIgnoreCase(axis)) {
            yBox = Double.MAX_VALUE;
        } else if ("z".equalsIgnoreCase(axis)) {
            zBox = Double.MAX_VALUE;
        }

        // rotate axis
        if (yBox >= xBox && yBox >= zBox) {
            if (xBox >= zBox) {
                // y > x > z
                // NOP
            } else {
                // y > z > x
                operations.add(new double[] { -90.0, 0.0, 1.0, 0.0 });
            }

        } else if (zBox >= xBox && zBox >= yBox) {
            if (xBox >= yBox) {
                // z > x > y
                operations.add(new double[] { -90.0, 1.0, 0.0, 0.0 });
            } else {
                // z > y > x
                operations.add(new double[] { -90.0, 1.0, 0.0, 0.0 });
                operations.add(new double[] { 90.0, 0.0, 1.0, 0.0 });
            }

        } else if (xBox >= yBox && xBox >= zBox) {
            if (yBox >= zBox) {
                // x > y > z
                operations.add(new double[] { 90.0, 0.0, 0.0, 1.0 });
                operations.add(new double[] { 180.0, 1.0, 0.0, 0.0 });
            } else {
                // x > z > y
                operations.add(new double[] { 90.0, 0.0, 0.0, 1.0 });
                operations.add(new double[] { 180.0, 1.0, 0.0, 0.0 });
                operations.add(new double[] { 90.0, 0.0, 1.0, 0.0 });
            }
        }

        return operations;
    }
}
