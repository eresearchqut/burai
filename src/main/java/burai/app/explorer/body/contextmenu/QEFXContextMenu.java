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

import javafx.scene.control.ContextMenu;
import burai.app.explorer.body.QEFXExplorerBody;
import burai.app.icon.QEFXFolderIcon;
import burai.app.icon.QEFXIcon;
import burai.app.icon.QEFXProjectIcon;
import burai.app.icon.QEFXUPFIcon;
import burai.app.icon.QEFXWebIcon;

public abstract class QEFXContextMenu<I extends QEFXIcon> extends ContextMenu {

    public static ContextMenu getContextMenu(QEFXIcon icon, QEFXExplorerBody body) {
        if (body == null) {
            return null;
        }

        if (icon == null) {
            return new QEFXBackgroundContextMenu(body);

        } else if (icon instanceof QEFXProjectIcon) {
            return new QEFXProjectContextMenu((QEFXProjectIcon) icon, body);

        } else if (icon instanceof QEFXWebIcon) {
            return new QEFXWebContextMenu((QEFXWebIcon) icon, body);

        } else if (icon instanceof QEFXUPFIcon) {
            return new QEFXUPFContextMenu((QEFXUPFIcon) icon, body);

        } else if (icon instanceof QEFXFolderIcon) {
            return new QEFXFolderContextMenu((QEFXFolderIcon) icon, body);
        }

        return null;
    }

    protected I icon;

    protected QEFXExplorerBody body;

    protected QEFXContextMenu(I icon, QEFXExplorerBody body) {
        super();

        //if (icon == null) {
        //    throw new IllegalArgumentException("icon is null.");
        //}

        if (body == null) {
            throw new IllegalArgumentException("body is null.");
        }

        this.icon = icon;
        this.body = body;
        this.getStyleClass().add("icon-context-menu");

        this.getItems().clear();
        this.createMenuItems();

        this.setOnShowing(event -> {
            this.getItems().clear();
            this.createMenuItems();
        });
    }

    protected abstract void createMenuItems();
}
