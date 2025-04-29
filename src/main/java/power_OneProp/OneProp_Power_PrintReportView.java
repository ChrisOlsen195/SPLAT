/****************************************************************************
 *              OneProp_Power_PrintReport_View                              * 
 *                         01/15/25                                         *
 *                          21:00                                           *
 ***************************************************************************/
package power_OneProp;

import superClasses.PrintTextReport_View;

public class OneProp_Power_PrintReportView extends PrintTextReport_View {
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    // My classes
   
    public OneProp_Power_PrintReportView(OneProp_Power_Model oneProp_Power_Model,  OneProp_Power_Dashboard oneProp_Power_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        if (printTheStuff == true) {
            System.out.println("22 *** OneProp_Power_PrintReportView, Constructing");
        }
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        
        sourceString = new String();
        stringsToPrint = oneProp_Power_Model.getPowerReport();
        strTitleText = "Power Analysis: One Prop";
    }    
}



