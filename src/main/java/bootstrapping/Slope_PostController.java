/**********************************************************************
 *                       Slope_PostController                         *
 *                             01/08/25                               *
 *                               15:00                                *
 *********************************************************************/
package bootstrapping;

import dataObjects.ColumnOfData;
import dataObjects.QuantitativeDataVariable;
import dialogs.Splat_Dialog;
import utilityClasses.MyAlerts;

public class Slope_PostController extends Splat_Dialog {
    
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    int sampleSize, nReplications;

    double original_XLower, original_XUpper, original_XRange;
    double adjusted_XLower, adjusted_XUpper;
    double[] adjustedValues, theBooteds;

    Bootstrap_Dashboard bootStrap_Dashboard;
    Boot_DistrModel original_DistrModel,
                    shifted_DistrModel;
    BootedStat_DialogView bootedStat_DialogView;
    Boot_DotPlot_DistrView boot_OriginalDotPlot_DistrView,
                           boot_ShiftedDotPlot_DistrView;
    Boot_Histo_DistrView boot_OriginalHisto_DistrView,
                         boot_ShiftedHisto_DistrView;
    Inference_Dialog inference_Dialog;
    ColumnOfData col_TheOriginals, col_Shifteds;
    NonGenericBootstrap_Info nonGen;
    QuantitativeDataVariable qdv_Originals, qdv_Shifteds;
    Slope_Controller slope_Controller;
    
    public Slope_PostController(NonGenericBootstrap_Info nonGen,
                                   Slope_Controller slope_Controller, 
                                   String varDescr, 
                                   double[] theBooteds) { 
        if (printTheStuff) {
            System.out.println("44 *** Slope_PostController, constructing");
        }
        this.slope_Controller = slope_Controller;
        this.nonGen = nonGen;
        nReplications = nonGen.getNReplications();
        this.theBooteds = new double[nReplications];
        System.arraycopy(theBooteds, 0, this.theBooteds, 0, nReplications);
        strReturnStatus = "OK";
        setTitle("Bootstrapping && Randomization");
    }

