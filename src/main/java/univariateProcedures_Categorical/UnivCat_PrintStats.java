/****************************************************************************
 *                     UnivCat_PrintStats                                   * 
 *                         11/01/23                                         *
 *                          15:00                                           *
 ***************************************************************************/
package univariateProcedures_Categorical;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import splat.Data_Manager;
import superClasses.PrintTextReport_View;
import utilityClasses.StringUtilities;

public class UnivCat_PrintStats extends PrintTextReport_View {
    // POJOs
    int nCategories;                                                                                                                                                                                  
    int[] frequencies;
    
    double observedTotal;
    
    double[] //expectedValues, chiSquareContribution, resids, standResids, 
             relativeFrequencies;
    
    //String waldoFile = "UnivCat_PrintStats";
    String waldoFile = ""; 
    
    String strGofVariable;
    ObservableList<String> categoriesAsStrings;
    
    // My classes
    Data_Manager dm;
 
  
    public UnivCat_PrintStats(UnivCat_Model univCat_Model, UnivCat_Dashboard gofDashboard, 
                              double placeHoriz, double placeVert,
                              double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        dm = univCat_Model.getDataManager();
        dm.whereIsWaldo(48, waldoFile, "Constructing");        
        categoriesAsStrings = FXCollections.observableArrayList();
        
        sourceString = new String();

        strGofVariable = univCat_Model.getTheVariable();
        nCategories = univCat_Model.getNCategories();
        relativeFrequencies = new double[nCategories];      
        frequencies = new int[nCategories];
        
        frequencies = univCat_Model.getObservedCounts();
        observedTotal = univCat_Model.getObservedTotal();
        
        for (int ithFreq = 0; ithFreq < nCategories; ithFreq++) {
            relativeFrequencies[ithFreq] = frequencies[ithFreq] / observedTotal;
        }
        
        categoriesAsStrings = univCat_Model.getCategoryLabels();
        strTitleText = "Categorical variable statistics";
    }   //  End constructor

    public void constructPrintLines() { 
        String tempString;
        addNBlankLines(2); 
        tempString = "                      Variable of interest: " + strGofVariable;
        stringsToPrint.add(tempString);        
        addNBlankLines(2);
        tempString = "     Value           Frequency     Relative frequency";
        stringsToPrint.add(tempString);         
        addNBlankLines(2);     
        
        for (int printRow = 0; printRow < nCategories; printRow++) {
            sourceString = StringUtilities.getleftMostNChars(categoriesAsStrings.get(printRow), 12);
            tempString = "";
            tempString += String.format("     %12s", sourceString);
            tempString += String.format("  %8d", frequencies[printRow]);
            tempString += String.format("            %8.3f", relativeFrequencies[printRow]);                              
            stringsToPrint.add(tempString);
            addNBlankLines(1);
        }        
        addNBlankLines(2);
    }
}


