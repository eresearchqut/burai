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

public class QECharacter extends QEValueBase {

    private String charValue;

    public QECharacter(String name, String c) {
        super(name);
        if (c == null) {
            this.charValue = "???";
        } else {
            this.charValue = c;
        }
    }

    @Override
    public int getIntegerValue() {
        int intValue = 0;

        QEValueChecker valueChecker = new QEValueChecker(this.charValue);
        if (valueChecker.isInteger()) {
            intValue = valueChecker.getInteger();
        }

        return intValue;
    }

    @Override
    public double getRealValue() {
        double realValue = 0.0;

        QEValueChecker valueChecker = new QEValueChecker(this.charValue);
        if (valueChecker.isReal()) {
            realValue = valueChecker.getReal();
        }

        return realValue;
    }

    @Override
    public boolean getLogicalValue() {
        boolean logValue = false;

        QEValueChecker valueChecker = new QEValueChecker(this.charValue);
        if (valueChecker.isLogical()) {
            logValue = valueChecker.getLogical();
        }

        return logValue;
    }

    @Override
    public String getCharacterValue() {
        return this.charValue;
    }

    @Override
    public String toString(int length) {
        String name = this.getName();
        while (name.length() < length) {
            name = name + " ";
        }

        return name + " = \"" + this.getCharacterValue() + "\"";
    }

    @Override
    public String toString() {
        return this.getName() + " = \"" + this.getCharacterValue() + "\"";
    }
}
