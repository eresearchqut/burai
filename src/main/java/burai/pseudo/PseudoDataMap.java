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

package burai.pseudo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PseudoDataMap implements Map<String, PseudoData> {

    private Map<String, PseudoData> pseudos;

    public PseudoDataMap() {
        this.pseudos = null;
    }

    private Map<String, PseudoData> getMap() {
        if (this.pseudos == null) {
            this.pseudos = new HashMap<String, PseudoData>();
        }

        return this.pseudos;
    }

    @Override
    public int size() {
        return this.getMap().size();
    }

    @Override
    public boolean isEmpty() {
        return this.getMap().isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.getMap().containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.getMap().containsValue(value);
    }

    @Override
    public PseudoData get(Object key) {
        return this.getMap().get(key);
    }

    @Override
    public PseudoData put(String key, PseudoData value) {
        return this.getMap().put(key, value);
    }

    @Override
    public PseudoData remove(Object key) {
        return this.getMap().remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends PseudoData> m) {
        this.getMap().putAll(m);
    }

    @Override
    public void clear() {
        this.getMap().clear();
    }

    @Override
    public Set<String> keySet() {
        return this.getMap().keySet();
    }

    @Override
    public Collection<PseudoData> values() {
        return this.getMap().values();
    }

    @Override
    public Set<Entry<String, PseudoData>> entrySet() {
        return this.getMap().entrySet();
    }
}
