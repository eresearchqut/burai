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

package burai.app.project.viewer.result;

import burai.app.QEFXAppController;
import burai.app.project.QEFXProjectController;

public abstract class QEFXResultViewerController extends QEFXAppController {

    private static final long INTER_LOADING_TIME = 2500L;

    private boolean loading;
    private Object loadingLock;

    protected QEFXProjectController projectController;

    public QEFXResultViewerController(QEFXProjectController projectController) {
        super(projectController == null ? null : projectController.getMainController());

        if (projectController == null) {
            throw new IllegalArgumentException("projectController is null.");
        }

        this.loading = false;
        this.loadingLock = new Object();

        this.projectController = projectController;
    }

    public abstract void reload();

    public void reloadSafely() {
        synchronized (this.loadingLock) {
            if (this.loading) {
                return;
            }

            this.loading = true;
        }

        this.reload();

        Thread thread = new Thread(() -> {
            synchronized (this.loadingLock) {
                try {
                    this.loadingLock.wait(INTER_LOADING_TIME);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                this.loading = false;
            }
        });

        thread.start();
    }
}
