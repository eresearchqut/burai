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

package burai.run.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import burai.project.property.ProjectEnergies;
import burai.project.property.ProjectProperty;

public class FermiParser extends LogParser {

    private ProjectEnergies fermiEnergies;

    public FermiParser(ProjectProperty property) {
        super(property);

        this.fermiEnergies = this.property.getFermiEnergies();
    }

    @Override
    public void parse(File file) throws IOException {
        if (this.fermiEnergies != null) {
            this.fermiEnergies.clearEnergies();
        }

        try {
            this.parseKernel(file);

        } catch (IOException e) {
            if (this.fermiEnergies != null) {
                this.fermiEnergies.clearEnergies();
            }
            throw e;

        } finally {
            this.property.saveFermiEnergies();
        }
    }

    private void parseKernel(File file) throws IOException {

        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(file));

            String line = null;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                String strFermi = null;

                if (line.startsWith("the Fermi energy")) {
                    String[] subLines = line.split("\\s+");
                    if (subLines != null && subLines.length > 4) {
                        strFermi = subLines[4];
                    }

                } else if (line.startsWith("highest occupied")) {
                    String[] subLines = line.split(":");

                    String line2 = null;
                    if (subLines != null && subLines.length > 1) {
                        line2 = subLines[1];
                    }
                    if (line2 != null) {
                        line2 = line2.trim();
                    }

                    String[] subLines2 = null;
                    if (line2 != null && !line2.isEmpty()) {
                        subLines2 = line2.split("\\s+");
                    }
                    if (subLines2 != null && subLines2.length > 0) {
                        strFermi = subLines2[0];
                    }
                }

                if (strFermi != null) {
                    try {
                        double fermi = Double.parseDouble(strFermi);
                        if (this.fermiEnergies != null) {
                            this.fermiEnergies.addEnergy(fermi);
                        }
                    } catch (NumberFormatException e) {
                        // NOP
                    }
                }
            }

        } catch (FileNotFoundException e1) {
            throw e1;

        } catch (IOException e2) {
            throw e2;

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e3) {
                    throw e3;
                }
            }
        }
    }
}
