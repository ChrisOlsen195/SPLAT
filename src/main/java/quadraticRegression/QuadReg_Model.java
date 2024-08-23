/**************************************************
 *                 QuadReg_Model                  *
 *                    11/05/23                    *
 *                     18:00                      *
 *************************************************/
package quadraticRegression;

import matrixProcedures.Matrix;
import dataObjects.QuantitativeDataVariable;
import utilityClasses.StringUtilities;
import java.util.ArrayList;
import probabilityDistributions.ChiSquareDistribution;
import probabilityDistributions.FDistribution;
import probabilityDistributions.TDistribution;
import proceduresOneUnivariate.PrintUStats_Model;
import splat.*;
import utilityClasses.DataUtilities;
import utilityClasses.MyAlerts;

public class QuadReg_Model 
{     
    // POJOs
    static int n, p, k, dfReg, dfResid, dfTotal, nPoints;
    static int nColumns, nRows, theYVariable; 

    private double ssTotal, ssResid, ssReg, sumY2_Over_n, msReg, msResid, 
           fStatistic, pValue_F, s, r2, adj_r2, paramEst, paramStdErr, 
           paramFRatio, paramPValue, slope, pearsonsR;
    
    private double[] leverage, predYs, resids;
    
    private double[] quadRegCoeffs;
    
    private String sourceString, explanatoryVariable, 
            responseVariable, respVsExplanVar, saveTheResids, saveTheHats, 
            explanLabelForSummary, responseLabelForSummary;
    
    String fortyFiveDashesLong, fiftyOneDashesLong, fiftyFiveDashesLong,
           strResid, strPredY, trimStrResid, trimStrPredY;
    
    String waldoFile;
            
    final String bunchaBlanks = "                                ";
    String[] paramTerm;
    String strAxisLabels[];
    ArrayList<String> quadRegReport, quadRegDiagnostics, quadReg_StatsReport;
    
    // My classes

    Matrix mat_X, XVar, mat_Y, mat_XPrime, mat_XPrimeX, mat_InvXPrimeX, mat_Hat, 
           mat_BetaHats, mat_YHats, /*XVar_4Scatter, YVar_4Scatter,*/ BXY, SSRes, 
           Y_Prime_Y, stErrCoef, tStat, PValue_T, mat_Resids, mat_StandResids, 
           mat_StudResids, mat_CooksD, mat_RStudent;

    static ChiSquareDistribution x2Dist;
    Data_Manager dm;
    static FDistribution fDist;
    QuantitativeDataVariable qdv_X, qdv_Y, qdv_Resids, qdv_PredYs; 
    QuadReg_Controller quadReg_Controller;
    static PositionTracker tracker;
    static TDistribution tDist;

    // POJOs / FX
    
    public QuadReg_Model(QuadReg_Controller quadReg_Controller)  {   
        this.quadReg_Controller = quadReg_Controller;
        dm = quadReg_Controller.getDataManager();
        //waldoFile = "QuadReg_Model";
        waldoFile = "";
        dm.whereIsWaldo(70, waldoFile, "Constructing");
        tracker = quadReg_Controller.getDataManager().getPositionTracker();
        explanatoryVariable = quadReg_Controller.getExplanVar();
        responseVariable = quadReg_Controller.getResponseVar();
        respVsExplanVar = quadReg_Controller.getSubTitle();
        saveTheHats = quadReg_Controller.getSaveTheHats(); 
        saveTheResids = quadReg_Controller.getSaveTheResids();
        fortyFiveDashesLong = StringUtilities.getUnicodeLineThisLong(45);
        fiftyOneDashesLong = StringUtilities.getUnicodeLineThisLong(51);
        fiftyFiveDashesLong = StringUtilities.getUnicodeLineThisLong(55);
        strAxisLabels = new String[2];
        strAxisLabels[0] = quadReg_Controller.getExplanVar();
        strAxisLabels[1] = quadReg_Controller.getResponseVar();
    }
    
