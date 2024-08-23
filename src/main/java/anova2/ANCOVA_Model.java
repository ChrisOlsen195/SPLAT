/**************************************************
 *                ANCOVA_Model                    *
 *                  06/30/24                      *
 *                   09:00                        *
 *************************************************/
/**************************************************
 *    Tested against Tamhane p101  06/30/24       *
 *    Tested against Huitema p140  06/30/24       *
 *    Tested against Montgomery p656  06/30/24    *
 *************************************************/
package anova2;

import dataObjects.ANCOVA_Object;
import dataObjects.CategoricalDataVariable;
import dataObjects.ColumnOfData;
import dataObjects.QuantitativeDataVariable;
import java.util.ArrayList;
import probabilityDistributions.FDistribution;
import probabilityDistributions.StudentizedRangeQ;
import probabilityDistributions.TDistribution;
import utilityClasses.StringUtilities;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import splat.Data_Manager;

public class ANCOVA_Model {
    
    int nLevels, nInAll, df_Error, df_Total, df_Treatments;
    
    double s, s_xx, s_xy, s_yy, ssWithinForTK;
    double huitema_Step_01, huitema_Step_02;
    double betaHat, qTK, wT, lowCI_TK, highCI_TK, qCritPlusMinus, slopeAll, 
           interceptAll, dbl_nInAll, dbl_nLevels;

    double fTreats, pVal_Treatments;
    
    double ss_Covariate, ss_Error, dbl_dfError, ms_Error, ss_Treatments, 
           dbl_dfTreatments, ms_Treatments, ss_HomogRegr; 

    double totalCovariate, totalResponse, totalCrossProduct, meanAllCovariate,
            totalSquareOfCovariate, totalSquareOfResponse;
    
    double[] nWithinCovariate, sumWithinCovariate, meanWithinCovariate, 
             stDevWithinCovariate, sumWithinResponse, meanWithinResponse, 
             stDevWithinResponse, adjustedMean, sumOfSquaresCovWithin,
             sumOfSquaresRespWithin, slopeWithin, interceptWithin, 
             slopeWithinNumerator, slopeWithinDenominator,
             sumOfSquaresCovRespWithin;
    
    double[] daCovariates, daResponses, daLevels, middleInterval,
             correlationWithin, studentizedRresidualWithin;
        
    String sourceString, strIthLevel, strJthLevel;
    
    String waldoFile = "";
    // String waldoFile = "ANCOVA_Model";
    
    ANCOVA_Controller ancova_Controller;
    ANCOVA_Object ancova_Object;
    ArrayList<String> ancova_Report, originalLevels, transformedLevels;   
    ArrayList<String>[] al_StudentizedResiduals;
    ArrayList <QuantitativeDataVariable> allTheQDVs;
    CategoricalDataVariable categoricalTreatments;
    ColumnOfData col_Covariate, col_Response, col_CategoricalTreatment, 
                 col_Treatment;
    Data_Manager dm;
    FDistribution fDist_Treatments, fDist_HomogSlopes;
    StudentizedRangeQ studRangeQ;
    TDistribution tDistribution;
    ObservableList<String> transformedLabels;
    
