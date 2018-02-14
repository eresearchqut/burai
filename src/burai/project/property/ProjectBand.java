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

package burai.project.property;

import java.io.File;

public class ProjectBand {

    private String path;

    private String prefix;

    private BandData[] bandDatas;

    public ProjectBand(String path, String prefix) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("path is empty.");
        }

        if (prefix == null || prefix.isEmpty()) {
            throw new IllegalArgumentException("prefix is empty.");
        }

        this.path = path;
        this.prefix = prefix;

        this.bandDatas = new BandData[2];
        this.reload();
    }

    public String getPath() {
        return this.path;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public BandData getBandData() {
        return this.getBandData(true);
    }

    public BandData getBandData(boolean spin) {
        return this.bandDatas[(spin ? 0 : 1)];
    }

    public boolean reload() {
        boolean reloaded = false;

        try {
            File dirFile = new File(this.path);
            if (!dirFile.isDirectory()) {
                return false;
            }

            File file1 = new File(dirFile, this.prefix + ".band1.gnu");
            File file2 = new File(dirFile, this.prefix + ".band2.gnu");
            File[] files = { file1, file2 };

            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (!file.isFile()) {
                    continue;
                }

                if (this.bandDatas[i] == null) {
                    this.bandDatas[i] = new BandData(file);
                    reloaded = true;

                } else {
                    BandData bandData = this.bandDatas[i];
                    if (bandData != null) {
                        reloaded = reloaded || bandData.reload();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return reloaded;
    }
}
