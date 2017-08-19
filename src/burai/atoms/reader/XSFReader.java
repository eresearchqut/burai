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
import java.util.List;

import burai.atoms.element.ElementUtil;
import burai.atoms.model.Cell;

public class XSFReader extends AtomsReader {

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

        } else {

        }

        return null;
    }

    private Cell readMolecule() throws IOException {
        return null;
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

            line = line.trim();
            if (!(line.isEmpty() || line.startsWith("#"))) {
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
