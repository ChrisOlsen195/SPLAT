/****************************************************************************
 *                         X2Assoc_Model                                    *
 *                           06/06/24                                       *
 *                             12:00                                        *
 ***************************************************************************/
package chiSquare_Assoc;

import dialogs.chisquare.X2Assoc_SummaryDialog;
import dataObjects.ColumnOfData;
import java.util.ArrayList;
import probabilityDistributions.* ;
import dataObjects.UnivariateCategoricalDataObj;
import utilityClasses.MyAlerts;

public class X2Assoc_Model {
    // POJOs
    
    int nLegalValues, nRows, nCols, nCells, nCellsBelow5, df;
    
    double chiSquare, pValue, cramersV, dblNLegalValues;    
    double[] rowProportions, columnProportions, cumulativeRowProps, 
             cumulativeColumnProps, rowTotals, columnTotals, cumMarginalRowProps;    
    double[][] observedValues, expectedValues, chiSquareContributions,
               residuals, standardizedResiduals, observedProportion,
               cumulativeProportions;

    String strTopVariable, strLeftVariable, strCategoryAxisLabel, strAssocType,
           strReturnStatus;
    
    String[] strTopValues, strLeftValues;

    //  My classes   
    ArrayList<ColumnOfData> al_ColumnOfData;
    ChiSquareDistribution x2Dist;
    UnivariateCategoricalDataObj ucdo_Top, ucdo_Left;
    X2Assoc_Controller x2Assoc_Controller;
    X2Assoc_SummaryDialog x2Assoc_SummaryDialog; 

    // POJOs / FX
    
    public  X2Assoc_Model(X2Assoc_Controller x2Assoc_Controller, String assocType) { 
        this.x2Assoc_Controller = x2Assoc_Controller;
        this.strAssocType = assocType;
    }    
                
    public String doModelFromFile() {
        //System.out.println("47 X2Assoc_Model, doModelFromFile()");
        al_ColumnOfData = new ArrayList();
        al_ColumnOfData = x2Assoc_Controller.getData(); 

        nLegalValues = al_ColumnOfData.get(0).getColumnSize();
        dblNLegalValues = nLegalValues;
        strTopVariable = al_ColumnOfData.get(0).getVarLabel();     //  From data col
        strLeftVariable =  al_ColumnOfData.get(1).getVarLabel();   //  From data col
  
        ucdo_Top = new UnivariateCategoricalDataObj(al_ColumnOfData.get(0));
        ucdo_Left = new UnivariateCategoricalDataObj(al_ColumnOfData.get(1));

        nRows = ucdo_Left.getNUniques();
        nCols = ucdo_Top.getNUniques();
        
        if ((nRows < 2) || (nCols < 2)) {
            MyAlerts.showTooFewChiSquareDFAlert();
            return "Cancel";
        }

        constructNecessaryArrays();

        strTopValues = ucdo_Top.getCategories();
        strLeftValues = ucdo_Left.getCategories();
        for (int iRow = 0; iRow < nRows; iRow++) {            
            for (int jCol = 0; jCol < nCols; jCol++) {                 
                String tempTopValue = strTopValues[jCol];                
                for (int ithPoint = 0; ithPoint < nLegalValues; ithPoint++) {
                    String tempTopDataPt = ucdo_Top.getIthValue(ithPoint);
                    String tempLeftDataPt = ucdo_Left.getIthValue(ithPoint);
                    if ((tempTopValue.equals(tempTopDataPt)
                        && (strLeftValues[iRow].equals(tempLeftDataPt)))) {
                            observedValues[iRow][jCol]++;
                    }
                }
            }
        }
        return "OK";
    } 
    
