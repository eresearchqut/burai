/*
 * Copyright (C) 2016 Satomichi Nishihara
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

import burai.atoms.model.Cell;

public class XSFReader extends AtomsReader {

    public XSFReader(String fileName) throws FileNotFoundException {
        super(fileName);
    }

    public XSFReader(File file) throws FileNotFoundException {
        super(file);
    }

    @Override
    public Cell readCell() throws IOException {
        if (this.reader == null) {
            return null;
        }

        // TODO

        return null;
    }
}
