/**************************************************
 *                 MultReg_Model                  *
 *                    05/28/24                    *
 *                     00:00                      *
 *************************************************/
/***************************************************
 *  All symbols, formulae, and page numbers are    *
 *  from Montgomery, Peck, Vining: Introduction to *
 *  Linear Regression Analysis (5th ed)            *
 **************************************************/
package multipleRegression;

import dataObjects.ColumnOfData;
import dataObjects.MultiVariateContinDataObj;
import matrixProcedures.Matrix;
import java.util.ArrayList;
import probabilityDistributions.FDistribution;
import probabilityDistributions.TDistribution;
import utilityClasses.StringUtilities;
import splat.*;
import utilityClasses.MyAlerts;

public class MultReg_Model {     
    // POJOs
    boolean calculateTheVIFs;
    int n, p, k, dfReg, dfResid, dfTotal;
    int nColumns, nRows; 

    double ssTotal, ssResid, ssReg, sumY2_Over_n, msReg, msResid, 
           fStatistic, pValue_F, s, r2, adj_r2, paramEst, paramStdErr, 
           paramTRatio, paramPValue;

    double[] theVIFs;       
    
    String sourceString, deBlankedRegEq;
    // String waldoFile = "MultReg_Model";
    String waldoFile = "";
    
    String[] str_DataLabels;
    String paramTerm[];
    ArrayList<String> regressionReport, regressionDiagnostics;
    
    // My classes
    Data_Manager dm;
    Matrix mat_X, XVar, mat_Y, mat_XPrime, mat_XPrimeX, mat_InvXPrimeX, mat_Hat, 
           mat_BetaHats, mat_YHats, /*XVar_4Scatter, YVar_4Scatter,*/ mat_DFFITS,
           BXY, SSRes, Y_Prime_Y, stErrCoef, tStat, PValue_T, mat_Resids, 
           mat_StandResids, mat_StudResids, mat_CooksD, mat_RStudent;

    static FDistribution fDist;
    Matrix theDataMatrix;   // For regression the Y variable is col 0
    MultiVariateContinDataObj multVarContinDataObj;
    TDistribution tDist;

    // POJOs / FX
    
    // This constructor is for doing VIFs
    public MultReg_Model(Data_Manager dm, MultiVariateContinDataObj multVarContinDataObj) {   
        this.dm = dm;
        dm.whereIsWaldo(60, waldoFile, "Constructing");
        this.multVarContinDataObj = multVarContinDataObj;
        this.multVarContinDataObj = multVarContinDataObj;
        nRows = multVarContinDataObj.getNCompleteCases();    //  Legal rows
        nColumns = multVarContinDataObj.getNVariables();
        theDataMatrix = new Matrix(nRows, nColumns);
        theDataMatrix = multVarContinDataObj.returnDataAsMatrix();
        //  Prevent infinite regress;
        calculateTheVIFs = false;
    }
    
    public MultReg_Model(Data_Manager dm, MultReg_Controller multReg_Controller, MultiVariateContinDataObj multVarContinDataObj) {   
        this.dm = dm;
        dm.whereIsWaldo(73, waldoFile, "Constructing");
        this.multVarContinDataObj = multVarContinDataObj;
        nRows = multVarContinDataObj.getNCompleteCases();    //  Legal rows
        nColumns = multVarContinDataObj.getNVariables();
        theDataMatrix = new Matrix(nRows, nColumns);
        theDataMatrix = multVarContinDataObj.returnDataAsMatrix();
        calculateTheVIFs = true;
        
        if (multVarContinDataObj.getNMissingCases() > 0) {
            MyAlerts.showUnequalNsInMultivariateProcessAlert();
        }
    }
    
