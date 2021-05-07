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

package burai.app.project.viewer.run;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import burai.app.QEFXMain;
import burai.app.project.viewer.ViewerActions;
import burai.com.consts.ConstantStyles;
import burai.com.env.Environments;
import burai.com.path.QEPath;
import burai.project.FilePathChanged;
import burai.project.Project;
import burai.run.RunningNode;
import burai.run.RunningType;
import burai.ssh.SSHJob;
import burai.ssh.SSHServer;
import burai.ssh.SSHServerList;

public class QEFXRunDialog extends Dialog<RunEvent> implements Initializable {

    private static final String PROP_KEY_MPI = "number_of_processes";
    private static final String PROP_KEY_OPENMP = "number_of_threads";
    private static final String PROP_KEY_HOST = "host_name";

    private static final String ERROR_STYLE = ConstantStyles.ERROR_COLOR;

    private static final String NAME_LOCAL_HOST = "Local Host";

    private Project project;

    private FilePathChanged pathChanged;

    private ViewerActions viewerActions;

    @FXML
    private ComboBox<RunningType> jobCombo;

    @FXML
    private TextField mpiField;

    @FXML
    private TextField ompField;

    @FXML
    private ComboBox<String> hostCombo;

    @FXML
    private Button saveButton;

    public QEFXRunDialog(Project project, ViewerActions viewerActions) {
        super();

        if (project == null) {
            throw new IllegalArgumentException("project is null.");
        }

        if (viewerActions == null) {
            throw new IllegalArgumentException("viewerActions is null.");
        }

        this.project = project;
        this.pathChanged = null;
        this.viewerActions = viewerActions;

        DialogPane dialogPane = this.getDialogPane();
        QEFXMain.initializeStyleSheets(dialogPane.getStylesheets());
        QEFXMain.initializeDialogOwner(this);

        this.setResizable(false);
        this.setTitle("Run a job");
        this.setHeaderText("Set conditions to run a job.");
        this.setupButtonTypes(false);

        Node node = null;
        try {
            node = this.createContent();
        } catch (Exception e) {
            node = new Label("ERROR: cannot show QEFXRunDialog.");
            e.printStackTrace();
        }

        dialogPane.setContent(node);

        this.setResultConverter(buttonType -> {
            if (ButtonType.OK.equals(buttonType)) {
                RunningNode runningNode = this.createRunningNode();
                if (runningNode != null) {
                    this.saveEnvProperties();
                    return new RunEvent(runningNode);
                }

                SSHJob sshJob = this.createSSHJob();
                if (sshJob != null) {
                    this.saveEnvProperties();
                    return new RunEvent(sshJob);
                }
            }

            return null;
        });

        this.setOnHidden(event -> {
            if (this.pathChanged != null) {
                this.project.removeOnFilePathChanged(this.pathChanged);
            }
        });
    }

