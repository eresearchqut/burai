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

import burai.project.property.ProjectBandPaths;
import burai.project.property.ProjectProperty;

public class BandPathParser extends LogParser {

    private ProjectBandPaths bandPaths;

    public BandPathParser(ProjectProperty property) {
        super(property);

        this.bandPaths = this.property.getBandPaths();
    }

    @Override
    public void parse(File file) throws IOException {
        if (this.bandPaths != null) {
            this.bandPaths.clearBandPaths();
        }

        try {
            this.parseKernel(file);

        } catch (IOException e) {
            if (this.bandPaths != null) {
                this.bandPaths.clearBandPaths();
            }
            throw e;

        } finally {
            this.property.saveBandPaths();
        }
    }

    private void parseKernel(File file) throws IOException {

        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(file));

            String line = null;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                String strPoint = null;
                final String header = "high-symmetry point: ";
                if (line.startsWith(header)) {
                    strPoint = line.substring(header.length());
                }

                if (strPoint == null || strPoint.length() < 21) {
                    continue;
                }

                String strKx = strPoint.substring(0, 7);
                if (strKx == null) {
                    continue;
                }

                String strKy = strPoint.substring(7, 14);
                if (strKy == null) {
                    continue;
                }

                String strKz = strPoint.substring(14, 21);
                if (strKz == null) {
                    continue;
                }

                strPoint = strPoint.substring(21);
                strPoint = strPoint == null ? null : strPoint.trim();
                if (strPoint == null || strPoint.isEmpty()) {
                    continue;
                }

                String strCoord = null;
                String[] subStr = strPoint.split("\\s+");
                if (subStr != null && subStr.length > 2) {
                    strCoord = subStr[2];
                }

                if (strCoord == null) {
                    continue;
                }

                try {
                    double kx = Double.parseDouble(strKx);
                    double ky = Double.parseDouble(strKy);
                    double kz = Double.parseDouble(strKz);
                    double coord = Double.parseDouble(strCoord);
                    if (this.bandPaths != null) {
                        this.bandPaths.addPoint(kx, ky, kz, coord);
                    }
                } catch (NumberFormatException e) {
                    // NOP
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
