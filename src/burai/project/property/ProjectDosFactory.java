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

package burai.project.property;

public class ProjectDosFactory {

    private String path;

    private String prefix;

    private ProjectDos dos;

    public ProjectDosFactory() {
        this.path = null;
        this.prefix = null;
        this.dos = null;
    }

    protected void setPath(String path, String prefix) {
        this.path = path;
        this.prefix = prefix;
    }

    public ProjectDos getProjectDos() {
        if (this.path == null || this.path.isEmpty() || this.prefix == null || this.prefix.isEmpty()) {
            this.dos = null;

        } else if (this.dos != null && this.path.equals(this.dos.getPath()) && this.prefix.equals(this.dos.getPrefix())) {
            this.dos.reload();

        } else {
            this.dos = new ProjectDos(this.path, this.prefix);
        }

        return this.dos;
    }
}
