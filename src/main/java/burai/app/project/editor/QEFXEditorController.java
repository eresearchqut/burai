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

package burai.app.project.editor;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import burai.app.QEFXAppController;
import burai.app.QEFXMainController;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;

public abstract class QEFXEditorController extends QEFXAppController {

    @FXML
    protected Accordion accordion;

    public QEFXEditorController(QEFXMainController mainController) {
        super(mainController);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setupConfigAccordion();
    }

    private void setupConfigAccordion() {
        if (this.accordion == null) {
            return;
        }

        List<TitledPane> panes = this.accordion.getPanes();
        if (panes != null && !panes.isEmpty()) {
            TitledPane pane = panes.get(0);
            if (pane != null) {
                this.accordion.setExpandedPane(pane);
            }
        }
    }
}
