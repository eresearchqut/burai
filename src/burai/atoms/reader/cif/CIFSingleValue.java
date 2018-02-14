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

package burai.atoms.reader.cif;

import burai.com.str.SmartSplitter;

public class CIFSingleValue {

    private String name;

    private String value;

    public CIFSingleValue() {
        this.name = null;
        this.value = null;
    }

    public boolean isName(String name) {
        if (this.name == null || this.name.isEmpty()) {
            return false;
        }

        return this.name.equalsIgnoreCase(name);
    }

    public boolean hasValue() {
        if (this.name == null || this.name.isEmpty()) {
            return false;
        }

        if (this.value == null || this.value.isEmpty()) {
            return false;
        }

        return true;
    }

    public String getValue() {
        return this.value;
    }

    public boolean read(String line) {
        if (line == null || line.isEmpty()) {
            return false;
        }

        //String[] subLines = line.trim().split("\\s+");
        String[] subLines = SmartSplitter.split(line.trim());
        if (subLines == null || subLines.length < 2) {
            return false;
        }

        if (!subLines[0].startsWith("_")) {
            return false;
        }

        this.name = subLines[0];

        this.value = subLines[1];

        return true;
    }
}
