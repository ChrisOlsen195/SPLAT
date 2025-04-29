/**************************************************
 *             Single_t_SumStats_Model            *
 *                    02/15/25                    *
 *                     09:00                      *
 *************************************************/

package the_t_procedures;

import dialogs.t_and_z.Single_t_SumStats_Dialog;
import java.util.ArrayList;
import probabilityDistributions.TDistribution;
import utilityClasses.*;

public class Single_t_SumStats_Model {
    // POJOs
    boolean printTheStuff = true;
    //boolean printTheStuff = false;
    
    int df, n, confidenceLevel;
    
    private double tStatistic, pValue, xBar, stDev, dbl_n, 
                   hypothMean, critical_t, stErr, ciMean_Low, ciMean_High, 
                   alpha, alphaOverTwo, effectSize;
    
    String altHypothesis, varLabel, returnStatus, strCITitle;
    Single_t_SumStats_Controller single_t_SummaryStats_Controller;
    Single_t_SumStats_Dialog oneMeanDialog;
    
    ArrayList<String> singleMeanReport;
    
    static TDistribution tDist;
    
    public Single_t_SumStats_Model (Single_t_SumStats_Controller single_t_SummaryStats_Controller, 
                                       Single_t_SumStats_Dialog oneMeanDialog) {
        this.single_t_SummaryStats_Controller = single_t_SummaryStats_Controller;
        this.oneMeanDialog = oneMeanDialog;
        if (printTheStuff == true) {
            System.out.println("38 *** Single_t_SumStats_Model, Constructing");
        }
    }
    
    public String doSingleTAnalysis() {
        if (printTheStuff == true) {
            System.out.println("44 --- Single_t_SumStats_Model, doSingleTAnalysis()");
        }
        returnStatus = "OK";
        altHypothesis = single_t_SummaryStats_Controller.getHypotheses();
        hypothMean = single_t_SummaryStats_Controller.getHypothesizedMean();
        n = oneMeanDialog.getN();
        xBar = oneMeanDialog.getXBar();
        stDev = oneMeanDialog.getStDev();
        effectSize = getCohensD(xBar, hypothMean, n, stDev);      
        df = n - 1;
        
        if (df < 1) {
            MyAlerts.showSampleSizeTooSmallAlert();
            returnStatus = "Cancel";
            return returnStatus;
        }
        
        dbl_n = n;
        //dbl_df = n - 1;
        stErr = stDev / Math.sqrt(dbl_n);
        tStatistic = (xBar - hypothMean) / stErr;
        
        varLabel = single_t_SummaryStats_Controller.getDescriptionOfVariable();
        varLabel = StringUtilities.getleftMostNChars(varLabel, 16);

        alpha = single_t_SummaryStats_Controller.getAlpha();
        confidenceLevel = (int)(100.0 *(1.0 - alpha));
        alphaOverTwo = alpha / 2.0;
        hypothMean = single_t_SummaryStats_Controller.getHypothesizedMean();
        
        alphaOverTwo = alpha / 2.0;
        tDist = new TDistribution(df);
        pValue = tDist.getRightTailArea(tStatistic);

        critical_t = tDist.getInvRightTailArea(alphaOverTwo);
        ciMean_Low = xBar - critical_t * stErr; 
        ciMean_High = xBar + critical_t * stErr;
        printStatistics();
        return returnStatus;
    }
    
