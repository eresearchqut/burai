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

package burai.app.matapi;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import burai.app.QEFXMain;
import burai.com.graphic.ToggleGraphics;
import burai.matapi.MaterialsAPILoader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;

public class QEFXMatAPIDialog extends Dialog<ButtonType> implements Initializable {

    private static final String TOGGLE_STYLE = "-fx-base: transparent";
    private static final double GRAPHIC_WIDTH = 255.0;
    private static final double GRAPHIC_HEIGHT = 24.0;
    private static final String GRAPHIC_TEXT_PRIMITIVE = "Primitive";
    private static final String GRAPHIC_TEXT_STANDARD = "Standard";
    private static final String GRAPHIC_STYLE_PRIMITIVE = "toggle-graphic-on";
    private static final String GRAPHIC_STYLE_STANDARD = "toggle-graphic-off";

    @FXML
    private Label usageLabel;

    @FXML
    private TextField apiKeyField;

    @FXML
    private Label cellLabel;

    @FXML
    private ToggleButton cellToggle;

    public QEFXMatAPIDialog() {
        super();

        DialogPane dialogPane = this.getDialogPane();
        QEFXMain.initializeStyleSheets(dialogPane.getStylesheets());
        QEFXMain.initializeDialogOwner(this);

        String header = "";
        header = header + "The Materials API allows you to search crystal structures.";
        header = header + System.lineSeparator();
        header = header + "               <https://www.materialsproject.org/docs/api>";

        this.setResizable(false);
        this.setTitle("Materials API");
        dialogPane.setHeaderText(header);
        dialogPane.getButtonTypes().clear();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Node node = null;
        try {
            node = this.createContent();
        } catch (Exception e) {
            node = new Label("ERROR: cannot show QEFXMatAPIDialog.");
            e.printStackTrace();
        }

        dialogPane.setContent(node);

        this.setResultConverter(buttonType -> {
            return buttonType;
        });
    }

    private Node createContent() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("QEFXMatAPIDialog.fxml"));
        fxmlLoader.setController(this);
        return fxmlLoader.load();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setupUsageLabel();
        this.setupApiKeyField();
        this.setupCellToggle();
    }

    private void setupUsageLabel() {
        if (this.usageLabel == null) {
            return;
        }

        String text = "";
        text = text + "Please input fllowing data (1 or 2), and press ENTER.";
        text = text + System.lineSeparator();
        text = text + System.lineSeparator();
        text = text + "  1. List of elements separated with \"-\".";
        text = text + System.lineSeparator();
        text = text + "       e.g.  Li-Fe-O";
        text = text + System.lineSeparator();
        text = text + System.lineSeparator();
        text = text + "  2. Chemical formula.";
        text = text + System.lineSeparator();
        text = text + "       e.g.  Fe2O3";
        text = text + System.lineSeparator();
        text = text + System.lineSeparator();

        this.usageLabel.setText(text);
    }

    private void setupApiKeyField() {
        if (this.apiKeyField == null) {
            return;
        }

        String apiKey = MaterialsAPILoader.getApiKey();
        if (apiKey == null) {
            this.apiKeyField.setText("");
        } else {
            this.apiKeyField.setText(apiKey);
        }

        this.updateApiKeyField();

        this.apiKeyField.textProperty().addListener(o -> {
            this.updateApiKeyField();
        });
    }

    private void updateApiKeyField() {
        if (this.apiKeyField == null) {
            return;
        }

        String text = this.apiKeyField.getText();

        boolean cellStatus = true;
        if (text == null || text.trim().isEmpty()) {
            cellStatus = false;
        }

        if (this.cellLabel != null) {
            this.cellLabel.setDisable(!cellStatus);
        }

        if (this.cellToggle != null) {
            this.cellToggle.setDisable(!cellStatus);
            if (!cellStatus) {
                this.cellToggle.setSelected(false);
            }
        }
    }

    private void setupCellToggle() {
        if (this.cellToggle == null) {
            return;
        }

        boolean primitiveCell = MaterialsAPILoader.isPrimitiveCell();
        this.cellToggle.setSelected(primitiveCell);

        this.cellToggle.setText("");
        this.cellToggle.setStyle(TOGGLE_STYLE);

        this.updateCellToggle();

        this.cellToggle.selectedProperty().addListener(o -> {
            this.updateCellToggle();
        });
    }

    private void updateCellToggle() {
        if (this.cellToggle == null) {
            return;
        }

        if (this.cellToggle.isSelected()) {
            this.cellToggle.setGraphic(ToggleGraphics.getGraphic(
                    GRAPHIC_WIDTH, GRAPHIC_HEIGHT, true, GRAPHIC_TEXT_PRIMITIVE, GRAPHIC_STYLE_PRIMITIVE));
        } else {
            this.cellToggle.setGraphic(ToggleGraphics.getGraphic(
                    GRAPHIC_WIDTH, GRAPHIC_HEIGHT, false, GRAPHIC_TEXT_STANDARD, GRAPHIC_STYLE_STANDARD));
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

        String apiKey = this.getApiKey();

        if (apiKey != null && (!apiKey.isEmpty())) {
            MaterialsAPILoader.setApiKey(apiKey);
        } else {
            MaterialsAPILoader.setApiKey(null);
        }

        boolean primitiveCell = this.isPrimitiveCell();
        MaterialsAPILoader.setPrimitiveCell(primitiveCell);
    }

    private String getApiKey() {
        if (this.apiKeyField == null) {
            return null;
        }

        String text = this.apiKeyField.getText();
        return text == null ? null : text.trim();
    }

    private boolean isPrimitiveCell() {
        if (this.cellToggle == null) {
            return false;
        }

        return this.cellToggle.isSelected();
    }
}
