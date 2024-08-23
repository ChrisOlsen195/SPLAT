/**************************************************
 *               Logistic_Dashboard               *
 *                    11/01/23                    *
 *                     00:00                      *
 *************************************************/
/**************************************************
*    Initial widths and heights from Super Class  *
*              are 400 and 350                    *
**************************************************/
package simpleLogisticRegression;

import superClasses.Dashboard;
import dataObjects.QuantitativeDataVariable;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import proceduresOneUnivariate.NormProb_Model;
import proceduresOneUnivariate.NormProb_View;

public class Logistic_Dashboard extends Dashboard {
    // POJOs
    
    // Make empty if no-print
    //String waldoFile = "Logistic_Dashboard";
    String waldoFile = "";    
 
    final String[] logisticCheckBoxDescr = { " Logistic Plot ", " Residuals ",
                                             "Standard Report", " NPP Residuals ", 
                                             "Diagnostic report"};
    
    // My classes
    //Data_Manager dm;
    Logistic_Resids_View logisticResids_View;
    Logistic_StandardReport_View logReg_StandardReport_View;
    Logistic_Diagnostic_Report_View logReg_Diagnostic_Report_View;
    LogisticReg_Model logistic_Model;     
    NormProb_Model normProb_Model;
    NormProb_View nppResids_View;   
    Logistic_View logistic_View;
    QuantitativeDataVariable qdv_Resids;

    // POJOs / FX
    Pane logisticViewContainingPane, logisticResidsContainingPane,
         logRegStandardReportContainingPane, nppResidsContainingPane, 
         logRegDiagnosticContainingPane; 
            
    public Logistic_Dashboard(Logistic_Controller logistic_Controller, LogisticReg_Model logistic_Model) {
        super(5);  
        dm = logistic_Controller.getDataManager();
        dm.whereIsWaldo(49, waldoFile, "Constructing");
        this.logistic_Model = logistic_Model;
        qdv_Resids = new QuantitativeDataVariable();
        qdv_Resids = logistic_Model.getQDVResids();
        normProb_Model = new NormProb_Model("Residuals", qdv_Resids);        
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
        
        if (checkBoxSettings[0] == true) {
            logisticViewContainingPane.setVisible(true);
            logistic_View.doTheGraph();
        }
        else { logisticViewContainingPane.setVisible(false); }

        if (checkBoxSettings[1] == true) {
            logisticResidsContainingPane.setVisible(true);
            logisticResids_View.doTheGraph();
        }
        else { logisticResidsContainingPane.setVisible(false);  }
        
        if (checkBoxSettings[2] == true) {
            logRegStandardReportContainingPane.setVisible(true);
        }
        else { logRegStandardReportContainingPane.setVisible(false);  }   

        if (checkBoxSettings[3] == true) {
            nppResidsContainingPane.setVisible(true);
            nppResids_View.doTheGraph();
        }
        else { nppResidsContainingPane.setVisible(false); } 
        
        if (checkBoxSettings[4] == true) {
            logRegDiagnosticContainingPane.setVisible(true);
        }
        else { logRegDiagnosticContainingPane.setVisible(false); 
        } 
    }
    
    public void populateTheBackGround() {
        initWidth[0] = 725;
        initHeight[0] = 650;        
        logReg_StandardReport_View = new Logistic_StandardReport_View(logistic_Model, this, sixteenths_across[0], sixteenths_down[0], initWidth[0], initHeight[0]);
        logReg_StandardReport_View.completeTheDeal();
        logRegStandardReportContainingPane = logReg_StandardReport_View.getTheContainingPane(); 
        logRegStandardReportContainingPane.setStyle(containingPaneStyle);

        initWidth[1] = 650;
        nppResids_View = new NormProb_View(normProb_Model, this, sixteenths_across[1], sixteenths_down[1], initWidth[1], initHeight[1]);
        nppResids_View.completeTheDeal();
        nppResidsContainingPane = nppResids_View.getTheContainingPane(); 
        nppResidsContainingPane.setStyle(containingPaneStyle);
   
        initWidth[2] = 650;
        logistic_View = new Logistic_View(logistic_Model, this, sixteenths_across[2], sixteenths_down[2], initWidth[2], initHeight[2]);
        logistic_View.completeTheDeal();
        logisticViewContainingPane = logistic_View.getTheContainingPane(); 
        logisticViewContainingPane.setStyle(containingPaneStyle);
        
        initWidth[3] = 650;
        logisticResids_View = new Logistic_Resids_View(logistic_Model, this, sixteenths_across[3], sixteenths_down[3], initWidth[3], initHeight[3]);
        logisticResids_View.completeTheDeal();        
        logisticResidsContainingPane = logisticResids_View.getTheContainingPane();  
        logisticResidsContainingPane.setStyle(containingPaneStyle);
        
        initWidth[4] = 750;
        logReg_Diagnostic_Report_View = new Logistic_Diagnostic_Report_View(logistic_Model, this, sixteenths_across[4], sixteenths_down[4], initWidth[4], initHeight[4]);
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
