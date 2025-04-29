/**************************************************
 *                ANOVA2_RM_Model                 *
 *                  02/23/25                      *
 *                    15:00                       *
 *************************************************/
/**************************************************
 *          Tested against Cohen p513             *
 *                   02/17/24                     *
 *************************************************/
package anova2;

import utilityClasses.StringUtilities;
import dataObjects.CategoricalDataVariable;
import dataObjects.ColumnOfData;
import dataObjects.UnivariateContinDataObj;
import dataObjects.QuantitativeDataVariable;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import probabilityDistributions.FDistribution;
import probabilityDistributions.StudentizedRangeQ;
import probabilityDistributions.TDistribution;
import splat.*;
import utilityClasses.MyAlerts;

public class ANOVA2_RM_Model {
    // POJOs
    boolean replicatesExist, balanceExists;
    
    int nLegalResponses, dfTreatments, dfSubjects, nBlocks, 
        nTreatments, nSubjects, dfTotal, dfError, dfWithin;
    int nOriginalColumns;
    
    double  msTreatments, msSubjects, msError, fStatTreatments,
            fStatSubjects, pValTreatments, pValSubjects, ssTreatments, 
            ssSubjects, ssError, ssTotal, omegaSquare_Subjects, 
            cohensF_Subjects, omegaSquare_Treatments, cohensF_Treatments,
            ssWithin, msWithin, partial_eta_squared;
    
    double lowCI_TK, highCI_TK, qCritPlusMinus, qTK, wT;
    
    double omegaSquare_Treats_Numerator, omegaSquareTreats_Denominator,
           omegaSquare_Subjects_Numerator, omegaSquareSubjects_Denominator;
    
    double[] middleInterval;
    
    String  treatmentsLabel, subjectsLabel, responseLabel, sourceString, 
            returnStatus, strIthLevel, strJthLevel,thisVarLabel, thisVarDescr;
    
    ObservableList<String> treatmentLevels, subjectLevels;
    
        String waldoFile = "";
        //String waldoFile = "ANOVA2_RM_Model"; 

    ArrayList<String> rm_Report, sphericity_Report, alStr_AllTheLabels;
    
    // My classes
    ANOVA2_RCB_PrelimANOVA1 prelimTreatments, prelimSubjects, prelimAB;
    ANOVA2_RM_Calculations anova2_RM_Calculations;
    ANOVA2_RM_Controller rm_Controller;
    CategoricalDataVariable treatmentValues, subjectValues;
    ColumnOfData[] originalDataColumns;
    Data_Manager dm; 
    QuantitativeDataVariable responseValues, tempQDV;
    ArrayList<QuantitativeDataVariable> allTheQDVs;
    StudentizedRangeQ studRangeQ;
    TDistribution tDistribution;
       
    public ANOVA2_RM_Model( Data_Manager dm,
                        ANOVA2_RM_Controller rm_Controller,
                        CategoricalDataVariable subjectValues,
                        CategoricalDataVariable treatmentValues, 
                        QuantitativeDataVariable responseValues) { 
        dm.whereIsWaldo(74, waldoFile, "Constructing, RM Model"); 
        this.dm = dm;
        this.rm_Controller = rm_Controller;
        this.treatmentValues = treatmentValues;
        this.subjectValues = subjectValues;
        this.responseValues = responseValues;
        anova2_RM_Calculations = rm_Controller.get_RM_Calculations();
        rm_Report = new ArrayList();
        treatmentsLabel = treatmentValues.getTheDataLabel();
        subjectsLabel = subjectValues.getTheDataLabel();
        responseLabel = responseValues.getTheVarLabel();
        nLegalResponses = responseValues.get_nDataPointsLegal();
        grabTheOriginalData();
    } 
    
