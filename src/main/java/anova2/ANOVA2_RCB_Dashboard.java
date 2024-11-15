/**************************************************
 *              ANOVA2_RCB_Dashboard              *
 *                    1012/24                    *
 *                     18:00                      *
 *************************************************/
/**************************************************
*    Initial widths and heights from Super Class  *
*              are 400 and 350                    *
**************************************************/
package anova2;

import superClasses.Dashboard;
import javafx.scene.paint.Color;
import javafx.scene.layout.Pane;

public class ANOVA2_RCB_Dashboard extends Dashboard {
    // POJOs
    final String[] anova2CheckBoxDescr = { " Box Plot ", " Circle Plot ",
                                           "MainEffectA", "MainEffectB",
                                           "Interaction ", " Print Stats "
                                         };
    //String waldoFile = "";
    String waldoFile = "ANOVA2_RCB_Dashboard";  
    
    // My classes
    ANOVA2_Factorial_Model anova2_Factorial_Model;     
    ANOVA2_BoxPlotView boxPlotView; 
    ANOVA2_CirclePlotView circlePlotView;
    ANOVA2_InteractionView interactionView;
    ANOVA2_MainEffect_AView mainEffect_AView;
    ANOVA2_MainEffect_BView mainEffect_BView;
    ANOVA2_PrintReportView printReportView;

    //  POJOs / FX
    Pane boxPlotContainingPane, circlePlotContainingPane,
         interactionContainingPane, printReportContainingPane,
         mainEffectAContainingPane, mainEffectBContainingPane;
            
    public ANOVA2_RCB_Dashboard(ANOVA2_RCB_Controller anova2_Controller, ANOVA2_Factorial_Model anova2_Factorial_Model) {
        super(6);
        dm = anova2_Controller.getDataManager();
        dm.whereIsWaldo(42, waldoFile, "Constructing");
        checkBoxDescr = new String[nCheckBoxes];
        setTitle("Two Way ANOVA dashboard");
        this.anova2_Factorial_Model = anova2_Factorial_Model;
        
        for (int ithCheckBox = 0; ithCheckBox < nCheckBoxes; ithCheckBox++) {
            checkBoxDescr[ithCheckBox] = anova2CheckBoxDescr[ithCheckBox];
            checkBoxes[ithCheckBox].setText(checkBoxDescr[ithCheckBox]);
            checkBoxes[ithCheckBox].setId(checkBoxDescr[ithCheckBox]);
            if (checkBoxes[ithCheckBox].isSelected() == true) {
                checkBoxes[ithCheckBox].setTextFill(Color.GREEN);
            }
            else { checkBoxes[ithCheckBox].setTextFill(Color.RED); }
        }  
    }  
    
    public void putEmAllUp() { 
        if (checkBoxSettings[0] == true) {
            boxPlotContainingPane.setVisible(true);
            boxPlotView.doThePlot();
        }
        else { boxPlotContainingPane.setVisible(false);   }
        if (checkBoxSettings[1] == true) {
            circlePlotContainingPane.setVisible(true);
            circlePlotView.doThePlot();
        }
        else { circlePlotContainingPane.setVisible(false);  }
        
        if (checkBoxSettings[2] == true) {
            mainEffectAContainingPane.setVisible(true);
            mainEffect_AView.doThePlot();
        }
        else {  mainEffectAContainingPane.setVisible(false);  }
  
        if (checkBoxSettings[3] == true) {
            mainEffectBContainingPane.setVisible(true);
            mainEffect_BView.doThePlot();
        }
        else { mainEffectBContainingPane.setVisible(false);  }

        if (checkBoxSettings[4] == true) {
            interactionContainingPane.setVisible(true);
            interactionView.doThePlot();
        }
        else { interactionContainingPane.setVisible(false);  } 

        if (checkBoxSettings[5] == true) {
            printReportContainingPane.setVisible(true);
        }
        else {  printReportContainingPane.setVisible(false);  } 
    }
    
    public void populateTheBackGround() {
        
        initWidth[0] = 450;
        initHeight[0] = 400;
        boxPlotView = new ANOVA2_BoxPlotView(anova2_Factorial_Model, this, sixteenths_across[0], sixteenths_down[0], initWidth[0], initHeight[0]);
        boxPlotView.completeTheDeal();
        boxPlotContainingPane = boxPlotView.getTheContainingPane();
    
        initWidth[1] = 450;
        initHeight[1] = 400;
        circlePlotView = new ANOVA2_CirclePlotView(anova2_Factorial_Model, this, sixteenths_across[1], sixteenths_down[1], initWidth[1], initHeight[1]);
        circlePlotView.completeTheDeal();        
        circlePlotContainingPane = circlePlotView.getTheContainingPane();  
     
        initWidth[2] = 450;
        initHeight[2] = 400;
        mainEffect_AView = new ANOVA2_MainEffect_AView(anova2_Factorial_Model, this, sixteenths_across[2], sixteenths_down[2], initWidth[3], initHeight[3]);
        mainEffect_AView.completeTheDeal();        
        mainEffectAContainingPane = mainEffect_AView.getTheContainingPane();  
        
        initWidth[3] = 450;
        initHeight[3] = 400;
        mainEffect_BView = new ANOVA2_MainEffect_BView(anova2_Factorial_Model, this, sixteenths_across[3], sixteenths_down[3], initWidth[3], initHeight[3]);
        mainEffect_BView.completeTheDeal();        
        mainEffectBContainingPane = mainEffect_BView.getTheContainingPane();  

        initWidth[4] = 450;
        initHeight[4] = 400;
        interactionView = new ANOVA2_InteractionView(anova2_Factorial_Model, this, sixteenths_across[4], sixteenths_down[4], initWidth[4], initHeight[4]);
        interactionView.completeTheDeal();
        interactionContainingPane = interactionView.getTheContainingPane(); 

        initWidth[5] = 700;
        printReportView = new ANOVA2_PrintReportView(anova2_Factorial_Model, this, sixteenths_across[5], sixteenths_down[5], initWidth[5], initHeight[5]);
        printReportView.completeTheDeal();
        printReportContainingPane = printReportView.getTheContainingPane(); 

        backGround.getChildren().addAll(boxPlotContainingPane, 
                                        circlePlotContainingPane, 
                                        mainEffectAContainingPane,
                                        mainEffectBContainingPane,
                                        interactionContainingPane,
                                        printReportContainingPane); 
    }
}

