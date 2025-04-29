/****************************************************************************
 *               TwoMeans_Power_PrintReport_View                            * 
 *                         01/15/25                                         *
 *                          21:00                                           *
 ***************************************************************************/
package power_twomeans;

import superClasses.PrintTextReport_View;

public class IndepMeans_Power_PrintReport_View extends PrintTextReport_View {
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    // My classes
   
    public IndepMeans_Power_PrintReport_View(IndepMeans_Power_Model indepMeans_Power_Model,  IndepMeans_Power_Dashboard indepMeans_Power_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        if (printTheStuff == true) {
            System.out.println("22 *** IndepMeans_Power_PrintReport_View, Constructing");
        }
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;

        sourceString = new String();
        stringsToPrint = indepMeans_Power_Model.getPowerReport();
        strTitleText = "Power Analysis: Two Indep means";
    }   
}



