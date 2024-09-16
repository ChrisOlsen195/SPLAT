/**************************************************
 *              ANOVA1_Cat_Dashboard              *
 *                    09/03/24                    *
 *                     09:00                      *
 *************************************************/
/**************************************************
*    Initial widths and heights from Super Class  *
*              are 400 and 300                    *
**************************************************/
package anova1.categorical;

import proceduresManyUnivariate.VerticalBoxPlot_Model;
import proceduresOneUnivariate.NormProb_Model;
import proceduresOneUnivariate.NormProb_View;
import proceduresManyUnivariate.VerticalBoxPlot_View;
import superClasses.Dashboard;
import dataObjects.QuantitativeDataVariable;
import java.util.ArrayList;
import javafx.scene.paint.Color;
import javafx.scene.layout.Pane;
import proceduresOneUnivariate.NormProb_DiffModel;
import proceduresOneUnivariate.NormProb_DiffView;
import splat.Data_Manager;

public class ANOVA1_Cat_Dashboard extends Dashboard {

    // POJOs
    private final String explanatoryVariable;
    private final String responseVariable;
    
    // Make empty if no-print
    //String waldoFile = "ANOVA1_Cat_Dashboard";
    String waldoFile = "";
    
    private final String[] anova1CheckBoxDescr = { " FDist \n (Inference)",  
                  "Box Plot ", " Circle Plot ", "Normal Plot", "Diff Plot",
                  "Homogeneity Check", " Print Stats "};

    // My classes
    private final ANOVA1_Cat_Model anova_Cat_Model;
    private ArrayList<QuantitativeDataVariable> allTheQDVs;
    private FDistPDFView fPDFView;
    private VerticalBoxPlot_View vertBoxPlotView;
    private ANOVA1_Cat_CirclePlotView anova1_Cat_CirclePlotView;
    private ANOVA1_Cat_HomogeneityCheck_View anova1_Cat_HomogeneityCheck_View; 
    private NormProb_View normProb_View; 
    private NormProb_Model normProb_Model;
    private NormProb_DiffView normProb_DiffView;
    private NormProb_DiffModel normProb_DiffModel;
    private ANOVA1_Cat_PrintReportView anova1_Cat_PrintReportView;
    private VerticalBoxPlot_Model verticalBoxPlot_Model;
    
    // POJOs / FX

    Pane fPDFPlotContainingPane, boxPlotContainingPane, 
         circlePlotContainingPane, normalPlotContainingPane, 
         diffPlotContainingPane,
         homogeneityCheckContainingPane, printReportContainingPane;
            
    public ANOVA1_Cat_Dashboard(ANOVA1_Cat_Controller anova1_cat_controller, ANOVA1_Cat_Model anova1Model) {
        super(7);
        dm = anova1_cat_controller.getDataManager();
        dm.whereIsWaldo(63, waldoFile, "\nConstructing");
        explanatoryVariable = anova1_cat_controller.getExplanatoryVariable();
        responseVariable = anova1_cat_controller.getResponseVariable();
        checkBoxDescr = new String[nCheckBoxes];
        
        for (int ithCheckBox = 0; ithCheckBox < nCheckBoxes; ithCheckBox++) {
            checkBoxDescr[ithCheckBox] = anova1CheckBoxDescr[ithCheckBox];
            checkBoxes[ithCheckBox].setText(checkBoxDescr[ithCheckBox]);
            checkBoxes[ithCheckBox].setId(checkBoxDescr[ithCheckBox]);
            if (checkBoxes[ithCheckBox].isSelected() == true) {
                checkBoxes[ithCheckBox].setTextFill(Color.GREEN);
            } else {
                checkBoxes[ithCheckBox].setTextFill(Color.RED);
            }
        } 
        
        setTitle("One Way Analysis of Variance dashboard");
        this.anova_Cat_Model = anova1Model;
    }  
    
