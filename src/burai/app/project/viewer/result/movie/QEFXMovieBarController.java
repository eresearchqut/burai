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

package burai.app.project.viewer.result.movie;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import burai.app.QEFXAppController;
import burai.app.project.QEFXProjectController;
import burai.atoms.viewer.AtomsViewerInterface;
import burai.com.graphic.svg.SVGLibrary;
import burai.com.graphic.svg.SVGLibrary.SVGData;

public class QEFXMovieBarController extends QEFXAppController {

    private static final double INSETS_SIZE = 4.0;

    private static final double GRAPHIC_SIZE = 20.0;
    private static final String GRAPHIC_CLASS = "piclight-button";
    private static final String GRAPHIC_CLASS_PLAY = "picplay-button";
    private static final String GRAPHIC_CLASS_PAUSE = "picpause-button";

    private static final double TIME_PER_GEOM = 250.0;

    private QEFXProjectController projectController;

    private QEFXMovieViewerController viewerController;

    @FXML
    private BorderPane basePane;

    @FXML
    private Button playButton;

    @FXML
    private Button nextButton;

    @FXML
    private Button prevButton;

    @FXML
    private Button firstButton;

    @FXML
    private Button lastButton;

    @FXML
    private Slider movieSlider;

    private boolean movieSliderBusy;

    private Timeline movieTimeline;

    public QEFXMovieBarController(QEFXProjectController projectController, QEFXMovieViewerController viewerController) {
        super(projectController == null ? null : projectController.getMainController());

        if (projectController == null) {
            throw new IllegalArgumentException("projectController is null.");
        }

        if (viewerController == null) {
            throw new IllegalArgumentException("viewerController is null.");
        }

        this.projectController = projectController;
        this.viewerController = viewerController;

        this.movieSliderBusy = false;

        this.movieTimeline = null;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setupBasePane();
        this.setupPlayButton();
        this.setupNextButton();
        this.setupPrevButton();
        this.setupFirstButton();
        this.setupLastButton();
        this.setupMovieSlider();
    }

    private void setupBasePane() {
        if (this.basePane == null) {
            return;
        }

        StackPane.setMargin(this.basePane, new Insets(INSETS_SIZE));
        StackPane.setAlignment(this.basePane, Pos.BOTTOM_CENTER);
    }

    private void setupPlayButton() {
        if (this.playButton == null) {
            return;
        }

        this.playButton.setText("");
        this.updatePlayButton();

        this.playButton.setOnAction(event -> {
            if (this.movieTimeline == null) {
                this.startMovie();
            } else {
                this.stopMovie();
            }

            this.updatePlayButton();
        });
    }

    private void updatePlayButton() {
        if (this.playButton == null) {
            return;
        }

        if (this.movieTimeline == null) {
            this.playButton.setTooltip(new Tooltip("play"));
            this.playButton.setGraphic(
                    SVGLibrary.getGraphic(SVGData.MOVIE_PLAY, GRAPHIC_SIZE, null, GRAPHIC_CLASS_PLAY));

        } else {
            this.playButton.setTooltip(new Tooltip("pause"));
            this.playButton.setGraphic(
                    SVGLibrary.getGraphic(SVGData.MOVIE_PAUSE, GRAPHIC_SIZE, null, GRAPHIC_CLASS_PAUSE));
        }
    }

    private void setupNextButton() {
        if (this.nextButton == null) {
            return;
        }

        this.nextButton.setText("");
        this.nextButton.setTooltip(new Tooltip("next"));
        this.nextButton.setGraphic(
                SVGLibrary.getGraphic(SVGData.MOVIE_NEXT, GRAPHIC_SIZE, null, GRAPHIC_CLASS));

        this.nextButton.setOnAction(event -> {
            if (this.movieTimeline == null) {
                this.viewerController.showNextGeometry();
            }
        });
    }

