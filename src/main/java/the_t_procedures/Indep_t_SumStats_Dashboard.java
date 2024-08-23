/**************************************************
 *           Indep_t_SumStats_Dashboard           *
 *                    11/01/23                    *
 *                     00:00                      *
 *************************************************/
/**************************************************
*    Initial widths and heights from Super Class  *
*              are 400 and 350                    *
**************************************************/
package the_t_procedures;

import superClasses.Dashboard;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import splat.Data_Manager;

public class Indep_t_SumStats_Dashboard extends Dashboard {
    //  POJOs
    String waldoFile = "Indep_t_SumStats_Dashboard";
    //String waldoFile = "";
    String[] indepTCheckBoxDescr = { "t-test", "InferenceReport" };   
    
    // My classes
    Data_Manager dm;
    Indep_t_SumStats_Model indep_t_SumStats_Model; 
    Indep_t_SumStats_PDFView indep_t_PDF_SumStats_View;
    Indep_t_Inf_SumStats_Report_View indep_t_Inf_SumStats_Report_View;

    // POJOs / FX
    Pane indep_t_ContainingPane, inf_t_ReportContainingPane; 

    public Indep_t_SumStats_Dashboard(Indep_t_Controller indep_t_Controller) {
        super(2);   
        System.out.println("34 Indep_t_SumStats_Dashboard, constructing");
        dm = indep_t_Controller.getDataManager();
        indep_t_SumStats_Model = indep_t_Controller.getInd_t_SumStatsModel();
        checkBoxDescr = new String[nCheckBoxes];
        
        for (int ithCheckBox = 0; ithCheckBox < nCheckBoxes; ithCheckBox++) {
            checkBoxDescr[ithCheckBox] = indepTCheckBoxDescr[ithCheckBox];
            checkBoxes[ithCheckBox].setText(checkBoxDescr[ithCheckBox]);
            checkBoxes[ithCheckBox].setId(checkBoxDescr[ithCheckBox]);
            
            //  Shouldn't they all be unselected in the constructor???
            if (checkBoxes[ithCheckBox].isSelected() == true) {
                checkBoxes[ithCheckBox].setTextFill(Color.GREEN);
            }
            else { checkBoxes[ithCheckBox].setTextFill(Color.RED); }
        }
        setTitle("Independent t inference dashboard");  
    }  

    
    public void putEmAllUp() { 
        if (checkBoxSettings[0]) {
            indep_t_ContainingPane.setVisible(true);
            indep_t_PDF_SumStats_View.doTheGraph();
        }
        else { indep_t_ContainingPane.setVisible(false); }

        if (checkBoxSettings[1]) {
            inf_t_ReportContainingPane.setVisible(true);
        }
        else { inf_t_ReportContainingPane.setVisible(false);  }
    }
    
    public void populateTheBackGround() {       
        containingPaneStyle =  "-fx-background-color: white;" +
            "-fx-border-color: blue, blue;" + 
            "-fx-border-width: 4, 4;" +
            "fx-border-radius: 0, 0;" +
            "-fx-border-insets: -4, -4;" +
            "-fx-border-style: solid centered, solid centered;";
        
        initWidth[0] = 700;
        indep_t_PDF_SumStats_View = new Indep_t_SumStats_PDFView(indep_t_SumStats_Model, this, sixteenths_across[0], sixteenths_down[0], initWidth[0], initHeight[0]);
        indep_t_PDF_SumStats_View.completeTheDeal();        
        indep_t_ContainingPane = indep_t_PDF_SumStats_View.getTheContainingPane();  
        indep_t_ContainingPane.setStyle(containingPaneStyle);
        
        initHeight[1] = 650; 
        initWidth[1] = 700;
        indep_t_Inf_SumStats_Report_View = new Indep_t_Inf_SumStats_Report_View(indep_t_SumStats_Model, this, sixteenths_across[1], sixteenths_down[1], initWidth[1], initHeight[1]);
        indep_t_Inf_SumStats_Report_View.completeTheDeal();
        inf_t_ReportContainingPane = indep_t_Inf_SumStats_Report_View.getTheContainingPane(); 
        inf_t_ReportContainingPane.setStyle(containingPaneStyle);
        
        backGround.getChildren().addAll(indep_t_ContainingPane,
                                        inf_t_ReportContainingPane);          
    }
}

