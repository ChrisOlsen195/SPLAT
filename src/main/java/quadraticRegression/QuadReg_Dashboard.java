/**************************************************
 *                QuadReg_Dashboard               *
 *                    11/03/23                    *
 *                     15:00                      *
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

public class QuadReg_Dashboard extends Dashboard {
    // POJOs
    String subTitle;
    final String[] regrCheckBoxDescr = { " Model Utility Test",
                                         " Scatterplot ", " Residual plot ",
                                         " RegrReport ", 
                                         "NPP Residuals", "StatSummary"};
    
    //String waldoFile = "QuadReg_Dashboard";
    String waldoFile = "";
    
    // My classes
    QuadReg_BestFit_View bestFitView; 
    PrintQuadRegReport_View printQuadRegReport_View;
    QuadReg_Model quadReg_Model;   
    NormProb_Model normProb_Model;
    QuadReg_PDFView regression_PDFView;  
    QuadReg_Residuals_View quadReg_Residuals_View;
    NormProb_View nppResidsView;
    PrintQuadRegStats_View printQuadRegStats_View;
    QuantitativeDataVariable qdv_Resids;

    // POJOs / FX
    Pane pdfViewContainingPane, bestFitContainingPane, residPlotContainingPane,
         printQuadRegReportContainingPane, nppResidsContainingPane, 
         printQuadRegStatsContainingPane; 
            
    public QuadReg_Dashboard(QuadReg_Controller quadReg_Controller, QuadReg_Model quadReg_Model) {
        super(6);
        dm = quadReg_Controller.getDataManager();
        dm.whereIsWaldo(49, waldoFile, "Constructing");
        this.quadReg_Model = quadReg_Model;
        qdv_Resids = new QuantitativeDataVariable();
        qdv_Resids = quadReg_Model.getQDVResids();
        subTitle = quadReg_Controller.getSubTitle();
        normProb_Model = new NormProb_Model(subTitle, qdv_Resids);
        checkBoxDescr = new String[nCheckBoxes];
        
        for (int ithCheckBox = 0; ithCheckBox < nCheckBoxes; ithCheckBox++) {
            checkBoxDescr[ithCheckBox] = regrCheckBoxDescr[ithCheckBox];
            checkBoxes[ithCheckBox].setText(checkBoxDescr[ithCheckBox]);
            checkBoxes[ithCheckBox].setId(checkBoxDescr[ithCheckBox]);
            if (checkBoxes[ithCheckBox].isSelected() == true) 
                checkBoxes[ithCheckBox].setTextFill(Color.GREEN);
            else { checkBoxes[ithCheckBox].setTextFill(Color.RED); }
        }
        setTitle("Quadratic regression dashboard"); 
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
        else { bestFitContainingPane.setVisible(false);   }
        if (checkBoxSettings[2] == true) {
            residPlotContainingPane.setVisible(true);
            quadReg_Residuals_View.doTheGraph();
        }
        else { residPlotContainingPane.setVisible(false);  }
        
        if (checkBoxSettings[3] == true) {
            printQuadRegReportContainingPane.setVisible(true);
        }
        else {  printQuadRegReportContainingPane.setVisible(false); } 
        
        if (checkBoxSettings[4] == true) {
            nppResidsContainingPane.setVisible(true);
            nppResidsView.doTheGraph();
        }
        else { nppResidsContainingPane.setVisible(false);  }  
        
        if (checkBoxSettings[5] == true) {
            printQuadRegStatsContainingPane.setVisible(true);
        }
        else { printQuadRegStatsContainingPane.setVisible(false);  }
    }
    
    public void populateTheBackGround() {
        
        regression_PDFView = new QuadReg_PDFView(quadReg_Model, this, sixteenths_across[0], sixteenths_down[0], initWidth[0], initHeight[0]);
        regression_PDFView.completeTheDeal();
        pdfViewContainingPane = regression_PDFView.getTheContainingPane(); 
        pdfViewContainingPane.setStyle(containingPaneStyle);
        
        initWidth[1] = 675;
        initHeight[1] = 450;
        bestFitView = new QuadReg_BestFit_View(quadReg_Model, this, sixteenths_across[1], sixteenths_down[1], initWidth[1], initHeight[1]);
        bestFitView.completeTheDeal();
        bestFitContainingPane = bestFitView.getTheContainingPane(); 
        bestFitContainingPane.setStyle(containingPaneStyle);
        
        initWidth[2] = 675;
        initHeight[2] = 500;
        printQuadRegReport_View = new PrintQuadRegReport_View(quadReg_Model, this, sixteenths_across[2], sixteenths_down[2], initWidth[2], initHeight[2]);
        printQuadRegReport_View.completeTheDeal();
        printQuadRegReportContainingPane = printQuadRegReport_View.getTheContainingPane(); 
        printQuadRegReportContainingPane.setStyle(containingPaneStyle);
        
        initWidth[3] = 675;
        initHeight[3] = 400;
        quadReg_Residuals_View = new QuadReg_Residuals_View(quadReg_Model, this, sixteenths_across[3], sixteenths_down[3], initWidth[3], initHeight[3]);
        quadReg_Residuals_View.completeTheDeal();        
        residPlotContainingPane = quadReg_Residuals_View.getTheContainingPane();  
        residPlotContainingPane.setStyle(containingPaneStyle);
        
        initWidth[3] = 775;
        initHeight[3] = 400;
        nppResidsView = new NormProb_View(normProb_Model, this, sixteenths_across[4], sixteenths_down[4], initWidth[4], initHeight[4]);
        nppResidsView.completeTheDeal();        
        nppResidsContainingPane = nppResidsView.getTheContainingPane();  
        nppResidsContainingPane.setStyle(containingPaneStyle);
        
        initWidth[5] = 675;
        initHeight[5] = 775;
        printQuadRegStats_View = new PrintQuadRegStats_View(quadReg_Model, this, sixteenths_across[5], sixteenths_down[5], initWidth[5], initHeight[5]);
        printQuadRegStats_View.completeTheDeal();        
        printQuadRegStatsContainingPane = printQuadRegStats_View.getTheContainingPane();  
        printQuadRegStatsContainingPane.setStyle(containingPaneStyle);

        backGround.getChildren().addAll(pdfViewContainingPane,
                                         bestFitContainingPane, 
                                         residPlotContainingPane,
                                         printQuadRegReportContainingPane,
                                         nppResidsContainingPane,
                                         printQuadRegStatsContainingPane);          
    }
}
