/****************************************************************************
 *                     Epi_PrintStatistics                                  *
 *                           08/19/24                                       *
 *                             00:00                                        *
 ***************************************************************************/
package epidemiologyProcedures;

import superClasses.PrintTextReport_View;
import utilityClasses.StringUtilities;

public class Epi_PrintStatistics extends PrintTextReport_View {
    
    // POJOs   
    double[] rowProportion, colProportion, cumRowProps, cumColProps, 
             cumMarginalRowProps;    
    double[][]  proportions, cumProps;
    
    int nRows, nCols, spacesAvailableForTitle, spacesAvailableInTotal, 
        ithRow, jthCol, totalN;
    
    int[] colTotal, rowTotal;
    
    int [][] replicates;

    String strTopVariable, strLeftVariable, strTitle, strTemp, 
           categoryAxisLabel, verticalAxisLabel, assocType; 
    String[] topLabels, leftLabels;

    // My classes   
    //Epi_Model epi_Model; 

    public  Epi_PrintStatistics(Epi_Model epi_Model, 
            Epi_Dashboard bivCat_Dashboard,  
            double placeHoriz, double placeVert,
            double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        //System.out.println("\n37 Epi_PrintStatistics, Constructing");   
        assocType = epi_Model.getAssociationType();
        categoryAxisLabel = " "; 
        verticalAxisLabel = " ";
        strTopVariable = epi_Model.getTopVariable();
        strLeftVariable = epi_Model.getLeftVariable();
        strTitle = strLeftVariable + " vs. " + strTopVariable;    
        nRows = 2;
        nCols = 2;
        
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
        topLabels = epi_Model.getOutcomeValues();
        leftLabels = epi_Model.getExposureValues();
        replicates = epi_Model.getReplicates();
        
        totalN = epi_Model.getTotalN();
        proportions = epi_Model.getProportions();
        rowTotal = epi_Model.getRowTotals();
        colTotal = epi_Model.getColumnTotals();
        rowProportion = epi_Model.getRowProportions();
        colProportion = epi_Model.getColumnProportions();
        cumColProps = epi_Model.getCumColProps();
        cumRowProps = epi_Model.getCumRowProps();
        cumMarginalRowProps = epi_Model.getCumMarginalRowProps();
        cumProps = epi_Model.getCellCumProps();  
        strTitleText = "Contingency Table";
    } 
    
    public void constructPrintLines() {
        //System.out.println("77 Epi_PrintAdvStats, constructPrintLines()");
        int leftPadSpaces;
        String leftFill;
        addNBlankLines(2);
        strTitle = "Association between: " + strTitle; //  Center this!
        spacesAvailableInTotal = 50 + 12 * nCols;  //  12 spaces for each col
        
        leftPadSpaces = 1;
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
                strTemp += String.format(" %11d", replicates[ithRow][jthCol]);
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
            
            for (int jthCol = 0; jthCol < nCols; jthCol++) {
                strTemp += String.format(" %11.2f", 100. * proportions[ithRow][jthCol] / rowProportion[ithRow]);
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
        addNBlankLines(3);
    } 
    
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


