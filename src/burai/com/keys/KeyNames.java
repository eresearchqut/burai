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
import burai.com.env.Environments;

public final class KeyNames {

    private KeyNames() {
        // NOP
    }

    public static String getShortcut() {
        return getShortcut(null);
    }

    public static String getShortcut(KeyCode keyCode) {
        String comb = Environments.isMac() ? "Command" : "Ctrl";
        String name = keyCode == null ? null : keyCode.getName();
        if (name == null) {
            return comb;
        } else {
            return comb + "+" + name;
        }
    }

    public static String getShortcutShift() {
        return getShortcutShift(null);
    }

    public static String getShortcutShift(KeyCode keyCode) {
        String comb = Environments.isMac() ? "Command+Shift" : "Ctrl+Shift";
        String name = keyCode == null ? null : keyCode.getName();
        if (name == null) {
            return comb;
        } else {
            return comb + "+" + name;
        }
    }

    public static String getAlt() {
        return getAlt(null);
    }

    public static String getAlt(KeyCode keyCode) {
        String comb = Environments.isMac() ? "Option" : "Alt";
        String name = keyCode == null ? null : keyCode.getName();
        if (name == null) {
            return comb;
        } else {
            return comb + "+" + name;
        }
    }
}
