/************************************************************
 *                     TwoProp_Inf_Model                    *
 *                          11/01/23                        *
 *                            12:00                         *
 ***********************************************************/
/************************************************************
 *    Plus-4 calculations agree with Moore/McCabe/Craig     *
 *    Introduction to the Practice of Statistics (7th ed)   *
 *    but would like confirmation.  (Round-off problem)     *
 *                          05/24/20                        *
 ***********************************************************/
package the_z_procedures;

import genericClasses.Point_2D;
import dialogs.t_and_z.TwoProp_Inference_Dialog;
import probabilityDistributions.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import utilityClasses.*;

class PropComparator implements Comparator<PropAndProb> { 
    public int compare(PropAndProb pp1, PropAndProb pp2) {
        double diff = pp1.getProportion() - pp2.getProportion();
        
        if (diff > 0.) 
            { return 1; }
        else 
        if (diff < 0.) 
            { return -1; }
        else
        { return 0; }
    }
}

public class TwoProp_Inf_Model {
    //  POJOs
    int nSuccesses_1, nSuccesses_2, nTrials_1, nTrials_2, nFailures_1, 
        nFailures_2, pAndPSize, confidenceLevel;
    
    double ciDiff_Low, ciDiff_High, pValueDiff, alpha, alphaOverTwo,
           diff_In_pHats, hypDiff, pValueFisher, zForTwoTails, zForOneTail, 
           stErrP1, stErrP2, ciLowP1, ciHighP1, ciLowP2, ciHighP2, stErrUnpooled, 
           stErrPooled, z_for_Pooled, dbl_NSuccesses_1, dbl_NSuccesses_2, 
           dbl_NTrials_1, dbl_NTrials_2, pHat_1, pHat_2, cohensH, effectSize; 
    
    final double ONE, TWO;

    String returnStatus, altHypoth, prop1_Descr, prop2_Descr, title_Descr;
    
    ArrayList<String> twoPropReport;
    
    // My Classes
    ArrayList<PropAndProb> propAndProbs;
    Point_2D  lowEnds, highEnds, pHats, ciDiff;

    // FX Classes
    StandardNormal standNorm;   
    TwoProp_Inference_Dialog twoProp_Inf_Dialog;

    public TwoProp_Inf_Model() {
        standNorm = new StandardNormal();
        twoProp_Inf_Dialog = new TwoProp_Inference_Dialog();
        ONE = 1.0; TWO = 2.0;
    }
    
