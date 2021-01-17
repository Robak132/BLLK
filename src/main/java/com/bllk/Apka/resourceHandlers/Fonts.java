package com.bllk.Apka.resourceHandlers;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class Fonts {
    private static Font radikal, adagio_slab, veranda;

    public Fonts() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        radikal = loadFont("radikalwut-bold.otf");
        adagio_slab = loadFont("adagio_slab-regular.otf");
        veranda = new Font("Veranda", Font.PLAIN, 14);
        ge.registerFont(radikal);
        ge.registerFont(adagio_slab);
    }

    private Font loadFont(String font_name) {
        String path = "/fonts/" + font_name;
        Font font = new Font("Veranda", Font.PLAIN, 14);
        try {
            InputStream stream = Fonts.class.getResourceAsStream(path);
            font = Font.createFont(Font.TRUETYPE_FONT, stream);
        } catch (IOException | FontFormatException e) {
            System.out.println("Have not found font file. Using default Veranda.");
        }
        return font;
    }

    public static Font getStandardFont() {
        return adagio_slab.deriveFont(14f);
    }
    public static Font getSmallHeaderFont() {
        return adagio_slab.deriveFont(16f);
    }
    public static Font getHeaderFont() {
        return adagio_slab.deriveFont(20f);
    }
    public static Font getLogoFont() {
        return radikal.deriveFont(48f);
    }
    public static Font getAlternativeFont() {
        return veranda.deriveFont(14f);
    }
}
