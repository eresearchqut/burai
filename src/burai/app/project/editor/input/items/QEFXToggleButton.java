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
import javafx.util.Callback;
import burai.com.graphic.ToggleGraphics;
import burai.input.namelist.QEValue;
import burai.input.namelist.QEValueBuffer;

public abstract class QEFXToggleButton<V> extends QEFXItem<ToggleButton> {

    private static final String TOGGLE_STYLE = "-fx-base: transparent";

    private static final double GRAPHIC_WIDTH = 185.0;
    private static final double GRAPHIC_HEIGHT = 24.0;
    private static final String GRAPHIC_TEXT_YES = "yes";
    private static final String GRAPHIC_TEXT_NO = "no";
    private static final String GRAPHIC_STYLE_YES = "toggle-graphic-on";
    private static final String GRAPHIC_STYLE_NO = "toggle-graphic-off";

    private boolean defaultSelected;

    private Callback<Boolean, V> valueFactory;

    protected QEFXToggleButton(QEValueBuffer valueBuffer, ToggleButton controlItem, boolean defaultSelected) {
        super(valueBuffer, controlItem);

        this.defaultSelected = defaultSelected;
        this.valueFactory = null;
        this.setupToggleGraphics();
        this.setupToggleButton();
    }

    protected abstract void setToValueBuffer(V value);

    protected abstract boolean setToControlItem(V value, QEValue qeValue, boolean selected);

    private void setupToggleGraphics() {
        this.controlItem.setText("");
        this.controlItem.setStyle(TOGGLE_STYLE);

        this.updateToggleGraphics();

        this.controlItem.selectedProperty().addListener(o -> {
            this.updateToggleGraphics();
        });
    }

    private void updateToggleGraphics() {
        if (this.controlItem.isSelected()) {
            this.controlItem.setGraphic(ToggleGraphics.getGraphic(
                    GRAPHIC_WIDTH, GRAPHIC_HEIGHT, true, GRAPHIC_TEXT_YES, GRAPHIC_STYLE_YES));
        } else {
            this.controlItem.setGraphic(ToggleGraphics.getGraphic(
                    GRAPHIC_WIDTH, GRAPHIC_HEIGHT, false, GRAPHIC_TEXT_NO, GRAPHIC_STYLE_NO));
        }
    }

    private void setupToggleButton() {
        if (this.valueBuffer.hasValue()) {
            this.onValueChanged(this.valueBuffer.getValue());
        } else {
            this.controlItem.setSelected(this.defaultSelected);
        }

        this.controlItem.setOnAction(event -> {
            boolean selected = this.controlItem.isSelected();

            if (this.valueFactory == null) {
                this.valueBuffer.setValue(selected);
                return;
            }

            V backedValue = this.valueFactory.call(selected);
            if (backedValue != null) {
                this.setToValueBuffer(backedValue);
            }
        });
    }

    @Override
    protected void onValueChanged(QEValue qeValue) {
        if (qeValue == null) {
            this.controlItem.setSelected(this.defaultSelected);
            return;
        }

        boolean value = qeValue.getLogicalValue();

        if (this.valueFactory == null) {
            this.controlItem.setSelected(value);
            return;
        }

        boolean[] selectedList = { true, false };
        for (boolean selected : selectedList) {
            V backedValue = this.valueFactory.call(selected);
            if (backedValue != null) {
                boolean hasSet = this.setToControlItem(backedValue, qeValue, selected);
                if (hasSet) {
                    return;
                }
            }
        }

        this.controlItem.setSelected(this.defaultSelected);
    }

    public void setValueFactory(Callback<Boolean, V> valueFactory) {
        this.valueFactory = valueFactory;

        if (this.valueBuffer.hasValue()) {
            this.onValueChanged(this.valueBuffer.getValue());
        }
    }
}
