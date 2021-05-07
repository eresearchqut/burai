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

package burai.app.project.viewer.result;

import java.io.IOException;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import burai.app.QEFXAppComponent;
import burai.com.keys.PriorKeyEvent;

public abstract class QEFXResultViewer<V extends QEFXResultViewerController> extends QEFXAppComponent<V> {

    public QEFXResultViewer(Node node, V controller) {
        super(node, controller);

        if (this.node != null) {
            this.setupKeys(this.node);
        }
    }

    public QEFXResultViewer(String fileFXML, V controller) throws IOException {
        super(fileFXML, controller);

        if (this.node != null) {
            this.setupKeys(this.node);
        }
    }

    public void reload() {
        if (this.controller != null) {
            this.controller.reload();
        }
    }

    private void setupKeys(Node node) {
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

            if (KeyCode.F5.equals(event.getCode())) {
                // F5
                this.controller.reloadSafely();
            }
        });
    }
}
