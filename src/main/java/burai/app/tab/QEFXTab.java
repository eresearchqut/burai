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

import javafx.scene.control.Tab;

public abstract class QEFXTab<T> extends Tab {

    private static final int MAX_TEXT = 16;
    private static final String DEFAULT_TEXT = "No Name";

    protected T body;

    public QEFXTab(T body) {
        super();

        if (body == null) {
            throw new IllegalArgumentException("body is null.");
        }

        this.body = body;
        this.setClosable(true);
    }

    public T getBody() {
        return this.body;
    }

    protected void setTabTitle(String title) {
        String text = null;
        if (title == null || title.trim().isEmpty()) {
            text = DEFAULT_TEXT;
        } else {
            text = title.trim();
        }

        if (text.length() > (MAX_TEXT + 3)) {
            text = text.substring(0, MAX_TEXT) + "...";
        }

        this.setText(text);
    }

    @Override
    public int hashCode() {
        return this.body == null ? 0 : this.body.hashCode();
    }

    @Override
    public abstract boolean equals(Object obj);

}
