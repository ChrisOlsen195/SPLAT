/**************************************************
 *              Inf_Regr_Dashboard                *
 *                    06/06/26                    *
 *                     12:00                      *
 *************************************************/
/**************************************************
*    Initial widths and heights from Super Class  *
*              are 400 and 350                    *
**************************************************/
package simpleRegression;

import proceduresOneUnivariate.NormProb_Model;
import proceduresOneUnivariate.NormProb_View;
import dataObjects.QuantitativeDataVariable;
import dialogs.power.Power_LinReg_Dialog;
import javafx.event.EventHandler;
import superClasses.Dashboard;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.WindowEvent;
import power_LinReg.LinReg_Power_Controller;

public class Inf_Regr_Dashboard extends Dashboard {
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    String subTitle;
    final String[] regrCheckBoxDescr = { " Model Utility Test",
                                         " Scatterplot ", " Residual plot ",
                                         " RegrReport ", " DiagReport ",
                                         "NPP Residuals", "StatSummary",
                                         "Power"};
    
    //String waldoFile = "Regr_Dashboard";
    String waldoFile = "";
    
    // My classes

    Inf_Regr_BestFit_View bestFitView;  
    PrintDiagReport_View prntDiagReportView; 
    PrintRegrReport_View prntRegReportView;
    Inf_Regr_Model inf_Regression_Model;   
    NormProb_Model normProb_Model;
    Regr_PDFView regression_PDFView;    
    Inf_Regr_Residuals_View residualsView;
    NormProb_View nppResidsView;
    PrintBivStats_View printBivStats_View;
    QuantitativeDataVariable qdv_Resids;
    LinReg_Power_Controller linReg_Power_Controller;


    // POJOs / FX
    Pane pdfViewContainingPane, bestFitContainingPane, residualsContainingPane,
         prntRegReportContainingPane, prntDiagReportContainingPane,
         nppResidsContainingPane, printBivStatsContainingPane; 
            
    public Inf_Regr_Dashboard(Inf_Regr_Controller inf_Regression_Controller, Inf_Regr_Model inf_Regression_Model) {
        super(8);
        if (printTheStuff) {
            System.out.println("61 *** Inf_Regr_Dashboard, Constructing");
        }
        dm = inf_Regression_Controller.getDataManager();
        dm.whereIsWaldo(64, waldoFile, "Constructing");
        this.inf_Regression_Model = inf_Regression_Model;
        linReg_Power_Controller = new LinReg_Power_Controller(this, inf_Regression_Controller);
        qdv_Resids = new QuantitativeDataVariable();
        qdv_Resids = inf_Regression_Model.getQDVResids();
        subTitle = inf_Regression_Controller.getSubTitle();
        normProb_Model = new NormProb_Model(subTitle, qdv_Resids);
        checkBoxDescr = new String[nCheckBoxes];
        
        addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, new EventHandler<WindowEvent>() {
            public void handle(WindowEvent event) {
                restoreNumberSeven();
                Power_LinReg_Dialog power_LinReg_Dialog = linReg_Power_Controller.get_Power_LinReg_Dialog();
                power_LinReg_Dialog.fireEvent(new WindowEvent(power_LinReg_Dialog, WindowEvent.WINDOW_CLOSE_REQUEST));
                close();
            }
        });
        
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
        if (printTheStuff) {
            System.out.println("96 --- Inf_Regr_Dashboard, putEmAllUp()");
        }
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
            linReg_Power_Controller.ShowNWait();
            checkBoxes[7].setSelected(false);
            checkBoxes[7].setTextFill(Color.RED);
            checkBoxSettings[7] = false;
        }
        else { }
    }
    
    public void populateTheBackGround() {
        if (printTheStuff) {
            System.out.println("146 --- Inf_Regr_Dashboard, populateTheBackGround()");
        }
        initWidth[0] = 450;
        initHeight[0] = 300;
        regression_PDFView = new Regr_PDFView(inf_Regression_Model, this, sixteenths_across[0], sixteenths_down[0], initWidth[0], initHeight[0]);
        regression_PDFView.completeTheDeal();
        pdfViewContainingPane = regression_PDFView.getTheContainingPane(); 
        pdfViewContainingPane.setStyle(containingPaneStyle);
        
        initWidth[1] = 700;
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
        bestFitView = new Inf_Regr_BestFit_View(inf_Regression_Model, this, sixteenths_across[3], sixteenths_down[3], initWidth[3], initHeight[3]);
        bestFitView.completeTheDeal();
        bestFitContainingPane = bestFitView.getTheContainingPane(); 
        bestFitContainingPane.setStyle(containingPaneStyle);
        
        initWidth[4] = 650;
        initHeight[4] = 350;
        residualsView = new Inf_Regr_Residuals_View(inf_Regression_Model, this, sixteenths_across[4], sixteenths_down[4], initWidth[4], initHeight[4]);
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
        initHeight[6] = 600;
        printBivStats_View = new PrintBivStats_View(inf_Regression_Model, this, sixteenths_across[6], sixteenths_down[6] - 150, initWidth[6], initHeight[6]);
        printBivStats_View.completeTheDeal();        
        printBivStatsContainingPane = printBivStats_View.getTheContainingPane();  
        printBivStatsContainingPane.setStyle(containingPaneStyle);
        
        backGround.getChildren().addAll( pdfViewContainingPane,
                                         bestFitContainingPane, 
                                         residualsContainingPane,
                                         prntRegReportContainingPane,
                                         prntDiagReportContainingPane,
                                         nppResidsContainingPane,
                                         printBivStatsContainingPane);         
    }
    
    public Inf_Regr_Dashboard get_Inf_Regr_Dashboard() { return this; }
    
    public void restoreNumberSeven() {
        if (printTheStuff) {
            System.out.println("210 Inf_Regr_Dashboard, restoreNumberSeven()");
        }
        checkBoxes[7].setSelected(false);
        checkBoxes[7].setTextFill(Color.RED);
        checkBoxSettings[7] = false;
    }
}