    // Columns are Covariate / Response / Treatment
    public ANCOVA_Model(ANCOVA_Controller ancova_Controller) {
        this.ancova_Controller = ancova_Controller;
        dm = ancova_Controller.getDataManager();
        dm.whereIsWaldo(76, waldoFile, "Constructing");
        ancova_Object = ancova_Controller.get_ANCOVAObject();
        col_Covariate = new ColumnOfData();
        col_Covariate = ancova_Object.getColCovariate();
        nInAll = col_Covariate.getColumnSize();
        dbl_nInAll = nInAll;
        originalLevels = new ArrayList();
        originalLevels = ancova_Controller.get_OriginalLevels();
        col_Response = new ColumnOfData();
        col_Response = ancova_Object.getColResponse();
        col_CategoricalTreatment = new ColumnOfData();
        
        // This column will be converted from treats to 0, 1, etc.
        col_CategoricalTreatment = ancova_Object.getColTreatment();
        col_Treatment = new ColumnOfData();
        col_Treatment = ancova_Object.getColTreatment();
        ancova_Report = new ArrayList<>(); 
        transformedLabels = FXCollections.observableArrayList();
        
        // Create a categorical variable for the treatments.  Need to get a
        // map from the variable to natural number sequence.        
        doTreatmentVariable();

        // Convert categorical treatments to 0, 1, indices
        for (int ithCase = 0; ithCase < nInAll; ithCase++) {
            for (int ithLevel = 0; ithLevel < nLevels; ithLevel++) {
                String strIthCase = col_CategoricalTreatment.getIthCase(ithCase);
                String strIthTreat = transformedLevels.get(ithLevel);
                if (strIthCase.equals(strIthTreat)) {
                    String quantTreat = Double.toString(ithLevel);
                    col_Treatment.setStringInIthRow(ithCase, quantTreat);
                }
            }
        }
        
        daCovariates = new double[nInAll]; 
        daResponses = new double[nInAll]; 
        daLevels = new double[nInAll]; 
        
        for (int ithCase = 0; ithCase < nInAll; ithCase++) {
            String cov = col_Covariate.getStringInIthRow(ithCase);
            String resp = col_Response.getStringInIthRow(ithCase);
            String treat = col_Treatment.getStringInIthRow(ithCase);
            daCovariates[ithCase] = Double.parseDouble(cov);
            daResponses[ithCase] = Double.parseDouble(resp);
            daLevels[ithCase] = Double.parseDouble(treat);
        }
       
        initializeTheArrays();
        doMeans();
        doStDevs();
        doTheSumsOfSquaresWithin();
        doCorrelationsAndRegressions();
        testForEqualityOfSlopes();
        
        betaHat = s_xy / s_xx;   
        ss_Covariate = s_xy * s_xy / s_xx;       
        ss_Error = s_yy - ss_Covariate;        

        ss_Covariate = s_xy * s_xy / s_xx;

        ss_Error = s_yy - ss_Covariate;
        df_Error = nInAll - nLevels - 1;
        dbl_dfError = df_Error;
        ms_Error = ss_Error / dbl_dfError;
        
        ss_Treatments = huitema_Step_02 - ss_Error;    // Adjusted, Huit 144

        df_Treatments = nLevels - 1;
        dbl_dfTreatments = df_Treatments;
        ms_Treatments = ss_Treatments / dbl_dfTreatments;

        fTreats = ms_Treatments / ms_Error;
        fDist_Treatments = new FDistribution( df_Treatments, df_Error);
        pVal_Treatments = fDist_Treatments.getRightTailArea(fTreats);
        df_Total = nInAll - 2;  // Huitema p144

        printTheStuff();
        print_ANCOVA_Table();
        print_RegressionHomogeneity_Table();
        doANCOVATukeyKramer();
        doTheResiduals();
        doResidsForHBoxes();
    }
    
    private void initializeTheArrays() {
        dm.whereIsWaldo(162, waldoFile, "initializeTheArrays()");
        nWithinCovariate = new double[nLevels];
        sumWithinCovariate = new double[nLevels];
        meanWithinCovariate = new double[nLevels];
        stDevWithinCovariate = new double[nLevels];
        sumWithinResponse = new double[nLevels];
        meanWithinResponse = new double[nLevels];
        sumOfSquaresCovWithin = new double[nLevels];
        sumOfSquaresCovRespWithin = new double[nLevels];
        sumOfSquaresRespWithin = new double[nLevels];
        stDevWithinCovariate = new double[nLevels];
        stDevWithinResponse = new double[nLevels];
        slopeWithinNumerator = new double[nLevels];
        slopeWithinDenominator = new double[nLevels];
        slopeWithin = new double[nLevels];
        interceptWithin = new double[nLevels];        
        studentizedRresidualWithin = new double[nInAll];
    }