    //  The QDVs are needed for labels
    public void setupQuadRegAnalysis(QuantitativeDataVariable theXs, QuantitativeDataVariable theYs) {   
        qdv_X = theXs;
        qdv_Y = theYs;
        
        /*  Calculations for Spearman's rho     */
        ArrayList<String> alXs = new ArrayList();
        ArrayList<String> alYs = new ArrayList();
        
        alXs = theXs.getAllTheCasesAsALStrings();
        alYs = theYs.getAllTheCasesAsALStrings();
        
        int nXSize = alXs.size();
        int nYSize = alYs.size();
        
        if (nXSize != nYSize) {
            MyAlerts.showUnequalNsInBivariateProcessAlert();
            return;
        }
        
        nPoints = nXSize;

        nRows = Math.min(theXs.get_nDataPointsLegal(), theYs.get_nDataPointsLegal());
        nColumns = 3;
        theYVariable = 1;    //  Hard coded for simple reg       
        mat_X = new Matrix (nRows, 3);  //  Hard coded for quadratic reg
        mat_Y = new Matrix (nRows, 1);
        XVar = new Matrix(nRows, 1); // For the scatterplot

        // Add column of 1's to X matrix as column[0]
        double sumY = 0.0;

        for (int ithRow = 0; ithRow < nRows; ithRow++) {
            double tempY = theYs.getIthDataPtAsDouble(ithRow);
            mat_Y.set(ithRow, 0, tempY);
            sumY += tempY;
            double tempX = theXs.getIthDataPtAsDouble(ithRow);           
            mat_X.set(ithRow, 0, 1.0);
            mat_X.set(ithRow, 1, tempX);
            mat_X.set(ithRow, 2, tempX * tempX);
            XVar.set(ithRow, 0, tempX);  //  For scatterplot
        }
        
        sumY2_Over_n = sumY * sumY / (double)nRows;
        paramTerm = new String[4];  // Hard coded for simple reg
        paramTerm[0] = "Intercept"; 
        paramTerm[1] = getLeftMostNChars(explanatoryVariable, 18);
        String preParam2 = explanatoryVariable + "^2"; 
        paramTerm[2] = getLeftMostNChars(preParam2 , 18);   
    }
    