    private Node createContent() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("QEFXRunDialog.fxml"));
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

    private void resetButtonTypes() {
        this.setupButtonTypes(this.isCorrectMPI() && this.isCorrectOpenMP() && this.isProjectSaved());
    }

    private boolean isCorrectMPI() {
        if (this.mpiField != null) {
            try {
                int numMPI = Integer.parseInt(this.mpiField.getText());
                if (numMPI < 1) {
                    return false;
                }

            } catch (Exception e) {
                return false;
            }
        }

        return true;
    }

    private boolean isCorrectOpenMP() {
        if (this.ompField != null) {
            try {
                int numOMP = Integer.parseInt(this.ompField.getText());
                if (numOMP < 1) {
                    return false;
                }

            } catch (Exception e) {
                return false;
            }
        }

        return true;
    }

    private boolean isProjectSaved() {
        String dirPath = this.project.getDirectoryPath();
        if (dirPath == null || dirPath.isEmpty()) {
            return false;
        }

        try {
            File dirFile = new File(dirPath);
            if (!dirFile.isDirectory()) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private int textFiledToInteger(TextField field, int value) {
        if (field == null) {
            return value;
        }

        String text = field.getText();
        if (text == null || text.isEmpty()) {
            return value;
        }

        int i = value;
        try {
            i = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return value;
        }

        return i;
    }

    private void saveEnvProperties() {
        int numMPI = this.textFiledToInteger(this.mpiField, 1);
        int numOMP = this.textFiledToInteger(this.ompField, 1);
        Environments.setProperty(PROP_KEY_MPI, numMPI);
        Environments.setProperty(PROP_KEY_OPENMP, numOMP);

        String hostName = this.hostCombo == null ? null : this.hostCombo.getValue();
        hostName = hostName == null ? null : hostName.trim();
        if (hostName == null || hostName.isEmpty()) {
            Environments.removeProperty(PROP_KEY_HOST);
        } else {
            Environments.setProperty(PROP_KEY_HOST, hostName);
        }
    }

    private boolean canRunningNode() {
        return this.hostCombo == null || NAME_LOCAL_HOST.equals(this.hostCombo.getValue());
    }

    private RunningNode createRunningNode() {
        if (!this.canRunningNode()) {
            return null;
        }

        RunningType runningType = null;
        if (this.jobCombo != null) {
            runningType = this.jobCombo.getValue();
        }
        if (runningType == null) {
            runningType = RunningType.SCF;
        }

        int numMPI = this.textFiledToInteger(this.mpiField, 1);
        int numOMP = this.textFiledToInteger(this.ompField, 1);

        RunningNode runningNode = new RunningNode(this.project);
        runningNode.setType(runningType);
        runningNode.setNumProcesses(numMPI);
        runningNode.setNumThreads(numOMP);
        return runningNode;
    }

    private SSHJob createSSHJob() {
        if (this.canRunningNode()) {
            return null;
        }

        String hostName = this.hostCombo == null ? null : this.hostCombo.getValue();
        hostName = hostName == null ? null : hostName.trim();
        if (hostName == null || hostName.isEmpty()) {
            return null;
        }

        SSHServer sshServer = SSHServerList.getInstance().getSSHServer(hostName);
        if (sshServer == null) {
            return null;
        }

        RunningType runningType = null;
        if (this.jobCombo != null) {
            runningType = this.jobCombo.getValue();
        }
        if (runningType == null) {
            runningType = RunningType.SCF;
        }

        int numMPI = this.textFiledToInteger(this.mpiField, 1);
        int numOMP = this.textFiledToInteger(this.ompField, 1);

        SSHJob sshJob = new SSHJob(this.project, sshServer);
        sshJob.setType(runningType);
        sshJob.setNumProcesses(numMPI);
        sshJob.setNumThreads(numOMP);
        return sshJob;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setupJobCombo();
        this.setupMPIField();
        this.setupOpenMPField();
        this.setupHostCombo();
        this.setupSaveButton();
        this.resetButtonTypes();
    }

    private void setupJobCombo() {
        if (this.jobCombo == null) {
            return;
        }

        for (RunningType runningType : RunningType.values()) {
            if (runningType != null) {
                this.jobCombo.getItems().add(runningType);
            }
        }

        RunningType runningType = RunningType.getRunningType(this.project);
        if (runningType != null) {
            this.jobCombo.setValue(runningType);
        } else {
            this.jobCombo.setValue(RunningType.SCF);
        }
    }

    private boolean canMPIParallel() {
        String mpiPath = QEPath.getMPIPath();
        if (mpiPath == null || mpiPath.trim().isEmpty()) {
            return false;
        }

        if (this.hasTwoByteChar(this.project.getDirectoryPath())) {
            return false;
        }

        return true;
    }

    private boolean hasTwoByteChar(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }

        byte[] bytes = str.getBytes();
        if (bytes == null || bytes.length < 1) {
            return false;
        }

        return bytes.length > str.length();
    }

    private void setupMPIField() {
        if (this.mpiField == null) {
            return;
        }

        int numMPI = 0;
        if (Environments.hasProperty(PROP_KEY_MPI)) {
            numMPI = Math.max(1, Environments.getIntProperty(PROP_KEY_MPI));

        } else {
            if (this.canMPIParallel()) {
                numMPI = Math.max(1, Environments.getNumCUPs());
            } else {
                numMPI = 1;
            }
        }

        String strMPI = Integer.toString(numMPI);
        this.mpiField.setText(strMPI);

        if (this.isCorrectMPI()) {
            this.mpiField.setStyle("");
        } else {
            this.mpiField.setStyle(ERROR_STYLE);
        }

        this.mpiField.textProperty().addListener(o -> {
            if (this.isCorrectMPI()) {
                this.mpiField.setStyle("");
            } else {
                this.mpiField.setStyle(ERROR_STYLE);
            }

            this.resetButtonTypes();
            this.mpiField.requestFocus();
        });
    }

    private void setupOpenMPField() {
        if (this.ompField == null) {
            return;
        }

        int numOMP = 0;
        if (Environments.hasProperty(PROP_KEY_OPENMP)) {
            numOMP = Math.max(1, Environments.getIntProperty(PROP_KEY_OPENMP));

        } else {
            if (!this.canMPIParallel()) {
                numOMP = Math.max(1, Environments.getNumCUPs());
            } else {
                numOMP = 1;
            }
        }

        String strOMP = Integer.toString(numOMP);
        this.ompField.setText(strOMP);

        if (this.isCorrectOpenMP()) {
            this.ompField.setStyle("");
        } else {
            this.ompField.setStyle(ERROR_STYLE);
        }

        this.ompField.textProperty().addListener(o -> {
            if (this.isCorrectOpenMP()) {
                this.ompField.setStyle("");
            } else {
                this.ompField.setStyle(ERROR_STYLE);
            }

            this.resetButtonTypes();
            this.ompField.requestFocus();
        });
    }

    private void setupHostCombo() {
        if (this.hostCombo == null) {
            return;
        }

        this.hostCombo.getItems().add(NAME_LOCAL_HOST);

        SSHServer[] sshServers = SSHServerList.getInstance().listSSHServers();
        if (sshServers != null) {
            for (SSHServer sshServer : sshServers) {
                String sshName = sshServer == null ? null : sshServer.getTitle();
                if (sshName != null && (!sshName.isEmpty())) {
                    this.hostCombo.getItems().add(sshName);
                }
            }
        }

        String hostName = Environments.getProperty(PROP_KEY_HOST);
        hostName = hostName == null ? null : hostName.trim();
        if (hostName == null || hostName.isEmpty()) {
            this.hostCombo.setValue(NAME_LOCAL_HOST);
        } else {
            this.hostCombo.setValue(hostName);
        }
    }

    private void setupSaveButton() {
        if (this.saveButton == null) {
            return;
        }

        if (this.isProjectSaved()) {
            this.saveButton.setStyle("");
            this.saveButton.setDisable(true);
        } else {
            this.saveButton.setStyle(ERROR_STYLE);
            this.saveButton.setDisable(false);
        }

        this.saveButton.setOnAction(event -> {
            DialogPane dialogPane = this.getDialogPane();
            dialogPane.setDisable(true);
            this.viewerActions.saveFile();
            dialogPane.setDisable(false);
        });

        this.pathChanged = (path -> {
            if (this.isProjectSaved()) {
                this.saveButton.setStyle("");
                this.saveButton.setDisable(true);
            } else {
                this.saveButton.setStyle(ERROR_STYLE);
                this.saveButton.setDisable(false);
            }

            this.resetButtonTypes();
        });

        this.project.addOnFilePathChanged(this.pathChanged);
    }

    @Override
    public int hashCode() {
        String str = this.project.toString();
        return str == null ? 0 : str.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }
}
