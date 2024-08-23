/**************************************************
 *             Boot_ChooseStats_Dashboard         *
 *                    04/26/24                    *
 *                     12:00                      *
 *************************************************/
/**************************************************
*    Initial widths and heights from Super Class  *
*              are 400 and 350                    *
**************************************************/
package bootstrapping;

import dataObjects.QuantitativeDataVariable;
import superClasses.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import splat.Data_Manager;

public class ChooseStats_Dashboard extends Dashboard {
    // POJOs
    
    final String[] regrCheckBoxDescr = { " Samp Dist -- Histo ",  " Samp Dist -- Dotplot ",
                                         " Hyp Test -- Histo ",  " Hyp Test -- Dotplot ", " Choices "};
    
    // Make empty if no-print
    //String waldoFile = "ChooseStats_Dashboard";
    String waldoFile = "";
    
    // My classes
    ChooseStats_Controller boot_Controller;
    ChooseStats_DistrModel boot_ChooseStats_Original_DistrModel;
    ChooseStats_DistrModel boot_ChooseStats_Shifted_DistrModel;
    ChooseStats_Histo_DistrView boot_ChooseStats_Original_Histo_DistrView,
                                     boot_ChooseStats_Shifted_Histo_DistrView;
    ChooseStats_DotPlot_DistrView boot_ChooseStats_Original_DotPlot_DistrView;
    ChooseStats_DotPlot_DistrView boot_ChooseStats_Shifted_DotPlot_DistrView;
    ChooseStats_DialogView boot_ChooseStats_DialogView;
    Data_Manager dm;
    
    QuantitativeDataVariable qdv_TheOriginalSample, qdv_bootstrappedStat;
   
    // POJOs / FX
    
    Pane oneStatHisto_OriginalContainingPane, oneStatDotPlot_OriginalContainingPane, 
         oneStatHisto_ShiftedContainingPane, oneStatDotPlot_ShiftedContainingPane,   
         oneStatDialogContainingPane;
            
    public ChooseStats_Dashboard(ChooseStats_Controller boot_ChooseStats_Controller, 
                                      ChooseStats_DistrModel boot_ChooseStats_Original_DistrModel,
                                      ChooseStats_DistrModel boot_ChooseStats_Shifted_DistrModel) {
        super(5);  // nCheckBoxes = 5;
        dm = boot_ChooseStats_Controller.getTheDataManager();
        dm.whereIsWaldo(44, waldoFile, "Constructing"); 
        checkBoxDescr = new String[nCheckBoxes];
        this.boot_Controller = boot_ChooseStats_Controller;
        this.boot_ChooseStats_Original_DistrModel = boot_ChooseStats_Original_DistrModel;
        this.boot_ChooseStats_Shifted_DistrModel = boot_ChooseStats_Shifted_DistrModel;

        qdv_TheOriginalSample = boot_ChooseStats_Controller.getTheOriginalSample();
        qdv_bootstrappedStat = boot_ChooseStats_Controller.getTheBootstrappedStats();
        
        for (int ithCheckBox = 0; ithCheckBox < nCheckBoxes; ithCheckBox++) {
            checkBoxDescr[ithCheckBox] = regrCheckBoxDescr[ithCheckBox];
            checkBoxes[ithCheckBox].setText(checkBoxDescr[ithCheckBox]);
            checkBoxes[ithCheckBox].setId(checkBoxDescr[ithCheckBox]);
            
            if (checkBoxes[ithCheckBox].isSelected() == true) {
                checkBoxes[ithCheckBox].setTextFill(Color.GREEN);
            }
            else {
                checkBoxes[ithCheckBox].setTextFill(Color.RED);
            }
        }
        setTitle("Bootstrapping One mean dashboard"); 
        
        /******************************************************************
         *    Re-use of these arrays from usual Dashboard!!!              *
         *****************************************************************/
         initWidth = new double[10];
         initHeight = new double[10];
         sixteenths_across = new double[10]; 
         sixteenths_down = new double[10];  
         initWidth = new double[10]; 
         initHeight = new double[10]; 
    }  
    
