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

package burai.atoms.viewer.operation.key;

import javafx.scene.input.KeyEvent;
import burai.atoms.viewer.operation.ViewerEventHandler;
import burai.atoms.viewer.operation.ViewerEventManager;
import burai.com.keys.PriorKeyEvent;

public class KeyPressedHandler extends ViewerEventHandler<KeyEvent> {

    public KeyPressedHandler(ViewerEventManager manager, boolean silent) {
        super(manager);

        if (!silent) {
            this.addKernel(new KeyPressedCompassPicking());
            this.addKernel(new KeyPressedCompass());
            this.addKernel(new KeyPressedEditorMenu());
            this.addKernel(new KeyPressedScope());
        }

        this.addKernel(new KeyPressedRegular(silent));
    }

    @Override
    public void handle(KeyEvent event) {
        if (event == null) {
            return;
        }

        if (PriorKeyEvent.isPriorKeyEvent(event)) {
            return;
        }

        event.consume();

        super.handle(event);
    }
}
