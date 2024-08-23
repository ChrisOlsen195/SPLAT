/**************************************************
 *                Regression_Model                *
 *                    11/01/23                    *
 *                     18:00                      *
 *************************************************/
/***************************************************
 *  All symbols, formulae, and page numbers are    *
 *  from Montgomery, Peck, Vining: Introduction to *
 *  Linear Regression Analysis (5th ed)            *
 **************************************************/
package superClasses;

import genericClasses.Point_2D;
import genericClasses.ResizableTextPane;
import matrixProcedures.Matrix;
import dataObjects.QuantitativeDataVariable;
import utilityClasses.StringUtilities;
import java.util.ArrayList;
import probabilityDistributions.ChiSquareDistribution;
import probabilityDistributions.FDistribution;
import probabilityDistributions.TDistribution;
import genericClasses.Transformations_Calculations;
import proceduresOneUnivariate.PrintUStats_Model;
import simpleRegression.*;
import splat.*;
import utilityClasses.*;

public class Regression_Model {     
    // POJOs
    public int n, p, k, dfReg, dfResid, dfTotal, nPoints, nCasesInStruct,
               nCases, nColumns, nRows, theYVariable, nMissing, nVarsCommitted,
               nResidsCalculated, nPredsCalculated, nVarsInStruct;

    public double ssTotal, ssResid, ssReg, sumY2_Over_n, msReg, msResid, 
                   fStatistic, pValue_F, s, r2, adj_r2, paramEst, paramStdErr, 
                   paramTRatio, paramPValue, spearmansRho, pearsonsR, 
                   dbl_n, leverageWarningTrigger, pearson_95CI_Lo, 
                   pearson_95CI_Hi, intercept, slope, fudgedLowerXBound,
                   fudgedUpperXBound, initialLowerYBound,
                   initialUpperYBound;
    
   public double[] leverage, predYs, resids;

   public String lineToPrint, sourceString, explanatoryVariable, responseVariable, 
            respVsExplanVar, saveTheResids, saveTheHats, returnStatus,
    
            strResid, strPredY, trimStrResid, trimStrPredY, 
            responseLabel2Print, explanLabel2Print, responseLabelForSummary, 
            fortyFiveDashesLong, fiftyOneDashesLong, fiftyFiveDashesLong, 
            subTitle;
   
   String deBlankedRegEq;
   
    // Make empty if no-print
    // String waldoFile = "Regression_Model";
    public String waldoFile = "";
    
   public String[] strAxisLabels, str_DataLabels, paramTerm;
   
    public String bunchaBlanks = "                                ";
    public ArrayList<String> regressionReport, regressionDiagnostics, statsReport;
    
    // My classes
    public Data_Manager dm;
    public Point_2D ciBeta_0, ciBeta_1, initialScaleLimits_0, initialScaleLimits_1;

    public Matrix mat_X, XVar, mat_Y, mat_XPrime, mat_XPrimeX, mat_InvXPrimeX, mat_Hat, 
           mat_BetaHats, mat_YHats, XVar_4Scatter, YVar_4Scatter, BXY, SSRes, 
           Y_Prime_Y, stErrCoef, tStat, PValue_T, mat_Resids, mat_StandResids, 
           mat_StudResids, mat_CooksD, mat_RStudent;

    public ChiSquareDistribution x2Dist;
    public FDistribution fDist;
    public QuantitativeDataVariable qdv_X, qdv_Y, qdv_Resids, qdv_PredYs;
    public ResizableTextPane rtp;   
    public Inf_Regression_Controller inf_regression_Controller;
    public NoInf_Regression_Controller noInf_Regression_Controller;
    public PositionTracker tracker;
    public TDistribution tDist;
    
    public Regression_Model(Inf_Regression_Controller inf_regression_Controller) {   
        this.inf_regression_Controller= inf_regression_Controller;
        dm = inf_regression_Controller.getDataManager();
        tracker = dm.getPositionTracker();
        dm.whereIsWaldo(90, waldoFile, "Constructing for Inf Reg");
        nVarsCommitted = tracker.getNVarsCommitted();
        nVarsInStruct = dm.getNVarsInStruct();
        subTitle = inf_regression_Controller.getSubTitle();
        explanatoryVariable = inf_regression_Controller.getExplanVar();
        responseVariable = inf_regression_Controller.getResponseVar();
        respVsExplanVar = inf_regression_Controller.getSubTitle();
        saveTheHats = inf_regression_Controller.getSaveTheHats();        
        saveTheResids = inf_regression_Controller.getSaveTheResids();
        fortyFiveDashesLong = StringUtilities.getUnicodeLineThisLong(45);
        fiftyOneDashesLong = StringUtilities.getUnicodeLineThisLong(51);
        fiftyFiveDashesLong = StringUtilities.getUnicodeLineThisLong(55);
        strAxisLabels = new String[2];
        strAxisLabels[0] = inf_regression_Controller.getExplanVar();
        strAxisLabels[1] = inf_regression_Controller.getResponseVar();
        paramTerm = new String[3];  // Hard coded for simple reg
        returnStatus = "OK";
    }
    
