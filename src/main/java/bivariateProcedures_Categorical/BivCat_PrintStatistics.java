/****************************************************************************
 *                    BivCat_PrintStatistics                                *
 *                           08/19/24                                       *
 *                             00:00                                        *
 ***************************************************************************/
package bivariateProcedures_Categorical;

import superClasses.PrintTextReport_View;
import utilityClasses.StringUtilities;

public class BivCat_PrintStatistics extends PrintTextReport_View {
    
    // POJOs  
    double[] rowProportion, colProportion, cumRowProps, cumColProps, 
             cumMarginalRowProps;    
    double[][]  proportions, cumProps;
    
    int nRows, nCols, spacesAvailableForTitle, spacesAvailableInTotal, 
        ithRow, jthCol, totalN;;
    
    int[] colTotal, rowTotal;
    
    int [][] observedValues;

    String strTopVariable, strLeftVariable, strTitle, strTemp, 
           categoryAxisLabel, verticalAxisLabel; 
    String[] topLabels, leftLabels;

    // My classes   

    public  BivCat_PrintStatistics(BivCat_Model bivCat_Model, 
            BivCat_Dashboard bivCat_Dashboard,  
            double placeHoriz, double placeVert,
            double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        //System.out.println("\n37 BivCat_PrintStatistics, Constructing");   
        categoryAxisLabel = " "; 
        verticalAxisLabel = " ";
        strTopVariable = bivCat_Model.getTopVariable();
        strLeftVariable = bivCat_Model.getLeftVariable();
        strTitle = strLeftVariable + " vs. " + strTopVariable;    
        nRows = bivCat_Model.getNumberOfRows();
        nCols = bivCat_Model.getNumberOfColumns();
        
        proportions = new double[nRows][nCols];
        rowTotal = new int[nRows];
        rowProportion = new double[nRows];
        cumRowProps = new double[nRows + 1];    //  0 at the top
        cumMarginalRowProps = new double[nRows + 1];
        colTotal = new int[nCols];
        colProportion = new double[nCols];
        cumColProps = new double[nCols + 1];    //  0 at the left
        cumProps = new double[nRows + 1][nCols];    //  Internal cum props
        leftLabels = new String[nRows]; 
        topLabels = new String[nCols];
        topLabels = bivCat_Model.getStrTopLabels();
        leftLabels = bivCat_Model.getStrLeftLabels();
        observedValues = bivCat_Model.getObservedValues();
        totalN = bivCat_Model.getTotalN();
        proportions = bivCat_Model.getProportions();
        rowTotal = bivCat_Model.getRowTotals();
        colTotal = bivCat_Model.getColumnTotals();
        rowProportion = bivCat_Model.getRowProportions();
        colProportion = bivCat_Model.getColumnProportions();
        cumColProps = bivCat_Model.getCumulativeColProps();
        cumRowProps = bivCat_Model.getCumulativeRowProps();
        cumMarginalRowProps = bivCat_Model.getCumMarginalRowProps();
        cumProps = bivCat_Model.getCellCumProps();  
        strTitleText = "Contingency Table";
    } 
    
