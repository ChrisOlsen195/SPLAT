/**************************************************
 *               Bootstrap_Dashboard              *
 *                    01/08/25                    *
 *                     15:00                      *
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

public class Bootstrap_Dashboard extends Dashboard {
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
        
    String strBootedStat;
    final String[] regrCheckBoxDescr = { " Bootstrap -- Histo ",  " Bootstrap -- Dotplot ",
                                         " Randomization -- Histo ",  " Randomization -- Dotplot ", " Choices "};
    
    // My classes
    OneStat_Controller boot_Controller;
    Boot_DistrModel original_DistrModel, shifted_DistrModel;
    Boot_Histo_DistrView original_Histo_DistrView,
                         shifted_Histo_DistrView;
    Boot_DotPlot_DistrView original_DotPlot_DistrView,
                           shifted_DotPlot_DistrView;
    BootedStat_DialogView bootedStat_DialogView;
    NonGenericBootstrap_Info nonGen;
    QuantitativeDataVariable qdv_TheOriginalSample, qdv_bootstrappedStat;
   
    // POJOs / FX
    Pane histo_OriginalContainingPane, dotPlot_OriginalContainingPane, 
         histo_ShiftedContainingPane, dotPlot_ShiftedContainingPane,   
         bootedStatContainingPane;
            
    public Bootstrap_Dashboard(NonGenericBootstrap_Info nonGen, 
                                      Boot_DistrModel original_DistrModel,
                                      Boot_DistrModel shifted_DistrModel) {
        super(5);  // nCheckBoxes = 5;
        nonGen.setTheDashboard(this);
        if (printTheStuff) {
            System.out.println("\n48 *** Bootstrap_Dashboard, Constructing");
        }
        checkBoxDescr = new String[nCheckBoxes];
        this.original_DistrModel = original_DistrModel;
        this.shifted_DistrModel = shifted_DistrModel;
        this.nonGen = nonGen;
        strBootedStat = nonGen.getTheBootingStat();
        qdv_TheOriginalSample = nonGen.getTheOriginalSample();
        qdv_bootstrappedStat = nonGen.getTheBootStrappedSample();
        
        doSomeInitializations();
    } 
    
    private void doSomeInitializations() {
        if (printTheStuff) {
            System.out.println("63 --- Bootstrap_Dashboard, doSomeInitializations()");
        } 
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
        
        setTitle("Bootstrapping && Randomization dashboard"); 
        
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
        if (printTheStuff) {
            System.out.println("93 --- Bootstrap_Dashboard, putEmAllUp()");
        } 
        if (checkBoxSettings[0] == true) {
            histo_OriginalContainingPane.setVisible(true);
            original_Histo_DistrView.doTheGraph();
        }
        else {
            histo_OriginalContainingPane.setVisible(false);
            bootedStatContainingPane.setVisible(false);
        }
        
        if (checkBoxSettings[1] == true) {
            dotPlot_OriginalContainingPane.setVisible(true);
            original_DotPlot_DistrView.doTheGraph();
        }
        else {
            dotPlot_OriginalContainingPane.setVisible(false);
            bootedStatContainingPane.setVisible(false);
        }
        
        if (checkBoxSettings[2] == true) {
            histo_ShiftedContainingPane.setVisible(true);
            shifted_Histo_DistrView.doTheGraph();
        }
        else {
            histo_ShiftedContainingPane.setVisible(false);
            bootedStatContainingPane.setVisible(false);
        }
        
        if (checkBoxSettings[3] == true) {
            dotPlot_ShiftedContainingPane.setVisible(true);
            shifted_DotPlot_DistrView.doTheGraph();
        }
        else {
            dotPlot_ShiftedContainingPane.setVisible(false);
            bootedStatContainingPane.setVisible(false);
        }
        
        if (checkBoxSettings[4] == true) {
            bootedStatContainingPane.setVisible(true);
            bootedStat_DialogView.doTheGraph();
        }
        else {
            bootedStatContainingPane.setVisible(false);
        }
    }
    
    // *****************************************************************
    // *  IMPORTANT NOTE:  The DialogViews must be constructed before  *
    // * the DistrViews b/c they need access to the Dialogs during the *
    // * construction of the DistrViews.                               *
    // *****************************************************************
    
    public void populateTheBackGround() {
        if (printTheStuff) {
            System.out.println("148 --- Bootstrap_Dashboard, populateTheBackGround()");
        } 
        // First, construct the DialogView and DistrView, in that order.
        initWidth[0] = 450; initHeight[0] = 300;
        sixteenths_across[0] = 1000; sixteenths_down[0] = 100; 
        bootedStat_DialogView = new BootedStat_DialogView(nonGen, this, sixteenths_across[0], sixteenths_down[0], initWidth[0], initHeight[0]);
        
        initWidth[1] = 600; initHeight[1] = 375;
        sixteenths_across[1] = 100; sixteenths_down[1] = 100;
        
        initWidth[2] = 600; initHeight[2] = 375;
        sixteenths_across[2] = 300; sixteenths_down[2] = 100;
        
        initWidth[3] = 600; initHeight[3] = 375;
        sixteenths_across[3] = 500; sixteenths_down[3] = 100;
        
        initWidth[4] = 600; initHeight[4] = 375;
        sixteenths_across[2] = 700; sixteenths_down[2] = 100;

        nonGen.setTheTitles("Bootstrap Distribution", strBootedStat);        
        original_Histo_DistrView = new Boot_Histo_DistrView(nonGen, original_DistrModel, sixteenths_across[1], sixteenths_down[1], initWidth[1], initHeight[1]);
        nonGen.setTheTitles("Bootstrap Distribution", strBootedStat);
        original_DotPlot_DistrView = new Boot_DotPlot_DistrView(nonGen, original_DistrModel, sixteenths_across[2], sixteenths_down[2], initWidth[2], initHeight[2]);

        nonGen.setTheTitles("Randomization Distribution", strBootedStat);        
        shifted_Histo_DistrView = new Boot_Histo_DistrView(nonGen, shifted_DistrModel, sixteenths_across[3], sixteenths_down[3], initWidth[3], initHeight[3]); 
        nonGen.setTheTitles("Randomization Distribution", strBootedStat);
        shifted_DotPlot_DistrView = new Boot_DotPlot_DistrView(nonGen, shifted_DistrModel, sixteenths_across[4], sixteenths_down[4], initWidth[4], initHeight[4]);

        
        nonGen.set_Original_Histo_DistrView(original_Histo_DistrView);
        nonGen.set_Original_DotPlot_DistrView(original_DotPlot_DistrView);
        
        nonGen.set_Shifted_Histo_DistrView(shifted_Histo_DistrView);
        nonGen.set_Shifted_DotPlot_DistrView(shifted_DotPlot_DistrView);
        
        // Now finish the construction of the DialogView and DistrView, in that order.
        bootedStat_DialogView.continueConstruction();
        original_Histo_DistrView.continueConstruction();
        original_DotPlot_DistrView.continueConstruction();
        
        shifted_Histo_DistrView.continueConstruction();
        shifted_DotPlot_DistrView.continueConstruction();
        
        // Now cross your fingers...
        bootedStat_DialogView.completeTheDeal();
        bootedStatContainingPane = bootedStat_DialogView.getTheContainingPane(); 
        bootedStatContainingPane.setStyle(containingPaneStyle);  

        original_Histo_DistrView.completeTheDeal();
        original_DotPlot_DistrView.completeTheDeal();
        
        histo_OriginalContainingPane = original_Histo_DistrView.getTheContainingPane();   
        dotPlot_OriginalContainingPane = original_DotPlot_DistrView.getTheContainingPane(); 
        
        shifted_Histo_DistrView.completeTheDeal();
        shifted_DotPlot_DistrView.completeTheDeal();
        
        histo_ShiftedContainingPane = shifted_Histo_DistrView.getTheContainingPane();   
        dotPlot_ShiftedContainingPane = shifted_DotPlot_DistrView.getTheContainingPane(); 

        backGround.getChildren().addAll(histo_OriginalContainingPane,
                                        dotPlot_OriginalContainingPane,
                                        histo_ShiftedContainingPane,
                                        dotPlot_ShiftedContainingPane,
                                        bootedStatContainingPane); 
    }
    
    public OneStat_Controller get_Boot_Controller() { return boot_Controller; }
    public Boot_Histo_DistrView get_Boot_OriginalHisto_DistrView() { return original_Histo_DistrView; }
    public Boot_DotPlot_DistrView get_Boot_OriginalDotPlot_DistrView() { return original_DotPlot_DistrView; }
    public Boot_Histo_DistrView get_Boot_ShiftedHisto_DistrView() { return shifted_Histo_DistrView; }
    public Boot_DotPlot_DistrView get_Boot_ShiftedDotPlot_DistrView() { return shifted_DotPlot_DistrView; }
    public BootedStat_DialogView get_Boot_DialogView() { return bootedStat_DialogView; }  
}


