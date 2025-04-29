/****************************************************************************
 *                     X2Assoc_PrintAdvStats                                *
 *                           04/19/25                                       *
 *                             12:00                                        *
 ***************************************************************************/
package chiSquare_Assoc;

import superClasses.PrintTextReport_View;
import utilityClasses.StringUtilities;

public class X2Assoc_PrintAdvStats extends PrintTextReport_View {
    
    // POJOs
    
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    double chiSquare, pValue, totalN;    
    double[] x2RowProps, x2ColProps, x2CumRowProps, x2CumColProps, x2RowTotals,
             x2ColTotals, cumMarginalRowProps;    
    double[][] obsVals, expVals, x2Contribution, standResids, proportions, 
               cumProps;
    
    int nRows, nCols, df, spacesAvailableForTitle,
        spacesAvailableInTotal, nCellsBelow5, iRow, jCol;

    String strTopVar, strLeftVar, titleString, tempString, categoryAxisLabel;
    String[] strTopLabels, strLeftLabels; 
    
    // My classes 
    X2Assoc_Model x2Assoc_Model; 

    public  X2Assoc_PrintAdvStats(X2Assoc_Model x2_Assoc_Model, 
            X2Assoc_Dashboard x2Assoc_Dashboard,  
            double placeHoriz, double placeVert,
            double withThisWidth, double withThisHeight) {
        
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        if (printTheStuff == true) {
            System.out.println("40 *** X2Assoc_PrintAdvStats, Constructing");
        }
        this.x2Assoc_Model = x2_Assoc_Model;    
        categoryAxisLabel = " "; 
        strTopVar = x2_Assoc_Model.getTopVariable();
        strLeftVar = x2_Assoc_Model.getLeftVariable();
        titleString = strLeftVar + " vs. " + strTopVar;    
        nRows = x2_Assoc_Model.getNumberOfRows();  // Rows of observed
        nCols = x2_Assoc_Model.getNumberOfColumns();  // Cols of observed
        obsVals = new double[nRows][nCols];
        standResids = new double[nRows][nCols];
        expVals = new double[nRows][nCols];
        proportions = new double[nRows][nCols];
        x2Contribution = new double[nRows][nCols];
        x2RowTotals = new double[nRows];
        x2RowProps = new double[nRows];
        x2CumRowProps = new double[nRows + 1];    //  0 at the top
        cumMarginalRowProps = new double[nRows + 1];
        x2ColTotals = new double[nCols];
        x2ColProps = new double[nCols];
        x2CumColProps = new double[nCols + 1];    //  0 at the left
        cumProps = new double[nRows + 1][nCols];    //  Internal cum props
        strLeftLabels = new String[nRows]; 
        strTopLabels = new String[nCols];
        strTopLabels = x2_Assoc_Model.getTopLabels();
        strLeftLabels = x2_Assoc_Model.getLeftLabels();
        obsVals = x2_Assoc_Model.getObservedValues();
        totalN = x2_Assoc_Model.getTotalN();
        proportions = x2_Assoc_Model.getProportions();
        x2RowTotals = x2_Assoc_Model.getRowTotals();
        x2ColTotals = x2_Assoc_Model.getColumnTotals();
        x2RowProps = x2_Assoc_Model.getRowProportions();
        x2ColProps = x2_Assoc_Model.getColumnProportions();
        expVals = x2_Assoc_Model.getExpectedValues();
        chiSquare = x2_Assoc_Model.getChiSquare();
        pValue = x2_Assoc_Model.getPValue();
        x2Contribution = x2_Assoc_Model.getX2Contributions();
        standResids = x2_Assoc_Model.getStandardizedResiduals();
        x2CumColProps = x2_Assoc_Model.getCumColProps();
        x2CumRowProps = x2_Assoc_Model.getCumRowProps();
        cumMarginalRowProps = x2_Assoc_Model.getCumMarginalRowProps();
        cumProps = x2_Assoc_Model.getCellCumProps();  
        strTitleText = "Advanced chi square statistics";
    } 
    
  
    public void constructPrintLines() {
        if (printTheStuff == true) {
            System.out.println("88 --- X2Assoc_PrintAdvStats, constructPrintLines()");
        }
        int leftPadSpaces;
        String leftFill;
        addNBlankLines(2);
        titleString = "Association between: " + titleString;
        spacesAvailableInTotal = 23 + 12 * nCols;  //  12 spaces for each col
        
        leftPadSpaces = 23;
        leftFill = StringUtilities.getStringOfNSpaces(leftPadSpaces);
        spacesAvailableForTitle = spacesAvailableInTotal - leftPadSpaces;
        String centeredTitle = StringUtilities.centerTextInString(titleString, spacesAvailableForTitle);
        stringsToPrint.add(leftFill + centeredTitle);
        addNBlankLines(2);
        leftPadSpaces = 23;
        leftFill = StringUtilities.getStringOfNSpaces(leftPadSpaces);
        
        tempString = "\n" + leftFill;        
        for (int col = 0; col < nCols; col++) {  
            String smallTop = StringUtilities.getleftMostNChars(strTopLabels[col], 12);
            tempString += StringUtilities.centerTextInString(smallTop, 12);   
        }
        
        stringsToPrint.add(tempString);
        addNBlankLines(1);

        for (iRow = 0; iRow < nRows; iRow ++)  {
            addNBlankLines(2);
            tempString = ""; 
            tempString += StringUtilities.centerTextInString(strLeftLabels[iRow], 15);
            stringsToPrint.add(tempString);
            addNBlankLines(2);
            
            //  Observed values
            tempString = "";
            tempString += String.format(StringUtilities.getleftMostNChars("Observed values", 20));
            
            for (jCol = 0; jCol < nCols; jCol++) {
                tempString += String.format(" %11.2f", obsVals[iRow][jCol]);
            }
            
            //  Marginal total for row
            tempString += String.format(" %11.2f", x2RowTotals[iRow]);
            stringsToPrint.add(tempString);
            //  Percent of total
            addNBlankLines(1);
            tempString = "";
            tempString += String.format(StringUtilities.getleftMostNChars("Percent of Total", 20));
            
            for (jCol = 0; jCol < nCols; jCol++) {
                tempString += String.format(" %11.2f", 100. * proportions[iRow][jCol]);
            }
 
            tempString += String.format(" %11.2f", 100. * x2RowProps[iRow]);
            stringsToPrint.add(tempString);
            
            addNBlankLines(1);
            tempString = "";
            tempString += String.format(StringUtilities.getleftMostNChars("Percent of Row", 20));
            
            for (int col = 0; col < nCols; col++) {
                tempString += String.format(" %11.2f", 100. * proportions[iRow][col] / x2RowProps[iRow]);
            }
            
            stringsToPrint.add(tempString);  
            
            //  Percent of col
            addNBlankLines(1);
            tempString = "";
            tempString += String.format(StringUtilities.getleftMostNChars("Percent of Column", 20));
            
            for (int iCol = 0; iCol < nCols; iCol++) {
                tempString += String.format(" %11.2f", 100. * proportions[iRow][iCol] / x2ColProps[iCol]);
            }
            
            stringsToPrint.add(tempString);
            //  Marginal proportion for row           
            
            //  Expected values
            addNBlankLines(1);
            tempString = "";
            tempString += String.format(StringUtilities.getleftMostNChars("Expected values", 20));
            
            for (int col = 0; col < nCols; col++) {
                tempString += String.format(" %11.2f", expVals[iRow][col]);
            }
            
            stringsToPrint.add(tempString);
            
            //  Contribution
            addNBlankLines(1);
            tempString = "";
            tempString += String.format(StringUtilities.getleftMostNChars("Contrib to X2", 20));  
            
            for (int col = 0; col < nCols; col++) {
                tempString += String.format(" %11.2f", x2Contribution[iRow][col]);
            }
            
            stringsToPrint.add(tempString); 
            addNBlankLines(1);
            tempString = "";
            tempString += String.format(StringUtilities.getleftMostNChars("Stand. Resid (z)", 20));
            
            for (int col = 0; col < nCols; col++) {
                tempString += String.format(" %11.2f", standResids[iRow][col]);
            }
            
            stringsToPrint.add(tempString);        
        }   //  Next iRow
        
        //  Column marginal totals
        addNBlankLines(2);
        tempString = "";
        tempString += String.format(StringUtilities.getleftMostNChars("Total", 20));
        
        for (int col = 0; col < nCols; col++) {
            tempString += String.format(" %11.2f", x2ColTotals[col]);
        }
        
        //  Marginal totals for column
        tempString += String.format(" %11.2f", totalN);
        stringsToPrint.add(tempString);

        //  Column marginal proportions
        addNBlankLines(1);
        tempString = "";
        tempString += String.format(StringUtilities.getleftMostNChars("Percent", 20));
        
        for (jCol = 0; jCol < nCols; jCol++) {
            tempString += String.format(" %11.2f", 100. * x2ColProps[jCol]);
        }
        
        stringsToPrint.add(String.format(tempString));
        addNBlankLines(2);
        
        df = (nRows - 1)*(nCols - 1);
        double cramersV = x2Assoc_Model.getCramersV();
        addNBlankLines(1);
        stringsToPrint.add(String.format("                Chi Square = %7.3f", chiSquare));
        addNBlankLines(1);
        stringsToPrint.add(String.format("                        df =  %2d", df));
        addNBlankLines(1);
        stringsToPrint.add(String.format("                   p-Value = %7.3f", pValue));
        addNBlankLines(1);
        stringsToPrint.add(String.format("                Cramer's V = %7.3f", cramersV));
        
        nCellsBelow5 = x2Assoc_Model.getNumberOfCellsBelow5();
        if (nCellsBelow5 > 0) {
            addNBlankLines(2);
            stringsToPrint.add(String.format("********************       Warning!    ********************"));       
            addNBlankLines(2);
            stringsToPrint.add(String.format("*** There are %d cells with expected values less than 5 ***", nCellsBelow5));
            addNBlankLines(2);
        }
        addNBlankLines(2);
    } 
    
    public int getDegreesOfFreedom() {return df; }
    public double getChiSquare()  {return chiSquare; }   
    public int getNumberOfRows() { return nRows; }
    public int getNumberOfColumns() { return nCols; }   
    public String getTopVariable() {return strTopVar; }
    public String getLeftVariable() {return strLeftVar; } 
    public String[] getTopLabels() {return strTopLabels; }
    public String[] getLeftLabels() {return strLeftLabels; }     
    public double[] getCumRowProps() { return x2CumRowProps; }
    public double[] getCumColProps() { return x2CumColProps; }    
    public double[] getCumMarginalRowProps() { return cumMarginalRowProps; }   
    public double[][] getCumProps() { return cumProps; } 
    
    public void setLabelForCategoryAxis(String toThisLabel) {
        categoryAxisLabel = toThisLabel;
    }   
    public String getLabelForCategoryAxis() { return categoryAxisLabel; }
    public String getLabelForVerticalAxis() { return categoryAxisLabel; }   
}
