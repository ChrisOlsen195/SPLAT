/**************************************************
 *         Single_t_SummaryStats_Controller       *
 *                    11/26/25                    *
 *                     12:00                      *
 *************************************************/
package the_t_procedures;

import dialogs.t_and_z.Single_t_SumStats_Dialog;
import utilityClasses.MyYesNoAlerts;

public class Single_t_SumStats_Controller {
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    String strReturnStatus, yesOrNo;
    
    // My classes
    MyYesNoAlerts myYesNoAlerts;
    Single_t_SumStats_Dialog single_t_SumStats_Dialog;
    Single_t_SumStats_Model single_t_SummaryStats_Model;   
    Single_t_SumStats_Dashboard single_t_SumStats_Dashboard;

    public Single_t_SumStats_Controller(/* Data_Manager dataManager */) {
        if (printTheStuff) {
            System.out.println("26 *** Single_t_SumStats_Controller, Constructing");
        }
        myYesNoAlerts = new MyYesNoAlerts();
        strReturnStatus = "OK";
    }
    
    public String doTheControllerThing() {
        if (printTheStuff) {
            System.out.println("34 *** Single_t_SumStats_Controller, doTheControllerThing()");
        }
        yesOrNo = "Yes";
        
        while (yesOrNo.equals("Yes")) {
            strReturnStatus = "Cancel";
            single_t_SumStats_Dialog = new Single_t_SumStats_Dialog(this);
            // The local strReturnStatus may have been reset in the dialog
            if (strReturnStatus.equals("Cancel")) {
                yesOrNo = "No";
                return strReturnStatus;
            }
            single_t_SummaryStats_Model = new Single_t_SumStats_Model(this, single_t_SumStats_Dialog);
            single_t_SummaryStats_Model.doSingleTAnalysis();
            
            single_t_SumStats_Dashboard = new Single_t_SumStats_Dashboard(this);
            strReturnStatus = showTheDashboard();

            myYesNoAlerts.showAvoidRepetitiousClicksAlert();
            yesOrNo = myYesNoAlerts.getYesOrNo();
        }
        strReturnStatus = "OK";
        return strReturnStatus;
    } 
    
    public String showTheDashboard() {
        if (printTheStuff) {
            System.out.println("61 *** Single_t_SumStats_Controller, showTheDashboard()");
        }
        strReturnStatus = "OK";
        single_t_SumStats_Dashboard.populateTheBackGround();
        single_t_SumStats_Dashboard.putEmAllUp();
        single_t_SumStats_Dashboard.showAndWait();
        strReturnStatus = single_t_SumStats_Dashboard.getStrReturnStatus();
        return strReturnStatus;           
    }
    
    public Single_t_SumStats_Model getSingle_t_SumStats_Model() {
        return single_t_SummaryStats_Model; 
    }
    public String getReturnStatus() { return strReturnStatus; }
    public void setReturnStatus(String retStat) {  strReturnStatus = retStat; }
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
