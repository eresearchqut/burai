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

package burai.input.card;

public class QECardEvent {

    public static final int EVENT_TYPE_NULL = 0;

    public static final int EVENT_TYPE_UNIT_CHANGED = 1;
    public static final int EVENT_TYPE_SPECIES_CHANGED = 2;
    public static final int EVENT_TYPE_SPECIES_ADDED = 3;
    public static final int EVENT_TYPE_SPECIES_REMOVED = 4;
    public static final int EVENT_TYPE_SPECIES_CLEARED = 5;
    public static final int EVENT_TYPE_ATOM_CHANGED = 6;
    public static final int EVENT_TYPE_ATOM_MOVED = 7;
    public static final int EVENT_TYPE_ATOM_ADDED = 8;
    public static final int EVENT_TYPE_ATOM_REMOVED = 9;
    public static final int EVENT_TYPE_ATOM_CLEARED = 10;
    public static final int EVENT_TYPE_KPOINT_CHANGED = 11;
    public static final int EVENT_TYPE_KPOINT_ADDED = 12;
    public static final int EVENT_TYPE_KPOINT_REMOVED = 13;
    public static final int EVENT_TYPE_KPOINT_CLEARED = 14;
    public static final int EVENT_TYPE_KGRID_CHANGED = 15;

    private QECard card;

    private int eventType;

    private int speciesIndex;

    private int atomIndex;

    private int kpointIndex;

    public QECardEvent(QECard card) {
        if (card == null) {
            throw new IllegalArgumentException("card is null.");
        }

        this.card = card;
        this.eventType = EVENT_TYPE_NULL;
        this.atomIndex = -1;
        this.kpointIndex = -1;
    }

    public QECard getCard() {
        return this.card;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public int getEventType() {
        return this.eventType;
    }

    public void setSpeciesIndex(int speciesIndex) {
        this.speciesIndex = speciesIndex;
    }

    public int getSpeciesIndex() {
        return this.speciesIndex;
    }

    public void setAtomIndex(int atomIndex) {
        this.atomIndex = atomIndex;
    }

    public int getAtomIndex() {
        return this.atomIndex;
    }

    public void setKPointIndex(int kpointIndex) {
        this.kpointIndex = kpointIndex;
    }

    public int getKPointIndex() {
        return this.kpointIndex;
    }
}
