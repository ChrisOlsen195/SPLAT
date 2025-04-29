/****************************************************************************
 *               Logistic_StandardReport_View                               * 
 *                        11/01/23                                          *
 *                          15:00                                           *
 ***************************************************************************/
package simpleLogisticRegression;

import splat.Data_Manager;
import superClasses.PrintTextReport_View;

public class Logistic_StandardReport_View extends PrintTextReport_View {
    // POJOs
    
    // Make empty if no-print
    //String waldoFile = "Logistic_StandardReport_View";
    String waldoFile = ""; 
    
    // My classes
    Data_Manager dm;
   
    public Logistic_StandardReport_View(Logistic_Model logistic_Model,  Logistic_Dashboard logistic_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        dm = logistic_Model.getDataManager();
        dm.whereIsWaldo(53, waldoFile, "Constructing");        
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;

        sourceString = new String();
        stringsToPrint = logistic_Model.getLogisticReport();
        strTitleText = "Logisic Regression Analysis";
    }
}


