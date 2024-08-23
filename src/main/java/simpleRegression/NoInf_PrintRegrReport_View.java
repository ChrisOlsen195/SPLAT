/****************************************************************************
 *                NoInf_PrintRegrReport_View                                * 
 *                         11/01/23                                         *
 *                          15:00                                           *
 ***************************************************************************/
package simpleRegression;

import superClasses.PrintTextReport_View;

public class NoInf_PrintRegrReport_View extends PrintTextReport_View {
    // POJOs
    
    // My classes
   
    public NoInf_PrintRegrReport_View(NoInf_Regression_Model noInf_RegrModel,  NoInf_Regression_Dashboard noInf_RegrDashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        //System.out.println("19 NoInf_PrintRegrReport_View");
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        sourceString = new String();
        stringsToPrint = noInf_RegrModel.getRegressionReport();
        strTitleText = "Regression Analysis";
    }
}