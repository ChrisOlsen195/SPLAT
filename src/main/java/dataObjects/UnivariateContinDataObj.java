/**************************************************
 *             UnivariateContinDataObj            *
 *                    05/24/24                    *
 *                     15:00                      *
 *************************************************/
package dataObjects;

import genericClasses.JustAnAxis;
import genericClasses.Point_2D;
import java.util.ArrayList;
import java.util.Arrays;
import probabilityDistributions.*;
import utilityClasses.MyAlerts;

public class UnivariateContinDataObj {
    // POJOs
    boolean meanBasedDone, medianBasedDone, binsCreated,
            hasForcedIntegerMajorTicks, hasForcedLowEndOfScale, 
            hasForcedHighEndOfScale, andersonDarlingCalculated,
            containsAZero, containsANegative, containsANonPositive;
    
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
        
    int nLegalDataPoints, nBins, nMajorIntervals, rankLoWhisker, rankHiWhisker;
    int n_Uniques;
    int[] binFrequency, cutOffRanks;
    int[] countsOfUniqueValues;
    
    double minimum, q1, q2, q3, maximum, iqr, range, marginOfError,
           sumOfX2, coefOfVar, sumOfX, mean, sampleVariance, sampleStandDev, 
           popVariance, popStandDev, adjusted_fisher_pearson_skew, kurtosis, 
           excessKurtosis, sumOfSquaresDev, seMean, minMajorTick, maxMajorTick, 
           majorTickRange, binWidth, intervalsPerMajorTick, maximumFreq, 
           forcedLowEndOfScaleIs, forcedHighEndOfScaleIs, fisher_pearson_skew,
           tri_mean, percentile, percentileRank;
    
    double[] sortedArray, middleInterval, deviations, unsortedArray, adStats,
             uniqueValues;
    
    String dataLabel, dataDescription;
    
    // My classes
    QuantitativeDataVariable qdv;
    TDistribution tDistribution;    //  For margin of error    
        
    public UnivariateContinDataObj ()  { 
        if (printTheStuff) {
            System.out.println("49 UnivariateContinDataObj, constructing");
        }    
    } //Need this constructor for ANOVA2

    public UnivariateContinDataObj(String callSource, QuantitativeDataVariable qdv) {
        if (printTheStuff) {
            System.out.println("55 UnivariateContinDataObj, constructing");
        }
        this.qdv = new QuantitativeDataVariable();
        this.qdv = qdv;
        dataLabel = qdv.getTheVarLabel();
        dataDescription = qdv.getTheVarDescription();
        nLegalDataPoints = this.qdv.get_nDataPointsLegal();
        init_UCDO();
        unsortedArray = new double[nLegalDataPoints];
        unsortedArray = this.qdv.getLegalDataAsDoubles();
        sortedArray = new double[nLegalDataPoints];
        System.arraycopy(unsortedArray, 0, sortedArray, 0, nLegalDataPoints);
        Arrays.sort(sortedArray);
        containsAZero = false;
        doAllStatistics();      
    }

    private void init_UCDO()  {
        if (printTheStuff) {
            System.out.println("74 UnivariateContinDataObj, init_UCDO()");
        }
        andersonDarlingCalculated = false;
        meanBasedDone = false;
        medianBasedDone = false;
        binsCreated = false;
        cutOffRanks = new int[2];
        intervalsPerMajorTick = 10.0;   // This value controls the bin size       
    }
       
    private void doAllStatistics(){
        doMedianBasedCalculations();
        doMeanBasedCalculations(); 
        constructFrequencyDistribution();
    }

    public void doMeanBasedCalculations() { calculateFirstFourMoments(); }
    
