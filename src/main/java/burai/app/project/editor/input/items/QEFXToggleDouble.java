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

import javafx.scene.control.ToggleButton;
import burai.input.namelist.QEValue;
import burai.input.namelist.QEValueBuffer;

public class QEFXToggleDouble extends QEFXToggleButton<Double> {

    private double DELTA = 1.0e-20;

    public QEFXToggleDouble(QEValueBuffer valueBuffer, ToggleButton controlItem, boolean defaultSelected) {
        super(valueBuffer, controlItem, defaultSelected);
    }

    public QEFXToggleDouble(QEValueBuffer valueBuffer,
            ToggleButton controlItem, boolean defaultSelected, double onValue, double offValue) {
        this(valueBuffer, controlItem, defaultSelected);

        this.setValueFactory(selected -> {
            if (selected) {
                return onValue;
            } else {
                return offValue;
            }
        });
    }

    @Override
    protected void setToValueBuffer(Double value) {
        if (value != null) {
            this.valueBuffer.setValue(value.doubleValue());
        }
    }

    @Override
    protected boolean setToControlItem(Double value, QEValue qeValue, boolean selected) {
        if (value == null || qeValue == null) {
            return false;
        }

        if (Math.abs(value.doubleValue() - qeValue.getRealValue()) < DELTA) {
            this.controlItem.setSelected(selected);
            return true;
        }

        return false;
    }
}