    public String doTwoWayANOVA() {
        dm.whereIsWaldo(90, waldoFile, " -- doTwoWayANOVA()");
        returnStatus = "OK";
        treatmentLevels = FXCollections.observableArrayList();
        subjectLevels = FXCollections.observableArrayList();
        returnStatus = performInitialOneWays();
        
        if (!returnStatus.equals("OK")) {
            MyAlerts.showUnexpectedErrorAlert("RM_Model, performing Initial One Ways");
            return returnStatus; 
        }   
        
        if (rm_Controller.getDataAreBalanced() == true) {
            balanceExists = true;
            replicatesExist = true;

            if (rm_Controller.getReplicatesExist() == false) {
                replicatesExist = false;
                doRMAnalysis(); 
            }
            else { replicatesExist = true; }     
        }
        else  {  balanceExists = false; }
            
        return returnStatus;     
    }

    private String performInitialOneWays() {
        dm.whereIsWaldo(117, waldoFile, " -- performInitialOneWays()");
        returnStatus = "OK";        
        prelimTreatments = new ANOVA2_RCB_PrelimANOVA1(dm, treatmentValues, responseValues);
        
        returnStatus = prelimTreatments.doThePrelims();        
        if (!returnStatus.equals("OK")) { return returnStatus; }
        
        returnStatus = prelimTreatments.doPrelimOneWayANOVA();         
        if (!returnStatus.equals("OK")) { return returnStatus; }
        
        nTreatments = prelimTreatments.getNLevels();
        treatmentLevels = prelimTreatments.getCategoryLabels();
        prelimSubjects = new ANOVA2_RCB_PrelimANOVA1(dm, subjectValues, responseValues);
        returnStatus = prelimSubjects.doThePrelims();
        
        if (!returnStatus.equals("OK")) { return returnStatus; }
        
        returnStatus = prelimSubjects.doPrelimOneWayANOVA();        
        if (!returnStatus.equals("OK")) { return returnStatus; }
        
        nSubjects = prelimSubjects.getNLevels();
        subjectLevels = prelimSubjects.getCategoryLabels();
        return returnStatus;
    }   
    
    private void doRMAnalysis() {  
        dm.whereIsWaldo(143, waldoFile, " -- doRMAnalysis()");
        ssTreatments = prelimTreatments.getSSTreatments();
        dfTreatments = prelimTreatments.getDFLevels();
        msTreatments = ssTreatments / dfTreatments;

        ssSubjects = prelimSubjects.getSSTreatments();
        dfSubjects = prelimSubjects.getDFLevels(); 
        msSubjects = ssSubjects / dfSubjects;
  
        ssTotal = responseValues.getTheSS();
        dfTotal = nLegalResponses - 1;

        ssError = ssTotal - ssSubjects - ssTreatments;
        dfError = (nSubjects - 1) * (nTreatments - 1);
        msError = ssError / dfError;

        ssWithin = ssSubjects + ssError;
        dfWithin = dfSubjects + dfError;
        msWithin = ssWithin / dfWithin;

        fStatTreatments = msTreatments / msError;
        FDistribution fDistBlocks = new FDistribution( dfTreatments, dfError);
        pValTreatments = fDistBlocks.getRightTailArea(fStatTreatments);
        
        fStatSubjects = msSubjects / msError;
        FDistribution fDistTreats = new FDistribution( dfSubjects, dfError);
        pValSubjects = fDistTreats.getRightTailArea(fStatSubjects);

        doTheEffectSizes();
    } 
    
