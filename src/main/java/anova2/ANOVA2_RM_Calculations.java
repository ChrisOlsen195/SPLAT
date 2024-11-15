/**************************************************
 *           ANOVA2_RM_Calculations               *
 *                  05/24/24                      *
 *                   12:00                        *
 *************************************************/
/**************************************************
 *     Tested against Cohen p513                  *
 *              10/08/23                          * 
 *************************************************/
package anova2;

import java.util.ArrayList;
import utilityClasses.*;

import splat.*;
import dataObjects.*;
import bivariateProcedures_Categorical.*;
import java.util.Collections;
import matrixProcedures.Matrix;
import probabilityDistributions.ChiSquareDistribution;
import theRProbDists.F;

public class ANOVA2_RM_Calculations {
    //  POJOs
    private boolean designIsBalanced, thereAreReplications, dataAreMissing;
                    //fileStructureIsOK;

    private int numRows, nCasesInStruct, nColumnsInStruct, nTimes, 
            nMeasures, nSubjects;
    
    double det_CSC, trace_CSC, mauchly_W, wChiSquare, epsilon_hat, 
           epsilon_tilde, mauchly_DF, mauchly_PValue;
    
    double df_Num_GG, df_Den_GG; 
    double df_Num_HF, df_Den_HF;
    double df_Num_Adj, df_Den_Adj;
    double df_Num_LB, df_Den_LB;
    
    double pValue_GG, pValue_HF, pValue_Adj, pValue_LB; 
    double fValue_GG, fValue_HF, fValue_Adj, fValue_LB;
    double msValue_GG, msValue_HF, msValue_Adj, msValue_LB;
    
    //String waldoFile = "ANOVA2_RM_Calculations";
    String waldoFile = ""; 
    
    private String str_NameResponse, returnStatus;
    private String[] str_FactorB_Labels;   
     
    // My classes
    ANOVA2_RM_Controller anova2_RM_Controller;
    ANOVA2_RM_Model anova2_RM_Model;
    ChiSquareDistribution x2Dist;
    F fDist_GG, fDist_HF, fDist_LB;
    Matrix mat_Covariance; 
    Matrix mat_C, mat_Ortho_Left, mat_Ortho_Right;
    Matrix mat_CSigmaC, mat_Temp;

    // POJOs / FX
    ColumnOfData col_Subjects, col_Treatments, col_Responses;
    Data_Manager dm;

    public ANOVA2_RM_Calculations(Data_Manager dm, ANOVA2_RM_Controller anova2_RM_Controller) {
        this.dm = dm;
        dm.whereIsWaldo(64, waldoFile, " *** constructing ***");
        this.anova2_RM_Controller = anova2_RM_Controller;
        nMeasures = anova2_RM_Controller.getNMeasures();
        nSubjects = anova2_RM_Controller.getNSubjects();
        returnStatus = "OK";
        dataAreMissing = false;
    }
    
