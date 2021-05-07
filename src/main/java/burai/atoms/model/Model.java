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

package burai.atoms.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import burai.atoms.model.event.ModelEvent;
import burai.atoms.model.event.ModelEventListener;
import burai.atoms.model.property.ModelProperty;
import burai.atoms.model.property.ModelPropertyListener;

public abstract class Model<E extends ModelEvent, L extends ModelEventListener> {

    protected boolean inDisplayed;

    protected List<L> listeners;

    protected Map<String, ModelProperty> properties;

    protected Model() {
        this.inDisplayed = false;
        this.listeners = null;
        this.properties = null;
    }

    protected abstract E createEvent();

    public boolean isInDisplayed() {
        return this.inDisplayed;
    }

    public void addListener(L listener) {
        this.addListener(listener, false);
    }

    public void addListenerFirst(L listener) {
        this.addListener(listener, true);
    }

    private void addListener(L listener, boolean first) {
        if (listener == null) {
            return;
        }

        if (this.listeners == null) {
            this.listeners = new ArrayList<L>();
        }

        if (first) {
            this.listeners.add(0, listener);
        } else {
            this.listeners.add(listener);
        }
    }

    public void flushListeners() {
        if (this.listeners == null || this.listeners.isEmpty()) {
            return;
        }

        ModelEventListener[] listenerArray =
                this.listeners.toArray(new ModelEventListener[this.listeners.size()]);

        for (ModelEventListener listener : listenerArray) {
            if (listener.isToBeFlushed()) {
                this.listeners.remove(listener);
            }
        }
    }

    public boolean hasProperty(String key) {
        if (key == null) {
            return false;
        }

        if (this.properties == null || this.properties.isEmpty()) {
            return false;
        }

        ModelProperty property = this.properties.get(key);
        if (property == null) {
            return false;
        }

        return property.getProperty() != null;
    }

    public boolean booleanProperty(String key) {
        Object obj = this.getProperty(key);

        if (obj != null && obj instanceof Boolean) {
            return ((Boolean) obj).booleanValue();
        }

        return false;
    }

    public int intProperty(String key) {
        Object obj = this.getProperty(key);

        if (obj != null && obj instanceof Integer) {
            return ((Integer) obj).intValue();
        }

        return 0;
    }

    public double doubleProperty(String key) {
        Object obj = this.getProperty(key);

        if (obj != null && obj instanceof Double) {
            return ((Double) obj).doubleValue();
        }

        return 0.0;
    }

    public String stringProperty(String key) {
        Object obj = this.getProperty(key);
        return obj == null ? null : obj.toString();
    }

    public Object getProperty(String key) {
        if (key == null) {
            return null;
        }

        if (this.properties == null) {
            return null;
        }

        if (!this.properties.containsKey(key)) {
            return null;
        }

        ModelProperty property = this.properties.get(key);
        return property.getProperty();
    }

    private ModelProperty getModelProperty(String key) {
        if (key == null) {
            return null;
        }

        if (this.properties == null) {
            this.properties = new HashMap<String, ModelProperty>();
        }

        if (!this.properties.containsKey(key)) {
            this.properties.put(key, new ModelProperty());
        }

        return this.properties.get(key);
    }

    public void setProperty(String key, boolean value) {
        this.setProperty(key, Boolean.valueOf(value));
    }

    public void setProperty(String key, int value) {
        this.setProperty(key, Integer.valueOf(value));
    }

    public void setProperty(String key, double value) {
        this.setProperty(key, Double.valueOf(value));
    }

    public void setProperty(String key, Object value) {
        if (key == null) {
            return;
        }

        ModelProperty property = this.getModelProperty(key);
        if (property != null) {
            property.setProperty(value);
        }
    }

    public void removeProperty(String key) {
        if (key == null) {
            return;
        }

        if (this.properties == null || this.properties.isEmpty()) {
            return;
        }

        this.properties.remove(key);
    }

    public void addPropertyListener(String key, ModelPropertyListener propertyListener) {
        if (key == null) {
            return;
        }

        if (propertyListener == null) {
            return;
        }

        ModelProperty property = this.getModelProperty(key);
        if (property != null) {
            property.addListener(propertyListener);
        }
    }

    public void display() {
        if (this.inDisplayed) {
            return;
        }

        this.inDisplayed = true;

        if (this.listeners != null) {
            ModelEvent event = this.createEvent();
            for (L listener : this.listeners) {
                listener.onModelDisplayed(event);
            }
        }
    }

    public void notDisplay() {
        if (!this.inDisplayed) {
            return;
        }

        this.inDisplayed = false;

        if (this.listeners != null) {
            E event = this.createEvent();
            for (L listener : this.listeners) {
                listener.onModelNotDisplayed(event);
            }
        }
    }
}
