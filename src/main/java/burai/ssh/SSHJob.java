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

package burai.ssh;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import burai.com.file.FileTools;
import burai.input.QEInput;
import burai.input.card.QEAtomicSpecies;
import burai.project.Project;
import burai.pseudo.PseudoPotential;
import burai.run.InputEditor;
import burai.run.RunningCondition;
import burai.run.RunningType;

public class SSHJob {

    private static final String DUMMY_INP_NAME = "__INP_NAME__";

    private Project project;

    private SSHServer sshServer;

    private RunningType type;

    private int numProcesses;

    private int numThreads;

    private File scriptFile;

    private List<File> inpFiles;

    private Set<File> pseudoFiles;

    public SSHJob(Project project, SSHServer sshServer) {
        if (project == null) {
            throw new IllegalArgumentException("project is null.");
        }

        if (sshServer == null) {
            throw new IllegalArgumentException("sshServer is null.");
        }

        this.project = project;

        this.sshServer = sshServer;

        this.type = RunningType.SCF;
        this.numProcesses = 1;
        this.numThreads = 1;

        this.scriptFile = null;
        this.inpFiles = null;
        this.pseudoFiles = null;
    }

    public Project getProject() {
        return this.project;
    }

    public SSHServer getSSHServer() {
        return this.sshServer;
    }

    public RunningType getType() {
        return this.type;
    }

    public void setType(RunningType type) {
        if (type != null) {
            this.type = type;
        }
    }

    public int getNumProcesses() {
        return this.numProcesses;
    }

    public void setNumProcesses(int numProcesses) {
        if (numProcesses > 0) {
            this.numProcesses = numProcesses;
        }
    }

    public int getNumThreads() {
        return this.numThreads;
    }

    public void setNumThreads(int numThreads) {
        if (numThreads > 0) {
            this.numThreads = numThreads;
        }
    }

    public boolean postJobToServer() {
        this.setupFiles();

        if (this.scriptFile == null) {
            return false;
        }

        this.ftpFile(this.scriptFile);

        if (this.inpFiles != null) {
            for (File inpFile : this.inpFiles) {
                if (inpFile != null) {
                    this.ftpFile(inpFile);
                }
            }
        }

        if (this.pseudoFiles != null) {
            for (File pseudoFile : this.pseudoFiles) {
                if (pseudoFile != null) {
                    this.ftpFile(pseudoFile);
                }
            }
        }

        // TODO

        return true;
    }

