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

package burai.app.project.viewer.result.band;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import burai.app.project.QEFXProjectController;
import burai.app.project.viewer.result.graph.GraphProperty;
import burai.app.project.viewer.result.graph.QEFXGraphViewerController;
import burai.app.project.viewer.result.graph.SeriesProperty;
import burai.com.env.Environments;
import burai.com.parallel.Parallel;
import burai.project.property.BandData;
import burai.project.property.ProjectBand;
import burai.project.property.ProjectBandFactory;
import burai.project.property.ProjectBandPaths;
import burai.project.property.ProjectEnergies;
import burai.project.property.ProjectProperty;
import burai.project.property.ProjectStatus;

public class QEFXBandViewerController extends QEFXGraphViewerController {

    private static final int NUM_LOADING_THREADS = Math.max(1, Environments.getNumCUPs() - 1);

    private static final String XAXIS_CLASS = "invisible-axis";
    private static final String COORD_CLASS = "coord-field";
    private static final double COORD_WIDTH = 40.0;
    private static final double COORD_OFFSET = 4.2;

    private static final double DELTA_COORD = 1.0e-4;
    private static final double DELTA_ENERGY = 1.0e-4;
    private static final double VLINE_BUFFER = 1.0; // eV

    private ProjectStatus projectStatus;

    private ProjectEnergies projectEnergies;

    private ProjectBandFactory projectBandFactory;

    private ProjectBandPaths projectBandPaths;

    @FXML
    private AnchorPane coordPane;

    public QEFXBandViewerController(QEFXProjectController projectController, ProjectProperty projectProperty) {
        super(projectController, null);

        if (projectProperty == null) {
            throw new IllegalArgumentException("projectProperty is null.");
        }

        this.projectStatus = projectProperty.getStatus();
        this.projectEnergies = projectProperty.getFermiEnergies();
        this.projectBandFactory = projectProperty.getBandFactory();
        this.projectBandPaths = projectProperty.getBandPaths();
    }

    @Override
    protected int getCalculationID() {
        if (this.projectStatus == null) {
            return 0;
        }

        return this.projectStatus.getBandCount();
    }

    @Override
    protected GraphProperty createProperty() {
        GraphProperty property = new GraphProperty();

        property.setTitle("Band structure");
        property.setXLabel("");
        property.setYLabel("Energy / eV");

        ProjectEnergies projectEnergies = null;
        if (this.projectEnergies != null) {
            projectEnergies = this.projectEnergies.copyEnergies();
        }

        ProjectBandPaths projectBandPaths = null;
        if (this.projectBandPaths != null) {
            projectBandPaths = this.projectBandPaths.copyBandPaths();
        }

        ProjectBand projectBand = null;
        if (this.projectBandFactory != null) {
            projectBand = this.projectBandFactory.getProjectBand();
        }

        BandData bandData1 = null;
        if (projectBand != null) {
            bandData1 = projectBand.getBandData(true);
        }

        BandData bandData2 = null;
        if (projectBand != null) {
            bandData2 = projectBand.getBandData(false);
        }

        if (bandData1 != null) {
            this.createBandProperty(property, true);
        }

        if (bandData1 != null && bandData2 != null) {
            this.createBandProperty(property, false);
        }

        if (projectBandPaths != null && projectBandPaths.numPoints() > 0) {
            this.createCoordProperty(property, projectBandPaths);
        }

        if (projectEnergies != null && projectEnergies.numEnergies() > 0) {
            double fermi = projectEnergies.getEnergy(projectEnergies.numEnergies() - 1);
            this.createEnergyProperty(property, bandData1, bandData2, fermi);
        }

        return property;
    }

