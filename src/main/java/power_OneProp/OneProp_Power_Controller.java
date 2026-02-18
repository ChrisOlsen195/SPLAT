/**************************************************
 *          OneProp_Power_Controller              *
 *                  01/15/25                      *
 *                    21:00                       *
 *************************************************/
package power_OneProp;

import dialogs.power.*;
import genericClasses.Point_2D;
import utilityClasses.MyAlerts;

public class OneProp_Power_Controller {
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    int  sampleSize;
    
    double  alpha, nullProp, altProp, standErrPHat, effectSize, power;
    
    String strRejectionCriterion, strPrinted_Null, strPrinted_Alt, strReturnStatus;
    
    // My classes
    OneProp_Power_Dashboard oneProp_Power_Dashboard;
    OneProp_Power_Model oneProp_Power_Model;
    Point_2D nonRejectionRegion;
    Power_SingleProp_Dialog power_SingleProp_Dialog;

    public OneProp_Power_Controller() {
        if (printTheStuff == true) {
            System.out.println("31 *** OneProp_Power_Controller, Constructing");
        }
        power_SingleProp_Dialog = new Power_SingleProp_Dialog();
    }
    
    public String ShowNWait() {
        power_SingleProp_Dialog.showAndWait();
        strReturnStatus = power_SingleProp_Dialog.getStrReturnStatus();
        
        if (strReturnStatus.equals("Cancel")) { return strReturnStatus;  }
        
        nullProp = power_SingleProp_Dialog.getNullProp();
        alpha = power_SingleProp_Dialog.getAlpha();
        effectSize = power_SingleProp_Dialog.getEffectSize();
        altProp = nullProp + effectSize;
        sampleSize = power_SingleProp_Dialog.getSampleSize();
        strRejectionCriterion = power_SingleProp_Dialog.getRejectionCriterion();
        standErrPHat = Math.sqrt(nullProp * (1.0 - nullProp) / sampleSize);  
        oneProp_Power_Model = new OneProp_Power_Model(this);
        oneProp_Power_Model.setNullProp(nullProp);
        
        switch (strRejectionCriterion) {
            case "LessThan":
                altProp = nullProp - effectSize;
                break;
                
            case "NotEqual":
                altProp = nullProp + effectSize;
                break;
                
            case "GreaterThan":
                altProp = nullProp + effectSize;
                break;
                
            default:
                String switchFailure = "Switch failure: OneProp_Power_Controller 66 " + strRejectionCriterion;
                MyAlerts.showUnexpectedErrorAlert(switchFailure);                
        }
        
        oneProp_Power_Model.setRejectionCriterion(strRejectionCriterion);
        oneProp_Power_Model.setAltProp(altProp);
        oneProp_Power_Model.setSampleSize(sampleSize);
        oneProp_Power_Model.setAlpha(alpha);  
        oneProp_Power_Model.setEffectSize(effectSize);

        oneProp_Power_Model.setStandErr_PAlt(standErrPHat);

        strPrinted_Null = "p = " + String.valueOf(nullProp);
        
        switch (strRejectionCriterion) {
            case "LessThan":
                strPrinted_Alt = "p < " + String.valueOf(nullProp);
                break;
                
            case "NotEqual":
                strPrinted_Alt = "p \u2260 " + String.valueOf(nullProp);
                break;
                
            case "GreaterThan":
                strPrinted_Alt = "p > " + String.valueOf(nullProp);
                break;

            default:
                String switchFailure = "Switch failure: OneProp_Power_Controller 90 " + strRejectionCriterion;
                MyAlerts.showUnexpectedErrorAlert(switchFailure); 
        }

        oneProp_Power_Model.setPrintedNullHypothesis(strPrinted_Null);
        oneProp_Power_Model.setPrintedAltHypothesis(strPrinted_Alt);
        oneProp_Power_Model.archiveNullValues();
        oneProp_Power_Model.constructNonRejectionRegion();
        power = oneProp_Power_Model.calculatePower();
        oneProp_Power_Model.print_Power_Table();
        oneProp_Power_Dashboard = new OneProp_Power_Dashboard(this);
        oneProp_Power_Dashboard.initializeFurther();

        //finished = false;
        oneProp_Power_Dashboard.populateTheBackGround();
        oneProp_Power_Dashboard.putEmAllUp();
        oneProp_Power_Dashboard.showAndWait();
        return strReturnStatus; 
    }  
    
    public OneProp_Power_Model get_power_Model_Z() { return oneProp_Power_Model;}
    public String getRejectionCriterion() { return strRejectionCriterion; }    
    public Point_2D getNonRejectionRegion() { return nonRejectionRegion; }    
    public double getNullProp() { return nullProp; }    
    public double getAltProp() { return altProp; }    
    public int getSampleSize() { return sampleSize; }    
}


