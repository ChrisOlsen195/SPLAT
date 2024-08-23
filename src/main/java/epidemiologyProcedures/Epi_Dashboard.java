/**************************************************
*                  Epi_Dashboard                  *
*                     08/21/24                    *
*                      12:00                      *
**************************************************/
/**************************************************
*    Initial widths and heights from Super Class  *
*              are 400 and 350                    *
**************************************************/
package epidemiologyProcedures;

import superClasses.Dashboard;
import javafx.scene.paint.Color;
import javafx.scene.layout.Pane;

public class Epi_Dashboard extends Dashboard {
    // POJOs
    
    String assocType;
    
    String[] assocCheckBoxDescr = {"Contingency table", 
                                   "   Mosaic \n    Plot", 
                                   " Segmented \n bar chart",
                                   " Pie Chart ", "Risk Analysis (2 x 2)"};
    
    String waldoFile = "Epi_Dashboard";
    //String waldoFile = "";
    
    // My classes
   
    Epi_Model epi_Model;
    Epi_MosaicPlotView epi_MosaicPlotView;
    Epi_PieChartView epi_PieChartView;
    Epi_PrintStatistics epi_PrintAdvStats;
    Epi_SegBarChartView epi_SegBarChartView;
    
    Epi_View epi_View;

    // POJOs / FX
    
    Pane mosaicPlotContainingPane, segmentedBarChartContainingPane,
             assocPrintAdvStatsContainingPane, pieChartContainingPane,
             epidemiologyContainingPane; 
      
    public Epi_Dashboard(Epi_Controller bivCat_Controller, Epi_Model epi_Model) {
        super(5);
        System.out.println("\n47 BivCat_Dashboard, Constructing");
        setTitle("Epidemiology dashboard");  
        this.epi_Model = epi_Model;  
        assocType = epi_Model.getAssociationType();
        checkBoxDescr = new String[nCheckBoxes];
        
        for (int ithCheckBox = 0; ithCheckBox < nCheckBoxes; ithCheckBox++) {
            checkBoxDescr[ithCheckBox] = assocCheckBoxDescr[ithCheckBox];
            checkBoxes[ithCheckBox].setText(checkBoxDescr[ithCheckBox]);
            checkBoxes[ithCheckBox].setId(checkBoxDescr[ithCheckBox]);
            if (checkBoxes[ithCheckBox].isSelected() == true) 
                checkBoxes[ithCheckBox].setTextFill(Color.GREEN);
            else
                checkBoxes[ithCheckBox].setTextFill(Color.RED);
        }  
    }  
     
    public void putEmAllUp() { 
        if (checkBoxSettings[0] == true) {
            assocPrintAdvStatsContainingPane.setVisible(true);
        }
        else
            assocPrintAdvStatsContainingPane.setVisible(false);   

        if (checkBoxSettings[1] == true) {
            mosaicPlotContainingPane.setVisible(true);
            epi_MosaicPlotView.doThePlot();
        }
        else
            mosaicPlotContainingPane.setVisible(false);    

        if (checkBoxSettings[2] == true) {
            segmentedBarChartContainingPane.setVisible(true);
            epi_SegBarChartView.doThePlot();
        }
        else
            segmentedBarChartContainingPane.setVisible(false); 
       
        if (checkBoxSettings[3] == true) {
            pieChartContainingPane.setVisible(true);
            epi_PieChartView.doTheGraph();
        }
        else
            pieChartContainingPane.setVisible(false); 
        
        if ((checkBoxSettings[4] == true) && (assocType.equals("Epidemiology"))) {
            epidemiologyContainingPane.setVisible(true);
            epi_View.doThePlot();
        }
        else
            epidemiologyContainingPane.setVisible(false);
    }
    
    public void populateTheBackGround() {
        initWidth[0] = 475;
        initHeight[0] = 450;
        epi_PrintAdvStats = new Epi_PrintStatistics(epi_Model, this, sixteenths_across[0], sixteenths_down[0], initWidth[0], initHeight[0]);
        epi_PrintAdvStats.completeTheDeal();
        assocPrintAdvStatsContainingPane = epi_PrintAdvStats.getTheContainingPane();
        assocPrintAdvStatsContainingPane.setStyle(containingPaneStyle);

        initWidth[1] = 600;
        epi_MosaicPlotView = new Epi_MosaicPlotView(epi_Model, this, sixteenths_across[1], sixteenths_down[1], initWidth[1], initHeight[1]);
        epi_MosaicPlotView.completeTheDeal();
        mosaicPlotContainingPane = epi_MosaicPlotView.getTheContainingPane();
        
        mosaicPlotContainingPane.setStyle(containingPaneStyle);
        initWidth[2] = 450;
        epi_SegBarChartView = new Epi_SegBarChartView(epi_Model, this, sixteenths_across[2], sixteenths_down[2], initWidth[2], initHeight[2]);
        epi_SegBarChartView.completeTheDeal();        
        segmentedBarChartContainingPane = epi_SegBarChartView.getTheContainingPane(); 
        segmentedBarChartContainingPane.setStyle(containingPaneStyle);
        
        initHeight[3] = 525;
        epi_PieChartView = new Epi_PieChartView(epi_Model, this, sixteenths_across[3], sixteenths_down[3], initWidth[3], initHeight[3]);
        epi_PieChartView.completeTheDeal();        
        pieChartContainingPane = epi_PieChartView.getTheContainingPane();
        pieChartContainingPane.setStyle(containingPaneStyle);
        
        initWidth[4] = 700;
        initHeight[4] = 625;
        epi_View = new Epi_View(epi_Model, this, sixteenths_across[4], sixteenths_down[4], initWidth[4], initHeight[4]);
        
        if (assocType.equals("Epidemiology")) {
            epi_View.completeTheDeal();   
        }
        
        epidemiologyContainingPane = epi_View.getTheContainingPane();
        epidemiologyContainingPane.setStyle(containingPaneStyle);

        backGround.getChildren().addAll(assocPrintAdvStatsContainingPane,
                                        mosaicPlotContainingPane, 
                                        segmentedBarChartContainingPane,
                                        pieChartContainingPane,
                                        epidemiologyContainingPane
                                        );          
    }
}

