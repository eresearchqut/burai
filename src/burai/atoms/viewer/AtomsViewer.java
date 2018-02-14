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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import burai.atoms.design.Design;
import burai.atoms.model.Atom;
import burai.atoms.model.Cell;
import burai.atoms.model.property.CellProperty;
import burai.atoms.viewer.logger.AtomsLogger;
import burai.atoms.viewer.logger.AtomsLoggerPFactory;
import burai.atoms.viewer.operation.ViewerEventManager;
import burai.atoms.visible.VisibleAtom;
import burai.atoms.visible.VisibleCell;
import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

public class AtomsViewer extends AtomsViewerBase<Group> {

    private boolean compassMode;

    private boolean initiallyOperated;

    private ViewerCell viewerCell;
    private ViewerSample viewerSample;
    private ViewerXYZAxis viewerXYZAxis;
    private ViewerCompass viewerCompass;

    private AtomsLogger logger;

    private Design design;
    private List<Node> backgroundNodes;
    private boolean showingLegend;
    private boolean showingAxis;
    private boolean showingCell;

    private boolean busyLinkedViewers;
    private List<AtomsViewer> linkedViewers;

    public AtomsViewer(Cell cell, double size) {
        this(cell, size, size);
    }

    public AtomsViewer(Cell cell, double size, boolean silent) {
        this(cell, size, size, silent);
    }

    public AtomsViewer(Cell cell, double width, double height) {
        this(cell, width, height, false);
    }

    public AtomsViewer(Cell cell, double width, double height, boolean silent) {
        super(cell, width, height);

        this.compassMode = false;

        this.viewerCell = new ViewerCell(this, this.cell, silent);
        this.viewerSample = new ViewerSample(this, this.cell);
        this.viewerXYZAxis = new ViewerXYZAxis(this);
        if (!silent) {
            this.viewerCompass = new ViewerCompass(this.viewerCell);
        } else {
            this.viewerCompass = null;
        }

        if (!silent) {
            this.logger = new AtomsLogger(this.cell);
        } else {
            this.logger = null;
        }

        this.design = this.createDesign();
        this.backgroundNodes = null;
        this.showingLegend = this.design.isShowingLegend();
        this.showingAxis = this.design.isShowingAxis();
        this.showingCell = this.design.isShowingCell();

        this.busyLinkedViewers = false;
        this.linkedViewers = null;

        this.sceneRoot.getChildren().add(this.viewerCell.getNode());
        this.sceneRoot.getChildren().add(this.viewerSample.getNode());
        this.sceneRoot.getChildren().add(this.viewerXYZAxis.getNode());
        if (!silent) {
            this.sceneRoot.getChildren().add(this.viewerCompass.getNode());
        }

        ViewerEventManager viewerEventManager = new ViewerEventManager(this, silent);
        this.subScene.setOnMousePressed(viewerEventManager.getMousePressedHandler());
        this.subScene.setOnMouseDragged(viewerEventManager.getMouseDraggedHandler());
        this.subScene.setOnMouseReleased(viewerEventManager.getMouseReleasedHandler());
        this.subScene.setOnScroll(viewerEventManager.getScrollHandler());

        this.subScene.setOnKeyPressed(event -> {
            EventHandler<KeyEvent> handler = viewerEventManager.getKeyPressedHandler();
            if (handler != null) {
                handler.handle(event);
            }
            if (this.subKeyHandler != null) {
                subKeyHandler.handle(event);
            }
        });

        this.applyDesign();

        this.initiallyOperated = false;
        this.initialOperations(null);
    }

