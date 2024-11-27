/**************************************************
 *              Indep_t_SumStats_Model            *
 *                    11/27/24                    *
 *                      12:00                     *
 *************************************************/
package the_t_procedures;

import dialogs.t_and_z.Indep_t_SumStats_Dialog;
import java.util.ArrayList;
import theRProbDists.*;
import utilityClasses.MyAlerts;
import utilityClasses.StringUtilities;

public class Indep_t_SumStats_Model {
    
    int pooled_df, n1, n2, confidenceLevel;
    
    double t_Unpooled, t_Pooled, satterthwaite_df, diffXBar, hypothDiff,
            xBar_1, xBar_2, var_1, var_2, double_n1, double_n2, v1, v2,  
            stErr_Pooled, stErr_Unpooled, oneMinusAlpha,
            ciDiff_Low_Unpooled, ciDiff_High_Unpooled, tForTwoTails_Unpooled,
            ciDiff_Low_Pooled, ciDiff_High_Pooled, 
            pValueDiff_Unpooled, pValueDiff_Pooled, stErr_Var1, stErr_Var2,
            critical_t_mean1, critical_t_mean2, critical_t_Unpooled,
            critical_t_Pooled, alpha, alphaOverTwo, 
            ciMean1_Low, ciMean1_High, 
            ciMean2_Low, ciMean2_High,
            stDev_Var1, stDev_Var2, 
            cohensD, cohensD_Unbiased, effectSize;
   
    String altHypothesis, longDescrOfDiff, shortDescrOfDiff, 
           var_1_String, var_2_String, long_descr_1, short_descr_1,
           long_descr_2, short_descr_2, returnStatus;
    
    String firstLabel, secondLabel, strCITitle;
    
    ArrayList<String> sumStatsIndTReport;

    Indep_t_Controller indep_t_Controller;
    Indep_t_SumStats_Dialog twoMeansDialog;
    
    T_double_df tDist_Pooled, tDist_Mean1, tDist_Mean2, theTParty;
    
    public Indep_t_SumStats_Model (Indep_t_Controller indep_t_Controller, 
                                       Indep_t_SumStats_Dialog twoMeansDialog) {
        this.indep_t_Controller = indep_t_Controller;
        //System.out.println("47 Indep_t_SumStats_Model, Constructing);       
        this.twoMeansDialog = twoMeansDialog;
        firstLabel = twoMeansDialog.getMean_1_Description();
        secondLabel = twoMeansDialog.getMean_2_Description(); 
    }
    
     public String doIndepTAnalysis() {
        //System.out.println("54 Indep_t_SumStats_Model, doIndepTAnalysis());  
        altHypothesis =  indep_t_Controller.getInd_T_SumStats_Hypotheses();
        hypothDiff = indep_t_Controller.getInd_T_SumStats_HypothesizedDiff();
        alpha = indep_t_Controller.getInd_T_SumStats_Alpha();
        confidenceLevel = (int)(100. * (1.0 - alpha));
        alphaOverTwo = alpha / 2.0;
        oneMinusAlpha = 1.0 - alpha;

        var_1_String = "Variable #1 Label";
        n1 = twoMeansDialog.getN1();
        n2 = twoMeansDialog.getN2();
        
        if ((n1  < 2) || (n2  < 2)) {
            MyAlerts.showTooFewIndtDFAlert();
            returnStatus = "Cancel";
            return returnStatus;
        }        
        
        xBar_1 = twoMeansDialog.getXBar1();
        xBar_2 = twoMeansDialog.getXBar2();
        var_1 = twoMeansDialog.getVariance1();
        var_2_String = "Variable #2 Label";
        var_2 = twoMeansDialog.getVariance2();  
        long_descr_1 = StringUtilities.getleftMostNChars(firstLabel, 16);
        long_descr_2 = StringUtilities.getleftMostNChars(secondLabel, 16);        
               
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
        theTParty = new T_double_df(satterthwaite_df);
        pooled_df = n1 + n2 - 2;
        tDist_Mean1 = new T_double_df(n1 - 1);
        critical_t_mean1 = tDist_Mean1.quantile(1.0 - alphaOverTwo);
        tDist_Mean2 = new T_double_df(n2 - 1);
        critical_t_mean2 = tDist_Mean2.quantile(1.0 - alphaOverTwo);   
        tDist_Pooled = new T_double_df(pooled_df);

        double s2_pooled_numerator = (double_n1 - 1.0) * var_1 + (double_n2 - 1.0) * var_2;
        double s2_pooled_denominator = (double)pooled_df;
        double s2_pooled_ratio = s2_pooled_numerator / s2_pooled_denominator;
        double s2_pooled_ns = 1.0 / double_n1 + 1.0 / double_n2;
        stErr_Pooled = Math.sqrt(s2_pooled_ratio * s2_pooled_ns);
        stErr_Unpooled = Math.sqrt(v1 + v2);
        tForTwoTails_Unpooled = (diffXBar - hypothDiff) / stErr_Unpooled;    
        t_Unpooled = (xBar_1 - xBar_2 - hypothDiff) / stErr_Unpooled;
        t_Pooled = (xBar_1 - xBar_2 - hypothDiff) / stErr_Pooled;
        sumStatsIndTReport = new ArrayList();  

        printStatistics();
        return returnStatus;
    }
    
