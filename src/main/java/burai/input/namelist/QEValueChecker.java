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


public class QEValueChecker {

    private String data;

    public QEValueChecker(String data) {
        this.data = data;
    }

    public boolean isInteger() {
        if (this.data == null) {
            return false;
        }

        try {
            Integer.parseInt(this.data);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public int getInteger() {
        return Integer.parseInt(this.data);
    }

    public boolean isReal() {
        if (this.data == null) {
            return false;
        }

        try {
            Double.parseDouble(this.data.replace('d', 'e').replace('D', 'E'));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public double getReal() {
        return Double.parseDouble(this.data.replace('d', 'e').replace('D', 'E'));
    }

    public boolean isLogical() {
        if (this.isTrue()) {
            return true;
        }

        if (this.isFalse()) {
            return true;
        }

        return false;
    }

    public boolean getLogical() {
        if(isTrue()) {
            return true;
        }

        if(isFalse()) {
            return false;
        }

        throw new IllegalArgumentException(this.data + " is not logical.");
    }

    private boolean isTrue() {
        if (this.data == null) {
            return false;
        }

        String data2 = this.data.trim().toUpperCase();

        if ("T".equals(data2)) {
            return true;
        }
        if (".T.".equals(data2)) {
            return true;
        }
        if ("TRUE".equals(data2)) {
            return true;
        }
        if (".TRUE.".equals(data2)) {
            return true;
        }

        return false;
    }

    private boolean isFalse() {
        if (this.data == null) {
            return false;
        }

        String data2 = this.data.trim().toUpperCase();

        if ("F".equals(data2)) {
            return true;
        }
        if (".F.".equals(data2)) {
            return true;
        }
        if ("FALSE".equals(data2)) {
            return true;
        }
        if (".FALSE.".equals(data2)) {
            return true;
        }

        return false;
    }
}
