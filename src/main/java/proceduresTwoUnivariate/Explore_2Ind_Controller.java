/**************************************************
 *             Explore_2Ind_Controller            *
 *                    02/07/24                    *
 *                     18:00                      *
 *************************************************/
package proceduresTwoUnivariate;

import proceduresManyUnivariate.VerticalBoxPlot_Model;
import proceduresManyUnivariate.HorizontalBoxPlot_Model;
import dataObjects.CatQuantDataVariable;
import dataObjects.ColumnOfData;
import dataObjects.QuantitativeDataVariable;
import java.util.ArrayList;
import dialogs.*;
import splat.*;

import utilityClasses.MyAlerts;
import utilityClasses.StringUtilities;

public class Explore_2Ind_Controller {
    //  POJOs
    public int n_QDVs; 
    String thisVarLabel, thisVarDescr, returnStatus, subTitle_And, subTitle_Vs,
           stackedOrSeparate, firstVarLabel, firstVarDescr, secondVarLabel, 
           secondVarDescr, dataOrSummary;
    public ArrayList<String> allTheLabels;
    
    // Make empty if no-print
    //String waldoFile = "Explore_2Ind_Controller";
    String waldoFile = "";
    
    // My classes
    public ArrayList<ColumnOfData> explore_2Ind_ColsOfData;
    public ArrayList<String> varLabel;
    public CatQuantDataVariable catQuantVar;
    
    public QuantitativeDataVariable qdv_forBBSL;
    public ArrayList<QuantitativeDataVariable> allTheQDVs;
    public Data_Manager dm;
    public boolean goodToGo, checkForLegalChoices;
    boolean[] isNumeric;
    private Explore_2Ind_Model explore_2Ind_Model;
    private Explore_2Ind_Dashboard explore_2Ind_Dashboard;
    Explore_2Ind_NotStacked_Dialog explore_2Ind_NS_Dialog;
    Explore_2Ind_Stacked_Dialog explore_2Ind_Stacked_Dialog;
    
    BBSL_Model bbsl_Model;
    ArrayList<ColumnOfData> catQualColumns;
    DotPlot_2Ind_Model dotPlot_2Ind_Model;
    HorizontalBoxPlot_Model hBox_Model;
    QQPlot_Model qqPlot_Model;
    VerticalBoxPlot_Model vBox_Model;
    
    public Explore_2Ind_Controller(Data_Manager dm) {
        this.dm = dm;
        dm.whereIsWaldo(55, waldoFile, "Constructing");
        explore_2Ind_ColsOfData = new ArrayList();
        varLabel = new ArrayList();
        returnStatus = "OK";
    }
    
    // Called from MainMenu
    public String chooseTheStructureOfData() {
        dm.whereIsWaldo(63, waldoFile, "chooseTheStructureOfData()");
        DataChoice_StackedOrNot dataChoice_Dialog_2Ind = new DataChoice_StackedOrNot(this);
        if (stackedOrSeparate.equals("TI8x-Like")) { doNotStacked(); }        
        if (stackedOrSeparate.equals("Group & Data")) { doPrepColumnsFromStacked(); }
                 
        if (stackedOrSeparate.equals("Bailed")) { 
                // No op
        }
        return "OK";
    }
    
    private String doPrepColumnsFromStacked() {
        ColumnOfData tempCol;
        dm.whereIsWaldo(77, waldoFile, "doPrepColumnsFromStacked()");
        returnStatus = "OK";
        
        MyAlerts.showNeedToUnstackAlert();
        
        explore_2Ind_Stacked_Dialog = new Explore_2Ind_Stacked_Dialog (dm, "None" );
        explore_2Ind_Stacked_Dialog.showAndWait();
        
        if (explore_2Ind_Stacked_Dialog.getReturnStatus().equals("OK")) {
            catQualColumns = explore_2Ind_Stacked_Dialog.getData();  
            catQuantVar = new CatQuantDataVariable(dm, catQualColumns.get(0), catQualColumns.get(1), false,  "Explore_2Ind_Controller");
            returnStatus = catQuantVar.finishConstructingStacked();           
            if (returnStatus.equals("OK")) {
                catQuantVar.unstackToDataStruct();
                return returnStatus;    // return good
            }     
        }
        return returnStatus;    // return bad   
    }

