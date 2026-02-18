/**************************************************
*                BivCat_Dashboard                 *
*                     12/11/25                    *
*                      21:00                      *
**************************************************/
/**************************************************
*    Initial widths and heights from Super Class  *
*              are 400 and 350                    *
**************************************************/
package bivariateProcedures_Categorical;

import superClasses.Dashboard;
import javafx.scene.paint.Color;
import javafx.scene.layout.Pane;

public class BivCat_Dashboard extends Dashboard {
    // POJOs
    
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    String assocType;
    
    String[] assocCheckBoxDescr = {"Contingency table", 
                                   "   Mosaic \n    Plot", 
                                   " Segmented \n bar chart",
                                   " Comparative bar chart",
                                   " Pie Chart "};
    
    //String waldoFile = "BivCat_Dashboard";
    String waldoFile = "";
    
    // My classes
   
    BivCat_Model bivCat_Model;
    BivCat_MosaicPlotView bivCat_MosaicPlotView;
    BivCat_PieChartView bivCat_PieChartView;
    BivCat_PrintStatistics bivCat_PrintStatistics;
    BivCat_SegBarChartView bivCat_SegBarChartView;
    BivCat_BoxPlotView bivCat_BoxPlotView;

    // POJOs / FX
    
    Pane mosaicPlotContainingPane, segmentedBarChartContainingPane,
             printStatisticsContainingPane, pieChartContainingPane,
             bivCat_ComparativeContainingPane; 
      
    public BivCat_Dashboard(BivCat_Controller bivCat_Controller, BivCat_Model bivCat_Model) {
        super(5);
        //System.out.println(*** 50 BivCat_Dashboard, Constructing");
        setTitle("Bivariate Categorical Association dashboard");  
        this.bivCat_Model = bivCat_Model;  
        assocType = bivCat_Model.getAssociationType();
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
            printStatisticsContainingPane.setVisible(true);
        }
        else
            printStatisticsContainingPane.setVisible(false);   

        if (checkBoxSettings[1] == true) {
            mosaicPlotContainingPane.setVisible(true);
            bivCat_MosaicPlotView.doTheGraph();
        }
        else
            mosaicPlotContainingPane.setVisible(false);    

        if (checkBoxSettings[2] == true) {
            segmentedBarChartContainingPane.setVisible(true);
            bivCat_SegBarChartView.doThePlot();
        }
        else
            segmentedBarChartContainingPane.setVisible(false); 
        
        if (checkBoxSettings[3] == true) {
            bivCat_ComparativeContainingPane.setVisible(true);
            bivCat_BoxPlotView.doThePlot();
        }
        else
            bivCat_ComparativeContainingPane.setVisible(false); 

       
        if (checkBoxSettings[4] == true) {
            pieChartContainingPane.setVisible(true);
            bivCat_PieChartView.doTheGraph();
        }
        else
            pieChartContainingPane.setVisible(false); 
    }
    
    public void populateTheBackGround() {
        initHeight[0] = 450;
        bivCat_PrintStatistics = new BivCat_PrintStatistics(bivCat_Model, this, sixteenths_across[0], sixteenths_down[0], initWidth[0], initHeight[0]);
        bivCat_PrintStatistics.completeTheDeal();
        printStatisticsContainingPane = bivCat_PrintStatistics.getTheContainingPane();
        printStatisticsContainingPane.setStyle(containingPaneStyle);
        
        initWidth[1] = 800;
        initHeight[1] = 700;
              
        bivCat_MosaicPlotView = new BivCat_MosaicPlotView(bivCat_Model, this, sixteenths_across[1], sixteenths_down[1], initWidth[1], initHeight[1]);
        bivCat_MosaicPlotView.completeTheDeal();
        mosaicPlotContainingPane = bivCat_MosaicPlotView.getTheContainingPane();
        mosaicPlotContainingPane.setStyle(containingPaneStyle);
        
        initWidth[2] = 550;
        initHeight[2] = 475;
        bivCat_SegBarChartView = new BivCat_SegBarChartView(bivCat_Model, this, sixteenths_across[2], sixteenths_down[2], initWidth[2], initHeight[2]);
        bivCat_SegBarChartView.completeTheDeal();        
        segmentedBarChartContainingPane = bivCat_SegBarChartView.getTheContainingPane(); 
        segmentedBarChartContainingPane.setStyle(containingPaneStyle);
        
        initWidth[3] = 550;
        initHeight[3] = 450;
        bivCat_BoxPlotView = new BivCat_BoxPlotView(bivCat_Model, this, sixteenths_across[3], sixteenths_down[3], initWidth[3], initHeight[3]);
        bivCat_BoxPlotView.completeTheDeal();        
        bivCat_ComparativeContainingPane = bivCat_BoxPlotView.getTheContainingPane(); 
        bivCat_ComparativeContainingPane.setStyle(containingPaneStyle);
        
        initWidth[4] = 600;
        initHeight[4] = 525;
        bivCat_PieChartView = new BivCat_PieChartView(bivCat_Model, this, sixteenths_across[4], sixteenths_down[4], initWidth[4], initHeight[4]);
        bivCat_PieChartView.completeTheDeal();        
        pieChartContainingPane = bivCat_PieChartView.getTheContainingPane();
        pieChartContainingPane.setStyle(containingPaneStyle);

        backGround.getChildren().addAll(printStatisticsContainingPane,
                                        mosaicPlotContainingPane, 
                                        segmentedBarChartContainingPane,
                                        bivCat_ComparativeContainingPane,
                                        pieChartContainingPane
                                        );          
    }
}
