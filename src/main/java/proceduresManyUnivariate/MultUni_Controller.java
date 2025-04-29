/**************************************************
 *                MultiUni_Controller             *
 *                    02/16/25                    *
 *                     00:00                      *
 *************************************************/
package proceduresManyUnivariate;

import dataObjects.CatQuantDataVariable;
import dataObjects.CatQuantPair;
import dataObjects.ColumnOfData;
import dataObjects.QuantitativeDataVariable;
import dialogs.*;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import splat.*;
import utilityClasses.MyAlerts;
import utilityClasses.MyYesNoAlerts;

public class MultUni_Controller {
    
    //  POJOs
    public boolean checkForLegalChoices, goodToGo;
    public boolean[] isNumeric;
    public int n_QDVs, TWO; 
    public int[] theNewOrder;
    public String strReturnStatus, daProcedure, tidyOrTI8x, 
                  firstVarDescr, secondVarDescr,
                  subTitle_And, subTitle_Vs;
    public String genVarDescr, varLabel, varDescr;
    String explanatoryVariable, responseVariable;
    public ArrayList<String> allTheLabels;
    private ObservableList<String> varLabels;
    
    // Make empty if no-print
    //String waldoFile = "MultUni_Controller";
    String waldoFile = "";
    
    String[] incomingLabels;
 
    // My classes
    ColumnOfData colOfCats, colOfQuants;
    ArrayList<CatQuantPair> catQuantPairs;
    public ArrayList<ColumnOfData> al_MultUni_ColOfData;
    public ArrayList<String> arStr_VarLabels;
    public CatQuantDataVariable cqdv;
    public QuantitativeDataVariable tempQDV, qdv_Pooled ;
    public ArrayList<QuantitativeDataVariable> incomingQDVs, allTheQDVs;
    
    public Data_Manager dm;
    
    private MultUni_DotPlotModel multUni_DotPlotModel;
    private MultUni_Model multUni_Model;
    private MultUni_Dashboard multUni_Dashboard;
    MultUni_Tidy_Dialog multUni_Tidy_Dialog;
    private MultUni_TI8x_Dialog multUni_TI8x_Dialog;
    ReOrderStringDisplay_Dialog reOrderStrings_Dialog;
    public MyYesNoAlerts myYesNoAlerts;
    // POJOs / FX

    public MultUni_Controller(Data_Manager dm) {
        this.dm = dm;
        dm.whereIsWaldo(63, waldoFile, " *** MultUni_Controller, Constructing");
        strReturnStatus = "OK";
        al_MultUni_ColOfData = new ArrayList();
        arStr_VarLabels = new ArrayList();
        TWO = 2;
        strReturnStatus = "OK";
        myYesNoAlerts = new MyYesNoAlerts();
    }

    public String doTidyOrNot() {
        dm.whereIsWaldo(73, waldoFile, " --- doTidyOrNot()");
        //  Check for cancel above ( = not NULL)
        myYesNoAlerts.setTheYes("Tidy");
        myYesNoAlerts.setTheNo("TI8x");
        myYesNoAlerts.showTidyOrTI8xAlert();
        tidyOrTI8x = myYesNoAlerts.getYesOrNo();
        if (tidyOrTI8x.equals("Cancel")) { return "Cancel"; }
        dm.setTIorTIDY(tidyOrTI8x);

        //              First time through                 Repeat
        if (tidyOrTI8x.equals("Yes") || tidyOrTI8x.equals("Tidy")) { // = Tidy
            tidyOrTI8x = "Tidy";
            dm.setTIorTIDY("Tidy");
            strReturnStatus = doTidy(); 
            if (strReturnStatus.equals("Cancel")) { return "Cancel"; }
        } else {    // No = TI8x
            tidyOrTI8x = "TI8x";
            dm.setTIorTIDY("TI8x");
            doTI8x(); 
        } 
        
        return "OK";
    }
    
