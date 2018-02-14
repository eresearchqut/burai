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

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.stage.Stage;
import burai.app.QEFXAppComponent;
import burai.app.QEFXMain;
import burai.app.QEFXMainController;
import burai.com.life.Dead;
import burai.com.life.Life;

public class QEFXWebPopup extends QEFXAppComponent<QEFXWebController> {

    private static final double INITIAL_WIDTH = -1.0;
    private static final double INITIAL_HEIGHT = -1.0;
    private static final double STAGE_PADDING = 20.0;

    private Stage stage;

    private Dead onDead;

    public QEFXWebPopup(QEFXMainController mainController) throws IOException {
        super("QEFXWebPopup.fxml", new QEFXWebController(mainController, "about:blank"));

        this.createStage();
        this.createOnDead();
        this.setupOnTitleChanged();
        this.setupPopupCloser();
    }

    private void createStage() {
        BorderPane root = new BorderPane();
        root.setCenter(this.node);

        Scene scene = new Scene(root);
        QEFXMain.initializeStyleSheets(scene.getStylesheets());

        this.stage = new Stage();
        this.stage.setScene(scene);
        QEFXMain.initializeTitleBarIcon(this.stage);

        this.stage.setOnHidden(event -> {
            if (Life.getInstance().isAlive()) {
                Life.getInstance().removeOnDead(this.onDead);
            }

            WebEngine engine = this.getEngine();
            if (engine != null) {
                engine.load("about:blank");
            }
        });
    }

    private void createOnDead() {
        this.onDead = () -> {
            this.hide();
        };
    }

    private void setupOnTitleChanged() {
        WebEngine engine = this.getEngine();
        if (engine == null) {
            return;
        }

        engine.titleProperty().addListener(o -> {
            String title = engine.getTitle();
            if (title != null) {
                this.stage.setTitle(title);
            }
        });
    }

    private void setupPopupCloser() {
        WebEngine engine = this.getEngine();
        if (engine == null) {
            return;
        }

        engine.setOnVisibilityChanged(event -> {
            if (event != null && (!event.getData())) {
                this.stage.hide();
            }
        });
    }

    public WebEngine getEngine() {
        if (this.controller == null) {
            return null;
        }

        return this.controller.getEngine();
    }

    public void show() {
        Stage mainStage = null;
        if (this.controller != null) {
            mainStage = this.controller.getStage();
        }

        double x = 0.0;
        double y = 0.0;
        if (mainStage != null) {
            x = mainStage.getX();
            y = mainStage.getY();
        }

        Life.getInstance().addOnDead(this.onDead);

        this.stage.setResizable(true);
        this.stage.setWidth(INITIAL_WIDTH);
        this.stage.setHeight(INITIAL_HEIGHT);
        this.stage.setX(x + STAGE_PADDING);
        this.stage.setY(y + STAGE_PADDING);
        this.stage.show();
    }

    public void hide() {
        this.stage.hide();
    }
}
