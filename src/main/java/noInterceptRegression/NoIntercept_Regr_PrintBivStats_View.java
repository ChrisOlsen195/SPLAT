/****************************************************************************
 *               NoInt_Regr_PrintBivStats_View                              * 
 *                         01/15/25                                         *
 *                          21:00                                           *
 ***************************************************************************/
package noInterceptRegression;

//import utilityClasses.StringUtilities;
import superClasses.PrintTextReport_View;

public class NoIntercept_Regr_PrintBivStats_View extends PrintTextReport_View {
    // POJOs
    
    // My classes
    
    public NoIntercept_Regr_PrintBivStats_View(NoIntercept_Regr_Model noInt_Regr_Model,  NoIntercept_Regr_Dashboard noInt_Regr_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        
        sourceString = new String();
        stringsToPrint = noInt_Regr_Model.getStatsReport();
        strTitleText = "Statistical Summary: Bivariate";
    }
}


