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
import burai.app.explorer.body.menuitem.QEFXDeleteWebMenuItem;
import burai.app.explorer.body.menuitem.QEFXMakeDirectoryMenuItem;
import burai.app.explorer.body.menuitem.QEFXOpenTabMenuItem;
import burai.app.explorer.body.menuitem.QEFXPasteFileMenuItem;
import burai.app.explorer.body.menuitem.QEFXRenameFileMenuItem;
import burai.app.icon.QEFXWebIcon;

public class QEFXWebContextMenu extends QEFXContextMenu<QEFXWebIcon> {

    public QEFXWebContextMenu(QEFXWebIcon icon, QEFXExplorerBody body) {
        super(icon, body);
    }

    @Override
    protected void createMenuItems() {

        this.getItems().add(new QEFXOpenTabMenuItem(this.icon, this.body));

        this.getItems().add(new QEFXRenameFileMenuItem(this.icon, this.body));

        this.getItems().add(new QEFXCopyFileMenuItem(this.icon, this.body));

        this.getItems().add(new QEFXPasteFileMenuItem(this.icon, this.body));

        this.getItems().add(new QEFXDeleteWebMenuItem(this.icon, this.body));

        this.getItems().add(new QEFXMakeDirectoryMenuItem(this.icon, this.body));
    }
}
