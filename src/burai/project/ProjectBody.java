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

package burai.project;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import burai.atoms.model.Cell;
import burai.atoms.model.property.CellProperty;
import burai.atoms.reader.AtomsReader;
import burai.atoms.reader.QEReader;
import burai.input.QEBandInput;
import burai.input.QEDOSInput;
import burai.input.QEGeometryInput;
import burai.input.QEInput;
import burai.input.QEInputReader;
import burai.input.QEMDInput;
import burai.input.QEOptInput;
import burai.input.QESCFInput;
import burai.input.QESecondaryInput;
import burai.project.property.ProjectProperty;
import burai.project.property.ProjectStatus;

public class ProjectBody extends Project {

    private static final String PREFIX_NAME = "espresso";
    private static final String FILE_NAME_INP = "espresso.in";
    private static final String FILE_NAME_LOG = "espresso.log";
    private static final String FILE_NAME_ERR = "espresso.err";
    private static final String FILE_NAME_GEOM = "espresso.geom.in";
    private static final String FILE_NAME_SCF = "espresso.scf.in";
    private static final String FILE_NAME_OPT = "espresso.opt.in";
    private static final String FILE_NAME_MD = "espresso.md.in";
    private static final String FILE_NAME_DOS = "espresso.dos.in";
    private static final String FILE_NAME_BAND = "espresso.band.in";

    private static final String MARK_KEY_GEOM_HEAD = "[geometry0]";
    private static final String MARK_KEY_GEOM_ALL = "[geometry1]";
    private static final String MARK_KEY_GEOM_SCF = "[scf]";
    private static final String MARK_KEY_GEOM_OPT = "[optimiz]";
    private static final String MARK_KEY_GEOM_MD = "[md]";
    private static final String MARK_KEY_GEOM_DOS = "[dos]";
    private static final String MARK_KEY_GEOM_BAND = "[band]";

    private String prefixName;
    private String inpFileName;
    private String logFileName;
    private String errFileName;

    private InputData geomData;
    private InputData scfData;
    private InputData optData;
    private InputData mdData;
    private InputData dosData;
    private InputData bandData;

    private Cell cell;

    private ProjectProperty property;

    private Map<String, String> markMap;

    protected ProjectBody(String rootFilePath, String directoryPath) throws IOException {
        super(rootFilePath, directoryPath);

        if (this.getDirectoryPath() != null) {
            if (!this.createDirectory()) {
                throw new IOException("cannot create directory: " + this.getDirectoryPath());
            }
        }

        this.createFileNames();

        this.createInputData();

        this.cell = null;

        this.property = null;

        this.markMap = null;

        if (this.getRootFilePath() != null) {
            this.buildFromRootFile();
        } else if (this.getDirectoryPath() != null) {
            this.buildFromDirectory();
        }

        this.setupDirectoryAction();
    }

    private void createFileNames() {
        this.prefixName = PREFIX_NAME;
        this.inpFileName = FILE_NAME_INP;
        this.logFileName = FILE_NAME_LOG;
        this.errFileName = FILE_NAME_ERR;
    }

    private void createInputData() {
        this.geomData = new InputData(FILE_NAME_GEOM);
        this.scfData = new InputData(FILE_NAME_SCF);
        this.optData = new InputData(FILE_NAME_OPT);
        this.mdData = new InputData(FILE_NAME_MD);
        this.dosData = new InputData(FILE_NAME_DOS);
        this.bandData = new InputData(FILE_NAME_BAND);
    }

    private boolean createDirectory() {
        return this.createDirectory(this.getDirectoryPath());
    }

    private boolean createDirectory(String directoryPath) {
        boolean status = true;
        File directory = new File(directoryPath);

        try {
            if (!directory.isDirectory()) {
                status = status && directory.mkdirs();
            }
            if (!directory.canExecute()) {
                status = status && directory.setExecutable(true);
            }
            if (!directory.canRead()) {
                status = status && directory.setReadable(true);
            }
            if (!directory.canWrite()) {
                status = status && directory.setWritable(true);
            }

        } catch (Exception e) {
            status = false;
        }

        return status;
    }

