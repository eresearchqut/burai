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

package burai.app.project.editor.input.items;

import burai.com.math.Calculator;
import burai.input.namelist.QEReal;
import burai.input.namelist.QEValueBuffer;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Callback;

public class QEFXTextFieldDouble extends QEFXTextField {

    public static final int BOUND_TYPE_NULL = 0;
    public static final int BOUND_TYPE_LESS_THAN = 1;
    public static final int BOUND_TYPE_LESS_EQUAL = 2;

    private int typeUpperBound;
    private int typeLowerBound;
    private double upperBound;
    private double lowerBound;

    private String hintMessage;

    private QEFXUnit unit;

    private Callback<String, String> valueFactoryInner;

    private Callback<String, String> textFactoryInner;

    private Callback<String, String> valueFactoryDefault;

    public QEFXTextFieldDouble(QEValueBuffer valueBuffer, TextField controlItem) {
        super(valueBuffer, controlItem);

        this.typeUpperBound = BOUND_TYPE_NULL;
        this.typeLowerBound = BOUND_TYPE_NULL;
        this.upperBound = 0.0;
        this.lowerBound = 0.0;

        this.hintMessage = null;

        this.unit = null;

        this.setupFactorys();

        this.setHintMessage(null);

        this.setupWarnning();
    }

    private void setupFactorys() {
        this.valueFactoryInner = null;

        this.textFactoryInner = null;

        this.valueFactoryDefault = (text) -> {
            if (text == null) {
                return null;
            }

            String value = null;
            try {
                double dbleText = Calculator.expr(text);
                value = Double.toString(dbleText);
            } catch (NumberFormatException e) {
                value = text;
            }
            return value;
        };

        this.valueFactory = this.valueFactoryDefault;
    }

    private void setupWarnning() {
        this.addWarningCondition((name, value) -> {
            String text = this.controlItem.getText();
            if (text == null || text.trim().isEmpty()) {
                return WarningCondition.OK;
            }

            double x = 0.0;
            try {
                x = Calculator.expr(text);
            } catch (NumberFormatException e) {
                return WarningCondition.ERROR;
            }

            if (!this.checkDoubleValue(x)) {
                return WarningCondition.ERROR;
            }

            return WarningCondition.OK;
        });

        this.pullAllTriggers();
    }

    private boolean checkDoubleValue(double x) {
        double y = x;
        if (this.unit != null) {
            y = this.unit.convertToValue(x);
        }

        if (this.typeUpperBound == BOUND_TYPE_LESS_THAN) {
            if (y < this.upperBound) {
                // NOP
            } else {
                return false;
            }
        } else if (this.typeUpperBound == BOUND_TYPE_LESS_EQUAL) {
            if (y <= this.upperBound) {
                // NOP
            } else {
                return false;
            }
        }

        if (this.typeLowerBound == BOUND_TYPE_LESS_THAN) {
            if (this.lowerBound < y) {
                // NOP
            } else {
                return false;
            }
        } else if (this.typeLowerBound == BOUND_TYPE_LESS_EQUAL) {
            if (this.lowerBound <= y) {
                // NOP
            } else {
                return false;
            }
        }

        return true;
    }

    public void setUpperBound(double bound, int type) {
        this.upperBound = bound;
        this.typeUpperBound = type;

        this.setHintMessage(this.hintMessage);

        if (this.valueBuffer.hasValue()) {
            this.onValueChanged(this.valueBuffer.getValue());
        }

        this.pullAllTriggers();
    }

    public void setLowerBound(double bound, int type) {
        this.lowerBound = bound;
        this.typeLowerBound = type;

        this.setHintMessage(this.hintMessage);

        if (this.valueBuffer.hasValue()) {
            this.onValueChanged(this.valueBuffer.getValue());
        }

        this.pullAllTriggers();
    }

