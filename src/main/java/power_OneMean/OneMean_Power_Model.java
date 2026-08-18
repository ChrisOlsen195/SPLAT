/**************************************************
 *            OneMean_Power_Model                 *
 *                  05/23/26                      *
 *                    18:00                       *
 *************************************************/
package power_OneMean;

import java.util.ArrayList;
import superClasses.*;

public class OneMean_Power_Model extends OneParam_Power_Model {
    // POJOs
    
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    // My classes
    OneMean_Power_Controller oneMean_Power_Controller;

    
    public OneMean_Power_Model(OneMean_Power_Controller oneMean_Power_Controller) {
        super();
        this.oneMean_Power_Controller = oneMean_Power_Controller;
        if (printTheStuff) {
            System.out.println("25 *** OneMean_Power_Model, Constructing");
        }
        str_al_PowerReport = new ArrayList();
    }
    
    //public int getSampleSize() { return oneMean_Power_Controller.g
    
    public void setNullParam(double toThis) { 
        nullParam = toThis;
        if (printTheStuff) {
            System.out.println("33 *** OneMean_Power_Model, setNullMean to " + nullParam);
        }
    }

    public double getAltParam() { return altParam; }
    public void setAltParam(double toThis) { 
        altParam = toThis;
        effectSize = Math.abs(altParam - nullParam);
        if (printTheStuff) {
            System.out.println("42 ... OneMean_Power_Model, setAltMean to " + altParam);
            System.out.println("43 ... OneMean_Power_Model, effectSize = " + effectSize);
        }
    }
    
    public double getNullSigma() {return nullSigma; }
    public void setNullSigma(double toThis) { 
        nullSigma = toThis; 
       if (printTheStuff) {
            System.out.println("51 ... OneMean_Power_Model, setNullSigma to " + nullSigma);
        }
    }

    
    public void archiveNullValues() {
        archivedNullParam = nullParam;
        archivedAltParam = altParam;
        archivedSampleSize = sampleSize;
        archivedNullSigma = nullSigma;
        archivedAlpha = alpha;
        archivedEffectSize = effectSize;
    }    
    

    public void restoreNullValues() {
        nullParam = archivedNullParam;
        altParam = archivedAltParam ;
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
        str_al_PowerReport.add(String.format("%20s %8.3f", strSourceString,stErr_NullParam));
        addNBlankLinesToPowerReport(1);
        strSourceString = "Effect Size =";
        str_al_PowerReport.add(String.format("%20s %8.3f", strSourceString,effectSize));        
        addNBlankLinesToPowerReport(1);
        strSourceString = "Power =";
        str_al_PowerReport.add(String.format("%20s %8.3f", strSourceString, power));
        addNBlankLinesToPowerReport(1);
   }    
   
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
