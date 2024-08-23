/**************************************************
 *               Single_t_Dashboard               *
 *                    11/01/23                    *
 *                     12:00                      *
 *************************************************/
/**************************************************
*    Initial widths and heights from Super Class  *
*              are 400 and 350                    *
**************************************************/
package the_t_procedures;

import superClasses.Dashboard;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class Single_t_SumStats_Dashboard extends Dashboard {
    //  POJOs
    String[] indepTCheckBoxDescr = { "t-test", "InferenceReport"};   
    
    // My classes   
    Single_t_SumStats_Model single_t_SumStats_Model;
    Single_t_PDFView single_t_PDF_View;
    Single_t_Inf_Report_View single_t_Report;

    // POJOs / FX
    Pane single_t_SumStats_ContainingPane, infReportContainingPane; 
      
    public Single_t_SumStats_Dashboard(Single_t_SumStats_Controller single_t_sumStats_Controller) {
        super(2);       
        single_t_SumStats_Model = single_t_sumStats_Controller.getSingle_t_SumStats_Model();        
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
        setTitle("Single t inference dashboard");         
    }  
    
    public void putEmAllUp() { 
        if (checkBoxSettings[0] == true) {
            single_t_SumStats_ContainingPane.setVisible(true);
            single_t_PDF_View.doTheGraph();
        }
        else { single_t_SumStats_ContainingPane.setVisible(false); }
        
        if (checkBoxSettings[1] == true) {
            infReportContainingPane.setVisible(true);
        }
        else { infReportContainingPane.setVisible(false); }
    }
    
    public void populateTheBackGround() {       
        containingPaneStyle =  "-fx-background-color: white;" +
            "-fx-border-color: blue, blue;" + 
            "-fx-border-width: 4, 4;" +
            "fx-border-radius: 0, 0;" +
            "-fx-border-insets: -4, -4;" +
            "-fx-border-style: solid centered, solid centered;";
        
        initWidth[0] = 600;
        single_t_PDF_View = new Single_t_PDFView(single_t_SumStats_Model, this, sixteenths_across[0], sixteenths_down[0], initWidth[0], initHeight[0]);
        single_t_PDF_View.completeTheDeal();        
        single_t_SumStats_ContainingPane = single_t_PDF_View.getTheContainingPane();  
        single_t_SumStats_ContainingPane.setStyle(containingPaneStyle);
        
        initWidth[1] = 625;
        initHeight[1] = 400;
        single_t_Report = new Single_t_Inf_Report_View(single_t_SumStats_Model, this, sixteenths_across[1], sixteenths_down[1], initWidth[1], initHeight[1]);
        single_t_Report.completeTheDeal();
        infReportContainingPane = single_t_Report.getTheContainingPane(); 
        infReportContainingPane.setStyle(containingPaneStyle);
        
        backGround.getChildren().addAll(single_t_SumStats_ContainingPane,
                                        infReportContainingPane);          
    }
}
