/****************************************************************************
 *                        UnivCat_Model                                     *
 *                           02/16/25                                       *
 *                            18:00                                         *
 ***************************************************************************/
package univariateProcedures_Categorical;

import dataObjects.ColumnOfData;
import dataObjects.UnivariateCategoricalDataObj;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import splat.Data_Manager;

public class UnivCat_Model {
    //POJOs
    
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    int nCategories;
    int[] observedCounts, observedCountsFromFile;
    
    double observedTotal, cohens_W;
    double[] observedProps;
    
    String descriptionOfVariable, strReturnStatus; //, sourceString, tempString ;
    String[] categoriesAsStrings; 
    ObservableList<String> categoryLabels;
    
    // My objects
    UnivariateCategoricalDataObj univCatDataObj;
    UnivCat_DataFromFileDialog univCat_DataFromFileDialog;
    Data_Manager dm;

/*******************************************************************************
 *          Define the dialog objects and their return info objects            *
 ******************************************************************************/
    
    public UnivCat_Model() { }
    
    public String doUnivCat_FromFile(UnivCat_Controller univCat_Controller) {
        dm = univCat_Controller.getDataManager();
        createUnivCatDialog_FromFile();
        if (printTheStuff == true) {
            System.out.println("45 --- UnivCat_Model, strReturnStatus = " + strReturnStatus);
        }  
        if (!strReturnStatus.equals("OK")) {
            strReturnStatus = "Cancel";
        }
        return strReturnStatus;
    }  

/******************************************************************************
*          Instantiate the dialog objects and their return info objects       *
******************************************************************************/     
    //// GOF information provided via UnivCatDataObj
    public String createUnivCatDialog_FromFile() {   
        // The 'categorical' is needed due to access to OneVarDialog
        univCat_DataFromFileDialog = new UnivCat_DataFromFileDialog(dm, "Categorical");
        univCat_DataFromFileDialog.showAndWait();
        strReturnStatus = univCat_DataFromFileDialog.getReturnStatus();
        if (printTheStuff == true) {
            System.out.println("63 --- UnivCat_Model, strReturnStatus = " + strReturnStatus);
        }  
        if (!strReturnStatus.equals("OK")) { return "Cancel"; }
        
        if (strReturnStatus.equals("OK")) {
            int thisCol = univCat_DataFromFileDialog.getVarIndex();   
            ColumnOfData col_x = dm.getAllTheColumns().get(thisCol);
            col_x.cleanTheColumn(dm, thisCol);
            strReturnStatus = col_x.getReturnStatus();
            if (printTheStuff == true) {
                System.out.println("73 --- UnivCat_Model, strReturnStatus = " + strReturnStatus);
            }             
            univCatDataObj = new UnivariateCategoricalDataObj(col_x); 
            nCategories = univCatDataObj.getNUniques();          
            categoriesAsStrings = new String[nCategories]; 
            observedCounts = new int[nCategories]; 
            observedProps = new double[nCategories];
           // Fill some of arrays
           categoriesAsStrings = univCatDataObj.getCategories();
           categoryLabels = FXCollections.observableArrayList(categoriesAsStrings);
           observedCounts = univCatDataObj.getObservedCounts();
           
           observedTotal = 0;
           for (int ithCat = 0; ithCat < nCategories; ithCat++) {
               observedTotal += observedCounts[ithCat];
           }
           
           for (int ithCat = 0; ithCat < nCategories; ithCat++) {
               observedProps[ithCat] = (double)observedCounts[ithCat] / (double)observedTotal;
           }
           
           descriptionOfVariable = univCat_DataFromFileDialog.getDescriptionOfVariable();
            if (printTheStuff == true) {
                System.out.println("96 --- UnivCat_Model, strReturnStatus = " + strReturnStatus);
            } 
       }
        
        return strReturnStatus;
    }
    
    public Data_Manager getDataManager() { return dm; }
    public UnivCat_Model getUnivCat_Model() {return this; }
    public String getTheVariable() {return descriptionOfVariable;}
    public String[] getCategoriesAsStrings() {return categoriesAsStrings; }
    public double getIthObservedCount(int ith) { return observedCounts[ith]; }
        
    public int getNCategories() { return nCategories; }    
    public String getDescriptionOfVariable() { return descriptionOfVariable; }    
    public ObservableList<String> getCategoryLabels () {return categoryLabels; }    
    public int[] getObservedCounts () { return observedCounts; }
    public double[] getObservedProps () { return observedProps; }    
    public int[] getObservedCountsFromFile() { return observedCountsFromFile; }         
    public double getObservedTotal() { return observedTotal; }
    public double getCohensW() {return cohens_W; }    
    public String getReturnStatus() { return strReturnStatus; }    
}


