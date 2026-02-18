/**************************************************
 *              TwoProp_Inf_Controller            *
 *                    12/10/25                    *
 *                     15:00                      *
 *************************************************/
package the_z_procedures;

import bivariateProcedures_Categorical.BivCat2x_Model;
import splat.MainMenu;
import utilityClasses.MyYesNoAlerts;

public class TwoProp_Inf_Controller {
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    int confidenceLevel;
    
    String strReturnStatus, yesOrNo;
    
    // My classes 
    MainMenu mainMenu;
    MyYesNoAlerts myYesNoAlerts;
    TwoProp_Inf_Model twoProp_Inf_Model;
    BivCat2x_Model bivCat2x_Model;
    TwoProp_Inf_Dashboard twoProp_Inf_Dashboard;
    
    public TwoProp_Inf_Controller(MainMenu mainMenu) {
        if (printTheStuff) {
            System.out.println("*** 29 TwoProp_Inf_Controller, Constructing");
        }
        this.mainMenu = mainMenu;
        myYesNoAlerts = new MyYesNoAlerts();
        strReturnStatus = "OK";
    }
    
    public String doTheControllerThing() { 
        if (printTheStuff) {
            System.out.println("*** 38 TwoProp_Inf_Controller, doTheControllerThing()");
        }
        yesOrNo = "Yes";
        while (yesOrNo.equals("Yes")) {  
            strReturnStatus = "OK";
            twoProp_Inf_Model = new TwoProp_Inf_Model();
            strReturnStatus = twoProp_Inf_Model.doZProcedure();
            bivCat2x_Model = twoProp_Inf_Model.getBivCat2xModel();
            if (strReturnStatus.equals("Cancel") 
                || strReturnStatus.equals("CloseWindow")) {
             mainMenu.setReturnStatus(strReturnStatus);
             return strReturnStatus;
            }

            if (strReturnStatus.equals("OK")) {
                confidenceLevel = twoProp_Inf_Model.getConfidenceLevel();
                twoProp_Inf_Dashboard = new TwoProp_Inf_Dashboard(this);
                twoProp_Inf_Dashboard.populateTheBackGround();
                twoProp_Inf_Dashboard.putEmAllUp();
                twoProp_Inf_Dashboard.showAndWait();
                strReturnStatus = twoProp_Inf_Dashboard.getStrReturnStatus();  
            }

            myYesNoAlerts.showAvoidRepetitiousClicksAlert();
            yesOrNo = myYesNoAlerts.getYesOrNo();    
        }
        return strReturnStatus;
    }
    
    public void setReturnStatus(String daStrStatus) { 
        strReturnStatus = daStrStatus; 
    }
    
    public BivCat2x_Model getBivCat2xModel() {return bivCat2x_Model; }
    public int getConfidenceLevel() { return confidenceLevel; }
    public TwoProp_Inf_Model getTwoPropModel() { return twoProp_Inf_Model; }
    public String getReturnStatus() { return strReturnStatus; }
}
