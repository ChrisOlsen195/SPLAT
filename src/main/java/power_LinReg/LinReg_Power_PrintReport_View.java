/****************************************************************************
 *                LinReg_Power_PrintReport_View                             * 
 *                         06/05/26                                         *
 *                          12:00                                           *
 ***************************************************************************/
package power_LinReg;

import superClasses.PrintTextReport_View;

public class LinReg_Power_PrintReport_View extends PrintTextReport_View {
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;    
    // My classes
   
    public LinReg_Power_PrintReport_View(LinReg_Power_Model linReg_Power_Model,  LinReg_Power_Dashboard linReg_Power_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        if (printTheStuff) {
            System.out.println("21 *** LinReg_Power_PrintReport_View, Constructing");
        }
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;

        sourceString = new String();
        stringsToPrint = linReg_Power_Model.getPowerReport();
        strTitleText = "Power Analysis: Regression";
    }
}


