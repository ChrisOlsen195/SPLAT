/****************************************************************************
 *                     SL_PrintText_View                                     * 
 *                         10/15/23                                         *
 *                          21:00                                           *
 ***************************************************************************/
package proceduresOneUnivariate;

//import java.util.ArrayList;
//import javafx.scene.control.TextArea;
import superClasses.PrintTextReport_View;
//import utilityClasses.StringUtilities;

public class SL_PrintText_View extends PrintTextReport_View {
    // POJOs
    //int nCategories, df, maxSpaces, spacesNeeded;                                                                                                                                                                                  
    //int[] observedValues; 
    //ArrayList<String> daStrings;
    
    // My classes
    //StringUtilities myStringUtilities;  
    //Exploration_Dashboard explorationDashboard;
    //TextArea theDesiredSLTA;
   
    public SL_PrintText_View(StemNLeaf_View snl_View,
                             Exploration_Dashboard explorationDashboard,
                             double placeHoriz, double placeVert,
                             double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        System.out.println("29 SL_PrintText_View, constructing");
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;

        //myStringUtilities = new StringUtilities();
        stringsToPrint = snl_View.getTheDesiredSL();
    }    
}
