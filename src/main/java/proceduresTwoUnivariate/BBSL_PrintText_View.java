/****************************************************************************
 *                    BBSL_PrintText_View                                   * 
 *                         11/01/23                                         *
 *                          15:00                                           *
 ***************************************************************************/
package proceduresTwoUnivariate;

import superClasses.PrintTextReport_View;
import the_t_procedures.Indep_t_Dashboard;

public class BBSL_PrintText_View extends PrintTextReport_View {
   
    public BBSL_PrintText_View(BBSL_View bbsl_View,
                             Indep_t_Dashboard independent_t_Dashboard,
                             double placeHoriz, double placeVert,
                             double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        //System.out.println("18 BBSL_PrintText_View, constructing");
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        stringsToPrint = bbsl_View.getTheDesiredBBSL();
    }    
}

