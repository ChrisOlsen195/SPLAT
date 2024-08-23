/**************************************************
 *             ANOVA1_Quant_PrintReportView       *
 *                    11/01/23                    *
 *                      12:00                     *
 *************************************************/
package anova1.quantitative;

import superClasses.PrintTextReport_View;

public class ANOVA1_Quant_PrintReportView extends PrintTextReport_View{
    // POJOs
    
    // My classes

    ANOVA1_Quant_PrintReportView(ANOVA1_Quant_Model qanova1Model,  ANOVA1_Quant_Dashboard qanova1Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {        
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        stringsToPrint = qanova1Model.getANOVA1Report();
        strTitleText = "One-way Analysis of Variance";
    }
}
