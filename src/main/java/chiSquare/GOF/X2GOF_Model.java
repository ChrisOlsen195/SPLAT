/****************************************************************************
 *                         X2GOF_Model                                      *
 *                          02/12/25                                        *
 *                           18:00                                          *
 ***************************************************************************/
package chiSquare.GOF;

import dialogs.chisquare.X2GOF_DataByHand_Dialog;
import dialogs.chisquare.X2GOF_DataFromFile_Dialog;
import dataObjects.ColumnOfData;
import dataObjects.UnivariateCategoricalDataObj;
import dialogs.ReOrderStringDisplay_Dialog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import probabilityDistributions.*;
import utilityClasses.*;

public class X2GOF_Model {
    //POJOs 
    
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    int df, nCategories, nCellsBelow_5, n_QDVs;
    int[] pre_ObservedCounts, observedCounts, theNewOrder;
    
    double chiSquare, pValue, observedTotal, propTotal, expectedTotal, 
           contribTotal, cohens_W;
    double[] expectedValues, chiSquareContribution, resids, standResids, 
             pre_ExpectedProportions, expectedProportions;
    
    String strTheGOFVariable, strReturnStatus;
    String[] pre_StrCategoriesAsStrings, strCategoriesAsStrings; 
    ObservableList<String> preCategoryLabels, categoryLabels;
    
    // My objects
    ChiSquareDistribution chi2Distr;
    ColumnOfData colOfData;
    UnivariateCategoricalDataObj univCatDataObj;
    X2GOF_DataByHand_Dialog x2GOF_DataByHand_Dialog;
    X2GOF_DataFromFile_Dialog x2GOF_DataFromFile_Dialog;
    X2GOF_DataDialogObj x2GOF_DataDialogObj;  
    
    ReOrderStringDisplay_Dialog reOrderStringDisplay_Dialog;

/*******************************************************************************
 *          Define the dialog objects and their return info objects            *
 ******************************************************************************/
    
    public X2GOF_Model( ) {
        if (printTheStuff == true) {
            System.out.println("52 *** X2GOF_Model, constructing");
        }
    }
    
    public String analyzeGOF_DataFromFile(X2GOF_Controller x2GOF_controller) {
        if (printTheStuff == true) {
            System.out.println("58 --- X2GOF_Model, analyzeGOF_DataFromFile");
        }
        strReturnStatus = "OK";
        colOfData  = new ColumnOfData(x2GOF_controller.getColumnOfData());
        strTheGOFVariable = colOfData.getVarLabel();
        univCatDataObj = new UnivariateCategoricalDataObj(colOfData); 
        nCategories = univCatDataObj.getNUniques();
        
        if (nCategories < 2) {
            MyAlerts.showTooFewChiSquareDFAlert();
            return "Cancel";
        }
        
        // Fill _some_ of arrays
        initializeTheArrays();

        pre_ObservedCounts = univCatDataObj.getObservedCounts();
        pre_StrCategoriesAsStrings = univCatDataObj.getCategories();
        
        df = nCategories - 1;
        chi2Distr = new ChiSquareDistribution(df);
        strReturnStatus = gofFromFile_Dlg();
        if (strReturnStatus.equals("OK")) { 
            performChiSqGOFCalculations(); 
        } 
        else { return "Cancel"; }        
        return "OK";
    }  
    
