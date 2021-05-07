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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import burai.com.str.SmartSplitter;

public class CIFLoopValue {

    private static final int MARK_LIMIT = 1024;

    private static final String LOOP_HEADER = "loop_";

    private List<CIFLoopElement> elements;

    public CIFLoopValue() {
        this.elements = new ArrayList<CIFLoopElement>();
    }

    public int numValues() {
        return this.elements.size();
    }

    public boolean containsName(String name) {
        if (this.elements == null || this.elements.isEmpty()) {
            return false;
        }

        CIFLoopElement element = this.elements.get(0);
        if (element.isEmpty()) {
            return false;
        }

        String value = element.getValue(name);
        if (value == null) {
            return false;
        }

        return true;
    }

    public boolean hasElements() {
        if (this.elements == null || this.elements.isEmpty()) {
            return false;
        }

        if (elements.get(0).isEmpty()) {
            return false;
        }

        return true;
    }

    public CIFLoopElement[] listElements() {
        if (this.elements == null || this.elements.isEmpty()) {
            return null;
        }

        CIFLoopElement[] elementArray = new CIFLoopElement[this.elements.size()];
        return this.elements.toArray(elementArray);
    }

    public boolean readHeader(String line) {
        if (line == null || line.isEmpty()) {
            return false;
        }

        return LOOP_HEADER.equalsIgnoreCase(line.trim());
    }

    public boolean readBody(BufferedReader reader) throws IOException {
        String line = null;
        this.elements.clear();

        /*
         * read definition
         */
        CIFLoopDefinition definition = new CIFLoopDefinition();

        while (true) {
            reader.mark(MARK_LIMIT);
            line = reader.readLine();
            if (line != null) {
                line = line.trim();
            }

            if (line != null && line.startsWith("_")) {
                definition.addName(line);

            } else {
                reader.reset();
                break;
            }
        }

        if (definition.isEmpty()) {
            return false;
        }

        /*
         * read values
         */
        while (true) {
            reader.mark(MARK_LIMIT);
            line = reader.readLine();
            if (line != null) {
                line = line.trim();
            }

            if (line == null || line.isEmpty() || line.startsWith("_") || line.equals(LOOP_HEADER)) {
                reader.reset();
                break;

            } else {
                //String[] subLines = line.split("\\s+");
                String[] subLines = SmartSplitter.split(line);
                if (subLines.length < definition.numNames()) {
                    return false;
                }

                CIFLoopElement element = new CIFLoopElement(definition);
                element.addValue(subLines);
                this.elements.add(element);
            }
        }

        if (this.elements.isEmpty()) {
            return false;
        }

        return true;
    }
}
