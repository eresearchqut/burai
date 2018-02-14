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

package burai.app.project.viewer.result.log;

public class FileLine implements Comparable<FileLine> {

    private int index;

    private String line;

    private boolean enhanced;

    public FileLine(int index, String line, boolean enhanced) {
        this.index = index;
        this.line = line;
        this.enhanced = enhanced;
    }

    public int getIndex() {
        return this.index;
    }

    public String getLine() {
        return this.line;
    }

    public boolean isEnhanced() {
        return this.enhanced;
    }

    @Override
    public int hashCode() {
        return this.getIndex();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    @Override
    public int compareTo(FileLine fileLine) {
        if (fileLine == null) {
            return -1;
        }

        if (this.index < fileLine.index) {
            return -1;

        } else if (this.index > fileLine.index) {
            return 1;
        }

        return 0;
    }
}
