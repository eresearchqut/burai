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

import javafx.scene.control.ComboBox;
import burai.input.namelist.QEValue;
import burai.input.namelist.QEValueBuffer;

public class QEFXComboBoolean extends QEFXComboBox<Boolean> {

    public QEFXComboBoolean(QEValueBuffer valueBuffer, ComboBox<String> controlItem) {
        super(valueBuffer, controlItem);
    }

    @Override
    protected void setToValueBuffer(Boolean value) {
        if (value != null) {
            this.valueBuffer.setValue(value.booleanValue());
        }
    }

    @Override
    protected boolean setToControlItem(Boolean value, QEValue qeValue, String item) {
        if (value == null || qeValue == null) {
            return false;
        }

        if (value.booleanValue() == qeValue.getLogicalValue()) {
            this.controlItem.setValue(item);
            return true;
        }

        return false;
    }
}