    private void createBandProperty(GraphProperty property, boolean spin) {
        if (property == null) {
            return;
        }

        SeriesProperty seriesProperty = new SeriesProperty();
        seriesProperty.setName(spin ? "up spin" : "down spin");
        seriesProperty.setColor(spin ? "blue" : "red");
        seriesProperty.setDash(spin ? SeriesProperty.DASH_NULL : SeriesProperty.DASH_SMALL);
        seriesProperty.setWithSymbol(false);
        seriesProperty.setWidth(1.0);
        property.addSeries(seriesProperty);
    }

    private void createCoordProperty(GraphProperty property, ProjectBandPaths projectBandPaths) {
        if (property == null) {
            return;
        }

        double[] coordRange = this.getCoordRange(projectBandPaths);
        if (coordRange == null || coordRange.length < 2) {
            return;
        }

        double minCoord = coordRange[0];
        double maxCoord = coordRange[1];

        if ((maxCoord - minCoord) >= DELTA_COORD) {
            property.setXAuto(false);
            property.setXLower(minCoord);
            property.setXUpper(maxCoord);
        }
    }

    private void createEnergyProperty(GraphProperty property, BandData bandData1, BandData bandData2, double fermi) {
        if (property == null) {
            return;
        }

        double[] energyRange = this.getEnergyRange(bandData1, bandData2, fermi);
        if (energyRange == null || energyRange.length < 2) {
            return;
        }

        double minEnergy = energyRange[0];
        double maxEnergy = energyRange[1];

        if ((maxEnergy - minEnergy) >= DELTA_ENERGY) {
            property.setYAuto(false);
            property.setYLower(minEnergy);
            property.setYUpper(maxEnergy);
            property.setYTick(1.0);
        }
    }

    @Override
    protected void initializeLineChart(LineChart<Number, Number> lineChart) {
        if (lineChart == null) {
            return;
        }

        Axis<Number> xAxis = lineChart.getXAxis();
        if (xAxis == null) {
            return;
        }

        xAxis.getStyleClass().add(XAXIS_CLASS);

        xAxis.widthProperty().addListener(o -> {
            ProjectBandPaths projectBandPaths = null;
            if (this.projectBandPaths != null) {
                projectBandPaths = this.projectBandPaths.copyBandPaths();
            }

            if (projectBandPaths != null && projectBandPaths.numPoints() > 0) {
                this.updateCoordPane(lineChart, projectBandPaths);
            }
        });

        xAxis.layoutXProperty().addListener(o -> {
            ProjectBandPaths projectBandPaths = null;
            if (this.projectBandPaths != null) {
                projectBandPaths = this.projectBandPaths.copyBandPaths();
            }

            if (projectBandPaths != null && projectBandPaths.numPoints() > 0) {
                this.updateCoordPane(lineChart, projectBandPaths);
            }
        });
    }

    @Override
    protected void reloadData(LineChart<Number, Number> lineChart) {
        if (lineChart == null) {
            return;
        }

        lineChart.getData().clear();

        ProjectEnergies projectEnergies = null;
        if (this.projectEnergies != null) {
            projectEnergies = this.projectEnergies.copyEnergies();
        }

        ProjectBandPaths projectBandPaths = null;
        if (this.projectBandPaths != null) {
            projectBandPaths = this.projectBandPaths.copyBandPaths();
        }

        ProjectBand projectBand = null;
        if (this.projectBandFactory != null) {
            projectBand = this.projectBandFactory.getProjectBand();
        }

        BandData bandData1 = null;
        if (projectBand != null) {
            bandData1 = projectBand.getBandData(true);
        }

        BandData bandData2 = null;
        if (projectBand != null) {
            bandData2 = projectBand.getBandData(false);
        }

        if (projectEnergies == null || projectEnergies.numEnergies() < 1) {
            return;
        }

        double fermi = projectEnergies.getEnergy(projectEnergies.numEnergies() - 1);

        if (bandData1 != null) {
            this.reloadBandData(lineChart, bandData1, bandData1, fermi);
        }

        if (bandData1 != null && bandData2 != null) {
            this.reloadBandData(lineChart, bandData1, bandData2, fermi);
        }

        if (projectBandPaths != null && projectBandPaths.numPoints() > 0) {
            this.reloadVLine(lineChart, projectBandPaths, bandData1, bandData2, fermi);
        }

        if (projectBandPaths != null && projectBandPaths.numPoints() > 0) {
            this.updateCoordPane(lineChart, projectBandPaths);
        }
    }

