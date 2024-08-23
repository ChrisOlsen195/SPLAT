/**************************************************
 *                ANCOVA_Dashboard                *
 *                    06/30/24                    *
 *                      09:00                     *
 *************************************************/
/**************************************************
*    Initial widths and heights from Super Class  *
*              are 400 and 350                    *
**************************************************/
package anova2;

import superClasses.Dashboard;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import proceduresManyUnivariate.HorizontalBoxPlot_Model;
import proceduresManyUnivariate.HorizontalBoxPlot_View;
import proceduresOneUnivariate.NormProb_Model;
import proceduresOneUnivariate.NormProb_View;

public class ANCOVA_Dashboard extends Dashboard {
    // POJOs
    final String[] regrCheckBoxDescr = { " Scatterplot ", " Residual plot ",
                                         "Norm Prob Plot", " ANCOVA Report ", 
                                         "Resids within"};
    
    // My classes
    ANCOVA_PrintReportView ancova_PrintReportView;
    ANCOVA_Model ancova_Model;   
    ANCOVA_BestFit_View ancova_BestFit_View;   
    ANCOVA_Residuals_View ancova_Residuals_View;
    NormProb_View normProb_View;
    NormProb_Model normProb_Model;
    HorizontalBoxPlot_View residsWithin_View;
    HorizontalBoxPlot_Model hBox_Model;

    // POJOs / FX
    Pane bestFitContainingPane, residualsContainingPane, normProbContainingPane,
         printANCOVAReportContainingPane, residsWithinContainingPane; 
            
    public ANCOVA_Dashboard(ANCOVA_Controller ancova_Controller, ANCOVA_Model ancova_Model) {
        super(5);
        this.ancova_Model = ancova_Model;
        normProb_Model = ancova_Controller.getNormProb_Model();
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
        setTitle("ANCOVA dashboard"); 
    }  
    
    public void putEmAllUp() {
        
        if (checkBoxSettings[0] == true) {  //  Scatterplot
            bestFitContainingPane.setVisible(true);
            ancova_BestFit_View.doTheGraph();
        } else { bestFitContainingPane.setVisible(false); }
        
        if (checkBoxSettings[1] == true) {  // Residual plot
            residualsContainingPane.setVisible(true);
            ancova_Residuals_View.doTheGraph();
        } else { residualsContainingPane.setVisible(false);  }
        
        if (checkBoxSettings[2] == true) {  //  Normal prob plot
            normProbContainingPane.setVisible(true);
            normProb_View.doTheGraph();
        } else { normProbContainingPane.setVisible(false);  } 

        if (checkBoxSettings[3] == true) {  // regr report
            printANCOVAReportContainingPane.setVisible(true);
        } else { printANCOVAReportContainingPane.setVisible(false); }    
        
        if (checkBoxSettings[4] == true) {  //  Resids within
            residsWithinContainingPane.setVisible(true);
        } else { residsWithinContainingPane.setVisible(false);  }
    }
    
    public void populateTheBackGround() {        
        initWidth[0] = 660;
        initHeight[0] = 400;
        ancova_BestFit_View = new ANCOVA_BestFit_View(ancova_Model, this, sixteenths_across[0], sixteenths_down[0], initWidth[0], initHeight[0]);
        ancova_BestFit_View.bfvCompleteTheDeal();
        bestFitContainingPane = ancova_BestFit_View.getTheContainingPane(); 
        bestFitContainingPane.setStyle(containingPaneStyle);
        
        initWidth[1] = 550;
        initHeight[1] = 350;
        normProb_View = new NormProb_View (normProb_Model, this, sixteenths_across[1], sixteenths_down[1], initWidth[1], initHeight[1]);
        normProb_View.completeTheDeal();        
        normProbContainingPane = normProb_View.getTheContainingPane();  
        normProbContainingPane.setStyle(containingPaneStyle);

        initWidth[2] = 660;
        initHeight[2] = 400;
        
        ancova_Residuals_View = new ANCOVA_Residuals_View(ancova_Model, this, sixteenths_across[2], sixteenths_down[2], initWidth[2], initHeight[2]);
        ancova_Residuals_View.completeTheDeal();        
        residualsContainingPane = ancova_Residuals_View.getTheContainingPane();  
        residualsContainingPane.setStyle(containingPaneStyle);
        
        initWidth[3] = 725;
        initHeight[3] = 600;
        ancova_PrintReportView = new ANCOVA_PrintReportView(ancova_Model, this, sixteenths_across[3], sixteenths_down[3], initWidth[3], initHeight[3]);
        ancova_PrintReportView.completeTheDeal();
        printANCOVAReportContainingPane = ancova_PrintReportView.getTheContainingPane(); 
        printANCOVAReportContainingPane.setStyle(containingPaneStyle);
        
        initWidth[4] = 750;
        initHeight[4] = 400;
        hBox_Model = new HorizontalBoxPlot_Model(ancova_Model.getAllTheQDVs());
        residsWithin_View = new HorizontalBoxPlot_View(hBox_Model, this, sixteenths_across[4], sixteenths_down[4], initWidth[4], initHeight[4]);
        residsWithin_View.completeTheDeal();        
        residsWithinContainingPane = residsWithin_View.getTheContainingPane();  
        residsWithinContainingPane.setStyle(containingPaneStyle);

        backGround.getChildren().addAll(bestFitContainingPane,
                                         residualsContainingPane,
                                         normProbContainingPane,
                                         printANCOVAReportContainingPane,
                                         residsWithinContainingPane);          
    }
}
