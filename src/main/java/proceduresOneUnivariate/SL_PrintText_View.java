/****************************************************************************
 *                     SL_PrintText_View                                     * 
 *                         01/16/25                                         *
 *                          12:00                                           *
 ***************************************************************************/
package proceduresOneUnivariate;

import superClasses.PrintTextReport_View;

public class SL_PrintText_View extends PrintTextReport_View {
    // POJOs
   
    public SL_PrintText_View(StemNLeaf_View snl_View,
                             Exploration_Dashboard explorationDashboard,
                             double placeHoriz, double placeVert,
                             double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        //System.out.println("18 SL_PrintText_View, constructing");
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        stringsToPrint = snl_View.getTheDesiredSL();
    }    
}