    public String doModelFromTable() {
        //System.out.println("88 X2Assoc_Model, doModelNotFromFile()");
        x2Assoc_SummaryDialog = new X2Assoc_SummaryDialog(this);
        x2Assoc_SummaryDialog.showAndWait();
        strReturnStatus = x2Assoc_SummaryDialog.getReturnStatus();        
        if (strReturnStatus.equals("OK")) {
            nRows = x2Assoc_SummaryDialog.getNRows();
            nCols = x2Assoc_SummaryDialog.getNCols();
            
            nLegalValues = 0;
            df = (nRows - 1) * (nCols - 1);
            
            if (df < 1) {
                MyAlerts.showTooFewChiSquareDFAlert();
                return "Cancel";
            }
        
            constructNecessaryArrays();

            strTopVariable = x2Assoc_SummaryDialog.getTopLabel();
            strLeftVariable = x2Assoc_SummaryDialog.getLeftLabel();

            strTopValues = x2Assoc_SummaryDialog.getXValues();
            strLeftValues = x2Assoc_SummaryDialog.getYValues();

            for (int iRow = 0; iRow < nRows; iRow++) {
                for (int jCol = 0; jCol < nCols; jCol++) {
                   observedValues[iRow][jCol] = x2Assoc_SummaryDialog.getObsVal_IJ(iRow, jCol);
                   nLegalValues += observedValues[iRow][jCol];
                }
            }
            dblNLegalValues = nLegalValues;
        } 
        return strReturnStatus;
    }
        
    private void constructNecessaryArrays() {
        //System.out.println("124 X2Assoc_Model, constructNecessaryArrays()");
        observedValues = new double[nRows][nCols];
        residuals = new double[nRows][nCols];
        standardizedResiduals = new double[nRows][nCols];
        expectedValues = new double[nRows][nCols];
        observedProportion = new double[nRows][nCols];
        chiSquareContributions = new double[nRows][nCols];
        rowTotals = new double[nRows];
        rowProportions = new double[nRows];
        cumulativeRowProps = new double[nRows + 1];    //  0 at the top
        cumMarginalRowProps = new double[nRows + 1];
        columnTotals = new double[nCols];
        columnProportions = new double[nCols];
        cumulativeColumnProps = new double[nCols + 1];    //  0 at the left
        cumulativeProportions = new double[nRows + 1][nCols + 1];    //  Internal cum props
        strLeftValues = new String[nRows]; 
        strTopValues = new String[nCols];
    }
     
    public void closeTheAssocDialog(boolean cleanReturn) {
        x2Assoc_SummaryDialog.close();
    }

