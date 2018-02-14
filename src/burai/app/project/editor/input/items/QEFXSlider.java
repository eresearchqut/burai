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
import burai.input.namelist.QEValue;
import burai.input.namelist.QEValueBuffer;

public abstract class QEFXSlider extends QEFXItem<Slider> {

    private QEValue defaultValue;

    protected QEFXSlider(QEValueBuffer valueBuffer, Slider controlItem, QEValue defaultValue) {
        super(valueBuffer, controlItem);

        this.defaultValue = defaultValue;
        this.setupSlider();
    }

    protected abstract void setToValueBuffer(double value);

    protected abstract void setToControlItem(QEValue qeValue);

    private void setupSlider() {
        if (this.valueBuffer.hasValue()) {
            this.onValueChanged(this.valueBuffer.getValue());
        } else {
            this.setToControlItem(this.defaultValue);
            this.setToValueBuffer(this.controlItem.getValue());
        }

        this.controlItem.valueProperty().addListener(o -> {
            double value = this.controlItem.getValue();
            this.setToValueBuffer(value);
        });
    }

    @Override
    protected void onValueChanged(QEValue qeValue) {
        if (qeValue == null) {
            this.setToControlItem(this.defaultValue);
        } else {
            this.setToControlItem(qeValue);
        }
    }
}
