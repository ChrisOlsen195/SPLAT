/**************************************************
 *             Regr_Compare_Model                 *
 *                  11/27/24                      *
 *                   12:00                        *
 *************************************************/
/**************************************************
 *    Tested against Tamhane p101  06/30/24       *
 *    Tested against Huitema p140  06/30/24       *
 *    Tested against Montgomery p656  06/30/24    *
 *************************************************/
package simpleRegression;

import dataObjects.CategoricalDataVariable;
import dataObjects.ColumnOfData;
import dataObjects.QuantitativeDataVariable;
import dataObjects.Regr_Compare_Object;
import java.util.ArrayList;
import probabilityDistributions.FDistribution;
import probabilityDistributions.StudentizedRangeQ;
import probabilityDistributions.TDistribution;
import utilityClasses.StringUtilities;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import splat.Data_Manager;

public class Regr_Compare_Model {
    
    int nLevels, nInAll, df_Error, df_Total, df_Treatments;
    
    double s, s_xx, s_xy, s_yy, ssWithinForTK;
    double huitema_Step_01, huitema_Step_02;
    double betaHat, qTK, wT, lowCI_TK, highCI_TK, qCritPlusMinus, slopeAll, 
           interceptAll, dbl_nInAll, dbl_nLevels;

    double fTreats, pVal_Treatments;
    
    double ss_Covariate, ss_Error, dbl_dfError, ms_Error, ss_Treatments, 
           dbl_dfTreatments, ms_Treatments, ss_HomogRegr; 

    double totalExplanatory, totalResponse, totalCrossProduct, meanAllExplanatory,
            totalSquareOfExplanatory, totalSquareOfResponse;
    
    double[] nWithinExplanatory, sumWithinExplanatory, meanWithinExplanatory, 
             stDevWithinExplanatory, sumWithinResponse, meanWithinResponse, 
             stDevWithinResponse, adjustedMean, sumOfSquaresExplanWithin,
             sumOfSquaresRespWithin, slopeWithin, interceptWithin, 
             slopeWithinNumerator, slopeWithinDenominator,
             sumOfSquaresCovRespWithin;
    
    double[] daExplans, daResponses, daLevels, middleInterval,
             correlationWithin, studentizedRresidualWithin;
        
    String sourceString, strIthLevel, strJthLevel;
    
    String waldoFile = "";
    // String waldoFile = "Regr_Compare_Model";
    
    Regr_Compare_Controller regr_Compare_Controller;
    Regr_Compare_Object regr_Compare_Object;
    ArrayList<String> regr_Compare_Report, originalLevels, transformedLevels;   
    ArrayList<String>[] al_StudentizedResiduals;
    ArrayList <QuantitativeDataVariable> allTheQDVs;
    CategoricalDataVariable categoricalTreatments;
    ColumnOfData col_Explanatory, col_Response, col_CategoricalTreatment, 
                 col_Treatment;
    Data_Manager dm;
    FDistribution fDist_Treatments, fDist_HomogSlopes;
    StudentizedRangeQ studRangeQ;
    TDistribution tDistribution;
    ObservableList<String> transformedLabels;
    
