/****************************************************************************
 *                   PrintRegrReport_View                                   * 
 *                         11/01/23                                         *
 *                          00:00                                           *
 ***************************************************************************/
package simpleRegression;

import splat.Data_Manager;
import superClasses.PrintTextReport_View;

public class PrintRegrReport_View extends PrintTextReport_View {
    // POJOs
    
    // Make empty if no-print
    //String waldoFile = "PrintRegrReport_View";
    String waldoFile = "";
    
    // My classes
    Data_Manager dm;
   
    public PrintRegrReport_View(Inf_Regression_Model inf_Regression_Model,  Regression_Dashboard regression_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        dm = inf_Regression_Model.getDataManager();
        dm.whereIsWaldo(26, waldoFile, "Constructing");        
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        sourceString = new String();
        stringsToPrint = inf_Regression_Model.getRegressionReport();
        strTitleText = "Regression Analysis";
    }
}