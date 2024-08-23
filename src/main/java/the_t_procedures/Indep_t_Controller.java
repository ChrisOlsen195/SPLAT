/**************************************************
 *               Indep_t_Controller               *
 *                    06/15/24                    *
 *                     12:00                      *
 *************************************************/
package the_t_procedures;

import dialogs.t_and_z.Ind_t_Stacked_Dialog;
import dialogs.t_and_z.Indep_t_Dialog;
import dataObjects.CatQuantDataVariable;
import dataObjects.ColumnOfData;
import dataObjects.QuantitativeDataVariable;
import dialogs.t_and_z.DataStructChoice_Dialog_t_Indep_Means;
import dialogs.t_and_z.Ind_t_SumStats_Dialog;
import java.util.ArrayList;
import splat.*;
import utilityClasses.*;

public class Indep_t_Controller {
    // POJOs  
    final int TWO;
            
    String returnStatus, firstVarDescription, secondVarDescription,
           dataOrSummary;
    //String waldoFile = "Indep_t_Controller";
    String waldoFile = "";
        
    // My classes
    ArrayList<ColumnOfData> quantColsOfData, catQualColumns;
    CatQuantDataVariable catQuantVar;
    Indep_t_Dialog ind_t_Dialog;
    Indep_t_PrepStructs ind_t_prepStructs;    
    QuantitativeDataVariable qdv_forBBSL;
    ArrayList<QuantitativeDataVariable> allTheQDVs;
    Data_Manager dm;
    
    Indep_t_SumStats_Model indep_t_SumStats_Model; 
    Ind_t_SumStats_Dialog ind_t_SummaryStats_Dialog;
    Indep_t_SumStats_Dashboard indep_t_SumStats_Dashboard;
    
    // ******  Constructor called from Main Menu  ******
    public Indep_t_Controller(Data_Manager dm) {
        this.dm = dm; 
        dm.whereIsWaldo(44, waldoFile, "Constructing");
        quantColsOfData = new ArrayList();
        catQualColumns = new ArrayList();
        TWO = 2;
    }
  
    //  Called from MainMenu
    public String chooseTheStructureOfData() {
        dm.whereIsWaldo(52, waldoFile, "chooseTheStructureOfData()");
        DataStructChoice_Dialog_t_Indep_Means sed = new DataStructChoice_Dialog_t_Indep_Means(this);
        
        if (dataOrSummary.equals("Data")) { doPrepColumnsFromNonStacked(); }        
        if (dataOrSummary.equals("Group & Data")) { doPrepColumnsFromStacked(); }
                
        if (dataOrSummary.equals("Summary")) {
            Single_t_SumStats_Controller singleT_SumStats_Controller = new Single_t_SumStats_Controller();
            returnStatus = singleT_SumStats_Controller.getReturnStatus();            
            if (returnStatus.equals("OK")) {
                returnStatus = doTheSumStatsProcedure();
            }
        } 
        if (dataOrSummary.equals("Bailed")) {
            // No op
        }
        return "OK";
    }
    
    private String doPrepColumnsFromStacked() {
        ColumnOfData tempCol;
        dm.whereIsWaldo(73, waldoFile, "doPrepColumnsFromStacked()");
        returnStatus = "OK";
        
        MyAlerts.showNeedToUnstackAlert();
        
        Ind_t_Stacked_Dialog ind_t_Stacked_Dialog = new Ind_t_Stacked_Dialog( dm );
        ind_t_Stacked_Dialog.showAndWait();
        
        if (ind_t_Stacked_Dialog.getReturnStatus().equals("OK")) {
            catQualColumns = ind_t_Stacked_Dialog.getData();  
            catQuantVar = new CatQuantDataVariable(dm, catQualColumns.get(0), catQualColumns.get(1), false,  "Indep_t_Controller");
            returnStatus = catQuantVar.finishConstructingStacked();           
            if (returnStatus.equals("OK")) {
                catQuantVar.unstackToDataStruct();
                return returnStatus;    // return good
            }     
        }
        return returnStatus;    // return bad   
    }
    
    public String doPrepColumnsFromNonStacked() {
        dm.whereIsWaldo(94, waldoFile, "doPrepColumnsFromNonStacked()");
        returnStatus = "Cancel";
        int casesInStruct = dm.getNCasesInStruct();
        
        if (casesInStruct == 0) {
            MyAlerts.showAintGotNoDataAlert();
            return "Cancel";
        }
        
        ind_t_Dialog = new Indep_t_Dialog(dm, "QUANTITATIVE");
        ind_t_Dialog.showAndWait();
        returnStatus = ind_t_Dialog.getReturnStatus();
        
        if (returnStatus.equals("Cancel")) { return returnStatus; }
        
        quantColsOfData = ind_t_Dialog.getData();
       
        firstVarDescription = ind_t_Dialog.getPreferredFirstVarDescription();
        secondVarDescription = ind_t_Dialog.getPreferredSecondVarDescription();
 
        returnStatus = doTheNonStackedProcedure();
        return returnStatus;
    } 
    
