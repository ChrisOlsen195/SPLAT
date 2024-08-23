/**************************************************
 *               UnivCat_Dashboard                *
 *                   11/01/23                     *
 *                     12:00                      *
 *************************************************/
/**************************************************
*    Initial widths and heights from Super Class  *
*              are 400 and 300                    *
**************************************************/
package univariateProcedures_Categorical;

import superClasses.Dashboard;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class UnivCat_Dashboard extends Dashboard {
    // POJOs

    String waldoFile = "UnivCat_Dashboard";
    //String waldoFile = ""; 
    
    final String[] univCatCheckBoxDescr = {"Print Statistics", 
                                 "Frequency Distribution", 
                                 "Relative Frequency Distribution",
                                 "ParetoChart", "PieChart"};

    // My classes
    UnivCat_Model univCat_Model;
    
    UnivCat_FreqDistr univCat_FreqDistr;
    UnivCat_RelFreqDistr univCat_RelFreqDistr;
    UnivCat_PrintStats univCat_PrintStats; 
    UnivCat_ParetoChart univCat_ParetoChart;
    UnivCat_PieChart univCat_PieChart;

    //  POJOs / FX    
    Pane printStatsContainingPane, rawFreqContainingPane, relFreqContainingPane, 
            paretoContainingPane, pieChartContainingPane; 
            
    public UnivCat_Dashboard(UnivCat_Controller univCat_Controller, UnivCat_Model univCat_Model) {
        super(5);
        dm = univCat_Controller.getDataManager();
        dm.whereIsWaldo(43, waldoFile, "Constructing");
        checkBoxDescr = new String[nCheckBoxes];
        
        for (int ithCheckBox = 0; ithCheckBox < nCheckBoxes; ithCheckBox++) {
            checkBoxDescr[ithCheckBox] = univCatCheckBoxDescr[ithCheckBox];
            checkBoxes[ithCheckBox].setText(checkBoxDescr[ithCheckBox]);
            checkBoxes[ithCheckBox].setId(checkBoxDescr[ithCheckBox]);
            if (checkBoxes[ithCheckBox].isSelected() == true) {
                checkBoxes[ithCheckBox].setTextFill(Color.GREEN);
            }
            else { checkBoxes[ithCheckBox].setTextFill(Color.RED); }
        }
        
        setTitle("Univariate Categorical dashboard");       
        this.univCat_Model = new UnivCat_Model();
        this.univCat_Model = univCat_Model;
        populateTheBackGround();
        putEmAllUp();
    }  

    @Override
    public void putEmAllUp() {   
        if (checkBoxSettings[0] == true) {
            printStatsContainingPane.setVisible(true);
        }
        else { printStatsContainingPane.setVisible(false); }
        
        if (checkBoxSettings[1] == true) {
            rawFreqContainingPane.setVisible(true);
            univCat_FreqDistr.doTheGraph();
        }
        else { rawFreqContainingPane.setVisible(false);  }
        
        if (checkBoxSettings[2] == true) {
            relFreqContainingPane.setVisible(true);
            univCat_RelFreqDistr.doTheGraph();
        }
        else { relFreqContainingPane.setVisible(false);  }
        
        if (checkBoxSettings[3] == true) {
            paretoContainingPane.setVisible(true);
            univCat_ParetoChart.doTheGraph();
        }
        else { paretoContainingPane.setVisible(false); }

        if (checkBoxSettings[4] == true) {
            pieChartContainingPane.setVisible(true);
            univCat_PieChart.doTheGraph();
        }
        else { pieChartContainingPane.setVisible(false); }
    }
    
    public void populateTheBackGround() {        
        univCat_PrintStats = new UnivCat_PrintStats(univCat_Model, this, sixteenths_across[0], sixteenths_down[0], initWidth[0] + 30, initHeight[0] - 50);
        univCat_PrintStats.completeTheDeal();
        printStatsContainingPane = univCat_PrintStats.getTheContainingPane(); 
        printStatsContainingPane.setStyle(containingPaneStyle);
        
        univCat_FreqDistr = new UnivCat_FreqDistr(univCat_Model, this, sixteenths_across[1], sixteenths_down[1], initWidth[1], initHeight[1]);
        univCat_FreqDistr.completeTheDeal();        
        rawFreqContainingPane = univCat_FreqDistr.getTheContainingPane();  
        rawFreqContainingPane.setStyle(containingPaneStyle);

        univCat_RelFreqDistr = new UnivCat_RelFreqDistr(univCat_Model, this, sixteenths_across[2], sixteenths_down[2], initWidth[2], initHeight[2]);
        univCat_RelFreqDistr.completeTheDeal();        
        relFreqContainingPane = univCat_RelFreqDistr.getTheContainingPane();  
        relFreqContainingPane.setStyle(containingPaneStyle);   
        

        univCat_ParetoChart = new UnivCat_ParetoChart(univCat_Model, this, sixteenths_across[3], sixteenths_down[3], initWidth[3], initHeight[3]);
        univCat_ParetoChart.completeTheDeal();        
        paretoContainingPane = univCat_ParetoChart.getTheContainingPane();  
        paretoContainingPane.setStyle(containingPaneStyle);  

        initHeight[4] = 500;
        univCat_PieChart = new UnivCat_PieChart(univCat_Model, this, sixteenths_across[4], sixteenths_down[4], initWidth[4], initHeight[4]);
        univCat_PieChart.completeTheDeal();        
        pieChartContainingPane = univCat_PieChart.getTheContainingPane();  
        pieChartContainingPane.setStyle(containingPaneStyle); 
        
        backGround.getChildren().addAll(printStatsContainingPane, 
                                         rawFreqContainingPane,
                                         relFreqContainingPane,
                                         paretoContainingPane,
                                         pieChartContainingPane);          
    }
}

