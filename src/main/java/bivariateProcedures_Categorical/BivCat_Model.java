/****************************************************************************
 *                         BivCat_Model                                     *
 *                           08/18/24                                       *
 *                             18:00                                        *
 ***************************************************************************/
package bivariateProcedures_Categorical;

import dataObjects.ColumnOfData;
import java.util.ArrayList;
import dataObjects.UnivariateCategoricalDataObj;
import splat.Data_Manager;

public class BivCat_Model {
    // POJOs
    boolean cleanReturnFromSummaryDialog, designIsBalanced, 
            thereAreReplications, dataAreMissing;
    
    int nLegalValues, nRows, nCols, nCells;
    int[][] observedValues;
    int[] rowTotals, columnTotals; 
    
    double dblNLegalValues;    
    double[] rowProportions, columnProportions, cumulativeRowProps, 
             cumulativeColumnProps,  cumMarginalRowProps;  
    
    double[][] observedProportion, cumulativeProportions;

    String strTopVariable, strLeftVariable, assocType, strReturnStatus;
    String[] strTopLabels, strLeftLabels;
    
    // Make empty if no-print
    String waldoFile = "BivCat_Model";
    //String waldoFile = "";

//  My classes   
    ArrayList<ColumnOfData> al_ColumnsOfData;
    BivCat_Controller bivCat_Controller;
    BivCat_SummaryDialog bivCat_SummaryDialog; 
    Data_Manager dm;
    UnivariateCategoricalDataObj ucdo_Top, ucdo_Left;
    ArrayList<ColumnOfData> al_ColumnOfData;

    // POJOs / FX
    
    public  BivCat_Model(BivCat_Controller bivCat_Controller, String assocType) { 
        System.out.println("\n47 BivCat_Model, Constructing");
        //System.out.println("48 BivCat_Model, assocType = " + assocType);
        this.bivCat_Controller = bivCat_Controller;
        this.assocType = assocType;
        dm = bivCat_Controller.getDataManager();
        dm.whereIsWaldo(55, waldoFile, "Constructing");
    }   
    
    /*************************************************************************
    *    This constructor  and method are only called from:                  *
    *         the ANOVA2_RCB_Dialog  line 331                                *
    *         and ANOVA2_RM_Calculations line 236                            *
    *    both of which procedures require equal n's.                         *
    *************************************************************************/ 
    public  BivCat_Model(ColumnOfData colA, ColumnOfData colB, String assocType) { 
        System.out.println("\n62 BivCat_Model, Constructing");
        this.assocType = assocType;
        designIsBalanced = false;       //  To avoid nulls
        thereAreReplications = false;   //  To avoid nulls
        strReturnStatus = doModelFromTwoFactorANOVA(colA, colB);
    } 
     
    public String doBivCatModelFromTable() {
        System.out.println("70 BivCat_Model, doModelFromTable()");
        bivCat_SummaryDialog = new BivCat_SummaryDialog(this);
        bivCat_SummaryDialog.showAndWait();
        strReturnStatus = bivCat_SummaryDialog.getReturnStatus();        
        if (strReturnStatus.equals("OK")) {
            nRows = bivCat_SummaryDialog.getNRows();
            nCols = bivCat_SummaryDialog.getNCols();
            
            constructNecessaryArrays();

            strTopVariable = bivCat_SummaryDialog.getTopLabel();
            strLeftVariable = bivCat_SummaryDialog.getLeftLabel();

            strTopLabels = bivCat_SummaryDialog.getXValues();
            strLeftLabels = bivCat_SummaryDialog.getYValues();
            
            nLegalValues = 0;

            for (int iRow = 0; iRow < nRows; iRow++) {
                for (int jCol = 0; jCol < nCols; jCol++) {
                   observedValues[iRow][jCol] = bivCat_SummaryDialog.get_IJth_Observed(iRow, jCol);
                   nLegalValues += observedValues[iRow][jCol];
                }
            }
            dblNLegalValues = nLegalValues;
            calculateTheProportions();
        } 
        return strReturnStatus;
    }
    
