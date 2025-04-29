/****************************************************************************
 *                 TwoProp_Inf_Report_View                                  * 
 *                         01/15/24                                         *
 *                           18:00                                          *
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
        System.out.println("19 *** TwoProp_Inf_Report_View, Constructing");
        sourceString = new String();
        stringsToPrint = twoProp_Inf_Model.getStringsToPrint();
        strTitleText = "Inference for a difference between proportions";
    }
}