    public String doQuadRegRegressionAnalysis()
    {
        //  Set up variables and matrices
        double jjResid;
        double tempDouble_01, tempDouble_02, tempDouble_03, tempDouble_04;
        n = mat_Y.getRowDimension();
        p = mat_X.getColumnDimension();    
        k = p - 1;                      // k is number of explanatory variables
        mat_Resids = new Matrix(n, 1);
        mat_StandResids = new Matrix(n, 1);
        mat_StudResids = new Matrix(n, 1);
        mat_CooksD = new Matrix(n, 1);
        mat_RStudent = new Matrix(n, 1);

        // MVP, p73
        mat_XPrime = mat_X.transpose();
        mat_XPrimeX = mat_XPrime.times(mat_X);  // OK
        mat_InvXPrimeX = mat_XPrimeX.inverse();   // OK
        mat_Hat = mat_X.times(mat_InvXPrimeX.times(mat_XPrime)); // OK
        mat_BetaHats = mat_InvXPrimeX.times(mat_XPrime.times(mat_Y));   //  OK
        mat_YHats = mat_Hat.times(mat_Y); // OK
        leverage = new double[n];

        // Calculate Sums of Squares
        BXY = (mat_BetaHats.transpose()).times(mat_XPrime.times(mat_Y));
        Y_Prime_Y = (mat_Y.transpose()).times(mat_Y);
        
        ssReg = BXY.get(0,0) - sumY2_Over_n;
        SSRes = Y_Prime_Y.minus(BXY);   //  This is the Matrix
        ssResid = SSRes.get(0,0);       // This is the scalar
        ssTotal = Y_Prime_Y.get(0,0) - sumY2_Over_n;  
        
        // Calculate regression summary
        r2 = ssReg / ssTotal;       
        slope = mat_BetaHats.get(1, 0);
        
        if (slope >= 0) { pearsonsR = Math.sqrt(r2); }  
        else { pearsonsR = -Math.sqrt(r2); }
 
        s = Math.sqrt(ssResid / (n - k - 1));
        adj_r2 = 1.0 - (ssResid / (n - k - 1)) / (ssTotal / (n - 1));
       
        // Calculations for ANOVA table
        dfReg = k;
        dfResid = n - k - 1;

        if (dfResid < 1) {
            MyAlerts.showTooFewMultRegDFAlert();
            return "Cancel";
        }          
                
        dfTotal = n - 1;

        msReg = ssReg / (double)k;
        msResid = ssResid / (double)(n - k - 1);
        fStatistic = msReg / msResid;
       
        fDist = new FDistribution(k, n - k - 1);
        pValue_F = fDist.getRightTailArea(fStatistic);
       
        // Calculations for diagnostics
        mat_Resids = mat_Y.minus(mat_YHats);
        tempDouble_01 = 1.0 / Math.sqrt(msResid);
        mat_StandResids = mat_Resids.times(tempDouble_01);
        for (int jj = 0; jj < n; jj++) { 
           tempDouble_02 = mat_Hat.get(jj, jj);
           mat_StudResids.set(jj, 0, mat_Resids.get(jj, 0) * tempDouble_01 / Math.sqrt(1.0 - tempDouble_02));
           tempDouble_03 = mat_StudResids.get(jj, 0) * mat_StudResids.get(jj, 0) / (double)p;
           tempDouble_04 = tempDouble_02 / (1.0 - tempDouble_02);
           mat_CooksD.set(jj, 0, tempDouble_03 * tempDouble_04);
           
           // Student-R calculations from 4.12, 4.13, p135
           jjResid = mat_Resids.get(jj, 0);
           double e_i_sq = jjResid * jjResid;   
           double oneMinus_hii = 1.0 - mat_Hat.get(jj, jj);
           double s_i_sq = ((n - p)*msResid - e_i_sq/oneMinus_hii) / (n - p - 1);
           mat_RStudent.set(jj, 0, jjResid / Math.sqrt(s_i_sq * oneMinus_hii));        
        }

        stErrCoef = new Matrix(k + 1, 1);  // Explanatory variables + intercept 
        tStat = new Matrix(k + 1, 1);  
        PValue_T = new Matrix(k + 1, 1); 
        tDist = new TDistribution (n - k - 1);

        for (int predictors = 0; predictors <= k; predictors++) {
           stErrCoef.set(predictors, 0, Math.sqrt(msResid * mat_InvXPrimeX.get(predictors, predictors)));
           tStat.set(predictors, 0, mat_BetaHats.get(predictors, 0) / stErrCoef.get(predictors, 0));
           PValue_T.set(predictors, 0, 2.0 * tDist.getRightTailArea(Math.abs(tStat.get(predictors, 0))));
        }
        
        qdv_Resids = new QuantitativeDataVariable(dm, "Residuals", "Residuals", mat_Resids);
        qdv_PredYs = new QuantitativeDataVariable(dm, "Y-Hats", "Y-Hats", mat_YHats);
        
        if (saveTheResids.equals("Yes") || saveTheHats.equals("Yes")) {                
            if (saveTheResids.equals("Yes") && saveTheHats.equals("Yes")){
                dm.addToStructOneColumnWithExistingQuantData(qdv_Resids);
                dm.addToStructOneColumnWithExistingQuantData(qdv_PredYs);
                tracker.setNVarsCommitted(tracker.getNVarsCommitted() + 2);
            }  else
            if (saveTheResids.equals("Yes")) {
                dm.addToStructOneColumnWithExistingQuantData(qdv_Resids);
                tracker.setNVarsCommitted(tracker.getNVarsCommitted() + 1);
            } else {
                dm.addToStructOneColumnWithExistingQuantData(qdv_PredYs);
                tracker.setNVarsCommitted(tracker.getNVarsCommitted() + 1);
            }
   
            // nVarsNow AFTER adding resids and/or preds
            int nVarsNow = dm.getNVarsInStruct();
            int nCasesNow = dm.getNCasesInStruct();
            predYs = new double[nCasesNow];
            resids = new double[nCasesNow];
            
            // Must go back to original data file and calculate the 
            // residuals and yHats because some data might be missing.             
            for (int ithCase = 0; ithCase < nCasesNow; ithCase++) {                
                if (DataUtilities.strIsADouble(qdv_X.getIthDataPtAsString(ithCase))
                   && DataUtilities.strIsADouble(qdv_X.getIthDataPtAsString(ithCase)) ){
                double xValue  = qdv_X.getIthDataPtAsDouble(ithCase);
                double yValue = qdv_Y.getIthDataPtAsDouble(ithCase);
                predYs[ithCase] = mat_BetaHats.get(0, 0) 
                                      + mat_BetaHats.get(1, 0) * xValue 
                                      + mat_BetaHats.get(2, 0) * xValue * xValue;
                resids[ithCase] = yValue - predYs[ithCase];
                strResid = Double.toString(resids[ithCase]);
                strPredY = Double.toString(predYs[ithCase]);
                trimStrResid = strResid.trim();
                trimStrPredY = strPredY.trim();
                } else {
                    trimStrResid = "  *  ";
                    trimStrPredY = "  *  ";
                } 

                if (saveTheResids.equals("Yes") || saveTheHats.equals("Yes")) {                    
                   if (saveTheResids.equals("Yes") && saveTheHats.equals("Yes")){
                        dm.getDataStruct().get(nVarsNow - 2)
                          .getTheCases_ArrayList()
                          .set(ithCase, trimStrResid);

                        dm.getDataStruct().get(nVarsNow - 1)
                          .getTheCases_ArrayList()
                          .set(ithCase, trimStrPredY);
                
                    }  else
                       if (saveTheResids.equals("Yes")){
                        dm.getDataStruct().get(nVarsNow - 1)
                          .getTheCases_ArrayList()
                          .set(ithCase, trimStrResid);

                    } else {
                        dm.getDataStruct().get(nVarsNow - 1)
                          .getTheCases_ArrayList()
                          .set(ithCase, trimStrPredY);
                    }
                }
            }   //  end cases
            if (saveTheResids.equals("Yes") || saveTheHats.equals("Yes")) {                
                if (saveTheResids.equals("Yes") && saveTheHats.equals("Yes")){
                    dm.getDataStruct().get(nVarsNow - 2).formatTheColumn();
                    dm.getDataStruct().get(nVarsNow - 1).formatTheColumn();
                }  else {
                    if (saveTheResids.equals("Yes")) 
                        dm.getDataStruct().get(nVarsNow - 1).formatTheColumn();
                    else
                        dm.getDataStruct().get(nVarsNow - 1).formatTheColumn();  
                }
            }
        }        
        printStatistics();
        return "OK";
   }
    

