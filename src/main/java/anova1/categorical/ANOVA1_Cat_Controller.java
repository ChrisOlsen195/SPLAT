/**************************************************
 *             ANOVA1_Cat_Controller              *
 *                    02/16/25                    *
 *                     00:00                      *
 *************************************************/
package anova1.categorical;

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

public class ANOVA1_Cat_Controller extends ANOVA1_Controller {
    //  POJOs
    
    public int[] theNewOrder;
    String thisVarLabel, thisVarDescr, tidyOrTI8x, strReturnStatus;
    String[] incomingLabels;
    // Make empty if no-print
    //String waldoFile = "ANOVA1_Cat_Controller";
    String waldoFile = "";

    public ObservableList<String> varLabels;
    boolean[] theStringIsNumeric;
    private ANOVA1_Cat_Model anova1_Cat_Model;
    private ANOVA1_Cat_Dashboard anova1_Cat_Dashboard;
    ANOVA1_Cat_TI8x_Dialog anova1_Cat_TI8x_Dialog;
    ANOVA1_Cat_Tidy_Dialog anova1_Cat_Tidy_Dialog;
    public ReOrderStringDisplay_Dialog reOrderStrings_Dialog;
    public MyYesNoAlerts myYesNoAlerts;
    
    public ANOVA1_Cat_Controller(Data_Manager dm) {
        super(dm);
        this.dm = dm;
        dm.whereIsWaldo(42, waldoFile, " *** Constructing");
        anova1_ColsOfData = new ArrayList();
        strReturnStatus = "OK";
        myYesNoAlerts = new MyYesNoAlerts();
    }
    
