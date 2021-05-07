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

import burai.input.card.QEAtomicSpecies;
import burai.input.card.QECard;
import burai.input.correcter.QEInputCorrecter;

public abstract class QESecondaryInput extends QEInput {

    private QEInput parentInput;

    protected QESecondaryInput() {
        super();
        this.parentInput = null;
    }

    protected QESecondaryInput(String fileName) throws IOException {
        super(fileName);
        this.parentInput = null;
    }

    protected QESecondaryInput(File file) throws IOException {
        super(file);
        this.parentInput = null;
    }

    public void setParentInput(QEInput parentInput) {
        this.parentInput = parentInput;
        this.reload();
        this.bindToParentAtomicSpecies();
    }

    @Override
    public void reload() {
        if (this.parentInput == null) {
            return;
        }

        QEInputCopier copier = new QEInputCopier(this.parentInput);
        copier.copyTo(this);

        QEInputCorrecter inputCorrecter = this.getInputCorrector();
        if (inputCorrecter != null) {
            inputCorrecter.correctInput();
        }
    }

    private void bindToParentAtomicSpecies() {
        if (this.parentInput == null) {
            return;
        }

        QECard srcCard = this.parentInput.getCard(QEAtomicSpecies.CARD_NAME);
        if (srcCard == null && !(srcCard instanceof QEAtomicSpecies)) {
            return;
        }

        QECard dstCard = this.getCard(QEAtomicSpecies.CARD_NAME);
        if (dstCard == null || !(dstCard instanceof QEAtomicSpecies)) {
            return;
        }

        srcCard.addListener(event -> {
            srcCard.copyTo(dstCard);
        });
    }
}
