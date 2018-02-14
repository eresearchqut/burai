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

package burai.com.keys;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public final class PriorKeyEvent {

    private PriorKeyEvent() {
        // NOP
    }

    public static boolean isPriorKeyEvent(KeyEvent event) {
        if (event == null) {
            return false;
        }

        KeyCode keyCode = event.getCode();

        // focus next item
        if (KeyCode.TAB.equals(keyCode)) {
            return true;
        }

        // quit system
        if (event.isShortcutDown() && KeyCode.Q.equals(keyCode)) {
            return true;
        }

        // close window
        if (event.isShortcutDown() && KeyCode.W.equals(keyCode)) {
            return true;
        }

        // save data
        if (event.isShortcutDown() && KeyCode.S.equals(keyCode)) {
            return true;
        }

        // return screen
        if (event.isShortcutDown() && KeyCode.LEFT.equals(keyCode)) {
            return true;
        }

        // print screen
        if (KeyCode.PRINTSCREEN.equals(keyCode)) {
            return true;
        }

        return false;
    }
}