   public static String getLeftMostNChars(String original, int leftChars) {
       return StringUtilities.getleftMostNChars(original, leftChars);
   }

   public void printStatistics() {   
       quadRegReport = new ArrayList<>();
       quadRegDiagnostics = new ArrayList<>();
       quadReg_StatsReport   = new ArrayList<>();

       print_ParamEstimates();
       print_ANOVA_Table();
       print_Diagnostics(); 
       print_BivStats();
   }
   
   public void print_ANOVA_Table() {
        addNBlankLinesToRegressionReport(2);
        quadRegReport.add("                            Analysis of Variance");
        addNBlankLinesToRegressionReport(2);
        quadRegReport.add(String.format("Source of           Sum of     Degrees of"));
        addNBlankLinesToRegressionReport(1);
        quadRegReport.add(String.format("Variation           Squares      Freedom         Mean Square       F         P-value"));
        addNBlankLinesToRegressionReport(1);
        quadRegReport.add(String.format(fiftyOneDashesLong));
        addNBlankLinesToRegressionReport(1);
        sourceString = getLeftMostNChars("Regression", 12);
        quadRegReport.add(String.format("%12s    %13.3f      %4d             %8.2f     %8.3f      %6.4f", sourceString,  ssReg,  dfReg,  msReg,  fStatistic, pValue_F));
        addNBlankLinesToRegressionReport(1);
        sourceString = getLeftMostNChars("Residual", 12);
        quadRegReport.add(String.format("%12s    %13.3f      %4d             %8.2f", sourceString, ssResid, dfResid,  msResid));
        addNBlankLinesToRegressionReport(1);
        sourceString = getLeftMostNChars("Total", 12);
        quadRegReport.add(String.format("%12s    %13.3f      %4d\n", sourceString, ssTotal, dfTotal));
        quadRegReport.add(String.format(fiftyOneDashesLong));
        addNBlankLinesToRegressionReport(1);
   }
   
