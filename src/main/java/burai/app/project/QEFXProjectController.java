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

package burai.app.project;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.ResourceBundle;

import burai.app.QEFXAppController;
import burai.app.QEFXMainController;
import burai.app.project.editor.EditorActions;
import burai.app.project.menu.MenuItemSelected;
import burai.app.project.menu.fan.QEFXFanMenu;
import burai.app.project.menu.teeth.QEFXTeethMenu;
import burai.app.project.viewer.ViewerActions;
import burai.atoms.viewer.AtomsViewerInterface;
import burai.com.graphic.svg.SVGLibrary;
import burai.com.graphic.svg.SVGLibrary.SVGData;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class QEFXProjectController extends QEFXAppController {

    private static final double GRAPHIC_SIZE = 20.0;
    private static final String GRAPHIC_CLASS = "pictured-button";

    private static final double TEETH_WIDTH = 285.0;
    private static final double TEETH_HEIGHT = 50.0;

    private static final double FAN_RADIUS = 180.0;
    private static final double FAN_WIDTH = 140.0;
    private static final double FAN_OFFSET = 10.0;

    private static final int MODE_NORMAL = 0;
    private static final int MODE_RESULT_EXPLORER = 1;
    private static final int MODE_RESULT_VIEWER = 2;
    private static final int MODE_MODELER = 3;
    private static final int MODE_MODELER_SLAB = 4;
    private static final int MODE_DESIGNER = 5;

    @FXML
    private Pane basePane;

    @FXML
    private BorderPane projectPane;

    private AtomsViewerInterface atomsViewer;

    @FXML
    private StackPane viewerPane;

    @FXML
    private Button viewerButton;

    private QEFXTeethMenu viewerMenu;

    private MenuItemSelected<Node> onViewerSelected;

    private List<Node> stackedsOnViewer;

    private Queue<Node> stackedsToViewer;

    private ViewerActions viewerActions;

    @FXML
    private Button editorButton;

    @FXML
    private Label editorLabel;

    private QEFXFanMenu editorMenu;

    private MenuItemSelected<String> onEditorSelected;

    private EditorActions editorActions;

    private ProjectAction shownAction;

    private ProjectAction detachAction;

    private int projectMode;

    private Map<Integer, ProjectAnsatz> projectAnsatzMap;

    private Map<Integer, ModeRestored> restoredActionMap;

    private Map<Integer, ModeBacked> backedActionMap;

    public QEFXProjectController(QEFXMainController mainController) {
        super(mainController);

        this.atomsViewer = null;

        this.viewerMenu = null;
        this.onViewerSelected = null;
        this.stackedsOnViewer = null;
        this.stackedsToViewer = null;
        this.viewerActions = null;

        this.editorMenu = null;
        this.onEditorSelected = null;
        this.editorActions = null;

        this.shownAction = null;
        this.detachAction = null;

        this.projectMode = MODE_NORMAL;
        this.projectAnsatzMap = null;
        this.restoredActionMap = null;
        this.backedActionMap = null;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.createViewerMenu();
        this.createEditorMenu();
        this.setupResizing();
        this.setupViewerButton();
        this.setupEditorButton();
        this.setupEditorLabel();
    }

    public BorderPane getProjectPane() {
        return this.projectPane;
    }

    public AtomsViewerInterface getAtomsViewer() {
        return this.atomsViewer;
    }

    /**
     * stack node on viewerPane.
     * @param node
     */
    public void stackOnViewerPane(Node node) {
        if (node == null) {
            return;
        }

        if (this.viewerPane != null && (!this.viewerPane.getChildren().isEmpty())) {
            if (this.stackedsOnViewer == null) {
                this.stackedsOnViewer = new ArrayList<Node>();
            }

            this.stackedsOnViewer.add(node);

            this.viewerPane.getChildren().add(node);
        }
    }

    /**
     * node will be stacked onto viewerPane, when new viewerPane will be set.
     * @param node
     */
    public void stackToViewerPane(Node node) {
        if (node == null) {
            return;
        }

        if (this.stackedsToViewer == null) {
            this.stackedsToViewer = new LinkedList<Node>();
        }

        this.stackedsToViewer.offer(node);
    }

    public void clearStackedsOnViewerPane() {
        if (this.stackedsOnViewer == null || this.stackedsOnViewer.isEmpty()) {
            return;
        }

        if (this.viewerPane != null) {
            List<Node> children = this.viewerPane.getChildren();
            for (Node stacked : this.stackedsOnViewer) {
                int index = children.indexOf(stacked);
                if (index > -1) {
                    children.remove(index);
                }
            }

            this.stackedsOnViewer.clear();
        }
    }

    public void setViewerPane(Node node) {
        if (this.viewerPane != null) {
            if (this.atomsViewer != null) {
                this.atomsViewer.unbindScene();
                this.atomsViewer = null;
            }

            if (this.stackedsOnViewer != null) {
                this.stackedsOnViewer.clear();
            }

            List<Node> children = this.viewerPane.getChildren();
            children.clear();

            if (node != null) {
                if (node instanceof AtomsViewerInterface) {
                    this.atomsViewer = (AtomsViewerInterface) node;
                    this.atomsViewer.bindSceneTo(this.viewerPane);
                }
                children.add(node);

                if (this.stackedsToViewer != null) {
                    while (!this.stackedsToViewer.isEmpty()) {
                        Node stacked = this.stackedsToViewer.poll();
                        this.stackOnViewerPane(stacked);
                    }
                }
            }
        }
    }

    public Node getViewerPane() {
        if (this.viewerPane != null) {
            List<Node> children = this.viewerPane.getChildren();
            if (!children.isEmpty()) {
                return children.get(0);
            }
        }

        return null;
    }

    public void setEditorPane(Node node) {
        if (this.projectPane != null) {
            this.projectPane.setRight(node);
        }
    }

    public Node getEditorPane() {
        return this.projectPane == null ? null : this.projectPane.getRight();
    }

    public void setOnViewerSelected(MenuItemSelected<Node> onViewerSelected) {
        this.onViewerSelected = onViewerSelected;
    }

    public void setOnEditorSelected(MenuItemSelected<String> onEditorSelected) {
        this.onEditorSelected = onEditorSelected;
    }

    public void addViewerMenuItems(Node... graphics) {
        if (graphics == null || graphics.length < 1) {
            return;
        }

        if (this.viewerMenu != null) {
            this.viewerMenu.clearItem();
            for (Node graphic : graphics) {
                if (graphic != null) {
                    this.viewerMenu.addItem(graphic);
                }
            }
        }
    }

    public void addEditorMenuItems(String... texts) {
        if (texts == null || texts.length < 1) {
            return;
        }

        if (this.editorMenu != null) {
            this.editorMenu.clearItem();
            for (String text : texts) {
                if (text != null) {
                    this.editorMenu.addItem(text);
                }
            }
        }
    }

    public void setEditorText(String text) {
        if (this.editorLabel != null) {
            this.editorLabel.setText((text == null ? "" : text) + " ");
        }
    }

    private void createViewerMenu() {
        this.viewerMenu = new QEFXTeethMenu(TEETH_WIDTH, TEETH_HEIGHT);

        this.viewerMenu.setOnMenuItemSelected(key -> {
            if (this.onViewerSelected != null) {
                this.onViewerSelected.onMenuItemSelected(key);
            }
            this.enableProjectPane();
        });

        this.viewerMenu.setOnMenuShowing(() -> {
            if (this.basePane != null) {
                this.basePane.getChildren().add(this.viewerMenu);
                this.disableProjectPane();
            }
        });
    }

    private void createEditorMenu() {
        this.editorMenu = new QEFXFanMenu(FAN_RADIUS, FAN_WIDTH);

        this.editorMenu.setOnMenuItemSelected(key -> {
            if (this.onEditorSelected != null) {
                this.onEditorSelected.onMenuItemSelected(key);
            }
            this.enableProjectPane();
        });

        this.editorMenu.setOnMenuShowing(() -> {
            if (this.basePane != null) {
                this.basePane.getChildren().add(this.editorMenu);
                this.disableProjectPane();
            }
        });
    }

    private void setupResizing() {
        if (this.basePane != null) {
            this.basePane.widthProperty().addListener(o -> this.resizeBasePane());
            this.basePane.heightProperty().addListener(o -> this.resizeBasePane());
        }

        if (this.viewerPane != null) {
            this.viewerPane.widthProperty().addListener(o -> this.resizeViewerPane());
            this.viewerPane.heightProperty().addListener(o -> this.resizeViewerPane());
        }
    }

    private void resizeBasePane() {
        if (this.basePane == null) {
            return;
        }

        double width = this.basePane.getWidth();
        double height = this.basePane.getHeight();
        if (width <= 0.0 || height <= 0.0) {
            return;
        }

        double xFan = Math.max(0.0, width - FAN_OFFSET);
        double yFan = Math.max(0.0, height - FAN_OFFSET);
        this.editorMenu.setLayoutX(xFan);
        this.editorMenu.setLayoutY(yFan);
    }

    private void resizeViewerPane() {
        if (this.basePane == null || this.viewerPane == null) {
            return;
        }

        double xViewer = 0.0;
        double yViewer = this.viewerPane.getHeight();
        Point2D pointViewer = this.viewerPane.localToScreen(xViewer, yViewer);
        Point2D pointBase = this.basePane.screenToLocal(pointViewer);
        double xRoll = pointBase.getX();
        double yRoll = pointBase.getY();
        this.viewerMenu.setLayoutX(xRoll);
        this.viewerMenu.setLayoutY(yRoll);
    }

    private void setupViewerButton() {
        if (this.viewerButton == null) {
            return;
        }

        this.viewerButton.setText("");
        this.viewerButton.setGraphic(SVGLibrary.getGraphic(SVGData.MENU, GRAPHIC_SIZE, null, GRAPHIC_CLASS));

        this.viewerButton.setOnAction(event -> {
            if (this.projectMode != MODE_NORMAL) {
                ModeBacked backedAction = null;
                if (this.backedActionMap != null) {
                    backedAction = this.backedActionMap.get(this.projectMode);
                }
                if (backedAction != null) {
                    boolean status = backedAction.onModeBacked(this);
                    if (!status) {
                        return;
                    }
                }
            }

            switch (this.projectMode) {
            case MODE_NORMAL:
                // show menu in normal mode
                this.viewerMenu.showMenu();
                break;
            case MODE_RESULT_EXPLORER:
                // shift result-explorer -> normal
                this.setNormalMode();
                break;
            case MODE_RESULT_VIEWER:
                // shift result-viewer -> result-explorer
                this.setResultExplorerMode();
                break;
            case MODE_MODELER:
                // shift modeler -> normal
                this.setNormalMode();
                break;
            case MODE_MODELER_SLAB:
                // shift modeler-slab -> modeler
                this.setModelerMode();
                break;
            case MODE_DESIGNER:
                // shift designer -> normal
                this.setNormalMode();
                break;
            }
        });
    }

    private void setupEditorButton() {
        if (this.editorButton == null) {
            return;
        }

        this.editorButton.setText("");
        this.editorButton.setGraphic(SVGLibrary.getGraphic(SVGData.CONTROL, GRAPHIC_SIZE, null, GRAPHIC_CLASS));

        this.editorButton.setOnAction(event -> {
            if (this.projectMode == MODE_NORMAL) {
                this.editorMenu.showMenu();
            }
        });
    }

    private void setupEditorLabel() {
        if (this.editorLabel == null) {
            return;
        }

        this.setEditorText("");
    }

    private void enableProjectPane() {
        if (this.projectPane != null) {
            this.projectPane.setDisable(false);
        }
    }

    private void disableProjectPane() {
        if (this.projectPane != null) {
            this.projectPane.setDisable(true);
        }
    }

    protected void setViewerActions(ViewerActions viewerActions) {
        this.viewerActions = viewerActions;
    }

    protected void setEditorActions(EditorActions editorActions) {
        this.editorActions = editorActions;
    }

    protected void pushViewerButton() {
        if (this.viewerButton == null) {
            return;
        }
        if (this.viewerButton.isDisable() || this.viewerButton.isDisabled()) {
            return;
        }

        EventHandler<ActionEvent> handler = this.viewerButton.getOnAction();
        if (handler != null) {
            handler.handle(new ActionEvent());
        }
    }

    public void toBeShown() {
        if (this.shownAction != null) {
            this.shownAction.actionOnProject(this);
        }
    }

    public void setOnShown(ProjectAction action) {
        this.shownAction = action;
    }

    public void detach() {
        if (this.detachAction != null) {
            this.detachAction.actionOnProject(this);
        }
    }

    public void setOnDetached(ProjectAction action) {
        this.detachAction = action;
    }

    public boolean saveFile() {
        return this.viewerActions.saveFile();
    }

    public void sceenShot() {
        this.viewerActions.screenShot();
    }

    public void sceenShot(Node subject) {
        this.viewerActions.screenShot(subject);
    }

    /*
     * ====================================================================
     *                        Control Project-Mode
     * ====================================================================
     */

    // Normal mode
    public boolean isNormalMode() {
        return this.projectMode == MODE_NORMAL;
    }

    public void setNormalMode() {
        this.setNormalMode(null);
    }

    public void setNormalMode(ModeRestored restoredAction) {
        this.storeProjectAnsatz();
        this.projectMode = MODE_NORMAL;
        if (this.restoreProjectAnsatz(restoredAction)) {
            return;
        }

        if (this.viewerButton != null) {
            this.viewerButton.setGraphic(
                    SVGLibrary.getGraphic(SVGData.MENU, GRAPHIC_SIZE, null, GRAPHIC_CLASS));
        }

        if (this.editorButton != null) {
            this.editorButton.setDisable(false);
            this.editorButton.setGraphic(
                    SVGLibrary.getGraphic(SVGData.CONTROL, GRAPHIC_SIZE, null, GRAPHIC_CLASS));
        }
    }

    // Result-Explorer mode
    public boolean isResultExplorerMode() {
        return this.projectMode == MODE_RESULT_EXPLORER;
    }

    public void setResultExplorerMode() {
        this.setResultExplorerMode(null);
    }

    public void setResultExplorerMode(ModeRestored restoredAction) {
        this.setAbnormalMode(MODE_RESULT_EXPLORER, restoredAction);
    }

    // Result-Viewer mode
    public boolean isResultViewerMode() {
        return this.projectMode == MODE_RESULT_VIEWER;
    }

    public void setResultViewerMode() {
        this.setResultViewerMode(null);
    }

    public void setResultViewerMode(ModeRestored restoredAction) {
        this.setAbnormalMode(MODE_RESULT_VIEWER, restoredAction);
    }

    // Modeler mode
    public boolean isModelerMode() {
        return this.projectMode == MODE_MODELER;
    }

    public void setModelerMode() {
        this.setModelerMode(null);
    }

    public void setModelerMode(ModeRestored restoredAction) {
        this.setAbnormalMode(MODE_MODELER, restoredAction);
    }

    // Modeler-Slab mode
    public boolean isModelerSlabMode() {
        return this.projectMode == MODE_MODELER_SLAB;
    }

    public void setModelerSlabMode() {
        this.setModelerSlabMode(null);
    }

    public void setModelerSlabMode(ModeRestored restoredAction) {
        this.setAbnormalMode(MODE_MODELER_SLAB, restoredAction);
    }

    // Modeler mode
    public boolean isDesignerMode() {
        return this.projectMode == MODE_DESIGNER;
    }

    public void setDesignerMode() {
        this.setDesignerMode(null);
    }

    public void setDesignerMode(ModeRestored restoredAction) {
        this.setAbnormalMode(MODE_DESIGNER, restoredAction);
    }

    private void setAbnormalMode(int projectMode, ModeRestored restoredAction) {
        this.storeProjectAnsatz();
        this.projectMode = projectMode;
        if (this.restoreProjectAnsatz(restoredAction)) {
            return;
        }

        if (this.viewerButton != null) {
            this.viewerButton.setGraphic(
                    SVGLibrary.getGraphic(SVGData.ARROW_LEFT, GRAPHIC_SIZE, null, GRAPHIC_CLASS));
        }

        if (this.editorButton != null) {
            this.editorButton.setDisable(true);
            this.editorButton.setGraphic(null);
        }

        this.setEditorText("");
    }

    private ProjectAnsatz getProjectAnsatzAlways() {
        if (this.projectAnsatzMap == null) {
            this.projectAnsatzMap = new HashMap<Integer, ProjectAnsatz>();
        }

        if (!this.projectAnsatzMap.containsKey(this.projectMode)) {
            this.projectAnsatzMap.put(this.projectMode, new ProjectAnsatz());
        }

        return this.projectAnsatzMap.get(this.projectMode);
    }

    private ProjectAnsatz getProjectAnsatzIfPossible() {
        if (this.projectAnsatzMap == null) {
            return null;
        }

        if (!this.projectAnsatzMap.containsKey(this.projectMode)) {
            return null;
        }

        return this.projectAnsatzMap.get(this.projectMode);
    }

    private List<Node> listViewerPaneNodes() {
        if (this.viewerPane != null) {
            List<Node> children = this.viewerPane.getChildren();
            if (children == null || children.isEmpty()) {
                return null;
            }

            List<Node> viewerPaneNodes = new ArrayList<Node>();

            if (this.atomsViewer != null) {
                viewerPaneNodes.add(this.atomsViewer);
                // avoid SubScene and AtomsViewer
                for (int i = 2; i < children.size(); i++) {
                    viewerPaneNodes.add(children.get(i));
                }

            } else {
                for (Node child : children) {
                    viewerPaneNodes.add(child);
                }
            }

            return viewerPaneNodes;
        }

        return null;
    }

    private void restoreViewerPaneNodes(List<Node> viewerPaneNodes) {
        if (viewerPaneNodes == null || viewerPaneNodes.isEmpty()) {
            this.setViewerPane(null);

        } else {
            this.setViewerPane(viewerPaneNodes.get(0));
            for (int i = 1; i < viewerPaneNodes.size(); i++) {
                this.stackOnViewerPane(viewerPaneNodes.get(i));
            }
        }
    }

    private boolean storeProjectAnsatz() {
        ProjectAnsatz projectAnsatz = this.getProjectAnsatzAlways();

        if (projectAnsatz != null) {
            List<Node> viewerPaneNodes = this.listViewerPaneNodes();
            projectAnsatz.setViewerPaneNodes(viewerPaneNodes);

            Node viewerMenuGraphic = this.viewerButton == null ? null : this.viewerButton.getGraphic();
            projectAnsatz.setViewerMenuGraphic(viewerMenuGraphic);

            Node editorPaneNode = this.getEditorPane();
            projectAnsatz.setEditorPaneNode(editorPaneNode);

            Node editorMenuGraphic = this.editorButton == null ? null : this.editorButton.getGraphic();
            projectAnsatz.setEditorMenuGraphic(editorMenuGraphic);

            boolean editorMenuDisable = this.editorButton == null ? false : this.editorButton.isDisable();
            projectAnsatz.setEditorMenuDisable(editorMenuDisable);

            String editorMenuText = this.editorLabel == null ? "" : this.editorLabel.getText();
            projectAnsatz.setEditorMenuText(editorMenuText);

            return true;
        }

        return false;
    }

    private boolean restoreProjectAnsatz(ModeRestored restoredAction) {
        if (restoredAction != null) {
            if (this.restoredActionMap == null) {
                this.restoredActionMap = new HashMap<Integer, ModeRestored>();
            }
            this.restoredActionMap.put(this.projectMode, restoredAction);
        }

        ProjectAnsatz projectAnsatz = this.getProjectAnsatzIfPossible();

        if (projectAnsatz != null) {
            List<Node> viewerPaneNodes = projectAnsatz.getViewerPaneNodes();
            this.restoreViewerPaneNodes(viewerPaneNodes);

            Node viewerMenuGraphic = projectAnsatz.getViewerMenuGraphic();
            if (this.viewerButton != null) {
                this.viewerButton.setGraphic(viewerMenuGraphic);
            }

            Node editorPaneNode = projectAnsatz.getEditorPaneNode();
            this.setEditorPane(editorPaneNode);

            Node editorMenuGraphic = projectAnsatz.getEditorMenuGraphic();
            if (this.editorButton != null) {
                this.editorButton.setGraphic(editorMenuGraphic);
            }

            boolean editorMenuDisable = projectAnsatz.isEditorMenuDisable();
            if (this.editorButton != null) {
                this.editorButton.setDisable(editorMenuDisable);
            }

            String editorMenuText = projectAnsatz.getEditorMenuText();
            if (this.editorLabel != null) {
                this.editorLabel.setText(editorMenuText);
            }

            ModeRestored restoredAction2 = null;
            if (this.restoredActionMap != null) {
                restoredAction2 = this.restoredActionMap.get(this.projectMode);
            }
            if (restoredAction2 != null) {
                restoredAction2.onModeRestored(this);
            }

            return true;
        }

        return false;
    }

    public void setOnModeBacked(ModeBacked backedAction) {
        if (backedAction != null) {
            if (this.backedActionMap == null) {
                this.backedActionMap = new HashMap<Integer, ModeBacked>();
            }
            this.backedActionMap.put(this.projectMode, backedAction);

        } else {
            if (this.backedActionMap != null) {
                this.backedActionMap.remove(this.projectMode);
            }
        }
    }
}
