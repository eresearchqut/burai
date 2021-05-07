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

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import burai.project.Project;

public class ExplorerQueue extends FileQueue {

    private boolean alive;

    private LinkedList<FileElement> fileElements;

    protected ExplorerQueue(QEFXExplorerBody explorerBody, List<Project> shownProjects) throws IOException {
        if (explorerBody == null) {
            throw new IllegalArgumentException("explorerBody is null.");
        }

        this.alive = true;

        this.fileElements = new LinkedList<FileElement>();

        this.setupFileElements(explorerBody, shownProjects);
    }

    private void setupFileElements(QEFXExplorerBody explorerBody, List<Project> shownProjects) throws IOException {
        FileLister fileLister = new FileLister(explorerBody);
        fileLister.list(shownProjects);
        String[] fileNames = fileLister.getFileNames();
        Project[] fileProjects = fileLister.getFileProjects();

        boolean hasFileProjects = false;
        if (fileProjects != null && fileProjects.length >= fileNames.length) {
            hasFileProjects = true;
        }

        if (fileNames != null && fileNames.length > 0) {
            for (int i = 0; i < fileNames.length; i++) {
                String fileName = fileNames[i];
                Project fileProject = hasFileProjects ? fileProjects[i] : null;
                FileElement fileElement = new FileElement(fileName);
                fileElement.setProject(fileProject);
                this.fileElements.add(fileElement);
            }
        }
    }

    @Override
    public synchronized FileElement pollFileElement() {
        while (this.alive && this.fileElements.isEmpty()) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (this.alive) {
            return this.fileElements.poll();
        } else {
            return null;
        }
    }

    @Override
    public synchronized FileElement peekFileElement() {
        while (this.alive && this.fileElements.isEmpty()) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (this.alive) {
            return this.fileElements.peek();
        } else {
            return null;
        }
    }

    @Override
    public synchronized void addFileElement(FileElement fileElement) {
        if (fileElement == null) {
            return;
        }

        this.fileElements.offer(fileElement);

        this.notifyAll();
    }

    @Override
    public synchronized boolean hasFileElements() {
        return !this.fileElements.isEmpty();
    }

    @Override
    public synchronized void stopFileElements() {
        this.alive = false;

        this.notifyAll();
    }
}
