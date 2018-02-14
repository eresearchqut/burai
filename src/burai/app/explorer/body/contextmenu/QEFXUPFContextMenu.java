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

package burai.app.explorer.body.contextmenu;

import burai.app.explorer.body.QEFXExplorerBody;
import burai.app.explorer.body.menuitem.QEFXCopyFileMenuItem;
import burai.app.explorer.body.menuitem.QEFXDeleteFileMenuItem;
import burai.app.explorer.body.menuitem.QEFXMakeDirectoryMenuItem;
import burai.app.explorer.body.menuitem.QEFXOpenFileMenuItem;
import burai.app.explorer.body.menuitem.QEFXPasteFileMenuItem;
import burai.app.explorer.body.menuitem.QEFXRenameFileMenuItem;
import burai.app.icon.QEFXUPFIcon;

public class QEFXUPFContextMenu extends QEFXContextMenu<QEFXUPFIcon> {

    public QEFXUPFContextMenu(QEFXUPFIcon icon, QEFXExplorerBody body) {
        super(icon, body);
    }

    @Override
    protected void createMenuItems() {

        String filePath = this.icon.getContent();
        if (filePath != null && (!filePath.isEmpty())) {
            this.getItems().add(new QEFXOpenFileMenuItem(filePath));
        }

        this.getItems().add(new QEFXRenameFileMenuItem(this.icon, this.body));

        this.getItems().add(new QEFXCopyFileMenuItem(this.icon, this.body));

        this.getItems().add(new QEFXPasteFileMenuItem(this.icon, this.body));

        this.getItems().add(new QEFXDeleteFileMenuItem(this.icon, this.body));

        this.getItems().add(new QEFXMakeDirectoryMenuItem(this.icon, this.body));
    }
}
