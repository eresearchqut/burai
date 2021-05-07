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

package burai.com.env;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MacCPUInfo extends CPUInfo {

    private static final String CHK_MAC_CPUCOM = "system_profiler SPHardwareDataType";

    private static final String CPU_WORD = "Cores";

    public MacCPUInfo() {
        super();
    }

    @Override
    protected int countNumCPUs() {

        int numCPUs = 1;

        try {
            Process process = Runtime.getRuntime().exec(CHK_MAC_CPUCOM);

            int exitCode = 0;
            try {
                exitCode = process.waitFor();
            } catch (InterruptedException e) {
                exitCode = 1;
            }
            if (exitCode != 0) {
                return 1;
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(process
                .getInputStream()));

            String line = null;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.contains(CPU_WORD)){

                    String[] nCPULine = line.split(" ", 0);

                    String strCPUs = null;
                    if (nCPULine != null && nCPULine.length > 0) {
                        strCPUs = nCPULine[nCPULine.length - 1];
                    }

                    if (strCPUs != null) {
                        try {
                            numCPUs = Integer.parseInt(strCPUs);
                        } catch (NumberFormatException e) {
                            numCPUs = 1;
                        }
                    } else {
                        numCPUs = 1;
                    }

                    if (numCPUs <= 0){
                        numCPUs = 1;
                    }

                    break;
                }
            }

        } catch (IOException e) {
            numCPUs = 1;
            e.printStackTrace();
        }

        return numCPUs;
    }
}
