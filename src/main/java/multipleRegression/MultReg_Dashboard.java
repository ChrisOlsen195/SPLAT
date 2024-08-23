/**************************************************
 *            MultRegression_Dashboard            *
 *                    05/27/24                    *
 *                     15:00                      *
 *************************************************/
/**************************************************
*    Initial widths and heights from Super Class  *
*              are 400 and 350                    *
**************************************************/
package multipleRegression;

import proceduresOneUnivariate.NormProb_Model;
import proceduresOneUnivariate.NormProb_View;
import dataObjects.QuantitativeDataVariable;
import superClasses.Dashboard;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class MultReg_Dashboard extends Dashboard {
    // POJOs
    
    final String[] regrCheckBoxDescr = { " Mult Reg Report ", " Mult Reg Diagnostics ",
                                         " Cooks Distance ", " Residuals vs Fit ",
                                         " Normal Resids "};
    
    //String returnStatus, theYVariable;
    //String waldoFile = "MultReg_Dashboard";
    String waldoFile = "";
    
    // My classes
    MultReg_CooksDist_View cooksDistView;      
    Print_MultReg_DiagReport_View prntMultDiagReportView; 
    PrintMultRegrReport_View prntMultRegReportView;
    MultReg_Model multRegression_Model;         
    MultReg_ResidsVsFit_View residsVsFit_View;
    NormProb_Model normalResids_Model;
    NormProb_View  normalResids_View;

    Pane cooksDistContainingPane, residsVsFitContainingPane,
         prntMultRegReportContainingPane, prntMultDiagReportContainingPane,
         normalResidsContainingPane; 
            
    public MultReg_Dashboard(MultReg_Controller multReg_Controller, MultReg_Model multReg_Model) {
        super(5);  // nCheckBoxes = 5;
        this.multRegression_Model = multReg_Model;
        dm = multReg_Model.getDataManager();
        dm.whereIsWaldo(47, waldoFile, "Constructing");
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
        setTitle("Multiple regression dashboard"); 
    }  
    
    public void putEmAllUp() { 
        if (checkBoxSettings[0] == true) {
            prntMultRegReportContainingPane.setVisible(true);
        }
        else
            prntMultRegReportContainingPane.setVisible(false);
        
        if (checkBoxSettings[1] == true) {
            prntMultDiagReportContainingPane.setVisible(true);
        }
        else 
            prntMultDiagReportContainingPane.setVisible(false); 
        
        if (checkBoxSettings[2] == true) {

            cooksDistContainingPane.setVisible(true);
            cooksDistView.doTheGraph();
        }
        else {
            cooksDistContainingPane.setVisible(false);
        }
        
        if (checkBoxSettings[3] == true) {
            
            residsVsFitContainingPane.setVisible(true);
            residsVsFit_View.doTheGraph();
        }
        else {
            residsVsFitContainingPane.setVisible(false); 
        }
        
        if (checkBoxSettings[4] == true) {
            
            normalResidsContainingPane.setVisible(true);
            normalResids_View.doTheGraph();
        }
        else {
            normalResidsContainingPane.setVisible(false); 
        }
    }
    
    public void populateTheBackGround() {
        initHeight[0] = 675;
        initWidth[0] = 700;
        prntMultRegReportView = new PrintMultRegrReport_View(multRegression_Model, this, sixteenths_across[0], sixteenths_down[0], initWidth[0], initHeight[0]);
        prntMultRegReportView.completeTheDeal();
        prntMultRegReportContainingPane = prntMultRegReportView.getTheContainingPane(); 
        prntMultRegReportContainingPane.setStyle(containingPaneStyle);
        
        initWidth[1] = 700;
        prntMultDiagReportView = new Print_MultReg_DiagReport_View(multRegression_Model, this, sixteenths_across[1], sixteenths_down[1], initWidth[1], initHeight[1]);
        prntMultDiagReportView.completeTheDeal();
        prntMultDiagReportContainingPane = prntMultDiagReportView.getTheContainingPane(); 
        prntMultDiagReportContainingPane.setStyle(containingPaneStyle);
        
        initWidth[2] = 600;
        cooksDistView = new MultReg_CooksDist_View(multRegression_Model, this, sixteenths_across[2], sixteenths_down[2], initWidth[2], initHeight[2]);
        cooksDistView.completeTheDeal();
        cooksDistContainingPane = cooksDistView.getTheContainingPane(); 
        cooksDistContainingPane.setStyle(containingPaneStyle);
        
        initWidth[3] = 600;
        residsVsFit_View = new MultReg_ResidsVsFit_View(multRegression_Model, this, sixteenths_across[3], sixteenths_down[3], initWidth[3], initHeight[3]);
        residsVsFit_View.completeTheDeal();        
        residsVsFitContainingPane = residsVsFit_View.getTheContainingPane();  
        residsVsFitContainingPane.setStyle(containingPaneStyle);

        // Convert for normalResids_Model
        initWidth[4] = 600;
        QuantitativeDataVariable qdv_Resids = new QuantitativeDataVariable(dm, "Residuals", "Residuals", multRegression_Model.getR_StudentizedResids());
        normalResids_Model = new NormProb_Model("Residuals", qdv_Resids);        
        normalResids_View = new NormProb_View(normalResids_Model, this, sixteenths_across[4], sixteenths_down[4], initWidth[4], initHeight[4]);
        normalResids_View.completeTheDeal();        
        normalResidsContainingPane = normalResids_View.getTheContainingPane();  
        normalResidsContainingPane.setStyle(containingPaneStyle);
        
        backGround.getChildren().addAll(prntMultRegReportContainingPane,
                                         prntMultDiagReportContainingPane,
                                         cooksDistContainingPane, 
                                         residsVsFitContainingPane,
                                         normalResidsContainingPane);   
    }
}