    public void putEmAllUp() { 
        dm.whereIsWaldo(85, waldoFile, "putEmAllUp()");
        
        if (checkBoxSettings[0] == true) {
            fPDFPlotContainingPane.setVisible(true);
            fPDFView.doTheGraph();
        } else { fPDFPlotContainingPane.setVisible(false); }
               
        if (checkBoxSettings[1] == true) {
            boxPlotContainingPane.setVisible(true);
            vertBoxPlotView.doTheGraph();
        } else { boxPlotContainingPane.setVisible(false); } 
        
        if (checkBoxSettings[2] == true) {
            circlePlotContainingPane.setVisible(true);
            anova1_Cat_CirclePlotView.doTheGraph();
        } else { circlePlotContainingPane.setVisible(false);  }
        
        if (checkBoxSettings[3] == true) {
            normalPlotContainingPane.setVisible(true);
            normProb_View.doTheGraph();
        } else { normalPlotContainingPane.setVisible(false);  }
        
        if (checkBoxSettings[4] == true) {
            diffPlotContainingPane.setVisible(true);
            normProb_DiffView.doTheGraph();
        } else { diffPlotContainingPane.setVisible(false);  } 

        if (checkBoxSettings[5] == true) {
            homogeneityCheckContainingPane.setVisible(true);
            anova1_Cat_HomogeneityCheck_View.doTheGraph();
        }  else { homogeneityCheckContainingPane.setVisible(false);  } 
        
        if (checkBoxSettings[6] == true) {
            printReportContainingPane.setVisible(true);
        } else {  printReportContainingPane.setVisible(false); } 
    }
    
    @Override
    public void populateTheBackGround() {
        dm.whereIsWaldo(123, waldoFile, "populateTheBackGround()");
        
        fPDFView = new FDistPDFView(anova_Cat_Model, this, sixteenths_across[0], sixteenths_down[0], initWidth[0], initHeight[0]);
        fPDFView.completeTheDeal();
        fPDFPlotContainingPane = fPDFView.getTheContainingPane(); 
        
        allTheQDVs = new ArrayList<>();
        allTheQDVs = anova_Cat_Model.getAllQDVs();
        verticalBoxPlot_Model = new VerticalBoxPlot_Model(anova_Cat_Model, allTheQDVs);
        
        initWidth[1] = 625; 
        vertBoxPlotView = new VerticalBoxPlot_View(verticalBoxPlot_Model, this, sixteenths_across[1], sixteenths_down[1], initWidth[1], initHeight[1]);
        vertBoxPlotView.completeTheDeal();
        boxPlotContainingPane = vertBoxPlotView.getTheContainingPane(); 

        initWidth[2] = 500;
        initHeight[2] = 350;
        anova1_Cat_CirclePlotView = new ANOVA1_Cat_CirclePlotView(anova_Cat_Model, this, sixteenths_across[2], sixteenths_down[2], initWidth[2], initHeight[2]);
        anova1_Cat_CirclePlotView.completeTheDeal();        
        circlePlotContainingPane = anova1_Cat_CirclePlotView.getTheContainingPane();

        initWidth[3] = 475;
        initHeight[3] = 350;
        normProb_Model = anova_Cat_Model.getNormProbModel();
        normProb_View = new NormProb_View(normProb_Model, this, sixteenths_across[3], sixteenths_down[3], initWidth[3], initHeight[3]);
        normProb_View.completeTheDeal();
        normalPlotContainingPane = normProb_View.getTheContainingPane();
        
        initWidth[4] = 475;
        initHeight[4] = 350;
        normProb_DiffModel = anova_Cat_Model.getNormProbDiffModel();
        normProb_DiffView = new NormProb_DiffView(normProb_DiffModel, this, sixteenths_across[4], sixteenths_down[4], initWidth[4], initHeight[4]);
        normProb_DiffView.completeTheDeal();
        diffPlotContainingPane = normProb_DiffView.getTheContainingPane(); 
        
        initWidth[5] = 475;
        initHeight[5] = 350;
        anova1_Cat_HomogeneityCheck_View = new ANOVA1_Cat_HomogeneityCheck_View(anova_Cat_Model, this, sixteenths_across[5], sixteenths_down[5], initWidth[5], initHeight[5]);
        anova1_Cat_HomogeneityCheck_View.completeTheDeal();
        homogeneityCheckContainingPane = anova1_Cat_HomogeneityCheck_View.getTheContainingPane(); 
        
        initWidth[6] = 750;
        initHeight[6] = 600;
        anova1_Cat_PrintReportView = new ANOVA1_Cat_PrintReportView(anova_Cat_Model, this, sixteenths_across[6] + 300, sixteenths_down[6] - 150, initWidth[6], initHeight[6]);
        anova1_Cat_PrintReportView.completeTheDeal();
        printReportContainingPane = anova1_Cat_PrintReportView.getTheContainingPane(); 

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
