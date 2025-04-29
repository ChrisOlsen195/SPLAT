/**************************************************
 *                 Indep_t_Model                  *
 *                    02/02/25                    *
 *                     06:00                      *
 *************************************************/
package the_t_procedures;

import dataObjects.QuantitativeDataVariable;
import dialogs.t_and_z.Indep_t_Dialog;
import java.util.ArrayList;
import splat.Data_Manager;
import utilityClasses.*;
import theRProbDists.*;

public class Indep_t_Model {
    
    int pooled_df, n1, n2, confidenceLevel;
    
    double t_Unpooled, t_Pooled, satterthwaite_df, diffXBar, hypothDiff,
            xBar_1, xBar_2, var_1, var_2, double_n1, double_n2, v1, v2,  
            stErr_Pooled, stErr_Unpooled, oneMinusAlpha,
            ciDiff_Low_Unpooled, ciDiff_High_Unpooled, tForTwoTails_Unpooled,
            ciDiff_Low_Pooled, ciDiff_High_Pooled,
            pValueDiff_Unpooled, pValueDiff_Pooled, stErr_Var1, stErr_Var2,
            critical_t_mean1, critical_t_mean2, critical_t_Unpooled,
            critical_t_Pooled, alpha, alphaOverTwo, oneMinusAlphaOverTwo,
            ciMean1_Low, ciMean1_High, ciMean2_Low, ciMean2_High,
            stDev_Var1, stDev_Var2, effectSize, cohensD, cohensD_Unbiased;
   
    String altHypothesis, longDescrOfDiff, shortDescrOfDiff, 
           var_1_String, var_2_String, long_descr_1, short_descr_1,
           long_descr_2, short_descr_2, returnStatus, strCITitle;
    
    //String waldoFile = "Indep_t_Model";
    String waldoFile = "";
    
    ArrayList<String> indepTReport;    
    
    // My classes
    ArrayList<QuantitativeDataVariable> allTheQDVs;
    Data_Manager dm;
    Indep_t_Controller indep_t_Controller;
    Indep_t_Dialog indep_t_Dialog;

    T_double_df tDist_Mean1, tDist_Mean2, tDist_Sat, tDist_Pooled;
    
// ***************  Called by Independent t procedure  **********************    
    public Indep_t_Model (Indep_t_Controller indep_t_Controller,
                                String firstVarDescr, String secondVarDescr,
                                ArrayList<QuantitativeDataVariable> allTheQDVs) {
        this.allTheQDVs = new ArrayList();
        this.indep_t_Controller = indep_t_Controller;
        indep_t_Dialog = indep_t_Controller.getIndepTDialog();
        dm = indep_t_Controller.getDataManager();
        dm.whereIsWaldo(55, waldoFile, " *** Constructing");
        
        this.allTheQDVs = new ArrayList();       
        for (int ithQDV = 0; ithQDV < allTheQDVs.size(); ithQDV++) {
            this.allTheQDVs.add(allTheQDVs.get(ithQDV));
        }
    }
    
