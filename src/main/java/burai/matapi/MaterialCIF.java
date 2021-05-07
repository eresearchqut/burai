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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;

import com.google.gson.Gson;

public class MaterialCIF extends MaterialData {

    private static final String MATERIALS_API_CIF = "/cif";

    public static MaterialCIF getInstance(String matID) {
        if (matID == null || matID.trim().isEmpty()) {
            throw new IllegalArgumentException("matID is empty.");
        }

        MaterialCIF matCif = null;

        try {
            matCif = readURL(matID.trim());
        } catch (IOException e) {
            e.printStackTrace();
            matCif = null;
        }

        return matCif;
    }

    private static MaterialCIF readURL(String matID) throws IOException {
        Reader reader = null;
        MaterialCIF matCif = null;

        try {
            URL url = new URL(MaterialsAPI.MATERIALS_API_URL + matID + MATERIALS_API_CIF);
            URLConnection urlConnection = url.openConnection();
            if (urlConnection == null) {
                throw new IOException("urlConnection is null.");
            }

            InputStream input = urlConnection.getInputStream();
            input = input == null ? null : new BufferedInputStream(input);
            if (input == null) {
                throw new IOException("input is null.");
            }

            reader = new InputStreamReader(input);

            Gson gson = new Gson();
            matCif = gson.<MaterialCIF> fromJson(reader, MaterialCIF.class);

        } catch (IOException e1) {
            throw e1;

        } catch (Exception e2) {
            throw new IOException(e2);

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e3) {
                    throw e3;
                }
            }
        }

        return matCif;
    }

    private String cif;

    private MaterialCIF() {
        super();
        this.cif = null;
    }

    @Override
    public String getCIF() {
        return this.cif;
    }
}