    public String doZProcedure() {
        returnStatus = "OK";
        twoProp_Inf_Dialog.showAndWait();
        returnStatus = twoProp_Inf_Dialog.getReturnStatus(); 
        if (returnStatus.equals("OK")) {
            prop1_Descr = twoProp_Inf_Dialog.getP1Label();
            prop2_Descr = twoProp_Inf_Dialog.getP2Label();
            title_Descr = twoProp_Inf_Dialog.getTheTitle();
            altHypoth = twoProp_Inf_Dialog.getAltHypothesis();
            twoPropReport = new ArrayList();
            nTrials_1 = twoProp_Inf_Dialog.getN1();
            nTrials_2 = twoProp_Inf_Dialog.getN2();
            nSuccesses_1 = twoProp_Inf_Dialog.getX1();
            nSuccesses_2 = twoProp_Inf_Dialog.getX2();
            nFailures_1 = nTrials_1 - nSuccesses_1;
            nFailures_2 = nTrials_2 - nSuccesses_2;
            
            dbl_NSuccesses_1 = nSuccesses_1;
            dbl_NSuccesses_2 = nSuccesses_2;

            dbl_NTrials_1 = nTrials_1;
            dbl_NTrials_2 = nTrials_2;

            pHat_1 = twoProp_Inf_Dialog.getP1();
            pHat_2 = twoProp_Inf_Dialog.getP2();
            
            effectSize = getCohensH(pHat_1, nTrials_1, pHat_2, nTrials_2);
            
            pHats = new Point_2D(pHat_1, pHat_2);  
            
            diff_In_pHats = pHat_1 - pHat_2;
            hypDiff = twoProp_Inf_Dialog.getHypothesizedDiff();

            alpha = twoProp_Inf_Dialog.getLevelOfSignificance();
            confidenceLevel = (int)(100. * (1 - alpha));
            alphaOverTwo = alpha / TWO;

            // These functions deliver the positive z's
            zForOneTail = -standNorm.getInvLeftTailArea(alpha);
            zForTwoTails =  -standNorm.getInvLeftTailArea(alphaOverTwo);     

            double temp1 = pHat_1 * (ONE - pHat_1) / dbl_NTrials_1 + pHat_2 * (ONE - pHat_2) / dbl_NTrials_2;
            double p_sub_c = (dbl_NSuccesses_1 + dbl_NSuccesses_2) / (dbl_NTrials_1 + dbl_NTrials_2);

            stErrP1 = Math.sqrt(pHat_1 * (ONE - pHat_1) / dbl_NTrials_1);
            stErrP2 = Math.sqrt(pHat_2 * (ONE - pHat_2) / dbl_NTrials_2);

            ciLowP1 = pHat_1 - zForTwoTails * stErrP1;
            ciLowP1 = Math.max(ciLowP1, -ONE);
            ciHighP1 = pHat_1 + zForTwoTails * stErrP1;
            ciHighP1 = Math.min(ciHighP1, ONE);

            ciLowP2 = pHat_2 - zForTwoTails * stErrP2;
            ciLowP2 = Math.max(ciLowP2, -ONE);
            ciHighP2 = pHat_2 + zForTwoTails * stErrP2;
            ciHighP2 = Math.min(ciHighP2, ONE);
            
            lowEnds = new Point_2D(ciLowP1, ciLowP2);
            highEnds = new Point_2D(ciHighP1, ciHighP2);
            
            stErrUnpooled = Math.sqrt(temp1);
            stErrPooled = Math.sqrt((p_sub_c) * (ONE  - p_sub_c) * (ONE / dbl_NTrials_1 + ONE / dbl_NTrials_2));
            z_for_Pooled = diff_In_pHats / stErrPooled;
            
            switch (altHypoth) {
                case "NotEqual":        
                    ciDiff_Low = diff_In_pHats - zForTwoTails * stErrUnpooled;
                    ciDiff_Low = Math.max(ciDiff_Low, -TWO);
                    ciDiff_High = diff_In_pHats + zForTwoTails * stErrUnpooled;
                    ciDiff_High = Math.min(ciDiff_High, TWO);
                    double zLo = -Math.abs(z_for_Pooled);
                    double zHi = Math.abs(z_for_Pooled);
                    pValueDiff = 1.0 - standNorm.getMiddleArea(zLo, zHi);
                    printSummaryInformation();
                    printNotEqualTo();

                break;

            case "LessThan":
                ciDiff_Low = diff_In_pHats - zForOneTail * stErrUnpooled;
                ciDiff_High = TWO;
                pValueDiff = standNorm.getLeftTailArea(z_for_Pooled);
                printSummaryInformation();
                printLessThan();
                break;

            case "GreaterThan":
                ciDiff_Low = -TWO;
                ciDiff_High = diff_In_pHats + zForOneTail * stErrUnpooled;
                pValueDiff = standNorm.getRightTailArea(z_for_Pooled);
                printSummaryInformation();
                printGreaterThan();
                break;

            default:
                String switchFailure = "Switch failure: TwoProp_Inf_Model 162 " + altHypoth;
                MyAlerts.showUnexpectedErrorAlert(switchFailure); 
            }   
            
            ciDiff = new Point_2D(ciDiff_Low, ciDiff_High);           
        }   //  end ok to continue
        else { return returnStatus; }
                
        return returnStatus;
    }
    
    private void printSummaryInformation() {
        addNBlankLinesToTwoPropReport(1);
        twoPropReport.add("                       *** Summary information ***   ");
        addNBlankLinesToTwoPropReport(2);
        twoPropReport.add("       Prop        NSize     NSucc     prop     ciLow     ciHigh");
        addNBlankLinesToTwoPropReport(1);
        twoPropReport.add(String.format("   %10s      %4d     %4d      %5.3f     %5.3f      %5.3f",     "Prop #1",
                                                                                   nTrials_1,
                                                                                   nSuccesses_1,
                                                                                   pHat_1,
                                                                                   ciLowP1,
                                                                                   ciHighP1));
        addNBlankLinesToTwoPropReport(1);

        twoPropReport.add(String.format("   %10s      %4d     %4d      %5.3f     %5.3f      %5.3f",     "Prop #2",
                                                                                   nTrials_2,
                                                                                   nSuccesses_2,
                                                                                   pHat_2,
                                                                                   ciLowP2,
                                                                                   ciHighP2));        
    }
    
