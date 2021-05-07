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

import java.util.ArrayList;
import java.util.List;

import burai.atoms.model.Cell;
import burai.atoms.model.property.CellProperty;
import burai.com.env.Environments;
import burai.input.QEInput;
import burai.input.card.QECellParameters;
import burai.input.card.QEKPoint;
import burai.input.card.QEKPoints;
import burai.input.namelist.QENamelist;
import burai.input.namelist.QEValue;
import burai.project.Project;
import burai.project.property.ProjectBandPaths;
import burai.project.property.ProjectProperty;
import burai.project.property.ProjectStatus;
import burai.run.RunningCommand.RunningCommandType;
import burai.run.parser.BandPathParser;
import burai.run.parser.FermiParser;
import burai.run.parser.GeometryParser;
import burai.run.parser.LogParser;
import burai.run.parser.ScfParser;
import burai.run.parser.VoidParser;

public enum RunningType {
    SCF("SCF", Project.INPUT_MODE_SCF),
    OPTIMIZ("Optimize", Project.INPUT_MODE_OPTIMIZ),
    MD("MD", Project.INPUT_MODE_MD),
    DOS("DOS", Project.INPUT_MODE_DOS),
    BAND("Band", Project.INPUT_MODE_BAND);

    private String label;

    private int inputMode;

    private RunningType(String label, int inputMode) {
        this.label = label;
        this.inputMode = inputMode;
    }

    @Override
    public String toString() {
        return label;
    }

    public static RunningType getRunningType(Project project) {
        if (project == null) {
            return null;
        }

        int inputMode = project.getInputMode();

        switch (inputMode) {
        case Project.INPUT_MODE_SCF:
            return SCF;

        case Project.INPUT_MODE_OPTIMIZ:
            return OPTIMIZ;

        case Project.INPUT_MODE_MD:
            return MD;

        case Project.INPUT_MODE_DOS:
            return DOS;

        case Project.INPUT_MODE_BAND:
            return BAND;

        default:
            return null;
        }
    }

    public QEInput getQEInput(Project project) {
        if (project == null) {
            return null;
        }

        QEInput srcInput = null;
        QEInput dstInput = null;

        switch (this.inputMode) {
        case Project.INPUT_MODE_SCF:
            srcInput = project.getQEInputScf();
            break;

        case Project.INPUT_MODE_OPTIMIZ:
            srcInput = project.getQEInputOptimiz();
            break;

        case Project.INPUT_MODE_MD:
            srcInput = project.getQEInputMd();
            break;

        case Project.INPUT_MODE_DOS:
            srcInput = project.getQEInputDos();
            break;

        case Project.INPUT_MODE_BAND:
            srcInput = project.getQEInputBand();
            break;

        default:
            srcInput = null;
            break;
        }

        if (srcInput != null) {
            dstInput = srcInput.copy();
        }

        if (dstInput != null) {
            this.modifyQEInput(dstInput, project);
        }

        return dstInput;
    }