    private Design createDesign() {
        Design design = new Design();

        if (this.cell != null) {
            if (this.cell.hasProperty(CellProperty.MOLECULE)) {
                if (this.cell.booleanProperty(CellProperty.MOLECULE)) {
                    design.setShowingCell(false);
                }
            }
        }

        design.addOnBackColorChanged(color -> {
            if (color == null) {
                return;
            }

            this.subScene.setFill(color);

            if (this.backgroundNodes != null && !this.backgroundNodes.isEmpty()) {
                String strColor = color.toString();
                strColor = strColor == null ? null : strColor.replaceAll("0x", "#");
                if (strColor != null) {
                    for (Node node : this.backgroundNodes) {
                        node.setStyle("-fx-background-color: " + strColor);
                    }
                }
            }
        });

        design.addOnShowingLegendChanged(showing -> {
            if (this.showingLegend != showing) {
                this.showingLegend = showing;
                this.viewerSample.getNode().setVisible(showing);
            }
        });

        design.addOnShowingAxisChanged(showing -> {
            if (this.showingAxis != showing) {
                this.showingAxis = showing;
                this.viewerXYZAxis.getNode().setVisible(showing);
            }
        });

        design.addOnShowingCellChanged(showing -> {
            if (this.showingCell != showing) {
                this.showingCell = showing;
                this.setCellToCenter();
            }
        });

        return design;
    }

    private void applyDesign() {
        this.subScene.setFill(this.design.getBackColor());
        this.viewerSample.getNode().setVisible(this.design.isShowingLegend());
        this.viewerXYZAxis.getNode().setVisible(this.design.isShowingAxis());
    }

    private void initialOperations(List<double[]> operations) {
        if (this.cell == null) {
            return;
        }

        List<double[]> operations_ = operations;
        if (operations_ == null || operations_.isEmpty()) {
            operations_ = InitialOperations.getOperations(this.cell, this.design);
        }

        if (operations_ == null || operations_.isEmpty()) {
            return;
        }

        for (double[] operation : operations_) {
            if (operation == null) {
                continue;
            }

            if (operation.length >= 4) {
                double angle = operation[0];
                double axisX = operation[1];
                double axisY = operation[2];
                double axisZ = operation[3];
                this.appendCellRotation(angle, axisX, axisY, axisZ);

            } else if (operation.length >= 3) {
                double x = operation[0];
                double y = operation[1];
                double z = operation[2];
                this.appendCellTranslation(x, y, z);

            } else if (operation.length >= 1) {
                double scale = operation[0];
                this.appendCellScale(scale);
            }
        }

        this.initiallyOperated = true;
    }

    @Override
    protected Group newSceneRoot() {
        return new Group();
    }

    @Override
    protected void onSceneResized() {
        if (this.viewerCell != null) {
            this.viewerCell.initialize(true);
        }

        if (this.viewerSample != null) {
            this.viewerSample.initialize();
        }

        if (this.viewerXYZAxis != null) {
            this.viewerXYZAxis.initialize(true);
        }

        if (this.viewerCompass != null) {
            this.viewerCompass.initialize(true);
        }
    }

    public boolean isCompassMode() {
        return this.compassMode;
    }

    public void setCompassMode(VisibleAtom targetAtom) {
        if (this.viewerCompass == null) {
            return;
        }

        this.compassMode = (targetAtom != null);
        this.viewerCompass.setTargetAtom(targetAtom);

        if (this.compassMode) {
            this.viewerCompass.initialize();
            this.viewerCompass.getNode().setVisible(true);
            this.startExclusiveMode();

        } else {
            this.viewerCompass.getNode().setVisible(false);
            this.stopExclusiveMode();
        }
    }

    public void appendCellScale(double scale) {
        if (this.compassMode) {
            return;
        }

        if (this.busyLinkedViewers) {
            return;
        }

        if (this.viewerCell != null) {
            this.viewerCell.appendScale(scale);
        }

        this.busyLinkedViewers = true;

        if (this.linkedViewers != null) {
            for (AtomsViewer atomsViewer : this.linkedViewers) {
                if (atomsViewer != null) {
                    atomsViewer.appendCellScale(scale);
                }
            }
        }

        this.busyLinkedViewers = false;

        this.initiallyOperated = false;
    }

