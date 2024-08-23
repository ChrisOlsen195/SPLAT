/**************************************************
 *               ANOVA2_RCB_Model                 *
 *                  05/24/24                      *
 *                    12:00                       *
 *************************************************/
package anova2;

import utilityClasses.StringUtilities;
import dataObjects.CategoricalDataVariable;
import dataObjects.UnivariateContinDataObj;
import dataObjects.QuantitativeDataVariable;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import probabilityDistributions.FDistribution;
import splat.*;
import utilityClasses.MyAlerts;

public class ANOVA2_RCB_Model {
    // POJOs
    boolean replicatesExist, balanceExists;
    
    int nDataTriples, nLegalResponses, dfBlocks, dfTreats, nBlocks, nTreats, 
        dfInteraction, dfTotal, dfError, nReplications; //, dfWithIn;
    
    double  msBlocks, msTreats, confLevel, msInteraction, msError, fStatRows,
            fStatColumns, fStatInteraction, pValRows, pValColumns, ssBlocks, 
            ssTreats, ssInteraction, ssCells, ssError, ssTotal, pValInteraction,
            omegaSquare_Treats, cohensF_Treats, omegaSquare_Blocks, cohensF_Blocks;
            // ssWithIn, msWithIn;
    
    double omegaSquare_Treats_Numerator, omegaSquareTreats_Denominator,
           omegaSquare_Blocks_Numerator, omegaSquareBlocks_Denominator;
    
    String  blocksLabel, treatsLabel, responseLabel, sourceString, 
            displayChoice, meansOrBars, returnStatus, whichANOVA2;
    ArrayList<String> rcb_Report;    
    ObservableList<String> blockLevels, treatLevels;
    
    //String  waldoFile = "";
    String waldoFile = "ANOVA2_RCB_Model";
    
    // My classes
    ANOVA2_RCB_Controller anova2_RCB_Controller;
    ANOVA2_RCB_PrelimANOVA1 prelimBlocks, prelimTreats, prelimAB;
    ANOVA2_RM_Controller anova2_RM_Controller;
    CategoricalDataVariable blockValues, treatValues, factor_AB_CatDataVar;
    Data_Manager dm;    //  Need for randomized block w/o reps
    QuantitativeDataVariable qdv_ResponseValues;
    
    public ANOVA2_RCB_Model( Data_Manager dm,
                        ANOVA2_RCB_Controller anova2_RCB_Controller,
                        CategoricalDataVariable treatValues, 
                        CategoricalDataVariable blockValues,
                        QuantitativeDataVariable qdv_ResponseValues) { 
        this.dm = dm;

        this.anova2_RCB_Controller = anova2_RCB_Controller;
        this.blockValues = blockValues;
        this.treatValues = treatValues;
        // qdv_ResponseValues are all the legal values
        this.qdv_ResponseValues = qdv_ResponseValues;
        whichANOVA2 = "RCB";
        dm.whereIsWaldo(64, waldoFile, "Constructing");         
        rcb_Report = new ArrayList();
        nDataTriples = qdv_ResponseValues.get_nDataPointsLegal();
        blocksLabel = blockValues.getTheDataLabel();
        treatsLabel = treatValues.getTheDataLabel();
        responseLabel = qdv_ResponseValues.getTheVarLabel();
        nLegalResponses = qdv_ResponseValues.get_nDataPointsLegal();
    }     
    
