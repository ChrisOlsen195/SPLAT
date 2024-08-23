/**************************************************
 *               Explore_2Ind_Model               *
 *                    02/19/24                    *
 *                      09:00                     *
 *************************************************/
package proceduresTwoUnivariate;

import proceduresOneUnivariate.NormProb_Model;
import utilityClasses.StringUtilities;
import dataObjects.QuantitativeDataVariable;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import proceduresOneUnivariate.PrintUStats_Model;
import utilityClasses.DataUtilities;
import splat.*;
import utilityClasses.MyAlerts;

public class Explore_2Ind_Model {
    // POJOs
    
    private int nLevels;

    private double confidenceLevel;
    private final String firstVariable;
    private final String secondVariable;
    private final String bunchaBlanks = "                                ";
    private String subTitle, returnValue, respVsExplanVar;

    // Make empty if no-print
    //String waldoFile = "Explore_2Ind_Model";
    String waldoFile = "";
    
    private ArrayList<String> anova1Report, alStr_AllTheLabels, twoVarReport;
    private final ObservableList<String> categoryLabels;
    
    // My classes
    ArrayList<QuantitativeDataVariable> all_TheQDVs;
    Data_Manager dm;
    NormProb_Model normProb_Model;
    QuantitativeDataVariable allData_QDV;

    public Explore_2Ind_Model (Explore_2Ind_Controller Explore_2Ind_Controller, 
                                 String firstVariable, 
                                 String secondVariable,
                                 ArrayList<QuantitativeDataVariable>  all_TheQDVs,
                                 ArrayList<String> alStr_AllTheLabels) {
        dm = Explore_2Ind_Controller.getDataManager();
        categoryLabels = FXCollections.observableArrayList();
        this.alStr_AllTheLabels = alStr_AllTheLabels;
        this.all_TheQDVs = all_TheQDVs;
        this.firstVariable = firstVariable;
        this.secondVariable = secondVariable;
        respVsExplanVar = secondVariable + " vs. " + firstVariable;
    }
    
    public String continueInitializing() { 
        dm.whereIsWaldo(58, waldoFile, "continueInitializing()");
        returnValue = "OK";
        
        for (int ithLabel = 0; ithLabel < alStr_AllTheLabels.size(); ithLabel++) {
            categoryLabels.add(alStr_AllTheLabels.get(ithLabel));
        }
        
        subTitle = secondVariable + " vs. " + firstVariable;
        allData_QDV = all_TheQDVs.get(0);
        allData_QDV.setTheVarLabel("Treatment Residuals");
        normProb_Model = new NormProb_Model("One Way ANOVA", allData_QDV);

        nLevels = all_TheQDVs.size() - 1;
        confidenceLevel = 0.95; 
        
        returnValue = setupAnalysis();
        
        if (returnValue.equals("Cancel")) {
            return returnValue;
        } else {
            doOneWayANOVA();
            return returnValue;
        }
    }
   
    private void doOneWayANOVA() {  
        dm.whereIsWaldo(84, waldoFile, "doOneWayANOVA()");
        prepare_TwoStats_Report();        
    }
   
    private String setupAnalysis() {
        dm.whereIsWaldo(89, waldoFile, "setupAnalysis()");
        for (int ithLevel = 1; ithLevel < nLevels; ithLevel++) {
            String varLabel = all_TheQDVs.get(ithLevel)
                                               .getTheVarLabel()
                                               .trim();            
            boolean variabilityFound = DataUtilities.checkForVariabilityInQDV(all_TheQDVs.get(ithLevel));
            if (!variabilityFound) {
                MyAlerts.showNoVarianceIn2IndAlert(varLabel);
                return "Cancel";
            }
        }        
        
        for (int ithLevel = 0; ithLevel <= nLevels; ithLevel++) {
            all_TheQDVs.get(ithLevel).doMedianBasedCalculations();
            all_TheQDVs.get(ithLevel).doMeanBasedCalculations();
        }
        return "OK";
    }    

