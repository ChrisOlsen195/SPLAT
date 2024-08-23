/****************************************************************************
 *                 PrintFile_PrintReportView                                 * 
 *                         05/12/24                                         *
 *                          15:00                                           *
 ***************************************************************************/
package printFile;

import superClasses.PrintTextReport_View;


public class PrintFile_PrintReportView extends PrintTextReport_View {
    // POJOs

    public PrintFile_PrintReportView(PrintFile_Model printFile_Model, PrintFile_Dashboard printFile_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        System.out.println("18 PrintMultRegrReport_View, constructing");
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;

        sourceString = new String();
        stringsToPrint = printFile_Model.getPrintFileReport();
        strTitleText = "Multiple Regression Analysis";
    }
    
    
}
