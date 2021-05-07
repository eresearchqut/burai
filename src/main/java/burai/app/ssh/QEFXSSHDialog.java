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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import burai.app.QEFXMain;
import burai.app.QEFXMainController;
import burai.com.consts.ConstantStyles;
import burai.com.graphic.svg.SVGLibrary;
import burai.com.graphic.svg.SVGLibrary.SVGData;
import burai.ssh.SSHServer;
import burai.ssh.SSHServerList;

public class QEFXSSHDialog extends Dialog<ButtonType> implements Initializable {

    private static final double GRAPHIC_SIZE = 20.0;
    private static final String GRAPHIC_CLASS = "piclight-button";

    private static final String DEFAULT_KEY_TEXT = "Select File";
    public static final String ERROR_KEY_STYLE = ConstantStyles.ERROR_COLOR;

    private QEFXMainController controller;

    @FXML
    private ComboBox<SSHServer> selectCombo;

    @FXML
    private Button addButton;

    @FXML
    private Button delButton;

    @FXML
    private TextField hostField;

    @FXML
    private TextField portField;

    @FXML
    private TextField userField;

    @FXML
    private PasswordField passField;

    @FXML
    private Button keyButton;

    @FXML
    private TextField postField;

    @FXML
    private TextArea scriptArea;

    public QEFXSSHDialog(QEFXMainController controller) {
        super();

        if (controller == null) {
            throw new IllegalArgumentException("controller is null.");
        }

        this.controller = controller;

        DialogPane dialogPane = this.getDialogPane();
        QEFXMain.initializeStyleSheets(dialogPane.getStylesheets());
        QEFXMain.initializeDialogOwner(this);

        this.setResizable(false);
        this.setTitle("Remote configuration");
        dialogPane.setHeaderText("Set remote configurations.");
        dialogPane.getButtonTypes().clear();
        dialogPane.getButtonTypes().addAll(ButtonType.CLOSE);

        Node node = null;
        try {
            node = this.createContent();
        } catch (Exception e) {
            node = new Label("ERROR: cannot show QEFXSSHDialog.");
            e.printStackTrace();
        }

        dialogPane.setContent(node);

        this.setResultConverter(buttonType -> {
            return buttonType;
        });
    }

