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

package burai.app.project.editor.modeler.slabmodel;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import burai.app.QEFXAppController;
import burai.app.QEFXMain;
import burai.app.project.QEFXProjectController;
import burai.app.project.viewer.modeler.slabmodel.SlabModel;
import burai.app.project.viewer.modeler.slabmodel.SlabModeler;
import burai.atoms.model.Cell;
import burai.atoms.vlight.AtomsVLight;
import burai.com.consts.ConstantStyles;
import burai.com.fx.FXBufferedThread;
import burai.com.graphic.svg.SVGLibrary;
import burai.com.graphic.svg.SVGLibrary.SVGData;
import burai.com.keys.KeyNames;

public class QEFXSlabEditorController extends QEFXAppController {

    private static final long SLEEP_SLAB_BUFFER = 300L;
    private static final long SLEEP_VACUUM_BUFFER = 150L;

    private static final double CTRL_GRAPHIC_SIZE = 20.0;
    private static final String CTRL_GRAPHIC_CLASS = "piclight-button";

    private static final double BUILD_GRAPHIC_SIZE = 20.0;
    private static final String BUILD_GRAPHIC_CLASS = "piclight-button";

    private static final String ERROR_STYLE = ConstantStyles.ERROR_COLOR;

    private static final double ATOMS_TILE_SIZE = 175.0;
    private static final double ATOMS_TILE_SCLAE = 1.6;
    private static final double ATOMS_TILE_INSET = 4.0;
    private static final String ATOMS_TILE_CLASS1 = "icon-slab";
    private static final String ATOMS_TILE_CLASS2 = "icon-slab-selected";
    private static final double ATOMS_LABEL_INSET = 12.0;
    private static final String ATOMS_LABEL_CLASS = "icon-slab";

    private QEFXProjectController projectController;

    private SlabModeler modeler;

    @FXML
    private Button screenButton;

    @FXML
    private Button initButton;

    @FXML
    private Button centerButton;

    @FXML
    private Label centerLabel;

    @FXML
    private TextField scaleField1;

    @FXML
    private TextField scaleField2;

    @FXML
    private Button superButton;

    private FXBufferedThread slabThread;

    @FXML
    private Slider slabSlider;

    private FXBufferedThread vacuumThread;

    @FXML
    private Slider vacuumSlider;

    private StackPane kindTile;

    @FXML
    private TilePane kindPane;

    @FXML
    private ScrollPane kindScroll;

    public QEFXSlabEditorController(QEFXProjectController projectController, SlabModeler modeler) {
        super(projectController == null ? null : projectController.getMainController());

        if (modeler == null) {
            throw new IllegalArgumentException("modeler is null.");
        }

        this.projectController = projectController;

        this.modeler = modeler;

        this.slabThread = new FXBufferedThread(SLEEP_SLAB_BUFFER, true);
        this.vacuumThread = new FXBufferedThread(SLEEP_VACUUM_BUFFER, true);

        this.kindTile = null;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setupScreenButton();
        this.setupInitButton();
        this.setupCenterButton();
        this.setupCenterLabel();

        this.setupScaleField(this.scaleField1);
        this.setupScaleField(this.scaleField2);
        this.setupSuperButton();

        this.setupSlabSlider();
        this.setupVacuumSlider();
        this.setupKindPane();
        this.setupKindScroll();
    }

    private void setupScreenButton() {
        if (this.screenButton == null) {
            return;
        }

        this.screenButton.setText("");
        this.screenButton.setGraphic(
                SVGLibrary.getGraphic(SVGData.CAMERA, CTRL_GRAPHIC_SIZE, null, CTRL_GRAPHIC_CLASS));

        this.screenButton.setOnAction(event -> {
            if (this.projectController != null) {
                this.projectController.sceenShot();
            }
        });
    }

    private void setupInitButton() {
        if (this.initButton == null) {
            return;
        }

        this.initButton.setText("");
        this.initButton.setGraphic(
                SVGLibrary.getGraphic(SVGData.INTO, CTRL_GRAPHIC_SIZE, null, CTRL_GRAPHIC_CLASS));

        this.initButton.setOnAction(event -> {
            if (this.modeler != null) {
                this.modeler.initialize();
            }

            this.initializeFXComponents();
        });
    }

