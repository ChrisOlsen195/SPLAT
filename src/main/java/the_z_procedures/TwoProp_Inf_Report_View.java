/****************************************************************************
 *                 TwoProp_Inf_Report_View                                  * 
 *                         11/01/23                                         *
 *                           21:00                                          *
 ***************************************************************************/
package the_z_procedures;

import superClasses.PrintTextReport_View;

public class TwoProp_Inf_Report_View extends PrintTextReport_View {
    // POJOs
    
    // My classes

    public TwoProp_Inf_Report_View(TwoProp_Inf_Model twoProp_Inf_Model,  TwoProp_Inf_Dashboard twoProp_Inf_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        
        sourceString = new String();
        stringsToPrint = twoProp_Inf_Model.getStringsToPrint();
        strTitleText = "Inference for a difference between proportions";
    }
}
