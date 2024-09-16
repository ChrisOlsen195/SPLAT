/**************************************************
 *               MultUni_Dashboard                *
 *                    09/03/24                    *
 *                     00:00                      *
 *************************************************/
/**************************************************
*    Initial widths and heights from Super Class  *
*              are 400 and 300                    *
**************************************************/
package proceduresManyUnivariate;

import superClasses.Dashboard;
import dataObjects.QuantitativeDataVariable;
import java.util.ArrayList;
import javafx.scene.paint.Color;
import javafx.scene.layout.Pane;
import splat.Data_Manager;

public class MultUni_Dashboard extends Dashboard {
    // POJOs   
    int nVariables;
    
    // Make empty if no-print
    //String waldoFile = "MultUni_Dashboard";
    String waldoFile = "";
    
    private String varDescr, responseVariable;
    private final String[] anova1CheckBoxDescr = {" Vert BoxPlot ", 
                                                  " Horiz BoxPlot",
                                                  " Circle Plot ",
                                                  " Mean and Error Bars ", 
                                                  " Dot Plot ",
                                                  " Print Stats "};

    // My classes
    Data_Manager dm;
    private MultUni_Model multUni_Model;
    private MultUni_DotPlotModel multUni_DotPlotModel;
    private ArrayList<QuantitativeDataVariable> allTheQDVs;
    private HorizontalBoxPlot_Model horizontalBoxPlot_Model;
    private HorizontalBoxPlot_View horizontalBoxPlot_View;
    private VerticalBoxPlot_View verticalBoxPlot_View;
    private MultUni_CirclePlotView circlePlotView;
    private MultUni_MeanAndErrorView meanAndBarsView; 
    private MultUni_DotPlotView multUni_DotPlotView; 
    private MultUni_PrintReportView multUni_PrintReportView;
    private VerticalBoxPlot_Model verticalBoxPlot_Model;
    
    // POJOs / FX

    Pane vertBoxPlotContainingPane, horizBoxPlotContainingPane, 
         circlePlotContainingPane,  meanAndBarsContainingPane, 
         dotPlotContainingPane, printReportContainingPane;
            
    public MultUni_Dashboard(MultUni_Controller multUni_Controller, MultUni_Model multUni_Model) {
        super(6);
        dm = multUni_Controller.getDataManager();
        dm.whereIsWaldo(58, waldoFile, "\nMultUni_Dashboard, constructing");
        this.multUni_Model = multUni_Model;
        multUni_DotPlotModel = multUni_Controller.getMultUni_DotPlotModel();
        varDescr = multUni_Controller.getVarDescr();
        checkBoxDescr = new String[nCheckBoxes];
        for (int ithCheckBox = 0; ithCheckBox < nCheckBoxes; ithCheckBox++) {
            checkBoxDescr[ithCheckBox] = anova1CheckBoxDescr[ithCheckBox];
            checkBoxes[ithCheckBox].setText(checkBoxDescr[ithCheckBox]);
            checkBoxes[ithCheckBox].setId(checkBoxDescr[ithCheckBox]);
            if (checkBoxes[ithCheckBox].isSelected() == true) 
                checkBoxes[ithCheckBox].setTextFill(Color.GREEN);
            else { checkBoxes[ithCheckBox].setTextFill(Color.RED); }
        }  
        
        setTitle("Comparing distributions dashboard");
        nVariables = multUni_Model.getNVariables();
    }  
    
