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

package burai.app.project.viewer.result.movie;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import burai.app.project.QEFXProjectController;
import burai.app.project.viewer.result.QEFXResultViewerController;
import burai.atoms.model.Atom;
import burai.atoms.model.Cell;
import burai.atoms.model.exception.ZeroVolumCellException;
import burai.atoms.model.property.CellProperty;
import burai.com.consts.Constants;
import burai.com.math.Matrix3D;
import burai.project.property.ProjectGeometry;
import burai.project.property.ProjectGeometryList;
import burai.project.property.ProjectProperty;
import javafx.scene.Node;

public class QEFXMovieViewerController extends QEFXResultViewerController {

    private static final double RMIN = 1.0e-3;
    private static final double RRMIN = RMIN * RMIN;

    private Cell cell;

    private int currentIndex;

    private QEFXMovieBar movieBar;

    private GeometryShown onGeometryShown;

    private ProjectGeometryList projectGeometryList;

    private ProjectGeometryList projectGeometryListSaved;

    public QEFXMovieViewerController(QEFXProjectController projectController, ProjectProperty projectProperty,
            Cell cell, boolean mdMode) {

        super(projectController);

        if (projectProperty == null) {
            throw new IllegalArgumentException("projectProperty is null.");
        }

        if (cell == null) {
            throw new IllegalArgumentException("cell is null.");
        }

        if (mdMode) {
            this.projectGeometryList = projectProperty.getMdList();
        } else {
            this.projectGeometryList = projectProperty.getOptList();
        }

        this.projectGeometryListSaved = null;

        this.cell = cell;
        this.currentIndex = 0;
        this.onGeometryShown = null;

        try {
            this.movieBar = new QEFXMovieBar(this.projectController, this);
        } catch (IOException e) {
            this.movieBar = null;
            e.printStackTrace();
        }
    }

    public void setOnGeometryShown(GeometryShown onGeometryShown) {
        this.onGeometryShown = onGeometryShown;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // NOP
    }

    @Override
    public void reload() {
        if (this.projectController != null) {
            Node movieBarNode = this.movieBar == null ? null : this.movieBar.getNode();
            if (movieBarNode != null) {
                this.projectController.clearStackedsOnViewerPane();
                this.projectController.stackOnViewerPane(movieBarNode);
            }
        }

        this.showCurrentGeometry();
    }

    public int numGeometries() {
        if (this.projectGeometryList == null) {
            return 0;
        }

        return this.projectGeometryList.numGeometries();
    }

    public ProjectGeometry getGeometry() {
        if (this.projectGeometryList == null) {
            return null;
        }

        ProjectGeometry projectGeometry = null;

        try {
            projectGeometry = this.projectGeometryList.getGeometry(this.currentIndex);
        } catch (Exception e) {
            projectGeometry = null;
        }

        return projectGeometry;
    }

    protected void freezeGeometries() {
        if (this.projectGeometryListSaved != null) {
            return;
        }

        this.projectGeometryListSaved = this.projectGeometryList;
        if (this.projectGeometryList != null) {
            this.projectGeometryList = this.projectGeometryList.copyGeometryList();
        }
    }

    protected void thawGeometries() {
        if (this.projectGeometryListSaved == null) {
            return;
        }

        this.projectGeometryList = this.projectGeometryListSaved;
        this.projectGeometryListSaved = null;
    }

    public boolean showCurrentGeometry() {
        return this.showGeometry(-1);
    }

    public boolean showNextGeometry() {
        return this.showGeometry(this.currentIndex + 1);
    }

    public boolean showPreviousGeometry() {
        return this.showGeometry(this.currentIndex - 1);
    }

    public boolean showFirstGeometry() {
        return this.showGeometry(0);
    }

    public boolean showLastGeometry() {
        if (this.projectGeometryList == null) {
            return false;
        }

        return this.showGeometry(this.projectGeometryList.numGeometries() - 1);
    }

    public boolean showGeometry(double rate) {
        if (this.projectGeometryList == null) {
            return false;
        }

        int numGeoms = this.projectGeometryList.numGeometries();
        if (numGeoms < 1) {
            return false;
        }

        if (numGeoms == 1) {
            return this.showGeometry(0);
        }

        double value = rate * ((double) (numGeoms - 1));
        int index = (int) (Math.rint(value) + 0.1);
        return this.showGeometry(index);
    }

