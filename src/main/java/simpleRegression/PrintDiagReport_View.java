/****************************************************************************
 *                   PrintDiagReport_View                                   * 
 *                         11/01/23                                         *
 *                          21:00                                           *
 ***************************************************************************/
package simpleRegression;

import splat.Data_Manager;
import superClasses.PrintTextReport_View;

public class PrintDiagReport_View extends PrintTextReport_View {
    // POJOs
    
    // Make empty if no-print
    //String waldoFile = "PrintDiagReport_View";
    String waldoFile = "";
    
    // My classes
    Data_Manager dm;

    public PrintDiagReport_View(Inf_Regr_Model inf_Regression_Model,  Regr_Dashboard regression_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        dm = inf_Regression_Model.getDataManager();
        dm.whereIsWaldo(26, waldoFile, "Constructing");        
        sourceString = new String();
        stringsToPrint = inf_Regression_Model.getDiagnostics();
        strTitleText = "Regression Diagnostics";
    }
}


