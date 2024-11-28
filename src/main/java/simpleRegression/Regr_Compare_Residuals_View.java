/**************************************************
 *           Regr_Compare_Residuals_View          *
 *                    11/27/24                    *
 *                      12:00                     *
 *************************************************/
package simpleRegression;

import dataObjects.CatQuantDataVariable;
import javafx.geometry.Side;
import javafx.scene.SnapshotParameters;
import superClasses.BivariateScale_W_CheckBoxes_View;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import utilityClasses.Colors_and_CSS_Strings;

public class Regr_Compare_Residuals_View extends BivariateScale_W_CheckBoxes_View {
    // POJOs
    
    int nCases, nLevels;
    
    double dotRadius, dotDiameter; 
    
    double[] daCovariates, r_Student, daLevels;

    String[] strLevels;
    
    // My classes 
    Regr_Compare_Controller ancova_Controller;
    
    CatQuantDataVariable cqdv;
    
    //  POJO / FX
    public Color[] graphColors; 
    Pane theContainingPane;
    
    public Regr_Compare_Residuals_View(Regr_Compare_Model ancova_Model, 
                                       Regr_Compare_Dashboard ancova_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {        
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);  
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        ancova_Controller = ancova_Model.getRegrCompare_Controller();
        nCases = ancova_Model.getNCases();
        nLevels = ancova_Model.getNLevels();
        strLevels = new String[nLevels];
        daCovariates = new double[nCases];
        daLevels = ancova_Model.getDaLevels();
        r_Student = new double[nCases];
        daCovariates = ancova_Model.getDaCovariates();
        cqdv = ancova_Model.getRegrCompare_Controller().get_CatQuant_DV();
        r_Student = ancova_Model.getStudentizedResiduals();
        nCheckBoxes = 0;
        
        graphColors = Colors_and_CSS_Strings.getGraphColors_02();
        graphsCSS = getClass().getClassLoader().getResource("Graphs.css").toExternalForm();
   
        dotRadius = 5.0;
        dotDiameter =  2.0 * dotRadius;
        
        txtTitle1 = new Text(50, 25, " Residual plot ");
        String tempStr = ancova_Controller.get_CovariateName();
        String strTxtTitle2 = "Residuals vs. " + tempStr;
        txtTitle2 = new Text (60, 45, strTxtTitle2);
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15));   
        
        checkBoxHeight = 350.0;
        graphCanvas = new Canvas(initWidth, initHeight);        
        makeTheCheckBoxes();    
        makeItHappen();
    }  
  
    private void makeItHappen() {       
        theContainingPane = new Pane();
        gc = graphCanvas.getGraphicsContext2D();
        gc.setFont(Font.font("Times New Roman", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 14));
        graphCanvas.heightProperty().addListener(ov-> {doTheGraph();});
        graphCanvas.widthProperty().addListener(ov-> {doTheGraph();});
    }
        
    public void completeTheDeal() { 
        residvInitializeGraphParameters();
        setUpUI();       
        setUpAnchorPane();
        setHandlers();
        doTheGraph();           
        theContainingPane = dragableAnchorPane.getTheContainingPane();       
    }
    
    public void residvInitializeGraphParameters() { 
        //System.out.println("104 BivariateScale_W_CheckBoxes_View, initializeGraphParameters()");
        residsConstructDataArray();
        xAxis = new genericClasses.JustAnAxis(xDataMin, xDataMax);
        xAxis.setSide(Side.BOTTOM); 
        yAxis = new genericClasses.JustAnAxis(yDataMin, yDataMax);
        yAxis.setSide(Side.LEFT);
        newX_Lower = xDataMin; newX_Upper = xDataMax;
        double smidgen = 0.05 * (yDataMax - yDataMin);  // lower dots under r
        newY_Lower = yDataMin; newY_Upper = yDataMax + smidgen;
        xAxis.setLowerBound(newX_Lower ); 
        xAxis.setUpperBound(newX_Upper );
        yAxis.setLowerBound(newY_Lower ); 
        yAxis.setUpperBound(newY_Upper );       
        
        for (int ithLevel = 0; ithLevel < nLevels; ithLevel++) {
            strLevels[ithLevel] = cqdv.getIthCatQuantPair_Cat(ithLevel);
        }
    }
    
    public void residsConstructDataArray() {
        //System.out.println("124 *** ANCOVA_BestFit_View, residsConstructDataArray()");
        
        xDataMin = daCovariates[0];
        xDataMax = daCovariates[0];
        yDataMin = r_Student[0];
        yDataMax = r_Student[0];
        
        for (int ithCase = 0; ithCase < nCases; ithCase++) {
            if (daCovariates[ithCase] < xDataMin) { xDataMin = daCovariates[ithCase]; }
            if (daCovariates[ithCase] > xDataMax) { xDataMax = daCovariates[ithCase]; }
            if (r_Student[ithCase] < yDataMin) { yDataMin = r_Student[ithCase]; }
            if (r_Student[ithCase] > yDataMax) { yDataMax = r_Student[ithCase]; }
        }
        
        xRange = xDataMax - xDataMin;
        yRange = yDataMax - yDataMin;
        
        //  Make room for the circles
        xDataMin = xDataMin - .02 * xRange; xDataMax = xDataMax + .02 * xRange;
        yDataMin = yDataMin - .02 * yRange; yDataMax = yDataMax + .02 * yRange;  
    }
   
    public void doTheGraph() {      
        
        double xDisplayStartKey, xDisplayStopKey, yDisplayStartKey, 
               yDisplayStopKey, xlb, yub;
        double text1Width = txtTitle1.getLayoutBounds().getWidth();
        double text2Width = txtTitle2.getLayoutBounds().getWidth();
        double paneWidth = dragableAnchorPane.getWidth();
        double txt1Edge = (paneWidth - text1Width) / (2 * paneWidth);
        double txt2Edge = (paneWidth - text2Width) / (2 * paneWidth);
        
        xlb = xAxis.getLB(); 
        yub = yAxis.getUpperBound();
        
        double tempHeight = dragableAnchorPane.getHeight();
        double tempWidth = dragableAnchorPane.getWidth();
        
        AnchorPane.setTopAnchor(txtTitle1, 0.06 * tempHeight);
        AnchorPane.setLeftAnchor(txtTitle1, txt1Edge * tempWidth);
        AnchorPane.setRightAnchor(txtTitle1, txt1Edge * tempWidth);
        AnchorPane.setBottomAnchor(txtTitle1, 0.10 * tempHeight);
                
        AnchorPane.setTopAnchor(txtTitle2, 0.11 * tempHeight);
        AnchorPane.setLeftAnchor(txtTitle2, txt2Edge * tempWidth);
        AnchorPane.setRightAnchor(txtTitle2, txt2Edge * tempWidth);
        AnchorPane.setBottomAnchor(txtTitle2, 0.2 * tempHeight);
        
        AnchorPane.setTopAnchor(xAxis, 0.9 * tempHeight);
        AnchorPane.setLeftAnchor(xAxis, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(xAxis, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(xAxis, 0.0 * tempHeight);
        
        AnchorPane.setTopAnchor(yAxis, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(yAxis, 0.0 * tempWidth);
        AnchorPane.setRightAnchor(yAxis, 0.9 * tempWidth);
        AnchorPane.setBottomAnchor(yAxis, 0.1 * tempHeight);
        
        AnchorPane.setTopAnchor(graphCanvas, 0.2 * tempHeight);
        AnchorPane.setLeftAnchor(graphCanvas, 0.1 * tempWidth);
        AnchorPane.setRightAnchor(graphCanvas, 0.0 * tempWidth);
        AnchorPane.setBottomAnchor(graphCanvas, 0.1 * tempHeight);
        
        for (int chex = 0; chex < nCheckBoxes; chex++) {
            AnchorPane.setLeftAnchor(scatterPlotCheckBoxes[chex], (chex - 1) * tempWidth / 250.0 - 25);
        }

        gc.clearRect(0, 0, graphCanvas.getWidth(), graphCanvas.getHeight());
        
        for (int ithLevel = 0; ithLevel < nLevels; ithLevel++) {
            double dbl_ithLevel = ithLevel;
            for (int ithCase = 0; ithCase < nCases; ithCase++) {
                double xx = xAxis.getDisplayPosition(daCovariates[ithCase]);
                double yy = yAxis.getDisplayPosition(r_Student[ithCase]);

                //  radius, diameter for centering the dots on point
                gc.setFill(graphColors[ithLevel]);
                if (daLevels[ithCase] == dbl_ithLevel) {
                    gc.fillOval(xx - dotRadius , yy - dotRadius , dotDiameter, dotDiameter); 
                }     
            }
            
        }   //  End nCases 
        
        for (int ithLevel = 0; ithLevel < nLevels; ithLevel++) {
            gc.setFill(graphColors[ithLevel]);
            gc.setLineWidth(2);

            if (nCases > 50) { gc.setLineWidth(4); }

            // Print the key
            xDisplayStartKey =  xAxis.getDisplayPosition(xlb) + 10;
            xDisplayStopKey =  xAxis.getDisplayPosition(xlb) + 30;
            yDisplayStartKey =  yAxis.getDisplayPosition(yub) + 10 + 20 * ithLevel;
            yDisplayStopKey =  yDisplayStartKey;

            gc.strokeLine(xDisplayStartKey, yDisplayStartKey, 
                          xDisplayStopKey, yDisplayStopKey);
            gc.fillText(strLevels[ithLevel], xDisplayStopKey + 10, yDisplayStartKey + 5);            
        } 

        double x1 = xAxis.getDisplayPosition(xDataMin);
        double y1 = yAxis.getDisplayPosition(0.0);
        double x2 = xAxis.getDisplayPosition(xDataMax);
        double y2 = yAxis.getDisplayPosition(0.0);
        gc.setLineWidth(2);

        if (nCases > 50) { gc.setLineWidth(4); }

        gc.setStroke(Color.BLACK);
        gc.strokeLine(x1, y1, x2, y2);  
            
        theContainingPane.requestFocus();
        theContainingPane.setOnKeyPressed((ke -> {
            KeyCode keyCode = ke.getCode();
            boolean doIt = ke.isControlDown() && (ke.getCode() == KeyCode.C);
            if (doIt) {
                WritableImage writableImage = theContainingPane.snapshot(new SnapshotParameters(), null);
                ImageView iv = new ImageView(writableImage);
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                content.put(DataFormat.IMAGE, writableImage);
                clipboard.setContent(content);
            }
        }));          
    }
    
   public Pane getTheContainingPane() { return theContainingPane; }
}