    //  The QDVs are needed for labels
    public void setupRegressionAnalysis() {     
        dm.whereIsWaldo(88, waldoFile, "setupRegressionAnalysis()");
        mat_X = new Matrix (nRows, nColumns);  // 1's plus X
        mat_Y = new Matrix (nRows, 1);

        str_DataLabels = new String[nColumns];
        str_DataLabels = multVarContinDataObj.getDataLabels();
        
        // Add column of 1's to X matrix as column[0]
        double sumY = 0.0;
        
        for (int ithRow = 0; ithRow < nRows; ithRow++) {
            double tempY = theDataMatrix.get(ithRow, 0);
            mat_Y.set(ithRow, 0, tempY);  
            sumY += tempY;
            mat_X.set(ithRow, 0, 1.0);  //  Add the columns of 1's 
            
            for (int jthCol = 1; jthCol < nColumns; jthCol++) {
                double tempX = theDataMatrix.get(ithRow, jthCol);    
                mat_X.set(ithRow, jthCol, tempX);          
            }
        }        
        sumY2_Over_n = sumY * sumY / (double)nRows;
        paramTerm = new String[nColumns];  // 1's + x's
        paramTerm[0] = "Intercept";
        System.arraycopy(str_DataLabels, 1, paramTerm, 1, nColumns - 1); //  x, y
    }

    public String doRegressionAnalysis() {
        dm.whereIsWaldo(116, waldoFile, "doRegressionAnalysis()");
        //  Set up variables and matrices
        double jjResid;
        double tempDouble_01, tempDouble_02, tempDouble_03, tempDouble_04;
        
        n = mat_Y.getRowDimension();
        k = mat_X.getColumnDimension() - 1; //  X now contains the 1's
        p = k + 1;  // MPV, p79

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
        // Calculate Sums of Squares
        BXY = (mat_BetaHats.transpose()).times(mat_XPrime.times(mat_Y));
        Y_Prime_Y = (mat_Y.transpose()).times(mat_Y);
        
        ssReg = BXY.get(0,0) - sumY2_Over_n;
        /*******************************************************
        *  It is very rare but possible that ssReg < 0 b/c     *
        *  of roundinf in the matrix calculations.             *
        *******************************************************/
        
        if (ssReg < 0.0) { ssReg = 0.0; }
        
        SSRes = Y_Prime_Y.minus(BXY);   //  This is the Matrix
        ssResid = SSRes.get(0,0);       // This is the scala
        ssTotal = Y_Prime_Y.get(0,0) - sumY2_Over_n;      
        // Calculate regression summary
        r2 = ssReg / ssTotal;
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
        msResid = ssResid / (double)(dfResid);
        fStatistic = msReg / msResid;  
        fDist = new FDistribution(k, dfResid);

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
        
        mat_DFFITS = new Matrix(n, 1);
        
        for (int ithRow = 0; ithRow < n; ithRow++) {
            double temp1 = mat_Hat.get(ithRow, ithRow);
            double temp2 = mat_RStudent.get(ithRow, 0);
            mat_DFFITS.set(ithRow, 0, temp1 * temp2);
        }

        stErrCoef = new Matrix(k + 1, 1);  // Explanatory variables + intercept 
        tStat = new Matrix(k + 1, 1);  
        PValue_T = new Matrix(k + 1, 1); 
        tDist = new TDistribution (dfResid);
        
        for (int predictors = 0; predictors <= k; predictors++) {
           stErrCoef.set(predictors, 0, Math.sqrt(msResid * mat_InvXPrimeX.get(predictors, predictors)));
           tStat.set(predictors, 0, mat_BetaHats.get(predictors, 0) / stErrCoef.get(predictors, 0));
           PValue_T.set(predictors, 0, 2.0 * tDist.getRightTailArea(Math.abs(tStat.get(predictors, 0))));
        }
        
        if (calculateTheVIFs) { calculateTheVIFs(); }        
        return "OK";
   }
    
