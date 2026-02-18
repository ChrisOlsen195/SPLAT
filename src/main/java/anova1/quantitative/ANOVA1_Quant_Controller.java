/**************************************************
 *            ANOVA1_Quant_Controller             *
 *                    02/14/25                    *
 *                     15:00                      *
 *************************************************/
package anova1.quantitative;

import superClasses.ANOVA1_Controller;
import dataObjects.CatQuantDataVariable;
import dataObjects.QuantitativeDataVariable;
import dialogs.*;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import splat.*;
import utilityClasses.MyAlerts;
import utilityClasses.MyYesNoAlerts;

public class ANOVA1_Quant_Controller extends ANOVA1_Controller {
    //  POJOs
    boolean[] theStringIsNumeric;
    public int[] theNewOrder;
    
    String thisVarLabel, thisVarDescr, tidyOrTI8x, strReturnStatus;
    String[] incomingLabels;
    
    // Make empty if no-print
    //String waldoFile = "ANOVA1_Quant_Controller";
    String waldoFile = "";
    
    public ObservableList<String> varLabels;
    boolean[] variableIsNumeric;
    private ANOVA1_Quant_Model anova1_Quant_Model;
    private ANOVA1_Quant_Dashboard anova1_Quant_Dashboard;
    ANOVA1_Quant_TI8x_Dialog anova1_Quant_TI8x_Dialog;
    ANOVA1_Quant_Tidy_Dialog anova1_Quant_Tidy_Dialog;
    ReOrderStringDisplay_Dialog reOrderStrings_Dialog;
    public MyYesNoAlerts myYesNoAlerts;
    
    public ANOVA1_Quant_Controller(Data_Manager dm) {
        super(dm);
        this.dm = dm;
        dm.whereIsWaldo(43, waldoFile, "Constructing");
        anova1_ColsOfData = new ArrayList();
        strReturnStatus = "OK";
        myYesNoAlerts = new MyYesNoAlerts();
    }
    
    public String doTidyOrTI8x() {
        dm.whereIsWaldo(50, waldoFile, " --- doTidyOrNot()");
        //  Check for existing value ( = not NULL)
        tidyOrTI8x = dm.getTIorTIDY();
        // If tidyOrTI8x not known, find out
        if (tidyOrTI8x.equals("NULL")) {
            myYesNoAlerts.setTheYes("Tidy");
            myYesNoAlerts.setTheNo("TI8x");
            myYesNoAlerts.showTidyOrTI8xAlert();
            // Get the Alert Yes/No = 'Yes' or 'No' and re-cast tidyOrTI8x
            tidyOrTI8x = myYesNoAlerts.getYesOrNo();
            if (tidyOrTI8x == null) { return "Cancel"; }
            dm.setTIorTIDY(tidyOrTI8x);
        }

        //              First time through                 Repeat
        if (tidyOrTI8x.equals("Yes") || tidyOrTI8x.equals("Tidy")) { // = Tidy
            tidyOrTI8x = "Tidy";
            dm.setTIorTIDY("Tidy");
            doTidy(); 
        } else {    // No = TI8x
            tidyOrTI8x = "TI8x";
            dm.setTIorTIDY("TI8x");
            doTI8x(); 
        } 
        return "OK";
    }

    protected String doTidy() {
        dm.whereIsWaldo(78, waldoFile, "doTidy()");
        do {
            int casesInStruct = dm.getNCasesInStruct();            
            if (casesInStruct == 0) {
                MyAlerts.showAintGotNoDataAlert();
                return "Cancel";
            }
            
            anova1_Quant_Tidy_Dialog = new ANOVA1_Quant_Tidy_Dialog( dm, "Categorical" );
            anova1_Quant_Tidy_Dialog.showAndWait();
            super.strReturnStatus = anova1_Quant_Tidy_Dialog.getStrReturnStatus();
            if (super.strReturnStatus.equals("Cancel")) {return "Cancel";}
            
            anova1_ColsOfData = anova1_Quant_Tidy_Dialog.getData();
            int nLevels = anova1_ColsOfData.get(0).getNumberOfDistinctValues();
            if (nLevels < 3) {
                MyAlerts.showAnova1_LT3_LevelsAlert();
                goodToGo = false;
                return "Cancel";
            }
            
            checkForLegalChoices = validateTidyChoices();
        } while (!checkForLegalChoices);
        
        dm.whereIsWaldo(102, waldoFile, "doTidy()");
        explVarDescr = anova1_Quant_Tidy_Dialog.getPreferredFirstVarDescription();
        respVarDescr = anova1_Quant_Tidy_Dialog.getPreferredSecondVarDescription();
  
        //                                Categorical,             Quantitative            return All and individuals
        cqdv = new CatQuantDataVariable(dm, anova1_ColsOfData.get(0), anova1_ColsOfData.get(1), true, "ANOVA1_Cat_Controller");   
        strReturnStatus = cqdv.finishConstructingTidy();
        if(strReturnStatus.equals("Cancel")) { return "Cancel"; } 
        if(strReturnStatus.equals("OK")) { 
            allTheQDVs = new ArrayList();
            allTheQDVs = cqdv.getAllQDVs();
            allTheQDVs.remove(0);   // Dump the All qdv
            n_QDVs = allTheQDVs.size();
            
            collectAllTheLabels();            
            doTheANOVA();
            return "OK";
        }
        return "Cancel";
    }

