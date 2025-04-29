/**************************************************
 *             ANOVA2_Factorial_Model             *
 *                  05/23/24                      *
 *                    18:00                       *
 *************************************************/
/**************************************************
 *   Unbalanced tested against Maxell p377        *
 *   Balanced tested against Tamhane p228         *
 *   UnBalanced tested against Tamhane p242       *
 *                02/17/24                        *
**************************************************/
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

public class ANOVA2_Factorial_Model {
    
    boolean dataAreBalanced;
    
    int nDataTriples, nResponses, dfRows, dfColumns, nFactorA_Levels, 
        nFactorB_Levels, dfInteraction, dfTotal, dfError;
    
    double  msRows, msColumns,  confLevel, msInteraction, msError, 
            fStat_TreatB, fStat_TreatA, fStat_Interaction, pValRows, 
            pValColumns, pValInteraction, ssRows, ssColumns, ssCells, 
            ssInteraction, ssError, ssTotal, omegaSquare_TreatA, cohensD_AB,
            omegaSquare_TreatB, omegaSquare_AB, cohensD_TreatA, cohensD_TreatB;
    
    String  str_FactorA, str_FactorB, sourceString, 
            displayChoice, meansOrBars, str_Response, returnStatus;

    String waldoFile = "";
    //String waldoFile = "ANOVA2_Factorial_Model";    
    ArrayList<String> anova2_Report;  // Are these
    
    // These are names for the levels
    ObservableList<String> str_ALevels, str_BLevels;

    // My classes  
    ANOVA2_UnbalancedModel unbalancedModel;  
    CategoricalDataVariable cdv_ALevels, cdv_BLevels, cdv_Interaction;
    ANOVA2_PrelimANOVA1 prelimA, prelimB, prelimAB;
    QuantitativeDataVariable qdv_Response;
    Data_Manager dm;
    
    public ANOVA2_Factorial_Model(Data_Manager dm,
                        ANOVA2_RCB_Controller anova2_Controller,
                        CategoricalDataVariable factorA_Var, 
                        CategoricalDataVariable factorB_Var,
                        QuantitativeDataVariable qdv_Response) {  
        this.dm = dm;
        this.cdv_ALevels = factorA_Var;
        this.cdv_BLevels = factorB_Var;
        this.qdv_Response = qdv_Response;
        dm.whereIsWaldo(63, waldoFile, "Constructing");        
        anova2_Report = new ArrayList();
        nDataTriples = qdv_Response.get_nDataPointsLegal();

        str_FactorA = factorA_Var.getTheDataLabel();
        str_FactorB = factorB_Var.getTheDataLabel();
        str_Response = qdv_Response.getTheVarLabel();
        nResponses = qdv_Response.get_nDataPointsLegal();
        dataAreBalanced = anova2_Controller.getDataAreBalanced();
    }   // End of constructor     
        
    public String doTwoWayANOVA() {
        dm.whereIsWaldo(75, waldoFile, "doTwoWayANOVA()");
        returnStatus = "OK";
        confLevel = 0.95;
        str_ALevels = FXCollections.observableArrayList();
        str_BLevels = FXCollections.observableArrayList();
        returnStatus = performInitialOneWays();
        
        if (!returnStatus.equals("OK")) {return returnStatus; }

        if (dataAreBalanced) {
            doTwoWayAnalysis();
            print_ANOVA2_Results();
        }
        
        else /* ... there is unbalance */ {
            unbalancedModel = new ANOVA2_UnbalancedModel(this,
                                                    cdv_ALevels,
                                                    cdv_BLevels,
                                                    qdv_Response);
            doTwoWayAnalysis();
            print_ANOVA2_Results();
        }
        return returnStatus;
    }

    private String performInitialOneWays() {
        dm.whereIsWaldo(101, waldoFile, "performInitialOneWays()");
        prelimA = new ANOVA2_PrelimANOVA1(dm, cdv_ALevels, qdv_Response);
        returnStatus = prelimA.doThePrelims();
        returnStatus = prelimA.doPrelimOneWayANOVA(); 

        if (!returnStatus.equals("OK")) { return returnStatus; }
        
        nFactorA_Levels = prelimA.getNLevels();
        str_ALevels = prelimA.getCategoryLabels();
       
        prelimB = new ANOVA2_PrelimANOVA1(dm, cdv_BLevels, qdv_Response);
        returnStatus = prelimB.doThePrelims();
        
        returnStatus = prelimB.doPrelimOneWayANOVA();
        
        if (!returnStatus.equals("OK")) { return returnStatus; }
        
        nFactorB_Levels = prelimB.getNLevels();       
        str_BLevels = prelimB.getCategoryLabels();
        nDataTriples = nResponses;
        cdv_Interaction = new CategoricalDataVariable ("Interactions", nDataTriples);
        
        String interactionABString;        
        for (int ithDataPoint = 0; ithDataPoint < nResponses; ithDataPoint++) {
            String dataA = cdv_ALevels.getIthDataPtAsString(ithDataPoint);
            String dataB = cdv_BLevels.getIthDataPtAsString(ithDataPoint);
            interactionABString = dataA + "&" + dataB;
            cdv_Interaction.setIthDataPtAsString(ithDataPoint, interactionABString);
        } 

        prelimAB = new ANOVA2_PrelimANOVA1(dm, cdv_Interaction, qdv_Response);
        returnStatus = prelimAB.doThePrelims();
        returnStatus = prelimAB.doPrelimOneWayANOVA();
        return returnStatus;
        
    }   //  end performOneWays
    
