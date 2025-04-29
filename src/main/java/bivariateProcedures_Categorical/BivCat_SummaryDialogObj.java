/****************************************************************************
 *                   BivCat_SummaryDialogObj                                * 
 *                           08/19/24                                       *
 *                            00:00                                         *
 ***************************************************************************/
package bivariateProcedures_Categorical;
/****************************************************************************
 *            Note:  X- and Y-variable names are messed up!!!               * 
 ***************************************************************************/

import chiSquare.X2_Grid;
import java.util.ArrayList;
import smarttextfield.*;

public class BivCat_SummaryDialogObj {
    // POJOs
    int nRows, nCols;
    int[][] observedValues;
    
    String str_XVariable, str_YVariable;
    String[] strXValues, strYValues;
    
    public BivCat_SummaryDialogObj(SmartTextField xVar,
                                    SmartTextField yVar,
                                    SmartTextField[] strXValues,
                                    SmartTextField[] strYValues,
                                    X2_Grid x2GriddyWiddy) {
    
        //System.out.println("\n29 BivCat_SummaryDialogObj, Constructing");
        nRows = 2; nCols = 2;
        
        str_XVariable = xVar.getText();
        str_YVariable = yVar.getText();

        this.strXValues =  new String[nCols];        
        this.strYValues = new String[nRows];

        observedValues = new int[nRows][nCols];
        
        for (int ithRow = 0; ithRow < nRows; ithRow++) {
            this.strYValues[ithRow] = strYValues[ithRow].getText();      
        }
        
        for (int ithCol = 0; ithCol < nCols; ithCol++) {
            this.strXValues[ithCol] = strXValues[ithCol].getText();      
        }
        
        for (int ithRow = 0; ithRow < nRows; ithRow++) {            
            for (int jthCol = 0; jthCol < nCols; jthCol++) {
                observedValues[ithRow][jthCol] =
                        x2GriddyWiddy.getGriddyWiddy_IJ(ithRow, jthCol);
            }
        }
    }
    
    public BivCat_SummaryDialogObj(BivCat_Model fromBivCat)  {
        //System.out.println("\n57 BivCat_SummaryDialogObj, Constructing from BivCat_Model");        
        //nRows = 2; nCols = 2;
        strXValues = new String[nRows];
        strYValues =  new String[nCols];
        observedValues = new int[nRows][nCols];  
        
        
    }
      
    public BivCat_SummaryDialogObj(ArrayList<String> fromEpiDialog)  {
        //System.out.println("\n67 BivCat_SummaryDialogObj, Constructing from Epi Dialog");        
        nRows = 2; nCols = 2;
        strXValues = new String[nRows];
        strYValues =  new String[nCols];
        observedValues = new int[2][2];
        
        str_XVariable = fromEpiDialog.get(0);
        str_YVariable = fromEpiDialog.get(3);
        
        strXValues[0] = fromEpiDialog.get(1);
        strXValues[1] = fromEpiDialog.get(2);
        
        strYValues[0] = fromEpiDialog.get(4);
        strYValues[1] = fromEpiDialog.get(5);
        
        observedValues[0][0] = Integer.parseInt(fromEpiDialog.get(6));
        observedValues[0][1] = Integer.parseInt(fromEpiDialog.get(7));
        observedValues[1][0] = Integer.parseInt(fromEpiDialog.get(8));
        observedValues[1][1] = Integer.parseInt(fromEpiDialog.get(9));
    }
    public int[][] getObservedValues() { return observedValues; }
    
    public int getNRows() { return nRows; }
    public int getNCols() { return nCols; }
    
    public String getTopLabel() { return str_XVariable; }
    public void setTopLabel(String toThis) { str_XVariable = toThis; }
    public String getLeftLabel() { return str_YVariable; }
    public void setLeftLabel(String toThis) { str_YVariable = toThis; }
    
    public String[] getYValues() { return strYValues; }
    public void setYValues(String[] toTheseYValues) {
        strYValues[0] = toTheseYValues[0];
        strYValues[1] = toTheseYValues[1];
    }
    public String[] getXValues() { return strXValues; }
    public void setXValues(String[] toTheseXValues) {
        strXValues[0] = toTheseXValues[0];
        strXValues[1] = toTheseXValues[1];
    }
    
    public int getObsVal_IJ(int ithRow, int jthCol) {
        return observedValues[ithRow][jthCol];
    }
    
    public void setObsVal_IJ(int[][] toThese) {
        observedValues[0][0] = toThese[0][0];
        observedValues[0][1] = toThese[0][1];
        observedValues[1][0] = toThese[1][0];
        observedValues[1][1] = toThese[1][1];
    }
    
    public BivCat_SummaryDialogObj getTheDialogObject() { return this; }
    
    @Override
    public String toString() {        
        String daReturnString = "finit BivCat_SummaryDialogObj";
        System.out.println("\nBivCat_SummaryDialogObj -- toString");
        System.out.println(" nRows / Cols = " + nRows + " / " + nCols);
        System.out.println(" Top Label = " + str_YVariable);
        System.out.println("Left Label = " + str_XVariable);
        System.out.println("XValues = " + strXValues[0] + " / " + strXValues[1]);
        System.out.println("YValues = " + strYValues[0] + " / " + strYValues[1]);
        System.out.println("   *****  Observed Values  *****");
        System.out.println(observedValues[0][0] + " / " + observedValues[0][1]);
        System.out.println(observedValues[1][0] + " / " + observedValues[1][1]);
        System.out.println("END UnivariateCategoricalDatObj\n");
        return daReturnString;
    }   
}

