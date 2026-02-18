/**************************************************
 *                ANOVA1_Cat_Model                *
 *                    01/26/25                    *
 *                      12:00                     *
 *************************************************/
/**************************************************
 *               ANOVA1 verified                  *
 *           Tukey-Kramer verified                *
 *          Anderson-Darling verified             *
 *               Tamhane, p72        .            *
 *          OmegaSquared and Cohen's f            *
 *                Kirk, p132                      *
 *                 02/12/24                       *
 *************************************************/
package anova1.categorical;

import proceduresOneUnivariate.*;
import utilityClasses.StringUtilities;
import probabilityDistributions.StudentizedRangeQ;
import dataObjects.QuantitativeDataVariable;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import probabilityDistributions.FDistribution;
import probabilityDistributions.TDistribution;
import proceduresOneUnivariate.NormProb_DiffModel;
import utilityClasses.DataUtilities;
import splat.*;
import utilityClasses.MyAlerts;

public class ANOVA1_Cat_Model {
    // POJOs
    
    private char[][] TK_Group_Symbols;
    
    private int nLevels, dfTreatments, dfError,  dfTotal, totalN;

    private double ssTreatments, ssError,  ssTotal, msTreatments, msError, 
           fStat, pValue, confidenceLevel, lowCI_TK, highCI_TK, qCritPlusMinus;
    
    double qTK, wT, omegaSquare, cohensD, sumAll, meanAll;
    
    double[] middleInterval;
    private final String explanatoryVariable;
    private final String responseVariable;
    private String subTitle, sourceString, strIthLevel, strJthLevel, 
                   returnValue, respVsExplanVar;

    // Make empty if no-print
    //String waldoFile = "ANOVA1_Cat_Model";
    String waldoFile = "";
    
    private ArrayList<String> anova1Report, postHocReport;
    public ObservableList<String> variableLabels;
    
    // My classes
    ArrayList<QuantitativeDataVariable> allTheQDVs;
    Data_Manager dm;
    FDistribution fDist;
    NormProb_Model normProb_Model;
    NormProb_DiffModel normProb_DiffModel;
    LevenesTest lev;  
    QuantitativeDataVariable allData_QDV;
    QuantitativeDataVariable qdvResiduals;
    StudentizedRangeQ studRangeQ;
    TDistribution tDistribution;

    public ANOVA1_Cat_Model (ANOVA1_Cat_Controller anova1_Cat_Controller, 
                            String explanatoryVariable, 
                            String responseVariable,
                            ArrayList<QuantitativeDataVariable>  allTheQDVs) {
        dm = anova1_Cat_Controller.getDataManager();
        dm.whereIsWaldo(74, waldoFile, "Constructing");
        variableLabels = FXCollections.observableArrayList();
        variableLabels = anova1_Cat_Controller.getVarLabels();
        this.allTheQDVs = allTheQDVs;    

        this.explanatoryVariable = explanatoryVariable;
        this.responseVariable = responseVariable;
        respVsExplanVar = responseVariable + " vs. " + explanatoryVariable;
    }
    
    public String continueInitializing() { 
        dm.whereIsWaldo(85, waldoFile, "continueInitializing()");
        returnValue = "OK";
        subTitle = responseVariable + " vs. " + explanatoryVariable;
        
        nLevels = allTheQDVs.size();
        confidenceLevel = 0.95; 
        anova1Report = new ArrayList<>();
        postHocReport = new ArrayList<>();
        TK_Group_Symbols = new char[nLevels][nLevels];
        
        for (int ithCol = 0; ithCol < nLevels; ithCol++) {            
            for (int jthRow = 0; jthRow < nLevels; jthRow++) {
                TK_Group_Symbols[ithCol][jthRow] = '-';
            }           
        }
   
        constructTheResiduals();
        
        normProb_Model = new NormProb_Model("One Way ANOVA", qdvResiduals);
        normProb_DiffModel = new NormProb_DiffModel("One Way ANOVA", qdvResiduals);
 
        returnValue = setupAnalysis();
        
        if (returnValue.equals("Cancel")) {
            return returnValue;
        } else {
            doOneWayANOVA();
            lev = new LevenesTest(allTheQDVs);
            return returnValue;
        }    
    }
   
    private void doOneWayANOVA() {  
        dm.whereIsWaldo(117, waldoFile, "doOneWayANOVA()");
        doAnalysis();        
        printANOVA_Results(); 
    }
    