    // ***************  Called by Independent t procedure  *******************
    public String doIndepTAnalysis() {
        dm.whereIsWaldo(65, waldoFile, " --- doIndepTAnalysis()");
        returnStatus = "OK";
        altHypothesis = indep_t_Dialog.getAltHypothesis();
        hypothDiff = indep_t_Dialog.getHypothesizedDiffInMeans();
        alpha = indep_t_Dialog.getInd_t_Alpha();
        confidenceLevel = (int)(100. *(1.0 - alpha));
        oneMinusAlpha = 1.0 - alpha;
        alphaOverTwo = alpha / 2.0;
        oneMinusAlphaOverTwo = 1.0 - alphaOverTwo;
        var_1_String = indep_t_Controller.getFirstVarDescr();
        var_2_String = indep_t_Controller.getSecondVarDescr();
        
        n1 = allTheQDVs.get(0).getLegalN();
        n2 = allTheQDVs.get(1).getLegalN();
        
        if ((n1 < 2) || (n2 < 2)) {
            MyAlerts.showSampleSizeTooSmallAlert();
            returnStatus = "Cancel";
            return returnStatus;
        } 
        
        if ((n1  > 200) || (n2 > 200)) {
            MyAlerts.showEffectSizeSuggestion();
        }

        xBar_1 = allTheQDVs.get(0).getTheMean();
        xBar_2 = allTheQDVs.get(1).getTheMean();
        var_1 = allTheQDVs.get(0).getTheVariance();
        var_2 = allTheQDVs.get(1).getTheVariance();  

        long_descr_1 = StringUtilities.getleftMostNChars(var_1_String, 16);
        long_descr_2 = StringUtilities.getleftMostNChars(var_2_String, 16);
        longDescrOfDiff = long_descr_1.trim() + " - " + long_descr_2.trim();
        
        short_descr_1 = StringUtilities.getleftMostNChars(var_1_String, 10);
        short_descr_2 = StringUtilities.getleftMostNChars(var_2_String, 10);
        shortDescrOfDiff = short_descr_1.trim() + " - " + short_descr_2.trim();
        shortDescrOfDiff = StringUtilities.getleftMostNChars(shortDescrOfDiff, 23);
        
        diffXBar = xBar_1 - xBar_2;
        double_n1 = n1;
        double_n2 = n2;
        v1 = var_1 / double_n1;
        v2 = var_2 / double_n2;
  
        stDev_Var1 = Math.sqrt(var_1);
        stDev_Var2 = Math.sqrt(var_2);
        stErr_Var1 = Math.sqrt(v1);
        stErr_Var2 = Math.sqrt(v2);
        
        effectSize = getCohensD(xBar_1, stDev_Var1, n1,
                                xBar_2, stDev_Var2, n2);
        
        double satterthwaite_numerator = (v1 + v2) * (v1 + v2);
        double temp1 = v1 * v1 / (double_n1 - 1.);
        double temp2 = v2 * v2 / (double_n2 - 1.);
        double satterthwaite_denominator = temp1 + temp2;
        
        satterthwaite_df = satterthwaite_numerator / satterthwaite_denominator;
        tDist_Sat = new T_double_df(satterthwaite_df);        
        pooled_df = n1 + n2 - 2;
        tDist_Mean1 = new T_double_df(n1 - 1);
        critical_t_mean1 = tDist_Mean1.quantile(1.0 - alphaOverTwo);
        tDist_Mean2 = new T_double_df(n2 - 1.0);
        critical_t_mean2 = tDist_Mean2.quantile(1.0 - alphaOverTwo); 
        tDist_Pooled = new T_double_df(pooled_df);

        double s2_pooled_numerator = (double_n1 - 1.0) * var_1 + (double_n2 - 1.0) * var_2;
        double s2_pooled_denominator = (double)pooled_df;
        double s2_pooled_ratio = s2_pooled_numerator / s2_pooled_denominator;
        double s2_pooled_ns = 1.0 / double_n1 + 1.0 / double_n2;
        stErr_Pooled = Math.sqrt(s2_pooled_ratio * s2_pooled_ns);
        stErr_Unpooled = Math.sqrt(var_1 / n1 + var_2 / n2);
        tForTwoTails_Unpooled = (diffXBar - hypothDiff) / stErr_Unpooled;    
        hypothDiff = indep_t_Dialog.getHypothesizedDiffInMeans();

        t_Unpooled = (xBar_1 - xBar_2 - hypothDiff) / stErr_Unpooled;
        t_Pooled = (xBar_1 - xBar_2 - hypothDiff) / stErr_Pooled;

        printStatistics();
        return returnStatus;
    }
    
