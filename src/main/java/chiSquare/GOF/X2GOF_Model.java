/****************************************************************************
 *                         X2GOF_Model                                      *
 *                          05/25/24                                        *
 *                           15:00                                          *
 ***************************************************************************/
package chiSquare.GOF;

import dialogs.chisquare.X2GOF_DataByHand_Dialog;
import dialogs.chisquare.X2GOF_DataFromFile_Dialog;
import dataObjects.ColumnOfData;
import dataObjects.UnivariateCategoricalDataObj;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import probabilityDistributions.*;
import utilityClasses.*;

public class X2GOF_Model {
    //POJOs 
    int df, nCategories, nCellsBelow_5;
    int[] observedCounts, observedCountsFromFile;
    
    double chiSquare, pValue, observedTotal, propTotal, expectedTotal, 
           contribTotal, cohens_W;
    double[] expectedValues, chiSquareContribution, resids, standResids, 
             expectedProportions;
    
    String strTheGOFVariable, strReturnStatus;
    String[] strObservedValuesFromFile, strCategoriesAsStrings; 
    ObservableList<String> categoryLabels;
    
    // My objects
    ChiSquareDistribution chi2Distr;
    ColumnOfData colOfData;
    UnivariateCategoricalDataObj univCatDataObj;
    X2GOF_DataByHand_Dialog x2GOF_DataByHand_Dialog;
    X2GOF_DataFromFile_Dialog x2GOF_DataFromFile_Dialog;
    X2GOF_DataDialogObj x2GOF_DataDialogObj;  

/*******************************************************************************
 *          Define the dialog objects and their return info objects            *
 ******************************************************************************/
    
    public X2GOF_Model( ) {//System.out.println("\n43 X2GOF_Model, Constructing empty)");
    }
    
    public String doX2FromFile(X2GOF_Controller x2GOF_controller) {
        //System.out.println("47 X2GOF_Model, doX2FromFile(X2GOF_Controller x2GOF_controller)");
        strReturnStatus = "OK";
        colOfData  = new ColumnOfData(x2GOF_controller.getColumnOfData());
        strTheGOFVariable = colOfData.getVarLabel();
        univCatDataObj = new UnivariateCategoricalDataObj(colOfData); 
        nCategories = univCatDataObj.getNUniques();
        
        if (nCategories < 2) {
            MyAlerts.showTooFewChiSquareDFAlert();
            return "Cancel";
        }
        
        df = nCategories - 1;
        chi2Distr = new ChiSquareDistribution(df);
        makeTheArrays();
        observedCountsFromFile = new int[nCategories];
        strObservedValuesFromFile = new String[nCategories];

        observedCountsFromFile = univCatDataObj.getObservedCounts();
        strObservedValuesFromFile = univCatDataObj.getUniqueCategories();
        createGOFDialog_File();
        
        if (strReturnStatus.equals("OK")) { doChiSqGOFCalculations(); } 
        else { return "Cancel"; }        
        return "OK";
    }  
    
    public String doX2FromTable() {
        //System.out.println("75 X2GOF_Model, doX2FromTable()");        
        strReturnStatus = createGOFDialog();  
        
        if (strReturnStatus.equals("OK")) { doChiSqGOFCalculations(); }
        
        return strReturnStatus;
    }
    
    private void makeTheArrays() {
        //System.out.println("84 X2GOF_Model, makeTheArrays()");
        strCategoriesAsStrings = new String[nCategories]; 
        observedCounts = new int[nCategories];
        expectedValues = new double[nCategories];
        expectedProportions = new double[nCategories];
        resids = new double[nCategories];
        chiSquareContribution = new double[nCategories];
        standResids = new double[nCategories];        
    }
    
/******************************************************************************
*          Instantiate the dialog objects and their return info objects       *
******************************************************************************/ 
    // GOF information added manually
    private String createGOFDialog() {
        //System.out.println("99 X2GOF_Model, createGOFDialog()");
        x2GOF_DataByHand_Dialog = new X2GOF_DataByHand_Dialog(this);
        x2GOF_DataByHand_Dialog.constructDialogGuts();
        x2GOF_DataByHand_Dialog.showAndWait();
        strReturnStatus = x2GOF_DataByHand_Dialog.getReturnStatus();
        
        if (strReturnStatus.equals("OK")) {
            x2GOF_DataDialogObj = new X2GOF_DataDialogObj();
            x2GOF_DataDialogObj = x2GOF_DataByHand_Dialog.getTheDialogObject();
            strTheGOFVariable = x2GOF_DataDialogObj.getGOFVariable();
            nCategories = x2GOF_DataDialogObj.getNCategories();
            
            if (nCategories < 2) {
                MyAlerts.showTooFewChiSquareDFAlert();
                return "Cancel";
            }
            
           df = nCategories - 1;
           chi2Distr = new ChiSquareDistribution(df);
           makeTheArrays();
           // Fill some of arrays
           strCategoriesAsStrings = x2GOF_DataDialogObj.getTheGOFCategories();
           categoryLabels = FXCollections.observableArrayList(strCategoriesAsStrings);
           observedCounts = x2GOF_DataDialogObj.getObservedValues();
           expectedProportions = x2GOF_DataDialogObj.getExpectedProps();
       }
       return strReturnStatus; 
    }
    
