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

package burai.input.correcter;

import java.util.ArrayList;
import java.util.List;

import burai.input.QEInput;
import burai.input.card.QEKPoint;

public class BrillouinPathGenerator {

    private QEInput input;

    private SymmetricKPointsGenerator symmetricKPointsGenerator;

    public BrillouinPathGenerator(QEInput input) {
        if (input == null) {
            throw new IllegalArgumentException("input is null.");
        }

        this.input = input;
        this.symmetricKPointsGenerator = new SymmetricKPointsGenerator(this.input);
    }

    public boolean isAvailable() {
        return this.symmetricKPointsGenerator.isAvailable();
    }

    public List<QEKPoint> getKPoints() {
        List<QEKPoint> kpoints = new ArrayList<QEKPoint>();

        int ibz = this.symmetricKPointsGenerator.getBZType();

        if (ibz == 1) {
            kpoints.add(new QEKPoint("gG", 20.0));
            kpoints.add(new QEKPoint("X", 20.0));
            kpoints.add(new QEKPoint("M", 20.0));
            kpoints.add(new QEKPoint("gG", 20.0));
            kpoints.add(new QEKPoint("R", 20.0));
            kpoints.add(new QEKPoint("X", 0.0));
            kpoints.add(new QEKPoint("M", 20.0));
            kpoints.add(new QEKPoint("R", 0.0));

        } else if (ibz == 2) {
            kpoints.add(new QEKPoint("gG", 20.0));
            kpoints.add(new QEKPoint("X", 20.0));
            kpoints.add(new QEKPoint("W", 20.0));
            kpoints.add(new QEKPoint("K", 20.0));
            kpoints.add(new QEKPoint("gG", 20.0));
            kpoints.add(new QEKPoint("L", 20.0));
            kpoints.add(new QEKPoint("U", 20.0));
            kpoints.add(new QEKPoint("W", 20.0));
            kpoints.add(new QEKPoint("L", 20.0));
            kpoints.add(new QEKPoint("K", 0.0));
            kpoints.add(new QEKPoint("U", 20.0));
            kpoints.add(new QEKPoint("X", 0.0));

        } else if (ibz == 3) {
            kpoints.add(new QEKPoint("gG", 20.0));
            kpoints.add(new QEKPoint("H", 20.0));
            kpoints.add(new QEKPoint("N", 20.0));
            kpoints.add(new QEKPoint("gG", 20.0));
            kpoints.add(new QEKPoint("P", 20.0));
            kpoints.add(new QEKPoint("H", 0.0));
            kpoints.add(new QEKPoint("P", 20.0));
            kpoints.add(new QEKPoint("N", 0.0));

        } else if (ibz == 4) {
            kpoints.add(new QEKPoint("gG", 20.0));
            kpoints.add(new QEKPoint("X", 20.0));
            kpoints.add(new QEKPoint("M", 20.0));
            kpoints.add(new QEKPoint("gG", 20.0));
            kpoints.add(new QEKPoint("Z", 20.0));
            kpoints.add(new QEKPoint("R", 20.0));
            kpoints.add(new QEKPoint("A", 20.0));
            kpoints.add(new QEKPoint("Z", 0.0));
            kpoints.add(new QEKPoint("X", 20.0));
            kpoints.add(new QEKPoint("R", 0.0));
            kpoints.add(new QEKPoint("M", 20.0));
            kpoints.add(new QEKPoint("A", 0.0));

        } else if (ibz == 5) {
            kpoints.add(new QEKPoint("gG", 20.0));
            kpoints.add(new QEKPoint("X", 20.0));
            kpoints.add(new QEKPoint("M", 20.0));
            kpoints.add(new QEKPoint("gG", 20.0));
            kpoints.add(new QEKPoint("Z", 20.0));
            kpoints.add(new QEKPoint("P", 20.0));
            kpoints.add(new QEKPoint("N", 20.0));
            kpoints.add(new QEKPoint("Z1", 20.0));
            kpoints.add(new QEKPoint("M", 0.0));
            kpoints.add(new QEKPoint("X", 20.0));
            kpoints.add(new QEKPoint("P", 0.0));

        } else if (ibz == 6) {
            kpoints.add(new QEKPoint("gG", 20.0));
            kpoints.add(new QEKPoint("X", 20.0));
            kpoints.add(new QEKPoint("Y", 20.0));
            kpoints.add(new QEKPoint("gS", 20.0));
            kpoints.add(new QEKPoint("gG", 20.0));
            kpoints.add(new QEKPoint("Z", 20.0));
            kpoints.add(new QEKPoint("gS1", 20.0));
            kpoints.add(new QEKPoint("N", 20.0));
            kpoints.add(new QEKPoint("P", 20.0));
            kpoints.add(new QEKPoint("Y1", 20.0));
            kpoints.add(new QEKPoint("Z", 0.0));
            kpoints.add(new QEKPoint("X", 20.0));
            kpoints.add(new QEKPoint("P", 0.0));

        } else if (ibz == 7) {
            kpoints.add(new QEKPoint("gG", 20.0));
            kpoints.add(new QEKPoint("X", 20.0));
            kpoints.add(new QEKPoint("S", 20.0));
            kpoints.add(new QEKPoint("Y", 20.0));
            kpoints.add(new QEKPoint("gG", 20.0));
            kpoints.add(new QEKPoint("Z", 20.0));
            kpoints.add(new QEKPoint("U", 20.0));
            kpoints.add(new QEKPoint("R", 20.0));
            kpoints.add(new QEKPoint("T", 20.0));
            kpoints.add(new QEKPoint("Z", 0.0));
            kpoints.add(new QEKPoint("Y", 20.0));
            kpoints.add(new QEKPoint("T", 0.0));
            kpoints.add(new QEKPoint("U", 20.0));
            kpoints.add(new QEKPoint("X", 0.0));
            kpoints.add(new QEKPoint("S", 20.0));
            kpoints.add(new QEKPoint("R", 0.0));

        } else if (ibz == 8) {
            kpoints.add(new QEKPoint("gG", 20.0));
            kpoints.add(new QEKPoint("Y", 20.0));
            kpoints.add(new QEKPoint("T", 20.0));
            kpoints.add(new QEKPoint("Z", 20.0));
            kpoints.add(new QEKPoint("gG", 20.0));
            kpoints.add(new QEKPoint("X", 20.0));
            kpoints.add(new QEKPoint("A1", 20.0));
            kpoints.add(new QEKPoint("Y", 0.0));
            kpoints.add(new QEKPoint("T", 20.0));
            kpoints.add(new QEKPoint("X1", 0.0));
            kpoints.add(new QEKPoint("X", 20.0));
            kpoints.add(new QEKPoint("A", 20.0));
            kpoints.add(new QEKPoint("Z", 0.0));
            kpoints.add(new QEKPoint("L", 20.0));
            kpoints.add(new QEKPoint("gG", 0.0));

        } else if (ibz == 9) {
            kpoints.add(new QEKPoint("gG", 20.0));
            kpoints.add(new QEKPoint("Y", 20.0));
            kpoints.add(new QEKPoint("C", 20.0));
            kpoints.add(new QEKPoint("D", 20.0));
            kpoints.add(new QEKPoint("X", 20.0));
            kpoints.add(new QEKPoint("gG", 20.0));
            kpoints.add(new QEKPoint("Z", 20.0));
            kpoints.add(new QEKPoint("D1", 20.0));
            kpoints.add(new QEKPoint("H", 20.0));
            kpoints.add(new QEKPoint("C", 0.0));
            kpoints.add(new QEKPoint("C1", 20.0));
            kpoints.add(new QEKPoint("Z", 0.0));
            kpoints.add(new QEKPoint("X", 20.0));
            kpoints.add(new QEKPoint("H1", 0.0));
            kpoints.add(new QEKPoint("H", 20.0));
            kpoints.add(new QEKPoint("Y", 0.0));
            kpoints.add(new QEKPoint("L", 20.0));
            kpoints.add(new QEKPoint("gG", 0.0));

        } else if (ibz == 10) {
            kpoints.add(new QEKPoint("gG", 20.0));
            kpoints.add(new QEKPoint("Y", 20.0));
            kpoints.add(new QEKPoint("T", 20.0));
            kpoints.add(new QEKPoint("Z", 20.0));
            kpoints.add(new QEKPoint("gG", 20.0));
            kpoints.add(new QEKPoint("X", 20.0));
            kpoints.add(new QEKPoint("A1", 20.0));
            kpoints.add(new QEKPoint("Y", 0.0));
            kpoints.add(new QEKPoint("X", 20.0));
            kpoints.add(new QEKPoint("A", 20.0));
            kpoints.add(new QEKPoint("Z", 0.0));
            kpoints.add(new QEKPoint("L", 20.0));
            kpoints.add(new QEKPoint("gG", 0.0));

        } else if (ibz == 11) {
            kpoints.add(new QEKPoint("gG", 20.0));
            kpoints.add(new QEKPoint("X", 20.0));
            kpoints.add(new QEKPoint("L", 20.0));
            kpoints.add(new QEKPoint("T", 20.0));
            kpoints.add(new QEKPoint("W", 20.0));
            kpoints.add(new QEKPoint("R", 20.0));
            kpoints.add(new QEKPoint("X1", 20.0));
            kpoints.add(new QEKPoint("Z", 20.0));
            kpoints.add(new QEKPoint("gG", 20.0));
            kpoints.add(new QEKPoint("Y", 20.0));
            kpoints.add(new QEKPoint("S", 20.0));
            kpoints.add(new QEKPoint("W", 0.0));
            kpoints.add(new QEKPoint("L1", 20.0));
            kpoints.add(new QEKPoint("Y", 0.0));
            kpoints.add(new QEKPoint("Y1", 20.0));
            kpoints.add(new QEKPoint("Z", 0.0));

        } else if (ibz == 12) {
            kpoints.add(new QEKPoint("gG", 20.0));
            kpoints.add(new QEKPoint("X", 20.0));
            kpoints.add(new QEKPoint("S", 20.0));
            kpoints.add(new QEKPoint("R", 20.0));
            kpoints.add(new QEKPoint("A", 20.0));
            kpoints.add(new QEKPoint("Z", 20.0));
            kpoints.add(new QEKPoint("gG", 20.0));
            kpoints.add(new QEKPoint("Y", 20.0));
            kpoints.add(new QEKPoint("X1", 20.0));
            kpoints.add(new QEKPoint("A1", 20.0));
            kpoints.add(new QEKPoint("T", 20.0));
            kpoints.add(new QEKPoint("Y", 0.0));
            kpoints.add(new QEKPoint("Z", 20.0));
            kpoints.add(new QEKPoint("T", 0.0));

        } else if (ibz == 13) {
            kpoints.add(new QEKPoint("gG", 20.0));
            kpoints.add(new QEKPoint("M", 20.0));
            kpoints.add(new QEKPoint("K", 20.0));
            kpoints.add(new QEKPoint("gG", 20.0));
            kpoints.add(new QEKPoint("A", 20.0));
            kpoints.add(new QEKPoint("L", 20.0));
            kpoints.add(new QEKPoint("H", 20.0));
            kpoints.add(new QEKPoint("A", 0.0));
            kpoints.add(new QEKPoint("L", 20.0));
            kpoints.add(new QEKPoint("M", 0.0));
            kpoints.add(new QEKPoint("K", 20.0));
            kpoints.add(new QEKPoint("H", 0.0));

        } else if (ibz == 14) {
            kpoints.add(new QEKPoint("gG", 20.0));
            kpoints.add(new QEKPoint("L", 20.0));
            kpoints.add(new QEKPoint("B1", 0.0));
            kpoints.add(new QEKPoint("B", 20.0));
            kpoints.add(new QEKPoint("Z", 20.0));
            kpoints.add(new QEKPoint("gG", 20.0));
            kpoints.add(new QEKPoint("X", 0.0));
            kpoints.add(new QEKPoint("Q", 20.0));
            kpoints.add(new QEKPoint("F", 20.0));
            kpoints.add(new QEKPoint("P1", 20.0));
            kpoints.add(new QEKPoint("Z", 0.0));
            kpoints.add(new QEKPoint("L", 20.0));
            kpoints.add(new QEKPoint("P", 0.0));

        } else if (ibz == 15) {
            kpoints.add(new QEKPoint("gG", 20.0));
            kpoints.add(new QEKPoint("P", 20.0));
            kpoints.add(new QEKPoint("Z", 20.0));
            kpoints.add(new QEKPoint("Q", 20.0));
            kpoints.add(new QEKPoint("gG", 20.0));
            kpoints.add(new QEKPoint("F", 20.0));
            kpoints.add(new QEKPoint("P1", 20.0));
            kpoints.add(new QEKPoint("Q1", 20.0));
            kpoints.add(new QEKPoint("L", 20.0));
            kpoints.add(new QEKPoint("Z", 0.0));

        } else if (ibz == 16) {
            kpoints.add(new QEKPoint("gG", 20.0));
            kpoints.add(new QEKPoint("Y", 20.0));
            kpoints.add(new QEKPoint("D", 20.0));
            kpoints.add(new QEKPoint("Z", 0.0));
            kpoints.add(new QEKPoint("X", 20.0));
            kpoints.add(new QEKPoint("A", 0.0));

        } else {
            return null;
        }

        return kpoints;
    }
}