    private void updateCoordPane(LineChart<Number, Number> lineChart, ProjectBandPaths projectBandPaths) {
        if (this.coordPane == null) {
            return;
        }

        Axis<Number> xAxis = lineChart == null ? null : lineChart.getXAxis();
        if (xAxis == null) {
            return;
        }

        if (projectBandPaths == null || projectBandPaths.numPoints() < 1) {
            return;
        }

        double[] coordRange = this.getCoordRange(projectBandPaths);
        if (coordRange == null || coordRange.length < 2) {
            return;
        }

        double minCoord = coordRange[0];
        double maxCoord = coordRange[1];
        if ((maxCoord - minCoord) < DELTA_COORD) {
            return;
        }

        List<String> labList = new ArrayList<String>();
        List<Integer> mulList = new ArrayList<Integer>();
        List<Double> posList = new ArrayList<Double>();

        double minPos = xAxis.getLayoutX();
        double maxPos = xAxis.getLayoutX() + xAxis.getWidth();

        double coordOld = projectBandPaths.getCoordinate(0);
        String labelOld = null;
        int multOld = 0;

        for (int i = 0; i < projectBandPaths.numPoints(); i++) {
            double coord = 0.0;
            if ((i + 1) < projectBandPaths.numPoints()) {
                coord = projectBandPaths.getCoordinate(i + 1);
            } else {
                coord = projectBandPaths.getCoordinate(i) + Math.max(1.0, 2.0 * DELTA_COORD);
            }

            String label = projectBandPaths.getLabel(i);
            label = label == null ? "" : label;

            if (labelOld == null) {
                labelOld = label;
                multOld = 1;
            } else {
                labelOld = labelOld + " | " + label;
                multOld++;
            }

            if (Math.abs(coord - coordOld) >= DELTA_COORD) {
                labList.add(labelOld);
                mulList.add(multOld);
                posList.add(coordOld * (maxPos - minPos) / (maxCoord - minCoord) + minPos);
                coordOld = coord;
                labelOld = null;
                multOld = 0;
            }
        }

        int numList = labList.size();
        if (numList != mulList.size() || numList != posList.size()) {
            return;
        }

        this.coordPane.getChildren().clear();

        for (int i = 0; i < numList; i++) {
            String label = labList.get(i);
            if (label == null) {
                continue;
            }

            int mult = mulList.get(i);
            if (mult < 1) {
                return;
            }

            double position = posList.get(i);
            if (position < 0.0) {
                return;
            }

            TextField field = new TextField(label);
            field.getStyleClass().add(COORD_CLASS);
            field.setPrefWidth(mult * COORD_WIDTH);
            field.setLayoutX(position - 0.5 * mult * COORD_WIDTH + COORD_OFFSET);
            this.coordPane.getChildren().add(field);
        }
    }

    private void reloadBandData(
            LineChart<Number, Number> lineChart, BandData bandData1, BandData bandData2, double fermi) {

        if (lineChart == null) {
            return;
        }

        if (bandData1 == null || bandData2 == null) {
            return;
        }

        int numData = bandData1.numPoints();
        if (numData != bandData2.numPoints()) {
            return;
        }

        Series<Number, Number> series = new Series<Number, Number>();

        Platform.runLater(() -> {
            @SuppressWarnings("unchecked")
            Data<Number, Number>[] dataList = new Data[numData];

            Integer[] indexes = new Integer[numData];
            for (int i = 0; i < indexes.length; i++) {
                indexes[i] = i;
            }

            Parallel<Integer, Object> parallel = new Parallel<Integer, Object>(indexes);
            parallel.setNumThreads(NUM_LOADING_THREADS);
            parallel.forEach(i -> {
                double coord = bandData1.getCoordinate(i);
                double energy = bandData2.getEnergy(i) - fermi;
                Data<Number, Number> data = new Data<Number, Number>(coord, energy);
                synchronized (dataList) {
                    dataList[i] = data;
                }
                return null;
            });

            for (Data<Number, Number> data : dataList) {
                if (data != null) {
                    series.getData().add(data);
                }
            }
        });

        lineChart.getData().add(series);
    }

