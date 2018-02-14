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

package burai.run;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import burai.com.env.Environments;
import burai.com.path.QEPath;

public class RunningCommand {

    public static enum RunningCommandType {
        MPIRUN("command_mpirun"),
        PWSCF("command_pwscf"),
        DOS("command_dos"),
        PROJWFC("command_projwfc"),
        BAND("command_band");

        private String propKey;

        private RunningCommandType(String propKey) {
            this.propKey = propKey;
        }

        public String getCommand() {
            String command = Environments.getProperty(this.propKey);
            return command == null ? null : command.trim();
        }

        public String getUnixCommand() {
            String command = Environments.getUnixProperty(this.propKey);
            return command == null ? null : command.trim();
        }

        public void setCommand(String command) {
            if (command == null || command.trim().isEmpty()) {
                Environments.removeProperty(this.propKey);
            } else {
                Environments.setProperty(this.propKey, command.trim());
            }
        }
    }

    private static final String VAR_PROC = "\\$NP";
    private static final String VAR_KPARA = "\\$NK";
    private static final String VAR_INPUT = "\\$IN";

    private String process;
    private String kParallel;
    private String input;

    private RunningCommandType commandType;

    public RunningCommand(RunningCommandType commandType) {
        if (commandType == null) {
            throw new IllegalArgumentException("commandType is null");
        }

        this.process = null;
        this.kParallel = null;
        this.input = null;

        this.commandType = commandType;
    }

    public void setProcess(int process) {
        this.setProcess(Integer.toString(process));
    }

    public void setProcess(String process) {
        this.process = process == null ? null : process.trim();
    }

    public void setKParallel(int kParallel) {
        this.setKParallel(Integer.toString(kParallel));
    }

    public void setKParallel(String kParallel) {
        this.kParallel = kParallel == null ? null : kParallel.trim();
    }

    public void setInput(String input) {
        this.input = input == null ? null : input.trim();
    }

    public String[] getCommand() {
        return this.getCommand(false);
    }

    public String[] getCommand(boolean unixServer) {
        int iProc = this.intProcess();
        int iKpara = this.intKParallel();

        String mpiCommand = null;
        if (iProc != 1) {
            if (unixServer) {
                mpiCommand = RunningCommandType.MPIRUN.getUnixCommand();
            } else {
                mpiCommand = RunningCommandType.MPIRUN.getCommand();
            }
        }

        String mainCommand = null;
        if (this.commandType != RunningCommandType.MPIRUN) {
            if (unixServer) {
                mainCommand = this.commandType.getUnixCommand();
            } else {
                mainCommand = this.commandType.getCommand();
            }
        }

        List<String> mpiCommandList = null;
        if (mpiCommand != null && !(mpiCommand.isEmpty())) {
            if (unixServer) {
                mpiCommandList = this.splitCommand(mpiCommand, null);
            } else {
                mpiCommandList = this.splitCommand(mpiCommand, QEPath.getMPIPath());
            }
        }

        List<String> mainCommandList = null;
        if (mainCommand != null && !(mainCommand.isEmpty())) {
            if (unixServer) {
                mainCommandList = this.splitCommand(mainCommand, null);
            } else {
                mainCommandList = this.splitCommand(mainCommand, QEPath.getPath());
            }
        }

        List<String> commandList = null;

        if (mpiCommandList != null && !(mpiCommandList.isEmpty())) {
            if (commandList == null) {
                commandList = new ArrayList<String>();
            }
            commandList.addAll(mpiCommandList);
        }

        if (mainCommandList != null && !(mainCommandList.isEmpty())) {
            if (commandList == null) {
                commandList = new ArrayList<String>();
            }
            commandList.addAll(mainCommandList);
        }

        if (commandList == null || commandList.isEmpty()) {
            return null;
        }

        String[] command = new String[commandList.size()];
        command = commandList.toArray(command);
        if (command == null || command.length < 1) {
            return null;
        }

        for (int i = 0; i < command.length; i++) {
            if (command[i] == null || command[i].isEmpty()) {
                continue;
            }

            if (this.process != null && !(this.process.isEmpty())) {
                if (iProc > 0) {
                    command[i] = command[i].replaceAll(VAR_PROC, Integer.toString(iProc));
                } else {
                    command[i] = command[i].replaceAll(VAR_PROC, this.process);
                }
            }

            if (this.kParallel != null && !(this.kParallel.isEmpty())) {
                if (iKpara > 0) {
                    command[i] = command[i].replaceAll(VAR_KPARA, Integer.toString(iKpara));
                } else {
                    command[i] = command[i].replaceAll(VAR_KPARA, this.kParallel);
                }
            }

            if (this.input != null && !(this.input.isEmpty())) {
                command[i] = command[i].replaceAll(VAR_INPUT, this.input);
            }
        }

        return command;
    }

    private int intProcess() {
        if (this.process == null || this.process.isEmpty()) {
            return 1;
        }

        int i = 0;
        try {
            i = Integer.parseInt(this.process);
        } catch (NumberFormatException e) {
            i = -1;
        }

        return i;
    }

    private int intKParallel() {
        if (this.kParallel == null || this.kParallel.isEmpty()) {
            return 1;
        }

        int i = 0;
        try {
            i = Integer.parseInt(this.kParallel);
        } catch (NumberFormatException e) {
            i = -1;
        }

        return i;
    }

    private List<String> splitCommand(String command, String path) {
        String command_ = command == null ? null : command.trim();
        String path_ = path == null ? null : path.trim();

        if (command_ == null || command_.isEmpty()) {
            return null;
        }

        String[] commands = command_.split("\\s+");
        if (commands == null || commands.length < 1) {
            return null;
        }

        String command0 = commands[0];
        if (command0 == null || command0.isEmpty()) {
            return null;
        }

        if (path_ != null && !(path_.isEmpty())) {
            boolean hasCommand = false;

            File file = new File(path_, command0);
            try {
                if (file.isFile()) {
                    command0 = file.getPath();
                    hasCommand = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if ((!hasCommand) && Environments.isWindows()) {
                for (String ext : new String[] { ".exe", ".EXE" }) {
                    File file2 = new File(path_, command0 + ext);
                    try {
                        if (file2.isFile()) {
                            command0 = file2.getPath();
                            hasCommand = true;
                            break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        List<String> commandList = new ArrayList<String>();
        commandList.add(command0);

        for (int i = 1; i < commands.length; i++) {
            if (commands[i] != null && !(commands[i].isEmpty())) {
                commandList.add(commands[i]);
            }
        }

        return commandList;
    }
}
