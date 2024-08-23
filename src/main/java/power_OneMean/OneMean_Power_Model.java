/**************************************************
 *            OneMean_Power_Model                 *
 *                  05/28/24                      *
 *                    15:00                       *
 *************************************************/
package power_OneMean;

import genericClasses.Point_2D;
import noncentrals.JDistr_Noncentrals.*;
import java.util.ArrayList;
import utilityClasses.*;

public class OneMean_Power_Model {
    // POJOs
    int sampleSize, archivedSampleSize;
    double alpha, altMu, effectSize, nullMu, nullSigma, power, lowerLimit, 
           upperLimit, standErrMean, loCum, hiCum, archivedNullMu, 
           archivedNullSigma, archivedAltMu, archivedAlpha, archivedEffectSize; 

    String strRejectionCriterion, strSourceString, strPrinted_NullHypoth, 
           strPrinted_AltHypoth;        
    ArrayList<String> str_al_PowerReport;
    
    // My classes
    OneMean_Power_Controller oneMean_Power_Controller;
    Point_2D nonRejectionRegion;
    
    public OneMean_Power_Model(OneMean_Power_Controller oneMean_Controller) {
        this.oneMean_Power_Controller = oneMean_Controller;
        //System.out.println("\n30 OneMean_Power_Model, Constructing");
        str_al_PowerReport = new ArrayList();
    }
    
    public void constructNonRejectionRegion() {
        lowerLimit = 0.0; upperLimit = 0.0;//  Happy compiler, happy runs
        standErrMean = nullSigma / Math.sqrt(sampleSize);
        
        switch (strRejectionCriterion) {
            case "LessThan":
                lowerLimit = Normal.quantile(alpha, nullMu, 
                                             standErrMean, true, false);
                upperLimit = Double.POSITIVE_INFINITY; 
                break;
                    
            case "NotEqual":
                lowerLimit = Normal.quantile(alpha / 2., nullMu, 
                                             standErrMean, true, false);
                upperLimit = Normal.quantile(1.0 - alpha / 2., nullMu, 
                                             standErrMean, true, false);  
                break;
                            
            case "GreaterThan":
                lowerLimit = Double.NEGATIVE_INFINITY;
                upperLimit = Normal.quantile(1.0 - alpha, nullMu, 
                                             standErrMean, true, false); 
                break;
            
            default:
                String switchFailure = "Switch failure: OneMean_Power_Model 59 " + strRejectionCriterion;
                MyAlerts.showUnexpectedErrorAlert(switchFailure); 
        }
        nonRejectionRegion = new Point_2D(lowerLimit, upperLimit);
    }
    
    public double calculatePower() {
        constructNonRejectionRegion();
        
        switch(strRejectionCriterion) {
            case "LessThan": 
                loCum = Normal.cumulative(lowerLimit, altMu, standErrMean, true, false);
                power = loCum;
                break;
                
            case "NotEqual":     
                loCum = Normal.cumulative(lowerLimit, altMu, standErrMean, true, false);
                hiCum = 1.0 - Normal.cumulative(upperLimit, altMu, standErrMean, true, false);
                power = loCum + hiCum;
                break;
                
            case "GreaterThan":
                hiCum = 1.0 - Normal.cumulative(upperLimit, altMu, standErrMean, true, false); 
                power = hiCum;
                break;   
                
            default:
                String switchFailure = "Switch failure: OneMean_Power_Model 86 " + strRejectionCriterion;
                MyAlerts.showUnexpectedErrorAlert(switchFailure); 
        }
        return power;        
    }
    
    public int getSampleSize() { return sampleSize; }
    public void setSampleSize(int toThis) {sampleSize = toThis; }
    
    public double getStandErrMean() { return standErrMean; }
    public void setStandErrMean(double toThis) { standErrMean = toThis; }
    
    public double getNullMu() { return nullMu; }
    public void setNullMu(double toThis) { nullMu = toThis; }
    
    public double getAltMu() { return altMu; }
    public void setAltMu(double toThis) { 
        altMu = toThis;
        effectSize = Math.abs(altMu - nullMu);
    }

    public double getNullSigma() {return nullSigma; }
    public void setNullSigma(double toThis) { nullSigma = toThis; }
    
    public double getAlpha() { return alpha; }
    public void setAlpha(double toThis) { alpha = toThis; }
    
    public String getPrintedNullHypothesis() { return strPrinted_NullHypoth;}
    public void setPrintedNullHypothesis(String toThis) {
        strPrinted_NullHypoth = toThis;
    }
    
