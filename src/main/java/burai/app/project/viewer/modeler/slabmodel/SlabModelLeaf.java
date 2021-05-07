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

public class SlabModelLeaf extends SlabModel {

    private SlabModelStem stem;

    public SlabModelLeaf(SlabModelStem stem, double offset) {
        super();

        if (stem == null) {
            throw new IllegalArgumentException("stem is null.");
        }

        this.stem = stem;
        this.offset = offset;
    }

    @Override
    public SlabModel[] getSlabModels() {
        return new SlabModel[] { this };
    }

    @Override
    protected boolean updateCell(Cell cell) {
        return this.stem.updateCell(cell, this);
    }

}