    private void grabTheOriginalData() { 
        nOriginalColumns = dm.getNVarsInStruct();
        nTreatments = nOriginalColumns - 1;
        originalDataColumns = new ColumnOfData[nTreatments+1];
        
        for (int ithCol = 0; ithCol < nOriginalColumns; ithCol++) {
            originalDataColumns[ithCol] = dm.getAllTheColumns().get(ithCol);
        }
        
        nSubjects = originalDataColumns[0].getNCasesInColumn();
        
        // Construct the All The QDVs.
        // Stack the columns into one, put in allTheQDVs[0]
        // Construct a ColumnOfData, make the QDV
        thisVarLabel = "All"; thisVarDescr = "All";
        
        // Construct QDV(0)
        ArrayList<String> tempAlStr = new ArrayList<>();
        
        for (int ith = 1; ith <= nTreatments; ith++) {
            ColumnOfData tempCol = originalDataColumns[ith];
            int nColSize = tempCol.getColumnSize();            
            for (int jth = 0; jth < nColSize; jth++) {
                tempAlStr.add(tempCol.getTheCases_ArrayList().get(jth));
            }
        }
        
        ColumnOfData tempCOD = new ColumnOfData(dm, thisVarLabel, thisVarDescr, tempAlStr);
        tempQDV = new QuantitativeDataVariable(thisVarLabel, thisVarDescr,tempCOD);
        allTheQDVs = new ArrayList();
        allTheQDVs.add(tempQDV);
        
        for (int ith = 0; ith < nTreatments; ith++) {
            thisVarLabel = originalDataColumns[ith + 1].getVarLabel();
            thisVarDescr = originalDataColumns[ith + 1].getVarDescription();
            tempQDV = new QuantitativeDataVariable(thisVarLabel, thisVarDescr, originalDataColumns[ith + 1]); 
            allTheQDVs.add(tempQDV);                 
        }       
        
        alStr_AllTheLabels = new ArrayList<>();        
        for (int iVars = 0; iVars < nTreatments + 1; iVars++) {
            alStr_AllTheLabels.add(allTheQDVs.get(iVars).getTheVarLabel());
        }            
    }  
    
private void printTheStuff() {  
        dm.whereIsWaldo(220, waldoFile, "printTheStuff()");
        rm_Report.add(String.format("\n"));
        rm_Report.add(String.format("               **********         Parameter estimates for Levels         **********\n\n"));
        rm_Report.add(String.format("   Treatment/   Sample     Sample      Sample     Std Err     Margin     Lower 95PC  Upper 95PC\n"));
        rm_Report.add(String.format("     Group       Size       Mean       St Dev     of mean    of Error      Bound        Bound\n"));
        
        for (int ithLevel = 1; ithLevel <= nTreatments; ithLevel++) {
            strIthLevel = StringUtilities.truncateString(alStr_AllTheLabels.get(ithLevel), 10);
            int iSampleSize = allTheQDVs.get(ithLevel).getLegalN();
            double iMean = allTheQDVs.get(ithLevel).getTheMean();
            double iStandDev = allTheQDVs.get(ithLevel).getTheStandDev();
            double iStandErr = iStandDev / Math.sqrt(iSampleSize - 1.0);
            
            // tDistribution.set_df_for_t(iSampleSize - 1);
            tDistribution = new TDistribution(iSampleSize - 1);
            middleInterval = new double[2];
            middleInterval = tDistribution.getInverseMiddleArea(0.95);
            double critical_t = middleInterval[1];
            double iMarginOfError = critical_t * iStandErr;
            double iLowerBound = iMean - iMarginOfError;
            double iUpperBound = iMean + iMarginOfError;
            rm_Report.add(String.format("%10s      %4d     %8.3f    %8.3f    %8.3f   %8.3f     %8.3f     %8.3f\n", strIthLevel,  
                                                                                                            iSampleSize,
                                                                                                            iMean,
                                                                                                            iStandDev,
                                                                                                            iStandErr,
                                                                                                            iMarginOfError,
                                                                                                            iLowerBound,
                                                                                                            iUpperBound));
        }     
    }

