/************************************************************
 *                 OneParam_QuadReg_Controller              *
 *                          11/01/23                        *
 *                            15:00                         *
 ***********************************************************/
package quadraticRegression;

import dialogs.regression.Regression_Dialog;
import dataObjects.BivariateContinDataObj;
import dataObjects.ColumnOfData;
import java.util.ArrayList;
import dataObjects.QuantitativeDataVariable;
import splat.Data_Manager;
import utilityClasses.MyAlerts;
import utilityClasses.PrintExceptionInfo;

public class OneParam_QuadReg_Controller {
    // POJOs
    
    // Make empty if no-print
    // String waldoFile = "OneParam_QuadReg_Controller";
    String waldoFile = "";
    
    private String explanatoryVariable, responseVariable, subTitle, saveTheResids, returnStatus;
    private ArrayList<String> xStrings, yStrings;
    
    // My classes
    private BivariateContinDataObj bivContin;
    private QuantitativeDataVariable qdv_XVariable, qdv_YVariable;
    private OneParam_QuadReg_Model noInt_QuadReg_Model;
    private OneParam_QuadReg_Dashboard noInt_QuadReg_Dashboard;
    private Data_Manager dm;
    
    // POJOs / FX
    
    public OneParam_QuadReg_Controller(Data_Manager dm) {
        this.dm = dm;
        dm.whereIsWaldo(38, waldoFile, "Constructing");
    }  
        
    public String doTheProcedure() {
        dm.whereIsWaldo(42, waldoFile, "doTheProcedure()");        
        try {
            int casesInStruct = dm.getNCasesInStruct();
            
            if (casesInStruct == 0) {
                MyAlerts.showAintGotNoDataAlert_1Var();
                return "Cancel";
            }
            
            Regression_Dialog regressionDialog = new Regression_Dialog(dm, "QUANTITATIVE", "One parameter Quadratic Regression");
            regressionDialog.showAndWait();
            returnStatus = regressionDialog.getReturnStatus();
            dm.whereIsWaldo(54, waldoFile, "regressionDialog.getReturnStatus()");
            
            if (!returnStatus.equals("OK")) { return returnStatus; }
            
            dm.whereIsWaldo(58, waldoFile, "doTheProcedure()");
            explanatoryVariable = regressionDialog.getPreferredFirstVarDescription();
            responseVariable = regressionDialog.getPreferredSecondVarDescription();
            subTitle = regressionDialog.getSubTitle();
            saveTheResids = regressionDialog.getSaveTheResids();
            ArrayList<ColumnOfData> data = regressionDialog.getData();

            bivContin = new BivariateContinDataObj(dm, data);            
            if (bivContin.getDataExists()) {
                bivContin.continueConstruction();
            }
            else
            {
                MyAlerts.showNoLegalBivDataAlert();
                returnStatus = "Cancel";
                return returnStatus;
            }

            xStrings = bivContin.getLegalXsAs_AL_OfStrings();
            yStrings = bivContin.getLegalYsAs_AL_OfStrings();

            qdv_XVariable = new QuantitativeDataVariable("qdReg81", "qdReg80", data.get(0));
            qdv_YVariable = new QuantitativeDataVariable("qdReg82", "qdReg81", data.get(1));    

            noInt_QuadReg_Model = new OneParam_QuadReg_Model(this);

            returnStatus = noInt_QuadReg_Model.setupOneParamQuadRegRegressionAnalysis(qdv_XVariable, qdv_YVariable); // 0 is the y-var
            
            if (returnStatus.equals("Cancel")) {   //  Unequal N's detected
                return returnStatus;
            }
            
            noInt_QuadReg_Model.doOneParamQuadRegRegressionAnalysis();

            noInt_QuadReg_Dashboard = new OneParam_QuadReg_Dashboard(this, noInt_QuadReg_Model);
            noInt_QuadReg_Dashboard.populateTheBackGround();
            noInt_QuadReg_Dashboard.putEmAllUp();
            noInt_QuadReg_Dashboard.showAndWait();
            returnStatus = noInt_QuadReg_Dashboard.getReturnStatus();

            returnStatus = "OK";
            return returnStatus;
        }
        catch (Exception ex) { // Constructs stack trace?
            PrintExceptionInfo pei = new PrintExceptionInfo(ex, "RegressionProcedure");
        }
        return returnStatus;
    }
    
    public Data_Manager getDataManager() { return dm; }
    public String getSaveTheResids() { return saveTheResids; }
    public String getExplanVar() { return explanatoryVariable; }
    public String getResponseVar() { return responseVariable; }
    public String getSubTitle() { return subTitle; }
}


