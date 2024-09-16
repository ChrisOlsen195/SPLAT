/**********************0****************************
 *                X2GOF_Dashboard                 *
 *                    09/05/24                    *
 *                     15:00                      *
 *************************************************/
/**************************************************
*    Initial widths and heights from Super Class  *
*              are 400 and 350                    *
**************************************************/
package chiSquare.GOF;

import chiSquare.ChiSqPDFView;
import superClasses.Dashboard;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class X2GOF_Dashboard extends Dashboard {
    // POJOs
    final String[] gofCheckBoxDescr = {" Chi square \n (Inference)", 
                                 "Print Statistics \n      (basic)", 
                                 "Print Statistics\n   (advanced)", 
                                 "   Plot of \n residuals", 
                                 " Plot of observed\n& expected values"};

    // My classes
    ChiSqPDFView x2PDFView;
    X2GOF_Model x2GOF_Model;
    X2GOF_ObsExpView chiSqObsExpView;
    X2GOF_PrintAdvStats gofPrintAdvStats;
    X2GOF_PrintStats gofPrintStats;
    X2GOF_ResidualsView gofResidualsView;

    //  POJOs / FX
    
    Pane obsExpContainingPane, gofResidualsContainingPane,
         gofPrintStatsContainingPane, gofPrintAdvStatsContainingPane,
         x2PDFContainingPane; 
            
    public X2GOF_Dashboard(X2GOF_Controller x2GOF_Controller, X2GOF_Model x2GOF_Model) {
        super(5);
        System.out.println("\n41 X2GOF_Dashboard, Constructing");
        // nCheckBoxes = 5;
        checkBoxDescr = new String[nCheckBoxes];
        
        for (int ithCheckBox = 0; ithCheckBox < nCheckBoxes; ithCheckBox++) {
            checkBoxDescr[ithCheckBox] = gofCheckBoxDescr[ithCheckBox];
            checkBoxes[ithCheckBox].setText(checkBoxDescr[ithCheckBox]);
            checkBoxes[ithCheckBox].setId(checkBoxDescr[ithCheckBox]);
            if (checkBoxes[ithCheckBox].isSelected() == true) 
                checkBoxes[ithCheckBox].setTextFill(Color.GREEN);
            else
                checkBoxes[ithCheckBox].setTextFill(Color.RED);
        }
        
        setTitle("ChiSquare GOF dashboard");       
        this.x2GOF_Model = new X2GOF_Model();
        this.x2GOF_Model = x2GOF_Model;
        populateTheBackGround();
        putEmAllUp();
    }  

    @Override
    public void putEmAllUp() {   
        if (checkBoxSettings[0] == true) {
            x2PDFContainingPane.setVisible(true);
            x2PDFView.doTheGraph();
        }
        else 
            x2PDFContainingPane.setVisible(false);  
        
        if (checkBoxSettings[1] == true) {
            gofPrintStatsContainingPane.setVisible(true);
        }
        else {
            gofPrintStatsContainingPane.setVisible(false); 
        }
        
        if (checkBoxSettings[2] == true) {
            gofPrintAdvStatsContainingPane.setVisible(true);
        }
        else {
            gofPrintAdvStatsContainingPane.setVisible(false); 
        }

        if (checkBoxSettings[3] == true) {
            gofResidualsContainingPane.setVisible(true);
            gofResidualsView.doTheGraph();
        }
        else {
            gofResidualsContainingPane.setVisible(false); 
        }   
        
        if (checkBoxSettings[4] == true) {
            obsExpContainingPane.setVisible(true);
            chiSqObsExpView.doTheGraph();
        }
        else {
            obsExpContainingPane.setVisible(false); 
        }
    }
    
    public void populateTheBackGround() {
        initWidth[0] = 500;
        x2PDFView = new ChiSqPDFView(x2GOF_Model, this, sixteenths_across[0], sixteenths_down[0], initWidth[0], initHeight[0]);
        x2PDFView.completeTheDeal();
        x2PDFContainingPane = x2PDFView.getTheContainingPane(); 
        x2PDFContainingPane.setStyle(containingPaneStyle);
        
        initWidth[1] = 650;
        gofPrintStats = new X2GOF_PrintStats(x2GOF_Model, this, sixteenths_across[1], sixteenths_down[1], initWidth[1] - 80, initHeight[1]);
        gofPrintStats.completeTheDeal();
        gofPrintStatsContainingPane = gofPrintStats.getTheContainingPane(); 
        gofPrintStatsContainingPane.setStyle(containingPaneStyle);
        
        initWidth[2] = 725;
        gofPrintAdvStats = new X2GOF_PrintAdvStats(x2GOF_Model, this, sixteenths_across[2], sixteenths_down[2], initWidth[2] + 30, initHeight[2]);
        gofPrintAdvStats.completeTheDeal();
        gofPrintAdvStatsContainingPane = gofPrintAdvStats.getTheContainingPane(); 
        gofPrintAdvStatsContainingPane.setStyle(containingPaneStyle);
        
        initWidth[3] = 500;
        gofResidualsView = new X2GOF_ResidualsView(x2GOF_Model, this, sixteenths_across[3], sixteenths_down[3], initWidth[3], initHeight[3]);
        gofResidualsView.completeTheDeal();        
        gofResidualsContainingPane = gofResidualsView.getTheContainingPane();  
        gofResidualsContainingPane.setStyle(containingPaneStyle);

        initWidth[4] = 450;
        chiSqObsExpView = new X2GOF_ObsExpView(x2GOF_Model, this, sixteenths_across[4], sixteenths_down[4], initWidth[4], initHeight[4]);
        chiSqObsExpView.completeTheDeal();        
        obsExpContainingPane = chiSqObsExpView.getTheContainingPane();  
        obsExpContainingPane.setStyle(containingPaneStyle);    
        
        backGround.getChildren().addAll(x2PDFContainingPane, 
                                         gofPrintStatsContainingPane,
                                         gofPrintAdvStatsContainingPane,
                                         gofResidualsContainingPane,
                                         obsExpContainingPane);          
    }
}
