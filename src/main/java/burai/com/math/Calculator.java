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
