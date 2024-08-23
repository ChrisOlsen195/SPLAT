/****************************************************************************
 *             Logistic_Diagnostic_Report_View                              * 
 *                         11/01/23                                         *
 *                          15:00                                           *
 ***************************************************************************/
package simpleLogisticRegression;

import superClasses.PrintTextReport_View;
import splat.Data_Manager;

public class Logistic_Diagnostic_Report_View extends PrintTextReport_View {
    // POJOs

    // Make empty if no-print
    //String waldoFile = "Logistic_Diagnostic_Report_View";
    String waldoFile = "";     
       
    // My classes
    Data_Manager dm;

    public Logistic_Diagnostic_Report_View(LogisticReg_Model logisticModel,  Logistic_Dashboard logisticDashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        dm = logisticModel.getDataManager();
        dm.whereIsWaldo(26, waldoFile, "Constructing");                
        sourceString = new String();
        stringsToPrint = logisticModel.getDiagnostics();
        strTitleText = "Logistic Diagnostics";
    }
}



