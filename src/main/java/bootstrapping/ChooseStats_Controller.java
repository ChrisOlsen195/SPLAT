/**********************************************************************
 *                       ChooseStats_Controller                       *
 *                             01/01/26                               *
 *                               00:00                                *
 *********************************************************************/
package bootstrapping;

import dataObjects.CatQuantDataVariable;
import dataObjects.ColumnOfData;
import dataObjects.QuantitativeDataVariable;
import dialogs.Splat_Dialog;
import dialogs.regression.Regr_Dialog;
import splat.Data_Manager;
import utilityClasses.MyAlerts;
import dialogs.t_and_z.Indep_t_Dialog;
import java.util.ArrayList;
import utilityClasses.MyYesNoAlerts;

public class ChooseStats_Controller extends Splat_Dialog {
    
    public Boolean goodToGo, checkForLegalChoices;
    Boolean[] cbArr_Rep_Stat_Values, theStrIsNumeric;
    
    int nCheckBoxes, sampleSize, nRepetitions, nStatsChecked, nColumnsOfData,
        TWO;
    
    double original_XLower, original_XUpper, original_XYRange;
    double adjusted_XLower, adjusted_XUpper, thetaHat, thetaNull;
    double[] arr_oneVarSample, adjustedValues;
    
    String strWhichBoot, strDescrVarOne, strDescrVarTwo, strChosenStatistic, 
            tidyOrTI8x, strReturnStatusX, strReturnStatusY, strThisVarLabel,
            strThisVarDescr, strFirstVarDescr, strSecondVarDescr, 
            strSubTitle_And, strSubTitle_Vs;
    
    String[] cbArr_Rep_Stat_Descriptions = {"Mean", "Variance", "Standard Deviation", "Skew",
                                    "Trimmed Mean", "Kurtosis", "Coef. of Variation", "Minimum",
                                    "First quartile", "Median", "Third quartile", "Maximum",
                                    "Interquartile range", "Range", "Tri-Mean"};
    
    // Make empty if no-print
    //String waldoFile = "ChooseStats_Controller";
    String waldoFile = "";
    
    public CatQuantDataVariable cqdv;
    ChooseStats_Dialog chooseStats_Dialog;
    ChooseStats_Dashboard chooseStats_Dashboard;
    ChooseStats_DistrModel original_DistrModel, shifted_DistrModel;
    ChooseStats_DialogView chooseStats_DialogView;
    ChooseStats_DotPlot_DistrView originalDotPlot_DistrView,
                                  shiftedDotPlot_DistrView;
    ChooseStats_Histo_DistrView chooseStats_OriginalHisto_DistrView,
                                chooseStats_ShiftedHisto_DistrView;
    Boot_OneVar_Dialog boot_OneVar_Dialog;
    Indep_t_Dialog indep_t_Dialog;
    BootedOneStat bootedOneStat;
    BootedTwoStat bootedTwoStat;
    BootedRegression bootedRegression;
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
    
    public ChooseStats_Controller(Data_Manager dm, String whichBoot) {
        dm.whereIsWaldo(73, waldoFile, "*** Constructing");  
        dm.whereIsWaldo(74, waldoFile, "*** Constructing " + whichBoot);
        this.dm = dm;
        this.strWhichBoot = whichBoot;
        strReturnStatus = "OK";
        TWO = 2;
        myYesNoAlerts = new MyYesNoAlerts();
        setTitle("Bootstrapping");
        nCheckBoxes = cbArr_Rep_Stat_Descriptions.length;
        cbArr_Rep_Stat_Values = new Boolean[nCheckBoxes];
        dm.whereIsWaldo(83, waldoFile, "*** End Constructing");
    }

