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

package burai.app.explorer.body.menuitem;

import burai.app.explorer.body.QEFXExplorerBody;
import burai.app.icon.QEFXIcon;

public class QEFXDeleteWebMenuItem extends QEFXMenuItem {

    public QEFXDeleteWebMenuItem(QEFXIcon icon, QEFXExplorerBody body) {
        super("Delete web-page");

        if (icon == null) {
            throw new IllegalArgumentException("icon is null.");
        }

        if (body == null) {
            throw new IllegalArgumentException("body is null.");
        }

        if (body.isExplorerMode()) {
            this.setOnAction(event -> {
                body.deleteIcon(icon);
            });

        } else if (body.isRecentlyUsedMode()) {
            this.setDisable(true);

        } else if (body.isCalculatingMode()) {
            this.setDisable(true);

        } else if (body.isSearchedMode()) {
            this.setDisable(true);

        } else if (body.isWebMode()) {
            this.setOnAction(event -> {
                body.deleteIcon(icon);
            });
        }
    }
}
