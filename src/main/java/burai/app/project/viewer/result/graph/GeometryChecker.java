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

import java.util.HashSet;
import java.util.Set;

import burai.com.consts.Constants;
import burai.com.math.Lattice;
import burai.project.property.ProjectGeometry;
import burai.project.property.ProjectGeometryList;

public class GeometryChecker {

    private static final double DELTA_ANGSTROM = 1.0e-4;
    private static final double DELTA_DEGREE = 1.0e-2;
    private static final double DELTA_KBAR = 1.0e-3;

    private ProjectGeometryList projectGeometryList;

    protected GeometryChecker(ProjectGeometryList projectGeometryList) {
        if (projectGeometryList == null) {
            throw new IllegalArgumentException("projectGeometryList is null.");
        }

        this.projectGeometryList = projectGeometryList;
    }

    protected boolean isAvailableLattice(LatticeViewerType lattVType) {
        if (this.projectGeometryList.numGeometries() < 2) {
            return false;
        }

        if (lattVType == null) {
            return false;
        }

        Set<Integer> indexSet = new HashSet<Integer>();
        indexSet.add(1);
        indexSet.add(Math.max(1, this.projectGeometryList.numGeometries() - 1));

        for (Integer index : indexSet) {
            if (this.checkLattice(lattVType, index)) {
                return true;
            }
        }

        return false;
    }

    protected boolean isAvailableStress() {
        if (this.projectGeometryList.numGeometries() < 1) {
            return false;
        }

        Set<Integer> indexSet = new HashSet<Integer>();
        indexSet.add(0);
        indexSet.add(Math.max(0, this.projectGeometryList.numGeometries() - 2));
        indexSet.add(Math.max(0, this.projectGeometryList.numGeometries() - 1));

        for (Integer index : indexSet) {
            if (this.checkStress(index)) {
                return true;
            }
        }

        return false;
    }

    private boolean checkLattice(LatticeViewerType lattVType, int i) {
        try {
            ProjectGeometry projectGeometry1 = this.projectGeometryList.getGeometry(0);
            ProjectGeometry projectGeometry2 = this.projectGeometryList.getGeometry(i);
            if (projectGeometry1 == null || projectGeometry2 == null) {
                return false;
            }

            double[][] cell1 = projectGeometry1.getCell();
            double[][] cell2 = projectGeometry2.getCell();
            if (cell1 == null || cell2 == null) {
                return false;
            }

            if (LatticeViewerType.A.equals(lattVType)) {
                double a1 = Lattice.getA(cell1) * Constants.BOHR_RADIUS_ANGS;
                double a2 = Lattice.getA(cell2) * Constants.BOHR_RADIUS_ANGS;
                if (Math.abs(a1 - a2) < DELTA_ANGSTROM) {
                    return false;
                }

            } else if (LatticeViewerType.B.equals(lattVType)) {
                double b1 = Lattice.getB(cell1) * Constants.BOHR_RADIUS_ANGS;
                double b2 = Lattice.getB(cell2) * Constants.BOHR_RADIUS_ANGS;
                if (Math.abs(b1 - b2) < DELTA_ANGSTROM) {
                    return false;
                }

            } else if (LatticeViewerType.C.equals(lattVType)) {
                double c1 = Lattice.getC(cell1) * Constants.BOHR_RADIUS_ANGS;
                double c2 = Lattice.getC(cell2) * Constants.BOHR_RADIUS_ANGS;
                if (Math.abs(c1 - c2) < DELTA_ANGSTROM) {
                    return false;
                }

            } else if (LatticeViewerType.ANGLE.equals(lattVType)) {
                double alpha1 = Lattice.getAlpha(cell1);
                double alpha2 = Lattice.getAlpha(cell2);
                boolean fixAlpha = (Math.abs(alpha1 - alpha2) < DELTA_DEGREE);

                double beta1 = Lattice.getBeta(cell1);
                double beta2 = Lattice.getBeta(cell2);
                boolean fixBeta = (Math.abs(beta1 - beta2) < DELTA_DEGREE);

                double gamma1 = Lattice.getGamma(cell1);
                double gamma2 = Lattice.getGamma(cell2);
                boolean fixGamma = (Math.abs(gamma1 - gamma2) < DELTA_DEGREE);

                if (fixAlpha && fixBeta && fixGamma) {
                    return false;
                }

            } else {
                return false;
            }

        } catch (Exception e) {
            return false;
        }

        return true;
    }

    private boolean checkStress(int i) {
        try {
            ProjectGeometry projectGeometry = this.projectGeometryList.getGeometry(i);
            if (projectGeometry == null || (!projectGeometry.isConverged())) {
                return false;
            }

            double[][] stress = projectGeometry.getStress();
            if (stress == null) {
                return false;
            }

            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    if (Math.abs(stress[j][k] * Constants.RY_KBAR) > DELTA_KBAR) {
                        return true;
                    }
                }
            }

        } catch (Exception e) {
            return false;
        }

        return false;
    }
}
