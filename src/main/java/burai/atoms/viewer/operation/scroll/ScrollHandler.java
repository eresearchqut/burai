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

package burai.atoms.viewer.operation.scroll;

import javafx.scene.input.ScrollEvent;
import burai.atoms.viewer.operation.ViewerEventHandler;
import burai.atoms.viewer.operation.ViewerEventManager;

public class ScrollHandler extends ViewerEventHandler<ScrollEvent> {

    public ScrollHandler(ViewerEventManager manager, boolean silent) {
        super(manager);

        if (!silent) {
            this.addKernel(new ScrollCompassPicking());
            this.addKernel(new ScrollCompass());
            this.addKernel(new ScrollEditorMenu());
            this.addKernel(new ScrollScope());
        }

        this.addKernel(new ScrollRegular());
    }

    @Override
    public void handle(ScrollEvent event) {
        if (event == null) {
            return;
        }

        event.consume();

        super.handle(event);
    }
}
