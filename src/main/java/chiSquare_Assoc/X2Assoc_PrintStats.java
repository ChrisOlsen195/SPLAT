/****************************************************************************
 *                      X2Assoc_PrintStats                                  *
 *                           05/23/24                                       *
 *                             12:00                                        *
 ***************************************************************************/
package chiSquare_Assoc;

import superClasses.PrintTextReport_View;
import java.util.ArrayList;
import utilityClasses.StringUtilities;

public class X2Assoc_PrintStats extends PrintTextReport_View {
    // POJOs
    double chiSquare, pValue; 
    double[] cumRowProps, cumColProps, cumMarginalRowProps;   
    double[][] obsVals, expVals, x2Contribution,
               standResids, cumProps;
    
    int nRows, nCols, df, spacesAvailableForTitle,
        spacesAvailableInTotal, nCellsBelow5;

    String strTopVar, strLeftVar, strTitle, categoryAxisLabel;
    String[] strTopLabels, strLeftLabels;
    
    // My classes

    X2Assoc_Model x2Assoc_Model; 
    
    public  X2Assoc_PrintStats(X2Assoc_Model x2Assoc_Model, 
            X2Assoc_Dashboard x2Assoc_Dashboard,  
            double placeHoriz, double placeVert,
            double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        //System.out.println("34 X2Assoc_PrintStats, constructing");
        this.x2Assoc_Model = x2Assoc_Model;
        stringsToPrint = new ArrayList<>();          
        strTopVar = x2Assoc_Model.getTopVariable();
        strLeftVar = x2Assoc_Model.getLeftVariable();
        strTitle = strLeftVar + " and " + strTopVar;    
        nRows = x2Assoc_Model.getNumberOfRows();  // Rows of observed
        nCols = x2Assoc_Model.getNumberOfColumns();  // Cols of observed
        obsVals = new double[nRows][nCols];
        standResids = new double[nRows][nCols];
        expVals = new double[nRows][nCols];
        x2Contribution = new double[nRows][nCols];
        cumRowProps = new double[nRows + 1];    //  0 at the top
        cumMarginalRowProps = new double[nRows + 1];
        cumColProps = new double[nCols + 1];    //  0 at the left
        cumProps = new double[nRows + 1][nCols];    //  Internal cum props
        strLeftLabels = new String[nRows]; 
        strTopLabels = new String[nCols];
        strTopLabels = x2Assoc_Model.getTopLabels();
        strLeftLabels = x2Assoc_Model.getLeftLabels();
        obsVals = x2Assoc_Model.getObservedValues();
        expVals = x2Assoc_Model.getExpectedValues();
        chiSquare = x2Assoc_Model.getChiSquare();
        pValue = x2Assoc_Model.getPValue();
        x2Contribution = x2Assoc_Model.getX2Contributions();
        standResids = x2Assoc_Model.getStandardizedResiduals();
        cumColProps = x2Assoc_Model.getCumColProps();
        cumRowProps = x2Assoc_Model.getCumRowProps();
        cumMarginalRowProps = x2Assoc_Model.getCumMarginalRowProps();
        cumProps = x2Assoc_Model.getCellCumProps();
        strTitleText = "Elementary chi square statistics";
    }
    