    public void doMedianBasedCalculations() {
        if (printTheStuff) {
            System.out.println("94 UnivariateContinDataObj, doMedianBasedCalculations()");
        }
        double temp1, temp2, lowOutlierCutOff, highOutlierCutOff; 
        
        if (medianBasedDone) { return; }
        
        Arrays.sort(sortedArray);   // Now is sorted
        medianBasedDone = true;
        cutOffRanks = new int[2];
                
        if( nLegalDataPoints >= 4) {        
            int mod4 = nLegalDataPoints % 4;
            
            switch (mod4) {
                case 0: 
                    temp1 = sortedArray[nLegalDataPoints / 4 - 1];
                    temp2 = sortedArray[nLegalDataPoints / 4];                
                    q1 =(temp1 + temp2) / 2.0;
                    temp1 = sortedArray[nLegalDataPoints / 2 - 1];
                    temp2 = sortedArray[nLegalDataPoints / 2];
                    q2 = (temp1 + temp2) / 2.0;
                    temp1 = sortedArray[3 * nLegalDataPoints / 4 - 1];
                    temp2 = sortedArray[3 * nLegalDataPoints / 4]; 
                    q3 = (temp1 + temp2) / 2.0;   

                    break;

                case 1:
                    temp1 = sortedArray[nLegalDataPoints / 4 - 1];
                    temp2 = sortedArray[nLegalDataPoints / 4];                
                    q1 =(temp1 + temp2) / 2.0;
                    q2 = sortedArray[nLegalDataPoints / 2];
                    temp1 = sortedArray[3 * nLegalDataPoints / 4 ];
                    temp2 = sortedArray[3 * nLegalDataPoints / 4 + 1];  
                    q3 = (temp1 + temp2) / 2.0; 
                    break;

                case 2:
                    q1 = sortedArray[nLegalDataPoints / 4];
                    temp1 = sortedArray[nLegalDataPoints / 2 - 1];
                    temp2 = sortedArray[nLegalDataPoints / 2];
                    q2 = (temp1 + temp2) / 2.0;
                    q3 = sortedArray[3 * nLegalDataPoints / 4];                
                   break; 

                case 3:
                    q1 = sortedArray[nLegalDataPoints / 4];
                    q2 = sortedArray[nLegalDataPoints / 2];
                    q3 = sortedArray[3 * nLegalDataPoints / 4]; 
                   break;

            default:
                String switchFailure = "Switch failure: UnivContinDataObj 146" + String.valueOf(mod4);
                MyAlerts.showUnexpectedErrorAlert(switchFailure);  
                    
            }   //  end switch  
        } // end if nDataPoints >= 4
        else {
            switch (nLegalDataPoints) {
                case 1:               
                    q1 = sortedArray[0];
                    q2 = sortedArray[0];
                    q3 = sortedArray[0];
                                                            
                    break;

                case 2:
                    q1 = sortedArray[0];
                    q2 = (sortedArray[0] + sortedArray[1]) / 2.0;
                    q3 = sortedArray[1];               
                   break; 

                case 3:

                    q1 = sortedArray[0];
                    q2 = sortedArray[1];
                    q3 = sortedArray[2];
                   break;

                default:
                    break;

            }   //  end switch  
        }
        minimum = sortedArray[0];
        maximum = sortedArray[nLegalDataPoints - 1];
        range = maximum - minimum;
        iqr = q3 - q1;
        
        lowOutlierCutOff = q1 - 1.5 * iqr;
        highOutlierCutOff = q3 + 1.5 * iqr;
        
        tri_mean = (q1 + 2  * q2 + q3) / 4.0;
        
        //  Seek out outliers
        rankLoWhisker = -1; 
        rankHiWhisker = -1;
        
        for (int ith_Rank = 0; ith_Rank < nLegalDataPoints; ith_Rank++){
            
            if (sortedArray[ith_Rank] < lowOutlierCutOff) {
                rankLoWhisker = ith_Rank + 1;
            }
            
            if (sortedArray[ith_Rank] <= highOutlierCutOff) {
                rankHiWhisker = ith_Rank;
            }
        }
        
        cutOffRanks[0] = rankLoWhisker;
        cutOffRanks[1] = rankHiWhisker;
        medianBasedDone = true;
    }   
    
