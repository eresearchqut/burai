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

package burai.app.explorer;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import burai.app.QEFXAppController;
import burai.app.QEFXMainController;
import burai.app.explorer.body.QEFXExplorerBody;
import burai.matapi.MaterialsAPILoader;
import burai.project.Project;

public class QEFXExplorerController extends QEFXAppController {

    private LocationSetupper locationSetupper;
    private ButtonsSetupper buttonsSetupper;
    private BodySetupper bodySetupper;

    @FXML
    private BorderPane bodyPane;

    @FXML
    private TextField locationField;

    @FXML
    private Button backwardButton;

    @FXML
    private Button forwardButton;

    @FXML
    private Button upwardButton;

    @FXML
    private MenuButton menuButton;

    @FXML
    private MenuItem listMenu;

    @FXML
    private MenuItem smallMenu;

    @FXML
    private MenuItem mediumMenu;

    @FXML
    private MenuItem largeMenu;

    @FXML
    private Button recentButton;

    @FXML
    private Button computerButton;

    @FXML
    private Button projectsButton;

    @FXML
    private Button calculatingButton;

    @FXML
    private Button searchedButton;

    @FXML
    private Button webButton;

    @FXML
    private Button downloadsButton;

    public QEFXExplorerController(QEFXMainController mainController) {
        super(mainController);
        this.locationSetupper = new LocationSetupper(this);
        this.buttonsSetupper = new ButtonsSetupper(this);
        this.bodySetupper = new BodySetupper(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.locationSetupper.setupLocationField(this.locationField);
        this.locationSetupper.setupBackwardButton(this.backwardButton);
        this.locationSetupper.setupForwardButton(this.forwardButton);
        this.locationSetupper.setupUpwardButton(this.upwardButton);

        this.buttonsSetupper.setupRecentButton(this.recentButton);
        this.buttonsSetupper.setupComputerButton(this.computerButton);
        this.buttonsSetupper.setupProjectsButton(this.projectsButton);
        this.buttonsSetupper.setupCalculatingButton(this.calculatingButton);
        this.buttonsSetupper.setupSearchedButton(this.searchedButton);
        this.buttonsSetupper.setupWebButton(this.webButton);
        this.buttonsSetupper.setupDownloadsButton(this.downloadsButton);

        this.bodySetupper.setupMenuButton(this.menuButton);
        this.bodySetupper.setupMenuItemList(this.listMenu);
        this.bodySetupper.setupMenuItemSmallTile(this.smallMenu);
        this.bodySetupper.setupMenuItemMediumTile(this.mediumMenu);
        this.bodySetupper.setupMenuItemLargeTile(this.largeMenu);
        this.bodySetupper.setupBodyPane(this.bodyPane);
    }

    protected BorderPane getBodyPane() {
        return this.bodyPane;
    }

    protected TextField getLocationField() {
        if (this.locationField == null) {
            this.locationField = new TextField();
        }

        return this.locationField;
    }

    protected String getPreviousLocation() {
        return this.locationSetupper.getPreviousLocation();
    }

    protected void storeLocation(String location) {
        this.locationSetupper.storeLocation(location);
    }

    protected void clearRedoLocations() {
        this.locationSetupper.clearRedoLocations();
    }

    protected void pushBackwardButton() {
        EventHandler<ActionEvent> handler = null;
        if (this.backwardButton != null) {
            handler = this.backwardButton.getOnAction();
        }

        if (handler != null) {
            handler.handle(new ActionEvent());
        }
    }

    protected void pushForwardButton() {
        EventHandler<ActionEvent> handler = null;
        if (this.forwardButton != null) {
            handler = this.forwardButton.getOnAction();
        }

        if (handler != null) {
            handler.handle(new ActionEvent());
        }
    }

    protected void pushUpwardButton() {
        EventHandler<ActionEvent> handler = null;
        if (this.upwardButton != null) {
            handler = this.upwardButton.getOnAction();
        }

        if (handler != null) {
            handler.handle(new ActionEvent());
        }
    }

    protected QEFXExplorerBody getExplorerBody() {
        return this.bodySetupper.getExplorerBody();
    }

    protected void updateExplorerBody() {
        this.bodySetupper.updateExplorerBody(false);
    }

    protected void updateExplorerBody(boolean asUndo) {
        this.bodySetupper.updateExplorerBody(asUndo);
    }

    protected void refreshProject(Project project) {
        this.bodySetupper.refreshProject(project);
    }

    protected void setMaterialsAPILoader(MaterialsAPILoader matApiLoader) {
        this.buttonsSetupper.toBeSearched();
        this.bodySetupper.setMaterialsAPILoader(matApiLoader);
    }
}