    private String doTheNonStackedProcedure() {
        dm.whereIsWaldo(119, waldoFile, "doTheNonStackedProcedure()");
        String strVarLabel = "All";
        ArrayList<String> tempAlStr = new ArrayList<>();
        
        for (int ith = 0; ith < TWO; ith++) {
            ColumnOfData tempCol = quantColsOfData.get(ith);
            System.out.println("125 Ind_t_Controller, ith/descr = " + ith + " / " + tempCol.getVarDescription());
            int nColSize = tempCol.getColumnSize();            
            for (int jth = 0; jth < nColSize; jth++) {
                tempAlStr.add(tempCol.getTheCases_ArrayList().get(jth));
            }
        }
        
        ColumnOfData tempCOD = new ColumnOfData(dm, strVarLabel, "Indep-t-Controller", tempAlStr);
        qdv_forBBSL = new QuantitativeDataVariable(firstVarDescription, secondVarDescription, tempCOD);

        allTheQDVs = new ArrayList();
        
        for (int ith = 0; ith < TWO; ith++) {
            QuantitativeDataVariable tempQDV = new QuantitativeDataVariable(firstVarDescription, secondVarDescription, quantColsOfData.get(ith)); 
            allTheQDVs.add(tempQDV);                 
        }   

        ind_t_prepStructs = new Indep_t_PrepStructs(this, qdv_forBBSL, allTheQDVs);
        returnStatus = ind_t_prepStructs.showTheDashboard();
        return returnStatus;        
    }
    
    public String doTheSumStatsProcedure() {
        dm.whereIsWaldo(148, waldoFile, "doTheSumStatsProcedure()");        
        String doItAgain = "Yes";
        
        while (doItAgain.equals("Yes")) {
            returnStatus = "Cancel";
            ind_t_SummaryStats_Dialog = new Ind_t_SumStats_Dialog();
            returnStatus = ind_t_SummaryStats_Dialog.getReturnStatus();
            
            if (returnStatus.equals("Cancel")) { return returnStatus; }

            indep_t_SumStats_Model = new Indep_t_SumStats_Model(this, ind_t_SummaryStats_Dialog);
            indep_t_SumStats_Model.doIndepTAnalysis();
            indep_t_SumStats_Dashboard = new Indep_t_SumStats_Dashboard(this);
            showTheSumStatsDashboard();

            String title = "I'm doing homework Alert";
            String header = "On the theory you might be doing lots of these," +
                            "\n would you like to do more right away?";
            YesNoCancel_Alert ync = new YesNoCancel_Alert(title, header);            
            doItAgain = ync.getReturnString();
        }
        returnStatus = "Finished";
        return returnStatus;
    } 
    
     public String showTheSumStatsDashboard() {
        indep_t_SumStats_Dashboard.populateTheBackGround();
        indep_t_SumStats_Dashboard.putEmAllUp();
        indep_t_SumStats_Dashboard.showAndWait();
        returnStatus = indep_t_SumStats_Dashboard.getReturnStatus();
        returnStatus = "Ok";
        return returnStatus;           
    } 
     
    public void setDataOrSummary(String toThis) { dataOrSummary = toThis; }
     
    public String getFirstVarDescription() { return firstVarDescription; }
    public String getSecondVarDescription() { return secondVarDescription; }
    
    /* ************  Independent t WITH DATA  ************************** */
    public double getInd_T_Alpha() { return ind_t_Dialog.getAlpha(); }    
    public String getInd_T_AltHypothesis() { 
        return ind_t_Dialog.getHypotheses(); 
    }
        
    public double getInd_T_HypothesizedDiff() { 
        return ind_t_Dialog.getHypothesizedDiff();
    }
    
    /* ************  Independent t WITHOUT DATA  ************************** */
    public double getInd_T_SumStats_Alpha() { return ind_t_SummaryStats_Dialog.getAlpha(); }
 
    public String getInd_T_SumStats_Hypotheses() { 
        return ind_t_SummaryStats_Dialog.getHypotheses(); 
    }

    public double getInd_T_SumStats_HypothesizedDiff() { 
        return ind_t_SummaryStats_Dialog.getHypothesizedDiff();
    }
    
    public Indep_t_PrepStructs getTStructs() { return ind_t_prepStructs; }
    
    public Indep_t_SumStats_Model getInd_t_SumStatsModel() {
        return indep_t_SumStats_Model;
    }
    
    public ArrayList<QuantitativeDataVariable> getTheQDVs() { return allTheQDVs; }    
    public Indep_t_Dialog getTDialog() { return ind_t_Dialog; }    
    public Data_Manager getDataManager() { return dm; }
}
