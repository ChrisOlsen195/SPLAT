/**************************************************
 *              OneProp_Inf_Controller            *
 *                    12/16/25                    *
 *                     00:00                      *
 *************************************************/
package the_z_procedures;

import utilityClasses.MyYesNoAlerts;

public class OneProp_Inf_Controller {
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    int confidenceLevel;
    double alpha;
            
    String strReturnStatus, yesOrNo;
    
    // My classes
    MyYesNoAlerts myYesNoAlerts;
    OneProp_Inf_Model oneProp_Inf_Model;
    OneProp_Inf_Dashboard oneProp_Inf_Dashboard;
    
    public OneProp_Inf_Controller() {
        strReturnStatus = "OK";
        //if (printTheStuff) {
        //    System.out.println("*** 28 OneProp_Inf_Controller, Constructing");
        //}
        myYesNoAlerts = new MyYesNoAlerts();
        strReturnStatus = "OK";
    }
    
    public String doTheControllerThing() { 
        //if (printTheStuff) {
        //    System.out.println("--- 36 OneProp_Inf_Controller, doTheControllerThing()");
        //}
        yesOrNo = "Yes";
        while (yesOrNo.equals("Yes")) {
            strReturnStatus = "OK";        
            oneProp_Inf_Model = new OneProp_Inf_Model(this);
            if (strReturnStatus.equals("OK")) {
                oneProp_Inf_Model.doZProcedure();
                if (strReturnStatus.equals("Cancel") 
                    || strReturnStatus.equals("CloseWindow")) {
                 return strReturnStatus; 
                }
                alpha = oneProp_Inf_Model.getAlpha();
                confidenceLevel = oneProp_Inf_Model.getConfidenceLevel();
                strReturnStatus = oneProp_Inf_Model.getReturnStatus();
            }

            if (strReturnStatus.equals("OK")) {
                oneProp_Inf_Dashboard = new OneProp_Inf_Dashboard(this);
                oneProp_Inf_Dashboard.populateTheBackGround();
                oneProp_Inf_Dashboard.putEmAllUp();
                oneProp_Inf_Dashboard.showAndWait();
                strReturnStatus = oneProp_Inf_Dashboard.getStrReturnStatus();        
                //if (printTheStuff) {
                //    System.out.println("--- 60 OneProp_Inf_Controller, strReturnStatus = " + strReturnStatus);
                //}
            }

            myYesNoAlerts.showAvoidRepetitiousClicksAlert();
            yesOrNo = myYesNoAlerts.getYesOrNo();        
        } 
         
        return strReturnStatus; 
    }
    
    public OneProp_Inf_Model getOnePropModel() { return oneProp_Inf_Model; }    
    public double getAlpha() { return alpha; }
    public int getConfidenceLevel() { return confidenceLevel; }    
    public String getReturnStatus() { return strReturnStatus; }
    public void setReturnStatus( String daReturnStatus) {
        strReturnStatus = daReturnStatus;
    }
}
