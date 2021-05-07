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

import java.util.HashMap;
import java.util.Map;

import burai.atoms.element.ElementUtil;

public class MaterialsAPI {

    protected static final String MATERIALS_API_URL = "https://www.materialsproject.org/rest/v1/materials/";

    private static final String[] ELEMENTS = ElementUtil.listAllElements();

    private String apiKey;

    private boolean primitiveCell;

    private String formula;

    private MaterialIDs matIDs;

    private Map<String, MaterialData> matDataMap;

    public MaterialsAPI(String formula) {
        this(formula, null, false);
    }

    public MaterialsAPI(String formula, String apiKey, boolean primitiveCell) {
        if (formula == null || formula.trim().isEmpty()) {
            throw new IllegalArgumentException("formula is empty.");
        }

        this.formula = this.correctFormula(formula);
        if (this.formula == null || this.formula.trim().isEmpty()) {
            throw new IllegalArgumentException("formula is incorrect.");
        }

        this.apiKey = apiKey;

        this.primitiveCell = primitiveCell;

        this.matIDs = MaterialIDs.getInstance(this.formula);

        this.matDataMap = null;
    }

    private String correctFormula(String formula) {
        String formula2 = formula;
        formula2 = formula2 == null ? null : formula2.replace('-', ' ');
        formula2 = formula2 == null ? null : formula2.replace(',', ' ');
        if (formula2 == null) {
            return null;
        }

        formula2 = formula2.trim();
        if (formula2.isEmpty()) {
            return null;
        }

        String formula3 = null;
        String[] subFormula = formula2.split("[\\s]+");

        if (subFormula.length > 1) {
            formula3 = this.toElementSymbol(subFormula[0]);
            for (int i = 1; i < subFormula.length; i++) {
                formula3 = formula3 + "-" + this.toElementSymbol(subFormula[i]);
            }

        } else {
            formula3 = this.toChemicalFormula(subFormula[0]);
        }

        return formula3;
    }

    private String toElementSymbol(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        char head = str.charAt(0);
        head = Character.toUpperCase(head);

        String str2 = null;
        if (str.length() < 2) {
            str2 = "";
        } else {
            str2 = str.substring(1).toLowerCase();
        }

        return head + str2;
    }

    private String toChemicalFormula(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        StringBuilder strBuilder = new StringBuilder(str);

        String formula = null;
        while (true) {
            String element = this.pollElement(strBuilder);
            if (element != null) {
                formula = (formula == null ? "" : formula) + element;
            } else {
                break;
            }
        }

        return formula;
    }

    private String pollElement(StringBuilder strBuilder) {
        if (strBuilder.length() < 1) {
            return null;
        }

        /*
         * in case of number
         */
        String strNum = "";
        while (strBuilder.length() > 0) {
            char c = strBuilder.charAt(0);
            if (Character.isDigit(c)) {
                strNum = strNum + c;
                strBuilder.deleteCharAt(0);

            } else {
                break;
            }
        }

        if (!strNum.isEmpty()) {
            return strNum;
        }

        /*
         * in case of element
         */
        char c1 = Character.toUpperCase(strBuilder.charAt(0));
        strBuilder.deleteCharAt(0);

        if (strBuilder.length() < 1) {
            // 1-char element
            return Character.toString(c1);
        }

        char c2 = strBuilder.charAt(0);
        if (Character.isDigit(c2)) {
            // 1-char element
            return Character.toString(c1);
        }

        // check to be 1-char element
        boolean isElement1 = false;
        String element1 = Character.toString(c1);
        for (String element : ELEMENTS) {
            if (element.equals(element1)) {
                isElement1 = true;
                break;
            }
        }

        // check to be 2-char element
        boolean isElement2 = false;
        String element2 = Character.toString(c1) + Character.toString(Character.toLowerCase(c2));
        for (String element : ELEMENTS) {
            if (element.equals(element2)) {
                isElement2 = true;
                break;
            }
        }

        if (Character.isLowerCase(c2)) {
            if (isElement2) {
                // 2-char element
                strBuilder.deleteCharAt(0);
                return element2;
            }
            if (isElement1) {
                // 1-char element
                return element1;
            }

        } else {
            if (isElement1) {
                // 1-char element
                return element1;
            }
            if (isElement2) {
                // 2-char element
                strBuilder.deleteCharAt(0);
                return element2;
            }
        }

        // 1-char element
        return element1;
    }

    public String getFormula() {
        return this.formula;
    }

    public int numMaterialIDs() {
        return this.matIDs == null ? 0 : this.matIDs.numIDs();
    }

    public String getMaterialID(int index) throws IndexOutOfBoundsException {
        if (index < 0 || this.numMaterialIDs() <= index) {
            throw new IndexOutOfBoundsException("incorrect index: " + index);
        }

        return this.matIDs == null ? null : this.matIDs.getID(index);
    }

    public MaterialData getMaterialData(int index) throws IndexOutOfBoundsException {
        String strMatID = this.getMaterialID(index);
        if (strMatID == null) {
            return null;
        }

        if (this.matDataMap == null) {
            this.matDataMap = new HashMap<String, MaterialData>();
        }

        if (!this.matDataMap.containsKey(strMatID)) {
            MaterialData matData = null;

            if (this.primitiveCell) {
                if (this.apiKey != null && (!this.apiKey.trim().isEmpty())) {
                    matData = MaterialData.getInstance(strMatID, this.apiKey);
                }
            }

            if (matData == null) {
                matData = MaterialData.getInstance(strMatID);
            }

            if (matData != null) {
                this.matDataMap.put(strMatID, matData);
            }
        }

        return this.matDataMap.get(strMatID);
    }
}
