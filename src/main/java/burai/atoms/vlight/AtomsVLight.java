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

package burai.atoms.vlight;

import java.util.List;

import burai.atoms.model.Cell;
import burai.atoms.viewer.InitialOperations;
import javafx.scene.AmbientLight;
import javafx.scene.Camera;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.ParallelCamera;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.paint.Color;

public class AtomsVLight extends Group {

    private static final Color BACKGROUND_COLOR = Color.TRANSPARENT;

    private double size;

    private Camera camera;
    private Group sceneRoot;
    private SubScene subScene;

    private VLightCell vlightCell;

    public AtomsVLight(Cell cell, double size) {
        this(cell, size, false);
    }

    public AtomsVLight(Cell cell, double size, boolean parallel) {
        super();

        if (cell == null) {
            throw new IllegalArgumentException("cell is null.");
        }

        if (size <= 0.0) {
            throw new IllegalArgumentException("width is not positive.");
        }

        this.size = size;

        this.camera = null;
        this.sceneRoot = null;
        this.subScene = null;

        this.vlightCell = new VLightCell(this, cell);

        this.createCamera(parallel);
        this.createSceneRoot();
        this.createSubScene();
        this.getChildren().add(this.subScene);

        this.initialOperations(cell);
    }

    private void createCamera(boolean parallel) {
        if (parallel) {
            this.camera = new ParallelCamera();
        } else {
            this.camera = new PerspectiveCamera(false);
        }
    }

    private void createSceneRoot() {
        this.sceneRoot = new Group();
        this.sceneRoot.setDepthTest(DepthTest.ENABLE);
        this.sceneRoot.getChildren().add(this.vlightCell.getNode());
        this.sceneRoot.getChildren().add(new AmbientLight(Color.WHITE));
    }

    private void createSubScene() {
        this.subScene = new SubScene(this.sceneRoot, this.size, this.size, true, SceneAntialiasing.BALANCED);
        this.subScene.setFill(BACKGROUND_COLOR);
        this.subScene.setCamera(this.camera);

        MouseLightHandler handler = new MouseLightHandler(this);
        this.subScene.setOnMousePressed(handler);
        this.subScene.setOnMouseDragged(handler);
        this.subScene.setOnMouseReleased(handler);
    }

    private void initialOperations(Cell cell) {
        if (cell == null) {
            return;
        }

        List<double[]> operations = InitialOperations.getOperations(cell);
        if (operations == null || operations.isEmpty()) {
            return;
        }

        for (double[] operation : operations) {
            if (operation == null) {
                continue;
            }

            if (operation.length >= 4) {
                double angle = operation[0];
                double axisX = operation[1];
                double axisY = operation[2];
                double axisZ = operation[3];
                this.appendRotation(angle, axisX, axisY, axisZ);

            } else if (operation.length >= 1) {
                double scale = operation[0];
                this.appendScale(scale);
            }
        }
    }

    public double getSize() {
        return this.size;
    }

    public void appendScale(double scale) {
        if (this.vlightCell != null) {
            this.vlightCell.appendScale(scale);
        }
    }

    public void appendRotation(double angle, double axisX, double axisY, double axisZ) {
        if (this.vlightCell != null) {
            this.vlightCell.appendRotation(angle, axisX, axisY, axisZ);
        }
    }

    public void detachFromCell() {
        if (this.vlightCell != null) {
            this.vlightCell.detachFromCell();
        }
    }
}
