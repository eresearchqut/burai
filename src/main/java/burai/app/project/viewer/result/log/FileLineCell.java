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

package burai.app.project.viewer.result.log;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public class FileLineCell extends ListCell<FileLine> {

    private static final double INDEX_WIDTH = 75.0;
    private static final String INDEX_CLASS = "result-log-index";
    private static final String TEXT_CLASS = "result-log-text";
    private static final String SEARCHED_CLASS = "result-log-searched";

    public FileLineCell() {
        // NOP
    }

    @Override
    protected void updateItem(FileLine fileLine, boolean empty) {
        super.updateItem(fileLine, empty);

        if (empty || fileLine == null) {
            this.setGraphic(null);
            while (this.getStyleClass().remove(SEARCHED_CLASS)) {
            }
            return;
        }

        Label indexLabel = new Label(Integer.toString(fileLine.getIndex()));
        indexLabel.getStyleClass().add(INDEX_CLASS);
        Pane indexPane = new Pane(indexLabel);
        indexPane.setPrefWidth(INDEX_WIDTH);

        Label lineLabel = new Label(fileLine.getLine());
        lineLabel.getStyleClass().add(TEXT_CLASS);
        Pane linePane = new Pane(lineLabel);

        BorderPane borderPane = new BorderPane();
        borderPane.setLeft(indexPane);
        borderPane.setCenter(linePane);

        if (fileLine.isEnhanced()) {
            if (!this.getStyleClass().contains(SEARCHED_CLASS)) {
                this.getStyleClass().add(SEARCHED_CLASS);
            }

        } else {
            while (this.getStyleClass().remove(SEARCHED_CLASS)) {
            }
        }

        this.setGraphic(borderPane);
    }
}
