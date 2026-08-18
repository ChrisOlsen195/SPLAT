/**************************************************
 *           IndepMeans_Power_Model               *
 *                  05/26/26                      *
 *                    12:00                       *
 *************************************************/
package power_twomeans;

import genericClasses.Point_2D;
import noncentrals.JDistr_Noncentrals.*;
import java.util.ArrayList;
import utilityClasses.*;

public class IndepMeans_Power_Model {
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    int sampleSize_1, sampleSize_2, archived_n_1, archived_n_2;
    
    double alpha, altDiffInMeans, nullDiffInMeans, effectSize, 
           stErrDiffInMeans, power, /*nullMean_1, nullMean_2,*/ sigma_1, 
           sigma_2, lowerLimit, upperLimit, loCum, hiCum, temp_var_1, temp_var_2, 
           archivedNullMeanDiff, archivedAltMeanDiff, archivedAlpha, 
           archivedEffectSize; 

    Point_2D nonRejectionRegion;
    String rejectionCriterion, sourceString, printedNullHypoth, printedAltHypoth;    
    
    ArrayList<String> powerReport;
    
    IndepMeans_Power_Controller indepMeans_Power_Controller;
    
    public IndepMeans_Power_Model(IndepMeans_Power_Controller iMPC) {
        if (printTheStuff) {
            System.out.println("35 --- IndepMeans_Power_Model, Constructing");
        }
        this.indepMeans_Power_Controller = iMPC;
        powerReport = new ArrayList();
    }
    
    public double calculatePower() {
        if (printTheStuff) {
            System.out.println("43 --- IndepMeans_Power_Model, calculatePower()");
        }
        temp_var_1 = sigma_1 * sigma_1;
        temp_var_2 = sigma_2 * sigma_2;
        sampleSize_1 = getSampleSize_1();
        sampleSize_2 = getSampleSize_2();
        stErrDiffInMeans = Math.sqrt(temp_var_1 / sampleSize_1 + temp_var_2 / sampleSize_2);
        if (printTheStuff) {
            System.out.println("\n451 --- IndepMeans_Power_Model, calculatePower()");
            System.out.println("52 --- Sample sizes, n1, n2 = " + sampleSize_1 + ", " + sampleSize_2);
            //System.out.println("53 --- Sample sizes, sigma 1, 2 = " + sigma_1 + ", " + sigma_2);
            //System.out.println("54 --- stErrDiffInParams = " + stErrDiffInMeans);
            //System.out.println("55 --- altDiffInMeans = " + altDiffInMeans);
        }
        constructNonRejectionRegion();
        switch(rejectionCriterion) {
            case "LessThan": 
                loCum = Normal.cumulative(lowerLimit, altDiffInMeans, stErrDiffInMeans, true, false);
                power = loCum;
                break;
                
            case "NotEqual":     
                loCum = Normal.cumulative(lowerLimit, altDiffInMeans, stErrDiffInMeans, true, false);
                hiCum = 1.0 - Normal.cumulative(upperLimit, altDiffInMeans, stErrDiffInMeans, true, false);
                power = loCum + hiCum;
            if (printTheStuff) {
                System.out.println("69 --- Lower Limit, n1, n2 = " + lowerLimit + ", " + upperLimit);
                System.out.println("70 --- altDiffInMeans = " + altDiffInMeans);
                System.out.println("71 --- stErrDiffInMeans = " + stErrDiffInMeans);
                System.out.println("72 --- loCum = " + loCum);
                System.out.println("73 --- hiCum = " + hiCum);
                System.out.println("74 --- power = " + power);

            }
                break;
                
            case "GreaterThan":
                hiCum = 1.0 - Normal.cumulative(upperLimit, altDiffInMeans, stErrDiffInMeans, true, false); 
                power = hiCum;
                break;   
                
            default:
                String switchFailure = "Switch failure: IndepMeans_Power_Model 85, " + rejectionCriterion;
                MyAlerts.showUnexpectedErrorAlert(switchFailure);
        }
        return power;        
    }
    