    private void doMeans() {
        dm.whereIsWaldo(182, waldoFile, "doMeans()");
        totalCovariate = 0.0; totalResponse = 0.0;
        for (int ithLevel = 0; ithLevel < nLevels; ithLevel++) {
            nWithinCovariate[ithLevel] = 0.0;
            sumWithinCovariate[ithLevel] = 0.0;
            sumWithinResponse[ithLevel] = 0.0;
        }

        for (int ithCase = 0; ithCase < nInAll; ithCase++) {
            int ithTreat = (int)daLevels[ithCase];
            nWithinCovariate[ithTreat]++;
            sumWithinCovariate[ithTreat] += daCovariates[ithCase];
            totalCovariate += daCovariates[ithCase];
            sumWithinResponse[ithTreat] += daResponses[ithCase];
            totalResponse += daResponses[ithCase];
            totalSquareOfCovariate += (daCovariates[ithCase] * daCovariates[ithCase]);
            totalCrossProduct += (daCovariates[ithCase] * daResponses[ithCase]);
            totalSquareOfResponse += (daResponses[ithCase] * daResponses[ithCase]);
                    
        }
        
        //  **************************  Huitema, p140  ********************************************************
        huitema_Step_01 = totalSquareOfResponse - totalResponse * totalResponse / nInAll;
        
        double tempPreNum = totalCrossProduct - totalCovariate * totalResponse / nInAll;
        double num = tempPreNum * tempPreNum;
        double den = totalSquareOfCovariate - totalCovariate * totalCovariate / nInAll;
        
        huitema_Step_02 = huitema_Step_01 - num / den;

        for (int ithLevel = 0; ithLevel < nLevels; ithLevel++) {
            meanWithinCovariate[ithLevel] = sumWithinCovariate[ithLevel] / nWithinCovariate[ithLevel];
            meanWithinResponse[ithLevel] = sumWithinResponse[ithLevel] / nWithinCovariate[ithLevel];
        }
        
        meanAllCovariate = totalCovariate / dbl_nInAll;
        //meanAllResponse = totalResponse / dbl_nInAll;   
    }
    
    private void doStDevs() {
        dm.whereIsWaldo(222, waldoFile, "doStDevs()");
        for (int ithLevel = 0; ithLevel < nLevels; ithLevel++) {
            sumOfSquaresCovWithin[ithLevel] = 0.0;
            sumOfSquaresCovRespWithin[ithLevel] = 0.0;
            sumOfSquaresRespWithin[ithLevel] = 0.0;
        }

        for (int ithCase = 0; ithCase < nInAll; ithCase++) {
            int ithLevel = (int)daLevels[ithCase];
            double tempDevCov = daCovariates[ithCase] - meanWithinCovariate[ithLevel];
            double tempDevResponse = daResponses[ithCase] - meanWithinResponse[ithLevel];
            sumOfSquaresCovWithin[ithLevel] += (tempDevCov * tempDevCov);
            
            sumOfSquaresCovRespWithin[ithLevel] += (tempDevCov * tempDevResponse);
            sumOfSquaresRespWithin[ithLevel] += (tempDevResponse * tempDevResponse);
        }

        for (int ithLevel = 0; ithLevel < nLevels; ithLevel++) {            
            stDevWithinCovariate[ithLevel] = Math.sqrt(sumOfSquaresCovWithin[ithLevel] / (nWithinCovariate[ithLevel] - 1));
            stDevWithinResponse[ithLevel] = Math.sqrt(sumOfSquaresRespWithin[ithLevel] / (nWithinCovariate[ithLevel] - 1));
        }           
    }
    
