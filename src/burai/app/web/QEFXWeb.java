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

package burai.app.web;

import java.io.IOException;

import javafx.scene.web.WebEngine;
import burai.app.QEFXAppComponent;
import burai.app.QEFXMainController;

public class QEFXWeb extends QEFXAppComponent<QEFXWebController> {

    public QEFXWeb(QEFXMainController mainController, String url) throws IOException {
        super("QEFXWeb.fxml", new QEFXWebController(mainController, url));

        this.setupOnTabClosed();
    }

    private void setupOnTabClosed() {
        WebEngine engine = this.getEngine();
        if (engine == null) {
            return;
        }

        engine.setOnVisibilityChanged(event -> {
            if (event != null && (!event.getData())) {
                QEFXMainController mainController = null;
                if (this.controller != null) {
                    mainController = this.controller.getMainController();
                }

                if (mainController != null) {
                    mainController.hideWebPage(engine);
                }
            }
        });
    }

    public WebEngine getEngine() {
        if (this.controller == null) {
            return null;
        }

        return this.controller.getEngine();
    }
}
