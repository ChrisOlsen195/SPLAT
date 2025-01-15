/****************************************************************************
 *                 Indep_t_Inf_Report_View                                  * 
 *                         06/15/24                                         *
 *                           12:00                                          *
 ***************************************************************************/
package the_t_procedures;

import superClasses.PrintTextReport_View;
import splat.Data_Manager;

public class Indep_t_Inf_Report_View extends PrintTextReport_View {
    // POJOs

    //String waldoFile = "Indep_t_Inf_Report_View";
    String waldoFile = "";
        
    // My classes
    Data_Manager dm;

    public Indep_t_Inf_Report_View(Indep_t_Model ind_t_Model,  Indep_t_Dashboard ind_t_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);        
        dm = ind_t_Model.getDataManager();
        dm.whereIsWaldo(25, waldoFile, "Constructing");        
        sourceString = new String();
        stringsToPrint = ind_t_Model.getIndepTReport();
        strTitleText = "Inference for independent means report";
    }
}