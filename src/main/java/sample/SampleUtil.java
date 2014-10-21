package sample;


import jp.ac.keio.sfc.ht.memsys.ghost.commonlib.data.OffloadableData;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Created by aqram on 10/20/14.
 */
public class SampleUtil {

    public static OffloadableData genData(String ID, String seq){

        OffloadableData data = new OffloadableData(ID, seq);

        double[] body = new double[10240];

        Random rnd = new SecureRandom();
        for(int i = 0; i<body.length; i++){
            body[i] = rnd.nextDouble();
        }

        data.putData(SampleTaskKeys.DATA, body);

        return data;
    }
}
