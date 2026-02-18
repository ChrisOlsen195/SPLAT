/************************************************************
 *                     OneProp_Inf_Model                    *
 *                          12/08/25                        *
 *                            12:00                         *
 ***********************************************************/
/***************************************************************************
 *  Interval for exact confidence intervals is the Jeffreys Interval.      *
 *  Brown, L. D., Cai, T. T, & DasGupta, A. (2001).Interval Estimation for *
 *  a Binomial proportion.  Statistical Science 16(2): 101-133, p108       *
 **************************************************************************/
/************************************************************
 *    Plus-4 calculations agree with Moore/McCabe/Craig     *
 *    Introduction to the Practice of Statistics (7th ed)   *
 *    but would like confirmation.  (Round-off problem)     *
 *                          05/24/20                        *
 ***********************************************************/
package the_z_procedures;

import genericClasses.Point_2D;
import dialogs.t_and_z.OneProp_Inf_Dialog;
import probabilityDistributions.*;
import java.util.ArrayList;
import utilityClasses.*;

public class OneProp_Inf_Model {
    //  POJOs
    boolean printTheStuff = true;
    //boolean printTheStuff = false;
    
    private boolean goodToGo, hypothTestDesired;
    
    private int n, x, confidenceLevel;
    
    double exact_CI_Low, exact_CI_High, standard_CI_Low, standard_CI_High, 
           wilson_CI_Low, wilson_CI_High, agCoul_CI_Low, agCoul_CI_High,
           standard_pValue, alpha, alphaOverTwo, dbl_x, dbl_n, standard_pHat,
           agCoul_pHat, hypothProp, crit_Z_twoTails, crit_Z_oneTail, k_oneTail,
           stErr_CI_agCoul, standard_SE_CI, wilson_pHat_1Tail,
           wilson_pHat_2Tails, stErr_Hypoth, z_statistic, zLo, zHi, agCoul_num,
           agCoul_den, cohensH, effectSize, k_twoTails, wilson_crit_1Tail, 
           wilson_crit_2Tails, stErr_CI_Wilson_1Tail, factor_CI_Wilson_2Tails,
           zeroPointZero, onePointZero, twoPointZero, fourPointZero;

    public String strReturnStatus, strAltHypoth, graphProp, graphTitle;
    ArrayList<String> onePropReport;
    
    // My Classes
    BetaDistribution betaDist_Low, betaDist_High;
    OneProp_Inf_Controller oneProp_Inf_Controller;

    // FX Classes 
    Point_2D confIntForP;
    StandardNormal standNorm;    
    OneProp_Inf_Dialog oneProp_Inf_Dialog;

    public OneProp_Inf_Model(OneProp_Inf_Controller oneProp_Inf_Controller) {
        //if (printTheStuff) {
        //    System.out.println("*** 58 OneProp_Inf_Model, Constructing");
        //}
        this.oneProp_Inf_Controller = oneProp_Inf_Controller;
        standNorm = new StandardNormal();
        oneProp_Inf_Dialog = new OneProp_Inf_Dialog(this);
        strReturnStatus = "OK";
        zeroPointZero = 0.0;
        onePointZero = 1.0;
        twoPointZero = 2.0;
        fourPointZero = 4.0;
    }
    
