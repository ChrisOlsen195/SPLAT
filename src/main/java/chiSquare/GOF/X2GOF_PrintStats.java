/****************************************************************************
 *                     X2GOF_PrintStats                                     * 
 *                         09/07/24                                         *
 *                          12:00                                           *
 ***************************************************************************/
package chiSquare.GOF;

import superClasses.PrintTextReport_View;
import utilityClasses.StringUtilities;

public class X2GOF_PrintStats extends PrintTextReport_View {
    // POJOs
    int nCategories, df, nCellsBelow5;                                                                                                                                                                                  
    int[] obsValues;
    
    double chiSquare, pValue;    
    double[] expValues, x2Contribution,  expProps;
    
    String strGOFVariable;
    String[] strCategories;
    
    // My classes
    X2GOF_Model x2GOF_Model;
 
   
    public X2GOF_PrintStats(X2GOF_Model x2GOF_Model, X2GOF_Dashboard gofDashboard, 
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        System.out.println("\n30 X2GOF_PrintStats, constructing");
        this.x2GOF_Model = x2GOF_Model;
        
        sourceString = new String();
        strGOFVariable = x2GOF_Model.getGOFVariable();
        nCategories = x2GOF_Model.getNCategories();
        x2Contribution = new double[nCategories];
        expProps = new double[nCategories];
        expValues = new double[nCategories];       
        obsValues = new int[nCategories];
        
        obsValues = x2GOF_Model.getObservedValues();
        expProps = x2GOF_Model.getExpectedProportions();  
        x2Contribution = x2GOF_Model.getX2Contributions();         
        expValues = x2GOF_Model.getExpectedValues(); 
        
        nCellsBelow5 = x2GOF_Model.getNCellsBelow5();
        pValue = x2GOF_Model.getPValue();
        
        strCategories = new String[nCategories]; 
        strCategories = x2GOF_Model.getCategoriesAsStrings();
        chiSquare = x2GOF_Model.getX2();

        df = x2GOF_Model.getDF();
        strTitleText = "Elementary chi square statistics";
    }   //  End constructor


    public void constructPrintLines() {
        //System.out.println("59 *** X2GOF_PrintStats, constructPrintLines()");
        String tempString;
        addNBlankLines(2); 
        tempString = "                                Variable of interest: " + strGOFVariable;
        stringsToPrint.add(tempString);        
        addNBlankLines(2);
        tempString = "                  Observed     Hypothesized     Expected      Contribution";
        stringsToPrint.add(tempString);    
        addNBlankLines(1);
        tempString = "Category            count       proportion       count       to Chi Square";
        stringsToPrint.add(tempString);     
        addNBlankLines(2);     
        
        for (int printRow = 0; printRow < nCategories; printRow++) {
            sourceString = StringUtilities.getleftMostNChars(strCategories[printRow], 12);
            tempString = "";
            tempString += String.format("%12s", sourceString);
            tempString += String.format("    %8d", obsValues[printRow]);
            tempString += String.format("        %8.3f", expProps[printRow]);
            tempString += String.format("       %8.2f", expValues[printRow]);
            tempString += String.format("        %8.3f", x2Contribution[printRow]);                               
            stringsToPrint.add(tempString);
            addNBlankLines(1);
        }
        
        addNBlankLines(2);
 
        tempString = String.format("                 Chi Square = %7.3f", chiSquare);
        stringsToPrint.add(tempString);
        addNBlankLines(1); 
        tempString = String.format("                         df = %3d", df);
        stringsToPrint.add(tempString);
        addNBlankLines(1);
        tempString = String.format("                    p-Value = %7.3f", pValue);
        stringsToPrint.add(tempString);
        addNBlankLines(2);
        nCellsBelow5 = x2GOF_Model.getNCellsBelow5();
        
        if (nCellsBelow5 > 0) {
            addNBlankLines(2);
            tempString = String.format("               ********************       Warning!    ********************");
            stringsToPrint.add(tempString); 
            addNBlankLines(2);
            tempString = String.format("               *** There are %d cells with expected values less than 5. ***\n\n", nCellsBelow5);
            stringsToPrint.add(tempString);
        }                
        addNBlankLines(2);  
    }
}