    private void doTwoWayAnalysis() {  
        dm.whereIsWaldo(139, waldoFile, "doTwoWayAnalysis()");
        
        if (dataAreBalanced) { 
            ssRows = prelimA.getSSTreatments();
            dfRows = prelimA.getDFLevels();
            msRows = ssRows / dfRows;

            ssColumns = prelimB.getSSTreatments();
            dfColumns = prelimB.getDFLevels(); 
            msColumns = ssColumns / dfColumns;

            ssCells = prelimAB.getSSTreatments();   // SS 'explained'
  
            ssError = prelimAB.getSSError();
            dfError = prelimAB.getDFError();
            msError = ssError / dfError;

            ssTotal = prelimAB.getSSTotal();
            dfTotal = prelimAB.getDFTotal(); 

            ssInteraction = ssCells - ssRows - ssColumns;
            dfInteraction = dfRows * dfColumns;
            msInteraction = ssInteraction / dfInteraction;      
        }
        else    /* Data are unbalanced */ {
            ssRows = unbalancedModel.getSSFactorA();
            dfRows = unbalancedModel.getDFFactorA();
            msRows = unbalancedModel.getMSFactorA();

            ssColumns = unbalancedModel.getSSFactorB();
            dfColumns = unbalancedModel.getDFFactorB();
            msColumns = unbalancedModel.getMSFactorB();  
            
            ssInteraction = unbalancedModel.getSSInteraction();
            dfInteraction = unbalancedModel.getDFInteraction();
            msInteraction = unbalancedModel.getMSInteraction(); 
            
            ssError = unbalancedModel.getSSError();
            dfError = unbalancedModel.getDFError();
            msError = unbalancedModel.getMSError(); 
            
            ssTotal = unbalancedModel.getSSTotal();
            dfTotal = unbalancedModel.getDFTotal();
        }

        fStat_TreatB = msRows / msError;
        FDistribution fDist_Rows = new FDistribution( dfRows, dfError);
        pValRows = fDist_Rows.getRightTailArea(fStat_TreatB);
        
        fStat_TreatA = msColumns / msError;
        FDistribution fDist_Columns = new FDistribution( dfColumns, dfError);
        pValColumns = fDist_Columns.getRightTailArea(fStat_TreatA);
        
        fStat_Interaction = msInteraction / msError;
        FDistribution fDist_Interaction = new FDistribution( dfInteraction, dfError);
        pValInteraction = fDist_Interaction.getRightTailArea(fStat_Interaction); 
        
        if (dataAreBalanced) { doTheEffectSizes_ANOVA2_Factorial_Balanced(); }
    } 
    
    private void print_ANOVA2_Results() {  
        dm.whereIsWaldo(200, waldoFile, "print_ANOVA2_Results()");
        anova2_Report = new ArrayList<>();    
        anova2_Report.add(String.format("-----------------------------------------------------------------------------------\n")); 
        anova2_Report.add(String.format("       Source of              Sum of\n"));
        anova2_Report.add(String.format("       Variation     df       Squares        Mean Square        F      P-value\n"));
        anova2_Report.add(String.format("-----------------------------------------------------------------------------------\n"));
        sourceString = getLeftMostChars(str_FactorA, 15);
        anova2_Report.add(String.format("%15s    %4d  %13.3f        %8.3f     %8.3f     %6.4f\n", sourceString, dfRows, ssRows,
                                                                                          msRows, fStat_TreatB, pValRows));
        sourceString = getLeftMostChars(str_FactorB, 15);
        anova2_Report.add(String.format("%15s    %4d  %13.3f        %8.3f     %8.3f     %6.4f\n", sourceString, dfColumns, ssColumns,
                                                                                          msColumns,fStat_TreatA, pValColumns));
        sourceString = getLeftMostChars(str_FactorA, 7) + "*" + getLeftMostChars(str_FactorB, 7);
        anova2_Report.add(String.format("%15s    %4d  %13.3f        %8.3f     %8.3f     %6.4f\n", sourceString, dfInteraction, ssInteraction,
                                                                                         msInteraction,fStat_Interaction, pValInteraction));
        sourceString = getLeftMostChars("Error", 15);
        anova2_Report.add(String.format("%15s    %4d  %13.3f        %8.3f\n", sourceString, dfError, ssError, msError));
        sourceString = getLeftMostChars("Total", 12);
        anova2_Report.add(String.format("%15s    %4d  %13.3f\n", sourceString, dfTotal, ssTotal));
        
        if (dataAreBalanced) {
            anova2_Report.add(String.format("\n  Omega Square for Treatment A =  %5.3f\n", omegaSquare_TreatA));
            anova2_Report.add(String.format("     Cohen's d for Treatment A =  %5.3f\n", cohensD_TreatA));

            anova2_Report.add(String.format("\n  Omega Square for Treatment B =  %5.3f\n", omegaSquare_TreatB));
            anova2_Report.add(String.format("     Cohen's d for Treatment B =  %5.3f\n", cohensD_TreatB));
            
            anova2_Report.add(String.format("\n  Omega Square for Interaction =  %5.3f\n", omegaSquare_AB));
            anova2_Report.add(String.format("     Cohen's d for Interaction =  %5.3f\n", cohensD_AB));
        }
        
        anova2_Report.add(String.format("-----------------------------------------------------------------------------------\n"));
   }    // end printANOVA_Results
    