    private void printNotEqualTo() {
        addNBlankLinesToTwoPropReport(2);       
        twoPropReport.add("                   ***  Hypothesis Test  ***");
        addNBlankLinesToTwoPropReport(2);    
        twoPropReport.add(String.format("           %15s", "       Null hypothesis:  Ho:p\u2081 - p\u2082 = 0"));
        addNBlankLinesToTwoPropReport(1);
        twoPropReport.add(String.format("           %15s", "Alternative hypothesis:  Ho:p\u2081 - p\u2082 \u2260 0"));
        addNBlankLinesToTwoPropReport(2);
        twoPropReport.add(String.format("           %15s %6.3f", "           z-statistic: ", z_for_Pooled));
        addNBlankLinesToTwoPropReport(1);
        twoPropReport.add(String.format("           %15s %6.3f", "               p-Value: ", pValueDiff));
        addNBlankLinesToTwoPropReport(2);
        String strCITitle = "              ***  " + String.valueOf(confidenceLevel) + "% Confidence interval for p\u2081 - p\u2082 ***";
        twoPropReport.add(strCITitle);
        addNBlankLinesToTwoPropReport(2);    
        twoPropReport.add("             p1 - p2      StandErr      ciLow    ciHigh");
        addNBlankLinesToTwoPropReport(1); 
        twoPropReport.add(String.format("             %6.4f       %6.3f      %6.3f    %6.3f",
                                                                                diff_In_pHats,
                                                                                stErrUnpooled,
                                                                                ciDiff_Low,
                                                                                ciDiff_High));  
        
        addNBlankLinesToTwoPropReport(2);
        twoPropReport.add(String.format("        Effect size (Cohen's H) = %5.3f",  effectSize)); 
        addNBlankLinesToTwoPropReport(1);
        
        if (!assumptionsAreOK()) {
            doFishersExact();
            addNBlankLinesToTwoPropReport(2);
            twoPropReport.add(String.format("%50s",   "*** Warning: Your data do not satisfy the assumptions for the normal approximation. ***"));
            addNBlankLinesToTwoPropReport(1);
            twoPropReport.add(String.format("%50s", "***  An alternative inference procedure in this circumstance is Fishers Exact Test. ***")); 
            addNBlankLinesToTwoPropReport(2);
            twoPropReport.add(String.format("                    %15s %5.3f", "p-value for Fishers Exact Test = ", pValueFisher));
        }
    }
    
    private void printLessThan() {
        addNBlankLinesToTwoPropReport(2);
        twoPropReport.add("                   ***  Hypothesis Test  ***");
        addNBlankLinesToTwoPropReport(2);
        twoPropReport.add(String.format("           %15s", "       Null hypothesis:  Ho:p\u2081 - p\u2082 = 0"));
        addNBlankLinesToTwoPropReport(1);
        twoPropReport.add(String.format("           %15s", "Alternative hypothesis:  Ho:p\u2081 - p\u2082 < 0"));
        addNBlankLinesToTwoPropReport(2);
        twoPropReport.add(String.format("           %15s %6.3f", "           z-statistic: ", z_for_Pooled));
        addNBlankLinesToTwoPropReport(1);
        twoPropReport.add(String.format("           %15s %6.3f", "               p-Value: ", pValueDiff));
        addNBlankLinesToTwoPropReport(2);
        String strCITitle = "              ***  " + String.valueOf(confidenceLevel) + "% Confidence interval for p\u2081 - p\u2082 ***";
        twoPropReport.add(strCITitle);
        addNBlankLinesToTwoPropReport(2);
        twoPropReport.add("             p1 - p2      StandErr     ciLow    ciHigh");
        addNBlankLinesToTwoPropReport(1);
        twoPropReport.add(String.format("             %6.4f        %5.3f     %6.3f    %6.3f",
                                                                            diff_In_pHats,
                                                                            stErrUnpooled,
                                                                            ciDiff_Low,
                                                                            ciDiff_High));  
        addNBlankLinesToTwoPropReport(2);
        twoPropReport.add(String.format("        Effect size (Cohen's H) = %5.3f",  effectSize)); 
        addNBlankLinesToTwoPropReport(1);
        
        if (!assumptionsAreOK()) {
            doFishersExact();
            addNBlankLinesToTwoPropReport(2);
            twoPropReport.add(String.format("%50s",   "*** Warning: Your data do not satisfy the assumptions for the normal approximation. ***"));
            addNBlankLinesToTwoPropReport(1);
            twoPropReport.add(String.format("%50s", "***  An alternative inference procedure in this circumstance is Fishers Exact Test. ***")); 
            addNBlankLinesToTwoPropReport(2);
            twoPropReport.add(String.format("                    %15s %5.3f", "p-value for Fishers Exact Test = ", pValueFisher));
        }
    }
    