    public void doOneWay4TwoWayANOVA() {
        dm.whereIsWaldo(123, waldoFile, "doOneWay4TwoWayANOVA()");
        doOneWayANOVA();
        doAnalysis();
    }
   
    private String setupAnalysis() {
        dm.whereIsWaldo(129, waldoFile, "setupAnalysis()");
        for (int ithLevel = 0; ithLevel < nLevels; ithLevel++) {
            String varLabel = allTheQDVs.get(ithLevel)
                                               .getTheVarLabel()
                                               .trim();            
            boolean variabilityFound = DataUtilities.checkForVariabilityInQDV(allTheQDVs.get(ithLevel));
            
            if (!variabilityFound) {
                MyAlerts.showNoVarianceInANOVAAlert(varLabel);
                return "Cancel";
            }
        }        
        
        for (int ithLevel = 0; ithLevel < nLevels; ithLevel++) {
            allTheQDVs.get(ithLevel).doMedianBasedCalculations();
            allTheQDVs.get(ithLevel).doMeanBasedCalculations();
        }        
        return "OK";
    } 
    
    public void doAnalysis() {  
        dm.whereIsWaldo(150, waldoFile, " *** doAnalysis()");

        sumAll = 0.;
        totalN = 0;
        for (int ithQDV = 0; ithQDV < nLevels; ithQDV++) {
            sumAll += allTheQDVs.get(ithQDV).getTheSum();
            totalN += allTheQDVs.get(ithQDV).getLegalN();
        }
        
        meanAll = sumAll / totalN;
        
        ssTotal = 0.0;
        for (int ithQDV = 0; ithQDV < nLevels; ithQDV++) {
            ArrayList<Double> sample = new ArrayList();
            sample = allTheQDVs.get(ithQDV).getLegalCases_AsALDoubles();
            for (int ithCase = 0; ithCase < sample.size(); ithCase++) {
               double temp = sample.get(ithCase) - meanAll;
               ssTotal += (temp * temp);
            }
        }

        ssError = 0.0;
        for (int ithLevel = 0; ithLevel < nLevels; ithLevel++) {
            ssError += allTheQDVs.get(ithLevel).getTheSS();
        }   
        
        ssTreatments = ssTotal - ssError;
        dfTotal = totalN - 1;
        dfError = totalN - nLevels;
        dfTreatments = nLevels - 1;        
        msTreatments = ssTreatments / dfTreatments;
        msError = ssError / dfError;
        fStat = msTreatments / msError;
        fDist = new FDistribution( dfTreatments, dfError);
        pValue = fDist.getRightTailArea(fStat);
        doTheEffectSizes();
        printTheStuff();
        doTukeyKramer();
    }  // end doFixedEffectsAnalysis
    
