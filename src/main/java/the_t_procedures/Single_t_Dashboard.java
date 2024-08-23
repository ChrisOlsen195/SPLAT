/**************************************************
 *               Single_t_Dashboard               *
 *                    11/01/23                    *
 *                     21:00                      *
 *************************************************/
/**************************************************
*    Initial widths and heights from Super Class  *
*              are 400 and 350                    *
**************************************************/
package the_t_procedures;

import proceduresManyUnivariate.VerticalBoxPlot_Model;
import proceduresManyUnivariate.HorizontalBoxPlot_Model;
import proceduresOneUnivariate.StemNLeaf_View;
import proceduresOneUnivariate.StemNLeaf_Model;
import proceduresManyUnivariate.VerticalBoxPlot_View;
import proceduresManyUnivariate.HorizontalBoxPlot_View;
import superClasses.Dashboard;
import dataObjects.QuantitativeDataVariable;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import splat.Data_Manager;

public class Single_t_Dashboard extends Dashboard {
    //  POJOs
    String descriptionOfVariable;
    String[] singleTCheckBoxDescr = { "t-test", "HBoxPlot", "VBoxPlot",
                                         "StemPlot", "Inference Report"
                                         };    
    //String waldoFile = "Single_t_Dashboard";
    String waldoFile = "";
    
    // My classes
    Data_Manager dm;
    StemNLeaf_Model stemNLeaf_Model;
    StemNLeaf_View stemNLeaf_View;  
    HorizontalBoxPlot_Model hBox_Model;
    HorizontalBoxPlot_View hBox_View; 
    VerticalBoxPlot_Model vBox_Model;
    VerticalBoxPlot_View vBox_View;
    Single_t_Model single_t_Model;
    Single_t_PDFView single_t_PDF_View;
    Single_t_Inf_Report_View single_t_Report;

    // POJOs / FX
    Pane hBoxContainingPane, vBoxContainingPane,
         bbslContainingPane, single_t_ContainingPane,
         infReportContainingPane; 
       
    public Single_t_Dashboard(Single_t_Controller single_t_Controller, QuantitativeDataVariable theQDV) {
        super(5);       
        dm = single_t_Controller.getDataManager();
        dm.whereIsWaldo(53, waldoFile, "Constructing");
        descriptionOfVariable = single_t_Controller.getDescriptionOfVariable();
        returnStatus = "OK";
        hBox_Model = new HorizontalBoxPlot_Model(descriptionOfVariable, theQDV);
        vBox_Model = new VerticalBoxPlot_Model(descriptionOfVariable, theQDV);
        
        // ****************************************************************
        // *  The stemNLeaf_Model parameters are also supporting a back-  *
        // *  to-back stem and leaf plot.                                 *
        // ****************************************************************
        stemNLeaf_Model = new StemNLeaf_Model(descriptionOfVariable, theQDV, false, 0, 0, 0);
        single_t_Model = single_t_Controller.getSingle_T_Model();
        
        checkBoxDescr = new String[nCheckBoxes];
        
        for (int ithCheckBox = 0; ithCheckBox < nCheckBoxes; ithCheckBox++) {
            checkBoxDescr[ithCheckBox] = singleTCheckBoxDescr[ithCheckBox];
            checkBoxes[ithCheckBox].setText(checkBoxDescr[ithCheckBox]);
            checkBoxes[ithCheckBox].setId(checkBoxDescr[ithCheckBox]);
            if (checkBoxes[ithCheckBox].isSelected() == true) 
                checkBoxes[ithCheckBox].setTextFill(Color.GREEN);
            else
                checkBoxes[ithCheckBox].setTextFill(Color.RED);
        }
        
        setTitle("Single t inference dashboard");  
    }  

    
    public void putEmAllUp() { 
        if (checkBoxSettings[0] == true) {
            single_t_ContainingPane.setVisible(true);
            single_t_PDF_View.doTheGraph();
        }
        else { single_t_ContainingPane.setVisible(false);  }
        
        if (checkBoxSettings[1] == true) {
            hBoxContainingPane.setVisible(true);
            hBox_View.doTheGraph();
        }
        else { hBoxContainingPane.setVisible(false); }
        
        if (checkBoxSettings[2] == true) {
            vBoxContainingPane.setVisible(true);
            vBox_View.doTheGraph();
        }
        else { vBoxContainingPane.setVisible(false); }

        if (checkBoxSettings[3] == true) {
            bbslContainingPane.setVisible(true);
        }
        else {  bbslContainingPane.setVisible(false);  } 
        
        if (checkBoxSettings[4] == true) {
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
        single_t_PDF_View = new Single_t_PDFView(single_t_Model, this, sixteenths_across[0], sixteenths_down[0], initWidth[0], initHeight[0]);
        single_t_PDF_View.completeTheDeal();        
        single_t_ContainingPane = single_t_PDF_View.getTheContainingPane();  
        single_t_ContainingPane.setStyle(containingPaneStyle);
        
        initWidth[1] = 625;
        hBox_View = new HorizontalBoxPlot_View(hBox_Model, this, sixteenths_across[1], sixteenths_down[1], initWidth[1], initHeight[1]);
        hBox_View.completeTheDeal();
        hBoxContainingPane = hBox_View.getTheContainingPane(); 
        hBoxContainingPane.setStyle(containingPaneStyle);
        
        initWidth[2] = 625;
        vBox_View = new VerticalBoxPlot_View(vBox_Model, this, sixteenths_across[2], sixteenths_down[2], initWidth[2], initHeight[2]);
        vBox_View.completeTheDeal();
        vBoxContainingPane = vBox_View.getTheContainingPane(); 
        vBoxContainingPane.setStyle(containingPaneStyle);

        initWidth[3] = 600;
        stemNLeaf_View = new StemNLeaf_View(stemNLeaf_Model, this, sixteenths_across[3], sixteenths_down[3], initWidth[3], initHeight[3]);
        stemNLeaf_View.completeTheDeal();        
        bbslContainingPane = stemNLeaf_View.getTheContainingPane();  
        bbslContainingPane.setStyle(containingPaneStyle);
        
        initWidth[4] = 650;
        initHeight[4] = 400;
        single_t_Report = new Single_t_Inf_Report_View(single_t_Model, this, sixteenths_across[4], sixteenths_down[4], initWidth[4], initHeight[4]);
        single_t_Report.completeTheDeal();
        infReportContainingPane = single_t_Report.getTheContainingPane(); 
        infReportContainingPane.setStyle(containingPaneStyle);
        
        backGround.getChildren().addAll(single_t_ContainingPane,
                                        hBoxContainingPane, 
                                        vBoxContainingPane,
                                        bbslContainingPane,
                                        infReportContainingPane);          
    }
    
    public Data_Manager getDataManager() { return dm; }
}
