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

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import burai.input.namelist.QEInteger;
import burai.input.namelist.QEValueBuffer;

public class QEFXTextFieldInteger extends QEFXTextField {

    public static final int BOUND_TYPE_NULL = 0;
    public static final int BOUND_TYPE_LESS_THAN = 1;
    public static final int BOUND_TYPE_LESS_EQUAL = 2;

    private int typeUpperBound;
    private int typeLowerBound;
    private int upperBound;
    private int lowerBound;

    private String hintMessage;

    public QEFXTextFieldInteger(QEValueBuffer valueBuffer, TextField controlItem) {
        super(valueBuffer, controlItem);

        this.typeUpperBound = BOUND_TYPE_NULL;
        this.typeLowerBound = BOUND_TYPE_NULL;
        this.upperBound = 0;
        this.lowerBound = 0;

        this.hintMessage = null;

        this.setHintMessage(null);

        this.setupWarnning();
    }

    private void setupWarnning() {
        this.addWarningCondition((name, value) -> {
            String text = this.controlItem.getText();
            if (text == null || text.trim().isEmpty()) {
                return WarningCondition.OK;
            }

            int i = 0;
            try {
                i = Integer.parseInt(text);
            } catch (NumberFormatException e) {
                return WarningCondition.ERROR;
            }

            if (!this.checkIntegerValue(i)) {
                return WarningCondition.ERROR;
            }

            return WarningCondition.OK;
        });

        this.pullAllTriggers();
    }

    private boolean checkIntegerValue(int i) {
        if (this.typeUpperBound == BOUND_TYPE_LESS_THAN) {
            if (i < this.upperBound) {
                // NOP
            } else {
                return false;
            }
        } else if (this.typeUpperBound == BOUND_TYPE_LESS_EQUAL) {
            if (i <= this.upperBound) {
                // NOP
            } else {
                return false;
            }
        }

        if (this.typeLowerBound == BOUND_TYPE_LESS_THAN) {
            if (this.lowerBound < i) {
                // NOP
            } else {
                return false;
            }
        } else if (this.typeLowerBound == BOUND_TYPE_LESS_EQUAL) {
            if (this.lowerBound <= i) {
                // NOP
            } else {
                return false;
            }
        }

        return true;
    }

    public void setUpperBound(int bound, int type) {
        this.upperBound = bound;
        this.typeUpperBound = type;

        this.setHintMessage(this.hintMessage);

        if (this.valueBuffer.hasValue()) {
            this.onValueChanged(this.valueBuffer.getValue());
        }

        this.pullAllTriggers();
    }

    public void setLowerBound(int bound, int type) {
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
            formula = " i ";
        }

        if (this.typeUpperBound == BOUND_TYPE_NULL) {
            formula = formula + "< Inf.";
        } else if (this.typeUpperBound == BOUND_TYPE_LESS_THAN) {
            formula = formula + "< " + new QEInteger("i", this.upperBound).getCharacterValue();
        } else if (this.typeUpperBound == BOUND_TYPE_LESS_EQUAL) {
            formula = formula + "<= " + new QEInteger("i", this.upperBound).getCharacterValue();
        }

        if (this.typeLowerBound == BOUND_TYPE_NULL) {
            formula = "-Inf. <" + formula;
        } else if (this.typeLowerBound == BOUND_TYPE_LESS_THAN) {
            formula = new QEInteger("i", this.lowerBound).getCharacterValue() + " <" + formula;
        } else if (this.typeLowerBound == BOUND_TYPE_LESS_EQUAL) {
            formula = new QEInteger("i", this.lowerBound).getCharacterValue() + " <=" + formula;
        }

        if (this.hintMessage == null || this.hintMessage.trim().isEmpty()) {
            super.setHintMessage(formula);
        } else {
            super.setHintMessage(formula + System.lineSeparator() + this.hintMessage);
        }
    }

    @Override
    public void setLabel(Label label) {
        super.setLabel(label);
        this.setHintMessage(this.hintMessage);
    }
}
