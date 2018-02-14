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

import burai.input.card.QECard;
import burai.input.namelist.QENamelist;

public class QEInputCopier {

    private QEInput srcInput;

    public QEInputCopier(QEInput srcInput) {
        if (srcInput == null) {
            throw new IllegalArgumentException("srcInput is null.");
        }

        this.srcInput = srcInput;
    }

    public void copyTo(QEInput dstInput) {
        this.copyTo(dstInput, true);
    }

    public void copyTo(QEInput dstInput, boolean protect) {
        if (dstInput == null) {
            throw new IllegalArgumentException("dstInput is null.");
        }

        String[] keyNamelists = QEInput.listNamelistKeys();
        for (String keyNamelist : keyNamelists) {
            QENamelist srcNamelist = this.srcInput.getNamelist(keyNamelist);
            QENamelist dstNamelist = dstInput.getNamelist(keyNamelist);
            if (srcNamelist != null && dstNamelist != null) {
                srcNamelist.copyTo(dstNamelist, protect);
            }
        }

        String[] keyCards = QEInput.listCardKeys();
        for (String keyCard : keyCards) {
            QECard srcCard = this.srcInput.getCard(keyCard);
            QECard dstCard = dstInput.getCard(keyCard);
            if (srcCard != null && dstCard != null) {
                srcCard.copyTo(dstCard, protect);
            }
        }
    }
}
