/**************************************************
 *            OneParam_Power_Model                *
 *                  05/26/26                      *
 *                    12:00                       *
 *************************************************/
package superClasses;

import genericClasses.Point_2D;
import java.util.ArrayList;
import noncentrals.JDistr_Noncentrals.Normal;
import utilityClasses.MyAlerts;
import utilityClasses.StringUtilities;

public class OneParam_Power_Model {
    
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    public int sampleSize, archivedSampleSize;
    
    public double alpha, altParam, effectSize, nullParam, nullSigma, power, lowerLimit, 
           upperLimit, stErr_NullParam, loCum, hiCum, archivedNullParam, 
           archivedNullSigma, archivedAltParam, archivedAlpha, 
           archivedEffectSize, stErr_AltParam; 

    public String strRejectionCriterion, strSourceString, strPrinted_NullHypoth, 
           strPrinted_AltHypoth;     
    
    public ArrayList<String> str_al_PowerReport;
    
    public Point_2D nonRejectionRegion;
    
    public OneParam_Power_Model() {
        if (printTheStuff) {
            System.out.println("36 *** OneParam_Power_Model, Constructing");
        }    
    }
    
    public void constructNonRejectionRegion() {
        if (printTheStuff) {
            System.out.println("42 --- OneParam_Power_Model, constructNonRejectionRegion()");
        }
        lowerLimit = 0.0; upperLimit = 0.0;//  Happy compiler, happy runs
     
        switch (strRejectionCriterion) {
            case "LessThan":
                lowerLimit = Normal.quantile(alpha, nullParam, 
                                             stErr_NullParam, true, false);
                upperLimit = Double.POSITIVE_INFINITY; 
                break;
                    
            case "NotEqual":
                if (printTheStuff) {
                    System.out.println("55 ... OneParam_Power_Model, nullParam = " + nullParam);
                    System.out.println("56 ... OneParam_Power_Model, stErr_NullParam = " + stErr_NullParam);
                }
                lowerLimit = Normal.quantile(alpha / 2., nullParam, 
                                             stErr_NullParam, true, false);
                upperLimit = Normal.quantile(1.0 - alpha / 2., nullParam, 
                                             stErr_NullParam, true, false);  
                break;
                            
            case "GreaterThan":
                lowerLimit = Double.NEGATIVE_INFINITY;
                upperLimit = Normal.quantile(1.0 - alpha, nullParam, 
                                             stErr_NullParam, true, false); 
                break;
            
            default:
                String switchFailure = "Switch failure: OneParam_Power_Model 71 " + strRejectionCriterion;
                MyAlerts.showUnexpectedErrorAlert(switchFailure); 
        }
        nonRejectionRegion = new Point_2D(lowerLimit, upperLimit);
    }
    
    public double calculatePower() {
        if (printTheStuff) {
            System.out.println("79 --- OneParam_Power_Model, calculatePower()");
        }
        constructNonRejectionRegion();
        
        switch(strRejectionCriterion) {
            case "LessThan": 
                loCum = Normal.cumulative(lowerLimit, altParam, stErr_AltParam, true, false);
                power = loCum;
                break;
                
            case "NotEqual":    
                if (printTheStuff) {
                    System.out.println("91 ... OneParam_Power_Model, altParam = " + altParam);
                    System.out.println("92 ... OneParam_Power_Model, stErr_AltParam = " + stErr_AltParam);
                }
                loCum = Normal.cumulative(lowerLimit, altParam, stErr_AltParam, true, false);
                hiCum = 1.0 - Normal.cumulative(upperLimit, altParam, stErr_AltParam, true, false);
                power = loCum + hiCum;
            if (printTheStuff) {
                System.out.println("98 --- Lower Limit, n1, n2 = " + lowerLimit + ", " + upperLimit);
                System.out.println("99 --- altParam = " + altParam);
                System.out.println("100 --- stErr_AltParam = " + stErr_AltParam);
                System.out.println("101 --- loCum = " + loCum);
                System.out.println("102 --- hiCum = " + hiCum);
                System.out.println("103 --- power = " + power);

            }
                break;
                
            case "GreaterThan":
                hiCum = 1.0 - Normal.cumulative(upperLimit, altParam, stErr_AltParam, true, false); 
                power = hiCum;
                break;   
                
            default:
                String switchFailure = "Switch failure: OneParam_Power_Model 114 " + strRejectionCriterion;
                MyAlerts.showUnexpectedErrorAlert(switchFailure); 
        }
        return power;        
    }
    
    public int getSampleSize() { return sampleSize; }
    public void setSampleSize(int toThis) { sampleSize = toThis; }
    
    public double getStErr_NullParam() {return stErr_NullParam; }
    public void setStErr_NullParam(double toThis) {
        stErr_NullParam = toThis;    
    }
    
    public double getStErr_AltParam() {return stErr_AltParam; }
    public void setStErr_AltParam(double toThis) {
        stErr_AltParam = toThis; 
    }
    
    public double getNullParam() { return nullParam; }
    public void setNullParam(double toThis) { nullParam = toThis; }
    
    public double getAltParam() { return altParam; }
    public void setAltParam(double toThis) { altParam = toThis; }
    
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
            altParam = nullParam - effectSize;
        }  else {
            altParam = nullParam + effectSize;   
        }
    }
    
   public Point_2D getNonRejectionRegion() { return nonRejectionRegion; }
   
  public void addNBlankLinesToPowerReport(int thisMany) {
        StringUtilities.addNLinesToArrayList(str_al_PowerReport, thisMany);
    }
    
    public ArrayList<String> getPowerReport() { return str_al_PowerReport; }
}
