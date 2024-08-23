/**************************************************
 *              OneParam_QuadReg_Model            *
 *                    11/01/23                    *
 *                     18:00                      *
 *************************************************/
package quadraticRegression;

import matrixProcedures.Matrix;
import dataObjects.QuantitativeDataVariable;
import utilityClasses.StringUtilities;
import java.util.ArrayList;
import probabilityDistributions.FDistribution;
import probabilityDistributions.TDistribution;
import proceduresOneUnivariate.PrintUStats_Model;
import splat.*;
import utilityClasses.MyAlerts;

public class OneParam_QuadReg_Model {     
    // POJOs
    static int n, p, k, dfReg, dfResid, dfTotal;
    static int nColumns, nPoints, theYVariable; //  In original data array

    private double ssTotal, ssResid, ssRegr, msReg, msResid, fStatistic,
            pValue_F, r2, quad_Coef, pearsonsR, sumXX, sumXY, 
            slope_NoInterceptModel, msRes, intercept_InterceptModel, 
            stErr_slopeNoInt, tValue, pValue_t, fullQuadReg_Intercept, 
            fullQuadRegLinTerm, fullQuadRegQuadTerm, meanX, meanY, stDevX, 
            stDevY, covariance;
    
    private double[] leverage;
    
    String sourceString0, responseLabel2Print, explanLabel2Print, returnStatus;

    private String sourceString, explanatoryVariable, responseVariable, 
           respVsExplanVar, saveTheResids;
    
    String fortyFiveDashesLong, fiftyOneDashesLong, fiftyFiveDashesLong;
    final String bunchaBlanks = "                                ";
    String[] str_DataLabels;
    String[] paramTerm;
    ArrayList<String> regressionReport, regressionDiagnostics, statsReport;
    
    String waldoFile = "OneParam_QuadReg_Model";
    //String waldoFile = "";
    
    // My classes

    Matrix mat_X, XVar, mat_Y, mat_XPrime, mat_XPrimeX, mat_InvXPrimeX, mat_Hat, 
           mat_BetaHats, mat_YHats, mat_Resids, mat_StandResids, 
           mat_StudResids, mat_CooksD, mat_RStudent;

    Data_Manager dm;
    static FDistribution fDist;
    static TDistribution tDist;
    QuantitativeDataVariable qdv_X, qdv_Y, qdv_Resids;   
    OneParam_QuadReg_Controller noInt_QuadReg_Controller;

    // POJOs / FX
    
    public OneParam_QuadReg_Model(OneParam_QuadReg_Controller noInt_QuadReg_Controller) {   
        this.noInt_QuadReg_Controller = noInt_QuadReg_Controller;
        dm = noInt_QuadReg_Controller.getDataManager();
        dm.whereIsWaldo(63, waldoFile, "Constructing");
        explanatoryVariable = noInt_QuadReg_Controller.getExplanVar();
        responseVariable = noInt_QuadReg_Controller.getResponseVar();
        respVsExplanVar = noInt_QuadReg_Controller.getSubTitle();
        saveTheResids = noInt_QuadReg_Controller.getSaveTheResids();
        fortyFiveDashesLong = StringUtilities.getUnicodeLineThisLong(45);
        fiftyOneDashesLong = StringUtilities.getUnicodeLineThisLong(51);
        fiftyFiveDashesLong = StringUtilities.getUnicodeLineThisLong(55);
    }
    
