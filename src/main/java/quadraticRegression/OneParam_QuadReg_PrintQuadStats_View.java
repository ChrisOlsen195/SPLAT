/***************************************************************
 *             OneParam_QuadReg_PrintQuadStats_View            * 
 *                        11/01/23                             *
 *                         18:00                               *
 **************************************************************/
package quadraticRegression;

import splat.Data_Manager;
import superClasses.PrintTextReport_View;

public class OneParam_QuadReg_PrintQuadStats_View extends PrintTextReport_View {
    // POJOs
    
    String waldoFile = "OneParam_QuadReg_PrintQuadStats_View";
    // String waldoFile = "";
    
    // My classes
    Data_Manager dm; 
   
    public OneParam_QuadReg_PrintQuadStats_View(OneParam_QuadReg_Model oneParam_QuadReg_Model,  OneParam_QuadReg_Dashboard oneParam_QuadReg_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        dm = oneParam_QuadReg_Model.getDataManager();
        dm.whereIsWaldo(31, waldoFile, "Constructing");        
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;

        sourceString = new String();
        stringsToPrint = oneParam_QuadReg_Model.getStatsReport();
        strTitleText = "Statistical Summary: Bivariate";
    }
}