    public ANOVA2_RCB_Model( Data_Manager dm,
                        ANOVA2_RM_Controller anova2_RM_Controller,
                        CategoricalDataVariable treatValues,
                        CategoricalDataVariable blockValues, 
                        QuantitativeDataVariable responseValues) { 
        this.dm = dm;
        dm.whereIsWaldo(79, waldoFile, "Constructing, RM Model");
        this.anova2_RM_Controller = anova2_RM_Controller;
        this.blockValues = blockValues;
        this.treatValues = treatValues;
        this.qdv_ResponseValues = responseValues;
        whichANOVA2 = "RM";
        rcb_Report = new ArrayList();        
        nDataTriples = responseValues.get_nDataPointsLegal();        
        blocksLabel = blockValues.getTheDataLabel();
        treatsLabel = treatValues.getTheDataLabel();
        responseLabel = responseValues.getTheVarLabel();
        nLegalResponses = responseValues.get_nDataPointsLegal();
    }   
    
    
    public String doTwoWayANOVA() {
        dm.whereIsWaldo(95, waldoFile, "doTwoWayANOVA()");
        returnStatus = "OK";
        confLevel = 0.95;
        blockLevels = FXCollections.observableArrayList();
        treatLevels = FXCollections.observableArrayList();

        returnStatus = performInitialOneWays();
        
        if (!returnStatus.equals("OK")) {return returnStatus; }
        
        switch (whichANOVA2) {
            case "RCB":
            if (anova2_RCB_Controller.getDataAreBalanced() == true) {
                balanceExists = true;
                replicatesExist = true;

                if (anova2_RCB_Controller.getReplicatesExist() == false) {
                    replicatesExist = false;
                    doRCBAnalysis_NoReplicates();
                    print_ANOVA2_Results_1();
                }
                else {
                    replicatesExist = true;
                    doRCBAnalysis_WithReplicates();
                    print_ANOVA2_Results_n();
                }     
            }
            else { balanceExists = false; }
            break;

            case "RM":
            if (anova2_RM_Controller.getDataAreBalanced() == true) {
                balanceExists = true;
                replicatesExist = true;

                if (anova2_RM_Controller.getReplicatesExist() == false) {
                    replicatesExist = false;
                    doRCBAnalysis_NoReplicates();
                    print_ANOVA2_Results_1();
                }
                else {
                    replicatesExist = true;
                    doRCBAnalysis_WithReplicates();
                    print_ANOVA2_Results_n();
                }     
            }
            else { balanceExists = false;}
            break;
            
            default:
                String switchFailure = "Switch failure: ANOVA2_RVB_Model 145 " + whichANOVA2;
                MyAlerts.showUnexpectedErrorAlert(switchFailure); 

        }
        return returnStatus;     
    }

    private String performInitialOneWays() {
        dm.whereIsWaldo(153, waldoFile, "performInitialOneWays()");
        returnStatus = "OK";
        prelimBlocks = new ANOVA2_RCB_PrelimANOVA1(dm, blockValues, qdv_ResponseValues);
        returnStatus = prelimBlocks.doThePrelims();
        
        if (!returnStatus.equals("OK")) { return returnStatus; }
        
        returnStatus = prelimBlocks.doPrelimOneWayANOVA();         
        if (!returnStatus.equals("OK")) { return returnStatus; }
        
        nBlocks = prelimBlocks.getNLevels();
        blockLevels = prelimBlocks.getCategoryLabels();
        prelimTreats = new ANOVA2_RCB_PrelimANOVA1(dm, treatValues, qdv_ResponseValues);
        returnStatus = prelimTreats.doThePrelims();
        
        if (!returnStatus.equals("OK")) { return returnStatus; }
        
        returnStatus = prelimTreats.doPrelimOneWayANOVA();        
        if (!returnStatus.equals("OK")) { return returnStatus; }
        
        nTreats = prelimTreats.getNLevels();       
        treatLevels = prelimTreats.getCategoryLabels();
        nDataTriples = nLegalResponses;
        factor_AB_CatDataVar = new CategoricalDataVariable ("Interactions", nDataTriples);
        return returnStatus;
    }   
    
    private void doRCBAnalysis_NoReplicates() {  
        dm.whereIsWaldo(181, waldoFile, "doRCBAnalysis_NoReplicates()");
        ssBlocks = prelimBlocks.getSSTreatments();
        dfBlocks = prelimBlocks.getDFLevels();
        msBlocks = ssBlocks / dfBlocks;

        ssTreats = prelimTreats.getSSTreatments();
        dfTreats = prelimTreats.getDFLevels(); 
        msTreats = ssTreats / dfTreats;

        ssTotal = qdv_ResponseValues.getTheSS();
        dfTotal = nLegalResponses - 1;

        ssError = ssTotal - ssTreats - ssBlocks;
        dfError = (nBlocks - 1) * (nTreats - 1);
        msError = ssError / dfError;

        //  For repeated measures
        ssInteraction = ssTotal - ssTreats - ssBlocks;
        dfInteraction = (nTreats - 1) + (nBlocks - 1);
        
        //ssWithIn = ssBlocks + ssError;
        //dfWithIn = dfBlocks + dfError;
        //msWithIn = ssWithIn / dfWithIn;

        fStatRows = msBlocks / msError;
        FDistribution fDistRows = new FDistribution( dfBlocks, dfError);
        pValRows = fDistRows.getRightTailArea(fStatRows);
        
        fStatColumns = msTreats / msError;
        FDistribution fDistColumns = new FDistribution( dfTreats, dfError);
        pValColumns = fDistColumns.getRightTailArea(fStatColumns);
        
        doTheEffectSizes_RCB_NoReplicates();
    }  
    