    private void setupPrevButton() {
        if (this.prevButton == null) {
            return;
        }

        this.prevButton.setText("");
        this.prevButton.setTooltip(new Tooltip("previous"));
        this.prevButton.setGraphic(
                SVGLibrary.getGraphic(SVGData.MOVIE_PREVIOUS, GRAPHIC_SIZE, null, GRAPHIC_CLASS));

        this.prevButton.setOnAction(event -> {
            if (this.movieTimeline == null) {
                this.viewerController.showPreviousGeometry();
            }
        });
    }

    private void setupFirstButton() {
        if (this.firstButton == null) {
            return;
        }

        this.firstButton.setText("");
        this.firstButton.setTooltip(new Tooltip("first"));
        this.firstButton.setGraphic(
                SVGLibrary.getGraphic(SVGData.MOVIE_FIRST, GRAPHIC_SIZE, null, GRAPHIC_CLASS));

        this.firstButton.setOnAction(event -> {
            if (this.movieTimeline == null) {
                this.viewerController.showFirstGeometry();
            }
        });
    }

    private void setupLastButton() {
        if (this.lastButton == null) {
            return;
        }

        this.lastButton.setText("");
        this.lastButton.setTooltip(new Tooltip("last"));
        this.lastButton.setGraphic(
                SVGLibrary.getGraphic(SVGData.MOVIE_LAST, GRAPHIC_SIZE, null, GRAPHIC_CLASS));

        this.lastButton.setOnAction(event -> {
            if (this.movieTimeline == null) {
                this.viewerController.showLastGeometry();
            }
        });
    }

    private void setupMovieSlider() {
        if (this.movieSlider == null) {
            return;
        }

        this.movieSlider.valueProperty().addListener(o -> {
            this.movieSliderBusy = true;

            double value = this.movieSlider.getValue();
            this.viewerController.showGeometry(value);

            this.movieSliderBusy = false;
        });
    }

    protected void disableNextButtons(boolean disable) {
        if (this.nextButton != null) {
            this.nextButton.setDisable(disable);
        }

        if (this.lastButton != null) {
            this.lastButton.setDisable(disable);
        }
    }

    protected void disablePreviousButtons(boolean disable) {
        if (this.prevButton != null) {
            this.prevButton.setDisable(disable);
        }

        if (this.firstButton != null) {
            this.firstButton.setDisable(disable);
        }
    }

    protected void setSliderValue(double value) {
        if (this.movieSliderBusy) {
            return;
        }

        if (this.movieSlider != null) {
            double value_ = Math.min(Math.max(0.0, value), 1.0);
            this.movieSlider.setValue(value_);
        }
    }

    private void startMovie() {
        if (this.movieSlider == null) {
            return;
        }

        if (this.movieTimeline != null) {
            return;
        }

        this.viewerController.freezeGeometries();
        int numGeoms = this.viewerController.numGeometries();
        if (numGeoms < 2) {
            this.viewerController.thawGeometries();
            return;
        }

        double value = this.movieSlider.getValue();
        if (value >= (1.0 - 1.0e-6)) {
            this.movieSlider.setValue(value = 0.0);
        }

        double time = TIME_PER_GEOM * (1.0 - value) * ((double) (numGeoms - 1));
        KeyValue keyValue = new KeyValue(this.movieSlider.valueProperty(), 1.0);
        KeyFrame keyFrame = new KeyFrame(Duration.millis(time), keyValue);

        this.movieTimeline = new Timeline();
        this.movieTimeline.setCycleCount(1);
        this.movieTimeline.getKeyFrames().add(keyFrame);
        this.movieTimeline.setOnFinished(event -> {
            this.stopMovie();
            this.updatePlayButton();
        });

        AtomsViewerInterface atomsViewer = this.projectController.getAtomsViewer();
        if (atomsViewer != null) {
            atomsViewer.startExclusiveMode();
        }

        try {
            this.movieTimeline.playFromStart();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopMovie() {
        if (this.movieTimeline == null) {
            return;
        }

        try {
            this.movieTimeline.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }

        AtomsViewerInterface atomsViewer = this.projectController.getAtomsViewer();
        if (atomsViewer != null) {
            atomsViewer.stopExclusiveMode();
        }

        this.movieTimeline = null;

        this.viewerController.thawGeometries();
    }
}
