/**************************************************
 *             TwoProp_Inf_Dashboard              *
 *                    11/01/23                    *
 *                     21:00                      *
 *************************************************/
/**************************************************
*    Initial widths and heights from Super Class  *
*              are 400 and 300                    *
**************************************************/
package the_z_procedures;

import superClasses.Dashboard;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class TwoProp_Inf_Dashboard extends Dashboard {
    //  POJOs
    String strConfidenceLevel;
    String[] twoProp_Inf_CheckBoxDescr;   
    
    // My classes
    TwoProp_Inf_Model twoProp_Inf_Model;
    TwoProp_Inf_PDFView twoProp_Inf_PDF_View;
    TwoProp_Inf_Report_View twoProp_Inf_Report_View;
    DIffProp_Inf_CI_View oneProp_Inf_CI_View;
    TwoProp_Inf_CI_View twoProp_Inf_CI_View;

    // POJOs / FX
    Pane twoProp_Inf_PDF_ContainingPane, twoProp_Inf_ReportContainingPane,
            oneProp_Inf_CI_ContainingPane, twoProp_Inf_CI_ContainingPane; 
      
    public TwoProp_Inf_Dashboard(TwoProp_Inf_Controller twoProp_Inf_Controller) {
        super(4);       
        //System.out.println("34 TwoProp_Inf_Dashboard, constructing");
        twoProp_Inf_Model = twoProp_Inf_Controller.getTwoPropModel();
        checkBoxDescr = new String[nCheckBoxes];
        twoProp_Inf_CheckBoxDescr = new String[nCheckBoxes];
        twoProp_Inf_CheckBoxDescr[0] = "z-test";
        twoProp_Inf_CheckBoxDescr[1] = "TwoPropReport";
        strConfidenceLevel = String.valueOf(twoProp_Inf_Controller.getConfidenceLevel());
        twoProp_Inf_CheckBoxDescr[2] = strConfidenceLevel + "% CI for Difference";
        twoProp_Inf_CheckBoxDescr[3] = strConfidenceLevel + "% CI for Props";        

        for (int ithCheckBox = 0; ithCheckBox < nCheckBoxes; ithCheckBox++) {
            checkBoxDescr[ithCheckBox] = twoProp_Inf_CheckBoxDescr[ithCheckBox];
            checkBoxes[ithCheckBox].setText(checkBoxDescr[ithCheckBox]);
            checkBoxes[ithCheckBox].setId(checkBoxDescr[ithCheckBox]);
            
            if (checkBoxes[ithCheckBox].isSelected() == true) 
                checkBoxes[ithCheckBox].setTextFill(Color.GREEN);
            else
                checkBoxes[ithCheckBox].setTextFill(Color.RED);
        }
        setTitle("Difference between independent proportions dashboard");  
    }  

    
    public void putEmAllUp() { 
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
    }
    
    public void populateTheBackGround() {       
        containingPaneStyle =  "-fx-background-color: white;" +
            "-fx-border-color: blue, blue;" + 
            "-fx-border-width: 4, 4;" +
            "fx-border-radius: 0, 0;" +
            "-fx-border-insets: -4, -4;" +
            "-fx-border-style: solid centered, solid centered;";
        
        twoProp_Inf_PDF_View = new TwoProp_Inf_PDFView(twoProp_Inf_Model, this, sixteenths_across[0], sixteenths_down[0], initWidth[0], initHeight[0]);
        twoProp_Inf_PDF_View.completeTheDeal();        
        twoProp_Inf_PDF_ContainingPane = twoProp_Inf_PDF_View.getTheContainingPane();  
        twoProp_Inf_PDF_ContainingPane.setStyle(containingPaneStyle);
        
        initWidth[1] = 650;
        initHeight[1] = 475;
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

        backGround.getChildren().addAll(twoProp_Inf_PDF_ContainingPane,
                                        twoProp_Inf_ReportContainingPane,
                                        oneProp_Inf_CI_ContainingPane,
                                        twoProp_Inf_CI_ContainingPane);  
    }
}