    // Columns are Covariate / Response / Treatment
    public Regr_Compare_Model(Regr_Compare_Controller regr_Compare_Controller) {
        this.regr_Compare_Controller = regr_Compare_Controller;
        dm = regr_Compare_Controller.getDataManager();
        dm.whereIsWaldo(76, waldoFile, "\nConstructing");
        regr_Compare_Object = regr_Compare_Controller.get_RegrCompareObject();
        col_Explanatory = new ColumnOfData();
        col_Explanatory = regr_Compare_Object.getColCovariate();
        nInAll = col_Explanatory.getColumnSize();
        dbl_nInAll = nInAll;
        originalLevels = new ArrayList();
        originalLevels = regr_Compare_Controller.get_OriginalLevels();
        col_Response = new ColumnOfData();
        col_Response = regr_Compare_Object.getColResponse();
        col_CategoricalTreatment = new ColumnOfData();
        
        // This column will be converted from treats to 0, 1, etc.
        col_CategoricalTreatment = regr_Compare_Object.getColTreatment();
        col_Treatment = new ColumnOfData();
        col_Treatment = regr_Compare_Object.getColTreatment();
        regr_Compare_Report = new ArrayList<>(); 
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
        
        daExplans = new double[nInAll]; 
        daResponses = new double[nInAll]; 
        daLevels = new double[nInAll]; 
        
        for (int ithCase = 0; ithCase < nInAll; ithCase++) {
            String explan = col_Explanatory.getStringInIthRow(ithCase);
            String resp = col_Response.getStringInIthRow(ithCase);
            String treat = col_Treatment.getStringInIthRow(ithCase);
            daExplans[ithCase] = Double.parseDouble(explan);
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

        printUnivariateInformation();
        printBivariateInformation();
        print_RegressionHomogeneity_Table();
        doANCOVATukeyKramer();
        doTheResiduals();
        doResidsForHBoxes();
    }
    
    private void initializeTheArrays() {
        dm.whereIsWaldo(162, waldoFile, "initializeTheArrays()");
        nWithinExplanatory = new double[nLevels];
        sumWithinExplanatory = new double[nLevels];
        meanWithinExplanatory = new double[nLevels];
        stDevWithinExplanatory = new double[nLevels];
        sumWithinResponse = new double[nLevels];
        meanWithinResponse = new double[nLevels];
        sumOfSquaresExplanWithin = new double[nLevels];
        sumOfSquaresCovRespWithin = new double[nLevels];
        sumOfSquaresRespWithin = new double[nLevels];
        stDevWithinExplanatory = new double[nLevels];
        stDevWithinResponse = new double[nLevels];
        slopeWithinNumerator = new double[nLevels];
        slopeWithinDenominator = new double[nLevels];
        slopeWithin = new double[nLevels];
        interceptWithin = new double[nLevels];        
        studentizedRresidualWithin = new double[nInAll];
    }

    private void doMeans() {
        dm.whereIsWaldo(182, waldoFile, "doMeans()");
        totalExplanatory = 0.0; totalResponse = 0.0;
        for (int ithLevel = 0; ithLevel < nLevels; ithLevel++) {
            nWithinExplanatory[ithLevel] = 0.0;
            sumWithinExplanatory[ithLevel] = 0.0;
            sumWithinResponse[ithLevel] = 0.0;
        }

        for (int ithCase = 0; ithCase < nInAll; ithCase++) {
            int ithTreat = (int)daLevels[ithCase];
            nWithinExplanatory[ithTreat]++;
            sumWithinExplanatory[ithTreat] += daExplans[ithCase];
            totalExplanatory += daExplans[ithCase];
            sumWithinResponse[ithTreat] += daResponses[ithCase];
            totalResponse += daResponses[ithCase];
            totalSquareOfExplanatory += (daExplans[ithCase] * daExplans[ithCase]);
            totalCrossProduct += (daExplans[ithCase] * daResponses[ithCase]);
            totalSquareOfResponse += (daResponses[ithCase] * daResponses[ithCase]);
                    
        }
        
        //  **************************  Huitema, p140  ********************************************************
        huitema_Step_01 = totalSquareOfResponse - totalResponse * totalResponse / nInAll;
        
        double tempPreNum = totalCrossProduct - totalExplanatory * totalResponse / nInAll;
        double num = tempPreNum * tempPreNum;
        double den = totalSquareOfExplanatory - totalExplanatory * totalExplanatory / nInAll;
        
        huitema_Step_02 = huitema_Step_01 - num / den;

        for (int ithLevel = 0; ithLevel < nLevels; ithLevel++) {
            meanWithinExplanatory[ithLevel] = sumWithinExplanatory[ithLevel] / nWithinExplanatory[ithLevel];
            meanWithinResponse[ithLevel] = sumWithinResponse[ithLevel] / nWithinExplanatory[ithLevel];
        }
        
        meanAllExplanatory = totalExplanatory / dbl_nInAll;  
    }
    
    private void doStDevs() {
        dm.whereIsWaldo(221, waldoFile, "doStDevs()");
        for (int ithLevel = 0; ithLevel < nLevels; ithLevel++) {
            sumOfSquaresExplanWithin[ithLevel] = 0.0;
            sumOfSquaresCovRespWithin[ithLevel] = 0.0;
            sumOfSquaresRespWithin[ithLevel] = 0.0;
        }

        for (int ithCase = 0; ithCase < nInAll; ithCase++) {
            int ithLevel = (int)daLevels[ithCase];
            double tempDevCov = daExplans[ithCase] - meanWithinExplanatory[ithLevel];
            double tempDevResponse = daResponses[ithCase] - meanWithinResponse[ithLevel];
            sumOfSquaresExplanWithin[ithLevel] += (tempDevCov * tempDevCov);
            
            sumOfSquaresCovRespWithin[ithLevel] += (tempDevCov * tempDevResponse);
            sumOfSquaresRespWithin[ithLevel] += (tempDevResponse * tempDevResponse);
        }

        for (int ithLevel = 0; ithLevel < nLevels; ithLevel++) {            
            stDevWithinExplanatory[ithLevel] = Math.sqrt(sumOfSquaresExplanWithin[ithLevel] / (nWithinExplanatory[ithLevel] - 1));
            stDevWithinResponse[ithLevel] = Math.sqrt(sumOfSquaresRespWithin[ithLevel] / (nWithinExplanatory[ithLevel] - 1));
        }           
    }
    
    private void doCorrelationsAndRegressions() {
        dm.whereIsWaldo(245, waldoFile, "doCorrelationsAndRegressions()");
        correlationWithin = new double[nLevels];
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
                    double tempDevCovariate = daExplans[ithCase] - meanWithinExplanatory[ithLevel];
                    double tempDevResponse = daResponses[ithCase] - meanWithinResponse[ithLevel];
                    double tempZCov = tempDevCovariate / stDevWithinExplanatory[ithLevel];
                    double tempZResponse = tempDevResponse  / stDevWithinResponse[ithLevel];
                    double tempCrossProduct = tempDevCovariate * tempDevResponse;
                    
                    correlationWithin[ithLevel] += (tempZCov * tempZResponse / (nWithinExplanatory[ithLevel] - 1.0)); 
                    slopeWithinNumerator[ithLevel] += tempCrossProduct;
                    slopeWithinDenominator[ithLevel] += (tempDevCovariate * tempDevCovariate);
                }
            }
        }   
                
        for (int ithLevel = 0; ithLevel < nLevels; ithLevel++) {
            slopeWithin[ithLevel] = slopeWithinNumerator[ithLevel] / slopeWithinDenominator[ithLevel]; 
            interceptWithin[ithLevel] = meanWithinResponse[ithLevel] - slopeWithin[ithLevel] * meanWithinExplanatory[ithLevel]; 
        }      
        
        slopeAll = regr_Compare_Object.getSlope();
        interceptAll = regr_Compare_Object.getIntercept();

        for (int ithLevel = 0; ithLevel < nLevels; ithLevel++) {
            meanWithinExplanatory[ithLevel] = regr_Compare_Object.getMeanCovWithin(ithLevel);
            meanWithinResponse[ithLevel] = regr_Compare_Object.getMeanRespWithin(ithLevel);
            stDevWithinExplanatory[ithLevel] = regr_Compare_Object.getStDevCovWithin(ithLevel);
            stDevWithinResponse[ithLevel] = regr_Compare_Object.getStDevRespWithin(ithLevel);             
            correlationWithin[ithLevel] =  regr_Compare_Object.getCorrelationWithin(ithLevel);
            slopeWithin[ithLevel] =  regr_Compare_Object.getSlopeWithin(ithLevel);
            interceptWithin[ithLevel] = regr_Compare_Object.getInterceptWithin(ithLevel);
        }
    }
    
    private void doTheSumsOfSquaresWithin() {
        dm.whereIsWaldo(291, waldoFile, "doTheSumsOfSquaresWithin()");      
        s_xx = 0.0; s_xy = 0.0; s_yy = 0;
        
        for (int ithData = 0; ithData < nInAll; ithData++) {
            int ithTreat = (int)daLevels[ithData];
            s_xx += ((daExplans[ithData] - meanWithinExplanatory[ithTreat]) * (daExplans[ithData] - meanWithinExplanatory[ithTreat]));
            s_xy += ((daExplans[ithData] - meanWithinExplanatory[ithTreat]) * (daResponses[ithData] - meanWithinResponse[ithTreat]));
            s_yy += ((daResponses[ithData] - meanWithinResponse[ithTreat]) * (daResponses[ithData] - meanWithinResponse[ithTreat]));                        
        } 
        
        ssWithinForTK = s_xx;
    }

    private void doTheResiduals() {
        dm.whereIsWaldo(305, waldoFile, "doTheResiduals()");
        s = Math.sqrt(ms_Error);    // For stErr of Residuals

        for (int ithCase = 0; ithCase < nInAll; ithCase++) {
            int ithLevel = (int)daLevels[ithCase];
            double seResponseHat_00 = 1/nWithinExplanatory[ithLevel];
            double seResponseHat_01 = daExplans[ithCase] - meanWithinExplanatory[ithLevel];
            double seResponseHat_02 = seResponseHat_01 * seResponseHat_01 / s_xx;
            double seResponseHat_03 = seResponseHat_00 + seResponseHat_02;
            double seResponseHat = s * Math.sqrt(1.0 - seResponseHat_03);
            double responseHat = meanWithinResponse[ithLevel] + betaHat * (daExplans[ithCase] - meanWithinExplanatory[ithLevel]);  
            double rawResidual = daResponses[ithCase] - responseHat;
            studentizedRresidualWithin[ithCase] = rawResidual / seResponseHat;
        }   
    }
    
    private void doResidsForHBoxes() {
        dm.whereIsWaldo(322, waldoFile, "doResidsForHBoxes()");
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
        dm.whereIsWaldo(351, waldoFile, "testForEqualityOfSlopes()");
        double firstSum = 0.0;
        double pre_s_2_Numerator = 0.0;
        double pre_s_2_Denominator = 0.0;
        System.out.println("\n");
        for (int ithLevel = 0; ithLevel < nLevels; ithLevel++) {
            firstSum += sumOfSquaresCovRespWithin[ithLevel] * sumOfSquaresCovRespWithin[ithLevel] / sumOfSquaresExplanWithin[ithLevel];
            pre_s_2_Numerator += sumOfSquaresCovRespWithin[ithLevel];
            pre_s_2_Denominator += sumOfSquaresExplanWithin[ithLevel];
        }
        
        double pre_s_2 = pre_s_2_Numerator * pre_s_2_Numerator / pre_s_2_Denominator;
        double s_2 = firstSum - pre_s_2;       
        ss_HomogRegr =  s_2;
    }  
    
    private void doTreatmentVariable() {
        dm.whereIsWaldo(368, waldoFile, "doTreatmentVariable()");
        categoricalTreatments = new CategoricalDataVariable("Treatments", col_CategoricalTreatment);
        transformedLevels = new ArrayList();
        // Transformed to 0, 1, ...
        transformedLevels = categoricalTreatments.getListOfLevels();
        nLevels = transformedLevels.size();
        dbl_nLevels = nLevels;
    }
    
    private void print_RegressionHomogeneity_Table() {    
        dm.whereIsWaldo(378, waldoFile, "print_RegressionHomogeneity_Table()");
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
        
        regr_Compare_Report.add(String.format("\n                                  Homogeneity of Slopes\n"));
        regr_Compare_Report.add(String.format("-----------------------------------------------------------------------------------\n"));
        regr_Compare_Report.add(String.format("        Source of        Sum of\n"));
        regr_Compare_Report.add(String.format("        Variation        Squares         df     Mean Square       F        P-value\n"));
        regr_Compare_Report.add(String.format("-----------------------------------------------------------------------------------\n"));
        sourceString = leftMostChars("Heterogeneity", 16);
        regr_Compare_Report.add(String.format("%15s    %13.3f       %4d      %8.3f     %8.3f     %6.4f\n", sourceString, ss_HomogSlopes, df_HomogSlopes, 
                                                                                          ms_HomogSlopes, fHomogSlopes, pVal_HomogSlopes));
        sourceString = leftMostChars("Residuals", 16);
        regr_Compare_Report.add(String.format("%15s    %13.3f       %4d      %8.3f\n", sourceString, ss_IndResids, df_IndResids, 
                                                                                          ms_IndResids));
    
        sourceString = leftMostChars("Within Resids", 16);
        regr_Compare_Report.add(String.format("%15s    %13.3f       %4d\n", sourceString, ss_WithinResids, df_WithinResids));

        regr_Compare_Report.add(String.format("----------------------------------------------------------------------------------------------\n"));
   } 
    
