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

package burai.com.math;

import burai.com.consts.Constants;

public final class Lattice {

    private static final double ROOT2 = Math.sqrt(2.0);
    private static final double ROOT3 = Math.sqrt(3.0);

    private static final double CELL_THRESHOLD = 1.0e-6;

    //private static final int[] IBRAV_LIST = { 1, 2, 3, -3, 4, 5, -5, 6, 7, 8, 9, -9, 10, 11, 12, -12, 13, 14 };
    private static final int[] IBRAV_LIST = { 1, 2, 3, 4, 5, -5, 6, 7, 8, 9, -9, 10, 11, 12, -12, 13, 14 };

    private Lattice() {
        // NOP
    }

    private static boolean checkCell(double[][] cell) {
        if (cell == null || cell.length < 3) {
            return false;
        }

        if (cell[0] == null || cell[0].length < 3) {
            return false;
        }

        if (cell[1] == null || cell[1].length < 3) {
            return false;
        }

        if (cell[2] == null || cell[2].length < 3) {
            return false;
        }

        return true;
    }

    public static double getA(double[][] cell) {
        if (!checkCell(cell)) {
            return -1.0;
        }

        return Matrix3D.norm(cell[0]);
    }

    public static double getB(double[][] cell) {
        if (!checkCell(cell)) {
            return -1.0;
        }

        return Matrix3D.norm(cell[1]);
    }

    public static double getC(double[][] cell) {
        if (!checkCell(cell)) {
            return -1.0;
        }

        return Matrix3D.norm(cell[2]);
    }

    public static double getCosAlpha(double[][] cell) {
        if (!checkCell(cell)) {
            return 1.0;
        }

        double b = Matrix3D.norm(cell[1]);
        if (b <= 0.0) {
            return 1.0;
        }

        double c = Matrix3D.norm(cell[2]);
        if (c <= 0.0) {
            return 1.0;
        }

        return Matrix3D.mult(cell[1], cell[2]) / b / c;
    }

    public static double getAlpha(double[][] cell) {
        double cosbc = getCosAlpha(cell);
        return Math.acos(Math.max(-1.0, Math.min(cosbc, 1.0))) * 180.0 / Math.PI;
    }

    public static double getCosBeta(double[][] cell) {
        if (!checkCell(cell)) {
            return 1.0;
        }

        double a = Matrix3D.norm(cell[0]);
        if (a <= 0.0) {
            return 1.0;
        }

        double c = Matrix3D.norm(cell[2]);
        if (c <= 0.0) {
            return 1.0;
        }

        return Matrix3D.mult(cell[0], cell[2]) / a / c;
    }

    public static double getBeta(double[][] cell) {
        double cosac = getCosBeta(cell);
        return Math.acos(Math.max(-1.0, Math.min(cosac, 1.0))) * 180.0 / Math.PI;
    }

    public static double getCosGamma(double[][] cell) {
        if (!checkCell(cell)) {
            return 1.0;
        }

        double a = Matrix3D.norm(cell[0]);
        if (a <= 0.0) {
            return 1.0;
        }

        double b = Matrix3D.norm(cell[1]);
        if (b <= 0.0) {
            return 1.0;
        }

        return Matrix3D.mult(cell[0], cell[1]) / a / b;
    }

    public static double getGamma(double[][] cell) {
        double cosab = getCosGamma(cell);
        return Math.acos(Math.max(-1.0, Math.min(cosab, 1.0))) * 180.0 / Math.PI;
    }

    public static double getXMax(double[][] cell) {
        if (!checkCell(cell)) {
            return 0.0;
        }

        double x = 0.0;

        if (cell[0][0] > 0.0) {
            x += cell[0][0];
        }

        if (cell[1][0] > 0.0) {
            x += cell[1][0];
        }

        if (cell[2][0] > 0.0) {
            x += cell[2][0];
        }

        return x;
    }