    public void printStatistics() {
        if (printTheStuff == true) {
            System.out.println("87 --- Single_t_SumStats_Model, printStatistics()");
        }
        double tLow, tHigh;
        singleMeanReport = new ArrayList();  
        
        switch (altHypothesis) {
            case "NotEqual":  
                critical_t = tDist.getCriticalT(0.025);                
                tLow = -critical_t ;
                tHigh = critical_t ;
                
                ciMean_Low = xBar - critical_t * stErr;
                ciMean_High = xBar + critical_t * stErr;
                pValue = 2.0 * tDist.getRightTailArea(Math.abs(tStatistic));                
                printSummaryInformation();                
                addNBlankLinesToOneMeanReport(2);
                
                strCITitle = "                     ***  Hypothesis Test & " 
                + String.valueOf(confidenceLevel) 
                + "% Confidence interval ***";
                singleMeanReport.add(strCITitle);            
                addNBlankLinesToOneMeanReport(2);
                singleMeanReport.add(String.format("  %30s %5.3f", "                   Null hypothesis: \u03BC\u2081  = ", hypothMean));
                addNBlankLinesToOneMeanReport(1);
                singleMeanReport.add(String.format("  %30s %5.3f", "            Alternative hypothesis: \u03BC\u2081  \u2260 ", hypothMean));
                addNBlankLinesToOneMeanReport(2);
                singleMeanReport.add("       Mean       StandErr        df     t_Value       pValue     ciLow   ciHigh");
                addNBlankLinesToOneMeanReport(1);
                singleMeanReport.add(String.format("     %8.3f     %8.3f      %4d    %8.3f    %8.3f   %8.3f  %8.3f",
                                                                                                  xBar,
                                                                                                  stErr,
                                                                                                  df,
                                                                                                  tStatistic,
                                                                                                  pValue,
                                                                                                  ciMean_Low,
                                                                                                  ciMean_High));  
                
                addNBlankLinesToOneMeanReport(2);
                singleMeanReport.add(String.format("      Effect size (Cohen's D) = %5.3f",  effectSize)); 
                addNBlankLinesToOneMeanReport(1);
                break;
        
            case "LessThan":
                System.out.println("109 singleTsumstats, doing LessThan");
                critical_t = tDist.getCriticalT(0.05);
                tHigh = critical_t ;
                ciMean_Low = xBar - critical_t * stErr;
                ciMean_High = xBar + critical_t * stErr;
                pValue = tDist.getLeftTailArea(tStatistic);
                printSummaryInformation();
                addNBlankLinesToOneMeanReport(2);
                strCITitle = "                     ***  Hypothesis Test & " 
                + String.valueOf(confidenceLevel) 
                + "% Confidence interval ***";
                addNBlankLinesToOneMeanReport(2);
                singleMeanReport.add(String.format("  %30s %5.3f", "                   Null hypothesis: \u03BC\u2081  = ", hypothMean));
                addNBlankLinesToOneMeanReport(1);
                singleMeanReport.add(String.format("  %30s %5.3f", "            Alternative hypothesis: \u03BC\u2081  < ", hypothMean));
                addNBlankLinesToOneMeanReport(2);
                singleMeanReport.add(String.format("\n\n       Mean       StandErr       df     t_Value      pValue    ciLow       ciHigh"));
                addNBlankLinesToOneMeanReport(1);
                singleMeanReport.add(String.format("   %8.3f     %8.3f       %4d    %8.3f   %8.3f   %8.3f        %1s",
                                                                                                  xBar,
                                                                                                  stErr,
                                                                                                  df,
                                                                                                  tStatistic,
                                                                                                  pValue,
                                                                                                  ciMean_Low,
                                                                                                  "\u221E")); 
                
                addNBlankLinesToOneMeanReport(2);
                singleMeanReport.add(String.format("      Effect size (Cohen's D) = %5.3f",  effectSize)); 
                addNBlankLinesToOneMeanReport(1);
                break;
            
            case "GreaterThan":
                System.out.println("138 singleTsumstats, doing GreaterThan");
                critical_t = tDist.getCriticalT(0.05);
                tLow = critical_t ;
                ciMean_Low = xBar - critical_t * stErr;
                ciMean_High = xBar + critical_t * stErr;
                pValue = tDist.getRightTailArea(tStatistic);
                printSummaryInformation();
                addNBlankLinesToOneMeanReport(2);
                strCITitle = "                     ***  Hypothesis Test & " 
                + String.valueOf(confidenceLevel) 
                + "% Confidence interval ***";
                addNBlankLinesToOneMeanReport(2);
                singleMeanReport.add(String.format("  %30s %5.3f", "                   Null hypothesis: \u03BC\u2081  = ", hypothMean));
                addNBlankLinesToOneMeanReport(1);
                singleMeanReport.add(String.format("  %30s %5.3f", "            Alternative hypothesis: \u03BC\u2081  > ", hypothMean));
                addNBlankLinesToOneMeanReport(2);
                singleMeanReport.add(String.format("        Mean       StandErr       df      t_Value     pValue      ciLow     ciHigh"));
                addNBlankLinesToOneMeanReport(1);
                singleMeanReport.add(String.format("     %8.3f     %8.3f      %4d    %8.3f    %8.3f        %2s     %8.3f ",
                                                                                                  xBar,
                                                                                                  stErr,
                                                                                                  df,
                                                                                                  tStatistic,
                                                                                                  pValue,
                                                                                                  "-\u221E",
                                                                                                  ciMean_High)); 
                
                addNBlankLinesToOneMeanReport(2);
                singleMeanReport.add(String.format("      Effect size (Cohen's D) = %5.3f",  effectSize)); 
                addNBlankLinesToOneMeanReport(1);
                break;
            
            default: 
                String switchFailure = "Switch failure: Single t SumStats_Model 185 " + altHypothesis;
                MyAlerts.showUnexpectedErrorAlert(switchFailure);
                break;
            }          
    }
    
private void printSummaryInformation() {
    if (printTheStuff == true) {
        System.out.println("204 --- Single_t_SumStats_Model, printSummaryInformation()");
    }
    addNBlankLinesToOneMeanReport(2);
    singleMeanReport.add("                       *** Summary information ***   ");
    addNBlankLinesToOneMeanReport(2);
    singleMeanReport.add("                    NSize         Mean        StDev     StErr");
    addNBlankLinesToOneMeanReport(1);
    singleMeanReport.add(String.format("   %16s %4d          %5.3f       %5.3f     %5.3f",  varLabel,
                                                                                            n,
                                                                                            xBar,
                                                                                            stDev,
                                                                                            stErr));    
}

    private double getCohensD(double xBar, double muNull, int n, double s) {
        double cohensD, cohensD_Unbiased;
        cohensD = (xBar - muNull) / s;
        
        if(altHypothesis.equals("NotEqual")) { cohensD = Math.abs(cohensD); }
        
        // Cumming, p294
        cohensD_Unbiased = cohensD * (1.0 - 3.0 / (4.0 * df - 1.0));
        return cohensD_Unbiased;
    }
    
    private void addNBlankLinesToOneMeanReport(int thisMany) {
        for (int ithBlank = 0; ithBlank < thisMany; ithBlank++) {
            singleMeanReport.add("\n");
        }
    }
       
    public ArrayList<String> getStringsToPrint() { return singleMeanReport; }
    public int getDF() { return df; }
    public double getAlpha() { return alpha; }
    public String getHypotheses() { return altHypothesis; }
    public double getTStat() { return tStatistic; }
    public double getPValue() { return pValue; } 
}


