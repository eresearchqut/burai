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

package burai.app.project.viewer.run;

import burai.app.QEFXMainController;
import burai.app.project.QEFXProjectController;
import burai.run.RunningManager;
import burai.run.RunningNode;
import burai.ssh.SSHJob;

public class RunAction {

    private QEFXProjectController controller;

    public RunAction(QEFXProjectController controller) {
        if (controller == null) {
            throw new IllegalArgumentException("controller is null.");
        }

        this.controller = controller;
    }

    public void runCalculation(RunEvent runEvent) {
        if (runEvent == null) {
            return;
        }

        if (runEvent.getRunningNode() != null) {
            this.runOnLocalMachine(runEvent.getRunningNode());

        } else if (runEvent.getSSHJob() != null) {
            this.runOnSSHServer(runEvent.getSSHJob());
        }
    }

    private void runOnLocalMachine(RunningNode runningNode) {
        if (runningNode == null) {
            return;
        }

        RunningManager.getInstance().addNode(runningNode);

        QEFXMainController mainController = this.controller.getMainController();
        if (mainController == null) {
            return;
        }

        mainController.offerOnHomeTabSelected(explorerFacade -> {
            if (explorerFacade != null && (!explorerFacade.isCalculatingMode())) {
                explorerFacade.setCalculatingMode();
            }
        });

        mainController.showHome();
    }

    private void runOnSSHServer(SSHJob sshJob) {
        if (sshJob == null) {
            return;
        }

        sshJob.postJobToServer();
    }
}