    private void calculateFirstFourMoments() {
        if (meanBasedDone) { return; }
        if (printTheStuff) {
            System.out.println("211 *** UnivariateContinDataObj, calculateFirstFourMoments()");
        }
        
        meanBasedDone = true;
        sumOfX = 0.0;
        sumOfSquaresDev = 0.0;
        sumOfX2 = 0.0;
        
        double[] deviation = new double[nLegalDataPoints];
        
        for (int ith_Data_Point = 0; ith_Data_Point < nLegalDataPoints; ith_Data_Point++) {
            double temp = sortedArray[ith_Data_Point];
            if (temp == 0.0) { containsAZero = true; containsANonPositive = true;}
            if (temp <= 0.0) { containsANonPositive = true; }
            if (temp < 0) {containsANegative = true; }

            sumOfX += temp;
            sumOfX2 += (temp * temp);
        }
        mean = sumOfX / nLegalDataPoints;
        
        for (int ith_Data_Point = 0; ith_Data_Point < nLegalDataPoints; ith_Data_Point++){
            deviation[ith_Data_Point] = sortedArray[ith_Data_Point] - mean;
            sumOfSquaresDev = sumOfSquaresDev + (deviation[ith_Data_Point] * deviation[ith_Data_Point]);
        }
        
        sampleVariance = sumOfSquaresDev / (nLegalDataPoints - 1);
        sampleStandDev = Math.sqrt(sampleVariance);
        /**********************************************************************
         *  The standard deviation for skew and kurtosis is calculated with N *
         *  rather than N - 1. Calculations for skew and kurtosis are from:   *                                             *                      *
         *  https://www.itl.nist.gov/div898/handbook/eda/section3/eda35b.htm  *
         *********************************************************************/
        popVariance = sumOfSquaresDev / (nLegalDataPoints);
        popStandDev = Math.sqrt(popVariance);        
 
        if (mean != 0.0) {
            coefOfVar = sampleStandDev / mean;
        }
        else {
            coefOfVar = Double.NaN; 
        }
        
        seMean = sampleStandDev / Math.sqrt(nLegalDataPoints - 1.0);

        double sumDev3, sumDev4;
        sumDev3 = sumDev4 = 0.0;
        
        for (int ith_Data_Point = 0; ith_Data_Point < nLegalDataPoints; ith_Data_Point++){
            double temp1 = deviation[ith_Data_Point];
            sumDev3 = sumDev3 + temp1 * temp1 * temp1;
            sumDev4 = sumDev4 + temp1 * temp1 * temp1 * temp1;    
        }
        
        //  Fisher-Pearson adjusted_fisher_pearson_skew
        fisher_pearson_skew = sumDev3 / (nLegalDataPoints * popStandDev * popStandDev * popStandDev);   
        double cap_g1_Factor = Math.sqrt(nLegalDataPoints * (nLegalDataPoints - 1.)) / (nLegalDataPoints - 2.0);
        // Adjusted Fisher-Pearson adjusted_fisher_pearson_skew
        adjusted_fisher_pearson_skew = cap_g1_Factor * fisher_pearson_skew;
        //  These are biased statistics.
        kurtosis = sumDev4 / (nLegalDataPoints * popStandDev * popStandDev * popStandDev * popStandDev); 
        excessKurtosis = kurtosis - 3.0;    } 
    
    // 100 pcTile fraction at or below pcTile
    public double fromPercentile_toPercentileRank(double pcTile) {
        if (printTheStuff) {
            System.out.println("290 *** UnivariateContinDataObj, fromPercentile_toPercentileRank");
            System.out.println("291 UCDO, pcTile: " + pcTile);
        }

        double pcRank = 0.;
        // Find pcRank of last data point less than or equal to pcTile
        for (int ithDataPoint = 0; ithDataPoint < nLegalDataPoints; ithDataPoint ++) {
            if (sortedArray[ithDataPoint] > pcTile) 
                break;
            
            pcRank = ithDataPoint;
        }
        double pcTileRank = 100. * (pcRank + 1.0)/ nLegalDataPoints;
        return pcTileRank;
    }
    
    /***********************************************************************
     *          Not actually sure when or why these might be used.         * 
     *                Make a table perhaps?                                *
     **********************************************************************/
    
    public double get_Percentile() { return percentile; }
    public void set_Percentile(double toThis) { percentile = toThis; }
    
