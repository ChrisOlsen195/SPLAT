/**************************************************
 *                 Single_t_Model                 *
 *                    11/01/23                    *
 *                     18:00                      *
 *************************************************/

package the_t_procedures;

import dataObjects.QuantitativeDataVariable;
import java.util.ArrayList;
import theRProbDists.*;
import splat.Data_Manager;
import utilityClasses.MyAlerts;
import utilityClasses.StringUtilities;

public class Single_t_Model {
    
    private int df, nLegal, confidenceLevel;
    
    private double tStatistic, pValue, xBar, variance, stDev, v1, double_n1, 
            hypothMean, critical_t, stErr, ciMean_Low, ciMean_High, alpha, 
            alphaOverTwo, effectSize;
    
    private String altHypothesis, varLabel, returnStatus;
    private String[] hypothesesToPrint;
    ArrayList<String> singleMeanReport;
    
    //String waldoFile = "Single_t_Model";
    String waldoFile = "";
    
    Data_Manager dm;
    private Single_t_Controller single_t_Controller;    
    private QuantitativeDataVariable theQDV;   
    private T_double_df tDist;
    
    
// ***************  Called by Independent t procedure  **********************    
    public Single_t_Model (Single_t_Controller single_t_Controller, QuantitativeDataVariable theQDV) {
        dm = single_t_Controller.getDataManager();
        dm.whereIsWaldo(40, waldoFile, "Constructing from Single_t_Controller");        
        this.single_t_Controller = single_t_Controller;
        this.theQDV = new QuantitativeDataVariable();
        this.theQDV = theQDV;
    }
    
    // ***************  Called by Independent t procedure  *******************
    public String doTAnalysis() {
        dm.whereIsWaldo(48, waldoFile, "--- doTAnalysis()");         
        altHypothesis = single_t_Controller.getHypotheses();
        hypothMean = single_t_Controller.getHypothesizedMean();
        hypothesesToPrint = new String[2];
        hypothesesToPrint = single_t_Controller.getHypothPair();
        alpha = single_t_Controller.getAlpha();
        confidenceLevel = (int)(100. * (1 - alpha));
        alphaOverTwo = alpha / 2.0;
        varLabel = single_t_Controller.getDescriptionOfVariable();
        varLabel = StringUtilities.getleftMostNChars(varLabel, 16);
        nLegal = theQDV.getLegalN();
        xBar = theQDV.getTheMean();
        variance = theQDV.getTheVariance();  
        stDev = Math.sqrt(variance);
        
        double_n1 = nLegal;
        v1 = variance / double_n1;
        df = nLegal - 1;

        if (df < 1) {
            MyAlerts.showSampleSizeTooSmallAlert();
            returnStatus = "Cancel";
            return returnStatus;
        }
        
         if (nLegal > 250) {
            MyAlerts.showEffectSizeSuggestion();
        }       
        
        stErr = Math.sqrt(v1);
        tStatistic = (xBar - hypothMean) / Math.sqrt(v1);        
        tDist = new T_double_df(df);
        effectSize = getCohensD(xBar, hypothMean, nLegal, stDev);
        printStatistics();
        return returnStatus;
    }
    