    public String analyzeGOF_DataByHand() {
        if (printTheStuff == true) {
            System.out.println("89 --- X2GOF_Model, analyzeGOF_DataByHand()");
        }       
        strReturnStatus = gofByHand_Dlg();  
        
        if (strReturnStatus.equals("OK")) { 
            performChiSqGOFCalculations(); 
        }
        return strReturnStatus;
    }
    

    
/******************************************************************************
*          Instantiate the dialog objects and their return info objects       *
******************************************************************************/ 
    // GOF information added manually
    private String gofByHand_Dlg() {
        if (printTheStuff == true) {
            System.out.println("107 --- X2GOF_Model, gofByHand_Dlg()");
        } 
        x2GOF_DataByHand_Dialog = new X2GOF_DataByHand_Dialog(this);
        x2GOF_DataByHand_Dialog.constructDialogGuts();
        x2GOF_DataByHand_Dialog.showAndWait();
        strReturnStatus = x2GOF_DataByHand_Dialog.getStrReturnStatus();
        
        if (strReturnStatus.equals("OK")) {
            x2GOF_DataDialogObj = new X2GOF_DataDialogObj();
            x2GOF_DataDialogObj = x2GOF_DataByHand_Dialog.getTheDialogObject();
            strTheGOFVariable = x2GOF_DataDialogObj.getGOFVariable();
            nCategories = x2GOF_DataDialogObj.getNCategories();

            if (nCategories < 2) {
                MyAlerts.showTooFewChiSquareDFAlert();
                return "Cancel";
            }
            initializeTheArrays();
            df = nCategories - 1;
            chi2Distr = new ChiSquareDistribution(df);
            pre_ObservedCounts = x2GOF_DataDialogObj.getObservedValues();
            pre_StrCategoriesAsStrings = x2GOF_DataDialogObj.getTheGOFCategories();
            preCategoryLabels = FXCollections.observableArrayList(pre_StrCategoriesAsStrings);
            pre_ExpectedProportions = x2GOF_DataDialogObj.getExpectedProps();

            askAboutGOFReOrdering();

            for (int ithCat = 0; ithCat < nCategories; ithCat++) {
                observedCounts[ithCat] = pre_ObservedCounts[theNewOrder[ithCat]];
                expectedProportions[ithCat] = pre_ExpectedProportions[theNewOrder[ithCat]];
                strCategoriesAsStrings[ithCat] = pre_StrCategoriesAsStrings[theNewOrder[ithCat]];
            }

            categoryLabels = FXCollections.observableArrayList(strCategoriesAsStrings);
       }
       return strReturnStatus; 
    }
    
    private String gofFromFile_Dlg() {  
        if (printTheStuff == true) {
            System.out.println("147 --- X2GOF_Model, gofFromFile_Dlg()");
        } 
        x2GOF_DataFromFile_Dialog = new X2GOF_DataFromFile_Dialog(this);
        x2GOF_DataFromFile_Dialog.x2FileDialog_Step1();
        x2GOF_DataFromFile_Dialog.showAndWait();
        strReturnStatus = x2GOF_DataFromFile_Dialog.getStrReturnStatus();
        
        if (strReturnStatus.equals("OK")) {
            x2GOF_DataDialogObj = new X2GOF_DataDialogObj();
            x2GOF_DataDialogObj = x2GOF_DataFromFile_Dialog.getTheDialogObject();
            
            nCategories = x2GOF_DataDialogObj.getNCategories();
            initializeTheArrays();

            if (nCategories < 2) {
                MyAlerts.showTooFewChiSquareDFAlert();
                return "Cancel";
            }

            df = nCategories - 1;
            chi2Distr = new ChiSquareDistribution(df);
            initializeTheArrays();
            pre_ObservedCounts = x2GOF_DataDialogObj.getObservedValues();
            pre_ExpectedProportions = x2GOF_DataDialogObj.getExpectedProps();
            pre_StrCategoriesAsStrings = x2GOF_DataDialogObj.getTheGOFCategories();
            preCategoryLabels = FXCollections.observableArrayList(pre_StrCategoriesAsStrings);
            
            askAboutGOFReOrdering();
            
            for (int ithCat = 0; ithCat < nCategories; ithCat++) {
                observedCounts[ithCat] = pre_ObservedCounts[theNewOrder[ithCat]];
                expectedProportions[ithCat] = pre_ExpectedProportions[theNewOrder[ithCat]];
                strCategoriesAsStrings[ithCat] = pre_StrCategoriesAsStrings[theNewOrder[ithCat]];
            }
            
            categoryLabels = FXCollections.observableArrayList(strCategoriesAsStrings);
        }
        return strReturnStatus;
    }
    
    /************************************************************************
     *         This is different from the ordering method in, e.g., ANOVA   *
     ***********************************************************************/
    private void askAboutGOFReOrdering() {
        //System.out.println("181  ***  X2GOF_Model, askAboutGOFReOrdering()");
        n_QDVs = nCategories;

        // Default
        for (int ithQDV= 0; ithQDV < n_QDVs; ithQDV++) {
            theNewOrder[ithQDV] = ithQDV;
        }       
        
        reOrderStringDisplay_Dialog = new ReOrderStringDisplay_Dialog(this, pre_StrCategoriesAsStrings);
        reOrderStringDisplay_Dialog.showAndWait();
        strReturnStatus = reOrderStringDisplay_Dialog.getStrReturnStatus();
    }
    
