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

public abstract class CPUInfo {

    private static CPUInfo instance = null;

    public static CPUInfo getInstance() {
        if (instance == null) {
            if (Environments.isWindows()) {
                instance = new WindowsCPUInfo();

            } else if (Environments.isMac()) {
                instance = new MacCPUInfo();

            } else if (Environments.isLinux()) {
                instance = new LinuxCPUInfo();
            }
        }

        return instance;
    }

    private int numCPUs;

    protected CPUInfo() {
        this.numCPUs = this.countNumCPUs();
    }

    public final int getNumCPUs() {
        return this.numCPUs;
    }

    protected abstract int countNumCPUs();

}
