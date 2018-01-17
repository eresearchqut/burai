/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.com.math;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public final class Calculator {

    private Calculator() {
        // NOP
    }

    public static double expr(String formula) throws NullPointerException, NumberFormatException {
        /*
         * Supported Functions
         * ----------------------------------------------------
         * abs: absolute value
         * acos: arc cosine
         * asin: arc sine
         * atan: arc tangent
         * cbrt: cubic root
         * ceil: nearest upper integer
         * cos: cosine
         * cosh: hyperbolic cosine
         * exp: euler's number raised to the power (e^x)
         * floor: nearest lower integer
         * log: logarithmus naturalis (base e)
         * log10: logarithm (base 10)
         * log2: logarithm (base 2)
         * sin: sine
         * sinh: hyperbolic sine
         * sqrt: square root
         * tan: tangent
         * tanh: hyperbolic tangent
         * signum: signum function
         */

        if (formula == null) {
            throw new NullPointerException("formula is null.");
        }

        String formula2 = formula.trim();
        if (formula2.isEmpty()) {
            throw new NumberFormatException("formula is empty.");
        }

        formula2 = formula2.toLowerCase();
        if (formula2.charAt(0) == 'd') {
            throw new NumberFormatException("formula starts with `D'.");
        }

        formula2 = formula2.replace('d', 'e');

        try {
            Expression objExpr = new ExpressionBuilder(formula2).build();
            return objExpr.evaluate();

        } catch (Exception e) {
            throw new NumberFormatException(e.getMessage());
        }
    }

    public static boolean isFormula(String formula) {
        if (formula == null || formula.isEmpty()) {
            return false;
        }

        try {
            Calculator.expr(formula);

        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }
}
