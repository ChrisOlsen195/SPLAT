/**************************************************
 *              ChooseStats_Dashboard             *
 *                    08/09/26                    *
 *                     21:00                      *
 *************************************************/
/**************************************************
*    Initial widths and heights from Super Class  *
*              are 400 and 350                    *
**************************************************/
package bootstrapping;

import superClasses.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import splat.Data_Manager;

public class ChooseStats_Dashboard extends Dashboard {
    // POJOs
    
    final String[] regrCheckBoxDescr = { " Sampling Distribution -- Histo ",  " Sampling Distribution -- Dotplot ",
                                         " Hypothesis Test -- Histo ",  " Hypothesis Test -- Dotplot ", " Choices "};
    
    // Make empty if no-print
    //String waldoFile = "ChooseStats_Dashboard";
    String waldoFile = "";
    
    // My classes
    Boot_Controller chooseStats_Controller;
    DistrModel original_DistrModel;
    DistrModel shifted_DistrModel;
    Histo_DistrView original_Histo_DistrView, shifted_Histo_DistrView;
    DotPlot_DistrView original_DotPlot_DistrView, shifted_DotPlot_DistrView;
    ChooseStats_DialogView chooseStats_DialogView;
    Data_Manager dm;

    // POJOs / FX
    
    Pane oneStatHisto_OriginalContainingPane, oneStatDotPlot_OriginalContainingPane, 
         oneStatHisto_ShiftedContainingPane, oneStatDotPlot_ShiftedContainingPane,   
         oneStatDialogContainingPane;
            
