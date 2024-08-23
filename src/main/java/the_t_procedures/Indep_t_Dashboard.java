/**************************************************
 *               Ind_t_Dashboard                  *
 *                    11/15/23                    *
 *                     12:00                      *
 *************************************************/
/**************************************************
*    Initial widths and heights from Super Class  *
*              are 400 and 300                    *
**************************************************/
package the_t_procedures;

import proceduresManyUnivariate.VerticalBoxPlot_Model;
import proceduresManyUnivariate.HorizontalBoxPlot_Model;
import proceduresManyUnivariate.VerticalBoxPlot_View;
import proceduresManyUnivariate.HorizontalBoxPlot_View;
import superClasses.Dashboard;
import dataObjects.QuantitativeDataVariable;
import java.util.ArrayList;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import proceduresTwoUnivariate.*;
import splat.Data_Manager;

public class Indep_t_Dashboard extends Dashboard {
    //  POJOs
    String[] indepTCheckBoxDescr = { "t-test", "HBoxPlot", "VBoxPlot",
                                         "QQPlot", "BBSLPlot", "InferenceReport"
                                         }; 
    //String waldoFile = "Indep_t_Dashboard";
    String waldoFile = "";
    
    // My classes
    BBSL_Model bbsl_Model;
    BBSL_View bbsl_View; 
    HorizontalBoxPlot_Model hBox_Model;
    HorizontalBoxPlot_View hBox_View; 
    QuantitativeDataVariable pooledQDV;
    QQPlot_Model qqPlot_Model;
    QQPlot_View qqPlot_View;
    VerticalBoxPlot_Model vBox_Model;
    VerticalBoxPlot_View vBox_View;
    Indep_t_Model indep_t_Model;
    Indep_t_PDFView indep_t_PDF_View;
    Indep_t_Inf_Report_View inf_t_Report;

    // POJOs / FX
    Pane hBoxContainingPane, vBoxContainingPane,
         qqPlotContainingPane, bbslContainingPane,
         indep_t_ContainingPane, inf_t_ReportContainingPane; 

    public Indep_t_Dashboard(Indep_t_PrepStructs indep_t_prepStructs, 
            QuantitativeDataVariable pooledQDV,
            ArrayList<QuantitativeDataVariable> allTheQDVs) {
        super(6);       
        dm = indep_t_prepStructs.getDataManager();
        dm.whereIsWaldo(53, waldoFile, "constructing");
        hBox_Model = indep_t_prepStructs.getHBox_Model();
        //vBox_Model = new VerticalBoxPlot_Model();
        vBox_Model = indep_t_prepStructs.getVBox_Model();
        qqPlot_Model = new QQPlot_Model();
        qqPlot_Model = indep_t_prepStructs.getQQ_Model();
        bbsl_Model = new BBSL_Model(indep_t_prepStructs, pooledQDV, allTheQDVs);
        bbsl_Model = indep_t_prepStructs.getBBSL_Model();
        indep_t_Model = indep_t_prepStructs.getIndepTModel();
        
        checkBoxDescr = new String[nCheckBoxes];
        for (int ithCheckBox = 0; ithCheckBox < nCheckBoxes; ithCheckBox++) {
            checkBoxDescr[ithCheckBox] = indepTCheckBoxDescr[ithCheckBox];
            checkBoxes[ithCheckBox].setText(checkBoxDescr[ithCheckBox]);
            checkBoxes[ithCheckBox].setId(checkBoxDescr[ithCheckBox]);
            if (checkBoxes[ithCheckBox].isSelected() == true) {
                checkBoxes[ithCheckBox].setTextFill(Color.GREEN);
            }
            else {
                checkBoxes[ithCheckBox].setTextFill(Color.RED);
            }
        }
        setTitle("Independent t inference dashboard");    
    }  
    