    public static double getXMin(double[][] cell) {
        if (!checkCell(cell)) {
            return 0.0;
        }

        double x = 0.0;

        if (cell[0][0] < 0.0) {
            x += cell[0][0];
        }

        if (cell[1][0] < 0.0) {
            x += cell[1][0];
        }

        if (cell[2][0] < 0.0) {
            x += cell[2][0];
        }

        return x;
    }

    public static double getYMax(double[][] cell) {
        if (!checkCell(cell)) {
            return 0.0;
        }

        double y = 0.0;

        if (cell[0][1] > 0.0) {
            y += cell[0][1];
        }

        if (cell[1][1] > 0.0) {
            y += cell[1][1];
        }

        if (cell[2][1] > 0.0) {
            y += cell[2][1];
        }

        return y;
    }

    public static double getYMin(double[][] cell) {
        if (!checkCell(cell)) {
            return 0.0;
        }

        double y = 0.0;

        if (cell[0][1] < 0.0) {
            y += cell[0][1];
        }

        if (cell[1][1] < 0.0) {
            y += cell[1][1];
        }

        if (cell[2][1] < 0.0) {
            y += cell[2][1];
        }

        return y;
    }

    public static double getZMax(double[][] cell) {
        if (!checkCell(cell)) {
            return 0.0;
        }

        double z = 0.0;

        if (cell[0][2] > 0.0) {
            z += cell[0][2];
        }

        if (cell[1][2] > 0.0) {
            z += cell[1][2];
        }

        if (cell[2][2] > 0.0) {
            z += cell[2][2];
        }

        return z;
    }

    public static double getZMin(double[][] cell) {
        if (!checkCell(cell)) {
            return 0.0;
        }

        double z = 0.0;

        if (cell[0][2] < 0.0) {
            z += cell[0][2];
        }

        if (cell[1][2] < 0.0) {
            z += cell[1][2];
        }

        if (cell[2][2] < 0.0) {
            z += cell[2][2];
        }

        return z;
    }

    /**
     * celldm of primitive cell
     */
    public static double[] getCellDm(double[][] cell) {
        return getCellDm(0, cell);
    }

    /**
     * celldm of primitive cell (ibrav = 0) or standard cell (ibrav > 0)
     */
    public static double[] getCellDm(int ibrav, double[][] cell) {
        if (!checkCell(cell)) {
            return null;
        }

        double[] celldm = new double[6];
        double a = getA(cell);
        double b = getB(cell);
        double c = getC(cell);
        double cosAlpha = getCosAlpha(cell);
        double cosBeta = getCosBeta(cell);
        double cosGamma = getCosGamma(cell);
        celldm[0] = a / Constants.BOHR_RADIUS_ANGS;
        celldm[1] = b / a;
        celldm[2] = c / a;
        celldm[3] = cosAlpha;
        celldm[4] = cosBeta;
        celldm[5] = cosGamma;

        if (isCorrectBravais(ibrav)) {
            return convertCellDm(ibrav, celldm);
        } else {
            return celldm;
        }
    }

    /**
     * a, b, c, alpha, beta, gamma of primitive cell
     */
    public static double[] getLatticeConstants(double[][] cell, boolean asCos) {
        return getLatticeConstants(0, cell, asCos);
    }

