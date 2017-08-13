/*
 * Copyright (C) 2017 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.viewer.designer;

import java.io.IOException;

import burai.app.QEFXAppComponent;
import burai.app.project.QEFXProjectController;
import burai.atoms.design.Design;
import burai.atoms.model.Cell;
import burai.atoms.viewer.NodeWrapper;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;

public class QEFXDesignerViewer extends QEFXAppComponent<QEFXDesignerViewerController> {

    public QEFXDesignerViewer(QEFXProjectController projectController, Cell cell) throws IOException {
        super("QEFXDesignerViewer.fxml", new QEFXDesignerViewerController(projectController, cell));
    }

    public void centerAtomsViewer() {
        if (this.controller != null) {
            this.controller.centerAtomsViewer();
        }
    }

    public void addExclusiveNode(Node node) {
        if (this.controller != null) {
            this.controller.addExclusiveNode(node);
        }
    }

    public void addExclusiveNode(NodeWrapper nodeWrapper) {
        if (this.controller != null) {
            this.controller.addExclusiveNode(nodeWrapper);
        }
    }

    public void setOnKeyPressed(EventHandler<? super KeyEvent> value) {
        if (this.controller != null) {
            this.controller.setOnKeyPressed(value);
        }
    }

    public Design getDesign() {
        return this.controller == null ? null : this.controller.getDesign();
    }

    public void setDesign(Design design) {
        if (design == null) {
            return;
        }

        Design design_ = this.getDesign();
        if (design_ != null) {
            design.copyTo(design_);
        }
    }

}
