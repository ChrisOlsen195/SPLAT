/****************************************************************************
 *                 NoInf_PrintBivStats_View                                 * 
 *                         11/01/23                                         *
 *                          15:00                                           *
 ***************************************************************************/
package simpleRegression;

import superClasses.PrintTextReport_View;

public class NoInf_PrintBivStats_View extends PrintTextReport_View {
    // POJOs
    
    // My classes
   
    public NoInf_PrintBivStats_View(NoInf_Regr_Model noInf_RegressionModel,  NoInf_Regr_Dashboard noInf_RegrDashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        //System.out.println("19 NoInf_PrintBivStats_View");
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        sourceString = new String();
        stringsToPrint = noInf_RegressionModel.getStatsReport();
        strTitleText = "Statistical Summary: Bivariate";
    }
}