    private void modifyQEInput(QEInput input, Project project) {
        if (input == null) {
            return;
        }

        if (project == null) {
            return;
        }

        String fileName = project.getRelatedFileName();
        fileName = fileName == null ? null : fileName.trim();

        String prefix = project.getPrefixName();
        prefix = prefix == null ? null : prefix.trim();

        QENamelist nmlControl = input.getNamelist(QEInput.NAMELIST_CONTROL);
        if (nmlControl != null) {
            if (fileName != null && (!fileName.isEmpty())) {
                nmlControl.setValue("title = '" + fileName + "(" + this.label + ")'");
            }

            if (prefix != null && (!prefix.isEmpty())) {
                nmlControl.setValue("prefix = '" + prefix + "'");
            }

            if (nmlControl.getValue("wf_collect") == null) {
                nmlControl.setValue("wf_collect = .TRUE.");
            }

            if (nmlControl.getValue("verbosity") != null) {
                nmlControl.removeValue("verbosity");
            }

            nmlControl.setValue("outdir = ./");
            nmlControl.setValue("wfcdir = ./");
            nmlControl.setValue("pseudo_dir = '" + Environments.getPseudosPath() + "'");
        }

        QENamelist nmlSystem = input.getNamelist(QEInput.NAMELIST_SYSTEM);
        if (nmlSystem != null) {
            QEValue ibravValue = nmlSystem.getValue("ibrav");
            if (ibravValue != null && ibravValue.getIntegerValue() == 0) {

                QECellParameters cellParam = input.getCard(QECellParameters.class);
                if (cellParam != null && (cellParam.isBohr() || cellParam.isAngstrom())) {
                    nmlSystem.removeValue("a");
                    nmlSystem.removeValue("celldm(1)");
                }

                nmlSystem.removeValue("b");
                nmlSystem.removeValue("c");
                nmlSystem.removeValue("cosab");
                nmlSystem.removeValue("cosac");
                nmlSystem.removeValue("cosbc");
                nmlSystem.removeValue("celldm(2)");
                nmlSystem.removeValue("celldm(3)");
                nmlSystem.removeValue("celldm(4)");
                nmlSystem.removeValue("celldm(5)");
                nmlSystem.removeValue("celldm(6)");
            }
        }

        QENamelist nmlDos = input.getNamelist(QEInput.NAMELIST_DOS);
        if (nmlDos != null) {
            if (prefix != null && (!prefix.isEmpty())) {
                nmlDos.setValue("prefix = '" + prefix + "'");
                nmlDos.setValue("fildos = '" + prefix + ".dos'");
            }

            nmlDos.setValue("outdir = ./");
        }

        QENamelist nmlProjwfc = input.getNamelist(QEInput.NAMELIST_PROJWFC);
        if (nmlProjwfc != null) {
            if (prefix != null && (!prefix.isEmpty())) {
                nmlProjwfc.setValue("prefix = '" + prefix + "'");
                nmlProjwfc.setValue("filpdos = '" + prefix + "'");
            }

            nmlProjwfc.setValue("outdir = ./");
        }

        QENamelist nmlBand = input.getNamelist(QEInput.NAMELIST_BANDS);
        if (nmlBand != null) {
            if (prefix != null && (!prefix.isEmpty())) {
                nmlBand.setValue("prefix = '" + prefix + "'");
                nmlBand.setValue("filband = '" + prefix + ".band1'");
                nmlBand.setValue("spin_component = 1");
            }

            nmlBand.setValue("outdir = ./");
        }
    }

    public List<String[]> getCommandList(String fileName) {
        return this.getCommandList(fileName, 1);
    }

    public List<String[]> getCommandList(String fileName, int numProc) {
        return this.getCommandList(fileName, numProc, false);
    }

    public List<String[]> getUnixCommandList(String fileName) {
        return this.getUnixCommandList(fileName, 1);
    }

    public List<String[]> getUnixCommandList(String fileName, int numProc) {
        return this.getCommandList(fileName, numProc, true);
    }

