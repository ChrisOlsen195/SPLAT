/**************************************************
 *            Regr_Compare_BestFit_View           *
 *                    09/19/24                    *
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
//import probabilityDistributions.*;
import splat.Data_Manager;
import utilityClasses.Colors_and_CSS_Strings;

public class Regr_Compare_BestFit_View extends BivariateScale_W_CheckBoxes_View {
    // POJOs
    int nCases, nLevels;
    
    double dotRadius,  dotDiameter;
    
    double[] daCovariates, daResponses, daLevels, slopesWithin, interceptsWithin; 
    
    public Color[] graphColors; 
    
    String xAxisLabel, yAxisLabel;
    String[] strLevels;
    
    String waldoFile = "";
    // String waldoFile = "Regr_Compare_BestFit_View";

    // My classes 
    CatQuantDataVariable cqdv;
    Data_Manager dm;
    
    //  POJO / FX
    Pane theContainingPane;

    public Regr_Compare_BestFit_View(Regr_Compare_Model ancova_Model, 
           Regr_Compare_Dashboard ancova_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {        
        super(placeHoriz, placeVert, withThisWidth, withThisHeight); 
        dm = ancova_Model.getDataManager();
        dm.whereIsWaldo(59, waldoFile, "Constructing");
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        xAxisLabel = ancova_Model.getRegrCompare_Controller().get_CovariateName();
        yAxisLabel = ancova_Model.getRegrCompare_Controller().get_ResponseName();
        nCases = ancova_Model.getNCases();
        nLevels = ancova_Model.getNLevels();
        daCovariates = new double[nCases];
        daResponses = new double[nCases];
        slopesWithin = new double[nLevels];
        interceptsWithin = new double[nLevels];
        strLevels = new String[nLevels];
        daCovariates = ancova_Model.getDaCovariates();
        daResponses = ancova_Model.getDaResponses();
        daLevels = ancova_Model.getDaLevels();
        cqdv = ancova_Model.getRegrCompare_Controller().get_CatQuant_DV();
        nCheckBoxes = 0;

        graphColors = Colors_and_CSS_Strings.getGraphColors_02();
        graphsCSS = getClass().getClassLoader().getResource("Graphs.css").toExternalForm();
           
        dotRadius = 5.0;
        dotDiameter =  2.0 * dotRadius;

        txtTitle1 = new Text(50, 25, " Scatterplot ");
        String strTitle2 = yAxisLabel + " vs. " + xAxisLabel;
        txtTitle2 = new Text (60, 45, strTitle2);

        checkBoxHeight = 350.0;
        slopesWithin = ancova_Model.getSlopesWithin();
        graphCanvas = new Canvas(initWidth, initHeight);  
        slopesWithin = ancova_Model.getSlopesWithin();
        interceptsWithin = ancova_Model.getInterceptsWithin();
        interceptsWithin = ancova_Model.getInterceptsWithin();
        makeTheCheckBoxes();    
        makeItHappen();
    }  
    
    public void bfvInitializeGraphParameters() { 
        dm.whereIsWaldo(98, waldoFile, "bfvInitializeGraphParameters()");
        bfvConstructDataArray();
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
  
    private void makeItHappen() {    
        dm.whereIsWaldo(118, waldoFile, "makeItHappen()");
        theContainingPane = new Pane();
        gc = graphCanvas.getGraphicsContext2D();
        gc.setFont(Font.font("Times New Roman", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 14));
        graphCanvas.heightProperty().addListener(ov-> {doTheGraph();});
        graphCanvas.widthProperty().addListener(ov-> {doTheGraph();}); 
    }
        
    public void bfvCompleteTheDeal() { 
        dm.whereIsWaldo(127, waldoFile, "bfvCompleteTheDeal()");
        bfvInitializeGraphParameters();
        setUpUI();       
        setUpAnchorPane();
        setHandlers();
        xAxis.setLabel(xAxisLabel);
        yAxis.setLabel(yAxisLabel);
        doTheGraph();     
        theContainingPane = dragableAnchorPane.getTheContainingPane(); 
    }
    
    public void bfvConstructDataArray() {
        dm.whereIsWaldo(139, waldoFile, "bfvConstructDataArray()");
        xDataMin = daCovariates[0];
        xDataMax = daCovariates[0];
        yDataMin = daResponses[0];
        yDataMax = daResponses[0];
        
        for (int ithCase = 0; ithCase < nCases; ithCase++) {
            if (daCovariates[ithCase] < xDataMin) { xDataMin = daCovariates[ithCase]; }
            if (daCovariates[ithCase] > xDataMax) { xDataMax = daCovariates[ithCase]; }
            if (daResponses[ithCase] < yDataMin) { yDataMin = daResponses[ithCase]; }
            if (daResponses[ithCase] > yDataMax) { yDataMax = daResponses[ithCase]; }
        }
        
        xRange = xDataMax - xDataMin;
        yRange = yDataMax - yDataMin;
        
        //  Make room for the circles
        xDataMin = xDataMin - .02 * xRange; xDataMax = xDataMax + .02 * xRange;
        yDataMin = yDataMin - .02 * yRange; yDataMax = yDataMax + .02 * yRange;  
    }
    
    public void doTheGraph() {      
        double xlb, yub, xDisplayStartKey, xDisplayStopKey, 
               yDisplayStartKey, yDisplayStopKey;

        double text1Width = txtTitle1.getLayoutBounds().getWidth();
        double text2Width = txtTitle2.getLayoutBounds().getWidth();
        double paneWidth = dragableAnchorPane.getWidth();
        double txt1Edge = (paneWidth - text1Width) / (2 * paneWidth);
        double txt2Edge = (paneWidth - text2Width) / (2 * paneWidth);
        
        xlb = xAxis.getLB(); //xub = xAxis.getUpperBound();
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

        gc.clearRect(0, 0, graphCanvas.getWidth(), graphCanvas.getHeight());

        for (int ithLevel = 0; ithLevel < nLevels; ithLevel++) {
            double dbl_ithLevel = ithLevel;
            for (int ithCase = 0; ithCase < nCases; ithCase++) {
                double xx = xAxis.getDisplayPosition(daCovariates[ithCase]);
                double yy = yAxis.getDisplayPosition(daResponses[ithCase]);

                //  radius, diameter for centering the dots on point
                gc.setFill(graphColors[ithLevel]);
                if (daLevels[ithCase] == dbl_ithLevel) {
                    gc.fillOval(xx - dotRadius , yy - dotRadius , dotDiameter, dotDiameter); 
                }
            }      
        }

        
        for (int ithLevel = 0; ithLevel < nLevels; ithLevel++) {
            gc.setFill(graphColors[ithLevel]);
            double x1 = xAxis.getDisplayPosition(xDataMin);
            double y1 = yAxis.getDisplayPosition(slopesWithin[ithLevel] * xDataMin + interceptsWithin[ithLevel]);
            double x2 = xAxis.getDisplayPosition(xDataMax);
            double y2 = yAxis.getDisplayPosition(slopesWithin[ithLevel] * xDataMax + interceptsWithin[ithLevel]);
            gc.setLineWidth(2);

            if (nCases > 50) { gc.setLineWidth(4); }

            gc.setStroke(graphColors[ithLevel]);
            gc.strokeLine(x1, y1, x2, y2);  

            // Print the key
            xDisplayStartKey =  xAxis.getDisplayPosition(xlb) + 10;
            xDisplayStopKey =  xAxis.getDisplayPosition(xlb) + 30;
            yDisplayStartKey =  yAxis.getDisplayPosition(yub) + 10 + 20 * ithLevel;
            yDisplayStopKey =  yDisplayStartKey;

            gc.strokeLine(xDisplayStartKey, yDisplayStartKey, 
                          xDisplayStopKey, yDisplayStopKey);
            gc.fillText(strLevels[ithLevel], xDisplayStopKey + 10, yDisplayStartKey + 5);            
        }   
            
        theContainingPane.requestFocus();
        theContainingPane.setOnKeyPressed((ke -> {
            KeyCode keyCode = ke.getCode();
            boolean doIt = ke.isControlDown() && (ke.getCode() == KeyCode.C);
            if (doIt) {
                WritableImage writableImage = theContainingPane.snapshot(new SnapshotParameters(), null);
                ImageView iv = new ImageView(writableImage);
                clipboard = Clipboard.getSystemClipboard();
                content = new ClipboardContent();
                content.put(DataFormat.IMAGE, writableImage);
                clipboard.setContent(content);
            }
        }));
    } 
    
    @Override
   public Pane getTheContainingPane() { return theContainingPane; }
}


