/**************************************************
 *              Inf_Regression_Model              *
 *                    12/13/25                    *
 *                     21:00                      *
 *************************************************/
package simpleRegression;

import genericClasses.Point_2D;
import utilityClasses.StringUtilities;
import java.util.ArrayList;
import probabilityDistributions.ChiSquareDistribution;
import splat.Data_Manager;
import superClasses.*;

public class Inf_Regr_Model extends Regression_Model
{     
    // POJOs
    String deBlankedRegEq; 
    
    // Make empty if no-print
    //String waldoFile = "Inf_Regression_Model";
    String waldoFile = "";    
    
    // My classes
    public Inf_Regr_Controller regression_Controller;

    // POJOs / FX
    
    public Inf_Regr_Model(Inf_Regr_Controller inf_regression_Controller) {   
        super(inf_regression_Controller);
        dm = inf_regression_Controller.getDataManager();
        dm.whereIsWaldo(32, waldoFile, "Constructing");
        this.regression_Controller = inf_regression_Controller;
        dm = inf_regression_Controller.getDataManager();
        tracker = inf_regression_Controller.getDataManager().getPositionTracker();
        nVarsCommitted = tracker.getNVarsCommitted();
        nVarsInStruct = dm.getNVarsInStruct();
        nCasesInStruct = dm.getNCasesInStruct();
        explanatoryVariable = inf_regression_Controller.getExplanVar();
        responseVariable = inf_regression_Controller.getResponseVar();
        respVsExplanVar = inf_regression_Controller.getSubTitle();
        saveTheHats = inf_regression_Controller.getSaveTheHats();        
        saveTheResids = inf_regression_Controller.getSaveTheResids();
        fortyFiveDashesLong = StringUtilities.getUnicodeLineThisLong(45);
        fiftyOneDashesLong = StringUtilities.getUnicodeLineThisLong(51);
        fiftyFiveDashesLong = StringUtilities.getUnicodeLineThisLong(55);
        strAxisLabels = new String[2];
        strAxisLabels = inf_regression_Controller.getAxisLabels();
        returnStatus = "OK";
        subTitle = inf_regression_Controller.getSubTitle();
    }
    
    public void pearsonRInferenceCalculations() {
        double arctanh_r = 0.5 * Math.log((1.0 + pearsonsR) / (1.0 - pearsonsR));
        pearson_95CI_Lo = Math.tanh((arctanh_r - 1.959964 / Math.sqrt((double)nPoints - 3.0)));
        pearson_95CI_Hi = Math.tanh((arctanh_r + 1.959964 / Math.sqrt((double)nPoints - 3.0))); 
    }
        
   public static String getLeftMostNChars(String original, int leftChars) {
       return StringUtilities.getleftMostNChars(original, leftChars);
   }

   public void printStatistics() {   
       regressionReport = new ArrayList<>();
       regressionDiagnostics = new ArrayList<>();
       statsReport   = new ArrayList<>();

       print_ParamEstimates();
       print_ANOVA_Table();
       print_Diagnostics(); 
       print_BivStats();
   }
   
   public void print_ANOVA_Table() {
        addNBlankLinesToRegressionReport(2);
        regressionReport.add("                            Analysis of Variance");
        addNBlankLinesToRegressionReport(2);
        regressionReport.add(String.format("Source of           Sum of     Degrees of"));
        addNBlankLinesToRegressionReport(1);
        regressionReport.add(String.format("Variation           Squares      Freedom         Mean Square       F         P-value"));
        addNBlankLinesToRegressionReport(1);
        regressionReport.add(String.format(fiftyOneDashesLong));
        addNBlankLinesToRegressionReport(1);
        sourceString = getLeftMostNChars("Regression", 12);
        regressionReport.add(String.format("%12s    %13.3f      %4d             %8.2f   %8.3f      %6.4f", sourceString,  ssReg,  dfReg,  msReg,  fStatistic, pValue_F));
        addNBlankLinesToRegressionReport(1);
        sourceString = getLeftMostNChars("Residual", 12);
        regressionReport.add(String.format("%12s    %13.3f      %4d             %8.2f", sourceString, ssResid, dfResid,  msResid));
        addNBlankLinesToRegressionReport(1);
        sourceString = getLeftMostNChars("Total", 12);
        regressionReport.add(String.format("%12s    %13.3f      %4d\n", sourceString, ssTotal, dfTotal));
        regressionReport.add(String.format(fiftyOneDashesLong));
        addNBlankLinesToRegressionReport(1);
   }
   
