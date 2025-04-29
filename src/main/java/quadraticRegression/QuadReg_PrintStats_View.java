/****************************************************************************
 *                  PrintQuadRegStats_View                                  * 
 *                         11/06/15                                         *
 *                          15:00                                           *
 ***************************************************************************/
package quadraticRegression;

import superClasses.PrintTextReport_View;

public class QuadReg_PrintStats_View extends PrintTextReport_View {
    // POJOs
    
    // My classes
   
    public QuadReg_PrintStats_View(QuadReg_Model quadRegModel,  QuadReg_Dashboard quadRegDashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        //System.out.println("19 PrintQuadRegStats_View, constructing");
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        sourceString = new String();
        stringsToPrint = quadRegModel.getStatsReport();
        strTitleText = "Summary: Quadratic Bivariate";
    }
}


