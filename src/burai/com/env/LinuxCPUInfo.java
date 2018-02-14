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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class LinuxCPUInfo extends CPUInfo {

    private static final String CPUINFO_PATH = "/proc/cpuinfo";

    private static final String PROC_WORD = "processor";

    public LinuxCPUInfo() {
        super();
    }

    @Override
    protected int countNumCPUs() {
        try {
            return this.parseCpuinfo();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return 1;
    }

    private int parseCpuinfo() throws IOException {
        int numCPUs = 0;
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(CPUINFO_PATH));

            String line = null;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith(PROC_WORD)) {
                    numCPUs++;
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

        return numCPUs;
    }
}
