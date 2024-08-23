/**************************************************
 *              Explore_2Ind_Dashboard            *
 *                    11/01/23                    *
 *                     12:00                      *
 *************************************************/
/**************************************************
*    Initial widths and heights from Super Class  *
*              are 400 and 300                    *
**************************************************/
package proceduresTwoUnivariate;

import superClasses.Dashboard;
import proceduresManyUnivariate.HorizontalBoxPlot_View;
import proceduresManyUnivariate.VerticalBoxPlot_Model;
import proceduresManyUnivariate.HorizontalBoxPlot_Model;
import proceduresManyUnivariate.VerticalBoxPlot_View;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import splat.Data_Manager;

public class Explore_2Ind_Dashboard extends Dashboard {
    //  POJOs
    final String[] regrCheckBoxDescr = { "HBoxPlot", "VBoxPlot",
                                         "QQPlot", "BBSLPlot",
                                         "Statistics", "DotPlots"};
    
    // My classes
    BBSL_Model bbsl_Model;
    BBSL_View bbsl_View; 
    
    // Make empty if no-print
    // String waldoFile = "Explore_2Ind_Dashboard";
    String waldoFile = "";
    
    DotPlot_2Ind_Model dotPlot_2Ind_Model;
    DotPlot_2Ind_View dotPlot_2Ind_View;
    Explore_2Ind_Controller explore_2Ind_Controller;
    HorizontalBoxPlot_Model hBox_Model;
    HorizontalBoxPlot_View hBox_View; 
    Print_TwoStats_View prntTwoStatsView; 
    QQPlot_Model qqPlot_Model;
    QQPlot_View qqPlot_View;
    VerticalBoxPlot_Model vBox_Model;
    VerticalBoxPlot_View vBox_View;

    // POJOs / FX
    Pane hBoxContainingPane, vBoxContainingPane, printTwoStatsContainingPane,
         qqPlotContainingPane, bbslContainingPane, dotPlot_2IndContainingPane; 

    public Explore_2Ind_Dashboard(Explore_2Ind_Controller explore_2Ind_Controller, Explore_2Ind_Model explore_2Ind_Model) {
        super(6);
        //System.out.println("52 Explore_2Ind_Dashboard, constructing");
        this.explore_2Ind_Controller = explore_2Ind_Controller;
        hBox_Model = explore_2Ind_Controller.getHBox_Model();
        vBox_Model = explore_2Ind_Controller.getVBox_Model();
        qqPlot_Model = explore_2Ind_Controller.getQQ_Model();
        bbsl_Model = explore_2Ind_Controller.getBBSL_Model();
        dotPlot_2Ind_Model = explore_2Ind_Controller.get_2Ind_Dot_Model();

        dm = explore_2Ind_Controller.getDataManager();
        dm.whereIsWaldo(62, waldoFile, "Constructing");
        dm.whereIsWaldo(54, waldoFile, "Constructing");

        checkBoxDescr = new String[nCheckBoxes];
        
        for (int ithCheckBox = 0; ithCheckBox < nCheckBoxes; ithCheckBox++) {
            checkBoxDescr[ithCheckBox] = regrCheckBoxDescr[ithCheckBox];
            checkBoxes[ithCheckBox].setText(checkBoxDescr[ithCheckBox]);
            checkBoxes[ithCheckBox].setId(checkBoxDescr[ithCheckBox]);
            if (checkBoxes[ithCheckBox].isSelected() == true) 
                checkBoxes[ithCheckBox].setTextFill(Color.GREEN);
            else
                checkBoxes[ithCheckBox].setTextFill(Color.RED);
        }        
        setTitle("Two independent samples dashboard"); 
    }  

