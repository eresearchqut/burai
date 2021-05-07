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

package burai.app.ssh;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import burai.app.QEFXMain;
import burai.ssh.SSHServerList;

public class QEFXNewConfDialog extends Dialog<String> implements Initializable {

    @FXML
    private TextField nameField;

    public QEFXNewConfDialog() {
        super();

        DialogPane dialogPane = this.getDialogPane();
        QEFXMain.initializeStyleSheets(dialogPane.getStylesheets());
        QEFXMain.initializeDialogOwner(this);

        this.setResizable(false);
        this.setTitle("New configuration");
        dialogPane.setHeaderText("Add a new configuration.");
        this.setupButtonTypes(false);

        Node node = null;
        try {
            node = this.createContent();
        } catch (Exception e) {
            node = new Label("ERROR: cannot show QEFXNewConfDialog.");
            e.printStackTrace();
        }

        dialogPane.setContent(node);

        this.setResultConverter(buttonType -> {
            if (ButtonType.OK.equals(buttonType)) {
                return this.getConfName();
            }

            return null;
        });
    }

    private Node createContent() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("QEFXNewConfDialog.fxml"));
        fxmlLoader.setController(this);
        return fxmlLoader.load();
    }

    private void setupButtonTypes(boolean withOK) {
        DialogPane dialogPane = this.getDialogPane();
        if (dialogPane == null) {
            return;
        }

        dialogPane.getButtonTypes().clear();
        if (withOK) {
            dialogPane.getButtonTypes().add(ButtonType.OK);
        }
        dialogPane.getButtonTypes().add(ButtonType.CANCEL);
    }

    private String getConfName() {
        if (this.nameField == null) {
            return null;
        }

        String name = this.nameField.getText();
        return name == null ? null : name.trim();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setupNameField();
    }

    private void setupNameField() {
        if (this.nameField == null) {
            return;
        }

        this.nameField.textProperty().addListener(o -> {
            String name = this.getConfName();
            if (name == null || name.isEmpty() || SSHServerList.getInstance().hasSSHServer(name)) {
                this.setupButtonTypes(false);
            } else {
                this.setupButtonTypes(true);
            }

            this.nameField.requestFocus();
        });

        this.setOnShown(event -> {
            if (this.nameField != null) {
                this.nameField.requestFocus();
            }
        });
    }
}
