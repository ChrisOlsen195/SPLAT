/**************************************************
 *           OneMean_Power_Dashboard              *
 *                  05/28/24                      *
 *                    12:00                       *
 *************************************************/
/**************************************************
*    Initial widths and heights from Super Class  *
*              are 400 and 350                    *
**************************************************/
package power_OneMean;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import superClasses.*;

public class OneMean_Power_Dashboard extends Dashboard {
    
    // My classes
    OneMean_Power_PdfView oneMean_Power_PdfView;
    OneMean_Power_VsEffectSizeView oneMean_Power_VsEffectSizeView;
    OneMean_Power_VsSampleSizeView oneMean_Power_VsSampleSizeView;
    OneMean_Power_VsAlphaView oneMean_Power_VsAlphaView; 
    OneMean_Power_PrintReport_View oneMean_Power_PrintReport_View;

    Pane pdfViewContainingPane, pVsESContainingPane, pVsNContainingPane,
            pVsAlphaContainingPane, prntReportContainingPane; 

    OneMean_Power_Controller oneMean_Power_Controller;
    OneMean_Power_Model oneMean_Power_Model;  
    
    public OneMean_Power_Dashboard(OneMean_Power_Controller oneMean_Power_Controller) {
        super(5);
        //System.out.println("\n33 OneMean_Power_Dashboard, Constructing");
        this.oneMean_Power_Controller = oneMean_Power_Controller;
    }
    
    public void initializeFurther() {
        oneMean_Power_Model = oneMean_Power_Controller.get_power_Model_Z();
        oneMean_Power_Model.printModelStuff();
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
            oneMean_Power_PdfView.doTheGraph();
        }  else { 
            pdfViewContainingPane.setVisible(false);  
        }
        
        if (checkBoxSettings[1] == true) {
            pVsESContainingPane.setVisible(true);
            oneMean_Power_VsEffectSizeView.doTheGraph();
        } else {  
            pVsESContainingPane.setVisible(false);  
        }
        
        if (checkBoxSettings[2] == true) {
            pVsNContainingPane.setVisible(true);
            oneMean_Power_VsSampleSizeView.doTheGraph();
        } else { 
            pVsNContainingPane.setVisible(false); 
        }
       
        if (checkBoxSettings[3] == true) {
            pVsAlphaContainingPane.setVisible(true);
            oneMean_Power_VsAlphaView.doTheGraph();
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
        oneMean_Power_Model.restoreNullValues();
        initHeight[0] = 500.0;
        oneMean_Power_PdfView = new OneMean_Power_PdfView(oneMean_Power_Model, this,
                                        sixteenths_across[0], sixteenths_down[0],
                                        initWidth[0], initHeight[0]);
        oneMean_Power_PdfView.makeItHappen();
        oneMean_Power_Model.restoreNullValues();
        oneMean_Power_VsEffectSizeView = new OneMean_Power_VsEffectSizeView(oneMean_Power_Model, this,
                                                      sixteenths_across[1], sixteenths_down[1],
                                                      initWidth[1], initHeight[1]);
        oneMean_Power_VsEffectSizeView.makeItHappen();
        oneMean_Power_Model.restoreNullValues();
        
        initWidth[2] = 425;
        oneMean_Power_VsSampleSizeView = new OneMean_Power_VsSampleSizeView(oneMean_Power_Model, this,
                                                     sixteenths_across[2], sixteenths_down[2],
                                                     initWidth[2], initHeight[2]);           
        oneMean_Power_Model.restoreNullValues();
        oneMean_Power_VsAlphaView = new OneMean_Power_VsAlphaView(oneMean_Power_Model, this,
                                                     sixteenths_across[3], sixteenths_down[3],
                                                     initWidth[3], initHeight[3]); 
        
        initWidth[4] = 300.0;
        oneMean_Power_PrintReport_View = new OneMean_Power_PrintReport_View(oneMean_Power_Model, this,
                                                     sixteenths_across[4], sixteenths_down[4],
                                                     initWidth[4], initHeight[4]);  
        oneMean_Power_PrintReport_View.completeTheDeal();        
        
        oneMean_Power_Model.restoreNullValues();
        pdfViewContainingPane = oneMean_Power_PdfView.getTheContainingPane();
        pVsESContainingPane = oneMean_Power_VsEffectSizeView.getTheContainingPane();
        pVsNContainingPane = oneMean_Power_VsSampleSizeView.getTheContainingPane();
        pVsAlphaContainingPane = oneMean_Power_VsAlphaView.getTheContainingPane();
        prntReportContainingPane = oneMean_Power_PrintReport_View.getTheContainingPane();

        backGround.getChildren().addAll(pdfViewContainingPane,
                                        pVsESContainingPane, 
                                        pVsNContainingPane,
                                        pVsAlphaContainingPane,
                                        prntReportContainingPane);         
    }
    
    public OneMean_Power_Controller getController() { return oneMean_Power_Controller; }
}