    public String doThePostControllerThing() {   
        if (printTheStuff) {
            System.out.println("57 --- Slope_PostController, doThePostControllerThing()");
        } 

        if (nReplications == 0 ) {
            MyAlerts.showZeroReplicationsAlert();
            strReturnStatus = "Cancel";
            return strReturnStatus;
        }
        
        qdv_Originals = new QuantitativeDataVariable("VarLabel",
                                                      "VarDescr",
                                                      theBooteds);
        original_XRange = qdv_Originals.getTheUCDO().getTheRange();
        
        original_XLower = qdv_Originals.getMinValue() - .025 * original_XRange;
        original_XUpper = qdv_Originals.getMaxValue() + .025 * original_XRange;
        
        nonGen.setOriginalXLower(original_XLower);
        nonGen.setOriginalXUpper(original_XUpper);        
        
        original_DistrModel = new Boot_DistrModel(nonGen, qdv_Originals);
        original_DistrModel.set_ShadeLeft(false);
        original_DistrModel.set_ShadeRight(false);
        original_DistrModel.set_LeftTail_IsChecked(false);
        original_DistrModel.set_TwoTail_IsChecked(false);
        original_DistrModel.set_RightTail_IsChecked(false);
        nonGen.setOriginalDistrModel(original_DistrModel);
        
        /******************************************************************
         *              Create a shifted copy for hypoth test             *
         *****************************************************************/
        int nBoots = qdv_Originals.getOriginalN();
        double thetaHat = qdv_Originals.getTheMean();
        double thetaNull = slope_Controller.getInd_T_HypothesizedDiff();
        adjustedValues = new double[nBoots];
        if (thetaNull <= thetaHat) {
            for (int ithBoot = 0; ithBoot < nBoots; ithBoot++) {
                adjustedValues[ithBoot] = qdv_Originals.getIthDataPtAsDouble(ithBoot) - (thetaHat - thetaNull);
            } 

            adjusted_XLower = original_XLower - (thetaHat - thetaNull);
            adjusted_XUpper = original_XUpper;  /// <----------------------
        }
        else {
            for (int ithBoot = 0; ithBoot < nBoots; ithBoot++) {
                adjustedValues[ithBoot] = qdv_Originals.getIthDataPtAsDouble(ithBoot) + (thetaNull - thetaHat);
            } 
            adjusted_XLower = original_XLower;  // <-------------------------
            adjusted_XUpper = thetaNull + (thetaNull - thetaHat);     
        }
        
        nonGen.setAdjustedXLower(adjusted_XLower);
        nonGen.setAdjustedXUpper(adjusted_XUpper);
        
        qdv_Shifteds = new QuantitativeDataVariable("null", "null", adjustedValues); 
        shifted_DistrModel = new Boot_DistrModel(nonGen, qdv_Shifteds);
        shifted_DistrModel.set_ShadeLeft(false);
        shifted_DistrModel.set_ShadeRight(false);
        shifted_DistrModel.set_LeftTail_IsChecked(false);
        shifted_DistrModel.set_TwoTail_IsChecked(false);
        shifted_DistrModel.set_RightTail_IsChecked(false);
        nonGen.setShiftedDistrModel(shifted_DistrModel);

        bootStrap_Dashboard = new Bootstrap_Dashboard(nonGen, 
                                                      original_DistrModel,
                                                      shifted_DistrModel);
        bootStrap_Dashboard.populateTheBackGround();
        bootStrap_Dashboard.putEmAllUp();
        boot_OriginalHisto_DistrView = bootStrap_Dashboard.get_Boot_OriginalHisto_DistrView();
        boot_OriginalDotPlot_DistrView = bootStrap_Dashboard.get_Boot_OriginalDotPlot_DistrView();
        
        boot_ShiftedHisto_DistrView = bootStrap_Dashboard.get_Boot_ShiftedHisto_DistrView();
        boot_ShiftedDotPlot_DistrView = bootStrap_Dashboard.get_Boot_ShiftedDotPlot_DistrView();
        
        bootStrap_Dashboard.showAndWait();
        strReturnStatus = bootStrap_Dashboard.getReturnStatus();   
        return strReturnStatus;
    }
          
        public Boot_DistrModel getOriginal_DistrModel() {
            return original_DistrModel; 
        }
        
        public Boot_DistrModel getShifted_DistrModel() {
            return shifted_DistrModel; 
        }
                
        public int getSampleSize() { return sampleSize; }        
        public void setSampleSize(int toThis) { sampleSize = toThis; }   
        
        public String getReturnStatus() { return strReturnStatus; }
        
        public double getOriginalXLower() { return original_XLower; }
        public double getOriginalXUpper() { return original_XUpper; }
        
        public double getAdjustedXLower() { return adjusted_XLower; }
        public double getAdjustedXUpper() { return adjusted_XUpper; }

        public void setReturnStatus(String returnStatus) { 
            this.strReturnStatus = returnStatus;
        }
 
        public Slope_PostController getTheBoot_Controller() { return this; }    
        public Bootstrap_Dashboard getThe_Boot_Dashboard() { return bootStrap_Dashboard; }
        public Boot_DistrModel get_Boot_OriginalDistrModel() {return original_DistrModel; } 
        public Boot_DistrModel get_Boot_ShiftedDistrModel() {return shifted_DistrModel; }
        
        public BootedStat_DialogView get_Boot_DialogView() {return bootedStat_DialogView; }    
        public void set_Boot_DialogView(BootedStat_DialogView bootstrap_ChooseStats_DialogView) {
            this.bootedStat_DialogView = bootstrap_ChooseStats_DialogView;
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
        
        public QuantitativeDataVariable getTheOriginalSample() { return qdv_Originals; }
        public QuantitativeDataVariable getTheBootstrappedStats() { return qdv_Originals; }
}
