/*
 * Copyright (C) 2017 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.design;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import burai.atoms.design.property.DesignLogger;
import burai.atoms.design.property.DesignProperty;
import burai.atoms.element.ElementUtil;
import javafx.scene.paint.Color;

public class Design {

    private AtomsStyle atomsStyle;
    private Color backColor;
    private Color fontColor;
    private Color cellColor;
    private boolean showingLegend;
    private boolean showingAxis;
    private boolean showingCell;
    private double bondWidth;
    private double cellWidth;

    private List<ColorChanged> onBackColorChangedList;
    private List<ColorChanged> onFontColorChangedList;
    private List<ColorChanged> onCellColorChangedList;
    private List<ShowingChanged> onShowingLegendChangedList;
    private List<ShowingChanged> onShowingAxisChangedList;
    private List<ShowingChanged> onShowingCellChangedList;
    private List<ValueChanged> onCellWidthChangedList;

    private Map<String, AtomDesign> atomDesigns;

    private DesignLogger logger;

    public Design() {
        this.atomsStyle = AtomsStyle.BALL_STICK;
        this.backColor = Color.DIMGRAY;
        this.fontColor = Color.BLACK;
        this.cellColor = Color.BLACK;
        this.showingLegend = true;
        this.showingAxis = true;
        this.showingCell = true;
        this.bondWidth = 1.0;
        this.cellWidth = 1.0;

        this.onBackColorChangedList = null;
        this.onFontColorChangedList = null;
        this.onCellColorChangedList = null;
        this.onShowingLegendChangedList = null;
        this.onShowingAxisChangedList = null;
        this.onShowingCellChangedList = null;
        this.onCellWidthChangedList = null;

        this.atomDesigns = null;

        this.logger = null;
    }

    public AtomsStyle getAtomsStyle() {
        return this.atomsStyle;
    }

    public void setAtomsStyle(AtomsStyle atomsStyle) {
        if (atomsStyle == null) {
            return;
        }

        this.atomsStyle = atomsStyle;

        Collection<AtomDesign> atomDesignColl = this.atomDesigns == null ? null : this.atomDesigns.values();
        if (atomDesignColl != null && !atomDesignColl.isEmpty()) {
            for (AtomDesign atomDesign : atomDesignColl) {
                if (atomDesign != null) {
                    atomDesign.setAtomsStyle(this.atomsStyle);
                }
            }
        }
    }

    public Color getBackColor() {
        return this.backColor;
    }

    public void setBackColor(Color backColor) {
        if (backColor == null) {
            return;
        }

        this.backColor = backColor;

        if (this.onBackColorChangedList != null) {
            for (ColorChanged onBackColorChanged : this.onBackColorChangedList) {
                onBackColorChanged.onColorChanged(this.backColor);
            }
        }
    }

    public void addOnBackColorChanged(ColorChanged onBackColorChanged) {
        if (onBackColorChanged == null) {
            return;
        }

        if (this.onBackColorChangedList == null) {
            this.onBackColorChangedList = new ArrayList<>();
        }

        this.onBackColorChangedList.add(onBackColorChanged);
    }

    public void removeOnBackColorChanged(ColorChanged onBackColorChanged) {
        if (onBackColorChanged == null) {
            return;
        }

        if (this.onBackColorChangedList != null) {
            this.onBackColorChangedList.remove(onBackColorChanged);
        }
    }

    public Color getFontColor() {
        return this.fontColor;
    }

    public void setFontColor(Color fontColor) {
        if (fontColor == null) {
            return;
        }

        this.fontColor = fontColor;

        if (this.onFontColorChangedList != null) {
            for (ColorChanged onFontColorChanged : this.onFontColorChangedList) {
                onFontColorChanged.onColorChanged(this.fontColor);
            }
        }
    }

    public void addOnFontColorChanged(ColorChanged onFontColorChanged) {
        if (onFontColorChanged == null) {
            return;
        }

        if (this.onFontColorChangedList == null) {
            this.onFontColorChangedList = new ArrayList<>();
        }

        this.onFontColorChangedList.add(onFontColorChanged);
    }

    public void removeOnFontColorChanged(ColorChanged onFontColorChanged) {
        if (onFontColorChanged == null) {
            return;
        }

        if (this.onFontColorChangedList != null) {
            this.onFontColorChangedList.remove(onFontColorChanged);
        }
    }

    public Color getCellColor() {
        return this.cellColor;
    }

    public void setCellColor(Color cellColor) {
        if (cellColor == null) {
            return;
        }

        this.cellColor = cellColor;

        if (this.onCellColorChangedList != null) {
            for (ColorChanged onCellColorChanged : this.onCellColorChangedList) {
                onCellColorChanged.onColorChanged(this.cellColor);
            }
        }
    }

    public void addOnCellColorChanged(ColorChanged onCellColorChanged) {
        if (onCellColorChanged == null) {
            return;
        }

        if (this.onCellColorChangedList == null) {
            this.onCellColorChangedList = new ArrayList<>();
        }

        this.onCellColorChangedList.add(onCellColorChanged);
    }

    public void removeOnCellColorChanged(ColorChanged onCellColorChanged) {
        if (onCellColorChanged == null) {
            return;
        }

        if (this.onCellColorChangedList != null) {
            this.onCellColorChangedList.remove(onCellColorChanged);
        }
    }

    public boolean isShowingLegend() {
        return this.showingLegend;
    }

    public void setShowingLegend(boolean showingLegend) {
        this.showingLegend = showingLegend;

        if (this.onShowingLegendChangedList != null) {
            for (ShowingChanged onShowingLegendChanged : this.onShowingLegendChangedList) {
                onShowingLegendChanged.onShowingChanged(this.showingLegend);
            }
        }
    }

    public void addOnShowingLegendChanged(ShowingChanged onShowingLegendChanged) {
        if (onShowingLegendChanged == null) {
            return;
        }

        if (this.onShowingLegendChangedList == null) {
            this.onShowingLegendChangedList = new ArrayList<>();
        }

        this.onShowingLegendChangedList.add(onShowingLegendChanged);
    }

    public void removeOnShowingLegendChanged(ShowingChanged onShowingLegendChanged) {
        if (onShowingLegendChanged == null) {
            return;
        }

        if (this.onShowingLegendChangedList != null) {
            this.onShowingLegendChangedList.remove(onShowingLegendChanged);
        }
    }

    public boolean isShowingAxis() {
        return this.showingAxis;
    }

    public void setShowingAxis(boolean showingAxis) {
        this.showingAxis = showingAxis;

        if (this.onShowingAxisChangedList != null) {
            for (ShowingChanged onShowingAxisChanged : this.onShowingAxisChangedList) {
                onShowingAxisChanged.onShowingChanged(this.showingAxis);
            }
        }
    }

    public void addOnShowingAxisChanged(ShowingChanged onShowingAxisChanged) {
        if (onShowingAxisChanged == null) {
            return;
        }

        if (this.onShowingAxisChangedList == null) {
            this.onShowingAxisChangedList = new ArrayList<>();
        }

        this.onShowingAxisChangedList.add(onShowingAxisChanged);
    }

    public void removeOnShowingAxisChanged(ShowingChanged onShowingAxisChanged) {
        if (onShowingAxisChanged == null) {
            return;
        }

        if (this.onShowingAxisChangedList != null) {
            this.onShowingAxisChangedList.remove(onShowingAxisChanged);
        }
    }

    public boolean isShowingCell() {
        return this.showingCell;
    }

    public void setShowingCell(boolean showingCell) {
        this.showingCell = showingCell;

        if (this.onShowingCellChangedList != null) {
            for (ShowingChanged onShowingCellChanged : this.onShowingCellChangedList) {
                onShowingCellChanged.onShowingChanged(this.showingCell);
            }
        }
    }

    public void addOnShowingCellChanged(ShowingChanged onShowingCellChanged) {
        if (onShowingCellChanged == null) {
            return;
        }

        if (this.onShowingCellChangedList == null) {
            this.onShowingCellChangedList = new ArrayList<>();
        }

        this.onShowingCellChangedList.add(onShowingCellChanged);
    }

    public void removeOnShowingCellChanged(ShowingChanged onShowingCellChanged) {
        if (onShowingCellChanged == null) {
            return;
        }

        if (this.onShowingCellChangedList != null) {
            this.onShowingCellChangedList.remove(onShowingCellChanged);
        }
    }

    public double getBondWidth() {
        return this.bondWidth;
    }

    public void setBondWidth(double bondWidth) {
        if (bondWidth <= 0.0) {
            return;
        }

        this.bondWidth = bondWidth;

        Collection<AtomDesign> atomDesignColl = this.atomDesigns == null ? null : this.atomDesigns.values();
        if (atomDesignColl != null && !atomDesignColl.isEmpty()) {
            for (AtomDesign atomDesign : atomDesignColl) {
                if (atomDesign != null) {
                    atomDesign.setBondWidth(this.bondWidth);
                }
            }
        }
    }

    public double getCellWidth() {
        return this.cellWidth;
    }

    public void setCellWidth(double cellWidth) {
        if (cellWidth <= 0.0) {
            return;
        }

        this.cellWidth = cellWidth;

        if (this.onCellWidthChangedList != null) {
            for (ValueChanged onCellWidthChanged : this.onCellWidthChangedList) {
                onCellWidthChanged.onValueChanged(this.cellWidth);
            }
        }
    }

    public void addOnCellWidthChanged(ValueChanged onCellWidthChanged) {
        if (onCellWidthChanged == null) {
            return;
        }

        if (this.onCellWidthChangedList == null) {
            this.onCellWidthChangedList = new ArrayList<>();
        }

        this.onCellWidthChangedList.add(onCellWidthChanged);
    }

    public void removeOnCellWidthChanged(ValueChanged onCellWidthChanged) {
        if (onCellWidthChanged == null) {
            return;
        }

        if (this.onCellWidthChangedList != null) {
            this.onCellWidthChangedList.remove(onCellWidthChanged);
        }
    }

    public AtomDesign getAtomDesign(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }

        String name2 = ElementUtil.toElementName(name);
        if (name2 == null || name2.isEmpty()) {
            return null;
        }

        if (this.atomDesigns == null) {
            this.atomDesigns = new HashMap<>();
        }

        if (!this.atomDesigns.containsKey(name2)) {
            AtomDesign atomDesign = new AtomDesign(name2);
            atomDesign.setAtomsStyle(this.atomsStyle);
            atomDesign.setBondWidth(this.bondWidth);
            this.atomDesigns.put(name2, atomDesign);
        }

        return this.atomDesigns.get(name2);
    }

    public String[] namesOfAtoms() {
        if (this.atomDesigns == null || this.atomDesigns.isEmpty()) {
            return null;
        }

        Set<String> nameSet = this.atomDesigns.keySet();
        if (nameSet == null || nameSet.isEmpty()) {
            return null;
        }

        String[] names = new String[nameSet.size()];
        return nameSet.toArray(names);
    }

    public void copyTo(Design design) {
        if (design == null) {
            return;
        }

        DesignProperty property = new DesignProperty(this);
        property.restoreDesign(design);
    }

    public void storeDesign() {
        if (this.logger == null) {
            this.logger = new DesignLogger(this);
        }

        this.logger.storeProperty();
    }

    public void restoreDesign() {
        if (this.logger == null) {
            this.logger = new DesignLogger(this);
        }

        this.logger.restoreProperty();
    }

    public void subRestoreDesign() {
        if (this.logger == null) {
            this.logger = new DesignLogger(this);
        }

        this.logger.subRestoreProperty();
    }

    public void readDesign(String path) {
        if (path == null) {
            return;
        }

        String path_ = path.trim();
        if (path_ == null || path_.isEmpty()) {
            return;
        }

        DesignProperty property = null;

        synchronized (this) {
            try {
                property = new DesignProperty(path_);
            } catch (IOException e) {
                //e.printStackTrace();
                property = null;
            }
        }

        if (property != null) {
            property.restoreDesign(this);
        }
    }

    public void writeDesign(String path) {
        this.writeDesign(path, false);
    }

    public void writeDesign(String path, boolean async) {
        if (path == null) {
            return;
        }

        String path_ = path.trim();
        if (path_ == null || path_.isEmpty()) {
            return;
        }

        DesignProperty property = new DesignProperty(this);

        Runnable runnable = () -> {
            synchronized (this) {
                try {
                    property.storeDesign(path_);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        if (async) {
            Thread thread = new Thread(runnable);
            thread.start();

        } else {
            runnable.run();
        }
    }
}
