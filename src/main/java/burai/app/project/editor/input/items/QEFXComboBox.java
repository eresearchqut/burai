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

import java.util.List;

import javafx.scene.control.ComboBox;
import javafx.util.Callback;
import burai.input.namelist.QEValue;
import burai.input.namelist.QEValueBuffer;

public abstract class QEFXComboBox<V> extends QEFXItem<ComboBox<String>> {

    private static final String EMPTY_VALUE = null;

    private Callback<String, V> valueFactory;

    protected QEFXComboBox(QEValueBuffer valueBuffer, ComboBox<String> controlItem) {
        super(valueBuffer, controlItem);

        this.valueFactory = null;
        this.setupComboBox();
        this.setupWarnning();
    }

    protected abstract void setToValueBuffer(V value);

    protected abstract boolean setToControlItem(V value, QEValue qeValue, String item);

    private void setupComboBox() {
        if (this.valueBuffer.hasValue()) {
            this.onValueChanged(this.valueBuffer.getValue());
        } else {
            this.controlItem.setValue(EMPTY_VALUE);
        }

        this.controlItem.setOnAction(event -> {
            String value = this.controlItem.getValue();
            if (value == null) {
                return;
            }

            if (this.valueFactory == null) {
                this.valueBuffer.setValue(value);
                return;
            }

            V backedValue = this.valueFactory.call(value);
            if (backedValue != null) {
                this.setToValueBuffer(backedValue);
            }
        });
    }

    private void setupWarnning() {
        this.addWarningCondition((name, qeValue) -> {
            if (name != null && name.equalsIgnoreCase(this.valueBuffer.getName())) {
                if (qeValue != null) {
                    List<String> items = this.controlItem.getItems();
                    String itemValue = this.controlItem.getValue();
                    if (itemValue != null && (!itemValue.isEmpty()) && (!items.contains(itemValue))) {
                        return WarningCondition.ERROR;
                    }
                }
            }

            return WarningCondition.OK;
        });

        this.pullAllTriggers();
    }

    @Override
    protected void onValueChanged(QEValue qeValue) {
        if (qeValue == null) {
            this.controlItem.setValue(EMPTY_VALUE);
            return;
        }

        String value = qeValue.getCharacterValue();

        if (this.valueFactory == null) {
            this.controlItem.setValue(value);
            return;
        }

        List<String> items = this.controlItem.getItems();
        for (String item : items) {
            V backedValue = this.valueFactory.call(item);
            if (backedValue != null) {
                boolean hasSet = this.setToControlItem(backedValue, qeValue, item);
                if (hasSet) {
                    return;
                }
            }
        }

        this.controlItem.setValue(EMPTY_VALUE);
    }

    public void setEmpty() {
        this.controlItem.setValue(EMPTY_VALUE);
        this.pullAllTriggers();
    }

    public void setValueFactory(Callback<String, V> valueFactory) {
        this.valueFactory = valueFactory;

        if (this.valueBuffer.hasValue()) {
            this.onValueChanged(this.valueBuffer.getValue());
        }

        this.pullAllTriggers();
    }

    public void addItems(String... items) {
        if (items == null || items.length < 1) {
            return;
        }

        this.controlItem.getItems().addAll(items);

        if (this.valueBuffer.hasValue()) {
            this.onValueChanged(this.valueBuffer.getValue());
        }

        this.pullAllTriggers();
    }

    public void removeItems(String... items) {
        if (items == null || items.length < 1) {
            return;
        }

        this.controlItem.getItems().removeAll(items);

        if (this.valueBuffer.hasValue()) {
            this.onValueChanged(this.valueBuffer.getValue());
        }

        this.pullAllTriggers();
    }
}