    protected String doTI8x() {
        dm.whereIsWaldo(125, waldoFile, "doTI8x()");
        goodToGo = true;
        int casesInStruct = dm.getNCasesInStruct();
        
        if (casesInStruct == 0) {
            MyAlerts.showAintGotNoDataAlert();
            return "Cancel";
        }
        
        anova1_Quant_TI8x_Dialog = new ANOVA1_Quant_TI8x_Dialog( dm );
        anova1_Quant_TI8x_Dialog.show_ANOVA1_TI8x_Dialog();
        strReturnStatus = anova1_Quant_TI8x_Dialog.getStrReturnStatus();
        
        goodToGo = strReturnStatus.equals("OK"); 
        if (!goodToGo) { return "Cancel"; }
        // else...
        explVarDescr = anova1_Quant_TI8x_Dialog.getExplanatoryVariable();
        respVarDescr = anova1_Quant_TI8x_Dialog.getResponseVariable();
        
        int nColumnsOfData = anova1_Quant_TI8x_Dialog.getNLevels();

        if (nColumnsOfData == 0) { 
            goodToGo = false;
            super.strReturnStatus = "Cancel";            
            return "Cancel"; 
        }
        
        if (nColumnsOfData < 3) {
            MyAlerts.showAnova1_LT3_LevelsAlert();
            goodToGo = false;
            return "Cancel";
        }
        
        // else...
        anova1_ColsOfData = anova1_Quant_TI8x_Dialog.getData();
        incomingQDVs = new ArrayList();

        for (int ith = 0; ith < nColumnsOfData; ith++) {
            thisVarLabel = anova1_ColsOfData.get(ith).getVarLabel();
            thisVarDescr = anova1_ColsOfData.get(ith).getVarDescription();
            tempQDV = new QuantitativeDataVariable(thisVarLabel, thisVarDescr, anova1_ColsOfData.get(ith));  
            incomingQDVs.add(tempQDV);                 
        } 
        
        n_QDVs = incomingQDVs.size();
        strReturnStatus = askAboutReOrdering();
        if (strReturnStatus.equals("Cancel")) { return "Cancel"; }
        doTheANOVA();

        dm.whereIsWaldo(174, waldoFile, " --- END doTI8x()");
        return strReturnStatus;
    }
    
    protected String doTheANOVA() {
        dm.whereIsWaldo(179, waldoFile, " *** doTheANOVA()");
        allTheLabels = new ArrayList<>();
        
        for (int iVars = 0; iVars < n_QDVs; iVars++) {
            allTheLabels.add(allTheQDVs.get(iVars).getTheVarLabel());
        }

        // The QDVs already have labels -- dump allTheLabels & get from QDV?
        anova1_Quant_Model = new ANOVA1_Quant_Model(this, explVarDescr, respVarDescr, allTheQDVs);
        String anovaOK = anova1_Quant_Model.continueInitializing();
        
        if (!anovaOK.equals("OK")) { return "OK";  }
        
        anova1_Quant_Dashboard = new ANOVA1_Quant_Dashboard(this, anova1_Quant_Model);
        anova1_Quant_Dashboard.populateTheBackGround();
        anova1_Quant_Dashboard.putEmAllUp();
        anova1_Quant_Dashboard.showAndWait();
        strReturnStatus = anova1_Quant_Dashboard.getStrReturnStatus();
        strReturnStatus = "OK";
        dm.whereIsWaldo(198, waldoFile, " END doTheANOVA()");
        return "OK";       
    } 
    
    public void setTidyOrTI8x(String toThis) {
        tidyOrTI8x = toThis;
    }
    
    private boolean validateTidyChoices() {
        dm.whereIsWaldo(207, waldoFile, " --- validateTidyChoices()");
        theStringIsNumeric = new boolean[2];
        
        for (int ithCol = 0; ithCol < 2; ithCol++){
            theStringIsNumeric[ithCol] = anova1_ColsOfData.get(ithCol).getDataType().equals("Quantitative");  
        }
        return true;
    }
    
    private String askAboutReOrdering() {
        dm.whereIsWaldo(217, waldoFile, "  --- askAboutReOrdering()");
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
        strReturnStatus = reOrderStrings_Dialog.getStrReturnStatus();
        if (strReturnStatus.equals("Cancel")) { return "Cancel"; }
        allTheQDVs = new ArrayList<>();
        for (int ithQDV = 0; ithQDV < n_QDVs; ithQDV++) {
            allTheQDVs.add(incomingQDVs.get(theNewOrder[ithQDV]));
        }

        collectAllTheLabels(); 
        return strReturnStatus;
    }
    
    private void collectAllTheLabels() {
        varLabels = FXCollections.observableArrayList();         
        for (int iVars = 0; iVars < n_QDVs; iVars++) {
            varLabels.add(allTheQDVs.get(iVars).getTheVarLabel());
        }
    }
    
    public void copyTheReOrder(int[] returnedOrder) {
        System.arraycopy(returnedOrder, 0, theNewOrder, 0, n_QDVs);
        reOrderStrings_Dialog.close();
    }  
    
    public ObservableList <String> getVarLabels() {  return varLabels; }
    
    public Data_Manager getDataManager() {return dm; }
}