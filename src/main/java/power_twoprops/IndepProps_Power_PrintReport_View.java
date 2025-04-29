/****************************************************************************
 *             IndProps_Power_PrintReport_View                              * 
 *                         01/16/25                                         *
 *                          00:00                                           *
 ***************************************************************************/
package power_twoprops;

import superClasses.PrintTextReport_View;

public class IndepProps_Power_PrintReport_View extends PrintTextReport_View {
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    // My classes
   
    public IndepProps_Power_PrintReport_View(IndepProps_Power_Model indepMeans_Power_Model,  IndepProps_Power_Dashboard indepMeans_Power_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        if (printTheStuff == true) {
            System.out.println("22 *** IndepProps_Power_PrintReport_View, Constructing");
        }
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;

        sourceString = new String();
        stringsToPrint = indepMeans_Power_Model.getPowerReport();
        strTitleText = "Power Analysis: Two Indep props";
    }    
}



