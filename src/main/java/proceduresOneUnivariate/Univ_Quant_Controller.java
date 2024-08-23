/************************************************************
 *                    Univ_Quant_Controller                 *
 *                          12/06/23                        *
 *                            12:00                         *
 ***********************************************************/
package proceduresOneUnivariate;

import proceduresManyUnivariate.VerticalBoxPlot_Model;
import proceduresManyUnivariate.HorizontalBoxPlot_Model;
import dialogs.ExploreUniv_Dialog;
import dataObjects.ColumnOfData;
import dataObjects.QuantitativeDataVariable;
import java.util.ArrayList;
import splat.Data_Manager;
import utilityClasses.MyAlerts;

public class Univ_Quant_Controller {
    // POJOs
    boolean goodToGo;
    String returnStatus, descriptionOfVar, strDataType;
    
    // Make empty if no-print
    //String waldoFile = "Univ_Quant_Controller";
    String waldoFile = "";
    
    // My classes
    ColumnOfData columnOfData;
    CumulativeFrequency_Model cumulativeFrequency_Model;
    Data_Manager dm;
    DotPlot_Model dotPlotModel; 
    private Exploration_Dashboard exploration_Dashboard;
    ExploreUniv_Dialog exploreUniv_Dialog;
    Histogram_Model histogram_Model;
    HorizontalBoxPlot_Model hBoxPlotModel;
    NormProb_Model normProbModel;
    NormProb_DiffModel normProb_DiffModel;
    PrintUStats_Model printUStats_Model;
    private QuantitativeDataVariable theQDV;
    ArrayList<QuantitativeDataVariable> qdvsForBoxPlots;
    StemNLeaf_Model stemNLeafModel;
    VerticalBoxPlot_Model vBoxModel;
    
    public Univ_Quant_Controller(Data_Manager dm, String strDataType) {
        this.dm = dm;
        this.strDataType = strDataType;
        dm.whereIsWaldo(43, waldoFile, "Constructing");
    }  
        
    // Called by MainMenu
    public String doTheQuantitativeProcedure() {
        dm.whereIsWaldo(49, waldoFile, "doTheQuantitativeProcedure()");
        int casesInStruct = dm.getNCasesInStruct();
        
        if (casesInStruct == 0) {
            MyAlerts.showAintGotNoDataAlert();
            return "Cancel";
        }
        
        if (casesInStruct > 5000) {
            MyAlerts.showLongTimeComingWarning();
        }
        
        exploreUniv_Dialog = new ExploreUniv_Dialog(dm, strDataType);
        exploreUniv_Dialog.showAndWait();
        returnStatus = exploreUniv_Dialog.getReturnStatus();
        
        if (returnStatus.equals("OK")) {
            descriptionOfVar = exploreUniv_Dialog.getDescriptionOfVariable();
            columnOfData = exploreUniv_Dialog.getData();
            
            if (columnOfData.getNumberOfDistinctValues() < 2) {
                MyAlerts.showNoVariabilityAlert();             
                returnStatus = "NoVariability";
                return returnStatus;
            }
            
            goodToGo = PrepareQuantitativeStructs();
            if (goodToGo) {
                exploration_Dashboard.populateTheBackGround();
                exploration_Dashboard.putEmAllUp();
                exploration_Dashboard.showAndWait();
                returnStatus = exploration_Dashboard.getReturnStatus();
                return returnStatus;  
            }
        }
        dm.whereIsWaldo(86, waldoFile, "end doTheQuantitativeProcedure()");
        return returnStatus;
    }
    
    public boolean PrepareQuantitativeStructs() {         
        dm.whereIsWaldo(91, waldoFile, "PrepareQuantitativeStructs()");
        theQDV = new QuantitativeDataVariable(columnOfData.getVarLabel(), columnOfData.getVarDescription(), columnOfData);  
        // Box plots require qdvs for All and Each
        qdvsForBoxPlots = new ArrayList<>();
        qdvsForBoxPlots.add(theQDV);
        qdvsForBoxPlots.add(theQDV);
        histogram_Model = new Histogram_Model(descriptionOfVar, theQDV);
        normProbModel = new NormProb_Model(descriptionOfVar, theQDV);
        normProb_DiffModel = new NormProb_DiffModel(descriptionOfVar, theQDV);
        printUStats_Model = new PrintUStats_Model(descriptionOfVar, theQDV, false);
        // The StemNLeaf parameters are also for BBSL -- zeros for non-BBSL
        stemNLeafModel = new StemNLeaf_Model(descriptionOfVar, theQDV, false, 0, 0, 0);

        dotPlotModel = new DotPlot_Model(descriptionOfVar, theQDV);
        hBoxPlotModel = new HorizontalBoxPlot_Model(descriptionOfVar, theQDV);
        vBoxModel = new VerticalBoxPlot_Model(descriptionOfVar, theQDV); 
        cumulativeFrequency_Model = new CumulativeFrequency_Model(descriptionOfVar, theQDV);
        exploration_Dashboard = new Exploration_Dashboard(this, theQDV); 
        dm.whereIsWaldo(109, waldoFile, "end PrepareQuantitativeStructs()");
        return true;
    }
    
    public Histogram_Model getHistModel() {return histogram_Model; }
    public NormProb_Model getNormProbModel() { return normProbModel; }
    public NormProb_DiffModel getNormProb_DiffModel() { return normProb_DiffModel; }
    public StemNLeaf_Model getStemNLeafModel() { return stemNLeafModel; }
    public DotPlot_Model getDotPlotModel()  { return dotPlotModel; }
    public HorizontalBoxPlot_Model getHBoxModel() { return hBoxPlotModel; }
    public VerticalBoxPlot_Model getVBoxModel() { return vBoxModel; }  
    public CumulativeFrequency_Model getOgiveModel() { return cumulativeFrequency_Model; }
    public PrintUStats_Model getPrintUStatsModel() { return printUStats_Model; }
    public Data_Manager getDataManager() { return dm; }
    public String getDescriptionOfVariable() {return descriptionOfVar; }
}
