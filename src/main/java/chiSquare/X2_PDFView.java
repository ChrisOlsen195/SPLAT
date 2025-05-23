/**************************************************
 *               ChiSquarePDFView                 *
 *                  02/26/25                      *
 *                    09:00                       *
 *************************************************/
package chiSquare;

import chiSquare_Assoc.X2Assoc_Dashboard;
import chiSquare_Assoc.X2Assoc_Model;
import chiSquare.GOF.X2GOF_Dashboard;
import chiSquare.GOF.X2GOF_Model;
import genericClasses.JustAnAxis;
import genericClasses.DragableAnchorPane;
import java.util.ArrayList;
import javafx.geometry.Side;
import javafx.scene.SnapshotParameters;
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
import probabilityDistributions.*;
import superClasses.*;

public class X2_PDFView extends BivariateScale_W_CheckBoxes_View {
    // POJOs
    
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    int maxSpaces, df;

    double chiSquare, xStart_X2PVal; 
    
    final double MIDDLE_CHISQ = 0.9999;
    final double[] alphas = {0.10, 0.05, 0.025, 0.01};
    double[] initialInterval;

    ArrayList<String> stringOfNSpaces; 
    
    // My classes
    ChiSquareDistribution chiSquareDistr;   
    
    //  POJOs / FX
    Pane x2PdfContainingPane;

    public X2_PDFView(X2GOF_Model x2GOF_Model, X2GOF_Dashboard x2GOF_Dashboard, 
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {

        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        if (printTheStuff == true) {
            System.out.println("62 *** ChiSqPDFView, constructing");
        }
        df = x2GOF_Model.getDF();
        chiSquare = x2GOF_Model.getX2();
        rightTailCutPoint = chiSquare;
        chiSquareDistr = new ChiSquareDistribution(df);
        initialInterval = new double[2];
        makeItHappen();
    }  

    public X2_PDFView(X2Assoc_Model x2Assoc_Model, X2Assoc_Dashboard x2Assoc_Dashboard, 
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight); 
        if (printTheStuff == true) {
            System.out.println("77 *** ChiSqPDFView, constructing");
        }
        df = x2Assoc_Model.getDF();
        chiSquare = x2Assoc_Model.getChiSquare();
        rightTailCutPoint = chiSquare;
        chiSquareDistr = new ChiSquareDistribution(df);
        initialInterval = new double[2];
        makeItHappen();
    } 
  
    private void makeItHappen() {
        if (printTheStuff == true) {
            System.out.println("89 --- ChiSqPDFView, makeItHappen()");
        }            
        for (int spaces = 1; spaces < maxSpaces; spaces++) {
            stringOfNSpaces.add(stringOfNSpaces.get(spaces - 1) + " ");
        }
        
        middle_ForGraph = MIDDLE_CHISQ; 
        df = chiSquareDistr.getDegreesOfFreedom();

        initialInterval = chiSquareDistr.getInverseMiddleArea(middle_ForGraph);
        identifyPValueIsDesired = true; assumptionCheckIsDesired = true;
        
        x2PdfContainingPane = new Pane();
        graphCanvas = new Canvas(initWidth, initHeight);
        gc = graphCanvas.getGraphicsContext2D();
        gc.setFont(Font.font("Times New Roman", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 14));
        graphCanvas.heightProperty().addListener(ov-> {doTheGraph();});
        graphCanvas.widthProperty().addListener(ov-> {doTheGraph();});
    }
        
    public void completeTheDeal() {
        if (printTheStuff == true) {
            System.out.println("111 --- ChiSqPDFView, completeTheDeal()");
        }
        initializeGraphParameters();
        setUpUI();       
        setUpGridPane();
        setHandlers();
        doTheGraph();     
        x2PdfContainingPane = dragableAnchorPane.getTheContainingPane();     
    }
        
