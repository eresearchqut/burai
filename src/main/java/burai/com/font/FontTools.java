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

package burai.com.font;

import burai.com.env.Environments;

public final class FontTools {

    private FontTools() {
        // NOP
    }

    public static String getBlackFont() {
        if (Environments.isLinux()) {
            return "Noto Sans CJK JP Black";

        } else {
            return "Arial Black";
        }
    }

    public static String getRomanFont() {
        if (Environments.isLinux()) {
            //return "Nimbus Roman No9 L";
            return "FreeSerif";

        } else {
            return "Times New Roman";
        }
    }

}
