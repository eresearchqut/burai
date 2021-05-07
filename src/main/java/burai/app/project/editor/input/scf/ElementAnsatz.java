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

package burai.app.project.editor.input.scf;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import burai.com.math.Calculator;

public class ElementAnsatz {

    private IntegerProperty index;
    private IntegerProperty index1;
    private StringProperty name;
    private StringProperty magX;
    private StringProperty magY;
    private StringProperty magZ;
    private StringProperty hubbard;

    public ElementAnsatz(int index) {
        this.setIndex(index);
        this.name = null;
        this.magX = null;
        this.magY = null;
        this.magZ = null;
        this.hubbard = null;
    }

    public void setIndex(int value) {
        this.indexProperty().set(value);
        this.index1Property().set(value + 1);
    }

    public int getIndex() {
        return this.indexProperty().get();
    }

    public IntegerProperty indexProperty() {
        if (this.index == null) {
            this.index = new SimpleIntegerProperty(this, "index");
        }

        return this.index;
    }

    public IntegerProperty index1Property() {
        if (this.index1 == null) {
            this.index1 = new SimpleIntegerProperty(this, "index1");
        }

        return this.index1;
    }

    public void setName(String value) {
        this.nameProperty().set(value);
    }

    public String getName() {
        return this.nameProperty().get();
    }

    public StringProperty nameProperty() {
        if (this.name == null) {
            this.name = new SimpleStringProperty(this, "name");
        }

        return this.name;
    }

    public void setMagX(String value) {
        this.magXProperty().set(value);
    }

    public void setMagX(double value) {
        this.magXProperty().set(String.format("%8.4f", value));
    }

    public String getMagX() {
        return this.magXProperty().get();
    }

    public double getMagXValue() throws RuntimeException {
        double value = 0.0;
        try {
            value = Calculator.expr(this.magXProperty().get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return value;
    }

    public StringProperty magXProperty() {
        if (this.magX == null) {
            this.magX = new SimpleStringProperty(this, "magX");
        }

        return this.magX;
    }

    public void setMagY(String value) {
        this.magYProperty().set(value);
    }

    public void setMagY(double value) {
        this.magYProperty().set(String.format("%8.4f", value));
    }

    public String getMagY() {
        return this.magYProperty().get();
    }

    public double getMagYValue() throws RuntimeException {
        double value = 0.0;
        try {
            value = Calculator.expr(this.magYProperty().get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return value;
    }

    public StringProperty magYProperty() {
        if (this.magY == null) {
            this.magY = new SimpleStringProperty(this, "magY");
        }

        return this.magY;
    }

    public void setMagZ(String value) {
        this.magZProperty().set(value);
    }

    public void setMagZ(double value) {
        this.magZProperty().set(String.format("%8.4f", value));
    }

    public String getMagZ() {
        return this.magZProperty().get();
    }

    public double getMagZValue() throws RuntimeException {
        double value = 0.0;
        try {
            value = Calculator.expr(this.magZProperty().get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return value;
    }

    public StringProperty magZProperty() {
        if (this.magZ == null) {
            this.magZ = new SimpleStringProperty(this, "magZ");
        }

        return this.magZ;
    }

    public void setHubbard(String value) {
        this.hubbardProperty().set(value);
    }

    public void setHubbard(double value) {
        this.hubbardProperty().set(String.format("%12.6f", value));
    }

    public String getHubbard() {
        return this.hubbardProperty().get();
    }

    public double getHubbardValue() throws RuntimeException {
        double value = 0.0;
        try {
            value = Calculator.expr(this.hubbardProperty().get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return value;
    }

    public StringProperty hubbardProperty() {
        if (this.hubbard == null) {
            this.hubbard = new SimpleStringProperty(this, "hubbard");
        }

        return this.hubbard;
    }

    @Override
    public String toString() {
        String str = "";
        str = str + this.getIndex() + " [";
        str = str + this.getName() + " ";
        str = str + this.getMagX() + " ";
        str = str + this.getMagY() + " ";
        str = str + this.getMagZ() + " ";
        str = str + this.getHubbard() + "]";
        return str;
    }

    @Override
    public int hashCode() {
        return this.getIndex();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }
}