    public void appendCellRotation(double angle, double axisX, double axisY, double axisZ) {
        if (this.compassMode) {
            return;
        }

        if (this.busyLinkedViewers) {
            return;
        }

        if (this.viewerCell != null) {
            this.viewerCell.appendRotation(angle, axisX, axisY, axisZ);
        }

        if (this.viewerXYZAxis != null) {
            this.viewerXYZAxis.appendRotation(angle, axisX, axisY, axisZ);
        }

        this.busyLinkedViewers = true;

        if (this.linkedViewers != null) {
            for (AtomsViewer atomsViewer : this.linkedViewers) {
                if (atomsViewer != null) {
                    atomsViewer.appendCellRotation(angle, axisX, axisY, axisZ);
                }
            }
        }

        this.busyLinkedViewers = false;

        this.initiallyOperated = false;
    }

    public void appendCellTranslation(double x, double y, double z) {
        if (this.compassMode) {
            return;
        }

        if (this.busyLinkedViewers) {
            return;
        }

        double scale1 = 1.0;
        if (this.viewerCell != null) {
            scale1 = this.viewerCell.getScale();
            this.viewerCell.appendTranslation(x, y, z);
        }

        this.busyLinkedViewers = true;

        if (this.linkedViewers != null) {
            for (AtomsViewer atomsViewer : this.linkedViewers) {
                if (atomsViewer == null) {
                    continue;
                }

                double scale2 = 1.0;
                if (atomsViewer.viewerCell != null) {
                    scale2 = atomsViewer.viewerCell.getScale();
                }

                double x2 = x * scale2 / scale1;
                double y2 = y * scale2 / scale1;
                double z2 = z * scale2 / scale1;
                atomsViewer.appendCellTranslation(x2, y2, z2);
            }
        }

        this.busyLinkedViewers = false;

        this.initiallyOperated = false;
    }

    public void appendCompassRotation(double angle, double axisX, double axisY, double axisZ) {
        if (!this.compassMode) {
            return;
        }

        if (this.viewerCompass != null) {
            this.viewerCompass.appendRotation(angle, axisX, axisY, axisZ);
        }
    }

    public boolean addChild(Node node) {
        if (this.compassMode) {
            return false;
        }

        if (this.sceneRoot != null) {
            return this.sceneRoot.getChildren().add(node);
        }

        return false;
    }

    public boolean removeChild(Node node) {
        if (this.compassMode) {
            return false;
        }

        if (this.sceneRoot != null) {
            return this.sceneRoot.getChildren().remove(node);
        }

        return false;
    }

    public boolean hasChild(Node node) {
        if (this.sceneRoot != null) {
            return this.sceneRoot.getChildren().contains(node);
        }

        return false;
    }

    public List<VisibleAtom> getVisibleAtoms() {
        List<VisibleAtom> visibleAtoms = new ArrayList<VisibleAtom>();

        VisibleCell visibleCell = null;
        if (this.viewerCell != null) {
            visibleCell = this.viewerCell.getNode();
        }

        if (visibleCell != null) {
            List<Node> children = visibleCell.getChildren();
            for (Node child : children) {
                if (child instanceof VisibleAtom) {
                    visibleAtoms.add((VisibleAtom) child);
                }
            }
        }

        return visibleAtoms;
    }

    public boolean isInCell(double sceneX, double sceneY, double sceneZ) {
        if (this.viewerCell == null) {
            return false;
        }

        return this.viewerCell.isInCell(sceneX, sceneY, sceneZ);
    }

    public Point3D sceneToCell(double sceneX, double sceneY, double sceneZ) {
        if (this.viewerCell == null) {
            return null;
        }

        VisibleCell visibleCell = this.viewerCell.getNode();
        if (visibleCell == null) {
            return null;
        }

        return visibleCell.sceneToLocal(sceneX, sceneY, sceneZ);
    }

    public double getSceneZOnCompass(double sceneX, double sceneY) {
        if (!this.compassMode) {
            return 0.0;
        }

        if (this.viewerCompass == null) {
            return 0.0;
        }

        return this.viewerCompass.getSceneZ(sceneX, sceneY);
    }

    public void putAtom(String name, double sceneX, double sceneY, double sceneZ) {
        if (this.compassMode) {
            return;
        }

        if (this.viewerCell == null) {
            return;
        }

        VisibleCell visibleCell = this.viewerCell.getNode();
        if (visibleCell == null) {
            return;
        }

        Point3D point3d = visibleCell.sceneToLocal(sceneX, sceneY, sceneZ);
        double x = point3d.getX();
        double y = point3d.getY();
        double z = point3d.getZ();

        if (name != null && !name.isEmpty()) {
            visibleCell.getModel().addAtom(new Atom(name, x, y, z));
        }
    }