    private void setupDirectoryAction() {
        this.addOnFilePathChanged(filePath -> {
            if (this.getDirectoryPath() == null) {
                this.property = null;
                return;
            }

            if (this.getPrefixName() == null) {
                this.property = null;
                return;
            }

            if (!this.createDirectory()) {
                this.property = null;
                return;
            }

            ProjectProperty property = new ProjectProperty(this.getDirectoryPath(), this.getPrefixName());
            if (this.property != null) {
                property.copyProperty(this.property);
            }

            this.property = property;
        });
    }

    private void buildFromRootFile() throws IOException {
        File rootFile = new File(this.getRootFilePath());

        try {
            if (!rootFile.exists()) {
                throw new IOException("no such file: " + rootFile);
            }
            if (!rootFile.canRead()) {
                throw new IOException("cannot read file: " + rootFile);
            }
        } catch (IOException e1) {
            throw e1;
        } catch (Exception e2) {
            throw new IOException(e2);
        }

        AtomsReader reader = AtomsReader.getInstance(rootFile.getPath());
        if (reader == null) {
            throw new IOException("cannot create an AtomsReader.");
        }

        try {
            this.cell = reader.readCell();
        } catch (IOException e) {
            //e.printStackTrace();
            throw e;
        } finally {
            reader.close();
        }

        if (reader instanceof QEReader) {
            QEInput input = ((QEReader) reader).getInput();
            if (input == null) {
                throw new IOException("cannot get QEInput from QEReader.");
            }
            this.setupInputGenerators(rootFile, input.getReader());
            this.geomData.setQEInput(input);

        } else {
            this.setupInputGenerators(null, null);
            this.geomData.setQEInput(new QEGeometryInput(this.cell));
        }
    }

    private void buildFromDirectory() throws IOException {
        this.setupInputGenerators(null, null);
        this.geomData.loadQEInput(this);

        QEInput input = this.geomData.getQEInput();
        if (input != null && input instanceof QEGeometryInput) {
            this.cell = ((QEGeometryInput) input).getCell();
        }

        if (this.getDirectoryPath() != null && this.getPrefixName() != null) {
            this.property = new ProjectProperty(this.getDirectoryPath(), this.getPrefixName());
        }

        if (this.cell != null) {
            ProjectStatus status = this.property == null ? null : this.property.getStatus();

            String axis = status == null ? null : status.getCellAxis();
            if (axis != null) {
                this.cell.setProperty(CellProperty.AXIS, axis);
            } else {
                this.cell.removeProperty(CellProperty.AXIS);
            }

            boolean molecule = status == null ? false : status.isMolecule();
            this.cell.setProperty(CellProperty.MOLECULE, molecule);
        }
    }

