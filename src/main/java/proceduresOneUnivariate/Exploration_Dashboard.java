/**************************************************
 *             Exploration_Dashboard              *
 *                   02/17/25                     *
 *                    18:00                       *
 *************************************************/
/**************************************************
*    Initial widths and heights from Super Class  *
*              are 400 and 350                    *
**************************************************/
package proceduresOneUnivariate;

import proceduresManyUnivariate.HorizontalBoxPlot_View;
import proceduresManyUnivariate.VerticalBoxPlot_Model;
import proceduresManyUnivariate.HorizontalBoxPlot_Model;
import proceduresManyUnivariate.VerticalBoxPlot_View;
import superClasses.Dashboard;
import dataObjects.QuantitativeDataVariable;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class Exploration_Dashboard extends Dashboard {
    // POJOs

    final String[] expCheckBoxDescr = { "Histogram", "NormalDiff", "NormalProb", 
                                         "StemNLeaf", "DotPlot", "HorizBox", 
                                         "VertBox", "PrintStats" };
    
    // Make empty if no-print
    //String waldoFile = "Exploration_Dashboard";
    String waldoFile = "";

    // My classes
    DotPlot_Model dotPlotModel;    
    Histogram_Model histModel;
    HorizontalBoxPlot_Model hBoxModel;
    NormProb_Model normProbModel;
    NormProb_DiffModel normProb_DiffModel;
    StemNLeaf_Model stemNLeafModel;
    VerticalBoxPlot_Model vBoxModel;
    PrintUStats_Model printUStatsModel;
    
    DotPlot_View dotPlot_View;          
    Histogram_View histogram_View; 
    HorizontalBoxPlot_View horizBoxView;
    NormProb_View normProb_View;
    NormProb_DiffView normProb_DiffView;
    StemNLeaf_View stemNLeaf_View;
    VerticalBoxPlot_View vertBoxView;
    PrintUStats_View printUStats_View;
    
    //  POJO / FX
    Pane histogramContainingPane, normProbContainingPane,
         normProbDiffContainingPane,
         stemNLeafContainingPane, dotPlotContainingPane,
          horizBoxContainingPane, vertBoxContainingPane,
          printUStatsContainingPane; 
        
    public Exploration_Dashboard(Univ_Quant_Controller univ_Quant_Controller, QuantitativeDataVariable univ_Model) {
        super(8);
        dm = univ_Quant_Controller.getDataManager();
        dm.whereIsWaldo(64, waldoFile, "Constructing");

        histModel = univ_Quant_Controller.getHistModel();
        normProb_DiffModel = univ_Quant_Controller.getNormProb_DiffModel();
        normProbModel = univ_Quant_Controller.getNormProbModel();
        stemNLeafModel = univ_Quant_Controller.getStemNLeafModel();
        dotPlotModel = univ_Quant_Controller.getDotPlotModel();
        dm.whereIsWaldo(74, waldoFile, "Constructing");
        hBoxModel = univ_Quant_Controller.getHBoxModel();
        vBoxModel = univ_Quant_Controller.getVBoxModel();  
        printUStatsModel = univ_Quant_Controller.getPrintUStatsModel(); 
        printUStatsModel.constructThePrintLines();
        dm.whereIsWaldo(80, waldoFile, "Constructing");
        checkBoxDescr = new String[nCheckBoxes];
        for (int ithCheckBox = 0; ithCheckBox < nCheckBoxes; ithCheckBox++) {
            checkBoxDescr[ithCheckBox] = expCheckBoxDescr[ithCheckBox];
            checkBoxes[ithCheckBox].setText(checkBoxDescr[ithCheckBox]);
            checkBoxes[ithCheckBox].setId(checkBoxDescr[ithCheckBox]);
            
            if (checkBoxes[ithCheckBox].isSelected() == true) 
                checkBoxes[ithCheckBox].setTextFill(Color.GREEN);
            else
                checkBoxes[ithCheckBox].setTextFill(Color.RED);
        }
        setTitle("Exploration dashboard"); 
    }  
    
    public void putEmAllUp() { 
        dm.whereIsWaldo(91, waldoFile, "putEmAllUp()");
        
        if (checkBoxSettings[0] == true) {
            histogramContainingPane.setVisible(true);
            histogram_View.doTheGraph();
        }
        else { histogramContainingPane.setVisible(false);   }
        
        if (checkBoxSettings[1] == true) {
            normProbDiffContainingPane.setVisible(true);
            normProb_DiffView.doTheGraph();
        }
        else {  normProbDiffContainingPane.setVisible(false);  }
        
        if (checkBoxSettings[2] == true) {
            normProbContainingPane.setVisible(true);
            normProb_View.doTheGraph();
        }
        else { normProbContainingPane.setVisible(false);  }

        if (checkBoxSettings[3] == true) {
            stemNLeafContainingPane.setVisible(true);
            stemNLeaf_View.doTheGraph();
        }
        else {  stemNLeafContainingPane.setVisible(false);  }
        
        if (checkBoxSettings[4] == true) {
            dotPlotContainingPane.setVisible(true);
            dotPlot_View.doTheGraph();
        }
        else { dotPlotContainingPane.setVisible(false);  }   
        
        if (checkBoxSettings[5] == true) {
            horizBoxContainingPane.setVisible(true);
            horizBoxView.doTheGraph();
        }
        else { horizBoxContainingPane.setVisible(false);  }

        if (checkBoxSettings[6] == true) {
            vertBoxContainingPane.setVisible(true);
            vertBoxView.doTheGraph();
        }
        else { vertBoxContainingPane.setVisible(false);  } 
        
        if (checkBoxSettings[7] == true) {
            printUStatsContainingPane.setVisible(true);
        }
        else { printUStatsContainingPane.setVisible(false); } 
        dm.whereIsWaldo(156, waldoFile, "end putEmAllUp()");
    }
    
    public void populateTheBackGround() {
        dm.whereIsWaldo(154, waldoFile, "populateTheBackGround()");
        containingPaneStyle = "-fx-background-color: white;" +
                            "-fx-border-color: blue, blue;" + 
                            "-fx-border-width: 4, 4;" +
                            "fx-border-radius: 0, 0;" +
                            "-fx-border-insets: -4, -4;" +
                            "-fx-border-style: solid centered, solid centered;";
        
        stemNLeaf_View = new StemNLeaf_View(stemNLeafModel, this, 0.9 * sixteenths_across[0], sixteenths_down[0], initWidth[0], initHeight[0]);
        stemNLeaf_View.completeTheDeal();
        stemNLeafContainingPane = stemNLeaf_View.getTheContainingPane(); 
        stemNLeafContainingPane.setStyle(containingPaneStyle);
        
        initWidth[1] = 650; 
        initHeight[1] = 300;
        dotPlot_View = new DotPlot_View(dotPlotModel, this, 0.9 * sixteenths_across[1], sixteenths_down[1], initWidth[1], initHeight[1]);
        dotPlot_View.completeTheDeal();
        dotPlotContainingPane = dotPlot_View.getTheContainingPane(); 
        dotPlotContainingPane.setStyle(containingPaneStyle);
        dm.whereIsWaldo(178, waldoFile, "populateTheBackGround()");
        histogram_View = new Histogram_View(histModel, this, 0.9 * sixteenths_across[2], sixteenths_down[2], initWidth[2], initHeight[2]);
        histogram_View.completeTheDeal();
        histogramContainingPane = histogram_View.getTheContainingPane(); 
        histogramContainingPane.setStyle(containingPaneStyle);
        
        initWidth[3] = 450;
        normProb_DiffView = new NormProb_DiffView(normProb_DiffModel, this, 0.9 * sixteenths_across[3], sixteenths_down[3], initWidth[3], initHeight[3]);
        normProb_DiffView.completeTheDeal();        
        normProbDiffContainingPane = normProb_DiffView.getTheContainingPane();  
        normProbDiffContainingPane.setStyle(containingPaneStyle);
        dm.whereIsWaldo(189, waldoFile, "populateTheBackGround()");
        normProb_View = new NormProb_View(normProbModel, this, 0.9 * sixteenths_across[4], sixteenths_down[4], initWidth[4], initHeight[4]);
        normProb_View.completeTheDeal();        
        normProbContainingPane = normProb_View.getTheContainingPane();  
        normProbContainingPane.setStyle(containingPaneStyle);
        
        initWidth[5] = 650; 
        initHeight[5] = 300;
        horizBoxView = new HorizontalBoxPlot_View(hBoxModel, this, 0.9 * sixteenths_across[5], sixteenths_down[5], initWidth[5], initHeight[5]);
        horizBoxView.completeTheDeal();        
        horizBoxContainingPane = horizBoxView.getTheContainingPane();  
        horizBoxContainingPane.setStyle(containingPaneStyle);
        
        initWidth[6] = 650; 
        initHeight[6] = 450;
        vertBoxView = new VerticalBoxPlot_View(vBoxModel, this, 0.9 * sixteenths_across[6], sixteenths_down[6], initWidth[6], initHeight[6]);
        vertBoxView.completeTheDeal();        
        vertBoxContainingPane = vertBoxView.getTheContainingPane();  
        vertBoxContainingPane.setStyle(containingPaneStyle);
        dm.whereIsWaldo(208, waldoFile, "populateTheBackGround()");

        initWidth[7] = 350; 
        initHeight[7] = 550;
        printUStats_View = new PrintUStats_View(printUStatsModel, this, 0.9 * sixteenths_across[7], sixteenths_down[7] - 225, initWidth[7], initHeight[7]);
        printUStats_View.completeTheDeal();        
        printUStatsContainingPane = printUStats_View.getTheContainingPane();  
        printUStatsContainingPane.setStyle(containingPaneStyle);
        
        backGround.getChildren().addAll(histogramContainingPane, 
                                         normProbDiffContainingPane,
                                         normProbContainingPane,
                                         stemNLeafContainingPane,
                                         dotPlotContainingPane,
                                         horizBoxContainingPane,
                                         vertBoxContainingPane,
                                         printUStatsContainingPane); 
        dm.whereIsWaldo(238, waldoFile, "end populateTheBackGround()");
    } 
}