/****************************************************************************
 *                   X2GOF_PrintAdvStats                                    * 
 *                        01/15/25                                          *
 *                          12:00                                           *
 ***************************************************************************/
package chiSquare.GOF;

import superClasses.PrintTextReport_View;
import java.util.ArrayList;
import javafx.scene.layout.AnchorPane;
import utilityClasses.StringUtilities;

public class X2GOF_PrintAdvStats extends PrintTextReport_View {
    // POJOs
    
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    int nCategories, df, nCellsBelow5;
    int[] observedValues;
    
    double chiSquare, pValue, cohens_W;    
    double[] expValues, chiSquareContribution, standResids, 
             expProportions;
    
    String strGOFVariable;
    String[] strCategories;
    
    // My classes 
    X2GOF_Model x2GOF_Model;

    // FX Objects
    AnchorPane anchorPane;

    public X2GOF_PrintAdvStats(X2GOF_Model x2GOF_Model, X2GOF_Dashboard gofDashboard, 
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        if (printTheStuff == true) {
            System.out.println("40 *** X2GOF_PrintAdvStats, Constructing");
        }
        this.x2GOF_Model = x2GOF_Model;        
        
        sourceString = new String();
        stringsToPrint = new ArrayList<>();

        strGOFVariable = x2GOF_Model.getGOFVariable();
        nCategories = x2GOF_Model.getNCategories();
        
        chiSquareContribution = new double[nCategories];
        expProportions = new double[nCategories];
        expValues = new double[nCategories];
        standResids = new double[nCategories];        
        observedValues = new int[nCategories];
        
        observedValues = x2GOF_Model.getObservedValues();
        expProportions = x2GOF_Model.getExpectedProportions();  
        chiSquareContribution = x2GOF_Model.getX2Contributions();         
        expValues = x2GOF_Model.getExpectedValues(); 
        standResids = x2GOF_Model.getStandResids();
        nCellsBelow5 = x2GOF_Model.getNCellsBelow5();
        pValue = x2GOF_Model.getPValue();
        cohens_W = x2GOF_Model.getCohensW();
        
        strCategories = new String[nCategories]; 
        strCategories = x2GOF_Model.getCategoriesAsStrings();
        chiSquare = x2GOF_Model.getX2();

        df = x2GOF_Model.getDF();
        strTitleText = "Advanced chi square statistics";
    }
    
    public void constructPrintLines() {  
        if (printTheStuff == true) {
            System.out.println("75 --- X2GOF_PrintAdvStats, constructPrintLines()");
        }
        String tempString;
        addNBlankLines(2);
        tempString = "                                Variable of interest: " + strGOFVariable;
        stringsToPrint.add(tempString); 
        addNBlankLines(2);
        tempString = "                  Observed     Hypothesized     Expected      Contribution       Standardized   ";
        stringsToPrint.add(tempString); 
        addNBlankLines(1);
        tempString = "   Category         count       proportion       count       to Chi Square     Contribution (z)   ";
        stringsToPrint.add(tempString);     
        
        for (int printRow = 0; printRow < nCategories; printRow++) {
            sourceString = StringUtilities.getleftMostNChars(strCategories[printRow], 12);
            addNBlankLines(1);
            tempString = "";
            tempString += String.format("%12s", sourceString);
            tempString += String.format("    %8d", observedValues[printRow]);
            tempString += String.format("       %8.3f", expProportions[printRow]);
            tempString += String.format("       %8.2f", expValues[printRow]);
            tempString += String.format("        %8.3f", chiSquareContribution[printRow]);
            tempString += String.format("            %8.3f", standResids[printRow]);                           
            stringsToPrint.add(tempString);
        }

        addNBlankLines(3);
        tempString = String.format("                 Chi Square = %7.3f", chiSquare);
        stringsToPrint.add(tempString);
        addNBlankLines(1);
        tempString = String.format("                         df = %3d", df);
        stringsToPrint.add(tempString);
        addNBlankLines(1);
        tempString = String.format("                    p-Value = %7.3f", pValue);
        stringsToPrint.add(tempString);
        addNBlankLines(1);
        stringsToPrint.add(String.format("                  Cohen's W = %7.3f", cohens_W));  
        addNBlankLines(2);
        nCellsBelow5 = x2GOF_Model.getNCellsBelow5();
        
        if (nCellsBelow5 > 0) {
            addNBlankLines(2);
            tempString = String.format("               ********************      Warning!    ********************   ");
            stringsToPrint.add(tempString);  
            addNBlankLines(2);
            tempString = String.format("               *** There are %d cells with expected values less than 5. ***", nCellsBelow5);
            stringsToPrint.add(tempString);
            addNBlankLines(2);
        }                 
    }
}

