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

import javafx.fxml.Initializable;
import javafx.stage.Stage;

public abstract class QEFXAppController implements Initializable {

    protected QEFXMainController mainController;

    public QEFXAppController() {
        this.mainController = null;
    }

    public QEFXAppController(QEFXMainController mainController) {
        if (mainController == null) {
            throw new IllegalArgumentException("mainController is null.");
        }

        this.mainController = mainController;
    }

    public QEFXMainController getMainController() {
        return this.mainController;
    }

    public Stage getStage() {
        if (this.mainController != null) {
            return this.mainController.getStage();
        }

        return null;
    }

    public void quitSystem() {
        if (this.mainController != null) {
            this.mainController.quitSystem();
        }
    }

    public void setMaximized(boolean maximized) {
        if (this.mainController != null) {
            this.mainController.setMaximized(maximized);
        }
    }

    public void setFullScreen(boolean fullScreen) {
        if (this.mainController != null) {
            this.mainController.setFullScreen(fullScreen);
        }
    }

    public void setResizable(boolean resizable) {
        if (this.mainController != null) {
            this.mainController.setResizable(resizable);
        }
    }
}
