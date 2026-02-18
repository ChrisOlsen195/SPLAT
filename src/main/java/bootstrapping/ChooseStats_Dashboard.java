/**************************************************
 *             Boot_ChooseStats_Dashboard         *
 *                    08/14/25                    *
 *                     15:00                      *
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
    
    final String[] regrCheckBoxDescr = { " Samp Dist -- Histo ",  " Samp Dist -- Dotplot ",
                                         " Hyp Test -- Histo ",  " Hyp Test -- Dotplot ", " Choices "};
    
    // Make empty if no-print
    //String waldoFile = "ChooseStats_Dashboard";
    String waldoFile = "";
    
    // My classes
    ChooseStats_Controller chooseStats_Controller;
    ChooseStats_DistrModel chooseStats_Original_DistrModel,
                           chooseStats_Shifted_DistrModel;
    ChooseStats_Histo_DistrView chooseStats_Original_Histo_DistrView,
                                chooseStats_Shifted_Histo_DistrView;
    ChooseStats_DotPlot_DistrView chooseStats_Original_DotPlot_DistrView,
                                  chooseStats_Shifted_DotPlot_DistrView;
    ChooseStats_DialogView chooseStats_DialogView;
    Data_Manager dm;

    // POJOs / FX
    
    Pane oneStatHisto_OriginalContainingPane, oneStatDotPlot_OriginalContainingPane, 
         oneStatHisto_ShiftedContainingPane, oneStatDotPlot_ShiftedContainingPane,   
         oneStatDialogContainingPane;
            
    public ChooseStats_Dashboard(ChooseStats_Controller boot_ChooseStats_Controller, 
                                      ChooseStats_DistrModel boot_ChooseStats_Original_DistrModel,
                                      ChooseStats_DistrModel boot_ChooseStats_Shifted_DistrModel) {
        super(5);  // nCheckBoxes = 5;
        dm = boot_ChooseStats_Controller.getTheDataManager();
        dm.whereIsWaldo(49, waldoFile, "Constructing"); 
        checkBoxDescr = new String[nCheckBoxes];
        this.chooseStats_Controller = boot_ChooseStats_Controller;
        this.chooseStats_Original_DistrModel = boot_ChooseStats_Original_DistrModel;
        this.chooseStats_Shifted_DistrModel = boot_ChooseStats_Shifted_DistrModel;

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
        dm.whereIsWaldo(81, waldoFile, "putEmAllUp()");
        if (checkBoxSettings[0] == true) {
            oneStatHisto_OriginalContainingPane.setVisible(true);
            chooseStats_Original_Histo_DistrView.doTheGraph();
        }
        else {
            oneStatHisto_OriginalContainingPane.setVisible(false);
            oneStatDialogContainingPane.setVisible(false);
        }
        
        if (checkBoxSettings[1] == true) {
            oneStatDotPlot_OriginalContainingPane.setVisible(true);
            chooseStats_Original_DotPlot_DistrView.doTheGraph();
        }
        else {
            oneStatDotPlot_OriginalContainingPane.setVisible(false);
            oneStatDialogContainingPane.setVisible(false);
        }
        
        if (checkBoxSettings[2] == true) {
            oneStatHisto_ShiftedContainingPane.setVisible(true);
            chooseStats_Shifted_Histo_DistrView.doTheGraph();
        }
        else {
            oneStatHisto_ShiftedContainingPane.setVisible(false);
            oneStatDialogContainingPane.setVisible(false);
        }
        
        if (checkBoxSettings[3] == true) {
            oneStatDotPlot_ShiftedContainingPane.setVisible(true);
            chooseStats_Shifted_DotPlot_DistrView.doTheGraph();
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
        dm.whereIsWaldo(135, waldoFile, "populateTheBackGround()");
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
        
        chooseStats_Original_Histo_DistrView = new ChooseStats_Histo_DistrView(chooseStats_Original_DistrModel, sixteenths_across[1], sixteenths_down[1], initWidth[1], initHeight[1]); 
        chooseStats_Original_DotPlot_DistrView = new ChooseStats_DotPlot_DistrView(chooseStats_Original_DistrModel, sixteenths_across[2], sixteenths_down[2], initWidth[2], initHeight[2]);
   
        chooseStats_Shifted_Histo_DistrView = new ChooseStats_Histo_DistrView(chooseStats_Shifted_DistrModel, sixteenths_across[3], sixteenths_down[3], initWidth[3], initHeight[3]); 
        chooseStats_Shifted_DotPlot_DistrView = new ChooseStats_DotPlot_DistrView(chooseStats_Shifted_DistrModel, sixteenths_across[4], sixteenths_down[4], initWidth[4], initHeight[4]);

        
        chooseStats_Controller.set_Boot_OriginalHisto_DistrView(chooseStats_Original_Histo_DistrView);
        chooseStats_Controller.set_Boot_OriginalDotPlot_DistrView(chooseStats_Original_DotPlot_DistrView);
        
        chooseStats_Controller.set_Boot_ShiftedHisto_DistrView(chooseStats_Shifted_Histo_DistrView);
        chooseStats_Controller.set_Boot_ShiftedDotPlot_DistrView(chooseStats_Shifted_DotPlot_DistrView);
        
        // Now finish the construction of the DialogView and DistrView, in that order.
        chooseStats_DialogView.continueConstruction();
        chooseStats_Original_Histo_DistrView.continueConstruction();
        chooseStats_Original_DotPlot_DistrView.continueConstruction();
        
        chooseStats_Shifted_Histo_DistrView.continueConstruction();
        chooseStats_Shifted_DotPlot_DistrView.continueConstruction();
        
        // Now cross your fingers...
        chooseStats_DialogView.completeTheDeal();
        oneStatDialogContainingPane = chooseStats_DialogView.getTheContainingPane(); 
        oneStatDialogContainingPane.setStyle(containingPaneStyle);  

        chooseStats_Original_Histo_DistrView.completeTheDeal();
        chooseStats_Original_DotPlot_DistrView.completeTheDeal();
        
        oneStatHisto_OriginalContainingPane = chooseStats_Original_Histo_DistrView.getTheContainingPane();   
        oneStatDotPlot_OriginalContainingPane = chooseStats_Original_DotPlot_DistrView.getTheContainingPane(); 
        
        chooseStats_Shifted_Histo_DistrView.completeTheDeal();
        chooseStats_Shifted_DotPlot_DistrView.completeTheDeal();
        
        oneStatHisto_ShiftedContainingPane = chooseStats_Shifted_Histo_DistrView.getTheContainingPane();   
        oneStatDotPlot_ShiftedContainingPane = chooseStats_Shifted_DotPlot_DistrView.getTheContainingPane(); 

        backGround.getChildren().addAll(oneStatHisto_OriginalContainingPane,
                                        oneStatDotPlot_OriginalContainingPane,
                                        oneStatHisto_ShiftedContainingPane,
                                        oneStatDotPlot_ShiftedContainingPane,
                                        oneStatDialogContainingPane);           
    }
    
    public ChooseStats_Controller get_Boot_Controller() { return chooseStats_Controller; }
    public ChooseStats_Histo_DistrView get_Boot_ChooseStats_OriginalHisto_DistrView() { return chooseStats_Original_Histo_DistrView; }
    public ChooseStats_DotPlot_DistrView get_Boot_ChooseStats_OriginalDotPlot_DistrView() { return chooseStats_Original_DotPlot_DistrView; }
    public ChooseStats_Histo_DistrView get_Boot_ChooseStats_ShiftedHisto_DistrView() { return chooseStats_Shifted_Histo_DistrView; }
    public ChooseStats_DotPlot_DistrView get_Boot_ChooseStats_ShiftedDotPlot_DistrView() { return chooseStats_Shifted_DotPlot_DistrView; }
    public ChooseStats_DialogView get_Boot_ChooseStats_DialogView() { return chooseStats_DialogView; }  
    public Data_Manager getDataManager() { return dm; }
}


