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

        //for (int i = 0; i < (width * height); i++)
            //pixels[i] &= 0x00ffffff;

        return pixels;
    }

    public static int[] convertTo2DUsingGetRGB(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        int[] dataBuffInt = image.getRGB(0, 0, width, height, null, 0, width);

        return dataBuffInt;
    }
}
