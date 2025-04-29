/**************************************************
 *           NoInf_Regression_Dashboard           *
 *                    01/16/25                    *
 *                     12:00                      *
 *************************************************/
/**************************************************
*    Initial widths and heights from Super Class  *
*              are 400 and 350                    *
**************************************************/
package simpleRegression;

import superClasses.Dashboard;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class NoInf_Regr_Dashboard extends Dashboard {
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    

    final String[] regrCheckBoxDescr = { " Scatterplot ", " Residual plot ",
                                         " RegrReport ", "StatSummary"};
    
    // My classes
    NoInf_PrintRegrReport_View prntRegReportView;
    NoInf_Regr_Model noInf_Regression_Model;   
    NoInf_Regr_BestFit_View noInf_Regr_BestFit_View;   
    NoInf_Regr_Residuals_View noInf_ResidualsView;
    NoInf_PrintBivStats_View noInf_PrintBivStats_View;

    // POJOs / FX
    Pane bestFitContainingPane, residualsContainingPane,
         prntRegReportContainingPane, printBivStatsContainingPane; 
            
    public NoInf_Regr_Dashboard(NoInf_Regr_Controller noInf_Regression_Controller, NoInf_Regr_Model noInf_Regression_Model) {
        super(4);
        if (printTheStuff == true) {
            System.out.println("39 *** NoInf_Regression_Dashboard, Constructing");
        }
        this.noInf_Regression_Model = noInf_Regression_Model;
        checkBoxDescr = new String[nCheckBoxes];
        for (int ithCheckBox = 0; ithCheckBox < nCheckBoxes; ithCheckBox++) {
            checkBoxDescr[ithCheckBox] = regrCheckBoxDescr[ithCheckBox];
            checkBoxes[ithCheckBox].setText(checkBoxDescr[ithCheckBox]);
            checkBoxes[ithCheckBox].setId(checkBoxDescr[ithCheckBox]);
            if (checkBoxes[ithCheckBox].isSelected()) 
                checkBoxes[ithCheckBox].setTextFill(Color.GREEN);
            else
                checkBoxes[ithCheckBox].setTextFill(Color.RED);
        }
        setTitle("Simple regression dashboard"); 
    }  
    
    public void putEmAllUp() {
        
        if (checkBoxSettings[0] == true) {
            bestFitContainingPane.setVisible(true);
            noInf_Regr_BestFit_View.doTheGraph();
        }
        else { bestFitContainingPane.setVisible(false); }
        
        if (checkBoxSettings[1] == true) {
            residualsContainingPane.setVisible(true);
            noInf_ResidualsView.doTheGraph();
        }
        else { residualsContainingPane.setVisible(false);  }
        
        if (checkBoxSettings[2] == true) {
            prntRegReportContainingPane.setVisible(true);
        }
        else { prntRegReportContainingPane.setVisible(false); }    
        
        if (checkBoxSettings[3] == true) {
            printBivStatsContainingPane.setVisible(true);
        }
        else { printBivStatsContainingPane.setVisible(false);  }
    }
    
    public void populateTheBackGround() {        
        initWidth[0] = 625;
        initHeight[0] = 350;
        prntRegReportView = new NoInf_PrintRegrReport_View(noInf_Regression_Model, this, sixteenths_across[0], sixteenths_down[0], initWidth[0], initHeight[0]);
        prntRegReportView.completeTheDeal();
        prntRegReportContainingPane = prntRegReportView.getTheContainingPane(); 
        prntRegReportContainingPane.setStyle(containingPaneStyle);
        
        initWidth[1] = 660;
        initHeight[1] = 400;
        noInf_Regr_BestFit_View = new NoInf_Regr_BestFit_View(noInf_Regression_Model, this, sixteenths_across[1], sixteenths_down[1], initWidth[1], initHeight[1]);
        noInf_Regr_BestFit_View.completeTheDeal();
        bestFitContainingPane = noInf_Regr_BestFit_View.getTheContainingPane(); 
        bestFitContainingPane.setStyle(containingPaneStyle);
        
        initWidth[2] = 660;
        initHeight[2] = 400;
        noInf_ResidualsView = new NoInf_Regr_Residuals_View(noInf_Regression_Model, this, sixteenths_across[2], sixteenths_down[2], initWidth[2], initHeight[2]);
        noInf_ResidualsView.completeTheDeal();        
        residualsContainingPane = noInf_ResidualsView.getTheContainingPane();  
        residualsContainingPane.setStyle(containingPaneStyle);
        
        initWidth[3] = 375;
        initHeight[3] = 675;
        noInf_PrintBivStats_View = new NoInf_PrintBivStats_View(noInf_Regression_Model, this, sixteenths_across[3], sixteenths_down[3], initWidth[3], initHeight[3]);
        noInf_PrintBivStats_View.completeTheDeal();        
        printBivStatsContainingPane = noInf_PrintBivStats_View.getTheContainingPane();  
        printBivStatsContainingPane.setStyle(containingPaneStyle);

        backGround.getChildren().addAll(
                                         bestFitContainingPane, 
                                         residualsContainingPane,
                                         prntRegReportContainingPane,
                                         printBivStatsContainingPane);          
    }
}