    protected String doTidy() {
        dm.whereIsWaldo(98, waldoFile, " --- doTidy()");
        do {
            int casesInStruct = dm.getNCasesInStruct();            
            if (casesInStruct == 0) {
                MyAlerts.showAintGotNoDataAlert();
                return "Cancel";
            }
            
            multUni_Tidy_Dialog = new MultUni_Tidy_Dialog( dm);
            multUni_Tidy_Dialog.showAndWait();
            strReturnStatus = multUni_Tidy_Dialog.getReturnStatus();
            if (strReturnStatus.equals("Cancel")) { return "Cancel"; }
            goodToGo = multUni_Tidy_Dialog.getGoodToGO();
            
            if (!goodToGo) {
                strReturnStatus = multUni_Tidy_Dialog.getReturnStatus();
                return "Cancel";
            }
            
            al_MultUni_ColOfData = multUni_Tidy_Dialog.getData();
            int nLevels = al_MultUni_ColOfData.get(0).getNumberOfDistinctValues();

            if (nLevels < TWO) {
                MyAlerts.showMultUni_LT2_VariablesAlert();
                goodToGo = false;
                return "Cancel";
            }
            
            checkForLegalChoices = validateTidyChoices();
        } while (!checkForLegalChoices);
        
        firstVarDescr = multUni_Tidy_Dialog.getPreferredFirstVarDescription();
        secondVarDescr = multUni_Tidy_Dialog.getPreferredSecondVarDescription();
        subTitle_And = firstVarDescr  + " & " + secondVarDescr;  
        subTitle_Vs = firstVarDescr  + " vs " + secondVarDescr;
  
        //                                Categorical,             Quantitative            return All and individuals
        cqdv = new CatQuantDataVariable(dm, al_MultUni_ColOfData.get(0), al_MultUni_ColOfData.get(1), true, "ANOVA1_Cat_Controller");   
        strReturnStatus = cqdv.finishConstructingTidy();
        
        if(strReturnStatus.equals("OK")) { 
            allTheQDVs = new ArrayList();
            allTheQDVs = cqdv.getAllQDVs();
            /******************************************************
             *  The qdv_Pooled is a qdv of the pooled allTheQDVs  *
             *****************************************************/
            qdv_Pooled = allTheQDVs.get(0);
            allTheQDVs.remove(0);   // Dump the All qdv
            n_QDVs = allTheQDVs.size();

            collectAllTheLabels();
            prepareTheStructs();
            dm.whereIsWaldo(150, waldoFile, " --- END doTidy()");
            return "OK";
        }
        dm.whereIsWaldo(153, waldoFile, " --- END doTidy()");
        return "Cancel";
    }

    protected String doTI8x() {
        dm.whereIsWaldo(158, waldoFile, "doTI8x()");
        strReturnStatus = "OK";
        multUni_TI8x_Dialog = new MultUni_TI8x_Dialog( dm );
        multUni_TI8x_Dialog.show_NS_Dialog();
        strReturnStatus = multUni_TI8x_Dialog.getReturnStatus();
        
        if (!strReturnStatus.equals("OK")) { return strReturnStatus; }
        // else...
        genVarDescr = multUni_TI8x_Dialog.getSubTitle();
        // Get the arrayList of ColumnsOfData
        al_MultUni_ColOfData = multUni_TI8x_Dialog.getData();
        int nColumnsOfData = al_MultUni_ColOfData.size();
        // Stack the columns into one, put in allTheQDVs[0]
        // Construct a ColumnOfData, make the QDV
        varLabel = "All";
        varDescr = "All";
        ArrayList<String> tempAlStr = new ArrayList<>();
        dm.whereIsWaldo(175, waldoFile, "doTI8x()");
     
        colOfCats = new ColumnOfData();
        colOfQuants = new ColumnOfData();   
        colOfCats.setVarLabel("CatLabel");
        colOfQuants.setVarLabel("QuantLabel");
        colOfCats.setVarDescription("CatDescr");
        colOfQuants.setVarDescription("QuantDescr");
        dm.whereIsWaldo(183, waldoFile, "doTI8x()");
    // Build Columns for the catQuantDataVariable
        for (int ith = 0; ith < nColumnsOfData; ith++) {
            ColumnOfData tempCol = al_MultUni_ColOfData.get(ith);
            String catValue = tempCol.getVarLabel();
            int nColSize = tempCol.getColumnSize();
            for (int jth = 0; jth < nColSize; jth++) {
                tempAlStr.add(tempCol.getTheCases_ArrayList().get(jth));                
                colOfCats.addNCasesOfThese(1, catValue);
                colOfQuants.addNCasesOfThese(1, tempCol.getTheCases_ArrayList().get(jth));
            }
        }
        
        cqdv = new CatQuantDataVariable(dm, colOfCats, colOfQuants, true, "MultUni_Controller");
        cqdv.finishConstructingTidy();
        cqdv.sortByQuantValue();
        catQuantPairs = new ArrayList();
        catQuantPairs = cqdv.getCatQuantPairs();
        ColumnOfData tempCOD = new ColumnOfData(dm, varLabel, varDescr, tempAlStr);
        tempQDV = new QuantitativeDataVariable(varLabel, varDescr, tempCOD);
        incomingQDVs = new ArrayList();
        for (int ith = 0; ith < nColumnsOfData; ith++) {
            varLabel = al_MultUni_ColOfData.get(ith).getVarLabel();
            varDescr = al_MultUni_ColOfData.get(ith).getVarDescription();
            tempQDV = new QuantitativeDataVariable(varLabel, varDescr, al_MultUni_ColOfData.get(ith)); 
            incomingQDVs.add(tempQDV);                 
        } 
        askAboutReOrdering();      
        prepareTheStructs();
        return strReturnStatus;
    }
    
