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

import java.util.ArrayList;
import java.util.List;

public class ProjectBandPaths {

    private static final String DEFAULT_LABEL = "?";

    private List<Point> points;

    public ProjectBandPaths() {
        this.points = null;
    }

    public synchronized void clearBandPaths() {
        if (this.points != null) {
            this.points.clear();
        }
    }

    public synchronized int numPoints() {
        return this.points == null ? 0 : this.points.size();
    }

    private Point getPoint(int i) throws IndexOutOfBoundsException {
        if (this.points == null || i < 0 || i >= this.points.size()) {
            throw new IndexOutOfBoundsException("incorrect index of points: " + i + ".");
        }

        return this.points.get(i);
    }

    public synchronized double getKx(int i) throws IndexOutOfBoundsException {
        return this.getPoint(i).kx;
    }

    public synchronized double getKy(int i) throws IndexOutOfBoundsException {
        return this.getPoint(i).ky;
    }

    public synchronized double getKz(int i) throws IndexOutOfBoundsException {
        return this.getPoint(i).kz;
    }

    public synchronized double getCoordinate(int i) throws IndexOutOfBoundsException {
        return this.getPoint(i).coord;
    }

    public synchronized String getLabel(int i) throws IndexOutOfBoundsException {
        String label = this.getPoint(i).label;
        return label == null ? DEFAULT_LABEL : label;
    }

    public synchronized void removePoint(int i) throws IndexOutOfBoundsException {
        if (this.points == null || i < 0 || i >= this.points.size()) {
            throw new IndexOutOfBoundsException("incorrect index of points: " + i + ".");
        }

        this.points.remove(i);
    }

    public synchronized void addPoint(double kx, double ky, double kz, double coord) {
        if (this.points == null) {
            this.points = new ArrayList<Point>();
        }

        this.points.add(new Point(kx, ky, kz, coord));
    }

    public synchronized void setLabel(int i, String label) throws IndexOutOfBoundsException {
        this.getPoint(i).label = label;
    }

    public synchronized ProjectBandPaths copyBandPaths() {
        ProjectBandPaths other = new ProjectBandPaths();

        if (this.points == null) {
            other.points = null;

        } else {
            other.points = new ArrayList<Point>(this.points);
        }

        return other;
    }

    private static class Point {
        public double kx;
        public double ky;
        public double kz;
        public double coord;
        public String label;

        public Point(double kx, double ky, double kz, double coord) {
            this.kx = kx;
            this.ky = ky;
            this.kz = kz;
            this.coord = coord;
            this.label = DEFAULT_LABEL;
        }
    }
}