   public double[] calculateTheVIFs() {
       double temp_r2;
       dm.whereIsWaldo(218, waldoFile, "calculateTheVIFs()");
       theVIFs = new double[nColumns];
       // Regress the ithVariable on all the others
       ArrayList<ColumnOfData> theXVariables = new ArrayList<>();
       theXVariables = multVarContinDataObj.getTheDataColumns();
       
       for (int ithXVariable = 1; ithXVariable < nColumns; ithXVariable++) {
            ArrayList<ColumnOfData> tempColumns = new ArrayList<>();
            // The ithXVariable is the regressee
            tempColumns.add(theXVariables.get(ithXVariable));
            
            for (int jthXVariable = 1; jthXVariable < nColumns; jthXVariable++) {
                if (ithXVariable != jthXVariable) {
                   tempColumns.add(theXVariables.get(jthXVariable));
                }
            }
            
            MultiVariateContinDataObj tempMCDO = new MultiVariateContinDataObj(dm, tempColumns);
            MultReg_Model tempMultReg = new MultReg_Model(dm, tempMCDO);
            tempMultReg.setupRegressionAnalysis();
            tempMultReg.doRegressionAnalysis(); 
            temp_r2 = tempMultReg.getRSquared();
            
            if (temp_r2 > 0) { theVIFs[ithXVariable] = 1.0 / (1.0 - temp_r2);
            } else {
                theVIFs[ithXVariable] = 1.0;
            }
       }      
       return theVIFs;
   }
    
   public static String leftMostChars(String original, int leftChars) {
       return StringUtilities.truncateString(original, leftChars);
   }
   
   public void printStatistics() {   
       dm.whereIsWaldo(254, waldoFile, "printStatistics()");
       regressionReport = new ArrayList<>();
       regressionDiagnostics = new ArrayList<>();

       print_ParamEstimates();
       print_ANOVA_Table();
       print_Diagnostics(); 
   }
   
   public void print_ANOVA_Table() {
        dm.whereIsWaldo(264, waldoFile, "print_ANOVA_Table()");
        addNBlankLinesToRegressionReport(2);
        regressionReport.add("                            Analysis of Variance");
        addNBlankLinesToRegressionReport(2);
        regressionReport.add(String.format("Source of           Sum of     Degrees of"));
        addNBlankLinesToRegressionReport(1);
        regressionReport.add(String.format("Variation           Squares      Freedom         Mean Square       F         P-value"));
        addNBlankLinesToRegressionReport(1);
        regressionReport.add(String.format("-----------------------------------------------------------------------------------"));
        addNBlankLinesToRegressionReport(1);
        sourceString = leftMostChars("Regression", 12);
        regressionReport.add(String.format("%10s    %13.3f      %4d             %8.2f     %8.3f      %6.4f", sourceString,  ssReg,  dfReg,  msReg,  fStatistic, pValue_F));
        addNBlankLinesToRegressionReport(1);
        sourceString = leftMostChars("Residual", 12);
        regressionReport.add(String.format("%10s    %13.3f      %4d             %8.2f", sourceString, ssResid, dfResid,  msResid));
        addNBlankLinesToRegressionReport(1);
        sourceString = leftMostChars("Total", 12);
        regressionReport.add(String.format("%10s    %13.3f      %4d\n", sourceString, ssTotal, dfTotal));
        regressionReport.add(String.format("-----------------------------------------------------------------------------------"));
        addNBlankLinesToRegressionReport(1);
   }
   