    private String doModelFromTwoFactorANOVA(ColumnOfData columnA, ColumnOfData columnB) {
        System.out.println("101 BivCat_Model, doModelFromTwoFactorANOVA(ColumnOfData columnA, ColumnOfData columnB)");
        al_ColumnsOfData = new ArrayList();
        al_ColumnsOfData.add(columnA); 
        al_ColumnsOfData.add(columnB); 
        
        if ((columnA.getHasMissingData()) || (columnB.getHasMissingData())) {
            dataAreMissing = true;
            return "Cancel";
        }
        
        nLegalValues = al_ColumnsOfData.get(0).getColumnSize();
        dblNLegalValues = nLegalValues;
        strTopVariable = al_ColumnsOfData.get(0).getVarLabel();
        strLeftVariable =  al_ColumnsOfData.get(1).getVarLabel();
        ucdo_Left = new UnivariateCategoricalDataObj(al_ColumnsOfData.get(0));
        ucdo_Top = new UnivariateCategoricalDataObj(al_ColumnsOfData.get(1));

        nRows = ucdo_Top.getNUniques();
        nCols = ucdo_Left.getNUniques();
        constructNecessaryArrays();

        strTopLabels = ucdo_Left.getUniqueCategories();
        strLeftLabels = ucdo_Top.getUniqueCategories();
        dataAreMissing = false;
        
        //   Count the replications for each treatment combination        
        for (int ithRow = 0; ithRow < nRows; ithRow++) {
            for (int jthCol = 0; jthCol < nCols; jthCol++) {
                String tempTopValue = strTopLabels[jthCol];
                for (int kthPoint = 0; kthPoint < nLegalValues; kthPoint++) {
                    String tempOutcomeDataPt = ucdo_Left.getIthValue(kthPoint);
                    String tempExposureDataPt = ucdo_Top.getIthValue(kthPoint);
                    
                    if (tempOutcomeDataPt.equals("*") || tempExposureDataPt.equals("*")) {
                        dataAreMissing = true;
                    }
                    
                    if ((tempTopValue.equals(tempOutcomeDataPt)
                        && (strLeftLabels[ithRow].equals(tempExposureDataPt)))) {
                            observedValues[ithRow][jthCol]++;                                   
                    }
                }
            }
        }
        // Check for potential imbalance in randomized block design 
        double minRepsInBlock = 9999.;
        double maxRepsInBlock = 0;
        
        for (int ithRow = 0; ithRow < nRows; ithRow++) {            
            for (int jthCol = 0; jthCol < nCols; jthCol++) {
                minRepsInBlock = Math.min(minRepsInBlock, observedValues[ithRow][jthCol]);
                maxRepsInBlock = Math.max(maxRepsInBlock, observedValues[ithRow][jthCol]);
            }
        }  
        
        designIsBalanced = (minRepsInBlock == maxRepsInBlock);
        thereAreReplications = (maxRepsInBlock != 1.0);       
        return "OK";
    } 
    
    public String doBivCatModelFromFile() {
        System.out.println("162 BivCat_Model, doModelFromFile()");
        al_ColumnOfData = new ArrayList();
        al_ColumnOfData = bivCat_Controller.getData(); 

        nLegalValues = al_ColumnOfData.get(0).getColumnSize();
        dblNLegalValues = nLegalValues;
        strTopVariable = al_ColumnOfData.get(0).getVarLabel();     //  From data col
        strLeftVariable =  al_ColumnOfData.get(1).getVarLabel();   //  From data col
  
        ucdo_Top = new UnivariateCategoricalDataObj(al_ColumnOfData.get(0));
        ucdo_Left = new UnivariateCategoricalDataObj(al_ColumnOfData.get(1));

        nRows = ucdo_Left.getNUniques();
        nCols = ucdo_Top.getNUniques();
        
        //if ((nRows < 2) || (nCols < 2)) {
        //    MyAlerts.showTooFewChiSquareDFAlert();
        //    return "Cancel";
        //}

        constructNecessaryArrays();

        strTopLabels = ucdo_Top.getUniqueCategories();
        strLeftLabels = ucdo_Left.getUniqueCategories();
        for (int ithRow = 0; ithRow < nRows; ithRow++) {            
            for (int jthCol = 0; jthCol < nCols; jthCol++) {                 
                String tempTopValue = strTopLabels[jthCol];                
                for (int ithPoint = 0; ithPoint < nLegalValues; ithPoint++) {
                    String tempTopDataPt = ucdo_Top.getIthValue(ithPoint);
                    String tempLeftDataPt = ucdo_Left.getIthValue(ithPoint);
                    if ((tempTopValue.equals(tempTopDataPt)
                        && (strLeftLabels[ithRow].equals(tempLeftDataPt)))) {
                            observedValues[ithRow][jthCol]++;
                    }
                }
            }
        }
        return "OK";
    }    
        
    private void constructNecessaryArrays() {
        //System.out.println("203 BivCat_Model, constructNecessaryArrays()");
        observedValues = new int[nRows][nCols];
        observedProportion = new double[nRows][nCols];
        rowTotals = new int[nRows];
        rowProportions = new double[nRows];
        cumulativeRowProps = new double[nRows + 1];    //  0 at the top
        cumMarginalRowProps = new double[nRows + 1];
        columnTotals = new int[nCols];
        columnProportions = new double[nCols];
        cumulativeColumnProps = new double[nCols + 1];    //  0 at the left
        cumulativeProportions = new double[nRows + 1][nCols + 1];    //  Internal cum props
    }
    
