/**************************************************
 *             TwoProp_Inf_Dashboard              *
 *                    12/10/25                    *
 *                     18:00                      *
 *************************************************/
/**************************************************
*    Initial widths and heights from Super Class  *
*              are 400 and 300                    *
**************************************************/
package the_z_procedures;

import bivariateProcedures_Categorical.BivCat2x_Model;
import bivariateProcedures_Categorical.BivCat2x_MosaicPlotView;
import superClasses.Dashboard;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class TwoProp_Inf_Dashboard extends Dashboard {
    //  POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    String strConfidenceLevel;
    String[] twoProp_Inf_CheckBoxDescr;   
    
    // My classes
    BivCat2x_Model bivCat2x_Model;
    TwoProp_Inf_Model twoProp_Inf_Model;
    TwoProp_Inf_PDFView twoProp_Inf_PDF_View;
    TwoProp_Inf_Report_View twoProp_Inf_Report_View;
    DIffProp_Inf_CI_View oneProp_Inf_CI_View;
    TwoProp_Inf_CI_View twoProp_Inf_CI_View;
    BivCat2x_MosaicPlotView bivCat2x_MosaicPlotView;

    // POJOs / FX
    Pane twoProp_Inf_PDF_ContainingPane, twoProp_Inf_ReportContainingPane,
            oneProp_Inf_CI_ContainingPane, twoProp_Inf_CI_ContainingPane,
            twoProp_Inf_Mosaic_ContainingPane; 
      
    public TwoProp_Inf_Dashboard(TwoProp_Inf_Controller twoProp_Inf_Controller) {
        super(5);       
        if (printTheStuff) {
            System.out.println("*** 43 TwoProp_Inf_Dashboard, Constructing");
        }
        twoProp_Inf_Model = twoProp_Inf_Controller.getTwoPropModel();
        bivCat2x_Model = twoProp_Inf_Controller.getBivCat2xModel();
        checkBoxDescr = new String[nCheckBoxes];
        twoProp_Inf_CheckBoxDescr = new String[nCheckBoxes];
        twoProp_Inf_CheckBoxDescr[0] = "z-test";
        twoProp_Inf_CheckBoxDescr[1] = "TwoPropReport";
        strConfidenceLevel = String.valueOf(twoProp_Inf_Controller.getConfidenceLevel());
        twoProp_Inf_CheckBoxDescr[2] = strConfidenceLevel + "% CI for Difference";
        twoProp_Inf_CheckBoxDescr[3] = strConfidenceLevel + "% CI for Props";  
        twoProp_Inf_CheckBoxDescr[4] = "Mosaic Plot";
        
        for (int ithCheckBox = 0; ithCheckBox < nCheckBoxes; ithCheckBox++) {
            checkBoxDescr[ithCheckBox] = twoProp_Inf_CheckBoxDescr[ithCheckBox];
            checkBoxes[ithCheckBox].setText(checkBoxDescr[ithCheckBox]);
            checkBoxes[ithCheckBox].setId(checkBoxDescr[ithCheckBox]);
            checkBoxes[ithCheckBox].setSelected(false);
            if (checkBoxes[ithCheckBox].isSelected() == true) 
                checkBoxes[ithCheckBox].setTextFill(Color.GREEN);
            else
                checkBoxes[ithCheckBox].setTextFill(Color.RED);
        }
        setTitle("Difference between independent proportions dashboard");  
    }  

    
    public void putEmAllUp() { 
        if (printTheStuff) {
            System.out.println("*** 72 TwoProp_Inf_Dashboard, putEmAllUp()");
        }
        if (checkBoxSettings[0] == true) {
            twoProp_Inf_PDF_ContainingPane.setVisible(true);
            twoProp_Inf_PDF_View.doTheGraph();
        }
        else { twoProp_Inf_PDF_ContainingPane.setVisible(false); }
        
        if (checkBoxSettings[1] == true) {
            twoProp_Inf_ReportContainingPane.setVisible(true);
            twoProp_Inf_PDF_View.doTheGraph();
        }
        else { twoProp_Inf_ReportContainingPane.setVisible(false); }
        
        if (checkBoxSettings[2] == true) {
            oneProp_Inf_CI_ContainingPane.setVisible(true);
            oneProp_Inf_CI_View.doTheGraph();
        }
        else { oneProp_Inf_CI_ContainingPane.setVisible(false); }
        
        if (checkBoxSettings[3] == true) {
            twoProp_Inf_CI_ContainingPane.setVisible(true);
            twoProp_Inf_CI_View.doTheGraph();
        }
        else { twoProp_Inf_CI_ContainingPane.setVisible(false); }
        
        if (checkBoxSettings[4] == true) {
            twoProp_Inf_Mosaic_ContainingPane.setVisible(true);
            bivCat2x_MosaicPlotView.doTheGraph();
        }
        else { twoProp_Inf_Mosaic_ContainingPane.setVisible(false); }
    }
    
    public void populateTheBackGround() { 
        if (printTheStuff) {
            System.out.println("*** 106 TwoProp_Inf_Dashboard, populateTheBackGround()");
        }
        containingPaneStyle =  "-fx-background-color: white;" +
            "-fx-border-color: blue, blue;" + 
            "-fx-border-width: 4, 4;" +
            "fx-border-radius: 0, 0;" +
            "-fx-border-insets: -4, -4;" +
            "-fx-border-style: solid centered, solid centered;";
        
        initWidth[0] = 550;
        twoProp_Inf_PDF_View = new TwoProp_Inf_PDFView(twoProp_Inf_Model, this, sixteenths_across[0], sixteenths_down[0], initWidth[0], initHeight[0]);
        twoProp_Inf_PDF_View.completeTheDeal();        
        twoProp_Inf_PDF_ContainingPane = twoProp_Inf_PDF_View.getTheContainingPane();  
        twoProp_Inf_PDF_ContainingPane.setStyle(containingPaneStyle);
        
        initWidth[1] = 575;
        initHeight[1] = 450;
        twoProp_Inf_Report_View = new TwoProp_Inf_Report_View(twoProp_Inf_Model, this, sixteenths_across[1], sixteenths_down[1], initWidth[1], initHeight[1]);
        twoProp_Inf_Report_View.completeTheDeal();
        twoProp_Inf_ReportContainingPane = twoProp_Inf_Report_View.getTheContainingPane(); 
        twoProp_Inf_ReportContainingPane.setStyle(containingPaneStyle);
        
        initHeight[2] = 300;
        oneProp_Inf_CI_View = new DIffProp_Inf_CI_View(twoProp_Inf_Model, this, sixteenths_across[2], sixteenths_down[2], initWidth[2], initHeight[2]);
        oneProp_Inf_CI_View.completeTheDeal();        
        oneProp_Inf_CI_ContainingPane = oneProp_Inf_CI_View.getTheContainingPane();  
        oneProp_Inf_CI_ContainingPane.setStyle(containingPaneStyle);
        
        initWidth[3] = 550;
        initHeight[3] = 300;
        twoProp_Inf_CI_View = new TwoProp_Inf_CI_View(twoProp_Inf_Model, this, sixteenths_across[3], sixteenths_down[3], initWidth[3], initHeight[3]);
        twoProp_Inf_CI_View.completeTheDeal();        
        twoProp_Inf_CI_ContainingPane = twoProp_Inf_CI_View.getTheContainingPane();  
        twoProp_Inf_CI_ContainingPane.setStyle(containingPaneStyle);
        
        initWidth[4] = 800;
        initHeight[4] = 700;
        bivCat2x_MosaicPlotView = new BivCat2x_MosaicPlotView(bivCat2x_Model, this, sixteenths_across[4] - 150.0, sixteenths_down[4] - 150.0, initWidth[4], initHeight[4]);
        bivCat2x_MosaicPlotView.completeTheDeal();        
        twoProp_Inf_Mosaic_ContainingPane = bivCat2x_MosaicPlotView.getTheContainingPane();  
        twoProp_Inf_Mosaic_ContainingPane.setStyle(containingPaneStyle);

        backGround.getChildren().addAll(twoProp_Inf_PDF_ContainingPane,
                                        twoProp_Inf_ReportContainingPane,
                                        oneProp_Inf_CI_ContainingPane,
                                        twoProp_Inf_CI_ContainingPane,
                                        twoProp_Inf_Mosaic_ContainingPane);  
    }
}
