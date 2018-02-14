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

import java.io.IOException;

import burai.app.QEFXMainController;
import burai.app.project.editor.QEFXEditorComponent;
import burai.input.QEInput;

public class QEFXScf extends QEFXEditorComponent<QEFXScfController> {

    private QEFXStandard standardComponent;
    private QEFXElectron electronComponent;
    private QEFXMagnetiz magnetizComponent;
    private QEFXHybrid hybridComponent;
    private QEFXHubbard hubbardComponent;
    private QEFXVdw vdwComponent;
    private QEFXIsolated isolatedComponent;

    public QEFXScf(QEFXMainController mainController, QEInput input) throws IOException {
        super("QEFXScf.fxml", new QEFXScfController(mainController));

        if (input == null) {
            throw new IllegalArgumentException("input is null.");
        }

        this.createComponents(input);
    }

    private void createComponents(QEInput input) throws IOException {
        QEFXMainController mainController = null;
        if (this.controller != null) {
            mainController = this.controller.getMainController();
        }

        this.standardComponent = new QEFXStandard(mainController, input);
        this.electronComponent = new QEFXElectron(mainController, input);
        this.magnetizComponent = new QEFXMagnetiz(mainController, input);
        this.hybridComponent = new QEFXHybrid(mainController, input);
        this.hubbardComponent = new QEFXHubbard(mainController, input);
        this.vdwComponent = new QEFXVdw(mainController, input);
        this.isolatedComponent = new QEFXIsolated(mainController, input);

        if (this.controller != null) {
            this.controller.setStandardPane(this.standardComponent.getNode());
            this.controller.setElectronPane(this.electronComponent.getNode());
            this.controller.setMagnetizPane(this.magnetizComponent.getNode());
            this.controller.setHybridPane(this.hybridComponent.getNode());
            this.controller.setHubbardPane(this.hubbardComponent.getNode());
            this.controller.setVdwPane(this.vdwComponent.getNode());
            this.controller.setIsolatedPane(this.isolatedComponent.getNode());
        }
    }

    @Override
    public void notifyEditorOpened() {
        this.standardComponent.notifyEditorOpened();
        this.electronComponent.notifyEditorOpened();
        this.magnetizComponent.notifyEditorOpened();
        this.hybridComponent.notifyEditorOpened();
        this.hubbardComponent.notifyEditorOpened();
        this.vdwComponent.notifyEditorOpened();
        this.isolatedComponent.notifyEditorOpened();
    }
}
