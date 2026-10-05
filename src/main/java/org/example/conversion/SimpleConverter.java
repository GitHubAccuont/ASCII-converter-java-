package org.example.conversion;

import org.example.utils.ConversionUtils;

import java.awt.image.BufferedImage;

public class SimpleConverter {

    // Feel free to adjust palette to bigger/smaller one
    private static final String PALETTE_FORWARD =
            "$@B%8&WM#*oahkbdpqwmZO0QLCJUYXzcvunxrjft/\\|()1{}[]?-_+~<>i!lI;:,\"^`'. ";

    private static final String PALETTE_INVERTED =
            new StringBuilder(PALETTE_FORWARD).reverse().toString();

    // Some default values to fallback to
    private int width = 80;
    private int height = 40;
    private double gamma = 1.0;
    private double contrast = 1.0;
    private boolean inverted = false;


    public void setWidth(int width) {
        this.width = width;
    }

    public void setGamma(double gamma) {
        this.gamma = gamma;
    }

    public void setContrast(double contrast) {
        this.contrast = contrast;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }

    public String convertToAscii(BufferedImage image) {

        if (height <= 0 || height > image.getHeight() || width > image.getWidth() || width <= 0) {
            return null;
        }

        BufferedImage downsized = ConversionUtils.downsize(image, width, height);

        double[][] brightnessGrid = ConversionUtils.prepareImage(downsized);

        return asciiFromBrightness(brightnessGrid);
    }

    // Uses received brightness values to directly start painting after applying gamma and contrast to each pixel
    // (made in one run to avoid creating more unnecessary loops)
    private String asciiFromBrightness(double[][] brightnessGrid) {

        String palette = inverted ? PALETTE_INVERTED : PALETTE_FORWARD;
        StringBuilder sb = new StringBuilder();
        for (int dy = 0; dy < height; dy++) {
            for (int dx = 0; dx < width; dx++) {

                double adjusted = Math.pow(brightnessGrid[dy][dx], gamma);
                adjusted = (adjusted - 0.5) * contrast + 0.5;
                adjusted = Math.clamp(adjusted, 0.0, 1.0);
                sb.append(palette.charAt((int) ((palette.length() - 1) * adjusted)));
            }
            sb.append('\n');
        }

        return sb.toString();
    }

}
