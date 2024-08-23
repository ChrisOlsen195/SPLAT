/**************************************************
 *          TwoProps_Power_Dashboard              *
 *                  05/30/24                      *
 *                    00:00                       *
 *************************************************/
/**************************************************
*    Initial widths and heights from Super Class  *
*              are 675 and 375                    *
**************************************************/
package power_twoprops;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import superClasses.*;

public class IndepProps_Power_Dashboard extends Dashboard {
   
    // POJOs
    
    // My classes
    IndepProps_Power_PdfView indepProps_Power_PdfView;
    IndepProps_Power_VsEffectSizeView indepProps_Power_VsEffectSizeView;
    IndepProps_Power_VsSampleSizeView indepProps_Power_VsSampleSizeView;
    IndepProps_Power_VsAlphaView indepProps_Power_VsAlphaView; 
    IndepProps_Power_PrintReport_View indepProps_Power_PrintReport_View;

    // FX
    IndepProps_Power_Controller indepProps_Power_Controller;
    IndepProps_Power_Model indepProps_Power_Model;     
    Pane pdfViewContainingPane, pVsESContainingPane, pVsNContainingPane,
            pVsAlphaContainingPane, prntReportContainingPane; 
    
    public IndepProps_Power_Dashboard(IndepProps_Power_Controller power_Controller) {
        super(5);
        //System.out.println("35 IndepProps_Power_Dashboard, constructing");
        this.indepProps_Power_Controller = power_Controller;
    }
    
    public void initializeFurther() {
        indepProps_Power_Model = indepProps_Power_Controller.get_power_Model_Z();
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
        setTitle("Power, Singe Mean, Sigma known");  
    }

    
    public void putEmAllUp() { 
        if (checkBoxSettings[0] == true) {
            pdfViewContainingPane.setVisible(true);
            indepProps_Power_PdfView.doTheGraph();
        }
        else { pdfViewContainingPane.setVisible(false);  }
        
        
        if (checkBoxSettings[1] == true) {
            pVsESContainingPane.setVisible(true);
            indepProps_Power_VsEffectSizeView.doTheGraph();
        }
        else { pVsESContainingPane.setVisible(false);  }
        
        if (checkBoxSettings[2] == true) {
            pVsNContainingPane.setVisible(true);
            indepProps_Power_VsSampleSizeView.doTheGraph();
        }
        else { pVsNContainingPane.setVisible(false);  }
       
        if (checkBoxSettings[3] == true) {
            pVsAlphaContainingPane.setVisible(true);
            indepProps_Power_VsAlphaView.doTheGraph();
        }
        else {  pVsAlphaContainingPane.setVisible(false);   }
        
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
        indepProps_Power_Model.restoreNullValues();
        initHeight[0] = 500.0;
        indepProps_Power_PdfView = new IndepProps_Power_PdfView(indepProps_Power_Model, this,
                                        sixteenths_across[0], sixteenths_down[0],
                                        initWidth[0], initHeight[0]);
        indepProps_Power_PdfView.makeItHappen();
        indepProps_Power_Model.restoreNullValues();
        indepProps_Power_VsEffectSizeView = new IndepProps_Power_VsEffectSizeView(indepProps_Power_Model, this,
                                                      sixteenths_across[1], sixteenths_down[1],
                                                      initWidth[1], initHeight[1]);
        indepProps_Power_VsEffectSizeView.makeItHappen();
        indepProps_Power_Model.restoreNullValues();
        indepProps_Power_VsSampleSizeView = new IndepProps_Power_VsSampleSizeView(indepProps_Power_Model, this,
                                                     sixteenths_across[2], sixteenths_down[2],
                                                     initWidth[2], initHeight[2]);           
        indepProps_Power_Model.restoreNullValues();
        
        indepProps_Power_VsAlphaView = new IndepProps_Power_VsAlphaView(indepProps_Power_Model, this,
                                                     sixteenths_across[3], sixteenths_down[3],
                                                     initWidth[3], initHeight[3]); 
        
        initWidth[4] = 300.0;
        indepProps_Power_PrintReport_View = new IndepProps_Power_PrintReport_View(indepProps_Power_Model, this,
                                                     sixteenths_across[4], sixteenths_down[4],
                                                     initWidth[4], initHeight[4]);  
        indepProps_Power_PrintReport_View.completeTheDeal();        
        
        indepProps_Power_Model.restoreNullValues();
        pdfViewContainingPane = indepProps_Power_PdfView.getTheContainingPane();
        pVsESContainingPane = indepProps_Power_VsEffectSizeView.getTheContainingPane();
        pVsNContainingPane = indepProps_Power_VsSampleSizeView.getTheContainingPane();
        pVsAlphaContainingPane = indepProps_Power_VsAlphaView.getTheContainingPane();
        prntReportContainingPane = indepProps_Power_PrintReport_View.getTheContainingPane();

        backGround.getChildren().addAll(pdfViewContainingPane,
                                        pVsESContainingPane, 
                                        pVsNContainingPane,
                                        pVsAlphaContainingPane,
                                        prntReportContainingPane);         
    }
    
    public IndepProps_Power_Controller getController() { return indepProps_Power_Controller; }     
}

