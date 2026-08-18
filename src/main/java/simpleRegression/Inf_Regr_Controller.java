/************************************************************
 *                     Inf_Regr_Controller                  *
 *                          06/05/26                        *
 *                            12:00                         *
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
    
    double msResid, sumOfSquares_x, varX;
    int sampleSize;
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
    private Inf_Regr_Dashboard regDashboard;
    private Data_Manager dm;
    
    // POJOs / FX
    
    public Inf_Regr_Controller(Data_Manager dm) { 
        this.dm = dm; 
        dm.whereIsWaldo(41, waldoFile, "*** Constructing");    
    }  
        
    public String doTheProcedure() {
        dm.whereIsWaldo(45, waldoFile, "*** doTheProcedure()"); 
        try {
            int casesInStruct = dm.getNCasesInStruct();
            
            if (casesInStruct == 0) {
                MyAlerts.showAintGotNoDataAlert();
                return "Cancel";
            }
            
            if (casesInStruct > 2000) {
                MyAlerts.showLongTimeComingWarning();
            } 
            dm.whereIsWaldo(57, waldoFile, " --- doTheProcedure()");
            Regr_Dialog regressionDialog = new Regr_Dialog(dm, "QUANTITATIVE", "Simple Linear Regression");
            dm.whereIsWaldo(59, waldoFile, " --- doTheProcedure()");
            regressionDialog.showAndWait();
            strReturnStatus = regressionDialog.getStrReturnStatus();
            dm.whereIsWaldo(62, waldoFile, " --- doTheProcedure()");
            if (!strReturnStatus.equals("OK")) { return strReturnStatus; }
            dm.whereIsWaldo(64, waldoFile, " --- doTheProcedure()"); 
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
            dm.whereIsWaldo(78, waldoFile, " --- doTheProcedure()");
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
            sampleSize = qdv_XVariable.getLegalN();
            regModel = new Inf_Regr_Model(this);
            dm.whereIsWaldo(93, waldoFile, " --- doTheProcedure()");
            strReturnStatus = regModel.setupRegressionAnalysis(qdv_XVariable, qdv_YVariable);   // 0 is the y-var
            
            if (strReturnStatus.equals("OK")) {
                dm.whereIsWaldo(97, waldoFile, " --- doRegressionAnalysis()");
                regModel.doRegressionAnalysis();
                msResid = regModel.getMSResid();
                sumOfSquares_x = qdv_XVariable.getTheSS();
                regModel.pearsonRInferenceCalculations();
                dm.whereIsWaldo(102, waldoFile, " --- pearsonRInferenceCalculations()");
                regDashboard = new Inf_Regr_Dashboard(this, regModel);
                dm.whereIsWaldo(104, waldoFile, " --- Regr_Dashboard");
                regDashboard.populateTheBackGround();
                dm.whereIsWaldo(106, waldoFile, " --- populateTheBackGround()");
                regDashboard.putEmAllUp();
                dm.whereIsWaldo(108, waldoFile, " --- putEmAllUp()");
                regDashboard.showAndWait();
            }
            else {
                strReturnStatus = "Cancel";
                return strReturnStatus;
            }
            dm.whereIsWaldo(115, waldoFile, " --- doTheProcedure()");
            strReturnStatus = regDashboard.getStrReturnStatus();

            return strReturnStatus;
        }
        catch (Exception ex) { // Constructs stack trace?
            PrintExceptionInfo pei = new PrintExceptionInfo(ex, "RegressionProcedure");
        }     
        dm.whereIsWaldo(123, waldoFile, " --- End doTheProcedure()"); 
        return strReturnStatus;
    }
    
    public Data_Manager getDataManager() { return dm; }
    public Inf_Regr_Model getRegModel() { return regModel; }
    public double get_MSResid() { return msResid; }
    public double get_SSX() { return sumOfSquares_x; }
    public double get_VarX() {
        varX = qdv_XVariable.getTheVariance();
        return varX;
    }
    
    public int getSampleSize() { return sampleSize; }
    public String getSaveTheHats() { return saveTheHats; }
    public String getSaveTheResids() { return saveTheResids; }
    public String getExplanVar() { return explanVarLabel; }
    public String getResponseVar() { return responseVarLabel; }
    public String getSubTitle() { return subTitle; }
    public int getNCasesInStruct() { return dm.getNCasesInStruct(); }
    public String[] getAxisLabels() { return strAxisLabels; }
    public Inf_Regr_Controller get_Inf_Regr_Controller() { return this; }
}
