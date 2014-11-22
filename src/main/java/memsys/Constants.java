package memsys;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

/**
 * Created by aqram on 11/16/14.
 */
public class Constants {

    public static final int WIDTH = 640;
    public static final int HEIGHT = 480;
    public static final int NUMOFACTORS = 48;
    public static boolean LOCAL_PROCESSING = true;
    public static int THRESHOLD = 200000;

    public static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }
}
