/************************************************************
 *                  Inf_Regression_Controller               *
 *                          01/07/26                        *
 *                            18:00                         *
 ***********************************************************/
package simpleRegression;

import dialogs.regression.Regr_Dialog;
import dataObjects.BivariateContinDataObj;
import dataObjects.ColumnOfData;
import java.util.ArrayList;
import dataObjects.QuantitativeDataVariable;
import splat.Data_Manager;
import utilityClasses.MyAlerts;
import utilityClasses.PrintExceptionInfo;

public class Inf_Regr_Controller {
    // POJOs
    private String explanVarLabel, responseVarLabel, explanVarDescr,  
            respVarDescr, subTitle, saveTheResids, saveTheHats, strReturnStatus;
    private String[] strAxisLabels;
    private ArrayList<String> xStrings, yStrings;
    
    // Make empty if no-print
    //String waldoFile = "Inf_Regression_Controller";
    String waldoFile = "";
    
    // My classes
    private BivariateContinDataObj bivContin;
    private QuantitativeDataVariable qdv_XVariable, qdv_YVariable;
    private Inf_Regr_Model regModel;
    private Regr_Dashboard regDashboard;
    private Data_Manager dm;
    
    // POJOs / FX
    
    public Inf_Regr_Controller(Data_Manager dm) { 
        this.dm = dm; 
        dm.whereIsWaldo(39, waldoFile, "*** Constructing");    
    }  
        
    public String doTheProcedure() {
        dm.whereIsWaldo(43, waldoFile, "*** doTheProcedure()"); 
        try {
            int casesInStruct = dm.getNCasesInStruct();
            
            if (casesInStruct == 0) {
                MyAlerts.showAintGotNoDataAlert();
                return "Cancel";
            }
            
            if (casesInStruct > 2000) {
                MyAlerts.showLongTimeComingWarning();
            } 
            dm.whereIsWaldo(55, waldoFile, "--- doTheProcedure()");
            Regr_Dialog regressionDialog = new Regr_Dialog(dm, "QUANTITATIVE", "Simple Linear Regression");
            dm.whereIsWaldo(57, waldoFile, "doTheProcedure()");
            regressionDialog.showAndWait();
            strReturnStatus = regressionDialog.getStrReturnStatus();
            dm.whereIsWaldo(60, waldoFile, "--- doTheProcedure()");
            if (!strReturnStatus.equals("OK")) { return strReturnStatus; }
            dm.whereIsWaldo(115, waldoFile, "--- doTheProcedure()"); 
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
            dm.whereIsWaldo(76, waldoFile, "--- doTheProcedure()");
            if (bivContin.getDataExists()) { bivContin.continueConstruction(); }
            else {
                MyAlerts.showNoLegalBivDataAlert();
                strReturnStatus = "Cancel";
                return strReturnStatus;
            }

            xStrings = bivContin.getLegalXsAs_AL_OfStrings();
            yStrings = bivContin.getLegalYsAs_AL_OfStrings();
            
            qdv_XVariable = new QuantitativeDataVariable(explanVarLabel, explanVarDescr, xStrings);
            qdv_YVariable = new QuantitativeDataVariable(responseVarLabel, respVarDescr, yStrings);   
             
            regModel = new Inf_Regr_Model(this);
            dm.whereIsWaldo(91, waldoFile, "--- doTheProcedure()");
            strReturnStatus = regModel.setupRegressionAnalysis(qdv_XVariable, qdv_YVariable);   // 0 is the y-var
            
            if (strReturnStatus.equals("OK")) {
                dm.whereIsWaldo(95, waldoFile, "--- doTheProcedure()");
                regModel.doRegressionAnalysis();
                regModel.pearsonRInferenceCalculations();
                dm.whereIsWaldo(98, waldoFile, "--- doTheProcedure()");
                regDashboard = new Regr_Dashboard(this, regModel);
                dm.whereIsWaldo(100, waldoFile, "--- doTheProcedure()");
                regDashboard.populateTheBackGround();
                dm.whereIsWaldo(102, waldoFile, "--- doTheProcedure()");
                regDashboard.putEmAllUp();
                dm.whereIsWaldo(104, waldoFile, "--- doTheProcedure()");
                regDashboard.showAndWait();
            }
            else {
                strReturnStatus = "Cancel";
                return strReturnStatus;
            }
            dm.whereIsWaldo(107, waldoFile, "--- doTheProcedure()");
            strReturnStatus = regDashboard.getStrReturnStatus();

            return strReturnStatus;
        }
        catch (Exception ex) { // Constructs stack trace?
            PrintExceptionInfo pei = new PrintExceptionInfo(ex, "RegressionProcedure");
        }     
        dm.whereIsWaldo(115, waldoFile, "--- End doTheProcedure()"); 
        return strReturnStatus;
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
