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

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;

public class CompassPlane extends Group {

    private static final double CYLINDER_RADIUS = 0.02;
    private static final double CYLINDER_HEIGHT = 7.5;
    private static final double SPHERE_RADIUS = 0.10;

    public CompassPlane() {
        super();
        this.creatCompassPlane();
    }

    public static double getHeight() {
        return CYLINDER_HEIGHT;
    }

    private void creatCompassPlane() {
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(Color.BLACK);
        material.setSpecularColor(Color.GHOSTWHITE);

        double height = CYLINDER_HEIGHT;
        Cylinder cylinder = new Cylinder(CYLINDER_RADIUS, height);
        cylinder.setMaterial(material);
        cylinder.setRotationAxis(Rotate.X_AXIS);
        cylinder.setRotate(90.0);

        Sphere sphere1 = new Sphere(SPHERE_RADIUS);
        sphere1.setMaterial(material);

        Sphere sphere2 = new Sphere(SPHERE_RADIUS);
        sphere2.setMaterial(material);
        sphere2.setTranslateZ(+0.5 * height);

        Sphere sphere3 = new Sphere(SPHERE_RADIUS);
        sphere3.setMaterial(material);
        sphere3.setTranslateZ(-0.5 * height);

        Rectangle rectangle = new Rectangle(-0.5 * height, -0.5 * height, height, height);
        rectangle.setFill(Color.rgb(0, 255, 0, 0.2));

        Group group = new Group();
        group.getChildren().add(cylinder);
        group.getChildren().add(sphere1);
        group.getChildren().add(sphere2);
        group.getChildren().add(sphere3);
        group.getChildren().add(rectangle);

        this.getChildren().add(group);
    }
}
