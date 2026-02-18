/**************************************************
 *             ANOVA1_Quant_Dashboard             *
 *                    02/14/25                    *
 *                     15:00                      *
 *************************************************/
/**************************************************
*    Initial widths and heights from Super Class  *
*              are 400 and 300                    *
**************************************************/
package anova1.quantitative;

import anova1.categorical.FDistPDFView;
import proceduresManyUnivariate.VerticalBoxPlot_Model;
import proceduresOneUnivariate.NormProb_Model;
import proceduresOneUnivariate.NormProb_View;
import superClasses.Dashboard;
import dataObjects.QuantitativeDataVariable;
import java.util.ArrayList;
import javafx.scene.paint.Color;
import javafx.scene.layout.Pane;
import proceduresOneUnivariate.NormProb_DiffModel;
import proceduresOneUnivariate.NormProb_DiffView;
import splat.Data_Manager;

public class ANOVA1_Quant_Dashboard extends Dashboard {

    // POJOs
    private final String explanatoryVariable;
    private final String responseVariable;
    
    // Make empty if no-print
    //String waldoFile = "ANOVA1_Quant_Dashboard";
    String waldoFile = "";
    
    private final String[] anova1CheckBoxDescr = { " FDist \n (Inference)",  
                  "Box Plot ", " Circle Plot ", "Normal Plot", "Diff Plot",
                  "Homogeneity Check", " Print Stats "};

    // My classes
    private final ANOVA1_Quant_Model anova_Quant_Model;
    private ArrayList<QuantitativeDataVariable> allTheQDVs;
    private FDistPDFView fPDFView;
    private ANOVA1_Quant_BoxPlotView anova1_Quant_BoxPlotView;
    private ANOVA1_Quant_CirclePlotView anova1_Quant_CirclePlotView;
    private ANOVA1_Quant_HomogeneityCheck_View anova1_Quant_HomogeneityCheck_View; 
    private NormProb_View normProb_View; 
    private NormProb_Model normProb_Model;
    private NormProb_DiffView normProb_DiffView;
    private NormProb_DiffModel normProb_DiffModel;
    private ANOVA1_Quant_PrintReportView anova1_Quant_PrintReportView;
    private VerticalBoxPlot_Model verticalBoxPlot_Model;
    
    // POJOs / FX

    Pane fPDFPlotContainingPane, boxPlotContainingPane, 
         circlePlotContainingPane, normalPlotContainingPane, 
         diffPlotContainingPane,
         homogeneityCheckContainingPane, printReportContainingPane;
            
    public ANOVA1_Quant_Dashboard(ANOVA1_Quant_Controller anova1_quant_controller, ANOVA1_Quant_Model anova1_Quant_Model) {
        super(7);
        dm = anova1_quant_controller.getDataManager();
        dm.whereIsWaldo(65, waldoFile, "Constructing");
        explanatoryVariable = anova1_quant_controller.getExplanatoryVariable();
        responseVariable = anova1_quant_controller.getResponseVariable();
        checkBoxDescr = new String[nCheckBoxes];
        
        for (int ithCheckBox = 0; ithCheckBox < nCheckBoxes; ithCheckBox++) {
            checkBoxDescr[ithCheckBox] = anova1CheckBoxDescr[ithCheckBox];
            checkBoxes[ithCheckBox].setText(checkBoxDescr[ithCheckBox]);
            checkBoxes[ithCheckBox].setId(checkBoxDescr[ithCheckBox]);
            if (checkBoxes[ithCheckBox].isSelected() == true) {
                checkBoxes[ithCheckBox].setTextFill(Color.GREEN);
            }
            else {
                checkBoxes[ithCheckBox].setTextFill(Color.RED);
            }
        } 
        
        setTitle("One Way Analysis of Variance dashboard");
        this.anova_Quant_Model = anova1_Quant_Model;
    }  
    
    public void putEmAllUp() { 
        dm.whereIsWaldo(87, waldoFile, "putEmAllUp()");

        if (checkBoxSettings[0] == true) {
            fPDFPlotContainingPane.setVisible(true);
            fPDFView.doTheGraph();
        }
        else { fPDFPlotContainingPane.setVisible(false); }
               
        if (checkBoxSettings[1] == true) {
            boxPlotContainingPane.setVisible(true);
            anova1_Quant_BoxPlotView.doTheGraph();
        }
        else { boxPlotContainingPane.setVisible(false); } 
        
        if (checkBoxSettings[2] == true) {
            circlePlotContainingPane.setVisible(true);
            anova1_Quant_CirclePlotView.doTheGraph();
        }
        else { circlePlotContainingPane.setVisible(false);  }
        
        if (checkBoxSettings[3] == true) {
            normalPlotContainingPane.setVisible(true);
            normProb_View.doTheGraph();
        }
        else { normalPlotContainingPane.setVisible(false);  }
        
        if (checkBoxSettings[4] == true) {
            diffPlotContainingPane.setVisible(true);
            normProb_DiffView.doTheGraph();
        }
        else { diffPlotContainingPane.setVisible(false);  } 

        if (checkBoxSettings[5] == true) {
            homogeneityCheckContainingPane.setVisible(true);
            anova1_Quant_HomogeneityCheck_View.doTheGraph();
        }
        else { homogeneityCheckContainingPane.setVisible(false);  } 
        
        if (checkBoxSettings[6] == true) {
            printReportContainingPane.setVisible(true);
        }
        else {  printReportContainingPane.setVisible(false); } 
    }
    