    //  The QDVs are needed for labels; these qdv's ar the legal values
    public String setupOneParamQuadRegRegressionAnalysis(QuantitativeDataVariable theXs, QuantitativeDataVariable theYs) {     
        dm.whereIsWaldo(75, waldoFile, "setupOneParamQuadRegRegressionAnalysis");   
        returnStatus = "OK";
        qdv_X = theXs;
        qdv_Y = theYs;

        nPoints = theXs.getOriginalN(); //  = LegalN
        
        if (nPoints < 2) {
            MyAlerts.showTooFewRegressionDFAlert();
            returnStatus = "Cancel";
            return returnStatus;
        }         

        //  Full Quad reg
        setupFullQuadRegAnalysis();
        doFullQuadRegRegressionAnalysis();
        //  Full Quad reg
 
        nColumns = 2;
        theYVariable = 1;    //  Hard coded for simple reg     
        // X = new Matrix (nPoints, nColumns);   
        mat_X = new Matrix (nPoints, 1);  //  Hard coded for noInt regression
        mat_Y = new Matrix (nPoints, 1);
        XVar = new Matrix(nPoints, 1); // For the scatterplot

        str_DataLabels = new String[nColumns];
        str_DataLabels[0] = theXs.getTheVarLabel();
        str_DataLabels[1] = theYs.getTheVarLabel();        
        
        // Add column of 1's to X matrix as column[0]
        sumXX = 0.0;
        sumXY = 0.0;
        ssTotal = 0;
        
        meanX = qdv_X.getTheMean(); stDevX = qdv_X.getTheStandDev();
        meanY = qdv_Y.getTheMean(); stDevY = qdv_Y.getTheStandDev();
        
        for (int ithRow = 0; ithRow < nPoints; ithRow++) {
            double tempY = theYs.getIthDataPtAsDouble(ithRow);
            mat_Y.set(ithRow, 0, tempY);
            double tempX = theXs.getIthDataPtAsDouble(ithRow);   
            mat_X.set(ithRow, 0, tempX);
            XVar.set(ithRow, 0, tempX);  //  For scatterplot
            
            sumXX = sumXX + tempX * tempX;
            sumXY = sumXY + tempX * tempY;
            ssTotal = ssTotal + tempY * tempY;
        }  
        
        covariance = 0.0;

        for (int ithRow = 0; ithRow < nPoints; ithRow++) {
            double devX = mat_X.get(ithRow, 0) - meanX;
            double devY = mat_Y.get(ithRow, 0) - meanY;
            covariance += devX * devY;
        } 
        
        pearsonsR = covariance / (stDevX * stDevY * (nPoints - 1.0));
        quad_Coef = pearsonsR * stDevY / stDevX;
        intercept_InterceptModel = meanY - quad_Coef * meanX;

        paramTerm = new String[3];  // Hard coded for simple reg
        paramTerm[0] = "Intercept";
        
        for (int ithColumn = 1; ithColumn <= nColumns; ithColumn++) {
            paramTerm[ithColumn] = str_DataLabels[ithColumn - 1];   //  x, y
        }  
        return returnStatus;
    }
    
    public void doOneParamQuadRegRegressionAnalysis() {
        dm.whereIsWaldo(146, waldoFile, "doOneParamQuadRegRegressionAnalysis()");
        slope_NoInterceptModel = sumXY / sumXX;
        msRes = (ssTotal - slope_NoInterceptModel * sumXY) / (nPoints - 1.0);
        ssResid = msRes * (nPoints - 1.0);
        ssRegr = ssTotal - ssResid;
        stErr_slopeNoInt = Math.sqrt(msRes / sumXX);
        tValue = slope_NoInterceptModel / stErr_slopeNoInt;
        
        /********************************************************************
        *   R-Square definition is from Montgomery, Peck, Vining ((2012).   *
        *   Introduction to Linear Regression Analysis (5th). John Wiley &  *
        *   Sons.  New York.   p49                                          *
        ********************************************************************/        
        double numerSumSq = 0.0;
        double denomSumSq = 0.0;
        meanY = qdv_Y.getTheMean();
        
        for (int ithRow = 0; ithRow < nPoints; ithRow++) {
            double errPredY = (mat_Y.get(ithRow, 0) - slope_NoInterceptModel * mat_X.get(ithRow, 0));
            numerSumSq = numerSumSq + errPredY * errPredY;
            double devFromMean = (mat_Y.get(ithRow, 0) - meanY);
            denomSumSq = denomSumSq + devFromMean * devFromMean;
        }
        
        r2 = 1.0 - numerSumSq / denomSumSq;
        
        mat_YHats = new Matrix(nPoints, 1); // OK
        mat_Resids = new Matrix(nPoints, 1);
        
        for (int ithResid = 0; ithResid < nPoints; ithResid++) {
            double yHat = slope_NoInterceptModel * mat_X.get(ithResid, 0);
            mat_YHats.set(ithResid, 0, yHat);
            double daResid = mat_Y.get(ithResid, 0) - yHat;
            mat_Resids.set(ithResid, 0, daResid);
        }
        
        qdv_Resids = new QuantitativeDataVariable(dm, "Residuals", "Residuals", mat_Resids);
        
        if (saveTheResids.equals("Yes")) {
            Data_Manager dm = noInt_QuadReg_Controller.getDataManager();
            dm.addToStructOneColumnWithExistingQuantData(qdv_Resids);
        }
        
        tDist = new TDistribution(nPoints - 1);

        // Calculations for ANOVA table
        dfReg = 1;
        dfResid = nPoints - 1;
        dfTotal = nPoints;

        msReg = ssRegr / dfReg;
        msResid = ssResid / (double)(nPoints - 1);
        fStatistic = msReg / msResid;
       
        fDist = new FDistribution(1, nPoints - 1);
        pValue_F = fDist.getRightTailArea(fStatistic);
        pValue_t = 2.0 *tDist.getRightTailArea(Math.abs(tValue));

        printStatistics();
   }

