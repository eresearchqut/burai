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

package burai.atoms.visible;

import javafx.scene.shape.Sphere;

public class AtomicSphere extends Sphere {

    private static final int SPHERE_DIV_HIGH = 24;
    private static final int SPHERE_DIV_LOW = 16;

    private VisibleAtom visibleAtom;

    public AtomicSphere(VisibleAtom visibleAtom, boolean divHigh) {
        super(1.0, divHigh ? SPHERE_DIV_HIGH : SPHERE_DIV_LOW);
        this.visibleAtom = visibleAtom;
    }

    public VisibleAtom getVisibleAtom() {
        return this.visibleAtom;
    }

}
