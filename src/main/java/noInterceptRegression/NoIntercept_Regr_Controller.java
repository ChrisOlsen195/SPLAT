/************************************************************
 *                 NoIntercept_Regr_Controller              *
 *                          02/11/25                        *
 *                            09:00                         *
 ***********************************************************/
package noInterceptRegression;

import dialogs.regression.Regr_Dialog;
import dataObjects.BivariateContinDataObj;
import dataObjects.ColumnOfData;
import java.util.ArrayList;
import dataObjects.QuantitativeDataVariable;
import splat.Data_Manager;
import utilityClasses.MyAlerts;
import utilityClasses.PrintExceptionInfo;

public class NoIntercept_Regr_Controller {
    // POJOs
    private String explanatoryVariable, responseVariable, subTitle, saveTheResids, returnStatus;
    
    String waldoFile = "NoIntercept_Regr_Controller";
    //String waldoFile = "";
    
    // My classes
    private BivariateContinDataObj bivContin;
    private QuantitativeDataVariable qdv_XVariable, qdv_YVariable; //, qdv_Resids;
    private NoIntercept_Regr_Model noInt_Regr_Model;
    private NoIntercept_Regr_Dashboard noInt_Regr_Dashboard;
    private Data_Manager dm;
    
    // POJOs / FX
    
    public NoIntercept_Regr_Controller(Data_Manager dm) {
        this.dm = dm;
        //dm.whereIsWaldo(35, waldoFile, "Constructing");
    }  
        
    public String doTheProcedure() {
        //dm.whereIsWaldo(39, waldoFile, "doTheProcedure()");
        try {
            int casesInStruct = dm.getNCasesInStruct();
            //System.out.println("42 No_Intercept_Regression_Controller_Controller, casesInStruct = " + casesInStruct);
            
            if (casesInStruct == 0) {
                MyAlerts.showAintGotNoDataAlert();
                return "Cancel";
            }
            
            Regr_Dialog regressionDialog = new Regr_Dialog(dm, "QUANTITATIVE", "One parameter linear regression");

            regressionDialog.showAndWait();
            returnStatus = regressionDialog.getReturnStatus();
            
            if (!returnStatus.equals("OK")) { return returnStatus; }

            explanatoryVariable = regressionDialog.getPreferredFirstVarDescription();
            responseVariable = regressionDialog.getPreferredSecondVarDescription();
            subTitle = regressionDialog.getSubTitle();
            saveTheResids = regressionDialog.getSaveTheResids();
            ArrayList<ColumnOfData> data = regressionDialog.getData();

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

            qdv_XVariable = new QuantitativeDataVariable("noIntReg-Contr74", "noIntReg-Contr74", data.get(0));
            qdv_YVariable = new QuantitativeDataVariable("noIntReg-Contr75", "noIntReg-Contr75", data.get(1));    

            noInt_Regr_Model = new NoIntercept_Regr_Model(this);

            noInt_Regr_Model.setupRegressionAnalysis(qdv_XVariable, qdv_YVariable);   // 0 is the y-var
            noInt_Regr_Model.doNoIntRegressionAnalysis();

            noInt_Regr_Dashboard = new NoIntercept_Regr_Dashboard(this, noInt_Regr_Model);
            noInt_Regr_Dashboard.populateTheBackGround();
            noInt_Regr_Dashboard.putEmAllUp();
            noInt_Regr_Dashboard.showAndWait();
            returnStatus = noInt_Regr_Dashboard.getReturnStatus();

            returnStatus = "Ok";
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