    public double get_PercentileRank() { return percentileRank; }
    public void set_PercentileRank(double toThis) { percentileRank = toThis; }
    
    /***********************************************************************
     *          Not actually sure when these might be used.                * 
     **********************************************************************/
    /**********************************************************************
     *                   NIST 7.2.6.2 Percentiles                         *
     * https://www.itl.nist.gov/div898/handbook/prc/section2/prc262.htm   *
     *********************************************************************/
    public double fromPercentileRank_toPercentile(double ithPCTileRank) { //    0 < pcTile < 1
        if (printTheStuff == true) {
            System.out.println("316 *** UnivariateContinDataObj, fromPercentileRank_toPercentile");
            System.out.println("317 UCDO, fromPercentileRank_toPercentile");
            System.out.println("318 UCDO, pcTileRank of: " + ithPCTileRank);
        }

        double temp1  = ithPCTileRank * (nLegalDataPoints + 1);
        double k = Math.floor(temp1) - 1;
        if (k <= 0.) {
            return sortedArray[0];
        }
        else
        if (k >= nLegalDataPoints - 1) {
            return sortedArray[nLegalDataPoints - 1];
        }
        else {              
            double d = temp1 - k;
            double y_k = sortedArray[(int)k];
            double y_kPlus1 = sortedArray[(int)(k + 1)];
            double pcTile = y_k + d * (y_kPlus1 - y_k);
            return pcTile;
        }
    }
    
    public double[] getTheDeviations() {
        deviations = new double[nLegalDataPoints];
        for (int ith = 0; ith < nLegalDataPoints; ith++) {
            deviations[ith] = sortedArray[ith] - mean;
        }        
        return deviations;
    }
    
    /******************************************************************************************************************
     *                                   Anderson-Darling Statistic                                                   *
     *    Algorithm from D'Agostino, R. B., * Stephens, M. M. (1986). Goodness-of-Fit-Techniques. Marcel Dekker.  NY. *
     *                                          p101ff                                                                *
     *     Also (and for variable names) see:                                                                         *
     *        https://www.spcforexcel.com/knowledge/basic-statistics/anderson-darling-test-for-normality              *
     *****************************************************************************************************************/
    
    public double[] getAndersonDarling() {
        if (printTheStuff) {
            System.out.println("357 *** UnivariateContinDataObj, getAndersonDarling()");
        }
        // Need to be sure data are sorted
        if (!medianBasedDone){ doMedianBasedCalculations(); }
    
        if (!andersonDarlingCalculated) {
            calculateAndersonDarling();
            andersonDarlingCalculated = true;
        }
        return adStats;
    }
    
    private void calculateAndersonDarling() {
        double ad, adStar, pValue;
        adStats = new double[3];
        double[] z2c = new double[nLegalDataPoints];
        double[] fOfXi = new double[nLegalDataPoints];
        double[] oneMinusFOfXni1 = new double[nLegalDataPoints];
        
        StandardNormal standNorm = new StandardNormal();
        
        for (int i = 0; i < nLegalDataPoints; i++) {
            z2c[i] = (sortedArray[i] - mean) / sampleStandDev;
            /******************************************************
            *   Keep left tail area between 0.0 and 1.0           *
            ******************************************************/
            if (z2c[i] > 5.0) {
                z2c[i] = 5.0;
            }
            if (z2c[i] < -5.0) {
                z2c[i] = -5.0;
            }
                
            fOfXi[i] = standNorm.getLeftTailArea(z2c[i]);
        }
        
        for (int i = 0; i < nLegalDataPoints; i++) {
            oneMinusFOfXni1[i] = 1.0 - fOfXi[nLegalDataPoints - i - 1];
        }        
         
        double sum = 0.0;
        
         for (int i = 0; i < nLegalDataPoints; i++) {
            double log1 = Math.log(fOfXi[i]);
            double log2 = Math.log(oneMinusFOfXni1[i]);
            /*********************************************************
             *   Note: This differs from the usual AD formula, which *
             *         sums from 1 to n.  Here the summation is from *
             *         0  to n - 1                                   *
             ********************************************************/
            sum = sum + (2.0 * i + 1) * (log1 + log2);
        }   
         
        double doubleN = nLegalDataPoints;
        // Anderson-Darling statistic...
        ad = -doubleN - 1.0 / doubleN * sum;
        // ... adjusted for small sample sizes
        adStar = ad * (1.0 + 0.75 / doubleN + 2.25 / (doubleN * doubleN));

        if (adStar >= 0.6) {
            pValue = Math.exp(1.2937 - 5.709 * adStar + 0.0186 * adStar * adStar);
        }
        else if ((0.34 < adStar) && (adStar < 0.6)) {
            pValue = Math.exp(0.9177 - 4.279 * adStar - 1.38 * adStar * adStar);
        }
        else if ((0.2 < adStar) && (adStar< 0.34)) {
            pValue = 1.0 - Math.exp(-8.318 + 42.796 * adStar - 59.938 * adStar * adStar);
        }  
        else {
            pValue = 1.0 - Math.exp(-13.436 + 101.14 * adStar - 223.73 * adStar * adStar);
        }   
        
        adStats[0] = ad;
        adStats[1] = adStar;
        adStats[2] = pValue;
    }
    