    public String doTheControllerThing() {   
        dm.whereIsWaldo(87, waldoFile, "*** doTheControllerThing()"); 
        int casesInStruct = dm.getNCasesInStruct();
        if (casesInStruct == 0) {
            MyAlerts.showAintGotNoDataAlert();
            strReturnStatus = "Cancel";
            return "Cancel";
        }
 
        chooseStats_Dialog = new ChooseStats_Dialog(this);
        chooseStats_Dialog.showAndWait();;
        strReturnStatus = chooseStats_Dialog.getStrReturnStatus();
        if (strReturnStatus.equals("Cancel")) { return strReturnStatus;  }
        int statChosenIndex = chooseStats_Dialog.getStatCheckedIndex();
        strChosenStatistic = cbArr_Rep_Stat_Descriptions[statChosenIndex]; 
        nRepetitions = chooseStats_Dialog.getNReps();
        nStatsChecked = chooseStats_Dialog.getNStatsChecked();
        dm.whereIsWaldo(105, waldoFile, "--- doTheControllerThing(), nRepetitions = " + nRepetitions);
        dm.whereIsWaldo(106, waldoFile, "--- doTheControllerThing(), nStatsChecked = " + nStatsChecked);

        switch (strWhichBoot) {
            case "ChooseUnivStat": 
                processOneStat();
                break;
            case "ChooseTwoStat": 
                doTidyOrNot();
                processTwoStat();
                break;
            case "ChooseRegression":
                dm.whereIsWaldo(129, waldoFile, "... Regression chosen");
                processRegression();
                break;
            default:
                String switchFailure = "Switch failure: Two-Variables_Dialog 119: " + strWhichBoot;
                MyAlerts.showUnexpectedErrorAlert(switchFailure); 
                strReturnStatus = "Cancel";
        }

        if (strReturnStatus.equals("Cancel")) { return strReturnStatus; }

        original_XYRange = qdv_bootstrappedStats.getTheRange();
        original_XLower = qdv_bootstrappedStats.getMinValue() - .025 * original_XYRange;
        original_XUpper = qdv_bootstrappedStats.getMaxValue() + .025 * original_XYRange;

        original_DistrModel = new ChooseStats_DistrModel(this, qdv_bootstrappedStats);
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
        shifted_DistrModel = new ChooseStats_DistrModel(this, qdv_Shifted);
        shifted_DistrModel.set_ShadeLeft(false);
        shifted_DistrModel.set_ShadeRight(false);
        shifted_DistrModel.set_LeftTail_IsChecked(false);
        shifted_DistrModel.set_TwoTail_IsChecked(false);
        shifted_DistrModel.set_RightTail_IsChecked(false);

        chooseStats_Dashboard = new ChooseStats_Dashboard(this, original_DistrModel,
                                                                          shifted_DistrModel);
        chooseStats_Dashboard.populateTheBackGround();
        chooseStats_Dashboard.putEmAllUp();
        chooseStats_OriginalHisto_DistrView = chooseStats_Dashboard.get_Boot_ChooseStats_OriginalHisto_DistrView();
        originalDotPlot_DistrView = chooseStats_Dashboard.get_Boot_ChooseStats_OriginalDotPlot_DistrView();
  
        chooseStats_ShiftedHisto_DistrView = chooseStats_Dashboard.get_Boot_ChooseStats_ShiftedHisto_DistrView();
        shiftedDotPlot_DistrView = chooseStats_Dashboard.get_Boot_ChooseStats_ShiftedDotPlot_DistrView();
        
        chooseStats_Dashboard.showAndWait();
        strReturnStatus = chooseStats_Dashboard.getStrReturnStatus();  
        return strReturnStatus;
    }
    
    private String processOneStat() {
        dm.whereIsWaldo(182, waldoFile, "*** processOneStat()"); 
        boot_OneVar_Dialog = new Boot_OneVar_Dialog(dm, "QUANTITATIVE");
        boot_OneVar_Dialog.showAndWait();
        strReturnStatus = boot_OneVar_Dialog.getStrReturnStatus();
        
        if (strReturnStatus.equals("Cancel")) { return strReturnStatus; }
        
        strDescrVarOne = boot_OneVar_Dialog.getDescriptionOfVariable();
        alCol_OneVar_Sample = boot_OneVar_Dialog.getData();
        qdv_oneVar_Sample = new QuantitativeDataVariable("null", "null", alCol_OneVar_Sample);        
        
        arr_oneVarSample = new double[sampleSize];
        arr_oneVarSample = qdv_oneVar_Sample.getTheUCDO().getTheDataSorted();

        bootedOneStat = new BootedOneStat(this, arr_oneVarSample);
        strChosenStatistic = bootedOneStat.getTheChosenStat();
        strReturnStatus = bootedOneStat.constructTheBootstrapSample();

        if (strReturnStatus.equals("Cancel")) { return strReturnStatus; }
        qdv_bootstrappedStats = bootedOneStat.getTheQDV();
        thetaHat = qdv_bootstrappedStats.getTheMean();
        thetaNull = boot_OneVar_Dialog.getHypothesizedMean();
        dm.whereIsWaldo(204, waldoFile, "--- end processOneStat()"); 
        return "OK";
    }
    