private void printUnivariateInformation() {
        dm.whereIsWaldo(416, waldoFile, "printUnivariateInformation()");
        regr_Compare_Report.add(String.format("\n"));
        regr_Compare_Report.add(String.format("\n\n          **********     Univariate parameter estimates for Groups/Treatments       **********\n\n"));  
        regr_Compare_Report.add(String.format("-----------------------------------------------------------------------------------------------\n"));        
        regr_Compare_Report.add(String.format("   Treatment/   Sample     Sample      Sample     Std Err     Margin     Lower 95PC  Upper 95PC\n"));
        regr_Compare_Report.add(String.format("     Group       Size       Mean       St Dev     of mean    of Error      Bound        Bound\n"));
        regr_Compare_Report.add(String.format("-----------------------------------------------------------------------------------------------\n")); 
                
        for (int ithLevel = 0; ithLevel < nLevels; ithLevel++) {
            strIthLevel = StringUtilities.truncateString(originalLevels.get(ithLevel), 10);
            int iSampleSize = (int)nWithinExplanatory[ithLevel];
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
            regr_Compare_Report.add(String.format("%10s      %4d     %8.3f    %8.3f    %8.3f   %8.3f     %8.3f     %8.3f\n", strIthLevel,  
                                                                                                            iSampleSize,
                                                                                                            iMean,
                                                                                                            iStandDev,
                                                                                                            iStandErr,
                                                                                                            iMarginOfError,
                                                                                                            iLowerBound,
                                                                                                            iUpperBound));
        }  
    }

