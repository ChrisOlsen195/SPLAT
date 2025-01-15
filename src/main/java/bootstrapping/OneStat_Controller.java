/**********************************************************************
 *                        OneStat_Controller                          *
 *                             01/08/25                               *
 *                               15:00                                *
 *********************************************************************/
package bootstrapping;

import dataObjects.ColumnOfData;
import dataObjects.QuantitativeDataVariable;
import dialogs.Splat_Dialog;
import splat.Data_Manager;
import utilityClasses.MyAlerts;

public class OneStat_Controller extends Splat_Dialog {
    
    int nCheckBoxes, sampleSize, nReplications, nStatsChecked;
    int confidenceLevel;

    Boolean[] cbArr_Rep_Stat_Values;
    
    double original_XLower, original_XUpper, original_XRange;
    double adjusted_XLower, adjusted_XUpper;
    double[] theOriginalSample, adjustedValues;
    
    String descriptionOfVariable, theChosenStatistic;
    String[] cbArr_Rep_Stat_Descriptions = {"Mean", "Variance", "Standard Deviation", "Skew",
                                    "Trimmed Mean", "Kurtosis", "Coef. of Variation", "Minimum",
                                    "First quartile", "Median", "Third quartile", "Maximum",
                                    "Interquartile range", "Range", "Tri-Mean"};
    
    // Make empty if no-print
    //String waldoFile = "OneStat_Controller";
    String waldoFile = "";
    
    ChooseOneStat_Dialog chooseOneStat_Dialog;
    Bootstrap_Dashboard bootstrap_Dashboard;
    Boot_DistrModel original_DistrModel,
                    shifted_DistrModel;
    BootedStat_DialogView bootedStat_DialogView;
    Boot_DotPlot_DistrView boot_OriginalDotPlot_DistrView,
                           boot_ShiftedDotPlot_DistrView;
    Boot_Histo_DistrView boot_OriginalHisto_DistrView,
                         boot_ShiftedHisto_DistrView;
    Inference_Dialog inference_Dialog;
    ChosenOneStat chosenOneStat;
    NonGenericBootstrap_Info nonGen;
    ColumnOfData colDat_TheSample, colDat_Shifted;
    Data_Manager dm;
    QuantitativeDataVariable qdv_TheOriginalSample, qdv_bootstrappedStats,
                             qdv_Shifted;
    
    public OneStat_Controller(Data_Manager dm, String whichBoot) {
        dm.whereIsWaldo(53, waldoFile, " *** Constructing");  
        this.dm = dm;
        nonGen = new NonGenericBootstrap_Info("UnivStats");
        strReturnStatus = "OK";
        setTitle("Bootstrapping");
        nCheckBoxes = cbArr_Rep_Stat_Descriptions.length;
        cbArr_Rep_Stat_Values = new Boolean[nCheckBoxes];
    }