    private void setupInputGenerators(File inputFile, QEInputReader inputReader) {
        this.geomData.setInputGenerator(file -> {
            QEInput input = null;
            if (file != null) {
                input = new QEGeometryInput(file);
            } else if (inputReader != null) {
                input = new QEGeometryInput();
                input.updateInputData(inputReader);
            } else if (inputFile != null) {
                input = new QEGeometryInput(inputFile);
            } else {
                input = new QEGeometryInput();
            }

            return input;
        });

        this.scfData.setInputGenerator(file -> {
            QESecondaryInput input = null;
            if (file != null) {
                input = new QESCFInput(file);
            } else if (inputReader != null) {
                input = new QESCFInput();
                input.updateInputData(inputReader);
            } else if (inputFile != null) {
                input = new QESCFInput(inputFile);
            } else {
                input = new QESCFInput();
            }

            QEInput parentInput = this.geomData.getQEInput();
            if (parentInput != null) {
                input.setParentInput(parentInput);
            }

            return input;
        });

        this.optData.setInputGenerator(file -> {
            QESecondaryInput input = null;
            if (file != null) {
                input = new QEOptInput(file);
            } else if (inputReader != null) {
                input = new QEOptInput();
                input.updateInputData(inputReader);
            } else if (inputFile != null) {
                input = new QEOptInput(inputFile);
            } else {
                input = new QEOptInput();
            }

            QEInput parentInput = this.scfData.getQEInput();
            if (parentInput != null) {
                input.setParentInput(parentInput);
            }

            return input;
        });

        this.mdData.setInputGenerator(file -> {
            QESecondaryInput input = null;
            if (file != null) {
                input = new QEMDInput(file);
            } else if (inputReader != null) {
                input = new QEMDInput();
                input.updateInputData(inputReader);
            } else if (inputFile != null) {
                input = new QEMDInput(inputFile);
            } else {
                input = new QEMDInput();
            }

            QEInput parentInput = this.scfData.getQEInput();
            if (parentInput != null) {
                input.setParentInput(parentInput);
            }

            return input;
        });

        this.dosData.setInputGenerator(file -> {
            QESecondaryInput input = null;
            if (file != null) {
                input = new QEDOSInput(file);
            } else if (inputReader != null) {
                input = new QEDOSInput();
                input.updateInputData(inputReader);
            } else if (inputFile != null) {
                input = new QEDOSInput(inputFile);
            } else {
                input = new QEDOSInput();
            }

            QEInput parentInput = this.scfData.getQEInput();
            if (parentInput != null) {
                input.setParentInput(parentInput);
            }

            return input;
        });

        this.bandData.setInputGenerator(file -> {
            QESecondaryInput input = null;
            if (file != null) {
                input = new QEBandInput(file);
            } else if (inputReader != null) {
                input = new QEBandInput();
                input.updateInputData(inputReader);
            } else if (inputFile != null) {
                input = new QEBandInput(inputFile);
            } else {
                input = new QEBandInput();
            }

            QEInput parentInput = this.scfData.getQEInput();
            if (parentInput != null) {
                input.setParentInput(parentInput);
            }

            return input;
        });
    }

