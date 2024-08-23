/**************************************************
 *              RCB_PrelimANOVA1                  *
 *                  05/24/24                      *
 *                    12:00                       *
 *************************************************/
package anova2;

import dataObjects.CatQuantDataVariable;
import dataObjects.CategoricalDataVariable;
import dataObjects.ColumnOfData;
import dataObjects.UnivariateContinDataObj;
import dataObjects.QuantitativeDataVariable;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import splat.*;

public class ANOVA2_RCB_PrelimANOVA1 {
    // POJOs
    boolean sampleSizesAreEqual;
    
    int nLevels, dfLevels, dfError, dfTotal, totalN, n_QDVs;
    
    double minVertical, maxVertical, ssTreatments, ssError, 
           ssTotal, confidenceLevel, grandMean;
    
    double[] ithMean, ithEffect;
    
    String theExplanVar, theRespVar, returnStatus;
    
    // Make empty if no-print
    // String waldoFile = "ANOVA2_RCB_PrelimANOVA1";
    String waldoFile = "";
    
    ArrayList<String> anova1Report, allTheLabels;;
    ObservableList<String> categoryLabels;
    
    // My classes
    ColumnOfData colExplanVar, colResponseVar;
    CatQuantDataVariable cqdv;
    ArrayList<QuantitativeDataVariable> allTheQDVs;
    UnivariateContinDataObj ucdo_AllData;
    UnivariateContinDataObj[] ucdo_All;
    Data_Manager dm;

    public ANOVA2_RCB_PrelimANOVA1 (Data_Manager dm, CategoricalDataVariable explanatoryVar, QuantitativeDataVariable qdv_ResponseVar) {
        this.dm = dm;
        dm.whereIsWaldo(48, waldoFile, "ANOVA2_RCB_PrelimANOA1, Constructing");         
        colExplanVar = new ColumnOfData(explanatoryVar);    
        colResponseVar = new ColumnOfData(qdv_ResponseVar);
    }
    
    public String doThePrelims() {
        dm.whereIsWaldo(54, waldoFile, "doThePrelims()");
        cqdv = new CatQuantDataVariable(dm, colExplanVar, colResponseVar, true, "ANOVA2_RCB_PrelimANOVA1");
        returnStatus = cqdv.finishConstructingStacked();
        if (!returnStatus.equals("OK")) { return "Cancel"; }
        allTheQDVs = new ArrayList<>();
        allTheQDVs = cqdv.getAllQDVs();   

        n_QDVs = allTheQDVs.size();
        ithMean = new double[n_QDVs];
        ithEffect = new double[n_QDVs];
        allTheLabels = new ArrayList<>();
        
        for (int iVars = 0; iVars < n_QDVs; iVars++) {
            allTheLabels.add(allTheQDVs.get(iVars).getTheVarLabel());
        }        
        
        dm.whereIsWaldo(70, waldoFile, "in doThePrelims()");
        categoryLabels = FXCollections.observableArrayList();
        
        for (int ithLabel = 0; ithLabel < allTheLabels.size(); ithLabel++) {
            categoryLabels.add(allTheLabels.get(ithLabel));
        }
        
        sampleSizesAreEqual = true;    //  Needed to distinguish T-K and HSD
        theExplanVar = colExplanVar.getVarLabel();
        theRespVar = colResponseVar.getVarLabel();
        dm.whereIsWaldo(80, waldoFile, "doThePrelims()");
        int numUDMs = allTheQDVs.size();
        ucdo_All = new UnivariateContinDataObj[numUDMs];
        
        for (int ithUDM = 0; ithUDM < numUDMs; ithUDM++) {
            ucdo_All[ithUDM] = new UnivariateContinDataObj("RCB_PrelimANOVA1", allTheQDVs.get(ithUDM));
        }
        
        ucdo_AllData = ucdo_All[0];
        nLevels = allTheQDVs.size() - 1;
        dm.whereIsWaldo(90, waldoFile, "End doThePrelims()");
        return returnStatus;
    }
   