    public String doTidyOrTI8x() {
        dm.whereIsWaldo(49, waldoFile, " --- doTidyOrNot()");
        //  Check for existing value ( = not NULL)
        tidyOrTI8x = dm.getTIorTIDY();
        if (tidyOrTI8x.equals("NULL")) {
            myYesNoAlerts.setTheYes("Tidy");
            myYesNoAlerts.setTheNo("TI8x");
            myYesNoAlerts.showTidyOrTI8xAlert();
            // Get the Alert Yes/No = 'Yes' or 'No' and re-cast tidyOrTI8x
            tidyOrTI8x = myYesNoAlerts.getYesOrNo();
            if (tidyOrTI8x.equals("Cancel")) { return "Cancel"; }
            dm.whereIsWaldo(59, waldoFile, " --- doTidyOrNot()");
            if (tidyOrTI8x == null) { return "Cancel"; }
            dm.setTIorTIDY(tidyOrTI8x);
            dm.whereIsWaldo(62, waldoFile, " --- END doTidyOrNot()");
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
        dm.whereIsWaldo(79, waldoFile, " --- doTidy()");
        do {
            int casesInStruct = dm.getNCasesInStruct();            
            if (casesInStruct == 0) {
                MyAlerts.showAintGotNoDataAlert();
                return "Cancel";
            }
            
            anova1_Cat_Tidy_Dialog = new ANOVA1_Cat_Tidy_Dialog( dm, "Categorical" );
            anova1_Cat_Tidy_Dialog.showAndWait();
            strReturnStatus = anova1_Cat_Tidy_Dialog.getReturnStatus();
            if (strReturnStatus.equals("Cancel")) {return "Cancel";}
            
            anova1_ColsOfData = anova1_Cat_Tidy_Dialog.getData();
            int nLevels = anova1_ColsOfData.get(0).getNumberOfDistinctValues();
            if (nLevels < 3) {
                MyAlerts.showAnova1_LT3_LevelsAlert();
                goodToGo = false;
                return "Cancel";
            }
            
            checkForLegalChoices = validateTidyChoices();
        } while (!checkForLegalChoices);
        dm.whereIsWaldo(102, waldoFile, " --- doTidy()");
        explVarDescr = anova1_Cat_Tidy_Dialog.getPreferredFirstVarDescription();
        respVarDescr = anova1_Cat_Tidy_Dialog.getPreferredSecondVarDescription();
        //                                Categorical,             Quantitative            return All and individuals
        cqdv = new CatQuantDataVariable(dm, anova1_ColsOfData.get(0), anova1_ColsOfData.get(1), true, "ANOVA1_Cat_Controller");      
        if ((strReturnStatus == null) || (!strReturnStatus.equals("OK"))) { return "Cancel"; }       
        strReturnStatus = cqdv.finishConstructingTidy();
        if ((strReturnStatus == null) || (!strReturnStatus.equals("OK"))) { return "Cancel"; }
        
        if(strReturnStatus.equals("OK")) { 
            allTheQDVs = new ArrayList();
            allTheQDVs = cqdv.getAllQDVs();
            allTheQDVs.remove(0);   // Dump the All qdv
            n_QDVs = allTheQDVs.size();
            dm.whereIsWaldo(119, waldoFile, " --- doTidy()");
            collectAllTheLabels();
            doTheANOVA();
            return "OK";
        }
        return "Cancel";
    }

    protected String doTI8x() {
        dm.whereIsWaldo(125, waldoFile, " --- doTI8x()");
        goodToGo = true;
        int casesInStruct = dm.getNCasesInStruct();
        
        if (casesInStruct == 0) {
            MyAlerts.showAintGotNoDataAlert();
            return "Cancel";
        }
        
        anova1_Cat_TI8x_Dialog = new ANOVA1_Cat_TI8x_Dialog( dm );
        anova1_Cat_TI8x_Dialog.show_ANOVA1_TI8x_Dialog();
        strReturnStatus = anova1_Cat_TI8x_Dialog.getReturnStatus();
         
        if (!super.strReturnStatus.equals("OK")) { return "Cancel"; }
        // else...
        explVarDescr = anova1_Cat_TI8x_Dialog.getExplanatoryVariable();
        respVarDescr = anova1_Cat_TI8x_Dialog.getResponseVariable();
        
        int nColumnsOfData = anova1_Cat_TI8x_Dialog.getNLevels();

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
        anova1_ColsOfData = anova1_Cat_TI8x_Dialog.getData();
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

        dm.whereIsWaldo(175, waldoFile, " --- END doTI8x()");
        return strReturnStatus;
    }
    
    protected String doTheANOVA() {
        dm.whereIsWaldo(180, waldoFile, " --- doTheANOVA()");
        allTheLabels = new ArrayList<>();
        
        for (int iVars = 0; iVars < n_QDVs; iVars++) {
            allTheLabels.add(allTheQDVs.get(iVars).getTheVarLabel());
        }

        // The QDVs already have labels -- dump allTheLabels & get from QDV?
        anova1_Cat_Model = new ANOVA1_Cat_Model(this, explVarDescr, respVarDescr, allTheQDVs);
        String anovaOK = anova1_Cat_Model.continueInitializing();
        
        if (!anovaOK.equals("OK")) { return "Cancel";  }
        
        anova1_Cat_Dashboard = new ANOVA1_Cat_Dashboard(this, anova1_Cat_Model);
        anova1_Cat_Dashboard.populateTheBackGround();
        anova1_Cat_Dashboard.putEmAllUp();
        anova1_Cat_Dashboard.showAndWait();
        strReturnStatus = anova1_Cat_Dashboard.getReturnStatus();
        strReturnStatus = "OK";
        dm.whereIsWaldo(199, waldoFile, " END doTheANOVA()");
        return strReturnStatus;        
    } 
    
    public void setTidyOrTI8x(String toThis) { tidyOrTI8x = toThis; }
    
    private boolean validateTidyChoices() {
        dm.whereIsWaldo(209, waldoFile, " --- validateTidyChoices()");
        theStringIsNumeric = new boolean[2];
        
        for (int ithCol = 0; ithCol < 2; ithCol++){
            theStringIsNumeric[ithCol] = anova1_ColsOfData.get(ithCol).getDataType().equals("Quantitative");  
        }
        return true;
    }
    
    private String askAboutReOrdering() {
        dm.whereIsWaldo(219, waldoFile, "  --- askAboutReOrdering()");
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
        strReturnStatus = reOrderStrings_Dialog.getReturnStatus();
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
        
    public ObservableList <String> getVarLabels() { 
        return varLabels; 
    }
    
    public Data_Manager getDataManager() {return dm; }
}