    private void prepare_TwoStats_Report() {
        dm.whereIsWaldo(109, waldoFile, "prepare_TwoStats_Report()");
        int int_1, int_2;
        double dbl_1, dbl_2;
        String str_1, str_2, centeredExplanVar, centeredRespVar, responseLabel2Print, explanLabel2Print;
        twoVarReport = new ArrayList();
        explanLabel2Print = getLeftMostNChars(all_TheQDVs.get(0).getTheVarLabel(), 10);
        responseLabel2Print = getLeftMostNChars(all_TheQDVs.get(1).getTheVarLabel(), 10);
        centeredExplanVar = StringUtilities.centerTextInString(explanLabel2Print, 10);
        centeredRespVar = StringUtilities.centerTextInString(responseLabel2Print, 10);
        PrintUStats_Model prntU_X = new PrintUStats_Model(centeredExplanVar, all_TheQDVs.get(0), true);
        PrintUStats_Model prntU_Y = new PrintUStats_Model(centeredRespVar, all_TheQDVs.get(1), true);        
        addNBlankLinesToTwoStatsReport(2);
        
        twoVarReport.add(String.format("     *******  File information  *******"));
        addNBlankLinesToTwoStatsReport(2);   
        
        str_1 = StringUtilities.truncateString(firstVariable + bunchaBlanks, 16);
        str_2 = StringUtilities.truncateString(secondVariable + bunchaBlanks, 16);

        twoVarReport.add(String.format(" Variables: %10s  %10s   ", str_1, str_2));        
        
        addNBlankLinesToTwoStatsReport(1);   
        int_1 = prntU_X.getLegalN();  int_2 = prntU_Y.getLegalN();        
        twoVarReport.add(String.format("         N: %6d            %6d", int_1, int_2));        
        addNBlankLinesToTwoStatsReport(2); 
        
        twoVarReport.add(String.format(" *****  Basic mean based statistics  *****"));
        addNBlankLinesToTwoStatsReport(2); 
        dbl_1 = prntU_X.getTheMean();  dbl_2 = prntU_Y.getTheMean(); 
        twoVarReport.add(String.format("       Mean:   %8.4f        %8.4f", dbl_1, dbl_2));
        addNBlankLinesToTwoStatsReport(1);
        dbl_1 = prntU_X.getTheVariance();  dbl_2 = prntU_Y.getTheVariance(); 
        twoVarReport.add(String.format("   Variance: %10.4f      %10.4f", dbl_1, dbl_2));   
        addNBlankLinesToTwoStatsReport(1);
        dbl_1 = prntU_X.getTheStDev();  dbl_2 = prntU_Y.getTheStDev(); 
        twoVarReport.add(String.format("   StandDev: %10.4f      %10.4f", dbl_1, dbl_2));  
        addNBlankLinesToTwoStatsReport(1);
        dbl_1 = prntU_X.getTheSkew();  dbl_2 = prntU_Y.getTheSkew(); 
        twoVarReport.add(String.format("       Skew:   %8.4f        %8.4f", dbl_1, dbl_2));
        addNBlankLinesToTwoStatsReport(1);  addNBlankLinesToTwoStatsReport(2);
        
        twoVarReport.add(String.format("  *****  Other mean based statistics  *****"));        
        addNBlankLinesToTwoStatsReport(2);
        dbl_1 = prntU_X.getTheTrimmedMean();  dbl_2 = prntU_Y.getTheTrimmedMean();
        twoVarReport.add(String.format("   Trimmed mean:   %8.4f    %8.4f", dbl_1, dbl_2)); 
        addNBlankLinesToTwoStatsReport(1);
        dbl_1 = prntU_X.getTheKurtosis();  dbl_2 = prntU_Y.getTheKurtosis();
        twoVarReport.add(String.format("       Kurtosis:   %8.4f    %8.4f", dbl_1, dbl_2));        
        addNBlankLinesToTwoStatsReport(1);
        dbl_1 = prntU_X.getTheCV();  dbl_2 = prntU_Y.getTheCV();
        twoVarReport.add(String.format("             CV:   %8.4f    %8.4f", dbl_1, dbl_2));        
        addNBlankLinesToTwoStatsReport(3); 
        
        twoVarReport.add(String.format("  *****     Five-number summaries    *****"));
        addNBlankLinesToTwoStatsReport(2);
        dbl_1 = prntU_X.getTheMin();  dbl_2 = prntU_Y.getTheMin();
        twoVarReport.add(String.format("        Minimum:   %8.4f     %8.4f", dbl_1, dbl_2)); 
        addNBlankLinesToTwoStatsReport(1);
        dbl_1 = prntU_X.getQ1();  dbl_2 = prntU_Y.getQ1();       
        twoVarReport.add(String.format("             Q1:   %8.4f     %8.4f", dbl_1, dbl_2));  
        addNBlankLinesToTwoStatsReport(1);
        dbl_1 = prntU_X.getTheMedian();  dbl_2 = prntU_Y.getTheMedian();
        twoVarReport.add(String.format("         Median:   %8.4f     %8.4f", dbl_1, dbl_2));        
        addNBlankLinesToTwoStatsReport(1);
        dbl_1 = prntU_X.getQ3();  dbl_2 = prntU_Y.getQ3();
        twoVarReport.add(String.format("             Q3:   %8.4f     %8.4f", dbl_1, dbl_2));        
        addNBlankLinesToTwoStatsReport(1);
        dbl_1 = prntU_X.getTheMax();  dbl_2 = prntU_Y.getTheMax();
        twoVarReport.add(String.format("        Maximum:   %8.4f     %8.4f", dbl_1, dbl_2));  
        addNBlankLinesToTwoStatsReport(3);
        
        twoVarReport.add(String.format("  *****  Other median based statistics  *****"));  
        addNBlankLinesToTwoStatsReport(2);
        dbl_1 = prntU_X.getTheIQR();  dbl_2 = prntU_Y.getTheIQR();
        twoVarReport.add(String.format("            IQR:   %8.4f     %8.4f", dbl_1, dbl_2));        
        addNBlankLinesToTwoStatsReport(1);
        dbl_1 = prntU_X.getTheRange();  dbl_2 = prntU_Y.getTheRange();
        twoVarReport.add(String.format("          Range:   %8.4f     %8.4f", dbl_1, dbl_2));   
    }
    
