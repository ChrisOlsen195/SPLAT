/**************************************************
 *           LinReg_Power_Dashboard              *
 *                  06/05/26                      *
 *                    15:00                       *
 *************************************************/
/**************************************************
*    Initial widths and heights from Super Class  *
*              are 400 and 350                    *
**************************************************/
package power_LinReg;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import superClasses.*;

public class LinReg_Power_Dashboard extends Dashboard {
    
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    // My classes
    LinReg_Power_PdfView linReg_Power_PdfView;
    LinReg_Power_VsEffectSizeView linReg_Power_VsEffectSizeView;
    LinReg_Power_VsSampleSizeView linReg_Power_VsSampleSizeView;
    LinReg_Power_VsAlphaView linReg_Power_VsAlphaView; 
    LinReg_Power_PrintReport_View linReg_Power_PrintReport_View;

    Pane pdfViewContainingPane, pVsESContainingPane, pVsNContainingPane,
            pVsAlphaContainingPane, prntReportContainingPane; 

    LinReg_Power_Controller linReg_Power_Controller;
    LinReg_Power_Model linReg_Power_Model;  
    
    public LinReg_Power_Dashboard(LinReg_Power_Controller linReg_Power_Controller) {
        super(5);
        if (printTheStuff) {
            System.out.println("38 *** LinReg_Power_Dashboard, Constructing");
        }
        this.linReg_Power_Controller = linReg_Power_Controller;
    }
    
    public void initializeFurther() {
        linReg_Power_Model = linReg_Power_Controller.get_LinReg_Power_Model();
        linReg_Power_Model.printModelStuff();
        nCheckBoxes = 5;
        checkBoxDescr = new String[5]; 
        checkBoxDescr[0] = "Power distributions";
        checkBoxDescr[1] = "pVsESView";
        checkBoxDescr[2] = "pVsNView";
        checkBoxDescr[3] = "pVsAlphaView";
        checkBoxDescr[4] = "Power report";
    
        for (int ithCheckBox = 0; ithCheckBox < nCheckBoxes; ithCheckBox++) {
            checkBoxes[ithCheckBox].setText(checkBoxDescr[ithCheckBox]);
            checkBoxes[ithCheckBox].setId(checkBoxDescr[ithCheckBox]);
            if (checkBoxes[ithCheckBox].isSelected() == true) 
                checkBoxes[ithCheckBox].setTextFill(Color.GREEN);
            else
                checkBoxes[ithCheckBox].setTextFill(Color.RED);
        }
        setTitle("Power, Single Mean");  
    }

    
    public void putEmAllUp() { 
        if (checkBoxSettings[0] == true) {
            pdfViewContainingPane.setVisible(true);
            linReg_Power_PdfView.doTheGraph();
        }  else { 
            pdfViewContainingPane.setVisible(false);  
        }
        
        if (checkBoxSettings[1] == true) {
            pVsESContainingPane.setVisible(true);
            linReg_Power_VsEffectSizeView.doTheGraph();
        } else {  
            pVsESContainingPane.setVisible(false);  
        }
        
        if (checkBoxSettings[2] == true) {
            pVsNContainingPane.setVisible(true);
            linReg_Power_VsSampleSizeView.doTheGraph();
        } else { 
            pVsNContainingPane.setVisible(false); 
        }
       
        if (checkBoxSettings[3] == true) {
            pVsAlphaContainingPane.setVisible(true);
            linReg_Power_VsAlphaView.doTheGraph();
        } else { 
            pVsAlphaContainingPane.setVisible(false); 
        }
        
        if (checkBoxSettings[4] == true) {
            prntReportContainingPane.setVisible(true);
        } else { 
            prntReportContainingPane.setVisible(false); 
        }
    }

    public void makeTheBackGround() {
        backGround = new Pane();
        backGround.setStyle("-fx-background-color: lightblue;");
        backGroundHeight = dashHeight;
        backGround.setPrefSize(dashWidth, backGroundHeight);  
    }
    
    public void populateTheBackGround() {
        String[] checkBoxDescr = { "Z-test", "HBoxPlot", "VBoxPlot"};  
        linReg_Power_Model.restoreNullValues();
        initHeight[0] = 500.0;
        initWidth[0] = 500.0;
        linReg_Power_PdfView = new LinReg_Power_PdfView(linReg_Power_Model, this,
                                        sixteenths_across[0], sixteenths_down[0],
                                        initWidth[0], initHeight[0]);
        linReg_Power_PdfView.makeItHappen();
        linReg_Power_Model.restoreNullValues();
        linReg_Power_VsEffectSizeView = new LinReg_Power_VsEffectSizeView(linReg_Power_Model, this,
                                                      sixteenths_across[1], sixteenths_down[1],
                                                      initWidth[1], initHeight[1]);
        linReg_Power_VsEffectSizeView.makeItHappen();
        linReg_Power_Model.restoreNullValues();
        
        initWidth[2] = 425;
        linReg_Power_VsSampleSizeView = new LinReg_Power_VsSampleSizeView(linReg_Power_Model, this,
                                                     sixteenths_across[2], sixteenths_down[2],
                                                     initWidth[2], initHeight[2]);           
        linReg_Power_Model.restoreNullValues();
        linReg_Power_VsAlphaView = new LinReg_Power_VsAlphaView(linReg_Power_Model, this,
                                                     sixteenths_across[3], sixteenths_down[3],
                                                     initWidth[3], initHeight[3]); 
        
        initWidth[4] = 300.0;
        linReg_Power_PrintReport_View = new LinReg_Power_PrintReport_View(linReg_Power_Model, this,
                                                     sixteenths_across[4], sixteenths_down[4],
                                                     initWidth[4], initHeight[4]);  
        linReg_Power_PrintReport_View.completeTheDeal();        
        
        linReg_Power_Model.restoreNullValues();
        pdfViewContainingPane = linReg_Power_PdfView.getTheContainingPane();
        pVsESContainingPane = linReg_Power_VsEffectSizeView.getTheContainingPane();
        pVsNContainingPane = linReg_Power_VsSampleSizeView.getTheContainingPane();
        pVsAlphaContainingPane = linReg_Power_VsAlphaView.getTheContainingPane();
        prntReportContainingPane = linReg_Power_PrintReport_View.getTheContainingPane();

        backGround.getChildren().addAll(pdfViewContainingPane,
                                        pVsESContainingPane, 
                                        pVsNContainingPane,
                                        pVsAlphaContainingPane,
                                        prntReportContainingPane);         
    }
    
    public LinReg_Power_Controller getController() { return linReg_Power_Controller; }
}
