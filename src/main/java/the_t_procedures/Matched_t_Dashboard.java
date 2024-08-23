/**************************************************
 *               Matched_t_Dashboard              *
 *                    11/01/23                    *
 *                     00:00                      *
 *************************************************/
/**************************************************
*    Initial widths and heights from Super Class  *
*              are 400 and 350                    *
**************************************************/
package the_t_procedures;

import proceduresOneUnivariate.PrintUStats_Model;
import proceduresManyUnivariate.VerticalBoxPlot_Model;
import proceduresManyUnivariate.HorizontalBoxPlot_Model;
import proceduresOneUnivariate.PrintUStats_View;
import proceduresOneUnivariate.StemNLeaf_View;
import proceduresOneUnivariate.StemNLeaf_Model;
import proceduresManyUnivariate.VerticalBoxPlot_View;
import proceduresManyUnivariate.HorizontalBoxPlot_View;
import superClasses.Dashboard;
import dataObjects.QuantitativeDataVariable;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import proceduresOneUnivariate.NormProb_DiffModel;
import proceduresOneUnivariate.NormProb_DiffView;
import proceduresOneUnivariate.NormProb_Model;
import proceduresOneUnivariate.NormProb_View;
import splat.Data_Manager;

public class Matched_t_Dashboard extends Dashboard {
    //  POJOs
    //String descriptionOfDifference;
    String[] matchedTCheckBoxDescr = { "t-test", "NormalDiff", "NormalProb", 
                                        "HBoxPlot", "VBoxPlot",
                                        "StemPlot", "InferenceReport",
                                        "DiffPlot", "Univ Statistics"
                                     };    
    
    // Make empty if no-print
    //String waldoFile = "Matched_t_Dashboard";
    String waldoFile = "";
    
    // My classes
    StemNLeaf_Model stemNLeaf_Model;
    StemNLeaf_View stemNLeaf_View;     
    HorizontalBoxPlot_Model hBox_Model;
    HorizontalBoxPlot_View hBox_View; 
    QuantitativeDataVariable theQDV;
    VerticalBoxPlot_Model vBox_Model;
    VerticalBoxPlot_View vBox_View;
    NormProb_Model normProbModel;
    NormProb_DiffModel normProb_DiffModel;
    NormProb_View normProb_View;
    NormProb_DiffView normProb_DiffView;
    Matched_t_Model matched_t_Model;
    Matched_t_DiffModel matched_t_DiffModel;
    Matched_t_DiffView matched_t_Diff_View;
    Matched_t_PDFView matched_t_PDF_View;
    Matched_t_Inf_Report_View matched_t_Report;
    PrintUStats_View printUStats_View;
    PrintUStats_Model printUStatsModel;

    // POJOs / FX
    Pane hBoxContainingPane, vBoxContainingPane,
         bbslContainingPane, matched_t_ContainingPane,
         normProbContainingPane, normProbDiffContainingPane,
         infReportContainingPane, printUStatsContainingPane,
         diffPlotContainingPane; 
            
    public Matched_t_Dashboard(Matched_t_Controller matched_t_controller, QuantitativeDataVariable theQDV) {
        super(9);  
        dm = matched_t_controller.getDataManager();
        dm.whereIsWaldo(73, waldoFile, "constructing");
        this.theQDV = theQDV;
        hBox_Model = matched_t_controller.getHBox_Model();
        //vBox_Model = new VerticalBoxPlot_Model();
        vBox_Model = matched_t_controller.getVBox_Model();
        normProb_DiffModel = new NormProb_DiffModel();
        normProb_DiffModel = matched_t_controller.getNormProb_DiffModel();
        
        normProbModel = new NormProb_Model();
        normProbModel = matched_t_controller.getNormProbModel();
        matched_t_DiffModel = new Matched_t_DiffModel(matched_t_controller);
        matched_t_Model = matched_t_controller.getMatchedTModel();    
        //descriptionOfDifference = matched_t_controller.getDescriptionOfDifference();
        
        // ****************************************************************
        // *  The stemNLeaf_Model parameters are also supporting a back-  *
        // *  to-back stem and leaf plot.                                 *
        // ****************************************************************
        //stemNLeaf_Model = new StemNLeaf_Model(descriptionOfDifference, theQDV, false, 0, 0, 0);
        stemNLeaf_Model = matched_t_controller.getStemNLeaf_Model();
        
        checkBoxDescr = new String[nCheckBoxes];
        for (int ithCheckBox = 0; ithCheckBox < nCheckBoxes; ithCheckBox++) {
            checkBoxDescr[ithCheckBox] = matchedTCheckBoxDescr[ithCheckBox];
            checkBoxes[ithCheckBox].setText(checkBoxDescr[ithCheckBox]);
            checkBoxes[ithCheckBox].setId(checkBoxDescr[ithCheckBox]);
            if (checkBoxes[ithCheckBox].isSelected() == true) 
                checkBoxes[ithCheckBox].setTextFill(Color.GREEN);
            else
                checkBoxes[ithCheckBox].setTextFill(Color.RED);
        }
        setTitle("Matched t inference dashboard");    
    }  

