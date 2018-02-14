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

import javafx.application.Preloader;
import javafx.stage.Stage;

public class QEFXPreloader extends Preloader {

    private QEFXSplash splash;

    @Override
    public void start(Stage primaryStage) {
        try {
            this.splash = new QEFXSplash();
            this.splash.showSplash();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification stateChangeNotification) {
        if (stateChangeNotification == null) {
            return;
        }

        if (stateChangeNotification.getType() == StateChangeNotification.Type.BEFORE_START) {
            if (this.splash != null) {
                this.splash.hideSplash();
            }
        }
    }
}
