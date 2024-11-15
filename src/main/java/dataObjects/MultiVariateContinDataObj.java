/**************************************************
 *            MultiVariateContinDataObj           *
 *                    05/24/24                    *
 *                      15:00                     *
 *************************************************/
package dataObjects;

import utilityClasses.DataUtilities;
import java.util.ArrayList;
import matrixProcedures.*;
import splat.*;
import utilityClasses.MyAlerts;

public class MultiVariateContinDataObj {

    // POJOs
    boolean someDataInThisCaseAreMissing; //, dataCanBeAMatrix;
    boolean thisObjectContainsStrings;
    int nOriginalDataPoints, nCompleteCases, nCasesWithMissingData, nVariables;
    String[] strDataTypes;

    ArrayList<ArrayList<String>> al_Non_Missing_Rows;
    
    String[] dataLabels;
    String[][] xDataAsStrings;
    
    //String waldoFile = "MultiVariateContinDataObj";
    String waldoFile = "";
    
    //My classes
    ArrayList<ColumnOfData> col_al_DataColumns;
    
    public MultiVariateContinDataObj(Data_Manager dm, ArrayList<ColumnOfData> dataColumns) {
        //this.dm = dm;
        dm.whereIsWaldo(35, waldoFile, "Constructing from ArrayList of Columns");
        
        //myUtil = new MyUtilities();
        this.col_al_DataColumns = new ArrayList<>();
        this.col_al_DataColumns = dataColumns;
        nVariables = dataColumns.size();
        nOriginalDataPoints = dataColumns.get(0).getColumnSize();   
        strDataTypes = new String[nVariables];

        // xDataAsStrings contains all the data
        xDataAsStrings = new String[nOriginalDataPoints][nVariables];
        dataLabels = new String[nVariables];
        
        for (int ithColumn = 0; ithColumn < nVariables; ithColumn++) {
            ColumnOfData tempColumn = new ColumnOfData(dataColumns.get(ithColumn));
            dataLabels[ithColumn] = tempColumn.getVarLabel();
            // Initialization
            strDataTypes[ithColumn] = "Numeric";
            thisObjectContainsStrings = true;
            
            for (int jthRow = 0; jthRow < nOriginalDataPoints; jthRow++) {
                String strFromColumn = tempColumn.getStringInIthRow(jthRow);
                xDataAsStrings[jthRow][ithColumn] = strFromColumn;
            }
        }
          
        // al_Non_Missing_Rows contains cases with non-Missing data
        al_Non_Missing_Rows = new ArrayList<>();
        
        for (int ithCase = 0; ithCase < nOriginalDataPoints; ithCase++) {
            // Check row of data for all doubles
            someDataInThisCaseAreMissing = false;
            
            for (int jthVariable = 0; jthVariable < nVariables; jthVariable++) {
                String dataToCheck = xDataAsStrings[ithCase][jthVariable];
                boolean thisDataPointIsOK = DataUtilities.strIsADouble(dataToCheck);
                
                if (!thisDataPointIsOK) {
                    if (dataToCheck.equals("*")) {
                        someDataInThisCaseAreMissing = true;
                    } else {
                        strDataTypes[jthVariable] = "Categorical";
                    }
                }
            }

            if (!someDataInThisCaseAreMissing) {
                ArrayList<String> str_al_TempRow = new ArrayList<>();
                
                for (int jthCol = 0; jthCol < nVariables; jthCol++) {
                    str_al_TempRow.add(xDataAsStrings[ithCase][jthCol]);
                }
                al_Non_Missing_Rows.add(str_al_TempRow);
            }
        }
        
        // The complete cases should have nothing missing, but could contain
        // non-numeric variables.
        nCompleteCases = al_Non_Missing_Rows.size();
        
        if (nCompleteCases == 0) { MyAlerts.showAintGotNoDataAlert_1Var(); }
    
        nCasesWithMissingData = nOriginalDataPoints - nCompleteCases; 
    }
    
    // public boolean getSomeDataMissing() { return someDataInThisCaseAreMissing; }
    public ArrayList<ColumnOfData> getTheDataColumns() { return col_al_DataColumns; }    
    public String[] getDataLabels() { return dataLabels; }
    // public String getJthLabel(int thisJ) { return dataLabels[thisJ]; }
    public int getNCompleteCases() { return nCompleteCases; }
    public int getNMissingCases() { return nCasesWithMissingData; }
    public int getNVariables() { return nVariables; }
    
    public String getIthRowJthColAsString(int thisI, int thisJ) {
        return al_Non_Missing_Rows.get(thisI).get(thisJ);
    }
    
    public Double getIthRowJthColAsDouble(int thisI, int thisJ) {
        Double daDouble = Double.valueOf(al_Non_Missing_Rows.get(thisI).get(thisJ));
        return daDouble;
    }
    
    public boolean canBeAMatrix() { return !thisObjectContainsStrings; }
    
    public Matrix returnDataAsMatrix() {
        Matrix dataMatrix = new Matrix(nCompleteCases, nVariables);
        
        for (int ithRow = 0; ithRow < nCompleteCases; ithRow++) {
            
            for (int jthCol = 0; jthCol < nVariables; jthCol++) {
                dataMatrix.set(ithRow, jthCol, Double.parseDouble(al_Non_Missing_Rows.get(ithRow).get(jthCol)));
            }
        }
        return dataMatrix;
    }
}
