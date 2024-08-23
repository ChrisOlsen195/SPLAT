/**************************************************
 *                 Matched_t_Model                *
 *                    11/01/23                    *
 *                     15:00                      *
 *************************************************/
package the_t_procedures;

import dataObjects.QuantitativeDataVariable;
import java.util.ArrayList;
import theRProbDists.*;
import utilityClasses.*;
import splat.*;

public class Matched_t_Model {
    
    private int df, nLegal, confidenceLevel;
    
    private double tStatistic, pValue, xBarDiff, variance, stDevDiff, v1, double_n1, hypothDiff,
           critical_t, stErr, ciMean_Low, ciMean_High, alpha, alphaOverTwo,
           effectSize;
    
    private String varLabel, returnStatus;
    private String[] hypothesesToPrint;
    ArrayList<String> matchedTReport;
    
    // Make empty if no-print
    //String waldoFile = "Matched_t_Model";
    String waldoFile = "";
    
    // My classes
    public Data_Manager dm;
    private Matched_t_Controller matched_t_Controller;    
    private QuantitativeDataVariable theQDV;   
    private T_double_df tDist;
    private String theAltHypothesis;
        
    public Matched_t_Model (Matched_t_Controller matched_t_Controller,
                                QuantitativeDataVariable theQDV) {
        this.matched_t_Controller = matched_t_Controller;
        this.theQDV = new QuantitativeDataVariable();
        this.theQDV = theQDV;
        dm = matched_t_Controller.getDataManager();
        dm.whereIsWaldo(43, waldoFile, "Constructing");
    }

    public String doMatched_TAnalysis() {
        dm.whereIsWaldo(47, waldoFile, "doMatched_TAnalysis()");
        varLabel = matched_t_Controller.getDescriptionOfDifference();
        varLabel = StringUtilities.getleftMostNChars(varLabel, 16);
        nLegal = theQDV.getLegalN();
        xBarDiff = theQDV.getTheMean();
        variance = theQDV.getTheVariance();  
        stDevDiff = Math.sqrt(variance);
        double_n1 = nLegal;
        v1 = variance / double_n1;
        df = nLegal - 1;
        
        if (df < 1) {
            MyAlerts.showTooFewMatchedPairDFAlert();
            returnStatus = "Cancel";
            return returnStatus;
        }
        
        stErr = Math.sqrt(v1);
        
        theAltHypothesis =  matched_t_Controller.getAltHypothesis();
        hypothDiff = matched_t_Controller.getHypothesizedDiff();
        tStatistic = (xBarDiff - hypothDiff) / Math.sqrt(v1);
        hypothesesToPrint = new String[2];
        hypothesesToPrint = matched_t_Controller.getHypothPair();        
        alpha = matched_t_Controller.getAlpha();
        confidenceLevel = matched_t_Controller.getConfidenceLevel();
        alphaOverTwo = alpha/2.0;
        
        tDist = new T_double_df(df);
        pValue = tDist.cumulative(Math.abs(tStatistic));    //  getRightTailArea
        effectSize = get_CohensD(xBarDiff, hypothDiff, nLegal, stDevDiff);
        printStatistics();
        return returnStatus;
    }
    
