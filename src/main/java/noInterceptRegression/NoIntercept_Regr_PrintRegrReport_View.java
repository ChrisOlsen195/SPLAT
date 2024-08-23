/****************************************************************************
 *            NoIntercept_Regr_PrintRegrReport_View                         * 
 *                         11/01/23                                         *
 *                           15:00                                          *
 ***************************************************************************/
package noInterceptRegression;

import superClasses.PrintTextReport_View;

public class NoIntercept_Regr_PrintRegrReport_View extends PrintTextReport_View {
    // POJOs
    
    // My classes

   
    public NoIntercept_Regr_PrintRegrReport_View(NoIntercept_Regr_Model regrModel,  NoIntercept_Regr_Dashboard noInt_Regr_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        //System.out.println("20 NoInt_Regr_PrintRegrReport_View, constructing");
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        
        sourceString = new String();
        stringsToPrint = regrModel.getRegressionReport();
        strTitleText = "No-intercept Regression Analysis";
    }
}


