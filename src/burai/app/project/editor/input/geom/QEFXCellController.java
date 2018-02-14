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

package burai.app.project.editor.input.geom;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import burai.app.QEFXMainController;
import burai.app.project.editor.input.QEFXInputModelController;
import burai.app.project.editor.input.items.QEFXComboInteger;
import burai.atoms.model.Cell;
import burai.com.consts.Constants;
import burai.com.math.Lattice;
import burai.input.QEInput;
import burai.input.card.QECard;
import burai.input.card.QECellParameters;
import burai.input.namelist.QENamelist;
import burai.input.namelist.QEValue;
import burai.input.namelist.QEValueBase;

public class QEFXCellController extends QEFXInputModelController {

    private static final String[] IBRAV_TEXTS = {
            "Free",
            "Cubic P (sc)",
            "Cubic F (fcc)",
            "Cubic I (bcc)",
            //"Cubic I (bcc), more symmetric",
            "Hexagonal and Trigonal P",
            "Trigonal R, 3fold axis c",
            "Trigonal R, 3fold axis 111",
            "Tetragonal P (st)",
            "Tetragonal I (bct)",
            "Orthorhombic P",
            "Orthorhombic base-c. #1",
            "Orthorhombic base-c. #2",
            "Orthorhombic face-c.",
            "Orthorhombic body-c.",
            "Monoclinic P, unique axis c",
            "Monoclinic P, unique axis b",
            "Monoclinic base-centered",
            "Triclinic"
    };

    private static final int[] IBRAV_INDEXES = {
            //0, 1, 2, 3, -3, 4, 5, -5, 6, 7, 8, 9, -9, 10, 11, 12, -12, 13, 14
            0, 1, 2, 3, 4, 5, -5, 6, 7, 8, 9, -9, 10, 11, 12, -12, 13, 14
    };

    private QEFXLatticeItem aItem;

    /*
     * ibrav
     */
    @FXML
    private Label ibravLabel;

    @FXML
    private ComboBox<String> ibravCombo;

    @FXML
    private Button ibravButton;

    /*
     * a
     */
    @FXML
    private Label aLabel;

    @FXML
    private TextField aField;

    @FXML
    private Button aButton;

    @FXML
    private ComboBox<String> aUnit;

    /*
     * b
     */
    @FXML
    private Label bLabel;

    @FXML
    private TextField bField;

    @FXML
    private Button bButton;

    @FXML
    private ComboBox<String> bUnit;

    /*
     * c
     */
    @FXML
    private Label cLabel;

    @FXML
    private TextField cField;

    @FXML
    private Button cButton;

    @FXML
    private ComboBox<String> cUnit;

    /*
     * alpha
     */
    @FXML
    private Label alphaLabel;

    @FXML
    private TextField alphaField;

    @FXML
    private Button alphaButton;

    @FXML
    private ComboBox<String> alphaUnit;

    /*
     * beta
     */
    @FXML
    private Label betaLabel;

    @FXML
    private TextField betaField;

    @FXML
    private Button betaButton;

    @FXML
    private ComboBox<String> betaUnit;

    /*
     * gamma
     */
    @FXML
    private Label gammaLabel;

    @FXML
    private TextField gammaField;

    @FXML
    private Button gammaButton;

    @FXML
    private ComboBox<String> gammaUnit;

    /*
     * Lattice Vector
     */
    @FXML
    private Label aVecLabel;

    @FXML
    private Label bVecLabel;

    @FXML
    private Label cVecLabel;

    @FXML
    private TextField aVecField1;

    @FXML
    private TextField aVecField2;

    @FXML
    private TextField aVecField3;

    @FXML
    private TextField bVecField1;

    @FXML
    private TextField bVecField2;

    @FXML
    private TextField bVecField3;

    @FXML
    private TextField cVecField1;

    @FXML
    private TextField cVecField2;

    @FXML
    private TextField cVecField3;

    @FXML
    private Button lattButton;

    @FXML
    private ComboBox<String> lattUnit;

    public QEFXCellController(QEFXMainController mainController, QEInput input, Cell modelCell) {
        super(mainController, input, modelCell);
        this.aItem = null;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setupIbravItem();
        this.setupAItem();
        this.setupBItem();
        this.setupCItem();
        this.setupAlphaItem();
        this.setupBetaItem();
        this.setupGammaItem();
        this.setupLatticeVector();
    }

