/**********************************************************************
 *                        ChooseStats_Controller                   *
 *                             04/17/24                               *
 *                               12:00                                *
 *********************************************************************/
package bootstrapping;

import dataObjects.ColumnOfData;
import dataObjects.QuantitativeDataVariable;
import dialogs.Splat_Dialog;
import splat.Data_Manager;
import utilityClasses.MyAlerts;

public class ChooseStats_Controller extends Splat_Dialog {
    
    int nCheckBoxes, sampleSize, nRepetitions, nStatsChecked;
    int confidenceLevel;

    Boolean[] cbArr_Rep_Stat_Values;
    
    double original_XLower, original_XUpper, original_XYRange;
    double adjusted_XLower, adjusted_XUpper;
    double[] theOriginalSample, adjustedValues;
    
    String descriptionOfVariable, theChosenStatistic;
    String[] cbArr_Rep_Stat_Descriptions = {"Mean", "Variance", "Standard Deviation", "Skew",
                                    "Trimmed Mean", "Kurtosis", "Coef. of Variation", "Minimum",
                                    "First quartile", "Median", "Third quartile", "Maximum",
                                    "Interquartile range", "Range", "Tri-Mean"};
    
    // Make empty if no-print
    String waldoFile = "ChooseStats_Controller";
    //String waldoFile = "";
    
    ChooseStats_Dialog boot_ChooseStats_Dialog;
    ChooseStats_Dashboard boot_ChooseStats_Dashboard;
    ChooseStats_DistrModel boot_Original_DistrModel;
    ChooseStats_DistrModel boot_Shifted_DistrModel;
    ChooseStats_DialogView boot_ChooseStats_DialogView;
    ChooseStats_DotPlot_DistrView boot_ChooseStats_OriginalDotPlot_DistrView;
    ChooseStats_DotPlot_DistrView boot_ChooseStats_ShiftedDotPlot_DistrView;
    ChooseStats_Histo_DistrView boot_ChooseStats_OriginalHisto_DistrView,
                                     boot_ChooseStats_ShiftedHisto_DistrView;
    Inference_Dialog boot_Inference_Dialog;
    TheChosenStat bootstrapTheStat;
    ColumnOfData colDat_TheSample, colDat_Shifted;
    Data_Manager dm;
    QuantitativeDataVariable qdv_TheOriginalSample, qdv_bootstrappedStats,
                             qdv_Shifted;
    
    public ChooseStats_Controller(Data_Manager dm, String whichBoot) {
        dm.whereIsWaldo(52, waldoFile, "Constructing");  
        this.dm = dm;
        strReturnStatus = "OK";
        setTitle("Bootstrapping");
        nCheckBoxes = cbArr_Rep_Stat_Descriptions.length;
        cbArr_Rep_Stat_Values = new Boolean[nCheckBoxes];
    }

