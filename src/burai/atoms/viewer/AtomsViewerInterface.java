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

package burai.atoms.viewer;

import burai.atoms.model.Cell;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

public abstract class AtomsViewerInterface extends Group {

    protected AtomsViewerInterface() {
        super();
    }

    public abstract Cell getCell();

    public abstract double getSceneWidth();

    public abstract double getSceneHeight();

    public abstract void setSceneStyle(String style);

    public abstract void addExclusiveNode(Node node);

    public abstract void addExclusiveNode(NodeWrapper nodeWrapper);

    public abstract void startExclusiveMode();

    public abstract void stopExclusiveMode();

    public abstract void bindSceneTo(Pane pane);

    public abstract void unbindScene();

}
