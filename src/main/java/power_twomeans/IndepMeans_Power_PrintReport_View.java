/****************************************************************************
 *               TwoMeans_Power_PrintReport_View                            * 
 *                         11/01/23                                         *
 *                          18:00                                           *
 ***************************************************************************/
package power_twomeans;

import superClasses.PrintTextReport_View;

public class IndepMeans_Power_PrintReport_View extends PrintTextReport_View {
    // POJOs
    
    // My classes
   
    public IndepMeans_Power_PrintReport_View(IndepMeans_Power_Model indepMeans_Power_Model,  IndepMeans_Power_Dashboard indepMeans_Power_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        //System.out.println("19 IndepMeans_Power_PrintReport_View, constructing");
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;

        sourceString = new String();
        stringsToPrint = indepMeans_Power_Model.getPowerReport();
        strTitleText = "Power Analysis: Two Indep means";
    }   
}



