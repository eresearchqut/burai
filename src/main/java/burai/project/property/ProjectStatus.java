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

package burai.project.property;

import java.util.Date;

public class ProjectStatus {

    private String date;

    private String cellAxis;
    private boolean molecule;

    private int scfCount;
    private int optCount;
    private int mdCount;
    private int dosCount;
    private int bandCount;

    public ProjectStatus() {
        this.updateDate();

        this.cellAxis = null;
        this.molecule = false;

        this.scfCount = 0;
        this.optCount = 0;
        this.mdCount = 0;
        this.dosCount = 0;
        this.bandCount = 0;
    }

    private void updateDate() {
        Date objDate = new Date();
        this.date = objDate.toString();
    }

    public synchronized String getDate() {
        return this.date;
    }

    public synchronized String getCellAxis() {
        return this.cellAxis;
    }

    public synchronized void setCellAxis(String cellAxis) {
        this.cellAxis = cellAxis;
    }

    public synchronized boolean isMolecule() {
        return this.molecule;
    }

    public synchronized void setMolecule(boolean molecule) {
        this.molecule = molecule;
    }

    public synchronized boolean isScfDone() {
        return this.scfCount > 0;
    }

    public synchronized int getScfCount() {
        return this.scfCount;
    }

    public synchronized void updateScfCount() {
        this.updateDate();
        this.scfCount++;
    }

    public synchronized boolean isOptDone() {
        return this.optCount > 0;
    }

    public synchronized int getOptCount() {
        return this.optCount;
    }

    public synchronized void updateOptDone() {
        this.updateDate();
        this.optCount++;
    }

    public synchronized boolean isMdDone() {
        return this.mdCount > 0;
    }

    public synchronized int getMdCount() {
        return this.mdCount;
    }

    public synchronized void updateMdCount() {
        this.updateDate();
        this.mdCount++;
    }

    public synchronized boolean isDosDone() {
        return this.dosCount > 0;
    }

    public synchronized int getDosCount() {
        return this.dosCount;
    }

    public synchronized void updateDosCount() {
        this.updateDate();
        this.dosCount++;
    }

    public synchronized boolean isBandDone() {
        return this.bandCount > 0;
    }

    public synchronized int getBandCount() {
        return this.bandCount;
    }

    public synchronized void updateBandDone() {
        this.updateDate();
        this.bandCount++;
    }
}