    public void closeTheSummaryDialog(boolean bool_ReturnStatus) { 
       bivCat_SummaryDialog.close();
    }
        
    public String calculateTheProportions() { 
        System.out.println("220 BivCat_Model, doChiSqAnalysisCalculations()");
        nCells = nRows * nCols;
              
        for (int ithRow = 0; ithRow < nRows; ithRow++) {            
            for (int jthCol = 0; jthCol < nCols; jthCol++) {
                observedProportion[ithRow][jthCol] = observedValues[ithRow][jthCol] / dblNLegalValues;
            }
        }

        for (int ithRow = 0; ithRow < nRows; ithRow++) {            
            rowTotals[ithRow] = 0;            
            for (int jthCol = 0; jthCol < nCols; jthCol++) {
                rowTotals[ithRow] += observedValues[ithRow][jthCol];
            }
        }
              
        for (int ithCol = 0; ithCol < nCols; ithCol++) {
            columnTotals[ithCol] = 0;            
            for (int jthRow = 0; jthRow < nRows; jthRow++) {
                columnTotals[ithCol] += observedValues[jthRow][ithCol];
            }
        }   
                   
        for (int ithRow = 0; ithRow < nRows; ithRow++) {
            rowProportions[ithRow] = rowTotals[ithRow] / dblNLegalValues; 
        }
              
        for (int ithCol = 0; ithCol < nCols; ithCol++) {
            columnProportions[ithCol] = columnTotals[ithCol] / dblNLegalValues;
        }
        
        cumulativeColumnProps[0] = 0;        
        for (int ithCol = 1; ithCol <= nCols; ithCol++) {
            cumulativeColumnProps[ithCol] = cumulativeColumnProps[ithCol - 1] + columnProportions[ithCol - 1];
        } 
       
        cumulativeRowProps[nRows] = 0;       
        for (int ithRow = 1; ithRow <= nRows; ithRow++) {
            cumulativeRowProps[nRows - ithRow] = cumulativeRowProps[nRows - ithRow + 1] + rowProportions[nRows - ithRow];
        }

        cumMarginalRowProps[nRows] = 0.0;        
        for (int ithRow = nRows - 1; ithRow >= 0; ithRow--) {
            cumMarginalRowProps[ithRow] = cumMarginalRowProps[ithRow + 1] + rowProportions[ithRow];
        }    

        cumMarginalRowProps[0] = 1.0;      
        for (int ithCol = 0; ithCol < nCols; ithCol++) {            
            cumulativeProportions[nRows][nCols] = 0.0;            
            for (int jthRow = nRows - 1; jthRow >= 0; jthRow--) {
                cumulativeProportions[jthRow][ithCol] = cumulativeProportions[jthRow + 1][ithCol] + observedProportion[jthRow][ithCol];
            }   //  end ithRow         
        } 
        return "OK";
    }
    
    public int getTotalN() {return nLegalValues; }
    
    public int getNumberOfRows() { return nRows; }
    public int getNumberOfColumns() { return nCols; }
    
    public int getNumberOfCells() { return nCells; }
    
    public String getTopVariable() {return strTopVariable; }
    public String getLeftVariable() {return strLeftVariable; } 

    public String[] getTopLabels() { return strTopLabels; }
    public String[] getLeftLabels() {  return strLeftLabels; }  
    
    public double[] getRowProportions() {return rowProportions; }
    public double[] getColumnProportions() {return columnProportions; } 
    public double[] getCumRowProps() { return cumulativeRowProps; }
    public double[] getCumColProps() { return cumulativeColumnProps; } 
    public int[] getRowTotals() { return rowTotals; }
    public int[] getColumnTotals() {return columnTotals; }
    public double[] getCumMarginalRowProps() { return cumMarginalRowProps; }
    
    public double[][] getCellCumProps() { return cumulativeProportions; }
    public int[][] getObservedValues() {return observedValues; }
    public double[][] getProportions() {return observedProportion; }
    
    public void setCleanReturnToProcedure(String toThis) { 
        strReturnStatus = toThis; 
    }

    public void setCleanReturnFromSummaryDialog(String toThis) {
        strReturnStatus = toThis;
    }
    
    public boolean getDesignIsBalanced() { return designIsBalanced; }
    public boolean getThereAreReplications() {return thereAreReplications; }
    public boolean getDataAreMissing() { return dataAreMissing; }    
    public String getAssociationType() { return assocType; }
}
