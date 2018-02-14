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

    public void setDesign(Design design, boolean prim, boolean dual) {
        if (this.controller != null) {
            this.controller.setDesign(design, prim, dual);
        }
    }

}