    public String doPrelimOneWayANOVA() {   
        dm.whereIsWaldo(95, waldoFile, "doPrelimOneWayANOVA()");
        
        returnStatus = setupAnalysis();
        if (!returnStatus.equals("OK")) { return returnStatus; }
        
        returnStatus = doAnalysis();  
        return returnStatus;
    }
    
    private String setupAnalysis() {
        dm.whereIsWaldo(105, waldoFile, "setupAnalysis()");
        returnStatus = "OK";
        minVertical = ucdo_AllData.getMinValue();
        maxVertical = ucdo_AllData.getMaxValue();
        
        // Determine if sample sizes are equal (for T-K vs. HSD)
        // And while at it, get the level labels
        for (int ithLevel = 1; ithLevel < nLevels; ithLevel++) {
            int n1 = ucdo_All[ithLevel].getLegalN();
            int n2 = ucdo_All[ithLevel + 1].getLegalN();           
            if (n1 != n2)
                sampleSizesAreEqual = false;
        }        
        
        for (int ithLevel = 0; ithLevel <= nLevels; ithLevel++) {
            ucdo_All[ithLevel].doMedianBasedCalculations();
            ucdo_All[ithLevel].doMeanBasedCalculations();
            grandMean = ucdo_All[0].getTheMean();
            ithMean[ithLevel] = ucdo_All[ithLevel].getTheMean();
            ithEffect[ithLevel] = ithMean[ithLevel] - grandMean;
        }
        
        return returnStatus;
    }
    
    public String doAnalysis() {  
        dm.whereIsWaldo(131, waldoFile, "doAnalysis()");
        ssTotal = ucdo_All[0].getTheSS();      
        ssError = 0.0;
        for (int ithLevel = 1; ithLevel <= nLevels; ithLevel++) {
            ssError += ucdo_All[ithLevel].getTheSS();
        }   
        
        ssTreatments = ssTotal - ssError;

        totalN = ucdo_All[0].getLegalN();
        dfTotal = totalN - 1;
        dfError = totalN - nLevels;
        dfLevels = nLevels - 1;

        return returnStatus;
    } 
    
    public String getExplanatoryVariable() {return theExplanVar; }
    public String getResponseVariable() {return theRespVar; }    
    public ArrayList<String> getANOVA1Report() { return anova1Report; }      
    public boolean getAreSampleSizesEqual() {return sampleSizesAreEqual; }
    public double getConfidenceLevel() { return confidenceLevel; }
    
    public void setConfidenceLevel( double atThisLevel) {
        confidenceLevel = atThisLevel;
    }
    
    public QuantitativeDataVariable getIthQDV(int ith) {
       return allTheQDVs.get(ith);
   }
      
    public double getGrandMean() { return grandMean; }
    public double getIthMean(int ith) { return ithMean[ith]; }
    public double getIthEffect(int ith) { return ithEffect[ith]; }
      
    public UnivariateContinDataObj getIthUCDO(int ith) {
      return ucdo_All[ith];
   }
   
   public int getNLevels() {  return nLevels; }
   
   public ObservableList <String> getCategoryLabels() {return categoryLabels; }
   public UnivariateContinDataObj getAllData_UCDO() { return ucdo_AllData; }
   public ArrayList<QuantitativeDataVariable> getAllTheQDVs() {return allTheQDVs; }

   // ***********************************************************
   //   The gets() below are for Two-Way ANOVA calculations
   // ***********************************************************
   
   public double getSSTreatments() {return ssTreatments; }
   public int getDFLevels() {return dfLevels; } 
   
   public double getSSError() {return ssError; }
   public int getDFError() {return dfError; } 
   
   public double getSSTotal() {return ssTotal; }
   public int getDFTotal() {return dfTotal; } 
   
   public double getMinVertical() { return minVertical; }
   public double getMaxVertical() { return maxVertical; }
}