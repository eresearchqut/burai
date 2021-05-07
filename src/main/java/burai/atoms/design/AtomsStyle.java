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

package burai.atoms.design;

public enum AtomsStyle {

    BALL_STICK(0, "Ball & Stick"),

    BALL(1, "Ball"),

    STICK(2, "Stick");

    private int id;

    private String label;

    private AtomsStyle(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public int getId() {
        return this.id;
    }

    public static AtomsStyle getInstance(int id) {
        AtomsStyle[] atomsStyles = values();
        if (atomsStyles == null || atomsStyles.length < 1) {
            return null;
        }

        for (AtomsStyle atomsStyle : atomsStyles) {
            if (atomsStyle != null && id == atomsStyle.getId()) {
                return atomsStyle;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return this.label;
    }
}
