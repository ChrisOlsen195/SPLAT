/**************************************************
 *         Single_t_SummaryStats_Controller       *
 *                    02/15/25                    *
 *                     09:00                      *
 *************************************************/
package the_t_procedures;

import dialogs.t_and_z.Single_t_SumStats_Dialog;
import utilityClasses.MyYesNoAlerts;

public class Single_t_SumStats_Controller {
    // POJOs
    boolean printTheStuff = true;
    //boolean printTheStuff = false;
    
    String returnStatus, yesOrNo;
    
    // My classes
    MyYesNoAlerts myYesNoAlerts;
    Single_t_SumStats_Dialog single_t_SumStats_Dialog;
    Single_t_SumStats_Model single_t_SummaryStats_Model;   
    Single_t_SumStats_Dashboard single_t_SumStats_Dashboard;

    public Single_t_SumStats_Controller(/* Data_Manager dataManager */) {
        if (printTheStuff == true) {
            System.out.println("26 *** Single_t_SumStats_Controller, Constructing");
        }
        myYesNoAlerts = new MyYesNoAlerts();
        returnStatus = "OK";
    }
    
    public String doTheControllerThing() {
        if (printTheStuff == true) {
            System.out.println("34 --- Single_t_SumStats_Controller, doTheControllerThing()");
        }
        yesOrNo = "Yes";
        
        while (yesOrNo.equals("Yes")) {
            returnStatus = "Cancel";
            single_t_SumStats_Dialog = new Single_t_SumStats_Dialog();
            returnStatus = single_t_SumStats_Dialog.getReturnStatus();

            single_t_SummaryStats_Model = new Single_t_SumStats_Model(this, single_t_SumStats_Dialog);
            single_t_SummaryStats_Model.doSingleTAnalysis();
            
            single_t_SumStats_Dashboard = new Single_t_SumStats_Dashboard(this);
            returnStatus = showTheDashboard();

            myYesNoAlerts.showAvoidRepetitiousClicksAlert();
            yesOrNo = myYesNoAlerts.getYesOrNo();
        }
        returnStatus = "OK";
        return returnStatus;
    } 
    
    public String showTheDashboard() {
        if (printTheStuff == true) {
            System.out.println("58 --- Single_t_SumStats_Controller, showTheDashboard()");
        }
        returnStatus = "OK";
        single_t_SumStats_Dashboard.populateTheBackGround();
        single_t_SumStats_Dashboard.putEmAllUp();
        single_t_SumStats_Dashboard.showAndWait();
        returnStatus = single_t_SumStats_Dashboard.getReturnStatus();
        return returnStatus;           
    }
    
    public Single_t_SumStats_Model getSingle_t_SumStats_Model() {
        return single_t_SummaryStats_Model; 
    }
    public String getReturnStatus() { return returnStatus; }
    public double getAlpha() {  return single_t_SumStats_Dialog.getAlpha(); }
    public String getDescriptionOfVariable() { 
        return single_t_SumStats_Dialog.getDescriptionOfVariable(); 
    }
    public String getHypotheses() { 
        return single_t_SumStats_Dialog.getHypotheses(); 
    }
    public double getHypothesizedMean() { 
        return single_t_SumStats_Dialog.getHypothesizedMean(); 
    }
}