    public String getPrintedAltHypothesis() { return strPrinted_AltHypoth;}
    public void setPrintedAltHypothesis(String toThis) {
        strPrinted_AltHypoth = toThis;
    }
    
    public double getPower() { return power; }
    
    public String getRejectionCriterion() { return strRejectionCriterion; }
    public void setRejectionCriterion(String toThis) {
        strRejectionCriterion = toThis;
    }
    
    public double getEffectSize() { return effectSize; }
    public void setEffectSize(double toThis) {
        effectSize = toThis;
        if (strRejectionCriterion.equals("LessThan")) {
            altMu = nullMu - effectSize;
        }  else {
            altMu = nullMu + effectSize;   
        }
    }
    
    public Point_2D getNonRejectionRegion() {       
        return nonRejectionRegion; 
    }
 
    public void archiveNullValues() {
        archivedNullMu = nullMu;
        archivedAltMu = altMu;
        archivedSampleSize = sampleSize;
        archivedNullSigma = nullSigma;
        archivedAlpha = alpha;
        archivedEffectSize = effectSize;
    }    
    

    public void restoreNullValues() {
        nullMu = archivedNullMu;
        altMu = archivedAltMu ;
        sampleSize = archivedSampleSize;
        nullSigma = archivedNullSigma;
        alpha  = archivedAlpha; 
        effectSize = archivedEffectSize;
    } 
    
   public void print_Power_Table() {
        addNBlankLinesToPowerReport(2);
        str_al_PowerReport.add("       Power Statistics");
        addNBlankLinesToPowerReport(2);
        str_al_PowerReport.add(String.format("Single Sample Mean"));
        addNBlankLinesToPowerReport(1);
        strSourceString = "       Null hypothesis: " + strPrinted_NullHypoth;
        addNBlankLinesToPowerReport(1);
        str_al_PowerReport.add(String.format("%15s ", strSourceString));
        strSourceString = "Alternative hypothesis: " + strPrinted_AltHypoth;
        addNBlankLinesToPowerReport(1);
        str_al_PowerReport.add(String.format("%15s ", strSourceString));
        addNBlankLinesToPowerReport(1);
        strSourceString = "alpha =";
        str_al_PowerReport.add(String.format("%20s %8.3f", strSourceString, alpha));
        addNBlankLinesToPowerReport(1);
        strSourceString = "Sample size =";
        str_al_PowerReport.add(String.format("%20s    %4d", strSourceString, sampleSize));
        addNBlankLinesToPowerReport(1);
        strSourceString = "Assumed Sigma =";
        str_al_PowerReport.add(String.format("%20s %8.3f", strSourceString,nullSigma)); 
        addNBlankLinesToPowerReport(1);
        strSourceString = "Standard error =";
        str_al_PowerReport.add(String.format("%20s %8.3f", strSourceString,standErrMean));
        addNBlankLinesToPowerReport(1);
        strSourceString = "Effect Size =";
        str_al_PowerReport.add(String.format("%20s %8.3f", strSourceString,effectSize));        
        addNBlankLinesToPowerReport(1);
        strSourceString = "Power =";
        str_al_PowerReport.add(String.format("%20s %8.3f", strSourceString, power));
        addNBlankLinesToPowerReport(1);
   }    
   
    private void addNBlankLinesToPowerReport(int thisMany) {
        StringUtilities.addNLinesToArrayList(str_al_PowerReport, thisMany);
    }

    public ArrayList<String> getPowerReport() { return str_al_PowerReport; }
    public OneMean_Power_Controller getController() { return oneMean_Power_Controller; }
    
    public void printModelStuff() {
        /*
        System.out.println("\n197 oneMean Z Model, printModelStuff()");
        System.out.println(" rejectionCrit = " + rejectionCriterion); 
        System.out.println("    sampleSize = " + sampleSize); 
        System.out.println("  null mu / sd = " + nullMu + " / " + nullSigma);
        System.out.println("        alt mu = " + altMu);  
        System.out.println("         alpha = " + alpha);
        System.out.println("      standErr = " + standErrMean);
        //System.out.println("oneMean Z Model, critValGT/hiCum = " + critValGT + " / " + hiCum );
        System.out.println("     l/u limit = " + lowerLimit + " / " + upperLimit);
        System.out.println("oneMean Z Model, power = " + power);
        */
    }  
}