    public void putEmAllUp() { 
        dm.whereIsWaldo(78, waldoFile, "putEmAllUp()");
        if (checkBoxSettings[0] == true) {
            oneStatHisto_OriginalContainingPane.setVisible(true);
            boot_ChooseStats_Original_Histo_DistrView.doTheGraph();
        }
        else {
            oneStatHisto_OriginalContainingPane.setVisible(false);
            oneStatDialogContainingPane.setVisible(false);
        }
        
        if (checkBoxSettings[1] == true) {
            oneStatDotPlot_OriginalContainingPane.setVisible(true);
            boot_ChooseStats_Original_DotPlot_DistrView.doTheGraph();
        }
        else {
            oneStatDotPlot_OriginalContainingPane.setVisible(false);
            oneStatDialogContainingPane.setVisible(false);
        }
        
        if (checkBoxSettings[2] == true) {
            oneStatHisto_ShiftedContainingPane.setVisible(true);
            boot_ChooseStats_Shifted_Histo_DistrView.doTheGraph();
        }
        else {
            oneStatHisto_ShiftedContainingPane.setVisible(false);
            oneStatDialogContainingPane.setVisible(false);
        }
        
        if (checkBoxSettings[3] == true) {
            oneStatDotPlot_ShiftedContainingPane.setVisible(true);
            boot_ChooseStats_Shifted_DotPlot_DistrView.doTheGraph();
        }
        else {
            oneStatDotPlot_ShiftedContainingPane.setVisible(false);
            oneStatDialogContainingPane.setVisible(false);
        }
        
        if (checkBoxSettings[4] == true) {
            oneStatDialogContainingPane.setVisible(true);
            boot_ChooseStats_DialogView.doTheGraph();
        }
        else {
            oneStatDialogContainingPane.setVisible(false);
            oneStatDialogContainingPane.setVisible(false);
        }
    }
    
    // *****************************************************************
    // *  IMPORTANT NOTE:  The DialogViews must be constructed before  *
    // * the DistrViews b/c they need access to the Dialogs during the *
    // * construction of the DistrViews.                               *
    // *****************************************************************
    