   public void print_Diagnostics() {
       double jjResid, jjStandResid, jjStudResid, jjLeverage, jjCooksD, jjRStud;           
       addNBlankLinesToDiagnosticReport(2);
        regressionDiagnostics.add("                                    Regression Diagnostics");
        addNBlankLinesToDiagnosticReport(1);
        regressionDiagnostics.add(String.format(fiftyFiveDashesLong));
        addNBlankLinesToDiagnosticReport(1);
        regressionDiagnostics.add("                                          Studentized     Studentized");
        addNBlankLinesToDiagnosticReport(1);
        regressionDiagnostics.add(String.format("                           Standardized   (Internal)      (External)"));
        addNBlankLinesToDiagnosticReport(1);
        regressionDiagnostics.add(String.format("Observation     Residual     Residual       Residual       Residual   Leverage      Cook's D"));
        addNBlankLinesToDiagnosticReport(1);
        regressionDiagnostics.add(String.format(fiftyFiveDashesLong));
        addNBlankLinesToDiagnosticReport(1);
       
        leverageWarningTrigger = 2.0 * p / n; //  p213
       
        for (int jj = 0; jj < n; jj++) {
            int jjIndex = jj + 1;
            jjResid = mat_Resids.get(jj, 0);
            jjStandResid = mat_StandResids.get(jj, 0);
            jjStudResid = mat_StudResids.get(jj, 0);
            jjLeverage = mat_Hat.get(jj, jj);  
            leverage[jj] = jjLeverage;
            jjCooksD = mat_CooksD.get(jj, 0);
            jjRStud = mat_RStudent.get(jj, 0); 
            regressionDiagnostics.add(String.format(" %5d         %8.3f     %8.3f       %8.3f       %8.3f    %8.3f     %8.4f", 
                             jjIndex, jjResid,  jjStandResid,  jjStudResid,  jjRStud, jjLeverage, jjCooksD));
            addNBlankLinesToDiagnosticReport(1);
        }
       
        //  Print diagnostic advisories
        int dfR_Student = n - p - 1;
        addNBlankLinesToDiagnosticReport(1);
        regressionDiagnostics.add(String.format(fiftyOneDashesLong));
        addNBlankLinesToDiagnosticReport(3);
        regressionDiagnostics.add(String.format("%30s %3d %3s",  "   Note: If regression assumptions are true, R-Student has a t distribution with", dfR_Student, "df."));
        addNBlankLinesToDiagnosticReport(1);
        regressionDiagnostics.add(String.format("%10s%4.3f %20s", "   Note: Points with Leverage > ", leverageWarningTrigger, "are potentially high leverage points."));
        addNBlankLinesToDiagnosticReport(1);
        regressionDiagnostics.add(String.format("   Note: Points with Cook's D values > 1.0 are potentially high influence points.")); 
        
        // Last two lines to give space in the scrollPane
        addNBlankLinesToDiagnosticReport(2);
   }
   
