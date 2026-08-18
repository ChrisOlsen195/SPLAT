/**********************************************************************
 *                         Boot_Controller                            *
 *                             08/09/26                               *
 *                               15:00                                *
 *********************************************************************/
package bootstrapping;

import dataObjects.CatQuantDataVariable;
import dataObjects.ColumnOfData;
import dataObjects.QuantitativeDataVariable;
import dialogs.Splat_Dialog;
import dialogs.regression.Regr_Dialog;
import splat.Data_Manager;
import utilityClasses.MyAlerts;
import dialogs.t_and_z.*;
import java.util.ArrayList;
import utilityClasses.MyYesNoAlerts;
import dialogs.*;

public class Boot_Controller extends Splat_Dialog {
    
    public Boolean goodToGo, checkForLegalChoices;
    Boolean[] cbArr_Rep_Stat_Values, theStrIsNumeric;
    
    int nCheckBoxes, sampleSize, nRepetitions, nStatsChecked, TWO;
    int nStatProcess;
    
    double original_XLower, original_XUpper, original_XYRange;
    double adjusted_XLower, adjusted_XUpper, thetaHat, thetaNull;
    double[] arr_oneVarSample, adjustedValues;
    
    String strWhichBoot, strDescrVarOne, strDescrVarTwo, strChosenStatistic, 
            tidyOrTI8x, strReturnStatusX, strReturnStatusY, strTitle1;
    
    String strExitStatus, strChosenVariable;
    
    String[] cbArr_One_Stat_Descriptions = {"Mean", "Variance", "Standard Deviation", "Skew",
                                    "Trimmed Mean", "Kurtosis", "Coef. of Variation", "Minimum",
                                    "First quartile", "Median", "Third quartile", "Maximum",
                                    "Interquartile range", "Range", "Tri-Mean"};
    
    String[] cbArr_Two_Stat_Descriptions = {"Means", "Variances", "Standard Deviations", "Skew",
                                    "Trimmed Means", "Kurtosis", "Coef. of Variations", "Minimums",
                                    "First quartile", "Median", "Third quartile", "Maximum",
                                    "Interquartile ranges", "Ranges", "Tri-Means"};
    
    // Make empty if no-print
    //String waldoFile = "Boot_Controller";
    String waldoFile = "";
    
    public CatQuantDataVariable cqdv;
    ChooseStats_Dialog chooseStats_Dialog;
    ChooseStats_Dashboard chooseStats_Dashboard;
    DistrModel original_DistrModel, bootstrap_DistrModel;
    ChooseStats_DialogView chooseStats_DialogView;
    DotPlot_DistrView originalDotPlot_DistrView;
    DotPlot_DistrView shiftedDotPlot_DistrView;
    Generic_AskForInteger generic_AskForInteger;
    Histo_DistrView originalHisto_DistrView;
    Histo_DistrView shiftedHisto_DistrView;
    Boot_OneStat_Dialog boot_OneVar_Dialog;
    Indep_t_Dialog indep_t_Dialog; 
    Indep_t_TI8x_Dialog indep_t_TI8x_Dialog;
    Indep_t_Tidy_Dialog indep_t_Tidy_Dialog;
    Boot_OneStat boot_OneStat;
    Boot_TwoStats boot_TwoStats;
    Boot_Regression boot_Regression;
    ArrayList<ColumnOfData> alCol_Regr;
    Regr_Dialog boot_Regr_Dialog;
    // ****************  These may be vestigial   *********************
    ColumnOfData alCol_OneVar_Sample;
    ArrayList<ColumnOfData> alCol_TwoVar_Sample, alCol_TwoVariables, 
                            alCol_indep_t;
// ****************  These may be vestigial   *********************
    Data_Manager dm;
    QuantitativeDataVariable qdv_oneVar_Sample, qdv_bootstrappedStats,
                             qdv_Shifted;
    ArrayList<QuantitativeDataVariable> allTheQDVs; 
    MyYesNoAlerts myYesNoAlerts;
    
