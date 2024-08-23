/****************************************************************************
 *               Ind_t_Inf_SumStats_Report_View                             * 
 *                         06/15/24                                         *
 *                           12:00                                          *
 ***************************************************************************/
package the_t_procedures;

import superClasses.PrintTextReport_View;

public class Indep_t_Inf_SumStats_Report_View extends PrintTextReport_View {
    // POJOs
    
    String waldoFile = "Ind_t_Inf_SumStats_Report_View";
    //String waldoFile = "";
    
    // My classes

    public Indep_t_Inf_SumStats_Report_View(Indep_t_SumStats_Model ind_t_SumStats_Model,  Indep_t_SumStats_Dashboard ind_t_SumStats_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        System.out.println("22 Indep_t_Inf_SumStats_Report_View, constructing");        
        sourceString = new String();
        stringsToPrint = ind_t_SumStats_Model.getIndep_T_SumStats_Report();
        strTitleText = "Inference for independent means report";
    }    
}