    private void setupIbravItem() {
        if (this.ibravCombo == null) {
            return;
        }

        QENamelist nmlSystem = this.input.getNamelist(QEInput.NAMELIST_SYSTEM);
        if (nmlSystem == null) {
            return;
        }

        this.ibravCombo.getItems().clear();
        QEFXComboInteger item = new QEFXComboInteger(nmlSystem.getValueBuffer("ibrav"), this.ibravCombo);

        if (this.ibravLabel != null) {
            item.setLabel(this.ibravLabel);
        }

        if (this.ibravButton != null) {
            item.setDefault(() -> {
                double[][] lattice = this.modelCell.copyLattice();
                int ibrav = 0;
                if (lattice != null && lattice.length > 2) {
                    ibrav = Lattice.getBravais(lattice);
                }
                return QEValueBase.getInstance("ibrav", ibrav);

            }, this.ibravButton);
        }

        item.addItems(IBRAV_TEXTS);

        if (IBRAV_TEXTS.length != IBRAV_INDEXES.length) {
            throw new RuntimeException("IBRAV_TEXTS.length != IBRAV_INDEXES.length");
        }

        Map<String, Integer> ibravMap = new HashMap<String, Integer>();
        for (int i = 0; i < IBRAV_TEXTS.length; i++) {
            ibravMap.put(IBRAV_TEXTS[i], IBRAV_INDEXES[i]);
        }

        item.setValueFactory(text -> {
            return ibravMap.get(text);
        });
    }

    private int getIbrav() {
        QENamelist nmlSystem = this.input.getNamelist(QEInput.NAMELIST_SYSTEM);
        QEValue value = nmlSystem == null ? null : nmlSystem.getValue("ibrav");
        return value == null ? 0 : value.getIntegerValue();
    }

    private void setupAItem() {
        if (this.aField == null || this.aLabel == null || this.aButton == null || this.aUnit == null) {
            return;
        }

        QEFXLatticeItem item = new QEFXLatticeItem("a",
                this.input, this.aField, this.aLabel, this.aButton, this.aUnit, false);

        item.addEnabledCondition((name, value) -> this.isAEnabled(name, value));

        item.setDefault(() -> {
            int ibrav = this.getIbrav();
            return QEValueBase.getInstance("a", this.modelCell.getA(ibrav));
        });

        item.pullAllTriggers();

        this.aItem = item;
    }

    private boolean isAEnabled(String name, QEValue value) {
        if ("ibrav".equalsIgnoreCase(name)) {
            if (value == null) {
                return false;
            }
            int i = value.getIntegerValue();
            if (i == 0) {
                QECard card = this.input.getCard(QECellParameters.CARD_NAME);
                if (card != null && card instanceof QECellParameters) {
                    QECellParameters cellParameters = (QECellParameters) card;
                    if (cellParameters.isAlat()) {
                        return true;
                    } else {
                        return false;
                    }
                }
            } else {
                return true;
            }
        }

        return true;
    }

    private void setupBItem() {
        if (this.bField == null || this.bLabel == null || this.bButton == null || this.bUnit == null) {
            return;
        }

        QEFXLatticeItem item = new QEFXLatticeItem("b",
                this.input, this.bField, this.bLabel, this.bButton, this.bUnit, false);

        item.addEnabledCondition((name, value) -> this.isBEnabled(name, value));

        item.setDefault(() -> {
            int ibrav = this.getIbrav();
            return QEValueBase.getInstance("b", this.modelCell.getB(ibrav));
        });

        item.pullAllTriggers();
    }

    private boolean isBEnabled(String name, QEValue value) {
        if ("ibrav".equalsIgnoreCase(name)) {
            if (value == null) {
                return false;
            }
            int i = value.getIntegerValue();
            return i == 8 || i == 9 || i == -9 || i == 91 || i == 10 || i == 11 ||
                    i == 12 || i == -12 || i == 13 || i == -13 || i == 14;
        }

        return true;
    }

    private void setupCItem() {
        if (this.cField == null || this.cLabel == null || this.cButton == null || this.cUnit == null) {
            return;
        }

        QEFXLatticeItem item = new QEFXLatticeItem("c",
                this.input, this.cField, this.cLabel, this.cButton, this.cUnit, false);

        item.addEnabledCondition((name, value) -> this.isCEnabled(name, value));

        item.setDefault(() -> {
            int ibrav = this.getIbrav();
            return QEValueBase.getInstance("c", this.modelCell.getC(ibrav));
        });

        item.pullAllTriggers();
    }

    private boolean isCEnabled(String name, QEValue value) {
        if ("ibrav".equalsIgnoreCase(name)) {
            if (value == null) {
                return false;
            }
            int i = value.getIntegerValue();
            return i == 4 || i == 6 || i == 7 || i == 8 || i == 9 || i == -9 || i == 91 ||
                    i == 10 || i == 11 || i == 12 || i == -12 || i == 13 || i == -13 || i == 14;
        }

        return true;
    }

