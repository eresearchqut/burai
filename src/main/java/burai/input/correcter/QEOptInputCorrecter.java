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

import burai.input.QEInput;
import burai.input.namelist.QEValue;

public class QEOptInputCorrecter extends QEInputCorrecter {

    public QEOptInputCorrecter(QEInput input) {
        super(input);
    }

    @Override
    public void correctInput() {
        this.correctNamelistControl();
        this.correctNamelistIons();
        this.correctNamelistCell();
    }

    private void correctNamelistControl() {
        if (this.nmlControl == null) {
            return;
        }

        QEValue value = null;

        /*
         * calculation
         */
        String calculation = null;
        value = this.nmlControl.getValue("calculation");
        if (value != null) {
            calculation = value.getCharacterValue();
        }
        if ((!"relax".equals(calculation)) && (!"vc-relax".equals(calculation))) {
            this.nmlControl.setValue("calculation = relax");
        }

        /*
         * max_seconds
         */
        value = this.nmlControl.getValue("max_seconds");
        if (value == null) {
            this.nmlControl.setValue("max_seconds = 1.728e+05");
        }

        /*
         * nstep
         */
        int nstep = 0;
        value = this.nmlControl.getValue("nstep");
        if (value != null) {
            nstep = value.getIntegerValue();
        }
        if (nstep <= 1) {
            this.nmlControl.setValue("nstep = 100");
        }

        /*
         * tprnfor
         */
        value = this.nmlControl.getValue("tprnfor");
        if (value != null && (!value.getLogicalValue())) {
            this.nmlControl.removeValue("tprnfor");
        }

        /*
         * tstress
         */
        value = this.nmlControl.getValue("tstress");
        if (value != null && (!value.getLogicalValue())) {
            this.nmlControl.removeValue("tstress");
        }

        /*
         * forc_conv_thr
         */
        value = this.nmlControl.getValue("forc_conv_thr");
        if (value == null) {
            this.nmlControl.setValue("forc_conv_thr = 1.0e-3");
        }
    }

    private void correctNamelistIons() {
        if (this.nmlIons == null) {
            return;
        }

        QEValue value = null;

        /*
         * ion_dynamics
         */
        value = this.nmlIons.getValue("ion_dynamics");
        if (value == null) {
            this.nmlIons.setValue("ion_dynamics = bfgs");
        }
    }

    private void correctNamelistCell() {
        if (this.nmlCell == null) {
            return;
        }

        QEValue calcValue = this.nmlControl.getValue("calculation");
        if (calcValue == null || (!"vc-relax".equals(calcValue.getCharacterValue()))) {
            return;
        }

        QEValue value = null;

        /*
         * cell_dynamics
         */
        value = this.nmlCell.getValue("cell_dynamics");
        if (value == null) {
            this.nmlCell.setValue("cell_dynamics = bfgs");
        }

        /*
         * press_conv_thr
         */
        value = this.nmlCell.getValue("press_conv_thr");
        if (value == null) {
            this.nmlCell.setValue("press_conv_thr = 0.5");
        }
    }
}