    public String doZProcedure() {
        //if (printTheStuff) {
        //    System.out.println("*** 72 OneProp_Inf_Model,  doZProcedure()");
        //}
        oneProp_Inf_Dialog.showAndWait();
        //if (printTheStuff) {
        //    System.out.println("--- 76 OneProp_Inf_Model, strReturnStatus = " + strReturnStatus);
        //}
        if (strReturnStatus.equals("Cancel") 
                || strReturnStatus.equals("CloseWindow")) {
            oneProp_Inf_Controller.setReturnStatus(strReturnStatus);
            return strReturnStatus; 
        }
        //if (printTheStuff) {
        //    System.out.println("--- 84 OneProp_Inf_Model, strReturnStatus = " + strReturnStatus);
        //} 
        goodToGo = oneProp_Inf_Dialog.getGoodToGo();

        if (goodToGo) {
            onePropReport = new ArrayList();
            graphProp = oneProp_Inf_Dialog.get_GraphProp();
            graphTitle = oneProp_Inf_Dialog.get_GraphTitle();
            hypothTestDesired = oneProp_Inf_Dialog.getHypothesisTestDesired();
            n = oneProp_Inf_Dialog.getN1();
            x = oneProp_Inf_Dialog.getX1();
            strAltHypoth = oneProp_Inf_Dialog.getAltHypothesis();
            dbl_x = x;
            dbl_n = n;
            standard_pHat = dbl_x / dbl_n;

            alpha = oneProp_Inf_Dialog.getLevelOfSignificance();
            confidenceLevel = (int)(100. * (onePointZero - alpha));
            alphaOverTwo = alpha / 2.0;             
            crit_Z_oneTail = -standNorm.getInvLeftTailArea(alpha);
            crit_Z_twoTails = -standNorm.getInvLeftTailArea(alphaOverTwo);
            k_oneTail = crit_Z_oneTail;
            k_twoTails = crit_Z_twoTails; // Used in Wilson calculations
            
            //  Modified sample proportion for the Agresti-Coul CI
            agCoul_num = dbl_x + 0.5 * crit_Z_twoTails * crit_Z_twoTails;
            agCoul_den = dbl_n + crit_Z_twoTails * crit_Z_twoTails;       
            agCoul_pHat = agCoul_num / agCoul_den;    
            hypothProp = oneProp_Inf_Dialog.getHypothesizedProp();
            stErr_Hypoth = Math.sqrt(hypothProp  * (onePointZero - hypothProp ) / dbl_n);
            
            effectSize = getCohensH(standard_pHat, hypothProp, n);
            int nSuccesses = x;
            int nFailures = n - nSuccesses;
            wilson_pHat_1Tail = (dbl_x + k_twoTails * k_twoTails / twoPointZero) / (dbl_n + k_twoTails * k_twoTails);
            wilson_pHat_2Tails = (dbl_x + k_twoTails * k_twoTails / twoPointZero) / (dbl_n + k_twoTails * k_twoTails);
            wilson_crit_1Tail = k_oneTail * Math.sqrt(dbl_n) / (dbl_n + k_oneTail * k_oneTail);            
            wilson_crit_2Tails = k_twoTails * Math.sqrt(dbl_n) / (dbl_n + k_twoTails * k_twoTails);

            doTheStandardInterval();
            doAgrestiCoul(); 
            doWilsonScore();
            
            betaDist_Low = new BetaDistribution(nSuccesses + 0.5, nFailures + 0.5);
            betaDist_High = new BetaDistribution(nSuccesses + 0.5, nFailures + 0.5);           
            
            switch (strAltHypoth) {
                case "NotEqual":     
                    //if (printTheStuff) {
                    //    System.out.println("133 OneProp_Inf_Model, NotEqual");
                    //}
                    standard_CI_Low = standard_pHat - crit_Z_twoTails * standard_SE_CI;
                    standard_CI_Low = Math.max(standard_CI_Low, zeroPointZero);
                    standard_CI_High = standard_pHat + crit_Z_twoTails * standard_SE_CI;
                    standard_CI_High = Math.min(standard_CI_High, onePointZero);
                    
                    agCoul_CI_Low = agCoul_pHat - crit_Z_twoTails * stErr_CI_agCoul;
                    agCoul_CI_Low = Math.max(agCoul_CI_Low, zeroPointZero);
                    agCoul_CI_High = agCoul_pHat + crit_Z_twoTails * stErr_CI_agCoul;
                    agCoul_CI_High = Math.min(agCoul_CI_High, onePointZero);
                    
                    wilson_CI_Low = wilson_pHat_2Tails - wilson_crit_2Tails * factor_CI_Wilson_2Tails;
                    wilson_CI_Low = Math.max(wilson_CI_Low, zeroPointZero);
                    wilson_CI_High = wilson_pHat_2Tails + wilson_crit_2Tails * factor_CI_Wilson_2Tails;
                    wilson_CI_High = Math.min(wilson_CI_High, onePointZero);
                    
                    /*
                    if (printTheStuff) {
                        System.out.println("152 OneProp_Inf_Model, NotEqual, k = " + k_twoTails);
                        System.out.println("153 OneProp_Inf_Model, NotEqual, wilson_pHat_2Tails = " + wilson_pHat_2Tails);
                        System.out.println("154 OneProp_Inf_Model, NotEqual, wilson_crit_2Tails = " + wilson_crit_2Tails);
                        System.out.println("155 OneProp_Inf_Model, NotEqual, factor_CI_Wilson_2Tails = " + factor_CI_Wilson_2Tails);
                        System.out.println("156 OneProp_Inf_Model, NotEqual, Wilson Low/High = " + wilson_CI_Low + " / " + wilson_CI_High);
                    }
                    */
                    
                    zLo = -Math.abs(z_statistic);
                    zHi = Math.abs(z_statistic);
                    standard_pValue = onePointZero - standNorm.getMiddleArea(zLo, zHi);
                    exact_CI_Low = betaDist_Low.getInverseLeftTailArea(alphaOverTwo);
                    exact_CI_High = betaDist_Low.getInverseLeftTailArea(1.0 - alphaOverTwo);
                    
                    printSummaryInformation();
                    printNotEqualTo();                    
                    break;

                case "LessThan":
                    //if (printTheStuff) {
                    //    System.out.println("172 OneProp_Inf_Model, LessThan");
                    //}
                    agCoul_CI_Low = agCoul_pHat - crit_Z_twoTails * stErr_CI_agCoul;
                    agCoul_CI_Low = Math.max(agCoul_CI_Low, zeroPointZero);
                    standard_CI_Low = standard_pHat - crit_Z_oneTail * standard_SE_CI;
                    standard_CI_Low = Math.max(standard_CI_Low, zeroPointZero);
                    wilson_CI_Low = wilson_pHat_1Tail - wilson_crit_1Tail * stErr_CI_Wilson_1Tail;
                    wilson_CI_Low = Math.max(wilson_CI_Low, zeroPointZero);
                    zLo = -Math.abs(z_statistic);
                    zHi = Math.abs(z_statistic);
                    standard_pValue = standNorm.getLeftTailArea(z_statistic);
                    exact_CI_Low = betaDist_Low.getInverseLeftTailArea(alpha);
 
                    printSummaryInformation();
                    printLessThan();
                    break;

                case "GreaterThan":
                    //if (printTheStuff) {
                    //    System.out.println("191 OneProp_Inf_Model, GreaterThan");
                    //}
                    standard_CI_High = standard_pHat + crit_Z_oneTail * standard_SE_CI;
                    standard_CI_High = Math.min(standard_CI_High, onePointZero);
                    agCoul_CI_High = agCoul_pHat + crit_Z_oneTail * stErr_CI_agCoul;
                    agCoul_CI_High = Math.min(agCoul_CI_High, onePointZero);
                    wilson_CI_High = wilson_pHat_1Tail + wilson_crit_1Tail * stErr_CI_Wilson_1Tail;
                    wilson_CI_High = Math.max(wilson_CI_High, zeroPointZero);
                    zLo = -Math.abs(z_statistic);
                    zHi = Math.abs(z_statistic);
                    standard_pValue = standNorm.getRightTailArea(z_statistic);
                    exact_CI_High = betaDist_High.getInverseLeftTailArea(onePointZero - alpha);
                    printSummaryInformation();
                    printGreaterThan();
                    break;

                default:
                    String switchFailure = "Switch failure: OneProp_Inf_Model 208 " + strAltHypoth;
                    MyAlerts.showUnexpectedErrorAlert(switchFailure);  
                }   
            
            confIntForP = new Point_2D(standard_CI_Low, standard_CI_High);
        }

        return strReturnStatus;
    }
    
