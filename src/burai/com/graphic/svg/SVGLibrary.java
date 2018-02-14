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

package burai.com.graphic.svg;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;

public final class SVGLibrary {

    private SVGLibrary() {
        // NOP
    }

    public static enum SVGData {
        ANATOM(SVGItemAnAtom.WIDTH, SVGItemAnAtom.HEIGHT, SVGItemAnAtom.CONTENT),
        ATOMS(SVGItemAtoms.WIDTH, SVGItemAtoms.HEIGHT, SVGItemAtoms.CONTENT),
        ARROW_UP(SVGItemArrowLeft.WIDTH, SVGItemArrowLeft.HEIGHT, 90.0, SVGItemArrowLeft.CONTENT),
        ARROW_DOWN(SVGItemArrowLeft.WIDTH, SVGItemArrowLeft.HEIGHT, -90.0, SVGItemArrowLeft.CONTENT),
        ARROW_LEFT(SVGItemArrowLeft.WIDTH, SVGItemArrowLeft.HEIGHT, 0.0, SVGItemArrowLeft.CONTENT),
        ARROW_RIGHT(SVGItemArrowLeft.WIDTH, SVGItemArrowLeft.HEIGHT, 180.0, SVGItemArrowLeft.CONTENT),
        ARROW_ROUND(SVGItemArrowRound.WIDTH, SVGItemArrowRound.HEIGHT, SVGItemArrowRound.CONTENT),
        CALCULATOR(SVGItemCalculator.WIDTH, SVGItemCalculator.HEIGHT, SVGItemCalculator.CONTENT),
        CAMERA(SVGItemCamera.WIDTH, SVGItemCamera.HEIGHT, SVGItemCamera.CONTENT),
        CENTER(SVGItemCenter.WIDTH, SVGItemCenter.HEIGHT, SVGItemCenter.CONTENT),
        CHECK(SVGItemCheck.WIDTH, SVGItemCheck.HEIGHT, SVGItemCheck.CONTENT),
        CLOSE(SVGItemClose.WIDTH, SVGItemClose.HEIGHT, SVGItemClose.CONTENT),
        COLORS(SVGItemColors.WIDTH, SVGItemColors.HEIGHT, SVGItemColors.STYLES, SVGItemColors.CONTENTS),
        CONTROL(SVGItemControl.WIDTH, SVGItemControl.HEIGHT, SVGItemControl.CONTENT),
        CROSS(SVGItemPlus.WIDTH, SVGItemPlus.HEIGHT, 45.0, SVGItemPlus.CONTENT),
        CRYSTAL(SVGItemCrystal.WIDTH, SVGItemCrystal.HEIGHT, SVGItemCrystal.CONTENT),
        DOTS(SVGItemDots.WIDTH, SVGItemDots.HEIGHT, SVGItemDots.CONTENT),
        DOWNLOAD(SVGItemDownload.WIDTH, SVGItemDownload.HEIGHT, SVGItemDownload.CONTENT),
        EARTH(SVGItemEarth.WIDTH, SVGItemEarth.HEIGHT, SVGItemEarth.CONTENT),
        ERROR(SVGItemError.WIDTH, SVGItemError.HEIGHT, SVGItemError.CONTENT),
        EXPORT(SVGItemExport.WIDTH, SVGItemExport.HEIGHT, SVGItemExport.CONTENT),
        FILE(SVGItemFile.WIDTH, SVGItemFile.HEIGHT, SVGItemFile.CONTENT),
        FOLDER(SVGItemFolder.WIDTH, SVGItemFolder.HEIGHT, SVGItemFolder.CONTENT),
        GEAR(SVGItemGear.WIDTH, SVGItemGear.HEIGHT, SVGItemGear.CONTENT),
        HEART(SVGItemHeart.WIDTH, SVGItemHeart.HEIGHT, SVGItemHeart.CONTENT),
        HELP(SVGItemHeart.WIDTH, SVGItemHeart.HEIGHT, SVGItemHeart.CONTENT),
        HOME(SVGItemHome.WIDTH, SVGItemHome.HEIGHT, SVGItemHome.CONTENT),
        HTML(SVGItemHtml.WIDTH, SVGItemHtml.WIDTH, SVGItemHtml.CONTENT),
        INFO(SVGItemInfo.WIDTH, SVGItemInfo.HEIGHT, SVGItemInfo.CONTENT),
        INTO(SVGItemInto.WIDTH, SVGItemInto.HEIGHT, SVGItemInto.CONTENT),
        INPUTFILE(SVGItemInputFile.WIDTH, SVGItemInputFile.HEIGHT, SVGItemInputFile.CONTENT),
        LIST(SVGItemList.WIDTH, SVGItemList.HEIGHT, SVGItemList.CONTENT),
        MAXIMIZE(SVGItemMaximize.WIDTH, SVGItemMaximize.HEIGHT, SVGItemMaximize.CONTENT),
        MENU(SVGItemMenu.WIDTH, SVGItemMenu.HEIGHT, SVGItemMenu.CONTENT),
        MINIMIZE(SVGItemMinimize.WIDTH, SVGItemMinimize.HEIGHT, SVGItemMinimize.CONTENT),
        MINUS(SVGItemMinus.WIDTH, SVGItemMinus.HEIGHT, SVGItemMinus.CONTENT),
        MOVIE(SVGItemMovie.WIDTH, SVGItemMovie.HEIGHT, SVGItemMovie.CONTENT),
        MOVIE_PLAY(SVGItemMoviePlay.WIDTH, SVGItemMoviePlay.HEIGHT, SVGItemMoviePlay.CONTENT),
        MOVIE_PAUSE(SVGItemMoviePause.WIDTH, SVGItemMoviePause.HEIGHT, SVGItemMoviePause.CONTENT),
        MOVIE_NEXT(SVGItemMovieNext.WIDTH, SVGItemMovieNext.HEIGHT, 90.0, SVGItemMovieNext.CONTENT),
        MOVIE_PREVIOUS(SVGItemMovieNext.WIDTH, SVGItemMovieNext.HEIGHT, -90.0, SVGItemMovieNext.CONTENT),
        MOVIE_FIRST(SVGItemMovieLast.WIDTH, SVGItemMovieLast.HEIGHT, 180.0, SVGItemMovieLast.CONTENT),
        MOVIE_LAST(SVGItemMovieLast.WIDTH, SVGItemMovieLast.HEIGHT, SVGItemMovieLast.CONTENT),
        OUT(SVGItemOut.WIDTH, SVGItemOut.HEIGHT, SVGItemOut.CONTENT),
        PLUS(SVGItemPlus.WIDTH, SVGItemPlus.HEIGHT, SVGItemPlus.CONTENT),
        REDO(SVGItemUndo.WIDTH, SVGItemUndo.HEIGHT, 180.0, SVGItemUndo.CONTENT),
        RESULT(SVGItemResult.WIDTH, SVGItemResult.HEIGHT, SVGItemResult.CONTENT),
        RUN(SVGItemRun.WIDTH, SVGItemRun.HEIGHT, SVGItemRun.CONTENT),
        SAVE(SVGItemSave.WIDTH, SVGItemSave.HEIGHT, SVGItemSave.CONTENT),
        SEARCH(SVGItemSearch.WIDTH, SVGItemSearch.HEIGHT, SVGItemSearch.CONTENT),
        STOP(SVGItemStop.WIDTH, SVGItemStop.HEIGHT, SVGItemStop.CONTENT),
        TILES(SVGItemTiles.WIDTH, SVGItemTiles.HEIGHT, SVGItemTiles.CONTENT),
        TOOL(SVGItemTool.WIDTH, SVGItemTool.HEIGHT, SVGItemTool.CONTENT),
        TRIANGLE_RIGHT(SVGItemTriangle.WIDTH, SVGItemTriangle.HEIGHT, SVGItemTriangle.CONTENT),
        TRIANGLE_UPWARD(SVGItemTriangle.WIDTH, SVGItemTriangle.HEIGHT, 90.0, SVGItemTriangle.CONTENT),
        UNDO(SVGItemUndo.WIDTH, SVGItemUndo.HEIGHT, SVGItemUndo.CONTENT),
        VECTOR_UP(SVGItemVectorRight.WIDTH, SVGItemVectorRight.HEIGHT, -90.0, SVGItemVectorRight.CONTENT),
        VECTOR_DOWN(SVGItemVectorRight.WIDTH, SVGItemVectorRight.HEIGHT, 90.0, SVGItemVectorRight.CONTENT),
        VECTOR_LEFT(SVGItemVectorRight.WIDTH, SVGItemVectorRight.HEIGHT, 180.0, SVGItemVectorRight.CONTENT),
        VECTOR_RIGHT(SVGItemVectorRight.WIDTH, SVGItemVectorRight.HEIGHT, 0.0, SVGItemVectorRight.CONTENT);

