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

import burai.run.RunningNode;
import burai.ssh.SSHJob;

public class RunEvent {

    private RunningNode runningNode;

    private SSHJob sshJob;

    private RunEvent() {
        this.runningNode = null;
        this.sshJob = null;
    }

    public RunEvent(RunningNode runningNode) {
        this();
        this.runningNode = runningNode;
    }

    public RunEvent(SSHJob sshJob) {
        this();
        this.sshJob = sshJob;
    }

    public RunningNode getRunningNode() {
        return this.runningNode;
    }

    public SSHJob getSSHJob() {
        return this.sshJob;
    }
}