    private void doTheStandardInterval() {
            standard_SE_CI = Math.sqrt(standard_pHat * (onePointZero - standard_pHat) / dbl_n);
            z_statistic = (standard_pHat - hypothProp) / stErr_Hypoth;     
    }

    private void doAgrestiCoul() {
        //  Modified sample proportion for the plus-4 confidence interval
        stErr_CI_agCoul = Math.sqrt(agCoul_pHat * (onePointZero - agCoul_pHat) / (dbl_n + 4.0));
    }
    
    private void doWilsonScore() {
        // Brown, L. D., Cai, T. T, & DasGupta, A. Formula 4, p107  
        double temp1 = standard_pHat * (onePointZero - standard_pHat);
        double temp2_1Tail = k_oneTail * k_oneTail / (fourPointZero * dbl_n);
        double temp2_2Tails = k_twoTails * k_twoTails / (fourPointZero * dbl_n);
        stErr_CI_Wilson_1Tail = Math.sqrt(temp1 + temp2_1Tail);
        factor_CI_Wilson_2Tails = Math.sqrt(temp1 + temp2_2Tails);
    }
    
    private void printSummaryInformation() {
        addNBlankLinesToOnePropReport(1);
        onePropReport.add("                 *** Summary information ***   ");
        addNBlankLinesToOnePropReport(2);
        onePropReport.add("          NSize     NSucc   propSucc");
        addNBlankLinesToOnePropReport(1);
        onePropReport.add(String.format("         %4d      %4d       %5.3f", n,
                                                                 x,
                                                                 standard_pHat));        
    }
    
