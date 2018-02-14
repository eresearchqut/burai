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

import java.util.ArrayList;
import java.util.List;

public class CIFLoopElement {

    private CIFLoopDefinition definition;

    private List<String> values;

    public CIFLoopElement(CIFLoopDefinition definition) {
        if (definition == null || definition.isEmpty()) {
            throw new IllegalArgumentException("loop definition of CIF is empty.");
        }

        this.definition = definition;

        this.values = new ArrayList<String>();
    }

    public boolean addValue(String value) {
        if (this.values.size() >= this.definition.numNames()) {
            return false;
        }

        return this.values.add(value);
    }

    public boolean addValue(String[] valueList) {
        if (valueList == null || valueList.length < 1) {
            return false;
        }

        boolean stat = true;
        for (String value : valueList) {
            stat = stat && this.addValue(value);
        }

        return stat;
    }

    public String getValue(String name) {
        int index = this.definition.indexOf(name);
        if (index < 0) {
            return null;
        }

        return values.get(index);
    }

    public boolean isEmpty() {
        return this.values.isEmpty();
    }
}
