/**************************************************
 *            NoIntercept_Regr_Dashboard          *
 *                    11/01/23                    *
 *                     18:00                      *
 *************************************************/
/**************************************************
*    Initial widths and heights from Super Class  *
*              are 400 and 350                    *
**************************************************/
package noInterceptRegression;

import proceduresOneUnivariate.NormProb_Model;
import proceduresOneUnivariate.NormProb_View;
import dataObjects.QuantitativeDataVariable;
import superClasses.Dashboard;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class NoIntercept_Regr_Dashboard extends Dashboard {
    // POJOs
    String subTitle;
    final String[] regrCheckBoxDescr = { " Model Utility Test",
                                         " Scatterplot ", " Residual plot ",
                                         " RegrReport ",
                                         "NPP Residuals", "StatSummary"};
    
    //String waldoFile = "NoInt_Regr_Dashboard";
    String waldoFile = "";
    
    // My classes
    NoIntercept_Regr_BestFit_View bestFitView; 
    NoIntercept_Regr_PrintRegrReport_View prntRegReportView;
    NoIntercept_Regr_Model noInt_Regr_Model;   
    NormProb_Model normProb_Model;
    NoIntercept_Regr_PDFView regression_PDFView;  
    NoIntercept_Regr_Residuals_View noInt_Regr_Residuals_View;
    NormProb_View normProb_View;
    NoIntercept_Regr_PrintBivStats_View noInt_Regr_PrintBivStats_View;
    QuantitativeDataVariable qdv_Resids;

    // POJOs / FX
    Pane pdfViewContainingPane, bestFitContainingPane, residualsContainingPane,
         prntRegReportContainingPane,
         nppResidsContainingPane, printBivStatsContainingPane; 
            
    public NoIntercept_Regr_Dashboard(NoIntercept_Regr_Controller noInt_Regr_Controller, NoIntercept_Regr_Model noInt_Regr_Model) {
        super(6);  
        this.noInt_Regr_Model = noInt_Regr_Model;
        dm = noInt_Regr_Controller.getDataManager();
        //dm.whereIsWaldo(50, waldoFile, "Constructing");
        qdv_Resids = new QuantitativeDataVariable();
        qdv_Resids = noInt_Regr_Model.getQDVResids();
        subTitle = noInt_Regr_Controller.getSubTitle();
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
        setTitle("No intercept regression dashboard"); 
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
        else { bestFitContainingPane.setVisible(false); }
        
        if (checkBoxSettings[2] == true) {
            residualsContainingPane.setVisible(true);
            noInt_Regr_Residuals_View.doTheGraph();
        }
        else { residualsContainingPane.setVisible(false); }
        
        if (checkBoxSettings[3] == true) {
            prntRegReportContainingPane.setVisible(true);
        }
        else { prntRegReportContainingPane.setVisible(false);  }
        
        if (checkBoxSettings[4] == true) {
            nppResidsContainingPane.setVisible(true);
            normProb_View.doTheGraph();
        }
        else { nppResidsContainingPane.setVisible(false);  }  
        
        if (checkBoxSettings[5] == true) {
            printBivStatsContainingPane.setVisible(true);
        }
        else { printBivStatsContainingPane.setVisible(false); }
    }
    
    public void populateTheBackGround() {
        initWidth[0] = 550;
        regression_PDFView = new NoIntercept_Regr_PDFView(noInt_Regr_Model, this, sixteenths_across[0], sixteenths_down[0], initWidth[0], initHeight[0]);
        regression_PDFView.completeTheDeal();
        pdfViewContainingPane = regression_PDFView.getTheContainingPane(); 
        pdfViewContainingPane.setStyle(containingPaneStyle);
        
        initWidth[1] = 625;
        initHeight[1] = 625;
        prntRegReportView = new NoIntercept_Regr_PrintRegrReport_View(noInt_Regr_Model, this, sixteenths_across[1], sixteenths_down[1], initWidth[1], initHeight[1]);
        prntRegReportView.completeTheDeal();
        prntRegReportContainingPane = prntRegReportView.getTheContainingPane(); 
        prntRegReportContainingPane.setStyle(containingPaneStyle);
        
        initWidth[2] = 600;
        initHeight[2] = 400;
        bestFitView = new NoIntercept_Regr_BestFit_View(noInt_Regr_Model, this, sixteenths_across[2], sixteenths_down[2], initWidth[2], initHeight[2]);
        bestFitView.completeTheDeal();
        bestFitContainingPane = bestFitView.getTheContainingPane(); 
        bestFitContainingPane.setStyle(containingPaneStyle);
        
        initWidth[3] = 600;
        initHeight[3] = 400;
        noInt_Regr_Residuals_View = new NoIntercept_Regr_Residuals_View(noInt_Regr_Model, this, sixteenths_across[3], sixteenths_down[3], initWidth[3], initHeight[3]);
        noInt_Regr_Residuals_View.completeTheDeal();        
        residualsContainingPane = noInt_Regr_Residuals_View.getTheContainingPane();  
        residualsContainingPane.setStyle(containingPaneStyle);
        
        initWidth[4] = 550;
        normProb_View = new NormProb_View(normProb_Model, this, sixteenths_across[4], sixteenths_down[4], initWidth[4], initHeight[4]);
        normProb_View.completeTheDeal();        
        nppResidsContainingPane = normProb_View.getTheContainingPane();  
        nppResidsContainingPane.setStyle(containingPaneStyle);
        
        initWidth[5] = 375;
        initHeight[5] = 675;
        noInt_Regr_PrintBivStats_View = new NoIntercept_Regr_PrintBivStats_View(noInt_Regr_Model, this, sixteenths_across[5] + 200, sixteenths_down[2], initWidth[5], initHeight[5]);
        noInt_Regr_PrintBivStats_View.completeTheDeal();        
        printBivStatsContainingPane = noInt_Regr_PrintBivStats_View.getTheContainingPane();  
        printBivStatsContainingPane.setStyle(containingPaneStyle);

        backGround.getChildren().addAll( pdfViewContainingPane,
                                         bestFitContainingPane, 
                                         residualsContainingPane,
                                         prntRegReportContainingPane,
                                         nppResidsContainingPane,
                                         printBivStatsContainingPane);          
    }
}
