/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.com.math;

import burai.com.consts.Constants;

public final class Lattice {

    private static final double ROOT2 = Math.sqrt(2.0);
    private static final double ROOT3 = Math.sqrt(3.0);

    private static final double CELL_THRESHOLD = 1.0e-6;

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

    public static double[] getCellDm(double[][] cell) {
        return getCellDm(0, cell);
    }

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

    // return a, b, c, alpha, beta and gamma
    public static double[] getLatticeConstants(double[][] cell, boolean asCos) {
        return getLatticeConstants(0, cell, asCos);
    }

    // return a, b, c, alpha, beta and gamma
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

    public static boolean isCorrectBravais(int ibrav) {
        for (int ibrav2 : IBRAV_LIST) {
            if (ibrav == ibrav2) {
                return true;
            }
        }

        return false;
    }

    public static int getBravais(double[][] cell) {
        if (!checkCell(cell)) {
            return 0;
        }

        double[] celldm = getCellDm(cell);
        if (celldm == null || celldm.length < 6) {
            return 0;
        }

        for (int ibrav : IBRAV_LIST) {
            double[] celldm2 = convertCellDm(ibrav, celldm);
            if (celldm == null || celldm.length < 6) {
                continue;
            }
            double[][] cell2 = getCell(ibrav, celldm2);
            if (cell2 != null && Matrix3D.equals(cell, cell2, CELL_THRESHOLD)) {
                return ibrav;
            }
        }

        return 0;
    }

    private static double[][] getCell(double[] celldm) {
        if (celldm == null || celldm.length < 6) {
            return null;
        }

        for (int ibrav : IBRAV_LIST) {
            double[] celldm1 = convertCellDm(ibrav, celldm);
            double[][] cell = getCell(ibrav, celldm1);
            double[] celldm2 = getCellDm(cell);
            if (celldm2 == null || celldm2.length < 6) {
                continue;
            }

            boolean sameCell = true;
            for (int i = 0; i < 6; i++) {
                if (Math.abs(celldm[i] - celldm2[i]) > CELL_THRESHOLD) {
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

        return getCell(celldm);
    }

    private static double[] convertCellDm(int ibrav, double[] celldm) {
        if (celldm == null || celldm.length < 6) {
            return null;
        }

        double[] celldm2 = new double[6];
        celldm2[0] = celldm[0];
        celldm2[1] = celldm[1];
        celldm2[2] = celldm[2];
        if (ibrav == 14) {
            celldm2[3] = celldm[3];
            celldm2[4] = celldm[4];
            celldm2[5] = celldm[5];
        } else if (ibrav == -12 || ibrav == -13) {
            celldm2[3] = 0.0;
            celldm2[4] = celldm[4];
            celldm2[5] = 0.0;
        } else if (isCorrectBravais(ibrav)) {
            celldm2[3] = celldm[5];
            celldm2[4] = 0.0;
            celldm2[5] = 0.0;
        } else {
            celldm2[3] = celldm[3];
            celldm2[4] = celldm[4];
            celldm2[5] = celldm[5];
        }

        switch (ibrav) {
        //case 1:
        //    break;

        case 2:
            celldm2[0] *= 2.0 / ROOT2;
            break;

        case 3:
            celldm2[0] *= 2.0 / ROOT3;
            break;

        //case 4:
        //    break;

        case 5:
            // TODO
            break;

        case -5:
            // TODO
            break;

        //case 6:
        //    break;

        case 7:
            // TODO
            break;

        //case 8:
        //    break;

        case 9:
            // TODO
            break;

        case -9:
            // TODO
            break;

        case 91:
            // TODO
            break;

        case 10:
            // TODO
            break;

        case 11:
            // TODO
            break;

        //case 12:
        //    break;

        //case -12:
        //    break;

        case 13:
            // TODO
            break;

        case -13:
            // TODO
            break;

        //case 14:
        //    break;
        }

        return celldm2;
    }

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
            lattice[1][0] = -term1;
            lattice[2][0] = -term1;
            lattice[2][1] = -term1;
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
