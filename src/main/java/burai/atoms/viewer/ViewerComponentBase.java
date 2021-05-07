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

import javafx.scene.Node;
import javafx.scene.transform.Affine;

public abstract class ViewerComponentBase<A, N extends Node> {

    protected A atomsViewer;

    private N node;
    protected Affine affine;
    protected double scale;
    protected double centerX;
    protected double centerY;
    protected double centerZ;

    public ViewerComponentBase(A atomsViewer) {
        if (atomsViewer == null) {
            throw new IllegalArgumentException("atomsViewer is null.");
        }

        this.atomsViewer = atomsViewer;

        this.node = null;
        this.scale = 1.0;
        this.centerX = 0.0;
        this.centerY = 0.0;
        this.centerZ = 0.0;
    }

    public abstract void initialize();

    protected abstract N createNode();

    public N getNode() {
        if (this.node == null) {
            this.initialize();
            this.node = this.createNode();
            this.node.getTransforms().add(this.affine);
        }

        return this.node;
    }

    public double getScale() {
        return this.affine == null ? 1.0 : this.affine.getMxx();
    }

    public void appendScale(double scale) {
        if (this.affine != null) {
            this.affine.prependScale(
                    scale, scale, scale, this.centerX, this.centerY, this.centerZ);
        }
    }

    public void appendRotation(double angle, double axisX, double axisY, double axisZ) {
        if (this.affine != null) {
            this.affine.prependRotation(
                    angle, this.centerX, this.centerY, this.centerZ, axisX, axisY, axisZ);
        }
    }

    public void appendTranslation(double x, double y, double z) {
        if (this.affine != null) {
            this.affine.prependTranslation(x, y, z);
        }
    }
}