    private void setupAlphaItem() {
        if (this.alphaField == null || this.alphaLabel == null || this.alphaButton == null || this.alphaUnit == null) {
            return;
        }

        QEFXLatticeItem item = new QEFXLatticeItem("cosbc",
                this.input, this.alphaField, this.alphaLabel, this.alphaButton, this.alphaUnit, true);

        item.addEnabledCondition((name, value) -> this.isAlphaEnabled(name, value));

        item.setDefault(() -> {
            int ibrav = this.getIbrav();
            return QEValueBase.getInstance("cosbc", this.modelCell.getCosAlpha(ibrav));
        });

        item.pullAllTriggers();
    }

    private boolean isAlphaEnabled(String name, QEValue value) {
        if ("ibrav".equalsIgnoreCase(name)) {
            if (value == null) {
                return false;
            }
            int i = value.getIntegerValue();
            return i == 14;
        }

        return true;
    }

    private void setupBetaItem() {
        if (this.betaField == null || this.betaLabel == null || this.betaButton == null || this.betaUnit == null) {
            return;
        }

        QEFXLatticeItem item = new QEFXLatticeItem("cosac",
                this.input, this.betaField, this.betaLabel, this.betaButton, this.betaUnit, true);

        item.addEnabledCondition((name, value) -> this.isBetaEnabled(name, value));

        item.setDefault(() -> {
            int ibrav = this.getIbrav();
            return QEValueBase.getInstance("cosac", this.modelCell.getCosBeta(ibrav));
        });

        item.pullAllTriggers();
    }

    private boolean isBetaEnabled(String name, QEValue value) {
        if ("ibrav".equalsIgnoreCase(name)) {
            if (value == null) {
                return false;
            }
            int i = value.getIntegerValue();
            return i == -12 || i == -13 || i == 14;
        }

        return true;
    }

    private void setupGammaItem() {
        if (this.gammaField == null || this.gammaLabel == null || this.gammaButton == null || this.gammaUnit == null) {
            return;
        }

        QEFXLatticeItem item = new QEFXLatticeItem("cosab",
                this.input, this.gammaField, this.gammaLabel, this.gammaButton, this.gammaUnit, true);

        item.addEnabledCondition((name, value) -> this.isGammaEnabled(name, value));

        item.setDefault(() -> {
            int ibrav = this.getIbrav();
            return QEValueBase.getInstance("cosab", this.modelCell.getCosGamma(ibrav));
        });

        item.pullAllTriggers();
    }

    private boolean isGammaEnabled(String name, QEValue value) {
        if ("ibrav".equalsIgnoreCase(name)) {
            if (value == null) {
                return false;
            }
            int i = value.getIntegerValue();
            return i == 5 || i == -5 || i == 12 || i == 13 || i == 14;
        }

        return true;
    }

    private void setupLatticeVector() {
        if (this.aVecField1 == null || this.aVecField2 == null || this.aVecField3 == null) {
            return;
        }

        if (this.bVecField1 == null || this.bVecField2 == null || this.bVecField3 == null) {
            return;
        }

        if (this.cVecField1 == null || this.cVecField2 == null || this.cVecField3 == null) {
            return;
        }

        if (this.aVecLabel == null || this.bVecLabel == null || this.cVecLabel == null) {
            return;
        }

        if (this.lattButton == null || this.lattUnit == null) {
            return;
        }

        TextField[][] lattFields = {
                { this.aVecField1, this.aVecField2, this.aVecField3 },
                { this.bVecField1, this.bVecField2, this.bVecField3 },
                { this.cVecField1, this.cVecField2, this.cVecField3 }
        };

        Label[] vecLabels = {
                this.aVecLabel, this.bVecLabel, this.cVecLabel
        };

        QEFXLatticeVector latticeVector =
                new QEFXLatticeVector(this.input, lattFields, vecLabels, this.lattButton, this.lattUnit);

        latticeVector.setOnUnitChanged(event -> {
            if (this.aItem != null) {
                this.aItem.pullAllTriggers();
            }
        });

        latticeVector.setDefault(event -> {
            QECard card = this.input.getCard(QECellParameters.CARD_NAME);
            if (card != null && card instanceof QECellParameters) {
                QECellParameters cellParameters = (QECellParameters) card;

                double unit = 1.0;
                if (latticeVector.isAlat()) {
                    QEValue aValue = null;
                    QENamelist nmlSystem = this.input.getNamelist(QEInput.NAMELIST_SYSTEM);
                    if (nmlSystem != null) {
                        aValue = nmlSystem.getValue("a");
                    }
                    if (aValue != null) {
                        unit = 1.0 / aValue.getRealValue();
                    }
                } else if (latticeVector.isBohr()) {
                    unit = 1.0 / Constants.BOHR_RADIUS_ANGS;
                }

                double[][] lattice = this.modelCell.copyLattice();
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        lattice[i][j] *= unit;
                    }
                }

                cellParameters.setVector(1, lattice[0]);
                cellParameters.setVector(2, lattice[1]);
                cellParameters.setVector(3, lattice[2]);
            }

            this.lattUnit.requestFocus();
        });
    }
}
