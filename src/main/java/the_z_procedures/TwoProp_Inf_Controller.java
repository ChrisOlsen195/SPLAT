/**************************************************
 *              TwoProp_Inf_Controller            *
 *                    11/01/23                    *
 *                     12:00                      *
 *************************************************/
package the_z_procedures;

public class TwoProp_Inf_Controller {
    // POJOs
    int confidenceLevel;
    
    String returnStatus;
    
    // My classes    
    TwoProp_Inf_Model twoProp_Inf_Model;
    TwoProp_Inf_Dashboard twoProp_Inf_Dashboard;
    
    public TwoProp_Inf_Controller() {
        //System.out.println("19 TwoProp_Inf_Controller, constructing");
    }
    
    public String doTheControllerThing() { 
        twoProp_Inf_Model = new TwoProp_Inf_Model();
        returnStatus = twoProp_Inf_Model.doZProcedure();
        
        if (returnStatus.equals("OK")) {
            confidenceLevel = twoProp_Inf_Model.getConfidenceLevel();
            twoProp_Inf_Dashboard = new TwoProp_Inf_Dashboard(this);
            twoProp_Inf_Dashboard.populateTheBackGround();
            twoProp_Inf_Dashboard.putEmAllUp();
            twoProp_Inf_Dashboard.showAndWait();
            returnStatus = twoProp_Inf_Dashboard.getReturnStatus();  
        }
        return returnStatus;
    }
    
    public int getConfidenceLevel() { return confidenceLevel; }
    public TwoProp_Inf_Model getTwoPropModel() { return twoProp_Inf_Model; }
    public String getReturnStatus() { return returnStatus; }
}
