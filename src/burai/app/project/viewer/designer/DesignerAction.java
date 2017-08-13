/*
 * Copyright (C) 2017 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.viewer.designer;

import java.io.File;
import java.io.IOException;

import burai.app.project.QEFXProjectController;
import burai.app.project.editor.designer.QEFXDesignerEditor;
import burai.app.project.viewer.atoms.AtomsAction;
import burai.atoms.design.Design;
import burai.atoms.model.Cell;
import burai.atoms.viewer.AtomsViewer;
import burai.atoms.viewer.AtomsViewerInterface;
import burai.project.Project;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

public class DesignerAction {

    private Project project;

    private QEFXProjectController controller;

    private QEFXDesignerViewer designerViewer;

    public DesignerAction(Project project, QEFXProjectController controller) {
        if (project == null) {
            throw new IllegalArgumentException("project is null.");
        }

        if (controller == null) {
            throw new IllegalArgumentException("controller is null.");
        }

        this.project = project;
        this.controller = controller;

        this.designerViewer = null;
    }

    public QEFXProjectController getController() {
        return this.controller;
    }

    public void showDesigner() {
        if (this.designerViewer == null) {
            this.initializeDesigner();
            return;
        }

        this.controller.setDesignerMode();
    }

    private void initializeDesigner() {
        if (this.designerViewer == null) {
            try {
                this.designerViewer = this.createDesignerViewer();
            } catch (IOException e) {
                this.designerViewer = null;
                e.printStackTrace();
            }
        }

        QEFXDesignerEditor designerEditor = null;
        if (this.designerViewer != null) {
            try {
                designerEditor = new QEFXDesignerEditor(this.controller, this.designerViewer);

                File designFile = AtomsAction.getAtomsDesignFile(this.project);
                if (designFile != null) {
                    designerEditor.setWritingFile(designFile);
                }

            } catch (IOException e) {
                designerEditor = null;
                e.printStackTrace();
            }
        }

        if (designerEditor != null && this.designerViewer != null) {
            final AtomsViewer atomsViewer;
            AtomsViewerInterface atomsViewerInterface = this.controller.getAtomsViewer();
            if (atomsViewerInterface != null && atomsViewerInterface instanceof AtomsViewer) {
                atomsViewer = (AtomsViewer) atomsViewerInterface;
            } else {
                atomsViewer = null;
            }

            this.controller.setDesignerMode();
            this.controller.clearStackedsOnViewerPane();

            if (atomsViewer != null) {
                Design srcDesign = atomsViewer.getDesign();
                if (srcDesign != null) {
                    this.designerViewer.setDesign(srcDesign);
                }

                this.controller.setOnModeBacked(controller2 -> {
                    Design dstDesign = this.designerViewer.getDesign();
                    if (dstDesign != null) {
                        atomsViewer.setDesign(dstDesign);
                    }
                    return true;
                });
            }

            Node viewerNode = this.designerViewer.getNode();
            if (viewerNode != null) {
                this.controller.setViewerPane(viewerNode);
            }

            Node editorNode = designerEditor.getNode();
            if (editorNode != null) {
                this.controller.setEditorPane(editorNode);
            }
        }
    }

    private QEFXDesignerViewer createDesignerViewer() throws IOException {
        Cell cell = this.project.getCell();
        if (cell == null) {
            return null;
        }

        QEFXDesignerViewer designerViewer = new QEFXDesignerViewer(this.controller, cell);

        final BorderPane projectPane;
        if (this.controller != null) {
            projectPane = this.controller.getProjectPane();
        } else {
            projectPane = null;
        }

        if (projectPane != null) {
            designerViewer.addExclusiveNode(() -> {
                return projectPane.getRight();
            });
            designerViewer.addExclusiveNode(() -> {
                return projectPane.getBottom();
            });
        }

        return designerViewer;
    }

}
