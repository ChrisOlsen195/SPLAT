/**************************************************
 *            ANOVA1_Quant_Controller             *
 *                    09/03/24                    *
 *                     15:00                      *
 *************************************************/
package anova1.quantitative;

import dialogs.DataChoice_StackedOrNot;
import superClasses.ANOVA1_Controller;
import dataObjects.CatQuantDataVariable;
import dataObjects.QuantitativeDataVariable;
import dialogs.*;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import splat.*;
import utilityClasses.MyAlerts;

public class ANOVA1_Quant_Controller extends ANOVA1_Controller {
    //  POJOs
    
    public int[] theNewOrder;
    
    String thisVarLabel, thisVarDescr, stackedOrSeparate;
    String[] incomingLabels;
    
    // Make empty if no-print
    String waldoFile = "ANOVA1_Quant_Controller";
    //String waldoFile = "";
    
    public ObservableList<String> varLabels;
    boolean[] isNumeric;
    private ANOVA1_Quant_Model anova1_Quant_Model;
    private ANOVA1_Quant_Dashboard anova1_Quant_Dashboard;
    ANOVA1_Quant_NotStacked_Dialog anova1_Quant_NS_Dialog;
    ANOVA1_Quant_Stacked_Dialog anova1_Quant_S_Dialog;
    ReOrderStringDisplay_Dialog reOrderStrings_Dialog;
    
    public ANOVA1_Quant_Controller(Data_Manager dm) {
        super(dm);
        this.dm = dm;
        dm.whereIsWaldo(37, waldoFile, "Constructing");
        anova1_ColsOfData = new ArrayList();
        returnStatus = "OK";
    }
    
    public void doStackedOrNot() {
        DataChoice_StackedOrNot stackedYesOrNo = new DataChoice_StackedOrNot(this);       
        // stackedOrSeparate is set by the dialog        
        if (stackedOrSeparate.equals("Group & Data")) { doStacked(); }
        if (stackedOrSeparate.equals("TI8x-Like")) { doNotStacked(); }        
    }

    protected boolean doStacked() {
        dm.whereIsWaldo(50, waldoFile, "doStacked()");
        do {
            int casesInStruct = dm.getNCasesInStruct();            
            if (casesInStruct == 0) {
                MyAlerts.showAintGotNoDataAlert();
                return false;
            }
            
            anova1_Quant_S_Dialog = new ANOVA1_Quant_Stacked_Dialog( dm, "Categorical" );
            anova1_Quant_S_Dialog.showAndWait();
            goodToGo = anova1_Quant_S_Dialog.getGoodToGO();
            
            if (!goodToGo) {
                returnStatus = anova1_Quant_S_Dialog.getReturnStatus();
                return false;
            }
            
            anova1_ColsOfData = anova1_Quant_S_Dialog.getData();
            //dm.whereIsWaldo(68, waldoFile, "doStacked()");
            int nLevels = anova1_ColsOfData.get(0).getNumberOfDistinctValues();
            if (nLevels < 3) {
                MyAlerts.showAnova1_LT3_LevelsAlert();
                goodToGo = false;
                return false;
            }
            
            checkForLegalChoices = validateStackChoices();
        } while (!checkForLegalChoices);
        
        //dm.whereIsWaldo(79, waldoFile, "doStacked()");
        explVarDescr = anova1_Quant_S_Dialog.getPreferredFirstVarDescription();
        respVarDescr = anova1_Quant_S_Dialog.getPreferredSecondVarDescription();
  
        //                                Categorical,             Quantitative            return All and individuals
        cqdv = new CatQuantDataVariable(dm, anova1_ColsOfData.get(0), anova1_ColsOfData.get(1), true, "ANOVA1_Cat_Controller");   
        returnStatus = cqdv.finishConstructingStacked();
        //dm.whereIsWaldo(86, waldoFile, " --- doStacked()");
        
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
        dm.whereIsWaldo(102, waldoFile, "doNotStacked()");
        goodToGo = true;
        int casesInStruct = dm.getNCasesInStruct();
        
        if (casesInStruct == 0) {
            MyAlerts.showAintGotNoDataAlert();
            return false;
        }
        
        anova1_Quant_NS_Dialog = new ANOVA1_Quant_NotStacked_Dialog( dm );
        anova1_Quant_NS_Dialog.show_ANOVA1_NS_Dialog();
        returnStatus = anova1_Quant_NS_Dialog.getReturnStatus();
        
        goodToGo = returnStatus.equals("OK"); 
        if (!goodToGo) { return false; }
        // else...
        explVarDescr = anova1_Quant_NS_Dialog.getExplanatoryVariable();
        respVarDescr = anova1_Quant_NS_Dialog.getResponseVariable();
        
        int nColumnsOfData = anova1_Quant_NS_Dialog.getNLevels();

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
        anova1_ColsOfData = anova1_Quant_NS_Dialog.getData();
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
        dm.whereIsWaldo(153, waldoFile, " *** doTheANOVA()");
        allTheLabels = new ArrayList<>();
        
        for (int iVars = 0; iVars < n_QDVs; iVars++) {
            allTheLabels.add(allTheQDVs.get(iVars).getTheVarLabel());
        }

        // The QDVs already have labels -- dump allTheLabels & get from QDV?
        anova1_Quant_Model = new ANOVA1_Quant_Model(this, explVarDescr, respVarDescr, allTheQDVs);
        String anovaOK = anova1_Quant_Model.continueInitializing();
        
        if (!anovaOK.equals("OK")) { return false;  }
        
        anova1_Quant_Dashboard = new ANOVA1_Quant_Dashboard(this, anova1_Quant_Model);
        anova1_Quant_Dashboard.populateTheBackGround();
        anova1_Quant_Dashboard.putEmAllUp();
        anova1_Quant_Dashboard.showAndWait();
        returnStatus = anova1_Quant_Dashboard.getReturnStatus();
        returnStatus = "OK";
        return true;        
    } 
    
