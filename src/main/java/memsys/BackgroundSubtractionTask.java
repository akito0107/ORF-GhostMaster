package memsys;

import jp.ac.keio.sfc.ht.memsys.ghost.commonlib.data.OffloadableData;
import jp.ac.keio.sfc.ht.memsys.ghost.commonlib.tasks.OffloadableTask;

import java.awt.*;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by aqram on 11/21/14.
 */
public class BackgroundSubtractionTask implements OffloadableTask {

    private double[] threshold = {0.25, 0.26, 0.27, 0.28, 0.29, 0.3, 0.31,
            0.32, 0.33, 0.34, 0.35, 0.36, 0.37, 0.38, 0.39, 0.4, 0.41, 0.42,
            0.43, 0.44, 0.45, 0.46, 0.47, 0.48, 0.49, 0.5, 0.51, 0.52, 0.53,
            0.54, 0.55, 0.56, 0.57, 0.58, 0.59, 0.6, 0.61, 0.62, 0.63, 0.64,
            0.65, 0.66, 0.67, 0.68, 0.69};


    @Override
    public OffloadableData run(OffloadableData offloadableData, OffloadableData pdata) {

        int[] original = (int[]) pdata.getData("ORIGINAL");
        int[] pics = (int[]) offloadableData.getData("PICS");

        int[] gray = new int[pics.length];

        Random rn = new Random();

        for (int i = 0; i < pics.length; i++) {

            //if (Math.abs(original[i] - pics[i]) < Constants.THRESHOLD) {

                int red = (pics[i] >>> 16) & 0xFF;
                int green = (pics[i] >>> 8) & 0xFF;
                int blue = (pics[i] >>> 0) & 0xFF;

                double lum = (red * 0.21f + green * 0.71f + blue * 0.07f) / 255;

                if (lum <= threshold[rn.nextInt(threshold.length)]) {
                    gray[i] = 0x000000;
                } else {
                    gray[i] = 0xFFFFFF;
                }
            /**
            }else{
                gray[i] = pics[i];
            }
             */
        }

        OffloadableData out = new OffloadableData("WEB", "CAM");
        out.putData("PICS", gray);

        return out;
    }

    @Override
    public String getName() {
        return "OFFLOADABLETASK";
    }
}
