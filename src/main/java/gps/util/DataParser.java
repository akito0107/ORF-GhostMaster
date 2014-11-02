package gps.util;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by usa on 11/2/14.
 */
public class DataParser {
    private static int fftSize = 4000;
    public static Complex[] convertToComplexArray(String fileName) {
        Complex[] result = new Complex[fftSize];

        try {
            File file = new File(fileName);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String str = br.readLine();
            result = parseLine(str);

        } catch (FileNotFoundException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println(e);
        }


        return result;
    }

    public static ArrayList<Complex[]> convertToComplexArrayList(String fileName) {
        int fftSize = 4000;
        int numdopplerBins = 81;
        ArrayList<Complex[]> result = new ArrayList<Complex[]>();

        try {
            File file = new File(fileName);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String str = br.readLine();
            while (str != null) {
                Complex[] complex = new Complex[fftSize];
                complex = parseLine(str);
                result.add(complex);
                str = br.readLine();
            }

        } catch (FileNotFoundException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println(e);
        }

        return result;
    }

    private static Complex[] parseLine(String str) {
        String[] strComplexArray = str.split("\\|", 0);
        Complex[] result = new Complex[fftSize];
        for (int i=0; i<fftSize; i++) {
            result[i] = parseComplex(strComplexArray[i]);
        }

        return result;
    }

    private static Complex parseComplex(String str) {
        str = str.replaceAll("\\(", "");
        str = str.replaceAll("\\)", "");
        String[] strComplex = str.split(",", 0);

        Complex result = new Complex(Double.parseDouble(strComplex[0]), Double.parseDouble(strComplex[1]));
        return result;
    }
}
