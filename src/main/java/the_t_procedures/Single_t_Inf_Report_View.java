/****************************************************************************
 *                  Single_t_Inf_Report_View                                   * 
 *                         11/01/23                                         *
 *                           18:00                                          *
 ***************************************************************************/
package the_t_procedures;

import superClasses.PrintTextReport_View;

public class Single_t_Inf_Report_View extends PrintTextReport_View {
    // POJOs

    public Single_t_Inf_Report_View(Single_t_Model single_t_Model,  Single_t_Dashboard single_t_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        //System.out.println("17 Single_t_Inf_Report_View, constructing");
        sourceString = new String();
        stringsToPrint = single_t_Model.getStringsToPrint();
        strTitleText = "Inference for a single mean report";
    }
    
    public Single_t_Inf_Report_View(Single_t_SumStats_Model single_t_SumStats_Model,  Single_t_SumStats_Dashboard single_t_SumStats_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        System.out.println("27 Single_t_Inf_Report_View, constructing");
        sourceString = new String();
        stringsToPrint = single_t_SumStats_Model.getStringsToPrint();
        strTitleText = "Inference for a single mean report";
    }    
}