    private void constructTheResiduals() {
        dm.whereIsWaldo(191, waldoFile, "*** constructTheResiduals()");
        ArrayList<String> residuals = new ArrayList();
        for (int ithQDV = 0; ithQDV < nLevels; ithQDV++) {
            QuantitativeDataVariable qdvThisTime = allTheQDVs.get(ithQDV);
            int nThisQDV = qdvThisTime.getLegalN();
            double meanThisQDV = qdvThisTime.getTheMean();
            for (int ithCase = 0; ithCase < nThisQDV;ithCase++) {
                double ithResid = qdvThisTime.getIthDataPtAsDouble(ithCase) - meanThisQDV;
                residuals.add(Double.toString(ithResid));
            }
        }
        
        qdvResiduals = new QuantitativeDataVariable(
                       "Residuals", "Residuals",
                       residuals);  
        dm.whereIsWaldo(206, waldoFile, "--- End constructTheResiduals()");
    }
    
private void printTheStuff() {  
        dm.whereIsWaldo(208, waldoFile, " *** printTheStuff()");
        postHocReport.add(String.format("\n"));
        
        postHocReport.add(String.format("               **********         Parameter estimates for Levels         **********\n\n"));        
        postHocReport.add(String.format("   Treatment/   Sample     Sample      Sample     Std Err     Margin     Lower 95PC  Upper 95PC\n"));
        postHocReport.add(String.format("     Group       Size       Mean       St Dev     of mean    of Error      Bound        Bound\n"));
        
        for (int ithLevel = 0; ithLevel < nLevels; ithLevel++) {
            dm.whereIsWaldo(218, waldoFile, "--- ithLevel = " + ithLevel);
            strIthLevel = StringUtilities.truncateString(variableLabels.get(ithLevel), 10);
            int iSampleSize = allTheQDVs.get(ithLevel).getLegalN();
            double iMean = allTheQDVs.get(ithLevel).getTheMean();
            double iStandDev = allTheQDVs.get(ithLevel).getTheStandDev();
            double iStandErr = iStandDev / Math.sqrt(iSampleSize - 1.0);
            
            tDistribution = new TDistribution(iSampleSize - 1);
            middleInterval = new double[2];
            middleInterval = tDistribution.getInverseMiddleArea(0.95);
            double critical_t = middleInterval[1];
            double iMarginOfError = critical_t * iStandErr;
            double iLowerBound = iMean - iMarginOfError;
            double iUpperBound = iMean + iMarginOfError;
            postHocReport.add(String.format("%10s      %4d     %8.3f    %8.3f    %8.3f   %8.3f     %8.3f     %8.3f\n", strIthLevel,  
                                                                                                            iSampleSize,
                                                                                                            iMean,
                                                                                                            iStandDev,
                                                                                                            iStandErr,
                                                                                                            iMarginOfError,
                                                                                                            iLowerBound,
                                                                                                            iUpperBound));
        }     
    }

private void doTukeyKramer() {
    dm.whereIsWaldo(241, waldoFile, " ** doTukeyKramer()");
    studRangeQ = new StudentizedRangeQ();
    qTK = studRangeQ.qrange(0.95, // cumulative p -- use .95 if alpha = .05
                           (double)nLevels, // number of groups
                           (double)dfError, // df error
                            1.0);  // use a 1.0 to get stu range stat)
    wT = qTK / Math.sqrt(2.0); // Dean & Voss, p85
    postHocReport.add(String.format("\n  Tukey-Kramer Test  %40s \n\n", respVsExplanVar));
    postHocReport.add(String.format("   Treatment/   Treatment/     Mean                     95PC CI Lower  95PC CI Upper\n"));   
    postHocReport.add(String.format("     Group       Group      Difference        +/-           Bound          Bound\n"));    
    for (int ithLevel = 0; ithLevel < nLevels - 1; ithLevel++) {
        int nIth = allTheQDVs.get(ithLevel).getLegalN();
        double xBarIth = allTheQDVs.get(ithLevel).getTheMean();
        strIthLevel = StringUtilities.truncateString(variableLabels.get(ithLevel), 10);        
        //  Store for Grouping
        for (int jthLevel = ithLevel + 1; jthLevel < nLevels; jthLevel++) {
            int nJth = allTheQDVs.get(jthLevel).getLegalN();
            double xBarJth = allTheQDVs.get(jthLevel).getTheMean();
            double diff_mean = xBarIth - xBarJth;
            qCritPlusMinus = wT * Math.sqrt(msError * (1.0/nIth + 1.0/nJth));
            lowCI_TK = diff_mean - qCritPlusMinus;
            highCI_TK = diff_mean + qCritPlusMinus;
            strJthLevel = StringUtilities.truncateString(variableLabels.get(jthLevel), 10);
            postHocReport.add(String.format("%10s  %10s       %8.3f     %8.3f       %8.3f       %8.3f\n", strIthLevel,  
                                                                                      strJthLevel,
                                                                                      diff_mean,
                                                                                      qCritPlusMinus,
                                                                                      lowCI_TK,
                                                                                      highCI_TK));
        }
    }
}

