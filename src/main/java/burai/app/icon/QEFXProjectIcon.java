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

package burai.app.icon;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import burai.atoms.element.ElementUtil;
import burai.atoms.model.Atom;
import burai.atoms.model.Cell;
import burai.atoms.model.event.AtomEvent;
import burai.atoms.model.event.AtomEventListener;
import burai.atoms.model.event.CellEvent;
import burai.atoms.model.event.CellEventListener;
import burai.atoms.model.event.ModelEvent;
import burai.atoms.vlight.AtomsVLight;
import burai.com.graphic.svg.SVGLibrary;
import burai.com.graphic.svg.SVGLibrary.SVGData;
import burai.project.Project;

public class QEFXProjectIcon extends QEFXIconBase<Project> implements AtomEventListener, CellEventListener {

    private static final String FIGURE_CLASS = "icon-atoms";
    private static final double FIGURE_FONT_SIZE1 = 0.32;
    private static final double FIGURE_FONT_SIZE2 = 0.20;
    private static final int FIGURE_FONT_WRAP = 3;

    private static final int BUFFER_LENGTH = 128;
    private static final String[] BUFFER_ELEMENTS = new String[BUFFER_LENGTH];
    private static final int[] BUFFER_MULTS = new int[BUFFER_LENGTH];

    private static final int MAX_ATOMS = 64;

    private double atomsSize;

    private Group atomsBase;

    private AtomsVLight atomsVLight;

    private boolean isLightFigure;

    private boolean toBeFlushed;

    public QEFXProjectIcon(Project project) {
        super(project);

        this.atomsSize = -1.0;
        this.atomsBase = null;
        this.atomsVLight = null;
        this.isLightFigure = false;
        this.toBeFlushed = false;

        this.setupCellAndAtoms();
    }

    private void setupCellAndAtoms() {
        Cell cell = this.content.getCell();
        if (cell != null) {
            cell.addListener(this);
            Atom[] atoms = cell.listAtoms(true);
            if (atoms != null) {
                for (Atom atom : atoms) {
                    if (atom != null) {
                        atom.addListener(this);
                    }
                }
            }
        }
    }

    private void detachAtomsVLight() {
        if (this.atomsVLight == null) {
            return;
        }

        AtomsVLight atomsVLight2 = this.atomsVLight;
        Platform.runLater(() -> {
            atomsVLight2.detachFromCell();
        });

        this.atomsVLight = null;
    }

    @Override
    public void detach() {
        this.detachAtomsVLight();

        this.toBeFlushed = true;
        Cell cell = this.content.getCell();
        if (cell != null) {
            cell.flushListeners();
        }

        this.content = null;
    }

    @Override
    public Node getFigure(double size) {
        Node atomsFigure = this.getAtomsFigure(size);

        if (atomsFigure == null) {
            return this.getErrorFigure(size);
        }

        if (this.atomsBase == null) {
            this.atomsBase = new Group(atomsFigure);
        }

        return this.atomsBase;
    }

    private Node getAtomsFigure(double size) {
        this.atomsSize = size;

        Cell cell = this.content.getCell();
        if (cell == null) {
            return null;
        }

        if (size <= 0.0) {
            this.detachAtomsVLight();
            return null;
        }

        if (this.isLightFigure || (cell.numAtoms(true) > MAX_ATOMS)) {
            this.isLightFigure = true;
            this.detachAtomsVLight();
            return this.getLightFigure(size);
        }

        if (this.atomsVLight == null || this.atomsVLight.getSize() != size) {
            this.detachAtomsVLight();
            this.atomsVLight = new AtomsVLight(cell, size);
        }

        return this.atomsVLight;
    }

    private Node getLightFigure(double size) {
        if (size <= 0.0) {
            return null;
        }

        Cell cell = this.content.getCell();
        if (cell == null) {
            return null;
        }

        double scaledSize = size * ICON_SCALE;
        double insetsSize = 0.5 * (size - scaledSize);
        StackPane stackPane = new StackPane();

        Node figure = SVGLibrary.getGraphic(SVGData.CRYSTAL, scaledSize, null, FIGURE_CLASS);
        StackPane.setAlignment(figure, Pos.CENTER);
        stackPane.getChildren().add(figure);

        Node title = this.getLightFigureTitle(size, cell);
        StackPane.setAlignment(title, Pos.CENTER);
        stackPane.getChildren().add(title);

        BorderPane.setMargin(stackPane, new Insets(insetsSize));
        BorderPane borderPane = new BorderPane();
        borderPane.setPrefSize(size, size);
        borderPane.setCenter(stackPane);

        return borderPane;
    }