        private double width;
        private double height;
        private double angle;
        private String content;

        private String[] styles;
        private String[] contents;

        private SVGData(double width, double height, String content) {
            this(width, height, 0.0, content);
        }

        private SVGData(double width, double height, double angle, String content) {
            if (width <= 0.0) {
                throw new IllegalArgumentException("width is not positive.");
            }

            if (height <= 0.0) {
                throw new IllegalArgumentException("height is not positive.");
            }

            if (content == null || content.trim().isEmpty()) {
                throw new IllegalArgumentException("content is empty.");
            }

            this.width = width;
            this.height = height;
            this.angle = angle;
            this.content = content;

            this.styles = null;
            this.contents = null;
        }

        private SVGData(double width, double height, String[] styles, String[] contents) {
            this(width, height, 0.0, styles, contents);
        }

        private SVGData(double width, double height, double angle, String[] styles, String[] contents) {
            if (width <= 0.0) {
                throw new IllegalArgumentException("width is not positive.");
            }

            if (height <= 0.0) {
                throw new IllegalArgumentException("height is not positive.");
            }

            if (styles == null || styles.length < 1) {
                throw new IllegalArgumentException("styles is empty.");
            }

            if (contents == null || contents.length < 1) {
                throw new IllegalArgumentException("contents is empty.");
            }

            this.width = width;
            this.height = height;
            this.angle = angle;
            this.content = null;

            this.styles = styles;
            this.contents = contents;
        }

    }