private void printBivariateInformation() {
        dm.whereIsWaldo(450, waldoFile, "printBivariateInformation()");

        regr_Compare_Report.add(String.format("\n\n          **********     Bivariate parameter estimates for Groups/Treatments       **********\n\n"));  
        regr_Compare_Report.add(String.format("-------------------------------------------------------------------------\n"));
        regr_Compare_Report.add(String.format("        Treatment/ \n"));
        regr_Compare_Report.add(String.format("          Group          Slope         Intercept       Correlation\n"));
        regr_Compare_Report.add(String.format("-------------------------------------------------------------------------\n"));        
                
        for (int ithLevel = 0; ithLevel < nLevels; ithLevel++) {
            strIthLevel = StringUtilities.truncateString(originalLevels.get(ithLevel), 10);
            slopeWithin[ithLevel] = slopeWithinNumerator[ithLevel] / slopeWithinDenominator[ithLevel]; 
            interceptWithin[ithLevel] = meanWithinResponse[ithLevel] - slopeWithin[ithLevel] * meanWithinExplanatory[ithLevel];
            
            regr_Compare_Report.add(String.format("%10s             %8.3f         %8.3f        %8.3f\n", strIthLevel,  
                                                                                                    slopeWithin[ithLevel],
                                                                                                    interceptWithin[ithLevel],
                                                                                                    correlationWithin[ithLevel]));
            
        } 
        regr_Compare_Report.add(String.format("\n-----------------------------------------------------------------------------------\n"));
    }

