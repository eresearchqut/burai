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

package burai.app.tab;

import javafx.scene.control.Tooltip;
import javafx.scene.web.WebEngine;

public class QEFXWebTab extends QEFXTab<WebEngine> {

    public QEFXWebTab(WebEngine engine) {
        super(engine);

        this.setupOnClosed();
        this.setupContent();
        this.updateTabTitle();
    }

    private void setupOnClosed() {
        this.setOnClosed(event -> {
            this.body.load("about:blank");
        });
    }

    private void setupContent() {
        this.body.titleProperty().addListener(o -> {
            this.updateTabTitle();
        });

        this.body.locationProperty().addListener(o -> {
            this.updateTabTitle();
        });
    }

    private void updateTabTitle() {
        String title = this.body.getTitle();
        String location = this.body.getLocation();

        if (title == null || title.trim().isEmpty()) {
            this.setTabTitle(this.body.getLocation());
        } else {
            this.setTabTitle(title);
        }

        this.setTooltip(new Tooltip(location));
    }

    public boolean equalsEngine(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }

        QEFXWebTab other = (QEFXWebTab) obj;
        return this.body == other.body;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }

        QEFXWebTab other = (QEFXWebTab) obj;

        String location1 = this.body.getLocation();
        String location2 = other.body.getLocation();
        if (location1 != null) {
            return location1.equals(location2);
        }

        return location1 == location2;
    }
}
