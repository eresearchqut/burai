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

public class CIFLoopDefinition {

    private List<String> names;

    public CIFLoopDefinition() {
        this.names = new ArrayList<String>();
    }

    public int numNames() {
        return this.names.size();
    }

    public boolean hasName(String name) {
        return this.names.contains(name);
    }

    public boolean addName(String name) {
        return this.names.add(name);
    }

    public boolean isEmpty() {
        return this.names.isEmpty();
    }

    public void clear() {
        this.names.clear();
    }

    public int indexOf(String name) {
        return this.names.indexOf(name);
    }
}