    public static Node getGraphic(SVGData svgData, double size) {
        return getGraphic(svgData, size, null);
    }

    public static Node getGraphic(SVGData svgData, double size, String style) {
        return getGraphic(svgData, size, style, null);
    }

    public static Node getGraphic(SVGData svgData, double size, String style, String styleClass) {
        if (svgData == null) {
            throw new IllegalArgumentException("svgData is null.");
        }

        if (size <= 0.0) {
            throw new IllegalArgumentException("size is not positive.");
        }

        Pane pane = new Pane();
        pane.setPrefSize(size, size);

        if (svgData.content != null) {

            SVGPath svgPath = new SVGPath();

            if (styleClass != null && (!styleClass.isEmpty())) {
                svgPath.getStyleClass().add(styleClass);
            }

            if (style != null && (!style.isEmpty())) {
                svgPath.setStyle(style);
            }

            svgPath.setContent(svgData.content);
            svgPath.getTransforms().add(new Scale(size / svgData.width, size / svgData.height, 0.0, 0.0));
            if (svgData.angle != 0.0) {
                svgPath.getTransforms().add(new Rotate(svgData.angle, 0.5 * svgData.width, 0.5 * svgData.height));
            }

            pane.getChildren().add(svgPath);

        } else if (svgData.contents != null) {

            for (int i = 0; i < svgData.contents.length; i++) {
                String style0 = svgData.styles[i];
                String content0 = svgData.contents[i];

                if (content0 == null || content0.isEmpty()) {
                    continue;
                }

                SVGPath svgPath = new SVGPath();

                if (styleClass != null && (!styleClass.isEmpty())) {
                    svgPath.getStyleClass().add(styleClass);
                }

                if (style0 != null && (!style0.isEmpty())) {
                    svgPath.setStyle(style0);

                } else if (style != null && (!style.isEmpty())) {
                    svgPath.setStyle(style);
                }

                svgPath.setContent(content0);
                svgPath.getTransforms().add(new Scale(size / svgData.width, size / svgData.height, 0.0, 0.0));
                if (svgData.angle != 0.0) {
                    svgPath.getTransforms().add(new Rotate(svgData.angle, 0.5 * svgData.width, 0.5 * svgData.height));
                }

                pane.getChildren().add(svgPath);
            }

        }

        Group group = new Group(pane);
        return group;
    }
}