    private String createGOFDialog_File() {  
        //System.out.println("129 X2GOF_Model, createGOFDialog_File()");
        x2GOF_DataFromFile_Dialog = new X2GOF_DataFromFile_Dialog(this);
        x2GOF_DataFromFile_Dialog.constructDialogGuts();
        x2GOF_DataFromFile_Dialog.showAndWait();
        strReturnStatus = x2GOF_DataFromFile_Dialog.getReturnStatus();
        
        if (strReturnStatus.equals("OK")) {
           x2GOF_DataDialogObj = new X2GOF_DataDialogObj();
           x2GOF_DataDialogObj = x2GOF_DataFromFile_Dialog.getTheDialogObject();
           nCategories = x2GOF_DataDialogObj.getNCategories();
            if (nCategories < 2) {
                MyAlerts.showTooFewChiSquareDFAlert();
                return "Cancel";
            }
           df = nCategories - 1;
           chi2Distr = new ChiSquareDistribution(df);
           makeTheArrays();
           // Fill some of arrays
           strCategoriesAsStrings = x2GOF_DataDialogObj.getTheGOFCategories();
           categoryLabels = FXCollections.observableArrayList(strCategoriesAsStrings);
           observedCounts = x2GOF_DataDialogObj.getObservedValues();
           expectedProportions = x2GOF_DataDialogObj.getExpectedProps();
        }
        return strReturnStatus;
    }
    
    private void doChiSqGOFCalculations() {
        //System.out.println("156 X2GOF_Model, doChiSqGOFCalculations()");
        observedTotal = 0.0;  expectedTotal = 0.0; propTotal = 0.0;
        chiSquare = 0.0; /*standResidsTotal = 0.0; */ contribTotal = 0.0;
        nCellsBelow_5 = 0;
        
        for (int col = 0; col < nCategories; col++) {  
            observedTotal += observedCounts[col]; 
            propTotal += expectedProportions[col]; 
        }   
        
        for (int col = 0; col < nCategories; col++) {           
            expectedValues[col] = observedTotal * expectedProportions[col];
            
            if (expectedValues[col] < 5.0) { nCellsBelow_5++; }
            
            expectedTotal += expectedValues[col];
            resids[col] = observedCounts[col] - expectedValues[col];
            chiSquareContribution[col] = resids[col] * resids[col] / expectedValues[col];
            contribTotal += chiSquareContribution[col];
            if (resids[col] >= 0) {
                standResids[col] = Math.sqrt(chiSquareContribution[col]);
            } else {
               standResids[col] = -Math.sqrt(chiSquareContribution[col]); 
            }
            chiSquare += chiSquareContribution[col];            
        }  
        cohens_W = Math.sqrt(chiSquare / observedTotal);
        pValue = chi2Distr.getRightTailArea(chiSquare);
    }       

/*******************************************************************************
*                          Ancillary methods                                   *
* @return 
*******************************************************************************/  
    public String getGOFVariable() {return strTheGOFVariable;}
    public String[] getCategoriesAsStrings() {return strCategoriesAsStrings; }
    public double getIthObservedCount(int ith) { return observedCounts[ith]; }
    public int getNCategories() { return nCategories; }
    public int getDF() { return df; }   
    public String getIthCategory(int ith) { return categoryLabels.get(ith); }  
    public ObservableList<String> getCategoryLabels () {return categoryLabels; }   
    public int[] getObservedCounts () { return observedCounts; }
    public int getNCellsBelow5 () { return nCellsBelow_5; }    
    public int[] getObservedCountsFromFile() { return observedCountsFromFile; }
    public String[] getObservedValuesFromFile() { return strObservedValuesFromFile; }    
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