    public String calculateTheCovarianceMatrix() {
        dm.whereIsWaldo(73, waldoFile, "calculateTheCovarianceMatrix()");
        mat_Covariance = new Matrix(nTimes, nTimes);
        for (int ithTime = 1; ithTime <= nTimes; ithTime++) {            
            for (int jthTime = 1; jthTime <= nTimes; jthTime++) {                
                ColumnOfData X = new ColumnOfData(dm.getSpreadsheetColumn(ithTime));
                ColumnOfData Y = new ColumnOfData(dm.getSpreadsheetColumn(jthTime));
                ArrayList<ColumnOfData> XandY = new ArrayList();
                XandY.add(X);
                XandY.add(Y); 
                BivariateContinDataObj xy = new BivariateContinDataObj(dm, XandY);
                
                if (xy.getDataExists() == true) {
                    xy.continueConstruction();
                }
                else
                {
                    MyAlerts.showNoLegalBivDataAlert();
                    returnStatus = "Cancel";
                    return returnStatus;
                }
                double cov = xy.getCovariance();
                mat_Covariance.set(ithTime - 1, jthTime - 1, cov);
            }
        }
        return "OK";
    }

    
/***************************************************************
 *                       Epsilon-hat                           *
 *  Kirk, R. E. (2013).  Experimental Design:Procedures for    * 
 *  the Behavioral Sciences.  Sage: Los Angeles                *
 *                         p313 - 4.                           *
 **************************************************************/
    public void adjustTheDistributions() {
        dm.whereIsWaldo(108, waldoFile, "adjustTheDistributions()");
        anova2_RM_Model = anova2_RM_Controller.get_RM_Model();
        double fValue_Unadjusted = anova2_RM_Model.get_RM_Model_F();
        df_Num_HF = epsilon_tilde * (nMeasures - 1.);
        df_Den_HF = epsilon_tilde * (nSubjects - 1.) * (nMeasures - 1.);
        fDist_HF = new F(df_Num_HF, df_Den_HF);
        fValue_HF = fValue_Unadjusted;
        pValue_HF = fDist_HF.getRightArea(df_Num_HF, df_Den_HF, fValue_HF);

        df_Num_GG = epsilon_hat * (nMeasures - 1.);
        df_Den_GG = epsilon_hat * (nMeasures - 1.) * (nSubjects - 1.);
        fDist_GG = new F(df_Num_GG, df_Den_GG);
        fValue_GG = fValue_Unadjusted;
        pValue_GG = fDist_GG.getRightArea(df_Num_GG, df_Den_GG, fValue_Unadjusted);
        
        df_Num_LB = 1.0;
        df_Den_LB = nSubjects - 1.;
        fDist_LB = new F(df_Num_LB, df_Den_LB);
        fValue_LB = fValue_Unadjusted;
        pValue_LB = fDist_LB.getRightArea(df_Num_LB, df_Den_LB, fValue_Unadjusted);
    }
    
/***************************************************************
 *                     Sphericity Condition                    *
 *  Kirk, R. E. ((2013).  Experimental Design:Procedures for   * 
 *  the Behavioral Sciences.  Sage: Los Angeles                *
 *                      p306 - 10.                             *
 **************************************************************/
    public void doLocallyBestInvariant() {
        dm.whereIsWaldo(137, waldoFile, "doLocallyBestInvariant()");
        double dblIthRow;
        
        mat_C = new Matrix(nMeasures - 1, nMeasures);
        mat_Ortho_Left = new Matrix(nMeasures - 1, nMeasures);
        mat_Ortho_Right = new Matrix(nMeasures, nMeasures - 1);
        
        for (int ithRow = 0; ithRow < nMeasures - 1; ithRow++) {
            dblIthRow = ithRow;            
            for (int jthCol = 0; jthCol <= ithRow; jthCol++) {
                mat_C.set(ithRow, jthCol, 1.0 / (dblIthRow + 1.0));
            }
            mat_C.set(ithRow, ithRow + 1, -1.0);
            for (int jthCol = ithRow + 2; jthCol < nMeasures; jthCol++) {
                mat_C.set(ithRow, jthCol, 0.0);
            }
        }

        // Left orthoganal matrix
        for (int ithRow = 0; ithRow < nMeasures - 1; ithRow++) {
           double rowSumSquares = 0.0;           
           for (int jthCol = 0; jthCol < nMeasures; jthCol++) {
               rowSumSquares += (mat_C.get(ithRow, jthCol) * (mat_C.get(ithRow, jthCol))); 
           }           
           double sqrSumSquares = Math.sqrt(rowSumSquares);           
           for (int jthCol = 0; jthCol < nMeasures; jthCol++) {
               mat_Ortho_Left.set(ithRow, jthCol, mat_C.get(ithRow, jthCol) / sqrSumSquares);
           }     
        }
        
        mat_Ortho_Right = mat_Ortho_Left.transpose(); 
        calculateTheCovarianceMatrix(); 
        mat_CSigmaC = new Matrix(nMeasures - 1, nMeasures - 1);  
        mat_Temp = new Matrix(nMeasures - 1, nMeasures);
        mat_Temp = mat_Ortho_Left.times(mat_Covariance);
        mat_CSigmaC = mat_Temp.times(mat_Ortho_Right);

        det_CSC = mat_CSigmaC.det();
        trace_CSC = mat_CSigmaC.trace();
        double traceSquared = trace_CSC * trace_CSC;
        double traceOfSquares = 0.0;
        
        for (int ithTrace = 0; ithTrace < nMeasures - 1; ithTrace++) {            
            for (int jthTrace = 0; jthTrace < nMeasures -1; jthTrace++) {
                double temp_Trace = mat_CSigmaC.get(ithTrace, jthTrace);
                traceOfSquares += (temp_Trace * temp_Trace);
            }
        }
        
        epsilon_hat = traceSquared / ((nMeasures - 1) * traceOfSquares);
        epsilon_tilde = epsilon_hat;
        double epsilon_tilde_numerator = nSubjects * (nMeasures - 1.) * epsilon_hat - 2.;
        double epsilon_tilde_denominator = (nMeasures - 1) * ((nSubjects - 1) - (nMeasures - 1.) * epsilon_hat);
        epsilon_tilde = epsilon_tilde_numerator / epsilon_tilde_denominator;
        // For possible future use in using epsilon-tilde -- vAsterisk
        double vAsteriskLeft = (nMeasures - 1) * (nSubjects - 1.) / 2.0;
        double vAsteriskRight = (nMeasures - 1) * traceOfSquares / traceSquared - 1.;
        double vAsterisk = vAsteriskLeft * vAsteriskRight;  
    }
    