    private void doCorrelationsAndRegressions() {
        dm.whereIsWaldo(246, waldoFile, "doCorrelationsAndRegressions()");
        correlationWithin = new double[nLevels];
        //correlationAll = 0.0;
        for (int ithLevel = 0; ithLevel < nLevels; ithLevel++) {
            correlationWithin[ithLevel] = 0.0;
            slopeWithinNumerator[ithLevel] = 0.0;
            slopeWithinDenominator[ithLevel] = 0.0;
            slopeWithin[ithLevel] = 0.0;
            interceptWithin[ithLevel] = 0.0;
        }
        
        for (int ithLevel = 0; ithLevel < nLevels; ithLevel++) {
            for (int ithCase = 0; ithCase < nInAll; ithCase++) {
                if (ithLevel == (int)daLevels[ithCase]) {
                    double tempDevCovariate = daCovariates[ithCase] - meanWithinCovariate[ithLevel];
                    double tempDevResponse = daResponses[ithCase] - meanWithinResponse[ithLevel];
                    double tempZCov = tempDevCovariate / stDevWithinCovariate[ithLevel];
                    double tempZResponse = tempDevResponse  / stDevWithinResponse[ithLevel];
                    double tempCrossProduct = tempDevCovariate * tempDevResponse;
                    
                    correlationWithin[ithLevel] += (tempZCov * tempZResponse / (nWithinCovariate[ithLevel] - 1.0)); 
                    slopeWithinNumerator[ithLevel] += tempCrossProduct;
                    slopeWithinDenominator[ithLevel] += (tempDevCovariate * tempDevCovariate);
                }
            }
        }   
                
        for (int ithLevel = 0; ithLevel < nLevels; ithLevel++) {
            slopeWithin[ithLevel] = slopeWithinNumerator[ithLevel] / slopeWithinDenominator[ithLevel]; 
            interceptWithin[ithLevel] = meanWithinResponse[ithLevel] - slopeWithin[ithLevel] * meanWithinCovariate[ithLevel]; 
        }      
        
        slopeAll = ancova_Object.getSlope();
        interceptAll = ancova_Object.getIntercept();

        for (int ithLevel = 0; ithLevel < nLevels; ithLevel++) {
            meanWithinCovariate[ithLevel] = ancova_Object.getMeanCovWithin(ithLevel);
            meanWithinResponse[ithLevel] = ancova_Object.getMeanRespWithin(ithLevel);
            stDevWithinCovariate[ithLevel] = ancova_Object.getStDevCovWithin(ithLevel);
            stDevWithinResponse[ithLevel] = ancova_Object.getStDevRespWithin(ithLevel);             
            correlationWithin[ithLevel] =  ancova_Object.getCorrelationWithin(ithLevel);
            slopeWithin[ithLevel] =  ancova_Object.getSlopeWithin(ithLevel);
            interceptWithin[ithLevel] = ancova_Object.getInterceptWithin(ithLevel);
        }
    }
    
    private void doTheSumsOfSquaresWithin() {
        dm.whereIsWaldo(293, waldoFile, "doTheSumsOfSquaresWithin()");      
        s_xx = 0.0; s_xy = 0.0; s_yy = 0;
        
        for (int ithData = 0; ithData < nInAll; ithData++) {
            int ithTreat = (int)daLevels[ithData];
            s_xx += ((daCovariates[ithData] - meanWithinCovariate[ithTreat]) * (daCovariates[ithData] - meanWithinCovariate[ithTreat]));
            s_xy += ((daCovariates[ithData] - meanWithinCovariate[ithTreat]) * (daResponses[ithData] - meanWithinResponse[ithTreat]));
            s_yy += ((daResponses[ithData] - meanWithinResponse[ithTreat]) * (daResponses[ithData] - meanWithinResponse[ithTreat]));                        
        } 
        
        ssWithinForTK = s_xx;
    }

    private void doTheResiduals() {
        dm.whereIsWaldo(307, waldoFile, "doTheResiduals()");
        s = Math.sqrt(ms_Error);    // For stErr of Residuals

        for (int ithCase = 0; ithCase < nInAll; ithCase++) {
            int ithLevel = (int)daLevels[ithCase];
            double seResponseHat_00 = 1/nWithinCovariate[ithLevel];
            double seResponseHat_01 = daCovariates[ithCase] - meanWithinCovariate[ithLevel];
            double seResponseHat_02 = seResponseHat_01 * seResponseHat_01 / s_xx;
            double seResponseHat_03 = seResponseHat_00 + seResponseHat_02;
            double seResponseHat = s * Math.sqrt(1.0 - seResponseHat_03);
            double responseHat = meanWithinResponse[ithLevel] + betaHat * (daCovariates[ithCase] - meanWithinCovariate[ithLevel]);  
            double rawResidual = daResponses[ithCase] - responseHat;
            studentizedRresidualWithin[ithCase] = rawResidual / seResponseHat;
        }   
    }
    
