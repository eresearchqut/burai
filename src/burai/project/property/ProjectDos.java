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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectDos {

    private String path;

    private String prefix;

    private Map<File, DosData> dosDataMap;

    public ProjectDos(String path, String prefix) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("path is empty.");
        }

        if (prefix == null || prefix.isEmpty()) {
            throw new IllegalArgumentException("prefix is empty.");
        }

        this.path = path;
        this.prefix = prefix;

        this.dosDataMap = null;
        this.reload();
    }

    public String getPath() {
        return this.path;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public List<DosData> listDosData() {
        if (this.dosDataMap == null || this.dosDataMap.isEmpty()) {
            return null;
        }

        Collection<DosData> dosDataColl = this.dosDataMap.values();
        if (dosDataColl == null || dosDataColl.isEmpty()) {
            return null;
        }

        List<DosData> dosDataList = new ArrayList<DosData>();
        dosDataList.addAll(dosDataColl);
        Collections.sort(dosDataList);

        return dosDataList;
    }

    public boolean reload() {
        if (this.dosDataMap == null) {
            this.dosDataMap = new HashMap<File, DosData>();
        }

        boolean reloaded = false;

        try {
            File dirFile = new File(this.path);
            if (!dirFile.isDirectory()) {
                return false;
            }

            File[] files = dirFile.listFiles((dir, name) -> {
                if (name == null || name.isEmpty()) {
                    return false;

                } else if (name.equals(this.prefix + ".dos")) {
                    return true;

                } else if (name.startsWith(this.prefix + ".pdos_atm")) {
                    return true;

                } else {
                    return false;
                }
            });

            if (files == null) {
                return false;
            }

            for (File file : files) {
                if (!file.isFile()) {
                    continue;
                }

                if (!this.dosDataMap.containsKey(file)) {
                    this.dosDataMap.put(file, new DosData(file));
                    reloaded = true;

                } else {
                    DosData dosData = this.dosDataMap.get(file);
                    if (dosData != null) {
                        reloaded = reloaded || dosData.reload();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return reloaded;
    }
}
