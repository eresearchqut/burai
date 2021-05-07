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

public abstract class SlabModel {

    private static final double DEFAULT_OFFSET = 0.0;

    private static final double DEFAULT_THICKNESS = 1.0;

    private static final double DEFAULT_VACUUM = 10.0; // angstrom

    private static final int DEFAULT_SCALE = 1;

    public static double defaultOffset() {
        return DEFAULT_OFFSET;
    }

    public static double defaultThickness() {
        return DEFAULT_THICKNESS;
    }

    public static double defaultVacuum() {
        return DEFAULT_VACUUM;
    }

    public static int defaultScale() {
        return DEFAULT_SCALE;
    }

    protected double offset;
    protected double thickness;
    protected double vacuum;
    protected int scaleA;
    protected int scaleB;

    private double lastOffset;
    private double lastThickness;
    private double lastVacuum;
    private int lastScaleA;
    private int lastScaleB;

    protected SlabModel() {
        this.offset = DEFAULT_OFFSET;
        this.thickness = DEFAULT_THICKNESS;
        this.vacuum = DEFAULT_VACUUM;
        this.scaleA = DEFAULT_SCALE;
        this.scaleB = DEFAULT_SCALE;

        this.lastOffset = this.offset;
        this.lastThickness = this.thickness;
        this.lastVacuum = this.vacuum;
        this.lastScaleA = this.scaleA;
        this.lastScaleB = this.scaleB;
    }

    public final void setOffset(double offset) {
        this.offset = offset;
    }

    protected final double getOffset() {
        return this.offset;
    }

    public final void setThickness(double thickness) {
        this.thickness = thickness;
    }

    protected final double getThickness() {
        return this.thickness;
    }

    public final void setVacuum(double vacuum) {
        this.vacuum = vacuum;
    }

    protected final double getVacuum() {
        return this.vacuum;
    }

    public final void setScaleA(int scaleA) {
        this.scaleA = scaleA;
    }

    protected final int getScaleA() {
        return this.scaleA;
    }

    public final void setScaleB(int scaleB) {
        this.scaleB = scaleB;
    }

    protected final int getScaleB() {
        return this.scaleB;
    }

    public abstract SlabModel[] getSlabModels();

    protected abstract boolean updateCell(Cell cell);

    public final boolean putOnCell(Cell cell) {

        boolean status = this.updateCell(cell);

        if (status) {
            this.lastOffset = this.offset;
            this.lastThickness = this.thickness;
            this.lastVacuum = this.vacuum;
            this.lastScaleA = this.scaleA;
            this.lastScaleB = this.scaleB;
        }

        return status;
    }

    public final boolean putOnLastCell(Cell cell) {
        this.offset = this.lastOffset;
        this.thickness = this.lastThickness;
        this.vacuum = this.lastVacuum;
        this.scaleA = this.lastScaleA;
        this.scaleB = this.lastScaleB;

        return this.updateCell(cell);
    }
}