    private void doResidsForHBoxes() {
        dm.whereIsWaldo(324, waldoFile, "doResidsForHBoxes()");
        // QDVs for the hBox Model 
        al_StudentizedResiduals = new ArrayList[nLevels + 1];
        for (int ithASR = 0; ithASR < nLevels + 1; ithASR++) {
            al_StudentizedResiduals[ithASR] = new ArrayList();
        }
        for (int ithCase = 0; ithCase < nInAll; ithCase++) {
            al_StudentizedResiduals[0].add(Double.toString(studentizedRresidualWithin[ithCase]));
            int ithTreat = (int)daLevels[ithCase];
            al_StudentizedResiduals[ithTreat + 1].add(Double.toString(studentizedRresidualWithin[ithCase]));            
        }

        allTheQDVs = new ArrayList();

        String tempLabel, tempDescr;
        for (int ithQDV = 0; ithQDV < nLevels; ithQDV++) {
            tempLabel = transformedLevels.get(ithQDV);
            String tempString = originalLevels.get(ithQDV);
            transformedLabels.add(tempLabel);   //  Is this vestigial???
            tempDescr = "Treatment" + String.valueOf(ithQDV);
            allTheQDVs.add(new QuantitativeDataVariable(tempString, tempString, al_StudentizedResiduals[ithQDV + 1]));
        }          
    }
    
    /*******************************************************************
     *   Winer, B. J. (1962).  Statistical Principles in Experimental  *
     *   Design.  NcGraw-Hill.  pp590-1                                *
     ******************************************************************/
    private void testForEqualityOfSlopes() {
        dm.whereIsWaldo(353, waldoFile, "testForEqualityOfSlopes()");
        double firstSum = 0.0;
        double pre_s_2_Numerator = 0.0;
        double pre_s_2_Denominator = 0.0;
        System.out.println("\n");
        for (int ithLevel = 0; ithLevel < nLevels; ithLevel++) {
            firstSum += sumOfSquaresCovRespWithin[ithLevel] * sumOfSquaresCovRespWithin[ithLevel] / sumOfSquaresCovWithin[ithLevel];
            pre_s_2_Numerator += sumOfSquaresCovRespWithin[ithLevel];
            pre_s_2_Denominator += sumOfSquaresCovWithin[ithLevel];
        }
        
        double pre_s_2 = pre_s_2_Numerator * pre_s_2_Numerator / pre_s_2_Denominator;
        double s_2 = firstSum - pre_s_2;       
        ss_HomogRegr =  s_2;
    }  
    
    public void doTreatmentVariable() {
        dm.whereIsWaldo(370, waldoFile, "doTreatmentVariable()");
        categoricalTreatments = new CategoricalDataVariable("Treatments", col_CategoricalTreatment);
        transformedLevels = new ArrayList();
        // Transformed to 0, 1, ...
        transformedLevels = categoricalTreatments.getListOfLevels();
        nLevels = transformedLevels.size();
        dbl_nLevels = nLevels;
    }
    