    public Boot_Controller(Data_Manager dm, String strWhichBoot) {
        dm.whereIsWaldo(80, waldoFile, " *** Constructing");  
        dm.whereIsWaldo(81, waldoFile, " --- Constructing " + strWhichBoot);
        this.dm = dm;
        this.strWhichBoot = strWhichBoot;
        strReturnStatus = "OK";
        nStatProcess = 0;
        TWO = 2;
        myYesNoAlerts = new MyYesNoAlerts();
        setTitle("Bootstrapping");
        nCheckBoxes = cbArr_One_Stat_Descriptions.length;
        cbArr_Rep_Stat_Values = new Boolean[nCheckBoxes];
        dm.whereIsWaldo(91, waldoFile, " --- End Constructing");
    }

    public String doTheControllerThing() {   
        dm.whereIsWaldo(95, waldoFile, " --- doTheControllerThing()"); 
        int casesInStruct = dm.getNCasesInStruct();
        if (casesInStruct == 0) {
            MyAlerts.showAintGotNoDataAlert();
            strReturnStatus = "Cancel";
            return "Cancel";
        }

        switch (strWhichBoot) {
            case "ChooseUnivStat": 
                processOneStat();
                dm.whereIsWaldo(106, waldoFile, "... OneStat chosen");
                break;
            case "ChooseTwoStat": 
                dm.whereIsWaldo(109, waldoFile, "... TwoStats chosen");
                doTidyOrNot();
                processTwoStat();
                break;
            case "ChooseRegression":
                dm.whereIsWaldo(114, waldoFile, "... Regression chosen");
                processRegression();
                break;
            default:
                String switchFailure = "Switch failure: Boot_Controller 118: " + strWhichBoot;
                MyAlerts.showUnexpectedErrorAlert(switchFailure); 
                strReturnStatus = "Cancel";
        }

        if (strReturnStatus.equals("Cancel")) { return strReturnStatus; }

        original_XYRange = qdv_bootstrappedStats.getTheRange();
        original_XLower = qdv_bootstrappedStats.getMinValue() - .025 * original_XYRange;
        original_XUpper = qdv_bootstrappedStats.getMaxValue() + .025 * original_XYRange;

        original_DistrModel = new DistrModel(this, qdv_bootstrappedStats);
        original_DistrModel.set_ShadeLeft(false);
        original_DistrModel.set_ShadeRight(false);
        original_DistrModel.set_LeftTail_IsChecked(false);
        original_DistrModel.set_TwoTail_IsChecked(false);
        original_DistrModel.set_RightTail_IsChecked(false);
        
        /******************************************************************
         *              Create a shifted copy for hypoth test             *
         *****************************************************************/
        int nBoots = qdv_bootstrappedStats.getOriginalN();
        adjustedValues = new double[nBoots];
        if (thetaNull <= thetaHat) {
            for (int ithBoot = 0; ithBoot < nBoots; ithBoot++) {
                adjustedValues[ithBoot] = qdv_bootstrappedStats.getIthDataPtAsDouble(ithBoot) - (thetaHat - thetaNull);
            } 

            adjusted_XLower = original_XLower - (thetaHat - thetaNull);
            adjusted_XUpper = original_XUpper;
        }
        else {
            for (int ithBoot = 0; ithBoot < nBoots; ithBoot++) {
                adjustedValues[ithBoot] = qdv_bootstrappedStats.getIthDataPtAsDouble(ithBoot) + (thetaNull - thetaHat);
            } 
            adjusted_XLower = original_XLower; 
            adjusted_XUpper = thetaNull + (thetaNull - thetaHat);     
        }

        qdv_Shifted = new QuantitativeDataVariable("null", "null", adjustedValues); 
        bootstrap_DistrModel = new DistrModel(this, qdv_Shifted);
        bootstrap_DistrModel.set_ShadeLeft(false);
        bootstrap_DistrModel.set_ShadeRight(false);
        bootstrap_DistrModel.set_LeftTail_IsChecked(false);
        bootstrap_DistrModel.set_TwoTail_IsChecked(false);
        bootstrap_DistrModel.set_RightTail_IsChecked(false);

        chooseStats_Dashboard = new ChooseStats_Dashboard(this, original_DistrModel,
                                                                          bootstrap_DistrModel);
        chooseStats_Dashboard.populateTheBackGround();
        chooseStats_Dashboard.putEmAllUp();
        originalHisto_DistrView = chooseStats_Dashboard.get_Boot_ChooseStats_OriginalHisto_DistrView();
        originalDotPlot_DistrView = chooseStats_Dashboard.get_Boot_ChooseStats_OriginalDotPlot_DistrView();
  
        shiftedHisto_DistrView = chooseStats_Dashboard.get_Boot_ChooseStats_ShiftedHisto_DistrView();
        shiftedDotPlot_DistrView = chooseStats_Dashboard.get_Boot_ChooseStats_ShiftedDotPlot_DistrView();
        
        chooseStats_Dashboard.showAndWait();
        strReturnStatus = chooseStats_Dashboard.getStrReturnStatus();  
        return strReturnStatus;
    }
    
