/**************************************************
 *           IndepMeans_Power_Model               *
 *                  05/29/24                      *
 *                    18:00                       *
 *************************************************/

package power_twomeans;

import genericClasses.Point_2D;
import noncentrals.JDistr_Noncentrals.*;
import java.util.ArrayList;
import utilityClasses.*;

public class IndepMeans_Power_Model {
    
    int n_1, n_2, archived_n_1, archived_n_2;
    
    double alpha, alt_DiffInMeans, null_DiffInMeans, effectSize, 
           standErrDiffMeans, power, nullMu_1, nullMu_2, nullSigma_1, 
           nullSigma_2, lowerLimit, upperLimit, loCum, hiCum, var_1, var_2, 
           archivedNullMeanDiff, archivedNullSigma_1, archivedNullSigma_2, 
           archivedAltMeanDiff, archivedAlpha, archivedEffectSize; 

    Point_2D nonRejectionRegion;
    String rejectionCriterion, sourceString, printedNullHypoth, printedAltHypoth;    
    
    ArrayList<String> powerReport;
    
    IndepMeans_Power_Controller indepMeans_Power_Controller;
    
    public IndepMeans_Power_Model(IndepMeans_Power_Controller iMPC) {
        this.indepMeans_Power_Controller = iMPC;
        powerReport = new ArrayList();
    }
    
    public double calculatePower() {
        var_1 = nullSigma_1 * nullSigma_1;
        var_2 = nullSigma_2 * nullSigma_2;
        standErrDiffMeans = Math.sqrt(var_1 / n_1 + var_2 / n_2);

        constructNonRejectionRegion();
        switch(rejectionCriterion) {
            case "LessThan": 
                loCum = Normal.cumulative(lowerLimit, alt_DiffInMeans, standErrDiffMeans, true, false);
                power = loCum;
                break;
                
            case "NotEqual":     
                loCum = Normal.cumulative(lowerLimit, alt_DiffInMeans, standErrDiffMeans, true, false);
                hiCum = 1.0 - Normal.cumulative(upperLimit, alt_DiffInMeans, standErrDiffMeans, true, false);
                power = loCum + hiCum;
                break;
                
            case "GreaterThan":
                hiCum = 1.0 - Normal.cumulative(upperLimit, alt_DiffInMeans, standErrDiffMeans, true, false); 
                power = hiCum;
                break;   
                
            default:
                String switchFailure = "Switch failure: IndepMeans_Power_Model 60 " + rejectionCriterion;
                MyAlerts.showUnexpectedErrorAlert(switchFailure);
        }
        return power;        
    }
    
    public void constructNonRejectionRegion() {
        lowerLimit = 0.0; upperLimit = 0.0;//  Happy compiler, happy runs
        
        switch (rejectionCriterion) {
            case "LessThan":
                lowerLimit = Normal.quantile(alpha, null_DiffInMeans, 
                                             standErrDiffMeans, true, false);
                upperLimit = Double.POSITIVE_INFINITY; 
            break;
                    
            case "NotEqual":
                lowerLimit = Normal.quantile(alpha / 2., null_DiffInMeans, 
                                             standErrDiffMeans, true, false);
                upperLimit = Normal.quantile(1.0 - alpha / 2., null_DiffInMeans, 
                                             standErrDiffMeans, true, false); 
            break;
                            
            case "GreaterThan":
                lowerLimit = Double.NEGATIVE_INFINITY;
                upperLimit = Normal.quantile(1.0 - alpha, null_DiffInMeans, 
                                             standErrDiffMeans, true, false); 
            break;
            
            default:
                String switchFailure = "Switch failure: IndepMeans_Power_Model 90 " + rejectionCriterion;
                MyAlerts.showUnexpectedErrorAlert(switchFailure);
        }

        nonRejectionRegion = new Point_2D(lowerLimit, upperLimit);
    }
    
    public double getNullMu_1() { return nullMu_1; }
    public void setNullMu_1(int toThis) {
        nullMu_1 = toThis;
    }
    
    public double getNullMu_2() { return nullSigma_2; }
    public void setNullMu_2(int toThis) {nullMu_2 = toThis; }
    
    public int getSampleSize_1() { return n_1; }
    public void setSampleSize_1(int toThis) { 
        n_1 = toThis; 
        //dbl_sampleSize_1 = n_1;
    }
    
    public int getSampleSize_2() { return n_2; }
    public void setSampleSize_2(int toThis) { 
        n_2 = toThis; 
        //dbl_sampleSize_2 = n_2;
    }
     
    public double getNullSigma_1() {return nullSigma_1; }
    public void setNullSigma_1(double toThis) { nullSigma_1 = toThis; }
    
    public double getNullSigma_2() {return nullSigma_2; }
    public void setNullSigma_2(double toThis) { nullSigma_2 = toThis; }
    
    public double getNullDiffMeans() { return null_DiffInMeans; }
    public void setNullDiffMu(double toThis) {  null_DiffInMeans = toThis; }
    
    public double getAltDiffMeans() { return alt_DiffInMeans; }
    public void setAltMuDiff(double toThis) { 
        alt_DiffInMeans = toThis;
        effectSize = Math.abs(alt_DiffInMeans - null_DiffInMeans);
    }
    