    private void print_RegressionHomogeneity_Table() {    
        dm.whereIsWaldo(380, waldoFile, "print_RegressionHomogeneity_Table()");
        double ss_HomogSlopes = ss_HomogRegr;
        int df_HomogSlopes = nLevels - 1;
        double dbl_dfHomogSlopes = df_HomogSlopes;
        double ms_HomogSlopes = ss_HomogSlopes / dbl_dfHomogSlopes;
        int df_Denom = nInAll - nLevels - 1;
        fDist_HomogSlopes = new FDistribution( df_HomogSlopes, df_Denom);

        double ss_WithinResids = ss_Error;
        int df_WithinResids = nInAll - nLevels - 1;
        
        double ss_IndResids = ss_Error - ss_HomogSlopes;
        int df_IndResids = nInAll - 2 * nLevels;
        double dbl_dfIndResids = df_IndResids;
        double ms_IndResids = ss_IndResids / dbl_dfIndResids;
        
        double fHomogSlopes = ms_HomogSlopes / ms_IndResids;
        double pVal_HomogSlopes = fDist_HomogSlopes.getRightTailArea(fHomogSlopes);
        
        ancova_Report.add(String.format("\n                                  Homogeneity of Slopes\n"));
        ancova_Report.add(String.format("-----------------------------------------------------------------------------------\n"));
        ancova_Report.add(String.format("        Source of        Sum of\n"));
        ancova_Report.add(String.format("        Variation        Squares         df     Mean Square       F        P-value\n"));
        ancova_Report.add(String.format("-----------------------------------------------------------------------------------\n"));
        sourceString = leftMostChars("Heterogeneity", 16);
        ancova_Report.add(String.format("%15s    %13.3f       %4d      %8.3f     %8.3f     %6.4f\n", sourceString, ss_HomogSlopes, df_HomogSlopes, 
                                                                                          ms_HomogSlopes, fHomogSlopes, pVal_HomogSlopes));
        sourceString = leftMostChars("Residuals", 16);
        ancova_Report.add(String.format("%15s    %13.3f       %4d      %8.3f\n", sourceString, ss_IndResids, df_IndResids, 
                                                                                          ms_IndResids));
    
        sourceString = leftMostChars("Within Resids", 16);
        ancova_Report.add(String.format("%15s    %13.3f       %4d\n", sourceString, ss_WithinResids, df_WithinResids));

        ancova_Report.add(String.format("-----------------------------------------------------------------------------------\n"));
   } 
    
private void printTheStuff() {
        dm.whereIsWaldo(418, waldoFile, "printTheStuff()");
        ancova_Report.add(String.format("\n"));
        ancova_Report.add(String.format("               **********              Analysis of Covariance            **********\n\n"));
        ancova_Report.add(String.format("               **********         Parameter estimates for Levels         **********\n\n"));        
        ancova_Report.add(String.format("   Treatment/   Sample     Sample      Sample     Std Err     Margin     Lower 95PC  Upper 95PC\n"));
        ancova_Report.add(String.format("     Group       Size       Mean       St Dev     of mean    of Error      Bound        Bound\n"));
        
        for (int ithLevel = 0; ithLevel < nLevels; ithLevel++) {
            strIthLevel = StringUtilities.truncateString(originalLevels.get(ithLevel), 10);
            int iSampleSize = (int)nWithinCovariate[ithLevel];
            double iMean = meanWithinResponse[ithLevel];
            double iStandDev = stDevWithinResponse[ithLevel];
            double iStandErr = iStandDev / Math.sqrt(iSampleSize - 1.0);
            
            tDistribution = new TDistribution(df_Error);
            middleInterval = new double[2];
            middleInterval = tDistribution.getInverseMiddleArea(0.95);
            double critical_t = middleInterval[1];
            double iMarginOfError = critical_t * iStandErr;
            double iLowerBound = iMean - iMarginOfError;
            double iUpperBound = iMean + iMarginOfError;
            ancova_Report.add(String.format("%10s      %4d     %8.3f    %8.3f    %8.3f   %8.3f     %8.3f     %8.3f\n", strIthLevel,  
                                                                                                            iSampleSize,
                                                                                                            iMean,
                                                                                                            iStandDev,
                                                                                                            iStandErr,
                                                                                                            iMarginOfError,
                                                                                                            iLowerBound,
                                                                                                            iUpperBound));
        }  
    }

private void doANCOVATukeyKramer() {
    // Dean, A. M., & Voss, D. (2013) Design and Analysis of Experiments
    dm.whereIsWaldo(452, waldoFile, "doANCOVATukeyKramer()");
    studRangeQ = new StudentizedRangeQ();
    /*************************************************************************
     *    Tukey-Kramer df's from Huitema, B. E.  (2011). The Analysis of     *
     *    Covariance and Alternatives: Statistical Methods for Experiments,  *
     *    Quasi-Experiments, and Single Case Studies.  Wiley                 *
     ************************************************************************/
     double dbl_df_ANCOVA_Error = dbl_nInAll - dbl_nLevels - 1.0;
    
    qTK = studRangeQ.qrange(0.95, // cumulative p -- use .95 if alpha = .05
                           dbl_nLevels, // number of groups
                            dbl_df_ANCOVA_Error, // df error
                            1.0);        // use a 1.0 to get stu range stat)

    wT = qTK / Math.sqrt(2.0); // Dean & Voss, p85
    ancova_Report.add(String.format("\n  Tukey-Kramer Tests (adjusted means) \n\n"));
    ancova_Report.add(String.format("   Treatment/   Treatment/     Mean                   95PC CI Lower  95PC CI Upper\n"));    
    ancova_Report.add(String.format("     Group       Group      Difference        +/-         Bound          Bound\n"));
    // Adjusted means --Huitema, p 134
    adjustedMean = new double[nLevels];
    for (int ithLevel = 0; ithLevel < nLevels; ithLevel++) {
        adjustedMean[ithLevel] = meanWithinResponse[ithLevel] - betaHat * (meanWithinCovariate[ithLevel] - meanAllCovariate);
    }
    
    for (int ithLevel = 0; ithLevel < nLevels - 1; ithLevel++) {
        int nIth = (int)nWithinCovariate[ithLevel];
        double xBarIth = adjustedMean[ithLevel];
        strIthLevel = StringUtilities.truncateString(originalLevels.get(ithLevel), 10);        

        for (int jthLevel = ithLevel + 1; jthLevel < nLevels; jthLevel++) {
            int nJth = (int)nWithinCovariate[jthLevel];
            double xBarJth = adjustedMean[jthLevel];
            double diff_AdjustedMean = xBarIth - xBarJth;
            double diff_SampleMean = meanWithinCovariate[ithLevel] - meanWithinCovariate[jthLevel];

            qCritPlusMinus = wT * Math.sqrt(ms_Error * (1.0/nIth + 1.0/nJth + diff_SampleMean * diff_SampleMean / ssWithinForTK));
            lowCI_TK = diff_AdjustedMean - qCritPlusMinus;
            highCI_TK = diff_AdjustedMean + qCritPlusMinus;
            strJthLevel = StringUtilities.truncateString(originalLevels.get(jthLevel), 10);
            ancova_Report.add(String.format(" %10s   %10s     %8.3f     %8.3f      %8.3f       %8.3f\n", strIthLevel,  
                                                                                      strJthLevel,
                                                                                      diff_AdjustedMean,
                                                                                      qCritPlusMinus,
                                                                                      lowCI_TK,
                                                                                      highCI_TK));
        }
    }
}
    
