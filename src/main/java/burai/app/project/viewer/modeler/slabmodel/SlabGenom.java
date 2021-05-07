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

package burai.app.project.viewer.modeler.slabmodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SlabGenom {

    private static final double COORD_THR = 0.10; // angstrom
    private static final double LAYER_THR = 0.10; // angstrom

    private List<Layer> layers;

    public SlabGenom(String[] names, double[] coords) {
        if (names == null || names.length < 1) {
            throw new IllegalArgumentException("names is empty.");
        }

        if (coords == null || coords.length < 1) {
            throw new IllegalArgumentException("coords is empty.");
        }

        if (names.length != coords.length) {
            throw new IllegalArgumentException("names.length != coords.length.");
        }

        this.setupLayers(names, coords);
    }

    private void setupLayers(String[] names, double[] coords) {
        this.layers = new ArrayList<Layer>();

        int istart = 0;
        int iend = 0;
        double coord1 = 0.0;
        double coord2 = 0.0;

        while (true) {
            istart = iend;
            iend = this.nextLayer(istart, coords);
            if (istart >= iend) {
                break;
            }

            coord1 = coord2;
            coord2 = 0.0;
            for (int i = istart; i < iend; i++) {
                coord2 += coords[i];
            }
            coord2 /= (double) (iend - istart);

            double distance = 0.0;
            if (!this.layers.isEmpty()) {
                distance = coord1 - coord2;
            }

            Layer layer = this.getLayer(istart, iend, names);
            if (layer != null) {
                layer.distance = distance;
                this.layers.add(layer);
            }
        }
    }

    private int nextLayer(int istart, double[] coords) {
        if (istart >= coords.length) {
            return coords.length;
        }

        int iend = coords.length;
        double coord0 = coords[istart];

        for (int i = istart + 1; i < coords.length; i++) {
            double coord = coords[i];
            if (Math.abs(coord - coord0) > COORD_THR) {
                iend = i;
                break;
            }
        }

        return iend;
    }

    private Layer getLayer(int istart, int iend, String[] names) {
        if ((iend - istart) == 1) {
            Layer layer = new Layer();
            layer.code = names[istart];
            return layer;
        }

        String[] names2 = new String[iend - istart];
        for (int i = istart; i < iend; i++) {
            names2[i - istart] = names[i];
        }

        Arrays.sort(names2);

        int mult = 0;
        String name = null;
        StringBuilder code = new StringBuilder();

        for (int i = 0; i <= names2.length; i++) {
            if (i < names2.length && name != null && name.equals(names2[i])) {
                mult++;
                continue;
            }

            if (name != null && !(name.isEmpty())) {
                if (code.length() > 0) {
                    code.append(' ');
                }

                code.append(name);
                if (mult > 1) {
                    code.append('*');
                    code.append(mult);
                }
            }

            if (i < names2.length) {
                mult = 1;
                name = names2[i];
            }
        }

        Layer layer = new Layer();
        layer.code = code.toString();
        return layer;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        if (this.layers != null) {
            for (Layer layer : this.layers) {
                if (layer != null) {
                    str.append('{');
                    str.append(layer.code);
                    str.append('|');
                    str.append(layer.distance);
                    str.append('}');
                }
            }
        }

        return str.length() > 0 ? str.toString() : "{}";
    }

    @Override
    public int hashCode() {
        return this.layers == null ? 0 : this.layers.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (this.getClass() != obj.getClass()) {
            return false;
        }

        SlabGenom other = (SlabGenom) obj;
        if (this.layers == null) {
            return this.layers == other.layers;
        } else {
            return this.layers.equals(other.layers);
        }
    }

    private static class Layer {
        public String code;
        public double distance;

        public Layer() {
            // NOP
        }

        @Override
        public int hashCode() {
            return this.code == null ? 0 : this.code.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }

            if (obj == null) {
                return false;
            }

            if (this.getClass() != obj.getClass()) {
                return false;
            }

            Layer other = (Layer) obj;

            if (this.code == null) {
                if (this.code != other.code) {
                    return false;
                }
            } else {
                if (!this.code.equals(other.code)) {
                    return false;
                }
            }

            return Math.abs(this.distance - other.distance) <= LAYER_THR;
        }
    }
}
