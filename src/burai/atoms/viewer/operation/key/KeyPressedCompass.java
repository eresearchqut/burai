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

import java.util.HashMap;
import java.util.Map;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import burai.atoms.viewer.operation.ViewerEventCompass;
import burai.atoms.viewer.operation.ViewerEventManager;

public class KeyPressedCompass extends ViewerEventCompass<KeyEvent> {

    private ViewerEventManager manager;

    private Map<KeyPressedAnsatz, KeyPressedKernel> keyKernels;

    public KeyPressedCompass() {
        super();
        this.manager = null;
        this.createKeyKernels();
    }

    @Override
    public void perform(ViewerEventManager manager, KeyEvent event) {
        if (manager == null) {
            return;
        }

        if (event == null) {
            return;
        }

        this.manager = manager;

        KeyCode keyCode = event.getCode();
        boolean shortStat = event.isShortcutDown();
        boolean shiftStat = event.isShiftDown();
        boolean altStat = event.isAltDown();

        KeyPressedKernel keyKernel = this.keyKernels.get(new KeyPressedAnsatz(keyCode, shortStat, shiftStat, altStat));
        if (keyKernel != null) {
            keyKernel.performOnKeyPressed();
        }
    }

    private void createKeyKernels() {
        this.keyKernels = new HashMap<KeyPressedAnsatz, KeyPressedKernel>();

        // Shortcut key is pressed
        this.keyKernels.put(new KeyPressedAnsatz(KeyCode.C, true, false, false),
                () -> this.manager.getAtomsViewer().setCompassToCenter());

        // any key is not pressed
        this.keyKernels.put(new KeyPressedAnsatz(KeyCode.ESCAPE),
                () -> this.manager.exitCompassMode());
        this.keyKernels.put(new KeyPressedAnsatz(KeyCode.RIGHT),
                () -> this.rotateRight());
        this.keyKernels.put(new KeyPressedAnsatz(KeyCode.LEFT),
                () -> this.rotateLeft());
        this.keyKernels.put(new KeyPressedAnsatz(KeyCode.UP),
                () -> this.rotateUp());
        this.keyKernels.put(new KeyPressedAnsatz(KeyCode.PAGE_UP),
                () -> this.rotateUp());
        this.keyKernels.put(new KeyPressedAnsatz(KeyCode.DOWN),
                () -> this.rotateDown());
        this.keyKernels.put(new KeyPressedAnsatz(KeyCode.PAGE_DOWN),
                () -> this.rotateDown());
    }

    private void rotateRight() {
        this.manager.getAtomsViewer().appendCompassRotation(KEY_ROTATE_SPEED, 0.0, -1.0, 0.0);
    }

    private void rotateLeft() {
        this.manager.getAtomsViewer().appendCompassRotation(KEY_ROTATE_SPEED, 0.0, 1.0, 0.0);
    }

    private void rotateUp() {
        this.manager.getAtomsViewer().appendCompassRotation(KEY_ROTATE_SPEED, -1.0, 0.0, 0.0);
    }

    private void rotateDown() {
        this.manager.getAtomsViewer().appendCompassRotation(KEY_ROTATE_SPEED, 1.0, 0.0, 0.0);
    }
}