    public void printStatistics() {        
        indepTReport = new ArrayList();
        dm.whereIsWaldo(150, waldoFile, " --- printStatistics(), altHypoth = " + altHypothesis);
        switch (altHypothesis) {
            case "NotEqual":  
                dm.whereIsWaldo(153, waldoFile, " --- printStatistics() -- Case not equal");
                critical_t_Unpooled = tDist_Sat.quantile(oneMinusAlphaOverTwo);
                critical_t_Pooled = tDist_Pooled.quantile(1.0 - alphaOverTwo);
                pValueDiff_Unpooled = 2.0 * (tDist_Sat.cumulative(-Math.abs(tForTwoTails_Unpooled)));              
                pValueDiff_Pooled = 2.0 * tDist_Pooled.cumulative(-Math.abs(t_Pooled));      
                ciMean1_Low = xBar_1 - critical_t_mean1 * stErr_Var1;
                ciMean1_High = xBar_1 + critical_t_mean1 * stErr_Var1;
                ciMean2_Low = xBar_2 - critical_t_mean2 * stErr_Var2;
                ciMean2_High = xBar_2 + critical_t_mean2 * stErr_Var2;
                ciDiff_Low_Unpooled = diffXBar - critical_t_Unpooled * stErr_Unpooled;
                ciDiff_High_Unpooled = diffXBar + critical_t_Unpooled * stErr_Unpooled;
                ciDiff_Low_Pooled = diffXBar - critical_t_Pooled * stErr_Pooled;
                ciDiff_High_Pooled = diffXBar + critical_t_Pooled * stErr_Pooled;           
                printDescriptiveStatistics();
                printNotEqualTo();
                break;
        
            case "LessThan":
                dm.whereIsWaldo(171, waldoFile, " --- printStatistics() -- Case less than");
                critical_t_Unpooled = tDist_Sat.quantile(oneMinusAlpha);
                critical_t_Pooled = tDist_Pooled.quantile(oneMinusAlpha);
                pValueDiff_Unpooled = tDist_Sat.cumulative(-Math.abs(tForTwoTails_Unpooled));
                pValueDiff_Pooled = tDist_Sat.cumulative(-Math.abs(t_Pooled));
                ciMean1_Low = xBar_1 - critical_t_mean1 * stErr_Var1;
                ciMean1_High = xBar_1 + critical_t_mean1 * stErr_Var1;
                ciMean2_Low = xBar_1 - critical_t_mean2 * stErr_Var2;
                ciMean2_High = xBar_1 + critical_t_mean2 * stErr_Var2;
                ciDiff_Low_Unpooled = diffXBar - critical_t_Unpooled * stErr_Unpooled;
                ciDiff_High_Unpooled = diffXBar + critical_t_Unpooled * stErr_Unpooled;
                ciDiff_Low_Pooled = diffXBar - critical_t_Pooled * stErr_Pooled;
                ciDiff_High_Pooled = diffXBar + critical_t_Pooled * stErr_Pooled;
                printDescriptiveStatistics();
                printLessThan(); 
                break;
            
            case "GreaterThan":
                dm.whereIsWaldo(189, waldoFile, " --- printStatistics() -- Case greater than");
                critical_t_Unpooled = tDist_Sat.quantile(oneMinusAlpha);
                critical_t_Pooled = tDist_Pooled.quantile(oneMinusAlpha);
                pValueDiff_Unpooled = 1.0 - tDist_Sat.cumulative(Math.abs(tForTwoTails_Unpooled));
                pValueDiff_Pooled = 1.0 - tDist_Pooled.cumulative(Math.abs(t_Pooled));
                
                ciMean1_Low = xBar_1 - critical_t_mean1 * stErr_Var1;
                ciMean1_High = xBar_1 + critical_t_mean1 * stErr_Var1;
                ciMean2_Low = xBar_1 - critical_t_mean2 * stErr_Var2;
                ciMean2_High = xBar_1 + critical_t_mean2 * stErr_Var2;
                ciDiff_Low_Unpooled = diffXBar - critical_t_Unpooled * stErr_Unpooled;
                ciDiff_High_Unpooled = diffXBar + critical_t_Unpooled * stErr_Unpooled;
                ciDiff_Low_Pooled = diffXBar - critical_t_Pooled * stErr_Pooled;
                ciDiff_High_Pooled = diffXBar + critical_t_Pooled * stErr_Pooled;

                printDescriptiveStatistics();
                printGreaterThan();
                break;
            
            default: 
                String switchFailure = "Switch failure: Ind t Model 209 " + altHypothesis;
                MyAlerts.showUnexpectedErrorAlert(switchFailure);
            }           
    }
    
