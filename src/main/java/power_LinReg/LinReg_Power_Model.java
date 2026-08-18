/**************************************************
 *            LinReg_Power_Model                  *
 *                  06/05/26                      *
 *                    15:00                       *
 *************************************************/
package power_LinReg;

import genericClasses.Point_2D;
import java.util.ArrayList;
import probabilityDistributions.TDistribution;
import utilityClasses.MyAlerts;
import utilityClasses.StringUtilities;

public class LinReg_Power_Model {
    // POJOs
    
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    public int sampleSize, maxSampleSize, archivedSampleSize;
    
    public double alpha, altBeta, effectSize, nullBeta, nullSigma, power, lowerLimit, 
           upperLimit, stErr_NullBeta, loCum, hiCum, archivedNullParam, 
           archivedNullSigma, archivedAltParam, archivedAlpha, 
           archivedEffectSize, stErr_AltBeta; 
    
    public double nullT_lower, nullT_upper, altT_lower, altT_upper,
           msResid, sigmaX;
    

    public String strRejectionCriterion, strSourceString, strPrinted_NullHypoth, 
           strPrinted_AltHypoth;     
    
    public ArrayList<String> str_al_PowerReport;
    
    public Point_2D nonRejectionRegion;
    
    // My classes
    LinReg_Power_Controller linReg_Power_Controller;
    TDistribution null_tDist, alt_tDist;
    
    public LinReg_Power_Model(LinReg_Power_Controller linReg_Power_Controller) {
        this.linReg_Power_Controller = linReg_Power_Controller;
        if (printTheStuff) {
            System.out.println("45 *** LinReg_Power_Model, Constructing");
        }
        str_al_PowerReport = new ArrayList();
        msResid = linReg_Power_Controller.getMSResid();
        sigmaX = linReg_Power_Controller.getSigmaX();
        maxSampleSize = linReg_Power_Controller.getMaxSampleSize();
    }
    
    public void constructNonRejectionRegion() {
        if (printTheStuff) {
            System.out.println("55 --- LinReg_Power_Model, constructNonRejectionRegion()");
        }
        
        null_tDist = new TDistribution(sampleSize - 2);
        alt_tDist = new TDistribution(sampleSize - 2);
        
        lowerLimit = 0.0; upperLimit = 0.0;//  Happy compiler, happy runs
     
        switch (strRejectionCriterion) {
            case "LessThan":
                nullT_lower = null_tDist.getInvLeftTailArea(alpha);
                lowerLimit = stErr_NullBeta * nullT_lower;
                upperLimit = Double.POSITIVE_INFINITY; 
                if (printTheStuff) {
                    //System.out.println("69 ... LinReg_Power_Model, lowerLimit = " + lowerLimit);   
                    //System.out.println("70 ... LinReg_Power_Model, lowerT = " + nullT_lower);
                }
                break;
                    
            case "NotEqual":
                nullT_lower = null_tDist.getInvLeftTailArea(alpha / 2.0);
                lowerLimit = stErr_NullBeta * nullT_lower;
                nullT_upper = null_tDist.getInvLeftTailArea(1.0 - alpha / 2.);
                upperLimit = stErr_NullBeta * nullT_upper;
                if (printTheStuff) {
                    //System.out.println("80 ... LinReg_Power_Model, lower/upperLimit = " + lowerLimit + " / " + upperLimit);   
                    //System.out.println("81 ... LinReg_Power_Model, lower/upperT = " + nullT_lower + " / " + nullT_upper);
                }
                break;
                            
            case "GreaterThan":
                lowerLimit = Double.NEGATIVE_INFINITY;
                nullT_upper = null_tDist.getInvLeftTailArea(1.0 - alpha);
                upperLimit = stErr_NullBeta * nullT_upper; 
                if (printTheStuff) {
                    //System.out.println("90 ... LinReg_Power_Model, upperLimit = " + upperLimit);   
                    //System.out.println("91 ... LinReg_Power_Model, theT_upper = " + nullT_upper);
                }                
                break;
            
            default:
                String switchFailure = "Switch failure: OneParam_Power_Model 96 " + strRejectionCriterion;
                MyAlerts.showUnexpectedErrorAlert(switchFailure); 
        }
        
        if (printTheStuff) {
            //System.out.println("101 ... LinReg_Power_Model, lower/upper limit: " + lowerLimit + ", " + upperLimit);
        }
        
        nonRejectionRegion = new Point_2D(lowerLimit, upperLimit);
    }
    
