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

package burai.atoms.design.property;

public class AtomDesignProperty {

    private double radius;

    private double[] color;

    private int atomsStyle;

    private double bondWidth;

    protected AtomDesignProperty() {
        this.radius = 0.0;
        this.color = null;
        this.atomsStyle = 0;
        this.bondWidth = 0.0;
    }

    protected double getRadius() {
        return this.radius;
    }

    protected void setRadius(double radius) {
        this.radius = radius;
    }

    protected double[] getColor() {
        return this.color;
    }

    protected void setColor(double[] color) {
        this.color = color;
    }

    protected int getAtomsStyle() {
        return this.atomsStyle;
    }

    protected void setAtomsStyle(int atomsStyle) {
        this.atomsStyle = atomsStyle;
    }

    protected double getBondWidth() {
        return this.bondWidth;
    }

    protected void setBondWidth(double bondWidth) {
        this.bondWidth = bondWidth;
    }
}
