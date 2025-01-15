/**************************************************
 *                 BootTheDiffs                   *
 *                   01/08/25                     *
 *                     15:00                      *
 *************************************************/
package bootstrapping;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BootTheDiffs {
    
    double[] theBooteds;
    
    public BootTheDiffs(int nReplications, double[] group1, double[] group2) {
        theBooteds = new double[nReplications];
        double observedDiff = mean(group1) - mean(group2);
        int numPermutations = nReplications;
        int countExtreme = 0;

        for (int ithShuffle = 0; ithShuffle < numPermutations; ithShuffle++) {
            double[] combined = combine(group1, group2);
            shuffle(combined);
            double[] shuffledGroup1 = Arrays.copyOfRange(combined, 0, group1.length);
            double[] shuffledGroup2 = Arrays.copyOfRange(combined, group1.length, combined.length);

            double shuffledDiff = mean(shuffledGroup1) - mean(shuffledGroup2);
            theBooteds[ithShuffle] = shuffledDiff;
            if (Math.abs(shuffledDiff) >= Math.abs(observedDiff)) {
                countExtreme++;
            }
        }

        double pValue = (double) countExtreme / numPermutations;             
    }
        
    private static double mean(double[] data) {
        double sum = 0;
        for (int ith = 0; ith < data.length; ith++) {
            sum += data[ith];
        }
        double daMean = sum / data.length;
        return daMean;
    }

    private static double[] combine(double[] a, double[] b) {
        double[] combined = new double[a.length + b.length];
        System.arraycopy(a, 0, combined, 0, a.length);
        System.arraycopy(b, 0, combined, a.length, b.length);
        return combined;
    }

    private static void shuffle(double[] data) {
        List<Double> dataList = Arrays.stream(data).boxed().collect(Collectors.toList());
        Collections.shuffle(dataList);
        for (int i = 0; i < data.length; i++) {
            data[i] = dataList.get(i);
        }
    }
    
    public double[] getTheBooteds() { return theBooteds; }
        
}
    
