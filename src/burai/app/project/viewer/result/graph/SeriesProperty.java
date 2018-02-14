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

package burai.app.project.viewer.result.graph;

public class SeriesProperty {

    public static final int DASH_NULL = 0;

    public static final int DASH_SMALL = 1;

    public static final int DASH_LARGE = 2;

    public static final int DASH_HYBRID = 3;

    private String name;

    private String color;

    private double width;

    private int dash;

    private boolean withSymbol;

    public SeriesProperty() {
        this.name = "SERIES";
        this.color = "black";
        this.width = 1.0;
        this.dash = DASH_NULL;
        this.withSymbol = true;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public double getWidth() {
        return this.width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public int getDash() {
        return this.dash;
    }

    public static String getDashStyle(int dash) {
        switch (dash) {
        case DASH_NULL:
            return null;
        case DASH_SMALL:
            return "4 3 4 3";
        case DASH_LARGE:
            return "12 4 12 4";
        case DASH_HYBRID:
            return "12 4 4 4";
        default:
            return null;
        }
    }

    public String getDashStyle() {
        return getDashStyle(this.dash);
    }

    public void setDash(int dash) {
        this.dash = dash;
    }

    public boolean isWithSymbol() {
        return this.withSymbol;
    }

    public void setWithSymbol(boolean withSymbol) {
        this.withSymbol = withSymbol;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
