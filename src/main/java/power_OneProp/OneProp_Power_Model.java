/**************************************************
 *             OneProp_Power_Model                *
 *                  05/30/24                      *
 *                    00:00                       *
 *************************************************/
package power_OneProp;

import genericClasses.Point_2D;
import noncentrals.JDistr_Noncentrals.*;
import java.util.ArrayList;
import utilityClasses.*;

public class OneProp_Power_Model {
    // POJOs
    int sampleSize, archivedSampleSize;
    double alpha, altProp, effectSize, standErr_PNull, standErr_PAlt, 
           nullProp, power, lowerLimit, upperLimit, loCum, hiCum, 
           archivedNullProp, archivedAltProp, archivedAlpha, archivedEffectSize; 

    Point_2D nonRejectionRegion;
    String strRejectionCriterion, strSourceString, strPrinted_NullHypoth, strPrinted_AltHypoth;    
    
    ArrayList<String> str_al_PowerReport;
    
    OneProp_Power_Controller oneProp_Power_Controller;
    
    public OneProp_Power_Model(OneProp_Power_Controller oneProp_Power_Controller) {
        this.oneProp_Power_Controller = oneProp_Power_Controller;
        str_al_PowerReport = new ArrayList();
        lowerLimit = 0.0; upperLimit = 0.0;//  Happy compiler, happy runs
        nullProp = oneProp_Power_Controller.getNullProp();
        altProp = oneProp_Power_Controller.getAltProp();
        sampleSize = oneProp_Power_Controller.getSampleSize();
        standErr_PNull = Math.sqrt(nullProp * (1.0 - nullProp) / sampleSize);
        standErr_PAlt = Math.sqrt(altProp * (1.0 - altProp) / sampleSize);
    }
    
    public void constructNonRejectionRegion() {
        lowerLimit = 0.0; upperLimit = 0.0;
        standErr_PNull = Math.sqrt(nullProp * (1.0 - nullProp) / sampleSize);
        standErr_PAlt = Math.sqrt(altProp * (1.0 - altProp) / sampleSize);
        
        switch (strRejectionCriterion) {
            case "LessThan":
                lowerLimit = Normal.quantile(alpha, nullProp, 
                                             standErr_PNull, true, false);
                upperLimit = Double.POSITIVE_INFINITY; 
                break;
                    
            case "NotEqual":
                lowerLimit = Normal.quantile(alpha / 2., nullProp, 
                                             standErr_PNull, true, false);
                upperLimit = Normal.quantile(1.0 - alpha / 2., nullProp, 
                                               standErr_PNull, true, false);  
                break;
                            
            case "GreaterThan":
                lowerLimit = Double.NEGATIVE_INFINITY;
                upperLimit = Normal.quantile(1.0 - alpha, nullProp, 
                                             standErr_PNull, true, false); 
                break;
            
            default:
                String switchFailure = "Switch failure: OneProp_Power_Model 64 " + strRejectionCriterion;
                MyAlerts.showUnexpectedErrorAlert(switchFailure); 
        }
        nonRejectionRegion = new Point_2D(lowerLimit, upperLimit);
    }
    
    public double calculatePower() {
        constructNonRejectionRegion();
        switch(strRejectionCriterion) {
            case "LessThan": 
                loCum = Normal.cumulative(lowerLimit, altProp, standErr_PAlt, true, false);
                power = loCum;
                break;
                
            case "NotEqual":     
                loCum = Normal.cumulative(lowerLimit, altProp, standErr_PAlt, true, false);
                hiCum = 1.0 - Normal.cumulative(upperLimit, altProp, standErr_PAlt, true, false);
                power = loCum + hiCum;
                break;
                
            case "GreaterThan":
                hiCum = 1.0 - Normal.cumulative(upperLimit, altProp, standErr_PAlt, true, false); 
                power = hiCum;
                break;   
                
            default:
                String switchFailure = "Switch failure: OneProp_Power_Model 90 " + strRejectionCriterion;
                MyAlerts.showUnexpectedErrorAlert(switchFailure); 
        }

        return power;        
    }
    
    public int getSampleSize() { return sampleSize; }
    public void setSampleSize(int toThis) { sampleSize = toThis; }
    
    public double getStErr_PNull() {return standErr_PNull; }
    public void setStErr_PNull(double toThis) {standErr_PNull = toThis; }
    
    public double getStandErr_PAlt() { return standErr_PAlt; }
    public void setStandErr_PAlt(double toThis) { standErr_PAlt = toThis; }
    
    public double getNullProp() { return nullProp; }
    public void setNullProp(double toThis) { nullProp = toThis; }
    
    public double getAltProp() { return altProp; }
    public void setAltProp(double toThis) { 
        altProp = toThis;
        standErr_PAlt = Math.sqrt(altProp * (1.0 - altProp) / sampleSize);
        effectSize = Math.abs(altProp - nullProp);
    }
 
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
            altProp = nullProp - effectSize;
        }  else {
            altProp = nullProp + effectSize;   
        }
    }
    
    public Point_2D getNonRejectionRegion() { return nonRejectionRegion; }
 
    public void archiveNullValues() {
        archivedNullProp = nullProp;
        archivedAltProp = altProp;
        archivedSampleSize = sampleSize;
        archivedAlpha = alpha;
        archivedEffectSize = effectSize;
    }    
    
    public void restoreNullValues() {
        nullProp = archivedNullProp;
        altProp = archivedAltProp ;
        sampleSize = archivedSampleSize;
        alpha  = archivedAlpha; 
        effectSize = archivedEffectSize;
    } 
    
   public void print_Power_Table() {
        addNBlankLinesToPowerReport(2);
        str_al_PowerReport.add("       Power Statistics");
        addNBlankLinesToPowerReport(2);
        str_al_PowerReport.add(String.format("Single Sample Prop"));
        addNBlankLinesToPowerReport(1);
        strSourceString = "       Null hypothesis: " + strPrinted_NullHypoth;
        addNBlankLinesToPowerReport(1);
        str_al_PowerReport.add(String.format("%15s ", strSourceString));
        strSourceString = "Alternative hypothesis: " + strPrinted_AltHypoth;
        addNBlankLinesToPowerReport(1);
        str_al_PowerReport.add(String.format("%15s ", strSourceString));
        addNBlankLinesToPowerReport(1);
        strSourceString = "Sample size =";
        str_al_PowerReport.add(String.format("%20s %4d", strSourceString, sampleSize));
        addNBlankLinesToPowerReport(1);
        strSourceString = "Standard error =";
        str_al_PowerReport.add(String.format("%20s %8.3f", strSourceString,standErr_PNull));
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

    public OneProp_Power_Controller getController() { return oneProp_Power_Controller; }
    
    public void printModelStuff() {
        /*
        System.out.println("\n197 oneMean Z Model, printModelStuff()");
        System.out.println(" rejectionCrit = " + rejectionCriterion); 
        System.out.println("    sampleSize = " + sampleSize); 
        System.out.println("  null mu / sd = " + nullProp + " / " + nullSigma);
        System.out.println("        alt mu = " + altProp);  
        System.out.println("         alpha = " + alpha);
        System.out.println("      standErr = " + standErrPHat);
        //System.out.println("oneMean Z Model, critValGT/hiCum = " + critValGT + " / " + hiCum );
        System.out.println("     l/u limit = " + lowerLimit + " / " + upperLimit);
        System.out.println("oneMean Z Model, power = " + power);
        */
    }    
}
