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

package burai.atoms.viewer.operation;

import javafx.event.Event;

public abstract class ViewerEventEditorMenu<T extends Event> implements ViewerEventKernel<T> {

    public ViewerEventEditorMenu() {
        // NOP
    }

    @Override
    public final boolean isToPerform(ViewerEventManager manager) {
        if(manager == null) {
            return false;
        }

        return manager.isEditorMenuBusy();
    }
}
