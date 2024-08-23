/****************************************************************************
 *               NoInt_Regr_PrintBivStats_View                              * 
 *                         10/15/23                                         *
 *                          15:00                                           *
 ***************************************************************************/
package noInterceptRegression;

//import utilityClasses.StringUtilities;
import superClasses.PrintTextReport_View;

public class NoIntercept_Regr_PrintBivStats_View extends PrintTextReport_View {
    // POJOs
    //int nCategories, df, maxSpaces, spacesNeeded, nCellsBelow5;                                                                                                                                                                                  
    //int[] observedValues; 
    
    // My classes
    //NoInt_Regr_Dashboard noInt_Regr_Dashboard;
    //NoInt_Regr_Model noInt_Regr_Model;
    //StringUtilities myStringUtilities;
    
    public NoIntercept_Regr_PrintBivStats_View(NoIntercept_Regr_Model noInt_Regr_Model,  NoIntercept_Regr_Dashboard noInt_Regr_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        
        //this.noInt_Regr_Model = noInt_Regr_Model;
        //myStringUtilities = new StringUtilities();
        
        sourceString = new String();
        stringsToPrint = noInt_Regr_Model.getStatsReport();
        strTitleText = "Statistical Summary: Bivariate";
    }
}