    private void reloadVLine(LineChart<Number, Number> lineChart,
            ProjectBandPaths projectBandPaths, BandData bandData1, BandData bandData2, double fermi) {

        if (lineChart == null) {
            return;
        }

        if (projectBandPaths == null || projectBandPaths.numPoints() < 1) {
            return;
        }

        double[] energyRange = this.getEnergyRange(bandData1, bandData2, fermi);
        if (energyRange == null || energyRange.length < 2) {
            return;
        }

        double minEnergy = energyRange[0];
        double maxEnergy = energyRange[1];
        if ((maxEnergy - minEnergy) < DELTA_ENERGY) {
            return;
        }

        double coordOld = -1.0;
        for (int i = 0; i < projectBandPaths.numPoints(); i++) {
            double coord = projectBandPaths.getCoordinate(i);
            if (Math.abs(coord - coordOld) < DELTA_COORD) {
                continue;
            }
            coordOld = coord;

            Data<Number, Number> data1 = new Data<Number, Number>(coord, minEnergy);
            Data<Number, Number> data2 = new Data<Number, Number>(coord, maxEnergy);
            Series<Number, Number> series = new Series<Number, Number>();
            series.getData().add(data1);
            series.getData().add(data2);
            lineChart.getData().add(series);
        }
    }

    private double[] getCoordRange(ProjectBandPaths projectBandPaths) {
        if (projectBandPaths == null || projectBandPaths.numPoints() < 1) {
            return null;
        }

        double minCoord = Double.MAX_VALUE;
        double maxCoord = 0.0;

        double coordOld = -1.0;
        for (int i = 0; i < projectBandPaths.numPoints(); i++) {
            double coord = projectBandPaths.getCoordinate(i);
            if (Math.abs(coord - coordOld) < DELTA_COORD) {
                continue;
            }
            coordOld = coord;

            minCoord = Math.min(minCoord, coord);
            maxCoord = Math.max(maxCoord, coord);
        }

        return new double[] { minCoord, maxCoord };
    }

    private double[] getEnergyRange(BandData bandData1, BandData bandData2, double fermi) {
        boolean empty1 = (bandData1 == null || bandData1.numPoints() < 1);
        boolean empty2 = (bandData2 == null || bandData2.numPoints() < 1);
        if (empty1 && empty2) {
            return null;
        }

        double minEnergy = +Double.MAX_VALUE;
        double maxEnergy = -Double.MAX_VALUE;

        if (!empty1) {
            for (int i = 0; i < bandData1.numPoints(); i++) {
                double energy = bandData1.getEnergy(i) - fermi;
                minEnergy = Math.min(minEnergy, energy);
                maxEnergy = Math.max(maxEnergy, energy);
            }
        }

        if (!empty2) {
            for (int i = 0; i < bandData2.numPoints(); i++) {
                double energy = bandData2.getEnergy(i) - fermi;
                minEnergy = Math.min(minEnergy, energy);
                maxEnergy = Math.max(maxEnergy, energy);
            }
        }

        minEnergy = Math.floor(minEnergy) - VLINE_BUFFER;
        maxEnergy = Math.ceil(maxEnergy) + VLINE_BUFFER;

        return new double[] { minEnergy, maxEnergy };
    }
}