    private void printDescriptiveStatistics() {
        dm.whereIsWaldo(215, waldoFile, " --- printDescriptiveStatistics()");
        addNBlankLinesToIndepTReport(1);
        indepTReport.add("               *****   Descriptive statistics and confidence intervals   *****");
        addNBlankLinesToIndepTReport(1);
        strCITitle = "                          ***  " + String.valueOf(confidenceLevel) + "% Confidence intervals for \u03BC\u2081 , \u03BC\u2082 ***";
        indepTReport.add(strCITitle);
        addNBlankLinesToIndepTReport(2);
        indepTReport.add(String.format("                    NSize          Mean          StDev        StErr     ciLow     ciHigh"));
        addNBlankLinesToIndepTReport(1);

        indepTReport.add(String.format("   %10s      %6d         %7.3f       %7.3f      %7.3f   %7.3f    %7.3f",     short_descr_1, 
                                                                                                                n1,
                                                                                                                xBar_1,
                                                                                                                stDev_Var1,
                                                                                                                stErr_Var1,
                                                                                                                ciMean1_Low,
                                                                                                                ciMean1_High));
        addNBlankLinesToIndepTReport(1);

        indepTReport.add(String.format("   %10s      %6d         %7.3f       %7.3f      %7.3f   %7.3f    %7.3f",     short_descr_2,
                                                                                                                n2,
                                                                                                                xBar_2,
                                                                                                                stDev_Var2,
                                                                                                                stErr_Var2,
                                                                                                                ciMean2_Low,
                                                                                                                ciMean2_High));   
        addNBlankLinesToIndepTReport(1);
    }
    
    private void printNotEqualTo() {
        dm.whereIsWaldo(245, waldoFile, " --- printGreaterThan()");
        addNBlankLinesToIndepTReport(1);
        indepTReport.add("          *****   Hypothesis test:    *****");
        addNBlankLinesToIndepTReport(2);
        indepTReport.add(String.format("  %30s %5.3f", "       Null hypothesis: \u03BC\u2081 - \u03BC\u2082  = ", hypothDiff));
        addNBlankLinesToIndepTReport(1);
        indepTReport.add(String.format("  %30s %5.3f", "Alternative hypothesis: \u03BC\u2081 - \u03BC\u2082  \u2260 ", hypothDiff));
        addNBlankLinesToIndepTReport(2);
        indepTReport.add("          Method       DiffMeans      St Err       df         t-stat      p-Value");
        addNBlankLinesToIndepTReport(2);
        indepTReport.add(String.format("      %13s    %8.3f     %8.3f   %8.3f     %8.3f    %8.3f",
                                                                                          "Satterthwaite",
                                                                                          diffXBar,
                                                                                          stErr_Unpooled,
                                                                                          satterthwaite_df,
                                                                                          t_Unpooled,
                                                                                          pValueDiff_Unpooled));
        addNBlankLinesToIndepTReport(1);
        indepTReport.add(String.format("   %13s       %8.3f     %8.3f     %5d      %8.3f    %8.3f",
                                                                                          "Pooled",
                                                                                          diffXBar,
                                                                                          stErr_Pooled,
                                                                                          pooled_df,
                                                                                          t_Pooled,
                                                                                          pValueDiff_Pooled));
        addNBlankLinesToIndepTReport(1);
        // }
        addNBlankLinesToIndepTReport(2);
        indepTReport.add(String.format("     *****   Estimation for the difference, %20s   *****", longDescrOfDiff));
        addNBlankLinesToIndepTReport(1);
        strCITitle = "                        ***  " + String.valueOf(confidenceLevel) + "% Confidence intervals for \u03BC\u2081 - \u03BC\u2082 ***";
        indepTReport.add(strCITitle);
        addNBlankLinesToIndepTReport(2);
        indepTReport.add("          Method       DiffMeans     StandErr        df        ciLow      ciHigh");
        addNBlankLinesToIndepTReport(2);
        indepTReport.add(String.format("      %13s   %8.3f     %8.3f      %8.3f    %8.3f   %8.3f",
                                                                                          "Satterthwaite",
                                                                                          diffXBar,
                                                                                          stErr_Unpooled,
                                                                                          satterthwaite_df,
                                                                                          ciDiff_Low_Unpooled,
                                                                                          ciDiff_High_Unpooled));
        addNBlankLinesToIndepTReport(1);

        indepTReport.add(String.format("   %13s      %8.3f     %8.3f      %6d      %8.3f   %8.3f",
                                                                                          "Pooled",
                                                                                          diffXBar,
                                                                                          stErr_Pooled,
                                                                                          pooled_df,
                                                                                          ciDiff_Low_Pooled,
                                                                                          ciDiff_High_Pooled));

        addNBlankLinesToIndepTReport(2);
        indepTReport.add(String.format("        Effect size (Cohen's D) = %5.3f",  effectSize)); 
        addNBlankLinesToIndepTReport(1);
    }
    
