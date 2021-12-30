/*
 * Decompiled with CFR 0.151.
 */
package org;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ColorPicker {
    public static void main(String[] args) {
        System.out.println(System.currentTimeMillis());
        int rad = 1024;
        BufferedImage img = new BufferedImage(rad, rad, 1);
        int centerX = img.getWidth() / 2;
        int centerY = img.getHeight() / 2;
        int radius = img.getWidth() / 2 * (img.getWidth() / 2);
        int redX = img.getWidth();
        int redY = img.getHeight() / 2;
        int redRad = img.getWidth() * img.getWidth();
        int greenX = 0;
        int greenY = img.getHeight() / 2;
        int greenRad = img.getWidth() * img.getWidth();
        int blueX = img.getWidth() / 2;
        int blueY = img.getHeight();
        int blueRad = img.getWidth() * img.getWidth();
        for (int i = 0; i < img.getWidth(); ++i) {
            for (int j = 0; j < img.getHeight(); ++j) {
                int a = i - centerX;
                int b = j - centerY;
                int distance = a * a + b * b;
                if (distance < radius) {
                    int rdx = i - redX;
                    int rdy = j - redY;
                    int redDist = rdx * rdx + rdy * rdy;
                    int redVal = (int)(255.0f - (float)redDist / (float)redRad * 256.0f);
                    int gdx = i - greenX;
                    int gdy = j - greenY;
                    int greenDist = gdx * gdx + gdy * gdy;
                    int greenVal = (int)(255.0f - (float)greenDist / (float)greenRad * 256.0f);
                    int bdx = i - blueX;
                    int bdy = j - blueY;
                    int blueDist = bdx * bdx + bdy * bdy;
                    int blueVal = (int)(255.0f - (float)blueDist / (float)blueRad * 256.0f);
                    Color c = new Color(redVal, greenVal, blueVal);
                    float[] hsbVals = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
                    Color highlight = Color.getHSBColor(hsbVals[0], hsbVals[1], 1.0f);
                    img.setRGB(i, j, ColorPicker.RGBtoHEX(highlight));
                    continue;
                }
                img.setRGB(i, j, -1);
            }
        }
        System.out.println(System.currentTimeMillis());
        try {
            ImageIO.write((RenderedImage)img, "png", new File("wheel.png"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int RGBtoHEX(Color color) {
        String hex = Integer.toHexString(color.getRGB() & 0xFFFFFF);
        if (hex.length() < 6) {
            if (hex.length() == 5) {
                hex = "0" + hex;
            }
            if (hex.length() == 4) {
                hex = "00" + hex;
            }
            if (hex.length() == 3) {
                hex = "000" + hex;
            }
        }
        hex = "#" + hex;
        return Integer.decode(hex);
    }
}

