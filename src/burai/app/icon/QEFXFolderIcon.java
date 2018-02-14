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

package burai.app.icon;

import java.io.File;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import burai.com.graphic.svg.SVGLibrary;
import burai.com.graphic.svg.SVGLibrary.SVGData;

public class QEFXFolderIcon extends QEFXIconBase<String> {

    public QEFXFolderIcon(String dirPath) {
        super(dirPath);
    }

    @Override
    public void detach() {
        this.content = null;
    }

    @Override
    public Node getFigure(double size) {
        if (size <= 0.0) {
            return null;
        }

        double scaledSize = size * ICON_SCALE;
        double insetsSize = 0.5 * (size - scaledSize);
        Node figure = SVGLibrary.getGraphic(SVGData.FOLDER, scaledSize, null, "icon-folder");
        BorderPane.setMargin(figure, new Insets(insetsSize));
        return figure;
    }

    @Override
    protected String initCaption() {
        File dirFile = new File(this.content);
        String dirName = dirFile.getName();
        if (dirName == null || dirName.trim().isEmpty()) {
            dirName = this.content;
        }

        return dirName;
    }

    @Override
    protected String initSubCaption() {
        return this.getFileDetail(this.content);
    }
}
