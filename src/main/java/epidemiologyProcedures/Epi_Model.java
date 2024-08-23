/****************************************************************************
 *                           Epi_Model                                     *
 *                           08/21/24                                       *
 *                             15:00                                        *
 ***************************************************************************/
package epidemiologyProcedures;

import dataObjects.ColumnOfData;
import java.util.ArrayList;
import dataObjects.UnivariateCategoricalDataObj;
import splat.Data_Manager;

public class Epi_Model {
    // POJOs
    boolean cleanReturnFromSummaryDialog, designIsBalanced, 
            thereAreReplications, dataAreMissing;
    
    int nLegalValues, nUniqueExposures, nUniqueOutcomes, nCells, nCellsBelow5;
    int[][] replicates, observedValues;
    int[] rowTotals, columnTotals;
    
    double dblNLegalValues;    
    double[] rowProportions, columnProportions, cumulativeRowProps, 
             cumulativeColumnProps,  cumMarginalRowProps;  
    
    double[][]  expectedValues, observedProportion, cumulativeProportions;

    String strTopVariable, strLeftVariable, assocType, returnStatus, 
           cleanReturn, valExpNo, valExpYes, valOutNo, valOutYes;
    String[] strUniqueOutcomes, strUniqueExposures;
    String[] strOutcomeValues, strExposureValues;
    
    String tempTopDataPt, tempLeftDataPt;
    
    // Make empty if no-print
    String waldoFile = "Epi_Model";
    //String waldoFile = "";

//  My classes   
    ArrayList<ColumnOfData> al_ColumnsOfData;
    ArrayList<String> str_ArrayList_4_RiskView;
    Epi_Controller epi_Controller;
    Epi_SummaryDialog epi_SummaryDialog; 
    Epi_SummaryDialogObj epi_SummaryDialogObj;
    Data_Manager dm;
    Epi_Values_Dialog epi_Values_Dialog;
    RiskAnalysisPane riskAnalysisPane;
    UnivariateCategoricalDataObj catUCDO_Outcomes, catUCDOExposures;

    // POJOs / FX
    
    public  Epi_Model(Epi_Controller epi_Controller, String assocType) { 
        //System.out.println("\n53 Epi_Model, Constructing");
        //System.out.println("54 Epi_Model, assocType = " + assocType);
        this.epi_Controller = epi_Controller;
        this.assocType = assocType;
        dm = epi_Controller.getDataManager();
        dm.whereIsWaldo(58, waldoFile, "\nConstructing");
    }    
    