    public String doChiSqAnalysisCalculations() {  
        //System.out.println("148 X2Assoc_Model, doChiSqAnalysisCalculations()");
        nCells = nRows * nCols;
        nCellsBelow5 = 0;
        
        // Construct the table counts and proportions        
        for (int row = 0; row < nRows; row++) {            
            for (int col = 0; col < nCols; col++) {
                observedProportion[row][col] = observedValues[row][col] / dblNLegalValues;
            }
        }

        // calculate row marginals, print at side
        for (int row = 0; row < nRows; row++) {
            rowTotals[row] = 0;           
            for (int col = 0; col < nCols; col++) {
                rowTotals[row] += observedValues[row][col];
            }
        }
        
        // calculate column marginals, print at bottom        
        for (int col = 0; col < nCols; col++) {            
            columnTotals[col] = 0;           
            for (int row = 0; row < nRows; row++) {
                columnTotals[col] += observedValues[row][col];
            }
        }   
            
        //  calculate row proportions
        for (int row = 0; row < nRows; row++) {
            rowProportions[row] = rowTotals[row] / dblNLegalValues; 
        }
        
        //  calculate col proportions        
        for (int col = 0; col < nCols; col++) {
            columnProportions[col] = columnTotals[col] / dblNLegalValues;
        }

        //  Calculate proportions, expectedValues, and resids        
        for (int row = 0; row < nRows; row++) {            
            for (int col = 0; col < nCols; col++) {                
                expectedValues[row][col] = rowTotals[row] * columnTotals[col] / dblNLegalValues ;                
                if (expectedValues[row][col] < 5) {
                    nCellsBelow5++;
                }
                residuals[row][col] = observedValues[row][col] - expectedValues[row][col];
            }
        }  
        //System.out.println("195 X2Assoc_Model, doChiSqAnalysisCalculations()");
        //  Chi square and contributions
        chiSquare = 0.0;
        for (int row = 0; row < nRows; row++) {            
            for (int col = 0; col < nCols; col++) {
                chiSquareContributions[row][col] = residuals[row][col] * residuals[row][col] / expectedValues[row][col];
                chiSquare += chiSquareContributions[row][col];
            }
        } 
        
        //  Standardized residuals
        for (int row = 0; row < nRows; row++) {            
            for (int col = 0; col < nCols; col++) {
                double temp = (1.0 - rowProportions[row]) * (1.0 - columnProportions[col]);
                standardizedResiduals[row][col] = residuals[row][col] / Math.sqrt(expectedValues[row][col] * temp);
            }
        }
                 
        cumulativeColumnProps[0] = 0;        
        for (int col = 1; col <= nCols; col++) {
            cumulativeColumnProps[col] = cumulativeColumnProps[col - 1] + columnProportions[col - 1];
        } 
       
        cumulativeRowProps[nRows] = 0;       
        for (int row = 1; row <= nRows; row++) {
            cumulativeRowProps[nRows - row] = cumulativeRowProps[nRows - row + 1] + rowProportions[nRows - row];
        }

        cumMarginalRowProps[nRows] = 0.0;        
        for (int row = nRows - 1; row >= 0; row--) {
            cumMarginalRowProps[row] = cumMarginalRowProps[row + 1] + rowProportions[row];
        }    

        cumMarginalRowProps[0] = 1.0;      
        for (int col = 0; col < nCols; col++) {
            cumulativeProportions[nRows][nCols] = 0.0;            
            for (int row = nRows - 1; row >= 0; row--) {
                cumulativeProportions[row][col] = cumulativeProportions[row + 1][col] + observedProportion[row][col];
            }   //  end row         
        } 

        // Cramer's V
        double temp = Math.min(nRows - 1, nCols - 1);
        cramersV = Math.sqrt(chiSquare / (dblNLegalValues * temp)); 
        df = (nRows - 1)*(nCols - 1);
        
        if (df < 1) {
            MyAlerts.showTooFewChiSquareDFAlert();
            return "Cancel";
        }
        x2Dist = new ChiSquareDistribution(df);
        pValue = x2Dist.getRightTailArea(chiSquare);
        return "OK";
    }
    
    public String getReturnStatus() { return strReturnStatus; }
    public int getDF() { return df; }  
    public double getChiSquare()  {return chiSquare; }
    public double getCramersV() {return cramersV; }
    public double getPValue() { return pValue; }
    public double getTotalN() {return nLegalValues; }
    public int getNumberOfRows() { return nRows; }
    
    public int getNumberOfColumns() { return nCols; }
    public int getNumberOfCells() { return nCells; }
    public int getNumberOfCellsBelow5() { return nCellsBelow5; }
    
    public String getTopVariable() { return strTopVariable; }
    public String getLeftVariable() { return strLeftVariable; } 
    public String[] getTopLabels() { return strTopValues; }
    public String[] getLeftLabels() {return strLeftValues; }  
    public double[] getRowProportions() {return rowProportions; }
    public double[] getColumnProportions() {return columnProportions; } 
    public double[] getCumRowProps() { return cumulativeRowProps; }
    public double[] getCumColProps() { return cumulativeColumnProps; } 
    public double[] getRowTotals() { return rowTotals; }
    public double[] getColumnTotals() {return columnTotals; }
    
    public double[] getCumMarginalRowProps() { return cumMarginalRowProps; }
    public double[][] getCellCumProps() { return cumulativeProportions; }
    public double[][] getObservedValues() {return observedValues; }
    public double[][] getExpectedValues() {return expectedValues; }
    public double[][] getX2Contributions() {return chiSquareContributions; }
    public double[][] getStandardizedResiduals() {return standardizedResiduals; }
    public double[][] getProportions() {return observedProportion; }
    
    public void setLabelForCategoryAxis(String toThisLabel) {
        strCategoryAxisLabel = toThisLabel;
    }
    
    public String getLabelForCategoryAxis() { return strCategoryAxisLabel; } 
    public String getLabelForVerticalAxis() { return strCategoryAxisLabel; }
    public String getAssociationType() { return strAssocType; }
}
