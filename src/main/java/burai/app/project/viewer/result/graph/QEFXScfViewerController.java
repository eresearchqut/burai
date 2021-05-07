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

package burai.app.project.viewer.result.graph;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import burai.app.project.QEFXProjectController;
import burai.project.property.ProjectEnergies;
import burai.project.property.ProjectProperty;
import burai.project.property.ProjectStatus;

public class QEFXScfViewerController extends QEFXGraphViewerController {

    private ProjectStatus projectStatus;

    private ProjectEnergies projectEnergies;

    public QEFXScfViewerController(QEFXProjectController projectController, ProjectProperty projectProperty) {
        super(projectController, null);

        if (projectProperty == null) {
            throw new IllegalArgumentException("projectProperty is null.");
        }

        this.projectStatus = projectProperty.getStatus();
        this.projectEnergies = projectProperty.getScfEnergies();
    }

    @Override
    protected int getCalculationID() {
        if (this.projectStatus == null) {
            return 0;
        }

        int offset = 0;
        if (this.projectEnergies != null && !(this.projectEnergies.isConverged())) {
            offset = 1;
        }

        return offset + this.projectStatus.getScfCount();
    }

    @Override
    protected GraphProperty createProperty() {
        GraphProperty property = new GraphProperty();

        property.setTitle("Convergence of SCF");
        property.setXLabel("# Iterations");
        property.setYLabel("Total energy / Ry");

        SeriesProperty seriesProperty = new SeriesProperty();
        seriesProperty.setName("Total energy");
        seriesProperty.setColor("dodgerblue");
        seriesProperty.setDash(SeriesProperty.DASH_NULL);
        seriesProperty.setWithSymbol(true);
        seriesProperty.setWidth(2.0);
        property.addSeries(seriesProperty);

        return property;
    }

    @Override
    protected void reloadData(LineChart<Number, Number> lineChart) {
        if (lineChart == null) {
            return;
        }

        if (this.projectEnergies == null) {
            lineChart.getData().clear();
            return;
        }

        ProjectEnergies projectEnergies = this.projectEnergies.copyEnergies();
        if (projectEnergies == null) {
            lineChart.getData().clear();
            return;
        }

        Series<Number, Number> series = new Series<Number, Number>();
        for (int i = 0; i < projectEnergies.numEnergies(); i++) {
            series.getData().add(new Data<Number, Number>(i + 1, projectEnergies.getEnergy(i)));
        }

        lineChart.getData().clear();
        lineChart.getData().add(series);

        int iteration = projectEnergies.numEnergies();
        String strIteration = iteration + " iteration" + (iteration > 1 ? "s were" : " was") + " done.";

        boolean converged = projectEnergies.isConverged();
        String strConverged = "SCF is " + (converged ? "" : "not ") + "converged.";

        String strEnergy = null;
        if (projectEnergies.numEnergies() > 0) {
            double energy = projectEnergies.getEnergy(projectEnergies.numEnergies() - 1);
            strEnergy = "Total energy = " + String.format("%.8f", energy) + " Ry";
        }

        Node note = null;
        if (strEnergy != null) {
            note = this.getNote(strIteration, strConverged, strEnergy);
        } else {
            note = this.getNote(strIteration, strConverged);
        }

        if (note != null) {
            this.stackNode(note, Pos.TOP_RIGHT);
        }
    }
}