    public void populateTheBackGround() {
        dm.whereIsWaldo(114, waldoFile, "populateTheBackGround()");
        // First, construct the DialogView and DistrView, in that order.
        
        initWidth[0] = 450; initHeight[0] = 300;
        sixteenths_across[0] = 1000; sixteenths_down[0] = 100; 
        boot_ChooseStats_DialogView = new ChooseStats_DialogView(this, sixteenths_across[0], sixteenths_down[0], initWidth[0], initHeight[0]);
        
        initWidth[1] = 600; initHeight[1] = 375;
        sixteenths_across[1] = 100; sixteenths_down[1] = 100;
        
        initWidth[2] = 600; initHeight[2] = 375;
        sixteenths_across[2] = 300; sixteenths_down[2] = 100;
        
        initWidth[3] = 600; initHeight[3] = 375;
        sixteenths_across[3] = 500; sixteenths_down[3] = 100;
        
        initWidth[4] = 600; initHeight[4] = 375;
        sixteenths_across[2] = 700; sixteenths_down[2] = 100;
        
        boot_ChooseStats_Original_Histo_DistrView = new ChooseStats_Histo_DistrView(boot_ChooseStats_Original_DistrModel, sixteenths_across[1], sixteenths_down[1], initWidth[1], initHeight[1]); 
        boot_ChooseStats_Original_DotPlot_DistrView = new ChooseStats_DotPlot_DistrView(boot_ChooseStats_Original_DistrModel, sixteenths_across[2], sixteenths_down[2], initWidth[2], initHeight[2]);
   
        boot_ChooseStats_Shifted_Histo_DistrView = new ChooseStats_Histo_DistrView(boot_ChooseStats_Shifted_DistrModel, sixteenths_across[3], sixteenths_down[3], initWidth[3], initHeight[3]); 
        boot_ChooseStats_Shifted_DotPlot_DistrView = new ChooseStats_DotPlot_DistrView(boot_ChooseStats_Shifted_DistrModel, sixteenths_across[4], sixteenths_down[4], initWidth[4], initHeight[4]);

        
        boot_Controller.set_Boot_OriginalHisto_DistrView(boot_ChooseStats_Original_Histo_DistrView);
        boot_Controller.set_Boot_OriginalDotPlot_DistrView(boot_ChooseStats_Original_DotPlot_DistrView);
        
        boot_Controller.set_Boot_ShiftedHisto_DistrView(boot_ChooseStats_Shifted_Histo_DistrView);
        boot_Controller.set_Boot_ShiftedDotPlot_DistrView(boot_ChooseStats_Shifted_DotPlot_DistrView);
        
        // Now finish the construction of the DialogView and DistrView, in that order.
        boot_ChooseStats_DialogView.continueConstruction();
        boot_ChooseStats_Original_Histo_DistrView.continueConstruction();
        boot_ChooseStats_Original_DotPlot_DistrView.continueConstruction();
        
        boot_ChooseStats_Shifted_Histo_DistrView.continueConstruction();
        boot_ChooseStats_Shifted_DotPlot_DistrView.continueConstruction();
        
        // Now cross your fingers...
        boot_ChooseStats_DialogView.completeTheDeal();
        oneStatDialogContainingPane = boot_ChooseStats_DialogView.getTheContainingPane(); 
        oneStatDialogContainingPane.setStyle(containingPaneStyle);  

        boot_ChooseStats_Original_Histo_DistrView.completeTheDeal();
        boot_ChooseStats_Original_DotPlot_DistrView.completeTheDeal();
        
        oneStatHisto_OriginalContainingPane = boot_ChooseStats_Original_Histo_DistrView.getTheContainingPane();   
        oneStatDotPlot_OriginalContainingPane = boot_ChooseStats_Original_DotPlot_DistrView.getTheContainingPane(); 
        
        boot_ChooseStats_Shifted_Histo_DistrView.completeTheDeal();
        boot_ChooseStats_Shifted_DotPlot_DistrView.completeTheDeal();
        
        oneStatHisto_ShiftedContainingPane = boot_ChooseStats_Shifted_Histo_DistrView.getTheContainingPane();   
        oneStatDotPlot_ShiftedContainingPane = boot_ChooseStats_Shifted_DotPlot_DistrView.getTheContainingPane(); 

        backGround.getChildren().addAll(oneStatHisto_OriginalContainingPane,
                                        oneStatDotPlot_OriginalContainingPane,
                                        oneStatHisto_ShiftedContainingPane,
                                        oneStatDotPlot_ShiftedContainingPane,
                                        oneStatDialogContainingPane);           
    }
    
    public ChooseStats_Controller get_Boot_Controller() { return boot_Controller; }
    public ChooseStats_Histo_DistrView get_Boot_ChooseStats_OriginalHisto_DistrView() { return boot_ChooseStats_Original_Histo_DistrView; }
    public ChooseStats_DotPlot_DistrView get_Boot_ChooseStats_OriginalDotPlot_DistrView() { return boot_ChooseStats_Original_DotPlot_DistrView; }
    public ChooseStats_Histo_DistrView get_Boot_ChooseStats_ShiftedHisto_DistrView() { return boot_ChooseStats_Shifted_Histo_DistrView; }
    public ChooseStats_DotPlot_DistrView get_Boot_ChooseStats_ShiftedDotPlot_DistrView() { return boot_ChooseStats_Shifted_DotPlot_DistrView; }
    public ChooseStats_DialogView get_Boot_ChooseStats_DialogView() { return boot_ChooseStats_DialogView; }  
    public Data_Manager getDataManager() { return dm; }
}


