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

import java.io.File;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import burai.app.project.QEFXProjectController;
import burai.app.project.viewer.result.movie.QEFXMovieViewerController;
import burai.project.Project;

public class MovieMaker {

    private QEFXProjectController projectController;

    private QEFXMovieViewerController viewerController;

    private Project project;

    protected MovieMaker(QEFXProjectController projectController, QEFXMovieViewerController viewerController, Project project) {
        if (projectController == null) {
            throw new IllegalArgumentException("projectController is null.");
        }

        if (viewerController == null) {
            throw new IllegalArgumentException("viewerController is null.");
        }

        if (project == null) {
            throw new IllegalArgumentException("project is null.");
        }

        this.projectController = projectController;
        this.viewerController = viewerController;
        this.project = project;
    }

    protected void makeMovie() {
        File mp4File = this.selectMP4File();
        if (mp4File == null) {
            return;
        }

        MP4Maker mp4Maker = new MP4Maker(this.projectController, this.viewerController);
        mp4Maker.makeMP4(mp4File);
    }

    private File selectMP4File() {
        File selectedFile = null;
        Stage stage = this.projectController.getStage();
        FileChooser fileChooser = this.createFileChooser();
        if (stage != null && fileChooser != null) {
            selectedFile = fileChooser.showSaveDialog(stage);
        }

        return selectedFile;
    }

    private FileChooser createFileChooser() {
        File dirFile = this.project.getDirectory();
        String fileName = dirFile == null ? null : dirFile.getName();
        fileName = fileName == null ? null : (fileName.trim() + ".mp4");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save movie");
        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Movie Files (*.mp4)", "*.mp4"));

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