    public void doMauchlyTest() {
        double k = nMeasures;
        double kMinusOne = k - 1.0;
        mauchly_DF = k * (k - 1.) / 2. - 1.;
        mauchly_W = det_CSC / Math.pow((trace_CSC / kMinusOne), kMinusOne);
        double temp1 = 2. * kMinusOne * kMinusOne + kMinusOne  + 2.;
        double temp2 = 6. * kMinusOne * (nSubjects - 1);
        double d = 1.0 - temp1 / temp2;
        wChiSquare = -(nSubjects - 1) * d * Math.log(mauchly_W);
        x2Dist = new ChiSquareDistribution(mauchly_DF);
        mauchly_PValue = x2Dist.getRightTailArea(wChiSquare);
    }    
    
    public String doTheRMStuff() { 
        dm.whereIsWaldo(211, waldoFile, "doTheRMStuff()");
        nCasesInStruct = dm.getNCasesInStruct();
        nColumnsInStruct = dm.getNVarsInStruct();   // Not sure this is needed
        
        // Cases and columns in the original RM file
        if ((nCasesInStruct == 0) || (nColumnsInStruct == 0)) {
            MyAlerts.showAintGotNoDataAlert_1Var();
            return "Cancel";
        }  
        
        //fileStructureIsOK = checkTheFileStructure();
        
        nTimes = nColumnsInStruct - 1;
       
        /******************************************************************
         *   Check for missing data here to avoid multiple error messages *
         *   when checking for missing data in the BivCon calculations.   *
         *   Can't check until nTimes is known.                           *
         *****************************************************************/
        for (int ithTime = 1; ithTime <= nTimes; ithTime++) {
            ColumnOfData X = new ColumnOfData(dm.getSpreadsheetColumn(ithTime));
            if (X.getHasMissingData()) { 
                dataAreMissing = true; 
            }            
        }

        if (dataAreMissing) { 
            MyAlerts.showBadRMFileStructureAlert();
            return "MissingData"; 
        } // else...
        doTheComputeStuff();
        return "OK";
    } 
    
    public void doTheComputeStuff() {
        dm.whereIsWaldo(246, waldoFile, "doTheComputeStuff()");
        constructTheRMColumns();
        BivCat_Model bivCatModel = new BivCat_Model( col_Subjects, col_Treatments, "RM");
        designIsBalanced = bivCatModel.getDesignIsBalanced();
        thereAreReplications = bivCatModel.getThereAreReplications();
        dataAreMissing = bivCatModel.getDataAreMissing();

        ArrayList<String> tmpCodes1;
        tmpCodes1 = getIndividualValues(0);
        numRows = tmpCodes1.size();

        str_FactorB_Labels = new String[numRows];            
        for (int i = 0; i < numRows; i++) {
            str_FactorB_Labels[i] = tmpCodes1.get(i);
        }        
    }
    
    private void constructTheRMColumns() {
        dm.whereIsWaldo(264, waldoFile, "constructTheRMColumns()");
        String tempStringA, tempStringB, tempStringC;
        int theNewNRows = nCasesInStruct * (nColumnsInStruct - 1);
        col_Subjects = new ColumnOfData(theNewNRows, "Subjects");
        col_Treatments = new ColumnOfData(theNewNRows, "Treatments"); 
        col_Responses = new ColumnOfData(theNewNRows, "Responses"); 
        
        for (int ithTime = 0; ithTime < nTimes; ithTime++){            
            for (int jthUnit = 0; jthUnit < nCasesInStruct; jthUnit++) {
                int tempRow = ithTime * nCasesInStruct + jthUnit;
                tempStringA = dm.getAllTheColumns().get(0).getStringInIthRow(jthUnit);
                col_Subjects.setStringInIthRow(tempRow, tempStringA);
                tempStringB = dm.getAllTheColumns().get(ithTime + 1).getVarLabel();
                col_Treatments.setStringInIthRow(tempRow, tempStringB);
                tempStringC = dm.getAllTheColumns().get(ithTime + 1).getStringInIthRow(jthUnit);
                col_Responses.setStringInIthRow(tempRow, tempStringC);
            }   
        }      
    }
    
