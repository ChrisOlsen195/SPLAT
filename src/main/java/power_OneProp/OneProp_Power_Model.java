/**************************************************
 *             OneProp_Power_Model                *
 *                  05/21/26                      *
 *                    18:00                       *
 *************************************************/
package power_OneProp;

import java.util.ArrayList;
import superClasses.*;

public class OneProp_Power_Model extends OneParam_Power_Model {
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;

    OneProp_Power_Controller oneProp_Power_Controller;
    
    public OneProp_Power_Model(OneProp_Power_Controller oneProp_Power_Controller) {
        //super();
        if (printTheStuff) {
            System.out.println("21 *** OneProp_Power_Model, Constructing");
        }
        this.oneProp_Power_Controller = oneProp_Power_Controller;
        str_al_PowerReport = new ArrayList();
        lowerLimit = 0.0; upperLimit = 0.0;//  Happy compiler, happy runs
        nullParam = oneProp_Power_Controller.getNullProp();
        altParam = oneProp_Power_Controller.getAltProp();
        sampleSize = oneProp_Power_Controller.getSampleSize();
        
        if (printTheStuff) {
            //System.out.println( "31 OneProp_Power_Model, nullParam = " +  nullParam);
            //System.out.println( "31 OneProp_Power_Model, altParam = " +  altParam);
            //System.out.println( "31 OneProp_Power_Model, effectSize = " +  effectSize);
            //System.out.println( "31 OneProp_Power_Model, strRejectionCriterion = " +  strRejectionCriterion);
        }
    }
 
    public void archiveNullValues() {
        archivedNullParam = nullParam;
        archivedAltParam = altParam;
        archivedSampleSize = sampleSize;
        archivedAlpha = alpha;
        archivedEffectSize = effectSize;
    }    
    
    public void restoreNullValues() {
        nullParam = archivedNullParam;
        altParam = archivedAltParam ;
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
        str_al_PowerReport.add(String.format("%20s %8.3f", strSourceString, stErr_NullParam));
        addNBlankLinesToPowerReport(1);
        strSourceString = "Effect Size =";
        str_al_PowerReport.add(String.format("%20s %8.3f", strSourceString, effectSize));        
        addNBlankLinesToPowerReport(1);
        strSourceString = "Power =";
        str_al_PowerReport.add(String.format("%20s %8.3f", strSourceString, power));
        addNBlankLinesToPowerReport(1);
   }    
   
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