    public void putEmAllUp() { 
        if (checkBoxSettings[0] == true) {
            matched_t_ContainingPane.setVisible(true);
            matched_t_PDF_View.doTheGraph();
        }
        else { matched_t_ContainingPane.setVisible(false); }
        
        if (checkBoxSettings[1] == true) {
            normProbDiffContainingPane.setVisible(true);
            normProb_DiffView.doTheGraph();
        }
        else { normProbDiffContainingPane.setVisible(false); }
        
        if (checkBoxSettings[2] == true) {
            normProbContainingPane.setVisible(true);
            normProb_View.doTheGraph();
        }
        else { normProbContainingPane.setVisible(false);  }        
        
        if (checkBoxSettings[3] == true) {
            hBoxContainingPane.setVisible(true);
            hBox_View.doTheGraph();
        }
        else { hBoxContainingPane.setVisible(false); }
        
        if (checkBoxSettings[4] == true) {
            vBoxContainingPane.setVisible(true);
            vBox_View.doTheGraph();
        }
        else { vBoxContainingPane.setVisible(false); }

        if (checkBoxSettings[5] == true) {
            bbslContainingPane.setVisible(true);
        }
        else { bbslContainingPane.setVisible(false);  } 
        
        if (checkBoxSettings[6] == true) {
            infReportContainingPane.setVisible(true);
        }
        else { infReportContainingPane.setVisible(false); }
        
        if (checkBoxSettings[7] == true) {
            diffPlotContainingPane.setVisible(true);
            matched_t_Diff_View.doTheGraph();
        }
        else { diffPlotContainingPane.setVisible(false); } 
        
        if (checkBoxSettings[8] == true) {
            printUStatsContainingPane.setVisible(true);
        }
        else { printUStatsContainingPane.setVisible(false); } 
    }
    
    public void populateTheBackGround() {       
        containingPaneStyle =  "-fx-background-color: white;" +
            "-fx-border-color: blue, blue;" + 
            "-fx-border-width: 4, 4;" +
            "fx-border-radius: 0, 0;" +
            "-fx-border-insets: -4, -4;" +
            "-fx-border-style: solid centered, solid centered;";
        
        initWidth[0] = 600;
        matched_t_PDF_View = new Matched_t_PDFView(matched_t_Model, this, sixteenths_across[0], sixteenths_down[0], initWidth[0], initHeight[0]);
        matched_t_PDF_View.completeTheDeal();        
        matched_t_ContainingPane = matched_t_PDF_View.getTheContainingPane();  
        matched_t_ContainingPane.setStyle(containingPaneStyle);
        
        initWidth[1] = 500;
        normProb_DiffView = new NormProb_DiffView(normProb_DiffModel, this, 0.5 * sixteenths_across[1], sixteenths_down[1], 670, 500);
        normProb_DiffView.completeTheDeal();        
        normProbDiffContainingPane = normProb_DiffView.getTheContainingPane();  
        normProbDiffContainingPane.setStyle(containingPaneStyle);
        
        initWidth[2] = 450;
        normProb_View = new NormProb_View(normProbModel, this, 0.5 * sixteenths_across[2], sixteenths_down[2], 675, 375);
        normProb_View.completeTheDeal();        
        normProbContainingPane = normProb_View.getTheContainingPane();  
        normProbContainingPane.setStyle(containingPaneStyle);
        
        initWidth[3] = 625;
        hBox_View = new HorizontalBoxPlot_View(hBox_Model, this, sixteenths_across[3], sixteenths_down[3], initWidth[3], initHeight[3]);
        hBox_View.completeTheDeal();
        hBoxContainingPane = hBox_View.getTheContainingPane(); 
        hBoxContainingPane.setStyle(containingPaneStyle);
        
        initWidth[4] = 625;
        vBox_View = new VerticalBoxPlot_View(vBox_Model, this, sixteenths_across[4], sixteenths_down[4], initWidth[4], initHeight[4]);
        vBox_View.completeTheDeal();
        vBoxContainingPane = vBox_View.getTheContainingPane(); 
        vBoxContainingPane.setStyle(containingPaneStyle);

        initWidth[5] = 550;
        stemNLeaf_View = new StemNLeaf_View(stemNLeaf_Model, this, sixteenths_across[5], sixteenths_down[5], initWidth[5], initHeight[5]);
        stemNLeaf_View.completeTheDeal();        
        bbslContainingPane = stemNLeaf_View.getTheContainingPane();  
        bbslContainingPane.setStyle(containingPaneStyle);
        
                
        initWidth[6] = 625;
        initHeight[6] = 375;
        matched_t_Report = new Matched_t_Inf_Report_View(matched_t_Model, this, sixteenths_across[6], sixteenths_down[6], initWidth[6], initHeight[6]);
        matched_t_Report.completeTheDeal();
        infReportContainingPane = matched_t_Report.getTheContainingPane(); 
        infReportContainingPane.setStyle(containingPaneStyle);
        matched_t_Diff_View = new Matched_t_DiffView(matched_t_DiffModel, this, 0.5 * sixteenths_across[7], sixteenths_down[7], initWidth[7], initHeight[7]);
        matched_t_Diff_View.completeTheDeal();        
        diffPlotContainingPane = matched_t_Diff_View.getTheContainingPane();  
        diffPlotContainingPane.setStyle(containingPaneStyle);        
        
        String varDescr = theQDV.getTheVarLabel();
        printUStatsModel = new PrintUStats_Model(varDescr, theQDV, false);
        printUStatsModel.constructThePrintLines();
        
        initWidth[8] = 400;
        initHeight[8] = 550;
        printUStats_View = new PrintUStats_View(printUStatsModel, this, 0.5 * sixteenths_across[8], sixteenths_down[8], initWidth[8], initHeight[8]);
        printUStats_View.completeTheDeal();        
        printUStatsContainingPane = printUStats_View.getTheContainingPane();  
        printUStatsContainingPane.setStyle(containingPaneStyle);
        
        backGround.getChildren().addAll(matched_t_ContainingPane,
                                        normProbDiffContainingPane,
                                        normProbContainingPane,
                                        hBoxContainingPane, 
                                        vBoxContainingPane,
                                        bbslContainingPane,
                                        infReportContainingPane,
                                        diffPlotContainingPane,
                                        printUStatsContainingPane);          
    }
    
    public Data_Manager getDataManager() { return dm; }
}