    public double getAlpha() { return alpha; }
    public void setAlpha(double toThis) { alpha = toThis; }
    
    public String getPrintedNullHypothesis() { return printedNullHypoth;}
    public void setPrintedNullHypothesis(String toThis) {
        printedNullHypoth = toThis;
    }
    
    public String getPrintedAltHypothesis() { return printedAltHypoth;}
    public void setPrintedAltHypothesis(String toThis) {
        printedAltHypoth = toThis;
    }
    
    public double getStandErrDiffMeans() {return standErrDiffMeans; }
    public void setStandErrDiffMeans(double toThis) {
        standErrDiffMeans = toThis; 
    }
    
    public double getPower() { return power; }
    
    public String getRejectionCriterion() { return rejectionCriterion; }
    public void setRejectionCriterion(String toThis) {
        rejectionCriterion = toThis;
    }
    
    public double getEffectSize() { return effectSize; }
    public void setEffectSize(double toThis) {
        effectSize = toThis;
        if (rejectionCriterion.equals("LessThan")) {
            alt_DiffInMeans = null_DiffInMeans - effectSize;
        }  else {
            alt_DiffInMeans = null_DiffInMeans + effectSize;   
        }
    }
    
    public Point_2D getNonRejectionRegion() { return nonRejectionRegion; }
 
    public void archiveNullValues() {
        archivedNullMeanDiff = null_DiffInMeans;
        archivedAltMeanDiff = alt_DiffInMeans;
        archived_n_1 = n_1;
        archivedNullSigma_1 = nullSigma_1;
        archived_n_2 = n_2;
        archivedNullSigma_2 = nullSigma_2;
        archivedAlpha = alpha;
        archivedEffectSize = effectSize;
    }    
    

    public void restoreNullValues() {
        null_DiffInMeans = archivedNullMeanDiff;
        alt_DiffInMeans = archivedAltMeanDiff ;
        n_1 = archived_n_1;
        n_2 = archived_n_2;
        nullSigma_1 = archivedNullSigma_1;
        nullSigma_2 = archivedNullSigma_2;
        alpha  = archivedAlpha; 
        effectSize = archivedEffectSize;
    } 
    
   public void print_Power_Table()
   {
        addNBlankLinesToPowerReport(2);
        powerReport.add("       Power Statistics");
        addNBlankLinesToPowerReport(2);
        powerReport.add(String.format("Two independent means"));
        addNBlankLinesToPowerReport(1);
        sourceString = "       Null hypothesis: " + printedNullHypoth;
        addNBlankLinesToPowerReport(1);
        powerReport.add(String.format("%15s ", sourceString));
        sourceString = "Alternative hypothesis: " + printedAltHypoth;;
        addNBlankLinesToPowerReport(1);
        powerReport.add(String.format("%15s ", sourceString));
        addNBlankLinesToPowerReport(1);
        sourceString = "Sample size 1 =";
        powerReport.add(String.format("%20s %4d", sourceString, n_1));
        addNBlankLinesToPowerReport(1);
        sourceString = "Sample size 2 =";
        powerReport.add(String.format("%20s %4d", sourceString, n_2));
        addNBlankLinesToPowerReport(1);
        sourceString = "Assumed Sigma 1 =";
        powerReport.add(String.format("%20s %8.3f", sourceString, nullSigma_1)); 
        addNBlankLinesToPowerReport(1);
        sourceString = "Assumed Sigma 2 =";
        powerReport.add(String.format("%20s %8.3f", sourceString, nullSigma_2)); 
        addNBlankLinesToPowerReport(1);
        sourceString = "Standard error =";
        powerReport.add(String.format("%20s %8.3f", sourceString, standErrDiffMeans));
        addNBlankLinesToPowerReport(1);
        sourceString = "Effect Size =";
        powerReport.add(String.format("%20s %8.3f", sourceString, effectSize));
        
        addNBlankLinesToPowerReport(1);
        sourceString = "Power =";
        powerReport.add(String.format("%20s %8.3f", sourceString, power));
        
        addNBlankLinesToPowerReport(1);
   }    
   
    private void addNBlankLinesToPowerReport(int thisMany) {
        StringUtilities.addNLinesToArrayList(powerReport, thisMany);
    }
    
    public ArrayList<String> getPowerReport() { return powerReport; }
 
    public IndepMeans_Power_Controller getController() { return indepMeans_Power_Controller; }
    
    public void printModelStuff() {
        /*
        System.out.println("\n\n*****************IndepMeansPowerController toString");
        System.out.println("nullDiffInMeans = " + nullDiffInMeans);        
        System.out.println(" altDiffInMeans = " + altDiffInMeans);
        System.out.println(   "      Mean_1 = " + nullMu_1);        
        System.out.println(   "      Mean_2 = " + nullMu_2);
        System.out.println("   sampleSize_1 = " + sampleSize_1);        
        System.out.println("   sampleSize_2 = " + sampleSize_2);
        System.out.println("    nullSigma_1 = " + nullSigma_1);        
        System.out.println("    nullSigma_2 = " + nullSigma_2);
        System.out.println("          alpha = " + alpha);        
        System.out.println("     effectSize = " + effectSize);
        System.out.println("   rejCriterion = " + rejectionCriterion + "***************\n\n\n"); 
        */
    }
}
