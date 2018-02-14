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

package burai.app.project.editor.input.scf;

import burai.app.QEFXMainController;
import burai.app.project.editor.QEFXEditorController;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;

public class QEFXScfController extends QEFXEditorController {

    @FXML
    public ScrollPane standardPane;

    @FXML
    public ScrollPane electronPane;

    @FXML
    public ScrollPane magnetizPane;

    @FXML
    public ScrollPane hybridPane;

    @FXML
    public ScrollPane hubbardPane;

    @FXML
    public ScrollPane vdwPane;

    @FXML
    public ScrollPane isolatedPane;

    public QEFXScfController(QEFXMainController mainController) {
        super(mainController);
    }

    public void setStandardPane(Node node) {
        if (node == null) {
            return;
        }

        if (this.standardPane != null) {
            this.standardPane.setContent(node);
        }
    }

    public void setElectronPane(Node node) {
        if (node == null) {
            return;
        }

        if (this.electronPane != null) {
            this.electronPane.setContent(node);
        }
    }

    public void setMagnetizPane(Node node) {
        if (node == null) {
            return;
        }

        if (this.magnetizPane != null) {
            this.magnetizPane.setContent(node);
        }
    }

    public void setHybridPane(Node node) {
        if (node == null) {
            return;
        }

        if (this.hybridPane != null) {
            this.hybridPane.setContent(node);
        }
    }

    public void setHubbardPane(Node node) {
        if (node == null) {
            return;
        }

        if (this.hubbardPane != null) {
            this.hubbardPane.setContent(node);
        }
    }

    public void setVdwPane(Node node) {
        if (node == null) {
            return;
        }

        if (this.vdwPane != null) {
            this.vdwPane.setContent(node);
        }
    }

    public void setIsolatedPane(Node node) {
        if (node == null) {
            return;
        }

        if (this.isolatedPane != null) {
            this.isolatedPane.setContent(node);
        }
    }
}