    public void printANOVA_Results() {
        dm.whereIsWaldo(275, waldoFile, " --- printANOVA_Results()");
        String strPreANOVALine = "\n\n  One-way Analysis of Variance: " + respVsExplanVar;
        String strANOVALine1 = StringUtilities.eliminateMultipleBlanks(strPreANOVALine);
        String strANOVALine2 = StringUtilities.centerTextInString(strANOVALine1, 84);
        anova1Report.add(strANOVALine2);
        
        anova1Report.add(String.format("\n-----------------------------------------------------------------------------------\n"));
        anova1Report.add(String.format("Source of      Degrees of       Sum of\n"));
        anova1Report.add(String.format("Variation       Freedom        Squares         Mean Square       F       P-value\n"));
        anova1Report.add(String.format("-----------------------------------------------------------------------------------\n"));
        sourceString = StringUtilities.truncateString("Treatments", 12);

        anova1Report.add(String.format("%10s      %4d     %13.2f        %8.2f     %8.2f     %6.4f\n", 
                                                                                              sourceString,  
                                                                                              dfTreatments,
                                                                                              ssTreatments,
                                                                                              msTreatments,
                                                                                              fStat, pValue)); 
        
        sourceString = StringUtilities.truncateString("Error", 12);
        anova1Report.add(String.format("%10s      %4d     %13.2f        %8.2f\n", sourceString, dfError,
                                                                                        ssError, msError));
        
        sourceString = StringUtilities.truncateString("Total", 12);
        anova1Report.add(String.format("%10s      %4d     %13.2f\n", sourceString, dfTotal, ssTotal));
        anova1Report.add(String.format("\nOmega Square =  %5.3f\n", omegaSquare));
        anova1Report.add(String.format("   Cohen's d =  %5.3f\n", cohensD));
        anova1Report.add(String.format("-----------------------------------------------------------------------------------\n"));
        
        //  Add the Post Hoc linwa
        int nPostHocLines = postHocReport.size();        
        for (int ithPHLine = 0; ithPHLine < nPostHocLines; ithPHLine++) {
            //dm.whereIsWaldo(310, waldoFile, " --- ithPostHocLine = " + ithPHLine);
            anova1Report.add(postHocReport.get(ithPHLine));
        }
        dm.whereIsWaldo(309, waldoFile, " --- End printANOVA_Results()");
   }    // end printANOVA_Results
    
    private void doTheEffectSizes() {
        dm.whereIsWaldo(313, waldoFile, " --- doTheEffectSizes()");
        /************************************************************
         * Kirk, Experimental Design: Procedures for the Behavioral *
         * Sciences (4th).  pp 134- 137                             *
         ***********************************************************/        
        double omegaSquare_Numerator = ssTreatments - (nLevels - 1) * msError;
        
        if (fStat < 1) { omegaSquare_Numerator = 0; }
        
        double omegaSquare_Denominator = ssTotal + msError;
        omegaSquare = omegaSquare_Numerator / omegaSquare_Denominator;
        cohensD = Math.sqrt(omegaSquare / (1.0 - omegaSquare));
    }
    
    public Data_Manager getDataManager() { return dm; }      
    public ArrayList<QuantitativeDataVariable> getAllQDVs() { return allTheQDVs; }    
    public ArrayList<String> getANOVA1Report() { return anova1Report; }  
    public double getConfidenceLevel() { return confidenceLevel; }
    
    public void setConfidenceLevel( double atThisLevel) {
        confidenceLevel = atThisLevel;
    }
   
    public QuantitativeDataVariable getIthQDV(int ith) {
       return allTheQDVs.get(ith);
    }
    
    public ObservableList <String> getVarLabels() { return variableLabels; }
   
    public FDistribution getFDist() { return fDist; }
    public int getNLevels() {  return nLevels; }
    public double getSSError() { return ssError; }   
    public int getDFError() {  return dfError; }
    public double getMSError() { return msError; }
    
    public double getSSTreats() { return ssTreatments; }
    public int getDFTreats() {  return dfTreatments; }
    public double getMSTreats() { return msTreatments; }    
    
    public double getSSTotal() { return ssTotal; }
    public int getDFTotal() {  return dfTotal; }

    public double getFStat() { return fStat; }
   
    public String getExplanatoryVariable() {return explanatoryVariable; }
    public String getResponseVariable() {return responseVariable; }
    public String getSubTitle() { return subTitle; }
    public ObservableList <String> getCategoryLabels() {return variableLabels; }
    
    public NormProb_Model getNormProbModel() { return normProb_Model; }   
    public NormProb_DiffModel getNormProbDiffModel() { return normProb_DiffModel; } 
    public QuantitativeDataVariable getAllData_QDV() { return allData_QDV; }   
    public ArrayList<QuantitativeDataVariable> getAllTheQDVs() {return allTheQDVs; }   
    public double getPostHocPlusMinus() { return qCritPlusMinus; }    
    public double getLevenesStat() { return lev.getLevenes_W(); }
    public double getLevenesPValue() { return lev.getLevenes_PValue(); }
}
