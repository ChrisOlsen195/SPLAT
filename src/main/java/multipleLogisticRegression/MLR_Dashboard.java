/**************************************************
 *                 MLR_Dashboard                  *
 *                    10/15/23                    *
 *                     12:00                      *
 *************************************************/
/**************************************************
*    Initial widths and heights from Super Class  *
*              are 400 and 300                    *
**************************************************/
package multipleLogisticRegression;

import superClasses.Dashboard;
//import genericClasses.DragableAnchorPane;
//import dataObjects.QuantitativeDataVariable;
//import javafx.scene.control.CheckBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
//import proceduresOneUnivariate.NormProb_Model;
import proceduresOneUnivariate.NormProb_View;
//import splat.Data_Manager;

public class MLR_Dashboard extends Dashboard {
    // POJOs
    final String[] logisticCheckBoxDescr = { " Logistic Plot ", " Residuals ",
                                             "Standard Report", " NPP Residuals ", 
                                             "Diagnostic report"};
    String waldoFile = "BivariateContinDataObj";
    //String waldoFile = "";
    
    // My classes
    //Data_Manager dm;
    //DragableAnchorPane pdfViewDRAnchorPane, bestFitDRAnchorPane, 
    //                   logResidsDRAnchorPane, prntRegReportDRAnchorPane,
    //                   nppResidsDRAnchorPane, logRegDiagnosticDRAnchorPane; 
    
    MLR_StandardReport_View logReg_StandardReport_View;
    MLR_Diagnostic_Report_View logReg_Diagnostic_Report_View;
    MLR_Model mlr_Model;     
    //NormProb_Model normProb_Model;
    NormProb_View nppResids_View;
    //MLR_Controller logistic_Controller;    

    //QuantitativeDataVariable qdv_Resids;

    // POJOs / FX
    //CheckBox[] regrCheckBoxes;
    Pane logisticViewContainingPane, logisticResidsContainingPane,
         logRegStandardReportContainingPane, nppResidsContainingPane, 
         logRegDiagnosticContainingPane; 
            
    public MLR_Dashboard(MLR_Controller mlr_Controller, MLR_Model mlr_Model) {
        super(5);  
        //this.logistic_Controller = mlr_Controller;
        this.mlr_Model = mlr_Model;
        dm = mlr_Controller.getDataManager();
        dm.whereIsWaldo(56, waldoFile, "Constructing");        
        //qdv_Resids = new QuantitativeDataVariable();
        //qdv_Resids = mlr_Model.getQDVResids();
        //normProb_Model = new NormProb_Model("Residuals", qdv_Resids);
        
        checkBoxDescr = new String[nCheckBoxes];
        for (int ithCheckBox = 0; ithCheckBox < nCheckBoxes; ithCheckBox++) {
            checkBoxDescr[ithCheckBox] = logisticCheckBoxDescr[ithCheckBox];
            checkBoxes[ithCheckBox].setText(checkBoxDescr[ithCheckBox]);
            checkBoxes[ithCheckBox].setId(checkBoxDescr[ithCheckBox]);
            if (checkBoxes[ithCheckBox].isSelected() == true) 
                checkBoxes[ithCheckBox].setTextFill(Color.GREEN);
            else
                checkBoxes[ithCheckBox].setTextFill(Color.RED);
        }
        setTitle("Logistic regression dashboard"); 
    }  
    
    public void putEmAllUp() { 
        
        /*
        if (checkBoxSettings[0] == true) {
            logisticViewContainingPane.setVisible(true);
            logistic_View.doTheGraph();
        }
        else 
            logisticViewContainingPane.setVisible(false);  


        if (checkBoxSettings[1] == true) {
            logisticResidsContainingPane.setVisible(true);
            logisticResids_View.doTheGraph();
        }
        else {
            logisticResidsContainingPane.setVisible(false); 
        }
    */
        
        if (checkBoxSettings[2] == true) {
            logRegStandardReportContainingPane.setVisible(true);
        }
        else {
            logRegStandardReportContainingPane.setVisible(false); 
        }   

        if (checkBoxSettings[3] == true) {
            nppResidsContainingPane.setVisible(true);
            nppResids_View.doTheGraph();
        }
        else {
            nppResidsContainingPane.setVisible(false); 
        } 
        
        if (checkBoxSettings[4] == true) {
            logRegDiagnosticContainingPane.setVisible(true);
        }
        else {
            logRegDiagnosticContainingPane.setVisible(false); 
        } 
    }
    
    public void populateTheBackGround() {
        initWidth[0] = 700;
        initHeight[0] = 700;        
        logReg_StandardReport_View = new MLR_StandardReport_View(mlr_Model, this, sixteenths_across[0], sixteenths_down[0], initWidth[0], initHeight[0]);
        logReg_StandardReport_View.completeTheDeal();
        logRegStandardReportContainingPane = logReg_StandardReport_View.getTheContainingPane(); 
        logRegStandardReportContainingPane.setStyle(containingPaneStyle);

        /*
        initWidth[1] = 700;
        nppResids_View = new NormProb_View(normProb_Model, this, sixteenths_across[1], sixteenths_down[1], initWidth[1], initHeight[1]);
        nppResids_View.completeTheDeal();
        nppResidsContainingPane = nppResids_View.getTheContainingPane(); 
        nppResidsContainingPane.setStyle(containingPaneStyle);
   

        logistic_View = new Logistic_View(mlr_Model, this, sixteenths_across[2], sixteenths_down[2], initWidth[2], initHeight[2]);
        logistic_View.completeTheDeal();
        logisticViewContainingPane = logistic_View.getTheContainingPane(); 
        logisticViewContainingPane.setStyle(containingPaneStyle);
        
        logisticResids_View = new Logistic_Resids_View(mlr_Model, this, sixteenths_across[3], sixteenths_down[3], initWidth[3], initHeight[3]);
        logisticResids_View.completeTheDeal();        
        logisticResidsContainingPane = logisticResids_View.getTheContainingPane();  
        logisticResidsContainingPane.setStyle(containingPaneStyle);
        */
        
        logReg_Diagnostic_Report_View = new MLR_Diagnostic_Report_View(mlr_Model, this, sixteenths_across[4], sixteenths_down[4], initWidth[4], initHeight[4]);
        logReg_Diagnostic_Report_View.completeTheDeal();        
        logRegDiagnosticContainingPane = logReg_Diagnostic_Report_View.getTheContainingPane();  
        logRegDiagnosticContainingPane.setStyle(containingPaneStyle);

        
        backGround.getChildren().addAll(logisticViewContainingPane, 
                                         logisticResidsContainingPane,
                                         logRegStandardReportContainingPane,
                                         nppResidsContainingPane,
                                         logRegDiagnosticContainingPane
                                         );          
    }
}
