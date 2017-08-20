/*
 * Copyright (C) 2017 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import burai.atoms.element.ElementUtil;
import burai.atoms.model.Atom;
import burai.atoms.model.Cell;
import burai.atoms.model.exception.ZeroVolumCellException;

public class XSFReader extends AtomsReader {

    private static final double EDGE_OF_MOLECULE = 5.0;

    private boolean animation;

    public XSFReader(String filePath, boolean animation) throws FileNotFoundException {
        super(filePath);
        this.animation = animation;
    }

    public XSFReader(File file, boolean animation) throws FileNotFoundException {
        super(file);
        this.animation = animation;
    }

    @Override
    public Cell readCell() throws IOException {
        if (this.reader == null) {
            return null;
        }

        if (this.animation) {
            return this.readAnimationCell();

        } else {
            return this.readSingleCell();
        }
    }

    private Cell readAnimationCell() throws IOException {

        // TODO

        return null;
    }

    private Cell readSingleCell() throws IOException {
        /*
         * read type of system
         */
        String sysType = this.readNetLine();

        if ("ATOMS".equalsIgnoreCase(sysType)) {
            return this.readMolecularStructure();

        } else if ("CRYSTAL".equalsIgnoreCase(sysType)
                || "SLAB".equalsIgnoreCase(sysType)
                || "POLYMER".equalsIgnoreCase(sysType)
                || "MOLECULE".equalsIgnoreCase(sysType)) {
            return this.readPeriodicStructure();

        } else {
            throw new IOException("incorrect type of system in reading a XSF file.");
        }
    }

    private Cell readMolecularStructure() throws IOException {
        List<String> elems = new ArrayList<>();
        List<double[]> coords = new ArrayList<>();

        /*
         * read atoms
         */
        while (this.readAndAddAtom(elems, coords)) {
            // NOP
        }

        if (elems.isEmpty() || coords.isEmpty()) {
            throw new IOException("no atoms in a XSF file.");
        }

        int numAtoms = elems.size();
        if (numAtoms != coords.size()) {
            throw new IOException("incorrect atoms in reading a XSF file.");
        }

        /*
         * create lattice vectors
         */
        double[][] lattice = this.createLatticeAroundMolecule(coords);

        /*
         * create an instance of Cell
         */
        Cell cell = null;
        try {
            cell = new Cell(lattice);
        } catch (ZeroVolumCellException e) {
            throw new IOException(e);
        }

        cell.stopResolving();

        for (int i = 0; i < numAtoms; i++) {
            String elem = elems.get(i);
            double[] coord = coords.get(i);
            cell.addAtom(new Atom(elem, coord[0], coord[1], coord[2]));
        }

        cell.restartResolving();

        return cell;
    }

    private double[][] createLatticeAroundMolecule(List<double[]> coords) {
        double[][] lattice = new double[3][3];
        lattice[0][0] = 2.0 * EDGE_OF_MOLECULE;
        lattice[0][1] = 0.0;
        lattice[0][2] = 0.0;
        lattice[1][0] = 0.0;
        lattice[1][1] = 2.0 * EDGE_OF_MOLECULE;
        lattice[1][2] = 0.0;
        lattice[2][0] = 0.0;
        lattice[2][1] = 0.0;
        lattice[2][2] = 2.0 * EDGE_OF_MOLECULE;

        if (coords == null || coords.isEmpty()) {
            return lattice;
        }

        int numCoords = coords.size();

        double[] coord0 = coords.get(0);
        double xMax = coord0[0];
        double xMin = coord0[0];
        double yMax = coord0[1];
        double yMin = coord0[1];
        double zMax = coord0[2];
        double zMin = coord0[2];

        for (int i = 1; i < numCoords; i++) {
            double[] coord = coords.get(i);
            xMax = Math.max(xMax, coord[0]);
            xMin = Math.min(xMin, coord[0]);
            yMax = Math.max(yMax, coord[1]);
            yMin = Math.min(yMin, coord[1]);
            zMax = Math.max(zMax, coord[2]);
            zMin = Math.min(zMin, coord[2]);
        }

        double xCenter = EDGE_OF_MOLECULE + 0.5 * (xMax - xMin);
        double yCenter = EDGE_OF_MOLECULE + 0.5 * (yMax - yMin);
        double zCenter = EDGE_OF_MOLECULE + 0.5 * (zMax - zMin);

        for (int i = 0; i < numCoords; i++) {
            double[] coord = coords.get(i);
            coord[0] += xCenter;
            coord[1] += yCenter;
            coord[2] += zCenter;
        }

        lattice[0][0] += xMax - xMin;
        lattice[1][1] += yMax - yMin;
        lattice[2][2] += zMax - zMin;
        return lattice;
    }

    private Cell readPeriodicStructure() throws IOException {
        double[][] lattice = null;
        List<String> elems = null;
        List<double[]> coords = null;

        while (lattice == null || elems == null || coords == null) {
            String tag = this.readNetLine();

            if ("PRIMVEC".equalsIgnoreCase(tag)) {
                /*
                 * read lattice vectors
                 */
                if (lattice == null) {
                    lattice = new double[3][];
                    lattice[0] = this.readDoubles(3);
                    lattice[1] = this.readDoubles(3);
                    lattice[2] = this.readDoubles(3);
                }

            } else if ("PRIMCOORD".equalsIgnoreCase(tag)) {
                /*
                 * read atoms
                 */
                if (elems == null || coords == null) {
                    if (elems == null) {
                        elems = new ArrayList<>();
                    } else {
                        elems.clear();
                    }

                    if (coords == null) {
                        coords = new ArrayList<>();
                    } else {
                        coords.clear();
                    }

                    int numAtoms = this.readInteger();
                    for (int i = 0; i < numAtoms; i++) {
                        if (!this.readAndAddAtom(elems, coords)) {
                            throw new IOException("too less atoms in a XSF file.");
                        }
                    }
                }
            }
        }

        if (elems.isEmpty() || coords.isEmpty()) {
            throw new IOException("no atoms in a XSF file.");
        }

        int numAtoms = elems.size();
        if (numAtoms != coords.size()) {
            throw new IOException("incorrect atoms in reading a XSF file.");
        }

        /*
         * create an instance of Cell
         */
        Cell cell = null;
        try {
            cell = new Cell(lattice);
        } catch (ZeroVolumCellException e) {
            throw new IOException(e);
        }

        cell.stopResolving();

        for (int i = 0; i < numAtoms; i++) {
            String elem = elems.get(i);
            double[] coord = coords.get(i);
            cell.addAtom(new Atom(elem, coord[0], coord[1], coord[2]));
        }

        cell.restartResolving();

        return cell;
    }

    private String readNetLine() throws IOException {
        return this.readNetLine(false);
    }

    private String readNetLine(boolean silent) throws IOException {
        String line = null;

        while (true) {
            line = this.reader.readLine();
            if (line == null) {
                if (silent) {
                    break;
                }

                throw new IOException("not enough lines in reading a XSF file.");
            }

            int index = line.indexOf('#');
            if (index > 0) {
                line = line.substring(0, index);
            } else if (index == 0) {
                line = "";
            }

            line = line.trim();
            if (!line.isEmpty()) {
                break;
            }
        }

        return line;
    }

    private String[] readSubLines(int size) throws IOException {
        return this.readSubLines(size, false);
    }

    private String[] readSubLines(int size, boolean silent) throws IOException {
        String line = this.readNetLine(silent);
        if (silent && line == null) {
            return null;
        }

        String[] subLines = line.split("[\\s,]+");
        if (subLines == null || subLines.length < size) {
            throw new IOException("not enough tokens in reading a XSF file.");
        }

        return subLines;
    }

    private double[] readDoubles(int size) throws IOException {
        String[] subLines = this.readSubLines(size);

        int size_ = size > 0 ? size : subLines.length;
        double[] values = new double[size_];

        try {
            for (int i = 0; i < values.length; i++) {
                values[i] = Double.parseDouble(subLines[i]);
            }
        } catch (NumberFormatException e) {
            throw new IOException(e);
        }

        return values;
    }

    private int readInteger() throws IOException {
        int[] values = this.readIntegers(1);
        return values[0];
    }

    private int[] readIntegers(int size) throws IOException {
        String[] subLines = this.readSubLines(size);

        int size_ = size > 0 ? size : subLines.length;
        int[] values = new int[size_];

        try {
            for (int i = 0; i < values.length; i++) {
                values[i] = Integer.parseInt(subLines[i]);
            }
        } catch (NumberFormatException e) {
            throw new IOException(e);
        }

        return values;
    }

    private boolean readAndAddAtom(List<String> elems, List<double[]> coords) throws IOException {
        String[] subLines = this.readSubLines(4, true);
        if (subLines == null || "ATOMS".equalsIgnoreCase(subLines[0])) {
            return false;
        }

        int ielem = -1;

        try {
            ielem = Integer.parseInt(subLines[0]);
        } catch (Exception e) {
            ielem = -1;
        }

        String elem = ielem < 1 ? null : ElementUtil.toElementName(ielem);
        if (elem == null) {
            elem = subLines[0] == null ? "X" : subLines[0];
        }

        if (elems != null) {
            elems.add(elem);
        }

        double x = 0.0;
        double y = 0.0;
        double z = 0.0;

        try {
            x = Double.parseDouble(subLines[1]);
            y = Double.parseDouble(subLines[2]);
            z = Double.parseDouble(subLines[3]);
        } catch (Exception e) {
            throw new IOException(e);
        }

        if (coords != null) {
            coords.add(new double[] { x, y, z });
        }

        return true;
    }
}
