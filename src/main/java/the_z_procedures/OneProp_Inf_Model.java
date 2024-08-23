/************************************************************
 *                     OneProp_Inf_Model                    *
 *                          11/21/23                        *
 *                            15:00                         *
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
import dialogs.t_and_z.OneProp_Inference_Dialog;
import probabilityDistributions.*;
import java.util.ArrayList;
import utilityClasses.*;

public class OneProp_Inf_Model {
    //  POJOs
    private boolean goodToGo;
    
    private int n, x, confidenceLevel;
    
    private double exact_CI_Low, exact_CI_High;
    private double standard_CI_Low, standard_CI_High, standard_pValue, alpha, 
                   alphaOverTwo, dbl_x, dbl_n, standard_pHat, agresti_Coul_pHat, 
                   hypothProp, crit_Z_twoTails, crit_Z_oneTail, stErr_CI_mod, 
                   z_statistic_mod, plus4_CI_Low, plus4_CI_High, standard_SE_CI,
                   stErr_Hypoth, z_statistic, zLo, zHi, agresti_Coul_numerator, 
                   agresti_Coul_denominator, cohensH, effectSize;
    
    private final double zeroPointZero, onePointZero;

    private String returnStatus, altHypoth, graphProp, graphTitle;
    ArrayList<String> onePropReport;
    
    BetaDistribution betaDist_Low, betaDist_High;

    // FX Classes 
    Point_2D confIntForP;
    StandardNormal standNorm;    
    OneProp_Inference_Dialog oneProp_Inf_Dialog;

    public OneProp_Inf_Model() {
        //System.out.println("52 OneProp_Inf_Model, constructing");
        standNorm = new StandardNormal();
        oneProp_Inf_Dialog = new OneProp_Inference_Dialog();
        goodToGo = true;
        returnStatus = "OK";
        zeroPointZero = 0.0;
        onePointZero = 1.0;
    }
    
    public String doZProcedure() {
        oneProp_Inf_Dialog.showAndWait();
        goodToGo = oneProp_Inf_Dialog.getGoodToGo();
        returnStatus = oneProp_Inf_Dialog.getReturnStatus();
  
        if (goodToGo) {
            onePropReport = new ArrayList();
            graphProp = oneProp_Inf_Dialog.get_GraphProp();
            graphTitle = oneProp_Inf_Dialog.get_GraphTitle();
            n = oneProp_Inf_Dialog.getN1();
            x = oneProp_Inf_Dialog.getX1();

            dbl_x = x;
            dbl_n = n;
            standard_pHat = dbl_x / dbl_n;

            alpha = oneProp_Inf_Dialog.getLevelOfSignificance();
            confidenceLevel = (int)(100. * (1 - alpha));
            alphaOverTwo = alpha / 2.0;            
            altHypoth = oneProp_Inf_Dialog.getAltHypothesis();  
            crit_Z_oneTail = -standNorm.getInvLeftTailArea(alpha);
            crit_Z_twoTails = -standNorm.getInvLeftTailArea(alphaOverTwo); 
            
            //  Modified sample proportion for the plus-4 confidence interval
            agresti_Coul_numerator = dbl_x + 0.5 * crit_Z_twoTails * crit_Z_twoTails;
            agresti_Coul_denominator = dbl_n + crit_Z_twoTails * crit_Z_twoTails;
                    
            agresti_Coul_pHat = agresti_Coul_numerator / agresti_Coul_denominator;
                       
            hypothProp = oneProp_Inf_Dialog.getHypothesizedProp();
            stErr_Hypoth = Math.sqrt(hypothProp  * (1.0 - hypothProp ) / dbl_n);
            
            effectSize = getCohensH(standard_pHat, hypothProp, n);
            int nSuccesses = x;
            int nFailures = n - nSuccesses;

            doTheStandardInterval();
            doAgrestiCoul();    
            
            betaDist_Low = new BetaDistribution(nSuccesses + 0.5, nFailures + 0.5);
            betaDist_High = new BetaDistribution(nSuccesses + 0.5, nFailures + 0.5);           
            
            switch (altHypoth) {
                case "NotEqual":     
                    //System.out.println("109 OneProp_Inf_Model, NotEqual");
                    standard_CI_Low = standard_pHat - crit_Z_twoTails * standard_SE_CI;
                    standard_CI_Low = Math.max(standard_CI_Low, zeroPointZero);
                    standard_CI_High = standard_pHat + crit_Z_twoTails * standard_SE_CI;
                    standard_CI_High = Math.min(standard_CI_High, onePointZero);
                    plus4_CI_Low = agresti_Coul_pHat - crit_Z_twoTails * stErr_CI_mod;
                    plus4_CI_Low = Math.max(plus4_CI_Low, zeroPointZero);
                    plus4_CI_High = agresti_Coul_pHat + crit_Z_twoTails * stErr_CI_mod;
                    plus4_CI_High = Math.min(plus4_CI_High, onePointZero);
                    zLo = -Math.abs(z_statistic);
                    zHi = Math.abs(z_statistic);
                    standard_pValue = 1.0 - standNorm.getMiddleArea(zLo, zHi);
                    
                    exact_CI_Low = betaDist_Low.getInverseLeftTailArea(alphaOverTwo);
                    exact_CI_High = betaDist_Low.getInverseLeftTailArea(1.0 - alphaOverTwo);
                    
                    printSummaryInformation();
                    printNotEqualTo();                    
                    break;

                case "LessThan":
                    //System.out.println("131 OneProp_Inf_Model, LessThan");
                    plus4_CI_Low = agresti_Coul_pHat - crit_Z_twoTails * stErr_CI_mod;
                    plus4_CI_Low = Math.max(plus4_CI_Low, zeroPointZero);
                    standard_CI_Low = standard_pHat - crit_Z_oneTail * standard_SE_CI;
                    standard_CI_Low = Math.max(standard_CI_Low, zeroPointZero);
                    zLo = -Math.abs(z_statistic);
                    zHi = Math.abs(z_statistic);
                    standard_pValue = standNorm.getLeftTailArea(z_statistic);
                    exact_CI_Low = betaDist_Low.getInverseLeftTailArea(alpha);

                    printSummaryInformation();
                    printLessThan();
                    break;

                case "GreaterThan":
                    standard_CI_High = standard_pHat + crit_Z_oneTail * standard_SE_CI;
                    standard_CI_High = Math.min(standard_CI_High, onePointZero);
                    plus4_CI_High = agresti_Coul_pHat + crit_Z_oneTail * stErr_CI_mod;
                    plus4_CI_High = Math.min(plus4_CI_High, onePointZero);
                    zLo = -Math.abs(z_statistic);
                    zHi = Math.abs(z_statistic);
                    standard_pValue = standNorm.getRightTailArea(z_statistic);

                    exact_CI_High = betaDist_High.getInverseLeftTailArea(1 - alpha);
                    printSummaryInformation();
                    printGreaterThan();
                    break;

                default:
                    String switchFailure = "Switch failure: OneProp_Inf_Model 155 " + altHypoth;
                    MyAlerts.showUnexpectedErrorAlert(switchFailure);  
                }   
            
            confIntForP = new Point_2D(standard_CI_Low, standard_CI_High);
            }   //  end ok to continue

        return returnStatus;
    }
    
    private void doTheStandardInterval() {
            standard_SE_CI = Math.sqrt(standard_pHat * (1.0 - standard_pHat) / dbl_n);
            z_statistic = (standard_pHat - hypothProp) / stErr_Hypoth;     
    }

    private void doAgrestiCoul() {
        //  Modified sample proportion for the plus-4 confidence interval
        stErr_CI_mod = Math.sqrt(agresti_Coul_pHat * (1.0 - agresti_Coul_pHat) / (dbl_n + 4.0));
        z_statistic_mod = (agresti_Coul_pHat - hypothProp) / standard_SE_CI;
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
                                                                            agresti_Coul_pHat,
                                                                            stErr_CI_mod,
                                                                            plus4_CI_Low,
                                                                            plus4_CI_High)); 
        
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
                                                                            agresti_Coul_pHat,
                                                                            stErr_CI_mod,
                                                                            plus4_CI_Low,
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
                                                                            agresti_Coul_pHat,
                                                                            stErr_CI_mod,
                                                                            zeroPointZero,
                                                                            plus4_CI_High));
        
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
    
    private double getCohensH(double pHat, double pNull, int n) {
        double phi_pHat = 2.0 * Math.asin(Math.sqrt(pHat));
        double phi_pNull = 2.0 * Math.asin(Math.sqrt(pNull));
        cohensH = phi_pHat - phi_pNull;
        
        if(altHypoth.equals("NotEqual")) { cohensH = Math.abs(cohensH); }
        return cohensH;
    }
    
    private void addNBlankLinesToOnePropReport(int thisMany) {
        StringUtilities.addNLinesToArrayList(onePropReport, thisMany);
    }
    
    public int getNSuccesses() { return x; }
    public int getSampeSize() { return n; }
    public double getHypothProp() { return hypothProp; }
    public String getHypotheses() {  return altHypoth; }
    public double getAlpha() { return alpha; }
    public int getConfidenceLevel() { return confidenceLevel; }
    public double getZStat() { return z_statistic;  }
    public double getPValue() { return standard_pValue; }
    public OneProp_Inf_Model getOnePropModel() { return this; }
    public ArrayList<String> getStringsToPrint() { return onePropReport; }
    public double getPHat() { return standard_pHat; }
    public boolean getGoodToGo() {return goodToGo; }
    public String getReturnStatus() { return returnStatus; }
    public Point_2D getConfIntForP() { return confIntForP; }
    public String getGraphTitle() { return graphTitle; }
    public String getGraphProp() { return graphProp; }
}