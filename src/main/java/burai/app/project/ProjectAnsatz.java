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

package burai.app.project;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;

public class ProjectAnsatz {

    private List<Node> viewerPaneNodes;

    private Node viewerMenuGraphic;

    private Node editorPaneNode;

    private Node editorMenuGraphic;

    private boolean editorMenuDisable;

    private String editorMenuText;

    protected ProjectAnsatz() {
        this.viewerPaneNodes = null;
        this.viewerMenuGraphic = null;
        this.editorPaneNode = null;
        this.editorMenuGraphic = null;
        this.editorMenuDisable = false;
        this.editorMenuText = null;
    }

    protected List<Node> getViewerPaneNodes() {
        return this.viewerPaneNodes;
    }

    protected void setViewerPaneNodes(List<Node> viewerPaneNodes) {
        if (viewerPaneNodes == null || viewerPaneNodes.isEmpty()) {
            this.viewerPaneNodes = null;

        } else {
            this.viewerPaneNodes = new ArrayList<Node>();
            this.viewerPaneNodes.addAll(viewerPaneNodes);
        }
    }

    protected Node getViewerMenuGraphic() {
        return this.viewerMenuGraphic;
    }

    protected void setViewerMenuGraphic(Node viewerMenuGraphic) {
        this.viewerMenuGraphic = viewerMenuGraphic;
    }

    protected Node getEditorPaneNode() {
        return this.editorPaneNode;
    }

    protected void setEditorPaneNode(Node editorPaneNode) {
        this.editorPaneNode = editorPaneNode;
    }

    protected Node getEditorMenuGraphic() {
        return this.editorMenuGraphic;
    }

    protected void setEditorMenuGraphic(Node editorMenuGraphic) {
        this.editorMenuGraphic = editorMenuGraphic;
    }

    protected boolean isEditorMenuDisable() {
        return this.editorMenuDisable;
    }

    protected void setEditorMenuDisable(boolean editorMenuDisable) {
        this.editorMenuDisable = editorMenuDisable;
    }

    protected String getEditorMenuText() {
        return this.editorMenuText;
    }

    protected void setEditorMenuText(String editorMenuText) {
        this.editorMenuText = editorMenuText;
    }
}