    private void doTheEffectSizes_ANOVA2_Factorial_Balanced() {
        /************************************************************
         * Kirk, Experimental Design: Procedures for the Behavioral *
         * Sciences (4th).  pp 134- 137                             *
         ***********************************************************/ 
        dm.whereIsWaldo(239, waldoFile, "doTheEffectSizes_ANOVA2_Factorial_Balanced()");
        double omegaSquare_TreatA_Numerator = (nFactorA_Levels - 1.0) * (fStat_TreatA - 1.0);
        double omegaSquareTreatA_Denominator = omegaSquare_TreatA_Numerator + nResponses;
        omegaSquare_TreatA = omegaSquare_TreatA_Numerator / omegaSquareTreatA_Denominator;
        cohensD_TreatA = Math.sqrt(omegaSquare_TreatA / (1.0 - omegaSquare_TreatA));
        double omegaSquare_TreatB_Numerator = (nFactorB_Levels - 1.0) * (fStat_TreatB - 1.0);
        double omegaSquare_TreatB_Denominator = omegaSquare_TreatB_Numerator + nResponses;
        omegaSquare_TreatB = omegaSquare_TreatB_Numerator / omegaSquare_TreatB_Denominator;
        cohensD_TreatB = Math.sqrt(omegaSquare_TreatB / (1.0 - omegaSquare_TreatB));
        double omegaSquare_AB_Numerator = (nFactorA_Levels - 1.0) * (nFactorB_Levels - 1.0) * (fStat_Interaction - 1.0);
        double omegaSquare_AB_Denominator = omegaSquare_AB_Numerator + nResponses;
        omegaSquare_AB = omegaSquare_AB_Numerator / omegaSquare_AB_Denominator;
        cohensD_AB = Math.sqrt(omegaSquare_AB/ (1.0 - omegaSquare_AB));
    }
        
    public double getConfidenceLevel() { return confLevel; }
    
    public void setConfidenceLevel( double atThisLevel) {
        confLevel = atThisLevel;
    }
    
   private String getLeftMostChars(String original, int leftChars) {
       return StringUtilities.truncateString(original, leftChars);
   }
   
     public String getFactorALabel() {return cdv_ALevels.getTheDataLabel();}
     public String getFactorBLabel() {return cdv_BLevels.getTheDataLabel();}
     public String getResponseLabel() { return str_Response; }
     public int getNFactorA_Levels() {  return nFactorA_Levels; }
     public int getNFactorB_Levels() {  return nFactorB_Levels; }
     public String getChoiceOfPlot() { return displayChoice; }
     public String getMeansOrBars() { return meansOrBars; }
     public ObservableList <String> getCategoryLevels() { return str_ALevels; }          
     public ObservableList <String> getFactorALevels() { return str_ALevels; }     
     public ObservableList <String> getFactorBLevels() { return str_BLevels; } 
     public double getMinVertical() {return prelimA.getMinVertical(); }
     public double getMaxVertical() {return prelimA.getMaxVertical(); }  
     
     public UnivariateContinDataObj  getAllDataUCDO() {
         UnivariateContinDataObj dummyUCDO = new UnivariateContinDataObj();
         return dummyUCDO;
     }

     public ANOVA2_PrelimANOVA1 getPrelimA() { return prelimA; }
     public ANOVA2_PrelimANOVA1 getPrelimB() { return prelimB; }
     public ANOVA2_PrelimANOVA1 getPrelimAB() { return prelimAB; }     
     public ArrayList<String> getANOVA2Report() {return anova2_Report;}
     public int get_nDataTriples() {return nDataTriples; }    
     public Data_Manager getDataManager() { return dm; }
}