   public void print_Diagnostics() {
       double jjResid, jjStandResid, jjStudResid, jjLeverage, jjCooksD, jjRStud;           
       addNBlankLinesToDiagnosticReport(2);
        quadRegDiagnostics.add("                                    Regression Diagnostics");
        addNBlankLinesToDiagnosticReport(1);
        quadRegDiagnostics.add(String.format(fiftyFiveDashesLong));
        addNBlankLinesToDiagnosticReport(1);
        quadRegDiagnostics.add("                                          Studentized     Studentized");
        addNBlankLinesToDiagnosticReport(1);
        quadRegDiagnostics.add(String.format("                           Standardized   (Internal)      (External)"));
        addNBlankLinesToDiagnosticReport(1);
        quadRegDiagnostics.add(String.format("Observation     Residual     Residual       Residual       Residual   Leverage      Cook's D"));
        addNBlankLinesToDiagnosticReport(1);
        quadRegDiagnostics.add(String.format(fiftyFiveDashesLong));
        addNBlankLinesToDiagnosticReport(1);       
        double leverageWarningTrigger = 2.0 * p / n; //  p213
       
        for (int jj = 0; jj < n; jj++) {
            int jjIndex = jj + 1;
            jjResid = mat_Resids.get(jj, 0);
            jjStandResid = mat_StandResids.get(jj, 0);
            jjStudResid = mat_StudResids.get(jj, 0);
            jjLeverage = mat_Hat.get(jj, jj);  
            leverage[jj] = jjLeverage;
            jjCooksD = mat_CooksD.get(jj, 0);
            jjRStud = mat_RStudent.get(jj, 0); 
            quadRegDiagnostics.add(String.format(" %5d         %8.3f     %8.3f       %8.3f       %8.3f    %8.3f     %8.4f", 
                             jjIndex, jjResid,  jjStandResid,  jjStudResid,  jjRStud, jjLeverage, jjCooksD));
            addNBlankLinesToDiagnosticReport(1);
        }
       
        int dfR_Student = n - p - 1;
        addNBlankLinesToDiagnosticReport(1);
        quadRegDiagnostics.add(String.format(fiftyOneDashesLong));
        addNBlankLinesToDiagnosticReport(3);
        quadRegDiagnostics.add(String.format("%30s %3d %3s",  "   Note: If regression assumptions are true, R-Student has a t distribution with", dfR_Student, "df."));
        addNBlankLinesToDiagnosticReport(1);
        quadRegDiagnostics.add(String.format("%10s%4.3f %20s", "   Note: Points with Leverage > ", leverageWarningTrigger, "are potentially high leverage points."));
        addNBlankLinesToDiagnosticReport(1);
        quadRegDiagnostics.add(String.format("   Note: Points with Cook's D values > 1.0 are potentially high influence points.")); 
        addNBlankLinesToDiagnosticReport(2);
   }
   
