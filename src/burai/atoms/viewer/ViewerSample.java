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

package burai.atoms.viewer;

import burai.atoms.model.Cell;
import burai.atoms.visible.AtomsSample;
import javafx.scene.transform.Affine;

public class ViewerSample extends ViewerComponent<AtomsSample> {

    private Cell cell;

    public ViewerSample(AtomsViewer atomsViewer, Cell cell) {
        super(atomsViewer);

        if (cell == null) {
            throw new IllegalArgumentException("cell is null.");
        }

        this.cell = cell;
    }

    @Override
    public void initialize() {
        double width = this.atomsViewer.getSceneWidth();
        double height = this.atomsViewer.getSceneHeight();
        double rangeScene = Math.min(width, height);

        this.scale = 0.05 * rangeScene;
        this.centerX = 0.065 * width;
        this.centerY = 0.065 * height;
        this.centerZ = -0.40 * rangeScene;

        if (this.affine == null) {
            this.affine = new Affine();
        }

        this.affine.setToIdentity();
        this.affine.prependScale(this.scale, this.scale, this.scale);
        this.affine.prependTranslation(this.centerX, this.centerY, this.centerZ);
    }

    @Override
    protected AtomsSample createNode() {
        return new AtomsSample(this.cell, this.atomsViewer.getDesign());
    }
}