    private String processOneStat() {
        dm.whereIsWaldo(181, waldoFile, " --- processOneStat()"); 
        // ----------------------------------------------------
        nStatProcess = 1;
        chooseStats_Dialog = new ChooseStats_Dialog(this);
        chooseStats_Dialog.showAndWait();
        strReturnStatus = chooseStats_Dialog.getStrReturnStatus();
        if (strReturnStatus.equals("Cancel")) { return strReturnStatus;  }
        int statChosenIndex = chooseStats_Dialog.getStatCheckedIndex();
        dm.whereIsWaldo(189, waldoFile, " ... statChosenIndex = " + statChosenIndex); 
        strChosenStatistic = cbArr_One_Stat_Descriptions[statChosenIndex];
        dm.whereIsWaldo(191, waldoFile, " ... strChosenStatistic = " + strChosenStatistic);
        nRepetitions = chooseStats_Dialog.getNReps();
        nStatsChecked = chooseStats_Dialog.getNStatsChecked();  
        strTitle1 = strChosenStatistic + "  -- " + getDescriptionOfVariable();
        // ----------------------------------------------------
        
        boot_OneVar_Dialog = new Boot_OneStat_Dialog(dm, "QUANTITATIVE");
        boot_OneVar_Dialog.showAndWait();
        strReturnStatus = boot_OneVar_Dialog.getStrReturnStatus();
        
        if (strReturnStatus.equals("Cancel")) { return strReturnStatus; }
        
        strChosenVariable = boot_OneVar_Dialog.getDescriptionOfVariable();
        dm.whereIsWaldo(204, waldoFile, " ... strChosenVariable = " + strChosenVariable);
        alCol_OneVar_Sample = boot_OneVar_Dialog.getData();
        qdv_oneVar_Sample = new QuantitativeDataVariable("null", "null", alCol_OneVar_Sample);        
        
        arr_oneVarSample = new double[sampleSize];
        arr_oneVarSample = qdv_oneVar_Sample.getTheUCDO().getTheDataSorted();

        boot_OneStat = new Boot_OneStat(this, arr_oneVarSample);
        //strChosenStatistic = boot_OneStat.getTheChosenStat();
        strReturnStatus = boot_OneStat.constructTheBootstrapSample();

        if (strReturnStatus.equals("Cancel")) { return strReturnStatus; }
        qdv_bootstrappedStats = boot_OneStat.getTheQDV();
        thetaHat = qdv_bootstrappedStats.getTheMean();
        thetaNull = boot_OneVar_Dialog.getHypothesizedMean();
        dm.whereIsWaldo(219, waldoFile, " ... end processOneStat()"); 
        return "OK";
    }
    