    public void printStatistics() {
        dm.whereIsWaldo(88, waldoFile, "printStatistics()");
        singleMeanReport = new ArrayList();  
        
        switch (altHypothesis) {
            case "NotEqual":  
                critical_t = tDist.quantile(1. - alphaOverTwo);
                ciMean_Low = xBar - critical_t * stErr;
                ciMean_High = xBar + critical_t * stErr;
                pValue = 2.0 * tDist.cumulative(-Math.abs(tStatistic));
                printSummaryInformation();                
                addNBlankLinesToOneMeanReport(2);
                singleMeanReport.add("                     ***  Hypothesis Test  ***");
                addNBlankLinesToOneMeanReport(2);
                singleMeanReport.add(hypothesesToPrint[0]);
                addNBlankLinesToOneMeanReport(1);
                singleMeanReport.add(hypothesesToPrint[1]);
                addNBlankLinesToOneMeanReport(2);

                String strCITitle = "                     ***  Hypothesis Test & " 
                    + String.valueOf(confidenceLevel) 
                    + "% Confidence interval ***";
                singleMeanReport.add(strCITitle);
                addNBlankLinesToOneMeanReport(2);  
                singleMeanReport.add("       Mean       StandErr       df     t_Value     pValue        ciLow       ciHigh");
                addNBlankLinesToOneMeanReport(1);
                singleMeanReport.add(String.format("    %8.3f     %8.3f      %4d    %8.3f      %5.3f      %8.3f     %8.3f",
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
                critical_t = tDist.quantile(1. - alpha);
                ciMean_Low = xBar - critical_t * stErr;
                ciMean_High = xBar + critical_t * stErr;
                pValue = 1.0 - tDist.cumulative(-Math.abs(tStatistic));
                printSummaryInformation();                
                addNBlankLinesToOneMeanReport(2);
                
                singleMeanReport.add("                     ***  Hypothesis Test  ***");
                addNBlankLinesToOneMeanReport(2);
                singleMeanReport.add(hypothesesToPrint[0]);
                addNBlankLinesToOneMeanReport(1);
                singleMeanReport.add(hypothesesToPrint[1]);
                addNBlankLinesToOneMeanReport(2);
                singleMeanReport.add(String.format("\n\n       Mean       StandErr       df     t_Value       pValue     ciLow    ciHigh"));
                addNBlankLinesToOneMeanReport(1);
                singleMeanReport.add(String.format("   %8.3f     %8.3f      %4d    %8.3f      %8.3f  %8.3f     %2s",
                                                                                                  xBar,
                                                                                                  stErr,
                                                                                                  df,
                                                                                                  tStatistic,
                                                                                                  pValue,
                                                                                                  ciMean_Low,
                                                                                                  "+\u221E")); 
                addNBlankLinesToOneMeanReport(2);
                singleMeanReport.add(String.format("      Effect size (Cohen's D) = %5.3f",  effectSize)); 
                addNBlankLinesToOneMeanReport(1);
                break;
            
            case "GreaterThan":
                critical_t = tDist.quantile(1. - alphaOverTwo);
                ciMean_Low = xBar - critical_t * stErr;
                ciMean_High = xBar + critical_t * stErr;
                pValue = tDist.cumulative(-Math.abs(tStatistic));
                printSummaryInformation();
                
                addNBlankLinesToOneMeanReport(2);
                singleMeanReport.add("                     ***  Hypothesis Test  ***");
                addNBlankLinesToOneMeanReport(2);
                singleMeanReport.add(hypothesesToPrint[0]);
                addNBlankLinesToOneMeanReport(1);
                singleMeanReport.add(hypothesesToPrint[1]);
                addNBlankLinesToOneMeanReport(2);
                singleMeanReport.add(String.format("       Mean       StandErr      df     t_Value      pValue     ciLow      ciHigh"));
                addNBlankLinesToOneMeanReport(1);
                singleMeanReport.add(String.format("   %8.3f     %8.3f      %4d    %8.3f    %8.3f      %2s       %8.3f",
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
                    String switchFailure = "Switch failure: Single_t_Model 179 " + altHypothesis;
                    MyAlerts.showUnexpectedErrorAlert(switchFailure);
            }        
    }
    
private void printSummaryInformation() {
    addNBlankLinesToOneMeanReport(2);
    singleMeanReport.add("                    *** Summary information ***   ");
    addNBlankLinesToOneMeanReport(2);
    singleMeanReport.add("                    NSize          Mean          StDev     StErr");
    addNBlankLinesToOneMeanReport(1);
    singleMeanReport.add(String.format("   %16s %4d          %5.3f          %5.3f     %5.3f",  varLabel,
                                                                               nLegal,
                                                                               xBar,
                                                                               stDev,
                                                                               stErr));    
}

    private double getCohensD(double xBar, double muNull, int n, double s) {
        double cohensD, cohensD_Unbiased;
        dm.whereIsWaldo(208, waldoFile, "getCohensD");
        cohensD = (xBar - muNull) / s;
        
        if(altHypothesis.equals("NotEqual")) { cohensD = Math.abs(cohensD); }

        cohensD_Unbiased = cohensD * (1.0 - 3.0 / (4.0 * (double)df - 1.0));
        return cohensD_Unbiased;
    }
    
    private void addNBlankLinesToOneMeanReport(int thisMany) {
        StringUtilities.addNLinesToArrayList(singleMeanReport, thisMany);
    }
    
    public Data_Manager getDataManager() { return dm; }
    public int getDF() { return df; }
    public double getTStat() { return tStatistic; }
    public double getPValue() { return pValue; }
    public double getAlpha() {return alpha; }
    public String getHypotheses() { return altHypothesis; }
    public ArrayList<String> getStringsToPrint() { return singleMeanReport; }    
}
