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

package burai.app.project.editor.result.movie;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;

import org.jcodec.api.awt.AWTSequenceEncoder8Bit;
import org.jcodec.codecs.h264.H264Encoder;

import burai.app.QEFXMain;
import burai.app.project.QEFXProjectController;
import burai.app.project.viewer.result.movie.QEFXMovieViewerController;
import burai.com.fx.FXThread;

public class MP4Maker {

    private QEFXProjectController projectController;

    private QEFXMovieViewerController viewerController;

    private boolean movieMaking;

    private MovieProgress movieProgress;

    protected MP4Maker(QEFXProjectController projectController, QEFXMovieViewerController viewerController) {
        if (projectController == null) {
            throw new IllegalArgumentException("projectController is null.");
        }

        if (viewerController == null) {
            throw new IllegalArgumentException("viewerController is null.");
        }

        this.projectController = projectController;
        this.viewerController = viewerController;

        this.movieMaking = false;
        this.movieProgress = null;
    }

    private synchronized boolean isMovieMaking() {
        return this.movieMaking;
    }

    private synchronized void setMovieMaking(boolean movieMaking) {
        this.movieMaking = movieMaking;
    }

    protected void makeMP4(File file) {
        if (file == null) {
            return;
        }

        if (this.isMovieMaking()) {
            return;
        }

        this.movieProgress = new MovieProgress(file);
        this.movieProgress.showProgress(event -> this.setMovieMaking(false));
        this.setMovieMaking(true);

        Thread thread = new Thread(() -> {
            boolean status = this.makeMP4Async(file);

            Platform.runLater(() -> {
                this.movieProgress.hideProgress();
                this.movieProgress = null;
                this.setMovieMaking(false);

                if (!status) {
                    this.showErrorDialog(file);
                }
            });
        });

        thread.start();
    }

    private void showErrorDialog(File file) {
        if (file == null) {
            return;
        }

        Alert alert = new Alert(AlertType.ERROR);
        QEFXMain.initializeDialogOwner(alert);
        alert.setHeaderText("ERROR in creating a movie file: " + file.getName());
        alert.showAndWait();
    }

    private boolean makeMP4Async(File file) {
        if (file == null) {
            return false;
        }

        int numGeoms = this.viewerController.numGeometries();
        if (numGeoms < 1) {
            return false;
        }

        AWTSequenceEncoder8Bit encoder = null;

        try {
            encoder = AWTSequenceEncoder8Bit.create30Fps(file);
            if (encoder == null) {
                return false;
            }

            H264Encoder h264Encoder = encoder.getEncoder();
            if (h264Encoder != null) {
                h264Encoder.setKeyInterval(30);
            }

            for (int i = 0; i < numGeoms; i++) {
                int i_ = i;

                if (!this.isMovieMaking()) {
                    return false;
                }

                FXThread<BufferedImage> thread1 = new FXThread<BufferedImage>(() -> {
                    if (!this.viewerController.showGeometry(i_)) {
                        return null;
                    }

                    Node subject = this.projectController.getViewerPane();
                    Image image = subject == null ? null : subject.snapshot(null, null);
                    return image == null ? null : SwingFXUtils.fromFXImage(image, null);
                });

                BufferedImage swingImage = thread1.getResult();
                swingImage = swingImage == null ? null : this.resizeSwingImage(swingImage);
                if (swingImage == null) {
                    return false;
                }

                if (!this.isMovieMaking()) {
                    return false;
                }

                encoder.encodeImage(swingImage);

                if (!this.isMovieMaking()) {
                    return false;
                }

                numGeoms = this.viewerController.numGeometries();
                double valueDone = (double) (i + 1);
                double valueTotal = (double) numGeoms;
                double rate = valueDone / valueTotal;

                FXThread<Double> thread2 = new FXThread<Double>(() -> {
                    if (this.movieProgress != null) {
                        this.movieProgress.setProgress(rate);
                    }
                    return rate;
                });

                thread2.runAndWait();
            }

        } catch (Exception e1) {
            e1.printStackTrace();
            return false;

        } finally {
            if (encoder != null) {
                try {
                    encoder.finish();

                } catch (Exception e2) {
                    e2.printStackTrace();
                    return false;
                }
            }
        }

        return true;
    }

    private BufferedImage resizeSwingImage(BufferedImage swingImage1) {
        if (swingImage1 == null) {
            return null;
        }

        int width1 = swingImage1.getWidth();
        int height1 = swingImage1.getHeight();
        int width2 = 8 * ((int) (Math.rint(((double) width1) / 8.0) + 0.1));
        int height2 = 8 * ((int) (Math.rint(((double) height1) / 8.0) + 0.1));
        if (width2 <= 0 || height2 <= 0) {
            return null;
        }

        BufferedImage swingImage2 = new BufferedImage(width2, height2, swingImage1.getType());

        Graphics graphics1 = swingImage1.getGraphics();
        Graphics graphics2 = swingImage2.getGraphics();
        if (graphics1 == null || graphics2 == null) {
            return null;
        }

        java.awt.Image image1 = swingImage1.getScaledInstance(width2, height2, java.awt.Image.SCALE_DEFAULT);
        if (image1 == null) {
            return null;
        }

        graphics2.drawImage(image1, 0, 0, null);

        return swingImage2;
    }
}
