/**************************************************
 *              VerticalBoxPlot_Model             *
 *                    04/02/24                    *
 *                      09:00                     *
 *************************************************/
package proceduresManyUnivariate;

import anova1.quantitative.ANOVA1_Quant_Model;
import anova1.categorical.ANOVA1_Cat_Model;
import anova2.ANOVA2_RM_Model;
import the_t_procedures.Indep_t_PrepStructs;
import dataObjects.QuantitativeDataVariable;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import proceduresTwoUnivariate.Explore_2Ind_Controller;

public class VerticalBoxPlot_Model {
    // POJOs
    
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    private int n_QDVs;
    private String strSubTitle, firstVarLabel, secondVarLabel;
    public ObservableList<String> varLabels, categoryLabels;
    private ArrayList<QuantitativeDataVariable> allTheQDVs;

    // Called by Univ_Quant_Controller
    public VerticalBoxPlot_Model(String descriptionOfVariable, QuantitativeDataVariable theQDV) {
        //System.out.println("27 VerticalBoxPlot_Model, constructing");
        allTheQDVs = new ArrayList<>();
        strSubTitle = descriptionOfVariable;
        allTheQDVs.add(theQDV);
        n_QDVs = allTheQDVs.size();
        collectAllTheLabels();
    }
    
    public VerticalBoxPlot_Model(Explore_2Ind_Controller explore_2Ind_Controller, String descriptionOfVariable, ArrayList<QuantitativeDataVariable> allTheQDVs) {
        if (printTheStuff == true) {
            System.out.println("41 *** VerticalBoxPlot_Model, constructing");
        }
        this.allTheQDVs = new ArrayList<>();
        this.allTheQDVs = allTheQDVs;
        n_QDVs = allTheQDVs.size();
        this.strSubTitle = descriptionOfVariable;
        firstVarLabel = explore_2Ind_Controller.getFirstVarDescr();
        secondVarLabel = explore_2Ind_Controller.getSecondVarDescr();    
        varLabels = FXCollections.observableArrayList();
        varLabels.add(firstVarLabel);
        varLabels.add(secondVarLabel);       
    }
    
    // Called by Indep_t_PrepStructs
    public VerticalBoxPlot_Model(Indep_t_PrepStructs indep_t_PrepStructs, String descriptionOfVariable, ArrayList<QuantitativeDataVariable> allTheQDVs) {
        if (printTheStuff == true) {
            System.out.println("57 *** VerticalBoxPlot_Model, constructing");
        }
        this.allTheQDVs = new ArrayList<>();
        this.allTheQDVs = allTheQDVs;
        n_QDVs = allTheQDVs.size();
        this.strSubTitle = descriptionOfVariable;
        firstVarLabel = indep_t_PrepStructs.getFirstVarDescription();
        allTheQDVs.get(0).setTheVarLabel(firstVarLabel);
        secondVarLabel = indep_t_PrepStructs.getSecondVarDescription();
        allTheQDVs.get(1).setTheVarLabel(secondVarLabel);
        strSubTitle = firstVarLabel + " & " + secondVarLabel;
        varLabels = FXCollections.observableArrayList();
        varLabels.add(firstVarLabel);
        varLabels.add(secondVarLabel);        
        collectAllTheLabels();      
    }
    
    public VerticalBoxPlot_Model(ANOVA1_Cat_Model anova1_Cat_Model, ArrayList<QuantitativeDataVariable> allTheQDVs) {
        if (printTheStuff == true) {
            System.out.println("76 *** VerticalBoxPlot_Model, constructing");
        }
        this.allTheQDVs = new ArrayList<>();
        this.allTheQDVs = allTheQDVs;
        n_QDVs = allTheQDVs.size();
        strSubTitle = anova1_Cat_Model.getSubTitle();
        varLabels = FXCollections.observableArrayList();
        varLabels = anova1_Cat_Model.getVarLabels();  
    }
    
    public VerticalBoxPlot_Model(ANOVA1_Quant_Model anova1_Quant_Model, ArrayList<QuantitativeDataVariable> allTheQDVs) {
        if (printTheStuff == true) {
            System.out.println("88 *** VerticalBoxPlot_Model, constructing");
        }
        this.allTheQDVs = new ArrayList<>();
        this.allTheQDVs = allTheQDVs;
        n_QDVs = allTheQDVs.size();
        
        strSubTitle = anova1_Quant_Model.getSubTitle();        
        collectAllTheLabels();         
    }
    
    public VerticalBoxPlot_Model(MultUni_Model multUni_Model, ArrayList<QuantitativeDataVariable> allTheQDVs) {
        if (printTheStuff == true) {
            System.out.println("100 *** VerticalBoxPlot_Model, constructing");
        }
        this.allTheQDVs = new ArrayList<>();
        this.allTheQDVs = allTheQDVs;
        n_QDVs = allTheQDVs.size();
        strSubTitle = multUni_Model.getSubTitle();
        collectAllTheLabels();          
    }
    
    public VerticalBoxPlot_Model(ANOVA2_RM_Model anova2_RM_Model, ArrayList<QuantitativeDataVariable> allTheQDVs) {
        if (printTheStuff == true) {
            System.out.println("111 *** VerticalBoxPlot_Model, constructing");
        }
        this.allTheQDVs = new ArrayList<>();
        this.allTheQDVs = allTheQDVs;
        n_QDVs = allTheQDVs.size();
        strSubTitle = "Repeated Measures: Subjects x response";
        collectAllTheLabels();          
    }
    
    public String getFirstVarLabel() { return firstVarLabel; }
    public String getSecondVarLabel() { return secondVarLabel; }  
    
    private void collectAllTheLabels() {
        varLabels = FXCollections.observableArrayList();         
        for (int iVars = 0; iVars < n_QDVs; iVars++) {
            varLabels.add(allTheQDVs.get(iVars).getTheVarLabel());
        }
    }
   
    public String getSubTitle() { return strSubTitle; }
    
    public ObservableList <String> getVarLabels() { 
        return varLabels; 
    }
    public ArrayList<QuantitativeDataVariable> getAllTheQDVs() { return allTheQDVs; }
    public QuantitativeDataVariable getIthQDV(int ith) {
       return allTheQDVs.get(ith);
    }
}