    public String doTheControllerThing() {   
        dm.whereIsWaldo(61, waldoFile, "doTheControllerThing()"); 
        int casesInStruct = dm.getNCasesInStruct();
        
        if (casesInStruct == 0) {
            MyAlerts.showAintGotNoDataAlert();
            return "Cancel";
        }
        
        boot_ChooseStats_Dialog = new ChooseStats_Dialog(this);
        boot_ChooseStats_Dialog.showAndWait();
        strReturnStatus = boot_ChooseStats_Dialog.getReturnStatus();
        System.out.println("72 ChooseStats_Controller, strReturnStatus = " + strReturnStatus);
        if (strReturnStatus.equals("Cancel")) { return strReturnStatus;  }

        nRepetitions = boot_ChooseStats_Dialog.getNReps();
        nStatsChecked = boot_ChooseStats_Dialog.getNStatsChecked();

        if (nRepetitions == 0 ) {
            MyAlerts.showZeroReplicationsAlert();
            strReturnStatus = "Cancel";
            return strReturnStatus;
        }
        
        if (nStatsChecked == 0 ) {
            MyAlerts.showZeroStatsChosenAlert();
            strReturnStatus = "Cancel";
            return strReturnStatus;
        }
        
        if (nStatsChecked > 1 ) {
            MyAlerts.showOnlyOneStatisticAllowedAlert();
            strReturnStatus = "Cancel";
            return strReturnStatus;
        }

        boot_Inference_Dialog = new Inference_Dialog(dm, "QUANTITATIVE");
        boot_Inference_Dialog.showAndWait();
        strReturnStatus = boot_Inference_Dialog.getReturnStatus();
        
        if (strReturnStatus.equals("Cancel")) { return strReturnStatus; }
        
        descriptionOfVariable = boot_Inference_Dialog.getDescriptionOfVariable();
        confidenceLevel = boot_Inference_Dialog.getConfidenceLevel();
        colDat_TheSample = boot_Inference_Dialog.getData();
        qdv_TheOriginalSample = new QuantitativeDataVariable("null", "null", colDat_TheSample);        
        
        theOriginalSample = new double[sampleSize];
        theOriginalSample = qdv_TheOriginalSample.getTheUCDO().getTheDataSorted();

        bootstrapTheStat = new TheChosenStat(this, theOriginalSample);
        theChosenStatistic = bootstrapTheStat.getTheChosenStat();

        strReturnStatus = bootstrapTheStat.constructTheBootstrapSample();

        if (strReturnStatus.equals("Cancel")) { return strReturnStatus; }
        
        qdv_bootstrappedStats = bootstrapTheStat.getTheQDV();
        System.out.println("103 BootController, origMean = " + qdv_bootstrappedStats.getTheMean());
        original_XYRange = qdv_bootstrappedStats.getTheUCDO().getTheRange();
        original_XLower = qdv_bootstrappedStats.getMinValue() - .025 * original_XYRange;
        original_XUpper = qdv_bootstrappedStats.getMaxValue() + .025 * original_XYRange;
        System.out.println("120 ChooseStats_OrigController, XLow/Up = " + original_XLower + " / " + original_XUpper);
        boot_Original_DistrModel = new ChooseStats_DistrModel(this, qdv_bootstrappedStats);
        boot_Original_DistrModel.set_ShadeLeft(false);
        boot_Original_DistrModel.set_ShadeRight(false);
        boot_Original_DistrModel.set_LeftTail_IsChecked(false);
        boot_Original_DistrModel.set_TwoTail_IsChecked(false);
        boot_Original_DistrModel.set_RightTail_IsChecked(false);
        
        /******************************************************************
         *              Create a shifted copy for hypoth test             *
         *****************************************************************/
        // ----------------------------------------------------------------
        int nBoots = qdv_bootstrappedStats.getOriginalN();
        double thetaHat = qdv_bootstrappedStats.getTheMean();
        double thetaNull = boot_Inference_Dialog.getHypothesizedMean();
        adjustedValues = new double[nBoots];
        if (thetaNull <= thetaHat) {
            for (int ithBoot = 0; ithBoot < nBoots; ithBoot++) {
                adjustedValues[ithBoot] = qdv_bootstrappedStats.getIthDataPtAsDouble(ithBoot) - (thetaHat - thetaNull);
            } 
            //original_XLower = original_XLower - (thetaHat - thetaNull);
            adjusted_XLower = original_XLower - (thetaHat - thetaNull);
            adjusted_XUpper = original_XUpper;  /// <----------------------
            System.out.println("142 ChooseStats_OrigController, XLow/Up = " + original_XLower + " / " + original_XUpper);
            System.out.println("143 ChooseStats_AdjController, XLow/Up = " + adjusted_XLower + " / " + adjusted_XUpper);
        }
        else {
            for (int ithBoot = 0; ithBoot < nBoots; ithBoot++) {
                adjustedValues[ithBoot] = qdv_bootstrappedStats.getIthDataPtAsDouble(ithBoot) + (thetaNull - thetaHat);
            } 
            adjusted_XLower = original_XLower;  // <-------------------------
            adjusted_XUpper = thetaNull + (thetaNull - thetaHat);     
            System.out.println("151 ChooseStats_OrigController, XLow/Up = " + original_XLower + " / " + original_XUpper);
            System.out.println("152 ChooseStats_AdjController, XLow/Up = " + adjusted_XLower + " / " + adjusted_XUpper);
        }

        qdv_Shifted = new QuantitativeDataVariable("null", "null", adjustedValues); 
        boot_Shifted_DistrModel = new ChooseStats_DistrModel(this, qdv_Shifted);
        boot_Shifted_DistrModel.set_ShadeLeft(false);
        boot_Shifted_DistrModel.set_ShadeRight(false);
        boot_Shifted_DistrModel.set_LeftTail_IsChecked(false);
        boot_Shifted_DistrModel.set_TwoTail_IsChecked(false);
        boot_Shifted_DistrModel.set_RightTail_IsChecked(false);
        // ----------------------------------------------------------------

        boot_ChooseStats_Dashboard = new ChooseStats_Dashboard(this, boot_Original_DistrModel,
                                                                          boot_Shifted_DistrModel);
        boot_ChooseStats_Dashboard.populateTheBackGround();
        boot_ChooseStats_Dashboard.putEmAllUp();
        boot_ChooseStats_OriginalHisto_DistrView = boot_ChooseStats_Dashboard.get_Boot_ChooseStats_OriginalHisto_DistrView();
        boot_ChooseStats_OriginalDotPlot_DistrView = boot_ChooseStats_Dashboard.get_Boot_ChooseStats_OriginalDotPlot_DistrView();
  
        boot_ChooseStats_ShiftedHisto_DistrView = boot_ChooseStats_Dashboard.get_Boot_ChooseStats_ShiftedHisto_DistrView();
        boot_ChooseStats_ShiftedDotPlot_DistrView = boot_ChooseStats_Dashboard.get_Boot_ChooseStats_ShiftedDotPlot_DistrView();
        
        boot_ChooseStats_Dashboard.showAndWait();
        strReturnStatus = boot_ChooseStats_Dashboard.getReturnStatus();        
        return strReturnStatus;
    }
        
