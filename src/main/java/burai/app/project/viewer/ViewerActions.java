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

package burai.app.project.viewer;

import java.io.IOException;
import java.util.Optional;

import burai.app.project.ProjectAction;
import burai.app.project.ProjectActions;
import burai.app.project.QEFXProjectController;
import burai.app.project.viewer.atoms.AtomsAction;
import burai.app.project.viewer.designer.DesignerAction;
import burai.app.project.viewer.inputfile.QEFXInputFile;
import burai.app.project.viewer.modeler.ModelerAction;
import burai.app.project.viewer.result.ResultAction;
import burai.app.project.viewer.run.QEFXRunDialog;
import burai.app.project.viewer.run.RunAction;
import burai.app.project.viewer.run.RunEvent;
import burai.app.project.viewer.save.SaveAction;
import burai.app.project.viewer.screenshot.QEFXScreenshotDialog;
import burai.project.Project;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;

public class ViewerActions extends ProjectActions<Node> {

    private ViewerItemSet itemSet;

    private AtomsAction atomsAction;

    private ModelerAction modelerAction;

    private DesignerAction designerAction;

    private ResultAction resultAction;

    public ViewerActions(Project project, QEFXProjectController controller) {
        super(project, controller);

        this.itemSet = new ViewerItemSet();

        this.atomsAction = null;
        this.modelerAction = null;
        this.designerAction = null;
        this.resultAction = null;

        this.setupOnViewerSelected();
        this.setupActions();
    }

    @Override
    public void actionInitially() {
        if (this.controller == null) {
            return;
        }

        this.controller.addViewerMenuItems(this.itemSet.getItems());

        ProjectAction action = this.actions.get(this.itemSet.getAtomsViewerItem());
        if (action != null) {
            action.actionOnProject(this.controller);
        }
    }

    public boolean saveFile() {
        return this.actionSaveFile(this.controller);
    }

    public void screenShot() {
        this.screenShot(null);
    }

    public void screenShot(Node subject) {
        this.actionScreenShot(this.controller, subject);
    }

    private void setupOnViewerSelected() {
        if (this.controller == null) {
            return;
        }

        this.controller.setOnViewerSelected(graphic -> {
            if (graphic == null) {
                return;
            }

            ProjectAction action = null;
            if (this.actions != null) {
                action = this.actions.get(graphic);
            }

            if (action != null && this.controller != null) {
                action.actionOnProject(this.controller);
            }
        });
    }

    private void setupActions() {
        ViewerItem[] items = this.itemSet.getItems();
        for (ViewerItem item : items) {
            if (item == null) {
                continue;
            }

            if (item == this.itemSet.getAtomsViewerItem()) {
                this.actions.put(item, controller2 -> this.actionAtomsViewer(controller2));

            } else if (item == this.itemSet.getInputFileItem()) {
                this.actions.put(item, controller2 -> this.actionInputFile(controller2));

            } else if (item == this.itemSet.getModelerItem()) {
                this.actions.put(item, controller2 -> this.actionModeler(controller2));

            } else if (item == this.itemSet.getSaveFileItem()) {
                this.actions.put(item, controller2 -> this.actionSaveFile(controller2));

            } else if (item == this.itemSet.getSaveAsFileItem()) {
                this.actions.put(item, controller2 -> this.actionSaveAsFile(controller2));

            } else if (item == this.itemSet.getDesignerItem()) {
                this.actions.put(item, controller2 -> this.actionDesigner(controller2));

            } else if (item == this.itemSet.getScreenShotItem()) {
                this.actions.put(item, controller2 -> this.actionScreenShot(controller2, null));

            } else if (item == this.itemSet.getRunItem()) {
                this.actions.put(item, controller2 -> this.actionRun(controller2));

            } else if (item == this.itemSet.getResultItem()) {
                this.actions.put(item, controller2 -> this.actionResult(controller2));
            }
        }
    }

    private void actionAtomsViewer(QEFXProjectController controller) {
        if (controller == null) {
            return;
        }

        if (this.atomsAction == null || controller != this.atomsAction.getController()) {
            this.atomsAction = new AtomsAction(this.project, controller);
        }

        if (this.atomsAction != null) {
            this.atomsAction.showAtoms();
        }
    }

    private void actionInputFile(QEFXProjectController controller) {
        if (controller == null) {
            return;
        }

        this.project.resolveQEInputs();

        try {
            QEFXInputFile inputFile = new QEFXInputFile(controller, this.project);
            controller.clearStackedsOnViewerPane();
            controller.stackOnViewerPane(inputFile.getNode());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void actionModeler(QEFXProjectController controller) {
        if (controller == null) {
            return;
        }

        if (this.modelerAction == null || controller != this.modelerAction.getController()) {
            this.modelerAction = new ModelerAction(this.project, controller);
        }

        if (this.modelerAction != null) {
            this.modelerAction.showModeler();
        }
    }

    private boolean actionSaveFile(QEFXProjectController controller) {
        if (controller == null) {
            return false;
        }

        SaveAction saveAction = new SaveAction(this.project, controller);
        return saveAction.saveProject();
    }

    private void actionSaveAsFile(QEFXProjectController controller) {
        if (controller == null) {
            return;
        }

        SaveAction saveAction = new SaveAction(this.project, controller);
        saveAction.saveProjectAsNew();
    }

    private void actionDesigner(QEFXProjectController controller) {
        if (controller == null) {
            return;
        }

        if (this.designerAction == null || controller != this.designerAction.getController()) {
            this.designerAction = new DesignerAction(this.project, controller);
        }

        if (this.designerAction != null) {
            this.designerAction.showDesigner();
        }
    }

    private void actionScreenShot(QEFXProjectController controller, Node subject) {
        if (controller == null) {
            return;
        }

        QEFXScreenshotDialog dialog = new QEFXScreenshotDialog(controller, this.project, subject);
        Optional<ButtonType> optButtonType = dialog.showAndWait();

        if (optButtonType != null && optButtonType.isPresent() && optButtonType.get() == ButtonType.YES) {
            try {
                dialog.saveImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void actionRun(QEFXProjectController controller) {
        if (controller == null) {
            return;
        }

        this.project.resolveQEInputs();

        QEFXRunDialog dialog = new QEFXRunDialog(this.project, this);
        Optional<RunEvent> optButtonType = dialog.showAndWait();

        if (optButtonType != null && optButtonType.isPresent()) {
            RunEvent runEvent = optButtonType.get();
            if (runEvent != null) {
                RunAction runAction = new RunAction(controller);
                runAction.runCalculation(runEvent);
            }
        }
    }

    private void actionResult(QEFXProjectController controller) {
        if (controller == null) {
            return;
        }

        if (this.resultAction == null || controller != this.resultAction.getController()) {
            this.resultAction = new ResultAction(this.project, controller);
        }

        if (this.resultAction != null) {
            this.resultAction.showResult();
        }
    }
}
