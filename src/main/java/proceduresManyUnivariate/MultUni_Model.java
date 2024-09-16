/**************************************************
 *                  MultUni_Model                 *
 *                    09/03/24                    *
 *                      09:00                     *
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
    int n_QDVs;
    public int[] theNewOrder;
    private String subTitle;
    String strAxisLabels[];
    // Make empty if no-print
    //String waldoFile = "MultUni_Model";
    String waldoFile = "";

    ArrayList<PrintUStats_Model> printUStats_Models;
    private ObservableList<String> varLabels;

    // My classes
    Data_Manager dm;
    MultUni_Controller multUni_Controller;
    ArrayList<QuantitativeDataVariable> allTheQDVs;  
    QuantitativeDataVariable allData_QDV;

    public MultUni_Model (MultUni_Controller multUni_Controller, 
                                 String subTitle,
                                 ArrayList<QuantitativeDataVariable>  allTheQDVs) {
        dm = multUni_Controller.getDataManager();
        dm.whereIsWaldo(39, waldoFile, "\nConstructing");
        this.multUni_Controller = multUni_Controller;
        this.subTitle = subTitle;
        this.allTheQDVs = new ArrayList();
        this.allTheQDVs = allTheQDVs;
        n_QDVs = allTheQDVs.size();
        printUStats_Models = new ArrayList();
        varLabels = FXCollections.observableArrayList();
        for (int ithVar = 0; ithVar < n_QDVs; ithVar++) {
            String tempStr = allTheQDVs.get(ithVar).getTheVarLabel();
            varLabels.add(tempStr);
            printUStats_Models.add(new PrintUStats_Model(tempStr,
                                   allTheQDVs.get(ithVar), false));
        }
    }

    public ArrayList<QuantitativeDataVariable> getAllQDVs() { return allTheQDVs; }

    public QuantitativeDataVariable getIthQDV(int ith) {
        return allTheQDVs.get(ith);
    }
    
    public int getNVariables() {  return n_QDVs; }   
    public String getSubTitle() { return subTitle; }
    public ObservableList <String> getCatLabels() { return varLabels;}
   
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

