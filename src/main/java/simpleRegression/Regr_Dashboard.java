/**************************************************
 *           Simple_Regression_Dashboard          *
 *                    11/01/23                    *
 *                     00:00                      *
 *************************************************/
/**************************************************
*    Initial widths and heights from Super Class  *
*              are 400 and 350                    *
**************************************************/
package simpleRegression;

import proceduresOneUnivariate.NormProb_Model;
import proceduresOneUnivariate.NormProb_View;
import dataObjects.QuantitativeDataVariable;
import superClasses.Dashboard;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class Regr_Dashboard extends Dashboard {
    // POJOs
    String subTitle;
    final String[] regrCheckBoxDescr = { " Model Utility Test",
                                         " Scatterplot ", " Residual plot ",
                                         " RegrReport ", " DiagReport ",
                                         "NPP Residuals", "StatSummary",
                                         "Joint CI"};
    
    // Make empty if no-print
    //String waldoFile = "Regression_Dashboard";
    String waldoFile = "";
    
    // My classes

    Regr_BestFit_View bestFitView;  
    PrintDiagReport_View prntDiagReportView; 
    PrintRegrReport_View prntRegReportView;
    Inf_Regr_Model inf_Regression_Model;   
    NormProb_Model normProb_Model;
    Regr_PDFView regression_PDFView;    
    Regr_Residuals_View residualsView;
    NormProb_View nppResidsView;
    PrintBivStats_View printBivStats_View;
    QuantitativeDataVariable qdv_Resids;
    Regr_JointCI_View jointCI_View;

    // POJOs / FX
    Pane pdfViewContainingPane, bestFitContainingPane, residualsContainingPane,
         prntRegReportContainingPane, prntDiagReportContainingPane,
         nppResidsContainingPane, printBivStatsContainingPane,
         jointCIContainingPane; 
            
    public Regr_Dashboard(Inf_Regr_Controller inf_Regression_Controller, Inf_Regr_Model inf_Regression_Model) {
        super(8);
        dm = inf_Regression_Controller.getDataManager();
        dm.whereIsWaldo(55, waldoFile, "Constructing");
        this.inf_Regression_Model = inf_Regression_Model;
        qdv_Resids = new QuantitativeDataVariable();
        qdv_Resids = inf_Regression_Model.getQDVResids();
        subTitle = inf_Regression_Controller.getSubTitle();
        normProb_Model = new NormProb_Model(subTitle, qdv_Resids);
        checkBoxDescr = new String[nCheckBoxes];
        
        for (int ithCheckBox = 0; ithCheckBox < nCheckBoxes; ithCheckBox++) {
            checkBoxDescr[ithCheckBox] = regrCheckBoxDescr[ithCheckBox];
            checkBoxes[ithCheckBox].setText(checkBoxDescr[ithCheckBox]);
            checkBoxes[ithCheckBox].setId(checkBoxDescr[ithCheckBox]);            
            if (checkBoxes[ithCheckBox].isSelected() == true) {
                checkBoxes[ithCheckBox].setTextFill(Color.GREEN);
            }
            else { checkBoxes[ithCheckBox].setTextFill(Color.RED); }
        }
        setTitle("Inference for regression dashboard"); 
    }  
    
    public void putEmAllUp() {         
        if (checkBoxSettings[0] == true) {
            pdfViewContainingPane.setVisible(true);
            regression_PDFView.doTheGraph();
        }
        else { pdfViewContainingPane.setVisible(false); }
        if (checkBoxSettings[1] == true) {
            bestFitContainingPane.setVisible(true);
            bestFitView.doTheGraph();
        }
        else { bestFitContainingPane.setVisible(false);  }
        if (checkBoxSettings[2] == true) {
            residualsContainingPane.setVisible(true);
            residualsView.doTheGraph();
        }
        else { residualsContainingPane.setVisible(false);  }
        
        if (checkBoxSettings[3] == true) {
            prntRegReportContainingPane.setVisible(true);
        }
        else { prntRegReportContainingPane.setVisible(false);  }

        if (checkBoxSettings[4] == true) {
            prntDiagReportContainingPane.setVisible(true);
        }
        else {  prntDiagReportContainingPane.setVisible(false);  }   
        
        if (checkBoxSettings[5] == true) {
            nppResidsContainingPane.setVisible(true);
            nppResidsView.doTheGraph();
        }
        else { nppResidsContainingPane.setVisible(false);  }  
        
        if (checkBoxSettings[6] == true) {
            printBivStatsContainingPane.setVisible(true);
        }
        else { printBivStatsContainingPane.setVisible(false);  }
        
        if (checkBoxSettings[7] == true) {
            jointCIContainingPane.setVisible(true);
            jointCI_View.doTheGraph();
        }
        else { jointCIContainingPane.setVisible(false);  }
    }
    
    public void populateTheBackGround() {
        initWidth[0] = 450;
        initHeight[0] = 300;
        regression_PDFView = new Regr_PDFView(inf_Regression_Model, this, sixteenths_across[0], sixteenths_down[0], initWidth[0], initHeight[0]);
        regression_PDFView.completeTheDeal();
        pdfViewContainingPane = regression_PDFView.getTheContainingPane(); 
        pdfViewContainingPane.setStyle(containingPaneStyle);
        
        initWidth[1] = 750;
        initHeight[1] = 500;
        prntRegReportView = new PrintRegrReport_View(inf_Regression_Model, this, sixteenths_across[1], sixteenths_down[1], initWidth[1], initHeight[1]);
        prntRegReportView.completeTheDeal();
        prntRegReportContainingPane = prntRegReportView.getTheContainingPane(); 
        prntRegReportContainingPane.setStyle(containingPaneStyle);
        
        initWidth[2] = 725;
        initHeight[2] = 600;
        prntDiagReportView = new PrintDiagReport_View(inf_Regression_Model, this, sixteenths_across[2], sixteenths_down[2], initWidth[2], initHeight[2]);
        prntDiagReportView.completeTheDeal();
        prntDiagReportContainingPane = prntDiagReportView.getTheContainingPane(); 
        prntDiagReportContainingPane.setStyle(containingPaneStyle);

        initWidth[3] = 650;
        initHeight[3] = 350;
        bestFitView = new Regr_BestFit_View(inf_Regression_Model, this, sixteenths_across[3], sixteenths_down[3], initWidth[3], initHeight[3]);
        bestFitView.completeTheDeal();
        bestFitContainingPane = bestFitView.getTheContainingPane(); 
        bestFitContainingPane.setStyle(containingPaneStyle);
        
        initWidth[4] = 650;
        initHeight[4] = 350;
        residualsView = new Regr_Residuals_View(inf_Regression_Model, this, sixteenths_across[4], sixteenths_down[4], initWidth[4], initHeight[4]);
        residualsView.completeTheDeal();        
        residualsContainingPane = residualsView.getTheContainingPane();  
        residualsContainingPane.setStyle(containingPaneStyle);
        
        initWidth[5] = 550;
        initHeight[5] = 350;
        nppResidsView = new NormProb_View(normProb_Model, this, sixteenths_across[5], sixteenths_down[5], initWidth[5], initHeight[5]);
        nppResidsView.completeTheDeal();        
        nppResidsContainingPane = nppResidsView.getTheContainingPane();  
        nppResidsContainingPane.setStyle(containingPaneStyle);
        
        initWidth[6] = 450;
        initHeight[6] = 550;
        printBivStats_View = new PrintBivStats_View(inf_Regression_Model, this, sixteenths_across[6], sixteenths_down[6] - 150, initWidth[6], initHeight[6]);
        printBivStats_View.completeTheDeal();        
        printBivStatsContainingPane = printBivStats_View.getTheContainingPane();  
        printBivStatsContainingPane.setStyle(containingPaneStyle);
        
        initWidth[7] = 550;
        initHeight[7] = 350;
        jointCI_View = new Regr_JointCI_View(inf_Regression_Model, this, sixteenths_across[7], sixteenths_down[7], initWidth[7], initHeight[7]);
        jointCI_View.completeTheDeal();        
        jointCIContainingPane = jointCI_View .getTheContainingPane();  
        jointCIContainingPane.setStyle(containingPaneStyle);

        backGround.getChildren().addAll( pdfViewContainingPane,
                                         bestFitContainingPane, 
                                         residualsContainingPane,
                                         prntRegReportContainingPane,
                                         prntDiagReportContainingPane,
                                         nppResidsContainingPane,
                                         printBivStatsContainingPane,
                                         jointCIContainingPane);          
    }
}