    public void setCellToCenter() {
        List<double[]> operations = InitialOperations.getOperations(this.cell, this.design);
        this.setCellToCenter(operations);
    }

    private void setCellToCenter(List<double[]> operations) {
        if (this.compassMode) {
            return;
        }

        if (this.busyLinkedViewers) {
            return;
        }

        if (this.viewerCell != null) {
            this.viewerCell.initialize();
        }

        if (this.viewerXYZAxis != null) {
            this.viewerXYZAxis.initialize();
        }

        this.initialOperations(operations);

        this.busyLinkedViewers = true;

        if (this.linkedViewers != null) {
            for (AtomsViewer atomsViewer : this.linkedViewers) {
                if (atomsViewer != null) {
                    atomsViewer.setCellToCenter(operations);
                }
            }
        }

        this.busyLinkedViewers = false;
    }

    public void setCompassToCenter() {
        if (!this.compassMode) {
            return;
        }

        if (this.viewerCompass != null) {
            this.viewerCompass.initialize();
        }
    }

    public void setLoggerPropertyFactory(AtomsLoggerPFactory propFactory) {
        if (this.logger != null) {
            this.logger.setPropertyFactory(propFactory);
        }
    }

    public void clearStoredCell() {
        if (this.logger != null) {
            this.logger.clearConfiguration();
        }
    }

    public void storeCell() {
        if (this.logger != null) {
            this.logger.storeConfiguration();
        }
    }

    public boolean canRestoreCell() {
        if (this.compassMode) {
            return false;
        }

        if (this.logger != null) {
            return this.logger.canRestoreConfiguration();
        }

        return false;
    }

    public boolean canSubRestoreCell() {
        if (this.compassMode) {
            return false;
        }

        if (this.logger != null) {
            return this.logger.canSubRestoreConfiguration();
        }

        return false;
    }

    public void restoreCell() {
        if (this.compassMode) {
            return;
        }

        if (this.logger != null) {
            this.logger.restoreConfiguration();
        }
    }

    public void subRestoreCell() {
        if (this.compassMode) {
            return;
        }

        if (this.logger != null) {
            this.logger.subRestoreConfiguration();
        }
    }

    public Design getDesign() {
        return this.design;
    }

    public void setDesign(Design design) {
        if (design != null) {
            design.copyTo(this.design);

            if (this.initiallyOperated) {
                this.setCellToCenter();
            }
        }
    }

    public void setDesign(String path) {
        if (path != null && !path.isEmpty()) {
            this.design.readDesign(path);

            if (this.initiallyOperated) {
                this.setCellToCenter();
            }
        }
    }

    public void setDesign(File file) {
        this.setDesign(file == null ? null : file.getPath());
    }

    public void saveDesign(String path) {
        if (path != null && !path.isEmpty()) {
            this.design.writeDesign(path);
        }
    }

    public void saveDesign(File file) {
        this.saveDesign(file == null ? null : file.getPath());
    }

    public void addBackgroundNode(Node node) {
        if (node == null) {
            return;
        }

        if (this.backgroundNodes == null) {
            this.backgroundNodes = new ArrayList<Node>();
        }

        Color color = this.design.getBackColor();
        String strColor = color == null ? null : color.toString();
        strColor = strColor == null ? null : strColor.replaceAll("0x", "#");
        if (strColor != null) {
            node.setStyle("-fx-background-color: " + strColor);
        }

        this.backgroundNodes.add(node);
    }

    public void linkAtomsViewer(AtomsViewer atomsViewer) {
        if (atomsViewer == null) {
            return;
        }

        if (this.linkedViewers == null) {
            this.linkedViewers = new ArrayList<AtomsViewer>();
        }

        if (this.linkedViewers.contains(atomsViewer)) {
            return;
        }

        this.linkedViewers.add(atomsViewer);

        atomsViewer.linkAtomsViewer(this);
    }
}
