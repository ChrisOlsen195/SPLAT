/************************************************************
 *                 NoInf_Regression_Controller              *
 *                          11/27/23                        *
 *                            12:00                         *
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

public class NoInf_Regression_Controller {
    // POJOs
    private String explanVarDescription, responseVarDescription, 
            explanVarLabel, respVarLabel, subTitle, 
            saveTheResids, saveTheHats, returnStatus;
    private String[] strAxisLabels;
    private ArrayList<String> xStrings, yStrings;
    
    // Make empty if no-print
    //String waldoFile = "NoInf_Regression_Controller";
    String waldoFile = "";
    
    // My classes
    private BivariateContinDataObj bivContin;
    private QuantitativeDataVariable qdv_XVariable, qdv_YVariable;
    private NoInf_Regression_Model noInf_RegModel;
    private NoInf_Regression_Dashboard noInf_RegDashboard;
    private Data_Manager dm;
    
    // POJOs / FX
    
    public NoInf_Regression_Controller(Data_Manager dm) {
        this.dm = dm;
        dm.whereIsWaldo(40, waldoFile, "NoInf_Regression_Controller, Constructing");
    }  
        
    public String doTheProcedure() {
        try {
            int casesInStruct = dm.getNCasesInStruct();
            
            if (casesInStruct == 0) {
                MyAlerts.showAintGotNoDataAlert_2Var();
                return "Cancel";
            }

            if (casesInStruct > 2000) {
                MyAlerts.showLongTimeComingWarning();
            }            
            
            Regression_Dialog regressionDialog = new Regression_Dialog(dm, "QUANTITATIVE", "Simple Linear Regression");
            dm.whereIsWaldo(51, waldoFile, "doTheProcedure()");
            regressionDialog.showAndWait();
            returnStatus = regressionDialog.getReturnStatus();
            
            if (!returnStatus.equals("OK")) { return returnStatus; }

            explanVarLabel = regressionDialog.getFirstVarLabel_InFile();
            respVarLabel = regressionDialog.getSecondVarLabel_InFile();
            explanVarDescription = regressionDialog.getPreferredFirstVarDescription();
            responseVarDescription = regressionDialog.getPreferredSecondVarDescription();
            strAxisLabels = new String[2];
            strAxisLabels[0] = explanVarDescription;
            strAxisLabels[1] = responseVarDescription;
            subTitle = regressionDialog.getSubTitle();
            saveTheResids = regressionDialog.getSaveTheResids();
            saveTheHats = regressionDialog.getSaveTheHats();
            ArrayList<ColumnOfData> data = regressionDialog.getData();
            bivContin = new BivariateContinDataObj(dm, data);
            
            if (bivContin.getDataExists() == true) {  bivContin.continueConstruction(); }
            else {
                MyAlerts.showNoLegalBivDataAlert();
                returnStatus = "Cancel";
                return returnStatus;
            }

            xStrings = bivContin.getLegalXsAs_AL_OfStrings();
            yStrings = bivContin.getLegalYsAs_AL_OfStrings();

            qdv_XVariable = new QuantitativeDataVariable(explanVarLabel, explanVarDescription, xStrings);
            qdv_YVariable = new QuantitativeDataVariable(respVarLabel, responseVarDescription, yStrings);   
            
            noInf_RegModel = new NoInf_Regression_Model(this);

            returnStatus = noInf_RegModel.setupRegressionAnalysis(qdv_XVariable, qdv_YVariable);   // 0 is the y-var
            
            if (returnStatus.equals("OK")) {
                noInf_RegModel.doRegressionAnalysis();
                noInf_RegDashboard = new NoInf_Regression_Dashboard(this, noInf_RegModel);
                noInf_RegDashboard.populateTheBackGround();
                noInf_RegDashboard.putEmAllUp();
                noInf_RegDashboard.showAndWait();
            }
            else {
                returnStatus = "Cancel";
                return returnStatus;
            }
            
            returnStatus = noInf_RegDashboard.getReturnStatus();
            return returnStatus;
        }
        catch (Exception ex) { // Constructs stack trace?
            PrintExceptionInfo pei = new PrintExceptionInfo(ex, "NoInf Regression:doTheProcedure()");
        }
        return returnStatus;
    }
    
    public Data_Manager getDataManager() { return dm; }
    public String getSaveTheHats() { return saveTheHats; }
    public String getSaveTheResids() { return saveTheResids; }
    public String getExplanVar() { return explanVarDescription; }
    public String getResponseVar() { return responseVarDescription; }
    public String getSubTitle() { return subTitle; }
    public int getNCasesInStruct() { return dm.getNCasesInStruct(); }
    public String[] getAxisLabels() { return strAxisLabels; }
}
