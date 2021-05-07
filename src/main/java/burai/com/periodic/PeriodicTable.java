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

package burai.com.periodic;

import java.util.Map;

import burai.app.QEFXMain;
import burai.atoms.element.ElementUtil;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.GridPane;

public class PeriodicTable extends Dialog<ElementButton> {

    private GridPane gridPane;

    public PeriodicTable() {
        this(null);
    }

    public PeriodicTable(Map<String, String> styles) {
        super();

        DialogPane dialogPane = this.getDialogPane();
        QEFXMain.initializeStyleSheets(dialogPane.getStylesheets());
        QEFXMain.initializeDialogOwner(this);

        this.setResizable(false);
        this.setTitle("Periodic Table");
        dialogPane.setHeaderText("Select an element.");
        dialogPane.getButtonTypes().addAll(ButtonType.CANCEL);

        this.createGridPane(styles);
        dialogPane.setContent(this.gridPane);

        this.setResultConverter(buttonType -> {
            return null;
        });
    }

    private void createGridPane(Map<String, String> styles) {
        this.gridPane = new GridPane();
        this.gridPane.setHgap(0.0);
        this.gridPane.setVgap(0.0);
        this.gridPane.setAlignment(Pos.CENTER);

        String[] elementNames = ElementUtil.listAllElements();
        for (String elementName : elementNames) {
            ElementButton elementButton = new ElementButton(elementName);
            elementButton.setDialog(this);

            if (styles != null && !styles.isEmpty()) {
                String style = styles.get(elementName);
                if (style != null && !style.isEmpty()) {
                    elementButton.setStyle(style);
                }
            }

            this.gridPane.add(elementButton, elementButton.getY() - 1, elementButton.getX() - 1);
        }
    }
}