        public Boolean getACheckBoxValue(int ithBox) {
            return cbArr_Rep_Stat_Values[ithBox];
        }
        
        public void setACheckBoxValue(int ithBox, boolean ithValue) {
            cbArr_Rep_Stat_Values[ithBox] = ithValue;
        }
        
        public ChooseStats_DistrModel getOriginal_DistrModel() {
            return boot_Original_DistrModel; 
        }
        
        public ChooseStats_DistrModel getShifted_DistrModel() {
            return boot_Shifted_DistrModel; 
        }
        
        public int getNReps() { return nRepetitions; }        
        public void setNReps(int toThis) { nRepetitions = toThis; }        
        public int getSampleSize() { return sampleSize; }        
        public void setSampleSize(int toThis) { sampleSize = toThis; }        
        public int getNCheckBoxes() { return nCheckBoxes; }        
        public String getReturnStatus() { return strReturnStatus; }
        
        public double getOriginalXLower() { return original_XLower; }
        public double getOriginalXUpper() { return original_XUpper; }
        
        public double getAdjustedXLower() { return adjusted_XLower; }
        public double getAdjustedXUpper() { return adjusted_XUpper; }
        
        public String getTheChosenStat() { return theChosenStatistic; }
        
        public void setReturnStatus(String returnStatus) { 
            this.strReturnStatus = returnStatus;
        }
        
        public Data_Manager getTheDataManager() { return dm; }
        
        public ChooseStats_Controller getTheBoot_Controller() { return this; }    
        public ChooseStats_Dashboard getThe_Boot_Dashboard() { return boot_ChooseStats_Dashboard; }
        public ChooseStats_DistrModel get_Boot_OriginalDistrModel() {return boot_Original_DistrModel; } 
        public ChooseStats_DistrModel get_Boot_ShiftedDistrModel() {return boot_Shifted_DistrModel; }
        
        public ChooseStats_DialogView get_Boot_DialogView() {return boot_ChooseStats_DialogView; }    
        public void set_Boot_DialogView(ChooseStats_DialogView bootstrap_ChooseStats_DialogView) {
            this.boot_ChooseStats_DialogView = bootstrap_ChooseStats_DialogView;
        }
        
        public ChooseStats_DotPlot_DistrView get_Boot_OriginalDotPlot_DistrView() {return boot_ChooseStats_OriginalDotPlot_DistrView; }        
        public void set_Boot_OriginalDotPlot_DistrView(ChooseStats_DotPlot_DistrView boot_ChooseStats_OriginalDotPlot_DistrView) {
            this.boot_ChooseStats_OriginalDotPlot_DistrView = boot_ChooseStats_OriginalDotPlot_DistrView;
        } 

        public ChooseStats_Histo_DistrView get_Boot_OriginalHisto_DistrView() {return boot_ChooseStats_OriginalHisto_DistrView; }        
        public void set_Boot_OriginalHisto_DistrView(ChooseStats_Histo_DistrView boot_ChooseStats_OriginalHisto_DistrView) {
            this.boot_ChooseStats_OriginalHisto_DistrView = boot_ChooseStats_OriginalHisto_DistrView;
        }   
        
        // ------------------
        public ChooseStats_DotPlot_DistrView get_Boot_ShiftedDotPlot_DistrView() {return boot_ChooseStats_ShiftedDotPlot_DistrView; }        
        public void set_Boot_ShiftedDotPlot_DistrView(ChooseStats_DotPlot_DistrView boot_ChooseStats_ShiftedDotPlot_DistrView) {
            this.boot_ChooseStats_ShiftedDotPlot_DistrView = boot_ChooseStats_ShiftedDotPlot_DistrView;
        } 

        public ChooseStats_Histo_DistrView get_Boot_ShiftedHisto_DistrView() {return boot_ChooseStats_ShiftedHisto_DistrView; }        
        public void set_Boot_ShiftedHisto_DistrView(ChooseStats_Histo_DistrView boot_ChooseStats_ShiftedHisto_DistrView) {
            this.boot_ChooseStats_ShiftedHisto_DistrView = boot_ChooseStats_ShiftedHisto_DistrView;
        }        
        // ------------------
        
        public QuantitativeDataVariable getTheOriginalSample() { return qdv_TheOriginalSample; }
        public QuantitativeDataVariable getTheBootstrappedStats() { return qdv_bootstrappedStats; }
        
        public Boolean[] getRepAndStatCheckBoxValues() { return cbArr_Rep_Stat_Values; }        
        public String[] getRepAndStatCheckBoxDescriptions() { return cbArr_Rep_Stat_Descriptions; } 
}
