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

import java.util.ArrayList;
import java.util.List;

public class ProjectEnergies {

    private boolean converged;

    private List<Double> energies;

    public ProjectEnergies() {
        this.converged = false;
        this.energies = null;
    }

    public synchronized boolean isConverged() {
        return this.converged;
    }

    public synchronized void setConverged(boolean converged) {
        this.converged = converged;
    }

    public synchronized void clearEnergies() {
        this.converged = false;

        if (this.energies != null) {
            this.energies.clear();
        }
    }

    public synchronized int numEnergies() {
        return this.energies == null ? 0 : this.energies.size();
    }

    public synchronized double getEnergy(int i) throws IndexOutOfBoundsException {
        if (this.energies == null || i < 0 || i >= this.energies.size()) {
            throw new IndexOutOfBoundsException("incorrect index of energies: " + i + ".");
        }

        return this.energies.get(i);
    }

    public synchronized void removeEnergy(int i) throws IndexOutOfBoundsException {
        if (this.energies == null || i < 0 || i >= this.energies.size()) {
            throw new IndexOutOfBoundsException("incorrect index of energies: " + i + ".");
        }

        this.energies.remove(i);
    }

    public synchronized void addEnergy(double energy) {
        if (this.energies == null) {
            this.energies = new ArrayList<Double>();
        }

        this.energies.add(energy);
    }

    public synchronized ProjectEnergies copyEnergies() {
        ProjectEnergies other = new ProjectEnergies();

        other.converged = this.converged;

        if (this.energies == null) {
            other.energies = null;

        } else {
            other.energies = new ArrayList<Double>(this.energies);
        }

        return other;
    }
}
