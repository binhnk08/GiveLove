package com.xlteam.givelove.external.datasource;

import com.xlteam.givelove.model.Color;

import java.util.ArrayList;

public class ColorDataSource {
    private final ArrayList<Color> colors = new ArrayList<>();

    private static class SingletonHelper {
        private static final ColorDataSource INSTANCE = new ColorDataSource();
    }

    public static ColorDataSource getInstance() {
        return ColorDataSource.SingletonHelper.INSTANCE;
    }

    private ColorDataSource() {
        createData();
    }

    private void createData() {
        colors.add(new Color("#FF000000", "#FFFFFFFF")); // black
        colors.add(new Color("#FFFFFFFF", "#FF000000")); // white
        colors.add(new Color("#FF0000FF", "#FFFFFFFF")); // blue
        colors.add(new Color("#FF00FFFF", "#FF000000")); // cyan, aqua
        colors.add(new Color("#FF00FF00", "#FFFFFFFF")); // green
        colors.add(new Color("#FFFFFF00", "#FF000000")); // yellow
        colors.add(new Color("#FFFF0000", "#FFFFFFFF")); // red
        colors.add(new Color("#FFFF6633", "#FFFFFFFF")); // orange
        colors.add(new Color("#FF800080", "#FFFFFFFF")); // purple
        colors.add(new Color("#FFFF00FF", "#FFFFFFFF")); // magenta
    }

    public ArrayList<Color> getAllData() {
        return colors;
    }

}