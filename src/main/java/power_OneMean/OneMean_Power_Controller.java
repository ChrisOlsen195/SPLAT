/**************************************************
 *          OneMean_Power_Controller              *
 *                  01/15/25                      *
 *                    21:00                       *
 *************************************************/
package power_OneMean;

import dialogs.power.*;
import genericClasses.Point_2D;
import utilityClasses.MyAlerts;

public class OneMean_Power_Controller {
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    int sampleSize;
    double alpha, nullMu, altMu, nullSigma, standErrMean, effectSize;

    String strRejectionCriterion, strPrinted_Null, strPrinted_Alt, 
           strReturnStatus;
    
    // My classes
    OneMean_Power_Dashboard oneMeanPower_Dashboard;
    OneMean_Power_Model oneMeanPower_Model;
    Point_2D nonRejectionRegion;
    Power_SingleMean_Dialog power_SingleMean_Dialog;

    public OneMean_Power_Controller() {
        if (printTheStuff == true) {
            System.out.println("31 *** OneMean_Power_Controller, constructing");
        }
        power_SingleMean_Dialog = new Power_SingleMean_Dialog();
    }
    
    public String ShowNWait() {
        power_SingleMean_Dialog.showAndWait();
        strReturnStatus = power_SingleMean_Dialog.getStrReturnStatus();
        
        if (strReturnStatus.equals("Cancel")) { return strReturnStatus; }
        
        nullMu = power_SingleMean_Dialog.getNullMean();
        nullSigma = power_SingleMean_Dialog.getSigma();
        alpha = power_SingleMean_Dialog.getAlpha();
        effectSize = power_SingleMean_Dialog.getEffectSize();
        sampleSize = power_SingleMean_Dialog.getSampleSize();
        strRejectionCriterion = power_SingleMean_Dialog.getRejectionCriterion();        
        standErrMean = nullSigma / Math.sqrt(sampleSize);
        oneMeanPower_Model = new OneMean_Power_Model(this);
        oneMeanPower_Model.setNullMu(nullMu);
        
        switch (strRejectionCriterion) {
            case "LessThan":
                altMu = nullMu - effectSize;
                break;
                
            case "NotEqual":
                altMu = nullMu + effectSize;
                break;
                
            case "GreaterThan":
                altMu = nullMu + effectSize;
                break;
                
            default:
                String switchFailure = "Switch failure: OneMean_Power_Controller 61 " + strRejectionCriterion;
                MyAlerts.showUnexpectedErrorAlert(switchFailure);                 
        }
        
        oneMeanPower_Model.setRejectionCriterion(strRejectionCriterion);
        oneMeanPower_Model.setAltMu(altMu);
        oneMeanPower_Model.setSampleSize(sampleSize);
        oneMeanPower_Model.setNullSigma(nullSigma);
        oneMeanPower_Model.setAlpha(alpha);  
        oneMeanPower_Model.setEffectSize(effectSize);
        oneMeanPower_Model.setStandErrMean(standErrMean);        
        oneMeanPower_Model.calculatePower();
        // printed Strings for Power Report
        strPrinted_Null = "\u03BC = " + String.valueOf(nullMu);
        
        switch (strRejectionCriterion) {
            case "LessThan":
                strPrinted_Alt = "\u03BC < " + String.valueOf(nullMu);
                break;
                
            case "NotEqual":
                strPrinted_Alt = "\u03BC \u2260 " + String.valueOf(nullMu);
                break;
                
            case "GreaterThan":
                strPrinted_Alt = "\u03BC > " + String.valueOf(nullMu);
                break;

            default:
                String switchFailure = "Switch failure: OneMean_Power_Controller 90 " + strRejectionCriterion;
                MyAlerts.showUnexpectedErrorAlert(switchFailure);
        }

        oneMeanPower_Model.setPrintedNullHypothesis(strPrinted_Null);
        oneMeanPower_Model.setPrintedAltHypothesis(strPrinted_Alt);
        oneMeanPower_Model.archiveNullValues();
        oneMeanPower_Model.constructNonRejectionRegion();
        oneMeanPower_Model.print_Power_Table();
        
        oneMeanPower_Dashboard = new OneMean_Power_Dashboard(this);
        oneMeanPower_Dashboard.initializeFurther();
        oneMeanPower_Dashboard.populateTheBackGround();
        oneMeanPower_Dashboard.putEmAllUp();
        oneMeanPower_Dashboard.showAndWait();
        return strReturnStatus; 
    }  
    
    public OneMean_Power_Model get_power_Model_Z() { return oneMeanPower_Model;}
    public String getRejectionCriterion() { return strRejectionCriterion; }    
    public Point_2D getNonRejectionRegion() { return nonRejectionRegion; }
}