    private String processTwoStat() {
        dm.whereIsWaldo(224, waldoFile, " --- processTwoStat()"); 
        // ----------------------------------------------------
        nStatProcess = 2;
        chooseStats_Dialog = new ChooseStats_Dialog(this);
        chooseStats_Dialog.showAndWait();
        strReturnStatus = chooseStats_Dialog.getStrReturnStatus();
        if (strReturnStatus.equals("Cancel")) { return strReturnStatus;  }
        int statChosenIndex = chooseStats_Dialog.getStatCheckedIndex();
        dm.whereIsWaldo(232, waldoFile, " ... statChosenIndex = " + statChosenIndex); 
        strChosenStatistic = cbArr_Two_Stat_Descriptions[statChosenIndex]; 
        dm.whereIsWaldo(234, waldoFile, " --- processTwoStat(), strChosenStatistic = " + strChosenStatistic);
        nRepetitions = chooseStats_Dialog.getNReps();
        nStatsChecked = chooseStats_Dialog.getNStatsChecked(); 

        if (strReturnStatus.equals("Cancel")) { return strReturnStatus; }

        strChosenVariable = indep_t_Dialog.getPreferredFirstVarDescription();
        strDescrVarTwo = indep_t_Dialog.getPreferredSecondVarDescription();
        strTitle1 = strChosenStatistic + "  -- " + strChosenVariable + " - " + strDescrVarTwo;
        alCol_TwoVar_Sample = indep_t_Dialog.getData();

        boot_TwoStats = new Boot_TwoStats(this, alCol_TwoVar_Sample);
        //strChosenStatistic = boot_TwoStats.getTheChosenStat();
        strReturnStatus = boot_TwoStats.constructTheBootstrapSample();

        if (strReturnStatus.equals("Cancel")) { return strReturnStatus; }
        qdv_bootstrappedStats = boot_TwoStats.getTheQDV();
        thetaHat = qdv_bootstrappedStats.getTheMean();
        //thetaNull = indep_t_Dialog.;
        dm.whereIsWaldo(253, waldoFile, " ... thetaHat = " + thetaHat); 
        dm.whereIsWaldo(254, waldoFile, " ... thetaNull = " + thetaNull); 
        dm.whereIsWaldo(255, waldoFile, " ... end processTwoStat()");
        return "OK";        
    }
    
    private String processRegression() {
        dm.whereIsWaldo(260, waldoFile, " --- processRegression()");
        strTitle1 = "Bootstrapped Slope (Utility test)";
        strWhichBoot = "ChooseRegression";
        tidyOrTI8x = "TI8x";
        dm.setTI8xorTIDY("TI8x");
        dm.whereIsWaldo(265, waldoFile, " ... processRegression()");
        boot_Regr_Dialog = new Regr_Dialog(dm, "varType", "Bootstrap Regression Slope");
        boot_Regr_Dialog.showAndWait();
        dm.whereIsWaldo(268, waldoFile, " ... processRegression()");
        strReturnStatus = boot_Regr_Dialog.getStrReturnStatus();
        if (strReturnStatus.equals("Cancel")) { return strReturnStatus; }
        
        String strDirections = "                     Easy Peasy Directions:" + 
                        "\n\n Please indicate the desired number of repetitions.";
        double width = 250.;
        double height = 200.;
        String strTitle = "This is a title";
        generic_AskForInteger = new Generic_AskForInteger(this, 
                                                        strTitle, 0.90,
                                                        strDirections,
                                                        width, height);
        
        strExitStatus = "Blank";
        dm.whereIsWaldo(281, waldoFile, " ... processRegression()");
        do {
            generic_AskForInteger.showAndWait();        
            int daNumber = generic_AskForInteger.getNReps();
            generic_AskForInteger.hide();
        } while (!strExitStatus.equals("OK") && !strExitStatus.equals("Cancel"));
        
        if (strExitStatus.equals("OK")) nRepetitions = generic_AskForInteger.getNReps();
        
        if (strExitStatus.equals("Cancel")) {
            return "Cancel";
        }

        strDescrVarOne = boot_Regr_Dialog.getFirstVarLabel_InFile();
        strDescrVarTwo = boot_Regr_Dialog.getSecondVarLabel_InFile();
        alCol_Regr = boot_Regr_Dialog.getData();
        boot_Regression = new Boot_Regression(this, alCol_Regr);
        boot_Regression.constructTheBootstrapSample();
        qdv_bootstrappedStats = boot_Regression.getTheQDV();
        thetaHat = qdv_bootstrappedStats.getTheMean();
        thetaNull = 0.0;
        thetaHat = qdv_bootstrappedStats.getTheMean();
        return "OK";
    }
    
