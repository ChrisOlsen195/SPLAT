/****************************************************************************
 *          OneParam_QuadReg_PrintQuadRegReport_View                        * 
 *                         11/01/23                                         *
 *                          18:00                                           *
 ***************************************************************************/

package quadraticRegression;

import splat.Data_Manager;
import superClasses.PrintTextReport_View;

public class OneParam_QuadReg_PrintQuadRegReport_View extends PrintTextReport_View {
    // POJOs
    
    String waldoFile = "OneParam_QuadReg_PrintQuadRegReport_View";
    //String waldoFile = "";
    
    // My classes
    Data_Manager dm;
   
    public OneParam_QuadReg_PrintQuadRegReport_View(OneParam_QuadReg_Model oneParam_QuadReg_Model,  OneParam_QuadReg_Dashboard oneParam_QuadReg_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        System.out.println("25 OneParam_QuadReg_PrintQuadRegReport, constructing");
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        dm = oneParam_QuadReg_Model.getDataManager();
        dm.whereIsWaldo(35, waldoFile, "Constructing");        
        
        sourceString = new String();
        stringsToPrint = oneParam_QuadReg_Model.getRegressionReport();
        strTitleText = "Regression Analysis";
    }
}



