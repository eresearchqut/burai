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

package burai.app.project.editor;

import burai.app.project.ProjectAction;
import burai.app.project.ProjectActions;
import burai.app.project.QEFXProjectController;
import burai.project.Project;

public abstract class EditorActions extends ProjectActions<String> {

    public EditorActions(Project project, QEFXProjectController controller) {
        super(project, controller);

        this.setupOnEditorSelected();
    }

    public void attach() {
        this.setupOnEditorSelected();
    }

    public void detach() {
        this.controller.setOnEditorSelected(null);
    }

    private void setupOnEditorSelected() {
        if (this.controller == null) {
            return;
        }

        this.controller.setOnEditorSelected(text -> {
            if (text == null || text.isEmpty()) {
                return;
            }

            ProjectAction action = null;
            if (this.actions != null) {
                action = this.actions.get(text);
            }

            if (action != null && this.controller != null) {
                action.actionOnProject(this.controller);
            }
        });
    }
}