    private void printGreaterThan() {
        addNBlankLinesToTwoPropReport(2);
        twoPropReport.add("                   ***  Hypothesis Test  ***");
        addNBlankLinesToTwoPropReport(2);
        twoPropReport.add(String.format("           %15s", "       Null hypothesis:  Ho:p\u2081 - p\u2082 = 0"));
        addNBlankLinesToTwoPropReport(1);
        twoPropReport.add(String.format("           %15s", "Alternative hypothesis:  Ho:p\u2081 - p\u2082 > 0"));
        addNBlankLinesToTwoPropReport(2);
        twoPropReport.add(String.format("           %15s %6.3f", "           z-statistic: ", z_for_Pooled));
        addNBlankLinesToTwoPropReport(1);
        twoPropReport.add(String.format("           %15s %6.3f", "               p-Value: ", pValueDiff));
        addNBlankLinesToTwoPropReport(2); 
        String strCITitle = "              ***  " + String.valueOf(confidenceLevel) + "% Confidence interval for p\u2081 - p\u2082 ***";
        twoPropReport.add(strCITitle);
        addNBlankLinesToTwoPropReport(2);
        twoPropReport.add("             p1 - p2      StandErr     ciLow    ciHigh");
        addNBlankLinesToTwoPropReport(1);
        twoPropReport.add(String.format("             %6.4f        %5.3f     %6.3f    %6.3f",
                                                                            diff_In_pHats,
                                                                            stErrUnpooled,
                                                                            ciDiff_Low,
                                                                            ciDiff_High));  
        addNBlankLinesToTwoPropReport(2);
        twoPropReport.add(String.format("        Effect size (Cohen's H) = %5.3f",  effectSize)); 
        addNBlankLinesToTwoPropReport(1);
        
        if (!assumptionsAreOK()) {
            doFishersExact();
            addNBlankLinesToTwoPropReport(2);
            twoPropReport.add(String.format("%50s",   "*** Warning: Your data do not satisfy the assumptions for the normal approximation. ***"));
            addNBlankLinesToTwoPropReport(1);
            twoPropReport.add(String.format("%50s", "***  An alternative inference procedure in this circumstance is Fishers Exact Test. ***")); 
            addNBlankLinesToTwoPropReport(2);
            twoPropReport.add(String.format("                    %15s %5.3f", "p-value for Fishers Exact Test = ", pValueFisher));
        }
    }
    
    //  This check only looks at the 'unpooled' rule; the 'pooled' rule is 
    //  not really complete in elementary textbooks.  The unstated assumption
    //  with the pooled rule is that the sample sizes are nearly equal
    private boolean assumptionsAreOK() {
       boolean assumptionsOK;
       assumptionsOK = ((nSuccesses_1 >= 10) && 
                        (nSuccesses_2 >= 10) && 
                        (nFailures_1 >= 10) && 
                        (nFailures_2 >= 10));   
       return assumptionsOK;
    }
    
    private void doFishersExact() {
        int a = nSuccesses_1; int b = nSuccesses_2;
        int c = nFailures_1; int d = nFailures_2;
        FishersExact fishyWishy = new FishersExact(a, b, c, d);
        pValueFisher = fishyWishy.getPValue(altHypoth);
    }
 
    private void addNBlankLinesToTwoPropReport(int thisMany) {
        StringUtilities.addNLinesToArrayList(twoPropReport, thisMany);
    }
    
    public int getNSuccesses_1() { return nSuccesses_1; }
    public int getNSuccesses_2() { return nSuccesses_2; } 
    public int getNTrials_1() { return nTrials_1; }
    public int getNTrials_2() { return nTrials_2; }
    
    public String getHypotheses() {  return altHypoth; }
    public double getAlpha() { return alpha; }
    public double getZStat() { return z_for_Pooled;  }
    public double getPValue() { return pValueDiff; }
    
    public double getStErr_Pooled() { return stErrPooled; }
    public double getStErr_Unpooled() { return stErrUnpooled; }
    
    public ArrayList<PropAndProb> getPandP () {
        Collections.sort(propAndProbs, new PropComparator());
        
        propAndProbs.get(0).setCDF(propAndProbs.get(0).getMass());
        for (int ithSortedProb = 1; ithSortedProb < pAndPSize; ithSortedProb++) {
            propAndProbs.get(ithSortedProb)
                        .setCDF(propAndProbs.get(ithSortedProb - 1).getCDF()
                            + propAndProbs.get(ithSortedProb).getMass());
        }
        return propAndProbs;
    }
    
    private double getCohensH(double pHat1, int n1, double pHat2, int n2) {
        double phi_pHat1 = 2.0 * Math.asin(Math.sqrt(pHat1));
        double phi_pHat2 = 2.0 * Math.asin(Math.sqrt(pHat2));
        cohensH = phi_pHat1 - phi_pHat2;
        if(altHypoth.equals("NotEqual")) { cohensH = Math.abs(cohensH); }
        return cohensH;
    }
    
    public ArrayList<String> getStringsToPrint() { return twoPropReport; }
    public String getReturnStatus() { return returnStatus; }
    public int getConfidenceLevel() {return confidenceLevel; }
    public Point_2D getLowEnds() {return lowEnds; }
    public Point_2D getHighEnds() {return highEnds; }
    public Point_2D getPHats() {return pHats; }   
    public String getP1_Descr() { return prop1_Descr; }
    public String getP2_Descr() { return prop2_Descr; }
    public String getTheTitle() { return title_Descr; }    
    public double getDiffInPHats() { return diff_In_pHats; }
    public Point_2D getCIDiff() { return ciDiff; }
}