    public void putEmAllUp() { 
        if (checkBoxSettings[0]) {
            indep_t_ContainingPane.setVisible(true);
            indep_t_PDF_View.doTheGraph();
        }
        else { indep_t_ContainingPane.setVisible(false);  }
        
        if (checkBoxSettings[1]) {
            hBoxContainingPane.setVisible(true);
            hBox_View.doTheGraph();
        }
        else  { hBoxContainingPane.setVisible(false); }
        
        if (checkBoxSettings[2]) {
            vBoxContainingPane.setVisible(true);
            vBox_View.doTheGraph();
        }
        else { vBoxContainingPane.setVisible(false);  }
        
        if (checkBoxSettings[3]) {
            qqPlotContainingPane.setVisible(true);
            qqPlot_View.doTheGraph();
        }
        else { qqPlotContainingPane.setVisible(false);  }

        if (checkBoxSettings[4]) { bbslContainingPane.setVisible(true); }
        else { bbslContainingPane.setVisible(false); } 
        
        if (checkBoxSettings[5]) {inf_t_ReportContainingPane.setVisible(true); }
        else { inf_t_ReportContainingPane.setVisible(false); }
    }
    
    public void populateTheBackGround() {       
        containingPaneStyle =  "-fx-background-color: white;" +
            "-fx-border-color: blue, blue;" + 
            "-fx-border-width: 4, 4;" +
            "fx-border-radius: 0, 0;" +
            "-fx-border-insets: -4, -4;" +
            "-fx-border-style: solid centered, solid centered;";
        
        initWidth[0] = 650;
        indep_t_PDF_View = new Indep_t_PDFView(indep_t_Model, this, sixteenths_across[0], sixteenths_down[0], initWidth[0], initHeight[0]);
        indep_t_PDF_View.completeTheDeal();        
        indep_t_ContainingPane = indep_t_PDF_View.getTheContainingPane();  
        indep_t_ContainingPane.setStyle(containingPaneStyle);
        
        initWidth[1] = 650;
        hBox_View = new HorizontalBoxPlot_View(hBox_Model, this, sixteenths_across[1], sixteenths_down[1], initWidth[1], initHeight[1]);
        hBox_View.completeTheDeal();
        hBoxContainingPane = hBox_View.getTheContainingPane(); 
        hBoxContainingPane.setStyle(containingPaneStyle);
        
        initWidth[2] = 650;
        vBox_View = new VerticalBoxPlot_View(vBox_Model, this, sixteenths_across[2], sixteenths_down[2], initWidth[2], initHeight[2]);
        vBox_View.completeTheDeal();
        vBoxContainingPane = vBox_View.getTheContainingPane(); 
        vBoxContainingPane.setStyle(containingPaneStyle);

        initWidth[3] = 650;
        qqPlot_View = new QQPlot_View(qqPlot_Model, this, sixteenths_across[3], sixteenths_down[3], initWidth[3], initHeight[3]);
        qqPlot_View.completeTheDeal();
        qqPlotContainingPane = qqPlot_View.getTheContainingPane(); 
        qqPlotContainingPane.setStyle(containingPaneStyle);
        
        initWidth[4] = 650;
        bbsl_View = new BBSL_View(bbsl_Model, this, sixteenths_across[4], sixteenths_down[4], initWidth[4], initHeight[4]);
        bbsl_View.completeTheDeal();        
        bbslContainingPane = bbsl_View.getTheContainingPane();  
        bbslContainingPane.setStyle(containingPaneStyle);
        
        initHeight[5] = 550; 
        initWidth[5] = 700;
        inf_t_Report = new Indep_t_Inf_Report_View(indep_t_Model, this, sixteenths_across[5], sixteenths_down[5], initWidth[5], initHeight[5]);
        inf_t_Report.completeTheDeal();
        inf_t_ReportContainingPane = inf_t_Report.getTheContainingPane(); 
        inf_t_ReportContainingPane.setStyle(containingPaneStyle);

        backGround.getChildren().addAll(indep_t_ContainingPane,
                                        hBoxContainingPane, 
                                        vBoxContainingPane,
                                        qqPlotContainingPane,
                                        bbslContainingPane,
                                        inf_t_ReportContainingPane);          
    }
    
    public Data_Manager getDataManager() { return dm; }
}
