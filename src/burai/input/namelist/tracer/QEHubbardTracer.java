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

package burai.input.namelist.tracer;

import burai.input.namelist.QENamelist;
import burai.input.namelist.QEValue;
import burai.input.namelist.QEValueBuffer;

public class QEHubbardTracer {

    private boolean busy;

    private QENamelist nmlSystem;

    public QEHubbardTracer(QENamelist nmlSystem) {
        if (nmlSystem == null) {
            throw new IllegalArgumentException("nmlSystem is null.");
        }

        this.busy = false;
        this.nmlSystem = nmlSystem;
    }

    public void traceHubbard() {
        this.setupLdaPlusU();
        this.setupNoncolin();
    }

    private void setupLdaPlusU() {
        QEValueBuffer valueBuffer = this.nmlSystem.getValueBuffer("lda_plus_u");

        if (valueBuffer.hasValue()) {
            this.updateLdaPlusU(valueBuffer.getValue());
        } else {
            this.updateLdaPlusU(null);
        }

        valueBuffer.addListener(value -> {
            this.updateLdaPlusU(value);
        });
    }

    private void updateLdaPlusU(QEValue value) {
        if (this.busy) {
            return;
        }

        this.busy = true;

        if (value != null && value.getLogicalValue()) {
            QEValue noncolinValue = this.nmlSystem.getValue("noncolin");
            if (noncolinValue != null && noncolinValue.getLogicalValue()) {
                this.nmlSystem.setValue("lda_plus_u_kind = 1");
            }
        }

        this.busy = false;
    }

    private void setupNoncolin() {
        QEValueBuffer valueBuffer = this.nmlSystem.getValueBuffer("noncolin");

        if (valueBuffer.hasValue()) {
            this.updateNoncolin(valueBuffer.getValue());
        } else {
            this.updateNoncolin(null);
        }

        valueBuffer.addListener(value -> {
            this.updateNoncolin(value);
        });
    }

    private void updateNoncolin(QEValue value) {
        if (this.busy) {
            return;
        }

        this.busy = true;

        if (value != null && value.getLogicalValue()) {
            QEValue ldaUValue = this.nmlSystem.getValue("lda_plus_u");
            if (ldaUValue != null && ldaUValue.getLogicalValue()) {
                this.nmlSystem.setValue("lda_plus_u_kind = 1");
            }
        }

        this.busy = false;
    }
}
