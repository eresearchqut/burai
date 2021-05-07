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

package burai.app.explorer.body;

public abstract class FileQueue {

    private FileElementDeleted onFileElementDeleted;

    protected FileQueue() {
        this.onFileElementDeleted = null;
    }

    public abstract FileElement pollFileElement();

    public abstract FileElement peekFileElement();

    public abstract void addFileElement(FileElement fileElement);

    public abstract boolean hasFileElements();

    public abstract void stopFileElements();

    public void setOnFileElementDeleted(FileElementDeleted onFileElementDeleted) {
        this.onFileElementDeleted = onFileElementDeleted;
    }

    protected void actionOnFileElementDeleted(FileElement fileElement) {
        if (this.onFileElementDeleted != null) {
            this.onFileElementDeleted.onFileElementDeleted(fileElement);
        }
    }
}