    private void printNotEqualTo() {
        if(oneProp_Inf_Dialog.getHypothesisTestDesired()) {
            addNBlankLinesToOnePropReport(2);
            onePropReport.add("           ***  Hypothesis Test  ***");
            addNBlankLinesToOnePropReport(2);
            onePropReport.add(String.format("   %15s %5.3f", "       Null hypothesis:  Ho: p\u2080 =", hypothProp));
            addNBlankLinesToOnePropReport(1);
            onePropReport.add(String.format("   %15s %5.3f", "Alternative hypothesis:  Ho: p\u2080 \u2260", hypothProp));
            addNBlankLinesToOnePropReport(2);
            onePropReport.add(String.format("   %15s %5.3f", "           z-statistic: ", z_statistic));
            addNBlankLinesToOnePropReport(1);
            onePropReport.add(String.format("   %15s %5.3f", "               p-Value: ", standard_pValue));
        }    
        
        addNBlankLinesToOnePropReport(2);
        String strCITitle = "              ***  " + String.valueOf(confidenceLevel) + "% Confidence intervals ***";
        onePropReport.add(strCITitle);
        addNBlankLinesToOnePropReport(2);
        onePropReport.add("      Method     Prop     StErr      CI_Lo    CI_Hi");
        addNBlankLinesToOnePropReport(1);
        onePropReport.add("      ------     ----     -----      -----    -----");
        addNBlankLinesToOnePropReport(1);
        onePropReport.add(String.format("  %12s  %5.3f     %5.3f      %5.3f    %5.3f", "  Standard",
                                                                            standard_pHat,
                                                                            standard_SE_CI,
                                                                            standard_CI_Low,
                                                                            standard_CI_High));
        addNBlankLinesToOnePropReport(1);
        //  Plus four
        onePropReport.add(String.format("%12s    %5.3f     %5.3f      %5.3f    %5.3f\n", "Plus-4",
                                                                            agCoul_pHat,
                                                                            stErr_CI_agCoul,
                                                                            agCoul_CI_Low,
                                                                            agCoul_CI_High)); 
        
        //  Wilson
        onePropReport.add(String.format("%12s    %5.3f                %5.3f    %5.3f\n", "Wilson",
                                                                            wilson_pHat_2Tails,
                                                                            //stErr_CI_Wilson_2Tails,
                                                                            wilson_CI_Low,
                                                                            wilson_CI_High)); 
        
        addNBlankLinesToOnePropReport(2);
        if (hypothTestDesired) {
            onePropReport.add(String.format("        Effect size (Cohen's H) = %5.3f",  effectSize)); 
            addNBlankLinesToOnePropReport(1);
        }
        
        if ((x < 10) || (n - x < 10)) {
        addNBlankLinesToOnePropReport(1);
        onePropReport.add(String.format("      %50s", "*** Warning: Your data do not satisfy the assumptions for this test. ***"));
        addNBlankLinesToOnePropReport(1);
        onePropReport.add(String.format("      %50s", "***     Consult the 'Exact test' option for further information.     ***"));
        addNBlankLinesToOnePropReport(2);
        onePropReport.add(String.format("     %25s      %5.3f    %5.3f", " Exact confidence interval",
                                                                            exact_CI_Low,
                                                                            exact_CI_High)); 
        }
        addNBlankLinesToOnePropReport(1);
    }
    
