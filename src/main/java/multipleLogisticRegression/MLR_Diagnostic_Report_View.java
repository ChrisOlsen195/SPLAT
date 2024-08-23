/****************************************************************************
 *                 MLR_Diagnostic_Report_View                               * 
 *                         10/15/23                                         *
 *                          21:00                                           *
 ***************************************************************************/
package multipleLogisticRegression;

//import genericClasses.DragableAnchorPane;
//import utilityClasses.StringUtilities;
import superClasses.PrintTextReport_View;
import genericClasses.ResizableTextPane;


public class MLR_Diagnostic_Report_View extends PrintTextReport_View {
    // POJOs
    //int nCategories, df, maxSpaces, spacesNeeded, nCellsBelow5;                                                                                                                                                                                  
    //int[] observedValues; 
    
    // My classes
    //DragableAnchorPane dragableAnchorPane;
    //StringUtilities myStringUtilities;  
    //MLR_Dashboard logisticDashboard;
    //MLR_Model logisticModel;
    //ResizableTextPane rtp;

    public MLR_Diagnostic_Report_View(MLR_Model logisticModel, MLR_Dashboard logisticDashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        System.out.println("30 MLR_Diagnostic_Report_View, constructing");
        //this.logisticModel = logisticModel;
        //myStringUtilities = new StringUtilities();
        
        //sourceString = new String();
        stringsToPrint = logisticModel.getDiagnostics();
        strTitleText = "Logistic Diagnostics";
    }
}



