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
import burai.atoms.viewer.operation.ViewerEventManager;
import burai.atoms.viewer.operation.ViewerEventRegular;
import burai.atoms.viewer.operation.editor.CenterMenuItem;
import burai.atoms.viewer.operation.editor.DeleteMenuItem;
import burai.atoms.viewer.operation.editor.NotSelectAnyMenuItem;
import burai.atoms.viewer.operation.editor.RedoMenuItem;
import burai.atoms.viewer.operation.editor.RenameMenuItem;
import burai.atoms.viewer.operation.editor.SelectAllMenuItem;
import burai.atoms.viewer.operation.editor.UndoMenuItem;

public class KeyPressedRegular extends ViewerEventRegular<KeyEvent> {

    private ViewerEventManager manager;

    private Map<KeyPressedAnsatz, KeyPressedKernel> keyKernels;

    public KeyPressedRegular(boolean silent) {
        super();
        this.manager = null;
        this.createKeyKernels(silent);
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

        KeyPressedKernel keyKernel =
                this.keyKernels.get(new KeyPressedAnsatz(keyCode, shortStat, shiftStat, altStat));

        if (keyKernel != null) {
            keyKernel.performOnKeyPressed();
        }
    }

    private void createKeyKernels(boolean silent) {
        this.keyKernels = new HashMap<KeyPressedAnsatz, KeyPressedKernel>();

        // Shortcut key is pressed
        this.keyKernels.put(new KeyPressedAnsatz(KeyCode.C, true, false, false),
                () -> new CenterMenuItem(this.manager).performAction());

        if (!silent) {
            this.keyKernels.put(new KeyPressedAnsatz(KeyCode.A, true, false, false),
                    () -> new SelectAllMenuItem(this.manager).performAction());
            this.keyKernels.put(new KeyPressedAnsatz(KeyCode.D, true, false, false),
                    () -> new DeleteMenuItem(this.manager).performAction());
            this.keyKernels.put(new KeyPressedAnsatz(KeyCode.R, true, false, false),
                    () -> new RenameMenuItem(this.manager).performAction());
            this.keyKernels.put(new KeyPressedAnsatz(KeyCode.Z, true, false, false),
                    () -> new UndoMenuItem(this.manager).performAction());
        }

        // Shift key is pressed
        this.keyKernels.put(new KeyPressedAnsatz(KeyCode.RIGHT, false, true, false),
                () -> this.translateRight());
        this.keyKernels.put(new KeyPressedAnsatz(KeyCode.LEFT, false, true, false),
                () -> this.translateLeft());
        this.keyKernels.put(new KeyPressedAnsatz(KeyCode.UP, false, true, false),
                () -> this.translateUp());
        this.keyKernels.put(new KeyPressedAnsatz(KeyCode.PAGE_UP, false, true, false),
                () -> this.translateUp());
        this.keyKernels.put(new KeyPressedAnsatz(KeyCode.DOWN, false, true, false),
                () -> this.translateDown());
        this.keyKernels.put(new KeyPressedAnsatz(KeyCode.PAGE_DOWN, false, true, false),
                () -> this.translateDown());

        // Alt key is pressed
        this.keyKernels.put(new KeyPressedAnsatz(KeyCode.UP, false, false, true),
                () -> this.scaleUp());
        this.keyKernels.put(new KeyPressedAnsatz(KeyCode.PAGE_UP, false, false, true),
                () -> this.scaleUp());
        this.keyKernels.put(new KeyPressedAnsatz(KeyCode.DOWN, false, false, true),
                () -> this.scaleDown());
        this.keyKernels.put(new KeyPressedAnsatz(KeyCode.PAGE_DOWN, false, false, true),
                () -> this.scaleDown());

        // Shortcut+Shift key is pressed
        this.keyKernels.put(new KeyPressedAnsatz(KeyCode.Z, true, true, false),
                () -> new RedoMenuItem(this.manager).performAction());

        // any key is not pressed
        this.keyKernels.put(new KeyPressedAnsatz(KeyCode.BACK_SPACE),
                () -> new NotSelectAnyMenuItem(this.manager).performAction());
        this.keyKernels.put(new KeyPressedAnsatz(KeyCode.ESCAPE),
                () -> new NotSelectAnyMenuItem(this.manager).performAction());
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
        this.manager.getAtomsViewer().appendCellRotation(KEY_ROTATE_SPEED, 0.0, -1.0, 0.0);
    }

    private void rotateLeft() {
        this.manager.getAtomsViewer().appendCellRotation(KEY_ROTATE_SPEED, 0.0, 1.0, 0.0);
    }

    private void rotateUp() {
        this.manager.getAtomsViewer().appendCellRotation(KEY_ROTATE_SPEED, -1.0, 0.0, 0.0);
    }

    private void rotateDown() {
        this.manager.getAtomsViewer().appendCellRotation(KEY_ROTATE_SPEED, 1.0, 0.0, 0.0);
    }

    private void translateRight() {
        this.manager.getAtomsViewer().appendCellTranslation(KEY_TRANS_SPEED, 0.0, 0.0);
    }

    private void translateLeft() {
        this.manager.getAtomsViewer().appendCellTranslation(-KEY_TRANS_SPEED, 0.0, 0.0);
    }

    private void translateUp() {
        this.manager.getAtomsViewer().appendCellTranslation(0.0, -KEY_TRANS_SPEED, 0.0);
    }

    private void translateDown() {
        this.manager.getAtomsViewer().appendCellTranslation(0.0, KEY_TRANS_SPEED, 0.0);
    }

    private void scaleUp() {
        this.manager.getAtomsViewer().appendCellScale(1.0 / KEY_SCALE_SPEED);
    }

    private void scaleDown() {
        this.manager.getAtomsViewer().appendCellScale(KEY_SCALE_SPEED);
    }
}
