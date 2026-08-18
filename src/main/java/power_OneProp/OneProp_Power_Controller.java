/**************************************************
 *          OneProp_Power_Controller              *
 *                  05/21/26                      *
 *                    18:00                       *
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
    
    double  alpha, nullProp, altProp, stErrNullP, stErrAltP, 
            effectSize, power;
    
    String strRejCriterion, strPrinted_Null, strPrinted_Alt, strReturnStatus;
    
    // My classes
    OneProp_Power_Dashboard oneProp_Power_Dashboard;
    OneProp_Power_Model oneProp_Power_Model;
    Point_2D nonRejectionRegion;
    Power_SingleProp_Dialog power_SingleProp_Dialog;

    public OneProp_Power_Controller() {
        if (printTheStuff) {
            System.out.println("32 *** OneProp_Power_Controller, Constructing");
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
        strRejCriterion = power_SingleProp_Dialog.getRejectionCriterion();
        stErrNullP = Math.sqrt(nullProp * (1.0 - nullProp) / sampleSize);
        stErrAltP = Math.sqrt(altProp * (1.0 - altProp) / sampleSize); 
        oneProp_Power_Model = new OneProp_Power_Model(this);
        oneProp_Power_Model.setNullParam(nullProp); 
        oneProp_Power_Model.setAltParam(altProp);
        oneProp_Power_Model.setStErr_NullParam(stErrNullP); 
        oneProp_Power_Model.setStErr_NullParam(stErrAltP);
        oneProp_Power_Model.setRejectionCriterion(strRejCriterion);
        oneProp_Power_Model.setEffectSize(effectSize);  // Needs RejCrit

        
        if (printTheStuff) {
            System.out.println( "56 OneProp_Power_Controller, alpha = " +  alpha);
            System.out.println( "56 OneProp_Power_Controller, nullProp = " +  nullProp);
            System.out.println( "56 OneProp_Power_Controller, altProp = " +  altProp);
            System.out.println( "56 OneProp_Power_Controller, effectSize = " +  effectSize);
            System.out.println( "56 OneProp_Power_Controller, sampleSize = " +  sampleSize);
            System.out.println("56 OneProp_Power_Controller, strRejectionCriterion = " +  strRejCriterion);
            System.out.println("56 OneProp_Power_Controller, stErrNullP = " +  stErrNullP);
            System.out.println("56 OneProp_Power_Controller, stErrAltP = " +  stErrAltP);
        }
        
        switch (strRejCriterion) {
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
                String switchFailure = "Switch failure: OneProp_Power_Controller 85 " + strRejCriterion;
                MyAlerts.showUnexpectedErrorAlert(switchFailure);                
        }
        
        oneProp_Power_Model.setRejectionCriterion(strRejCriterion);
        oneProp_Power_Model.setSampleSize(sampleSize);
        oneProp_Power_Model.setAlpha(alpha);  
        oneProp_Power_Model.setEffectSize(effectSize);
        oneProp_Power_Model.setStErr_NullParam(stErrNullP);
        oneProp_Power_Model.setStErr_AltParam(stErrAltP);

        strPrinted_Null = "p = " + String.valueOf(nullProp);
        
        switch (strRejCriterion) {
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
                String switchFailure = "Switch failure: OneProp_Power_Controller 112 " + strRejCriterion;
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
    public String getRejectionCriterion() { return strRejCriterion; }    
    public Point_2D getNonRejectionRegion() { return nonRejectionRegion; }    
    public double getNullProp() { return nullProp; }    
    public double getAltProp() { return altProp; }    
    public int getSampleSize() { return sampleSize; }    
}