    public Regression_Model(NoInf_Regression_Controller noInf_Regression_Controller) {   
        this.noInf_Regression_Controller = noInf_Regression_Controller;
        dm = noInf_Regression_Controller.getDataManager(); 
        tracker = dm.getPositionTracker();
        dm.whereIsWaldo(108, waldoFile, "Constructing Inf Reg");
        nVarsCommitted = tracker.getNVarsCommitted();
        subTitle = noInf_Regression_Controller.getSubTitle();
        nCasesInStruct = dm.getNCasesInStruct();
        explanatoryVariable = noInf_Regression_Controller.getExplanVar();
        responseVariable = noInf_Regression_Controller.getResponseVar();
        respVsExplanVar = noInf_Regression_Controller.getSubTitle();
        saveTheHats = noInf_Regression_Controller.getSaveTheHats(); 
        saveTheResids = noInf_Regression_Controller.getSaveTheResids();
        fortyFiveDashesLong = StringUtilities.getUnicodeLineThisLong(45);
        strAxisLabels = new String[2];
        strAxisLabels[0] = noInf_Regression_Controller.getExplanVar();
        strAxisLabels[1] = noInf_Regression_Controller.getResponseVar();
        paramTerm = new String[3];  // Hard coded for simple reg
        returnStatus = "OK";
    }
    
