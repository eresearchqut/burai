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

package burai.input;

import java.io.File;
import java.io.IOException;

import burai.input.card.QEAtomicPositions;
import burai.input.card.QEAtomicSpecies;
import burai.input.card.QECard;
import burai.input.card.QECellParameters;
import burai.input.card.QEKPoints;
import burai.input.correcter.QEDOSInputCorrecter;
import burai.input.correcter.QEInputCorrecter;
import burai.input.namelist.QENamelist;
import burai.input.namelist.tracer.QEDOSTracer;

public class QEDOSInput extends QESecondaryInput {

    public QEDOSInput() {
        super();
    }

    public QEDOSInput(String fileName) throws IOException {
        super(fileName);
    }

    public QEDOSInput(File file) throws IOException {
        super(file);
    }

    @Override
    protected void setupNamelists(QEInputReader reader) throws IOException {
        boolean hasNmlControl = this.namelists.containsKey(NAMELIST_CONTROL);
        boolean hasNmlSystem = this.namelists.containsKey(NAMELIST_SYSTEM);
        boolean hasNmlDos = this.namelists.containsKey(NAMELIST_DOS);
        boolean hasNmlProjwfc = this.namelists.containsKey(NAMELIST_PROJWFC);

        this.setupNamelist(NAMELIST_CONTROL, reader);
        this.setupNamelist(NAMELIST_SYSTEM, reader);
        this.setupNamelist(NAMELIST_ELECTRONS, reader);
        this.setupNamelist(NAMELIST_DOS, reader);
        this.setupNamelist(NAMELIST_PROJWFC, reader);

        if (!hasNmlControl) {
            QENamelist nmlControl = this.namelists.get(NAMELIST_CONTROL);
            nmlControl.addProtectedValue("restart_mode");
            nmlControl.addProtectedValue("calculation");
        }

        if (!hasNmlSystem) {
            QENamelist nmlSystem = this.namelists.get(NAMELIST_SYSTEM);
            nmlSystem.addProtectedValue("nbnd");
            nmlSystem.addProtectedValue("occupations");
            nmlSystem.addProtectedValue("smearing");
            nmlSystem.addProtectedValue("degauss");
        }

        if ((!hasNmlDos) && (!hasNmlProjwfc)) {
            QENamelist nmlDos = this.namelists.get(NAMELIST_DOS);
            QENamelist nmlProjwfc = this.namelists.get(NAMELIST_PROJWFC);
            QEDOSTracer dosTracer = new QEDOSTracer(nmlDos, nmlProjwfc);
            dosTracer.traceDos();
        }
    }

    @Override
    protected void setupCards(QEInputReader reader) throws IOException {
        this.setupCard(new QEKPoints(), reader);
        this.setupCard(new QECellParameters(), reader);
        this.setupCard(new QEAtomicSpecies(), reader);
        this.setupCard(new QEAtomicPositions(), reader);

        QECard cardKPoints = this.cards.get(QEKPoints.CARD_NAME);
        cardKPoints.setProtectedToCopy(true);
    }

    @Override
    public QEDOSInput copy() {
        QEDOSInput input = new QEDOSInput();
        QEInputCopier copier = new QEInputCopier(this);
        copier.copyTo(input, false);
        return input;
    }

    @Override
    protected QEInputCorrecter createInputCorrector() {
        return new QEDOSInputCorrecter(this);
    }
}
