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

package burai.app.tab;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.event.Event;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.web.WebEngine;
import burai.app.QEFXMainController;
import burai.app.project.QEFXProject;
import burai.app.web.QEFXWeb;
import burai.project.Project;
import burai.pseudo.PseudoLibrary;

public class QEFXTabManager {

    private QEFXMainController controller;

    private TabPane tabPane;

    public QEFXTabManager(QEFXMainController controller, TabPane tabPane) {
        if (controller == null) {
            throw new IllegalArgumentException("controller is null.");
        }

        if (tabPane == null) {
            throw new IllegalArgumentException("tabPane is null.");
        }

        this.controller = controller;
        this.tabPane = tabPane;
        this.setupTabPane();
    }

    private void setupTabPane() {
        this.tabPane.setOnKeyPressed(event -> {
            if (event == null) {
                return;
            }

            if (event.isShortcutDown() && KeyCode.W.equals(event.getCode())) {
                // Shortcut + W
                SingleSelectionModel<Tab> selectionModel = this.tabPane.getSelectionModel();
                if (selectionModel != null) {
                    Tab tab = null;
                    int index = selectionModel.getSelectedIndex();
                    if (index > 0) {
                        tab = this.tabPane.getTabs().get(index);
                    }

                    Event event2 = new Event(Event.ANY);

                    if (tab != null) {
                        if (!event2.isConsumed()) {
                            if (tab.getOnCloseRequest() != null) {
                                tab.getOnCloseRequest().handle(event2);
                            }
                        }
                    }

                    tab = null;
                    if (!event2.isConsumed()) {
                        if (index > 0) {
                            tab = this.tabPane.getTabs().remove(index);
                        }
                    }

                    if (tab != null) {
                        if (!event2.isConsumed()) {
                            if (tab.getOnClosed() != null) {
                                tab.getOnClosed().handle(event2);
                            }
                        }
                    }
                }
            }
        });
    }

    public boolean showHomeTab() {
        SingleSelectionModel<Tab> selectionModel = this.tabPane.getSelectionModel();
        if (selectionModel != null) {
            selectionModel.select(0);
            this.tabPane.requestFocus();
            return true;
        }

        return false;
    }

    public Project showTab(Project project) {
        if (project == null) {
            return null;
        }

        QEFXProjectTab projectTab = new QEFXProjectTab(project);
        int index = this.tabPane.getTabs().indexOf(projectTab);

        if (index > -1) {
            Tab tab = this.tabPane.getTabs().get(index);
            if (tab instanceof QEFXProjectTab) {
                projectTab = (QEFXProjectTab) tab;
            }

        } else {
            PseudoLibrary.getInstance().waitToLoad();
            project.resolveQEInputs();

            QEFXProject qefxProject = null;
            try {
                qefxProject = new QEFXProject(this.controller, project);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            projectTab.setContent(qefxProject.getNode());
            projectTab.setProjectController(qefxProject.getController());
            this.tabPane.getTabs().add(projectTab);

            project.markQEInputs();
        }

        SingleSelectionModel<Tab> selectionModel = this.tabPane.getSelectionModel();
        if (selectionModel != null) {
            selectionModel.select(projectTab);
            this.tabPane.requestFocus();
            return project;
        }

        return null;
    }

    public boolean hideTab(Project project) {
        if (project == null) {
            return false;
        }

        Tab tab = new QEFXProjectTab(project);
        int index = this.tabPane.getTabs().indexOf(tab);

        if (index < 0) {
            return false;
        }

        tab = this.tabPane.getTabs().remove(index);
        if (tab != null) {
            Event event = new Event(Event.ANY);
            if (!event.isConsumed()) {
                if (tab.getOnClosed() != null) {
                    tab.getOnClosed().handle(event);
                }
            }
            return true;
        }

        return false;
    }

    public List<Project> getProjects() {
        List<Project> projects = new ArrayList<Project>();

        List<Tab> tabs = this.tabPane.getTabs();
        for (Tab tab : tabs) {
            if (tab != null && (tab instanceof QEFXProjectTab)) {
                Project project = ((QEFXProjectTab) tab).getBody();
                if (project != null) {
                    projects.add(project);
                }
            }
        }

        return projects;
    }

    public WebEngine showTab(String url) {
        if (url == null || url.trim().isEmpty()) {
            return null;
        }

        QEFXWeb qefxWeb = null;
        try {
            qefxWeb = new QEFXWeb(this.controller, url);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        WebEngine engine = qefxWeb.getEngine();
        if (engine == null) {
            return null;
        }

        Tab tab = new QEFXWebTab(engine);
        int index = this.tabPane.getTabs().indexOf(tab);

        if (index > -1) {
            tab = this.tabPane.getTabs().get(index);

        } else {
            tab.setContent(qefxWeb.getNode());
            this.tabPane.getTabs().add(tab);
        }

        SingleSelectionModel<Tab> selectionModel = this.tabPane.getSelectionModel();
        if (selectionModel != null) {
            selectionModel.select(tab);
            this.tabPane.requestFocus();
            return engine;
        }

        return null;
    }

    public boolean hideTab(WebEngine engine) {
        if (engine == null) {
            return false;
        }

        QEFXWebTab webTab = new QEFXWebTab(engine);
        List<Tab> tabs = this.tabPane.getTabs();

        int numTabs = tabs == null ? 0 : tabs.size();
        if (numTabs < 1) {
            return false;
        }

        for (int i = 0; i < numTabs; i++) {
            Tab tab = tabs.get(i);
            if (webTab.equalsEngine(tab)) {
                tabs.remove(i);
                Event event = new Event(Event.ANY);
                if (!event.isConsumed()) {
                    if (tab.getOnClosed() != null) {
                        tab.getOnClosed().handle(event);
                    }
                }
                return true;
            }
        }

        return false;
    }

    public boolean hideAllTabs() {
        List<Tab> tabs = this.tabPane.getTabs();
        if (tabs == null || tabs.isEmpty()) {
            return true;
        }

        Tab[] tabArray = new Tab[tabs.size()];
        tabArray = tabs.toArray(tabArray);
        if (tabArray.length < 1) {
            return false;
        }

        for (int i = 1; i < tabArray.length; i++) { // avoid home tab
            Tab tab = tabArray[i];
            if (tab == null) {
                continue;
            }

            boolean status = tabs.remove(tab);
            if (!status) {
                return false;
            }

            Event event = new Event(Event.ANY);
            if (!event.isConsumed()) {
                if (tab.getOnClosed() != null) {
                    tab.getOnClosed().handle(event);
                }
            }
        }

        return true;
    }
}