    private Node getLightFigureTitle(double size, Cell cell) {
        VBox vbox = new VBox();

        int numElements = this.countElements(cell, BUFFER_ELEMENTS, BUFFER_MULTS);

        for (int i = 0; i < numElements; i += FIGURE_FONT_WRAP) {

            HBox hbox = new HBox();
            for (int j = i; j < Math.min(i + FIGURE_FONT_WRAP, numElements); j++) {
                Label label1 = new Label();
                label1.getStyleClass().add(FIGURE_CLASS);
                label1.setStyle("-fx-font-size: " + (size * FIGURE_FONT_SIZE1 / FIGURE_FONT_WRAP));
                label1.setText(BUFFER_ELEMENTS[j]);

                Label label2 = new Label();
                label2.getStyleClass().add(FIGURE_CLASS);
                label2.setStyle("-fx-font-size: " + (size * FIGURE_FONT_SIZE2 / FIGURE_FONT_WRAP));
                label2.setText(Integer.toString(BUFFER_MULTS[j]));

                BorderPane borderPane = new BorderPane();
                BorderPane.setAlignment(label1, Pos.CENTER);
                BorderPane.setAlignment(label2, Pos.BOTTOM_LEFT);
                borderPane.setCenter(label1);
                borderPane.setRight(label2);
                hbox.getChildren().add(borderPane);
            }

            vbox.getChildren().add(hbox);
        }

        return new Group(vbox);
    }

    private Node getErrorFigure(double size) {
        if (size <= 0.0) {
            return null;
        }

        double scaledSize = size * ICON_SCALE;
        double insetsSize = 0.5 * (size - scaledSize);
        Node figure = SVGLibrary.getGraphic(SVGData.ERROR, scaledSize, null, "icon-error");
        BorderPane.setMargin(figure, new Insets(insetsSize));
        return figure;
    }

    @Override
    protected String initCaption() {
        String caption = this.content.getRootFileName();
        if (caption == null) {
            caption = this.content.getDirectoryName();
        }

        if (caption == null) {
            caption = "No Name";
        }

        return caption;
    }

    @Override
    protected String initSubCaption() {
        String caption = null;
        String path = this.content.getRelatedFilePath();
        if (path != null && (!path.trim().isEmpty())) {
            caption = this.getFileDetail(path);
        }

        Cell cell = this.content.getCell();

        if (cell != null) {
            String cellDetail = this.getCellDetail(cell);
            if (cellDetail != null && (!cellDetail.trim().isEmpty())) {
                if (caption != null) {
                    caption = caption + System.lineSeparator();
                } else {
                    caption = "";
                }
                caption = caption + cellDetail;
            }

        } else {
            if (caption != null) {
                caption = caption + System.lineSeparator();
            } else {
                caption = "";
            }
            caption = caption + "ERROR: cannot read file.";
        }

        return caption;
    }

    private String getCellDetail(Cell cell) {
        if (cell == null) {
            return null;
        }

        String caption = null;

        String captionLattice = this.getLatticeDetail(cell);
        if (captionLattice != null && (!captionLattice.trim().isEmpty())) {
            caption = captionLattice;
        }

        String captionAtoms = this.getAtomsDetail(cell);
        if (captionAtoms != null && (!captionAtoms.trim().isEmpty())) {
            if (caption != null) {
                caption = caption + System.lineSeparator();
            } else {
                caption = "";
            }
            caption = caption + captionAtoms;
        }

        return caption;
    }

    private String getLatticeDetail(Cell cell) {
        if (cell == null) {
            return null;
        }

        String caption = null;

        double a = cell.getA();
        double b = cell.getB();
        double c = cell.getC();
        double alpha = cell.getAlpha();
        double beta = cell.getBeta();
        double gamma = cell.getGamma();

        final String formatLength = "%6.3f";
        final String formatAngle = "%6.2f";
        caption = "Lattice: ";
        caption = caption + "a =" + String.format(formatLength, a) + ", ";
        caption = caption + "b =" + String.format(formatLength, b) + ", ";
        caption = caption + "c =" + String.format(formatLength, c) + ", ";
        caption = caption + "alpha =" + String.format(formatAngle, alpha) + ", ";
        caption = caption + "beta =" + String.format(formatAngle, beta) + ", ";
        caption = caption + "gamma =" + String.format(formatAngle, gamma);

        return caption;
    }

