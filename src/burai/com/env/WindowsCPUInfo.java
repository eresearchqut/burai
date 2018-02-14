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

public class WindowsCPUInfo extends CPUInfo {

    private static final String NUM_PROC_VAR = "NUMBER_OF_PROCESSORS";

    public WindowsCPUInfo() {
        super();
    }

    @Override
    protected int countNumCPUs() {
        try {
            String strNcpu = System.getenv(NUM_PROC_VAR);
            if (strNcpu != null) {
                int ncpu = Integer.parseInt(strNcpu);
                return Math.max(ncpu, 1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 1;
    }
}