    public void print_ANOVA2_Results_1() { 
        dm.whereIsWaldo(253, waldoFile, " -- print_ANOVA2_Results_1()");
        rm_Report = new ArrayList<>();    
        rm_Report.add(String.format("-----------------------------------------------------------------------------------\n"));
        rm_Report.add(String.format("       Source of      Sum of\n"));
        rm_Report.add(String.format("       Variation      Squares       df       Mean Square        F       P-value\n"));
        rm_Report.add(String.format("-----------------------------------------------------------------------------------\n"));
        sourceString = leftMostChars(treatmentsLabel, 12);
        rm_Report.add(String.format("%15s %13.3f     %4d    %13.3f     %8.3f     %6.4f\n", sourceString, ssTreatments, dfTreatments, 
                                                                                          msTreatments, fStatTreatments, pValTreatments));        
        sourceString = leftMostChars(subjectsLabel, 12);
        rm_Report.add(String.format("%15s %13.3f     %4d    %13.3f     %8.3f     %6.4f\n", sourceString, ssSubjects, dfSubjects, 
                                                                                          msSubjects,fStatSubjects, pValSubjects));
        sourceString = leftMostChars("Error", 12);
        rm_Report.add(String.format("%15s %13.3f     %4d    %13.3f\n", sourceString, ssError, dfError,  msError));
        sourceString = leftMostChars("Total", 12);
        rm_Report.add(String.format("%15s %13.3f     %4d    \n", sourceString, ssTotal, dfTotal ));
        rm_Report.add(String.format("\n  Omega Square for Treatments =  %5.3f\n", omegaSquare_Treatments));
        rm_Report.add(String.format("     Cohen's f for Treatments =  %5.3f\n", cohensF_Treatments));
        rm_Report.add(String.format("\n  Omega Square for Subjects =  %5.3f\n", omegaSquare_Subjects));
        rm_Report.add(String.format("     Cohen's f for Subjects =  %5.3f\n", cohensF_Subjects));
        rm_Report.add(String.format("-----------------------------------------------------------------------------------\n"));
        printTheStuff();
        doTukeyKramer();
    }    // end printANOVA_Results
    
private void doTukeyKramer() {
    dm.whereIsWaldo(279, waldoFile, "doTukeyKramer()");
    studRangeQ = new StudentizedRangeQ();
    qTK = studRangeQ.qrange(0.95, // cumulative p -- use .95 if alpha = .05
                           (double)nTreatments, // number of groups
                           (double)dfError, // df error
                            1.0);  // use a 1.0 to get stu range stat)
    wT = qTK / Math.sqrt(2.0); // Dean & Voss, p85
    rm_Report.add(String.format( "\n\n               **********           Tukey-Kramer Post Hoc Tests         **********\n\n"));
    rm_Report.add(String.format("   Treatment/   Treatment/     Mean        Critical     95PC CI Lower  95PC CI Upper\n"));
    rm_Report.add(String.format("     Group       Group      Difference         Q          Bound          Bound\n"));    
    
    for (int ithLevel = 1; ithLevel < nTreatments; ithLevel++) {
        int nIth = allTheQDVs.get(ithLevel).getLegalN();
        double xBarIth = allTheQDVs.get(ithLevel).getTheMean();
        strIthLevel = StringUtilities.truncateString(alStr_AllTheLabels.get(ithLevel), 10);
        
        //  Store for Grouping
        for (int jthLevel = ithLevel + 1; jthLevel < nTreatments + 1; jthLevel++) {
            int nJth = allTheQDVs.get(jthLevel).getLegalN();
            double xBarJth = allTheQDVs.get(jthLevel).getTheMean();
            double diff_mean = xBarIth - xBarJth;
            qCritPlusMinus = wT * Math.sqrt(msError * (1.0/nIth + 1.0/nJth));
            lowCI_TK = diff_mean - qCritPlusMinus;
            highCI_TK = diff_mean + qCritPlusMinus;
            strJthLevel = StringUtilities.truncateString(alStr_AllTheLabels.get(jthLevel), 10);
            rm_Report.add(String.format("%10s  %10s       %8.3f     %8.3f       %8.3f       %8.3f\n", strIthLevel,  
                                                                                      strJthLevel,
                                                                                      diff_mean,
                                                                                      qCritPlusMinus,
                                                                                      lowCI_TK,
                                                                                      highCI_TK));
            }
        }
    }
    