    public void populateTheBackGround() {
        dm.whereIsWaldo(144, waldoFile, "populateTheBackGround()");
        
        initWidth[0] = 500.;
        initHeight[0] = 325.;
        fPDFView = new FDistPDFView(anova_Quant_Model, this, sixteenths_across[0], sixteenths_down[0], initWidth[0], initHeight[0]);
        fPDFView.completeTheDeal();
        fPDFPlotContainingPane = fPDFView.getTheContainingPane(); 
        
        //allTheQDVs = new ArrayList<>();
        //allTheQDVs = anova_Quant_Model.getAllQDVs();
        //verticalBoxPlot_Model = new VerticalBoxPlot_Model(anova_Quant_Model, allTheQDVs);
        
        initWidth[1] = 625; 
        anova1_Quant_BoxPlotView = new ANOVA1_Quant_BoxPlotView(anova_Quant_Model, this, sixteenths_across[1], sixteenths_down[1], initWidth[1], initHeight[1]);
        anova1_Quant_BoxPlotView.completeTheDeal();
        boxPlotContainingPane = anova1_Quant_BoxPlotView.getTheContainingPane(); 

        initWidth[2] = 500;
        initHeight[2] = 400;
        anova1_Quant_CirclePlotView = new ANOVA1_Quant_CirclePlotView(anova_Quant_Model, this, sixteenths_across[2], sixteenths_down[2], initWidth[2], initHeight[2]);
        anova1_Quant_CirclePlotView.completeTheDeal();        
        circlePlotContainingPane = anova1_Quant_CirclePlotView.getTheContainingPane();

        initWidth[3] = 450;
        initHeight[3] = 350;
        normProb_Model = anova_Quant_Model.getNormProbModel();
        normProb_View = new NormProb_View(normProb_Model, this, sixteenths_across[3], sixteenths_down[3], initWidth[3], initHeight[3]);
        normProb_View.completeTheDeal();
        normalPlotContainingPane = normProb_View.getTheContainingPane();
        
        initWidth[4] = 450;
        initHeight[4] = 350;
        normProb_DiffModel = anova_Quant_Model.getNormProbDiffModel();
        normProb_DiffView = new NormProb_DiffView(normProb_DiffModel, this, sixteenths_across[4], sixteenths_down[4], initWidth[4], initHeight[4]);
        normProb_DiffView.completeTheDeal();
        diffPlotContainingPane = normProb_DiffView.getTheContainingPane(); 
        
        initWidth[5] = 500;
        initHeight[5] = 400;
        anova1_Quant_HomogeneityCheck_View = new ANOVA1_Quant_HomogeneityCheck_View(anova_Quant_Model, this, sixteenths_across[5], sixteenths_down[5], initWidth[5], initHeight[5]);
        anova1_Quant_HomogeneityCheck_View.completeTheDeal();
        homogeneityCheckContainingPane = anova1_Quant_HomogeneityCheck_View.getTheContainingPane(); 
        
        initWidth[6] = 750;
        initHeight[6] = 600;
        anova1_Quant_PrintReportView = new ANOVA1_Quant_PrintReportView(anova_Quant_Model, this, sixteenths_across[6] + 300, sixteenths_down[6] - 150, initWidth[6], initHeight[6]);
        anova1_Quant_PrintReportView.completeTheDeal();
        printReportContainingPane = anova1_Quant_PrintReportView.getTheContainingPane(); 

        backGround.getChildren().addAll(fPDFPlotContainingPane,
                                        boxPlotContainingPane, 
                                        circlePlotContainingPane,
                                        normalPlotContainingPane,
                                        diffPlotContainingPane,
                                        homogeneityCheckContainingPane,
                                        printReportContainingPane);  
    }
    
    public String getExplanVar() { return explanatoryVariable; }
    public String getResponseVar() { return responseVariable; }
    
    public String getSubTitle() { 
        String subTitle = responseVariable + " vs. " + explanatoryVariable; 
        return subTitle;
    }
    
    public Data_Manager getDataManager() { return dm; }
}
