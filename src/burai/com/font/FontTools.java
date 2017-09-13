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

    public static String getArialBlack() {
        if (Environments.isLinux()) {
            // TODO
            return "";

        } else {
            return "Arial Black";
        }
    }

    public static String getTimesNewRoman() {
        if (Environments.isLinux()) {
            // TODO
            return "";

        } else {
            return "Times New Roman";
        }
    }

}
