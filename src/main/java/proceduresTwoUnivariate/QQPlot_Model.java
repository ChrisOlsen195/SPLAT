/**************************************************
 *                  QQPlot_Model                  *
 *                    01/27/25                    *
 *                      12:00                     *
 *************************************************/
package proceduresTwoUnivariate;

import dataObjects.QuantitativeDataVariable;
import java.util.ArrayList;
import the_t_procedures.Indep_t_Controller;

public class QQPlot_Model {
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    int n_QDVs; 
    String subTitle, firstVarDescription, secondVarDescription;
     ArrayList<String> allTheLabels;
     
    // My classes
    ArrayList<QuantitativeDataVariable> allTheQDVs; 
    
    public QQPlot_Model() { }

    public QQPlot_Model(Explore_2Ind_Controller explore_2Ind_Controller, ArrayList<QuantitativeDataVariable> allTheQDVs) {
        if (printTheStuff == true) {
            System.out.println("27 *** QQPlot_Model(Explore_2Ind_Controller, constructing");
        }
        this.allTheQDVs = new ArrayList<>();
        this.allTheQDVs = allTheQDVs;
        n_QDVs = allTheQDVs.size();
        allTheLabels = new ArrayList<>();
        
        for (int iVars = 0; iVars < n_QDVs; iVars++) {
            allTheLabels.add(allTheQDVs.get(iVars).getTheVarLabel());
        }  
        
        firstVarDescription = explore_2Ind_Controller.getFirstVarDescr();
        secondVarDescription = explore_2Ind_Controller.getSecondVarDescr();
    } 
    
    // This constructor is for independent t
    public QQPlot_Model(Indep_t_Controller indep_t_Controller, String subTitle, ArrayList<QuantitativeDataVariable> allTheQDVs) {
        if (printTheStuff == true) {
            System.out.println("45 *** QQPlot_Model(Indep_t_Controller, Constructing");
        }
        this.allTheQDVs = new ArrayList<>();
        this.allTheQDVs = allTheQDVs;
        n_QDVs = allTheQDVs.size();
        this.subTitle = subTitle;
        allTheLabels = new ArrayList<>();
        
        for (int iVars = 0; iVars < n_QDVs; iVars++) {
            allTheLabels.add(allTheQDVs.get(iVars).getTheVarLabel());
        }      
        
        firstVarDescription = indep_t_Controller.getFirstVarDescr();
        secondVarDescription = indep_t_Controller.getSecondVarDescr(); 
    }
    
    public String getFirstVarDescription() { return firstVarDescription; }
    public String getSecondVarDescription() { return secondVarDescription; }
    
    public String getSubTitle() { return subTitle; }
    public ArrayList<String> getTheLabels() { return allTheLabels; }
    public ArrayList<QuantitativeDataVariable> getAllTheQDVs() { return allTheQDVs; }    
}
