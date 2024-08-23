/**************************************************
 *                MultiUni_Controller             *
 *                    04/02/24                    *
 *                     09:00                      *
 *************************************************/
package proceduresManyUnivariate;

import dataObjects.CatQuantDataVariable;
import dataObjects.CatQuantPair;
import dataObjects.ColumnOfData;
import dataObjects.QuantitativeDataVariable;
import dialogs.*;
import java.util.ArrayList;
import splat.*;

public class MultUni_Controller {
    
    //  POJOs
    public boolean checkForLegalChoices;
    public boolean[] isNumeric;
    public int n_QDVs; 
    public String returnStatus, daProcedure, stackedOrSeparate;
    public String genVarDescr, varLabel, varDescr;
    String explanatoryVariable, responseVariable;
    public ArrayList<String> allTheLabels;
    
    // Make empty if no-print
    //String waldoFile = "MultUni_Controller";
    String waldoFile = "";
 
    // My classes
    ColumnOfData colOfCats, colOfQuants;
    ArrayList<CatQuantPair> catQuantPairs;
    public ArrayList<ColumnOfData> al_ColOfData;
    public ArrayList<String> arStr_VarLabels;
    public CatQuantDataVariable cqdv;
    public QuantitativeDataVariable tempQDV;
    public ArrayList<QuantitativeDataVariable> allTheQDVs;
    public Data_Manager dm;
    
    private MultUni_DotPlotModel multUni_DotPlotModel;
    private MultUni_Model multUni_Model;
    private MultUni_Dashboard multUni_Dashboard;
    MultUni_Stacked_Dialog multUni_S_Dialog;
    private MultUni_NotStackedDialog multUni_NS_Dialog;
    
    // POJOs / FX

    public MultUni_Controller(Data_Manager dm) {
        this.dm = dm;
        dm.whereIsWaldo(51, waldoFile, "Constructing with dm");
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
        dm.whereIsWaldo(66, waldoFile, "doStacked()");
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
            allTheQDVs = new ArrayList();
            allTheQDVs = cqdv.getAllQDVs();
            explanatoryVariable = multUni_S_Dialog.getPreferredFirstVarDescription();
            responseVariable = multUni_S_Dialog.getPreferredSecondVarDescription();
            prepareTheStructs();
            return returnStatus;    //  return good
        }
        return returnStatus;    //  return bad
    }

    protected String doNotStacked() {
        dm.whereIsWaldo(92, waldoFile, "doNotStacked()");
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
        dm.whereIsWaldo(111, waldoFile, "doNotStacked()");
     
        colOfCats = new ColumnOfData();
        colOfQuants = new ColumnOfData();   
        colOfCats.setVarLabel("CatLabel");
        colOfQuants.setVarLabel("QuantLabel");
        colOfCats.setVarDescription("CatDescr");
        colOfQuants.setVarDescription("QuantDescr");
        dm.whereIsWaldo(122, waldoFile, "doNotStacked()");
    // Build Columns for the catQuantDataVariable
        for (int ith = 0; ith < nColumnsOfData; ith++) {
            dm.whereIsWaldo(125, waldoFile, "Looping");
            ColumnOfData tempCol = al_ColOfData.get(ith);
            String catValue = tempCol.getVarLabel();
            int nColSize = tempCol.getColumnSize();
            for (int jth = 0; jth < nColSize; jth++) {
                tempAlStr.add(tempCol.getTheCases_ArrayList().get(jth));                
                colOfCats.addNCasesOfThese(1, catValue);
                colOfQuants.addNCasesOfThese(1, tempCol.getTheCases_ArrayList().get(jth));
            }
        }
        dm.whereIsWaldo(135, waldoFile, "doNotStacked()");        
        cqdv = new CatQuantDataVariable(dm, colOfCats, colOfQuants, true, "MultUni_Controller");
        dm.whereIsWaldo(137, waldoFile, "doNotStacked()"); 
        cqdv.finishConstructingStacked();
        cqdv.sortByQuantValue();
        dm.whereIsWaldo(140, waldoFile, "doNotStacked()");
        catQuantPairs = new ArrayList();
        catQuantPairs = cqdv.getCatQuantPairs();
        dm.whereIsWaldo(143, waldoFile, "doNotStacked()");
        ColumnOfData tempCOD = new ColumnOfData(dm, varLabel, varDescr, tempAlStr);
        tempQDV = new QuantitativeDataVariable(varLabel, varDescr, tempCOD);
        dm.whereIsWaldo(146, waldoFile, "doNotStacked()");
        allTheQDVs = new ArrayList();
        allTheQDVs.add(tempQDV);
        dm.whereIsWaldo(149, waldoFile, "doNotStacked()");
        for (int ith = 0; ith < nColumnsOfData; ith++) {
            varLabel = al_ColOfData.get(ith).getVarLabel();
            varDescr = al_ColOfData.get(ith).getVarDescription();
            tempQDV = new QuantitativeDataVariable(varLabel, varDescr, al_ColOfData.get(ith)); 
            allTheQDVs.add(tempQDV);                 
        } 
        dm.whereIsWaldo(156, waldoFile, "doNotStacked()");        
        prepareTheStructs();
        return returnStatus;
    }
    
    protected String prepareTheStructs() {  // for the MultUniModel
        dm.whereIsWaldo(162, waldoFile, "prepareTheStructs()");
        n_QDVs = allTheQDVs.size();
        allTheLabels = new ArrayList<>();
        
        for (int iVars = 0; iVars < n_QDVs; iVars++) {
            allTheLabels.add(allTheQDVs.get(iVars).getTheVarLabel());
        }

        allTheQDVs.remove(0);
        allTheLabels.remove(0);
        n_QDVs = allTheQDVs.size(); 
        dm.whereIsWaldo(173, waldoFile, "prepareTheStructs()");        
        multUni_Model = new MultUni_Model(this, 
                                          genVarDescr,  // subTitle 
                                          allTheQDVs, 
                                          allTheLabels); 
        multUni_DotPlotModel = new MultUni_DotPlotModel(genVarDescr, cqdv);
        dm.whereIsWaldo(179, waldoFile, "prepareTheStructs()");
        multUni_Dashboard = new MultUni_Dashboard(this, multUni_Model);
        multUni_Dashboard.populateTheBackGround();
        multUni_Dashboard.putEmAllUp();
        multUni_Dashboard.showAndWait();
        dm.whereIsWaldo(184, waldoFile, "prepareTheStructs()");
        returnStatus = multUni_Dashboard.getReturnStatus();
        returnStatus = "Ok";
        dm.whereIsWaldo(187, waldoFile, "end prepareTheStructs()");
        return returnStatus;        
    }
    
    public void setStackedOrSeparate(String toThis) {
        stackedOrSeparate = toThis;
    }
    
    private boolean validateStackChoices() {
        dm.whereIsWaldo(194, waldoFile, "validateStackChoices()");
        isNumeric = new boolean[2];
        
        for (int ithCol = 0; ithCol < 2; ithCol++){
            isNumeric[ithCol] = al_ColOfData.get(ithCol).getIsNumeric();  
        }
        return true;
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