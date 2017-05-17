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

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import burai.app.QEFXMain;
import burai.app.QEFXMainController;
import burai.atoms.model.Atom;
import burai.atoms.model.Cell;
import burai.atoms.model.exception.ZeroVolumCellException;
import burai.com.consts.Constants;
import burai.com.env.Environments;
import burai.com.math.Matrix3D;
import burai.project.Project;
import burai.project.property.ProjectGeometry;

public class ProjectExporter {

    private QEFXMainController mainController;

    private Project project;

    private ProjectGeometry geometry;

    protected ProjectExporter(QEFXMainController mainController, Project project, ProjectGeometry geometry) {
        if (mainController == null) {
            throw new IllegalArgumentException("mainController is null.");
        }

        if (project == null) {
            throw new IllegalArgumentException("project is null.");
        }

        if (geometry == null) {
            throw new IllegalArgumentException("geometry is null.");
        }

        this.mainController = mainController;
        this.project = project;
        this.geometry = geometry;
    }

    protected void exportProject() {
        Project project = this.saveNewProject();
        if (project == null) {
            return;
        }

        boolean status = this.editProject(project);
        if (!status) {
            System.err.println("cannot edit project.");
            return;
        }

        if (this.mainController != null) {
            this.mainController.showProject(project);
        }
    }

    private Project saveNewProject() {
        File directory = null;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export new project");

        String projPath = this.project == null ? null : this.project.getDirectoryPath();
        projPath = projPath == null ? null : projPath.trim();

        File projDir = null;
        if (projPath != null && !(projPath.isEmpty())) {
            projDir = new File(projPath);
        }

        File initDir = projDir == null ? null : projDir.getParentFile();
        String initPath = initDir == null ? null : initDir.getPath();
        if (initDir == null || initPath == null || initPath.trim().isEmpty()) {
            initPath = Environments.getProjectsPath();
            initDir = new File(initPath);
        }

        if (initDir != null) {
            try {
                if (initDir.isDirectory()) {
                    fileChooser.setInitialDirectory(initDir);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Stage stage = this.mainController == null ? null : this.mainController.getStage();
        if (stage != null) {
            directory = fileChooser.showSaveDialog(stage);
        }
        if (directory == null) {
            return null;
        }

        try {
            if (directory.exists()) {
                Alert alert = new Alert(AlertType.ERROR);
                QEFXMain.initializeDialogOwner(alert);
                alert.setHeaderText(directory.getName() + " already exists.");
                alert.showAndWait();
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        Project project = null;
        if (this.project != null) {
            project = this.project.cloneProject(directory);
        }

        if (project == null) {
            Alert alert = new Alert(AlertType.ERROR);
            QEFXMain.initializeDialogOwner(alert);
            alert.setHeaderText("Cannot create project: " + directory.getPath());
            alert.showAndWait();
            return null;
        }

        return project;
    }

    private boolean editProject(Project project) {
        Cell cell = project == null ? null : project.getCell();
        if (cell == null) {
            return false;
        }

        if (this.geometry == null) {
            return false;
        }

        double[][] lattice = this.geometry.getCell();
        lattice = Matrix3D.mult(Constants.BOHR_RADIUS_ANGS, lattice);
        if (lattice == null || lattice.length < 3) {
            return false;
        }
        if (lattice[0] == null || lattice[0].length < 3) {
            return false;
        }
        if (lattice[1] == null || lattice[1].length < 3) {
            return false;
        }
        if (lattice[2] == null || lattice[2].length < 3) {
            return false;
        }

        try {
            cell.moveLattice(lattice);
        } catch (ZeroVolumCellException e) {
            e.printStackTrace();
            return false;
        }

        int natom = this.geometry.numAtoms();
        int natom_ = cell.numAtoms(true);

        Atom[] refAtoms = null;
        if (natom == natom_) {
            refAtoms = cell.listAtoms(true);
        }

        if (refAtoms != null && refAtoms.length >= natom) {
            for (int i = 0; i < natom; i++) {
                String name = this.geometry.getName(i);
                if (name == null || name.trim().isEmpty()) {
                    continue;
                }

                double x = this.geometry.getX(i) * Constants.BOHR_RADIUS_ANGS;
                double y = this.geometry.getY(i) * Constants.BOHR_RADIUS_ANGS;
                double z = this.geometry.getZ(i) * Constants.BOHR_RADIUS_ANGS;

                Atom atom = refAtoms[i];
                if (atom == null) {
                    cell.addAtom(new Atom(name, x, y, z));
                } else {
                    atom.setName(name);
                    atom.moveTo(x, y, z);
                }
            }

        } else {
            cell.removeAllAtoms();

            for (int i = 0; i < natom; i++) {
                String name = this.geometry.getName(i);
                if (name == null || name.trim().isEmpty()) {
                    continue;
                }

                double x = this.geometry.getX(i) * Constants.BOHR_RADIUS_ANGS;
                double y = this.geometry.getY(i) * Constants.BOHR_RADIUS_ANGS;
                double z = this.geometry.getZ(i) * Constants.BOHR_RADIUS_ANGS;
                cell.addAtom(new Atom(name, x, y, z));
            }
        }

        project.saveQEInputs();

        return true;
    }
}
