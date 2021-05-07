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

package burai.atoms.reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import burai.atoms.element.ElementUtil;
import burai.atoms.model.Atom;
import burai.atoms.model.Cell;
import burai.atoms.model.exception.ZeroVolumCellException;
import burai.com.consts.Constants;

public class CubeReader extends AtomsReader {

    public CubeReader(String filePath) throws FileNotFoundException {
        super(filePath);
    }

    public CubeReader(File file) throws FileNotFoundException {
        super(file);
    }

    private double toDouble(String value) throws NumberFormatException {
        if (value == null || value.isEmpty()) {
            throw new NumberFormatException("value is empty.");
        }

        return Double.parseDouble(value.replace('d', 'e').replace('D', 'E'));
    }

    @Override
    public Cell readCell() throws IOException {
        if (this.reader == null) {
            return null;
        }

        String[] subLines = null;
        this.readNetLine();
        this.readNetLine();

        /*
         * read #atoms, origin
         */
        subLines = this.readSubLines(4);

        int numAtoms = 0;
        try {
            numAtoms = Math.abs(Integer.parseInt(subLines[0]));
        } catch (NumberFormatException e) {
            throw new IOException("cannot read #atoms from a CUBE file.");
        }

        double[] origin = new double[3];
        try {
            origin[0] = this.toDouble(subLines[1]);
            origin[1] = this.toDouble(subLines[2]);
            origin[2] = this.toDouble(subLines[3]);
        } catch (NumberFormatException e) {
            throw new IOException("cannot read origin from a CUBE file.");
        }

        /*
         * read lattice
         */
        subLines = this.readSubLines(4);
        double[] aVector = new double[3];
        try {
            int numVector = Math.abs(Integer.parseInt(subLines[0]));
            aVector[0] = numVector * this.toDouble(subLines[1]) - origin[0];
            aVector[1] = numVector * this.toDouble(subLines[2]) - origin[1];
            aVector[2] = numVector * this.toDouble(subLines[3]) - origin[2];
        } catch (NumberFormatException e) {
            throw new IOException("cannot read a-vector from a CUBE file.");
        }

        subLines = this.readSubLines(4);
        double[] bVector = new double[3];
        try {
            int numVector = Math.abs(Integer.parseInt(subLines[0]));
            bVector[0] = numVector * this.toDouble(subLines[1]) - origin[0];
            bVector[1] = numVector * this.toDouble(subLines[2]) - origin[1];
            bVector[2] = numVector * this.toDouble(subLines[3]) - origin[2];
        } catch (NumberFormatException e) {
            throw new IOException("cannot read b-vector from a CUBE file.");
        }

        subLines = this.readSubLines(4);
        double[] cVector = new double[3];
        try {
            int numVector = Math.abs(Integer.parseInt(subLines[0]));
            cVector[0] = numVector * this.toDouble(subLines[1]) - origin[0];
            cVector[1] = numVector * this.toDouble(subLines[2]) - origin[1];
            cVector[2] = numVector * this.toDouble(subLines[3]) - origin[2];
        } catch (NumberFormatException e) {
            throw new IOException("cannot read c-vector from a CUBE file.");
        }

        /*
         * read atoms
         */
        String[] name = new String[numAtoms];
        double[][] coord = new double[numAtoms][3];
        for (int i = 0; i < numAtoms; i++) {
            subLines = this.readSubLines(5);
            try {
                int atomNum = Math.abs(Integer.parseInt(subLines[0]));
                name[i] = ElementUtil.toElementName(atomNum);
                coord[i][0] = this.toDouble(subLines[2]) - origin[0];
                coord[i][1] = this.toDouble(subLines[3]) - origin[1];
                coord[i][2] = this.toDouble(subLines[4]) - origin[2];
            } catch (NumberFormatException e) {
                throw new IOException("cannot read atomic coordinate from a CUBE file.");
            }
            if (name[i] == null || name[i].trim().isEmpty()) {
                throw new IOException("incorrect atomic number in reading a CUBE file.");
            }
        }

        /*
         * create an instance of Cell
         */
        double[][] lattice = new double[3][3];
        for (int i = 0; i < 3; i++) {
            lattice[0][i] = Constants.BOHR_RADIUS_ANGS * aVector[i];
            lattice[1][i] = Constants.BOHR_RADIUS_ANGS * bVector[i];
            lattice[2][i] = Constants.BOHR_RADIUS_ANGS * cVector[i];
        }

        Cell cell = null;
        try {
            cell = new Cell(lattice);
        } catch (ZeroVolumCellException e) {
            throw new IOException(e);
        }

        cell.stopResolving();

        for (int i = 0; i < numAtoms; i++) {
            double x = Constants.BOHR_RADIUS_ANGS * coord[i][0];
            double y = Constants.BOHR_RADIUS_ANGS * coord[i][1];
            double z = Constants.BOHR_RADIUS_ANGS * coord[i][2];
            cell.addAtom(new Atom(name[i], x, y, z));
        }

        cell.restartResolving();

        return cell;
    }

    private String readNetLine() throws IOException {
        String line = this.reader.readLine();
        if (line == null) {
            throw new IOException("not enough lines in reading a CUBE file.");
        }

        return line.trim();
    }

    private String[] readSubLines(int size) throws IOException {
        String line = this.readNetLine();
        String[] subLines = line.split("[\\s,]+");
        if (subLines == null || subLines.length < size) {
            throw new IOException("not enough tokens in reading a CUBE file.");
        }

        return subLines;
    }
}
