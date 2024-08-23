/**************************************************
 *         Single_t_SummaryStats_Controller       *
 *                    11/01/23                    *
 *                     21:00                      *
 *************************************************/
package the_t_procedures;

import dialogs.t_and_z.Single_t_SumStats_Dialog;
import utilityClasses.YesNoCancel_Alert;

public class Single_t_SumStats_Controller {
    // POJOs

    String returnStatus, doItAgain;
    
    String playItAgainSam_Title = "I'm doing homework Alert";
    String playItAgainSam_Header = "On the theory you might be doing lots of these," +
                    "\n would you like to do more right away?";
        
    // My classes
    Single_t_SumStats_Dialog single_t_SumStats_Dialog;
    Single_t_SumStats_Model single_t_SummaryStats_Model;   
    Single_t_SumStats_Dashboard single_t_SumStats_Dashboard;

    public Single_t_SumStats_Controller(/* Data_Manager dataManager */) {
        System.out.println("26 Single_t_SummaryStats_Controller, constructing");
        returnStatus = "OK";
    }
    
    public String doTheProcedure() {
        doItAgain = "Yes";
        
        while (doItAgain.equals("Yes")) {
            returnStatus = "Cancel";
            single_t_SumStats_Dialog = new Single_t_SumStats_Dialog();
            returnStatus = single_t_SumStats_Dialog.getReturnStatus();
            
            if (returnStatus.equals("Cancel")) { return returnStatus; }
            
            single_t_SummaryStats_Model = new Single_t_SumStats_Model(this, single_t_SumStats_Dialog);
            single_t_SummaryStats_Model.doSingleTAnalysis();
            
            single_t_SumStats_Dashboard = new Single_t_SumStats_Dashboard(this);
            returnStatus = showTheDashboard();

            YesNoCancel_Alert ync = new YesNoCancel_Alert(playItAgainSam_Title, playItAgainSam_Header);
            
            doItAgain = ync.getReturnString();
        }
        returnStatus = "OK";
        return returnStatus;
    } 
    
    public String showTheDashboard() {
        returnStatus = "OK";
        single_t_SumStats_Dashboard.populateTheBackGround();
        single_t_SumStats_Dashboard.putEmAllUp();
        single_t_SumStats_Dashboard.showAndWait();
        returnStatus = single_t_SumStats_Dashboard.getReturnStatus();
        return returnStatus;           
    }
    
    public Single_t_SumStats_Model getSingle_t_SumStats_Model() { return single_t_SummaryStats_Model; }
    public String getReturnStatus() { return returnStatus; }
    public double getAlpha() {  return single_t_SumStats_Dialog.getAlpha(); }
    public String getDescriptionOfVariable() { return single_t_SumStats_Dialog.getDescriptionOfVariable(); }
    public String getHypotheses() { 
        return single_t_SumStats_Dialog.getHypotheses(); 
    }
    public double getHypothesizedMean() { return single_t_SumStats_Dialog.getHypothesizedMean(); }
}