    protected boolean doNotStacked() {
        dm.whereIsWaldo(98, waldoFile, "doNotStacked()");
        goodToGo = true;
        int casesInStruct = dm.getNCasesInStruct();        
        if (casesInStruct == 0) {
            MyAlerts.showAintGotNoDataAlert();
            return false;
        }
        
        explore_2Ind_NS_Dialog = new Explore_2Ind_NotStacked_Dialog( dm );
        explore_2Ind_NS_Dialog.show_ANOVA1_NS_Dialog();
        returnStatus = explore_2Ind_NS_Dialog.getReturnStatus();
                
        if (goodToGo = returnStatus.equals("OK")) 
            if (!goodToGo) { return false; }
        // else...
        firstVarDescr = explore_2Ind_NS_Dialog.getFirstVariable();
        secondVarDescr = explore_2Ind_NS_Dialog.getSecondVariable();
 
        if (StringUtilities.stringIsEmpty(firstVarDescr) || StringUtilities.stringIsEmpty(secondVarDescr))  {
            subTitle_And = firstVarLabel  + " & " + secondVarLabel;  
            subTitle_Vs = firstVarLabel  + " vs " + secondVarLabel;
        } else {
            subTitle_And = firstVarDescr  + " & " + secondVarDescr; 
            subTitle_Vs = firstVarLabel  + " vs " + secondVarLabel;
        }   

        if (secondVarDescr.equals(firstVarDescr)) {
            firstVarDescr = firstVarDescr + "_01";
            secondVarDescr = firstVarDescr + "_02";
        }
        
        int nColumnsOfData = explore_2Ind_NS_Dialog.getNLevels();
        if (nColumnsOfData == 0) { 
            goodToGo = false;
            returnStatus = "Cancel";            
            return goodToGo; 
        }
        
        // else...
        explore_2Ind_ColsOfData = explore_2Ind_NS_Dialog.getData();
        // Stack the columns into one column for the BBSL procedure
        // the 2nd and third colums are passed to the other procedures
        thisVarLabel = "All";
        thisVarDescr = "All";
        ArrayList<String> tempAlStr = new ArrayList<>();
        
        for (int ith = 0; ith < nColumnsOfData; ith++) {
            ColumnOfData tempCol = explore_2Ind_ColsOfData.get(ith);
            int nColSize = tempCol.getColumnSize();
            
            for (int jth = 0; jth < nColSize; jth++) {
                tempAlStr.add(tempCol.getTheCases_ArrayList().get(jth));
            }
        }
        
        ColumnOfData tempCOD = new ColumnOfData(dm, thisVarLabel, thisVarDescr, tempAlStr);
        qdv_forBBSL = new QuantitativeDataVariable(thisVarLabel, thisVarDescr,tempCOD);
 
        allTheQDVs = new ArrayList();

        for (int ith = 0; ith < nColumnsOfData; ith++) {
            thisVarLabel = explore_2Ind_ColsOfData.get(ith).getVarLabel();
            thisVarDescr = explore_2Ind_ColsOfData.get(ith).getVarDescription();
            QuantitativeDataVariable tempQDV = new QuantitativeDataVariable(thisVarLabel, thisVarDescr, explore_2Ind_ColsOfData.get(ith));  
            allTheQDVs.add(tempQDV);                 
        } 
        
        System.out.println("166 Explore2Ind_Cont, qdvSize = " + allTheQDVs.size());
        
        compare_The_2Ind();
        return goodToGo;
    }
    
