package gps;

import gps.util.Complex;
import gps.util.DataParser;
import jp.ac.keio.sfc.ht.memsys.ghost.commonlib.data.OffloadableData;
import jp.ac.keio.sfc.ht.memsys.ghost.commonlib.tasks.OffloadableTask;

import java.util.ArrayList;

/**
 * Created by usa on 10/31/14.
 */
public class GPSTaskImpl implements OffloadableTask {
    private static final String TASK_NAME = "GPS";

//    public static void main(String[] args) {
//        int fftSize = 4000;
//        int numDopplerBins = 81;
//
//        Complex[] in = DataParser.convertToComplexArray("sampledata/in.dat");
//        ArrayList<Complex[]> dopplerWipeoffs = DataParser.convertToComplexArrayList("sampledata/doppler_wipeoff.dat");
//        Complex[] prn = DataParser.convertToComplexArray("sampledata/prn.dat");
//
//        Acquisition acq = new Acquisition(in, prn, dopplerWipeoffs, numDopplerBins);
//        double magt = acq.lookForPeak();
//
//        System.out.println(magt);
//
//    }

    @Override
    public OffloadableData run(OffloadableData data) {
        Complex[] complexSet = (Complex[])data.getData(GPSTaskKeys.DATA);

        int fftSize = 4000;
        int numDopplerBins = 81;

//        Complex[] in = DataParser.convertToComplexArray("sampledata/in.dat");
//        Complex[] prn = DataParser.convertToComplexArray("sampledata/prn.dat");
//        ArrayList<Complex[]> dopplerWipeoffs = DataParser.convertToComplexArrayList("sampledata/doppler_wipeoff.dat");

        Complex[] in = new Complex[fftSize];
        for (int i=0; i<fftSize; i++) {
            in[i] = complexSet[i];
        }

        Complex[] prn = new Complex[fftSize];
        for (int i=0; i<fftSize; i++) {
            prn[i] = complexSet[i + fftSize];
        }

        int largeIdx = fftSize * 2;
        ArrayList<Complex[]> dopplerWipeoffs = new ArrayList<Complex[]>();
        for (int i=0; i<numDopplerBins; i++) {
            Complex[] doppler = new Complex[fftSize];
            for (int j=0; j<fftSize; j++) {
                doppler[j] = complexSet[largeIdx];
                largeIdx++;
            }
            dopplerWipeoffs.add(doppler);
        }

        Acquisition acq = new Acquisition(in, prn, dopplerWipeoffs, numDopplerBins);
        double magt = acq.lookForPeak();

        OffloadableData result = new OffloadableData(data.NAME_SPACE);
        result.putData(GPSTaskKeys.DATA,magt);

        return result;
    }

    @Override
    public String getName() {
        return TASK_NAME;
    }
}