    public void setStackedOrSeparate(String toThis) {
        stackedOrSeparate = toThis;
    }
    
    private boolean validateStackChoices() {
        dm.whereIsWaldo(180, waldoFile, "validateStackChoices()");
        isNumeric = new boolean[2];
        
        for (int ithCol = 0; ithCol < 2; ithCol++){
            isNumeric[ithCol] = anova1_ColsOfData.get(ithCol).getIsNumeric();  
        }
        return true;
    }
    
    private void askAboutReOrdering() {
        System.out.println("  *** 202 MultUni_Model, askAboutReOrdering()");
        n_QDVs = incomingQDVs.size();
        theNewOrder = new int[n_QDVs];
        // Default
        for (int ithQDV= 0; ithQDV < n_QDVs; ithQDV++) {
            theNewOrder[ithQDV] = ithQDV;
        }
        incomingLabels = new String[n_QDVs];
        for (int iVars = 0; iVars < n_QDVs; iVars++) {
            incomingLabels[iVars] = incomingQDVs.get(iVars).getTheVarLabel();
        }        
        
        reOrderStrings_Dialog = new ReOrderStringDisplay_Dialog(this, incomingLabels);
        reOrderStrings_Dialog.showAndWait();

        allTheQDVs = new ArrayList<>();
        for (int ithQDV = 0; ithQDV < n_QDVs; ithQDV++) {
            allTheQDVs.add(incomingQDVs.get(theNewOrder[ithQDV]));
        }

        collectAllTheLabels(); 
    }
    
    private void collectAllTheLabels() {
        varLabels = FXCollections.observableArrayList();         
        for (int iVars = 0; iVars < n_QDVs; iVars++) {
            varLabels.add(allTheQDVs.get(iVars).getTheVarLabel());
        }
    }
    
    public void closeTheReOrderDialog(int[] returnedOrder) {
        System.arraycopy(returnedOrder, 0, theNewOrder, 0, n_QDVs);
        reOrderStrings_Dialog.close();
    } 
    
    public ObservableList <String> getVarLabels() { 
        return varLabels; 
    }
    
    public Data_Manager getDataManager() {return dm; }
}