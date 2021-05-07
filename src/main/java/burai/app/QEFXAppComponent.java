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

package burai.app;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

public abstract class QEFXAppComponent<T extends QEFXAppController> {

    protected Node node;

    protected T controller;

    public QEFXAppComponent(Node node, T controller) {
        if (node == null) {
            throw new IllegalArgumentException("node is null.");
        }

        if (controller == null) {
            throw new IllegalArgumentException("controller is null.");
        }

        this.node = node;
        this.controller = controller;

        this.initializeNode();
    }

    public QEFXAppComponent(String fileFXML, T controller) throws IOException {
        if (fileFXML == null || fileFXML.trim().isEmpty()) {
            throw new IllegalArgumentException("file name of FXML is empty.");
        }

        if (controller == null) {
            throw new IllegalArgumentException("controller is null.");
        }

        this.controller = controller;

        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource(fileFXML));
        fxmlLoader.setController(this.controller);
        this.node = fxmlLoader.load();

        this.initializeNode();
    }

    private void initializeNode() {
        AnchorPane.setBottomAnchor(this.node, 0.0);
        AnchorPane.setTopAnchor(this.node, 0.0);
        AnchorPane.setLeftAnchor(this.node, 0.0);
        AnchorPane.setRightAnchor(this.node, 0.0);
    }

    public final Node getNode() {
        return this.node;
    }

    public final T getController() {
        return this.controller;
    }
}