    private void print_ANCOVA_Table() { 
        dm.whereIsWaldo(502, waldoFile, "print_ANCOVA_Table()");
        ancova_Report.add(String.format("\n                            Analysis of Covariance\n"));
        ancova_Report.add(String.format("-----------------------------------------------------------------------------------\n"));
        ancova_Report.add(String.format("        Source of        Sum of\n"));
        ancova_Report.add(String.format("        Variation        Squares         df     Mean Square       F        P-value\n"));
        ancova_Report.add(String.format("-----------------------------------------------------------------------------------\n"));
        sourceString = leftMostChars("Treatments", 16);
        ancova_Report.add(String.format("%15s    %13.3f       %4d      %8.3f     %8.3f     %6.4f\n", sourceString, ss_Treatments, df_Treatments, 
                                                                                          ms_Treatments,fTreats, pVal_Treatments));
        
        sourceString = leftMostChars("Error", 16);
        ancova_Report.add(String.format("%15s    %13.3f       %4d      %8.3f\n", sourceString, ss_Error, df_Error,  ms_Error));
        
        sourceString = leftMostChars("Total", 16);
        ancova_Report.add(String.format("%15s    %13.3f       %4d\n", sourceString, huitema_Step_02, df_Total));
        ancova_Report.add(String.format("-----------------------------------------------------------------------------------\n"));
   } 
    
    public ANCOVA_Object get_ANCOVA_Object() { return ancova_Object; }
    public ArrayList<String> getANCOVA_Report() { return ancova_Report;} 
    public ANCOVA_Controller getANCOVA_Controller() { return ancova_Controller; }
    public ObservableList<String> getTransformedLabels() { return transformedLabels; }
    public int getNCases() { return nInAll; }    
    public int getNLevels() { return nLevels; }    
    public double[] getDaCovariates() { return daCovariates; }    
    public double[] getDaResponses() { return daResponses; }    
    public double[] getStudentizedResiduals() { return studentizedRresidualWithin; }    
    public double[] getDaLevels() { return daLevels; }    
    public double[] getSlopesWithin() { return slopeWithin; }    
    public double[] getInterceptsWithin() { return interceptWithin; }
    public double getSlopeAll() { return slopeAll; }    
    public double getInterceptAll() { return interceptAll; }
    public ArrayList<QuantitativeDataVariable> getAllTheQDVs() {return allTheQDVs; }
    
    public Data_Manager getDataManager() { return dm; }
    
    private String leftMostChars(String original, int leftChars) {
        return StringUtilities.truncateString(original, leftChars);
    }
}
