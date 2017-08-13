/*
 * Copyright (C) 2017 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.editor.designer;

import java.io.File;
import java.io.IOException;

import burai.app.QEFXAppComponent;
import burai.app.project.QEFXProjectController;
import burai.app.project.viewer.designer.QEFXDesignerViewer;
import burai.atoms.design.Design;
import burai.com.keys.PriorKeyEvent;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;

public class QEFXDesignerEditor extends QEFXAppComponent<QEFXDesignerEditorController> {

    public QEFXDesignerEditor(QEFXProjectController projectController, QEFXDesignerViewer viewer) throws IOException {
        super("QEFXDesignerEditor.fxml", new QEFXDesignerEditorController(projectController, viewer));

        if (this.node != null) {
            this.node.setOnMouseReleased(event -> this.node.requestFocus());
        }

        if (this.node != null && viewer != null) {
            this.setupEditorKeys(this.node, viewer);
        }

        if (viewer != null) {
            this.setupViewerKeys(viewer);
        }
    }

    public void setWritingFile(File file) {
        this.setWritingPath(file == null ? null : file.getPath());
    }

    public void setWritingPath(String path) {
        if (this.controller != null) {
            this.controller.setWritingPath(path);
        }
    }

    private void setupEditorKeys(Node node, QEFXDesignerViewer viewer) {
        if (node == null) {
            return;
        }

        node.setOnKeyPressed(event -> {
            if (event == null) {
                return;
            }

            if (PriorKeyEvent.isPriorKeyEvent(event)) {
                return;
            }

            if (event.isShortcutDown() && KeyCode.Z.equals(event.getCode())) {
                Design design = viewer == null ? null : viewer.getDesign();

                if (!event.isShiftDown()) {
                    // Shortcut + Z
                    if (design != null) {
                        design.restoreDesign();
                    }
                    if (this.controller != null) {
                        this.controller.refreshEditor();
                    }

                } else {
                    // Shortcut + Shift + Z
                    if (design != null) {
                        design.subRestoreDesign();
                    }
                    if (this.controller != null) {
                        this.controller.refreshEditor();
                    }
                }

            } else if (event.isShortcutDown() && KeyCode.C.equals(event.getCode())) {
                // Shortcut + C
                if (viewer != null) {
                    viewer.centerAtomsViewer();
                }
            }
        });
    }

    private void setupViewerKeys(QEFXDesignerViewer viewer) {
        if (viewer == null) {
            return;
        }

        viewer.setOnKeyPressed(event -> {
            if (event == null) {
                return;
            }

            if (PriorKeyEvent.isPriorKeyEvent(event)) {
                return;
            }

            if (event.isShortcutDown() && KeyCode.Z.equals(event.getCode())) {
                Design design = viewer.getDesign();

                if (!event.isShiftDown()) {
                    // Shortcut + Z
                    if (design != null) {
                        design.restoreDesign();
                    }
                    if (this.controller != null) {
                        this.controller.refreshEditor();
                    }

                } else {
                    // Shortcut + Shift + Z
                    if (design != null) {
                        design.subRestoreDesign();
                    }
                    if (this.controller != null) {
                        this.controller.refreshEditor();
                    }
                }
            }
        });
    }
}