    public double getTheTrimmedMean(double trimProp) {
        if (printTheStuff == true) {
            System.out.println("436 *** UnivariateContinDataObj, getTheTrimmedMean");
        }
        double sum, trimmedMean;
        int nToDrop = (int)Math.floor(trimProp * nLegalDataPoints);
        int stopAddingAt = nLegalDataPoints - nToDrop;
        sum = 0.0;
        
        for (int ithTrim = nToDrop; ithTrim < stopAddingAt; ithTrim++) {
            sum += sortedArray[ithTrim];
        }
        
        trimmedMean = sum / (nLegalDataPoints - 2 * nToDrop);
        return trimmedMean;
    }
    
    /*******************************************************************
     *   This calculation is for jittering in the vertical box plot.   *
     ******************************************************************/
    private void constructFrequencyDistribution() {
        if (printTheStuff) {
            System.out.println("456 *** UnivariateContinDataObj, constructFrequencyDistribution()");
        }        
        ArrayList <Point_2D>  al_StartStop;
        ArrayList <Double> al_UniqueValues;

        al_StartStop = new ArrayList();
        al_UniqueValues = new ArrayList();

        for (int ith = 0; ith < nLegalDataPoints - 1; ith++) {
            
            if (sortedArray[ith] != sortedArray[ith + 1]) {
                if (ith == 0) {
                    al_StartStop.add(new Point_2D(ith, ith));
                    al_UniqueValues.add(sortedArray[ith]);                        
                }
                else if (ith == nLegalDataPoints - 2) {
                    al_StartStop.add(new Point_2D(ith + 1, ith + 1));
                    al_UniqueValues.add(sortedArray[ith]);                         
                }
                else {
                    al_StartStop.add(new Point_2D(ith, ith + 1));
                    al_UniqueValues.add(sortedArray[ith]);
                }
            }
        }
        
        n_Uniques = al_UniqueValues.size();
         
        al_UniqueValues.add(sortedArray[nLegalDataPoints - 1]);
        n_Uniques++;

        countsOfUniqueValues = new int[n_Uniques];
        uniqueValues = new double[n_Uniques];
        
        for (int ithUnique = 0; ithUnique < n_Uniques; ithUnique++) {
            uniqueValues[ithUnique] = al_UniqueValues.get(ithUnique);
        }

        for (int ithInList = 0; ithInList < nLegalDataPoints; ithInList++) {
            
            for (int ithUnique = 0; ithUnique < n_Uniques; ithUnique++) {
                if (sortedArray[ithInList] == al_UniqueValues.get(ithUnique)) {
                    countsOfUniqueValues[ithUnique] = countsOfUniqueValues[ithUnique] +1;
                }
            }      
        } 
        if (printTheStuff) {
            System.out.println("503 *** UnivariateContinDataObj, end constructFrequencyDistribution()");
        }
    }