    private List<String[]> getCommandList(String fileName, int numProc, boolean unixServer) {
        String fileName2 = fileName == null ? null : fileName.trim();
        if (fileName2 == null || fileName2.isEmpty()) {
            return null;
        }

        int numProc2 = Math.max(1, numProc);

        String[] command = null;
        List<String[]> commandList = new ArrayList<String[]>();

        switch (this.inputMode) {
        case Project.INPUT_MODE_SCF:
        case Project.INPUT_MODE_OPTIMIZ:
        case Project.INPUT_MODE_MD:
            // pw.x
            command = this.createCommand(RunningCommandType.PWSCF, fileName2, numProc2, unixServer);
            if (command != null && command.length > 0) {
                commandList.add(command);
            }

            break;

        case Project.INPUT_MODE_DOS:
            // pw.x (scf)
            command = this.createCommand(RunningCommandType.PWSCF, fileName2, numProc2, unixServer);
            if (command != null && command.length > 0) {
                commandList.add(command);
            }

            // pw.x (nscf)
            command = this.createCommand(RunningCommandType.PWSCF, fileName2, numProc2, unixServer);
            if (command != null && command.length > 0) {
                commandList.add(command);
            }

            // dos.x
            command = this.createCommand(RunningCommandType.DOS, fileName2, numProc2, unixServer);
            if (command != null && command.length > 0) {
                commandList.add(command);
            }

            // projwfc.x
            command = this.createCommand(RunningCommandType.PROJWFC, fileName2, numProc2, unixServer);
            if (command != null && command.length > 0) {
                commandList.add(command);
            }

            break;

        case Project.INPUT_MODE_BAND:
            // pw.x (scf)
            command = this.createCommand(RunningCommandType.PWSCF, fileName2, numProc2, unixServer);
            if (command != null && command.length > 0) {
                commandList.add(command);
            }

            // pw.x (bands)
            command = this.createCommand(RunningCommandType.PWSCF, fileName2, numProc2, unixServer);
            if (command != null && command.length > 0) {
                commandList.add(command);
            }

            // bands.x (up spin)
            command = this.createCommand(RunningCommandType.BAND, fileName2, numProc2, unixServer);
            if (command != null && command.length > 0) {
                commandList.add(command);
            }

            // bands.x (down spin)
            command = this.createCommand(RunningCommandType.BAND, fileName2, numProc2, unixServer);
            if (command != null && command.length > 0) {
                commandList.add(command);
            }

            break;

        default:
            // NOP
            break;
        }

        return commandList;
    }

    private String[] createCommand(RunningCommandType commandType, String fileName, int numProc, boolean unixServer) {
        if (commandType == null) {
            return null;
        }

        if (fileName == null || fileName.isEmpty()) {
            return null;
        }

        if (numProc < 1) {
            return null;
        }

        RunningCommand command = new RunningCommand(commandType);
        command.setInput(fileName);
        command.setProcess(numProc);
        return command.getCommand(unixServer);
    }

    public List<RunningCondition> getConditionList() {
        List<RunningCondition> conditionList = new ArrayList<RunningCondition>();

        switch (this.inputMode) {
        case Project.INPUT_MODE_SCF:
            conditionList.add((project, input) -> true);
            break;

        case Project.INPUT_MODE_OPTIMIZ:
            conditionList.add((project, input) -> true);
            break;

        case Project.INPUT_MODE_MD:
            conditionList.add((project, input) -> true);
            break;

        case Project.INPUT_MODE_DOS:
            conditionList.add((project, input) -> {
                ProjectProperty projectProperty = project == null ? null : project.getProperty();
                if (projectProperty == null) {
                    return true;
                }

                ProjectStatus projectStatus = projectProperty.getStatus();
                if (projectStatus == null) {
                    return true;
                }

                if (projectStatus.isScfDone() || projectStatus.isOptDone()) {
                    return false;
                } else {
                    return true;
                }
            });

            conditionList.add((project, input) -> true);
            conditionList.add((project, input) -> true);

            conditionList.add((project, input) -> {
                QENamelist nmlSystem = input == null ? null : input.getNamelist(QEInput.NAMELIST_SYSTEM);
                if (nmlSystem == null) {
                    return false;
                }

                QEValue value = nmlSystem.getValue("occupations");
                String occup = value == null ? "" : value.getCharacterValue();
                return !(occup.startsWith("tetrahedra"));
            });

            break;

        case Project.INPUT_MODE_BAND:
            conditionList.add((project, input) -> {
                ProjectProperty projectProperty = project == null ? null : project.getProperty();
                if (projectProperty == null) {
                    return true;
                }

                ProjectStatus projectStatus = projectProperty.getStatus();
                if (projectStatus == null) {
                    return true;
                }

                if (projectStatus.isScfDone() || projectStatus.isOptDone()) {
                    return false;
                } else {
                    return true;
                }
            });

            conditionList.add((project, input) -> true);
            conditionList.add((project, input) -> true);

            conditionList.add((project, input) -> {
                QENamelist nmlSystem = input == null ? null : input.getNamelist(QEInput.NAMELIST_SYSTEM);
                if (nmlSystem == null) {
                    return false;
                }

                QEValue value = nmlSystem.getValue("nspin");
                int nspin = value == null ? 1 : value.getIntegerValue();
                return nspin == 2;
            });

            break;

        default:
            // NOP
            break;
        }

        return conditionList;
    }

