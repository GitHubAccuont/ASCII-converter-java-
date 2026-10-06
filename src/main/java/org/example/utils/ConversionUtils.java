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

    /*
    It is simple method used for downsizing. Considering we use in base image 4*4 coverted to 2*2
    the method is basic one used for large size downscaling, the box averaging.

    Since al pixels in image are stored in the cell has its own int value (24 bits with 8 bits per R G B colors)
    The method simply calculates first ratio of initial image to its final size and by that determines of where the box
    representing each pixel is located at.
    The values to represent final image and box are simply put as:
    dx,dy: pixels in final image we pass through in a cycle as we set them in final image.
    The cell in initial image defined by following parameters (idy0,id1) initial and final y position (same principle for idx0,idx1)
    Values for rgb are being transferred for each channel in separate value and then average is used for final image representation.

    |1|5|2|4|
    |1|1|4|2|
    |0|2|1|5|   |2|3|
    |1|1|7|3|   |1|4|
    The method is slightly modified and uses overload of getRGB by taking batch of pixels instead of individual ones, basically going
    line by line instead of one by one. but principle is still same.
    */

    public static BufferedImage downsize(BufferedImage initial_image, int dstW, int dstH) {

        // Preserve type for the image
        BufferedImage result = new BufferedImage(dstW, dstH, BufferedImage.TYPE_INT_RGB);

        double ratioW = (double) initial_image.getWidth() / dstW;
        double ratioH = (double) initial_image.getHeight() / dstH;

        //  Loop for painting each of resized pixels

        for (int dy = 0; dy < dstH; dy++) {

            int[] finalRow = new int[dstW];
            for (int dx = 0; dx < dstW; dx++) {
                // Values to store the RGB channels and sum them up (sinec each channel in integer has only
                // 8 bits for each channel, summing them up must be made in the separate value for each color
                int sumR = 0;
                int sumG = 0;
                int sumB = 0;

                // Values for border  start/end on the ceiling (y-axis) of compressed area in initial image
                int iy0 = (int) Math.floor(dy * ratioH);
                int iy1 = (int) Math.floor((dy + 1) * ratioH);
                int idy = iy1 - iy0;

                // Values for border start/end on the width (x-axis) of compressed area in initial image
                int ix0 = (int) Math.floor(dx * ratioW);
                int ix1 = (int) Math.floor((dx + 1) * ratioW);
                int idx = ix1 - ix0;

                // Will throw exception if cellSize goes 0, but app has brakes at form inputs to prevent that
                int cellSize = idx*idy;
                int[] cell = new int[cellSize];
                initial_image.getRGB(ix0, iy0, idx, idy, cell, 0, idx);

                for (int ipx = 0; ipx < cellSize; ipx++) {

                    int rgb = cell[ipx];

                    // Should work without parenthesis, because >> goes before & . Either way added fore readability
                    sumR += (rgb >> 16) & 0xFF;
                    sumG += (rgb >> 8) & 0xFF;
                    sumB += (rgb) & 0xFF;

                }

                // Pack the averaged channels into an ARGB int: R in bits 16-23, G in 8-15, B in 0-7
                int pixel = ((sumR / cellSize) << 16) | ((sumG / cellSize) << 8) | (sumB / cellSize);
                finalRow[dx] = pixel;
            }
            result.setRGB(0, dy, dstW, 1, finalRow, 0, dstW);
        }

        return result;
    }
}
