/****************************************************************************
 *                   PrintMultDiagReport_View                                   * 
 *                         03/04/24                                         *
 *                          21:00                                           *
 ***************************************************************************/
package multipleRegression;

import superClasses.PrintTextReport_View;


public class Print_MultReg_DiagReport_View extends PrintTextReport_View {
    // POJOs
   
    public Print_MultReg_DiagReport_View(MultReg_Model multReg_Model,  MultReg_Dashboard multReg_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        //System.out.println("18 PrintMultReg_DiagReport_View, constructing");
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;

        sourceString = new String();
        stringsToPrint = multReg_Model.getDiagnostics();
        strTitleText = "Multiple Regression Analysis";
    }
}