    public String doTidyOrNot() {
        dm.whereIsWaldo(307, waldoFile, " --- doTidyOrNot()");      
        dm.setRawOrSummary("Raw");
        //  Check for existing value ( = not NULL)
        tidyOrTI8x = dm.getTIorTIDY();
        if (tidyOrTI8x.equals("NULL")) {
            myYesNoAlerts.setTheYes("Tidy");
            myYesNoAlerts.setTheNo("TI8x");
            myYesNoAlerts.showTidyOrTI8xAlert();
            // Get the Alert Yes/No = 'Yes' or 'No' and re-cast tidyOrTI8x
            tidyOrTI8x = myYesNoAlerts.getYesOrNo();
            if (tidyOrTI8x == null) { return "Cancel"; }
        }

        //              First time through                 Repeat
        if (tidyOrTI8x.equals("Yes") || tidyOrTI8x.equals("Tidy")) { // = Tidy
            tidyOrTI8x = "Tidy";
            dm.setTI8xorTIDY("Tidy");
            strReturnStatus = doTidy(); 

            if (!strReturnStatusX.equals("OK") || !strReturnStatusX.equals("OK")) {
                strReturnStatus = "Cancel"; 
                return strReturnStatus;
            }
        } else {    // No = TI8x
            tidyOrTI8x = "TI8x";
            dm.setTI8xorTIDY("TI8x");
            strReturnStatus =  doTI8x();
            if (strReturnStatus.equals("Cancel")) { return "Cancel"; }
            if (!strReturnStatusX.equals("OK") || !strReturnStatusX.equals("OK")) {strReturnStatus = "Cancel"; }
        }  
        
        //thetaHat = qdv_bootstrappedStats.getTheMean();
        //thetaNull = indep_t_Dialog.getHypothesizedDiffInMeans();
        dm.whereIsWaldo(340, waldoFile, " ... end doTidyOrNot()");
        return strReturnStatus;
    }
        
    protected String doTI8x() {
        dm.whereIsWaldo(345, waldoFile, " --- doTI8x()");
        goodToGo = true;
        int casesInStruct = dm.getNCasesInStruct();
        
        if (casesInStruct == 0) {
            MyAlerts.showAintGotNoDataAlert();
            strReturnStatus = "Cancel";
            return strReturnStatus;
        }

        indep_t_TI8x_Dialog = new Indep_t_TI8x_Dialog(dm);
        indep_t_TI8x_Dialog.showAndWait();
        
        strReturnStatus = indep_t_Dialog.getStrReturnStatus();
        strReturnStatusX = indep_t_Dialog.getReturnStatusX();
        strReturnStatusY = indep_t_Dialog.getReturnStatusY();
        
        if (strReturnStatus == null) { strReturnStatus = "Cancel"; }
        if (strReturnStatusX == null) { strReturnStatusX = "Cancel"; }
        if (strReturnStatusY == null) { strReturnStatusY = "Cancel"; }

        if (!strReturnStatus.equals("OK") 
                || !strReturnStatusX.equals("OK")
                || ! strReturnStatusY.equals("OK")) {
            return "Cancel";
        }
        
        alCol_TwoVariables = new ArrayList<>();
        alCol_TwoVariables = indep_t_Dialog.getData();
        
        dm.whereIsWaldo(375, waldoFile, " ... end doTI8x()");
        return strReturnStatus;
    }
        