    public void putEmAllUp() { 
        
        if (checkBoxSettings[0] == true) {
            hBoxContainingPane.setVisible(true);
            hBox_View.doTheGraph();
        }
        else { hBoxContainingPane.setVisible(false); }
        
        if (checkBoxSettings[1] == true) {
            vBoxContainingPane.setVisible(true);
            vBox_View.doTheGraph();
        }
        else { vBoxContainingPane.setVisible(false); }
        
        if (checkBoxSettings[2] == true) {
            qqPlotContainingPane.setVisible(true);
            qqPlot_View.doTheGraph();
        }
        else { qqPlotContainingPane.setVisible(false); }

        if (checkBoxSettings[3] == true) {
            bbslContainingPane.setVisible(true);
            bbsl_View.doTheGraph();
        }
        else { bbslContainingPane.setVisible(false);  }    
        
        if (checkBoxSettings[4] == true) {
            printTwoStatsContainingPane.setVisible(true);
        }
        else { printTwoStatsContainingPane.setVisible(false); }  
        
        if (checkBoxSettings[5] == true) {
            dotPlot_2IndContainingPane.setVisible(true);
        }
        else { dotPlot_2IndContainingPane.setVisible(false); } 
    }
    
    public void populateTheBackGround() {       
        containingPaneStyle =  "-fx-background-color: white;" +
            "-fx-border-color: blue, blue;" + 
            "-fx-border-width: 4, 4;" +
            "fx-border-radius: 0, 0;" +
            "-fx-border-insets: -4, -4;" +
            "-fx-border-style: solid centered, solid centered;";
        
        initWidth[0] = 650;
        hBox_View = new HorizontalBoxPlot_View(hBox_Model, this, 0.5 * sixteenths_across[0], sixteenths_down[0], initWidth[0], initHeight[0]);
        hBox_View.completeTheDeal();
        hBoxContainingPane = hBox_View.getTheContainingPane(); 
        hBoxContainingPane.setStyle(containingPaneStyle);
        
        initWidth[1] = 650;
        vBox_View = new VerticalBoxPlot_View(vBox_Model, this, 0.5 * sixteenths_across[1], sixteenths_down[1], initWidth[1], initHeight[1]);
        vBox_View.completeTheDeal();
        vBoxContainingPane = vBox_View.getTheContainingPane(); 
        vBoxContainingPane.setStyle(containingPaneStyle);

        qqPlot_View = new QQPlot_View(qqPlot_Model, this, 0.5 * sixteenths_across[2], sixteenths_down[2], initWidth[2], initHeight[2]);
        qqPlot_View.completeTheDeal();
        qqPlotContainingPane = qqPlot_View.getTheContainingPane(); 
        qqPlotContainingPane.setStyle(containingPaneStyle);
        
        initWidth[3] = 650;
        bbsl_View = new BBSL_View(bbsl_Model, this, 0.5 * sixteenths_across[3], sixteenths_down[3], initWidth[3], initHeight[3]);
        bbsl_View.completeTheDeal();        
        bbslContainingPane = bbsl_View.getTheContainingPane();  
        bbslContainingPane.setStyle(containingPaneStyle);
        
        dotPlot_2Ind_View = new DotPlot_2Ind_View(dotPlot_2Ind_Model, this, 0.5 * sixteenths_across[4], sixteenths_down[4], initWidth[4], initHeight[4]);
        dotPlot_2Ind_View.completeTheDeal();        
        dotPlot_2IndContainingPane = dotPlot_2Ind_View.getTheContainingPane();  
        dotPlot_2IndContainingPane.setStyle(containingPaneStyle);

        initWidth[5] = 375;
        initHeight[5] = 650;
        prntTwoStatsView = new Print_TwoStats_View(explore_2Ind_Controller, this, sixteenths_across[5], sixteenths_down[5] - 200, initWidth[5], initHeight[5]);
        prntTwoStatsView.completeTheDeal();        
        printTwoStatsContainingPane = prntTwoStatsView.getTheContainingPane();  
        printTwoStatsContainingPane.setStyle(containingPaneStyle);        
        backGround.getChildren().addAll(hBoxContainingPane, 
                                         vBoxContainingPane,
                                         qqPlotContainingPane,
                                         bbslContainingPane,
                                         dotPlot_2IndContainingPane,
                                         printTwoStatsContainingPane);          
    }
    
    @Override
    public Data_Manager getDataManager() { return dm; }
}