package gps;

import gps.util.Complex;
import gps.util.FFT;
import jp.ac.keio.sfc.ht.memsys.ghost.commonlib.data.OffloadableData;
import jp.ac.keio.sfc.ht.memsys.ghost.commonlib.tasks.OffloadableTask;

import java.util.ArrayList;

/**
 * Created by usa on 10/31/14.
 */
public class Acquisition{
    private Complex[] in;
    private Complex[] prn;
    private ArrayList<Complex[]> dopplerWipeoffs;
    private int numDopplerBins;

    public Acquisition(Complex[] in, Complex[] prn, ArrayList<Complex[]> dw, int ndb) {
        this.in = in;
        this.prn = prn;
        this.dopplerWipeoffs = dw;
        this.numDopplerBins = ndb;
    }

    public double lookForPeak() {
        double magt = 0;
        double fftSize = 4000;
        double fftNormalizationFactor = fftSize * fftSize;


        for (int dopplerIndex = 0; dopplerIndex<numDopplerBins; dopplerIndex++) {
            // multiply doppler by input signal
            Complex[] complex = new Complex[in.length];
            for (int i=0; i<in.length; i++) {
                complex[i] = in[i].times(dopplerWipeoffs.get(dopplerIndex)[i]);
            }

            // FFT
            double[] real = new double[complex.length];
            double[] imag = new double[complex.length];
            for (int i=0; i<complex.length; i++) {
                real[i] = complex[i].re();
                imag[i] = complex[i].im();
            }
//            Complex[] fftTransformed = FFT.fft(complex);
            FFT.transform(real, imag);
            Complex[] fftTransformed = new Complex[complex.length];

            for (int i=0; i<complex.length; i++) {
                fftTransformed[i] = new Complex(real[i], imag[i]);
            }

            // fft transformed * prn
            complex = new Complex[in.length];
            for (int i=0; i<in.length; i++) {
                complex[i] = fftTransformed[i].times(prn[i]);
            }

            // IFFT
            double[] reali = new double[complex.length];
            double[] imagi = new double[complex.length];
            for (int i=0; i<complex.length; i++) {
                reali[i] = complex[i].re();
                imagi[i] = complex[i].im();
            }
            FFT.inverseTransform(reali, imagi);
            Complex[] ifftTransformed = new Complex[complex.length];

            for (int i=0; i<complex.length; i++) {
                ifftTransformed[i] = new Complex(reali[i], imagi[i]);
            }

            // square the value
            double[] squared = new double[ifftTransformed.length];
            for (int i=0; i<ifftTransformed.length; i++) {
                squared[i] = ifftTransformed[i].sqr();
            }

            // find max value
            double maxValue = findMaxValue(squared);

            // normalize value
            maxValue = maxValue / (fftNormalizationFactor * fftNormalizationFactor);

            if (magt < maxValue) {
                magt = maxValue;
            }

        }
        return magt;
    }

    public double findMaxValue(double[] dblArray) {
        double maxValue = 0;

        for (int i=0; i<dblArray.length; i++) {
            if (maxValue < Math.max(maxValue, dblArray[i])) {
                maxValue = dblArray[i];
            }
        }

        return maxValue;
    }

}
