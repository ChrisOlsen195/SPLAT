/**************************************************
 *              OneProp_Inf_Controller            *
 *                    09/14/24                    *
 *                     03:00                      *
 *************************************************/
package the_z_procedures;

import utilityClasses.MyYesNoAlerts;

public class OneProp_Inf_Controller {
    boolean goodToGo;
    int confidenceLevel;
    double alpha;
            
    String returnStatus, yesOrNo;
    
    // My classes
    MyYesNoAlerts myYesNoAlerts;
    OneProp_Inf_Model oneProp_Inf_Model;
    OneProp_Inf_Dashboard oneProp_Inf_Dashboard;
    
    public OneProp_Inf_Controller() {
        //System.out.println("18 OneProp_Inf_Controller, constructing");
        myYesNoAlerts = new MyYesNoAlerts();
    }
    
    public String doTheControllerThing() { 
        yesOrNo = "Yes";
        while (yesOrNo.equals("Yes")) {
            returnStatus = "Cancel";        
            oneProp_Inf_Model = new OneProp_Inf_Model();
            goodToGo = oneProp_Inf_Model.getGoodToGo();
            returnStatus = oneProp_Inf_Model.getReturnStatus();
        
            if (goodToGo) {
                oneProp_Inf_Model.doZProcedure();
                alpha = oneProp_Inf_Model.getAlpha();
                confidenceLevel = oneProp_Inf_Model.getConfidenceLevel();
                goodToGo = oneProp_Inf_Model.getGoodToGo();
                returnStatus = oneProp_Inf_Model.getReturnStatus();
            }

            if (goodToGo) {
                returnStatus = "OK";
                oneProp_Inf_Dashboard = new OneProp_Inf_Dashboard(this);
                oneProp_Inf_Dashboard.populateTheBackGround();
                oneProp_Inf_Dashboard.putEmAllUp();
                oneProp_Inf_Dashboard.showAndWait();
                returnStatus = oneProp_Inf_Dashboard.getReturnStatus();        
                //return returnStatus;
            }

            myYesNoAlerts.showAvoidRepetitiousClicksAlert("You betcha", "No way");
            yesOrNo = myYesNoAlerts.getYesOrNo();        
        } 
         
        return returnStatus; 
    }
    
    public OneProp_Inf_Model getOnePropModel() { return oneProp_Inf_Model; }    
    public double getAlpha() { return alpha; }
    public int getConfidenceLevel() { return confidenceLevel; }    
    public String getReturnStatus() { return returnStatus; }
}
