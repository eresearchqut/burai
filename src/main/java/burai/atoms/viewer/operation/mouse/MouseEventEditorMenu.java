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

package burai.atoms.viewer.operation.mouse;

import burai.atoms.viewer.operation.ViewerEventEditorMenu;
import burai.atoms.viewer.operation.ViewerEventManager;
import javafx.scene.input.MouseEvent;

public class MouseEventEditorMenu extends ViewerEventEditorMenu<MouseEvent> implements MouseEventKernel {

    private MouseEventProxy proxy;

    public MouseEventEditorMenu(MouseEventHandler handler) {
        super();
        this.proxy = new MouseEventProxy(handler, this);
    }

    @Override
    public void perform(ViewerEventManager manager, MouseEvent event) {
        this.proxy.perform(manager, event);
    }

    @Override
    public void performOnMousePressed(MouseEvent event) {
        // NOP
    }

    @Override
    public void performOnMouseDragged(MouseEvent event) {
        // NOP
    }

    @Override
    public void performOnMouseReleased(MouseEvent event) {
        // NOP
    }
}
