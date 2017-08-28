/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import burai.atoms.model.Cell;

public abstract class AtomsReader {

    private static final int FILE_TYPE_NULL = 0;
    private static final int FILE_TYPE_QE = 1;
    private static final int FILE_TYPE_XYZ = 2;
    private static final int FILE_TYPE_CIF = 3;
    private static final int FILE_TYPE_CUBE = 4;
    private static final int FILE_TYPE_XSF = 5;
    private static final int FILE_TYPE_AXSF = 6;
    private static final int FILE_TYPE_VASP = 7;

    private static final String VASP_NAME_POSCAR = File.separator + "POSCAR";
    private static final String VASP_NAME_CONTCAR = File.separator + "CONTCAR";

    private static int getFileType(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return FILE_TYPE_NULL;
        }

        String[] subNames = filePath.trim().split("\\.");
        if (subNames == null || subNames.length < 1) {
            return FILE_TYPE_NULL;
        }

        String extName = subNames[subNames.length - 1];
        if (extName == null || extName.isEmpty()) {
            return FILE_TYPE_NULL;
        }

        if ("in".equalsIgnoreCase(extName)) {
            return FILE_TYPE_QE;
        } else if ("xyz".equalsIgnoreCase(extName)) {
            return FILE_TYPE_XYZ;
        } else if ("cif".equalsIgnoreCase(extName)) {
            return FILE_TYPE_CIF;
        } else if ("cube".equalsIgnoreCase(extName) || "cub".equalsIgnoreCase(extName)) {
            return FILE_TYPE_CUBE;
        } else if ("xsf".equalsIgnoreCase(extName)) {
            return FILE_TYPE_XSF;
        } else if ("axsf".equalsIgnoreCase(extName)) {
            //return FILE_TYPE_AXSF;
            return FILE_TYPE_NULL;
        } else if (filePath.endsWith(VASP_NAME_POSCAR) || filePath.endsWith(VASP_NAME_CONTCAR)) {
            return FILE_TYPE_VASP;
        } else {
            return FILE_TYPE_NULL;
        }
    }

    public static boolean isToBeInstance(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return false;
        }

        return getFileType(filePath) != FILE_TYPE_NULL;
    }

    public static AtomsReader getInstance(String filePath) throws IOException {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("file path is empty.");
        }

        AtomsReader atomsReader = null;
        int fileType = getFileType(filePath);

        switch (fileType) {
        case FILE_TYPE_QE:
            atomsReader = new QEReader(filePath);
            break;
        case FILE_TYPE_XYZ:
            atomsReader = new XYZReader(filePath);
            break;
        case FILE_TYPE_CIF:
            atomsReader = new CIFReader(filePath);
            break;
        case FILE_TYPE_CUBE:
            atomsReader = new CubeReader(filePath);
            break;
        case FILE_TYPE_XSF:
            atomsReader = new XSFReader(filePath, false);
            break;
        case FILE_TYPE_AXSF:
            atomsReader = new XSFReader(filePath, true);
            break;
        case FILE_TYPE_VASP:
            atomsReader = new VASPReader(filePath);
            break;
        default:
            throw new IOException("cannot read a file: " + filePath);
        }

        return atomsReader;
    }

    protected BufferedReader reader;

    protected AtomsReader() {
        this.reader = null;
    }

    protected AtomsReader(String filePath) throws FileNotFoundException {
        this(filePath == null || filePath.isEmpty() ? null : new File(filePath));
    }

    protected AtomsReader(File file) throws FileNotFoundException {
        if (file == null) {
            throw new IllegalArgumentException("file is null.");
        }

        this.reader = new BufferedReader(new FileReader(file));
    }

    public abstract Cell readCell() throws IOException;

    public void close() throws IOException {
        if (this.reader != null) {
            this.reader.close();
        }
    }
}