   public void print_ParamEstimates() {
        addNBlankLinesToRegressionReport(2);
        String sourceString0, sourceString1, sourceString2, sourceString3;

        // Print equation on one line if simple regression
        String respVsExplan = StringUtilities.centerTextInString(quadReg_Controller.getSubTitle(), 80);
        quadRegReport.add(respVsExplan);
        addNBlankLinesToRegressionReport(2);
        sourceString0 = "The regression equation is:";
        sourceString1 = getLeftMostNChars(responseVariable, 10) + " = ";        //  y ==
        responseLabelForSummary = getLeftMostNChars(responseVariable, 10);      
        sourceString2 = getLeftMostNChars("* " + explanatoryVariable, 10);      // * x
        explanLabelForSummary = getLeftMostNChars(explanatoryVariable , 10);
        sourceString3 = getLeftMostNChars(explanatoryVariable, 10) + "^2";     // * x^2
        
        quadRegReport.add(sourceString0);
        addNBlankLinesToRegressionReport(1);

        String tempRegrEq = String.format(" %10s %6.3f %3s %6.3f  %3s %10s %6.3f %10s",
                             sourceString1, mat_BetaHats.get(0, 0), "+",
                             mat_BetaHats.get(1, 0), sourceString2, "+", 
                             mat_BetaHats.get(2, 0), sourceString3
                          );   
        
        String deBlankedRegEq = StringUtilities.eliminateMultipleBlanks(tempRegrEq);
        quadRegReport.add(deBlankedRegEq );
   
        addNBlankLinesToRegressionReport(2);
        quadRegReport.add(String.format(fortyFiveDashesLong));
        addNBlankLinesToRegressionReport(1);
        quadRegReport.add(String.format("Parameter Estimates"));
        addNBlankLinesToRegressionReport(1);
        quadRegReport.add(String.format("       Term                Estimate     Std Error     t Ratio      P-value"));
        addNBlankLinesToRegressionReport(1);
        quadRegReport.add(String.format(fortyFiveDashesLong));
        addNBlankLinesToRegressionReport(1);
       
        for (int jj = 0; jj <= k; jj++) {
          sourceString = getLeftMostNChars(paramTerm[jj] + bunchaBlanks, 21); 
          paramEst = mat_BetaHats.get(jj, 0);
          paramStdErr = stErrCoef.get(jj, 0);
          paramFRatio = tStat.get(jj, 0);
          paramPValue = PValue_T.get(jj, 0);
          
          quadRegReport.add(String.format("%20s     %9.5f     %9.5f    %9.5f      %6.5f", sourceString,  paramEst,  paramStdErr,  paramFRatio,  paramPValue));
          addNBlankLinesToRegressionReport(1);
        }    
       
        addNBlankLinesToRegressionReport(1); 
        quadRegReport.add(String.format("%4s  %6.3f    %8s %5.3f     %12s  %5.3f", "S = ", s, "R-sq = ", r2, "R-sq(adj) = ", adj_r2));
        addNBlankLinesToRegressionReport(1);
        
        x2Dist = new ChiSquareDistribution(dfResid);
        
        //double theCriticalValue = tDist.getInvRightTailArea(0.975);
        addNBlankLinesToRegressionReport(2);
        quadRegReport.add(String.format(fortyFiveDashesLong));
        addNBlankLinesToRegressionReport(1);
        
   }
      