    private String doRCBAnalysis_WithReplicates() {  
        dm.whereIsWaldo(217, waldoFile, "doRCBAnalysis_WithReplicates()");
        nReplications = anova2_RCB_Controller.getNReplications();
        String interactionABString;
        factor_AB_CatDataVar = new CategoricalDataVariable ("Interactions", nDataTriples);
        
        for (int ithDataPoint = 0; ithDataPoint < nLegalResponses; ithDataPoint++) {
            String dataA = blockValues.getIthDataPtAsString(ithDataPoint);
            String dataB = treatValues.getIthDataPtAsString(ithDataPoint);
            interactionABString = dataA + "&" + dataB;
            factor_AB_CatDataVar.setIthDataPtAsString(ithDataPoint, interactionABString);
        }        
        
        prelimAB = new ANOVA2_RCB_PrelimANOVA1(dm, factor_AB_CatDataVar, qdv_ResponseValues);
        returnStatus = prelimAB.doThePrelims();
        
        if (!returnStatus.equals("OK")) { return returnStatus; }
        
        returnStatus = prelimAB.doPrelimOneWayANOVA();        
        if (!returnStatus.equals("OK")) { return returnStatus; }
        
        ssBlocks = prelimBlocks.getSSTreatments();
        dfBlocks = prelimBlocks.getDFLevels();
        msBlocks = ssBlocks / dfBlocks;

        ssTreats = prelimTreats.getSSTreatments();
        dfTreats = prelimTreats.getDFLevels(); 
        msTreats = ssTreats / dfTreats;

        ssCells = prelimAB.getSSTreatments();

        ssInteraction = ssCells - ssBlocks - ssTreats;
        dfInteraction = dfBlocks * dfTreats;
        msInteraction = ssInteraction / dfInteraction;

        ssTotal = qdv_ResponseValues.getTheSS();
        dfTotal = nLegalResponses - 1;

        ssError = ssTotal - ssTreats - ssBlocks - ssInteraction;
        dfError = nBlocks * nTreats * (nReplications - 1);
        msError = ssError / dfError;

        fStatRows = msBlocks / msError;
        FDistribution fDistRows = new FDistribution( dfBlocks, dfError);
        pValRows = fDistRows.getRightTailArea(fStatRows);
        
        fStatColumns = msTreats / msError;
        FDistribution fDistColumns = new FDistribution( dfTreats, dfError);
        pValColumns = fDistColumns.getRightTailArea(fStatColumns);
        
        fStatInteraction = msInteraction / msError;
        FDistribution fDistInteraction = new FDistribution( dfInteraction, dfError);
        pValInteraction = fDistInteraction.getRightTailArea(fStatInteraction); 
        return returnStatus;
    }  
    
    private void print_ANOVA2_Results_1() { 
        dm.whereIsWaldo(273, waldoFile, "print_ANOVA2_Results_1()");
        rcb_Report = new ArrayList<>();    
        rcb_Report.add(String.format("-----------------------------------------------------------------------------------\n"));
        rcb_Report.add(String.format("       Source of              Sum of\n"));
        rcb_Report.add(String.format("       Variation     df       Squares        Mean Square          F      P-value\n"));
        rcb_Report.add(String.format("-----------------------------------------------------------------------------------\n"));
        sourceString = leftMostChars(treatsLabel, 12);
        rcb_Report.add(String.format("%15s    %4d    %13.3f    %13.3f     %8.3f     %6.4f\n", sourceString, dfTreats, ssTreats,
                                                                                          msTreats,fStatColumns, pValColumns));
        sourceString = leftMostChars(blocksLabel, 12);
        rcb_Report.add(String.format("%15s    %4d    %13.3f    %13.3f     %8.3f     %6.4f\n", sourceString, dfBlocks, ssBlocks,
                                                                                          msBlocks, fStatRows, pValRows));
        sourceString = leftMostChars("Error", 12);
        rcb_Report.add(String.format("%15s    %4d    %13.3f    %13.3f\n", sourceString, dfError, ssError, msError));
        sourceString = leftMostChars("Total", 12);
        rcb_Report.add(String.format("%15s    %4d    %13.3f\n", sourceString, dfTotal, ssTotal));

        switch (whichANOVA2) {
            case "Factorial":

                break;  
            
            case "RCB":     // Randomized complete block
                rcb_Report.add(String.format("\nOmega Square for Treatments =  %5.3f\n", omegaSquare_Treats));
                rcb_Report.add(String.format("   Cohen's f for Treatments =  %5.3f\n", cohensF_Treats));
                rcb_Report.add(String.format("\n    Omega Square for Blocks =  %5.3f\n", omegaSquare_Blocks));
                rcb_Report.add(String.format("       Cohen's f for Blocks =  %5.3f\n", cohensF_Blocks));
                break;
                
            case "RM":      // Repeated measures    
                rcb_Report.add(String.format("\nOmega Square for Treatments =  %5.3f\n", omegaSquare_Treats));
                rcb_Report.add(String.format("   Cohen's f for Treatments =  %5.3f\n", cohensF_Treats));
                rcb_Report.add(String.format("\n  Omega Square for Subjects =  %5.3f\n", omegaSquare_Blocks));
                rcb_Report.add(String.format("     Cohen's f for Subjects =  %5.3f\n", cohensF_Blocks));
                break;
                
                default:
                    String switchFailure = "Switch failure: ANOVA2_RVB_Model 310 " + whichANOVA2;
                    MyAlerts.showUnexpectedErrorAlert(switchFailure);                 
        }        
        
        rcb_Report.add(String.format("-----------------------------------------------------------------------------------\n"));
   }    // end printANOVA_Results
    