   public void print_Diagnostics() {
       dm.whereIsWaldo(287, waldoFile, "print_Diagnostics()");
       double jjResid, jjStandResid, jjStudResid, jjLeverage, jjCooksD, jjRStud;           
       addNBlankLinesToDiagnosticReport(2);
        regressionDiagnostics.add("                                    Regression Diagnostics");
        addNBlankLinesToDiagnosticReport(1);
        regressionDiagnostics.add(String.format("-----------------------------------------------------------------------------------------------"));
        addNBlankLinesToDiagnosticReport(1);
        regressionDiagnostics.add("                                          Studentized     Studentized");
        addNBlankLinesToDiagnosticReport(1);
        regressionDiagnostics.add(String.format("                           Standardized   (Internal)      (External)"));
        addNBlankLinesToDiagnosticReport(1);
        regressionDiagnostics.add(String.format("Observation     Residual     Residual       Residual       Residual   Leverage      Cook's D"));
        addNBlankLinesToDiagnosticReport(1);
        regressionDiagnostics.add(String.format("-----------------------------------------------------------------------------------------------"));
        addNBlankLinesToDiagnosticReport(1);       
        double leverageWarningTrigger = 2.0 * p / n; //  p213
       
        for (int jj = 0; jj < n; jj++) {
            int jjIndex = jj + 1;
            jjResid = mat_Resids.get(jj, 0);
            jjStandResid = mat_StandResids.get(jj, 0);
            jjStudResid = mat_StudResids.get(jj, 0);
            jjLeverage = mat_Hat.get(jj, jj);        
            jjCooksD = mat_CooksD.get(jj, 0);
            jjRStud = mat_RStudent.get(jj, 0); 
            regressionDiagnostics.add(String.format(" %5d         %8.3f     %8.3f       %8.3f       %8.3f    %8.3f     %8.4f", 
                             jjIndex, jjResid,  jjStandResid,  jjStudResid,  jjRStud, jjLeverage, jjCooksD));
            addNBlankLinesToDiagnosticReport(1);
        }       
        //  Print diagnostic advisories       
        int dfR_Student = n - p - 1;
        addNBlankLinesToDiagnosticReport(1);
        regressionDiagnostics.add(String.format("-----------------------------------------------------------------------------------------------"));
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
       dm.whereIsWaldo(331, waldoFile, "print_ParamEstimates()");
        double lowBound, daParam, hiBound;
        String parameter;
        addNBlankLinesToRegressionReport(2);
        String sourceString0, sourceString1, sourceString2, sourceString3;

        // Print equation on one line if simple regression
        if (k == 1)  {
            sourceString0 = "The regression equation is:";
            sourceString1 = leftMostChars(paramTerm[2], 10) + " = ";
            sourceString2 = leftMostChars(paramTerm[1], 10);

            String tempRegrEq = String.format(" %20s  %10s %8.3f %3s %8.3f %10s",
                                 sourceString0, sourceString1, mat_BetaHats.get(0, 0), "+",
                                 mat_BetaHats.get(1, 0), sourceString2
                              );
            deBlankedRegEq = StringUtilities.eliminateMultipleBlanks(tempRegrEq);
            regressionReport.add(deBlankedRegEq);            
        }        
        else {
            sourceString0 = "The regression equation is: \n\n";
            sourceString1 = leftMostChars(str_DataLabels[0], 10) + " = ";
            sourceString2 = String.format(" %8.3f ", mat_BetaHats.get(0, 0));
            sourceString3 = sourceString0 + sourceString1 + sourceString2;
            for (int jj = 1; jj <= k; jj++) {
                sourceString = leftMostChars(paramTerm[jj], 10);
                sourceString3 += String.format(" %1s %8.3f %10s", "+", mat_BetaHats.get(jj, 0), sourceString);
             }
            deBlankedRegEq = StringUtilities.eliminateMultipleBlanks(sourceString3);
            regressionReport.add(deBlankedRegEq);  
        } 
        
        addNBlankLinesToRegressionReport(2);
        regressionReport.add(String.format("---------------------------------------------------------------------------------------"));
        addNBlankLinesToRegressionReport(1);
        regressionReport.add(String.format("Parameter Estimates"));
        addNBlankLinesToRegressionReport(1);
        regressionReport.add(String.format("       Term                Estimate     Std Error     t Ratio      P-value       VIF"));
        addNBlankLinesToRegressionReport(1);
        regressionReport.add(String.format("---------------------------------------------------------------------------------------"));
        addNBlankLinesToRegressionReport(1);
       
        for (int jj = 0; jj <= k; jj++) {
          sourceString = leftMostChars(paramTerm[jj], 21); 
          paramEst = mat_BetaHats.get(jj, 0);
          paramStdErr = stErrCoef.get(jj, 0);
          paramTRatio = tStat.get(jj, 0);
          paramPValue = PValue_T.get(jj, 0);
          double daVIF = theVIFs[jj];

          if (jj == 0) {    //  No VIF for the intercept
            regressionReport.add(String.format("%20s     %9.5f     %9.5f    %9.5f      %6.5f", sourceString,  paramEst,  paramStdErr,  paramTRatio,  paramPValue));
            addNBlankLinesToRegressionReport(1);
          }
          else {
            regressionReport.add(String.format("%20s     %9.5f     %9.5f    %9.5f      %6.5f     %6.5f", sourceString,  paramEst,  paramStdErr,  paramTRatio,  paramPValue,  daVIF));
            addNBlankLinesToRegressionReport(1);              
          }
              
        }    
       
        addNBlankLinesToRegressionReport(1); 
        regressionReport.add(String.format("           %4s  %6.3f    %8s %5.3f     %12s  %5.3f", "S = ", s, "R-sq = ", r2, "R-sq(adj) = ", adj_r2));
        addNBlankLinesToRegressionReport(1);               
        double theCriticalValue = tDist.getInvRightTailArea(0.475);
        addNBlankLinesToRegressionReport(2);
        regressionReport.add(String.format("-----------------------------------------------------------------------------------"));
        addNBlankLinesToRegressionReport(1);
        regressionReport.add(String.format("%40s", "                           95% Confidence interval"));
        addNBlankLinesToRegressionReport(1);
        regressionReport.add(String.format("     Parameter            Lower bound       Estimate         Upper Bound"));
        addNBlankLinesToRegressionReport(2);
        
        for (int ithParam = 0; ithParam <= k; ithParam++) {
            parameter = leftMostChars(paramTerm[ithParam], 21);
            daParam = mat_BetaHats.get(ithParam, 0);
            lowBound = daParam - theCriticalValue * stErrCoef.get(ithParam, 0);        
            hiBound = daParam + theCriticalValue * stErrCoef.get(ithParam, 0);
            regressionReport.add(String.format(" %20s     %9.5f         %9.5f        %9.5f", parameter,  lowBound,  daParam,  hiBound));
            addNBlankLinesToRegressionReport(1);
        }
        
        //  Need a confidence interval for s???
        /*
        parameter = leftMostChars("Error", 21);
        double x2Low = x2Dist.getInvLeftTailArea(0.025);
        double x2Hi = x2Dist.getInvRightTailArea(0.025);
        lowBound = Math.sqrt(dfResid * msResid / x2Hi);
        daParam = s;
        hiBound = Math.sqrt(dfResid * msResid / x2Low);
        regressionReport.add(String.format(" %20s     %9.5f         %9.5f        %9.5f", parameter,  lowBound,  daParam,  hiBound));
        addNBlankLinesToRegressionReport(1);
        */
        regressionReport.add(String.format("-----------------------------------------------------------------------------------"));    
   }
   
