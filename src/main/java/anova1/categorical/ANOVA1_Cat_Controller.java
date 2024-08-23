/**************************************************
 *             ANOVA1_Cat_Controller              *
 *                    06/22/24                    *
 *                     18:00                      *
 *************************************************/
package anova1.categorical;

//import dialogs.DataChoice_StackedOrNot;
import superClasses.ANOVA1_Controller;
import dataObjects.CatQuantDataVariable;
import dataObjects.QuantitativeDataVariable;
import dialogs.*;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import splat.*;
import utilityClasses.MyAlerts;

public class ANOVA1_Cat_Controller extends ANOVA1_Controller {
    //  POJOs
    
    String thisVarLabel, thisVarDescr, stackedOrSeparate;
    // Make empty if no-print
    //String waldoFile = "ANOVA1_Cat_Controller";
    String waldoFile = "";

    public ObservableList<String> varLabels;
    boolean[] isNumeric;
    private ANOVA1_Cat_Model anova1_Cat_Model;
    private ANOVA1_Cat_Dashboard anova1_Cat_Dashboard;
    ANOVA1_Cat_NotStacked_Dialog anova1_Cat_NS_Dialog;
    ANOVA1_Cat_Stacked_Dialog anova1_Cat_S_Dialog;
    
    public ANOVA1_Cat_Controller(Data_Manager dm) {
        super(dm);
        this.dm = dm;
        dm.whereIsWaldo(38, waldoFile, "Constructing");
        anova1_ColsOfData = new ArrayList();
        returnStatus = "OK";
    }
    
    public void doStackedOrNot() {
       // DataChoice_StackedOrNot stackedYesOrNo = new DataChoice_StackedOrNot(this);
        
        // stackedOrSeparate is set by the dialog         
        if (stackedOrSeparate.equals("Group & Data")) { doStacked(); }
        if (stackedOrSeparate.equals("TI8x-Like")) { doNotStacked(); }        
    }

    protected boolean doStacked() {
        dm.whereIsWaldo(52, waldoFile, "doStacked()");
        do {
            int casesInStruct = dm.getNCasesInStruct();            
            if (casesInStruct == 0) {
                MyAlerts.showAintGotNoDataAlert();
                return false;
            }
            
            anova1_Cat_S_Dialog = new ANOVA1_Cat_Stacked_Dialog( dm, "Categorical" );
            anova1_Cat_S_Dialog.showAndWait();
            goodToGo = anova1_Cat_S_Dialog.getGoodToGO();
            
            if (!goodToGo) {
                returnStatus = anova1_Cat_S_Dialog.getReturnStatus();
                return false;
            }
            
            anova1_ColsOfData = anova1_Cat_S_Dialog.getData();
            dm.whereIsWaldo(70, waldoFile, "doStacked()");
            int nLevels = anova1_ColsOfData.get(0).getNumberOfDistinctValues();
            if (nLevels < 3) {
                MyAlerts.showAnova1_LT3_LevelsAlert();
                goodToGo = false;
                return false;
            }
            
            checkForLegalChoices = validateStackChoices();
        } while (!checkForLegalChoices);
        
        dm.whereIsWaldo(81, waldoFile, "doStacked()");
        explVarDescr = anova1_Cat_S_Dialog.getPreferredFirstVarDescription();
        respVarDescr = anova1_Cat_S_Dialog.getPreferredSecondVarDescription();
  
        //                                Categorical,             Quantitative            return All and individuals
        cqdv = new CatQuantDataVariable(dm, anova1_ColsOfData.get(0), anova1_ColsOfData.get(1), true, "ANOVA1_Cat_Controller");   
        returnStatus = cqdv.finishConstructingStacked();
        dm.whereIsWaldo(91, waldoFile, "doStacked()");
        
        if(returnStatus.equals("OK")) { 
            allTheQDVs = new ArrayList();
            allTheQDVs = cqdv.getAllQDVs();
            allTheQDVs.remove(0);   // Dump the All qdv
            n_QDVs = allTheQDVs.size();

            collectAllTheLabels();
            doTheANOVA();
            return true;
        }
        return false;
    }

