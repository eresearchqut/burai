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

package burai.app;

import java.io.File;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.ResourceBundle;

import burai.app.about.QEFXAboutDialog;
import burai.app.explorer.QEFXExplorer;
import burai.app.explorer.QEFXExplorerFacade;
import burai.app.icon.QEFXFolderIcon;
import burai.app.icon.QEFXIcon;
import burai.app.icon.QEFXProjectIcon;
import burai.app.icon.QEFXWebIcon;
import burai.app.matapi.QEFXMatAPIDialog;
import burai.app.onclose.QEFXSavingDialog;
import burai.app.path.QEFXPathDialog;
import burai.app.proxy.QEFXProxyDialog;
import burai.app.ssh.QEFXSSHDialog;
import burai.app.tab.QEFXTabManager;
import burai.com.env.Environments;
import burai.com.graphic.svg.SVGLibrary;
import burai.com.graphic.svg.SVGLibrary.SVGData;
import burai.com.keys.KeyNames;
import burai.com.life.Life;
import burai.matapi.MaterialsAPILoader;
import burai.project.Project;
import burai.run.RunningManager;
import burai.ver.Version;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class QEFXMainController implements Initializable {

    private static final long SLEEP_TIME_TO_DROP_FILES = 650L;

    private static final double TOPMENU_GRAPHIC_SIZE = 16.0;
    private static final String TOPMENU_GRAPHIC_CLASS = "picblack-button";

    private static final double HOME_GRAPHIC_SIZE = 20.0;
    private static final String HOME_GRAPHIC_CLASS = "pichome-button";

    private static final double SEARCH_GRAPHIC_SIZE = 14.0;
    private static final String SEARCH_GRAPHIC_CLASS = "picblack-button";

    private Stage stage;

    private QEFXTabManager tabManager;

    private QEFXExplorerFacade explorerFacade;

    private Queue<HomeTabSelected> onHomeTabSelectedQueue;

    @FXML
    private BorderPane basePane;

    @FXML
    private Menu topMenu;

    @FXML
    private MenuItem aboutMItem;

    @FXML
    private MenuItem docsMItem;

    @FXML
    private MenuItem qeMItem;

    @FXML
    private MenuItem pseudoMItem;

    @FXML
    private MenuItem manPwMItem;

    @FXML
    private MenuItem manDosMItem;

    @FXML
    private MenuItem manProjMItem;

    @FXML
    private MenuItem manBandMItem;

    @FXML
    private MenuItem pathMItem;

    @FXML
    private MenuItem proxyMItem;

    @FXML
    private MenuItem remoteMItem;

    @FXML
    private MenuItem fullScrMItem;

    @FXML
    private MenuItem quitMItem;

    @FXML
    private TabPane tabPane;

    @FXML
    private Tab homeTab;

    @FXML
    private AnchorPane homePane;

    @FXML
    private Button matApiButton;

    @FXML
    private TextField matApiField;

    public QEFXMainController() {
        this.stage = null;
        this.tabManager = null;
        this.explorerFacade = null;
        this.onHomeTabSelectedQueue = null;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setupBasePane();
        this.setupTopMenu();
        this.setupMenuItems();
        this.setupTabPane();
        this.setupHomeTab();
        this.setupHomePane();
        this.setupMatApiButton();
        this.setupMatApiField();
    }

    private void setupBasePane() {
        if (this.basePane == null) {
            return;
        }

        this.basePane.setOnDragOver(event -> {
            Dragboard board = event == null ? null : event.getDragboard();
            if (board == null) {
                return;
            }

            if (board.hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
        });

        this.basePane.setOnDragDropped(event -> {
            Dragboard board = event == null ? null : event.getDragboard();
            if (board == null) {
                return;
            }

            if (!board.hasFiles()) {
                event.setDropCompleted(false);
                return;
            }

            List<File> files = board.getFiles();
            if (files != null) {
                Thread thread = new Thread(() -> {
                    for (File file : files) {
                        if (file != null) {
                            this.showFile(file);
                        }

                        synchronized (files) {
                            try {
                                files.wait(SLEEP_TIME_TO_DROP_FILES);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

                thread.start();
            }

            event.setDropCompleted(true);
        });
    }

    private void setupTopMenu() {
        if (this.topMenu == null) {
            return;
        }

        this.topMenu.setText("");
        this.topMenu.setGraphic(
                SVGLibrary.getGraphic(SVGData.VECTOR_RIGHT, TOPMENU_GRAPHIC_SIZE, null, TOPMENU_GRAPHIC_CLASS));

        this.topMenu.showingProperty().addListener(o -> {
            if (this.topMenu.isShowing()) {
                this.topMenu.setGraphic(SVGLibrary.getGraphic(
                        SVGData.VECTOR_DOWN, TOPMENU_GRAPHIC_SIZE, null, TOPMENU_GRAPHIC_CLASS));
            } else {
                this.topMenu.setGraphic(SVGLibrary.getGraphic(
                        SVGData.VECTOR_RIGHT, TOPMENU_GRAPHIC_SIZE, null, TOPMENU_GRAPHIC_CLASS));
            }
        });
    }

    private void setupMenuItems() {
        if (this.aboutMItem != null) {
            this.aboutMItem.setOnAction(event -> {
                QEFXAboutDialog dialog = new QEFXAboutDialog();
                dialog.showAndWait();
            });
        }

        if (this.docsMItem != null) {
            this.docsMItem.setOnAction(event -> {
                this.showWebPage(Environments.getDocumentsWebsite());
            });
        }

        if (this.qeMItem != null) {
            this.qeMItem.setOnAction(event -> {
                this.showWebPage(Environments.getEspressoWebsite());
            });
        }

        if (this.pseudoMItem != null) {
            this.pseudoMItem.setOnAction(event -> {
                this.showWebPage(Environments.getPseudoWebsite());
            });
        }

        if (this.manPwMItem != null) {
            this.manPwMItem.setOnAction(event -> {
                this.showWebPage(Environments.getManPwscfWebsite());
            });
        }

        if (this.manDosMItem != null) {
            this.manDosMItem.setOnAction(event -> {
                this.showWebPage(Environments.getManDosWebsite());
            });
        }

        if (this.manProjMItem != null) {
            this.manProjMItem.setOnAction(event -> {
                this.showWebPage(Environments.getManProjwfcWebsite());
            });
        }

        if (this.manBandMItem != null) {
            this.manBandMItem.setOnAction(event -> {
                this.showWebPage(Environments.getManBandsWebsite());
            });
        }

        if (this.pathMItem != null) {
            this.pathMItem.setOnAction(event -> {
                QEFXPathDialog dialog = new QEFXPathDialog(this);
                dialog.showAndSetProperties();
            });
        }

        if (this.proxyMItem != null) {
            this.proxyMItem.setOnAction(event -> {
                QEFXProxyDialog dialog = new QEFXProxyDialog();
                dialog.showAndSetProperties();
            });
        }

        if (this.remoteMItem != null) {
            this.remoteMItem.setOnAction(event -> {
                QEFXSSHDialog dialog = new QEFXSSHDialog(this);
                dialog.showAndSetProperties();
            });
        }

        if (this.fullScrMItem != null) {
            this.fullScrMItem.setOnAction(event -> {
                if (this.stage != null && this.stage.isFullScreen()) {
                    this.setFullScreen(false);
                } else {
                    this.setFullScreen(true);
                }
            });
        }

        if (this.quitMItem != null) {
            this.quitMItem.setText("Quit [" + KeyNames.getShortcut(KeyCode.Q) + "]");
            this.quitMItem.setOnAction(event -> {
                this.quitSystem();
            });
        }
    }

    private void setupTabPane() {
        if (this.tabPane == null) {
            return;
        }

        this.tabManager = new QEFXTabManager(this, this.tabPane);
    }

    private void setupHomeTab() {
        if (this.homeTab == null) {
            return;
        }

        this.homeTab.setText("");
        this.homeTab.setGraphic(
                SVGLibrary.getGraphic(SVGData.HOME, HOME_GRAPHIC_SIZE, null, HOME_GRAPHIC_CLASS));

        this.homeTab.setClosable(false);
        this.homeTab.setTooltip(new Tooltip("home"));

        this.homeTab.setOnSelectionChanged(event -> {
            if (this.homeTab.isSelected()) {
                this.executeOnHomeTabSelected();
            }
        });
    }

    private void setupHomePane() {
        if (this.homePane == null) {
            return;
        }

        this.homePane.setOnKeyPressed(event -> {
            if (event == null) {
                return;
            }

            if (event.isShortcutDown() && KeyCode.LEFT.equals(event.getCode())) {
                // Shortcut + <-
                if (this.explorerFacade != null) {
                    this.explorerFacade.goBackward();
                }
            } else if (event.isShortcutDown() && KeyCode.RIGHT.equals(event.getCode())) {
                // Shortcut + ->
                if (this.explorerFacade != null) {
                    this.explorerFacade.goForward();
                }
            } else if (event.isShortcutDown() && KeyCode.UP.equals(event.getCode())) {
                // Shortcut + ^
                if (this.explorerFacade != null) {
                    this.explorerFacade.goUpward();
                }
            }
        });
    }

    private void executeOnHomeTabSelected() {
        if (this.onHomeTabSelectedQueue == null) {
            return;
        }

        if (this.explorerFacade != null) {
            this.explorerFacade.startSingleReloadMode();
        }

        while (!this.onHomeTabSelectedQueue.isEmpty()) {
            HomeTabSelected onHomeTabSelected = this.onHomeTabSelectedQueue.poll();
            if (onHomeTabSelected != null && this.explorerFacade != null) {
                onHomeTabSelected.onHomeTabSelected(this.explorerFacade);
            }
        }

        if (this.explorerFacade != null) {
            this.explorerFacade.endSingleReloadMode();
        }
    }

    private void setupMatApiButton() {
        if (this.matApiButton == null) {
            return;
        }

        this.matApiButton.setGraphic(
                SVGLibrary.getGraphic(SVGData.SEARCH, SEARCH_GRAPHIC_SIZE, null, SEARCH_GRAPHIC_CLASS));

        this.matApiButton.setOnAction(event -> {
            QEFXMatAPIDialog dialog = new QEFXMatAPIDialog();
            dialog.showAndSetProperties();
        });
    }

    private void setupMatApiField() {
        if (this.matApiField == null) {
            return;
        }

        String messageTip = "";
        messageTip = messageTip + "1)  Li-Fe-O" + System.lineSeparator();
        messageTip = messageTip + "2)  Fe2O3";
        this.matApiField.setTooltip(new Tooltip(messageTip));

        this.matApiField.setOnAction(event -> {
            String text = this.matApiField.getText();
            text = text == null ? null : text.trim();
            if (text == null || text.isEmpty()) {
                MaterialsAPILoader.deleteLoader();
                return;
            }

            MaterialsAPILoader matApiLoader = new MaterialsAPILoader(text);

            if (matApiLoader.numMaterialIDs() > 0) {
                this.matApiField.setText(matApiLoader.getFormula());
                if (this.explorerFacade != null) {
                    this.explorerFacade.setMaterialsAPILoader(matApiLoader);
                    this.explorerFacade.setSearchedMode();
                    this.showHome();
                }

            } else {
                String message = "The Materials API says there are no data of " + matApiLoader.getFormula() + ".";
                Alert alert = new Alert(AlertType.ERROR);
                QEFXMain.initializeDialogOwner(alert);
                alert.setHeaderText(message);
                alert.showAndWait();
            }
        });
    }

    public Stage getStage() {
        return this.stage;
    }

    protected void setStage(Stage stage) {
        this.stage = stage;
        if (this.stage == null) {
            return;
        }

        Rectangle2D rectangle2d = Screen.getPrimary().getVisualBounds();
        double width = rectangle2d.getWidth();
        double height = rectangle2d.getHeight();

        this.stage.setWidth(width);
        this.stage.setHeight(height);
        this.stage.setTitle("BURAI" + Version.VERSION + ", a GUI of Quantum ESPRESSO.");
        this.stage.setOnCloseRequest(event -> this.actionOnCloseRequest(event));
        this.stage.setOnHidden(event -> this.actionOnHidden(event));

        Scene scene = this.stage.getScene();
        if (scene != null) {
            scene.setOnKeyPressed(event -> {
                if (event == null) {
                    return;
                }
                if (event.isShortcutDown() && KeyCode.Q.equals(event.getCode())) {
                    this.quitSystem();
                }
            });
        }
    }

    private void actionOnCloseRequest(WindowEvent event) {
        List<Project> projects = this.tabManager == null ? null : this.tabManager.getProjects();
        if (projects != null && (!projects.isEmpty())) {
            QEFXSavingDialog dialog = new QEFXSavingDialog(projects);
            if (dialog.hasProjects()) {
                boolean status = dialog.showAndSave();
                if (!status) {
                    if (event != null) {
                        event.consume();
                    }
                }
            }
        }

        if (event != null && event.isConsumed()) {
            return;
        }

        if (!RunningManager.getInstance().isEmpty()) {
            Alert alert = new Alert(AlertType.WARNING);
            QEFXMain.initializeDialogOwner(alert);
            alert.setHeaderText("Calculations are running. Do you delete them ?");
            alert.getButtonTypes().clear();
            alert.getButtonTypes().add(ButtonType.YES);
            alert.getButtonTypes().add(ButtonType.NO);

            Optional<ButtonType> optButtonType = alert.showAndWait();

            ButtonType buttonType = null;
            if (optButtonType != null && optButtonType.isPresent()) {
                buttonType = optButtonType.get();
            }

            if (!ButtonType.YES.equals(buttonType)) {
                if (event != null) {
                    event.consume();
                }
            }
        }
    }

    private void actionOnHidden(WindowEvent event) {
        if (event != null && event.isConsumed()) {
            return;
        }

        if (this.tabManager != null) {
            this.tabManager.hideAllTabs();
        }

        Life.getInstance().toBeDead();
    }

    public void quitSystem() {
        if (this.stage != null) {
            WindowEvent event = new WindowEvent(this.stage, WindowEvent.ANY);

            if (!event.isConsumed()) {
                if (this.stage.getOnCloseRequest() != null) {
                    this.stage.getOnCloseRequest().handle(event);
                }
            }

            if (!event.isConsumed()) {
                this.stage.hide();
            }
        }
    }

    public void setMaximized(boolean maximized) {
        if (this.stage != null) {
            this.stage.setMaximized(maximized);
        }
    }

    public void setFullScreen(boolean fullScreen) {
        if (this.stage != null) {
            this.stage.setFullScreen(fullScreen);
        }
    }

    public void setResizable(boolean resizable) {
        if (this.stage != null) {
            this.stage.setResizable(resizable);
        }
    }

    protected void setExplorer(QEFXExplorer explorer) {
        if (explorer == null) {
            return;
        }

        if (this.homePane != null) {
            Node node = explorer.getNode();
            if (node != null) {
                this.homePane.getChildren().clear();
                this.homePane.getChildren().add(node);
                this.explorerFacade = explorer.getFacade();
            }
        }
    }

    public void refreshProjectOnExplorer(Project project) {
        if (project == null) {
            return;
        }

        if (this.explorerFacade != null) {
            this.explorerFacade.refreshProject(project);
        }
    }

    public void offerOnHomeTabSelected(HomeTabSelected onHomeTabSelected) {
        if (onHomeTabSelected == null) {
            return;
        }

        if (this.onHomeTabSelectedQueue == null) {
            this.onHomeTabSelectedQueue = new LinkedList<HomeTabSelected>();
        }

        this.onHomeTabSelectedQueue.offer(onHomeTabSelected);
    }

    public boolean showHome() {
        if (this.tabManager != null) {
            return this.tabManager.showHomeTab();
        }

        return false;
    }

    public Project showProject(Project project) {
        if (project == null) {
            return null;
        }

        if (this.tabManager != null) {
            Project project2 = this.tabManager.showTab(project);
            if (project2 != null) {
                Environments.addRecentFilePath(project2.getRelatedFilePath());
                this.offerOnHomeTabSelected(explorerFacade -> {
                    if (explorerFacade != null && explorerFacade.isRecentlyUsedMode()) {
                        explorerFacade.reloadLocation();
                    }
                });
            }

            return project2;
        }

        return null;
    }

    public WebEngine showWebPage(String url) {
        if (url == null || url.trim().isEmpty()) {
            return null;
        }

        if (this.tabManager != null) {
            return this.tabManager.showTab(url);
        }

        return null;
    }

    public boolean hideProject(Project project) {
        if (project == null) {
            return false;
        }

        if (this.tabManager != null) {
            return this.tabManager.hideTab(project);
        }

        return false;
    }

    public boolean hideWebPage(WebEngine engine) {
        if (engine == null) {
            return false;
        }

        if (this.tabManager != null) {
            return this.tabManager.hideTab(engine);
        }

        return false;
    }

    public List<Project> getShownProjects() {
        if (this.tabManager != null) {
            return this.tabManager.getProjects();
        }

        return null;
    }

    public void showFile(String path) {
        if (path == null || path.trim().isEmpty()) {
            return;
        }

        this.showFile(new File(path));
    }

    public void showFile(File file) {
        if (file == null) {
            return;
        }

        try {
            if (!file.exists()) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        Platform.runLater(() -> {
            QEFXIcon icon = QEFXIcon.getInstance(file);
            if (icon == null) {
                return;
            }

            if (icon instanceof QEFXProjectIcon) {
                Project project = ((QEFXProjectIcon) icon).getContent();
                if (project != null && project.getCell() != null) {
                    this.showProject(project);
                }

            } else if (icon instanceof QEFXWebIcon) {
                String url = ((QEFXWebIcon) icon).getInitialURL();
                if (url != null && !(url.trim().isEmpty())) {
                    this.showWebPage(url);
                }

            } else if (icon instanceof QEFXFolderIcon) {
                String dirPath = ((QEFXFolderIcon) icon).getContent();
                if (dirPath == null) {
                    return;
                }
                dirPath = dirPath.trim();
                if (dirPath.isEmpty()) {
                    return;
                }

                File dirFile = new File(dirPath);
                try {
                    if (!dirFile.isDirectory()) {
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }

                if (this.explorerFacade != null) {
                    this.explorerFacade.setLocation(dirFile.getPath());
                    this.showHome();
                }
            }
        });
    }
}
