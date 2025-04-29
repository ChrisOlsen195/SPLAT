/****************************************************************************
 *               OneMean_Power_PrintReport_View                             * 
 *                         01/15/25                                         *
 *                          21:00                                           *
 ***************************************************************************/
package power_OneMean;

import superClasses.PrintTextReport_View;

public class OneMean_Power_PrintReport_View extends PrintTextReport_View {
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;    
    // My classes
   
    public OneMean_Power_PrintReport_View(OneMean_Power_Model oneMean_Power_Model,  OneMean_Power_Dashboard oneMean_Power_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        if (printTheStuff == true) {
            System.out.println("21 *** OneMean_Power_PrintReport_View, Constructing");
        }
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;

        sourceString = new String();
        stringsToPrint = oneMean_Power_Model.getPowerReport();
        strTitleText = "Power Analysis: One Mean";
    }
}