    private void printLessThan() {
        if(oneProp_Inf_Dialog.getHypothesisTestDesired()) {
            addNBlankLinesToOnePropReport(2);
            onePropReport.add("           ***  Hypothesis Test  ***");
            addNBlankLinesToOnePropReport(2);
            onePropReport.add(String.format("   %15s %5.3f", "       Null hypothesis:  Ho: p\u2080 =", hypothProp));
            addNBlankLinesToOnePropReport(1);
            onePropReport.add(String.format("   %15s %5.3f", "Alternative hypothesis:  Ho: p\u2080 <", hypothProp));
            addNBlankLinesToOnePropReport(2);
            onePropReport.add(String.format("   %15s %5.3f", "           z-statistic: ", z_statistic));
            addNBlankLinesToOnePropReport(1);
            onePropReport.add(String.format("   %15s %5.3f", "               p-Value: ", standard_pValue));
            addNBlankLinesToOnePropReport(1);
        }
        
        addNBlankLinesToOnePropReport(1);
        String strCITitle = "              ***  " + String.valueOf(confidenceLevel) + "% Confidence intervals ***";
        onePropReport.add(strCITitle);
        addNBlankLinesToOnePropReport(2);
        onePropReport.add("      Method     Prop     StErr    CI_Lo      CI_Hi");
        addNBlankLinesToOnePropReport(1);
        onePropReport.add("      ------     ----     -----    -----      -----");
        addNBlankLinesToOnePropReport(1);
        onePropReport.add(String.format(" %12s   %5.3f     %5.3f    %5.3f      %5.3f", " Standard",
                                                                            standard_pHat,
                                                                            standard_SE_CI,
                                                                            standard_CI_Low,
                                                                            onePointZero));
        //  Plus four
        addNBlankLinesToOnePropReport(1);
        onePropReport.add(String.format(" %12s   %5.3f     %5.3f    %5.3f      %5.3f\n", "Plus-4",
                                                                            agCoul_pHat,
                                                                            stErr_CI_agCoul,
                                                                            agCoul_CI_Low,
                                                                            onePointZero));  
        
        //  Wilson
        onePropReport.add(String.format("%12s    %5.3f              %5.3f      %5.3f\n", "Wilson",
                                                                            wilson_pHat_1Tail,
                                                                            //stErr_CI_Wilson_1Tail,
                                                                            wilson_CI_Low,
                                                                            onePointZero)); 
        
        addNBlankLinesToOnePropReport(2);
        onePropReport.add(String.format("        Effect size (Cohen's H) = %5.3f",  effectSize)); 
        addNBlankLinesToOnePropReport(1);
        
        if ((x < 10) || (n - x < 10)) {
        addNBlankLinesToOnePropReport(1);
        onePropReport.add(String.format("      %50s", "*** Warning: Your data do not satisfy the assumptions for this test. ***"));
        addNBlankLinesToOnePropReport(1);
        onePropReport.add(String.format("      %50s", "***     Consult the 'Exact test' option for further information.     ***"));
        addNBlankLinesToOnePropReport(2);
        onePropReport.add(String.format("     %25s      %5.3f    %5.3f", " Exact confidence interval",
                                                                            zeroPointZero,
                                                                            exact_CI_High));
        }

        //  Plus four
        addNBlankLinesToOnePropReport(1);
        
    }
    
