/**************************************************
 *                MultiUni_Controller             *
 *                    11/27/24                    *
 *                     12:00                      *
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

public class MultUni_Controller {
    
    //  POJOs
    public boolean checkForLegalChoices;
    public boolean[] isNumeric;
    public int n_QDVs; 
    public int[] theNewOrder;
    public String returnStatus, daProcedure, stackedOrSeparate;
    public String genVarDescr, varLabel, varDescr;
    String explanatoryVariable, responseVariable;
    public ArrayList<String> allTheLabels;
    private ObservableList<String> varLabels;
    
    // Make empty if no-print
    String waldoFile = "MultUni_Controller";
    //String waldoFile = "";
    
    String[] incomingLabels;
 
    // My classes
    ColumnOfData colOfCats, colOfQuants;
    ArrayList<CatQuantPair> catQuantPairs;
    public ArrayList<ColumnOfData> al_ColOfData;
    public ArrayList<String> arStr_VarLabels;
    public CatQuantDataVariable cqdv;
    public QuantitativeDataVariable tempQDV;
    public ArrayList<QuantitativeDataVariable> incomingQDVs, allTheQDVs;
    
    public Data_Manager dm;
    
    private MultUni_DotPlotModel multUni_DotPlotModel;
    private MultUni_Model multUni_Model;
    private MultUni_Dashboard multUni_Dashboard;
    MultUni_Stacked_Dialog multUni_S_Dialog;
    private MultUni_NotStackedDialog multUni_NS_Dialog;
    ReOrderStringDisplay_Dialog reOrderStrings_Dialog;
    
    // POJOs / FX

    public MultUni_Controller(Data_Manager dm) {
        this.dm = dm;
        dm.whereIsWaldo(59, waldoFile, "\nConstructing with dm");
        al_ColOfData = new ArrayList();
        arStr_VarLabels = new ArrayList();
        returnStatus = "OK";
    }
    
    public void doStackedOrNot() {
        DataChoice_StackedOrNot sed = new DataChoice_StackedOrNot(this);
        // stackedOrSeparate has been updated by the dialog
        if (stackedOrSeparate.equals("Group & Data")) { doStacked(); }        
        if (stackedOrSeparate.equals("TI8x-Like")) { doNotStacked(); }         
        if (stackedOrSeparate.equals("Bailed")) {  /* No op */  }
    }      
    
    protected String doStacked() {
        dm.whereIsWaldo(74, waldoFile, "doStacked()");
        returnStatus = "OK";
        do {
            // multUni_S_Dialog's superclass is Two_Variables_Dialog
            multUni_S_Dialog = new MultUni_Stacked_Dialog( dm );
            multUni_S_Dialog.showAndWait();   
            
            if (multUni_S_Dialog.getReturnStatus().equals("Cancel")) {
                return "Cancel";
            }  
            
            al_ColOfData = multUni_S_Dialog.getData();
            checkForLegalChoices = validateStackChoices();
        } while (!checkForLegalChoices);
        //                                Categorical,             Quantitative               Return All and individuals
        cqdv = new CatQuantDataVariable(dm, al_ColOfData.get(0), al_ColOfData.get(1), true, "MultUni_Controller");   
        returnStatus = cqdv.finishConstructingStacked();
        
        if (returnStatus.equals("OK")) {
            incomingQDVs = new ArrayList();
            incomingQDVs = cqdv.getAllQDVs();

            askAboutReOrdering();
            explanatoryVariable = multUni_S_Dialog.getPreferredFirstVarDescription();
            responseVariable = multUni_S_Dialog.getPreferredSecondVarDescription();
            prepareTheStructs();
            return returnStatus;    //  return good
        }
        return returnStatus;    //  return bad
    }

    protected String doNotStacked() {
        dm.whereIsWaldo(106, waldoFile, "doNotStacked()");
        returnStatus = "OK";
        multUni_NS_Dialog = new MultUni_NotStackedDialog( dm );
        multUni_NS_Dialog.show_NS_Dialog();
        returnStatus = multUni_NS_Dialog.getReturnStatus();
        
        if (!returnStatus.equals("OK")) { return returnStatus; }
        // else...
        genVarDescr = multUni_NS_Dialog.getSubTitle();
        // Get the arrayList of ColumnsOfData
        al_ColOfData = multUni_NS_Dialog.getData();
        int nColumnsOfData = al_ColOfData.size();
        // Stack the columns into one, put in allTheQDVs[0]
        // Construct a ColumnOfData, make the QDV
        varLabel = "All";
        varDescr = "All";
        ArrayList<String> tempAlStr = new ArrayList<>();
        dm.whereIsWaldo(123, waldoFile, "doNotStacked()");
     
        colOfCats = new ColumnOfData();
        colOfQuants = new ColumnOfData();   
        colOfCats.setVarLabel("CatLabel");
        colOfQuants.setVarLabel("QuantLabel");
        colOfCats.setVarDescription("CatDescr");
        colOfQuants.setVarDescription("QuantDescr");
        dm.whereIsWaldo(131, waldoFile, "doingNotStacked()");
    // Build Columns for the catQuantDataVariable
        for (int ith = 0; ith < nColumnsOfData; ith++) {
            dm.whereIsWaldo(134, waldoFile, "Looping");
            ColumnOfData tempCol = al_ColOfData.get(ith);
            String catValue = tempCol.getVarLabel();
            int nColSize = tempCol.getColumnSize();
            for (int jth = 0; jth < nColSize; jth++) {
                tempAlStr.add(tempCol.getTheCases_ArrayList().get(jth));                
                colOfCats.addNCasesOfThese(1, catValue);
                colOfQuants.addNCasesOfThese(1, tempCol.getTheCases_ArrayList().get(jth));
            }
        }
        
        cqdv = new CatQuantDataVariable(dm, colOfCats, colOfQuants, true, "MultUni_Controller");
        cqdv.finishConstructingStacked();
        cqdv.sortByQuantValue();
        catQuantPairs = new ArrayList();
        catQuantPairs = cqdv.getCatQuantPairs();
        ColumnOfData tempCOD = new ColumnOfData(dm, varLabel, varDescr, tempAlStr);
        tempQDV = new QuantitativeDataVariable(varLabel, varDescr, tempCOD);
        incomingQDVs = new ArrayList();
        for (int ith = 0; ith < nColumnsOfData; ith++) {
            varLabel = al_ColOfData.get(ith).getVarLabel();
            varDescr = al_ColOfData.get(ith).getVarDescription();
            tempQDV = new QuantitativeDataVariable(varLabel, varDescr, al_ColOfData.get(ith)); 
            incomingQDVs.add(tempQDV);                 
        } 
        askAboutReOrdering();      
        prepareTheStructs();
        return returnStatus;
    }
    
    protected String prepareTheStructs() {  // for the MultUniModel
        dm.whereIsWaldo(165, waldoFile, "prepareTheStructs()");
        n_QDVs = allTheQDVs.size();
        allTheLabels = new ArrayList<>();
        
        for (int iVars = 0; iVars < n_QDVs; iVars++) {
            allTheLabels.add(allTheQDVs.get(iVars).getTheVarLabel());
        }
        
        n_QDVs = allTheQDVs.size(); 
        dm.whereIsWaldo(174, waldoFile, "prepareTheStructs()");        
        multUni_Model = new MultUni_Model(this, 
                                          genVarDescr,  // subTitle 
                                          allTheQDVs); 
        multUni_DotPlotModel = new MultUni_DotPlotModel(genVarDescr, cqdv);
        multUni_Dashboard = new MultUni_Dashboard(this, multUni_Model);
        multUni_Dashboard.populateTheBackGround();
        multUni_Dashboard.putEmAllUp();
        multUni_Dashboard.showAndWait();
        returnStatus = multUni_Dashboard.getReturnStatus();
        returnStatus = "Ok";
        return returnStatus;        
    }
    
    public void setStackedOrSeparate(String toThis) {
        stackedOrSeparate = toThis;
    }
    
    private boolean validateStackChoices() {
        isNumeric = new boolean[2];
        
        for (int ithCol = 0; ithCol < 2; ithCol++){
            isNumeric[ithCol] = al_ColOfData.get(ithCol).getIsNumeric();  
        }
        return true;
    }
    
    private void askAboutReOrdering() {
        System.out.println("202 MultUni_Controller, askAboutReOrdering()");
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
    
    public ArrayList<CatQuantPair> getCatQuantPairs() {return catQuantPairs; }    
    public String getVarDescr() { return genVarDescr; }    
    
    public String getExplanatoryVariable() { return explanatoryVariable; }
    public String getResponseVariable() { return responseVariable; }
    public Data_Manager getDataManager() { return dm; }  
    
    public MultUni_DotPlotModel getMultUni_DotPlotModel() { 
        return multUni_DotPlotModel; 
    }
    
    public String getReturnStatus() { return returnStatus; }
}
