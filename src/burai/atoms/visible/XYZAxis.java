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

package burai.atoms.visible;

import burai.atoms.design.Design;
import burai.com.env.Environments;
import burai.com.font.FontTools;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;

public class XYZAxis extends Group {

    private static final double CYLINDER_RADIUS = 0.03;
    private static final double CYLINDER_HEIGHT = 1.00;
    private static final double TEXT_SIZE = Environments.isLinux() ? 1.0 : 0.5;
    private static final double TEXT_SCALE = Environments.isLinux() ? 0.5 : 1.0;
    private static final String TEXT_FONT = FontTools.getRomanFont();

    private Design design;

    public XYZAxis() {
        this(null);
    }

    public XYZAxis(Design design) {
        super();

        this.design = design;

        this.creatAx(0, "X", Color.RED);
        this.creatAx(1, "Y", Color.BLUE);
        this.creatAx(2, "Z", Color.GREEN);
    }

    private void creatAx(int index, String label, Color color) {
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(color);
        material.setSpecularColor(Color.SILVER);

        Cylinder cylinder = new Cylinder(CYLINDER_RADIUS, CYLINDER_HEIGHT);
        cylinder.setMaterial(material);

        Text text = new Text(label);
        text.setFont(Font.font(TEXT_FONT, TEXT_SIZE));
        text.setTranslateX(-0.33 * TEXT_SIZE);
        text.setTranslateY(0.10 * TEXT_SIZE + CYLINDER_HEIGHT);
        text.setRotationAxis(Rotate.Z_AXIS);
        text.setRotate(180.0);
        text.setScaleX(TEXT_SCALE);
        text.setScaleY(TEXT_SCALE);

        if (this.design != null) {
            Color fontColor = this.design.getFontColor();
            if (fontColor != null) {
                text.setFill(fontColor);
            }

            this.design.addOnFontColorChanged(fontColor_ -> {
                if (fontColor_ != null) {
                    text.setFill(fontColor_);
                }
            });
        }

        Group group = new Group();
        group.getChildren().add(cylinder);
        group.getChildren().add(text);

        Affine affine = new Affine();
        affine.prependRotation(180.0, Point3D.ZERO, Rotate.Y_AXIS);
        affine.prependTranslation(0.0, 0.5 * CYLINDER_HEIGHT, 0.0);
        if (index == 0) {
            affine.prependRotation(-90.0, Point3D.ZERO, Rotate.Z_AXIS);
            affine.prependRotation(-90.0, Point3D.ZERO, Rotate.X_AXIS);
            affine.prependRotation(45.0, Point3D.ZERO, Rotate.X_AXIS);
        } else if (index == 1) {
            affine.prependRotation(45.0, Point3D.ZERO, Rotate.Y_AXIS);
        } else if (index == 2) {
            affine.prependRotation(90.0, Point3D.ZERO, Rotate.X_AXIS);
            affine.prependRotation(90.0, Point3D.ZERO, Rotate.Z_AXIS);
            affine.prependRotation(45.0, Point3D.ZERO, Rotate.Z_AXIS);
        }

        group.getTransforms().add(affine);
        this.getChildren().add(group);
    }
}
