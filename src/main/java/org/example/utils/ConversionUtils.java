package org.example.utils;

import java.awt.image.BufferedImage;

public class ConversionUtils {

    public static double[][] prepareImage(BufferedImage image) {

        final double r_conversion = 0.2126;
        final double g_conversion = 0.7152;
        final double b_conversion = 0.0722;

        int width = image.getWidth();
        int height = image.getHeight();

        double[][] brightnesVals = new double[height][width];

        for (int y = 0; y < height; y++) {

            double[] row = new double[width];

            for (int x = 0; x < width; x++) {

                int rgb = image.getRGB(x, y);
                double brightness = (r_conversion * ((rgb >> 16) & 0xFF) + g_conversion * ((rgb >> 8) & 0xFF) + b_conversion * (rgb & 0xFF)) / 255;
                row[x] = brightness;
            }
            brightnesVals[y] = row;
        }

        return brightnesVals;
    }

    public BufferedImage downsize(BufferedImage initial_image, int dstW, int dstH) {

        // Preserve type for the image
        BufferedImage result = new BufferedImage(dstW, dstH, BufferedImage.TYPE_INT_RGB);

        double ratioW = (double) initial_image.getWidth() / dstW;
        double ratioH = (double) initial_image.getHeight() / dstH;

        //Loop for painting each of resized pixels

        for (int dy = 0; dy < dstH; dy++) {
            for (int dx = 0; dx < dstW; dx++) {
                //Loop to find average for each cell
                int count = 0;
                int sumR = 0;
                int sumG = 0;
                int sumB = 0;

                // Values for border  start/end on the ceiling of compressed area in initial image
                int iy0 = (int) Math.floor(dy * ratioH);
                int iy1 = (int) Math.floor((dy + 1) * ratioH);

                // Values for border start/end on the width of compressed area in initial image
                int id0 = (int) Math.floor(dx * ratioW);
                int id1 = (int) Math.floor((dx + 1) * ratioW);

                for (int m = iy0; m < iy1; m++) {
                    for (int n = id0; n < id1; n++) {
                        int rgb = initial_image.getRGB(n,m);

                        // Should work without parenthesis, because >> goes before & . Either way added fore readability
                        sumR += (rgb >> 16) & 0xFF;
                        sumG += (rgb >> 8) & 0xFF;
                        sumB += (rgb) & 0xFF;
                        count++;
                    }

                }

                //Count pixel rgb correctly and move to appropriate bytes, then assemble with | into full value.
                // << goes before / so parenthesis should not be removed
                int pixel = ((sumR / count) << 16) | ((sumG / count) << 8) | (sumB / count);
                result.setRGB(dx, dy, pixel);
            }
        }

        return result;
    }
}