    public void constructNonRejectionRegion() {
        lowerLimit = 0.0; upperLimit = 0.0;//  Happy compiler, happy runs
        
        switch (rejectionCriterion) {
            case "LessThan":
                lowerLimit = Normal.quantile(alpha, nullDiffInMeans, 
                                             stErrDiffInMeans, true, false);
                upperLimit = Double.POSITIVE_INFINITY; 
            break;
                    
            case "NotEqual":
                lowerLimit = Normal.quantile(alpha / 2., nullDiffInMeans, 
                                             stErrDiffInMeans, true, false);
                upperLimit = Normal.quantile(1.0 - alpha / 2., nullDiffInMeans, 
                                             stErrDiffInMeans, true, false); 
            break;
                            
            case "GreaterThan":
                lowerLimit = Double.NEGATIVE_INFINITY;
                upperLimit = Normal.quantile(1.0 - alpha, nullDiffInMeans, 
                                             stErrDiffInMeans, true, false); 
            break;
            
            default:
                String switchFailure = "Switch failure: IndepMeans_Power_Model 115 " + rejectionCriterion;
                MyAlerts.showUnexpectedErrorAlert(switchFailure);
        }

        nonRejectionRegion = new Point_2D(lowerLimit, upperLimit);
    }
    
    public int getSampleSize_1() { 
       if (printTheStuff) {
            System.out.println("124 --- IndepMeans_Power_Model, Getting SampleSize_1 = " + sampleSize_1);
        }
        return sampleSize_1; 
    }
    
    public void setSampleSize_1(int toThis) { 
    if (printTheStuff) {
        System.out.println("131 --- IndepMeans_Power_Model, Setting SampleSize_1 to " + toThis);
    }
        sampleSize_1 = toThis; 
        //dbl_sampleSize_1 = n_1;
    }
    
    public int getSampleSize_2() { 
        if (printTheStuff) {
            System.out.println("139 --- IndepMeans_Power_Model, Getting SampleSize_2 = " + sampleSize_2);
        }    
        return sampleSize_2; 
    }
    public void setSampleSize_2(int toThis) { 
    if (printTheStuff) {
        System.out.println("145 --- IndepMeans_Power_Model, Setting SampleSize_2 to " + toThis);
    }
        sampleSize_2 = toThis; 
    }
     
    public double getSigma_1() {return sigma_1; }
    public void setSigma_1(double toThis) { sigma_1 = toThis; }
    
    public double getSigma_2() {return sigma_2; }
    public void setSigma_2(double toThis) { sigma_2 = toThis; }
    
    public double getNullDiffInMeans() { return nullDiffInMeans; }
    public void setNullDiffInMeans(double toThis) {  nullDiffInMeans = toThis; }
    
    public double getAltDiffInMeans() { return altDiffInMeans; }
    public void setAltDiffInMeans(double toThis) { 
        altDiffInMeans = toThis;
        effectSize = Math.abs(altDiffInMeans - nullDiffInMeans);
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
    
    public double getStErrDiffInMeans() {return stErrDiffInMeans; }
    public void setStErrDiffInMeans(double toThis) {
        stErrDiffInMeans = toThis; 
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
            altDiffInMeans = nullDiffInMeans - effectSize;
        }  else {
            altDiffInMeans = nullDiffInMeans + effectSize;   
        }
    }
    
    public Point_2D getNonRejectionRegion() { return nonRejectionRegion; }
 
    public void archiveNullValues() {
        archivedNullMeanDiff = nullDiffInMeans;
        archivedAltMeanDiff = altDiffInMeans;
        archived_n_1 = sampleSize_1;
        archived_n_2 = sampleSize_2;
        archivedAlpha = alpha;
        archivedEffectSize = effectSize;
    }    
    

    public void restoreNullValues() {
        nullDiffInMeans = archivedNullMeanDiff;
        altDiffInMeans = archivedAltMeanDiff ;
        sampleSize_1 = archived_n_1;
        sampleSize_2 = archived_n_2;
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
        powerReport.add(String.format("%20s %4d", sourceString, sampleSize_1));
        addNBlankLinesToPowerReport(1);
        sourceString = "Sample size 2 =";
        powerReport.add(String.format("%20s %4d", sourceString, sampleSize_2));
        addNBlankLinesToPowerReport(1);
        sourceString = "Assumed Sigma 1 =";
        powerReport.add(String.format("%20s %8.3f", sourceString, sigma_1)); 
        addNBlankLinesToPowerReport(1);
        sourceString = "Assumed Sigma 2 =";
        powerReport.add(String.format("%20s %8.3f", sourceString, sigma_2)); 
        addNBlankLinesToPowerReport(1);
        sourceString = "Standard error =";
        powerReport.add(String.format("%20s %8.3f", sourceString, stErrDiffInMeans));
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
    
    //double getArchivedNullMu() { return archivedNullMeanDiff; }
    //double getArchivedAltMu() { return archivedAltMeanDiff; }
    //int getArchivedSampleSize() { return archived_n_1; }
    //double getArchivedNullSigma() { return archivedNullSigma_1; }
    //double getArchivedAlpha() { return archivedAlpha; }
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