    protected String doTidy() {
        dm.whereIsWaldo(380, waldoFile, " --- doTidy()");
        strReturnStatus = "OK";
        strReturnStatusX = "OK";
        strReturnStatusX = "OK";
        do {
            int casesInStruct = dm.getNCasesInStruct();            
            if (casesInStruct == 0) {
                MyAlerts.showAintGotNoDataAlert();
                return "Cancel";
            }
            indep_t_Tidy_Dialog = new Indep_t_Tidy_Dialog(dm, "Indep_t_tidy");
            indep_t_Dialog.showAndWait();

            strReturnStatus = indep_t_Dialog.getStrReturnStatus();
            strReturnStatusX = indep_t_Dialog.getReturnStatusX();
            strReturnStatusY = indep_t_Dialog.getReturnStatusY();
            
            if (strReturnStatus == null) { strReturnStatus = "Cancel"; }
            if (strReturnStatusX == null) { strReturnStatusX = "Cancel"; }
            if (strReturnStatusY == null) { strReturnStatusY = "Cancel"; }

            if (!strReturnStatus.equals("OK") 
                    || !strReturnStatusX.equals("OK")
                    || ! strReturnStatusY.equals("OK")) {
                return "Cancel";
            }
            
            alCol_indep_t = indep_t_Dialog.getData();
            int nLevels = alCol_indep_t.get(0).getNumberOfDistinctValues();
            if (nLevels != TWO) {
                MyAlerts.showExplore2Ind_NE2_LevelsAlert();
                goodToGo = false;
                return "Cancel";
            }
            dm.whereIsWaldo(414, waldoFile, " ... doTidy(), strReturnStatus = " + strReturnStatus);
            checkForLegalChoices = validateTidyChoices();
        } while (!checkForLegalChoices);

        //                                Categorical,             Quantitative            return All and individuals
        dm.whereIsWaldo(419, waldoFile, " ... doTidy(), strReturnStatus = " + strReturnStatus);
        cqdv = new CatQuantDataVariable(dm, alCol_indep_t.get(0), alCol_indep_t.get(1), true, "ANOVA1_Cat_Controller"); 
        dm.whereIsWaldo(421, waldoFile, " ... doTidy(), strReturnStatus = " + strReturnStatus);
        strReturnStatus = cqdv.finishConstructingTidy();

        if(strReturnStatus.equals("OK")) { 
            allTheQDVs = new ArrayList();
            allTheQDVs = cqdv.getAllQDVs();
            /******************************************************
             *  The qdv_Pooled is a qdv of the pooled allTheQDVs  *
             *****************************************************/
            allTheQDVs.remove(0);   // Dump the first col (pooled)          
            dm.whereIsWaldo(431, waldoFile, " ... end doTidy()");
            return "OK";
        }
        dm.whereIsWaldo(434, waldoFile, " ... end doTidy()");
        return "Cancel";
    }
    
    private boolean validateTidyChoices() {
        dm.whereIsWaldo(439, waldoFile, " --- validateTidyChoices()???");
        theStrIsNumeric = new Boolean[TWO];        
        for (int ithCol = 0; ithCol < TWO; ithCol++){
            theStrIsNumeric[ithCol] = alCol_indep_t.get(ithCol).getStrDataType().equals("Quantitative");  
        }
        return true;
    }
        
    public Boolean getACheckBoxValue(int ithBox) {
        return cbArr_Rep_Stat_Values[ithBox];
    }

    public void setACheckBoxValue(int ithBox, boolean ithValue) {
        cbArr_Rep_Stat_Values[ithBox] = ithValue;
    }

    public DistrModel getOriginal_DistrModel() {
        return original_DistrModel; 
    }

    public DistrModel getBootstrap_DistrModel() {
        return bootstrap_DistrModel; 
    }
        
    public int getNReps() { return nRepetitions; }        
    public void setNReps(int toThis) { nRepetitions = toThis; }        
    public int getSampleSize() { return sampleSize; }        
    public void setSampleSize(int toThis) { sampleSize = toThis; }        
    public int getNCheckBoxes() { return nCheckBoxes; }        

    public double getOriginalXLower() { return original_XLower; }
    public double getOriginalXUpper() { return original_XUpper; }

    public double getAdjustedXLower() { return adjusted_XLower; }
    public double getAdjustedXUpper() { return adjusted_XUpper; }

    public String getTheChosenStatistic() { 
        dm.whereIsWaldo(476, waldoFile, " --- strChosenStatistic = " + strChosenStatistic);
        return strChosenStatistic; }

    public void setReturnStatus(String returnStatus) { 
        this.strReturnStatus = returnStatus;
    }
    
    public void setExitStatus(String strExitStatus) { 
        this.strExitStatus = strExitStatus;
    }

    public String getDescriptionOfVariable() { return strChosenVariable; }
    
    public Data_Manager getTheDataManager() { return dm; }
    public String getTidyOrTI8x() { return tidyOrTI8x; }
    public Boot_Controller getTheBoot_Controller() { return this; }    
    public ChooseStats_Dashboard getThe_Boot_Dashboard() { return chooseStats_Dashboard; }
    public DistrModel get_Boot_OriginalDistrModel() {return original_DistrModel; } 
    public DistrModel get_Boot_ShiftedDistrModel() {return bootstrap_DistrModel; }

