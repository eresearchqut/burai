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
import java.io.PrintStream;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import burai.app.explorer.QEFXExplorer;
import burai.app.icon.web.WebEngineFactory;
import burai.app.proxy.ProxyServer;
import burai.com.env.Environments;
import burai.com.file.FileTools;
import burai.com.life.Life;
import burai.com.path.QEPath;
import burai.pseudo.PseudoLibrary;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class QEFXMain extends Application {

    private static Stage mainStage = null;

    private static final String[] STYLE_SHEET_NAMES = {
            "QEFXApplication.css",
            "QEFXAppText.css",
            "QEFXAppButton.css",
            "QEFXAppToggle.css",
            "QEFXAppMenuItem.css",
            "QEFXAppExplorer.css",
            "QEFXAppIcon.css",
            "QEFXAppViewer.css",
            "QEFXAppResult.css",
            Environments.isLinux() ? "QEFXAppFontLinux.css" : "QEFXAppFont.css"
    };

    private static final String OUT_LOG_NAME = "_logOut.txt";
    private static final String ERR_LOG_NAME = "_logErr.txt";

    private static final long SLEEP_TIME_FOR_INIT_FILES = 750L;

    public static void initializeStyleSheets(List<String> stylesheets) {
        if (stylesheets != null) {
            stylesheets.clear();

            for (String styleSheetName : STYLE_SHEET_NAMES) {
                URL url = null;
                if (styleSheetName != null) {
                    url = QEFXMain.class.getResource(styleSheetName);
                }

                String cssName = null;
                if (url != null) {
                    cssName = url.toExternalForm();
                }
                if (cssName != null) {
                    cssName = cssName.trim();
                }

                if (cssName != null && (!cssName.isEmpty())) {
                    stylesheets.add(cssName);
                }
            }
        }
    }

    public static void initializeTitleBarIcon(Stage stage) {
        if (stage != null) {
            URL url = QEFXMain.class.getResource("resource/image/icon_048.png");
            String iconName = url == null ? null : url.toExternalForm();
            if (iconName != null && (!iconName.isEmpty())) {
                Image icon = new Image(iconName);
                stage.getIcons().add(icon);
            }
        }
    }

    public static void initializeDialogOwner(Dialog<?> dialog) {
        if (dialog != null) {
            dialog.initOwner(mainStage);
        }
    }

    @Override
    public void init() {
        Locale.setDefault(Locale.ENGLISH);

        if (Environments.existsProjectsPath()) {
            this.setLogFiles();

        } else {
            this.setLogFiles();
            this.copyPseudos();
            this.copyExamples();
        }

        this.setBinaryPath();

        PseudoLibrary.getInstance().touch();

        ProxyServer.initProxyServer();
    }

    private void copyPseudos() {
        String pseudoPath = Environments.getPseudosPath();
        if (pseudoPath == null || pseudoPath.isEmpty()) {
            return;
        }

        File srcFile = new File("pseudopot");
        File dstFile = new File(pseudoPath);

        Thread thread = new Thread(() -> {
            FileTools.copyAllFiles(srcFile, dstFile, false);
        });

        thread.start();
    }

    private void copyExamples() {
        String projectsPath = Environments.getProjectsPath();
        if (projectsPath == null || projectsPath.isEmpty()) {
            return;
        }

        File srcFile = new File("examples");
        File dstFile = new File(projectsPath, "Examples");
        FileTools.copyAllFiles(srcFile, dstFile, false);
    }

    private void setBinaryPath() {
        if (Environments.isWindows()) {
            this.setBinaryPathWindows();
        } else if (Environments.isMac() || Environments.isLinux()) {
            this.setBinaryPathUnix();
        }
    }

    private void setBinaryPathWindows() {
        File execDir = new File("exec.WIN");

        String qePath = QEPath.getPath();
        if (qePath == null || qePath.trim().isEmpty()) {
            QEPath.setPath(new File(execDir, "qe"));
        }

        String mpiPath = QEPath.getMPIPath();
        if (mpiPath == null || mpiPath.trim().isEmpty()) {
            QEPath.setMPIPath(new File(execDir, "mpi"));
        }
    }

    private void setBinaryPathUnix() {
        File mpiDir = null;
        File qeMpiFile = null;
        File qeSerFile = null;
        boolean canMPI = false;
        boolean canSer = false;

        if (Environments.isMac()) {
            File execDir = new File("exec.MAC");
            qeMpiFile = new File(execDir, "qe_openmpi-gcc6");
            qeSerFile = new File(execDir, "qe_serial-gcc6");

            try {
                mpiDir = new File("/opt/local/libexec/openmpi-gcc6");
                if (mpiDir.isDirectory()) {
                    canMPI = true;
                }
            } catch (Exception e) {
                canMPI = false;
            }

            canSer = true;

        } else { //if (Environments.isLinux()) {
            File execDir = new File("exec.LINUX");
            qeMpiFile = new File(execDir, "qe_openmpi");
            qeSerFile = new File(execDir, "qe_serial");

            boolean hasMPI = false;
            try {
                File mpiFile = new File("/usr/bin/mpirun");
                if (mpiFile.isFile()) {
                    mpiDir = mpiFile.getParentFile();
                    hasMPI = (mpiDir != null);
                }
            } catch (Exception e) {
                hasMPI = false;
            }

            boolean hasGcc = false;
            try {
                File gccFile = new File("/usr/bin/gfortran");
                if (gccFile.isFile()) {
                    hasGcc = true;
                }
            } catch (Exception e) {
                hasGcc = false;
            }

            canMPI = hasMPI && hasGcc;
            canSer = hasGcc;
        }

        if (canMPI && mpiDir != null) {
            String qePath = QEPath.getPath();
            String qeSerPath = qeSerFile.getAbsolutePath();
            if (qePath == null || qePath.trim().isEmpty() || qePath.equals(qeSerPath)) {
                QEPath.setPath(qeMpiFile);
            }

            String mpiPath = QEPath.getMPIPath();
            if (mpiPath == null || mpiPath.trim().isEmpty()) {
                QEPath.setMPIPath(mpiDir);
            }

        } else if (canSer) {
            String qePath = QEPath.getPath();
            if (qePath == null || qePath.trim().isEmpty()) {
                QEPath.setPath(qeSerFile);
            }
        }
    }

    private void setLogFiles() {
        String debug = null;
        try {
            debug = System.getProperty("debug", null);
        } catch (Exception e) {
            debug = null;
        }

        if (debug != null) {
            return;
        }

        String projectsPath = Environments.getProjectsPath();
        if (projectsPath == null || projectsPath.isEmpty()) {
            return;
        }

        PrintStream outStream = null;
        PrintStream errStream = null;

        try {
            File outFile = new File(projectsPath, OUT_LOG_NAME);
            outStream = new PrintStream(outFile); // not buffered to flush always
        } catch (Exception e) {
        }

        try {
            File errFile = new File(projectsPath, ERR_LOG_NAME);
            errStream = new PrintStream(errFile); // not buffered to flush always
        } catch (Exception e) {
        }

        if (outStream != null) {
            try {
                System.setOut(outStream);
            } catch (Exception e) {
            }
        }

        if (outStream != null) {
            try {
                System.setErr(errStream);
            } catch (Exception e) {
            }
        }

        PrintStream outStream_ = outStream;
        PrintStream errStream_ = errStream;

        Life.getInstance().addOnDead(() -> {
            if (outStream_ != null) {
                outStream_.close();
            }
            if (errStream_ != null) {
                errStream_.close();
            }
        });
    }

    @Override
    public void start(Stage stage) {
        try {
            // initial operation
            mainStage = stage;
            WebEngineFactory.getInstance().touchAllWebEngines();

            // create QEFXMainController
            QEFXMainController controller = new QEFXMainController();

            // create Root (load FXML)
            FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("QEFXMain.fxml"));
            fxmlLoader.setController(controller);
            Parent root = fxmlLoader.load();

            // create Scene
            Scene scene = new Scene(root);
            setUserAgentStylesheet(STYLESHEET_MODENA);
            initializeStyleSheets(scene.getStylesheets());
            mainStage.setScene(scene);

            // setup QEFXMainController
            controller.setStage(mainStage);
            controller.setMaximized(Environments.isWindows());
            controller.setFullScreen(false);
            controller.setResizable(true);
            controller.setExplorer(new QEFXExplorer(controller));

            // show Stage
            initializeTitleBarIcon(mainStage);
            mainStage.show();

            // files from command-line arguments
            this.showInitialFiles(controller);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showInitialFiles(QEFXMainController controller) {
        if (controller == null) {
            return;
        }

        Parameters parameters = this.getParameters();
        if (parameters == null) {
            return;
        }

        List<String> strList = parameters.getRaw();
        if (strList == null || strList.isEmpty()) {
            return;
        }

        Thread thread = new Thread(() -> {
            for (String str : strList) {
                if (str != null && !(str.isEmpty())) {
                    controller.showFile(str);
                }

                synchronized (strList) {
                    try {
                        strList.wait(SLEEP_TIME_FOR_INIT_FILES);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
