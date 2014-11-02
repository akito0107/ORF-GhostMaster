package gps;

import gps.util.Complex;
import gps.util.DataParser;
import jp.ac.keio.sfc.ht.memsys.ghost.commonlib.data.OffloadableData;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by usa on 11/3/14.
 */
public class GPSUtil {

    public static OffloadableData genData(String ID, String seq){

        OffloadableData data = new OffloadableData(ID, seq);

        Complex[] body = new Complex[332000];

        Complex[] in = DataParser.convertToComplexArray("/Users/usa/orf2014/GPSAcquisition/sampledata/in.dat");
        Complex[] prn = DataParser.convertToComplexArray("/Users/usa/orf2014/GPSAcquisition/sampledata/prn.dat");
        ArrayList<Complex[]> dopplerWipeoffs = DataParser.convertToComplexArrayList("/Users/usa/orf2014/GPSAcquisition/sampledata/doppler_wipeoff.dat");

        for (int i=0; i<4000; i++) {
            body[i] = in[i];
        }

        for (int i=4000; i<8000; i++) {
            body[i] = prn[i-4000];
        }

        Complex[] dopplerComplexArray = new Complex[81 * 4000];
        int largeIdx = 0;
        for (int i=0; i<81; i++) {
            Complex[] tmpArray = dopplerWipeoffs.get(i);
            for (int j=0; j<4000; j++) {
                dopplerComplexArray[largeIdx] = tmpArray[j];
                largeIdx++;
            }
        }

        for (int i=8000; i<body.length; i++) {
            body[i] = dopplerComplexArray[i - 8000];
        }

        data.putData(GPSTaskKeys.DATA, body);

        return data;
    }
}
