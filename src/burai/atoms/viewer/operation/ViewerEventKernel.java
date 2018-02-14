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

import javafx.event.Event;

public interface ViewerEventKernel<T extends Event> {

    public static final double KEY_SCALE_SPEED = 1.1;

    public static final double KEY_ROTATE_SPEED = 10.0;

    public static final double KEY_TRANS_SPEED = 10.0;

    public static final double MOUSE_SCALE_SPEED = 0.01;

    public static final double MOUSE_ROTATE_SPEED = 0.50;

    public static final double MOUSE_TRANS_SPEED = 1.50;

    public static final double SCROLL_SCALE_SPEED = 0.002;

    public abstract boolean isToPerform(ViewerEventManager manager);

    public abstract void perform(ViewerEventManager manager, T event);

}
