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

package burai.run;

import java.util.LinkedList;
import java.util.Queue;

public class RunningQueue implements RunningManagerListener {

    private boolean alive;

    private RunningManager manager;

    private RunningManagerListener listener;

    private Queue<RunningNode> bufferingNodes;

    protected RunningQueue(RunningManager manager, Queue<RunningNode> bufferingNodes) {
        if (manager == null) {
            throw new IllegalArgumentException("manager is null.");
        }

        this.alive = true;

        this.manager = manager;

        this.listener = null;

        if (bufferingNodes != null) {
            this.bufferingNodes = bufferingNodes;
        } else {
            this.bufferingNodes = new LinkedList<RunningNode>();
        }
    }

    public synchronized void stopQueue() {
        this.alive = false;
        this.manager.removeListener(this);
        this.notifyAll();
    }

    public synchronized RunningNode pollNode() {
        RunningNode node = null;

        while (this.alive) {
            node = this.bufferingNodes.poll();
            if (node != null) {
                break;
            }

            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return node;
    }

    public synchronized RunningNode peekNode() {
        RunningNode node = null;

        while (this.alive) {
            node = this.bufferingNodes.peek();
            if (node != null) {
                break;
            }

            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return node;
    }

    public synchronized boolean hasNodes() {
        RunningNode node = this.bufferingNodes.peek();
        return node != null;
    }

    public synchronized void setListener(RunningManagerListener listener) {
        this.listener = listener;
    }

    @Override
    public synchronized void onNodeAdded(RunningNode node) {
        if (node != null) {
            boolean status = this.bufferingNodes.add(node);
            if (status) {
                this.notifyAll();
            }
        }

        if (this.listener != null) {
            this.listener.onNodeAdded(node);
        }
    }

    @Override
    public synchronized void onNodeRemoved(RunningNode node) {
        if (node != null) {
            this.bufferingNodes.remove(node);
        }

        if (this.listener != null) {
            this.listener.onNodeRemoved(node);
        }
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }
}
