/**************************************************
 *              ANOVA1_PrintReportView            *
 *                    11/01/23                    *
 *                      09:00                     *
 *************************************************/
package anova1.categorical;

import superClasses.PrintTextReport_View;

public class ANOVA1_Cat_PrintReportView extends PrintTextReport_View{

    ANOVA1_Cat_PrintReportView(ANOVA1_Cat_Model anova1Model,  ANOVA1_Cat_Dashboard anova1Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        stringsToPrint = anova1Model.getANOVA1Report();
        strTitleText = "One-way Analysis of Variance";
    }
}
