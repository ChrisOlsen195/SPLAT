/****************************************************************************
 *                     PrintUStats_View                                     * 
 *                         01/16/25                                         *
 *                          12:00                                           *
 ***************************************************************************/
package proceduresOneUnivariate;

import superClasses.PrintTextReport_View;
import the_t_procedures.Matched_t_Dashboard;

public class PrintUStats_View extends PrintTextReport_View {
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    // My classes

    public PrintUStats_View(PrintUStats_Model printUStatsModel,  Exploration_Dashboard regrDashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        if (printTheStuff == true) {
            System.out.println("23 *** PrintUStats_View, Constructing");
        }
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        sourceString = new String();
        stringsToPrint = printUStatsModel.getStringsToPrint();
        strTitleText = "Print univ statistics";
    }
    
    public PrintUStats_View(PrintUStats_Model printUStatsModel,  Matched_t_Dashboard matchTDashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        if (printTheStuff == true) {
            System.out.println("37 *** PrintUStats_View, Constructing");
        }
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        sourceString = new String();
        stringsToPrint = printUStatsModel.getStringsToPrint();
        strTitleText = "Print matched pair statistics";
    }
}
