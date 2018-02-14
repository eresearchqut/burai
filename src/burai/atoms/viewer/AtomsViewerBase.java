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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import burai.atoms.model.Cell;
import javafx.event.EventHandler;
import javafx.scene.Camera;
import javafx.scene.DepthTest;
import javafx.scene.Node;
import javafx.scene.ParallelCamera;
import javafx.scene.Parent;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public abstract class AtomsViewerBase<R extends Parent> extends AtomsViewerInterface {

    protected Cell cell;

    protected double width;
    protected double height;

    private Camera camera;
    protected R sceneRoot;
    protected SubScene subScene;
    protected EventHandler<? super KeyEvent> subKeyHandler;

    private List<NodeWrapper> exclusiveNodes;
    private Map<NodeWrapper, Boolean> exclusiveDisables;

    protected AtomsViewerBase(Cell cell, double width, double height) {
        super();

        if (cell == null) {
            throw new IllegalArgumentException("cell is null.");
        }

        if (width <= 0.0) {
            throw new IllegalArgumentException("width is not positive.");
        }

        if (height <= 0.0) {
            throw new IllegalArgumentException("height is not positive.");
        }

        this.cell = cell;

        this.width = width;
        this.height = height;

        this.camera = null;
        this.sceneRoot = null;
        this.subScene = null;
        this.subKeyHandler = null;

        this.exclusiveNodes = null;
        this.exclusiveDisables = null;

        this.createCamera();
        this.createSceneRoot();
        this.createSubScene();
        this.getChildren().add(this.subScene);
    }

    private void createCamera() {
        if (this.camera == null) {
            this.camera = new ParallelCamera();
        }

        double range = Math.min(this.width, this.height);
        this.camera.setFarClip(100.0 * range);
        this.camera.setNearClip(1.0e-4);
    }

    protected abstract R newSceneRoot();

    private void createSceneRoot() {
        this.sceneRoot = this.newSceneRoot();
        this.sceneRoot.setDepthTest(DepthTest.ENABLE);
    }

    private void createSubScene() {
        this.subScene = new SubScene(this.sceneRoot, this.width, this.height, true, SceneAntialiasing.BALANCED);
        this.subScene.getStyleClass().add("atoms-viewer");
        this.subScene.setCamera(this.camera);
        this.subScene.setManaged(false);
        this.subScene.setOnMouseClicked(event -> this.subScene.requestFocus());
        this.subScene.widthProperty().addListener(o -> this.resizeScene());
        this.subScene.heightProperty().addListener(o -> this.resizeScene());
    }

    @Override
    public Cell getCell() {
        return this.cell;
    }

    @Override
    public double getSceneWidth() {
        return this.width;
    }

    @Override
    public double getSceneHeight() {
        return this.height;
    }

    @Override
    public void setSceneStyle(String style) {
        if (this.subScene != null) {
            this.subScene.setStyle(style);
        }
    }

    @Override
    public void addExclusiveNode(Node node) {
        if (node == null) {
            return;
        }

        this.addExclusiveNode(() -> {
            return node;
        });
    }

    @Override
    public void addExclusiveNode(NodeWrapper nodeWrapper) {
        if (nodeWrapper == null) {
            return;
        }

        if (this.exclusiveNodes == null) {
            this.exclusiveNodes = new ArrayList<NodeWrapper>();
        }

        this.exclusiveNodes.add(nodeWrapper);
    }

    @Override
    public void startExclusiveMode() {
        if (this.exclusiveNodes == null) {
            return;
        }

        if (this.exclusiveDisables == null) {
            this.exclusiveDisables = new HashMap<NodeWrapper, Boolean>();
        }

        for (NodeWrapper nodeWrapper : this.exclusiveNodes) {
            if (nodeWrapper == null) {
                continue;
            }
            Node node = nodeWrapper.getNode();
            if (node == null) {
                continue;
            }
            this.exclusiveDisables.put(nodeWrapper, node.isDisable());
            node.setDisable(true);
        }
    }

    @Override
    public void stopExclusiveMode() {
        if (this.exclusiveNodes == null) {
            return;
        }

        if (this.exclusiveDisables == null) {
            return;
        }

        for (NodeWrapper nodeWrapper : this.exclusiveNodes) {
            if (nodeWrapper == null) {
                continue;
            }
            Node node = nodeWrapper.getNode();
            if (node == null) {
                continue;
            }
            Boolean disable = this.exclusiveDisables.get(nodeWrapper);
            if (disable != null) {
                node.setDisable(disable);
            }
        }
    }

    protected abstract void onSceneResized();

    private void resizeScene() {
        double widthTmp = -1.0;
        double heightTmp = -1.0;

        if (this.subScene != null) {
            widthTmp = this.subScene.getWidth();
            heightTmp = this.subScene.getHeight();
        }

        if (widthTmp <= 0.0 || heightTmp <= 0.0) {
            return;
        }

        this.width = widthTmp;
        this.height = heightTmp;

        this.createCamera();

        this.onSceneResized();
    }

    @Override
    public void bindSceneTo(Pane pane) {
        if (pane == null) {
            return;
        }

        if (this.subScene == null) {
            return;
        }

        if (pane instanceof AnchorPane) {
            AnchorPane.setBottomAnchor(this.subScene, 0.0);
            AnchorPane.setTopAnchor(this.subScene, 0.0);
            AnchorPane.setLeftAnchor(this.subScene, 0.0);
            AnchorPane.setRightAnchor(this.subScene, 0.0);
        }

        this.subScene.widthProperty().bind(pane.widthProperty());
        this.subScene.heightProperty().bind(pane.heightProperty());

        Parent parent = this.subScene.getParent();
        if (parent == this) {
            List<Node> children = this.getChildren();
            if (children.contains(this.subScene)) {
                children.remove(this.subScene);
            }
        }

        pane.getChildren().add(this.subScene);

        this.subKeyHandler = this.getOnKeyPressed();
        this.setOnKeyPressed(null);
    }

    @Override
    public void unbindScene() {

        this.subScene.widthProperty().unbind();
        this.subScene.heightProperty().unbind();

        Parent parent = this.subScene.getParent();
        if (parent != null && parent instanceof Pane) {
            Pane pane = (Pane) parent;
            List<Node> children = pane.getChildren();
            if (children.contains(this.subScene)) {
                children.remove(this.subScene);
            }
        }

        this.getChildren().add(this.subScene);

        this.setOnKeyPressed(this.subKeyHandler);
        this.subKeyHandler = null;
    }
}
