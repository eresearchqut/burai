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

import java.io.File;
import java.io.IOException;

import burai.input.QEInput;

public class InputData {

    private String fileName;

    private QEInput qeInput;

    private InputGenerator inputGenerator;

    public InputData(String fileName) {
        String fileName2 = fileName == null ? null : fileName.trim();
        if (fileName2 == null || fileName2.isEmpty()) {
            throw new IllegalArgumentException("file name is empty.");
        }

        this.fileName = fileName2;
        this.qeInput = null;
        this.inputGenerator = null;
    }

    public String getFileName() {
        return this.fileName;
    }

    public QEInput getQEInput() {
        if (this.qeInput == null) {
            try {
                this.generateQEInput(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return this.qeInput;
    }

    public void setQEInput(QEInput qeInput) {
        this.qeInput = qeInput;
    }

    public void setInputGenerator(InputGenerator inputGenerator) {
        this.inputGenerator = inputGenerator;
    }

    private void generateQEInput(File file) throws IOException {
        if (this.inputGenerator != null) {
            this.qeInput = this.inputGenerator.generate(file);
        }
    }

    public void resolveQEInput() {
        if (this.qeInput == null) {
            try {
                this.generateQEInput(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (this.qeInput != null) {
            this.qeInput.reload();
        }
    }

    public void requestQEInput(Project project) {
        if (this.qeInput != null) {
            return;
        }

        try {
            this.loadQEInput(project);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadQEInput(Project project) throws IOException {
        if (project == null) {
            return;
        }

        String directoryPath = project.getDirectoryPath();
        if (directoryPath == null || directoryPath.trim().isEmpty()) {
            throw new IOException("directoryPath is empty.");
        }

        File directory = new File(directoryPath);
        if (!directory.isDirectory()) {
            throw new IOException("no such directory: " + directory);
        }

        File file = new File(directory, this.fileName);
        if (!file.exists()) {
            throw new IOException("no such file: " + file);
        }
        if (!file.canRead()) {
            throw new IOException("cannot read file: " + file);
        }

        this.generateQEInput(file);
    }
}