    private void printLessThan() {
        dm.whereIsWaldo(302, waldoFile, " --- printLessThan()");
        addNBlankLinesToIndepTReport(2);
        indepTReport.add("          *****   Hypothesis test:    *****");
        addNBlankLinesToIndepTReport(2);                
        indepTReport.add(String.format("  %30s %5.3f", "       Null hypothesis: \u03BC\u2081 - \u03BC\u2082  = ", hypothDiff));
        addNBlankLinesToIndepTReport(1);
        indepTReport.add(String.format("  %30s %5.3f", "Alternative hypothesis: \u03BC\u2081 - \u03BC\u2082  < ", hypothDiff));
        addNBlankLinesToIndepTReport(2);
        indepTReport.add("          Method       DiffMeans      St Err       df       t-stat      p-Value");
        addNBlankLinesToIndepTReport(2);
        indepTReport.add(String.format("      %13s    %8.3f     %8.3f   %8.3f   %8.3f    %8.3f",
                                                                            "Satterthwaite",
                                                                            diffXBar,
                                                                            stErr_Unpooled,
                                                                            satterthwaite_df,
                                                                            t_Unpooled,
                                                                            pValueDiff_Unpooled));
        addNBlankLinesToIndepTReport(1);
        indepTReport.add(String.format("   %13s       %8.3f     %8.3f    %5d     %8.3f    %8.3f",
                                                                            "Pooled",
                                                                            diffXBar,
                                                                            stErr_Pooled,
                                                                            pooled_df,
                                                                            t_Pooled,
                                                                            pValueDiff_Pooled));
        addNBlankLinesToIndepTReport(1);

        addNBlankLinesToIndepTReport(2);
        indepTReport.add(String.format("      *****   Estimation for the difference, %10s", longDescrOfDiff));
        addNBlankLinesToIndepTReport(1);
        strCITitle = "                        ***  " + String.valueOf(confidenceLevel) + "% Confidence intervals for \u03BC\u2081 - \u03BC\u2082 ***";
        indepTReport.add(strCITitle);
        addNBlankLinesToIndepTReport(2);                
        indepTReport.add(String.format("          Method       DiffMeans     StandErr      df        ciLow       ciHigh"));
        addNBlankLinesToIndepTReport(1);
        indepTReport.add(String.format("      %13s   %8.3f     %8.3f    %8.3f    %8.3f        %2s ",
                                                                            "Satterthwaite",
                                                                            diffXBar,
                                                                            stErr_Unpooled,
                                                                            satterthwaite_df,
                                                                            ciDiff_Low_Unpooled,
                                                                            "+\u221E"));
        addNBlankLinesToIndepTReport(1);


        indepTReport.add(String.format("   %13s      %8.3f     %8.3f     %5d      %8.3f        %2s",
                                                                            "Pooled",
                                                                            diffXBar,
                                                                            stErr_Pooled,
                                                                            pooled_df,
                                                                            ciDiff_Low_Pooled,
                                                                            "+\u221E"));

         addNBlankLinesToIndepTReport(2);
         indepTReport.add(String.format("        Effect size (Cohen's D) = %5.3f",  effectSize)); 
         addNBlankLinesToIndepTReport(1);
    }
    
