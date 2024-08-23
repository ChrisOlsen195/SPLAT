/**************************************************
 *                 MultiUni_Model                 *
 *                    03/19/24                    *
 *                      12:00                     *
 *************************************************/
package proceduresManyUnivariate;

import dataObjects.CatQuantPair;
import dataObjects.QuantitativeDataVariable;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import proceduresOneUnivariate.PrintUStats_Model;
import splat.Data_Manager;

public class MultUni_Model {
    // POJOs
    int nVariables;
    
    private String subTitle;
    String strAxisLabels[];
    
    // Make empty if no-print
    //String waldoFile = "MultUni_Model";
    String waldoFile = "";

    ArrayList<PrintUStats_Model> printUStats_Models;
    private ObservableList<String> categoryLabels;

    // My classes
    Data_Manager dm;
    MultUni_Controller multUni_Controller;
    ArrayList<QuantitativeDataVariable> allTheQDVs;  
    QuantitativeDataVariable allData_QDV;

    public MultUni_Model (MultUni_Controller multUni_Controller, 
                                 String subTitle,
                                 ArrayList<QuantitativeDataVariable>  allTheQDVs,
                                 ArrayList<String> allTheLabels) {
        dm = multUni_Controller.getDataManager();
        dm.whereIsWaldo(41, waldoFile, "Constructing");
        this.subTitle = subTitle;
        this.multUni_Controller = multUni_Controller;
        categoryLabels = FXCollections.observableArrayList();
        this.allTheQDVs = new ArrayList();
        this.allTheQDVs = allTheQDVs;
             
        for (int ithLabel = 0; ithLabel < allTheLabels.size(); ithLabel++) {
            categoryLabels.add(allTheLabels.get(ithLabel));
        }
               
        //int nQDVs = allTheQDVs.size();
        allData_QDV = allTheQDVs.get(0);        
        nVariables = allTheQDVs.size();        
        printUStats_Models = new ArrayList();
        
        for (int ithVar = 0; ithVar < nVariables; ithVar++) {
            String tempStr = allTheQDVs.get(ithVar).getTheVarLabel();
            printUStats_Models.add(new PrintUStats_Model(tempStr,
                                   allTheQDVs.get(ithVar), false));
        }
    }
    
    public ArrayList<QuantitativeDataVariable> getAllQDVs() { return allTheQDVs; }

    public QuantitativeDataVariable getIthQDV(int ith) {
        return allTheQDVs.get(ith);
    }
   
    public int getNVariables() {  return nVariables; }   
    public String getSubTitle() { return subTitle; }
    public ObservableList <String> getCatLabels() { return categoryLabels;}
   
    public QuantitativeDataVariable getAllData_QDV() { return allData_QDV; }  
    public ArrayList<QuantitativeDataVariable> getAllTheQDVs() {
        return allTheQDVs; 
    }  
    
    public ArrayList<CatQuantPair> getCatQuantPairs() {
        return multUni_Controller.getCatQuantPairs(); 
    }
    ArrayList<PrintUStats_Model> getPrintUStatsModels() { 
        return printUStats_Models; 
    }    
    public String[] getAxisLabels() { return strAxisLabels; }    
    public Data_Manager getDataManager() { return dm; }
}

