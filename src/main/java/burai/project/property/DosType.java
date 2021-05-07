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

package burai.project.property;

public enum DosType {
    TOTAL(-1, ""),
    PDOS_S(0, "s"),
    PDOS_P(1, "p"),
    PDOS_D(2, "d"),
    PDOS_F(3, "f");

    private int momentum;

    private String orbital;

    private DosType(int momentum, String orbital) {
        this.momentum = momentum;
        this.orbital = orbital;
    }

    public int getMomentum() {
        return this.momentum;
    }

    public String getOrbital() {
        return this.orbital;
    }
}
