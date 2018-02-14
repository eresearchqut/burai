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

package burai.atoms.viewer.operation;

import java.util.ArrayList;
import java.util.List;

import javafx.event.Event;
import javafx.event.EventHandler;

public abstract class ViewerEventHandler<T extends Event> implements EventHandler<T> {

    protected ViewerEventManager manager;

    protected List<ViewerEventKernel<T>> kernels;

    public ViewerEventHandler() {
        this.manager = null;
        this.kernels = new ArrayList<ViewerEventKernel<T>>();
    }

    public ViewerEventHandler(ViewerEventManager manager) {
        this();

        if (manager == null) {
            throw new IllegalArgumentException("manager is null.");
        }

        this.manager = manager;
    }

    @Override
    public void handle(T event) {
        if (event == null) {
            return;
        }

        for (ViewerEventKernel<T> kernel : this.kernels) {
            if (kernel.isToPerform(this.manager)) {
                kernel.perform(this.manager, event);
                break;
            }
        }
    }

    protected void addKernel(ViewerEventKernel<T> kernel) {
        if (kernel == null) {
            return;
        }

        this.kernels.add(kernel);
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }

        @SuppressWarnings("unchecked")
        ViewerEventHandler<T> handler = (ViewerEventHandler<T>) obj;
        return this.manager == handler.manager;
    }
}
