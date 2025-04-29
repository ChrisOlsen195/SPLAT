/****************************************************************************
 *                 PrintMultRegrReport_View                                 * 
 *                         01/15/25                                         *
 *                          21:00                                           *
 ***************************************************************************/
package multipleRegression;

import superClasses.PrintTextReport_View;


public class PrintMultRegrReport_View extends PrintTextReport_View {
    // POJOs

    public PrintMultRegrReport_View(MultReg_Model multReg_Model,  MultReg_Dashboard multReg_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        //System.out.println("18 PrintMultRegrReport_View, Constructing");
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;

        sourceString = new String();
        stringsToPrint = multReg_Model.getRegressionReport();
        strTitleText = "Multiple Regression Analysis";
    }
}
