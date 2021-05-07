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

import burai.atoms.model.Cell;

public class SlabModelBuilder {

    private Cell cell;

    public SlabModelBuilder(Cell cell) {
        if (cell == null) {
            throw new IllegalArgumentException("cell is null.");
        }

        this.cell = cell;
    }

    public SlabModel[] build(int h, int k, int l) {
        SlabModel slabModel = null;
        try {
            slabModel = new SlabModelStem(this.cell, h, k, l);
        } catch (MillerIndexException e) {
            //e.printStackTrace();
            return null;
        }

        SlabModel[] slabModels = slabModel.getSlabModels();
        if (slabModels == null || slabModels.length < 1) {
            return null;
        }

        return slabModels;
    }
}