    protected String prepareTheStructs() {  // for the MultUniModel
        dm.whereIsWaldo(216, waldoFile, "prepareTheStructs()");
        n_QDVs = allTheQDVs.size();
        allTheLabels = new ArrayList<>();
        
        for (int iVars = 0; iVars < n_QDVs; iVars++) {
            allTheLabels.add(allTheQDVs.get(iVars).getTheVarLabel());
        }
        
        n_QDVs = allTheQDVs.size(); 
        dm.whereIsWaldo(225, waldoFile, "prepareTheStructs()");        
        multUni_Model = new MultUni_Model(this, 
                                          genVarDescr,  // subTitle 
                                          allTheQDVs); 
        multUni_DotPlotModel = new MultUni_DotPlotModel(genVarDescr, cqdv);
        multUni_Dashboard = new MultUni_Dashboard(this, multUni_Model);
        multUni_Dashboard.populateTheBackGround();
        multUni_Dashboard.putEmAllUp();
        multUni_Dashboard.showAndWait();
        strReturnStatus = multUni_Dashboard.getReturnStatus();
        strReturnStatus = "Ok";
        return strReturnStatus;        
    }

    private boolean validateTidyChoices() {
        isNumeric = new boolean[TWO];
        
        for (int ithCol = 0; ithCol < TWO; ithCol++){
            isNumeric[ithCol] = al_MultUni_ColOfData.get(ithCol).getDataType().equals("Quantitative");  
        }
        return true;
    }
    
    private void askAboutReOrdering() {
        n_QDVs = incomingQDVs.size();
        theNewOrder = new int[n_QDVs];
        // Default
        for (int ithQDV = 0; ithQDV < n_QDVs; ithQDV++) {
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
    
    public void copyTheReOrderDialog(int[] returnedOrder) {
        System.arraycopy(returnedOrder, 0, theNewOrder, 0, n_QDVs);
        reOrderStrings_Dialog.close();
    }  
    
    public void setTidyOrTI8x(String toThis) {
        tidyOrTI8x = toThis;
    }
    
    public ArrayList<CatQuantPair> getCatQuantPairs() {return catQuantPairs; }    
    public String getVarDescr() { return genVarDescr; }    
    
    public String getExplanatoryVariable() { return explanatoryVariable; }
    public String getResponseVariable() { return responseVariable; }
    public Data_Manager getDataManager() { return dm; }  
    
    public MultUni_DotPlotModel getMultUni_DotPlotModel() { 
        return multUni_DotPlotModel; 
    }
    
    public String getReturnStatus() { return strReturnStatus; }
}