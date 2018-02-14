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

import burai.project.Project;
import burai.run.RunningNode;

public class FileElement {

    private String name;

    private int position;

    private boolean swapping;

    private Project project;

    private RunningNode runningNode;

    protected FileElement(String name) {
        this(name, -1);
    }

    protected FileElement(String name, int position) {
        this(name, position, false);
    }

    protected FileElement(String name, int position, boolean swapping) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name is empty.");
        }

        this.name = name;
        this.position = position;
        this.swapping = swapping;
        this.project = null;
        this.runningNode = null;
    }

    protected String getName() {
        return this.name;
    }

    protected int getPosition() {
        return this.position;
    }

    protected boolean isSwapping() {
        return this.swapping;
    }

    protected void setProject(Project project) {
        this.project = project;
    }

    protected Project getProject() {
        return this.project;
    }

    protected void setRunningNode(RunningNode runningNode) {
        this.runningNode = runningNode;
    }

    protected RunningNode getRunningNode() {
        return this.runningNode;
    }
}
