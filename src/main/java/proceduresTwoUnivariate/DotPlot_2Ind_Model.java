/**************************************************
 *                DotPlot_2Ind__Model             *
 *                    02/07/24                    *
 *                      18:00                     *
 *************************************************/
package proceduresTwoUnivariate;

import dataObjects.QuantitativeDataVariable;
import java.util.ArrayList;
import splat.Data_Manager;
import proceduresOneUnivariate.*;


public class DotPlot_2Ind_Model {
    // POJOs
    String firstVarDescription, secondVarDescription;
    
    // Make empty if no-print
    //String waldoFile = "DotPlot_2Ind_Model";
    String waldoFile = "";
     
    // My classes
    Data_Manager dm;
    DotPlot_Model dotPlotModel_1, dotPlotModel_2;
    DotPlot_View dotPlot_View_1, dotPlot_View_2;
    ArrayList<QuantitativeDataVariable> allTheQDVs;   // Array list of DataVariables
    
    public DotPlot_2Ind_Model() { }

    public DotPlot_2Ind_Model(Explore_2Ind_Controller explore_2Ind_Controller, String subTitle, ArrayList<QuantitativeDataVariable> allTheQDVs) {
        dm = explore_2Ind_Controller.getDataManager();
        dm.whereIsWaldo(32, waldoFile, "Constructing from explore_2_Ind_Controller");
        this.allTheQDVs = new ArrayList<>();
        this.allTheQDVs = allTheQDVs;
        
        firstVarDescription = explore_2Ind_Controller.getFirstVarDescr();
        secondVarDescription = explore_2Ind_Controller.getSecondVarDescr();
        // Should be 3 QDV's -- All and the two distributions separated
        
        dotPlotModel_1 = new DotPlot_Model(firstVarDescription, allTheQDVs.get(0));
        dotPlotModel_2 = new DotPlot_Model(secondVarDescription, allTheQDVs.get(1));
 
        dotPlot_View_1 = new DotPlot_View(dotPlotModel_1, 25, 25, 650, 300);
        dotPlot_View_2 = new DotPlot_View(dotPlotModel_2, 25, 25, 650, 300);
    } 
    
    public ArrayList<QuantitativeDataVariable> getAllTheQDVs() { return allTheQDVs; }     
    public DotPlot_Model getModel_01() { return dotPlotModel_1; }    
    public DotPlot_Model getModel_02() { return dotPlotModel_2; }    
    public DotPlot_View getView_01() { return dotPlot_View_1; }    
    public DotPlot_View getView_02() { return dotPlot_View_2; }
}