    private String getAtomsDetail(Cell cell) {
        if (cell == null) {
            return null;
        }

        int numElements = this.countElements(cell, BUFFER_ELEMENTS, BUFFER_MULTS);

        String formula = "";
        for (int i = 0; i < numElements; i++) {
            String element = BUFFER_ELEMENTS[i];
            formula = formula + " " + element;

            int mult = BUFFER_MULTS[i];
            if (mult > 1) {
                formula = formula + mult;
            }
        }

        return "Formula:" + formula;
    }

    private int countElements(Cell cell, String[] elements, int[] mults) {
        if (cell == null || elements == null || mults == null) {
            return 0;
        }

        Atom[] atoms = cell.listAtoms(true);
        if (atoms != null && atoms.length > 0) {
            Map<String, Integer> elemMap = new HashMap<String, Integer>();
            for (Atom atom : atoms) {
                String elemName = ElementUtil.toElementName(atom.getName());
                if (elemName == null || elemName.isEmpty()) {
                    continue;
                }
                int count = 1;
                if (elemMap.containsKey(elemName)) {
                    count = elemMap.get(elemName) + 1;
                }
                elemMap.put(elemName, count);
            }

            if (elemMap.isEmpty()) {
                return 0;
            }

            String[] elemNames = elemMap.keySet().toArray(new String[elemMap.size()]);
            Arrays.sort(elemNames, (elemName1, elemName2) -> {
                double elecNeg1 = ElementUtil.getElectronegativity(elemName1);
                double elecNeg2 = ElementUtil.getElectronegativity(elemName2);
                if (elecNeg1 < elecNeg2) {
                    return -1;
                }
                if (elecNeg1 > elecNeg2) {
                    return 1;
                }
                return 0;
            });

            for (int i = 0; i < elemNames.length; i++) {
                String elemName = elemNames[i];
                int count = elemMap.get(elemName);

                if (i < elements.length) {
                    elements[i] = elemName;
                }
                if (i < mults.length) {
                    mults[i] = count;
                }
            }

            return elemNames.length;
        }

        return 0;
    }

    @Override
    public boolean isToBeFlushed() {
        return this.toBeFlushed;
    }

    @Override
    public void onModelDisplayed(ModelEvent event) {
        // NOP
    }

    @Override
    public void onModelNotDisplayed(ModelEvent event) {
        // NOP
    }

    @Override
    public void onLatticeMoved(CellEvent event) {
        StringProperty caption = this.subCaptionProperty();
        caption.set(this.initSubCaption());
    }

    @Override
    public void onAtomAdded(CellEvent event) {
        Atom atom = event.getAtom();
        if (atom == null || atom.isSlaveAtom()) {
            return;
        }
        atom.addListener(this);

        StringProperty caption = this.subCaptionProperty();
        caption.set(this.initSubCaption());

        if (this.atomsBase != null) {
            Node atomsFigure = this.getAtomsFigure(this.atomsSize);
            if (atomsFigure != null) {
                this.atomsBase.getChildren().clear();
                this.atomsBase.getChildren().add(atomsFigure);
            }
        }
    }

    @Override
    public void onAtomRemoved(CellEvent event) {
        Atom atom = event.getAtom();
        if (atom == null || atom.isSlaveAtom()) {
            return;
        }

        StringProperty caption = this.subCaptionProperty();
        caption.set(this.initSubCaption());

        if (this.atomsBase != null) {
            Node atomsFigure = this.getAtomsFigure(this.atomsSize);
            if (atomsFigure != null) {
                this.atomsBase.getChildren().clear();
                this.atomsBase.getChildren().add(atomsFigure);
            }
        }
    }

    @Override
    public void onBondAdded(CellEvent event) {
        // NOP
    }

    @Override
    public void onBondRemoved(CellEvent event) {
        // NOP
    }

    @Override
    public void onAtomRenamed(AtomEvent event) {
        Object source = event.getSource();
        if (source != null && (source instanceof Atom)) {
            if (((Atom) source).isSlaveAtom()) {
                return;
            }
        }

        StringProperty caption = this.subCaptionProperty();
        caption.set(this.initSubCaption());

        if (this.atomsBase != null) {
            Node atomsFigure = this.getAtomsFigure(this.atomsSize);
            if (atomsFigure != null) {
                this.atomsBase.getChildren().clear();
                this.atomsBase.getChildren().add(atomsFigure);
            }
        }
    }

    @Override
    public void onAtomMoved(AtomEvent event) {
        // NOP
    }
}