    /**
     * a, b, c, alpha, beta, gamma of primitive cell (ibrav = 0) or standard cell (ibrav > 0)
     */
    public static double[] getLatticeConstants(int ibrav, double[][] cell, boolean asCos) {
        if (!checkCell(cell)) {
            return null;
        }

        double[] celldm = getCellDm(ibrav, cell);
        if (celldm == null || celldm.length < 6) {
            return null;
        }

        double a = celldm[0] * Constants.BOHR_RADIUS_ANGS;
        double b = a * celldm[1];
        double c = a * celldm[2];

        double cosAlpha = 0.0;
        double cosBeta = 0.0;
        double cosGamma = 0.0;
        if (ibrav == 14) {
            cosAlpha = celldm[3];
            cosBeta = celldm[4];
            cosGamma = celldm[5];
        } else if (ibrav == -12 || ibrav == -13) {
            cosAlpha = 0.0;
            cosBeta = celldm[4];
            cosGamma = 0.0;
        } else if (isCorrectBravais(ibrav)) {
            cosAlpha = 0.0;
            cosBeta = 0.0;
            cosGamma = celldm[3];
        } else {
            cosAlpha = celldm[3];
            cosBeta = celldm[4];
            cosGamma = celldm[5];
        }

        if (asCos) {
            return new double[] { a, b, c, cosAlpha, cosBeta, cosGamma };
        }

        double alpha = Math.acos(Math.max(-1.0, Math.min(cosAlpha, 1.0))) * 180.0 / Math.PI;
        double beta = Math.acos(Math.max(-1.0, Math.min(cosBeta, 1.0))) * 180.0 / Math.PI;
        double gamma = Math.acos(Math.max(-1.0, Math.min(cosGamma, 1.0))) * 180.0 / Math.PI;

        return new double[] { a, b, c, alpha, beta, gamma };
    }

    /**
     * check available value of ibrav
     */
    public static boolean isCorrectBravais(int ibrav) {
        for (int ibrav2 : IBRAV_LIST) {
            if (ibrav == ibrav2) {
                return true;
            }
        }

        return false;
    }

    /**
     * detect ibrav from lattice vectors
     */
    public static int getBravais(double[][] cell) {
        if (!checkCell(cell)) {
            return 0;
        }

        // lattice vector -> primitive celldm
        double[] celldmPrim = getCellDm(cell);
        if (celldmPrim == null || celldmPrim.length < 6) {
            return 0;
        }

        for (int ibrav : IBRAV_LIST) {
            // primitive celldm -> standard celldm
            double[] celldmStd = convertCellDm(ibrav, celldmPrim);
            if (celldmStd == null || celldmStd.length < 6) {
                continue;
            }

            // standard celldm -> lattice vectors
            double[][] cell_ = getCell(ibrav, celldmStd);
            if (cell_ != null && Matrix3D.equals(cell, cell_, CELL_THRESHOLD)) {
                return ibrav;
            }
        }

        return 0;
    }

    /**
     * detect lattice vectors from primitive celldm
     */
    private static double[][] getCell(double[] celldmPrim) {
        // primitive celldm
        if (celldmPrim == null || celldmPrim.length < 6) {
            return null;
        }

        for (int ibrav : IBRAV_LIST) {
            // primitive celldm -> standard celldm
            double[] celldmStd = convertCellDm(ibrav, celldmPrim);
            if (celldmStd == null || celldmStd.length < 6) {
                continue;
            }

            // standard celldm -> lattice vectors
            double[][] cell = getCell(ibrav, celldmStd);
            if (cell == null) {
                continue;
            }

            // lattice vectors -> primitive celldm
            double[] celldmPrim_ = getCellDm(cell);
            if (celldmPrim_ == null || celldmPrim_.length < 6) {
                continue;
            }

            boolean sameCell = true;
            for (int i = 0; i < 6; i++) {
                if (Math.abs(celldmPrim[i] - celldmPrim_[i]) > CELL_THRESHOLD) {
                    sameCell = false;
                    break;
                }
            }

            if (sameCell) {
                return cell;
            }
        }

        return null;
    }

    /**
     * detect lattice vectors from primitive a, b, c, alpha, beta, gamma
     */
    public static double[][] getCell(double a, double b, double c, double alpha, double beta, double gamma) {
        if (a <= 0.0) {
            return null;
        }
        if (b <= 0.0) {
            return null;
        }
        if (c <= 0.0) {
            return null;
        }
        if (alpha <= 0.0 || 180.0 <= alpha) {
            return null;
        }
        if (beta <= 0.0 || 180.0 <= beta) {
            return null;
        }
        if (gamma <= 0.0 || 180.0 <= gamma) {
            return null;
        }

        // primitive celldm
        double[] celldm = new double[6];
        double cosAlpha = Math.cos(alpha * Math.PI / 180.0);
        double cosBeta = Math.cos(beta * Math.PI / 180.0);
        double cosGamma = Math.cos(gamma * Math.PI / 180.0);
        celldm[0] = a / Constants.BOHR_RADIUS_ANGS;
        celldm[1] = b / a;
        celldm[2] = c / a;
        celldm[3] = cosAlpha;
        celldm[4] = cosBeta;
        celldm[5] = cosGamma;

        // primitive celldm -> lattice vectors
        return getCell(celldm);
    }

