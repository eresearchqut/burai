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

package burai.app.preloader;

import java.io.IOException;

import burai.app.QEFXAppComponent;
import burai.app.QEFXAppController;
import burai.app.QEFXMain;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class QEFXSplash extends QEFXAppComponent<QEFXAppController> {

    private Stage splashStage;

    public QEFXSplash() throws IOException {
        super("QEFXSplash.fxml", new QEFXSplashController());
        this.splashStage = null;
    }

    public void showSplash() {
        if (this.splashStage == null) {
            this.splashStage = new Stage(StageStyle.TRANSPARENT);
            if (this.node != null && this.node instanceof Parent) {
                QEFXMain.initializeStyleSheets(((Parent) this.node).getStylesheets());
                this.splashStage.setScene(new Scene((Parent) this.node));
            }
        }

        this.splashStage.show();
    }

    public void hideSplash() {
        if (this.splashStage != null) {
            this.splashStage.close();
        }
    }
}
