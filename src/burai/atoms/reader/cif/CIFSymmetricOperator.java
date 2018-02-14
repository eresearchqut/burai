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

package burai.atoms.reader.cif;

public class CIFSymmetricOperator {

    private CIFSymmetricFormula xFormula;
    private CIFSymmetricFormula yFormula;
    private CIFSymmetricFormula zFormula;

    public CIFSymmetricOperator(String operator) throws IncorrectCIFSymmetricException {
        if (operator == null || operator.isEmpty()) {
            throw new IllegalArgumentException("operator is null or empty.");
        }

        String operator2 = operator;
        operator2 = operator2.replace('(', ' ');
        operator2 = operator2.replace(')', ' ');
        operator2 = operator2.replace('[', ' ');
        operator2 = operator2.replace(']', ' ');
        operator2 = operator2.replace('{', ' ');
        operator2 = operator2.replace('}', ' ');

        String[] subOperator = operator2.trim().split("[,:;]+");
        if (subOperator == null || subOperator.length < 3) {
            throw new IncorrectCIFSymmetricException();
        }

        this.xFormula = new CIFSymmetricFormula(subOperator[0]);
        this.yFormula = new CIFSymmetricFormula(subOperator[1]);
        this.zFormula = new CIFSymmetricFormula(subOperator[2]);
    }

    public double[] operate(double[] coord) {
        if (coord == null || coord.length < 3) {
            return null;
        }

        double x = this.xFormula.operate(coord);
        double y = this.yFormula.operate(coord);
        double z = this.zFormula.operate(coord);

        return new double[] { x, y, z };
    }
}
