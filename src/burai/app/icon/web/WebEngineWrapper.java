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

package burai.app.icon.web;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.web.WebEngine;
import burai.com.env.Environments;

public class WebEngineWrapper {

    private WebEngine engine;

    private ReadOnlyStringWrapper title;

    private ReadOnlyStringWrapper location;

    public WebEngineWrapper() {
        this(null);
    }

    public WebEngineWrapper(String url) {
        String url2 = url == null ? null : url.trim();

        String strTitle = null;
        if (url2 != null && (!url2.isEmpty())) {
            strTitle = Environments.titleWebsite(url2);
        }

        this.location = new ReadOnlyStringWrapper(this, "location");
        this.location.set(url2);

        this.title = new ReadOnlyStringWrapper(this, "title");
        if (strTitle != null && (!strTitle.isEmpty())) {
            this.title.set(strTitle);
        }

        this.engine = null;
        if (strTitle == null || strTitle.isEmpty()) {
            this.engine = new WebEngine();
        }
    }

    public WebEngine getWebEngine() {
        return this.engine;
    }

    public void load(String url) {
        if (this.engine != null) {
            this.engine.load(url);
        }
    }

    public String getTitle() {
        if (this.engine == null) {
            return this.title.getValue();
        } else {
            return this.engine.getTitle();
        }
    }

    public ReadOnlyStringProperty titleProperty() {
        if (this.engine == null) {
            return this.title.getReadOnlyProperty();
        } else {
            return this.engine.titleProperty();
        }
    }

    public String getLocation() {
        if (this.engine == null) {
            return this.location.getValue();
        } else {
            return this.engine.getLocation();
        }
    }

    public ReadOnlyStringProperty locationProperty() {
        if (this.engine == null) {
            return this.location.getReadOnlyProperty();
        } else {
            return this.engine.locationProperty();
        }
    }
}