    public boolean showGeometry(int index) {
        if (this.projectGeometryList == null) {
            return false;
        }

        if (index == this.currentIndex) {
            return true; // already shown
        }

        int index_ = index;
        if (index_ < 0) {
            index_ = this.currentIndex;
        }

        if (index_ < 0 || this.projectGeometryList.numGeometries() <= index_) {
            return false;
        }

        ProjectGeometry projectGeometry = null;

        try {
            projectGeometry = this.projectGeometryList.getGeometry(index_);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return false;
        }

        double[][] lattice = projectGeometry.getCell();
        lattice = Matrix3D.mult(Constants.BOHR_RADIUS_ANGS, lattice);
        if (lattice == null || lattice.length < 3) {
            return false;
        }
        if (lattice[0] == null || lattice[0].length < 3) {
            return false;
        }
        if (lattice[1] == null || lattice[1].length < 3) {
            return false;
        }
        if (lattice[2] == null || lattice[2].length < 3) {
            return false;
        }

        //this.cell.stopResolving();

        try {
            this.cell.moveLattice(lattice);
        } catch (ZeroVolumCellException e) {
            e.printStackTrace();
            //this.cell.restartResolving();
            return false;
        }

        String axis = this.projectGeometryList.getCellAxis();
        if (axis != null) {
            this.cell.setProperty(CellProperty.AXIS, axis);
        } else {
            this.cell.removeProperty(CellProperty.AXIS);
        }

        boolean molecule = this.projectGeometryList.isMolecule();
        this.cell.setProperty(CellProperty.MOLECULE, molecule);

        int natom = projectGeometry.numAtoms();
        int natom2 = this.cell.numAtoms(true);

        Atom[] refAtoms = null;
        if (natom == natom2) {
            refAtoms = this.cell.listAtoms(true);
        }

        if (refAtoms != null && refAtoms.length >= natom) {
            for (int i = 0; i < natom; i++) {
                String name = projectGeometry.getName(i);
                if (name == null || name.trim().isEmpty()) {
                    continue;
                }

                double x = projectGeometry.getX(i) * Constants.BOHR_RADIUS_ANGS;
                double y = projectGeometry.getY(i) * Constants.BOHR_RADIUS_ANGS;
                double z = projectGeometry.getZ(i) * Constants.BOHR_RADIUS_ANGS;

                Atom atom = refAtoms[i];
                if (atom == null) {
                    this.cell.addAtom(new Atom(name, x, y, z));

                } else {
                    String name2 = atom.getName();
                    if (!name.equals(name2)) {
                        atom.setName(name);
                    }

                    double x2 = atom.getX();
                    double y2 = atom.getY();
                    double z2 = atom.getZ();
                    double dx = x - x2;
                    double dy = y - y2;
                    double dz = z - z2;
                    double rr = dx * dx + dy * dy + dz * dz;
                    if (rr > RRMIN) {
                        atom.moveTo(x, y, z);
                    }
                }
            }

        } else {
            this.cell.removeAllAtoms();

            for (int i = 0; i < natom; i++) {
                String name = projectGeometry.getName(i);
                if (name == null || name.trim().isEmpty()) {
                    continue;
                }

                double x = projectGeometry.getX(i) * Constants.BOHR_RADIUS_ANGS;
                double y = projectGeometry.getY(i) * Constants.BOHR_RADIUS_ANGS;
                double z = projectGeometry.getZ(i) * Constants.BOHR_RADIUS_ANGS;

                this.cell.addAtom(new Atom(name, x, y, z));
            }
        }

        //this.cell.restartResolving();

        this.currentIndex = index_;
        this.postShowGeometry(projectGeometry);

        return true;
    }

    private void postShowGeometry(ProjectGeometry projectGeometry) {
        int numGeoms = 0;
        if (this.projectGeometryList != null) {
            numGeoms = this.projectGeometryList.numGeometries();
        }

        QEFXMovieBarController movieBarController = this.movieBar == null ? null : this.movieBar.getController();
        if (movieBarController != null && numGeoms > 0) {
            movieBarController.disablePreviousButtons(this.currentIndex <= 0);
            movieBarController.disableNextButtons(this.currentIndex >= (numGeoms - 1));

            double rate = 0.5;
            if (numGeoms > 1) {
                rate = ((double) this.currentIndex) / ((double) (numGeoms - 1));
            }

            movieBarController.setSliderValue(rate);
        }

        if (this.onGeometryShown != null) {
            this.onGeometryShown.onGeometryShown(this.currentIndex, numGeoms, projectGeometry);
        }
    }
}