    private void print_BivStats() {
        int int_1, int_2;
        double dbl_1, dbl_2;
        String str_1, str_2;
        
        PrintUStats_Model prntU_X = new PrintUStats_Model(explanLabelForSummary , qdv_X, false);
        PrintUStats_Model prntU_Y = new PrintUStats_Model(responseLabelForSummary, qdv_Y, true);        
        addNBlankLinesToBivStatsReport(2);
        quadReg_StatsReport.add("       Bivariate statistics summary");
        addNBlankLinesToBivStatsReport(2);    
        
        quadReg_StatsReport.add(String.format("     *******  File information  *******"));
        addNBlankLinesToBivStatsReport(2);   
        str_1 = StringUtilities.truncateString(prntU_X.getVarDescr() + bunchaBlanks, 10);
        str_2 = StringUtilities.truncateString(prntU_Y.getVarDescr() + bunchaBlanks, 10);
        quadReg_StatsReport.add(String.format("       Variable: %10s   %10s", str_1, str_2));
        addNBlankLinesToBivStatsReport(1);   
        int_1 = prntU_X.getOriginalN();
        int_2 = prntU_Y.getOriginalN();        
        quadReg_StatsReport.add(String.format("      N in file:   %4d         %4d", int_1, int_2));
        addNBlankLinesToBivStatsReport(1);  
        int_1 = prntU_X.getMissingN();
        int_2 = prntU_Y.getMissingN(); 
        quadReg_StatsReport.add(String.format("      N missing:   %4d         %4d", int_1, int_2));        
        addNBlankLinesToBivStatsReport(1);  
        int_1 = prntU_X.getLegalN();
        int_2 = prntU_Y.getLegalN(); 
        quadReg_StatsReport.add(String.format("        N Legal:   %4d         %4d", int_1, int_2)); 
        addNBlankLinesToBivStatsReport(3); 
        
        quadReg_StatsReport.add(String.format(" *****  Basic mean based statistics  *****"));
        addNBlankLinesToBivStatsReport(2);
        dbl_1 = prntU_X.getTheMean();
        dbl_2 = prntU_Y.getTheMean(); 
        quadReg_StatsReport.add(String.format("           Mean:   %8.4f   %8.4f", dbl_1, dbl_2));
        addNBlankLinesToBivStatsReport(1);
        dbl_1 = prntU_X.getTheVariance();
        dbl_2 = prntU_Y.getTheVariance(); 
        quadReg_StatsReport.add(String.format("       Variance: %10.4f %10.4f", dbl_1, dbl_2));        
        addNBlankLinesToBivStatsReport(1);
        dbl_1 = prntU_X.getTheSkew();
        dbl_2 = prntU_Y.getTheSkew(); 
        quadReg_StatsReport.add(String.format("           Skew:   %8.4f   %8.4f", dbl_1, dbl_2));
        addNBlankLinesToBivStatsReport(1);
        dbl_1 = prntU_X.getTheAdjSkew();
        dbl_2 = prntU_Y.getTheAdjSkew();
        quadReg_StatsReport.add(String.format("  Adjusted Skew:   %8.4f   %8.4f", dbl_1, dbl_2));        
        addNBlankLinesToBivStatsReport(3);
        
        quadReg_StatsReport.add(String.format("  *****  Other mean based statistics  *****"));        
        addNBlankLinesToBivStatsReport(2);
        dbl_1 = prntU_X.getTheTrimmedMean();
        dbl_2 = prntU_Y.getTheTrimmedMean();
        quadReg_StatsReport.add(String.format("   Trimmed mean:   %8.4f    %8.4f", dbl_1, dbl_2)); 
        addNBlankLinesToBivStatsReport(1);
        dbl_1 = prntU_X.getTheKurtosis();
        dbl_2 = prntU_Y.getTheKurtosis();
        quadReg_StatsReport.add(String.format("       Kurtosis:   %8.4f    %8.4f", dbl_1, dbl_2));        
        addNBlankLinesToBivStatsReport(1);
        dbl_1 = prntU_X.getTheCV();
        dbl_2 = prntU_Y.getTheCV();
        quadReg_StatsReport.add(String.format("             CV:   %8.4f    %8.4f", dbl_1, dbl_2));               
        addNBlankLinesToBivStatsReport(3); 
        
        quadReg_StatsReport.add(String.format("  *****     Five-number summaries    *****"));
        addNBlankLinesToBivStatsReport(2);
        dbl_1 = prntU_X.getTheMin();
        dbl_2 = prntU_Y.getTheMin();
        quadReg_StatsReport.add(String.format("        Minimum:   %8.4f   %8.4f", dbl_1, dbl_2)); 
        addNBlankLinesToBivStatsReport(1);
        dbl_1 = prntU_X.getQ1();
        dbl_2 = prntU_Y.getQ1();       
        quadReg_StatsReport.add(String.format("             Q1:   %8.4f   %8.4f", dbl_1, dbl_2));  
        addNBlankLinesToBivStatsReport(1);
        dbl_1 = prntU_X.getTheMedian();
        dbl_2 = prntU_Y.getTheMedian();
        quadReg_StatsReport.add(String.format("         Median:   %8.4f   %8.4f", dbl_1, dbl_2));        
        addNBlankLinesToBivStatsReport(1);
        dbl_1 = prntU_X.getQ3();
        dbl_2 = prntU_Y.getQ3();
        quadReg_StatsReport.add(String.format("             Q3:   %8.4f   %8.4f", dbl_1, dbl_2));        
        addNBlankLinesToBivStatsReport(1);
        dbl_1 = prntU_X.getTheMax();
        dbl_2 = prntU_Y.getTheMax();
        quadReg_StatsReport.add(String.format("        Maximum:   %8.4f   %8.4f", dbl_1, dbl_2));          
        addNBlankLinesToBivStatsReport(3);
        
        quadReg_StatsReport.add(String.format("  *****  Other median based statistics  *****"));  
        addNBlankLinesToBivStatsReport(2);
        dbl_1 = prntU_X.getTheIQR();
        dbl_2 = prntU_Y.getTheIQR();
        quadReg_StatsReport.add(String.format("            IQR:   %8.4f   %8.4f", dbl_1, dbl_2));        
        addNBlankLinesToBivStatsReport(1);
        dbl_1 = prntU_X.getTheRange();
        dbl_2 = prntU_Y.getTheRange();
        quadReg_StatsReport.add(String.format("          Range:   %8.4f   %8.4f", dbl_1, dbl_2));   
    }
   