    private void print_ANOVA2_Results_n() {  
        dm.whereIsWaldo(318, waldoFile, "print_ANOVA2_Results_n()");
        rcb_Report = new ArrayList<>();    
        rcb_Report.add(String.format("-----------------------------------------------------------------------------------\n"));
        rcb_Report.add(String.format("       Source of              Sum of\n"));
        rcb_Report.add(String.format("       Variation     df       Squares        Mean Square        F      P-value\n"));
        rcb_Report.add(String.format("-----------------------------------------------------------------------------------\n"));
        sourceString = leftMostChars(treatsLabel, 16);
        rcb_Report.add(String.format("%15s    %4d  %13.3f        %8.3f     %8.3f     %6.4f\n", sourceString, dfTreats, ssTreats,
                                                                                          msTreats,fStatColumns, pValColumns));
        sourceString = leftMostChars(blocksLabel, 16);
        rcb_Report.add(String.format("%15s    %4d  %13.3f        %8.3f     %8.3f     %6.4f\n", sourceString, dfBlocks, ssBlocks,
                                                                                          msBlocks, fStatRows, pValRows));
        sourceString = leftMostChars(treatsLabel, 7) + "*" + leftMostChars(blocksLabel, 7);       
        rcb_Report.add(String.format("%15s    %4d  %13.3f        %8.3f     %8.3f     %6.4f\n", sourceString, dfInteraction, ssInteraction,
                                                                                          msInteraction, fStatInteraction, pValInteraction));
        sourceString = leftMostChars("Error", 16);
        rcb_Report.add(String.format("%15s    %4d  %13.3f        %8.3f\n", sourceString, dfError, ssError, msError));
        
        sourceString = leftMostChars("Total", 16);
        rcb_Report.add(String.format("%15s    %4d  %13.3f\n", sourceString, dfTotal, ssTotal));
        rcb_Report.add(String.format("-----------------------------------------------------------------------------------\n"));
   }    // end printANOVA_Results
    