    private String processTwoStat() {
        dm.whereIsWaldo(209, waldoFile, "*** processTwoStat()"); 

        if (strReturnStatus.equals("Cancel")) { return strReturnStatus; }

        strDescrVarOne = indep_t_Dialog.getPreferredFirstVarDescription();
        strDescrVarTwo = indep_t_Dialog.getPreferredSecondVarDescription();
        alCol_TwoVar_Sample = indep_t_Dialog.getData();

        bootedTwoStat = new BootedTwoStat(this, alCol_TwoVar_Sample);
        strChosenStatistic = bootedTwoStat.getTheChosenStat();
        strReturnStatus = bootedTwoStat.constructTheBootstrapSample();

        if (strReturnStatus.equals("Cancel")) { return strReturnStatus; }
        qdv_bootstrappedStats = bootedTwoStat.getTheQDV();
        thetaHat = qdv_bootstrappedStats.getTheMean();
        thetaNull = indep_t_Dialog.getHypothesizedDiffInMeans();
        dm.whereIsWaldo(225, waldoFile, "thetaHat = " + thetaHat); 
        dm.whereIsWaldo(226, waldoFile, "thetaNull = " + thetaNull); 
        dm.whereIsWaldo(227, waldoFile, "--- end processTwoStat()");
        return "OK";        
    }
    
    private String processRegression() {
        dm.whereIsWaldo(232, waldoFile, " *** processRegression()");
        tidyOrTI8x = "TI8x";
        dm.setTIorTIDY("TI8x");
        boot_Regr_Dialog = new Regr_Dialog(dm, "varType", "Bootstrap Regression Slope");
        boot_Regr_Dialog.showAndWait();
        strReturnStatus = boot_Regr_Dialog.getStrReturnStatus();

        if (strReturnStatus.equals("Cancel")) { return strReturnStatus; }
        dm.whereIsWaldo(240, waldoFile, " ... processRegression()");
        strDescrVarOne = boot_Regr_Dialog.getFirstVarLabel_InFile();
        strDescrVarTwo = boot_Regr_Dialog.getFirstVarLabel_InFile();
        System.out.println("243 ChooseStats_Controller, Lab X/Y = " + strDescrVarOne + " / " + strDescrVarTwo);
        alCol_Regr = boot_Regr_Dialog.getData();
        dm.whereIsWaldo(245, waldoFile, " ... processRegression(), toStrings");
        //alCol_Regr.get(0).toString();
        //alCol_Regr.get(1).toString();
        bootedRegression = new BootedRegression(this, alCol_Regr);
        bootedRegression.constructTheBootstrapSample();
        dm.whereIsWaldo(250, waldoFile, " ... create qdv_bootstrappedStats");
        qdv_bootstrappedStats = bootedRegression.getTheQDV();

        thetaHat = qdv_bootstrappedStats.getTheMean();
        System.out.println("254 ChooseStats_Controller, thetaHat = " + thetaHat);
        thetaNull = 0.0;
        //qdv_bootstrappedStats.toString();
        dm.whereIsWaldo(257, waldoFile, " --- processRegression()");
        return "OK";
    }
    
    public String doTidyOrNot() {
        dm.whereIsWaldo(262, waldoFile, " *** doTidyOrNot()");      
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
            dm.setTIorTIDY("Tidy");
            strReturnStatus = doTidy(); 

            if (!strReturnStatusX.equals("OK") || !strReturnStatusX.equals("OK")) {
                strReturnStatus = "Cancel"; 
                return strReturnStatus;
            }
        } else {    // No = TI8x
            tidyOrTI8x = "TI8x";
            dm.setTIorTIDY("TI8x");
            strReturnStatus =  doTI8x();
            if (strReturnStatus.equals("Cancel")) { return "Cancel"; }
            if (!strReturnStatusX.equals("OK") || !strReturnStatusX.equals("OK")) {strReturnStatus = "Cancel"; }
        }  
        