    @Override
    public void setNetProject(Project project) {
        return;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public boolean isSameAs(Project project) {
        if (project == null) {
            return false;
        }

        if (project instanceof ProjectBody) {
            return this == project;
        }

        return project.isSameAs(this);
    }

    @Override
    public ProjectProperty getProperty() {
        return this.property;
    }

    @Override
    public String getPrefixName() {
        return this.prefixName;
    }

    @Override
    public String getInpFileName(String ext) {
        String ext_ = ext == null ? null : ext.trim();
        if (ext_ == null || ext_.isEmpty()) {
            return this.inpFileName;
        } else {
            return this.inpFileName + "." + ext_;
        }
    }

    @Override
    public String getLogFileName(String ext) {
        String ext_ = ext == null ? null : ext.trim();
        if (ext_ == null || ext_.isEmpty()) {
            return this.logFileName;
        } else {
            return this.logFileName + "." + ext_;
        }
    }

    @Override
    public String getErrFileName(String ext) {
        String ext_ = ext == null ? null : ext.trim();
        if (ext_ == null || ext_.isEmpty()) {
            return this.errFileName;
        } else {
            return this.errFileName + "." + ext_;
        }
    }

    @Override
    public String getExitFileName() {
        return this.prefixName + ".EXIT";
    }

    @Override
    public QEInput getQEInputGeometry() {
        if (this.getDirectoryPath() != null) {
            this.geomData.requestQEInput(this);
        }

        return this.geomData.getQEInput();
    }

    @Override
    public QEInput getQEInputScf() {
        if (this.getDirectoryPath() != null) {
            this.scfData.requestQEInput(this);
        }

        return this.scfData.getQEInput();
    }

    @Override
    public QEInput getQEInputOptimiz() {
        if (this.getDirectoryPath() != null) {
            this.optData.requestQEInput(this);
        }

        return this.optData.getQEInput();
    }

    @Override
    public QEInput getQEInputMd() {
        if (this.getDirectoryPath() != null) {
            this.mdData.requestQEInput(this);
        }

        return this.mdData.getQEInput();
    }

    @Override
    public QEInput getQEInputDos() {
        if (this.getDirectoryPath() != null) {
            this.dosData.requestQEInput(this);
        }

        return this.dosData.getQEInput();
    }

    @Override
    public QEInput getQEInputBand() {
        if (this.getDirectoryPath() != null) {
            this.bandData.requestQEInput(this);
        }

        return this.bandData.getQEInput();
    }

    @Override
    public Cell getCell() {
        return this.cell;
    }

    @Override
    protected void loadQEInputs() {
        if (this.getDirectoryPath() != null) {
            this.geomData.requestQEInput(this);
            this.scfData.requestQEInput(this);
            this.optData.requestQEInput(this);
            this.mdData.requestQEInput(this);
            this.dosData.requestQEInput(this);
            this.bandData.requestQEInput(this);
        }
    }

    @Override
    public void resolveQEInputs() {
        this.loadQEInputs();
        this.geomData.resolveQEInput();
        this.scfData.resolveQEInput();
        this.optData.resolveQEInput();
        this.mdData.resolveQEInput();
        this.dosData.resolveQEInput();
        this.bandData.resolveQEInput();
    }

    @Override
    public void markQEInputs() {
        if (this.markMap == null) {
            this.markMap = new HashMap<String, String>();
        } else {
            this.markMap.clear();
        }

        QEInput input = null;

        input = this.getQEInputGeometry();
        if (input != null) {
            this.markMap.put(MARK_KEY_GEOM_HEAD, input.toString(false));
        }

        input = this.getQEInputScf();
        if (input != null) {
            this.markMap.put(MARK_KEY_GEOM_SCF, input.toString(false));
        }

        input = this.getQEInputOptimiz();
        if (input != null) {
            this.markMap.put(MARK_KEY_GEOM_OPT, input.toString(false));
        }

        input = this.getQEInputMd();
        if (input != null) {
            this.markMap.put(MARK_KEY_GEOM_MD, input.toString(false));
        }

        input = this.getQEInputDos();
        if (input != null) {
            this.markMap.put(MARK_KEY_GEOM_DOS, input.toString(false));
        }

        input = this.getQEInputBand();
        if (input != null) {
            this.markMap.put(MARK_KEY_GEOM_BAND, input.toString(false));
        }

        // with ATOMIC_POSITIONS
        input = this.getQEInputGeometry();
        if (input != null) {
            this.markMap.put(MARK_KEY_GEOM_ALL, input.toString(true));
        }

    }

    @Override
    public boolean isQEInputChanged() {
        QEInput input = null;
        String markNew = null;
        String markOld = null;

        input = this.getQEInputGeometry();
        if (input != null) {
            markNew = input.toString(false);
            markOld = this.markMap == null ? null : this.markMap.get(MARK_KEY_GEOM_HEAD);
            if (!this.equalsString(markNew, markOld)) {
                return true;
            }
        }

        input = this.getQEInputScf();
        if (input != null) {
            markNew = input.toString(false);
            markOld = this.markMap == null ? null : this.markMap.get(MARK_KEY_GEOM_SCF);
            if (!this.equalsString(markNew, markOld)) {
                return true;
            }
        }

        input = this.getQEInputOptimiz();
        if (input != null) {
            markNew = input.toString(false);
            markOld = this.markMap == null ? null : this.markMap.get(MARK_KEY_GEOM_OPT);
            if (!this.equalsString(markNew, markOld)) {
                return true;
            }
        }

        input = this.getQEInputMd();
        if (input != null) {
            markNew = input.toString(false);
            markOld = this.markMap == null ? null : this.markMap.get(MARK_KEY_GEOM_MD);
            if (!this.equalsString(markNew, markOld)) {
                return true;
            }
        }

        input = this.getQEInputDos();
        if (input != null) {
            markNew = input.toString(false);
            markOld = this.markMap == null ? null : this.markMap.get(MARK_KEY_GEOM_DOS);
            if (!this.equalsString(markNew, markOld)) {
                return true;
            }
        }

        input = this.getQEInputBand();
        if (input != null) {
            markNew = input.toString(false);
            markOld = this.markMap == null ? null : this.markMap.get(MARK_KEY_GEOM_BAND);
            if (!this.equalsString(markNew, markOld)) {
                return true;
            }
        }

        // with ATOMIC_POSITIONS
        input = this.getQEInputGeometry();
        if (input != null) {
            markNew = input.toString(true);
            markOld = this.markMap == null ? null : this.markMap.get(MARK_KEY_GEOM_ALL);
            if (!this.equalsString(markNew, markOld)) {
                return true;
            }
        }

        return false;
    }

    private boolean equalsString(String str1, String str2) {
        if (str1 == null) {
            return str1 == str2;
        } else {
            return str1.equals(str2);
        }
    }

    @Override
    public void saveQEInputs(String directoryPath) {
        String directoryPath2 = directoryPath == null ? null : directoryPath.trim();
        if (directoryPath2 == null || directoryPath2.isEmpty()) {
            directoryPath2 = this.getDirectoryPath();
        }

        if (directoryPath2 == null || directoryPath2.isEmpty()) {
            return;
        }

        if (!directoryPath2.equals(this.getDirectoryPath())) {
            this.loadQEInputs();
            this.setDirectoryPath(directoryPath2);
        }

        if (!this.createDirectory(directoryPath2)) {
            return;
        }

        this.resolveQEInputs();
        this.markQEInputs();

        this.saveQEInput(this.geomData.getFileName(), this.getQEInputGeometry());
        this.saveQEInput(this.scfData.getFileName(), this.getQEInputScf());
        this.saveQEInput(this.optData.getFileName(), this.getQEInputOptimiz());
        this.saveQEInput(this.mdData.getFileName(), this.getQEInputMd());
        this.saveQEInput(this.dosData.getFileName(), this.getQEInputDos());
        this.saveQEInput(this.bandData.getFileName(), this.getQEInputBand());

        if (this.property != null) {
            ProjectStatus status = this.property.getStatus();
            if (status != null) {
                if (this.cell != null && this.cell.hasProperty(CellProperty.AXIS)) {
                    status.setCellAxis(this.cell.stringProperty(CellProperty.AXIS));
                } else {
                    status.setCellAxis(null);
                }

                if (this.cell != null && this.cell.hasProperty(CellProperty.MOLECULE)) {
                    status.setMolecule(this.cell.booleanProperty(CellProperty.MOLECULE));
                } else {
                    status.setMolecule(false);
                }
            }

            this.property.saveProperty();
        }
    }

    private void saveQEInput(String fileName, QEInput input) {
        this.saveQEInput(this.getDirectoryPath(), fileName, input);
    }

    private void saveQEInput(String directoryPath, String fileName, QEInput input) {
        if (directoryPath == null) {
            return;
        }

        if (fileName == null) {
            return;
        }

        if (input == null) {
            return;
        }

        PrintWriter writer = null;
        File file = new File(directoryPath, fileName);

        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            writer.println(input.toString());

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    @Override
    public Project cloneProject(String directoryPath) {
        String directoryPath2 = directoryPath == null ? null : directoryPath.trim();
        if (directoryPath2 == null || directoryPath2.isEmpty()) {
            return null;
        }

        try {
            if (new File(directoryPath2).exists()) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }

        if (!this.createDirectory(directoryPath2)) {
            return null;
        }

        this.saveQEInput(directoryPath2, this.geomData.getFileName(), this.getQEInputGeometry());
        this.saveQEInput(directoryPath2, this.scfData.getFileName(), this.getQEInputScf());
        this.saveQEInput(directoryPath2, this.optData.getFileName(), this.getQEInputOptimiz());
        this.saveQEInput(directoryPath2, this.mdData.getFileName(), this.getQEInputMd());
        this.saveQEInput(directoryPath2, this.dosData.getFileName(), this.getQEInputDos());
        this.saveQEInput(directoryPath2, this.bandData.getFileName(), this.getQEInputBand());

        Project project = Project.getInstance(directoryPath2);
        if (project == null) {
            return null;
        }

        if (project.getDirectoryPath() != null && this.getPrefixName() != null) {
            ProjectProperty property = new ProjectProperty(project.getDirectoryPath(), this.getPrefixName());
            property.saveProperty();
        }

        return project;
    }
}
