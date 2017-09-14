/*
 * Copyright (C) 2017 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
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
            return "Nimbus Roman No9 L";

        } else {
            return "Times New Roman";
        }
    }

}
