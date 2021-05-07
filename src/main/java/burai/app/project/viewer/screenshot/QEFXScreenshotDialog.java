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

package burai.app.project.viewer.screenshot;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import javax.imageio.ImageIO;

import burai.app.QEFXMain;
import burai.app.project.QEFXProjectController;
import burai.project.Project;

public class QEFXScreenshotDialog extends Dialog<ButtonType> {

    private static final double SHOWING_WIDTH = 512.0;

    private Project project;

    private QEFXProjectController projectController;

    private Node subject;

    private Image image;

    public QEFXScreenshotDialog(QEFXProjectController projectController, Project project) {
        this(projectController, project, null);
    }

    public QEFXScreenshotDialog(QEFXProjectController projectController, Project project, Node subject) {
        super();

        if (project == null) {
            throw new IllegalArgumentException("project is null.");
        }

        if (projectController == null) {
            throw new IllegalArgumentException("projectController is null.");
        }

        this.project = project;
        this.projectController = projectController;
        this.subject = subject;
        this.image = null;

        DialogPane dialogPane = this.getDialogPane();
        QEFXMain.initializeStyleSheets(dialogPane.getStylesheets());
        QEFXMain.initializeDialogOwner(this);

        this.setResizable(false);
        this.setTitle("Screen-shot");
        dialogPane.getButtonTypes().clear();
        dialogPane.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
        dialogPane.setHeaderText("Save this image ?");
        dialogPane.setContent(this.createContent());
    }

    private Node createContent() {
        Node subject2 = this.subject;
        if (subject2 == null) {
            subject2 = this.projectController.getViewerPane();
        }

        if (subject2 != null) {
            if (subject2.isFocused()) {
                this.shiftFocus(subject2, true);
            }

            this.image = subject2.snapshot(null, null);
        }

        ImageView imageView = null;
        if (this.image != null) {
            imageView = new ImageView(this.image);
            double width1 = this.image.getWidth();
            double height1 = this.image.getHeight();
            double width2 = SHOWING_WIDTH;
            double height2 = SHOWING_WIDTH * (height1 / width1);
            imageView.setFitWidth(width2);
            imageView.setFitHeight(height2);
        }

        if (imageView != null) {
            return imageView;
        }

        return new Label("ERROR: cannot show QEFXScreenshotDialog.");
    }

    private void shiftFocus(Node node, boolean first) {
        if (node == null) {
            return;
        }

        if ((!first) && node.isFocusTraversable()) {
            node.requestFocus();
            return;
        }

        Parent parent = node.getParent();
        if (parent == null) {
            return;
        }

        List<Node> children = parent.getChildrenUnmodifiable();
        if (children != null) {
            for (Node child : children) {
                if (child != null && child != node) {
                    if (child.isFocusTraversable()) {
                        child.requestFocus();
                        return;
                    }
                }
            }
        }

        this.shiftFocus(parent, false);
    }

    public void saveImage() throws IOException {
        if (this.image == null) {
            throw new IOException("image is null.");
        }

        File selectedFile = null;
        Stage stage = this.projectController.getStage();
        FileChooser fileChooser = this.createFileChooser();
        if (stage != null && fileChooser != null) {
            selectedFile = fileChooser.showSaveDialog(stage);
        }

        if (selectedFile != null) {
            String extension = "png";
            String selectedName = selectedFile.getName();
            if (selectedName != null) {
                String[] subNames = selectedName.split("[\\.]+");
                if (subNames != null && subNames.length > 0) {
                    String extension_ = subNames[subNames.length - 1];
                    if ("png".equalsIgnoreCase(extension_)) {
                        extension = "png";
                    } else if ("gif".equalsIgnoreCase(extension_)) {
                        extension = "gif";
                    }
                }
            }

            ImageIO.write(SwingFXUtils.fromFXImage(this.image, null), extension, selectedFile);
        }
    }

    private FileChooser createFileChooser() {
        File dirFile = null;
        String fileName = null;

        String dirPath = this.project.getDirectoryPath();
        String rootPath = this.project.getRootFilePath();

        if (dirPath != null) {
            dirFile = new File(dirPath);
            fileName = dirFile.getName();
            if (fileName != null) {
                fileName = fileName.trim() + ".png";
            }

        } else if (rootPath != null) {
            File rootFile = new File(rootPath);
            dirFile = rootFile.getParentFile();
            String rootName = rootFile.getName();
            if (rootName != null) {
                int index = rootName.lastIndexOf('.');
                if (index > -1) {
                    fileName = rootName.substring(0, index) + ".png";
                } else {
                    fileName = rootName + ".png";
                }
            }
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save image");
        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Image Files (*.png, *.gif)", "*.png", "*.gif"));

        try {
            if (dirFile != null && dirFile.isDirectory()) {
                fileChooser.setInitialDirectory(dirFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (fileName != null && !(fileName.isEmpty())) {
            fileChooser.setInitialFileName(fileName);
        }

        return fileChooser;
    }
}