    public void constructPrintLines() {
        int leftPadSpaces;
        String tempString, leftFill;
        stringsToPrint.add("\n\n");
        strTitle = "Association between: " + strTitle; //  Center this!
        spacesAvailableInTotal = 50 + 12 * nCols;  //  12 spaces for each col
        leftPadSpaces = 23;
        leftFill = StringUtilities.getStringOfNSpaces(leftPadSpaces);
        spacesAvailableForTitle = spacesAvailableInTotal - leftPadSpaces;
        String centeredTitle = StringUtilities.centerTextInString(strTitle, spacesAvailableForTitle) + "\n";
        stringsToPrint.add(leftFill + centeredTitle + "\n");
        leftPadSpaces = 23;
        leftFill = StringUtilities.getStringOfNSpaces(leftPadSpaces);
        
        tempString = "\n" + leftFill;        
        for (int col = 0; col < nCols; col++) {  
            String smallTop = StringUtilities.getleftMostNChars(strTopLabels[col], 8);
            tempString += StringUtilities.centerTextInString(smallTop, 12);   
        }
        
        stringsToPrint.add(tempString);
        for (int iRow = 0; iRow < nRows; iRow ++)  {
            tempString = "\n\n"; 
            stringsToPrint.add(tempString);
            tempString = StringUtilities.centerTextInString(strLeftLabels[iRow], 15) + "\n";
            stringsToPrint.add(tempString);
            
            //  Observed values
            tempString = "";
            tempString += String.format(StringUtilities.getleftMostNChars("\nObserved values", 20));
            
            for (int col = 0; col < nCols; col++) {
                tempString += String.format(" %11.2f", obsVals[iRow][col]);
            }
            stringsToPrint.add(tempString);       
            
            //  Expected values
            tempString = "";
            tempString += String.format(StringUtilities.getleftMostNChars("\nExpected values", 20));
            
            for (int col = 0; col < nCols; col++) {
                tempString += String.format(" %11.2f", expVals[iRow][col]);
            }
            stringsToPrint.add(tempString);
            
            //  Expected values
            tempString = "";
            tempString += String.format(StringUtilities.getleftMostNChars("\nContrib to X2", 20));  
            
            for (int col = 0; col < nCols; col++) {
                tempString += String.format(" %11.2f", x2Contribution[iRow][col]);
            }
            
            stringsToPrint.add(tempString);              
            tempString = "";
            tempString += String.format(StringUtilities.getleftMostNChars("\nStand. Resid (z)", 20));
            
            for (int col = 0; col < nCols; col++) {
                tempString += String.format(" %11.2f", standResids[iRow][col]);
            }
            stringsToPrint.add(tempString);  
            addNBlankLines(2);
        }   //  Next iRow
        
        df = (nRows - 1)*(nCols - 1);
        addNBlankLines(1);
        stringsToPrint.add(String.format("               Chi Square = %7.3f", chiSquare));
        addNBlankLines(1);
        stringsToPrint.add(String.format("                       df =  %2d", df));  
        addNBlankLines(1);
        stringsToPrint.add(String.format("                  p-Value = %7.3f", pValue));
        nCellsBelow5 = x2Assoc_Model.getNumberOfCellsBelow5();

        if (nCellsBelow5 > 0) {
            addNBlankLines(2);
            stringsToPrint.add(String.format("********************       Warning!    ********************"));  
            addNBlankLines(2);
            
            if (nCellsBelow5 == 1) {
                stringsToPrint.add(String.format("*** There is one cell with an expected value less than 5 ***"));
            } 
            
            if (nCellsBelow5 > 1) {
                stringsToPrint.add(String.format("*** There are %d cells with expected values less than 5 ***", nCellsBelow5));
            }               
        }
        addNBlankLines(4);
    }
    
    public int getDegreesOfFreedom() {return df; }
    public double getChiSquare()  {return chiSquare; }   
    public int getNumberOfRows() { return nRows; }
    public int getNumberOfColumns() { return nCols; }    
    public String getTopVariable() {return strTopVar; }
    public String getLeftVariable() {return strLeftVar; } 
    public String[] getTopLabels() {return strTopLabels; }
    public String[] getLeftLabels() {return strLeftLabels; }      
    public double[] getCumRowProps() { return cumRowProps; }
    public double[] getCumColProps() { return cumColProps; }   
    public double[] getCumMarginalRowProps() { return cumMarginalRowProps; }  
    public double[][] getCumProps() { return cumProps; }     
    public String getLabelForCategoryAxis() { return categoryAxisLabel; }
    public String getLabelForVerticalAxis() { return categoryAxisLabel; }
}
