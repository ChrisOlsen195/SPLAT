/**************************************************
 *               ANOVA2_RM_Dashboard              *
 *                    05/24/24                    *
 *                     12:00                      *
 *************************************************/
/**************************************************
*    Initial widths and heights from Super Class  *
*              are 400 and 350                    *
**************************************************/
package anova2;

import dataObjects.QuantitativeDataVariable;
import java.util.ArrayList;
import superClasses.Dashboard;
import javafx.scene.paint.Color;
import javafx.scene.layout.Pane;
import proceduresManyUnivariate.VerticalBoxPlot_View;
import proceduresManyUnivariate.VerticalBoxPlot_Model;

public class ANOVA2_RM_Dashboard extends Dashboard {
    // POJOs
    final String[] anova2CheckBoxDescr = { " Line Plot ", " Box Plot ", 
                                       " Print Stats ", " Sphericity " };
    
    String waldoFile = "";
    //String waldoFile = "ANOVA2_RM_Dashboard";

    // My classes
    ANOVA2_RM_Model anova2_RM_Model;
    private ArrayList<QuantitativeDataVariable> allTheQDVs;
    ANOVA2_RM_LinePlotView anova_RM_LinePlotView; 
    ANOVA2_RM_PrintReportView anova2_RM_PrintReportView;
    ANOVA2_RM_SphericityReportView anova2_RM_SphericityReportView;
    private VerticalBoxPlot_Model verticalBoxPlotModel;
    private VerticalBoxPlot_View verticalBoxPlotView;

    //  FX Classes
    Pane linePlotContainingPane, boxPlotContainingPane,
         printReportContainingPane, sphericityReportContainingPane;

    public ANOVA2_RM_Dashboard(ANOVA2_RM_Controller anova2_rm_controller, ANOVA2_RM_Model anova2_RM_Model) {
        super(4);
        this.anova2_RM_Model = anova2_RM_Model;
        dm = anova2_RM_Model.getDataManager();
        dm.whereIsWaldo(45, waldoFile, "Constructing");
        //txtTitle = new Text("ANOVA2 RM");
        checkBoxDescr = new String[nCheckBoxes];
        
        for (int ithCheckBox = 0; ithCheckBox < nCheckBoxes; ithCheckBox++) {
            checkBoxDescr[ithCheckBox] = anova2CheckBoxDescr[ithCheckBox];
            checkBoxes[ithCheckBox].setText(checkBoxDescr[ithCheckBox]);
            checkBoxes[ithCheckBox].setId(checkBoxDescr[ithCheckBox]);
            if (checkBoxes[ithCheckBox].isSelected() == true) 
                checkBoxes[ithCheckBox].setTextFill(Color.GREEN);
            else
                checkBoxes[ithCheckBox].setTextFill(Color.RED);
        }  
        setTitle("ANOVA Repeated Measure");
        this.anova2_RM_Model = anova2_RM_Model;
    }  
    
    public void putEmAllUp() { 
        if (checkBoxSettings[0] == true) {
            linePlotContainingPane.setVisible(true);
            anova_RM_LinePlotView.doThePlot();
        }
        else { linePlotContainingPane.setVisible(false); }
        
        if (checkBoxSettings[1] == true) {
            boxPlotContainingPane.setVisible(true);
            verticalBoxPlotView.doTheGraph();
        }
        else { boxPlotContainingPane.setVisible(false); }
       
        if (checkBoxSettings[2] == true) {
            printReportContainingPane.setVisible(true);
        }
        else {  printReportContainingPane.setVisible(false);  } 
        
        if (checkBoxSettings[3] == true) {
            sphericityReportContainingPane.setVisible(true);
        }
        else { sphericityReportContainingPane.setVisible(false);  }
    }
    
    public void populateTheBackGround() {
        initWidth[0] = 550;
        anova_RM_LinePlotView = new ANOVA2_RM_LinePlotView(anova2_RM_Model, this, sixteenths_across[0], sixteenths_down[0], initWidth[0], initHeight[0]);
        anova_RM_LinePlotView.completeTheDeal();
        linePlotContainingPane = anova_RM_LinePlotView.getTheContainingPane();
        
        initWidth[1] = 625;
        allTheQDVs = new ArrayList<>();
        allTheQDVs = anova2_RM_Model.getAllTheQDVs();
        verticalBoxPlotModel = new VerticalBoxPlot_Model(anova2_RM_Model, allTheQDVs);
        verticalBoxPlotView = new VerticalBoxPlot_View(verticalBoxPlotModel, this, sixteenths_across[1], sixteenths_down[1], initWidth[1], initHeight[1]);
        verticalBoxPlotView.completeTheDeal();
        boxPlotContainingPane = verticalBoxPlotView.getTheContainingPane(); 
    
        initWidth[2] = 750;  
        initHeight[2] = 650; 
        anova2_RM_PrintReportView = new ANOVA2_RM_PrintReportView(anova2_RM_Model, this, sixteenths_across[2], sixteenths_down[2], initWidth[2], initHeight[2]);
        anova2_RM_PrintReportView.completeTheDeal();
        printReportContainingPane = anova2_RM_PrintReportView.getTheContainingPane(); 
        
        initWidth[3] = 700;  
        anova2_RM_SphericityReportView = new ANOVA2_RM_SphericityReportView(anova2_RM_Model, this, sixteenths_across[3], sixteenths_down[3], initWidth[3], initHeight[3]);
        anova2_RM_SphericityReportView.completeTheDeal();
        sphericityReportContainingPane = anova2_RM_SphericityReportView.getTheContainingPane(); 

        backGround.getChildren().addAll(linePlotContainingPane, 
                                        boxPlotContainingPane,
                                        printReportContainingPane,
                                        sphericityReportContainingPane); 
    }
}



