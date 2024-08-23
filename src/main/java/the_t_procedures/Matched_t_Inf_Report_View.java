/****************************************************************************
 *                  Matched_t_Inf_Report_View                               * 
 *                         11/01/23                                         *
 *                           18:00                                          *
 ***************************************************************************/
package the_t_procedures;

import superClasses.PrintTextReport_View;

public class Matched_t_Inf_Report_View extends PrintTextReport_View {
    // POJOs; 
    
    // My classes

    public Matched_t_Inf_Report_View(Matched_t_Model matched_t_Model,  Matched_t_Dashboard matched_t_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        System.out.println("19 Matched_t_Inf_Report_View, constructing");
        sourceString = new String();
        stringsToPrint = matched_t_Model.getSingleTReport();
        strTitleText = "Inference for matched pairs report";
    }
}