    public ChooseStats_Dashboard(Boot_Controller boot_ChooseStats_Controller, 
                                      DistrModel original_DistrModel,
                                      DistrModel shifted_DistrModel) {
        super(5);  // nCheckBoxes = 5;
        dm = boot_ChooseStats_Controller.getTheDataManager();
        dm.whereIsWaldo(47, waldoFile, " *** Constructing"); 
        checkBoxDescr = new String[nCheckBoxes];
        this.chooseStats_Controller = boot_ChooseStats_Controller;
        this.original_DistrModel = original_DistrModel;
        this.shifted_DistrModel = shifted_DistrModel;

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
        dm.whereIsWaldo(79, waldoFile, " --- putEmAllUp()");
        if (checkBoxSettings[0] == true) {
            oneStatHisto_OriginalContainingPane.setVisible(true);
            original_Histo_DistrView.doTheGraph();
        }
        else {
            oneStatHisto_OriginalContainingPane.setVisible(false);
            oneStatDialogContainingPane.setVisible(false);
        }
        
        if (checkBoxSettings[1] == true) {
            oneStatDotPlot_OriginalContainingPane.setVisible(true);
            original_DotPlot_DistrView.doTheGraph();
        }
        else {
            oneStatDotPlot_OriginalContainingPane.setVisible(false);
            oneStatDialogContainingPane.setVisible(false);
        }
        
        if (checkBoxSettings[2] == true) {
            oneStatHisto_ShiftedContainingPane.setVisible(true);
            shifted_Histo_DistrView.doTheGraph();
        }
        else {
            oneStatHisto_ShiftedContainingPane.setVisible(false);
            oneStatDialogContainingPane.setVisible(false);
        }
        
        if (checkBoxSettings[3] == true) {
            oneStatDotPlot_ShiftedContainingPane.setVisible(true);
            shifted_DotPlot_DistrView.doTheGraph();
        }
        else {
            oneStatDotPlot_ShiftedContainingPane.setVisible(false);
            oneStatDialogContainingPane.setVisible(false);
        }
        
        if (checkBoxSettings[4] == true) {
            oneStatDialogContainingPane.setVisible(true);
            chooseStats_DialogView.doTheGraph();
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
        dm.whereIsWaldo(133, waldoFile, " --- populateTheBackGround()");
        // First, construct the DialogView and DistrView, in that order.
        
        initWidth[0] = 450; initHeight[0] = 300;
        sixteenths_across[0] = 1000; sixteenths_down[0] = 100; 
        chooseStats_DialogView = new ChooseStats_DialogView(this, sixteenths_across[0], sixteenths_down[0], initWidth[0], initHeight[0]);
        
        initWidth[1] = 600; initHeight[1] = 375;
        sixteenths_across[1] = 100; sixteenths_down[1] = 100;
        
        initWidth[2] = 600; initHeight[2] = 375;
        sixteenths_across[2] = 300; sixteenths_down[2] = 100;
        
        initWidth[3] = 600; initHeight[3] = 375;
        sixteenths_across[3] = 500; sixteenths_down[3] = 100;
        
        initWidth[4] = 600; initHeight[4] = 375;
        sixteenths_across[2] = 700; sixteenths_down[2] = 100;
        
        original_Histo_DistrView = new Histo_DistrView(original_DistrModel, sixteenths_across[1], sixteenths_down[1], initWidth[1], initHeight[1]); 
        original_Histo_DistrView.setStrTitle2(regrCheckBoxDescr[0]);
        original_DotPlot_DistrView = new DotPlot_DistrView(original_DistrModel, sixteenths_across[2], sixteenths_down[2], initWidth[2], initHeight[2]);
        original_DotPlot_DistrView.setStrTitle2(regrCheckBoxDescr[1]);
        shifted_Histo_DistrView = new Histo_DistrView(shifted_DistrModel, sixteenths_across[3], sixteenths_down[3], initWidth[3], initHeight[3]); 
        shifted_Histo_DistrView.setStrTitle2(regrCheckBoxDescr[2]);
        shifted_DotPlot_DistrView = new DotPlot_DistrView(shifted_DistrModel, sixteenths_across[4], sixteenths_down[4], initWidth[4], initHeight[4]);
        shifted_DotPlot_DistrView.setStrTitle2(regrCheckBoxDescr[3]);
        
        chooseStats_Controller.set_Boot_OriginalHisto_DistrView(original_Histo_DistrView);
        chooseStats_Controller.set_Boot_OriginalDotPlot_DistrView(original_DotPlot_DistrView);
        
        chooseStats_Controller.set_Boot_ShiftedHisto_DistrView(shifted_Histo_DistrView);
        chooseStats_Controller.set_Boot_ShiftedDotPlot_DistrView(shifted_DotPlot_DistrView);
        
        // Now finish the construction of the DialogView and DistrView, in that order.
        chooseStats_DialogView.continueConstruction();
        original_Histo_DistrView.continueConstruction();
        original_DotPlot_DistrView.continueConstruction();
        
        shifted_Histo_DistrView.continueConstruction();
        shifted_DotPlot_DistrView.continueConstruction();
        
        // Now cross your fingers...
        chooseStats_DialogView.completeTheDeal();
        oneStatDialogContainingPane = chooseStats_DialogView.getTheContainingPane(); 
        oneStatDialogContainingPane.setStyle(containingPaneStyle);  

        original_Histo_DistrView.completeTheDeal();
        original_DotPlot_DistrView.completeTheDeal();
        
        oneStatHisto_OriginalContainingPane = original_Histo_DistrView.getTheContainingPane();   
        oneStatDotPlot_OriginalContainingPane = original_DotPlot_DistrView.getTheContainingPane(); 
        
        shifted_Histo_DistrView.completeTheDeal();
        shifted_DotPlot_DistrView.completeTheDeal();
        
        oneStatHisto_ShiftedContainingPane = shifted_Histo_DistrView.getTheContainingPane();   
        oneStatDotPlot_ShiftedContainingPane = shifted_DotPlot_DistrView.getTheContainingPane(); 

        backGround.getChildren().addAll(oneStatHisto_OriginalContainingPane,
                                        oneStatDotPlot_OriginalContainingPane,
                                        oneStatHisto_ShiftedContainingPane,
                                        oneStatDotPlot_ShiftedContainingPane,
                                        oneStatDialogContainingPane);           
    }
    
    public Boot_Controller get_Boot_Controller() { return chooseStats_Controller; }
    public Histo_DistrView get_Boot_ChooseStats_OriginalHisto_DistrView() { return original_Histo_DistrView; }
    public DotPlot_DistrView get_Boot_ChooseStats_OriginalDotPlot_DistrView() { return original_DotPlot_DistrView; }
    public Histo_DistrView get_Boot_ChooseStats_ShiftedHisto_DistrView() { return shifted_Histo_DistrView; }
    public DotPlot_DistrView get_Boot_ChooseStats_ShiftedDotPlot_DistrView() { return shifted_DotPlot_DistrView; }
    public ChooseStats_DialogView get_Boot_ChooseStats_DialogView() { return chooseStats_DialogView; }  
    //public Data_Manager getDataManager() { return dm; }
}


