/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.project.property;

import java.util.ArrayList;
import java.util.List;

public class ProjectGeometryList {

    private String cellAxis;

    private boolean molecule;

    private boolean converged;

    private List<ProjectGeometry> geometries;

    public ProjectGeometryList() {
        this.cellAxis = null;
        this.molecule = false;

        this.converged = false;
        this.geometries = null;
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

    public synchronized boolean isConverged() {
        return this.converged;
    }

    public synchronized void setConverged(boolean converged) {
        this.converged = converged;
    }

    public synchronized void clearGeometries() {
        this.converged = false;

        if (this.geometries != null) {
            this.geometries.clear();
        }
    }

    public synchronized int numGeometries() {
        return this.geometries == null ? 0 : this.geometries.size();
    }

    public synchronized ProjectGeometry getGeometry(int i) throws IndexOutOfBoundsException {
        if (this.geometries == null || i < 0 || i >= this.geometries.size()) {
            throw new IndexOutOfBoundsException("incorrect index of geometries: " + i + ".");
        }

        return this.geometries.get(i);
    }

    public synchronized void removeGeometry(int i) throws IndexOutOfBoundsException {
        if (this.geometries == null || i < 0 || i >= this.geometries.size()) {
            throw new IndexOutOfBoundsException("incorrect index of geometries: " + i + ".");
        }

        this.geometries.remove(i);
    }

    public synchronized void addGeometry(ProjectGeometry geometry) {
        if (geometry == null) {
            throw new IllegalArgumentException("geometry is null.");
        }

        if (this.geometries == null) {
            this.geometries = new ArrayList<ProjectGeometry>();
        }

        this.geometries.add(geometry);
    }

    public synchronized boolean hasAnyConvergedGeometries() {
        if (this.geometries == null || this.geometries.isEmpty()) {
            return false;
        }

        for (ProjectGeometry geometry : geometries) {
            if (geometry != null && geometry.isConverged()) {
                return true;
            }
        }

        return false;
    }

    public synchronized boolean hasAllConvergedGeometries() {
        if (this.geometries == null || this.geometries.isEmpty()) {
            return false;
        }

        for (ProjectGeometry geometry : geometries) {
            if (geometry == null || (!geometry.isConverged())) {
                return false;
            }
        }

        return true;
    }

    public synchronized ProjectGeometryList copyGeometryList() {
        ProjectGeometryList other = new ProjectGeometryList();

        other.cellAxis = this.cellAxis;
        other.molecule = this.molecule;

        other.converged = this.converged;

        if (this.geometries == null) {
            other.geometries = null;

        } else {
            other.geometries = new ArrayList<ProjectGeometry>(this.geometries);
        }

        return other;
    }
}
