/**************************************************
 *              NoInf_Regression_Model            *
 *                    01/11/25                    *
 *                     18:00                      *
 *************************************************/
/***************************************************
 *  Regression and ANOVA checked against MPV on    *
 *  12/09/17. Need to check diagnostic statistics. *
 **************************************************/
package simpleRegression;

import utilityClasses.StringUtilities;
import java.util.ArrayList;
import splat.Data_Manager;
import superClasses.*;

public class NoInf_Regression_Model  extends Regression_Model
{     
    // POJOs
    NoInf_Regression_Controller noInf_Regr_Controller;
    
    // Make empty if no-print
    // String waldoFile = "No Inf_Regression_Model";
    String waldoFile = ""; 

    // POJOs / FX
    
    public NoInf_Regression_Model(NoInf_Regression_Controller noInf_Regression_Controller) {   
        super (noInf_Regression_Controller);
        //System.out.println("26 NoInfRegression_Model, constructing");
        this.noInf_Regr_Controller = noInf_Regression_Controller;
        dm = noInf_Regr_Controller.getDataManager(); 
        dm.whereIsWaldo(33, waldoFile, "Constructing");
        nCases = dm.getNCasesInStruct();
        nVarsInStruct = dm.getNVarsInStruct();
        tracker = noInf_Regr_Controller.getDataManager().getPositionTracker();
        nVarsCommitted = tracker.getNVarsCommitted();
        explanatoryVariable = noInf_Regression_Controller.getExplanVar();
        responseVariable = noInf_Regression_Controller.getResponseVar();
        respVsExplanVar = noInf_Regression_Controller.getSubTitle();
        saveTheHats = noInf_Regression_Controller.getSaveTheHats(); 
        saveTheResids = noInf_Regression_Controller.getSaveTheResids();
        fortyFiveDashesLong = StringUtilities.getUnicodeLineThisLong(45);
        strAxisLabels = new String[2];
        strAxisLabels[0] = noInf_Regression_Controller.getExplanVar();
        strAxisLabels[1] = noInf_Regression_Controller.getResponseVar();
        returnStatus = "OK";
    }
    
   //public static String getLeftMostNChars(String original, int leftChars)
   //{ 
   //    return StringUtilities.getleftMostNChars(original, leftChars); 
   //}
   
   public Data_Manager getDataManager() { return dm; }

   public void printStatistics() {   
       regressionReport = new ArrayList<>();
       regressionDiagnostics = new ArrayList<>();
       statsReport = new ArrayList<>();
       print_ParamEstimates();
       print_BivStats();
   }
}