    private void ftpFile(File file) {
        if (file == null) {
            return;
        }

        try {
            if (!file.isFile()) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // TODO
    }

    private void setupFiles() {

        this.scriptFile = null;

        if (this.inpFiles == null) {
            this.inpFiles = new ArrayList<File>();
        } else {
            this.inpFiles.clear();
        }

        if (this.pseudoFiles == null) {
            this.pseudoFiles = new HashSet<File>();
        } else {
            this.pseudoFiles.clear();
        }

        File directory = this.getDirectory();
        if (directory == null) {
            return;
        }

        this.project.resolveQEInputs();
        QEInput input = this.type.getQEInput(this.project);
        if (input == null) {
            return;
        }

        List<String[]> commandList = this.type.getUnixCommandList(DUMMY_INP_NAME, this.numProcesses);
        if (commandList == null || commandList.isEmpty()) {
            return;
        }

        List<RunningCondition> conditionList = this.type.getConditionList();
        if (conditionList == null || conditionList.size() < commandList.size()) {
            return;
        }

        List<InputEditor> inputEditorList = this.type.getInputEditorList(this.project);
        if (inputEditorList == null || inputEditorList.size() < commandList.size()) {
            return;
        }

        List<String> inpNameList = this.type.getInpNameList(this.project);
        if (inpNameList == null || inpNameList.size() < commandList.size()) {
            return;
        }

        List<String> logNameList = this.type.getLogNameList(this.project);
        if (logNameList == null || logNameList.size() < commandList.size()) {
            return;
        }

        List<String> errNameList = this.type.getErrNameList(this.project);
        if (errNameList == null || errNameList.size() < commandList.size()) {
            return;
        }

        this.deleteExitFile(directory);

        List<String> qeCommands = new ArrayList<String>();

        for (int i = 0; i < commandList.size(); i++) {
            String[] command = commandList.get(i);
            if (command == null || command.length < 1) {
                continue;
            }

            RunningCondition condition = conditionList.get(i);
            if (condition == null) {
                continue;
            }

            InputEditor inputEditor = inputEditorList.get(i);
            if (inputEditor == null) {
                continue;
            }

            String inpName = inpNameList.get(i);
            inpName = inpName == null ? null : inpName.trim();
            if (inpName == null || inpName.isEmpty()) {
                continue;
            }

            String logName = logNameList.get(i);
            logName = logName == null ? null : logName.trim();
            if (logName == null || logName.isEmpty()) {
                continue;
            }

            String errName = errNameList.get(i);
            errName = errName == null ? null : errName.trim();
            if (errName == null || errName.isEmpty()) {
                continue;
            }

            QEInput input2 = inputEditor.editInput(input);
            if (input2 == null) {
                continue;
            }

            if (!condition.toRun(this.project, input2)) {
                continue;
            }

            String command0 = null;
            for (String token : command) {
                token = token == null ? null : token.trim();
                if (token == null || token.isEmpty()) {
                    continue;
                }
                if (DUMMY_INP_NAME.equals(token)) {
                    token = inpName;
                }
                if (command0 == null) {
                    command0 = token;
                } else {
                    command0 = command0 + " " + token;
                }
            }

            command0 = command0 == null ? null : command0.trim();
            if (command0 == null || command0.isEmpty()) {
                continue;
            }

            command0 = command0 + " </dev/null";
            command0 = command0 + " 1>" + logName;
            command0 = command0 + " 2>" + errName;

            File inpFile = new File(directory, inpName);
            boolean inpStatus = this.writeQEInput(input2, inpFile);
            if (!inpStatus) {
                continue;
            }

            File logFile = new File(directory, logName);
            File errFile = new File(directory, errName);
            this.deleteLogFiles(logFile, errFile);

            qeCommands.add(command0);

            this.inpFiles.add(inpFile);

            QEAtomicSpecies atomicSpecies = input2.getCard(QEAtomicSpecies.class);
            if (atomicSpecies != null) {
                int numSpec = atomicSpecies.numSpecies();
                for (int iSpec = 0; iSpec < numSpec; iSpec++) {
                    PseudoPotential pseudoPot = atomicSpecies.getPseudoPotential(iSpec);
                    File pseudoFile = pseudoPot == null ? null : pseudoPot.getFile();
                    if (pseudoFile != null) {
                        this.pseudoFiles.add(pseudoFile);
                    }
                }
            }
        }

        if (qeCommands != null && (!qeCommands.isEmpty())) {
            this.scriptFile = this.writeScript(directory, qeCommands);
        }
    }

    private File getDirectory() {
        String dirPath = this.project.getDirectoryPath();
        if (dirPath == null) {
            return null;
        }

        File dirFile = new File(dirPath);
        try {
            if (!dirFile.isDirectory()) {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return dirFile;
    }

    private boolean writeQEInput(QEInput input, File file) {
        if (input == null) {
            return false;
        }

        if (file == null) {
            return false;
        }

        String strInput = input.toString();
        if (strInput == null) {
            return false;
        }

        PrintWriter writer = null;

        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            writer.println(strInput);

        } catch (IOException e) {
            e.printStackTrace();
            return false;

        } finally {
            if (writer != null) {
                writer.close();
            }
        }

        return true;
    }

    private File writeScript(File directory, List<String> commands) {
        if (directory == null) {
            return null;
        }

        if (commands == null || commands.isEmpty()) {
            return null;
        }

        String scriptContent = this.sshServer.getJobScript(commands, this.numProcesses, this.numThreads);
        if (scriptContent == null || scriptContent.isEmpty()) {
            return null;
        }

        String ext = ".sh";
        if (scriptContent.startsWith("#!/bin/sh")) {
            ext = ".sh";
        } else if (scriptContent.startsWith("#!/bin/bash")) {
            ext = ".bash";
        } else if (scriptContent.startsWith("#!/bin/csh")) {
            ext = ".csh";
        } else if (scriptContent.startsWith("#!/bin/tcsh")) {
            ext = ".tcsh";
        } else if (scriptContent.startsWith("#!/bin/zsh")) {
            ext = ".zsh";
        }

        String name = this.project.getPrefixName();
        name = name == null ? null : name.trim() + ext;
        File file = new File(directory, name);

        PrintWriter writer = null;

        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            writer.print(scriptContent.replaceAll("[\\r\\n]+", "\\n"));

        } catch (IOException e) {
            e.printStackTrace();
            return null;

        } finally {
            if (writer != null) {
                writer.close();
            }
        }

        return file;
    }

    private void deleteExitFile(File directory) {
        if (directory == null) {
            return;
        }

        String exitName = this.project.getExitFileName();
        exitName = exitName == null ? null : exitName.trim();
        if (exitName != null && (!exitName.isEmpty())) {
            try {
                File exitFile = new File(directory, exitName);
                if (exitFile.exists()) {
                    FileTools.deleteAllFiles(exitFile, false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteLogFiles(File logFile, File errFile) {
        try {
            if (logFile != null && logFile.exists()) {
                FileTools.deleteAllFiles(logFile, false);
            }

            if (errFile != null && errFile.exists()) {
                FileTools.deleteAllFiles(errFile, false);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
