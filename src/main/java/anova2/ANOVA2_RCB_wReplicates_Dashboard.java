/**************************************************
 *          ANOVA2_RCB_wReplicates_Dashboard      *
 *                    05/24/24                    *
 *                     12:00                      *
 *************************************************/
/**************************************************
*    Initial widths and heights from Super Class  *
*              are 400 and 350                    *
**************************************************/
package anova2;

import superClasses.Dashboard;
import javafx.scene.paint.Color;
import javafx.scene.layout.Pane;

public class ANOVA2_RCB_wReplicates_Dashboard extends Dashboard {
    // POJOs
    final String[] anova2CheckBoxDescr = { " Box Plot ", " Circle Plot ",
                                           "MainEffectA", "MainEffectB",
                                           "Interaction ", " Print Stats "
                                         };
    
    //String waldoFile = "";
    String waldoFile = "ANOVA2_RCB_wReplicates_Dashboard";

    // My classes
    ANOVA2_RCB_Model anova2_RCB_Model;
    ANOVA2_RCB_BoxPlotView anova2_RCB_BoxPlotView; 
    ANOVA2_RCB_CirclePlotView anova2_RCB_CirclePlotView;
    ANOVA2_RCB_InteractionView anova2_RCB_InteractionView;
    ANOVA2_RCB_MainEffect_AView anova2_RCB_MainEffect_AView;
    ANOVA2_RCB_MainEffect_BView anova2_RCB_MainEffect_BView;
    ANOVA2_RCB_wReplicates_PrintReportView anova2_RCB_wReplicates_PrintReportView;

    //  FX Classes
    Pane boxPlotContainingPane, circlePlotContainingPane, 
         interactionContainingPane, printReportContainingPane,
         mainEffectAContainingPane, mainEffectBContainingPane;
            
    public ANOVA2_RCB_wReplicates_Dashboard(ANOVA2_RCB_Controller anova2_Controller, ANOVA2_RCB_Model anova2_RCB_Model) {
        super(6);
        dm = anova2_Controller.getDataManager();
        dm.whereIsWaldo(43, waldoFile, "Constructing");
        checkBoxDescr = new String[nCheckBoxes];
        
        for (int ithCheckBox = 0; ithCheckBox < nCheckBoxes; ithCheckBox++) {
            checkBoxDescr[ithCheckBox] = anova2CheckBoxDescr[ithCheckBox];
            checkBoxes[ithCheckBox].setText(checkBoxDescr[ithCheckBox]);
            checkBoxes[ithCheckBox].setId(checkBoxDescr[ithCheckBox]);
            
            if (checkBoxes[ithCheckBox].isSelected() == true) {
                checkBoxes[ithCheckBox].setTextFill(Color.GREEN);
            }
            else { checkBoxes[ithCheckBox].setTextFill(Color.RED); }
        }  
        
        setTitle("Two Way ANOVA dashboard");
        this.anova2_RCB_Model = anova2_RCB_Model;
    }  
    
    public void putEmAllUp() { 
        if (checkBoxSettings[0] == true) {
            boxPlotContainingPane.setVisible(true);
            anova2_RCB_BoxPlotView.doThePlot();
        }
        else { boxPlotContainingPane.setVisible(false); }
        
        if (checkBoxSettings[1] == true) {
            circlePlotContainingPane.setVisible(true);
            anova2_RCB_CirclePlotView.doThePlot();
        }
        else { circlePlotContainingPane.setVisible(false);  }
        
        if (checkBoxSettings[2] == true) {
            mainEffectAContainingPane.setVisible(true);
            anova2_RCB_MainEffect_AView.doThePlot();
        }
        else { mainEffectAContainingPane.setVisible(false); }
  
        if (checkBoxSettings[3] == true) {
            mainEffectBContainingPane.setVisible(true);
            anova2_RCB_MainEffect_BView.doThePlot();
        }
        else { mainEffectBContainingPane.setVisible(false);  }

        if (checkBoxSettings[4] == true) {
            interactionContainingPane.setVisible(true);
            anova2_RCB_InteractionView.doThePlot();
        }
        else { interactionContainingPane.setVisible(false); } 

       
        if (checkBoxSettings[5] == true) {
            printReportContainingPane.setVisible(true);
        }
        else { printReportContainingPane.setVisible(false); } 
    }
    
    public void populateTheBackGround() {
        anova2_RCB_BoxPlotView = new ANOVA2_RCB_BoxPlotView(anova2_RCB_Model, this, sixteenths_across[0], sixteenths_down[0], initWidth[0], initHeight[0]);
        anova2_RCB_BoxPlotView.completeTheDeal();
        boxPlotContainingPane = anova2_RCB_BoxPlotView.getTheContainingPane();
    
        anova2_RCB_CirclePlotView = new ANOVA2_RCB_CirclePlotView(anova2_RCB_Model, this, sixteenths_across[1], sixteenths_down[1], initWidth[1], initHeight[1]);
        anova2_RCB_CirclePlotView.completeTheDeal();        
        circlePlotContainingPane = anova2_RCB_CirclePlotView.getTheContainingPane();  
     
        anova2_RCB_MainEffect_AView = new ANOVA2_RCB_MainEffect_AView(anova2_RCB_Model, this, sixteenths_across[2], sixteenths_down[2], initWidth[3], initHeight[3]);
        anova2_RCB_MainEffect_AView.completeTheDeal();        
        mainEffectAContainingPane = anova2_RCB_MainEffect_AView.getTheContainingPane();  
        
        anova2_RCB_MainEffect_BView = new ANOVA2_RCB_MainEffect_BView(anova2_RCB_Model, this, sixteenths_across[3], sixteenths_down[3], initWidth[3], initHeight[3]);
        anova2_RCB_MainEffect_BView.completeTheDeal();        
        mainEffectBContainingPane = anova2_RCB_MainEffect_BView.getTheContainingPane();  


        anova2_RCB_InteractionView = new ANOVA2_RCB_InteractionView(anova2_RCB_Model, this, sixteenths_across[4], sixteenths_down[4], initWidth[4], initHeight[4]);
        anova2_RCB_InteractionView.completeTheDeal();
        interactionContainingPane = anova2_RCB_InteractionView.getTheContainingPane(); 

        initWidth[5] = 625; 
        anova2_RCB_wReplicates_PrintReportView = new ANOVA2_RCB_wReplicates_PrintReportView(anova2_RCB_Model, this, sixteenths_across[5], sixteenths_down[5], initWidth[5], initHeight[5]);
        anova2_RCB_wReplicates_PrintReportView.completeTheDeal();
        printReportContainingPane = anova2_RCB_wReplicates_PrintReportView.getTheContainingPane(); 

        backGround.getChildren().addAll(boxPlotContainingPane, 
                                        circlePlotContainingPane, 
                                        mainEffectAContainingPane,
                                        mainEffectBContainingPane,
                                        interactionContainingPane,
                                        printReportContainingPane); 
    }
}