        //thetaHat = qdv_bootstrappedStats.getTheMean();
        //thetaNull = indep_t_Dialog.getHypothesizedDiffInMeans();
        dm.whereIsWaldo(295, waldoFile, " --- end doTidyOrNot()");
        return strReturnStatus;
    }
        
    protected String doTI8x() {
        dm.whereIsWaldo(311, waldoFile, " *** doTI8x()");
        goodToGo = true;
        int casesInStruct = dm.getNCasesInStruct();
        
        if (casesInStruct == 0) {
            MyAlerts.showAintGotNoDataAlert();
            strReturnStatus = "Cancel";
            return strReturnStatus;
        }

        indep_t_Dialog = new Indep_t_Dialog(dm, "Indep_t_ti8x");
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

        strThisVarLabel = "All";
        strThisVarDescr = "All";
        
        alCol_TwoVariables = new ArrayList<>();
        alCol_TwoVariables = indep_t_Dialog.getData();
        
        dm.whereIsWaldo(333, waldoFile, " --- end doTI8x()");
        return strReturnStatus;
    }
        
    protected String doTidy() {
        dm.whereIsWaldo(338, waldoFile, " *** doTidy()");
        strReturnStatus = "OK";
        strReturnStatusX = "OK";
        strReturnStatusX = "OK";
        do {
            int casesInStruct = dm.getNCasesInStruct();            
            if (casesInStruct == 0) {
                MyAlerts.showAintGotNoDataAlert();
                return "Cancel";
            }
            indep_t_Dialog = new Indep_t_Dialog(dm, "Indep_t_tidy");
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
            dm.whereIsWaldo(372, waldoFile, " --- doTidy(), strReturnStatus = " + strReturnStatus);
            checkForLegalChoices = validateTidyChoices();
        } while (!checkForLegalChoices);

        //                                Categorical,             Quantitative            return All and individuals
        dm.whereIsWaldo(377, waldoFile, " --- doTidy(), strReturnStatus = " + strReturnStatus);
        cqdv = new CatQuantDataVariable(dm, alCol_indep_t.get(0), alCol_indep_t.get(1), true, "ANOVA1_Cat_Controller"); 
        dm.whereIsWaldo(379, waldoFile, " --- doTidy(), strReturnStatus = " + strReturnStatus);
        strReturnStatus = cqdv.finishConstructingTidy();

        if(strReturnStatus.equals("OK")) { 
            allTheQDVs = new ArrayList();
            allTheQDVs = cqdv.getAllQDVs();
            /******************************************************
             *  The qdv_Pooled is a qdv of the pooled allTheQDVs  *
             *****************************************************/
            allTheQDVs.remove(0);   // Dump the first col (pooled)

            strFirstVarDescr = allTheQDVs.get(0).getTheVarLabel();
            strSecondVarDescr = allTheQDVs.get(1).getTheVarLabel();
            strSubTitle_And = strFirstVarDescr  + " & " + strSecondVarDescr;  
            strSubTitle_Vs = strFirstVarDescr  + " vs " + strSecondVarDescr;            
            dm.whereIsWaldo(394, waldoFile, " --- end doTidy()");
            return "OK";
        }
        dm.whereIsWaldo(397, waldoFile, " --- end doTidy()");
        return "Cancel";
    }
    
    private boolean validateTidyChoices() {
        dm.whereIsWaldo(402, waldoFile, " *** validateTidyChoices()???");
        theStrIsNumeric = new Boolean[TWO];        
        for (int ithCol = 0; ithCol < TWO; ithCol++){
            theStrIsNumeric[ithCol] = alCol_indep_t.get(ithCol).getDataType().equals("Quantitative");  
        }
        return true;
    }
        
    public Boolean getACheckBoxValue(int ithBox) {
        return cbArr_Rep_Stat_Values[ithBox];
    }

    public void setACheckBoxValue(int ithBox, boolean ithValue) {
        cbArr_Rep_Stat_Values[ithBox] = ithValue;
    }

    public ChooseStats_DistrModel getOriginal_DistrModel() {
        return original_DistrModel; 
    }

    public ChooseStats_DistrModel getShifted_DistrModel() {
        return shifted_DistrModel; 
    }
        
    public int getNReps() { return nRepetitions; }        
    public void setNReps(int toThis) { nRepetitions = toThis; }        
    public int getSampleSize() { return sampleSize; }        
    public void setSampleSize(int toThis) { sampleSize = toThis; }        
    public int getNCheckBoxes() { return nCheckBoxes; }        
    //public String getReturnStatus() { return strReturnStatus; }

    public double getOriginalXLower() { return original_XLower; }
    public double getOriginalXUpper() { return original_XUpper; }

    public double getAdjustedXLower() { return adjusted_XLower; }
    public double getAdjustedXUpper() { return adjusted_XUpper; }

    public String getTheChosenStat() { return strChosenStatistic; }

    public void setReturnStatus(String returnStatus) { 
        this.strReturnStatus = returnStatus;
    }

    public String getDescriptionOfVariable() {
        if (strDescrVarOne.isEmpty()) { 
            strDescrVarOne = strChosenStatistic;
        }
        return strDescrVarOne;
    }

    public Data_Manager getTheDataManager() { return dm; }
    public String getTidyOrTI8x() { return tidyOrTI8x; }
    public ChooseStats_Controller getTheBoot_Controller() { return this; }    
    public ChooseStats_Dashboard getThe_Boot_Dashboard() { return chooseStats_Dashboard; }
    public ChooseStats_DistrModel get_Boot_OriginalDistrModel() {return original_DistrModel; } 
    public ChooseStats_DistrModel get_Boot_ShiftedDistrModel() {return shifted_DistrModel; }

    public ChooseStats_DialogView get_Boot_DialogView() {return chooseStats_DialogView; }    
    public void set_Boot_DialogView(ChooseStats_DialogView bootstrap_ChooseStats_DialogView) {
        this.chooseStats_DialogView = bootstrap_ChooseStats_DialogView;
    }

    public ChooseStats_DotPlot_DistrView get_Boot_OriginalDotPlot_DistrView() {return originalDotPlot_DistrView; }        
    public void set_Boot_OriginalDotPlot_DistrView(ChooseStats_DotPlot_DistrView boot_ChooseStats_OriginalDotPlot_DistrView) {
        this.originalDotPlot_DistrView = boot_ChooseStats_OriginalDotPlot_DistrView;
    } 

    public ChooseStats_Histo_DistrView get_Boot_OriginalHisto_DistrView() {return chooseStats_OriginalHisto_DistrView; }        
    public void set_Boot_OriginalHisto_DistrView(ChooseStats_Histo_DistrView boot_ChooseStats_OriginalHisto_DistrView) {
        this.chooseStats_OriginalHisto_DistrView = boot_ChooseStats_OriginalHisto_DistrView;
    }   

    public ChooseStats_DotPlot_DistrView get_Boot_ShiftedDotPlot_DistrView() {return shiftedDotPlot_DistrView; }        
    public void set_Boot_ShiftedDotPlot_DistrView(ChooseStats_DotPlot_DistrView boot_ChooseStats_ShiftedDotPlot_DistrView) {
        this.shiftedDotPlot_DistrView = boot_ChooseStats_ShiftedDotPlot_DistrView;
    } 

    public ChooseStats_Histo_DistrView get_Boot_ShiftedHisto_DistrView() {return chooseStats_ShiftedHisto_DistrView; }        
    public void set_Boot_ShiftedHisto_DistrView(ChooseStats_Histo_DistrView boot_ChooseStats_ShiftedHisto_DistrView) {
        this.chooseStats_ShiftedHisto_DistrView = boot_ChooseStats_ShiftedHisto_DistrView;
    }        

    public QuantitativeDataVariable getTheOriginalSample() { return qdv_oneVar_Sample; }
    public QuantitativeDataVariable getTheBootstrappedStats() { return qdv_bootstrappedStats; }

    public Boolean[] getRepAndStatCheckBoxValues() { return cbArr_Rep_Stat_Values; }        
    public String[] getRepAndStatCheckBoxDescriptions() { return cbArr_Rep_Stat_Descriptions; } 
}