    private void printGreaterThan() {    
        if(oneProp_Inf_Dialog.getHypothesisTestDesired()) {
            addNBlankLinesToOnePropReport(2);
            onePropReport.add("           ***  Hypothesis Test  ***");
            addNBlankLinesToOnePropReport(2);
            onePropReport.add(String.format("   %15s %5.3f", "       Null hypothesis:  Ho: p\u2080 =", hypothProp));
            addNBlankLinesToOnePropReport(1);
            onePropReport.add(String.format("   %15s %5.3f", "Alternative hypothesis:  Ho: p\u2080 >", hypothProp));
            addNBlankLinesToOnePropReport(2);
            onePropReport.add(String.format("   %15s %5.3f", "           z-statistic: ", z_statistic));
            addNBlankLinesToOnePropReport(1);
            onePropReport.add(String.format("   %15s %5.3f", "               p-Value: ", standard_pValue));
        }
        
        addNBlankLinesToOnePropReport(2);
        String strCITitle = "              ***  " + String.valueOf(confidenceLevel) + "% Confidence intervals ***";
        onePropReport.add(strCITitle);
        addNBlankLinesToOnePropReport(2);
        onePropReport.add("      Method     Prop     StErr     CI_Lo     CI_Hi");
        addNBlankLinesToOnePropReport(1);
        onePropReport.add("      ------     ----     -----    -----      -----");
        addNBlankLinesToOnePropReport(1);
        onePropReport.add(String.format(" %12s   %5.3f     %5.3f     %5.3f     %5.3f", " Standard",
                                                                            standard_pHat,
                                                                            standard_SE_CI,
                                                                            zeroPointZero,
                                                                            standard_CI_High));
        //  Plus four
        addNBlankLinesToOnePropReport(1);
        onePropReport.add(String.format(" %12s   %5.3f     %5.3f     %5.3f     %5.3f\n", "Plus-4",
                                                                            agCoul_pHat,
                                                                            stErr_CI_agCoul,
                                                                            zeroPointZero,
                                                                            agCoul_CI_High));
        
        //  Wilson
        onePropReport.add(String.format("%12s    %5.3f               %5.3f     %5.3f\n", "Wilson",
                                                                            wilson_pHat_1Tail,
                                                                            //stErr_CI_Wilson_1Tail,
                                                                            zeroPointZero,
                                                                            wilson_CI_High)); 
        
        addNBlankLinesToOnePropReport(2);
        onePropReport.add(String.format("        Effect size (Cohen's H) = %5.3f",  effectSize)); 
        addNBlankLinesToOnePropReport(1);
        
        if ((x < 10) || (n - x < 10)) {
        addNBlankLinesToOnePropReport(1);
        onePropReport.add(String.format("      %50s", "*** Warning: Your data do not satisfy the assumptions for this test. ***"));
        addNBlankLinesToOnePropReport(1);
        onePropReport.add(String.format("      %50s", "***     Consult the 'Exact test' option for further information.     ***"));
        addNBlankLinesToOnePropReport(2);
        onePropReport.add(String.format("     %25s      %5.3f    %5.3f", " Exact confidence interval",
                                                                            zeroPointZero,
                                                                            exact_CI_High));
        }
    }
    
    //  ?????????????  Why n ?????????????????
    private double getCohensH(double pHat, double pNull, int n) {
        double phi_pHat = twoPointZero * Math.asin(Math.sqrt(pHat));
        double phi_pNull = twoPointZero * Math.asin(Math.sqrt(pNull));
        cohensH = phi_pHat - phi_pNull;
        
        if(strAltHypoth.equals("NotEqual")) { cohensH = Math.abs(cohensH); }
        return cohensH;
    }
    
    private void addNBlankLinesToOnePropReport(int thisMany) {
        StringUtilities.addNLinesToArrayList(onePropReport, thisMany);
    }
    
    public int getNSuccesses() { return x; }
    public int getSampeSize() { return n; }
    public double getHypothProp() { return hypothProp; }
    public String getAltHypotheses() {  return strAltHypoth; }
    public double getAlpha() { return alpha; }
    public int getConfidenceLevel() { return confidenceLevel; }
    public double getZStat() { return z_statistic;  }
    public double getPValue() { return standard_pValue; }
    public OneProp_Inf_Model getOnePropModel() { return this; }
    public ArrayList<String> getStringsToPrint() { return onePropReport; }
    public double getPHat() { return standard_pHat; }
    public String getReturnStatus() { return strReturnStatus; }
    public void setReturnStatus(String daReturnStatus) { 
        strReturnStatus = daReturnStatus; 
    }
    public boolean getGoodToGo() {return goodToGo; }
    public Point_2D getConfIntForP() { return confIntForP; }
    public String getGraphTitle() { return graphTitle; }
    public String getGraphProp() { return graphProp; }
}