    private void addNBlankLinesToRegressionReport(int thisMany) {
        StringUtilities.addNLinesToArrayList(quadRegReport, thisMany);
    }
    
    private void addNBlankLinesToDiagnosticReport(int thisMany) {
        StringUtilities.addNLinesToArrayList(quadRegDiagnostics, thisMany);
    }
    
    private void addNBlankLinesToBivStatsReport(int thisMany) {
        StringUtilities.addNLinesToArrayList(quadReg_StatsReport, thisMany);
    }
    
    public int getDF() { return nPoints; }
   
   public ArrayList<String> getRegressionReport() { return quadRegReport; }
   public ArrayList<String> getDiagnostics() { return quadRegDiagnostics; }   
   public ArrayList<String> getStatsReport() { return quadReg_StatsReport; }
   
   public int getNRows()    {return nRows;}
   
   public String getExplanatoryVariable() { return explanatoryVariable; }
   public String getResonseVariable() { return responseVariable; }
   
   public String getRespVsExplSubtitle() { return respVsExplanVar; }

   public String[] getAxisLabels() { return strAxisLabels; }
   public Matrix getXVar() {return XVar;}
   public Matrix getX() { return mat_X; }
   public Matrix getY() {return mat_Y;}
   public Matrix getYHats() {return mat_YHats;}
   
   public double[] getBetaHats() { 
       quadRegCoeffs = new double[3];
       for (int ithCoef = 0; ithCoef < 3; ithCoef++) {
           quadRegCoeffs[ithCoef] = mat_BetaHats.get(ithCoef, 0);
       }
       return quadRegCoeffs; 
   }
   
   public Matrix getResids() {return mat_Resids;}
   public Data_Manager getDataManager() { return dm; }
   public QuantitativeDataVariable getXVariable() {return qdv_X;}
   public QuantitativeDataVariable getYVariable() {return qdv_Y;} 
   public QuantitativeDataVariable getQDVResids() {return qdv_Resids;}
   public Matrix getStandardizedResids() {return mat_StandResids;}
   public Matrix getStudentizedResids() {return mat_StudResids;}
   public Matrix getR_StudentizedResids() {return mat_RStudent;}
   public Matrix getCooksD() { return mat_CooksD; }
   public double[] getLeverage() { return leverage; }
   public int getDF_Regression() { return dfReg; }
   public int getDF_Residuals() { return dfResid; }
   public double getFStat()  {return  fStatistic;}
   public double getPValue()  {return  paramPValue;}
}
