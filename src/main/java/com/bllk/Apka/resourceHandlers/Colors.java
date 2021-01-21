package com.bllk.Apka.resourceHandlers;

import java.awt.*;

public class Colors {
    private static Color brightTextColor, orange, blue, darkGrey, grey, lightGrey, brightGrey, red, green;

    public Colors() {
        brightTextColor = Color.decode("#EEEEEE");
        blue = Color.decode("#1891FF");
        orange = Color.decode("#FF7F00");
        darkGrey = Color.decode("#222222");
        grey = Color.decode("#333333");
        lightGrey = Color.decode("#444444");
        brightGrey = Color.decode("#808080");
        red = Color.decode("#FF3C30");
        green = Color.decode("#A1FF03");
    }

    public static Color getBrightTextColor() {
        return brightTextColor;
    }
    public static Color getOrange() {
        return orange;
    }
    public static Color getBlue() {
        return blue;
    }
    public static Color getDarkGrey() {
        return darkGrey;
    }
    public static Color getGrey() {
        return grey;
    }
    public static Color getLightGrey() {
        return lightGrey;
    }
    public static Color getBrightGrey() {
        return brightGrey;
    }
    public static Color getRed() {
        return red;
    }
    public static Color getGreen() {
        return green;
    }
}
