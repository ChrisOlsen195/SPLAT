/****************************************************************************
 *                 MLR_StandardReport_View                                  * 
 *                         10/15/23                                         *
 *                          15:00                                           *
 ***************************************************************************/
package multipleLogisticRegression;

import splat.Data_Manager;
//import utilityClasses.StringUtilities;
import superClasses.PrintTextReport_View;

public class MLR_StandardReport_View extends PrintTextReport_View {
    // POJOs
    //int nCategories, df, maxSpaces, spacesNeeded, nCellsBelow5;                                                                                                                                                                                  
    //int[] observedValues; 
    
    String waldoFile = "MLR_StandardReport_View";
    //String waldoFile = "";
    
    // My classes
    Data_Manager dm;
    //StringUtilities myStringUtilities;  
    //MLR_Dashboard mlr_Dashboard;
    //MLR_Model mlr_Model;
   
    public MLR_StandardReport_View(MLR_Model mlr_Model,  MLR_Dashboard mlr_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        //this.mlr_Model = mlr_Model;
        dm = mlr_Model.getDataManager();
        dm.whereIsWaldo(34, waldoFile, "Constructing");
        //myStringUtilities = new StringUtilities();        
        //sourceString = new String();
        stringsToPrint = mlr_Model.getLogisticReport();
        strTitleText = "Logisic Regression Analysis";
    }
}


