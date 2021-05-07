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
import burai.project.property.ProjectGeometry;
import burai.project.property.ProjectGeometryList;
import burai.project.property.ProjectProperty;
import burai.project.property.ProjectStatus;

public class QEFXEnergyViewerController extends QEFXGraphViewerController {

    private boolean mdMode;

    private EnergyType energyType;

    private ProjectStatus projectStatus;

    private ProjectGeometryList projectGeometryList;

    public QEFXEnergyViewerController(QEFXProjectController projectController,
            ProjectProperty projectProperty, EnergyType energyType, boolean mdMode) {

        super(projectController, null);

        if (projectProperty == null) {
            throw new IllegalArgumentException("projectProperty is null.");
        }

        if (energyType == null) {
            throw new IllegalArgumentException("energyType is null.");
        }

        this.projectStatus = projectProperty.getStatus();

        if (mdMode) {
            this.projectGeometryList = projectProperty.getMdList();
        } else {
            this.projectGeometryList = projectProperty.getOptList();
        }

        this.mdMode = mdMode;
        this.energyType = energyType;
    }

    @Override
    protected int getCalculationID() {
        if (this.projectStatus == null) {
            return 0;
        }

        int offset = 0;
        if (this.projectGeometryList != null && !(this.projectGeometryList.isConverged())) {
            offset = 1;
        }

        if (this.mdMode) {
            return offset + this.projectStatus.getMdCount();
        } else {
            return offset + this.projectStatus.getOptCount();
        }
    }

    @Override
    protected GraphProperty createProperty() {
        GraphProperty property = new GraphProperty();
        SeriesProperty seriesProperty = new SeriesProperty();

        if (this.mdMode) {
            property.setTitle("Molecular Dynamics");
            property.setXLabel("Time / ps");

        } else {
            property.setTitle("Geometric Optimization");
            property.setXLabel("# Iterations");
        }

        if (EnergyType.TOTAL.equals(this.energyType)) {
            property.setYLabel("Total energy / Ry");
            seriesProperty.setName("Total energy");

        } else if (EnergyType.KINETIC.equals(this.energyType)) {
            property.setYLabel("Kinetic energy / Ry");
            seriesProperty.setName("Kinetic energy");

        } else if (EnergyType.CONSTANT.equals(this.energyType)) {
            property.setYLabel("Total energy + Kinetic energy / Ry");
            seriesProperty.setName("Total energy + Kinetic energy");

        } else if (EnergyType.TEMPERATURE.equals(this.energyType)) {
            property.setYLabel("Temperature / K");
            seriesProperty.setName("Temperature");
        }

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

        if (this.projectGeometryList == null) {
            lineChart.getData().clear();
            return;
        }

        ProjectGeometryList projectGeometryList = this.projectGeometryList.copyGeometryList();
        if (projectGeometryList == null) {
            lineChart.getData().clear();
            return;
        }

        int numConverged = 0;
        double lastValue = 0.0;

        Series<Number, Number> series = new Series<Number, Number>();

        for (int i = 0; i < projectGeometryList.numGeometries(); i++) {
            ProjectGeometry projectGeometry = projectGeometryList.getGeometry(i);
            boolean converged = projectGeometry == null ? false : projectGeometry.isConverged();

            double value = 0.0;
            if (EnergyType.TOTAL.equals(this.energyType)) {
                double totEnergy = projectGeometry == null ? 0.0 : projectGeometry.getEnergy();
                value = totEnergy;

            } else if (EnergyType.KINETIC.equals(this.energyType)) {
                double kinEnergy = projectGeometry == null ? 0.0 : projectGeometry.getKinetic();
                value = kinEnergy;

            } else if (EnergyType.CONSTANT.equals(this.energyType)) {
                double totEnergy = projectGeometry == null ? 0.0 : projectGeometry.getEnergy();
                double kinEnergy = projectGeometry == null ? 0.0 : projectGeometry.getKinetic();
                value = totEnergy + kinEnergy;

            } else if (EnergyType.TEMPERATURE.equals(this.energyType)) {
                double temp = projectGeometry == null ? 0.0 : projectGeometry.getTemperature();
                value = temp;
            }

            if (converged) {
                numConverged++;
                lastValue = value;

                if (this.mdMode) {
                    double time = projectGeometry == null ? 0.0 : projectGeometry.getTime();
                    series.getData().add(new Data<Number, Number>(time, value));

                } else {
                    series.getData().add(new Data<Number, Number>(i + 1, value));
                }
            }
        }

        lineChart.getData().clear();
        lineChart.getData().add(series);

        if (!this.mdMode) {
            int iteration = numConverged;
            String strIteration = iteration + " iteration" + (iteration > 1 ? "s were" : " was") + " done.";

            boolean converged = projectGeometryList.isConverged();
            String strConverged = "Optimization is " + (converged ? "" : "not ") + "converged.";

            String strEnergy = null;
            if (numConverged > 0) {
                if (EnergyType.TOTAL.equals(this.energyType)) {
                    strEnergy = "Total energy = " + String.format("%.8f", lastValue) + " Ry";

                } else if (EnergyType.KINETIC.equals(this.energyType)) {
                    strEnergy = "Kinetic energy = " + String.format("%.8f", lastValue) + " Ry";

                } else if (EnergyType.CONSTANT.equals(this.energyType)) {
                    strEnergy = "Total + Kinetic energy = " + String.format("%.8f", lastValue) + " Ry";

                } else if (EnergyType.TEMPERATURE.equals(this.energyType)) {
                    strEnergy = "Temperature = " + String.format("%.3f", lastValue) + " K";
                }
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
}