    protected boolean doNotStacked() {
        dm.whereIsWaldo(104, waldoFile, "doNotStacked()");
        goodToGo = true;
        int casesInStruct = dm.getNCasesInStruct();
        
        if (casesInStruct == 0) {
            MyAlerts.showAintGotNoDataAlert();
            return false;
        }
        
        anova1_Cat_NS_Dialog = new ANOVA1_Cat_NotStacked_Dialog( dm );
        anova1_Cat_NS_Dialog.show_ANOVA1_NS_Dialog();
        returnStatus = anova1_Cat_NS_Dialog.getReturnStatus();
        
        goodToGo = returnStatus.equals("OK"); 
            if (!goodToGo) { return false; }
        // else...
        explVarDescr = anova1_Cat_NS_Dialog.getExplanatoryVariable();
        respVarDescr = anova1_Cat_NS_Dialog.getResponseVariable();
        
        int nColumnsOfData = anova1_Cat_NS_Dialog.getNLevels();
        
        System.out.println("125 -- ANOVA1_Quant_Controller, nCols" + nColumnsOfData );
        
        if (nColumnsOfData == 0) { 
            goodToGo = false;
            returnStatus = "Cancel";            
            return goodToGo; 
        }
        
        if (nColumnsOfData < 3) {
            MyAlerts.showAnova1_LT3_LevelsAlert();
            goodToGo = false;
            return false;
        }
        
        // else...
        anova1_ColsOfData = anova1_Cat_NS_Dialog.getData();
        allTheQDVs = new ArrayList();

        for (int ith = 0; ith < nColumnsOfData; ith++) {
            thisVarLabel = anova1_ColsOfData.get(ith).getVarLabel();
            thisVarDescr = anova1_ColsOfData.get(ith).getVarDescription();
            tempQDV = new QuantitativeDataVariable(thisVarLabel, thisVarDescr, anova1_ColsOfData.get(ith));  
            allTheQDVs.add(tempQDV);                 
        } 
        n_QDVs = allTheQDVs.size();
        collectAllTheLabels();  
        doTheANOVA();
        return goodToGo;
    }
    
    protected boolean doTheANOVA() {
        dm.whereIsWaldo(156, waldoFile, " *** doTheANOVA()");
        allTheLabels = new ArrayList<>();
        
        for (int iVars = 0; iVars < n_QDVs; iVars++) {
            allTheLabels.add(allTheQDVs.get(iVars).getTheVarLabel());
        }

        // The QDVs already have labels -- dump allTheLabels & get from QDV?
        anova1_Cat_Model = new ANOVA1_Cat_Model(this, explVarDescr, respVarDescr, allTheQDVs);
        String anovaOK = anova1_Cat_Model.continueInitializing();
        
        if (!anovaOK.equals("OK")) { return false;  }
        
        anova1_Cat_Dashboard = new ANOVA1_Cat_Dashboard(this, anova1_Cat_Model);
        anova1_Cat_Dashboard.populateTheBackGround();
        anova1_Cat_Dashboard.putEmAllUp();
        anova1_Cat_Dashboard.showAndWait();
        returnStatus = anova1_Cat_Dashboard.getReturnStatus();
        returnStatus = "OK";
        return true;        
    } 
    
    public void setStackedOrSeparate(String toThis) {
        stackedOrSeparate = toThis;
    }
    
    private boolean validateStackChoices() {
        dm.whereIsWaldo(183, waldoFile, "validateStackChoices()");
        isNumeric = new boolean[2];
        
        for (int ithCol = 0; ithCol < 2; ithCol++){
            isNumeric[ithCol] = anova1_ColsOfData.get(ithCol).getIsNumeric();  
        }
        return true;
    }
    
    private void collectAllTheLabels() {
        varLabels = FXCollections.observableArrayList();         
        for (int ithVar = 0; ithVar < n_QDVs; ithVar++) {
            varLabels.add(allTheQDVs.get(ithVar).getTheVarLabel());
        }
    }
        
    public ObservableList <String> getVarLabels() { 
        return varLabels; 
    }
    
    public Data_Manager getDataManager() {return dm; }
}