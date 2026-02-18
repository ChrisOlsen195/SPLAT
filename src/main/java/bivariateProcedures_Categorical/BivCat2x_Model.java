/****************************************************************************
 *                        BivCat2x_Model                                    *
 *                           12/10/25                                       *
 *                            18:00                                         *
 ****************************************************************************
 *   BivCat2x_Model is a stripped down version of BivCat_Model.  Here, the  *
 *   information normally acquired from bivCat_SummaryDialog will have to   *
 *   be supplied by the calling program.  nRows and nCols are set at 2.     *
 ***************************************************************************/
package bivariateProcedures_Categorical;

import the_z_procedures.TwoProp_Inf_Model;

public class BivCat2x_Model {
    // POJOs
    
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    boolean designIsBalanced, thereAreReplications, dataAreMissing;
    
    int nLegalValues;
    final int NROWS, NCOLS;
    int[][] observedValues;
    int[] rowTotals, columnTotals; 
    
    double dblNLegalValues, maxProportion;    
    double[] rowProportions, columnProportions, cumulativeRowProps, 
             cumulativeColumnProps,  cumMarginalRowProps;  
    
    double[][] observedProportions, cumulativeProportions;

    String strTopVariable, strLeftVariable, assocType, strReturnStatus,
            title2, varOfInterest;
    String[] strTopLabels, strLeftLabels;
    
    // Make empty if no-print
    //String waldoFile = "BivCat2x_Model";
    String waldoFile = "";

//  My classes   
    TwoProp_Inf_Model twoProp_Inf_Model;

    // POJOs / FX
    
    public  BivCat2x_Model(TwoProp_Inf_Model twoProp_Inf_Model, String assocType) { 
        if (printTheStuff) {
            System.out.println("48 *** BivCat2x_Model, Constructing");
        }
        this.twoProp_Inf_Model = twoProp_Inf_Model;
        this.assocType = assocType;
        title2 = twoProp_Inf_Model.getTheVariable();
        NROWS = 2; NCOLS = 2;
        strTopVariable = twoProp_Inf_Model.getFirstProp_Label();
        strLeftVariable = twoProp_Inf_Model.getSecondProp_Label();
        varOfInterest = twoProp_Inf_Model.getTheVariable();
        strReturnStatus = "OK";
    }    
     
    public String doBivCat2xModelFrom2PropInf() {
        if (printTheStuff) {
            System.out.println("*** 62 BivCat2x_Model, doBivCat2xModelFrom2PropInf()");
        } 
        if (strReturnStatus.equals("OK")) { 
            constructNecessaryArrays();
            
            observedValues[0][0] = twoProp_Inf_Model.getVar1Succeses();
            observedValues[0][1] = twoProp_Inf_Model.getVar1Failures();
            observedValues[1][0] = twoProp_Inf_Model.getVar2Succeses();
            observedValues[1][1] = twoProp_Inf_Model.getVar2Failures();

            nLegalValues = 0;                                                                           
            for (int iRow = 0; iRow < NROWS; iRow++) {
                for (int jCol = 0; jCol < NCOLS; jCol++) {
                   nLegalValues += observedValues[iRow][jCol];
                }
            }

            dblNLegalValues = nLegalValues;
            calculateTheProportions();
        } 
        return strReturnStatus;
    }   
        
    private void constructNecessaryArrays() {
        if (printTheStuff) {
            System.out.println("*** 87 BivCat_Model, constructNecessaryArrays()");
        }
        observedValues = new int[NROWS][NCOLS];
        observedProportions = new double[NROWS][NCOLS];
        rowTotals = new int[NROWS];
        rowProportions = new double[NROWS];
        cumulativeRowProps = new double[NROWS + 1];    //  0 at the top
        cumMarginalRowProps = new double[NROWS + 1];
        columnTotals = new int[NCOLS];
        columnProportions = new double[NCOLS];
        cumulativeColumnProps = new double[NCOLS + 1];    //  0 at the left
        cumulativeProportions = new double[NROWS + 1][NCOLS + 1];    //  Internal cum props
    }
 
