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

package burai.matapi;

public abstract class MaterialData {

    public static MaterialData getInstance(String matID) {
        return MaterialData.getInstance(matID, null);
    }

    public static MaterialData getInstance(String matID, String apiKey) {

        MaterialData matData = null;
        if (apiKey != null && (!apiKey.trim().isEmpty())) {
            matData = MaterialAllData.getInstance(matID, apiKey);
        }

        if (matData == null) {
            matData = MaterialCIF.getInstance(matID);
        }

        return matData;
    }

    protected MaterialData() {
        // NOP
    }

    public abstract String getCIF();

}
