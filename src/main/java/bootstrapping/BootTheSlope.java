/**************************************************
 *                 BootTheSlope                   *
 *                   01/08/25                     *
 *                     15:00                      *
 *************************************************/
package bootstrapping;

public class BootTheSlope {
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    int sampleSize;
    double observedSlope, observedCorr, shuffledSlope, shuffledCorr, corr, slope;
    double[] theBootedSlopes, theBootedCorrs, bootieX, bootieY;
    double[] group1, group2;
    
    public BootTheSlope(NonGenericBootstrap_Info nonGenericBootStrap_Info,
                                                 int nReplications, 
                                                 double[] originalX, 
                                                 double[] originalY) {
        if (printTheStuff) {
            System.out.println("\n20 *** BootTheSlope, Constructing");
        }
        sampleSize = originalX.length;
        group1 = new double[sampleSize];
        group2 = new double[sampleSize];
        nonGenericBootStrap_Info.getTheBootingStat();
        System.arraycopy(originalX, 0, group1, 0, sampleSize);
        System.arraycopy(originalY, 0, group2, 0, sampleSize);
        
        theBootedSlopes = new double[nReplications];
        theBootedCorrs = new double[nReplications];
        bootieX = new double[sampleSize];
        bootieY = new double[sampleSize];
        int countExtreme = 0;
        calculateCorrAndSlope(originalX, originalY);
        observedSlope = getSlope();
        observedCorr = getCorr();
        
        for (int ithShuffle = 0; ithShuffle < nReplications; ithShuffle++) {;
            resamplePairs();
            calculateCorrAndSlope(bootieX, bootieY);
            shuffledSlope = getSlope();
            shuffledCorr = getCorr();
            theBootedSlopes[ithShuffle] = shuffledSlope;
            theBootedCorrs[ithShuffle] = shuffledCorr;
            if (Math.abs(shuffledSlope) >= Math.abs(observedSlope)) {
                countExtreme++;
            }
        }  
        
        double pValue = (double) countExtreme / nReplications;  
    }

    private void resamplePairs() {
        for (int ithUnit = 0; ithUnit < sampleSize; ithUnit++) {
            int obsUnit = (int)(sampleSize * Math.random());
            bootieX[ithUnit] = group1[obsUnit];
            bootieY[ithUnit] = group2[obsUnit];
        } 
    }
    
    private void calculateCorrAndSlope(double x[], double y[]) {
        double sumX, sumY, meanX, meanY, sumDevX2, sumDevY2,
               sumProdOfDevs, daCorr, daSlope, dbl_SampleSize;
        double stDevX, stDevY;
        double[] devX, devY;
        
        sumX = sumY = sumDevX2 = sumDevY2 = sumProdOfDevs = 0.;
        devX = new double[sampleSize]; devY = new double[sampleSize];
        dbl_SampleSize = sampleSize;
        
        for (int ith = 0; ith < sampleSize; ith++) {
            sumX += x[ith]; sumY += y[ith];
        }
        
        meanX = sumX / (dbl_SampleSize); meanY = sumY / (dbl_SampleSize);

        for (int ith = 0; ith < sampleSize; ith++) {
            devX[ith] = x[ith] - meanX; devY[ith] = y[ith] - meanY;
            
            sumDevX2 += (devX[ith] * devX[ith]);
            sumDevY2 += (devY[ith] * devY[ith]);
        }
        
        stDevX = Math.sqrt(sumDevX2 / (dbl_SampleSize - 1.));
        stDevY = Math.sqrt(sumDevY2 / (dbl_SampleSize - 1.));

        for (int ith = 0; ith < sampleSize; ith++) {
            sumProdOfDevs += (devX[ith] * devY[ith]);
        }
        daCorr = sumProdOfDevs / ((dbl_SampleSize - 1) * stDevX * stDevY);
        daSlope = daCorr * stDevY / stDevX;
        setCorr(daCorr); setSlope(daSlope);
    } 
    
    public double[] getTheBooteds() {  return theBootedSlopes; }
    
    private double getCorr() { return corr; } 
    private double getSlope() { return slope; }   
    private void setCorr(double daCorr) { corr = daCorr; } 
    private void setSlope(double daSlope) { slope = daSlope; } 
}
    