    //  The QDVs are needed for labels; these are only the LEGAL points, not the column sizes
    public String setupRegressionAnalysis(QuantitativeDataVariable theXs, QuantitativeDataVariable theYs) {     
        dm.whereIsWaldo(127, waldoFile, "setupRegressionAnalysis()");
        nPoints = theXs.getLegalN();
        
        qdv_X = theXs;
        qdv_Y = theYs;
        
        /*  Calculations for Spearman's rho     */
        ArrayList<String> alXs = new ArrayList();
        ArrayList<String> alYs = new ArrayList();
        
        // These are actually only the "legal" points
        alXs = theXs.getAllTheCasesAsALStrings();
        alYs = theYs.getAllTheCasesAsALStrings();

        nMissing = nCasesInStruct - nPoints;
        
        if (nPoints < 3) {
            MyAlerts.showTwoPointsAlert();
            returnStatus = "Cancel";
            return returnStatus;         
        }
        
        // Check for straight line
        double firstX = qdv_X.getIthDataPtAsDouble(0);
        double firstY = qdv_Y.getIthDataPtAsDouble(0);
        double secondX = 0.0;   //  Temporarily!
        double secondY = 0.0;   //  Temporarily!

        // Find point with different firstX
        boolean diffXFound = false;
        
        // This is a simple algorithm -- it will return the last point with a
        // different X.  All that is needed is one different X, so no big.
        
        for (int restOfThePoints = 1; restOfThePoints < nPoints; restOfThePoints++) {
            double ithRestX = qdv_X.getIthDataPtAsDouble(restOfThePoints);
            
            if (firstX != ithRestX) {
                secondX = ithRestX;
                secondY = qdv_Y.getIthDataPtAsDouble(restOfThePoints);
                diffXFound = true;
            }   
        }
        
        if (!diffXFound) {
            MyAlerts.showInfiniteSlopeAlert();
            returnStatus = "Cancel";
            return returnStatus;             
        }
        
        //  Now check for different slopes
        boolean diffSlopesFound = false;
        double testSlope = (secondY - firstY) / (secondX - firstX);
        
        for (int restOfThePoints = 1; restOfThePoints < nPoints; restOfThePoints++) {
            
            double ithRestX = qdv_X.getIthDataPtAsDouble(restOfThePoints);
            double ithRestY = qdv_Y.getIthDataPtAsDouble(restOfThePoints);
            
            if (ithRestX != firstX) {
                double ithSlope = (ithRestY - firstY) / (ithRestX - firstX);
                
                if (ithSlope != testSlope) {
                    diffSlopesFound = true;
                }  
            } 
        }
        
        if (!diffSlopesFound) {
            MyAlerts.showStraightLineAlert();
            returnStatus = "Cancel";
            return returnStatus;         
        }
        
        // Just in case...
        if (returnStatus.equals("Cancel")) { return returnStatus; }
        
        String[] ranksX = new String[nPoints];
        String[] ranksY = new String[nPoints];
        
        String[] zScoresX = new String[nPoints];
        String[] zScoresY = new String[nPoints];  
        
        Transformations_Calculations t_c = new Transformations_Calculations();
        ranksX = t_c.unaryOpsOfVars(alXs, "rank");
        ranksY = t_c.unaryOpsOfVars(alYs, "rank");        
        zScoresX = t_c.unaryOpsOfVars(ranksX, "z-score");
        zScoresY = t_c.unaryOpsOfVars(ranksY, "z-score");
        
        spearmansRho = 0.0;
        
        for (int ithNum = 0; ithNum < nPoints; ithNum++) {
            spearmansRho += Double.parseDouble(zScoresX[ithNum]) * Double.parseDouble(zScoresY[ithNum]);
        }
        
        spearmansRho /= (double)(nPoints - 1);

        nRows = Math.min(theXs.get_nDataPointsLegal(), theYs.get_nDataPointsLegal());
        nColumns = 2;
        theYVariable = 1;    //  Hard coded for simple reg       
        mat_X = new Matrix (nRows, 2);  //  Hard coded for simple reg
        mat_Y = new Matrix (nRows, 1);
        XVar = new Matrix(nRows, 1); // For the scatterplot
        mat_Hat = new Matrix(nRows, nRows);
        str_DataLabels = new String[nColumns];
        
        str_DataLabels[0] = explanatoryVariable;
        str_DataLabels[1] = responseVariable; 

        if (StringUtilities.stringIsEmpty(explanatoryVariable) || StringUtilities.stringIsEmpty(responseVariable))  {
            str_DataLabels[0] = theXs.getTheVarLabel();
            str_DataLabels[1] = theYs.getTheVarLabel();         
        }
        
        // Add column of 1's to X matrix as column[0]
        double sumY = 0.0;
        
        for (int ithRow = 0; ithRow < nRows; ithRow++) {
            double tempY = theYs.getIthDataPtAsDouble(ithRow);
            mat_Y.set(ithRow, 0, tempY);
            sumY += tempY;
            
            double tempX = theXs.getIthDataPtAsDouble(ithRow);           
            mat_X.set(ithRow, 0, 1.0);
            mat_X.set(ithRow, 1, tempX);
            XVar.set(ithRow, 0, tempX);  //  For scatterplot
        }
        
        sumY2_Over_n = sumY * sumY / (double)nRows;
        paramTerm[0] = "Intercept";
        
        for (int ithColumn = 1; ithColumn <= nColumns; ithColumn++) {
            paramTerm[ithColumn] = str_DataLabels[ithColumn - 1];   //  x, y
        }  
        
        return returnStatus;
    }
    
