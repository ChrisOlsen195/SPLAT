/**********************************************************************
 *                        ChooseStats_Controller                   *
 *                             02/24/25                               *
 *                               09:00                                *
 *********************************************************************/
package bootstrapping;

import dataObjects.ColumnOfData;
import dataObjects.QuantitativeDataVariable;
import dialogs.Splat_Dialog;
import splat.Data_Manager;
import utilityClasses.MyAlerts;

public class ChooseStats_Controller extends Splat_Dialog {
    
    int nCheckBoxes, sampleSize, nRepetitions, nStatsChecked;
    //int confidenceLevel;

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
    //String waldoFile = "ChooseStats_Controller";
    String waldoFile = "";
    
    ChooseStats_Dialog chooseStats_Dialog;
    ChooseStats_Dashboard chooseStats_Dashboard;
    ChooseStats_DistrModel original_DistrModel, shifted_DistrModel;
    ChooseStats_DialogView chooseStats_DialogView;
    ChooseStats_DotPlot_DistrView originalDotPlot_DistrView,
                                  shiftedDotPlot_DistrView;
    ChooseStats_Histo_DistrView chooseStats_OriginalHisto_DistrView,
                                chooseStats_ShiftedHisto_DistrView;
    Inference_Dialog inference_Dialog;
    TheChosenStat theChosenStat;
    ColumnOfData colDat_TheSample, colDat_Shifted;
    Data_Manager dm;
    QuantitativeDataVariable qdv_TheOriginalSample, qdv_bootstrappedStats,
                             qdv_Shifted;
    
    public ChooseStats_Controller(Data_Manager dm, String whichBoot) {
        dm.whereIsWaldo(51, waldoFile, "Constructing");  
        this.dm = dm;
        strReturnStatus = "OK";
        setTitle("Bootstrapping");
        nCheckBoxes = cbArr_Rep_Stat_Descriptions.length;
        cbArr_Rep_Stat_Values = new Boolean[nCheckBoxes];
    }

    public String doTheControllerThing() {   
        dm.whereIsWaldo(60, waldoFile, "doTheControllerThing()"); 
        int casesInStruct = dm.getNCasesInStruct();
        
        if (casesInStruct == 0) {
            MyAlerts.showAintGotNoDataAlert();
            return "Cancel";
        }
        
        chooseStats_Dialog = new ChooseStats_Dialog(this);
        chooseStats_Dialog.showAndWait();
        strReturnStatus = chooseStats_Dialog.getReturnStatus();
        if (strReturnStatus.equals("Cancel")) { return strReturnStatus;  }
        int statChosenIndex = chooseStats_Dialog.getStatCheckedIndex();
        theChosenStatistic = cbArr_Rep_Stat_Descriptions[statChosenIndex]; 
        nRepetitions = chooseStats_Dialog.getNReps();
        nStatsChecked = chooseStats_Dialog.getNStatsChecked();

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

        inference_Dialog = new Inference_Dialog(dm, "QUANTITATIVE");
        inference_Dialog.showAndWait();
        strReturnStatus = inference_Dialog.getReturnStatus();
        
        if (strReturnStatus.equals("Cancel")) { return strReturnStatus; }
        
        descriptionOfVariable = inference_Dialog.getDescriptionOfVariable();
        //confidenceLevel = boot_Inference_Dialog.getConfidenceLevel();
        colDat_TheSample = inference_Dialog.getData();
        qdv_TheOriginalSample = new QuantitativeDataVariable("null", "null", colDat_TheSample);        
        
        theOriginalSample = new double[sampleSize];
        theOriginalSample = qdv_TheOriginalSample.getTheUCDO().getTheDataSorted();

        theChosenStat = new TheChosenStat(this, theOriginalSample);
        theChosenStatistic = theChosenStat.getTheChosenStat();
        strReturnStatus = theChosenStat.constructTheBootstrapSample();

        if (strReturnStatus.equals("Cancel")) { return strReturnStatus; }
        
        qdv_bootstrappedStats = theChosenStat.getTheQDV();
        original_XYRange = qdv_bootstrappedStats.getTheUCDO().getTheRange();
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
        double thetaHat = qdv_bootstrappedStats.getTheMean();
        double thetaNull = inference_Dialog.getHypothesizedMean();
        adjustedValues = new double[nBoots];
        if (thetaNull <= thetaHat) {
            for (int ithBoot = 0; ithBoot < nBoots; ithBoot++) {
                adjustedValues[ithBoot] = qdv_bootstrappedStats.getIthDataPtAsDouble(ithBoot) - (thetaHat - thetaNull);
            } 

            adjusted_XLower = original_XLower - (thetaHat - thetaNull);
            adjusted_XUpper = original_XUpper;  /// <----------------------
        }
        else {
            for (int ithBoot = 0; ithBoot < nBoots; ithBoot++) {
                adjustedValues[ithBoot] = qdv_bootstrappedStats.getIthDataPtAsDouble(ithBoot) + (thetaNull - thetaHat);
            } 
            adjusted_XLower = original_XLower;  // <-------------------------
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
        strReturnStatus = chooseStats_Dashboard.getReturnStatus();        
        return strReturnStatus;
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
        public String getReturnStatus() { return strReturnStatus; }
        
        public double getOriginalXLower() { return original_XLower; }
        public double getOriginalXUpper() { return original_XUpper; }
        
        public double getAdjustedXLower() { return adjusted_XLower; }
        public double getAdjustedXUpper() { return adjusted_XUpper; }
        
        public String getTheChosenStat() { return theChosenStatistic; }
        
        public void setReturnStatus(String returnStatus) { 
            this.strReturnStatus = returnStatus;
        }
        
        public String getDescriptionOfVariable() {
            if (descriptionOfVariable.isEmpty()) { 
                descriptionOfVariable = theChosenStatistic;
            }
            return descriptionOfVariable;
        }
        
        public Data_Manager getTheDataManager() { return dm; }
        
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
        
        public QuantitativeDataVariable getTheOriginalSample() { return qdv_TheOriginalSample; }
        public QuantitativeDataVariable getTheBootstrappedStats() { return qdv_bootstrappedStats; }
        
        public Boolean[] getRepAndStatCheckBoxValues() { return cbArr_Rep_Stat_Values; }        
        public String[] getRepAndStatCheckBoxDescriptions() { return cbArr_Rep_Stat_Descriptions; } 
}
