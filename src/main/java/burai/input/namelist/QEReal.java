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

public class QEReal extends QEValueBase {

    private double realValue;

    public QEReal(String name, double r) {
        super(name);
        this.realValue = r;
    }

    @Override
    public int getIntegerValue() {
        return (int) this.realValue;
    }

    @Override
    public double getRealValue() {
        return this.realValue;
    }

    @Override
    public boolean getLogicalValue() {
        int intValue = (int) this.realValue;
        return intValue != 0;
    }

    @Override
    public String getCharacterValue() {
        return String.format("%12.5e", Math.abs(this.realValue) < 1.0e-20 ? 0.0 : this.realValue);
    }
}