    public void printStatistics() {
        matchedTReport = new ArrayList();       
        
        switch (theAltHypothesis) {
            case "NotEqual":  
                critical_t = tDist.quantile(1.0 - alphaOverTwo);                
                ciMean_Low = xBarDiff - critical_t * stErr;
                ciMean_High = xBarDiff + critical_t * stErr;
                pValue = 2.0 * tDist.cumulative(-Math.abs(tStatistic));
                printSummaryInformation();
                addNBlankLinesToDiffMeanReport(2);
                matchedTReport.add("                     ***  Hypothesis Test  ***");
                addNBlankLinesToDiffMeanReport(2);
                matchedTReport.add(hypothesesToPrint[0]);
                addNBlankLinesToDiffMeanReport(1);
                matchedTReport.add(hypothesesToPrint[1]);                
                addNBlankLinesToDiffMeanReport(2);
                String strCITitle = "                     ***  Hypothesis Test & " 
                    + String.valueOf(confidenceLevel) 
                    + "% Confidence interval ***";
                matchedTReport.add(strCITitle);
                
                addNBlankLinesToDiffMeanReport(2);

                matchedTReport.add("       Mean       StandErr      df     t_Value     pValue      ciLow       ciHigh");
                addNBlankLinesToDiffMeanReport(1);
                matchedTReport.add(String.format("     %8.3f     %8.3f    %4d   %8.3f    %8.3f    %8.3f    %8.3f",
                                                                                                  xBarDiff,
                                                                                                  stErr,
                                                                                                  df,
                                                                                                  tStatistic,
                                                                                                  pValue,
                                                                                                  ciMean_Low,
                                                                                                  ciMean_High));  
                addNBlankLinesToDiffMeanReport(2);
                matchedTReport.add(String.format("      Effect size (Cohen's D) = %5.3f",  effectSize)); 
                addNBlankLinesToDiffMeanReport(1);

                break;
        
            case "LessThan":                
                critical_t = tDist.quantile(1.0 - alpha);
                ciMean_Low = xBarDiff - critical_t * stErr;
                pValue = tDist.cumulative(tStatistic);                
                printSummaryInformation();                
                addNBlankLinesToDiffMeanReport(2);
                matchedTReport.add("                     ***  Hypothesis Test  ***");
                addNBlankLinesToDiffMeanReport(2);
                matchedTReport.add(hypothesesToPrint[0]);
                addNBlankLinesToDiffMeanReport(1);
                matchedTReport.add(hypothesesToPrint[1]);                
                addNBlankLinesToDiffMeanReport(2);
                matchedTReport.add(String.format("\n\n       Mean       StandErr       df     t_Value     pValue      ciLow    ciHigh"));
                addNBlankLinesToDiffMeanReport(1);
                matchedTReport.add(String.format("    %8.3f    %8.3f       %4d    %8.3f   %8.3f   %8.3f      %2s",
                                                                                                  xBarDiff,
                                                                                                  stErr,
                                                                                                  df,
                                                                                                  tStatistic,
                                                                                                  pValue,
                                                                                                  ciMean_Low,
                                                                                                  "+\u221E")); 
                addNBlankLinesToDiffMeanReport(2);
                matchedTReport.add(String.format("   Effect size (Cohen's D) = %5.3f",  effectSize)); 
                addNBlankLinesToDiffMeanReport(1);
                break;
            
            case "GreaterThan":
                critical_t = tDist.quantile(1.0 - alphaOverTwo);
                ciMean_Low = xBarDiff - critical_t * stErr;
                ciMean_High = xBarDiff + critical_t * stErr;
                pValue = tDist.cumulative(-Math.abs(tStatistic));
                printSummaryInformation();
                addNBlankLinesToDiffMeanReport(2);
                matchedTReport.add("                     ***  Hypothesis Test  ***");
                addNBlankLinesToDiffMeanReport(2);
                matchedTReport.add(hypothesesToPrint[0]);
                addNBlankLinesToDiffMeanReport(1);
                matchedTReport.add(hypothesesToPrint[1]);                
                addNBlankLinesToDiffMeanReport(2);
                matchedTReport.add(String.format("       Mean       StandErr       df     t_Value     pValue      ciLow     ciHigh"));
                addNBlankLinesToDiffMeanReport(1);
                matchedTReport.add(String.format("     %8.3f   %8.3f       %4d    %8.3f   %8.3f       %2s     %8.3f ",
                                                                                                  xBarDiff,
                                                                                                  stErr,
                                                                                                  df,
                                                                                                  tStatistic,
                                                                                                  pValue,
                                                                                                  "-\u221E",
                                                                                                  ciMean_High));
                addNBlankLinesToDiffMeanReport(2);
                matchedTReport.add(String.format("   Effect size (Cohen's D) = %5.3f",  effectSize)); 
                addNBlankLinesToDiffMeanReport(1);
                break;
            
            default:
                String switchFailure = "Switch failure: Matched t Model 178 " + theAltHypothesis;
                MyAlerts.showUnexpectedErrorAlert(switchFailure);
            break;
            }          
    }
    
    private double get_CohensD(double xBarDiff, double nullDiff, int n, double stDev) {
            double dbl_n, dbl_df, cohensD, cohensD_unbiased;
            dbl_n = n;
            dbl_df = dbl_n - 1.;
            cohensD = (xBarDiff - nullDiff) / stDev;
            
            if(theAltHypothesis.equals("NotEqual")) {
               cohensD = Math.abs(cohensD);
            }
            
            // Cumming, p294
            cohensD_unbiased = cohensD * (1.0 - 3.0 / (4.0 * df - 1.0));      
            return cohensD_unbiased;
    }
    
private void printSummaryInformation() {
    addNBlankLinesToDiffMeanReport(2);
    matchedTReport.add("                    *** Summary information ***   ");
    addNBlankLinesToDiffMeanReport(2);
    matchedTReport.add("                      NSize        Mean        StDev     StErr");
    addNBlankLinesToDiffMeanReport(1);
    matchedTReport.add(String.format("   %16s   %4d         %5.3f       %5.3f     %5.3f",  varLabel,
                                                                               nLegal,
                                                                               xBarDiff,
                                                                               stDevDiff,
                                                                               stErr));    
}
    
    private void addNBlankLinesToDiffMeanReport(int thisMany) {
        StringUtilities.addNLinesToArrayList(matchedTReport, thisMany);
    }
    
    public int getDF() { return df; }    
    public double getTStat() { return tStatistic; }
    public double getAlpha() {return alpha; }
    public String getHypotheses() { return theAltHypothesis; }       
    public Data_Manager getDataManager() { return dm; }
    public double getPValue() { return pValue; }    
   public ArrayList<String> getSingleTReport() { return matchedTReport; }
}