   public void printStatistics() {   
       regressionReport = new ArrayList<>();
       regressionDiagnostics = new ArrayList<>();
       statsReport   = new ArrayList<>();

       print_ParamEstimates();
       print_ANOVA_Table();
       //print_Diagnostics(); 
       print_BivStats();
   }
   
   public void print_ANOVA_Table() {
        addNBlankLinesToRegressionReport(2);
        regressionReport.add("   *****  NOTE: No intercept in model.  R-Square is redefined   *****");
        addNBlankLinesToRegressionReport(2);
        regressionReport.add("                            Analysis of Variance");
        addNBlankLinesToRegressionReport(2);
        regressionReport.add(String.format("Source of           Sum of     Degrees of"));
        addNBlankLinesToRegressionReport(1);
        regressionReport.add(String.format("Variation           Squares      Freedom         Mean Square       F         P-value"));
        addNBlankLinesToRegressionReport(1);
        regressionReport.add(String.format(fiftyOneDashesLong));
        addNBlankLinesToRegressionReport(1);
        sourceString = StringUtilities.getleftMostNChars("Regression", 12);
        regressionReport.add(String.format("%12s    %13.3f      %4d             %8.2f     %8.3f      %6.4f", sourceString,  ssRegr,  dfReg,  msReg,  fStatistic, pValue_F));
        addNBlankLinesToRegressionReport(1);
        sourceString = StringUtilities.getleftMostNChars("Residual", 12);
        regressionReport.add(String.format("%12s    %13.3f      %4d             %8.2f", sourceString, ssResid, dfResid,  msResid));
        addNBlankLinesToRegressionReport(1);
        sourceString = StringUtilities.getleftMostNChars("Total", 12);
        regressionReport.add(String.format("%12s    %13.3f      %4d\n", sourceString, ssTotal, dfTotal));        
        addNBlankLinesToRegressionReport(1);
        regressionReport.add(String.format("                                 R-square = %4.3f\n", r2));        
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
        double leverageWarningTrigger = 2.0 * p / n; //  p213
       
        for (int jj = 0; jj < n; jj++) {
            System.out.println( "291, in loop, jj = " + jj);
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
        addNBlankLinesToRegressionReport(2);
        // Print equation on one line if simple regression
        String respVsExplan = StringUtilities.centerTextInString(noInt_QuadReg_Controller.getSubTitle(), 80);
        regressionReport.add(respVsExplan);
        addNBlankLinesToRegressionReport(2);
        sourceString0 = "The regression equation is:";
        responseLabel2Print = StringUtilities.getleftMostNChars(responseVariable, 15) + " = ";
        explanLabel2Print = StringUtilities.getleftMostNChars(explanatoryVariable + "^2 ", 15);
        regressionReport.add(String.format(" %20s  %10s %8.3f %15s",
                             sourceString0, responseLabel2Print,
                             slope_NoInterceptModel, explanLabel2Print
                          ));         
        addNBlankLinesToRegressionReport(1);
        regressionReport.add(String.format(fortyFiveDashesLong));
        addNBlankLinesToRegressionReport(1);
        regressionReport.add(String.format("Parameter Estimates"));
        addNBlankLinesToRegressionReport(1);
        regressionReport.add(String.format("       Term                Estimate     Std Error     t Ratio      P-value"));
        addNBlankLinesToRegressionReport(1);
        regressionReport.add(String.format(fortyFiveDashesLong));
        addNBlankLinesToRegressionReport(1);        
        regressionReport.add(String.format("%20s     %9.5f     %9.5f    %9.5f      %6.5f", explanLabel2Print,  slope_NoInterceptModel,  stErr_slopeNoInt,  tValue,  pValue_t));
        addNBlankLinesToRegressionReport(1);
   }
      
    private void print_BivStats() {
        int int_1, int_2;
        double dbl_1, dbl_2;
        String str_1, str_2, centeredExplanVar, centeredRespVar;
        centeredExplanVar = StringUtilities.centerTextInString(explanLabel2Print, 10);
        centeredRespVar = StringUtilities.centerTextInString(responseLabel2Print, 10);
        PrintUStats_Model prntU_X = new PrintUStats_Model(centeredExplanVar, qdv_X, true);
        PrintUStats_Model prntU_Y = new PrintUStats_Model(centeredRespVar, qdv_Y, true);        
        addNBlankLinesToBivStatsReport(2);
        statsReport.add("  1-Param Bivariate statistics summary");
        addNBlankLinesToBivStatsReport(2);  
        
        statsReport.add(String.format("     *******  File information  *******"));
        addNBlankLinesToBivStatsReport(2);   
        str_1 = StringUtilities.truncateString(prntU_X.getVarDescr() + bunchaBlanks, 10);
        str_2 = StringUtilities.truncateString(prntU_Y.getVarDescr() + bunchaBlanks, 10);
        statsReport.add(String.format("       Variable: %10s   %10s", str_1, str_2));
        addNBlankLinesToBivStatsReport(1);   
        int_1 = prntU_X.getOriginalN();
        int_2 = prntU_Y.getOriginalN();        
        statsReport.add(String.format("      N in file:   %4d         %4d", int_1, int_2));
        addNBlankLinesToBivStatsReport(1);  
        int_1 = prntU_X.getMissingN();
        int_2 = prntU_Y.getMissingN(); 
        statsReport.add(String.format("      N missing:   %4d         %4d", int_1, int_2));        
        addNBlankLinesToBivStatsReport(1);  
        int_1 = prntU_X.getLegalN();
        int_2 = prntU_Y.getLegalN(); 
        statsReport.add(String.format("        N Legal:   %4d         %4d", int_1, int_2)); 
        // addNBlankLinesToReport(1);
        // statsReport.add(String.format(fortyDashesLong));
        addNBlankLinesToBivStatsReport(3); 
        
        statsReport.add(String.format(" *****  Basic mean based statistics  *****"));
        addNBlankLinesToBivStatsReport(2);
        dbl_1 = prntU_X.getTheMean();
        dbl_2 = prntU_Y.getTheMean(); 
        statsReport.add(String.format("           Mean:   %8.4f   %8.4f", dbl_1, dbl_2));
        addNBlankLinesToBivStatsReport(1);
        dbl_1 = prntU_X.getTheVariance();
        dbl_2 = prntU_Y.getTheVariance(); 
        statsReport.add(String.format("       Variance: %10.4f %10.4f", dbl_1, dbl_2));        
        addNBlankLinesToBivStatsReport(1);
        dbl_1 = prntU_X.getTheSkew();
        dbl_2 = prntU_Y.getTheSkew(); 
        statsReport.add(String.format("           Skew:   %8.4f   %8.4f", dbl_1, dbl_2));
        addNBlankLinesToBivStatsReport(1);
        dbl_1 = prntU_X.getTheAdjSkew();
        dbl_2 = prntU_Y.getTheAdjSkew();
        statsReport.add(String.format("  Adjusted Skew:   %8.4f   %8.4f", dbl_1, dbl_2));        
        addNBlankLinesToBivStatsReport(3);
        
        statsReport.add(String.format("  *****  Other mean based statistics  *****"));        
        addNBlankLinesToBivStatsReport(2);
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
        addNBlankLinesToBivStatsReport(3); 
        
        statsReport.add(String.format("  *****     Five-number summaries    *****"));
        addNBlankLinesToBivStatsReport(2);
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
        addNBlankLinesToBivStatsReport(3);
        
        statsReport.add(String.format("  *****  Other median based statistics  *****"));  
        addNBlankLinesToBivStatsReport(2);
        dbl_1 = prntU_X.getTheIQR();
        dbl_2 = prntU_Y.getTheIQR();
        statsReport.add(String.format("            IQR:   %8.4f   %8.4f", dbl_1, dbl_2));        
        addNBlankLinesToBivStatsReport(1);
        dbl_1 = prntU_X.getTheRange();
        dbl_2 = prntU_Y.getTheRange();
        statsReport.add(String.format("          Range:   %8.4f   %8.4f", dbl_1, dbl_2));   
    }
    
    //  ************   All we need from this is the betas for the quadratic regression  ***********************
    public void setupFullQuadRegAnalysis() {     
        dm.whereIsWaldo(419, waldoFile, "setupFullQuadRegAnalysis()");               
        nColumns = 3;
        theYVariable = 1;    //  Hard coded for simple reg       
        mat_X = new Matrix (nPoints, 3);  //  Hard coded for quadratic reg
        mat_Y = new Matrix (nPoints, 1);
        XVar = new Matrix(nPoints, 1); // For the scatterplot
        
        // Add column of 1's to X matrix as column[0]
        double sumY = 0.0;        
        for (int ithRow = 0; ithRow < nPoints; ithRow++) {
            double tempY = qdv_Y.getIthDataPtAsDouble(ithRow);
            mat_Y.set(ithRow, 0, tempY);
            sumY += tempY;
            double tempX = qdv_X.getIthDataPtAsDouble(ithRow);           
            mat_X.set(ithRow, 0, 1.0);
            mat_X.set(ithRow, 1, tempX);
            mat_X.set(ithRow, 2, tempX * tempX);
            XVar.set(ithRow, 0, tempX);  //  For scatterplot
        }
    }
    
    public void doFullQuadRegRegressionAnalysis()  {
        dm.whereIsWaldo(441, waldoFile, "doFullQuadRegRegressionAnalysis()"); 
        //  Set up variables and matrices
        
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
        
        fullQuadReg_Intercept = mat_BetaHats.get(0, 0);
        fullQuadRegLinTerm = mat_BetaHats.get(1, 0);
        fullQuadRegQuadTerm = mat_BetaHats.get(2, 0);
        
        mat_BetaHats.print(6, 3);
        
        //leverage = new double[n];
   }
   
    private void addNBlankLinesToRegressionReport(int thisMany) {
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
    public int getNRows()    {return nPoints;}
    public String getExplanatoryVariable() { return explanatoryVariable; }
    public String getResonseVariable() { return responseVariable; }
    public String getRespVsExplSubtitle() { return respVsExplanVar; }
    public double getQuadCoefficient() { return quad_Coef; }
    //  Full quad reg coefficients
    public double getFQR_Intercept() { return fullQuadReg_Intercept; }
    public double getFQR_LinTerm() { return fullQuadRegLinTerm; }
    public double getFQR_QuadTerm() { return fullQuadRegQuadTerm ; }
    public String[] getLabels() { return str_DataLabels; }
    
    public Matrix getXVar() {return XVar;}
    public Matrix getX() { return mat_X; }
    public Matrix getY() {return mat_Y;}
    public Matrix getYHats() {return mat_YHats;}
    public Matrix getResids() {return mat_Resids;}
    
    public QuantitativeDataVariable getXVariable() {return qdv_X;}
    public QuantitativeDataVariable getYVariable() {return qdv_Y;} 
    public QuantitativeDataVariable getQDVResids() {return qdv_Resids;}
    
    public Matrix getStandardizedResids() {return mat_StandResids;}
    public Matrix getStudentizedResids() {return mat_StudResids;}
    public Matrix getR_StudentizedResids() {return mat_RStudent;}
    public Matrix getCooksD() { return mat_CooksD; }
    
    public double[] getLeverage() { return leverage; }
    public int getRegDF() { return dfResid; }
    public double get_Intercept_InterceptModel()  {return intercept_InterceptModel;}
    public double get_SlopeInterceptModel()  {return quad_Coef;}
    public double get_SlopeNoInterceptModel()  {return slope_NoInterceptModel;}
    public double getTStat()  {return  tValue;}
    public double getPValue()  {return  pValue_t;}
   
    public Data_Manager getDataManager() { return dm; }
}