    protected boolean compare_The_2Ind() {
        dm.whereIsWaldo(172, waldoFile, "compare_The_2Ind");
        n_QDVs = allTheQDVs.size();
        allTheLabels = new ArrayList<>();
        
        firstVarLabel = allTheQDVs.get(0).getTheVarLabel();
        secondVarLabel = allTheQDVs.get(1).getTheVarLabel();        
        
        for (int iVars = 0; iVars < n_QDVs; iVars++) {            
            if (allTheQDVs.get(iVars).getTheVarDescription() == null) {
                allTheQDVs.get(iVars).setTheVarDescription(allTheQDVs.get(iVars).getTheVarLabel());
            }
           
            allTheLabels.add(allTheQDVs.get(iVars).getTheVarLabel());
        }
        
        firstVarLabel = allTheQDVs.get(0).getTheVarLabel();
        secondVarLabel = allTheQDVs.get(1).getTheVarLabel();

        // The QDVs already have labels -- dump allTheLabels & get from QDV?
        explore_2Ind_Model = new Explore_2Ind_Model(this, firstVarDescr, secondVarDescr, allTheQDVs, allTheLabels);
        String anovaOK = explore_2Ind_Model.continueInitializing();
        
        if (!anovaOK.equals("OK")) { return false; }

        if (StringUtilities.stringIsEmpty(firstVarDescr) || StringUtilities.stringIsEmpty(secondVarDescr))  {
            subTitle_And = firstVarLabel  + " & " + firstVarLabel;  
            subTitle_Vs = secondVarLabel  + " vs " + firstVarLabel;
        } else {
            subTitle_And = firstVarDescr  + " & " + secondVarDescr; 
            subTitle_Vs = secondVarLabel  + " vs " + secondVarLabel;
        }   

        if (secondVarDescr.equals(firstVarDescr)) {
            firstVarDescr = firstVarDescr + "_01";
            secondVarDescr = firstVarDescr + "_02";
        }
        
        hBox_Model = new HorizontalBoxPlot_Model(this, subTitle_And, allTheQDVs);
        vBox_Model = new VerticalBoxPlot_Model(this, subTitle_And, allTheQDVs);
        qqPlot_Model = new QQPlot_Model(this, allTheQDVs);
        bbsl_Model = new BBSL_Model(this, qdv_forBBSL, allTheQDVs);
        dotPlot_2Ind_Model = new DotPlot_2Ind_Model(this, subTitle_And, allTheQDVs);        
        
        explore_2Ind_Dashboard = new Explore_2Ind_Dashboard(this, explore_2Ind_Model);
        explore_2Ind_Dashboard.populateTheBackGround();
        explore_2Ind_Dashboard.putEmAllUp();
        explore_2Ind_Dashboard.showAndWait();
        returnStatus = explore_2Ind_Dashboard.getReturnStatus();
        returnStatus = "OK";
        return true;
        
    } 
    
    public void setStackedOrSeparate(String toThis) { stackedOrSeparate = toThis; }
    
    private boolean validateStackChoices() {
        dm.whereIsWaldo(233, waldoFile, "validateStackChoices()");
        isNumeric = new boolean[2];        
        for (int ithCol = 0; ithCol < 2; ithCol++){
            isNumeric[ithCol] = explore_2Ind_ColsOfData.get(ithCol).getIsNumeric();  
        }
        return true;
    }
    
    public String getSubTitleAnd() { return subTitle_And; }    
    public String getSubTitleVs() { return subTitle_Vs; }   
    public String getFirstVarDescr() { return firstVarDescr; }
    public String getSecondVarDescr() { return secondVarDescr; }
    public Explore_2Ind_Model getThe2IndModel() { return explore_2Ind_Model; }    
    public HorizontalBoxPlot_Model getHBox_Model() { return hBox_Model; }
    public DotPlot_2Ind_Model get_2Ind_Dot_Model() { return dotPlot_2Ind_Model; }
    public QQPlot_Model getQQ_Model() { return qqPlot_Model; }
    public VerticalBoxPlot_Model getVBox_Model() { return vBox_Model; }
    public BBSL_Model getBBSL_Model() { return bbsl_Model; }

    public Data_Manager getDataManager() {return dm; }
}