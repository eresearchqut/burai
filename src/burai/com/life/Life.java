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

package burai.com.life;

import java.util.ArrayList;
import java.util.List;

public class Life {

    private static Life instance = null;

    public static Life getInstance() {
        if (instance == null) {
            instance = new Life();
        }

        return instance;
    }

    private boolean alive;

    private List<Dead> onDeadList;

    private Life() {
        this.alive = true;
        this.onDeadList = null;
    }

    public synchronized boolean isAlive() {
        return this.alive;
    }

    public synchronized void toBeDead() {
        if (!this.alive) {
            return;
        }

        this.alive = false;

        if (this.onDeadList != null) {
            for (Dead onDead : onDeadList) {
                if (onDead != null) {
                    onDead.onDead();
                }
            }
        }
    }

    public synchronized void addOnDead(Dead onDead) {
        if (!this.alive) {
            return;
        }

        if (onDead == null) {
            return;
        }

        if (this.onDeadList == null) {
            this.onDeadList = new ArrayList<Dead>();
        }

        this.onDeadList.add(onDead);
    }

    public synchronized void removeOnDead(Dead onDead) {
        if (!this.alive) {
            return;
        }

        if (onDead == null) {
            return;
        }

        if (this.onDeadList == null) {
            return;
        }

        int index = this.onDeadList.indexOf(onDead);
        if (index > -1) {
            this.onDeadList.remove(index);
        }
    }
}