    public double calculatePower() {
        if (printTheStuff) {
            System.out.println("109 --- LinReg_Power_Model, calculatePower()");
        }
        constructNonRejectionRegion();
        
        switch(strRejectionCriterion) {
            case "LessThan": 
                //           (lowerLimit - -effectSize) / stErr_NullBeta; 
                altT_lower = (lowerLimit + effectSize) / stErr_NullBeta; 
                loCum = alt_tDist.getLeftTailArea(altT_lower);
                hiCum = 0.0;
                power = loCum;
                break;
                
            case "NotEqual":    
                altT_lower = (lowerLimit - effectSize) / stErr_NullBeta;
                loCum = alt_tDist.getLeftTailArea(altT_lower);
                altT_upper = (upperLimit - effectSize) / stErr_NullBeta;
                hiCum = 1.0 - alt_tDist.getLeftTailArea(altT_upper);
                power = loCum + hiCum;
                break;
                
            case "GreaterThan":
                altT_upper = (upperLimit - effectSize) / stErr_NullBeta;
                hiCum = 1.0 - alt_tDist.getLeftTailArea(altT_upper);
                loCum = 0.0;
                power = hiCum;
                break;   
                
            default:
                String switchFailure = "Switch failure: OneParam_Power_Model 138 " + strRejectionCriterion;
                MyAlerts.showUnexpectedErrorAlert(switchFailure); 
        }
            /*
            if (printTheStuff) {
                System.out.println("\n\n143 ... LinReg_Power_Model, lower/upperLimit = " + lowerLimit + " / " + upperLimit);   
                System.out.println("144 ... LinReg_Power_Model, Null lower/upperT = " + nullT_lower + " / " + nullT_upper);
                System.out.println("145 ... LinReg_Power_Model, Alt lower/upperT = " + altT_lower + " / " + altT_upper);
                System.out.println("146 ... altParam = " + altBeta);
                System.out.println("147 ... stErr_NullBeta = " +  stErr_NullBeta);
                System.out.println("148 ... loCum = " + loCum);
                System.out.println("149 ... hiCum = " + hiCum);
                System.out.println("150 ... power = " + power);
                System.out.println("\n\n");
            }
            */
        return power;        
    }
    
    public double getNullBeta() { return nullBeta; }
    public void setNullBeta(double toThis) { 
        nullBeta = toThis;
        effectSize = Math.abs(altBeta - nullBeta);
    }
    public double getAltBeta() { return altBeta; }
    public void setAltBeta(double toThis) { 
        if (printTheStuff) {
            //System.out.println("165 --- LinReg_Power_Model, setting altBeta = " + toThis);
        }
        altBeta = toThis;
        effectSize = Math.abs(altBeta - nullBeta);
    }
    
    public int getMaxSampleSize() { return maxSampleSize; }

    public int getSampleSize() { return sampleSize; }
    public void setSampleSize(int toThis) { 
        if (printTheStuff) {
            System.out.println("176 --- LinReg_Power_Model, setting SampleSize = " + toThis);
        }
        sampleSize = toThis; 
    }
    
    public double getStErr_Beta() {return stErr_NullBeta; }
    public void setStErr_Beta(double toThis) {
        if (printTheStuff) {
            System.out.println("184 --- LinReg_Power_Model, setting stErr_Beta = " + toThis);
        }        
        stErr_NullBeta = toThis;    
    }
    
    public double getStErr_AltBeta() {return stErr_AltBeta; }
    public void setStErr_AltBeta(double toThis) {
        if (printTheStuff) {
            //System.out.println("192 --- LinReg_Power_Model, setting stErr_Alt = " + toThis);
        } 
        stErr_AltBeta = toThis; 
    }

    public double getAlpha() { return alpha; }
    public void setAlpha(double toThis) { 
        if (printTheStuff) {
            System.out.println("200 --- LinReg_Power_Model, setting alpha = " + toThis);
        }         
        alpha = toThis; 
    }
    
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
        if (printTheStuff) {
            System.out.println("226 --- LinReg_Power_Model, setting effect size = " + toThis);
        }         
        
        effectSize = toThis;
        if (strRejectionCriterion.equals("LessThan")) {
            altBeta = nullBeta - effectSize;
        }  else {
            altBeta = nullBeta + effectSize;   
        }
    }
    
    public double getMSResid() { return msResid; }
    public void setMSResid(double toThis) {
       if (printTheStuff) {
            System.out.println("240 --- LinReg_Power_Model, setting msResid = " + msResid);
        }
        msResid = toThis;
    }
    
    public double getSigmaX() { return sigmaX; }
    public void setSigmaX(double toThis) {
       if (printTheStuff) {
            System.out.println("248 --- LinReg_Power_Model, setting sigmaX = " + sigmaX);
        }
        sigmaX = toThis;
    }
    
   public Point_2D getNonRejectionRegion() { return nonRejectionRegion; }
   
  public void addNBlankLinesToPowerReport(int thisMany) {
        StringUtilities.addNLinesToArrayList(str_al_PowerReport, thisMany);
    }
    
    public ArrayList<String> getPowerReport() { return str_al_PowerReport; }
    
    public double get_df() {
        int df = sampleSize - 2;
        return df;
    };
 
    public void archiveNullValues() {
        archivedNullParam = nullBeta;
        archivedAltParam = altBeta;
        archivedSampleSize = sampleSize;
        archivedNullSigma = nullSigma;
        archivedAlpha = alpha;
        archivedEffectSize = effectSize;
    }    
    

    public void restoreNullValues() {
        nullBeta = archivedNullParam;
        altBeta = archivedAltParam ;
        sampleSize = archivedSampleSize;
        nullSigma = archivedNullSigma;
        alpha  = archivedAlpha; 
        effectSize = archivedEffectSize;
    } 
    
   public void print_Power_Table() {
        addNBlankLinesToPowerReport(2);
        str_al_PowerReport.add("       Power Statistics");
        addNBlankLinesToPowerReport(2);
        str_al_PowerReport.add(String.format("      Model Utility Test"));
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
        strSourceString = "Min Effect Size =";
        str_al_PowerReport.add(String.format("%20s %8.3f", strSourceString,effectSize));        
        addNBlankLinesToPowerReport(1);
        strSourceString = "Power =";
        str_al_PowerReport.add(String.format("%20s %8.3f", strSourceString, power));
        addNBlankLinesToPowerReport(1);
   }    
   
    public LinReg_Power_Controller getController() { return linReg_Power_Controller; }
    
    public void printModelStuff() {
        /*
        System.out.println("\n316 oneMean Z Model, printModelStuff()");
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
