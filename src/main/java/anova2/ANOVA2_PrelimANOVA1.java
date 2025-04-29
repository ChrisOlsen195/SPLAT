/**************************************************
 *             ANOVA2_PrelimANOVA1                *
 *                  05/24/24                      *
 *                    06:00                       *
 *************************************************/
package anova2;

import dataObjects.CatQuantDataVariable;
import dataObjects.CategoricalDataVariable;
import utilityClasses.DataUtilities;
import dataObjects.ColumnOfData;
import dataObjects.UnivariateContinDataObj;
import dataObjects.QuantitativeDataVariable;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import probabilityDistributions.FDistribution;
import splat.*;
import utilityClasses.MyAlerts;

public class ANOVA2_PrelimANOVA1 {
    // POJOs
    boolean sampleSizesAreEqual;
    
    int nLevels, dfLevels, dfError, dfTotal, totalN, n_QDVs;
    
    double minVertical, maxVertical, ssTreatments, ssError, fStat, pValue, 
           confidenceLevel, qCritPlusMinus, ssTotal, msTreatments, msError;
    
    String theExplanVar, theRespVar, returnStatus;
    
    String waldoFile = "";
    //String waldoFile = "ANOVA2_PrelimANOVA1";

    ArrayList<String> anova1Report, allTheLabels;;
    ObservableList<String> categoryLabels;
    
    // My classes
    CatQuantDataVariable cqdv;
    ColumnOfData colExplanVar, colResponseVar;
    ArrayList<QuantitativeDataVariable> allTheQDVs;
    UnivariateContinDataObj allData_UCDO;
    UnivariateContinDataObj[] allTheUCDOs;
    Data_Manager dm;

    public ANOVA2_PrelimANOVA1 (Data_Manager dm, CategoricalDataVariable explanatoryVar, QuantitativeDataVariable responseVar) {
        this.dm = dm;
        dm.whereIsWaldo(48, waldoFile, "Constructing"); 
        colExplanVar = new ColumnOfData(explanatoryVar);    
        colResponseVar = new ColumnOfData(responseVar); 
    }
    
    public String doThePrelims() {
        dm.whereIsWaldo(54, waldoFile, "doThePrelims()");
        returnStatus = "OK";
        cqdv = new CatQuantDataVariable(dm, colExplanVar, colResponseVar, true, "ANOVA2_PrelimANOVA1");
        returnStatus = cqdv.finishConstructingTidy();
        
        if (!returnStatus.equals("OK")) { return "Cancel"; }
        
        allTheQDVs = new ArrayList<>();
        allTheQDVs = cqdv.getAllQDVs();   

        n_QDVs = allTheQDVs.size();
        allTheLabels = new ArrayList<>();
        
        for (int iVars = 0; iVars < n_QDVs; iVars++) {
            allTheLabels.add(allTheQDVs.get(iVars).getTheVarLabel());
        }        
        
        categoryLabels = FXCollections.observableArrayList();

        for (int ithLabel = 0; ithLabel < allTheLabels.size(); ithLabel++) {
            categoryLabels.add(allTheLabels.get(ithLabel));
        }
        
        sampleSizesAreEqual = true;    //  Needed to distinguish T-K and HSD
              
        theExplanVar = colExplanVar.getVarLabel();
        theRespVar = colResponseVar.getVarLabel();
        
        int numUDMs = allTheQDVs.size();
        allTheUCDOs = new UnivariateContinDataObj[numUDMs];        
        for (int ithUDM = 0; ithUDM < numUDMs; ithUDM++) {
            allTheUCDOs[ithUDM] = new UnivariateContinDataObj("ANOVA2_PrelimANOVA1", allTheQDVs.get(ithUDM));
        }
        
        allData_UCDO = allTheUCDOs[0];
        nLevels = allTheQDVs.size() - 1;
        confidenceLevel = 0.95;
        anova1Report = new ArrayList<>();   
        return returnStatus;
    }
   
    public String doPrelimOneWayANOVA() {  
        dm.whereIsWaldo(98, waldoFile, "doPrelimOneWayANOVA()");
        returnStatus = setupAnalysis();
        
        if (!returnStatus.equals("OK")) { return returnStatus; }
        
        returnStatus = doAnalysis();  
        return returnStatus;      
    }
 
    private String setupAnalysis() {
        dm.whereIsWaldo(108, waldoFile, "setupAnalysis()");
        returnStatus = "OK";
        minVertical = allData_UCDO.getMinValue();
        maxVertical = allData_UCDO.getMaxValue();
        
        // Determine if sample sizes are equal (for T-K vs. HSD)
        // And while at it, get the level labels
        for (int ithLevel = 1; ithLevel < nLevels; ithLevel++) {
            int n1 = allTheUCDOs[ithLevel].getLegalN();
            int n2 = allTheUCDOs[ithLevel + 1].getLegalN();
            // Check for sufficiently large sample sizes
            
            boolean variabilityFound = DataUtilities.checkForVariabilityInQDV(allTheUCDOs[ithLevel].getTheQDV());           
            if (!variabilityFound) { 
                MyAlerts.showPossible_CRD_not_RBD();
                return "cancel";
            }
            
            if (n1 != n2) {
                sampleSizesAreEqual = false;
            }
        }        

        for (int ithLevel = 0; ithLevel <= nLevels; ithLevel++) {
            allTheUCDOs[ithLevel].doMedianBasedCalculations();
            allTheUCDOs[ithLevel].doMeanBasedCalculations();
        }
        
        return returnStatus;
    } 
    
    public String doAnalysis() {  
        dm.whereIsWaldo(140, waldoFile, "doAnalysis()");
        returnStatus = "OK";
        /********************************************************
         *           Calculate inferential statistics           *
         *******************************************************/
        ssTotal = allTheUCDOs[0].getTheSS();      
        ssError = 0.0;
        
        for (int ithLevel = 1; ithLevel <= nLevels; ithLevel++) {
            ssError += allTheUCDOs[ithLevel].getTheSS();
        }   
        
        ssTreatments = ssTotal - ssError;
        totalN = allTheUCDOs[0].getLegalN();
        dfTotal = totalN - 1;
        dfError = totalN - nLevels;
        dfLevels = nLevels - 1;
        
        msTreatments = ssTreatments / dfLevels;
        msError = ssError / dfError;

        fStat = msTreatments / msError;
        FDistribution fDist = new FDistribution( dfLevels, dfError);
        pValue = fDist.getRightTailArea(fStat);
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
   //  Print the leftmost leftChars of original
    public static String leftMostChars(String original, int leftChars) {
        String longString = original + "                       ";
        String truncated = longString.substring(0, leftChars - 1);
        return truncated;
    }
   
    public QuantitativeDataVariable getIthUDM(int ith) {
       return allTheQDVs.get(ith);
    }
   
    public UnivariateContinDataObj getIthUCDO(int ith) {
       return allTheUCDOs[ith];
    }
   
    public int getNLevels() {  return nLevels; }
    public ObservableList <String> getCategoryLabels() {return categoryLabels; }
    public UnivariateContinDataObj getAllData_UCDO() { return allData_UCDO; }
    public ArrayList<QuantitativeDataVariable> getAllTheUDMs() {return allTheQDVs; }
    public double getPostHocPlusMinus() { return qCritPlusMinus; }
   
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