    // *********************************************************************
    //                    Bin stuff begins 
    // *********************************************************************
    
    public void createTheBins( JustAnAxis justAnAxis) {
        if (binsCreated) { return; }
        
        binsCreated = true;

        int nMajorTickPositions = justAnAxis.getNMajorTix();
        minMajorTick = (justAnAxis.getMajorTickMarkPositions().get(0)).doubleValue();
        maxMajorTick = (justAnAxis.getMajorTickMarkPositions().get(nMajorTickPositions - 1)).doubleValue();   

        double majorTikInterval = (justAnAxis.getMajorTickMarkPositions().get(1)).doubleValue()
                                    - (justAnAxis.getMajorTickMarkPositions().get(0)).doubleValue();

        minMajorTick -= majorTikInterval;
        maxMajorTick += majorTikInterval;
        majorTickRange = maxMajorTick - minMajorTick;
     
        binWidth = majorTikInterval / intervalsPerMajorTick; 
        nMajorIntervals = (int)Math.floor(majorTickRange / majorTikInterval + .001) + 1;
        nBins = (int)Math.floor(nMajorIntervals * intervalsPerMajorTick + .001);

        binFrequency = new int[nBins + 1]; //  nBins+1 in case data is on right end of max interval
  
        double firstBin = minMajorTick;
        double lastBin = minMajorTick + nBins * binWidth;
        double rangeOfBins = lastBin - firstBin;
        double m = nBins / rangeOfBins;
        double b = - nBins * firstBin / rangeOfBins;
        
        for (int ith_Data_Point = 0; ith_Data_Point < nLegalDataPoints; ith_Data_Point++) {
            int ith_Bin = (int)Math.floor(m * unsortedArray[ith_Data_Point] + b);
            binFrequency[ith_Bin] = binFrequency[ith_Bin] + 1;
        }
        
        maximumFreq = 0.0;
        for (int ith_Bin = 0; ith_Bin < nBins; ith_Bin++) {        
            if (binFrequency[ith_Bin] > maximumFreq)
                maximumFreq = binFrequency[ith_Bin];
        }
        
        maximumFreq += 1.0;  // Safety pad
    }
    
    // The binRange is needed by the View for graphing the bars
    public double[] getBinRange(int whichBin) {
        double[] binRange = new double[2];
        double leftEndOfBin = minMajorTick + whichBin * binWidth;
        double rightEndOfBin = leftEndOfBin + binWidth;
        binRange[0] = leftEndOfBin;
        binRange[1] = rightEndOfBin;
        return binRange;       
    }
       
    public boolean getHasForcedIntTix() { return hasForcedIntegerMajorTicks; }
    
    public void setForceIntTix(boolean forceIntTix) { 
        hasForcedIntegerMajorTicks = forceIntTix;
    }    
    
    public void forceLowScaleEndToBe( double thisLowEnd)  { 
        forcedLowEndOfScaleIs = thisLowEnd;
        hasForcedLowEndOfScale = true;
    }
    
    public void forceHighScaleEndToBe( double thisHighEnd)  { 
        forcedHighEndOfScaleIs = thisHighEnd;
        hasForcedHighEndOfScale = true;  
    }
    
    public void forceScaleLowAndHighEndsToBe(double thisLowEnd, double thisHighEnd) {
        forcedLowEndOfScaleIs = thisLowEnd;
        hasForcedLowEndOfScale = true;
        forcedHighEndOfScaleIs = thisHighEnd;
        hasForcedHighEndOfScale = true;
    }
    
    public boolean getHasForcedLowScaleEnd() {return hasForcedLowEndOfScale; }
    public boolean getHasForcedHighScaleEnd() {return hasForcedHighEndOfScale; }
    
    public double getForcedLowScaleEnd() {return forcedLowEndOfScaleIs; }
    public double getForcedHighScaleEnd() {return forcedHighEndOfScaleIs; }
    
    public void setForcedAxisEndsFalse()  { //  i.e. return to unforced state
        hasForcedLowEndOfScale = false;
        hasForcedHighEndOfScale = false;
    }
    