    public String doEpiFromFile() {    
        dm.whereIsWaldo(62, waldoFile, "doEpiFromFile()");
        al_ColumnsOfData = new ArrayList();
        al_ColumnsOfData = epi_Controller.getData(); 
        nLegalValues = al_ColumnsOfData.get(0).getColumnSize();
        dblNLegalValues = nLegalValues;
        strTopVariable = al_ColumnsOfData.get(0).getVarLabel();
        strLeftVariable =  al_ColumnsOfData.get(1).getVarLabel();

        catUCDOExposures = new UnivariateCategoricalDataObj(al_ColumnsOfData.get(1));
        strUniqueExposures = catUCDOExposures.getUniqueCategories();    

        catUCDO_Outcomes = new UnivariateCategoricalDataObj(al_ColumnsOfData.get(0));
        strUniqueOutcomes = catUCDO_Outcomes.getUniqueCategories();

        cleanReturn = "NO";
         do {
            epi_Values_Dialog = new Epi_Values_Dialog(this);
        } while (cleanReturn.equals("NO"));
        
        epi_Values_Dialog.getFileResponseListAsStrings();

        nUniqueExposures = catUCDOExposures.getNUniques();  // nRows
        nUniqueOutcomes = catUCDO_Outcomes.getNUniques();   // nCols

        valExpNo =  epi_Values_Dialog.getValueExposuresNo();
        valExpYes =  epi_Values_Dialog.getValueExposuresYes();
        valOutNo =  epi_Values_Dialog.getValueOutcomesNo();
        valOutYes =  epi_Values_Dialog.getValueOutcomesYes();

        constructArrays();

        int daRealLegals = 0; // Sum of replicates in the 2 x 2 table
        for (int ithPoint = 0; ithPoint < nLegalValues; ithPoint++) {
            tempTopDataPt = catUCDO_Outcomes.getIthValue(ithPoint);
            tempLeftDataPt = catUCDOExposures.getIthValue(ithPoint);
            if (tempTopDataPt.equals(valOutYes) && (tempLeftDataPt.equals(valExpYes))) {
                    replicates[0][0]++;
                    daRealLegals++;
            } else 
                if (tempTopDataPt.equals(valOutNo) && (tempLeftDataPt.equals(valExpYes))) {
                        replicates[0][1]++;
                        daRealLegals++;
            } else 
                if (tempTopDataPt.equals(valOutYes) && (tempLeftDataPt.equals(valExpNo))) {
                        replicates[1][0]++; 
                        daRealLegals++;
            } else 
                if (tempTopDataPt.equals(valOutNo) && (tempLeftDataPt.equals(valExpNo))) {
                        replicates[1][1]++; 
                        daRealLegals++;
                }
        }

        // Fill the values of the Risk Table
        observedValues = new int[2][2];
        observedValues[0][0] = replicates[0][0];
        observedValues[0][1] = replicates[0][1];
        observedValues[1][0] = replicates[1][0];
        observedValues[1][1] = replicates[1][1];   

        /**********************************************************************
         *               Make the Eight for str_ArrayList_4_RiskView          *
         *********************************************************************/
        str_ArrayList_4_RiskView = new ArrayList();
        
        str_ArrayList_4_RiskView.add(epi_Values_Dialog.getExposureLabel()); // Exposure
        str_ArrayList_4_RiskView.add(epi_Values_Dialog.getValueExposuresYes()); // ExpYes
        str_ArrayList_4_RiskView.add(epi_Values_Dialog.getValueExposuresNo()); // ExpNo
        str_ArrayList_4_RiskView.add(epi_Values_Dialog.getOutcomeLabel()); // Outcome     
        str_ArrayList_4_RiskView.add(epi_Values_Dialog.getValueOutcomesYes()); // OutYes
        str_ArrayList_4_RiskView.add(epi_Values_Dialog.getValueOutcomesNo()); // OutNo
        
        // 6 - 9
        str_ArrayList_4_RiskView.add(String.valueOf(observedValues[0][0]));
        str_ArrayList_4_RiskView.add(String.valueOf(observedValues[0][1]));
        str_ArrayList_4_RiskView.add(String.valueOf(observedValues[1][0]));
        str_ArrayList_4_RiskView.add(String.valueOf(observedValues[1][1]));

        /****************************************************************
         *   Redefine the nLegalValues as the sum of replicates in only *
         *   the chosen variables for the 2 x 2 table.                  *
         ***************************************************************/
        nLegalValues = daRealLegals;
        dblNLegalValues = nLegalValues;
        calculateTheProportions();
        riskAnalysisPane = new RiskAnalysisPane(str_ArrayList_4_RiskView);
        return "OK";
    } 

