/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.explorer.body.menuitem;

import burai.app.explorer.body.QEFXExplorerBody;
import burai.app.icon.QEFXIcon;
import burai.app.icon.QEFXRunningIcon;

public class QEFXRenameFileMenuItem extends QEFXMenuItem {

    public QEFXRenameFileMenuItem(QEFXIcon icon, QEFXExplorerBody body) {
        super("Rename file");

        if (icon == null) {
            throw new IllegalArgumentException("icon is null.");
        }

        if (body == null) {
            throw new IllegalArgumentException("body is null.");
        }

        if (body.isExplorerMode()) {
            if (icon instanceof QEFXRunningIcon) {
                this.setDisable(true);

            } else {
                this.setOnAction(event -> {
                    body.renameIcon(icon);
                });
            }

        } else if (body.isRecentlyUsedMode()) {
            this.setDisable(true);

        } else if (body.isCalculatingMode()) {
            this.setDisable(true);

        } else if (body.isSearchedMode()) {
            this.setDisable(true);

        } else if (body.isWebMode()) {
            this.setDisable(true);
        }
    }
}
