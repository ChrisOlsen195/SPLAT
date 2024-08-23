/****************************************************************************
 *                 OneProp_Inf_Report_View                                  * 
 *                         11/21/23                                         *
 *                           15:00                                          *
 ***************************************************************************/
package the_z_procedures;

import superClasses.PrintTextReport_View;

public class OneProp_Inf_Report_View extends PrintTextReport_View {
    // POJOs                                                                                                                                                                                  

    // My classes

    public OneProp_Inf_Report_View(OneProp_Inf_Model oneProp_Inf_Model,  OneProp_Inf_Dashboard oneProp_Inf_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        //System.out.println("19 OneProp_Inf_Report_View, constructing");
        sourceString = new String();
        stringsToPrint = oneProp_Inf_Model.getStringsToPrint();
        strTitleText = "Inference for a single proportion report";
    }
}