    private void printGreaterThan() {
        dm.whereIsWaldo(361, waldoFile, " --- printGreaterThan()");
        addNBlankLinesToIndepTReport(2);
        indepTReport.add("          *****   Hypothesis test:    *****");
        addNBlankLinesToIndepTReport(2);                
        indepTReport.add(String.format("  %30s %5.3f", "       Null hypothesis: \u03BC\u2081 - \u03BC\u2082  = ", hypothDiff));
        addNBlankLinesToIndepTReport(1);
        indepTReport.add(String.format("  %30s %5.3f", "Alternative hypothesis: \u03BC\u2081 - \u03BC\u2082  > ", hypothDiff));
        addNBlankLinesToIndepTReport(2);
        indepTReport.add("          Method       DiffMeans      St Err       df      t-stat      p-Value");
        indepTReport.add(String.format("\n      %13s    %8.3f     %8.3f   %8.3f  %8.3f    %8.3f",
                                                                            "Satterthwaite",
                                                                            diffXBar,
                                                                            stErr_Unpooled,
                                                                            satterthwaite_df,
                                                                            t_Unpooled,
                                                                            pValueDiff_Unpooled));
        addNBlankLinesToIndepTReport(1);
        indepTReport.add(String.format("   %13s       %8.3f     %8.3f    %6d   %8.3f    %8.3f",
                                                                            "Pooled",
                                                                            diffXBar,
                                                                            stErr_Pooled,
                                                                            pooled_df,
                                                                            t_Pooled,
                                                                            pValueDiff_Pooled));
        addNBlankLinesToIndepTReport(2);
               
        indepTReport.add(String.format("      *****   Estimation for the difference, %20s", longDescrOfDiff));
        addNBlankLinesToIndepTReport(1);
        strCITitle = "                        ***  " + String.valueOf(confidenceLevel) + "% Confidence intervals for \u03BC\u2081 - \u03BC\u2082 ***";
        indepTReport.add(strCITitle);
        addNBlankLinesToIndepTReport(2);                
        indepTReport.add("          Method       DiffMeans     StandErr        df       ciLow      ciHigh");
        addNBlankLinesToIndepTReport(1); 
        indepTReport.add(String.format("      %13s    %8.3f     %8.3f     %8.3f      %2s      %8.3f",
                                                                            "Satterthwaite",
                                                                            diffXBar,
                                                                            stErr_Unpooled,
                                                                            satterthwaite_df,
                                                                            "-\u221E",
                                                                            ciDiff_High_Unpooled));
        addNBlankLinesToIndepTReport(1); 


        indepTReport.add(String.format("   %13s       %8.3f     %8.3f      %6d       %2s      %8.3f",
                                                                            "Pooled",
                                                                            diffXBar,
                                                                            stErr_Pooled,
                                                                            pooled_df,
                                                                            "-\u221E",
                                                                            ciDiff_High_Pooled));  
        
        addNBlankLinesToIndepTReport(2);
        indepTReport.add(String.format("        Effect size (Cohen's D) = %5.3f",  effectSize)); 
        addNBlankLinesToIndepTReport(1);
    }
    
    private double getCohensD(double xBar1, double standDev1, int n1,
                              double xBar2, double standDev2, int n2) {
        dm.whereIsWaldo(420, waldoFile, " --- getCohensD()");
        double dbl_n1 = n1;
        double dbl_n2 = n2;
        double df;
        df = dbl_n1 + dbl_n2 - 2.0;
        double tempNum = (dbl_n1 - 1) * standDev1 * standDev1 + (dbl_n2 - 1) * standDev2 * standDev2;
        double tempDen = (dbl_n1 + dbl_n2 - 2.0);
        double sPooled = Math.sqrt(tempNum / tempDen);
        cohensD = ((xBar1 - xBar2) - hypothDiff) / sPooled;

        if(altHypothesis.equals("NotEqual")) {
           cohensD = Math.abs(cohensD);
        }

        cohensD_Unbiased = cohensD * (1.0 - 3.0 / (4.0 * df - 1.0));          
        return cohensD_Unbiased;
    }

    private void addNBlankLinesToIndepTReport(int thisMany) {
        StringUtilities.addNLinesToArrayList(indepTReport, thisMany);
    }
    
    public double getDF() { return satterthwaite_df; }
    public double getTStat() { return t_Unpooled; }
    public double getAlpha() {return alpha; }
    public int getConfidenceLevel() { return confidenceLevel; }
    public String getHypotheses() { return altHypothesis; }    
    public double getPValue() { return pValueDiff_Unpooled; }    
    public ArrayList<String> getIndepTReport() { return indepTReport; }
    public Data_Manager getDataManager() { return dm; }
    
    public String toString() {
        return "indep_t_Model -- toString";
    }
}