    public ChooseStats_DialogView get_Boot_DialogView() {return chooseStats_DialogView; }    
    public void set_Boot_DialogView(ChooseStats_DialogView bootstrap_ChooseStats_DialogView) {
        this.chooseStats_DialogView = bootstrap_ChooseStats_DialogView;
    }

    public DotPlot_DistrView get_Boot_OriginalDotPlot_DistrView() {return originalDotPlot_DistrView; }        
    public void set_Boot_OriginalDotPlot_DistrView(DotPlot_DistrView boot_ChooseStats_OriginalDotPlot_DistrView) {
        this.originalDotPlot_DistrView = boot_ChooseStats_OriginalDotPlot_DistrView;
    } 

    public Histo_DistrView get_Boot_OriginalHisto_DistrView() {return originalHisto_DistrView; }        
    public void set_Boot_OriginalHisto_DistrView(Histo_DistrView boot_ChooseStats_OriginalHisto_DistrView) {
        this.originalHisto_DistrView = boot_ChooseStats_OriginalHisto_DistrView;
    }   

    public DotPlot_DistrView get_Boot_ShiftedDotPlot_DistrView() {return shiftedDotPlot_DistrView; }        
    public void set_Boot_ShiftedDotPlot_DistrView(DotPlot_DistrView boot_ChooseStats_ShiftedDotPlot_DistrView) {
        this.shiftedDotPlot_DistrView = boot_ChooseStats_ShiftedDotPlot_DistrView;
    } 

    public Histo_DistrView get_Boot_ShiftedHisto_DistrView() {return shiftedHisto_DistrView; }        
    public void set_Boot_ShiftedHisto_DistrView(Histo_DistrView boot_ChooseStats_ShiftedHisto_DistrView) {
        this.shiftedHisto_DistrView = boot_ChooseStats_ShiftedHisto_DistrView;
    }        

    public QuantitativeDataVariable getTheOriginalSample() { return qdv_oneVar_Sample; }
    public QuantitativeDataVariable getTheBootstrappedStats() { return qdv_bootstrappedStats; }

    public Boolean[] getRepAndStatCheckBoxValues() { return cbArr_Rep_Stat_Values; }        
    public String[] getRepAndStatCheckBoxDescriptions() { 
        switch (strWhichBoot) {
            case "ChooseUnivStat":
                return cbArr_One_Stat_Descriptions;
            case "ChooseTwoStat":
                return cbArr_Two_Stat_Descriptions;
            case "ChooseRegression":
                return cbArr_Two_Stat_Descriptions;
            default:
                String switchFailure = "Switch failure: Boot_Controller 534: " + nStatProcess;
                MyAlerts.showUnexpectedErrorAlert(switchFailure); 
        }
        return cbArr_One_Stat_Descriptions; //  Dummy Default!!!!!!!!!!    
    }
    
    public String getStrTitle1() { 
        dm.whereIsWaldo(509, waldoFile, " --- getStrTitle1()");
        switch (strWhichBoot) {        
            case "ChooseUnivStat":
                strTitle1 = "Statistic: " + getDescriptionOfVariable();
                dm.whereIsWaldo(513, waldoFile, " ... origDistrModel.getDescr = " + strTitle1);
                break;
            case "ChooseTwoStat":
                strTitle1 = strChosenStatistic + ": " + strDescrVarOne + " - " + strDescrVarTwo;
                dm.whereIsWaldo(517, waldoFile, " ... origDistrModel.getDescr = " + strTitle1);
                break;
            case "ChooseRegression":
               dm.whereIsWaldo(520, waldoFile, " ... strChosenStatistic = " + strChosenStatistic);
                strTitle1 = "Slope: " + strDescrVarTwo + " vs. " + strDescrVarOne;
                break;
            default:
                String switchFailure = "Switch failure: Boot_Controller 524: " + strWhichBoot;
                MyAlerts.showUnexpectedErrorAlert(switchFailure); 
                strTitle1 = "???";
        }
        return strTitle1; }
}