    public String calculateTheProportions() { 
        if (printTheStuff) {
            System.out.println("*** 103 BivCat_Model, calculateTheProportions()");
        }
              
        maxProportion = 0.0;
        for (int ithRow = 0; ithRow < NROWS; ithRow++) {            
            for (int jthCol = 0; jthCol < NCOLS; jthCol++) {
                observedProportions[ithRow][jthCol] = observedValues[ithRow][jthCol] / dblNLegalValues;
                maxProportion = Math.max(maxProportion, observedProportions[ithRow][jthCol]);
            }
        }

        for (int ithRow = 0; ithRow < NROWS; ithRow++) {            
            rowTotals[ithRow] = 0;            
            for (int jthCol = 0; jthCol < NCOLS; jthCol++) {
                rowTotals[ithRow] += observedValues[ithRow][jthCol];
            }
        }
              
        for (int ithCol = 0; ithCol < NCOLS; ithCol++) {
            columnTotals[ithCol] = 0;            
            for (int jthRow = 0; jthRow < NROWS; jthRow++) {
                columnTotals[ithCol] += observedValues[jthRow][ithCol];
            }
        }   
                   
        for (int ithRow = 0; ithRow < NROWS; ithRow++) {
            rowProportions[ithRow] = rowTotals[ithRow] / dblNLegalValues; 
        }
              
        for (int ithCol = 0; ithCol < NCOLS; ithCol++) {
            columnProportions[ithCol] = columnTotals[ithCol] / dblNLegalValues;
        }
        
        cumulativeColumnProps[0] = 0;        
        for (int ithCol = 1; ithCol <= NCOLS; ithCol++) {
            cumulativeColumnProps[ithCol] = cumulativeColumnProps[ithCol - 1] + columnProportions[ithCol - 1];
        } 
       
        cumulativeRowProps[NROWS] = 0;       
        for (int ithRow = 1; ithRow <= NROWS; ithRow++) {
            cumulativeRowProps[NROWS - ithRow] = cumulativeRowProps[NROWS - ithRow + 1] + rowProportions[NROWS - ithRow];
        }

        cumMarginalRowProps[NROWS] = 0.0;        
        for (int ithRow = NROWS - 1; ithRow >= 0; ithRow--) {
            cumMarginalRowProps[ithRow] = cumMarginalRowProps[ithRow + 1] + rowProportions[ithRow];
        }    

        cumMarginalRowProps[0] = 1.0;      
        for (int ithCol = 0; ithCol < NCOLS; ithCol++) {            
            cumulativeProportions[NROWS][NCOLS] = 0.0;            
            for (int jthRow = NROWS - 1; jthRow >= 0; jthRow--) {
                cumulativeProportions[jthRow][ithCol] = cumulativeProportions[jthRow + 1][ithCol] + observedProportions[jthRow][ithCol];
            }   //  end ithRow         
        } 
        return "OK";
    }
    
    public int getTotalN() {return nLegalValues; }
    public String getTheTitle() { return title2; }
    public String getTheVariable() { return varOfInterest; }
    public String getTopVariable() {return strTopVariable; }    //  A
    public String getLeftVariable() {return strLeftVariable; }  // B

    public String[] getStrTopLabels() { return strTopLabels; } // A
    public String[] getStrLeftLabels() {  return strLeftLabels; } // B 
    
    public double getMaxProportion() { return maxProportion; }
    
    public double[][] getObservedProportions() { return observedProportions; }
    public double[] getRowProportions() {return rowProportions; }
    public double[] getColumnProportions() {return columnProportions; } 
    public double[] getCumulativeRowProps() { return cumulativeRowProps; }
    public double[] getCumulativeColProps() { return cumulativeColumnProps; } 
    public int[] getRowTotals() { return rowTotals; }
    public int[] getColumnTotals() {return columnTotals; }
    public double[] getCumMarginalRowProps() { return cumMarginalRowProps; }
    
    public double[][] getCumulativeProportions() { return cumulativeProportions; }
    public int[][] getObservedValues() {return observedValues; }
    public double[][] getProportions() {return observedProportions; }
    
    public String getReturnStatus() { return strReturnStatus; }    
    public void setReturnStatus(String toThis) { 
        strReturnStatus = toThis; 
    }
    
    public boolean getDesignIsBalanced() { return designIsBalanced; }
    public boolean getThereAreReplications() {return thereAreReplications; }
    public boolean getDataAreMissing() { return dataAreMissing; }    
    public String getAssociationType() { return assocType; }
}
