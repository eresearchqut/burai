/*
 * Copyright (C) 2017 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.viewer.modeler.slabmodel;

import java.io.IOException;
import java.util.Optional;

import burai.app.QEFXMain;
import burai.app.project.QEFXProjectController;
import burai.app.project.editor.modeler.slabmodel.QEFXSlabEditor;
import burai.app.project.viewer.atoms.AtomsAction;
import burai.app.project.viewer.modeler.ModelerIcon;
import burai.atoms.design.Design;
import burai.atoms.model.Cell;
import burai.atoms.viewer.AtomsViewer;
import burai.atoms.viewer.AtomsViewerInterface;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;

public class SlabAction {

    private Cell cell;

    private QEFXProjectController controller;

    private SlabModeler slabModeler;

    private QEFXSlabEditor slabEditor;

    private AtomsViewer atomsViewer;

    public SlabAction(Cell cell, QEFXProjectController controller) {
        if (cell == null) {
            throw new IllegalArgumentException("cell is null.");
        }

        if (controller == null) {
            throw new IllegalArgumentException("controller is null.");
        }

        this.cell = cell;
        this.controller = controller;

        this.slabModeler = null;
        this.slabEditor = null;
        this.atomsViewer = null;
    }

    public void showSlabModeler(SlabModel[] slabModels) {
        this.showInitialDialog();

        if (this.slabModeler == null || this.slabEditor == null || this.atomsViewer == null) {
            this.initializeSlabModeler(slabModels);
            return;
        }

        if (slabModels != null && slabModels.length > 0) {
            this.slabEditor.setSlabModels(slabModels);
        }

        this.atomsViewer.setCellToCenter();

        this.controller.setModelerSlabMode();
    }

    private void initializeSlabModeler(SlabModel[] slabModels) {
        final AtomsViewer srcAtomsViewer;
        AtomsViewerInterface srcAtomsViewerInterface = this.controller.getAtomsViewer();
        if (srcAtomsViewerInterface != null && srcAtomsViewerInterface instanceof AtomsViewer) {
            srcAtomsViewer = (AtomsViewer) srcAtomsViewerInterface;
        } else {
            srcAtomsViewer = null;
        }

        if (this.slabModeler == null) {
            this.slabModeler = new SlabModeler(this.cell);
        }

        if (this.slabEditor == null) {
            this.slabEditor = this.createSlabEditor(slabModels);
        }

        if (this.atomsViewer == null) {
            this.atomsViewer = this.createAtomsViewer(srcAtomsViewer);
        }

        if (this.slabEditor != null && this.atomsViewer != null) {
            this.controller.setModelerSlabMode(controller2 -> {
                Design srcDesign = srcAtomsViewer == null ? null : srcAtomsViewer.getDesign();
                if (srcDesign != null) {
                    this.atomsViewer.setDesign(srcDesign);
                }
            });

            this.controller.setOnModeBacked(controller2 -> {
                boolean status = this.showFinishDialog();

                if (status) {
                    Cell cell = this.slabModeler == null ? null : this.slabModeler.getCell();
                    if (cell != null) {
                        cell.removeAllAtoms();
                    }

                    if (this.slabEditor != null) {
                        this.slabEditor.cleanSlabModels();
                    }
                }

                return status;
            });

            this.controller.clearStackedsOnViewerPane();

            if (this.atomsViewer != null) {
                this.controller.setViewerPane(this.atomsViewer);
            }

            this.controller.stackOnViewerPane(new ModelerIcon("Slab" + System.lineSeparator() + "Model"));

            Node editorNode = this.slabEditor.getNode();
            if (editorNode != null) {
                this.controller.setEditorPane(editorNode);
            }
        }
    }

    private QEFXSlabEditor createSlabEditor(SlabModel[] slabModels) {
        QEFXSlabEditor slabEditor = null;

        if (this.slabModeler != null) {
            try {
                slabEditor = new QEFXSlabEditor(this.controller, this.slabModeler);
            } catch (IOException e) {
                slabEditor = null;
                e.printStackTrace();
            }
        }

        if (slabEditor != null) {
            if (slabModels != null && slabModels.length > 0) {
                slabEditor.setSlabModels(slabModels);
            }
        }

        return slabEditor;
    }

    private AtomsViewer createAtomsViewer(AtomsViewer srcAtomsViewer) {
        Cell cell = this.slabModeler == null ? null : this.slabModeler.getCell();
        if (cell == null) {
            return null;
        }

        AtomsViewer atomsViewer = new AtomsViewer(cell, AtomsAction.getAtomsViewerSize(), true);

        Design srcDesign = srcAtomsViewer == null ? null : srcAtomsViewer.getDesign();
        if (srcDesign != null) {
            atomsViewer.setDesign(srcDesign);
        }

        final BorderPane projectPane;
        if (this.controller != null) {
            projectPane = this.controller.getProjectPane();
        } else {
            projectPane = null;
        }

        if (projectPane != null) {
            atomsViewer.addExclusiveNode(() -> {
                return projectPane.getRight();
            });
            atomsViewer.addExclusiveNode(() -> {
                return projectPane.getBottom();
            });
        }

        this.slabModeler.setAtomsViewer(atomsViewer);

        return atomsViewer;
    }

    private void showInitialDialog() {
        Alert alert = new Alert(AlertType.INFORMATION);
        QEFXMain.initializeDialogOwner(alert);
        alert.setHeaderText("Please set the details of slab model.");
        alert.show();
    }

    private boolean showFinishDialog() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        QEFXMain.initializeDialogOwner(alert);
        alert.setHeaderText("Finish to model the slab ?");
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);

        Optional<ButtonType> optButtonType = alert.showAndWait();
        if (optButtonType == null || (!optButtonType.isPresent())) {
            return false;
        }
        if (!ButtonType.YES.equals(optButtonType.get())) {
            return false;
        }

        if (this.slabModeler != null && this.slabModeler.isToReflect()) {
            if (this.slabModeler != null) {
                this.slabModeler.reflect();
            }

            Platform.runLater(() -> {
                AtomsViewerInterface atomsViewer = this.controller.getAtomsViewer();
                if (atomsViewer != null && atomsViewer instanceof AtomsViewer) {
                    ((AtomsViewer) atomsViewer).setCellToCenter();
                }
            });
        }

        return true;
    }
}
