/**************************************************
 *                Single_t_Controller             *
 *                    02/06/26                    *
 *                     15:00                      *
 *************************************************/
package the_t_procedures;

import dialogs.t_and_z.Single_t_Dialog;
import dataObjects.ColumnOfData;
import dataObjects.QuantitativeDataVariable;
import dialogs.t_and_z.DataStructChoice_Dialog_t_Single_Mean;
import splat.*;
import utilityClasses.MyAlerts;

public class Single_t_Controller {
    // POJOs
    int confidenceLevel;
           
    String descriptionOfVariable, returnStatus, dataOrSummary;
    
    // String waldoFile = "Single_t_Controller";
    String waldoFile = "";
        
    // My classes
    ColumnOfData colOfData;
    Data_Manager dm;
    QuantitativeDataVariable theQDV;
    Single_t_Dialog single_t_Dialog;
    Single_t_Model single_t_Model;
    private Single_t_Dashboard single_t_Dashboard;    

    //Single_Mean_DataStruct_Dialog dataStructChoice;
    // ******  Constructor called from Main Menu  ******
    public Single_t_Controller(Data_Manager dm) {
        this.dm = dm; 
        dm.whereIsWaldo(36, waldoFile, "*** Constructing from Data_Manager");
        dm.whereIsWaldo(37, waldoFile, "... Constructing from Data_Manager, dm = " + dm.getTheFileName());
    }

    public String chooseDataOrSummary() {
        dm.whereIsWaldo(41, waldoFile, "--- chooseDataOrSummary()");
        DataStructChoice_Dialog_t_Single_Mean sed = new DataStructChoice_Dialog_t_Single_Mean(this);
        
        if (dataOrSummary.equals("Raw data")) { doHaveData(); }
        
        if (dataOrSummary.equals("Summary")) {
            Single_t_SumStats_Controller singleT_SumStats_Controller = new Single_t_SumStats_Controller();
            returnStatus = singleT_SumStats_Controller.getReturnStatus();
            if (returnStatus.equals("OK")) {
                returnStatus = singleT_SumStats_Controller.doTheControllerThing();
            }
        } 
        
        if (dataOrSummary.equals("Bailed")) {  /* No op */ }
        
        return "OK";
    }
    
    // Have data
    public String doHaveData() {
        dm.whereIsWaldo(61, waldoFile, "--- doHaveData()");
        String tempFileName = dm.getTheFileName();
        dm.whereIsWaldo(63, waldoFile, "... doHaveData(), file = " + tempFileName);
        returnStatus = "OK";
        int casesInStruct = dm.getNCasesInStruct();
        
        if ((casesInStruct == 0) || tempFileName.equals("null")) {
            MyAlerts.showAintGotNoDataAlert();
            return "Cancel";
        };
        
        single_t_Dialog = new Single_t_Dialog(dm, "QUANTITATIVE");
        single_t_Dialog.showAndWait();
        returnStatus = single_t_Dialog.getStrReturnStatus();
        
        if (returnStatus.equals("Cancel")) {
            return returnStatus;
        }
        
        descriptionOfVariable = single_t_Dialog.getDescriptionOfVariable();
        confidenceLevel = single_t_Dialog.getConfidenceLevel();
        colOfData = single_t_Dialog.getData(); 
        theQDV = new QuantitativeDataVariable(descriptionOfVariable, descriptionOfVariable, colOfData);
        returnStatus = doTheProcedure();
        return returnStatus;
    } 
    
    private String doTheProcedure() {
        dm.whereIsWaldo(89, waldoFile, "--- doTheProcedure()");
        returnStatus = "OK";
        single_t_Model = new Single_t_Model(this, theQDV);
        single_t_Model.doTAnalysis();
        single_t_Dashboard = new Single_t_Dashboard(this, theQDV);
        single_t_Dashboard.populateTheBackGround();
        single_t_Dashboard.putEmAllUp();
        single_t_Dashboard.showAndWait();
        returnStatus = single_t_Dashboard.getStrReturnStatus();
        return returnStatus;        
    }
    
    public void setDataOrSummary(String toThis) { dataOrSummary = toThis; }    
    public String getDescriptionOfVariable() { return descriptionOfVariable; }
    public String getHypotheses() { return single_t_Dialog.getChosenHypothesis(); }
    public double getHypothesizedMean() { return single_t_Dialog.getHypothesizedMean(); }
    public double getAlpha() { return single_t_Dialog.getAlpha(); }
    public int getConfidenceLevel() { return confidenceLevel; }
    public String[] getHypothPair() {
        return single_t_Dialog.getHypothesesToPrint();
    }
    public Single_t_Model getSingle_T_Model() { return single_t_Model; }    
    public QuantitativeDataVariable getTheQDVs() { return theQDV; }    
    public Data_Manager getDataManager() { return dm; }
}

