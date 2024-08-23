/**************************************************
 *             OneInf_Prop_Dashboard              *
 *                    11/21/23                    *
 *                     15:00                      *
 *************************************************/
/**************************************************
*    Initial widths and heights from Super Class  *
*              are 400 and 300                    *
**************************************************/
package the_z_procedures;

import superClasses.Dashboard;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class OneProp_Inf_Dashboard extends Dashboard {
    //  POJOs   
    String strConfidenceLevel;
    String[] indepTCheckBoxDescr; 
    
    // My classes
    OneProp_Inf_Model oneProp_Inf_Model;
    OneProp_Inf_PDFView oneProp_Inf_PDF_View;
    OneProp_Exact_PDFView oneProp_Exact_PDF_View;
    OneProp_Inf_Report_View oneProp_Inf_Report_View;
    OneProp_Inf_CI_View oneProp_Inf_CI_View;

    // POJOs / FX
    Pane oneProp_Inf_PDF_ContainingPane, oneProp_Exact_PDF_ContainingPane,
         oneProp_Inf_CI_ContainingPane, oneProp_Inf_ReportContainingPane; 

    public OneProp_Inf_Dashboard(OneProp_Inf_Controller oneProp_Inf_Controller) {
        super(4);       
        //System.out.println("34 OneProp_Inf_Dashboard, constructing");
        oneProp_Inf_Model = oneProp_Inf_Controller.getOnePropModel();
        indepTCheckBoxDescr = new String[nCheckBoxes];
        indepTCheckBoxDescr[0] = "Normal approximation";
        indepTCheckBoxDescr[1] = "OnePropReport";
        strConfidenceLevel = String.valueOf(oneProp_Inf_Controller.getConfidenceLevel());
        indepTCheckBoxDescr[2] = strConfidenceLevel + "% Conf Int";
        indepTCheckBoxDescr[3] = "Exact test";
        checkBoxDescr = new String[nCheckBoxes];
        
        for (int ithCheckBox = 0; ithCheckBox < nCheckBoxes; ithCheckBox++) {
            checkBoxDescr[ithCheckBox] = indepTCheckBoxDescr[ithCheckBox];
            checkBoxes[ithCheckBox].setText(checkBoxDescr[ithCheckBox]);
            checkBoxes[ithCheckBox].setId(checkBoxDescr[ithCheckBox]);
            
            if (checkBoxes[ithCheckBox].isSelected() == true) {
                checkBoxes[ithCheckBox].setTextFill(Color.GREEN);
            }
            else { checkBoxes[ithCheckBox].setTextFill(Color.RED); }
        }
        setTitle("Single proportion inference dashboard");          
    }  

    
    public void putEmAllUp() { 
        if (checkBoxSettings[0] == true) {
            oneProp_Inf_PDF_ContainingPane.setVisible(true);
            oneProp_Inf_PDF_View.doTheGraph();
        }
        else { oneProp_Inf_PDF_ContainingPane.setVisible(false);  }
        
        if (checkBoxSettings[1] == true) {
            oneProp_Inf_ReportContainingPane.setVisible(true);
            oneProp_Inf_PDF_View.doTheGraph();
        }
        else { oneProp_Inf_ReportContainingPane.setVisible(false);   }
        
        if (checkBoxSettings[2] == true) {
            oneProp_Inf_CI_ContainingPane.setVisible(true);
            oneProp_Inf_CI_View.doTheGraph();
        }
        else { oneProp_Inf_CI_ContainingPane.setVisible(false); }
        if (checkBoxSettings[3] == true) {
            oneProp_Exact_PDF_ContainingPane.setVisible(true);
            oneProp_Exact_PDF_View.doTheGraph();
        }
        else { oneProp_Exact_PDF_ContainingPane.setVisible(false); }
    }
    
    public void populateTheBackGround() {       
        containingPaneStyle =  "-fx-background-color: white;" +
            "-fx-border-color: blue, blue;" + 
            "-fx-border-width: 4, 4;" +
            "fx-border-radius: 0, 0;" +
            "-fx-border-insets: -4, -4;" +
            "-fx-border-style: solid centered, solid centered;";
        
        oneProp_Inf_PDF_View = new OneProp_Inf_PDFView(oneProp_Inf_Model, this, sixteenths_across[0], sixteenths_down[0], initWidth[0], initHeight[0]);
        oneProp_Inf_PDF_View.completeTheDeal();        
        oneProp_Inf_PDF_ContainingPane = oneProp_Inf_PDF_View.getTheContainingPane();  
        oneProp_Inf_PDF_ContainingPane.setStyle(containingPaneStyle);
        
        initWidth[1] = 500;
        initHeight[1] = 400;
        oneProp_Inf_Report_View = new OneProp_Inf_Report_View(oneProp_Inf_Model, this, sixteenths_across[1], sixteenths_down[1], initWidth[1], initHeight[1]);
        oneProp_Inf_Report_View.completeTheDeal();
        oneProp_Inf_ReportContainingPane = oneProp_Inf_Report_View.getTheContainingPane(); 
        oneProp_Inf_ReportContainingPane.setStyle(containingPaneStyle);
        
        oneProp_Inf_CI_View = new OneProp_Inf_CI_View(oneProp_Inf_Model, this, sixteenths_across[2], sixteenths_down[2], initWidth[2], initHeight[2]);
        oneProp_Inf_CI_View.completeTheDeal();        
        oneProp_Inf_CI_ContainingPane = oneProp_Inf_CI_View.getTheContainingPane();  
        oneProp_Inf_CI_ContainingPane.setStyle(containingPaneStyle);
        
        oneProp_Exact_PDF_View = new OneProp_Exact_PDFView(oneProp_Inf_Model, this, sixteenths_across[2], sixteenths_down[2], initWidth[2], initHeight[2]);
        oneProp_Exact_PDF_View.completeTheDeal();        
        oneProp_Exact_PDF_ContainingPane = oneProp_Exact_PDF_View.getTheContainingPane();  
        oneProp_Exact_PDF_ContainingPane.setStyle(containingPaneStyle);
        
        backGround.getChildren().addAll(oneProp_Inf_PDF_ContainingPane,
                                        oneProp_Inf_ReportContainingPane,
                                        oneProp_Inf_CI_ContainingPane,
                                        oneProp_Exact_PDF_ContainingPane);  

    }
}