    public void constructPrintLines() {
        //System.out.println("73 BivCat_PrintAdvStats, constructPrintLines()");
        int leftPadSpaces;
        String leftFill;
        addNBlankLines(2);
        strTitle = "Association between: " + strTitle; //  Center this!
        spacesAvailableInTotal = 50 + 12 * nCols;  //  12 spaces for each col
        
        leftPadSpaces = 23;
        leftFill = StringUtilities.getStringOfNSpaces(leftPadSpaces);
        spacesAvailableForTitle = spacesAvailableInTotal - leftPadSpaces;
        String centeredTitle = StringUtilities.centerTextInString(strTitle, spacesAvailableForTitle);
        stringsToPrint.add(leftFill + centeredTitle);
        addNBlankLines(2);
        leftPadSpaces = 23;
        leftFill = StringUtilities.getStringOfNSpaces(leftPadSpaces);
        
        strTemp = "\n" + leftFill;
        
        for (int col = 0; col < nCols; col++) {  
            String smallTop = StringUtilities.getleftMostNChars(topLabels[col], 8);
            strTemp += StringUtilities.centerTextInString(smallTop, 12);   
        }
        
        stringsToPrint.add(strTemp);
        addNBlankLines(1);

        for (ithRow = 0; ithRow < nRows; ithRow ++)  {
            addNBlankLines(2);
            strTemp = ""; 
            strTemp += StringUtilities.centerTextInString(leftLabels[ithRow], 15);
            stringsToPrint.add(strTemp);
            addNBlankLines(2);
            
            //  Observed values (doubles)
            strTemp = "";
            strTemp += String.format(StringUtilities.getleftMostNChars("Observed values", 20));
            
            for (jthCol = 0; jthCol < nCols; jthCol++) {
                strTemp += String.format(" %11d", observedValues[ithRow][jthCol]);
            }

            //  Marginal total for row
            strTemp += String.format(" %11d", rowTotal[ithRow]);
            stringsToPrint.add(strTemp);
            //  Percent of total
            addNBlankLines(1);
            strTemp = "";
            strTemp += String.format(StringUtilities.getleftMostNChars("Percent of Total", 20));
            
            for (jthCol = 0; jthCol < nCols; jthCol++) {
                strTemp += String.format(" %11.2f", 100. * proportions[ithRow][jthCol]);
            }
 
            strTemp += String.format(" %11.2f", 100. * rowProportion[ithRow]);
            stringsToPrint.add(strTemp);
            
            addNBlankLines(1);
            strTemp = "";
            strTemp += String.format(StringUtilities.getleftMostNChars("Percent of Row", 20));
            
            for (int col = 0; col < nCols; col++) {
                strTemp += String.format(" %11.2f", 100. * proportions[ithRow][col] / rowProportion[ithRow]);
            }
            
            stringsToPrint.add(strTemp);  
            
            //  Percent of col
            addNBlankLines(1);
            strTemp = "";
            strTemp += String.format(StringUtilities.getleftMostNChars("Percent of Column", 20));
            
            for (int iCol = 0; iCol < nCols; iCol++) {
                strTemp += String.format(" %11.2f", 100. * proportions[ithRow][iCol] / colProportion[iCol]);
            }
            
            stringsToPrint.add(strTemp); 
        }   //  Next iRow
        
        //  Column marginal totals
        addNBlankLines(2);
        strTemp = "";
        strTemp += String.format(StringUtilities.getleftMostNChars("Total", 20));
        
        for (int col = 0; col < nCols; col++) {
            strTemp += String.format(" %11d", colTotal[col]);
        }
        
        //  Marginal totals for column
        strTemp += String.format(" %11d", totalN);
        stringsToPrint.add(strTemp);

        //  Column marginal proportions
        addNBlankLines(1);
        strTemp = "";
        strTemp += String.format(StringUtilities.getleftMostNChars("Percent", 20));
        
        for (jthCol = 0; jthCol < nCols; jthCol++) {
            strTemp += String.format(" %11.2f", 100. * colProportion[jthCol]);
        }
        
        stringsToPrint.add(String.format(strTemp));
        addNBlankLines(2);
        addNBlankLines(1);
    } 
    
    public int getNumberOfRows() { return nRows; }
    public int getNumberOfColumns() { return nCols; }
    
    public String getTopVariable() {return strTopVariable; }
    public String getLeftVariable() {return strLeftVariable; } 

    public String[] getTopLabels() {return topLabels; }
    public String[] getLeftLabels() {return leftLabels; }  
    
    public double[] getCumRowProps() { return cumRowProps; }
    public double[] getCumColProps() { return cumColProps; }
    
    public double[] getCumMarginalRowProps() { return cumMarginalRowProps; }
    
    public double[][] getCumProps() { return cumProps; }
    
    public void setLabelForCategoryAxis(String toThisLabel) {
        categoryAxisLabel = toThisLabel;
    }
    
    public String getLabelForCategoryAxis() { return categoryAxisLabel; }
    
    public void setLabelForVerticalAxis(String toThisLabel) {
        verticalAxisLabel = toThisLabel;
    }
    
    public String getLabelForVerticalAxis() { return categoryAxisLabel; }   
}