    @Override
    public void setHintMessage(String message) {
        this.hintMessage = message;

        String formula = null;

        String title = null;
        if (this.label != null) {
            title = this.label.getText();
        }

        if (title != null) {
            formula = " " + title.trim() + " ";
        } else {
            formula = " x ";
        }

        double upperBound2 = this.upperBound;
        if (this.unit != null) {
            upperBound2 = this.unit.convertToText(this.upperBound);
        }
        if (this.typeUpperBound == BOUND_TYPE_NULL) {
            formula = formula + "< Inf.";
        } else if (this.typeUpperBound == BOUND_TYPE_LESS_THAN) {
            formula = formula + "< " + new QEReal("x", upperBound2).getCharacterValue();
        } else if (this.typeUpperBound == BOUND_TYPE_LESS_EQUAL) {
            formula = formula + "<= " + new QEReal("x", upperBound2).getCharacterValue();
        }

        double lowerBound2 = this.lowerBound;
        if (this.unit != null) {
            lowerBound2 = this.unit.convertToText(this.lowerBound);
        }
        if (this.typeLowerBound == BOUND_TYPE_NULL) {
            formula = "-Inf. <" + formula;
        } else if (this.typeLowerBound == BOUND_TYPE_LESS_THAN) {
            formula = new QEReal("x", lowerBound2).getCharacterValue() + " <" + formula;
        } else if (this.typeLowerBound == BOUND_TYPE_LESS_EQUAL) {
            formula = new QEReal("x", lowerBound2).getCharacterValue() + " <=" + formula;
        }

        if (this.hintMessage == null || this.hintMessage.trim().isEmpty()) {
            super.setHintMessage(formula);
        } else {
            super.setHintMessage(formula + System.lineSeparator() + this.hintMessage);
        }
    }

    public void setUnit(QEFXUnit unit) {
        if (this.unit == null && unit != null) {
            if (this.valueFactory == this.valueFactoryDefault) {
                this.valueFactory = null;
            }

            this.valueFactoryInner = this.valueFactory;
            this.textFactoryInner = this.textFactory;

        } else if (this.unit != null && unit == null) {
            this.valueFactory = this.valueFactoryInner;
            this.textFactory = this.textFactoryInner;
            this.valueFactoryInner = null;
            this.textFactoryInner = null;

            if (this.valueFactory == null) {
                this.valueFactory = this.valueFactoryDefault;
            }
        }

        this.unit = unit;

        if (this.unit != null) {
            this.valueFactory = (text) -> {
                String medium = this.unit.convertToValue(text);
                String value = medium;
                if (this.valueFactoryInner != null) {
                    value = this.valueFactoryInner.call(medium);
                }
                return value;
            };

            this.textFactory = (value) -> {
                String medium = value;
                if (this.textFactoryInner != null) {
                    medium = this.textFactoryInner.call(value);
                }
                String text = this.unit.convertToText(medium);
                return text;
            };

            this.unit.setOnAction(event -> {
                this.setHintMessage(this.hintMessage);

                String text = this.controlItem.getText();
                this.controlItem.setText(text);
            });
        }

        this.setHintMessage(this.hintMessage);

        String text = this.controlItem.getText();
        this.controlItem.setText(text);

        if (this.valueBuffer.hasValue()) {
            this.onValueChanged(this.valueBuffer.getValue());
        }

        this.pullAllTriggers();
    }

    @Override
    public void setValueFactory(Callback<String, String> valueFactory) {
        if (this.unit == null) {
            this.valueFactory = valueFactory;
        } else {
            this.valueFactoryInner = valueFactory;
        }
    }

    @Override
    public void setTextFactory(Callback<String, String> textFactory) {
        if (this.unit == null) {
            this.textFactory = textFactory;
        } else {
            this.textFactoryInner = textFactory;
        }

        if (this.valueBuffer.hasValue()) {
            this.onValueChanged(this.valueBuffer.getValue());
        }

        this.pullAllTriggers();
    }

    @Override
    public void setLabel(Label label) {
        super.setLabel(label);
        this.setHintMessage(this.hintMessage);
    }

    @Override
    public void setDisable(boolean disable) {
        super.setDisable(disable);

        if (this.unit != null) {
            this.unit.setDisable(disable);
        }
    }
}