    public String doEpiFromTable() {
        //System.out.println("152 Epi_Model, doEpiFromTable()");
        epi_SummaryDialog = new Epi_SummaryDialog(this);
        epi_SummaryDialog.doShowAndWait();
        returnStatus = epi_SummaryDialog.getReturnStatus();
        
        if (returnStatus.equals("OK")) {
            nUniqueExposures = epi_SummaryDialogObj.getNRows();
            nUniqueOutcomes = epi_SummaryDialogObj.getNCols();
            nLegalValues = 0;
            
            constructArrays();
            
            observedValues = epi_SummaryDialogObj.getObservedValues();
            riskAnalysisPane = new RiskAnalysisPane(str_ArrayList_4_RiskView);
            strTopVariable = epi_SummaryDialogObj.getTopLabel();
            strLeftVariable = epi_SummaryDialogObj.getLeftLabel();

            strOutcomeValues = epi_SummaryDialogObj.getYValues();
            strExposureValues = epi_SummaryDialogObj.getXValues();

            valExpNo =  strExposureValues[1];
            valExpYes =  strExposureValues[0];
            valOutNo =  strOutcomeValues[1];
            valOutYes =  strOutcomeValues[0];

            for (int ithRow = 0; ithRow < nUniqueExposures; ithRow++) {                
                for (int jthCol = 0; jthCol < nUniqueOutcomes; jthCol++) {                    
                   replicates[ithRow][jthCol] = epi_SummaryDialogObj.getObsVal_IJ(ithRow, jthCol);
                   nLegalValues += replicates[ithRow][jthCol];
                }
            }
     
            dblNLegalValues = nLegalValues;
            calculateTheProportions();
            returnStatus = "OK";
        } else {
            returnStatus = "Cancel";
        }  
        return returnStatus;
    }
        
    private void constructArrays() {
        replicates = new int[nUniqueExposures][nUniqueOutcomes];
        observedValues = new int[nUniqueExposures][nUniqueOutcomes];
        expectedValues = new double[nUniqueExposures][nUniqueOutcomes];
        observedProportion = new double[nUniqueExposures][nUniqueOutcomes];
        rowTotals = new int[nUniqueExposures];
        rowProportions = new double[nUniqueExposures];
        cumulativeRowProps = new double[nUniqueExposures + 1];
        cumMarginalRowProps = new double[nUniqueExposures + 1];
        columnTotals = new int[nUniqueOutcomes];
        columnProportions = new double[nUniqueOutcomes];
        cumulativeColumnProps = new double[nUniqueOutcomes + 1]; 
        cumulativeProportions = new double[nUniqueExposures + 1][nUniqueOutcomes + 1];
        strExposureValues = new String[nUniqueExposures]; 
        strOutcomeValues = new String[nUniqueOutcomes];
    }
    
    public void closeTheSummaryDialog(boolean cleanReturn) {
        cleanReturnFromSummaryDialog = cleanReturn;        
        if (cleanReturnFromSummaryDialog) {            
            if (!assocType.equals("Epidemiology")) {
                epi_SummaryDialog.close();
            } else {
                epi_SummaryDialog.close();
            }
        }
    }
    
    public void calculateTheProportions() { 
        
        for (int ithRow = 0; ithRow < nUniqueExposures; ithRow++) {            
            for (int jthCol = 0; jthCol < nUniqueOutcomes; jthCol++) {
                observedProportion[ithRow][jthCol] = replicates[ithRow][jthCol] / dblNLegalValues;
            }
        }

        // calculate ithRow marginals, print at side
        for (int ithRow = 0; ithRow < nUniqueExposures; ithRow++) {            
            rowTotals[ithRow] = 0;            
            for (int jthCol = 0; jthCol < nUniqueOutcomes; jthCol++) {
                rowTotals[ithRow] += replicates[ithRow][jthCol];
            }
        }
        
        // calculate column marginals, print at bottom        
        for (int ithCol = 0; ithCol < nUniqueOutcomes; ithCol++) {
            columnTotals[ithCol] = 0;            
            for (int jthRow = 0; jthRow < nUniqueExposures; jthRow++) {
                columnTotals[ithCol] += replicates[jthRow][ithCol];
            }
        }   
            
        //  calculate ithRow proportions        
        for (int ithRow = 0; ithRow < nUniqueExposures; ithRow++) {
            rowProportions[ithRow] = rowTotals[ithRow] / dblNLegalValues; 
        }
        
        //  calculate jthCol proportions        
        for (int ithCol = 0; ithCol < nUniqueOutcomes; ithCol++) {
            columnProportions[ithCol] = columnTotals[ithCol] / dblNLegalValues;
        }
        
        cumulativeColumnProps[0] = 0;        
        for (int ithCol = 1; ithCol <= nUniqueOutcomes; ithCol++) {
            cumulativeColumnProps[ithCol] = cumulativeColumnProps[ithCol - 1] + columnProportions[ithCol - 1];
        } 
       
        cumulativeRowProps[nUniqueExposures] = 0;       
        for (int ithRow = 1; ithRow <= nUniqueExposures; ithRow++) {
            cumulativeRowProps[nUniqueExposures - ithRow] = cumulativeRowProps[nUniqueExposures - ithRow + 1] + rowProportions[nUniqueExposures - ithRow];
        }

        cumMarginalRowProps[nUniqueExposures] = 0.0;        
        for (int ithRow = nUniqueExposures - 1; ithRow >= 0; ithRow--) {
            cumMarginalRowProps[ithRow] = cumMarginalRowProps[ithRow + 1] + rowProportions[ithRow];
        }    

        cumMarginalRowProps[0] = 1.0;      
        for (int ithCol = 0; ithCol < nUniqueOutcomes; ithCol++) {            
            cumulativeProportions[nUniqueExposures][nUniqueOutcomes] = 0.0;            
            for (int jthRow = nUniqueExposures - 1; jthRow >= 0; jthRow--) {
                cumulativeProportions[jthRow][ithCol] = cumulativeProportions[jthRow + 1][ithCol] + observedProportion[jthRow][ithCol];
            }   //  end ithRow         
        } 
    }
    
