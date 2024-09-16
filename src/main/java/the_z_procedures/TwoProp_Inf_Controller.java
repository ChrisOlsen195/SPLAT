/**************************************************
 *              TwoProp_Inf_Controller            *
 *                    09/14/24                    *
 *                     03:00                      *
 *************************************************/
package the_z_procedures;

import utilityClasses.MyYesNoAlerts;

public class TwoProp_Inf_Controller {
    // POJOs
    int confidenceLevel;
    
    String goodToGo, yesOrNo;
    
    // My classes    
    MyYesNoAlerts myYesNoAlerts;
    TwoProp_Inf_Model twoProp_Inf_Model;
    TwoProp_Inf_Dashboard twoProp_Inf_Dashboard;
    
    public TwoProp_Inf_Controller() {
        //System.out.println("19 TwoProp_Inf_Controller, constructing");
        myYesNoAlerts = new MyYesNoAlerts();
    }
    
    public String doTheControllerThing() { 
        yesOrNo = "Yes";
        while (yesOrNo.equals("Yes")) {        
            twoProp_Inf_Model = new TwoProp_Inf_Model();
            goodToGo = twoProp_Inf_Model.doZProcedure();

            if (goodToGo.equals("OK")) {
                confidenceLevel = twoProp_Inf_Model.getConfidenceLevel();
                twoProp_Inf_Dashboard = new TwoProp_Inf_Dashboard(this);
                twoProp_Inf_Dashboard.populateTheBackGround();
                twoProp_Inf_Dashboard.putEmAllUp();
                twoProp_Inf_Dashboard.showAndWait();
                goodToGo = twoProp_Inf_Dashboard.getReturnStatus();  
            }

            myYesNoAlerts.showAvoidRepetitiousClicksAlert();
            yesOrNo = myYesNoAlerts.getYesOrNo();          
        }
        return goodToGo;
    }
    
    public int getConfidenceLevel() { return confidenceLevel; }
    public TwoProp_Inf_Model getTwoPropModel() { return twoProp_Inf_Model; }
    public String getReturnStatus() { return goodToGo; }
}
