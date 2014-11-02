package demo1;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by aqram on 11/3/14.
 */
public class SIFTUtil {

    public static int[] getPixelsTab(BufferedImage mImage) {

        int width =mImage.getWidth();
        int height = mImage.getHeight();

        int[] pixels = convertTo2DUsingGetRGB(mImage);


        // copy pixels of picture into the tab
        // On Android, Color are coded in 4 bytes (argb),
        // whereas SIFT needs color coded in 3 bytes (rgb)

        for (int i = 0; i < (width * height); i++)
            pixels[i] &= 0x00ffffff;

        return pixels;
    }

    public static int[] convertTo2DUsingGetRGB(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[] result = new int[height * width];

        for (int row = 0; row < height; row++) {
            for (int col = 1; col <=width; col++) {
                result[row * col] = image.getRGB(col-1, row);
            }
        }
        return result;
    }
}
