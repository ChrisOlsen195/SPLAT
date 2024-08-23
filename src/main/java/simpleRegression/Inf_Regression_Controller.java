/************************************************************
 *                  Inf_Regression_Controller               *
 *                          11/18/23                        *
 *                            15:00                         *
 ***********************************************************/
package simpleRegression;

import dialogs.regression.Regression_Dialog;
import dataObjects.BivariateContinDataObj;
import dataObjects.ColumnOfData;
import java.util.ArrayList;
import dataObjects.QuantitativeDataVariable;
import splat.Data_Manager;
import utilityClasses.MyAlerts;
import utilityClasses.PrintExceptionInfo;

public class Inf_Regression_Controller {
    // POJOs
    private String explanVarLabel, responseVarLabel, explanVarDescr,  
            respVarDescr, subTitle, saveTheResids, saveTheHats, returnStatus;
    private String[] strAxisLabels;
    private ArrayList<String> xStrings, yStrings;
    
    // Make empty if no-print
    //String waldoFile = "Inf_Regression_Controller";
    String waldoFile = "";
    
    // My classes
    private BivariateContinDataObj bivContin;
    private QuantitativeDataVariable qdv_XVariable, qdv_YVariable;
    private Inf_Regression_Model regModel;
    private Regression_Dashboard regDashboard;
    private Data_Manager dm;
    
    // POJOs / FX
    
    public Inf_Regression_Controller(Data_Manager dm) { 
        this.dm = dm; 
        dm.whereIsWaldo(39, waldoFile, "Inf_Regression_Controller, Constructing");    
    }  
        
    public String doTheProcedure() {
        dm.whereIsWaldo(43, waldoFile, "Inf_Regression_Controller, Constructing"); 
        try {
            int casesInStruct = dm.getNCasesInStruct();
            
            if (casesInStruct == 0) {
                MyAlerts.showAintGotNoDataAlert();
                return "Cancel";
            }
            
            if (casesInStruct > 2000) {
                MyAlerts.showLongTimeComingWarning();
            } 
            
            Regression_Dialog regressionDialog = new Regression_Dialog(dm, "QUANTITATIVE", "Simple Linear Regression");
            dm.whereIsWaldo(57, waldoFile, "doTheProcedure()");
            regressionDialog.showAndWait();
            returnStatus = regressionDialog.getReturnStatus();
            
            if (!returnStatus.equals("OK")) { return returnStatus; }

            explanVarLabel = regressionDialog.getFirstVarLabel_InFile();
            responseVarLabel = regressionDialog.getSecondVarLabel_InFile();
            explanVarDescr = regressionDialog.getPreferredFirstVarDescription();
            respVarDescr = regressionDialog.getPreferredSecondVarDescription();

            strAxisLabels = new String[2];
            strAxisLabels[0] = explanVarDescr;
            strAxisLabels[1] = respVarDescr;
            subTitle = regressionDialog.getSubTitle();
            saveTheResids = regressionDialog.getSaveTheResids();
            saveTheHats = regressionDialog.getSaveTheHats();
            ArrayList<ColumnOfData> data = regressionDialog.getData();
            bivContin = new BivariateContinDataObj(dm, data);
            
            if (bivContin.getDataExists()) { bivContin.continueConstruction(); }
            else {
                MyAlerts.showNoLegalBivDataAlert();
                returnStatus = "Cancel";
                return returnStatus;
            }

            xStrings = bivContin.getLegalXsAs_AL_OfStrings();
            yStrings = bivContin.getLegalYsAs_AL_OfStrings();
            
            qdv_XVariable = new QuantitativeDataVariable(explanVarLabel, explanVarDescr, xStrings);
            qdv_YVariable = new QuantitativeDataVariable(responseVarLabel, respVarDescr, yStrings);   
             
            regModel = new Inf_Regression_Model(this);

            returnStatus = regModel.setupRegressionAnalysis(qdv_XVariable, qdv_YVariable);   // 0 is the y-var
            
            if (returnStatus.equals("OK")) {
                regModel.doRegressionAnalysis();
                regModel.pearsonRInferenceCalculations();
                regDashboard = new Regression_Dashboard(this, regModel);
                regDashboard.populateTheBackGround();
                regDashboard.putEmAllUp();
                regDashboard.showAndWait();
            }
            else {
                returnStatus = "Cancel";
                return returnStatus;
            }
            
            returnStatus = regDashboard.getReturnStatus();

            return returnStatus;
        }
        catch (Exception ex) { // Constructs stack trace?
            PrintExceptionInfo pei = new PrintExceptionInfo(ex, "RegressionProcedure");
        }     
        return returnStatus;
    }
    
    public Data_Manager getDataManager() { return dm; }
    public String getSaveTheHats() { return saveTheHats; }
    public String getSaveTheResids() { return saveTheResids; }
    public String getExplanVar() { return explanVarLabel; }
    public String getResponseVar() { return responseVarLabel; }
    public String getSubTitle() { return subTitle; }
    public int getNCasesInStruct() { return dm.getNCasesInStruct(); }
    public String[] getAxisLabels() { return strAxisLabels; }
}