    private void initializeFXComponents() {
        if (this.scaleField1 != null) {
            this.scaleField1.setText(Integer.toString(this.modeler.getScaleA()));
        }

        if (this.scaleField2 != null) {
            this.scaleField2.setText(Integer.toString(this.modeler.getScaleB()));
        }

        if (this.slabSlider != null) {
            this.slabSlider.setValue(this.modeler.getThickness());
        }

        if (this.vacuumSlider != null) {
            this.vacuumSlider.setValue(this.modeler.getVacuum());
        }
    }

    private void setupCenterButton() {
        if (this.centerButton == null) {
            return;
        }

        this.centerButton.setText("");
        this.centerButton.setGraphic(
                SVGLibrary.getGraphic(SVGData.CENTER, CTRL_GRAPHIC_SIZE, null, CTRL_GRAPHIC_CLASS));

        this.centerButton.setOnAction(event -> {
            if (this.modeler != null) {
                this.modeler.center();
            }
        });
    }

    private void setupCenterLabel() {
        if (this.centerLabel == null) {
            return;
        }

        String text = this.centerLabel.getText();
        if (text == null || text.isEmpty()) {
            return;
        }

        text = text.replaceAll("Shortcut", KeyNames.getShortcut());
        this.centerLabel.setText(text);
    }

    private void setupScaleField(TextField textField) {
        if (textField == null) {
            return;
        }

        textField.setText(Integer.toString(SlabModel.defaultScale()));

        textField.setStyle("");

        textField.textProperty().addListener(o -> {
            int value = this.getScaleValue(textField);
            if (value > 0) {
                textField.setStyle("");
            } else {
                textField.setStyle(ERROR_STYLE);
            }

            if (this.superButton != null) {
                this.superButton.setDisable(!this.isAvailSuper());
            }
        });

        textField.setOnAction(event -> {
            if (this.superButton != null && !(this.superButton.isDisable())) {
                EventHandler<ActionEvent> handler = this.superButton.getOnAction();
                if (handler != null) {
                    handler.handle(event);
                }
            }
        });
    }

    private void setupSuperButton() {
        if (this.superButton == null) {
            return;
        }

        this.superButton.setDisable(!this.isAvailSuper());
        this.superButton.getStyleClass().add(BUILD_GRAPHIC_CLASS);
        this.superButton.setGraphic(
                SVGLibrary.getGraphic(SVGData.GEAR, BUILD_GRAPHIC_SIZE, null, BUILD_GRAPHIC_CLASS));

        String text = this.superButton.getText();
        if (text != null) {
            this.superButton.setText(text + " ");
        }

        this.superButton.setOnAction(event -> {
            if (this.modeler == null) {
                return;
            }

            int n1 = this.getScaleValue(this.scaleField1);
            int n2 = this.getScaleValue(this.scaleField2);
            boolean status = this.modeler.changeArea(n1, n2);

            if (!status) {
                this.showErrorDialog();

                if (this.scaleField1 != null) {
                    this.scaleField1.setText(Integer.toString(this.modeler.getScaleA()));
                }

                if (this.scaleField2 != null) {
                    this.scaleField2.setText(Integer.toString(this.modeler.getScaleB()));
                }
            }

            this.superButton.setDisable(!this.isAvailSuper());
        });
    }

    private int getScaleValue(TextField textField) {
        if (textField == null) {
            return 0;
        }

        String text = textField.getText();
        text = text == null ? null : text.trim();
        if (text == null || text.isEmpty()) {
            return 0;
        }

        int value = 0;

        try {
            value = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return 0;
        }

        return value;
    }

    private boolean isAvailSuper() {
        int n1 = this.getScaleValue(this.scaleField1);
        if (n1 < 1) {
            return false;
        }

        int n2 = this.getScaleValue(this.scaleField2);
        if (n2 < 1) {
            return false;
        }

        if (this.modeler != null) {
            int m1 = this.modeler.getScaleA();
            int m2 = this.modeler.getScaleB();
            if (n1 == m1 && n2 == m2) {
                return false;
            }
        }

        return (n1 * n2) > 0;
    }

    private void showErrorDialog() {
        Alert alert = new Alert(AlertType.ERROR);
        QEFXMain.initializeDialogOwner(alert);
        alert.setHeaderText("Error has occurred in modering.");
        alert.setContentText("Atoms are too much.");
        alert.showAndWait();
    }