private void doANCOVATukeyKramer() {
    // Dean, A. M., & Voss, D. (2013) Design and Analysis of Experiments
    dm.whereIsWaldo(474, waldoFile, "doANCOVATukeyKramer()");
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

    adjustedMean = new double[nLevels];
    for (int ithLevel = 0; ithLevel < nLevels; ithLevel++) {
        adjustedMean[ithLevel] = meanWithinResponse[ithLevel] - betaHat * (meanWithinExplanatory[ithLevel] - meanAllExplanatory);
    }
    
    for (int ithLevel = 0; ithLevel < nLevels - 1; ithLevel++) {
        int nIth = (int)nWithinExplanatory[ithLevel];
        double xBarIth = adjustedMean[ithLevel];
        strIthLevel = StringUtilities.truncateString(originalLevels.get(ithLevel), 10);        

        for (int jthLevel = ithLevel + 1; jthLevel < nLevels; jthLevel++) {
            int nJth = (int)nWithinExplanatory[jthLevel];
            double xBarJth = adjustedMean[jthLevel];
            double diff_AdjustedMean = xBarIth - xBarJth;
            double diff_SampleMean = meanWithinExplanatory[ithLevel] - meanWithinExplanatory[jthLevel];

            qCritPlusMinus = wT * Math.sqrt(ms_Error * (1.0/nIth + 1.0/nJth + diff_SampleMean * diff_SampleMean / ssWithinForTK));
            lowCI_TK = diff_AdjustedMean - qCritPlusMinus;
            highCI_TK = diff_AdjustedMean + qCritPlusMinus;
            strJthLevel = StringUtilities.truncateString(originalLevels.get(jthLevel), 10);
        }
    }
}
    
    public Regr_Compare_Object get_Regr_Compare_Object() { return regr_Compare_Object; }
    public ArrayList<String> getRegrCompare_Report() { return regr_Compare_Report;} 
    public Regr_Compare_Controller getRegrCompare_Controller() { return regr_Compare_Controller; }
    public ObservableList<String> getTransformedLabels() { return transformedLabels; }
    public int getNCases() { return nInAll; }    
    public int getNLevels() { return nLevels; }    
    public double[] getDaCovariates() { return daExplans; }    
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
