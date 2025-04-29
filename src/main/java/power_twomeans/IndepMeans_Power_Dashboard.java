/**************************************************
 *          IndepMeans_Power_Dashboard            *
 *                  01/15/25                      *
 *                    21:00                       *
 *************************************************/
/**************************************************
*    Initial widths and heights from Super Class  *
*              are 400 and 350                    *
**************************************************/
package power_twomeans;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import superClasses.*;

public class IndepMeans_Power_Dashboard extends Dashboard {
   
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    // My classes
    IndepMeans_Power_PdfView indepMeans_Power_PdfView;
    IndepMeans_Power_VsEffectSizeView indepMeans_Power_VsEffectSizeView;
    IndepMeans_Power_VsSampleSizeView indepMeans_PowerVs_SampleSizeView;
    IndepMeans_Power_VsAlphaView indepMeans_Power_VsAlphaView; 
    IndepMeans_Power_PrintReport_View indepMeans_Power_PrintReport_View;

    // FX  
    Pane pdfViewContainingPane, pVsESContainingPane, pVsNContainingPane,
            pVsAlphaContainingPane, prntReportContainingPane; 

    IndepMeans_Power_Controller indepMeans_Power_Controller;
    IndepMeans_Power_Model indepMeans_Power_Model;  
    
    public IndepMeans_Power_Dashboard(IndepMeans_Power_Controller indepMeans_Power_Controller) {
        super(5);
        if (printTheStuff == true) {
            System.out.println("39 *** IndepMeans_Power_Dashboard, Constructing");
        }
        this.indepMeans_Power_Controller = indepMeans_Power_Controller;
    }
    
    public void initializeFurther() {
        indepMeans_Power_Model = indepMeans_Power_Controller.get_power_Model_Z();
        indepMeans_Power_Model.printModelStuff();
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
            
            if (checkBoxes[ithCheckBox].isSelected() == true) {
                checkBoxes[ithCheckBox].setTextFill(Color.GREEN);
            }
            else { checkBoxes[ithCheckBox].setTextFill(Color.RED); }
        }
        setTitle("Power, Singe Mean, Sigma known");  
    }

    
    public void putEmAllUp() { 
        if (checkBoxSettings[0] == true) {
            pdfViewContainingPane.setVisible(true);
            indepMeans_Power_PdfView.doTheGraph();
        }
        else { pdfViewContainingPane.setVisible(false);  }
        
        
        if (checkBoxSettings[1] == true) {
            pVsESContainingPane.setVisible(true);
            indepMeans_Power_VsEffectSizeView.doTheGraph();
        }
        else { pVsESContainingPane.setVisible(false);  }
        
        if (checkBoxSettings[2] == true) {
            pVsNContainingPane.setVisible(true);
            indepMeans_PowerVs_SampleSizeView.doTheGraph();
        }
        else { pVsNContainingPane.setVisible(false);   }
       
        if (checkBoxSettings[3] == true) {
            pVsAlphaContainingPane.setVisible(true);
            indepMeans_Power_VsAlphaView.doTheGraph();
        }
        else { pVsAlphaContainingPane.setVisible(false); }
        
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
        indepMeans_Power_Model.restoreNullValues();
        initHeight[0] = 500.0;
        indepMeans_Power_PdfView = new IndepMeans_Power_PdfView(indepMeans_Power_Model, this,
                                        sixteenths_across[0], sixteenths_down[0],
                                        initWidth[0], initHeight[0]);
        indepMeans_Power_PdfView.makeItHappen();
        indepMeans_Power_Model.restoreNullValues();
        indepMeans_Power_VsEffectSizeView = new IndepMeans_Power_VsEffectSizeView(indepMeans_Power_Model, this,
                                                      sixteenths_across[1], sixteenths_down[1],
                                                      initWidth[1], initHeight[1]);
        indepMeans_Power_VsEffectSizeView.makeItHappen();
        indepMeans_Power_Model.restoreNullValues();
        initWidth[2] = 800.0;
        initHeight[2] = 625.0; 
        indepMeans_PowerVs_SampleSizeView = new IndepMeans_Power_VsSampleSizeView(indepMeans_Power_Model, this,
                                                     sixteenths_across[2], sixteenths_down[2],
                                                     initWidth[2], initHeight[2]);           
        indepMeans_Power_Model.restoreNullValues();
        
        indepMeans_Power_VsAlphaView = new IndepMeans_Power_VsAlphaView(indepMeans_Power_Model, this,
                                                     sixteenths_across[3], sixteenths_down[3],
                                                     initWidth[3], initHeight[3]); 
        
        initWidth[4] = 300.0;
        indepMeans_Power_PrintReport_View = new IndepMeans_Power_PrintReport_View(indepMeans_Power_Model, this,
                                                     sixteenths_across[4], sixteenths_down[4],
                                                     initWidth[4], initHeight[4]);  
        indepMeans_Power_PrintReport_View.completeTheDeal();        
        
        indepMeans_Power_Model.restoreNullValues();
        pdfViewContainingPane = indepMeans_Power_PdfView.getTheContainingPane();
        pVsESContainingPane = indepMeans_Power_VsEffectSizeView.getTheContainingPane();
        pVsNContainingPane = indepMeans_PowerVs_SampleSizeView.getTheContainingPane();
        pVsAlphaContainingPane = indepMeans_Power_VsAlphaView.getTheContainingPane();
        prntReportContainingPane = indepMeans_Power_PrintReport_View.getTheContainingPane();

        backGround.getChildren().addAll(pdfViewContainingPane,
                                        pVsESContainingPane, 
                                        pVsNContainingPane,
                                        pVsAlphaContainingPane,
                                        prntReportContainingPane);         
    }
    
    public IndepMeans_Power_Controller getController() { return indepMeans_Power_Controller; }
     
}

