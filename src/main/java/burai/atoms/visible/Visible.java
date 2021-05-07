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

package burai.atoms.visible;

import burai.atoms.design.Design;
import burai.atoms.model.Model;
import burai.atoms.model.event.ModelEvent;
import burai.atoms.model.event.ModelEventListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Group;

public abstract class Visible<M extends Model<? extends ModelEvent, ? extends ModelEventListener>> extends Group
        implements ModelEventListener {

    protected M model;

    protected Design design;

    private BooleanProperty toBeFlushed;

    protected Visible(M model, Design design) {
        super();

        if (model == null) {
            throw new IllegalArgumentException("model is null.");
        }

        this.model = model;
        this.design = design;
        this.toBeFlushed = null;
    }

    public M getModel() {
        return this.model;
    }

    public BooleanProperty toBeFlushedProperty() {
        if (this.toBeFlushed == null) {
            this.toBeFlushed = new SimpleBooleanProperty(false);
        }

        return this.toBeFlushed;
    }

    public void setToBeFlushed(boolean toBeFlushed) {
        this.toBeFlushedProperty().set(toBeFlushed);
    }

    @Override
    public boolean isToBeFlushed() {
        return this.toBeFlushedProperty().get();
    }

    @Override
    public void onModelDisplayed(ModelEvent event) {
        this.setVisible(true);
    }

    @Override
    public void onModelNotDisplayed(ModelEvent event) {
        this.setVisible(false);
    }
}