    public ArrayList<String> getIndividualValues(int groupingVar) {
        dm.whereIsWaldo(285, waldoFile, "getIndividualValues()");
        ArrayList<String> alstr_SortedTempCodes = new ArrayList();
        ArrayList<String> alstr_ValuesToReturn = new ArrayList();
        ArrayList<String> alstr_TempData = dm.getSpreadsheetColumnAsStrings(groupingVar, -1, null);

        for (int ith = 0; ith < alstr_TempData.size(); ith++) {
            alstr_SortedTempCodes.add(alstr_TempData.get(ith));
        }

        Collections.sort(alstr_SortedTempCodes);    //  Superfluous???

        alstr_ValuesToReturn.add(alstr_SortedTempCodes.get(0));
        for (int i = 1; i < alstr_TempData.size(); i++) {            
            if (!alstr_SortedTempCodes.get(i).equals(alstr_SortedTempCodes.get(i - 1))) {
                alstr_ValuesToReturn.add(alstr_SortedTempCodes.get(i));
            }
        }
        return alstr_ValuesToReturn;
    }
    
    public boolean checkTheFileStructure() {
        // Columns the same size?
        for (int ithCol = 0; ithCol < dm.getNVarsInStruct() - 1; ithCol++) {
            int ithColSize = dm.getAllTheColumns().get(ithCol).getColumnSize();
            int nextColSize = dm.getAllTheColumns().get(ithCol + 1).getColumnSize();
            if (ithColSize != nextColSize) {
                return false;
            }
        }
        
        for (int ithCol = 1; ithCol < dm.getNVarsInStruct(); ithCol++) {
            boolean isNumeric = dm.getAllTheColumns().get(ithCol).getIsNumeric();
            boolean hasMissingData = dm.getAllTheColumns().get(ithCol).getHasMissingData();
            if (!isNumeric || hasMissingData) {
                return false;
            }
        }        
        
        return true;    // File structure is OK
    }
    
    public ColumnOfData getCol_Subjects() { return col_Subjects; }
    public ColumnOfData getCol_Treatments() { return col_Treatments; }
    public ColumnOfData getCol_Responses() { return col_Responses; }    
    public boolean getDesignIsBalanced() { return designIsBalanced; } 
    public boolean getThereAreReplications() { return thereAreReplications; }
    public boolean getDataAreMissing() { return dataAreMissing; }
    public String getReturnStatus() { return returnStatus; }
    public String getResponse_Name() { return str_NameResponse; }        
    public double getMauchlyW() { return mauchly_W; }
    public double getMauchlyX2() { return wChiSquare; }
    public double getMauchly_DF() { return mauchly_DF; }
    public double getMauchly_PValue() { return mauchly_PValue; }
    public double get_NumDF_Greenhouse_Geisser() { return df_Num_GG; }
    public double get_DenDF_Greenhouse_Geisser() { return df_Den_GG; }
    public double getMS_Greenhouse_Geisser() { return msValue_GG; }
    public double getF_Greenhouse_Geisser() { return fValue_GG; }
    public double getPValue_Greenhouse_Geisser() { return pValue_GG; }
    public double get_NumDF_Huynh_Feldt() { return df_Num_HF; }
    public double get_DenDF_Huynh_Feldt() { return df_Den_HF; }
    public double getMS_Huynh_Feldt() { return msValue_HF; }
    public double getF_Huynh_Feldt() { return fValue_HF; }
    public double getPValue_Huynh_Feldt() { return pValue_HF; }
    public double get_NumDF_Adjusted() { return df_Num_Adj; }
    public double get_DenDF_Adjusted() { return df_Den_Adj; }
    public double getMS_Adjusted() { return msValue_Adj; }
    public double getF_Adjusted() { return fValue_Adj; }
    public double getPValue_Adjusted() { return pValue_Adj; }     
    public double get_NumDF_LB() { return df_Num_LB; }
    public double get_DenDF_LB() { return df_Den_LB; }
    public double getMS_LB() { return msValue_LB; }
    public double getF_LB() { return fValue_LB; }
    public double getPValue_LB() { return pValue_LB; }
}