    public List<InputEditor> getInputEditorList(Project project) {
        if (project == null) {
            return null;
        }

        List<InputEditor> editorList = new ArrayList<InputEditor>();

        switch (this.inputMode) {
        case Project.INPUT_MODE_SCF:
            editorList.add((input) -> input);
            break;

        case Project.INPUT_MODE_OPTIMIZ:
            editorList.add((input) -> input);
            break;

        case Project.INPUT_MODE_MD:
            editorList.add((input) -> input);
            break;

        case Project.INPUT_MODE_DOS:
            editorList.add((input) -> {
                QEInput input2 = project.getQEInputScf();
                QEInput input3 = input2 == null ? null : input2.copy();

                if (input3 != null) {
                    this.modifyQEInput(input3, project);
                }
                return input3;
            });

            editorList.add((input) -> input);
            editorList.add((input) -> input);
            editorList.add((input) -> input);
            break;

        case Project.INPUT_MODE_BAND:
            editorList.add((input) -> {
                QEInput input2 = project.getQEInputScf();
                QEInput input3 = input2 == null ? null : input2.copy();

                if (input3 != null) {
                    this.modifyQEInput(input3, project);
                }
                return input3;
            });

            editorList.add((input) -> input);
            editorList.add((input) -> input);

            editorList.add((input) -> {
                QEInput input2 = input == null ? null : input.copy();
                QENamelist nmlBand = input2 == null ? null : input2.getNamelist(QEInput.NAMELIST_BANDS);
                if (nmlBand != null) {
                    QEValue value = nmlBand.getValue("filband");
                    String filband = value == null ? null : value.getCharacterValue();
                    if (filband != null && (!filband.isEmpty())) {
                        filband = filband.substring(0, filband.length() - 1) + "2";
                        nmlBand.setValue("filband = '" + filband);
                    }
                    nmlBand.setValue("spin_component = 2");
                }
                return input2;
            });

            break;

        default:
            // NOP
            break;
        }

        return editorList;
    }

    public List<String> getInpNameList(Project project) {
        if (project == null) {
            return null;
        }

        List<String> inpList = new ArrayList<String>();

        switch (this.inputMode) {
        case Project.INPUT_MODE_SCF:
            inpList.add(project.getInpFileName("scf"));
            break;

        case Project.INPUT_MODE_OPTIMIZ:
            inpList.add(project.getInpFileName("opt"));
            break;

        case Project.INPUT_MODE_MD:
            inpList.add(project.getInpFileName("md"));
            break;

        case Project.INPUT_MODE_DOS:
            inpList.add(project.getInpFileName("scf"));
            inpList.add(project.getInpFileName("nscf"));
            inpList.add(project.getInpFileName("dos"));
            inpList.add(project.getInpFileName("pdos"));
            break;

        case Project.INPUT_MODE_BAND:
            inpList.add(project.getInpFileName("scf"));
            inpList.add(project.getInpFileName("bands"));
            inpList.add(project.getInpFileName("band.up"));
            inpList.add(project.getInpFileName("band.down"));
            break;

        default:
            // NOP
            break;
        }

        return inpList;
    }