    public void putEmAllUp() { 
        dm.whereIsWaldo(77, waldoFile, "MultUni_Dashboard, putEmAllUp()");
        if (checkBoxSettings[0] == true) {
            vertBoxPlotContainingPane.setVisible(true);
            verticalBoxPlot_View.doTheGraph();
        }
        else { vertBoxPlotContainingPane.setVisible(false); }
        
        if (checkBoxSettings[1] == true) {
            horizBoxPlotContainingPane.setVisible(true);
            horizontalBoxPlot_View.doTheGraph();
        }
        else { horizBoxPlotContainingPane.setVisible(false); }
        
        if (checkBoxSettings[2] == true) {
            circlePlotContainingPane.setVisible(true);
            circlePlotView.doTheGraph();
        }
        else { circlePlotContainingPane.setVisible(false);  }
        
        if (checkBoxSettings[3] == true) {
            meanAndBarsContainingPane.setVisible(true);
            meanAndBarsView.doTheGraph();
        }
        else { meanAndBarsContainingPane.setVisible(false);  }
        
        if (checkBoxSettings[4] == true) {
            dotPlotContainingPane.setVisible(true);
        }
        else { dotPlotContainingPane.setVisible(false); } 
        
        if (checkBoxSettings[5] == true) {
            printReportContainingPane.setVisible(true);
        }
        else { printReportContainingPane.setVisible(false); } 
    }
    
    public void populateTheBackGround() {
        dm.whereIsWaldo(114, waldoFile, "MultUni_Dashboard, populateTheBackGround()");
        initWidth[0] = 625;    
        allTheQDVs = new ArrayList<>();
        allTheQDVs = multUni_Model.getAllQDVs();
        verticalBoxPlot_Model = new VerticalBoxPlot_Model(multUni_Model, allTheQDVs);
        verticalBoxPlot_View = new VerticalBoxPlot_View(verticalBoxPlot_Model, this, sixteenths_across[0], sixteenths_down[0], initWidth[0], initHeight[0]);
        verticalBoxPlot_View.completeTheDeal();
        vertBoxPlotContainingPane = verticalBoxPlot_View.getTheContainingPane(); 
        
        initWidth[1] = 675;    
        initHeight[1] = 450.;
        allTheQDVs = new ArrayList<>();
        allTheQDVs = multUni_Model.getAllQDVs();
        horizontalBoxPlot_Model = new HorizontalBoxPlot_Model(multUni_Model, allTheQDVs);
        horizontalBoxPlot_View = new HorizontalBoxPlot_View(horizontalBoxPlot_Model, this, sixteenths_across[1], sixteenths_down[1], initWidth[1], initHeight[1]);
        horizontalBoxPlot_View.completeTheDeal();
        horizBoxPlotContainingPane = horizontalBoxPlot_View.getTheContainingPane(); 
        
        initWidth[2] = 625; 
        circlePlotView = new MultUni_CirclePlotView(multUni_Model, this, sixteenths_across[2], sixteenths_down[2], initWidth[2], initHeight[2]);
        circlePlotView.completeTheDeal();        
        circlePlotContainingPane = circlePlotView.getTheContainingPane();  

        initWidth[3] = 650;
        meanAndBarsView = new MultUni_MeanAndErrorView(multUni_Model, this, sixteenths_across[3], sixteenths_down[3], initWidth[3], initHeight[3]);
        meanAndBarsView.completeTheDeal();
        meanAndBarsContainingPane = meanAndBarsView.getTheContainingPane();

        initWidth[4] = 175. * nVariables;
        initHeight[4] = 400.;
        multUni_DotPlotView = new MultUni_DotPlotView(multUni_DotPlotModel, this, sixteenths_across[4], sixteenths_down[4], initWidth[4], initHeight[4]);
        multUni_DotPlotView.completeTheDeal();
        dotPlotContainingPane = multUni_DotPlotView.getTheContainingPane();
        multUni_DotPlotView.doTheGraph();
        
        // "Override
        initWidth[5] = 290. * nVariables;
        initHeight[5] = 625.;
        multUni_PrintReportView = new MultUni_PrintReportView(multUni_Model, this, sixteenths_across[5], sixteenths_down[5], initWidth[5], initHeight[5]);
        multUni_PrintReportView.completeTheDeal();
        printReportContainingPane = multUni_PrintReportView.getTheContainingPane(); 

        backGround.getChildren().addAll(vertBoxPlotContainingPane, 
                                        horizBoxPlotContainingPane,
                                        circlePlotContainingPane,
                                        meanAndBarsContainingPane,
                                        dotPlotContainingPane,
                                        printReportContainingPane);  
    }
    
    public String getVarDescr() { return varDescr; }
    public String getResponseVar() { return responseVariable; }
}