    public void setUpUI() { 
        String title2String;
        txtTitle1 = new Text(50, 25, "Chi square distribution");
        
        if (df > 1) {
            title2String = String.valueOf(df) + " degrees of freedom";
        }
        else {
            title2String = String.valueOf(df) + " degree of freedom";
        }
        
        txtTitle2 = new Text (60, 45, title2String);
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15)); 
    }
    
    private void setUpGridPane() {
        dragableAnchorPane = new DragableAnchorPane();
        graphCanvas.heightProperty().bind(dragableAnchorPane.heightProperty().multiply(.70));
        graphCanvas.widthProperty().bind(dragableAnchorPane.widthProperty().multiply(.90));
        anchorPane = dragableAnchorPane.getTheAP();
        dragableAnchorPane.makeDragable();
        dragableAnchorPane.getStylesheets().add(graphsCSS);    
        dragableAnchorPane.getTheAP()
                           .getChildren()
                           .addAll(txtTitle1, txtTitle2, xAxis, yAxis, graphCanvas);
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }
    
    public void initializeGraphParameters() {
        initialInterval = chiSquareDistr.getInverseMiddleArea(MIDDLE_CHISQ);
        fromHere = initialInterval[0];
        toThere = initialInterval[1];
        xAxis = new JustAnAxis(fromHere, toThere);
        xAxis.setSide(Side.BOTTOM);       
        prepareTheDensityAxis();
        
        //  Density at mode
        yDataMax = 1.05;
        double dbl_df = df;
        
        if (df > 2) {
            double modeIsAt = dbl_df - 2.0;
            yDataMax = 1.05 * chiSquareDistr.getDensity(modeIsAt);
        }    

        yAxis = new JustAnAxis(yDataMin, yDataMax);
        yAxis.setSide(Side.LEFT);
        yAxis.forceLowScaleEndToBe(0.0);  
        yAxis.setBounds(0.0, yDataMax);
        newX_Lower = fromHere; newX_Upper = toThere;
        newY_Lower = yDataMin; newY_Upper = yDataMax;
        xAxis.setLowerBound(newX_Lower ); 
        xAxis.setUpperBound(newX_Upper );
        yAxis.setLowerBound(newY_Lower ); 
        yAxis.setUpperBound(newY_Upper );  
        shadeLeftTail = false;  //  These values are the defaults
        shadeRightTail = true;  //  These values are the defaults
    }

    private void prepareTheDensityAxis() {
        xGraphLeft = fromHere;
        xAxis.forceLowScaleEndToBe(0.0);
        xGraphLeft = 0.0000001;   
        xGraphRight = toThere;
        bigDelta = (xGraphRight - xGraphLeft) / NUMBER_OF_DXs;
        delta = bigDelta;
        xDataMin = xDataMax = xGraphLeft;
        xRange = xGraphRight - xGraphLeft;        
        yRange = yDataMax = getInitialYMax();
        // These constants control the rate of axis scale change when dragging
        deltaX = 0.005 * xRange; deltaY = 0.005 * yRange;   
    }
    
    public void setIntervalOfInterest(double startHere, double endHere)  {
        fromHere = startHere; toThere = endHere;
        delta = (endHere - startHere) / bigDelta * NUMBER_OF_DXs; 
    }
    
    public double getInitialYMax() {
        yDataMax = 1.0;
        if (df == 2)
            yDataMax = 0.5;                
        if (df > 2)
            yDataMax = 0.35;
        if (df > 5)
            yDataMax = 0.25;                
        return yDataMax;
    }
    
    public void doTheGraph() {   
        double xx0, yy0, xx1, yy1;
        String tempString;
        double text1Width = txtTitle1.getLayoutBounds().getWidth();
        double text2Width = txtTitle2.getLayoutBounds().getWidth();
        double paneWidth = dragableAnchorPane.getWidth();
        double txt1Edge = (paneWidth - text1Width) / (2 * paneWidth);
        double txt2Edge = (paneWidth - text2Width) / (2 * paneWidth);
        
        double tempHeight = dragableAnchorPane.getHeight();
        double tempWidth = dragableAnchorPane.getWidth();
        AnchorPane.setTopAnchor(txtTitle1, 0.0 * tempHeight);
        AnchorPane.setLeftAnchor(txtTitle1, txt1Edge * tempWidth);
        AnchorPane.setRightAnchor(txtTitle1, txt1Edge * tempWidth);
        AnchorPane.setBottomAnchor(txtTitle1, 0.1 * tempHeight);
                
        AnchorPane.setTopAnchor(txtTitle2, 0.1 * tempHeight);
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
        
        //  Start point for graph
        xx0 = xGraphLeft; yy0 = chiSquareDistr.getDensity(xx0);
        
        for (double x = xGraphLeft; x <= xGraphRight; x += delta) {
            xx1 = x;
            yy1 = chiSquareDistr.getDensity(xx1);
            xStart = xAxis.getDisplayPosition(xx0); 
            yStart = yAxis.getDisplayPosition(yy0); 
            xStop = xAxis.getDisplayPosition(xx1);
            yStop = yAxis.getDisplayPosition(yy1);
            gc.setLineWidth(2);
            gc.setStroke(Color.BLACK);
            gc.strokeLine(xStart, yStart, xStop, yStop);

            if ((shadeLeftTail == true) && (x < leftTailCutPoint)) {
                yStart = yAxis.getDisplayPosition(0.0);                
                gc.strokeLine(xStart, yStart, xStop, yStop);
            }
            
            if ((shadeRightTail == true) && (x > rightTailCutPoint)) {
                yStart = yAxis.getDisplayPosition(0.0);                
                gc.strokeLine(xStart, yStart, xStop, yStop);
            }     

            xx0 = xx1; yy0 = yy1;   //  Next start point for line segment
        }   
        
        if (identifyPValueIsDesired) {
            double elFactoro = 7.0;
            // Vert line for X2 / pVal
            xStart = xStop = xStart_X2PVal = xAxis.getDisplayPosition(rightTailCutPoint);
            yStart = yAxis.getDisplayPosition(0.0);
                        
            double heightAtPoint_10 = yAxis.getDisplayPosition(chiSquareDistr.getDensity(chiSquareDistr.getInvRightTailArea(0.10))) - elFactoro * 0.1 * yStart;
            double heightAtPoint_05 = yAxis.getDisplayPosition(chiSquareDistr.getDensity(chiSquareDistr.getInvRightTailArea(0.05))) - elFactoro * 0.05 * yStart;            
            
            yStop = (heightAtPoint_10 +  heightAtPoint_05) / 2.0;        
 
            gc.setLineWidth(2);
            gc.setStroke(Color.RED);
            gc.strokeLine(xStart, yStart, xStop, yStop);
            double thePValue = chiSquareDistr.getRightTailArea(chiSquare);
            double rightEndPad = 225.;
            double dbl_df = df;
            double temp  = dbl_df + Math.sqrt(2.0 * dbl_df);
            if (paneWidth - xStart_X2PVal < rightEndPad) { xStart_X2PVal = xAxis.getDisplayPosition(temp) - 100.; }
            tempString = String.format("\u03C7\u00B2 = %6.3f, pValue = %4.3f", chiSquare, thePValue);              
            gc.setFill(Color.RED);
            gc.fillText(tempString, xStart_X2PVal + 5, yStop - 5);
        }
        
        if (assumptionCheckIsDesired) {
            // elFactoro is there to help the alphas to avoid each other. It is intended
            //  to represent a fraction of the vertical size of the window
            double elFactoro = 7.0;
            for (double e: alphas) {
                theCriticalValue = chiSquareDistr.getInvRightTailArea(e); 
                xStart = xStop = xAxis.getDisplayPosition(theCriticalValue);
                yStart = yAxis.getDisplayPosition(0.0);
                yStop = yAxis.getDisplayPosition(chiSquareDistr.getDensity(theCriticalValue)) - elFactoro * e *yStart;   // Hard-wired test statistic  
                gc.setLineWidth(2);
                gc.setStroke(Color.BLUE);
                gc.strokeLine(xStart, yStart, xStop, yStop);
                gc.setFill(Color.BLUE);
                tempString = String.format("Critical Value (\u03B1 = %4.3f) =%7.3f", e, theCriticalValue);
                gc.fillText(tempString, xStop - 00, yStop - 5);  // <---
            }
        } 
        
        x2PdfContainingPane.requestFocus();
        x2PdfContainingPane.setOnKeyPressed((ke -> {
            KeyCode keyCode = ke.getCode();
            boolean doIt = ke.isControlDown() && (ke.getCode() == KeyCode.C);
            if (doIt) {
                WritableImage writableImage = anchorPane.snapshot(new SnapshotParameters(), null);
                ImageView iv = new ImageView(writableImage);
                clipboard = Clipboard.getSystemClipboard();
                content = new ClipboardContent();
                content.put(DataFormat.IMAGE, writableImage);
                clipboard.setContent(content);
            }
        }));        
        
    }

    public Pane getTheContainingPane() { return x2PdfContainingPane; }
}