    /**
     * convert celldm: primitive cell -> standard cell
     */
    private static double[] convertCellDm(int ibrav, double[] celldmPrim) {
        if (celldmPrim == null || celldmPrim.length < 6) {
            return null;
        }

        double[] celldmStd = new double[6];
        for (int i = 0; i < 6; i++) {
            celldmStd[i] = 0.0;
        }

        switch (ibrav) {
        case 1:
            celldmStd[0] = celldmPrim[0];
            break;

        case 2:
            celldmStd[0] = (2.0 / ROOT2) * celldmPrim[0];
            break;

        case 3:
            celldmStd[0] = (2.0 / ROOT3) * celldmPrim[0];
            break;

        case -3:
            celldmStd[0] = (2.0 / ROOT3) * celldmPrim[0];
            break;

        case 4:
            celldmStd[0] = celldmPrim[0];
            celldmStd[2] = celldmPrim[2];
            break;

        case 5:
            celldmStd[0] = celldmPrim[0];
            celldmStd[3] = celldmPrim[5];
            break;

        case -5:
            celldmStd[0] = celldmPrim[0];
            celldmStd[3] = celldmPrim[5];
            break;

        case 6:
            celldmStd[0] = celldmPrim[0];
            celldmStd[2] = celldmPrim[2];
            break;

        case 7:
            if (celldmPrim[0] <= 0.0) {
                celldmStd = null;
                break;
            }
            if (celldmPrim[5] <= 0.0 || 1.0 <= celldmPrim[5]) {
                celldmStd = null;
                break;
            }
            celldmStd[0] = ROOT2 * celldmPrim[0] * Math.sqrt(1.0 - celldmPrim[5]);
            celldmStd[2] = 2.0 * celldmPrim[0] * Math.sqrt(celldmPrim[5]) / celldmStd[0];
            break;

        case 8:
            celldmStd[0] = celldmPrim[0];
            celldmStd[1] = celldmPrim[1];
            celldmStd[2] = celldmPrim[2];
            break;

        case 9:
            if (celldmPrim[0] <= 0.0) {
                celldmStd = null;
                break;
            }
            if (Math.abs(celldmPrim[5]) >= 1.0) {
                celldmStd = null;
                break;
            }
            celldmStd[0] = ROOT2 * celldmPrim[0] * Math.sqrt(1 - celldmPrim[5]);
            celldmStd[1] = Math.sqrt(4.0 * celldmPrim[0] * celldmPrim[0] - celldmStd[0] * celldmStd[0]) / celldmStd[0];
            celldmStd[2] = celldmPrim[2] * celldmPrim[0] / celldmStd[0];
            break;

        case -9:
            if (celldmPrim[0] <= 0.0) {
                celldmStd = null;
                break;
            }
            if (Math.abs(celldmPrim[5]) >= 1.0) {
                celldmStd = null;
                break;
            }
            celldmStd[0] = ROOT2 * celldmPrim[0] * Math.sqrt(1 + celldmPrim[5]);
            celldmStd[1] = Math.sqrt(4.0 * celldmPrim[0] * celldmPrim[0] - celldmStd[0] * celldmStd[0]) / celldmStd[0];
            celldmStd[2] = celldmPrim[2] * celldmPrim[0] / celldmStd[0];
            break;

        case 91:
            if (Math.abs(celldmPrim[3]) >= 1.0) {
                celldmStd = null;
                break;
            }
            celldmStd[0] = celldmPrim[0];
            celldmStd[1] = ROOT2 * celldmPrim[1] * Math.sqrt(1 + celldmPrim[3]);
            celldmStd[2] = Math.sqrt(4.0 * celldmPrim[1] * celldmPrim[1] - celldmStd[1] * celldmStd[1]);
            break;

        case 10:
            double ap = celldmPrim[0];
            double bp = celldmPrim[1] * ap;
            double cp = celldmPrim[2] * ap;
            double asSqr = ap * ap + bp * bp - cp * cp;
            double bsSqr = bp * bp + cp * cp - ap * ap;
            double csSqr = ap * ap + cp * cp - bp * bp;
            if (asSqr <= 0.0 || bsSqr <= 0.0 || csSqr <= 0.0) {
                celldmStd = null;
                break;
            }

            double as = ROOT2 * Math.sqrt(asSqr);
            double bs = ROOT2 * Math.sqrt(bsSqr);
            double cs = ROOT2 * Math.sqrt(csSqr);
            celldmStd[0] = as;
            celldmStd[1] = bs / as;
            celldmStd[2] = cs / as;
            break;

        case 11:
            double rr = 4.0 * celldmPrim[0] * celldmPrim[0];
            double x1 = rr * celldmPrim[3];
            double x2 = rr * celldmPrim[4];
            double x3 = rr * celldmPrim[5];
            double aa = 0.5 * (x1 - x2);
            double bb = 0.5 * (x2 - x3);
            double cc = 0.5 * (x3 + x1);
            if (aa <= 0.0 || bb <= 0.0 || cc <= 0.0) {
                celldmStd = null;
                break;
            }

            double a = Math.sqrt(aa);
            double b = Math.sqrt(bb);
            double c = Math.sqrt(cc);
            celldmStd[0] = a;
            celldmStd[1] = b / a;
            celldmStd[2] = c / a;
            break;

        case 12:
            celldmStd[0] = celldmPrim[0];
            celldmStd[1] = celldmPrim[1];
            celldmStd[2] = celldmPrim[2];
            celldmStd[3] = celldmPrim[5];
            break;

        case -12:
            celldmStd[0] = celldmPrim[0];
            celldmStd[1] = celldmPrim[1];
            celldmStd[2] = celldmPrim[2];
            celldmStd[4] = celldmPrim[4];
            break;

        case 13:
            if (celldmPrim[0] <= 0.0) {
                celldmStd = null;
                break;
            }
            if (Math.abs(celldmPrim[4]) >= 1.0) {
                celldmStd = null;
                break;
            }
            celldmStd[0] = ROOT2 * celldmPrim[0] * Math.sqrt(1 + celldmPrim[4]);
            celldmStd[1] = celldmPrim[1] * celldmPrim[0] / celldmStd[0];
            celldmStd[2] = Math.sqrt(4.0 * celldmPrim[0] * celldmPrim[0] - celldmStd[0] * celldmStd[0]) / celldmStd[0];
            celldmStd[3] = celldmPrim[5] * Math.sqrt(1.0 + celldmStd[2] * celldmStd[2]);
            break;

        case -13:
            if (celldmPrim[0] <= 0.0) {
                celldmStd = null;
                break;
            }
            if (Math.abs(celldmPrim[5]) >= 1.0) {
                celldmStd = null;
                break;
            }
            celldmStd[0] = ROOT2 * celldmPrim[0] * Math.sqrt(1 + celldmPrim[5]);
            celldmStd[1] = Math.sqrt(4.0 * celldmPrim[0] * celldmPrim[0] - celldmStd[0] * celldmStd[0]) / celldmStd[0];
            celldmStd[2] = celldmPrim[2] * celldmPrim[0] / celldmStd[0];
            celldmStd[4] = celldmPrim[4] * Math.sqrt(1.0 + celldmStd[1] * celldmStd[1]);
            break;

        case 14:
            celldmStd[0] = celldmPrim[0];
            celldmStd[1] = celldmPrim[1];
            celldmStd[2] = celldmPrim[2];
            celldmStd[3] = celldmPrim[3];
            celldmStd[4] = celldmPrim[4];
            celldmStd[5] = celldmPrim[5];
            break;

        default:
            celldmStd[0] = celldmPrim[0];
            celldmStd[1] = celldmPrim[1];
            celldmStd[2] = celldmPrim[2];
            celldmStd[3] = celldmPrim[3];
            celldmStd[4] = celldmPrim[4];
            celldmStd[5] = celldmPrim[5];
            break;
        }

        return celldmStd;
    }

