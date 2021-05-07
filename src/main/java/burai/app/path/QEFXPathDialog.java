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

package burai.app.path;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import burai.app.QEFXMain;
import burai.app.QEFXMainController;
import burai.com.consts.ConstantStyles;
import burai.com.env.Environments;
import burai.com.path.QEPath;
import burai.run.RunningCommand.RunningCommandType;

public class QEFXPathDialog extends Dialog<ButtonType> implements Initializable {

    private static final String DEFAULT_TEXT = "Select Directory";

    public static final String ERROR_STYLE = ConstantStyles.ERROR_COLOR;

    private QEFXMainController controller;

    @FXML
    private Label qeLabel;

    @FXML
    private Button qeButton;

    @FXML
    private Label mpiLabel;

    @FXML
    private Button mpiButton;

    @FXML
    private TextField mpiField;

    public QEFXPathDialog(QEFXMainController controller) {
        super();

        if (controller == null) {
            throw new IllegalArgumentException("controller is null.");
        }

        this.controller = controller;

        DialogPane dialogPane = this.getDialogPane();
        QEFXMain.initializeStyleSheets(dialogPane.getStylesheets());
        QEFXMain.initializeDialogOwner(this);

        this.setResizable(false);
        this.setTitle("Path");
        dialogPane.setHeaderText("Set path.");
        dialogPane.getButtonTypes().clear();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Node node = null;
        try {
            node = this.createContent();
        } catch (Exception e) {
            node = new Label("ERROR: cannot show QEFXPathDialog.");
            e.printStackTrace();
        }

        dialogPane.setContent(node);

        this.setResultConverter(buttonType -> {
            return buttonType;
        });
    }

    private Node createContent() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("QEFXPathDialog.fxml"));
        fxmlLoader.setController(this);
        return fxmlLoader.load();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setupQeLabel();
        this.setupButton(this.qeButton, false);
        this.setupMpiLabel();
        this.setupButton(this.mpiButton, true);
        this.setupMpiField();
    }

    private void setupQeLabel() {
        if (this.qeLabel == null) {
            return;
        }

        String text = this.qeLabel.getText();
        if (text != null) {
            if (Environments.isWindows()) {
                text = text.replaceAll("pw.x", "pw.exe");
                this.qeLabel.setText(text);
            }
        }
    }

    private void setupMpiLabel() {
        if (this.mpiLabel == null) {
            return;
        }

        String text = this.mpiLabel.getText();
        if (text != null) {
            if (Environments.isWindows()) {
                text = text.replaceAll("mpirun", "mpiexec.exe");
                this.mpiLabel.setText(text);
            }
        }
    }

    private void setupMpiField() {
        if (this.mpiField == null) {
            return;
        }

        String text = this.mpiField.getPromptText();
        if (text != null) {
            if (Environments.isWindows()) {
                text = text.replaceAll("mpirun", "mpiexec.exe");
                this.mpiField.setPromptText(text);
            }
        }

        String command = RunningCommandType.MPIRUN.getCommand();
        this.mpiField.setText(command == null ? "" : command);
    }

    private void setupButton(Button button, boolean isMPI) {
        if (button == null) {
            return;
        }

        String path = null;
        if (isMPI) {
            path = QEPath.getMPIPath();
        } else {
            path = QEPath.getPath();
        }

        if (path != null) {
            path = path.trim();
        }

        this.updateButtonPath(button, path);

        button.setOnAction(event -> {
            DirectoryChooser chooser = new DirectoryChooser();
            if (isMPI) {
                chooser.setTitle("Path of MPI");
            } else {
                chooser.setTitle("Path of Quantum ESPRESSO");
            }

            String initPath = this.getButtonText(button);
            File initDir = initPath == null ? null : new File(initPath);
            if (initDir != null) {
                try {
                    if (initDir.isDirectory()) {
                        chooser.setInitialDirectory(initDir);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            File directory = null;
            Stage stage = this.controller.getStage();
            if (stage != null) {
                directory = chooser.showDialog(stage);
            }

            String dirPath = directory == null ? null : directory.getPath();
            dirPath = dirPath == null ? null : dirPath.trim();

            if (dirPath != null && !(dirPath.isEmpty())) {
                this.updateButtonPath(button, dirPath);
            }
        });
    }

    private void updateButtonPath(Button button, String path) {
        if (button == null) {
            return;
        }

        if (path == null || path.isEmpty()) {
            button.setText(DEFAULT_TEXT);
            button.setTooltip(null);
            button.setStyle("");
            return;
        }

        button.setText(path);
        button.setTooltip(new Tooltip(path));

        try {
            File file = new File(path);
            if (file.isDirectory()) {
                button.setStyle("");
            } else {
                button.setStyle(ERROR_STYLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showAndSetProperties() {
        Optional<ButtonType> optButtonType = this.showAndWait();
        if (optButtonType == null || !optButtonType.isPresent()) {
            return;
        }
        if (optButtonType.get() != ButtonType.OK) {
            return;
        }

        String qePath = this.getButtonText(this.qeButton);
        String mpiPath = this.getButtonText(this.mpiButton);

        String mpiCommand = this.mpiField == null ? null : this.mpiField.getText();
        mpiCommand = mpiCommand == null ? null : mpiCommand.trim();

        if (qePath == null || qePath.isEmpty()) {
            QEPath.setPath((String) null);
        } else {
            QEPath.setPath(qePath);
        }

        if (mpiPath == null || mpiPath.isEmpty()) {
            QEPath.setMPIPath((String) null);
        } else {
            QEPath.setMPIPath(mpiPath);
        }

        if (mpiCommand == null || mpiCommand.isEmpty()) {
            RunningCommandType.MPIRUN.setCommand(null);
        } else {
            RunningCommandType.MPIRUN.setCommand(mpiCommand);
        }
    }

    private String getButtonText(Button button) {
        if (button == null) {
            return null;
        }

        String value = button.getText();
        if (DEFAULT_TEXT.equals(value)) {
            value = null;
        }

        return value == null ? null : value.trim();
    }
}
