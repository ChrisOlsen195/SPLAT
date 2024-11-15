/**************************************************
 *              HorizontalBoxPlot_Model           *
 *                    04/02/24                    *
 *                      06:00                     *
 *************************************************/
package proceduresManyUnivariate;

import the_t_procedures.Indep_t_PrepStructs;
import the_t_procedures.Single_t_Controller;
import dataObjects.QuantitativeDataVariable;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import proceduresTwoUnivariate.*;

public class HorizontalBoxPlot_Model {
    
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    int n_QDVs;
    private String strSubTitle, firstCharDescr, secondVarDescr;
    ArrayList<String> allTheVarLabels, allTheVarDescriptions;
    public ObservableList<String> categoryLabels;
    ArrayList<QuantitativeDataVariable> allTheQDVs;

    public HorizontalBoxPlot_Model(String descriptionOfVariable, QuantitativeDataVariable theQDV) {
        if (printTheStuff) {
            System.out.println("29 *** ----- HorizontalBoxPlot_Model, constructing");
        }
        allTheQDVs = new ArrayList<>();
        strSubTitle = descriptionOfVariable;
        allTheQDVs.add(theQDV);
        n_QDVs = allTheQDVs.size();
        collectAllTheLabels();
        // OK, this is Fox News
    }
    
    public HorizontalBoxPlot_Model(Single_t_Controller single_t_Controller, String descriptionOfVariable, QuantitativeDataVariable theQDV) {
        if (printTheStuff) {
            System.out.println("41 *** ----- HorizontalBoxPlot_Model, constructing");
        }
        allTheQDVs = new ArrayList<>();
        allTheQDVs.add(theQDV);
        n_QDVs = allTheQDVs.size();
        allTheVarDescriptions = new ArrayList<>();     
        allTheVarLabels.add(theQDV.getTheVarLabel()); 
        allTheVarLabels.add(theQDV.getTheVarLabel());
        allTheVarDescriptions.add(theQDV.getTheVarDescription()); 
        allTheVarDescriptions.add(theQDV.getTheVarDescription()); 
        strSubTitle = descriptionOfVariable;
    }

    public HorizontalBoxPlot_Model(Explore_2Ind_Controller explore_2Ind_Controller, String descriptionOfVariable, ArrayList<QuantitativeDataVariable> allTheQDVs) {
        if (printTheStuff) {
            System.out.println("56 *** ----- HorizontalBoxPlot_Model, constructing");
        }
        this.allTheQDVs = new ArrayList<>();
        this.allTheQDVs = allTheQDVs;
        n_QDVs = allTheQDVs.size();
        strSubTitle = descriptionOfVariable;
        firstCharDescr = explore_2Ind_Controller.getFirstVarDescr();
        secondVarDescr = explore_2Ind_Controller.getSecondVarDescr();
        categoryLabels = FXCollections.observableArrayList();
        categoryLabels.add(firstCharDescr);
        categoryLabels.add(secondVarDescr); 
    }
    
    // This constructor is for independent t
    public HorizontalBoxPlot_Model(Indep_t_PrepStructs indep_t_PrepStructs, String descriptionOfVariable, ArrayList<QuantitativeDataVariable> allTheQDVs) {
        if (printTheStuff) {
            System.out.println("72 *** ----- HorizontalBoxPlot_Model, constructing");
        }
        this.allTheQDVs = new ArrayList<>();
        this.allTheQDVs = allTheQDVs;
        n_QDVs = allTheQDVs.size();
        strSubTitle = descriptionOfVariable;
        firstCharDescr = indep_t_PrepStructs.getFirstVarDescription();
        secondVarDescr = indep_t_PrepStructs.getSecondVarDescription();  
        categoryLabels = FXCollections.observableArrayList();
        categoryLabels.add(firstCharDescr);
        categoryLabels.add(secondVarDescr);             
    }
    
        // This constructor is for ANCOVA -- Labels/Descr handled externally
    public HorizontalBoxPlot_Model(ArrayList<QuantitativeDataVariable> allTheQDVs) {
        if (printTheStuff) {
            System.out.println("88 *** ----- HorizontalBoxPlot_Model, constructing");
        }
        this.allTheQDVs = new ArrayList<>();
        this.allTheQDVs = allTheQDVs;
        n_QDVs = allTheQDVs.size();
        categoryLabels = FXCollections.observableArrayList(); 
        collectAllTheLabels();
    }
    
    public HorizontalBoxPlot_Model(MultUni_Model multUni_Model, ArrayList<QuantitativeDataVariable> allTheQDVs) {
        if (printTheStuff) {
            System.out.println("99 *** ----- HorizontalBoxPlot_Model, constructing");
        }
        this.allTheQDVs = new ArrayList<>();
        this.allTheQDVs = allTheQDVs;
        n_QDVs = allTheQDVs.size();
        strSubTitle = multUni_Model.getSubTitle();
        collectAllTheLabels();          
    }

    public String getFirstVarDescription() { return firstCharDescr; }
    public String getSecondVarDescription() { return secondVarDescr; } 
    
    private void collectAllTheLabels() {
        categoryLabels = FXCollections.observableArrayList(); 
        for (int iVars = 0; iVars < n_QDVs; iVars++) {
            categoryLabels.add(allTheQDVs.get(iVars).getTheVarLabel());
        }    
    }
   
    public String getSubTitle() { return strSubTitle; }
    public ObservableList <String> getCategoryLabels() { 
        return categoryLabels; 
    }
    public ArrayList<QuantitativeDataVariable> getAllTheQDVs() { 
        return allTheQDVs; 
    }
    public QuantitativeDataVariable getIthQDV(int ith) {
       return allTheQDVs.get(ith);
    }  
}