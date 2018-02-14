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

package burai.app.project.viewer.result.graph.tools;

import burai.app.project.viewer.result.graph.GraphProperty;
import burai.app.project.viewer.result.graph.SeriesProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Line;

public class QEFXGraphLegend {

    private static final double LINE_SIZE = 100.0;

    private static final String LEGEND_CLASS = "result-graph-legend";
    private static final String TEXT_CLASS = "result-graph-legend-text";

    private GraphProperty property;

    private GridPane basePane;

    public QEFXGraphLegend(GraphProperty property) {
        if (property == null) {
            throw new IllegalArgumentException("property is null.");
        }

        this.property = property;

        this.basePane = null;
        this.setupBasePane();
    }

    public Node getNode() {
        return this.basePane;
    }

    private void setupBasePane() {
        this.basePane = new GridPane();
        this.basePane.getStyleClass().add(LEGEND_CLASS);
    }

    public void reload() {
        this.basePane.getChildren().clear();

        String background = this.property.getBackground();
        background = background == null ? null : background.trim();
        if (background != null && (!background.isEmpty())) {
            if (this.basePane != null) {
                this.basePane.setStyle("-fx-background-color:" + background);
            }
        }

        int numSeries = this.property.numSeries();
        for (int iSeries = 0; iSeries < numSeries; iSeries++) {
            SeriesProperty seriesProperty = this.property.getSeries(iSeries);
            if (seriesProperty != null) {
                this.setLegendItem(iSeries, seriesProperty);
            }
        }
    }

    private void setLegendItem(int i, SeriesProperty seriesProperty) {
        if (seriesProperty == null) {
            return;
        }

        String name = seriesProperty.getName();
        if (name == null || name.isEmpty()) {
            name = "NO NAME";
        }

        String color = seriesProperty.getColor();
        if (color == null || color.isEmpty()) {
            color = "black";
        }

        double width = seriesProperty.getWidth();
        if (width <= 0.0) {
            width = 1.0;
        }

        int dash = seriesProperty.getDash();
        String strDash = SeriesProperty.getDashStyle(dash);
        if (strDash == null || strDash.isEmpty()) {
            strDash = SeriesProperty.getDashStyle(SeriesProperty.DASH_NULL);
        }

        String styleColor = "-fx-stroke: " + color + ";";
        String styleWidth = "-fx-stroke-width: " + width + "px;";
        String styleDash = "-fx-stroke-dash-array: " + strDash + ";";

        Line line = new Line(0.0, 0.0, LINE_SIZE, 0.0);
        line.setStyle(styleColor + styleWidth + styleDash);
        BorderPane.setAlignment(line, Pos.CENTER);
        BorderPane linePane = new BorderPane(line);
        this.basePane.add(linePane, 0, i);

        Label label = new Label(name);
        label.getStyleClass().add(TEXT_CLASS);
        BorderPane.setAlignment(label, Pos.CENTER);
        BorderPane labelPane = new BorderPane(label);
        this.basePane.add(labelPane, 1, i);
    }
}
