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

package burai.atoms.model.event;

public class AtomEvent extends ModelEvent {

    private String name;
    private String oldName;
    private double x;
    private double y;
    private double z;
    private double deltaX;
    private double deltaY;
    private double deltaZ;

    public AtomEvent(Object source) {
        super(source);
        this.name = null;
        this.oldName = null;
        this.x = 0.0;
        this.y = 0.0;
        this.z = 0.0;
        this.deltaX = 0.0;
        this.deltaY = 0.0;
        this.deltaZ = 0.0;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setOldName(String oldName) {
        this.oldName = oldName;
    }

    public String getOldName() {
        return this.oldName;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getX() {
        return this.x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getY() {
        return this.y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getZ() {
        return this.z;
    }

    public void setDeltaX(double deltaX) {
        this.deltaX = deltaX;
    }

    public double getDeltaX() {
        return this.deltaX;
    }

    public void setDeltaY(double deltaY) {
        this.deltaY = deltaY;
    }

    public double getDeltaY() {
        return this.deltaY;
    }

    public void setDeltaZ(double deltaZ) {
        this.deltaZ = deltaZ;
    }

    public double getDeltaZ() {
        return this.deltaZ;
    }
}
