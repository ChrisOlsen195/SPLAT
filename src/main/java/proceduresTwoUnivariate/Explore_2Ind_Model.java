/**************************************************
 *               Explore_2Ind_Model               *
 *                    01/29/25                    *
 *                      12:00                     *
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

    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    private ArrayList<String> anova1Report, twoVarReport;
    public ObservableList<String> variableLabels;
    
    // My classes
    ArrayList<QuantitativeDataVariable> all_TheQDVs;
    Data_Manager dm;
    NormProb_Model normProb_Model;
    QuantitativeDataVariable allData_QDV;

    public Explore_2Ind_Model (Explore_2Ind_Controller explore_2Ind_Controller, 
                                 String firstVariable, 
                                 String secondVariable,
                                 ArrayList<QuantitativeDataVariable>  all_TheQDVs) {
        if (printTheStuff == true) {
            System.out.println("47 *** Explore_2Ind_Model, constructing");
        }
        dm = explore_2Ind_Controller.getDataManager();
        variableLabels = FXCollections.observableArrayList();
        variableLabels = explore_2Ind_Controller.getCategoryLabels();
        //System.out.println(" 52 Explore_2Ind_Model, categoryLabels = " + variableLabels);
        this.all_TheQDVs = all_TheQDVs;
        this.firstVariable = firstVariable;
        this.secondVariable = secondVariable;
        respVsExplanVar = secondVariable + " vs. " + firstVariable;
    }
    
    public String continueInitializing() { 
        if (printTheStuff == true) {
            System.out.println("60 --- Explore_2Ind_Model, continueInitializing()");
        }
        returnValue = "OK";
        if (printTheStuff == true) {
            System.out.println("64 --- Explore_2Ind_Model, continueInitializing()");
        }        
        
        subTitle = secondVariable + " vs. " + firstVariable;
        allData_QDV = all_TheQDVs.get(0);
        allData_QDV.setTheVarLabel("Treatment Residuals");
        normProb_Model = new NormProb_Model("One Way ANOVA", allData_QDV);
        if (printTheStuff == true) {
            System.out.println("77 --- Explore_2Ind_Model, continueInitializing()");
        }
        nLevels = all_TheQDVs.size() - 1;
        //confidenceLevel = 0.95; 
        
        returnValue = setupAnalysis();
        if (printTheStuff == true) {
            System.out.println("84 --- Explore_2Ind_Model, continueInitializing(), returnValue = " + returnValue);
        }
        if (returnValue.equals("Cancel")) {
            return returnValue;
        } else {
            doReportPrep();
            return returnValue;
        }
    }
   
    private void doReportPrep() { prepare_TwoStats_Report(); }  // !?!?!?!?!?!?
   
    private String setupAnalysis() {
        if (printTheStuff == true) {
            System.out.println("92 --- Explore_2Ind_Model, setupAnalysis()");
        }
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
        if (printTheStuff == true) {
            System.out.println("114 --- Explore_2Ind_Model, prepare_TwoStats_Report()");
        }
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
        if (printTheStuff == true) {
            System.out.println("196 --- Explore_2Ind_Model, END prepare_TwoStats_Report()");
        }
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
 
    public QuantitativeDataVariable getIthQDV(int ith) {
       return all_TheQDVs.get(ith);
    }
   
    public String getExplanatoryVariable() {return firstVariable; }
    public String getResponseVariable() {return secondVariable; }
    public String getSubTitle() { return subTitle; }
    public ObservableList <String> getCategoryLabels() { return variableLabels; }  
    public NormProb_Model getNormProbModel() { return normProb_Model; }   
    public QuantitativeDataVariable getAllData_QDV() { return allData_QDV; }   
    public ArrayList<QuantitativeDataVariable> getAllTheQDVs() {return all_TheQDVs; }    
    public ArrayList<String> getStatsReport() { return twoVarReport; }
}

