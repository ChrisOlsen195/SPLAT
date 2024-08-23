/**************************************************
 *            OneParam_QuadReg_Dashboard          *
 *                    11/01/23                    *
 *                     12:00                      *
 *************************************************/
/**************************************************
*    Initial widths and heights from Super Class  *
*              are 400 and 300                    *
**************************************************/
package quadraticRegression;

import proceduresOneUnivariate.NormProb_Model;
import proceduresOneUnivariate.NormProb_View;
import dataObjects.QuantitativeDataVariable;
import superClasses.Dashboard;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class OneParam_QuadReg_Dashboard extends Dashboard {
    // POJOs
    String subTitle;
    final String[] regrCheckBoxDescr = { " Model Utility Test",
                                         " Scatterplot ", " Residual plot ",
                                         " RegrReport ",
                                         "NPP Residuals", "StatSummary"};    
    // My classes
    OneParam_QuadReg_BestFit_View noInt_QuadRegr_BestFitView;   
    OneParam_QuadReg_PrintQuadRegReport_View prntRegReportView;
    OneParam_QuadReg_Model noInt_QuadReg_Model;   
    NormProb_Model normProb_Model;
    One_Param_QuadReg_PDFView noInt_QuadReg_PDFView;   
    OneParam_QuadReg_Residuals_View noInt_QuadReg_ResidualsView;
    NormProb_View nppResidsView;
    OneParam_QuadReg_PrintQuadStats_View noInt_QuadReg_PrintQuadStats_View;
    QuantitativeDataVariable qdv_Resids;

    // POJOs / FX
    Pane pdfViewContainingPane, bestFitContainingPane, residualsContainingPane,
         prntRegReportContainingPane, nppResidsContainingPane, 
         printBivStatsContainingPane; 
            
    public OneParam_QuadReg_Dashboard(OneParam_QuadReg_Controller regression_Controller, OneParam_QuadReg_Model noInt_QuadReg_Model) {
        super(6); 
        System.out.println("44 OneParam_QuadReg_Dashboard, constructing");
        this.noInt_QuadReg_Model = noInt_QuadReg_Model;
        qdv_Resids = new QuantitativeDataVariable();
        qdv_Resids = noInt_QuadReg_Model.getQDVResids();
        subTitle = regression_Controller.getSubTitle();
        normProb_Model = new NormProb_Model(subTitle, qdv_Resids);
        checkBoxDescr = new String[nCheckBoxes];
        
        for (int ithCheckBox = 0; ithCheckBox < nCheckBoxes; ithCheckBox++) {
            checkBoxDescr[ithCheckBox] = regrCheckBoxDescr[ithCheckBox];
            checkBoxes[ithCheckBox].setText(checkBoxDescr[ithCheckBox]);
            checkBoxes[ithCheckBox].setId(checkBoxDescr[ithCheckBox]);
            
            if (checkBoxes[ithCheckBox].isSelected() == true) 
                checkBoxes[ithCheckBox].setTextFill(Color.GREEN);
            else
                checkBoxes[ithCheckBox].setTextFill(Color.RED);
        }
        setTitle("No-intercept quadratic regression dashboard"); 
    }  
    
    public void putEmAllUp() {         
        if (checkBoxSettings[0] == true) {
            pdfViewContainingPane.setVisible(true);
            noInt_QuadReg_PDFView.doTheGraph();
        }
        else { pdfViewContainingPane.setVisible(false); }
        
        if (checkBoxSettings[1] == true) {
            bestFitContainingPane.setVisible(true);
            noInt_QuadRegr_BestFitView.doTheGraph();
        }
        else {  bestFitContainingPane.setVisible(false);  }
        
        if (checkBoxSettings[2] == true) {
            residualsContainingPane.setVisible(true);
            noInt_QuadReg_ResidualsView.doTheGraph();
        }
        else { residualsContainingPane.setVisible(false);  }
        
        if (checkBoxSettings[3] == true) {
            prntRegReportContainingPane.setVisible(true);
        }
        else { prntRegReportContainingPane.setVisible(false);  }
        
        if (checkBoxSettings[4] == true) {
            nppResidsContainingPane.setVisible(true);
            nppResidsView.doTheGraph();
        }
        else {  nppResidsContainingPane.setVisible(false);  }  
        
        if (checkBoxSettings[5] == true) {
            printBivStatsContainingPane.setVisible(true);
        }
        else { printBivStatsContainingPane.setVisible(false); }
    }
    
    public void populateTheBackGround() {
        noInt_QuadReg_PDFView = new One_Param_QuadReg_PDFView(noInt_QuadReg_Model, this, sixteenths_across[0], sixteenths_down[0], initWidth[0], initHeight[0]);
        noInt_QuadReg_PDFView.completeTheDeal();
        pdfViewContainingPane = noInt_QuadReg_PDFView.getTheContainingPane(); 
        pdfViewContainingPane.setStyle(containingPaneStyle);
        
        initHeight[1] = 525;
        prntRegReportView = new OneParam_QuadReg_PrintQuadRegReport_View(noInt_QuadReg_Model, this, sixteenths_across[1], sixteenths_down[1], initWidth[1], initHeight[1]);
        prntRegReportView.completeTheDeal();
        prntRegReportContainingPane = prntRegReportView.getTheContainingPane(); 
        prntRegReportContainingPane.setStyle(containingPaneStyle);
        
        initWidth[2] = 700;
        initHeight[2] = 400;
        noInt_QuadRegr_BestFitView = new OneParam_QuadReg_BestFit_View(noInt_QuadReg_Model, this, sixteenths_across[2], sixteenths_down[2], initWidth[2], initHeight[2]);
        noInt_QuadRegr_BestFitView.completeTheDeal();
        bestFitContainingPane = noInt_QuadRegr_BestFitView.getTheContainingPane(); 
        bestFitContainingPane.setStyle(containingPaneStyle);
        
        noInt_QuadReg_ResidualsView = new OneParam_QuadReg_Residuals_View(noInt_QuadReg_Model, this, sixteenths_across[3], sixteenths_down[3], initWidth[3], initHeight[3]);
        noInt_QuadReg_ResidualsView.completeTheDeal();        
        residualsContainingPane = noInt_QuadReg_ResidualsView.getTheContainingPane();  
        residualsContainingPane.setStyle(containingPaneStyle);
        
        nppResidsView = new NormProb_View(normProb_Model, this, sixteenths_across[4], sixteenths_down[4], initWidth[4], initHeight[4]);
        nppResidsView.completeTheDeal();        
        nppResidsContainingPane = nppResidsView.getTheContainingPane();  
        nppResidsContainingPane.setStyle(containingPaneStyle);
        
        initWidth[5] = 375;
        initHeight[5] = 725;
        noInt_QuadReg_PrintQuadStats_View = new OneParam_QuadReg_PrintQuadStats_View(noInt_QuadReg_Model, this, sixteenths_across[5], sixteenths_down[5], initWidth[5], initHeight[5]);
        noInt_QuadReg_PrintQuadStats_View.completeTheDeal();        
        printBivStatsContainingPane = noInt_QuadReg_PrintQuadStats_View.getTheContainingPane();  
        printBivStatsContainingPane.setStyle(containingPaneStyle);
        backGround.getChildren().addAll( pdfViewContainingPane,
                                         bestFitContainingPane, 
                                         residualsContainingPane,
                                         prntRegReportContainingPane,
                                         nppResidsContainingPane,
                                         printBivStatsContainingPane);          
    }
}

