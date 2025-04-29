/**************************************************
 *            OneProp_Power_Dashboard             *
 *                  01/15/25                      *
 *                    21:00                       *
 *************************************************/
/**************************************************
*    Initial widths and heights from Super Class  *
*              are 400 and 350                    *
**************************************************/
package power_OneProp;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import superClasses.*;

public class OneProp_Power_Dashboard extends Dashboard {
    // POJOs
    
    //boolean printTheStuff = true;
    boolean printTheStuff = false;

    // My Classes
    OneProp_Power_PdfView oneProp_Power_PdfView;
    OneProp_Power_VsEffectSizeView oneProp_Power_VsEffectSizeView;
    OneProp_Power_VsSampleSizeView oneProp_Power_VsSampleSizeView;
    OneProp_Power_VsAlphaView oneProp_Power_VsAlphaView; 
    OneProp_Power_PrintReportView oneProp_Power_PrintReportView;   
 
    Pane pdfViewContainingPane, pVsESContainingPane, pVsNContainingPane,
            pVsAlphaContainingPane, prntReportContainingPane; 

    OneProp_Power_Controller oneProp_Power_Controller;
    OneProp_Power_Model oneProp_Power_Model;  
    
    public OneProp_Power_Dashboard(OneProp_Power_Controller oneProp_Power_Controller) {
        super(5);
        if (printTheStuff == true) {
            System.out.println("38 *** OneProp_Power_Dashboard, Constructing");
        }
        this.oneProp_Power_Controller = oneProp_Power_Controller;
    }
    
    public void initializeFurther() {
        oneProp_Power_Model = oneProp_Power_Controller.get_power_Model_Z();
        oneProp_Power_Model.printModelStuff();
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
        setTitle("Power, Single Proportion");  
    }

    public void putEmAllUp() { 
        if (checkBoxSettings[0] == true) {
            pdfViewContainingPane.setVisible(true);
            oneProp_Power_PdfView.doTheGraph();
        }
        else { pdfViewContainingPane.setVisible(false); }
        
        
        if (checkBoxSettings[1] == true) {
            pVsESContainingPane.setVisible(true);
            oneProp_Power_VsEffectSizeView.doTheGraph();
        }
        else { pVsESContainingPane.setVisible(false); }
        
        if (checkBoxSettings[2] == true) {
            pVsNContainingPane.setVisible(true);
            oneProp_Power_VsSampleSizeView.doTheGraph();
        }
        else { pVsNContainingPane.setVisible(false);  }
       
        if (checkBoxSettings[3] == true) {
            pVsAlphaContainingPane.setVisible(true);
            oneProp_Power_VsAlphaView.doTheGraph();
        }
        else { pVsAlphaContainingPane.setVisible(false);  }
        
        if (checkBoxSettings[4] == true) {
            prntReportContainingPane.setVisible(true);
        }
        else { prntReportContainingPane.setVisible(false); }
    }

    public void makeTheBackGround() {
        backGround = new Pane();
        backGround.setStyle("-fx-background-color: lightblue;");
        backGroundHeight = dashHeight /* - titleTextHeight - checkBoxHeight */;
        backGround.setPrefSize(dashWidth, backGroundHeight);  
    }
    
    public void populateTheBackGround() {
        //String[] checkBoxDescr = { "Z-test", "HBoxPlot", "VBoxPlot"};  
        oneProp_Power_Model.restoreNullValues();
        initHeight[0] = 500.0;
        oneProp_Power_PdfView = new OneProp_Power_PdfView(oneProp_Power_Model, this,
                                        sixteenths_across[0], sixteenths_down[0],
                                        initWidth[0], initHeight[0]);
        oneProp_Power_PdfView.makeItHappen();
        oneProp_Power_Model.restoreNullValues();
        oneProp_Power_VsEffectSizeView = new OneProp_Power_VsEffectSizeView(oneProp_Power_Model, this,
                                                      sixteenths_across[1], sixteenths_down[1],
                                                      initWidth[1], initHeight[1]);
        oneProp_Power_VsEffectSizeView.makeItHappen();
        oneProp_Power_Model.restoreNullValues();
        oneProp_Power_VsSampleSizeView = new OneProp_Power_VsSampleSizeView(oneProp_Power_Model, this,
                                                     sixteenths_across[2], sixteenths_down[2],
                                                     initWidth[2], initHeight[2]);           
        oneProp_Power_Model.restoreNullValues();
        
        oneProp_Power_VsAlphaView = new OneProp_Power_VsAlphaView(oneProp_Power_Model, this,
                                                     sixteenths_across[3], sixteenths_down[3],
                                                     initWidth[3], initHeight[3]); 
        
        initWidth[4] = 300.0;
        oneProp_Power_PrintReportView = new OneProp_Power_PrintReportView(oneProp_Power_Model, this,
                                                     sixteenths_across[4], sixteenths_down[4],
                                                     initWidth[4], initHeight[4]);  
        oneProp_Power_PrintReportView.completeTheDeal();        
        
        oneProp_Power_Model.restoreNullValues();
        pdfViewContainingPane = oneProp_Power_PdfView.getTheContainingPane();
        pVsESContainingPane = oneProp_Power_VsEffectSizeView.getTheContainingPane();
        pVsNContainingPane = oneProp_Power_VsSampleSizeView.getTheContainingPane();
        pVsAlphaContainingPane = oneProp_Power_VsAlphaView.getTheContainingPane();
        prntReportContainingPane = oneProp_Power_PrintReportView.getTheContainingPane();

        backGround.getChildren().addAll(pdfViewContainingPane,
                                        pVsESContainingPane, 
                                        pVsNContainingPane,
                                        pVsAlphaContainingPane,
                                        prntReportContainingPane);         
    }
    
    public OneProp_Power_Controller getController() { return oneProp_Power_Controller; }    
}