    private void doTheEffectSizes_RCB_NoReplicates() {
        /************************************************************
         * Kirk, Experimental Design: Procedures for the Behavioral *
         * Sciences (4th).  pp 134- 137                             *
         ***********************************************************/ 
        dm.whereIsWaldo(346, waldoFile, "doTheEffectSizes_RCB_NoReplicates()");
        
        if (whichANOVA2.equals("RCB")) {
            // Kirk, p293
            omegaSquare_Treats_Numerator = (nTreats - 1.0) * (fStatColumns - 1.0);
            omegaSquareTreats_Denominator = omegaSquare_Treats_Numerator + nTreats * nBlocks;
            omegaSquare_Treats = omegaSquare_Treats_Numerator / omegaSquareTreats_Denominator;
            if (omegaSquare_Treats < 0) {
                omegaSquare_Treats = 0;
            }
            cohensF_Treats = Math.sqrt(omegaSquare_Treats / (1.0 - omegaSquare_Treats));

            omegaSquare_Blocks_Numerator = (nBlocks - 1.0) * (fStatRows - 1.0);
            omegaSquareBlocks_Denominator = omegaSquare_Blocks_Numerator + nTreats * nBlocks;
            omegaSquare_Blocks = omegaSquare_Blocks_Numerator / omegaSquareBlocks_Denominator;
            
            if (omegaSquare_Blocks < 0) {
                omegaSquare_Blocks = 0;
            }

            cohensF_Blocks = Math.sqrt(omegaSquare_Blocks / (1.0 - omegaSquare_Blocks));

        }

        else if (whichANOVA2.equals("RM")) {
            System.out.println("371 ANOVA2_RCB_Model -- SWITCH ACCESS PROBLEM, whichANOVA2 = RM");
            /*
            omegaSquare_Treats_Numerator = ssTreats - (nTreats- 1) * msWithIn;
            omegaSquareTreats_Denominator = ssTotal + msWithIn;
            System.out.println("407 RCB/RM_Model, nTreats/msWithin = " + nTreats + " / " + msWithIn);
            System.out.println("408 RCB/RM_Model, num/den = " + omegaSquare_Treats_Numerator + " / " + omegaSquareTreats_Denominator);
            omegaSquare_Treats = omegaSquare_Treats_Numerator / omegaSquareTreats_Denominator;
            if (omegaSquare_Treats < 0) {
                omegaSquare_Treats = 0;
            }
            cohensF_Treats = Math.sqrt(omegaSquare_Treats / (1.0 - omegaSquare_Treats));

            omegaSquare_Blocks_Numerator = (nBlocks - 1.0) * (fStatRows - 1.0);
            omegaSquareBlocks_Denominator = omegaSquare_Blocks_Numerator + nTreats * nBlocks;
            System.out.println("403 RM_Model, num/den = " + omegaSquare_Blocks_Numerator + " / " + omegaSquareBlocks_Denominator);
            omegaSquare_Blocks = omegaSquare_Blocks_Numerator / omegaSquareBlocks_Denominator;
            if (omegaSquare_Blocks < 0) {
                omegaSquare_Blocks = 0;
            }
            cohensF_Blocks = Math.sqrt(omegaSquare_Blocks / (1.0 - omegaSquare_Blocks));
            */
        }
    }
        
    public double getConfidenceLevel() { return confLevel; }
    
    public void setConfidenceLevel( double atThisLevel) {
        confLevel = atThisLevel;
    }
    
   //  Print the leftmost leftChars of original
    private String leftMostChars(String original, int leftChars) {
        return StringUtilities.truncateString(original, leftChars);
    }
   
    public String getBlockLabel() {return blockValues.getTheDataLabel();}
    public String getTreatmentLabel() {return treatValues.getTheDataLabel();}
    public String getResponseLabel() { return responseLabel; }

     public int getNFactorA_Levels() {  return nBlocks; }
     public int getNFactorB_Levels() {  return nTreats; }

     public String getChoiceOfPlot() { return displayChoice; }
     public String getMeansOrBars() { return meansOrBars; }

     public ObservableList <String> getCategoryLevels() {  return blockLevels; }     
     public ObservableList <String> getFactorALevels() {  return blockLevels; }
     public ObservableList <String> getFactorBLevels() { return treatLevels; } 

     public double getMinVertical() {return prelimBlocks.getMinVertical(); }
     public double getMaxVertical() {return prelimBlocks.getMaxVertical(); }  
     
     public UnivariateContinDataObj  getAllDataUCDO() {
         UnivariateContinDataObj dummyUCDO = new UnivariateContinDataObj();
         return dummyUCDO;
     }
     
    public boolean getReplicatesExist() { return replicatesExist; }
    public boolean getBalanceExists() { return balanceExists; }

    public ANOVA2_RCB_PrelimANOVA1 getPrelimA() { return prelimBlocks; }
    public ANOVA2_RCB_PrelimANOVA1 getPrelimB() { return prelimTreats; }
    public ANOVA2_RCB_PrelimANOVA1 getPrelimAB() { return prelimAB; }
    
    public CategoricalDataVariable getRMTreatValues() { return treatValues; }
    public CategoricalDataVariable getRMSubjectValues() { return blockValues; }
    public QuantitativeDataVariable getRMResponseValues() { return qdv_ResponseValues; }
     
    public ArrayList<String> getANOVA2Report() {  return rcb_Report; }
    public String getWhichANOVA2() { return whichANOVA2; }
     
     public Data_Manager getDataManager() { return dm; }
     public int get_nDP() {return nDataTriples; }
}
