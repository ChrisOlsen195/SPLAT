/****************************************************************************
 *                 PrintQuadRegReport_View                                  * 
 *                         11/01/23                                         *
 *                          12:00                                           *
 ***************************************************************************/
package quadraticRegression;

import superClasses.PrintTextReport_View;

public class QuadReg_PrintReport_View extends PrintTextReport_View {
    // POJOs
    
    // My classes
   
    public QuadReg_PrintReport_View(QuadReg_Model regrModel,  QuadReg_Dashboard regrDashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        //System.out.println("19 PrintQuadRegReport_View, constructing");
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        sourceString = new String();
        stringsToPrint = regrModel.getRegressionReport();
        strTitleText = "Regression Analysis";
    }
}


