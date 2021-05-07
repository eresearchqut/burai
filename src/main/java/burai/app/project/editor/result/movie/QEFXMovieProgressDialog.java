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

package burai.app.project.editor.result.movie;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import burai.app.QEFXMain;

public class QEFXMovieProgressDialog extends Dialog<File> implements Initializable {

    private File movieFile;

    @FXML
    private Label progressLabel;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private Button stopButton;

    public QEFXMovieProgressDialog(File movieFile) {
        super();

        if (movieFile == null) {
            throw new IllegalArgumentException("movieFile is null.");
        }

        this.movieFile = movieFile;

        DialogPane dialogPane = this.getDialogPane();
        QEFXMain.initializeStyleSheets(dialogPane.getStylesheets());
        QEFXMain.initializeDialogOwner(this);

        this.setResizable(false);
        this.setTitle("Movie: " + this.movieFile.getName());
        dialogPane.getButtonTypes().clear();

        Node node = null;
        try {
            node = this.createContent();
        } catch (Exception e) {
            node = new Label("ERROR: cannot show QEFXMovieProgressDialog.");
            e.printStackTrace();
        }

        dialogPane.setContent(node);

        this.setResultConverter(buttonType -> {
            return this.movieFile;
        });
    }

    private Node createContent() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("QEFXMovieProgressDialog.fxml"));
        fxmlLoader.setController(this);
        return fxmlLoader.load();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setupProgressLabel();
        this.setupProgressIndicator();
        this.setupStopButton();
    }

    private void setupProgressLabel() {
        if (this.progressLabel == null) {
            return;
        }

        String text = this.progressLabel.getText();
        text = text == null ? "" : text;
        text = text + System.lineSeparator() + this.movieFile.getPath();
        this.progressLabel.setText(text);
    }

    private void setupProgressIndicator() {
        if (this.progressIndicator == null) {
            return;
        }

        this.progressIndicator.setProgress(0.0);
    }

    private void setupStopButton() {
        if (this.stopButton == null) {
            return;
        }

        // NOP
    }

    public void setProgress(double value) {
        if (this.progressIndicator != null) {
            this.progressIndicator.setProgress(value);
        }
    }

    public void showProgress() {
        this.setProgress(0.0);
        DialogPane dialogPane = this.getDialogPane();
        dialogPane.getButtonTypes().clear();
        this.show();
    }

    public void hideProgress() {
        DialogPane dialogPane = this.getDialogPane();
        dialogPane.getButtonTypes().clear();
        dialogPane.getButtonTypes().add(ButtonType.CLOSE);
        this.hide();
    }

    public void setOnStopAction(EventHandler<ActionEvent> value) {
        if (this.stopButton != null) {
            this.stopButton.setOnAction(value);
        }
    }
}
