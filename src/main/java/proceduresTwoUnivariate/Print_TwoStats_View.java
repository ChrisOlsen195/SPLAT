/****************************************************************************
 *                   Print_TwoStats_View                                    * 
 *                         11/01/23                                         *
 *                          12:00                                           *
 ***************************************************************************/
package proceduresTwoUnivariate;

import superClasses.PrintTextReport_View;

public class Print_TwoStats_View extends PrintTextReport_View {
    // POJOs
    
    // My classes

    Explore_2Ind_Model exPlore_2Ind_Model;
   
    public Print_TwoStats_View(Explore_2Ind_Controller ex2Ind_Controller,  Explore_2Ind_Dashboard noInf_Regression_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        //System.out.println("21 Print_TwoStats_View, constructing");
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;        
        exPlore_2Ind_Model = ex2Ind_Controller.getThe2IndModel();
        sourceString = new String();
        stringsToPrint = exPlore_2Ind_Model.getStatsReport();
        strTitleText = "*****  Summary of Statistics  *****";
    }    
}