    public List<String> getLogNameList(Project project) {
        if (project == null) {
            return null;
        }

        List<String> logList = new ArrayList<String>();

        switch (this.inputMode) {
        case Project.INPUT_MODE_SCF:
            logList.add(project.getLogFileName("scf"));
            break;

        case Project.INPUT_MODE_OPTIMIZ:
            logList.add(project.getLogFileName("opt"));
            break;

        case Project.INPUT_MODE_MD:
            logList.add(project.getLogFileName("md"));
            break;

        case Project.INPUT_MODE_DOS:
            logList.add(project.getLogFileName("scf"));
            logList.add(project.getLogFileName("nscf"));
            logList.add(project.getLogFileName("dos"));
            logList.add(project.getLogFileName("pdos"));
            break;

        case Project.INPUT_MODE_BAND:
            logList.add(project.getLogFileName("scf"));
            logList.add(project.getLogFileName("bands"));
            logList.add(project.getLogFileName("band.up"));
            logList.add(project.getLogFileName("band.down"));
            break;

        default:
            // NOP
            break;
        }

        return logList;
    }

    public List<String> getErrNameList(Project project) {
        if (project == null) {
            return null;
        }

        List<String> errList = new ArrayList<String>();

        switch (this.inputMode) {
        case Project.INPUT_MODE_SCF:
            errList.add(project.getErrFileName("scf"));
            break;

        case Project.INPUT_MODE_OPTIMIZ:
            errList.add(project.getErrFileName("opt"));
            break;

        case Project.INPUT_MODE_MD:
            errList.add(project.getErrFileName("md"));
            break;

        case Project.INPUT_MODE_DOS:
            errList.add(project.getErrFileName("scf"));
            errList.add(project.getErrFileName("nscf"));
            errList.add(project.getErrFileName("dos"));
            errList.add(project.getErrFileName("pdos"));
            break;

        case Project.INPUT_MODE_BAND:
            errList.add(project.getErrFileName("scf"));
            errList.add(project.getErrFileName("bands"));
            errList.add(project.getErrFileName("band.up"));
            errList.add(project.getErrFileName("band.down"));
            break;

        default:
            // NOP
            break;
        }

        return errList;
    }

    public List<LogParser> getParserList(Project project) {
        ProjectProperty projectProperty = project == null ? null : project.getProperty();
        if (projectProperty == null) {
            return null;
        }

        List<LogParser> parserList = new ArrayList<LogParser>();

        switch (this.inputMode) {
        case Project.INPUT_MODE_SCF:
            parserList.add(new ScfParser(projectProperty));
            break;

        case Project.INPUT_MODE_OPTIMIZ:
        case Project.INPUT_MODE_MD:
            Cell cell = project == null ? null : project.getCell();

            String axis = null;
            if (cell != null && cell.hasProperty(CellProperty.AXIS)) {
                axis = cell.stringProperty(CellProperty.AXIS);
            }

            boolean molecule = false;
            if (cell != null && cell.hasProperty(CellProperty.MOLECULE)) {
                molecule = cell.booleanProperty(CellProperty.MOLECULE);
            }

            GeometryParser parser = new GeometryParser(projectProperty, this.inputMode == Project.INPUT_MODE_MD);
            parser.setCellAxis(axis);
            parser.setMolecule(molecule);

            parserList.add(parser);
            break;

        case Project.INPUT_MODE_DOS:
            parserList.add(new ScfParser(projectProperty));
            parserList.add(new FermiParser(projectProperty));
            parserList.add(new VoidParser(projectProperty));
            parserList.add(new VoidParser(projectProperty));
            break;

        case Project.INPUT_MODE_BAND:
            parserList.add(new ScfParser(projectProperty));
            parserList.add(new VoidParser(projectProperty));
            parserList.add(new BandPathParser(projectProperty));
            parserList.add(new VoidParser(projectProperty));
            break;

        default:
            // NOP
            break;
        }

        return parserList;
    }