    private void addNBlankLinesToRegressionReport(int thisMany) {
        StringUtilities.addNLinesToArrayList(regressionReport, thisMany);
    }
    
    private void addNBlankLinesToDiagnosticReport(int thisMany) {
        StringUtilities.addNLinesToArrayList(regressionDiagnostics, thisMany);
    }
   
   public ArrayList<String> getRegressionReport() { return regressionReport; }
   public ArrayList<String> getDiagnostics() { return regressionDiagnostics; }      
   public int getNRows()    {return nRows;}
   public String[] getLabels() { return str_DataLabels; }
   public Matrix getXVar() {return XVar;}
   public Matrix getX() { return mat_X; }
   public Matrix getY() {return mat_Y;}
   public Matrix getYHats() {return mat_YHats;}
   public Matrix getResids() {return mat_Resids;}
   public Matrix getStandardizedResids() {return mat_StandResids;}
   public Matrix getStudentizedResids() {return mat_StudResids;}
   public Matrix getR_StudentizedResids() {return mat_RStudent;}
   public Matrix getDFFITS() { return mat_DFFITS; }
   public Matrix getCooksD() { return mat_CooksD; }
   public int getRegDF() { return dfResid; }
   public double getRSquared() { return r2; }
   public double getSimpleRegSlope()  {return mat_BetaHats.get(1, 0);}
   public double getSimpleRegIntercept()  {return  mat_BetaHats.get(0, 0);}
   public double getTStat()  {return  paramTRatio;}
   public double getPValue()  {return  paramPValue;}   
   public Data_Manager getDataManager() { return dm; }
}