    public String doTheControllerThing() {   
        dm.whereIsWaldo(63, waldoFile, "doTheControllerThing()"); 
        int casesInStruct = dm.getNCasesInStruct();
        
        if (casesInStruct == 0) {
            MyAlerts.showAintGotNoDataAlert_1Var();
            return "Cancel";
        }
        
        chooseOneStat_Dialog = new ChooseOneStat_Dialog(this);
        chooseOneStat_Dialog.showAndWait();
        strReturnStatus = chooseOneStat_Dialog.getReturnStatus();
        dm.whereIsWaldo(74, waldoFile, "doTheControllerThing()");
        if (strReturnStatus.equals("Cancel")) { return strReturnStatus;  }

        nReplications = chooseOneStat_Dialog.getNReps();
        nonGen.setNReplications(nReplications);
        nStatsChecked = chooseOneStat_Dialog.getNStatsChecked();

        if (nReplications == 0 ) {
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
        confidenceLevel = inference_Dialog.getConfidenceLevel();
        colDat_TheSample = inference_Dialog.getData();
        qdv_TheOriginalSample = new QuantitativeDataVariable("null", "null", colDat_TheSample);        
        
        theOriginalSample = new double[sampleSize];
        theOriginalSample = qdv_TheOriginalSample.getTheUCDO().getTheDataSorted();

        chosenOneStat = new ChosenOneStat(this, theOriginalSample);
        theChosenStatistic = chosenOneStat.getTheChosenStat();

        strReturnStatus = chosenOneStat.constructTheBootstrapSample();

        if (strReturnStatus.equals("Cancel")) { return strReturnStatus; }
        
        qdv_bootstrappedStats = chosenOneStat.getTheQDV();
        dm.whereIsWaldo(121, waldoFile, "doTheControllerThing()");
        original_XRange = qdv_bootstrappedStats.getTheUCDO().getTheRange();
        
        original_XLower = qdv_bootstrappedStats.getMinValue() - .025 * original_XRange;
        original_XUpper = qdv_bootstrappedStats.getMaxValue() + .025 * original_XRange;
        
        nonGen.setOriginalXLower(original_XLower);
        nonGen.setOriginalXUpper(original_XUpper);
        
        original_DistrModel = new Boot_DistrModel(nonGen, qdv_bootstrappedStats);
        original_DistrModel.set_ShadeLeft(false);
        original_DistrModel.set_ShadeRight(false);
        original_DistrModel.set_LeftTail_IsChecked(false);
        original_DistrModel.set_TwoTail_IsChecked(false);
        original_DistrModel.set_RightTail_IsChecked(false);
        nonGen.setOriginalDistrModel(original_DistrModel);
        /******************************************************************
         *              Create a shifted copy for hypoth test             *
         *****************************************************************/
        dm.whereIsWaldo(140, waldoFile, "doTheControllerThing()");
        int nBoots = qdv_bootstrappedStats.getOriginalN();
        double thetaHat = qdv_bootstrappedStats.getTheMean();
        double thetaNull = inference_Dialog.getHypothesizedMean();
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
        
        nonGen.setAdjustedXLower(adjusted_XLower);
        nonGen.setAdjustedXUpper(adjusted_XUpper);

        qdv_Shifted = new QuantitativeDataVariable("null", "null", adjustedValues); 
        shifted_DistrModel = new Boot_DistrModel(nonGen, qdv_Shifted);
        shifted_DistrModel.set_ShadeLeft(false);
        shifted_DistrModel.set_ShadeRight(false);
        shifted_DistrModel.set_LeftTail_IsChecked(false);
        shifted_DistrModel.set_TwoTail_IsChecked(false);
        shifted_DistrModel.set_RightTail_IsChecked(false);
        nonGen.setShiftedDistrModel(shifted_DistrModel);
        bootstrap_Dashboard = new Bootstrap_Dashboard(nonGen, 
                                                      original_DistrModel,
                                                      shifted_DistrModel);
        bootstrap_Dashboard.populateTheBackGround();
        bootstrap_Dashboard.putEmAllUp();
        boot_OriginalHisto_DistrView = bootstrap_Dashboard.get_Boot_OriginalHisto_DistrView();
        boot_OriginalDotPlot_DistrView = bootstrap_Dashboard.get_Boot_OriginalDotPlot_DistrView();
  
        boot_ShiftedHisto_DistrView = bootstrap_Dashboard.get_Boot_ShiftedHisto_DistrView();
        boot_ShiftedDotPlot_DistrView = bootstrap_Dashboard.get_Boot_ShiftedDotPlot_DistrView();
        
        bootstrap_Dashboard.showAndWait();
        strReturnStatus = bootstrap_Dashboard.getReturnStatus();        
        return strReturnStatus;
    }
        
    public Boolean getACheckBoxValue(int ithBox) {
        return cbArr_Rep_Stat_Values[ithBox];
    }

    public void setACheckBoxValue(int ithBox, boolean ithValue) {
        cbArr_Rep_Stat_Values[ithBox] = ithValue;
    }

    public Boot_DistrModel getOriginal_DistrModel() {
        return original_DistrModel; 
    }

    public Boot_DistrModel getShifted_DistrModel() {
        return shifted_DistrModel; 
    }
     
    public int getNReps() { return nReplications; }        
    public void setNReps(int toThis) { nReplications = toThis; }        
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

    public OneStat_Controller getTheBoot_Controller() { return this; }    
    public Bootstrap_Dashboard getThe_Boot_Dashboard() { return bootstrap_Dashboard; }
    public Boot_DistrModel get_Boot_OriginalDistrModel() {return original_DistrModel; } 
    public Boot_DistrModel get_Boot_ShiftedDistrModel() {return shifted_DistrModel; }

    public BootedStat_DialogView get_Boot_DialogView() {return bootedStat_DialogView; }    
    public void set_Boot_DialogView(BootedStat_DialogView bootedStat_DialogView) {
        this.bootedStat_DialogView = bootedStat_DialogView;
    }

    public Boot_DotPlot_DistrView get_Boot_OriginalDotPlot_DistrView() {return boot_OriginalDotPlot_DistrView; }        
    public void set_Boot_OriginalDotPlot_DistrView(Boot_DotPlot_DistrView boot_ChooseStats_OriginalDotPlot_DistrView) {
        this.boot_OriginalDotPlot_DistrView = boot_ChooseStats_OriginalDotPlot_DistrView;
    } 

    public Boot_Histo_DistrView get_Boot_OriginalHisto_DistrView() {return boot_OriginalHisto_DistrView; }        
    public void set_Boot_OriginalHisto_DistrView(Boot_Histo_DistrView boot_ChooseStats_OriginalHisto_DistrView) {
        this.boot_OriginalHisto_DistrView = boot_ChooseStats_OriginalHisto_DistrView;
    }   

    public Boot_DotPlot_DistrView get_Boot_ShiftedDotPlot_DistrView() {return boot_ShiftedDotPlot_DistrView; }        
    public void set_Boot_ShiftedDotPlot_DistrView(Boot_DotPlot_DistrView boot_ChooseStats_ShiftedDotPlot_DistrView) {
        this.boot_ShiftedDotPlot_DistrView = boot_ChooseStats_ShiftedDotPlot_DistrView;
    } 

    public Boot_Histo_DistrView get_Boot_ShiftedHisto_DistrView() {return boot_ShiftedHisto_DistrView; }        
    public void set_Boot_ShiftedHisto_DistrView(Boot_Histo_DistrView boot_ChooseStats_ShiftedHisto_DistrView) {
        this.boot_ShiftedHisto_DistrView = boot_ChooseStats_ShiftedHisto_DistrView;
    }        

    public QuantitativeDataVariable getTheOriginalSample() { return qdv_TheOriginalSample; }
    public QuantitativeDataVariable getTheBootstrappedStats() { return qdv_bootstrappedStats; }

    public Boolean[] getRepAndStatCheckBoxValues() { return cbArr_Rep_Stat_Values; }        
    public String[] getRepAndStatCheckBoxDescriptions() { return cbArr_Rep_Stat_Descriptions; } 
        
    @Override
    public String toString() {  
        System.out.println("\n  OneStat_Controller -- toString()");
        System.out.println("dm.toString...");
        dm.toString();
        System.out.println("OneStat_Controller -- end toString\n");
        return "OneStat_Controller.toString() -- end";
    } 
}
