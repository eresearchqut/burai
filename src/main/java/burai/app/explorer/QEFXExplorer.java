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

package burai.app.explorer;

import java.io.IOException;

import burai.app.QEFXAppComponent;
import burai.app.QEFXMainController;

public class QEFXExplorer extends QEFXAppComponent<QEFXExplorerController> {

    public QEFXExplorer(QEFXMainController mainController) throws IOException {
        super("QEFXExplorer.fxml", new QEFXExplorerController(mainController));
    }

    public QEFXExplorerFacade getFacade() {
        if(this.controller == null) {
            return null;
        }

        return new QEFXExplorerFacade(this.controller);
    }

}
