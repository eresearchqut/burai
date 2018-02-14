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

import javafx.scene.control.Slider;
import burai.input.namelist.QEReal;
import burai.input.namelist.QEValue;
import burai.input.namelist.QEValueBuffer;

public class QEFXSliderDouble extends QEFXSlider {

    public QEFXSliderDouble(QEValueBuffer valueBuffer, Slider controlItem, double defaultValue) {
        super(valueBuffer, controlItem, new QEReal("x", defaultValue));
    }

    @Override
    protected void setToValueBuffer(double value) {
        this.valueBuffer.setValue(value);
    }

    @Override
    protected void setToControlItem(QEValue qeValue) {
        double maxValue = this.controlItem.getMax();
        double minValue = this.controlItem.getMin();
        double value = qeValue == null ? minValue : qeValue.getRealValue();
        value = Math.min(Math.max(minValue, value), maxValue);
        this.controlItem.setValue(value);
    }
}