    private Node createContent() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("QEFXSSHDialog.fxml"));
        fxmlLoader.setController(this);
        return fxmlLoader.load();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setupSelectCombo();
        this.setupAddButton();
        this.setupDelButton();
        this.setupSSHProperties();
        this.setupCommandProperties();
    }

    private SSHServer getSSHServer() {
        if (this.selectCombo == null) {
            return null;
        }

        return this.selectCombo.getValue();
    }

    private void setupSelectCombo() {
        if (this.selectCombo == null) {
            return;
        }

        SSHServer[] sshServers = SSHServerList.getInstance().listSSHServers();
        if (sshServers != null && sshServers.length > 0) {
            for (SSHServer sshServer : sshServers) {
                if (sshServer != null) {
                    this.selectCombo.getItems().add(sshServer);
                }
            }

            SingleSelectionModel<SSHServer> selectionMode = this.selectCombo.getSelectionModel();
            if (selectionMode != null) {
                selectionMode.selectFirst();
            }
        }

        this.selectCombo.setOnAction(event -> {
            SSHServer sshServer = this.getSSHServer();
            this.updateSSHProperties(sshServer);
            this.updateCommandProperties(sshServer);
        });
    }

    private void setupAddButton() {
        if (this.addButton == null) {
            return;
        }

        this.addButton.setText("");
        this.addButton.getStyleClass().add(GRAPHIC_CLASS);
        this.addButton.setGraphic(SVGLibrary.getGraphic(SVGData.PLUS, GRAPHIC_SIZE, null, GRAPHIC_CLASS));

        this.addButton.setOnAction(event -> {
            QEFXNewConfDialog dialog = new QEFXNewConfDialog();
            Optional<String> optName = dialog.showAndWait();
            if (optName == null || (!optName.isPresent())) {
                return;
            }

            String name = optName.get();
            name = name == null ? null : name.trim();
            if (name == null || name.isEmpty()) {
                return;
            }

            SSHServer sshServer = new SSHServer(name);
            if (SSHServerList.getInstance().hasSSHServer(sshServer)) {
                return;
            }

            SSHServerList.getInstance().addSSHServer(sshServer);

            if (this.selectCombo != null) {
                while (this.selectCombo.getItems().remove(sshServer)) {
                }

                this.selectCombo.getItems().add(sshServer);
                this.selectCombo.setValue(sshServer);
                this.selectCombo.requestFocus();
            }
        });
    }

    private void setupDelButton() {
        if (this.delButton == null) {
            return;
        }

        this.delButton.setText("");
        this.delButton.getStyleClass().add(GRAPHIC_CLASS);
        this.delButton.setGraphic(SVGLibrary.getGraphic(SVGData.MINUS, GRAPHIC_SIZE, null, GRAPHIC_CLASS));

        this.delButton.setOnAction(event -> {
            SSHServer sshServer = this.getSSHServer();
            if (sshServer == null) {
                return;
            }

            Alert alert = new Alert(AlertType.CONFIRMATION);
            QEFXMain.initializeDialogOwner(alert);
            alert.setHeaderText("'" + sshServer.toString() + "' will be deleted.");
            Optional<ButtonType> optButtonType = alert.showAndWait();
            if (optButtonType == null || (!optButtonType.isPresent())) {
                return;
            }
            if (!ButtonType.OK.equals(optButtonType.get())) {
                return;
            }

            SSHServerList.getInstance().removeSSHServer(sshServer);

            if (this.selectCombo != null) {
                while (this.selectCombo.getItems().remove(sshServer)) {
                }

                if (!this.selectCombo.getItems().isEmpty()) {
                    SingleSelectionModel<SSHServer> selectionMode = this.selectCombo.getSelectionModel();
                    if (selectionMode != null && selectionMode.getSelectedIndex() < 0) {
                        selectionMode.selectFirst();
                    }
                }

                this.selectCombo.requestFocus();
            }
        });
    }

    private void setupSSHProperties() {
        SSHServer sshServer = this.getSSHServer();
        this.updateSSHProperties(sshServer);

        if (this.hostField != null) {
            this.hostField.textProperty().addListener(o -> {
                SSHServer sshServer_ = this.getSSHServer();
                if (sshServer_ != null) {
                    sshServer_.setHost(this.getHost());
                }
            });
        }

        if (this.portField != null) {
            this.portField.textProperty().addListener(o -> {
                SSHServer sshServer_ = this.getSSHServer();
                if (sshServer_ != null) {
                    sshServer_.setPort(this.getPort());
                }
            });
        }

        if (this.userField != null) {
            this.userField.textProperty().addListener(o -> {
                SSHServer sshServer_ = this.getSSHServer();
                if (sshServer_ != null) {
                    sshServer_.setUser(this.getUser());
                }
            });
        }

        if (this.passField != null) {
            this.passField.textProperty().addListener(o -> {
                SSHServer sshServer_ = this.getSSHServer();
                if (sshServer_ != null) {
                    sshServer_.setPassword(this.getPassword());
                }
            });
        }

        if (this.keyButton != null) {
            SSHServer sshServer_ = this.getSSHServer();
            this.keyButton.setOnAction(event -> this.actionKeyButton(sshServer_));
        }
    }

    private void actionKeyButton(SSHServer sshServer) {
        String initPath = this.getKeyPath();
        File initFile = initPath == null ? null : new File(initPath);
        File initDir = initFile == null ? null : initFile.getParentFile();
        String initName = initFile == null ? null : initFile.getName();

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Path of SSH private key");

        if (initDir != null) {
            chooser.setInitialDirectory(initDir);
        }
        if (initName != null && !initName.isEmpty()) {
            chooser.setInitialFileName(initName);
        }

        Stage stage = this.controller.getStage();
        File keyFile = stage == null ? null : chooser.showOpenDialog(stage);
        String keyPath = keyFile == null ? null : keyFile.getPath();
        keyPath = keyPath == null ? null : keyPath.trim();

        if (keyPath != null && !(keyPath.isEmpty())) {
            this.updateKeyButton(keyPath);

            if (sshServer != null) {
                sshServer.setKeyPath(this.getKeyPath());
            }
        }
    }

    private void updateSSHProperties(SSHServer sshServer) {
        if (this.hostField != null) {
            String host = sshServer == null ? null : sshServer.getHost();
            this.hostField.setText(host == null ? "" : host.trim());
            this.hostField.setDisable(sshServer == null);
        }

        if (this.portField != null) {
            String port = sshServer == null ? null : sshServer.getPort();
            this.portField.setText(port == null ? "" : port.trim());
            this.postField.setDisable(sshServer == null);
        }

        if (this.userField != null) {
            String user = sshServer == null ? null : sshServer.getUser();
            this.userField.setText(user == null ? "" : user.trim());
            this.userField.setDisable(sshServer == null);
        }

        if (this.passField != null) {
            String pass = sshServer == null ? null : sshServer.getPassword();
            this.passField.setText(pass == null ? "" : pass.trim());
            this.passField.setDisable(sshServer == null);
        }

        if (this.keyButton != null) {
            String keyPath = sshServer == null ? null : sshServer.getKeyPath();
            this.updateKeyButton(keyPath == null ? null : keyPath.trim());
            this.keyButton.setDisable(sshServer == null);
        }
    }

    private void updateKeyButton(String keyPath) {
        if (this.keyButton == null) {
            return;
        }

        if (keyPath == null || keyPath.isEmpty()) {
            this.keyButton.setText(DEFAULT_KEY_TEXT);
            this.keyButton.setTooltip(null);
            this.keyButton.setStyle("");
            return;
        }

        this.keyButton.setText(keyPath);
        this.keyButton.setTooltip(new Tooltip(keyPath));

        try {
            File file = new File(keyPath);
            if (file.isFile()) {
                this.keyButton.setStyle("");
            } else {
                this.keyButton.setStyle(ERROR_KEY_STYLE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupCommandProperties() {
        SSHServer sshServer = this.getSSHServer();
        this.updateCommandProperties(sshServer);

        if (this.postField != null) {
            this.postField.textProperty().addListener(o -> {
                SSHServer sshServer_ = this.getSSHServer();
                if (sshServer_ != null) {
                    sshServer_.setJobCommand(this.getPostCommand());
                }
            });
        }

        if (this.scriptArea != null) {
            this.scriptArea.textProperty().addListener(o -> {
                SSHServer sshServer_ = this.getSSHServer();
                if (sshServer_ != null) {
                    sshServer_.setJobScript(this.getScriptText());
                }
            });
        }
    }

    private void updateCommandProperties(SSHServer sshServer) {
        if (this.postField != null) {
            String postCommand = sshServer == null ? null : sshServer.getJobCommand();
            this.postField.setText(postCommand == null ? "" : postCommand.trim());
            this.portField.setDisable(sshServer == null);
        }

        if (this.scriptArea != null) {
            String scriptText = sshServer == null ? null : sshServer.getJobScript();
            this.scriptArea.setText(scriptText == null ? "" : scriptText);
            this.scriptArea.setDisable(sshServer == null);
        }
    }

    private String getHost() {
        if (this.hostField == null) {
            return null;
        }

        String value = this.hostField.getText();
        return value == null ? null : value.trim();
    }

    private String getPort() {
        if (this.portField == null) {
            return null;
        }

        String value = this.portField.getText();
        return value == null ? null : value.trim();
    }

    private String getUser() {
        if (this.userField == null) {
            return null;
        }

        String value = this.userField.getText();
        return value == null ? null : value.trim();
    }

    private String getPassword() {
        if (this.passField == null) {
            return null;
        }

        String value = this.passField.getText();
        return value == null ? null : value.trim();
    }

    private String getKeyPath() {
        if (this.keyButton == null) {
            return null;
        }

        String value = this.keyButton.getText();
        if (DEFAULT_KEY_TEXT.equals(value)) {
            value = null;
        }

        return value == null ? null : value.trim();
    }

    private String getPostCommand() {
        if (this.postField == null) {
            return null;
        }

        String value = this.postField.getText();
        return value == null ? null : value.trim();
    }

    private String getScriptText() {
        if (this.scriptArea == null) {
            return null;
        }

        String value = this.scriptArea.getText();
        return value == null ? null : value.trim();
    }

    public void showAndSetProperties() {
        this.showAndWait();
        SSHServerList.getInstance().saveToFile();
    }
}
