/************************************************************
 *                      QuadReg_Controller                  *
 *                          11/03/23                        *
 *                            12:00                         *
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

public class QuadReg_Controller {
    // POJOs
    private String explanatoryVariable, responseVariable, subTitle, 
            saveTheResids, saveTheHats, returnStatus;
    
    String waldoFile;
    
    // My classes
    private BivariateContinDataObj bivContin;
    private QuantitativeDataVariable qdv_XVariable, qdv_YVariable; //, qdv_Resids;
    private QuadReg_Model quadReg_Model;
    private QuadReg_Dashboard quadReg_Dashboard;
    private Data_Manager dm;
    
    // POJOs / FX
    
    public QuadReg_Controller(Data_Manager dm) {
        this.dm = dm;
        //waldoFile = "QuadReg_Controller";
        waldoFile = "";
        dm.whereIsWaldo(37, waldoFile, "constructing"); 
    }  
        
    public String doTheProcedure() {
        try {
            int casesInStruct = dm.getNCasesInStruct();
            dm.whereIsWaldo(43, waldoFile, "doTheProcedure()");
            
            if (casesInStruct == 0) {
                MyAlerts.showAintGotNoDataAlert();
                return "Cancel";
            }
            dm.whereIsWaldo(49, waldoFile, "doTheProcedure()");
            Regression_Dialog regressionDialog = new Regression_Dialog(dm, "QUANTITATIVE", "Quadratic regression");

            regressionDialog.showAndWait();
            returnStatus = regressionDialog.getReturnStatus();
            
            if (!returnStatus.equals("OK")) { return returnStatus; }

            explanatoryVariable = regressionDialog.getPreferredFirstVarDescription();
            responseVariable = regressionDialog.getPreferredSecondVarDescription();
            subTitle = regressionDialog.getSubTitle();
            saveTheResids = regressionDialog.getSaveTheResids();
            saveTheHats = regressionDialog.getSaveTheHats();
            ArrayList<ColumnOfData> data = regressionDialog.getData();
            dm.whereIsWaldo(63, waldoFile, "doTheProcedure()");
            bivContin = new BivariateContinDataObj(dm, data);
            
            if (bivContin.getDataExists() == true) {
                bivContin.continueConstruction();
            }
            else
            {
                MyAlerts.showNoLegalBivDataAlert();
                returnStatus = "Cancel";
                return returnStatus;
            }

            qdv_XVariable = new QuantitativeDataVariable("qdRegCont75", "qdRegCont75", data.get(0));
            qdv_YVariable = new QuantitativeDataVariable("qdRegCont76", "qdRegCont76", data.get(1));    

            quadReg_Model = new QuadReg_Model(this);
            quadReg_Model.setupQuadRegAnalysis(qdv_XVariable, qdv_YVariable);   // 0 is the y-var
            quadReg_Model.doQuadRegRegressionAnalysis();

            quadReg_Dashboard = new QuadReg_Dashboard(this, quadReg_Model);
            quadReg_Dashboard.populateTheBackGround();
            quadReg_Dashboard.putEmAllUp();
            quadReg_Dashboard.showAndWait();
            returnStatus = quadReg_Dashboard.getReturnStatus();
            returnStatus = "Ok";
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
    public String getExplanVar() { return explanatoryVariable; }
    public String getResponseVar() { return responseVariable; }
    public String getSubTitle() { return subTitle; }
}