    public List<PostOperation> getPostList() {
        List<PostOperation> postList = new ArrayList<PostOperation>();

        switch (this.inputMode) {
        case Project.INPUT_MODE_SCF:
            postList.add((project) -> {
                return;
            });
            break;

        case Project.INPUT_MODE_OPTIMIZ:
            postList.add((project) -> {
                return;
            });
            break;

        case Project.INPUT_MODE_MD:
            postList.add((project) -> {
                return;
            });
            break;

        case Project.INPUT_MODE_DOS:
            postList.add((project) -> {
                if (project != null) {
                    this.setProjectStatus(project, Project.INPUT_MODE_SCF);
                }
                return;
            });
            postList.add((project) -> {
                return;
            });
            postList.add((project) -> {
                return;
            });
            postList.add((project) -> {
                return;
            });
            break;

        case Project.INPUT_MODE_BAND:
            postList.add((project) -> {
                if (project != null) {
                    this.setProjectStatus(project, Project.INPUT_MODE_SCF);
                }
                return;
            });
            postList.add((project) -> {
                return;
            });
            postList.add((project) -> {
                if (project != null) {
                    this.setupSymmetricKPoints(project);
                }
                return;
            });
            postList.add((project) -> {
                return;
            });
            break;

        default:
            // NOP
            break;
        }

        return postList;
    }

    private void setupSymmetricKPoints(Project project) {
        // keep QEKPoints
        QEInput input = project == null ? null : project.getQEInputBand();
        input = input == null ? null : input.copy();
        if (input == null) {
            return;
        }

        QEKPoints kpoints = input.getCard(QEKPoints.class);
        if (kpoints == null || kpoints.numKPoints() < 1) {
            return;
        }

        // keep ProjectBandPaths
        ProjectProperty projectProperty = project == null ? null : project.getProperty();
        if (projectProperty == null) {
            return;
        }

        ProjectBandPaths projectBandPaths = projectProperty.getBandPaths();
        if (projectBandPaths == null || projectBandPaths.numPoints() < 1) {
            return;
        }

        // copy QEKPoints -> ProjectBandPaths
        synchronized (projectBandPaths) {
            int numData = projectBandPaths.numPoints();
            if (kpoints.numKPoints() < numData) {
                return;
            }

            for (int i = 0; i < numData; i++) {
                QEKPoint kpoint = kpoints.getKPoint(i);

                String klabel = null;
                if (kpoint != null && kpoint.hasLetter()) {
                    klabel = kpoint.getLetter();
                }

                if (klabel != null && !(klabel.isEmpty())) {
                    if (klabel.equalsIgnoreCase("gG")) {
                        klabel = "�?";
                    } else if (klabel.equalsIgnoreCase("gS")) {
                        klabel = "Σ";
                    } else if (klabel.equalsIgnoreCase("gS1")) {
                        klabel = "Σ1";
                    }

                    projectBandPaths.setLabel(i, klabel);
                }
            }
        }

        projectProperty.saveBandPaths();
    }

    public void setProjectStatus(Project project) {
        this.setProjectStatus(project, this.inputMode);
    }

    private void setProjectStatus(Project project, int inputMode) {
        ProjectProperty projectProperty = project == null ? null : project.getProperty();
        if (projectProperty == null) {
            return;
        }

        ProjectStatus projectStatus = projectProperty.getStatus();
        if (projectStatus == null) {
            return;
        }

        switch (inputMode) {
        case Project.INPUT_MODE_SCF:
            projectStatus.updateScfCount();
            projectProperty.saveStatus();
            break;

        case Project.INPUT_MODE_OPTIMIZ:
            projectStatus.updateOptDone();
            projectProperty.saveStatus();
            break;

        case Project.INPUT_MODE_MD:
            projectStatus.updateMdCount();
            projectProperty.saveStatus();
            break;

        case Project.INPUT_MODE_DOS:
            projectStatus.updateDosCount();
            projectProperty.saveStatus();
            break;

        case Project.INPUT_MODE_BAND:
            projectStatus.updateBandDone();
            projectProperty.saveStatus();
            break;
        }
    }
}
