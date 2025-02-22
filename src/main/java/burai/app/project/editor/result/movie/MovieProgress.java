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

package burai.app.project.editor.result.movie;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class MovieProgress {

    private File file;

    private QEFXMovieProgressDialog dialog;

    protected MovieProgress(File file) {
        this.file = file;
        this.dialog = null;
    }

    protected void setProgress(double value) {
        if (this.dialog != null) {
            this.dialog.setProgress(value);
        }
    }

    protected void showProgress() {
        this.showProgress(null);
    }

    protected void showProgress(EventHandler<ActionEvent> handler) {
        if (this.dialog != null) {
            return;
        }

        if (this.file != null) {
            this.dialog = new QEFXMovieProgressDialog(this.file);
            this.dialog.setOnStopAction(handler);
            this.dialog.showProgress();
        }
    }

    protected void hideProgress() {
        if (this.dialog == null) {
            return;
        }

        this.dialog.hideProgress();
        this.dialog = null;
    }
}
