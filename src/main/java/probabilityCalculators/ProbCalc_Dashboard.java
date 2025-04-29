/**************************************************
 *              ProbCalc_Dashboard                *
 *                    01/16/25                    *
 *                     09:00                      *
 *************************************************/
/**************************************************
*    Initial widths and heights from Super Class  *
*              are 400 and 350                    *
**************************************************/
package probabilityCalculators;

import superClasses.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class ProbCalc_Dashboard extends Dashboard {
    // POJOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    final String[] regrCheckBoxDescr = { " Normal ", " t ",
                                         " Chi square ", " Binomial ",
                                         " Geometric "};
    
    // My classes

    BootstrapDist_Calc_PDFView normal_Calc_PDFView;
    NormalDist_Calc_DialogView normalDist_Calc_DialogView;
    
    TDist_Calc_PDFView tDist_Calc_PDFView;
    TDist_Calc_DialogView tDist_Calc_DialogView;
    
    X2Dist_Calc_PDFView x2Dist_Calc_PDFView;
    X2Dist_Calc_DialogView x2Dist_Calc_DialogView;
    
    BinomialDist_Calc_PDFView binomialDist_Calc_PDFView;
    BinomialDist_Calc_DialogView binomialDist_Calc_DialogView;
    
    GeometricDist_Calc_PDFView geometricDist_Calc_PDFView;
    GeometricDist_Calc_DialogView geometricDist_Calc_DialogView;

    // POJOs / FX
    
    Pane normalDistContainingPane, tDistContainingPane,
         x2DistContainingPane, binomialDistContainingPane,
         geometricDistContainingPane; 
    
    Pane normalCalcContainingPane, tCalcContainingPane,
         x2CalcContainingPane, binomialCalcContainingPane,
         geometricCalcContainingPane;
            
    public ProbCalc_Dashboard(ProbCalc_Controller probCalc_Controller) {
        super(5);  // nCheckBoxes = 5;
        if (printTheStuff == true) {
            System.out.println("55 *** ProbCalc_Dashboard, Constructing");
        }
        checkBoxDescr = new String[nCheckBoxes];
        
        for (int ithCheckBox = 0; ithCheckBox < nCheckBoxes; ithCheckBox++) {
            checkBoxDescr[ithCheckBox] = regrCheckBoxDescr[ithCheckBox];
            checkBoxes[ithCheckBox].setText(checkBoxDescr[ithCheckBox]);
            checkBoxes[ithCheckBox].setId(checkBoxDescr[ithCheckBox]);
            
            if (checkBoxes[ithCheckBox].isSelected() == true) {
                checkBoxes[ithCheckBox].setTextFill(Color.GREEN);
            }
            else {
                checkBoxes[ithCheckBox].setTextFill(Color.RED);
            }
        }
        setTitle("Probability calculations dashboard"); 
        
        /******************************************************************
         *    Re-use of these arrays from usual Dashboard!!!              *
         *****************************************************************/
         initWidth = new double[10];
         initHeight = new double[10];
         sixteenths_across = new double[10]; 
         sixteenths_down = new double[10];  
         initWidth = new double[10]; 
         initHeight = new double[10]; 
        /******************************************************************
         *    Re-use of these arrays from usual Dashboard!!!              *
         *****************************************************************/
    }  
    
    public void putEmAllUp() { 

        if (checkBoxSettings[0] == true) {
            normalDistContainingPane.setVisible(true);
            normalCalcContainingPane.setVisible(true);
            normal_Calc_PDFView.doTheGraph();
        }
        else {
            normalDistContainingPane.setVisible(false);
            normalCalcContainingPane.setVisible(false);
        }

        if (checkBoxSettings[1] == true) {
            tDistContainingPane.setVisible(true);
            tCalcContainingPane.setVisible(true);
            tDist_Calc_PDFView.doTheGraph();
        }
        else {
            tDistContainingPane.setVisible(false);
            tCalcContainingPane.setVisible(false);
        }
        
        if (checkBoxSettings[2] == true) {
            x2DistContainingPane.setVisible(true);
            x2CalcContainingPane.setVisible(true);
            x2Dist_Calc_PDFView.doTheGraph();
        }
        else {
            x2DistContainingPane.setVisible(false);
            x2CalcContainingPane.setVisible(false);
        }
             
        if (checkBoxSettings[3] == true) {
            binomialDistContainingPane.setVisible(true);
            binomialCalcContainingPane.setVisible(true);
            binomialDist_Calc_PDFView.doTheGraph();
        }
        else {
            binomialDistContainingPane.setVisible(false);
            binomialCalcContainingPane.setVisible(false);
        }        
       
        if (checkBoxSettings[4] == true) {
            geometricDistContainingPane.setVisible(true);
            geometricCalcContainingPane.setVisible(true);
            geometricDist_Calc_PDFView.doTheGraph();
        }
        else {
            geometricDistContainingPane.setVisible(false); 
            geometricCalcContainingPane.setVisible(false);
        }

    }
    
    // *****************************************************************
    // *  IMPORTANT NOTE:  The DialogViews must be constructed before  *
    // * the PDFViews b/c the PDFs need access to the Dialogs during   *
    // * the construction of the PDFViews.                             *
    // *****************************************************************
    
    public void populateTheBackGround() {
        
        //  ******************    Normal  *********************************        
        initWidth[0] = 475; initHeight[0] = 350;
        sixteenths_across[0] = 375; sixteenths_down[0] = 450;  
        normalDist_Calc_DialogView = new NormalDist_Calc_DialogView(this, sixteenths_across[0], sixteenths_down[0], initWidth[0], initHeight[0]);
        normalDist_Calc_DialogView.completeTheDeal();
        normalCalcContainingPane = normalDist_Calc_DialogView.getTheContainingPane();       
        
        initWidth[1] = 600; initHeight[1] = 375;
        sixteenths_across[1] = 100; sixteenths_down[1] = 50; 
        normal_Calc_PDFView = new BootstrapDist_Calc_PDFView(this, sixteenths_across[1], sixteenths_down[1], initWidth[1], initHeight[1]);
        normal_Calc_PDFView.completeTheDeal();
        normalDistContainingPane = normal_Calc_PDFView.getTheContainingPane(); 
        normalDistContainingPane.setStyle(containingPaneStyle);
        
        normalDist_Calc_DialogView.setNormal_PDFView(normal_Calc_PDFView);
        
        //  ******************    t  *********************************        
        initWidth[2] = 475; initHeight[2] = 350;
        sixteenths_across[2] = 375; sixteenths_down[2] = 450;  
        tDist_Calc_DialogView = new TDist_Calc_DialogView(this, sixteenths_across[2], sixteenths_down[2], initWidth[2], initHeight[2]);
        tDist_Calc_DialogView.completeTheDeal();
        tCalcContainingPane = tDist_Calc_DialogView.getTheContainingPane(); 
      
        initWidth[3] = 600; initHeight[3] = 375;
        sixteenths_across[3] = 100; sixteenths_down[3] = 50; 
        tDist_Calc_PDFView = new TDist_Calc_PDFView(this, sixteenths_across[3], sixteenths_down[3], initWidth[3], initHeight[3]);
        tDist_Calc_PDFView.completeTheDeal();
        tDistContainingPane = tDist_Calc_PDFView.getTheContainingPane(); 
        tDistContainingPane.setStyle(containingPaneStyle); 
        
        tDist_Calc_DialogView.set_t_PDFView(tDist_Calc_PDFView);        
        
        //  ******************   x2  *********************************
        initWidth[4] = 475; initHeight[4] = 350;
        sixteenths_across[4] = 375; sixteenths_down[4] = 450;  
        x2Dist_Calc_DialogView = new X2Dist_Calc_DialogView(this, sixteenths_across[4], sixteenths_down[4], initWidth[4], initHeight[4]);
        x2Dist_Calc_DialogView.completeTheDeal();
        x2CalcContainingPane = x2Dist_Calc_DialogView.getTheContainingPane(); 
      
        initWidth[5] = 600; initHeight[5] = 375;
        sixteenths_across[5] = 100; sixteenths_down[5] = 50; 
        x2Dist_Calc_PDFView = new X2Dist_Calc_PDFView(this, sixteenths_across[5], sixteenths_down[5], initWidth[5], initHeight[5]);
        x2Dist_Calc_PDFView.completeTheDeal();
        x2DistContainingPane = x2Dist_Calc_PDFView.getTheContainingPane(); 
        x2DistContainingPane.setStyle(containingPaneStyle); 
        
        x2Dist_Calc_DialogView.set_X2_PDFView(x2Dist_Calc_PDFView);  
        
        //  ******************   Binomial  *********************************      
        initWidth[6] = 525; initHeight[6] = 350;
        sixteenths_across[6] = 375; sixteenths_down[6] = 450;  
        binomialDist_Calc_DialogView = new BinomialDist_Calc_DialogView(this, sixteenths_across[6], sixteenths_down[6], initWidth[6], initHeight[6]);
        binomialDist_Calc_DialogView.completeTheDeal();
        binomialCalcContainingPane = binomialDist_Calc_DialogView.getTheContainingPane(); 
        
        initWidth[7] = 600; initHeight[7] = 375;
        sixteenths_across[7] = 100; sixteenths_down[7] = 50;
        binomialDist_Calc_PDFView = new BinomialDist_Calc_PDFView(this, sixteenths_across[7], sixteenths_down[7], initWidth[7], initHeight[7]);
        binomialDist_Calc_PDFView.completeTheDeal();        
        binomialDistContainingPane = binomialDist_Calc_PDFView.getTheContainingPane();  
        binomialDistContainingPane.setStyle(containingPaneStyle);
        
        binomialDist_Calc_DialogView.set_Binomial_PDFView(binomialDist_Calc_PDFView);  
        
        //  ******************   Geometric  *********************************        
        initWidth[8] = 525; initHeight[8] = 350;
        sixteenths_across[8] = 375; sixteenths_down[8] = 450;  
        geometricDist_Calc_DialogView = new GeometricDist_Calc_DialogView(this, sixteenths_across[8], sixteenths_down[8], initWidth[8], initHeight[8]);
        geometricDist_Calc_DialogView.completeTheDeal();
        geometricCalcContainingPane = geometricDist_Calc_DialogView.getTheContainingPane(); 
        
        initWidth[9] = 600; initHeight[9] = 375;
        sixteenths_across[9] = 100; sixteenths_down[9] = 50;
        geometricDist_Calc_PDFView = new GeometricDist_Calc_PDFView(this, sixteenths_across[9], sixteenths_down[9], initWidth[9], initHeight[9]);
        geometricDist_Calc_PDFView.completeTheDeal();        
        geometricDistContainingPane = geometricDist_Calc_PDFView.getTheContainingPane();  
        geometricDistContainingPane.setStyle(containingPaneStyle);
        
        geometricDist_Calc_DialogView.set_Geometric_PDFView(geometricDist_Calc_PDFView);  
        
        backGround.getChildren().addAll(normalDistContainingPane,
                                        normalCalcContainingPane,
                                        tDistContainingPane,
                                        tCalcContainingPane,
                                        x2DistContainingPane,
                                        x2CalcContainingPane,
                                        binomialDistContainingPane,
                                        binomialCalcContainingPane,
                                        geometricDistContainingPane,
                                        geometricCalcContainingPane);   
    }
    
    public BootstrapDist_Calc_PDFView get_Normal_PDFView() { return normal_Calc_PDFView; }
    public NormalDist_Calc_DialogView get_Normal_DialogView() { return normalDist_Calc_DialogView; }
    
    public TDist_Calc_PDFView get_T_PDFView() { return tDist_Calc_PDFView; }
    public TDist_Calc_DialogView get_T_DialogView() { return tDist_Calc_DialogView; }
    
    public X2Dist_Calc_PDFView get_X2_PDFView() { return x2Dist_Calc_PDFView; }
    public X2Dist_Calc_DialogView get_X2_DialogView() { return x2Dist_Calc_DialogView; }
    
    public BinomialDist_Calc_PDFView get_Binomial_PDFView() { return binomialDist_Calc_PDFView; }
    public BinomialDist_Calc_DialogView get_Binomial_DialogView() { return binomialDist_Calc_DialogView; }
    
    public GeometricDist_Calc_PDFView get_Geometric_PDFView() { return geometricDist_Calc_PDFView; }
    public GeometricDist_Calc_DialogView get_Geometric_DialogView() { return geometricDist_Calc_DialogView; }
}