    private void setupSlabSlider() {
        if (this.slabSlider == null) {
            return;
        }

        this.slabSlider.setValue(SlabModel.defaultThickness());

        this.slabSlider.valueProperty().addListener(o -> {
            if (this.modeler == null) {
                return;
            }

            double thickness = this.slabSlider.getValue();

            this.slabThread.runLater(() -> {
                this.modeler.changeThickness(Math.max(thickness, 0.0));
            });
        });
    }

    private void setupVacuumSlider() {
        if (this.vacuumSlider == null) {
            return;
        }

        this.vacuumSlider.setValue(SlabModel.defaultVacuum());

        this.vacuumSlider.valueProperty().addListener(o -> {
            if (this.modeler == null) {
                return;
            }

            double vacuum = this.vacuumSlider.getValue();

            this.vacuumThread.runLater(() -> {
                this.modeler.changeVacuum(Math.max(vacuum, 0.0));
            });
        });
    }

    private void setupKindPane() {
        if (this.kindPane == null) {
            return;
        }

        this.kindPane.setOnMouseClicked(event -> {
            if (this.kindScroll != null) {
                this.kindScroll.requestFocus();
            }
        });
    }

    private void setupKindScroll() {
        if (this.kindScroll == null) {
            return;
        }

        this.kindScroll.setFocusTraversable(true);
    }

    private void setupKindTile(StackPane kindTile) {
        if (this.kindTile != null) {
            if (this.kindTile.getStyleClass().contains(ATOMS_TILE_CLASS2)) {
                this.kindTile.getStyleClass().remove(ATOMS_TILE_CLASS2);
            }
            this.kindTile.getStyleClass().add(ATOMS_TILE_CLASS1);
        }

        if (kindTile != null) {
            if (kindTile.getStyleClass().contains(ATOMS_TILE_CLASS1)) {
                kindTile.getStyleClass().remove(ATOMS_TILE_CLASS1);
            }
            kindTile.getStyleClass().add(ATOMS_TILE_CLASS2);
        }

        this.kindTile = kindTile;
    }

    private void setSlabModel(SlabModel slabModel) {
        if (this.modeler == null) {
            return;
        }

        if (slabModel != null) {
            this.modeler.setSlabModel(slabModel);
        } else {
            this.modeler.setSlabModel(null);
        }

        this.initializeFXComponents();
    }

    public void setSlabModels(SlabModel[] slabModels) {
        if (slabModels == null || slabModels.length < 1) {
            return;
        }

        if (this.kindPane == null) {
            return;
        }

        for (int i = 0; i < slabModels.length; i++) {
            SlabModel slabModel = slabModels[i];
            if (slabModel == null) {
                continue;
            }

            Cell cell = Cell.getEmptyCell();
            if (cell == null) {
                return;
            }

            if (!slabModel.putOnCell(cell)) {
                return;
            }

            StackPane pane = new StackPane();
            TilePane.setMargin(pane, new Insets(ATOMS_TILE_INSET));
            pane.getStyleClass().add(ATOMS_TILE_CLASS1);
            pane.setOnMouseClicked(event -> {
                if (event != null && event.getClickCount() >= 2) {
                    this.setupKindTile(pane);
                    this.setSlabModel(slabModel);
                }
            });

            AtomsVLight atomsVLight = new AtomsVLight(cell, ATOMS_TILE_SIZE, true);
            atomsVLight.appendScale(ATOMS_TILE_SCLAE);
            pane.getChildren().add(atomsVLight);

            Label label = new Label("#" + Integer.toString(i + 1));
            label.getStyleClass().add(ATOMS_LABEL_CLASS);
            StackPane.setAlignment(label, Pos.TOP_LEFT);
            StackPane.setMargin(label, new Insets(ATOMS_LABEL_INSET));
            pane.getChildren().add(label);

            this.kindPane.getChildren().add(pane);

            if (i == 0) {
                this.setupKindTile(pane);
                this.setSlabModel(slabModel);
            }
        }
    }

    public void cleanSlabModels() {
        if (this.kindPane != null) {
            this.kindPane.getChildren().clear();
        }

        this.setupKindTile(null);
        this.setSlabModel(null);
    }
}