    public void printStatistics() {
        //System.out.println("131 Indep_t_SumStats_Model, printStatistics()");       
        sumStatsIndTReport = new ArrayList();
        //System.out.println("133 Indep_t_SumStats_Model, altHypothesis = " + altHypothesis);
        switch (altHypothesis) {
            case "NotEqual":  
                //System.out.println("141 Indep_t_SumStats_Model, case NotEqual");
                critical_t_Unpooled = theTParty.quantile(1.0 - alphaOverTwo);
                critical_t_Pooled = tDist_Pooled.quantile(1.0 - alphaOverTwo);
                pValueDiff_Unpooled = 2.0 * (theTParty.cumulative(-Math.abs(tForTwoTails_Unpooled)));
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
                //System.out.println("155 Indep_t_SumStats_Model, case LessThan");
                critical_t_Unpooled = theTParty.quantile(oneMinusAlpha);
                critical_t_Pooled = tDist_Pooled.quantile(oneMinusAlpha);
                pValueDiff_Unpooled = theTParty.cumulative(-Math.abs(tForTwoTails_Unpooled));
                pValueDiff_Pooled = tDist_Pooled.cumulative(-Math.abs(t_Pooled));
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
                //System.out.println("174 Indep_t_SumStats_Model, case GreaterThan");
                critical_t_Unpooled = theTParty.quantile(oneMinusAlpha);
                critical_t_Pooled = tDist_Pooled.quantile(oneMinusAlpha);
                pValueDiff_Unpooled = theTParty.cumulative(Math.abs(tForTwoTails_Unpooled));
                pValueDiff_Pooled = tDist_Pooled.cumulative(Math.abs(t_Pooled)); 
                
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
                String switchFailure = "Switch failure: Indep_t_SumStats_Model 194 " + altHypothesis;
                MyAlerts.showUnexpectedErrorAlert(switchFailure); 
        }           
    }
    
    private void printDescriptiveStatistics() {
        addNBlankLinesToSumStatsReport(1);
        sumStatsIndTReport.add("                 *****   Descriptive statistics and confidence intervals   *****");
        addNBlankLinesToSumStatsReport(2);
        sumStatsIndTReport.add("                    NSize      Mean       StDev        StErr      CI_Low    CI_High");
        addNBlankLinesToSumStatsReport(1);
        strCITitle = "                             ***  " + String.valueOf(confidenceLevel) + "% Confidence intervals for \u03BC\u2081 , \u03BC\u2082 ***";
        sumStatsIndTReport.add(strCITitle);
        addNBlankLinesToSumStatsReport(2);
        sumStatsIndTReport.add(String.format("   %10s      %4d        %6.3f      %6.3f      %6.3f     %6.3f    %6.3f",     short_descr_1, 
                                                                                                                n1,
                                                                                                                xBar_1,
                                                                                                                stDev_Var1,
                                                                                                                stErr_Var1,
                                                                                                                ciMean1_Low,
                                                                                                                ciMean1_High));
        addNBlankLinesToSumStatsReport(1);
        sumStatsIndTReport.add(String.format("   %10s      %4d        %6.3f      %6.3f      %6.3f     %6.3f    %6.3f",     short_descr_2,
                                                                                                                n2,
                                                                                                                xBar_2,
                                                                                                                stDev_Var2,
                                                                                                                stErr_Var2,
                                                                                                                ciMean2_Low,
                                                                                                                ciMean2_High));   
        addNBlankLinesToSumStatsReport(1);
    }
    
