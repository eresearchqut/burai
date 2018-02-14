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

package burai.run;

import javafx.application.Platform;
import burai.input.QEInput;
import burai.project.Project;

public class FXQEInputFactory {

    private RunningType type;

    private QEInput qeInput;

    private boolean hasQEInput;

    protected FXQEInputFactory(RunningType type) {
        if (type == null) {
            throw new IllegalArgumentException("type is null.");
        }

        this.type = type;
        this.qeInput = null;
        this.hasQEInput = false;
    }

    protected QEInput getQEInput(Project project) {
        if (project == null) {
            return null;
        }

        this.hasQEInput = false;

        Platform.runLater(() -> {
            project.resolveQEInputs();
            this.qeInput = this.type.getQEInput(project);

            synchronized (this) {
                this.hasQEInput = true;
                this.notifyAll();
            }
        });

        synchronized (this) {
            while (!this.hasQEInput) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        this.hasQEInput = false;

        return this.qeInput;
    }
}