   public void print_ParamEstimates() {
        double lowBound, daParam, hiBound;
        String parameter;
        addNBlankLinesToRegressionReport(2);
        
        // Print equation on one line if simple regression
        if (k == 1)  {
            String respVsExplan = StringUtilities.centerTextInString(subTitle, 80);
            regressionReport.add(respVsExplan);
            addNBlankLinesToRegressionReport(2);
            sourceString = "The regression equation is:";
            responseLabel2Print = getLeftMostNChars(paramTerm[2], 10) + " = ";
            responseLabelForSummary = getLeftMostNChars(paramTerm[2], 10);
            explanLabel2Print = getLeftMostNChars(paramTerm[1], 10);
            String tempRegrEq = String.format(" %20s  %10s %8.3f %3s %8.3f %10s",
                                 sourceString, responseLabel2Print, mat_BetaHats.get(0, 0), "+",
                                 mat_BetaHats.get(1, 0), explanLabel2Print
                              );
            deBlankedRegEq = StringUtilities.eliminateMultipleBlanks(tempRegrEq);
            regressionReport.add(deBlankedRegEq);                     
        }        
        else {
            sourceString = getLeftMostNChars(paramTerm[theYVariable] + bunchaBlanks, 10);
            regressionReport.add(String.format("%10s  %8.3f", sourceString, mat_BetaHats.get(0, 0)));
        
            for (int jj = 1; jj <= k; jj++) {
                sourceString = getLeftMostNChars(paramTerm[jj] + bunchaBlanks, 10);
                regressionReport.add(String.format(" %1s %8.3f %10s", "+", mat_BetaHats.get(jj, 0), sourceString));
             }
            
        }    
        addNBlankLinesToRegressionReport(1);
        regressionReport.add(String.format(fortyFiveDashesLong));
        addNBlankLinesToRegressionReport(2);
        regressionReport.add(String.format("Parameter Estimates"));
        addNBlankLinesToRegressionReport(1);
        regressionReport.add(String.format("       Term                Estimate     Std Error     t Ratio      P-value"));
        addNBlankLinesToRegressionReport(1);
        regressionReport.add(String.format(fortyFiveDashesLong));
        addNBlankLinesToRegressionReport(1);
       
        for (int jj = 0; jj <= k; jj++) {
          sourceString = getLeftMostNChars(paramTerm[jj] + bunchaBlanks, 21); 
          paramEst = mat_BetaHats.get(jj, 0);
          paramStdErr = stErrCoef.get(jj, 0);
          paramTRatio = tStat.get(jj, 0);
          paramPValue = PValue_T.get(jj, 0);
          
          regressionReport.add(String.format("%20s     %9.5f     %9.5f    %9.5f      %6.5f", sourceString,  paramEst,  paramStdErr,  paramTRatio,  paramPValue));
          addNBlankLinesToRegressionReport(1);
        }    
       
        addNBlankLinesToRegressionReport(1); 
        regressionReport.add(String.format("%4s  %6.3f    %8s %5.3f     %12s  %5.3f", "S = ", s, "R-sq = ", r2, "R-sq(adj) = ", adj_r2));
        addNBlankLinesToRegressionReport(1);
        
        x2Dist = new ChiSquareDistribution(dfResid);
        
        double theCriticalValue = Math.abs(tDist.getInvRightTailArea(0.975));
        addNBlankLinesToRegressionReport(1);
        regressionReport.add(String.format(fortyFiveDashesLong));
        addNBlankLinesToRegressionReport(1);
        regressionReport.add(String.format("%40s", "                           95% Confidence interval"));
        addNBlankLinesToRegressionReport(1);
        regressionReport.add(String.format("     Parameter            Lower bound       Estimate         Upper Bound"));
        addNBlankLinesToRegressionReport(1);
        
        parameter = getLeftMostNChars(paramTerm[0] + bunchaBlanks, 21);
        daParam = mat_BetaHats.get(0, 0);
        
        double fudgeFactor = 1.7;   // To put the JointCI inside the graph
        
        lowBound = daParam - theCriticalValue * stErrCoef.get(0, 0);        
        hiBound = daParam + theCriticalValue * stErrCoef.get(0, 0);
        ciBeta_0 = new Point_2D(lowBound, hiBound); //  For joint CI
        
        fudgedLowerXBound = daParam - fudgeFactor * theCriticalValue * stErrCoef.get(0, 0);  
        fudgedUpperXBound = daParam + fudgeFactor * theCriticalValue * stErrCoef.get(0, 0);
        initialScaleLimits_0 = new Point_2D(fudgedLowerXBound, fudgedUpperXBound);
        
        regressionReport.add(String.format(" %20s     %9.5f         %9.5f        %9.5f", parameter,  lowBound,  daParam,  hiBound));
        addNBlankLinesToRegressionReport(1);
        
        parameter = getLeftMostNChars(paramTerm[1] + bunchaBlanks, 21);
        daParam = mat_BetaHats.get(1, 0);
        lowBound = daParam - theCriticalValue * stErrCoef.get(1, 0);        
        hiBound = daParam + theCriticalValue * stErrCoef.get(1, 0);
        ciBeta_1 = new Point_2D(lowBound, hiBound); //  For joint CI        
        initialLowerYBound = daParam - fudgeFactor * theCriticalValue * stErrCoef.get(1, 0);  
        initialUpperYBound = daParam + fudgeFactor * theCriticalValue * stErrCoef.get(1, 0);

        initialScaleLimits_1 = new Point_2D(initialLowerYBound, initialUpperYBound);
        
        regressionReport.add(String.format(" %20s     %9.5f         %9.5f        %9.5f", parameter,  lowBound,  daParam,  hiBound));
        addNBlankLinesToRegressionReport(1);
        
        parameter = getLeftMostNChars("Error" + bunchaBlanks, 21);
        double x2Low = x2Dist.getInvLeftTailArea(0.025);
        double x2Hi = x2Dist.getInvRightTailArea(0.025);
        lowBound = Math.sqrt(dfResid * msResid / x2Hi);
        daParam = s;
        hiBound = Math.sqrt(dfResid * msResid / x2Low);
        regressionReport.add(String.format(" %20s     %9.5f         %9.5f        %9.5f", parameter,  lowBound,  daParam,  hiBound));
        addNBlankLinesToRegressionReport(1);

        parameter = getLeftMostNChars("Pearson's r" + bunchaBlanks, 21);
        lowBound = pearson_95CI_Lo;
        daParam = pearsonsR;
        hiBound = pearson_95CI_Hi;
        regressionReport.add(String.format(" %20s     %9.5f         %9.5f        %9.5f", parameter,  lowBound,  daParam,  hiBound));        
    
        addNBlankLinesToRegressionReport(1);
        regressionReport.add(String.format(fortyFiveDashesLong));       
   }
   
   public Data_Manager getDataManager() { return dm; }
   
    private void addNBlankLinesToDiagnosticReport(int thisMany) {
        StringUtilities.addNLinesToArrayList(regressionDiagnostics, thisMany);
    }
}