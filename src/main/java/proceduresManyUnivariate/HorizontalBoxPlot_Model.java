/**************************************************
 *              HorizontalBoxPlot_Model           *
 *                    01/31/26                    *
 *                      18:00                     *
 *************************************************/
package proceduresManyUnivariate;

import the_t_procedures.Single_t_Controller;
import dataObjects.QuantitativeDataVariable;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import proceduresTwoUnivariate.*;
import the_t_procedures.Indep_t_Controller;

public class HorizontalBoxPlot_Model {
    
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    int n_QDVs;
    private String strSubTitle, firstVarDescr, secondVarDescr;
    ArrayList<String> allTheVarLabels, allTheVarDescriptions;
    public ObservableList<String> categoryLabels;
    ArrayList<QuantitativeDataVariable> allTheQDVs;

    public HorizontalBoxPlot_Model(String descriptionOfVariable, QuantitativeDataVariable theQDV) {
        if (printTheStuff) {
            System.out.println("*** 29 HorizontalBoxPlot_Model, constructing from QDV");
            //System.out.println("--- 30 HorizontalBoxPlot_Model, theQDV = " + theQDV.toString());
        }
        
        allTheQDVs = new ArrayList<>();
        strSubTitle = descriptionOfVariable;
        allTheQDVs.add(theQDV);
        n_QDVs = allTheQDVs.size();
        collectAllTheLabels();
    }
    
    // This constructor is for a single set of data
    public HorizontalBoxPlot_Model(Single_t_Controller single_t_Controller, String descriptionOfVariable, QuantitativeDataVariable theQDV) {
        if (printTheStuff) {
            System.out.println("*** 42 HorizontalBoxPlot_Model, constructing from Single_t_Controller");
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

    // This constructor is for two indep data sets
    public HorizontalBoxPlot_Model(Explore_2Ind_Controller explore_2Ind_Controller, String descriptionOfVariable, ArrayList<QuantitativeDataVariable> allTheQDVs) {
        if (printTheStuff) {
            System.out.println("58 *** HorizontalBoxPlot_Model, constructing from Explore_2Ind_Controller");
        }
        this.allTheQDVs = new ArrayList<>();
        this.allTheQDVs = allTheQDVs;
        n_QDVs = allTheQDVs.size();
        strSubTitle = descriptionOfVariable;
        firstVarDescr = explore_2Ind_Controller.getFirstVarDescr();
        secondVarDescr = explore_2Ind_Controller.getSecondVarDescr();
        categoryLabels = FXCollections.observableArrayList();
        categoryLabels.add(firstVarDescr);
        categoryLabels.add(secondVarDescr); 
    }
    
    // This constructor is for independent t
    public HorizontalBoxPlot_Model(Indep_t_Controller indep_t_Controller, String descriptionOfVariable, ArrayList<QuantitativeDataVariable> allTheQDVs) {
        if (printTheStuff) {
            System.out.println("74 *** HorizontalBoxPlot_Model, constructing from Indep_t_Controller");
        }
        this.allTheQDVs = new ArrayList<>();
        this.allTheQDVs = allTheQDVs;
        n_QDVs = allTheQDVs.size();
        strSubTitle = descriptionOfVariable;
        firstVarDescr = indep_t_Controller.getFirstVarDescr();
        secondVarDescr = indep_t_Controller.getSecondVarDescr();  
        categoryLabels = FXCollections.observableArrayList();
        categoryLabels.add(firstVarDescr);
        categoryLabels.add(secondVarDescr);             
    }
    
        // This constructor is for ANCOVA -- Labels/Descr handled externally
    public HorizontalBoxPlot_Model(ArrayList<QuantitativeDataVariable> allTheQDVs) {
        if (printTheStuff) {
            System.out.println("*** 90 HorizontalBoxPlot_Model, constructing from ANOVA");
        }
        this.allTheQDVs = new ArrayList<>();
        this.allTheQDVs = allTheQDVs;
        n_QDVs = allTheQDVs.size();
        categoryLabels = FXCollections.observableArrayList(); 
        collectAllTheLabels();
    }
    
    public HorizontalBoxPlot_Model(MultUni_Model multUni_Model, ArrayList<QuantitativeDataVariable> allTheQDVs) {
        if (printTheStuff) {
            System.out.println("*** 102 HorizontalBoxPlot_Model, constructing from MultUni_Model");
        }
        this.allTheQDVs = new ArrayList<>();
        this.allTheQDVs = allTheQDVs;
        n_QDVs = allTheQDVs.size();
        strSubTitle = multUni_Model.getSubTitle();
        if (printTheStuff) {
            System.out.println("--- 109 HorizontalBoxPlot_Model, strSubTitle = " + strSubTitle);
        }
        collectAllTheLabels();          
    }

    public String getFirstVarDescription() { return firstVarDescr; }
    public String getSecondVarDescription() { return secondVarDescr; } 
    
    private void collectAllTheLabels() {
        categoryLabels = FXCollections.observableArrayList(); 
        for (int iVars = 0; iVars < n_QDVs; iVars++) {
            categoryLabels.add(allTheQDVs.get(iVars).getTheVarLabel());
            if (printTheStuff) {
                System.out.println("--- 121 HorizontalBoxPlot_Model, Label(ith) = " + allTheQDVs.get(iVars).getTheVarLabel());
            }
        }   
    }
   
    public String getSubTitle() { return strSubTitle; }
    public ObservableList <String> getCategoryLabels() { return categoryLabels; }
    public ArrayList<QuantitativeDataVariable> getAllTheQDVs() { return allTheQDVs; }
    public QuantitativeDataVariable getIthQDV(int ith) {
       return allTheQDVs.get(ith);
    }  
}