    private void printNotEqualTo() {
        addNBlankLinesToSumStatsReport(1);
        sumStatsIndTReport.add("          *****   Hypothesis test:    *****");
        addNBlankLinesToSumStatsReport(2);
        sumStatsIndTReport.add(String.format("  %30s %5.3f", "       Null hypothesis: \u03BC\u2081 - \u03BC\u2082  = ", hypothDiff));
        addNBlankLinesToSumStatsReport(1);
        sumStatsIndTReport.add(String.format("  %30s %5.3f", "Alternative hypothesis: \u03BC\u2081 - \u03BC\u2082  \u2260 ", hypothDiff));
        addNBlankLinesToSumStatsReport(2);
        strCITitle = "                        ***  " + String.valueOf(confidenceLevel) + "% Confidence intervals for \u03BC\u2081 - \u03BC\u2082 ***";
        sumStatsIndTReport.add(strCITitle);
        addNBlankLinesToSumStatsReport(2);
        sumStatsIndTReport.add("          Method       DiffMeans      St Err       df         t-stat      p-Value");
        addNBlankLinesToSumStatsReport(1);

        addNBlankLinesToSumStatsReport(2);
        sumStatsIndTReport.add(String.format("      %13s    %8.3f     %8.3f   %8.3f     %8.3f    %8.3f",
                                                                                        "Satterthwaite",
                                                                                        diffXBar,
                                                                                        stErr_Unpooled,
                                                                                        satterthwaite_df,
                                                                                        t_Unpooled,
                                                                                        pValueDiff_Unpooled));
        addNBlankLinesToSumStatsReport(1);
        sumStatsIndTReport.add(String.format("   %13s       %8.3f     %8.3f     %4d       %8.3f    %8.3f",
                                                                                        "Pooled",
                                                                                        diffXBar,
                                                                                        stErr_Pooled,
                                                                                        pooled_df,
                                                                                        t_Pooled,
                                                                                        pValueDiff_Pooled));
      
        addNBlankLinesToSumStatsReport(2);
        sumStatsIndTReport.add(String.format("        Effect size (Cohen's D) = %5.3f",  effectSize)); 
        addNBlankLinesToSumStatsReport(1);
        addNBlankLinesToSumStatsReport(2);
        sumStatsIndTReport.add(String.format("     *****   Estimation for the difference, %20s   *****", longDescrOfDiff));
        addNBlankLinesToSumStatsReport(2);
        strCITitle = "                        ***  " + String.valueOf(confidenceLevel) + "% Confidence intervals for \u03BC\u2081 - \u03BC\u2082 ***";
        sumStatsIndTReport.add(strCITitle);
        addNBlankLinesToSumStatsReport(2);
        sumStatsIndTReport.add("          Method       DiffMeans     StErr         df         ciLow      ciHigh");
        addNBlankLinesToSumStatsReport(1);
        strCITitle = "                        ***  " + String.valueOf(confidenceLevel) + "% Confidence intervals for \u03BC\u2081 - \u03BC\u2082 ***";
        sumStatsIndTReport.add(strCITitle);
        addNBlankLinesToSumStatsReport(2);
        sumStatsIndTReport.add(String.format("      %13s   %8.3f     %8.3f    %8.3f     %8.3f   %8.3f",
                                                                                        "Satterthwaite",
                                                                                        diffXBar,
                                                                                        stErr_Unpooled,
                                                                                        satterthwaite_df,
                                                                                        ciDiff_Low_Unpooled,
                                                                                        ciDiff_High_Unpooled));
        addNBlankLinesToSumStatsReport(1);

        sumStatsIndTReport.add(String.format("   %13s      %8.3f     %8.3f      %4d       %8.3f   %8.3f",
                                                                                        "Pooled",
                                                                                        diffXBar,
                                                                                        stErr_Pooled,
                                                                                        pooled_df,
                                                                                        ciDiff_Low_Pooled,
                                                                                        ciDiff_High_Pooled));
        addNBlankLinesToSumStatsReport(1);
    }
    
