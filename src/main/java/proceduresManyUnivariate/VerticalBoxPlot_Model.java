/**************************************************
 *              VerticalBoxPlot_Model             *
 *                    10/07/24                    *
 *                      15:00                     *
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
    
    // boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    private int n_QDVs;
    public int[] theNewOrder;
    private String strSubTitle, firstVarLabel, secondVarLabel;
    String[] incomingLabels;
    public ObservableList<String> varLabels, categoryLabels;
    private ArrayList<QuantitativeDataVariable> allTheQDVs;
    
    // Called by Univ_Quant_Controller
    public VerticalBoxPlot_Model(String descriptionOfVariable, QuantitativeDataVariable theQDV) {
        if (printTheStuff) {
            System.out.println("\n34 *** VerticalBoxPlot_Model, Constructing");
        }
        allTheQDVs = new ArrayList<>();
        strSubTitle = descriptionOfVariable;
        allTheQDVs.add(theQDV);
        n_QDVs = allTheQDVs.size();
        collectAllTheLabels();
    }
    
    public VerticalBoxPlot_Model(Explore_2Ind_Controller explore_2Ind_Controller, String descriptionOfVariable, ArrayList<QuantitativeDataVariable> allTheQDVs) {
        if (printTheStuff) {
            System.out.println("\n45 *** VerticalBoxPlot_Model, constructing");
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
        if (printTheStuff) {
            System.out.println("\n61 *** VerticalBoxPlot_Model, constructing");
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
    
    public VerticalBoxPlot_Model(ANOVA1_Cat_Model anova1_Cat_Model, ArrayList<QuantitativeDataVariable>  allTheQDVs) {
        if (printTheStuff) {
            System.out.println("\n80 *** VerticalBoxPlot_Model, constructing");
            System.out.println("  81 VerticalBoxPlot_Model, allTheQDVs.size = " + allTheQDVs.size());
        }
        strSubTitle = anova1_Cat_Model.getSubTitle();
        this.allTheQDVs = new ArrayList();
        this.allTheQDVs = allTheQDVs;
        n_QDVs = allTheQDVs.size();
        collectAllTheLabels();       
    }
    
    public VerticalBoxPlot_Model(ANOVA1_Quant_Model anova1_Quant_Model, ArrayList<QuantitativeDataVariable> allTheQDVs) {
        if (printTheStuff) {
            System.out.println("\n92 VerticalBoxPlot_Model, Constructing");
            System.out.println("  *** 93 VerticalBoxPlot_Model, allTheQDVs.size = " + allTheQDVs.size());
        }
        strSubTitle = anova1_Quant_Model.getSubTitle();
        this.allTheQDVs = new ArrayList();
        this.allTheQDVs = allTheQDVs;
        n_QDVs = allTheQDVs.size();
        collectAllTheLabels();  
    }
    
    public VerticalBoxPlot_Model(MultUni_Model multUni_Model, ArrayList<QuantitativeDataVariable> allTheQDVs) {
        if (printTheStuff) {
            System.out.println("\n104 *** VerticalBoxPlot_Model, Constructing");
            //System.out.println("  *** 103 VerticalBoxPlot_Model, allTheQDVs.size = " + allTheQDVs.size());
        }
        strSubTitle = multUni_Model.getSubTitle();
        this.allTheQDVs = new ArrayList();
        this.allTheQDVs = allTheQDVs;
        n_QDVs = allTheQDVs.size();
        collectAllTheLabels();
    }
    
    public VerticalBoxPlot_Model(ANOVA2_RM_Model anova2_RM_Model, ArrayList<QuantitativeDataVariable> allTheQDVs) {
        if (printTheStuff) {
            System.out.println("\n116 VerticalBoxPlot_Model, Constructing");
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