    // Called in Epi_SummaryDialog
    public void setEpiArrayList(ArrayList<String> fromEpiSummaryDialog) {
        str_ArrayList_4_RiskView = new ArrayList();
        str_ArrayList_4_RiskView = fromEpiSummaryDialog;
        epi_SummaryDialogObj = new Epi_SummaryDialogObj(str_ArrayList_4_RiskView); 
        epi_SummaryDialogObj.toString();
    }
    
    public int getTotalN() {return nLegalValues; }
    
    public int getNumberOfRows() { return nUniqueExposures; }
    public int getNumberOfColumns() { return nUniqueOutcomes; }
    
    public int getNumberOfCells() { return nCells; }
    public int getNumberOfCellsBelow5() { return nCellsBelow5; }
    
    public String getTopVariable() {return strTopVariable; }
    public String getLeftVariable() {return strLeftVariable; } 

    public String[] getOutcomeValues() {
        strOutcomeValues[0] = valOutYes;
        strOutcomeValues[1] = valOutNo;
        return strOutcomeValues; 
    }
    public String[] getExposureValues() {
        strExposureValues[0] = valExpYes;
        strExposureValues[1] = valExpNo;   
        return strExposureValues; }  
    
    public double[] getRowProportions() {return rowProportions; }
    public double[] getColumnProportions() {return columnProportions; } 
    public double[] getCumRowProps() { return cumulativeRowProps; }
    public double[] getCumColProps() { return cumulativeColumnProps; } 
    public int[] getRowTotals() { return rowTotals; }
    public int[] getColumnTotals() {return columnTotals; }
    public double[] getCumMarginalRowProps() { return cumMarginalRowProps; }
    
    public double[][] getCellCumProps() { return cumulativeProportions; }
    public int[][] getReplicates() { return replicates; }
    public int[][] getObservedValues() {return observedValues; }
    public double[][] getExpectedValues() {return expectedValues; }
    public double[][] getProportions() {return observedProportion; }
    
    public RiskAnalysisPane getRiskAnalysisPane() { return riskAnalysisPane; }

    public String[] getExposureUniques()  { return strUniqueExposures; }
    public String[] getOutcomeUniques()  { return strUniqueOutcomes; }
    
    public String getCleanReturnFromSummaryDialog() { return cleanReturn; }    
    
    public void setCleanReturnFromSummaryDialog(String toThis) {
        cleanReturn = toThis;
    }
    
    public boolean getDesignIsBalanced() { return designIsBalanced; }
    public boolean getThereAreReplications() {return thereAreReplications; }
    public boolean getDataAreMissing() { return dataAreMissing; }    
    public String getAssociationType() { return assocType; }
}