    private void addNBlankLinesToTwoStatsReport(int thisMany) {
        StringUtilities.addNLinesToArrayList(twoVarReport, thisMany);
    }
    
    public static String getLeftMostNChars(String original, int leftChars) {
       return StringUtilities.getleftMostNChars(original, leftChars);
   }

    public Data_Manager getDataManager() { return dm; }      
    public ArrayList<QuantitativeDataVariable> getAllQDVs() { return all_TheQDVs; }    
    public ArrayList<String> getANOVA1Report() { return anova1Report; }  
    public double getConfidenceLevel() { return confidenceLevel; }    
    public void setConfidenceLevel( double atThisLevel) {
        confidenceLevel = atThisLevel;
    }  
    public QuantitativeDataVariable getIthQDV(int ith) {
       return all_TheQDVs.get(ith);
    }
   
    public String getExplanatoryVariable() {return firstVariable; }
    public String getResponseVariable() {return secondVariable; }
    public String getSubTitle() { return subTitle; }
    public ObservableList <String> getCategoryLabels() {return categoryLabels; }   
    public NormProb_Model getNormProbModel() { return normProb_Model; }   
    public QuantitativeDataVariable getAllData_QDV() { return allData_QDV; }   
    public ArrayList<QuantitativeDataVariable> getAllTheQDVs() {return all_TheQDVs; }    
    public ArrayList<String> getStatsReport() { return twoVarReport; }
}

