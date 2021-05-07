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

public final class KeyCodeConverter {

    private KeyCodeConverter() {
        // NOP
    }

    public static char toCharacter(KeyCode code) {
        String codeStr = null;
        if (code != null) {
            codeStr = code.getName();
        }
        if (codeStr == null || codeStr.isEmpty()) {
            return 0;
        }

        if (codeStr.length() == 1) {
            return codeStr.charAt(0);
        }

        char codeChar = 0;

        if (KeyCode.MINUS.equals(code)) {
            codeChar = '-';

        } else if (KeyCode.PLUS.equals(code)) {
            codeChar = '+';

        } else if (KeyCode.CIRCUMFLEX.equals(code)) {
            codeChar = '^';

        } else if (KeyCode.BACK_SLASH.equals(code)) {
            codeChar = '\\';

        } else if (KeyCode.AT.equals(code)) {
            codeChar = '@';

        } else if (KeyCode.COLON.equals(code)) {
            codeChar = ':';

        } else if (KeyCode.SEMICOLON.equals(code)) {
            codeChar = ';';

        } else if (KeyCode.OPEN_BRACKET.equals(code)) {
            codeChar = '[';

        } else if (KeyCode.CLOSE_BRACKET.equals(code)) {
            codeChar = ']';

        } else if (KeyCode.COMMA.equals(code)) {
            codeChar = ',';

        } else if (KeyCode.PERIOD.equals(code)) {
            codeChar = '.';

        } else if (KeyCode.SLASH.equals(code)) {
            codeChar = '/';

        } else if (KeyCode.UNDERSCORE.equals(code)) {
            codeChar = '_';
        }

        return codeChar;
    }
}
