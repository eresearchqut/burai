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

package burai.input.namelist;

public class QEInteger extends QEValueBase {

    private int intValue;

    public QEInteger(String name, int i) {
        super(name);
        this.intValue = i;
    }

    @Override
    public int getIntegerValue() {
        return this.intValue;
    }

    @Override
    public double getRealValue() {
        return (double) this.intValue;
    }

    @Override
    public boolean getLogicalValue() {
        return this.intValue != 0;
    }

    @Override
    public String getCharacterValue() {
        return String.valueOf(this.intValue);
    }
}
