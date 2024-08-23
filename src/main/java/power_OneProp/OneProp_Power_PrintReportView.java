/****************************************************************************
 *              OneProp_Power_PrintReport_View                              * 
 *                         11/01/23                                         *
 *                          15:00                                           *
 ***************************************************************************/
package power_OneProp;

import superClasses.PrintTextReport_View;

public class OneProp_Power_PrintReportView extends PrintTextReport_View {
    // POJOs
    
    // My classes
   
    public OneProp_Power_PrintReportView(OneProp_Power_Model oneProp_Power_Model,  OneProp_Power_Dashboard oneProp_Power_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        //System.out.println("19 OneProp_Power_PrintReport_View, constructing");
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        
        sourceString = new String();
        stringsToPrint = oneProp_Power_Model.getPowerReport();
        strTitleText = "Power Analysis: One Prop";
    }    
}