    public void print_ANOVA2_Results_2() {
        dm.whereIsWaldo(315, waldoFile, " -- print_ANOVA2_Results_2()");
        sphericity_Report = new ArrayList<>();    
        sphericity_Report.add(String.format("-----------------------------------------------------------------------------------\n"));
        sphericity_Report.add(String.format("                  Advanced Repeated Measures  Concerns: Sphericity                 \n"));
        sphericity_Report.add(String.format("-----------------------------------------------------------------------------------\n"));
        double mauchly_W = anova2_RM_Calculations.getMauchlyW();
        double mauchly_X2 = anova2_RM_Calculations.getMauchlyX2();
        double mauchly_DF = anova2_RM_Calculations.getMauchly_DF();
        double mauchly_PValue = anova2_RM_Calculations.getMauchly_PValue();
        sphericity_Report.add(String.format("           Mauchly's W     Chi-Square        df     P-value\n"));  
        sphericity_Report.add(String.format("             %6.3f        %8.3f     %8.3f     %6.4f\n", mauchly_W, mauchly_X2, mauchly_DF, mauchly_PValue)); 
        sphericity_Report.add(String.format("-----------------------------------------------------------------------------------\n"));
        sphericity_Report.add(String.format("               Source of      Sum of\n"));
        sphericity_Report.add(String.format("               Variation      Squares     dfNum      dfDen       F     P-value\n"));
        sphericity_Report.add(String.format("-----------------------------------------------------------------------------------\n"));
        
        sourceString = leftMostChars("Unadjusted", 25);
        double ss_sa = ssTreatments;
        double df_num_sa = dfTreatments;
        double df_den_sa = dfError;
        //double ms_sa = msTreatments;
        double  f_sa = fStatTreatments;
        double pVal_sa = pValTreatments;
        sphericity_Report.add(String.format("%25s %10.3f     %6.3f    %8.3f %8.3f     %6.4f\n", sourceString, ss_sa, df_num_sa, df_den_sa, f_sa, pVal_sa)); 
        
        sourceString = leftMostChars("Huynh_Feldt", 25);
        double ss_hf = ssTreatments;
        double df_num_hf = anova2_RM_Calculations.get_NumDF_Huynh_Feldt();
        double df_den_hf = anova2_RM_Calculations.get_DenDF_Huynh_Feldt();
        double f_hf = anova2_RM_Calculations.getF_Huynh_Feldt();
        double pVal_hf = anova2_RM_Calculations.getPValue_Huynh_Feldt();
        sphericity_Report.add(String.format("%25s %10.3f     %6.3f    %8.3f %8.3f     %6.4f\n", sourceString, ss_hf, df_num_hf, df_den_hf, f_hf, pVal_hf)); 
        
        sourceString = leftMostChars("Greenhouse_Geisser", 25);
        double ss_gg = ssTreatments;
        double df_num_gg = anova2_RM_Calculations.get_NumDF_Greenhouse_Geisser();
        double df_den_gg = anova2_RM_Calculations.get_DenDF_Greenhouse_Geisser();
        double f_gg = anova2_RM_Calculations.getF_Greenhouse_Geisser();
        double pVal_gg = anova2_RM_Calculations.getPValue_Greenhouse_Geisser();
        sphericity_Report.add(String.format("%25s %10.3f     %6.3f    %8.3f %8.3f     %6.4f\n", sourceString, ss_gg, df_num_gg, df_den_gg, f_gg, pVal_gg)); 
        
        sourceString = leftMostChars("Lower Bound", 25);
        double ss_lb = ssTreatments;
        double df_num_lb = anova2_RM_Calculations.get_NumDF_LB();
        double df_den_lb = anova2_RM_Calculations.get_DenDF_LB();
        double f_lb = anova2_RM_Calculations.getF_LB();
        double pVal_lb = anova2_RM_Calculations.getPValue_LB();
        sphericity_Report.add(String.format("%25s %10.3f     %6.3f    %8.3f %8.3f     %6.4f\n", sourceString, ss_lb, df_num_lb, df_den_lb, f_lb, pVal_lb)); 
    }
    