    private void printLessThan() {
        addNBlankLinesToSumStatsReport(2);
        sumStatsIndTReport.add("          *****   Hypothesis test:    *****");
        addNBlankLinesToSumStatsReport(2);                
        sumStatsIndTReport.add(String.format("  %30s %5.3f", "       Null hypothesis: \u03BC\u2081 - \u03BC\u2082  = ", hypothDiff));
        addNBlankLinesToSumStatsReport(1);
        sumStatsIndTReport.add(String.format("  %30s %5.3f", "Alternative hypothesis: \u03BC\u2081 - \u03BC\u2082  < ", hypothDiff));
        addNBlankLinesToSumStatsReport(2);
        strCITitle = "                        ***  " + String.valueOf(confidenceLevel) + "% Confidence intervals for \u03BC\u2081 - \u03BC\u2082 ***";
        sumStatsIndTReport.add(strCITitle);
        addNBlankLinesToSumStatsReport(2);
        sumStatsIndTReport.add("          Method       DiffMeans      St Err       df       t-stat      p-Value");
        addNBlankLinesToSumStatsReport(1);
        addNBlankLinesToSumStatsReport(2);
        sumStatsIndTReport.add(String.format("      %13s    %8.3f     %8.3f   %8.3f   %8.3f    %8.3f",
                                                                           "Satterthwaite",
                                                                           diffXBar,
                                                                           stErr_Unpooled,
                                                                           satterthwaite_df,
                                                                           t_Unpooled,
                                                                           pValueDiff_Unpooled));
        addNBlankLinesToSumStatsReport(1);
        sumStatsIndTReport.add(String.format("   %13s       %8.3f     %8.3f     %4d     %8.3f    %8.3f",
                                                                           "Pooled",
                                                                           diffXBar,
                                                                           stErr_Pooled,
                                                                           pooled_df,
                                                                           t_Pooled,
                                                                           pValueDiff_Pooled));
        addNBlankLinesToSumStatsReport(1);
        addNBlankLinesToSumStatsReport(2);
        sumStatsIndTReport.add(String.format(" *****   Estimation for the difference, %20s", longDescrOfDiff));
        addNBlankLinesToSumStatsReport(2);   
        strCITitle = "                        ***  " + String.valueOf(confidenceLevel) + "% Confidence intervals for \u03BC\u2081 - \u03BC\u2082 ***";
        sumStatsIndTReport.add(strCITitle);
        addNBlankLinesToSumStatsReport(2);
        sumStatsIndTReport.add(String.format("          Method       DiffMeans     StandErr      df       ciLow      ciHigh"));
        addNBlankLinesToSumStatsReport(1);
        addNBlankLinesToSumStatsReport(2);
        sumStatsIndTReport.add(String.format("      %13s   %8.3f     %8.3f    %8.3f    %8.3f      %2s ",
                                                                           "Satterthwaite",
                                                                           diffXBar,
                                                                           stErr_Unpooled,
                                                                           satterthwaite_df,
                                                                           ciDiff_Low_Unpooled,
                                                                           "+\u221E"));
       


        addNBlankLinesToSumStatsReport(1);
        sumStatsIndTReport.add(String.format("   %13s      %8.3f     %8.3f      %4d      %8.3f      %2s",
                                                                           "Pooled",
                                                                           diffXBar,
                                                                           stErr_Pooled,
                                                                           pooled_df,
                                                                           ciDiff_Low_Pooled,
                                                                           "+\u221E"));
        addNBlankLinesToSumStatsReport(2);
        sumStatsIndTReport.add(String.format("        Effect size (Cohen's D) = %5.3f",  effectSize)); 
        addNBlankLinesToSumStatsReport(1);
        addNBlankLinesToSumStatsReport(1);
    }
    
