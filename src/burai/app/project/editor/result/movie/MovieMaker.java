/*
 * Copyright (C) 2017 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.app.project.editor.result.movie;

import java.io.File;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import burai.app.project.QEFXProjectController;
import burai.project.Project;

public class MovieMaker {

    private QEFXProjectController projectController;

    private Project project;

    protected MovieMaker(QEFXProjectController projectController, Project project) {
        if (projectController == null) {
            throw new IllegalArgumentException("projectController is null.");
        }

        if (project == null) {
            throw new IllegalArgumentException("project is null.");
        }

        this.projectController = projectController;
        this.project = project;
    }

    protected void makeMovie() {
        File file = this.selectFile();
    }

    private File selectFile() {
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