    public void closeTheReOrderDialog(int[] returnedOrder) {
        System.arraycopy(returnedOrder, 0, theNewOrder, 0, n_QDVs);
        reOrderStringDisplay_Dialog.close();
    }
    
    private void initializeTheArrays() {
        if (printTheStuff == true) {
            System.out.println("212 --- X2GOF_Model, initializeTheArrays()");
        } 
        pre_StrCategoriesAsStrings = new String[nCategories]; 
        pre_ObservedCounts = new int[nCategories];
        pre_ExpectedProportions = new double[nCategories]; 
        
        strCategoriesAsStrings = new String[nCategories]; 
        observedCounts = new int[nCategories];
        expectedProportions = new double[nCategories];        

        expectedValues = new double[nCategories];
        resids = new double[nCategories];
        chiSquareContribution = new double[nCategories];
        standResids = new double[nCategories];  
        theNewOrder = new int[nCategories];
    }
    
    private void performChiSqGOFCalculations() {
        if (printTheStuff == true) {
            System.out.println("231 --- X2GOF_Model, performChiSqGOFCalculations()");
        }
        observedTotal = 0.0;  expectedTotal = 0.0; propTotal = 0.0;
        chiSquare = 0.0; contribTotal = 0.0;
        nCellsBelow_5 = 0;
        
        for (int ithCat = 0; ithCat < nCategories; ithCat++) {  
            observedTotal += observedCounts[ithCat]; 
            propTotal += expectedProportions[ithCat]; 
        }   
        
        for (int ithCat = 0; ithCat < nCategories; ithCat++) {           
            expectedValues[ithCat] = observedTotal * expectedProportions[ithCat];
            
            if (expectedValues[ithCat] < 5.0) { nCellsBelow_5++; }
            
            expectedTotal += expectedValues[ithCat];
            resids[ithCat] = observedCounts[ithCat] - expectedValues[ithCat];
            chiSquareContribution[ithCat] = resids[ithCat] * resids[ithCat] / expectedValues[ithCat];
            contribTotal += chiSquareContribution[ithCat];
            if (resids[ithCat] >= 0) {
                standResids[ithCat] = Math.sqrt(chiSquareContribution[ithCat]);
            } else {
               standResids[ithCat] = -Math.sqrt(chiSquareContribution[ithCat]); 
            }
            chiSquare += chiSquareContribution[ithCat];            
        }  
        cohens_W = Math.sqrt(chiSquare / observedTotal);
        pValue = chi2Distr.getRightTailArea(chiSquare);
    }       

/*******************************************************************************
*                          Ancillary methods                                   *
* @return 
*******************************************************************************/  
    public String getGOFVariable() {return strTheGOFVariable;}
    public String[] getPreCategoriesAsStrings() {return pre_StrCategoriesAsStrings; }
    public String[] getCategoriesAsStrings() {return strCategoriesAsStrings; }
    public double getIthObservedCount(int ith) { return observedCounts[ith]; }
    public int getNCategories() { return nCategories; }
    public int getDF() { return df; }   
    public String getIthCategory(int ith) { return categoryLabels.get(ith); }  
    public ObservableList<String> getCategoryLabels () {return categoryLabels; }   
    public int[] getPreObservedValues() { return pre_ObservedCounts; }
    public int[] getObservedValues() { return observedCounts; }
    public int getNCellsBelow5 () { return nCellsBelow_5; } 

    public double[] getExpectedValues () { return expectedValues; }
    public double[] getExpectedProportions() {return expectedProportions; }
    public double[] getX2Contributions() {return chiSquareContribution; }
    public double[] getResids () { return resids; }
    public double[] getStandResids () { return standResids; }     
    public double getX2() { return chiSquare; }
    public double getObservedTotal() { return observedTotal; }
    public double getPropTotal() { return propTotal; }
    public double getExpectedTotal() { return expectedTotal; }
    public double getContribTotal() { return contribTotal; }
    public double getPValue() {return pValue; }
    public double getCohensW() {return cohens_W; } 
    public String getReturnStatus() { return strReturnStatus; }
}