    // *********************************************************************
    //                    Bin stuff ends 
    // *********************************************************************
 
    public int getFrequencyForBin(int theBin) { return binFrequency[theBin]; }    
    public int[] getWhiskerEndRanks() { return cutOffRanks; }
    public double getSumX() { return sumOfX; }   
    public double getSumX2() { return sumOfX2; } 
    public double getTheMean() { return mean; }
    public double getTheStandDev() { return sampleStandDev; }
    public double getTheVariance() { return sampleVariance; }
    public double getFisherPearsonSkew() {  return fisher_pearson_skew; }
    public double getAdjustedFisherPearsonSkew() { return adjusted_fisher_pearson_skew; }
    public double getTheKurtosis() { return kurtosis; }
    public double getTheExcessKurtosis() { return excessKurtosis; }        
    public int getLegalN()  {return nLegalDataPoints; }
    public double getTheSS() {return sumOfSquaresDev; }    
    public double getStandErrMean() {return seMean;}    
    public double getTheMarginOfErr(double middleOfDist) { 
        int df = nLegalDataPoints - 1;
        tDistribution = new TDistribution(df);
        middleInterval = new double[2];
        middleInterval = tDistribution.getInverseMiddleArea(middleOfDist);
        double critical_t = middleInterval[1];
        marginOfError = critical_t * seMean;
        return marginOfError; 
    }

    public double[] getTheMoments() {
        double[] moments = new double[5];
        moments[0] = mean;  
        moments[1] = sampleStandDev;
        moments[2] = adjusted_fisher_pearson_skew;  
        moments[3] = kurtosis;
        moments[4] = excessKurtosis;
        return moments;
    }
    
    public double getSumSquaresOfDevs() { return sumOfSquaresDev; }   

    public double getMinValue() { return minimum; }
    public double getTheQ1() { return q1; }
    public double getTheMedian() { return q2; }   
    public double getTheQ3() { return q3; }
    public double getMaxValue() { return maximum; }    
    public double getTheCV() { return coefOfVar; }    
    public double getTheIQR() { return iqr; }    
    public double getTheTriMean() { return tri_mean; }
    public boolean getContainsAZero() { return containsAZero; }
    public boolean getContainsANonPositive() { return containsANonPositive; }
    public boolean getContainsANegative() {return containsANegative; }        
    public double getIthSortedValue(int rank) {return sortedArray[rank];} 
    
    //        For jittering
    public int getNUniques() { return n_Uniques; }
    public double[] getUniqueValues() {return uniqueValues; }
    public int[] getCountsOfUniqueValues() { return countsOfUniqueValues; }   
    public UnivariateContinDataObj getTheUnivContinDataObj() {return this; }    
    public int getNumberOfBins() { return nBins; }
    public double getBinWidth() { return binWidth; }
    public double getMaxFreq() { return maximumFreq; }
    public double getCoefOfVar() { return coefOfVar; }
    public double getTheRange() { return range; } 
    public String getTheDataLabel() { return dataLabel; }
    public String getTheDataDescription() { return dataDescription; }    
    public double[] getTheDataSorted() { return sortedArray; }    
    public double[] get_5NumberSummary(){
        double[] fiveNum = new double[5];
        fiveNum[0] = minimum;
        fiveNum[1] = q1;
        fiveNum[2] = q2;
        fiveNum[3] = q3;
        fiveNum[4] = maximum;
        return fiveNum;
    }    
    public double[] getTheSortedArray() { return sortedArray; }    
   
    public QuantitativeDataVariable getTheQDV() { return qdv; }    
    public String toString() {
        System.out.println("ucdo ToString, min = " + String.valueOf(minimum));
        System.out.println("ucdo ToString, q1 = " + String.valueOf(q1));
        System.out.println("ucdo ToString, q2 = " + String.valueOf(q2));
        System.out.println("ucdo ToString, mean = " + String.valueOf(mean));
        System.out.println("ucdo ToString, q3 = " + String.valueOf(q3));
        System.out.println("ucdo ToString, max = " + String.valueOf(maximum));        
        return "xxx";
    }
}
