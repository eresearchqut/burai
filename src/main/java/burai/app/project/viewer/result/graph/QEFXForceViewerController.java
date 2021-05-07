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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import burai.app.project.QEFXProjectController;
import burai.atoms.element.ElementUtil;
import burai.project.property.ProjectGeometry;
import burai.project.property.ProjectGeometryList;
import burai.project.property.ProjectProperty;
import burai.project.property.ProjectStatus;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.paint.Color;

public class QEFXForceViewerController extends QEFXGraphViewerController {

    private boolean mdMode;

    private String[] elements;

    private ProjectStatus projectStatus;

    private ProjectGeometryList projectGeometryList;

    public QEFXForceViewerController(
            QEFXProjectController projectController, ProjectProperty projectProperty, boolean mdMode) {

        super(projectController, Pos.BOTTOM_RIGHT);

        if (projectProperty == null) {
            throw new IllegalArgumentException("projectProperty is null.");
        }

        this.projectStatus = projectProperty.getStatus();

        if (mdMode) {
            this.projectGeometryList = projectProperty.getMdList();
        } else {
            this.projectGeometryList = projectProperty.getOptList();
        }

        this.mdMode = mdMode;

        this.elements = null;
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

    private void createElements() {
        if (this.elements != null && this.elements.length > 0) {
            return;
        }

        ProjectGeometry projectGeometry = null;
        if (this.projectGeometryList != null && this.projectGeometryList.numGeometries() > 0) {
            projectGeometry = this.projectGeometryList.getGeometry(0);
        }

        if (projectGeometry != null) {
            Set<String> elementSet = new LinkedHashSet<String>();
            int numAtoms = projectGeometry.numAtoms();
            for (int iAtom = 0; iAtom < numAtoms; iAtom++) {
                String name = projectGeometry.getName(iAtom);
                if (name != null) {
                    elementSet.add(name);
                }
            }

            this.elements = elementSet.toArray(new String[elementSet.size()]);
        }
    }

    @Override
    protected GraphProperty createProperty() {

        this.elements = null;
        this.createElements();

        GraphProperty property = new GraphProperty();

        if (this.mdMode) {
            property.setTitle("Molecular Dynamics");
            property.setXLabel("Time / ps");

        } else {
            property.setTitle("Geometric Optimization");
            property.setXLabel("# Iterations");
        }

        property.setYLabel("Force on atom / (Ry/Bohr)");

        SeriesProperty seriesProperty = null;
        seriesProperty = new SeriesProperty();
        seriesProperty.setName("Total force");
        seriesProperty.setColor("black");
        seriesProperty.setDash(SeriesProperty.DASH_LARGE);
        seriesProperty.setWithSymbol(true);
        seriesProperty.setWidth(2.0);
        property.addSeries(seriesProperty);

        if (this.elements != null) {
            for (int iElem = 0; iElem < this.elements.length; iElem++) {
                String element = this.elements[iElem];
                if (element == null) {
                    element = "X";
                }

                Color refColor = Color.color(
                        0.75 * Color.LIGHTGRAY.getRed(),
                        0.75 * Color.LIGHTGRAY.getGreen(),
                        0.75 * Color.LIGHTGRAY.getBlue());

                Color color = element == null ? null : ElementUtil.getColor(element, refColor);
                String strColor = color == null ? null : color.toString();
                strColor = strColor == null ? "black" : strColor.replaceAll("0x", "#");

                seriesProperty = new SeriesProperty();
                seriesProperty.setName(element);
                seriesProperty.setColor(strColor);
                seriesProperty.setDash(SeriesProperty.DASH_NULL);
                seriesProperty.setWithSymbol(true);
                seriesProperty.setWidth(1.5);
                property.addSeries(seriesProperty);
            }
        }

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

        this.createElements();

        int numConverged = 0;
        double lastForce = 0.0;

        List<Series<Number, Number>> seriesList = new ArrayList<Series<Number, Number>>();
        seriesList.add(new Series<Number, Number>());
        if (this.elements != null) {
            for (int iElem = 0; iElem < this.elements.length; iElem++) {
                seriesList.add(new Series<Number, Number>());
            }
        }

        for (int i = 0; i < projectGeometryList.numGeometries(); i++) {
            ProjectGeometry projectGeometry = projectGeometryList.getGeometry(i);
            boolean converged = projectGeometry == null ? false : projectGeometry.isConverged();
            double totalForce = projectGeometry == null ? 0.0 : projectGeometry.getTotalForce();

            if (converged) {
                numConverged++;
                lastForce = totalForce;

                Number xValue = null;
                if (this.mdMode) {
                    double time = projectGeometry == null ? 0.0 : projectGeometry.getTime();
                    xValue = time;
                } else {
                    xValue = (i + 1);
                }

                seriesList.get(0).getData().add(new Data<Number, Number>(xValue, totalForce));

                if (projectGeometry != null) {
                    double[] forces = null;
                    if (this.elements != null) {
                        forces = new double[this.elements.length];
                        for (int iElem = 0; iElem < this.elements.length; iElem++) {
                            forces[iElem] = 0.0;
                        }
                    }

                    int numAtoms = projectGeometry.numAtoms();
                    for (int iAtom = 0; iAtom < numAtoms; iAtom++) {
                        String name = projectGeometry.getName(iAtom);
                        double xForce = projectGeometry.getForceX(iAtom);
                        double yForce = projectGeometry.getForceY(iAtom);
                        double zForce = projectGeometry.getForceZ(iAtom);
                        double force = xForce * xForce + yForce * yForce + zForce * zForce;
                        if (this.elements != null) {
                            for (int iElem = 0; iElem < this.elements.length; iElem++) {
                                String element = elements[iElem];
                                if (element != null && element.equals(name)) {
                                    forces[iElem] += force;
                                    break;
                                }
                            }
                        }
                    }

                    if (this.elements != null) {
                        for (int iElem = 0; iElem < this.elements.length; iElem++) {
                            double force = Math.sqrt(forces[iElem]);
                            seriesList.get(iElem + 1).getData().add(new Data<Number, Number>(xValue, force));
                        }
                    }
                }
            }
        }

        lineChart.getData().clear();
        lineChart.getData().addAll(seriesList);

        if (!this.mdMode) {
            int iteration = numConverged;
            String strIteration = iteration + " iteration" + (iteration > 1 ? "s were" : " was") + " done.";

            boolean converged = projectGeometryList.isConverged();
            String strConverged = "Optimization is " + (converged ? "" : "not ") + "converged.";

            String strForce = null;
            if (numConverged > 0) {
                strForce = "Total force = " + String.format("%.6f", lastForce) + " Ry/Bohr";
            }

            Node note = null;
            if (strForce != null) {
                note = this.getNote(strIteration, strConverged, strForce);
            } else {
                note = this.getNote(strIteration, strConverged);
            }

            if (note != null) {
                this.stackNode(note, Pos.TOP_RIGHT);
            }
        }
    }
}