    private void doTheEffectSizes() {
        /************************************************************
         * Kirk, Experimental Design: Procedures for the Behavioral *
         * Sciences (4th).  pp 134- 137                             *
         ***********************************************************/ 
        dm.whereIsWaldo(370, waldoFile, " -- doTheEffectSizes_RCB_NoReplicates()");
        // omegaSquare_Treats_Numerator = (nTreatments - 1.0) * (fStatTreatments - 1.0);
        omegaSquare_Treats_Numerator = ssTreatments - (nTreatments - 1) * msWithin;
        omegaSquareTreats_Denominator = ssTotal + msWithin;
        omegaSquare_Treatments = omegaSquare_Treats_Numerator / omegaSquareTreats_Denominator;
        
        if (omegaSquare_Treatments < 0) { omegaSquare_Treatments = 0;  }
        
        cohensF_Treatments = Math.sqrt(omegaSquare_Treatments / (1.0 - omegaSquare_Treatments));
        omegaSquare_Subjects_Numerator = (nSubjects - 1.0) * (fStatSubjects - 1.0);
        omegaSquareSubjects_Denominator = omegaSquare_Subjects_Numerator + nTreatments * nSubjects;
        omegaSquare_Subjects = omegaSquare_Subjects_Numerator / omegaSquareSubjects_Denominator;
        
        if (omegaSquare_Subjects < 0) { omegaSquare_Subjects = 0; }
        
        cohensF_Subjects = Math.sqrt(omegaSquare_Subjects / (1.0 - omegaSquare_Subjects));
        
        partial_eta_squared = ssTreatments / (ssTreatments + ssError);
        //System.out.println("388 RM_Model, partial_eta_squared = " + partial_eta_squared);
    }
    
    private String leftMostChars(String original, int leftChars) {
        return StringUtilities.truncateString(original, leftChars);
    }
   
    public String getTreatmentLabels() {return treatmentValues.getTheDataLabel();}
    public String getSubjectLabels() {return subjectValues.getTheDataLabel();}
    public String getResponseLabel() { return responseLabel; }

    public int getNSubjects() {  return nBlocks; }
    public int getNTreatments() {  return nTreatments; }

    public ObservableList <String> getTreatmentLevels() { 
        return treatmentLevels; 
    }     

    public ObservableList <String> getSubjectLevels() { return subjectLevels; } 

    public double getMinVertical() {return prelimTreatments.getMinVertical(); }
    public double getMaxVertical() {return prelimTreatments.getMaxVertical(); }  
     
    public UnivariateContinDataObj  getAllDataUCDO() {
        UnivariateContinDataObj dummyUCDO = new UnivariateContinDataObj();
        return dummyUCDO;
    }
     
    public boolean getReplicatesExist() { return replicatesExist; }
    public boolean getBalanceExists() { return balanceExists; }

    public ANOVA2_RCB_PrelimANOVA1 getPrelimA() { return prelimTreatments; }
    public ANOVA2_RCB_PrelimANOVA1 getPrelimB() { return prelimSubjects; }
    public ANOVA2_RCB_PrelimANOVA1 getPrelimAB() { return prelimAB; }
    
    public CategoricalDataVariable getRM_SubjectValues() { return subjectValues; }
    public CategoricalDataVariable getRM_TreatmentValues() { return treatmentValues; }
    public QuantitativeDataVariable getRMResponseValues() { return responseValues; }
     
    public ArrayList<String> getANOVA2Report() {  return rm_Report; }
    public ArrayList<String> getSphericityReport() {  return sphericity_Report; }
     
    public Data_Manager getDataManager() { return dm; }
    public ANOVA2_RM_Model get_RM_Model() { return this; }
    public ArrayList<QuantitativeDataVariable> getAllTheQDVs() {return allTheQDVs;}
     
    public double get_RM_Model_Num_df() { return dfTreatments; }
    public double get_RM_Model_Den_df() { return dfError; }
    public double get_RM_Model_SSTreatments() { return ssTreatments; }    
    public double get_RM_Model_MSTreatments() { return msTreatments; }
    public double get_RM_Model_MSError() { return msError; }
    public double get_RM_Model_F() { return fStatTreatments; }
    public double get_RM_Model_PVal() { return pValTreatments; }
}
