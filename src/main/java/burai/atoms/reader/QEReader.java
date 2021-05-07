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
import java.io.IOException;

import burai.atoms.model.Cell;
import burai.atoms.model.exception.ZeroVolumCellException;
import burai.input.QEGeometryInput;

public class QEReader extends AtomsReader {

    private QEGeometryInput input;

    public QEReader(String filePath) throws IOException {
        super();
        this.input = new QEGeometryInput(filePath);
    }

    public QEReader(File file) throws IOException {
        super();
        this.input = new QEGeometryInput(file);
    }

    public QEGeometryInput getInput() {
        return this.input;
    }

    @Override
    public Cell readCell() {
        Cell cell = null;
        if (this.input != null) {
            cell = this.input.getCell();
        }

        if (cell == null) {
            double[][] lattice = { { 1.0, 0.0, 0.0 }, { 0.0, 1.0, 0.0 }, { 0.0, 0.0, 1.0 } };
            try {
                cell = new Cell(lattice);
            } catch (ZeroVolumCellException e) {
                e.printStackTrace();
            }
        }

        return cell;
    }
}
