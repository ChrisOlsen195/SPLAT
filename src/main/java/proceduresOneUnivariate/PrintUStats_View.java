/****************************************************************************
 *                     PrintUStats_View                                     * 
 *                         11/01/23                                         *
 *                          15:00                                           *
 ***************************************************************************/
package proceduresOneUnivariate;

//import utilityClasses.StringUtilities;
import superClasses.PrintTextReport_View;
import the_t_procedures.Matched_t_Dashboard;

public class PrintUStats_View extends PrintTextReport_View {
    // POJOs
  
    // My classes

    public PrintUStats_View(PrintUStats_Model printUStatsModel,  Exploration_Dashboard regrDashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        //System.out.println("21 PrintUStats_View, constructing");
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
        System.out.println("33 PrintUStats_View, constructing");
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        sourceString = new String();
        stringsToPrint = printUStatsModel.getStringsToPrint();
        strTitleText = "Print matched pair statistics";
    }
}
