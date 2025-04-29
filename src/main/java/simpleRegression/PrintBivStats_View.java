/****************************************************************************
 *                    PrintBivStats_View                                     * 
 *                         11/01/23                                         *
 *                          12:00                                           *
 ***************************************************************************/
package simpleRegression;

import splat.Data_Manager;
import superClasses.PrintTextReport_View;

public class PrintBivStats_View extends PrintTextReport_View {
    // POJOs
    
    // Make empty if no-print
    //String waldoFile = "PrintBivStats_View";
    String waldoFile = "";
    
    // My classes
    Data_Manager dm;
   
    public PrintBivStats_View(Inf_Regr_Model inf_Regression_Model,  Regr_Dashboard regression_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        dm = inf_Regression_Model.getDataManager();
        dm.whereIsWaldo(26, waldoFile, "Constructing");
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        
        sourceString = new String();
        stringsToPrint = inf_Regression_Model.getStatsReport();
        strTitleText = "Statistical Summary: Bivariate";
    }
}