    public String doRegressionAnalysis() {
        double jjResid;
        double tempDouble_01, tempDouble_02, tempDouble_03, tempDouble_04;
        dm.whereIsWaldo(269, waldoFile, "doRegressionAnalysis()");
        returnStatus = "OK";
        n = mat_Y.getRowDimension();
        dbl_n = n;
        p = mat_X.getColumnDimension();    
        k = p - 1;                      // k is number of explanatory variables
        mat_Resids = new Matrix(n, 1);
        mat_StandResids = new Matrix(n, 1);
        mat_StudResids = new Matrix(n, 1);
        mat_CooksD = new Matrix(n, 1);
        mat_RStudent = new Matrix(n, 1);
        dm.whereIsWaldo(279, waldoFile, "doRegressionAnalysis()");
        // MPV, p73
        mat_XPrime = mat_X.transpose();
        mat_XPrimeX = mat_XPrime.times(mat_X);  // OK
        mat_InvXPrimeX = mat_XPrimeX.inverse();   // OK
        mat_Hat = mat_X.times(mat_InvXPrimeX.times(mat_XPrime)); // OK
        mat_BetaHats = mat_InvXPrimeX.times(mat_XPrime.times(mat_Y));   //  OK
        mat_YHats = mat_Hat.times(mat_Y); // OK
        leverage = new double[n];
        
        for (int hatsie = 0; hatsie < n; hatsie++) {
            leverage[hatsie] = mat_Hat.get(hatsie, hatsie);
        }
        
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
        intercept = mat_BetaHats.get(0, 0);
        
        if (slope >= 0) {
            pearsonsR = Math.sqrt(r2);
        }  
        else {
            pearsonsR = -Math.sqrt(r2);
        }
        dm.whereIsWaldo(309, waldoFile, "doRegressionAnalysis()");
        pearsonRInferenceCalculations();
 
        s = Math.sqrt(ssResid / (n - k - 1));
        adj_r2 = 1.0 - (ssResid / (n - k - 1)) / (ssTotal / (n - 1));
       
        // Calculations for ANOVA table
        dfReg = k;
        dfResid = n - k - 1;

        if (dfResid < 1) {
            MyAlerts.showTooFewRegressionDFAlert();
            returnStatus = "Cancel";
            return returnStatus;
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
        
        nResidsCalculated = tracker.getNResidualsCalculated();
        nPredsCalculated = tracker.getNPredictedsCalculated();
        
        String tempResidName = "Resid";
        String tempPredsName = "YHat"; 
        dm.whereIsWaldo(348, waldoFile, "doRegressionAnalysis()");
        qdv_Resids = new QuantitativeDataVariable(dm, tempResidName, tempResidName, mat_Resids);
        qdv_PredYs = new QuantitativeDataVariable(dm, tempPredsName, tempPredsName, mat_YHats);        
        dm.whereIsWaldo(351, waldoFile, "doRegressionAnalysis()");
        
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
        
        if (saveTheResids.equals("Yes") || saveTheHats.equals("Yes")) {
            dm.setDataAreClean(false);
            
            if (saveTheResids.equals("Yes") && saveTheHats.equals("Yes")){
                dm.addToStructOneColumnWithExistingQuantData(qdv_Resids);
                dm.addToStructOneColumnWithExistingQuantData(qdv_PredYs);
                tracker.setNResidualsCalculated(nResidsCalculated + 1);
                tracker.setNPredictedsCalculated(nPredsCalculated + 1);
                tracker.setNVarsCommitted(tracker.getNVarsCommitted() + 2);
            }  else {
                if (saveTheResids.equals("Yes")) {
                    dm.addToStructOneColumnWithExistingQuantData(qdv_Resids);
                    tracker.setNResidualsCalculated(nResidsCalculated + 1);
                    tracker.setNVarsCommitted(tracker.getNVarsCommitted() + 1);
                } else {
                    dm.addToStructOneColumnWithExistingQuantData(qdv_PredYs);
                    tracker.setNPredictedsCalculated(nPredsCalculated + 1);
                    tracker.setNVarsCommitted(tracker.getNVarsCommitted() + 1);
                }
            }
            dm.whereIsWaldo(399, waldoFile, "doRegressionAnalysis()");
            predYs = new double[nCases];
            resids = new double[nCases];
            // Must go back to original data file and calculate the 
            // residuals and yHats because some data might be missing. 
            intercept = mat_BetaHats.get(0, 0);
            slope = mat_BetaHats.get(1, 0);
            
            for (int ithCase = 0; ithCase < nCases; ithCase++) {                
                if (DataUtilities.strIsADouble(qdv_X.getIthDataPtAsString(ithCase))
                   && DataUtilities.strIsADouble(qdv_X.getIthDataPtAsString(ithCase)) ){
                double xValue  = qdv_X.getIthDataPtAsDouble(ithCase);
                double yValue = qdv_Y.getIthDataPtAsDouble(ithCase);
                predYs[ithCase] = intercept + slope * xValue;
                resids[ithCase] = yValue - predYs[ithCase];
                strResid = Double.toString(resids[ithCase]);
                strPredY = Double.toString(predYs[ithCase]);
                trimStrResid = strResid.trim();
                trimStrPredY = strPredY.trim();
                } else {
                    trimStrResid = "  *  ";
                    trimStrPredY = "  *  ";
                } 
                
                nVarsCommitted = tracker.getNVarsCommitted();
                
                if (saveTheResids.equals("Yes") || saveTheHats.equals("Yes")) {                    
                    if (saveTheResids.equals("Yes") && saveTheHats.equals("Yes")){
                        dm.getDataStruct().get(nVarsCommitted - 2)
                          .getTheCases_ArrayList()
                          .set(ithCase, trimStrResid);

                        dm.getDataStruct().get(nVarsCommitted - 1)
                          .getTheCases_ArrayList()
                          .set(ithCase, trimStrPredY);
                
                    }  else
                    if (saveTheResids.equals("Yes")){
                        dm.getDataStruct().get(nVarsCommitted - 1)
                          .getTheCases_ArrayList()
                          .set(ithCase, trimStrResid);

                    } else {
                        dm.getDataStruct().get(nVarsCommitted - 1)
                          .getTheCases_ArrayList()
                          .set(ithCase, trimStrPredY);
                    }
                }
            }   //  end cases
            
            dm.whereIsWaldo(449, waldoFile, "doRegressionAnalysis()");
            if (saveTheResids.equals("Yes") || saveTheHats.equals("Yes")) {                
                if (saveTheResids.equals("Yes") && saveTheHats.equals("Yes")){
                    dm.getDataStruct().get(nVarsCommitted - 2).formatTheColumn();
                    dm.getDataStruct().get(nVarsCommitted - 1).formatTheColumn();
                }  else {
                if (saveTheResids.equals("Yes")) 
                    dm.getDataStruct().get(nVarsCommitted - 1).formatTheColumn();
                else    //  Only predicteds chosen to save
                    dm.getDataStruct().get(nVarsCommitted - 1).formatTheColumn();  
                }
            }
        }
        if (returnStatus.equals("OK")) {
            printStatistics();
        }
        return returnStatus;
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
       dm.whereIsWaldo(479, waldoFile, "printStatistics()");
       regressionReport = new ArrayList<>();
       regressionDiagnostics = new ArrayList<>();
       statsReport   = new ArrayList<>();

       print_ParamEstimates();
       print_ANOVA_Table();
       print_Diagnostics(); 
       print_BivStats();
   }
   
   public void print_ANOVA_Table() {
        dm.whereIsWaldo(491, waldoFile, "print_ANOVA_Table()");
        addNBlankLinesToRegressionReport(2);
        
        regressionReport.add("                            Analysis of Variance");
        addNBlankLinesToRegressionReport(1);
        regressionReport.add(String.format("Source of           Sum of     Degrees of"));
        addNBlankLinesToRegressionReport(1);
        regressionReport.add(String.format("Variation           Squares      Freedom         Mean Square       F         P-value"));
        addNBlankLinesToRegressionReport(1);
        regressionReport.add(String.format(fiftyOneDashesLong));
        addNBlankLinesToRegressionReport(1);
        sourceString = getLeftMostNChars("Regression", 12);
        regressionReport.add(String.format("%12s    %13.3f      %4d             %8.2f %8.3f   %6.4f", sourceString,  ssReg,  dfReg,  msReg,  fStatistic, pValue_F));
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
       dm.whereIsWaldo(516, waldoFile, "print_Diagnostics()");
       addNBlankLinesToDiagnosticReport(1);
       
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
        addNBlankLinesToDiagnosticReport(1);
        regressionDiagnostics.add(String.format("%30s %3d %3s",  "   Note: If regression assumptions are true, R-Student has a t distribution with", dfR_Student, "df."));
        addNBlankLinesToDiagnosticReport(1);
        regressionDiagnostics.add(String.format("%10s%4.3f %20s", "   Note: Points with Leverage > ", leverageWarningTrigger, "are potentially high leverage points."));
        addNBlankLinesToDiagnosticReport(1);
        regressionDiagnostics.add(String.format("   Note: Points with Cook's D values > 1.0 are potentially high influence points.")); 
        
        // Last two lines to give space in the scrollPane
        addNBlankLinesToDiagnosticReport(1);
   }
   
   public void print_ParamEstimates() {
        double lowBound, daParam, hiBound;
        String parameter;
        dm.whereIsWaldo(569, waldoFile, "print_ParamEstimates()");
        addNBlankLinesToRegressionReport(1);
        
        // Print equation on one line if simple regression
        if (k == 1)  {
            String respVsExplan = StringUtilities.centerTextInString(subTitle, 80);
            regressionReport.add(respVsExplan);
            addNBlankLinesToRegressionReport(1);
            sourceString = "The regression equation is:";
            responseLabel2Print = getLeftMostNChars(paramTerm[2], 15) + " = ";
            responseLabelForSummary = getLeftMostNChars(paramTerm[2], 15);
            explanLabel2Print = getLeftMostNChars(paramTerm[1], 15);
            String tempRegrEq = String.format(" %20s  %15s %8.3f %3s %8.3f %15s",
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
        addNBlankLinesToRegressionReport(1);
        
        regressionReport.add(String.format("Parameter Estimates"));
        addNBlankLinesToRegressionReport(1);
        regressionReport.add(String.format("       Term                Estimate      Std Error      t Ratio       P-value"));
        addNBlankLinesToRegressionReport(1);
        regressionReport.add(String.format(fortyFiveDashesLong));
        addNBlankLinesToRegressionReport(1);
       
        for (int jj = 0; jj <= k; jj++) {
          sourceString = getLeftMostNChars(paramTerm[jj] + bunchaBlanks, 21); 
          paramEst = mat_BetaHats.get(jj, 0);
          paramStdErr = stErrCoef.get(jj, 0);
          paramTRatio = tStat.get(jj, 0);
          paramPValue = PValue_T.get(jj, 0);
          
          regressionReport.add(String.format("%20s     %11.4f   %11.4f  %11.4f  %11.4f", sourceString,  paramEst,  paramStdErr,  paramTRatio,  paramPValue));
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
        regressionReport.add(String.format("     Parameter            Lower bound         Estimate         Upper Bound"));
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
        
        regressionReport.add(String.format(" %20s     %10.5f         %10.5f        %10.5f", parameter,  lowBound,  daParam,  hiBound));
        addNBlankLinesToRegressionReport(1);
        
        parameter = getLeftMostNChars(paramTerm[1] + bunchaBlanks, 21);
        daParam = mat_BetaHats.get(1, 0);
        lowBound = daParam - theCriticalValue * stErrCoef.get(1, 0);        
        hiBound = daParam + theCriticalValue * stErrCoef.get(1, 0);
        ciBeta_1 = new Point_2D(lowBound, hiBound); //  For joint CI        
        initialLowerYBound = daParam - fudgeFactor * theCriticalValue * stErrCoef.get(1, 0);  
        initialUpperYBound = daParam + fudgeFactor * theCriticalValue * stErrCoef.get(1, 0);

        initialScaleLimits_1 = new Point_2D(initialLowerYBound, initialUpperYBound);
        
        regressionReport.add(String.format(" %20s     %10.5f         %10.5f        %10.5f", parameter,  lowBound,  daParam,  hiBound));
        addNBlankLinesToRegressionReport(1);
        
        parameter = getLeftMostNChars("Error" + bunchaBlanks, 21);
        double x2Low = x2Dist.getInvLeftTailArea(0.025);
        double x2Hi = x2Dist.getInvRightTailArea(0.025);
        lowBound = Math.sqrt(dfResid * msResid / x2Hi);
        daParam = s;
        hiBound = Math.sqrt(dfResid * msResid / x2Low);
        regressionReport.add(String.format(" %20s     %10.5f         %10.5f        %10.5f", parameter,  lowBound,  daParam,  hiBound));
        addNBlankLinesToRegressionReport(1);

        parameter = getLeftMostNChars("Pearson's r" + bunchaBlanks, 21);
        lowBound = pearson_95CI_Lo;
        daParam = pearsonsR;
        hiBound = pearson_95CI_Hi;
        regressionReport.add(String.format(" %20s     %10.5f         %10.5f        %10.5f", parameter,  lowBound,  daParam,  hiBound));        
        
        addNBlankLinesToRegressionReport(1);
        regressionReport.add(String.format(fortyFiveDashesLong));       
   }
   
    public void print_BivStats() {
        int int_1, int_2;
        double dbl_1, dbl_2;
        String str_1, str_2, centeredExplanVar, centeredRespVar;
        dm.whereIsWaldo(693, waldoFile, "print_BivStats()");
        centeredExplanVar = StringUtilities.centerTextInString(explanLabel2Print, 10);
        centeredRespVar = StringUtilities.centerTextInString(responseLabelForSummary, 10);
        PrintUStats_Model prntU_X = new PrintUStats_Model(centeredExplanVar, qdv_X, true);
        PrintUStats_Model prntU_Y = new PrintUStats_Model(centeredRespVar, qdv_Y, true);
        
        addNBlankLinesToBivStatsReport(1);
        
        statsReport.add(String.format("     *******  File information  *******"));
        addNBlankLinesToBivStatsReport(1);   
        str_1 = StringUtilities.truncateString(prntU_X.getVarDescr() + bunchaBlanks, 16);
        str_2 = StringUtilities.truncateString(prntU_Y.getVarDescr() + bunchaBlanks, 16);
        statsReport.add(String.format("  Explanatory variable: %10s", str_1));
        statsReport.add(String.format("\n     Response variable: %10s", str_2));
        addNBlankLinesToBivStatsReport(1);          
        statsReport.add(String.format("\n            N in file:   %4d", nCasesInStruct));
        addNBlankLinesToBivStatsReport(1);  
        statsReport.add(String.format("            N missing:   %4d", nMissing));        
        addNBlankLinesToBivStatsReport(1);  
        int_1 = prntU_X.getLegalN();  
        int_2 = prntU_Y.getLegalN(); 
        statsReport.add(String.format("     N complete pairs:   %4d", int_1)); 
        addNBlankLinesToBivStatsReport(2); 
        
        statsReport.add(String.format(" *****  Basic mean based statistics  *****"));
        statsReport.add(String.format("\n *****         Expl var   Resp var  *****"));
        addNBlankLinesToBivStatsReport(1);
        dbl_1 = prntU_X.getTheMean();  
        dbl_2 = prntU_Y.getTheMean(); 
        statsReport.add(String.format("       Mean:   %8.4f   %8.4f", dbl_1, dbl_2));

        addNBlankLinesToBivStatsReport(1);
        dbl_1 = prntU_X.getTheStDev();  dbl_2 = prntU_Y.getTheStDev(); 
        statsReport.add(String.format("   St. Dev.: %10.4f %10.4f", dbl_1, dbl_2));         
          
        addNBlankLinesToBivStatsReport(1);
        dbl_1 = prntU_X.getTheVariance();  
        dbl_2 = prntU_Y.getTheVariance(); 
        statsReport.add(String.format("   Variance: %10.4f %10.4f", dbl_1, dbl_2));        
        addNBlankLinesToBivStatsReport(1);
        dbl_1 = prntU_X.getTheSkew();  
        dbl_2 = prntU_Y.getTheSkew(); 
        statsReport.add(String.format("       Skew:   %8.4f   %8.4f", dbl_1, dbl_2));
        addNBlankLinesToBivStatsReport(2);
        statsReport.add(String.format("  *****  Other mean based statistics  *****"));        
        addNBlankLinesToBivStatsReport(1);
        dbl_1 = prntU_X.getTheTrimmedMean();  
        dbl_2 = prntU_Y.getTheTrimmedMean();
        statsReport.add(String.format("   Trimmed mean:   %8.4f    %8.4f", dbl_1, dbl_2)); 
        addNBlankLinesToBivStatsReport(1);
        dbl_1 = prntU_X.getTheKurtosis();  
        dbl_2 = prntU_Y.getTheKurtosis();
        statsReport.add(String.format("       Kurtosis:   %8.4f    %8.4f", dbl_1, dbl_2));        
        addNBlankLinesToBivStatsReport(1);
        dbl_1 = prntU_X.getTheCV();  
        dbl_2 = prntU_Y.getTheCV();
        statsReport.add(String.format("             CV:   %8.4f    %8.4f", dbl_1, dbl_2));               
        addNBlankLinesToBivStatsReport(2); 
        
        statsReport.add(String.format("  *****     Five-number summaries    *****"));
        addNBlankLinesToBivStatsReport(1);
        dbl_1 = prntU_X.getTheMin();  
        dbl_2 = prntU_Y.getTheMin();
        statsReport.add(String.format("        Minimum:   %8.4f   %8.4f", dbl_1, dbl_2)); 
        addNBlankLinesToBivStatsReport(1);
        dbl_1 = prntU_X.getQ1();  
        dbl_2 = prntU_Y.getQ1();       
        statsReport.add(String.format("             Q1:   %8.4f   %8.4f", dbl_1, dbl_2));  
        addNBlankLinesToBivStatsReport(1);
        dbl_1 = prntU_X.getTheMedian();  
        dbl_2 = prntU_Y.getTheMedian();
        statsReport.add(String.format("         Median:   %8.4f   %8.4f", dbl_1, dbl_2));        
        addNBlankLinesToBivStatsReport(1);
        dbl_1 = prntU_X.getQ3();  
        dbl_2 = prntU_Y.getQ3();
        statsReport.add(String.format("             Q3:   %8.4f   %8.4f", dbl_1, dbl_2));        
        addNBlankLinesToBivStatsReport(1);
        dbl_1 = prntU_X.getTheMax();  
        dbl_2 = prntU_Y.getTheMax();
        statsReport.add(String.format("        Maximum:   %8.4f   %8.4f", dbl_1, dbl_2));  
        addNBlankLinesToBivStatsReport(2);
        
        statsReport.add(String.format("  *****  Other median based statistics  *****"));  
        addNBlankLinesToBivStatsReport(1);
        dbl_1 = prntU_X.getTheIQR();  
        dbl_2 = prntU_Y.getTheIQR();
        statsReport.add(String.format("            IQR:   %8.4f   %8.4f", dbl_1, dbl_2));        
        addNBlankLinesToBivStatsReport(1);
        dbl_1 = prntU_X.getTheRange();  
        dbl_2 = prntU_Y.getTheRange();
        statsReport.add(String.format("          Range:   %8.4f   %8.4f", dbl_1, dbl_2));   
        addNBlankLinesToBivStatsReport(2);
        statsReport.add(String.format("            *****  Correlations *****")); 
        addNBlankLinesToBivStatsReport(1);
        statsReport.add(String.format("                 Pearson's r:   %5.4f", pearsonsR));
        addNBlankLinesToBivStatsReport(1);
        statsReport.add(String.format("              Spearman's rho:   %5.4f", spearmansRho));
    }
   
    public void addNBlankLinesToRegressionReport(int thisMany) {
        StringUtilities.addNLinesToArrayList(regressionReport, thisMany);
    }
    
    private void addNBlankLinesToDiagnosticReport(int thisMany) {
        StringUtilities.addNLinesToArrayList(regressionDiagnostics, thisMany);
    }
    
    private void addNBlankLinesToBivStatsReport(int thisMany) {
        StringUtilities.addNLinesToArrayList(statsReport, thisMany);
    }
    
    public int getDF() { return nPoints; }
   
   public ArrayList<String> getRegressionReport() { return regressionReport; }
   public ArrayList<String> getDiagnostics() { return regressionDiagnostics; }   
   public ArrayList<String> getStatsReport() { return statsReport; }
   
   public int getNRows()    {return nRows;}
   
   public String getExplanatoryVariable() { return explanatoryVariable; }
   public String getResonseVariable() { return responseVariable; }
   
   public String getRespVsExplSubtitle() { return respVsExplanVar; }
   
   public int getNCasesInStruct() { return  nCasesInStruct; }
   public int getNMissing() { return nMissing; }

   public String[] getAxisLabels() { return strAxisLabels; }
   public Matrix getXVar() {return XVar;}
   public Matrix getX() { return mat_X; }
   public Matrix getY() {return mat_Y;}
   public Matrix getYHats() {return mat_YHats;}
   public Matrix getResids() {return mat_Resids;}
   public QuantitativeDataVariable getXVariable() {return qdv_X;}
   public QuantitativeDataVariable getYVariable() {return qdv_Y;} 
   public QuantitativeDataVariable getQDVResids() {return qdv_Resids; }
   public Matrix getStandardizedResids() {return mat_StandResids;}
   public Matrix getStudentizedResids() {return mat_StudResids;}
   public Matrix getR_StudentizedResids() {return mat_RStudent;}
   public Matrix getCooksD() { return mat_CooksD; }
   public double getLeverageWarningTrigger() { return leverageWarningTrigger; }
   public double[] getLeverage() { return leverage; }
   public int getRegDF() { return dfResid; }
   public double getSimpleRegSlope()  {return mat_BetaHats.get(1, 0);}
   public double getSimpleRegIntercept()  {return  mat_BetaHats.get(0, 0);}
   public double getTStat()  {return  paramTRatio;}
   public double getPValue()  {return  paramPValue;}
   public Point_2D getCIBeta_0() { return ciBeta_0; }
   public Point_2D getCIBeta_1() { return ciBeta_1; }
   public Point_2D get_InitialScaleLimits_b0() { return initialScaleLimits_0; }
   public Point_2D get_InitialScaleLimits_b1() { return initialScaleLimits_1; }
   public double getMSRes() { return msResid; }
   public double getSlope() { return slope; }
   public double getIntercept() { return intercept; }
   public double getPearsonsR() { return pearsonsR; }
   public String getRegEq() { return deBlankedRegEq; }
}