    /**
     * create lattice vectors from standard celldm
     */
    public static double[][] getCell(int ibrav, double[] celldm) {
        if (celldm == null || celldm.length < 6) {
            return null;
        }

        if (celldm[0] == 0.0) {
            return null;
        }

        double[][] lattice = Matrix3D.zero();

        double term1;
        double term2;

        switch (ibrav) {
        case 1:
            lattice[0][0] = celldm[0];
            lattice[1][1] = celldm[0];
            lattice[2][2] = celldm[0];
            break;

        case 2:
            term1 = 0.5 * celldm[0];
            lattice[0][0] = -term1;
            lattice[0][2] = term1;
            lattice[1][1] = term1;
            lattice[1][2] = term1;
            lattice[2][0] = -term1;
            lattice[2][1] = term1;
            break;

        case 3:
            term1 = 0.5 * celldm[0];
            for (int i = 0; i < 3; i++) {
                lattice[0][i] = term1;
                lattice[1][i] = term1;
                lattice[2][i] = term1;
            }
            lattice[1][0] *= -1.0;
            lattice[2][0] *= -1.0;
            lattice[2][1] *= -1.0;
            break;

        case -3:
            term1 = 0.5 * celldm[0];
            for (int i = 0; i < 3; i++) {
                lattice[0][i] = term1;
                lattice[1][i] = term1;
                lattice[2][i] = term1;
            }
            lattice[0][0] *= -1.0;
            lattice[1][1] *= -1.0;
            lattice[2][2] *= -1.0;
            break;

        case 4:
            if (celldm[2] <= 0.0) {
                lattice = null;
                break;
            }
            lattice[0][0] = celldm[0];
            lattice[1][0] = -celldm[0] / 2.0;
            lattice[1][1] = celldm[0] * Math.sqrt(3.0) / 2.0;
            lattice[2][2] = celldm[0] * celldm[2];
            break;

        case 5:
            if (celldm[3] <= -0.5 || celldm[3] >= 1.0) {
                lattice = null;
                break;
            }
            term1 = Math.sqrt(1.0 + 2.0 * celldm[3]);
            term2 = Math.sqrt(1.0 - celldm[3]);
            lattice[1][1] = ROOT2 * celldm[0] * term2 / ROOT3;
            lattice[1][2] = celldm[0] * term1 / ROOT3;
            lattice[0][0] = celldm[0] * term2 / ROOT2;
            lattice[0][1] = -lattice[0][0] / ROOT3;
            lattice[0][2] = lattice[1][2];
            lattice[2][0] = -lattice[0][0];
            lattice[2][1] = lattice[0][1];
            lattice[2][2] = lattice[1][2];
            break;

        case -5:
            if (celldm[3] <= -0.5 || celldm[3] >= 1.0) {
                lattice = null;
                break;
            }
            term1 = Math.sqrt(1.0 + 2.0 * celldm[3]);
            term2 = Math.sqrt(1.0 - celldm[3]);
            lattice[0][0] = celldm[0] * (term1 - 2.0 * term2) / 3.0;
            lattice[0][1] = celldm[0] * (term1 + term2) / 3.0;
            lattice[0][2] = lattice[0][1];
            lattice[1][0] = lattice[0][2];
            lattice[1][1] = lattice[0][0];
            lattice[1][2] = lattice[0][1];
            lattice[2][0] = lattice[0][1];
            lattice[2][1] = lattice[0][2];
            lattice[2][2] = lattice[0][0];
            break;

        case 6:
            if (celldm[2] <= 0.0) {
                lattice = null;
                break;
            }
            lattice[0][0] = celldm[0];
            lattice[1][1] = celldm[0];
            lattice[2][2] = celldm[0] * celldm[2];
            break;

        case 7:
            if (celldm[2] <= 0.0) {
                lattice = null;
                break;
            }
            lattice[1][0] = celldm[0] / 2.0;
            lattice[1][1] = lattice[1][0];
            lattice[1][2] = celldm[2] * celldm[0] / 2.0;
            lattice[0][0] = lattice[1][0];
            lattice[0][1] = -lattice[1][0];
            lattice[0][2] = lattice[1][2];
            lattice[2][0] = -lattice[1][0];
            lattice[2][1] = -lattice[1][0];
            lattice[2][2] = lattice[1][2];
            break;

        case 8:
            if (celldm[1] <= 0.0) {
                lattice = null;
                break;
            }
            if (celldm[2] <= 0.0) {
                lattice = null;
                break;
            }
            lattice[0][0] = celldm[0];
            lattice[1][1] = celldm[0] * celldm[1];
            lattice[2][2] = celldm[0] * celldm[2];
            break;

        case 9:
            if (celldm[1] <= 0.0) {
                lattice = null;
                break;
            }
            if (celldm[2] <= 0.0) {
                lattice = null;
                break;
            }
            lattice[0][0] = 0.5 * celldm[0];
            lattice[0][1] = lattice[0][0] * celldm[1];
            lattice[1][0] = -lattice[0][0];
            lattice[1][1] = lattice[0][1];
            lattice[2][2] = celldm[0] * celldm[2];
            break;

        case -9:
            if (celldm[1] <= 0.0) {
                lattice = null;
                break;
            }
            if (celldm[2] <= 0.0) {
                lattice = null;
                break;
            }
            lattice[0][0] = 0.5 * celldm[0];
            lattice[0][1] = -lattice[0][0] * celldm[1];
            lattice[1][0] = lattice[0][0];
            lattice[1][1] = -lattice[0][1];
            lattice[2][2] = celldm[0] * celldm[2];
            break;

        case 91:
            if (celldm[1] <= 0.0) {
                lattice = null;
                break;
            }
            if (celldm[2] <= 0.0) {
                lattice = null;
                break;
            }
            lattice[0][0] = celldm[0];
            lattice[1][1] = celldm[0] * celldm[1] * 0.5;
            lattice[1][2] = -celldm[0] * celldm[2] * 0.5;
            lattice[2][1] = lattice[1][1];
            lattice[2][2] = -lattice[1][2];
            break;

        case 10:
            if (celldm[1] <= 0.0) {
                lattice = null;
                break;
            }
            if (celldm[2] <= 0.0) {
                lattice = null;
                break;
            }
            lattice[1][0] = 0.5 * celldm[0];
            lattice[1][1] = lattice[1][0] * celldm[1];
            lattice[0][0] = lattice[1][0];
            lattice[0][2] = lattice[1][0] * celldm[2];
            lattice[2][1] = lattice[1][0] * celldm[1];
            lattice[2][2] = lattice[0][2];
            break;

        case 11:
            if (celldm[1] <= 0.0) {
                lattice = null;
                break;
            }
            if (celldm[2] <= 0.0) {
                lattice = null;
                break;
            }
            lattice[0][0] = 0.5 * celldm[0];
            lattice[0][1] = lattice[0][0] * celldm[1];
            lattice[0][2] = lattice[0][0] * celldm[2];
            lattice[1][0] = -lattice[0][0];
            lattice[1][1] = lattice[0][1];
            lattice[1][2] = lattice[0][2];
            lattice[2][0] = -lattice[0][0];
            lattice[2][1] = -lattice[0][1];
            lattice[2][2] = lattice[0][2];
            break;

        case 12:
            if (celldm[1] <= 0.0) {
                lattice = null;
                break;
            }
            if (celldm[2] <= 0.0) {
                lattice = null;
                break;
            }
            if (Math.abs(celldm[3]) >= 1.0) {
                lattice = null;
                break;
            }
            lattice[0][0] = celldm[0];
            lattice[1][0] = celldm[0] * celldm[1] * celldm[3];
            lattice[1][1] = celldm[0] * celldm[1] * Math.sqrt(1.0 - celldm[3] * celldm[3]);
            lattice[2][2] = celldm[0] * celldm[2];
            break;

        case -12:
            if (celldm[1] <= 0.0) {
                lattice = null;
                break;
            }
            if (celldm[2] <= 0.0) {
                lattice = null;
                break;
            }
            if (Math.abs(celldm[4]) >= 1.0) {
                lattice = null;
                break;
            }
            lattice[0][0] = celldm[0];
            lattice[1][1] = celldm[0] * celldm[1];
            lattice[2][0] = celldm[0] * celldm[2] * celldm[4];
            lattice[2][2] = celldm[0] * celldm[2] * Math.sqrt(1.0 - celldm[4] * celldm[4]);
            break;

        case 13:
            if (celldm[1] <= 0.0) {
                lattice = null;
                break;
            }
            if (celldm[2] <= 0.0) {
                lattice = null;
                break;
            }
            if (Math.abs(celldm[3]) >= 1.0) {
                lattice = null;
                break;
            }
            lattice[0][0] = 0.5 * celldm[0];
            lattice[0][2] = -lattice[0][0] * celldm[2];
            lattice[1][0] = celldm[0] * celldm[1] * celldm[3];
            lattice[1][1] = celldm[0] * celldm[1] * Math.sqrt(1.0 - celldm[3] * celldm[3]);
            lattice[2][0] = lattice[0][0];
            lattice[2][2] = -lattice[0][2];
            break;

        case -13:
            if (celldm[1] <= 0.0) {
                lattice = null;
                break;
            }
            if (celldm[2] <= 0.0) {
                lattice = null;
                break;
            }
            if (Math.abs(celldm[4]) >= 1.0) {
                lattice = null;
                break;
            }
            lattice[0][0] = 0.5 * celldm[0];
            lattice[0][1] = -lattice[0][0] * celldm[1];
            lattice[1][0] = lattice[0][0];
            lattice[1][1] = -lattice[0][1];
            lattice[2][0] = celldm[0] * celldm[2] * celldm[4];
            lattice[2][2] = celldm[0] * celldm[2] * Math.sqrt(1.0 - celldm[4] * celldm[4]);
            break;

        case 14:
            if (celldm[1] <= 0.0) {
                lattice = null;
                break;
            }
            if (celldm[2] <= 0.0) {
                lattice = null;
                break;
            }
            if (Math.abs(celldm[3]) >= 1.0) {
                lattice = null;
                break;
            }
            if (Math.abs(celldm[4]) >= 1.0) {
                lattice = null;
                break;
            }
            if (Math.abs(celldm[5]) >= 1.0) {
                lattice = null;
                break;
            }
            term1 = Math.sqrt(1.0 - celldm[5] * celldm[5]);
            if (term1 == 0.0) {
                lattice = null;
                break;
            }
            term2 = 1.0 + 2.0 * celldm[3] * celldm[4] * celldm[5];
            term2 += -celldm[3] * celldm[3] - celldm[4] * celldm[4] - celldm[5] * celldm[5];
            if (term2 < 0.0) {
                lattice = null;
                break;
            }
            term2 = Math.sqrt(term2 / (1.0 - celldm[5] * celldm[5]));
            lattice[0][0] = celldm[0];
            lattice[1][0] = celldm[0] * celldm[1] * celldm[5];
            lattice[1][1] = celldm[0] * celldm[1] * term1;
            lattice[2][0] = celldm[0] * celldm[2] * celldm[4];
            lattice[2][1] = celldm[0] * celldm[2] * (celldm[3] - celldm[4] * celldm[5]) / term1;
            lattice[2][2] = celldm[0] * celldm[2] * term2;
            break;

        default:
            lattice = null;
            break;
        }

        if (lattice != null) {
            lattice = Matrix3D.mult(Constants.BOHR_RADIUS_ANGS, lattice);
        }

        return lattice;
    }
}