    private void printGreaterThan() {
        addNBlankLinesToSumStatsReport(2);
        sumStatsIndTReport.add("          *****   Hypothesis test:    *****");
        addNBlankLinesToSumStatsReport(2);                
        sumStatsIndTReport.add(String.format("  %30s %5.3f", "       Null hypothesis: \u03BC\u2081 - \u03BC\u2082  = ", hypothDiff));
        addNBlankLinesToSumStatsReport(1);
        sumStatsIndTReport.add(String.format("  %30s %5.3f", "Alternative hypothesis: \u03BC\u2081 - \u03BC\u2082  > ", hypothDiff));
        addNBlankLinesToSumStatsReport(2);
        sumStatsIndTReport.add("          Method       DiffMeans      St Err       df      t-stat      p-Value");
        sumStatsIndTReport.add(String.format("\n      %13s    %8.3f     %8.3f   %8.3f  %8.3f    %8.3f",
                                                                            "Satterthwaite",
                                                                            diffXBar,
                                                                            stErr_Unpooled,
                                                                            satterthwaite_df,
                                                                            t_Unpooled,
                                                                            pValueDiff_Unpooled));
        addNBlankLinesToSumStatsReport(1);
        sumStatsIndTReport.add(String.format("   %13s       %8.3f     %8.3f     %4d    %8.3f    %8.3f",
                                                                            "Pooled",
                                                                            diffXBar,
                                                                            stErr_Pooled,
                                                                            pooled_df,
                                                                            t_Pooled,
                                                                            pValueDiff_Pooled));
        addNBlankLinesToSumStatsReport(2);               
        addNBlankLinesToSumStatsReport(2);  
        strCITitle = "                        ***  " + String.valueOf(confidenceLevel) + "% Confidence intervals for \u03BC\u2081 - \u03BC\u2082 ***";
        sumStatsIndTReport.add(strCITitle);
        addNBlankLinesToSumStatsReport(2);
        sumStatsIndTReport.add(String.format("          Method       DiffMeans     StandErr      df       ciLow      ciHigh"));
        addNBlankLinesToSumStatsReport(2); 
        sumStatsIndTReport.add(String.format("      %13s    %8.3f     %8.3f     %8.3f      %2s      %8.3f",
                                                                            "Satterthwaite",
                                                                            diffXBar,
                                                                            stErr_Unpooled,
                                                                            satterthwaite_df,
                                                                            "-\u221E",
                                                                            ciDiff_High_Unpooled));
        addNBlankLinesToSumStatsReport(2); 
        sumStatsIndTReport.add(String.format("   %13s       %8.3f     %8.3f       %4d        %2s      %8.3f",
                                                                            "Pooled",
                                                                            diffXBar,
                                                                            stErr_Pooled,
                                                                            pooled_df,
                                                                            "-\u221E",
                                                                            ciDiff_High_Pooled));          
        addNBlankLinesToSumStatsReport(2);
        sumStatsIndTReport.add(String.format("        Effect size (Cohen's D) = %5.3f",  effectSize)); 
        addNBlankLinesToSumStatsReport(1);
    }
    
    private double getCohensD(double xBar1, double standDev1, int n1,
                               double xBar2, double standDev2, int n2) {
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
    
    private void addNBlankLinesToSumStatsReport(int thisMany) {
        StringUtilities.addNLinesToArrayList(sumStatsIndTReport, thisMany);
    }
    
    public String getHypotheses() { return altHypothesis; }
    public int getDF() { return (int)Math.floor(satterthwaite_df); }
    public int getConfidenceLevel() { return confidenceLevel; }
    public double getTStat() { return t_Unpooled; }
    public double getPValue() { return pValueDiff_Unpooled; }
    public double getAlpha() { return alpha; }
    public ArrayList<String> getIndep_T_SumStats_Report() { return sumStatsIndTReport; }
}

