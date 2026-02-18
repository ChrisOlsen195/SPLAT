/**************************************************
*                X2Assoc_Dashboard                *
*                     01/15/25                    *
*                      12:00                      *
**************************************************/
/**************************************************
*    Initial widths and heights from Super Class  *
*              are 400 and 350                    *
**************************************************/
package chiSquare_Assoc;

import chiSquare.X2_PDFView;
import superClasses.Dashboard;
import javafx.scene.paint.Color;
import javafx.scene.layout.Pane;

public class X2Assoc_Dashboard extends Dashboard {
    // POJOs

    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    String[] assocCheckBoxDescr = {" Chi square \n (Inference)", 
                                   "Print Statistics \n      (basic)", 
                                   "Print Statistics\n   (advanced)", 
                                   "   Mosaic \n    Plot", 
                                   " Segmented \n bar chart",
                                   " Pie Chart "};
    // My classes
    X2_PDFView x2PDFView; 
    X2Assoc_Model x2assoc_Model;
    X2Assoc_MosaicPlotView x2Assoc_MosaicPlotView;
    X2Assoc_PieChartView x2Assoc_PieChartView;
    X2Assoc_PrintAdvStats x2Assoc_PrintAdvStats;
    X2Assoc_PrintStats x2assoc_PrintStats;
    X2Assoc_SegBarChartView x2Assoc_SegBarChartView;

    // POJOs / FX
    Pane pdfViewContainingPane, mosaicPlotContainingPane, segmentedBarChartContainingPane,
             assocPrintStatsContainingPane, assocPrintAdvStatsContainingPane,
             pieChartContainingPane; 
      
    public X2Assoc_Dashboard(X2Assoc_Controller x2Assoc_Controller, X2Assoc_Model x2assoc_Model) {
        super(6);
        if (printTheStuff == true) {
            System.out.println("46 *** X2Assoc_Dashboard, Constructing");
        }
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
        setTitle("ChiSquare Association dashboard");  
        this.x2assoc_Model = x2assoc_Model;   
    }  
     
    public void putEmAllUp() { 
        if (checkBoxSettings[0] == true) {
            pdfViewContainingPane.setVisible(true);
            x2PDFView.doTheGraph();
        }
        else { pdfViewContainingPane.setVisible(false);  }
        
        if (checkBoxSettings[1] == true) {
            assocPrintStatsContainingPane.setVisible(true);
        }
        else { assocPrintStatsContainingPane.setVisible(false); }
        
        if (checkBoxSettings[2] == true) {
            assocPrintAdvStatsContainingPane.setVisible(true);
        }
        else { assocPrintAdvStatsContainingPane.setVisible(false); }
        
        if (checkBoxSettings[3] == true) {
            mosaicPlotContainingPane.setVisible(true);
            x2Assoc_MosaicPlotView.doThePlot();
        }
        else {  mosaicPlotContainingPane.setVisible(false); }
        
        if (checkBoxSettings[4] == true) {
            segmentedBarChartContainingPane.setVisible(true);
            x2Assoc_SegBarChartView.doThePlot();
        }
        else { segmentedBarChartContainingPane.setVisible(false); }
        
        if (checkBoxSettings[5] == true) {
            pieChartContainingPane.setVisible(true);
            x2Assoc_PieChartView.doTheGraph();
        }
        else { pieChartContainingPane.setVisible(false);  }
    }
    
    public void populateTheBackGround() {
        x2PDFView = new X2_PDFView(x2assoc_Model, this, sixteenths_across[0], sixteenths_down[0], initWidth[0], initHeight[0]);
        x2PDFView.completeTheDeal();
        pdfViewContainingPane = x2PDFView.getTheContainingPane();
        pdfViewContainingPane.setStyle(containingPaneStyle);

        initWidth[1] = 700;
        x2assoc_PrintStats = new X2Assoc_PrintStats(x2assoc_Model, this, sixteenths_across[1], sixteenths_down[1], initWidth[1], initHeight[1] + 175);
        x2assoc_PrintStats.completeTheDeal();
        assocPrintStatsContainingPane = x2assoc_PrintStats.getTheContainingPane();
        assocPrintStatsContainingPane.setStyle(containingPaneStyle);
        
        initWidth[2] = 700;
        x2Assoc_PrintAdvStats = new X2Assoc_PrintAdvStats(x2assoc_Model, this, sixteenths_across[2], sixteenths_down[2], initWidth[2], initHeight[2] + 275);
        x2Assoc_PrintAdvStats.completeTheDeal();
        assocPrintAdvStatsContainingPane = x2Assoc_PrintAdvStats.getTheContainingPane();
        assocPrintAdvStatsContainingPane.setStyle(containingPaneStyle);

        initWidth[3] = 800;
        initHeight[3] = 700;
        x2Assoc_MosaicPlotView = new X2Assoc_MosaicPlotView(x2assoc_Model, this, sixteenths_across[3], sixteenths_down[3], initWidth[3], initHeight[3]);
        x2Assoc_MosaicPlotView.completeTheDeal();
        mosaicPlotContainingPane = x2Assoc_MosaicPlotView.getTheContainingPane();
        mosaicPlotContainingPane.setStyle(containingPaneStyle);

        initHeight[4] = 500;
        x2Assoc_SegBarChartView = new X2Assoc_SegBarChartView(x2assoc_Model, this, sixteenths_across[4], sixteenths_down[4], initWidth[4], initHeight[4]);
        x2Assoc_SegBarChartView.completeTheDeal();        
        segmentedBarChartContainingPane = x2Assoc_SegBarChartView.getTheContainingPane(); 
        segmentedBarChartContainingPane.setStyle(containingPaneStyle);
        
        initHeight[5] = 525;
        x2Assoc_PieChartView = new X2Assoc_PieChartView(x2assoc_Model, this, sixteenths_across[5], sixteenths_down[5], initWidth[5], initHeight[5]);
        x2Assoc_PieChartView.completeTheDeal();        
        pieChartContainingPane = x2Assoc_PieChartView.getTheContainingPane();
        pieChartContainingPane.setStyle(containingPaneStyle);
   
        backGround.getChildren().addAll(pdfViewContainingPane,
                                        assocPrintStatsContainingPane, 
                                        assocPrintAdvStatsContainingPane,
                                        mosaicPlotContainingPane, 
                                        segmentedBarChartContainingPane,
                                        pieChartContainingPane
                                        );          
    }
}
