/**************************************************
 *                MultUni_DotPlotView             *
 *                     04/02/24                   *
 *                      09:00                     *
 *************************************************/
package proceduresManyUnivariate;

import dataObjects.CatQuantDataVariable;
import dataObjects.CatQuantPair;
import genericClasses.JustAnAxis;
import genericClasses.DragableAnchorPane;
import dialogs.Change_Bins_Dialog;
import dialogs.Change_Radius_Dialog;
import genericClasses.Point_2D;
import java.util.ArrayList;
import javafx.geometry.Side;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import superClasses.BivariateScale_W_CheckBoxes_View;
import utilityClasses.Colors_and_CSS_Strings;

public class MultUni_DotPlotView extends BivariateScale_W_CheckBoxes_View {
    // POJOs
    
    boolean printTheStuff = true;
    //boolean printTheStuff = false;
    
    int nLegalDataPoints, nBinsToLeft, nBinsToRight, nBinsTotal, nLevels;
    
    int[] freqAllLevels;
    int[][] freqByLevel, cumFreqByLevel;
    
    double xMin, xMax, minScale, maximumFreq, yMin, yMax, initial_xMin, 
           initial_xMax, initial_xRange,  ithBinLow, ithBinHigh, binWidth,
           xCenter, yCenter, minDataRange, maxDataRange, relRad;

    double[] sortedData, leftBinEnd, rightBinEnd;
    
    String returnStatus;  

    // My classes
    AnchorPane anchorPane;
    Button btn_BinReset, btn_RadiusReset;
    ArrayList<String> categoryLevels;
    CatQuantDataVariable cqdv;
    Change_Bins_Dialog chBins_Dialog;
    Change_Radius_Dialog chRadius_Dialog;
    HBox hBox_BinReset;
    MultUni_DotPlotModel multUni_DotPlotModel;
    Pane theContainingPane;
    Point_2D ithBinLimits;
    
    // POJO FX
    Color[] dotColors;

    // For a single dotplot
    public MultUni_DotPlotView(MultUni_DotPlotModel multUni_DotPlotModel, MultUni_Dashboard multUni_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        if (printTheStuff) {
            System.out.println("77 *** MultUni_DotPlotView, constructing");
        }
        this.multUni_DotPlotModel = multUni_DotPlotModel;
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        this.multUni_DotPlotModel = multUni_DotPlotModel;
        if (printTheStuff) {
            System.out.println("84 *** MultUni_DotPlotView, constructing");
        }
        cqdv = multUni_DotPlotModel.getCQDV();
        categoryLevels = new ArrayList();
        if (printTheStuff) {
            System.out.println("89 *** MultUni_DotPlotView, constructing");
        }
        categoryLevels = cqdv.getCategoryLevels();
        if (printTheStuff) {
            System.out.println("93 *** MultUni_DotPlotView, constructing");
        }
        nLevels = categoryLevels.size();
        finishConstruction();
    }
    
    private void finishConstruction() {
        if (printTheStuff) {
            System.out.println("101 *** MultUni_DotPlotView, finishConstruction()");
        }
        nLegalDataPoints = multUni_DotPlotModel.getQDV().getLegalN();
        ithBinLimits = multUni_DotPlotModel.getBinLimits();
        ithBinLow = ithBinLimits.getFirstValue();
        ithBinHigh = ithBinLimits.getSecondValue();    
        sortedData = new double[nLegalDataPoints];
        sortedData = multUni_DotPlotModel.getUCDO().getTheDataSorted();
        
        graphCanvas = new Canvas(600, 600);
        gc = graphCanvas.getGraphicsContext2D();
        btn_BinReset = new Button("Change bin limits");
        btn_RadiusReset = new Button("Change radius");
        minDataRange = multUni_DotPlotModel.getUCDO().getMinValue();
        maxDataRange = multUni_DotPlotModel.getUCDO().getMaxValue();
        dotColors = Colors_and_CSS_Strings.getGraphColors_07(); 
        relRad = 0.5;  //  Default -- needed before first btn click

        btn_BinReset.setOnAction(e -> {
            chBins_Dialog = new Change_Bins_Dialog(minDataRange, maxDataRange);
            chBins_Dialog.showAndWait();
            ithBinLow = chBins_Dialog.getDblLeftBin();
            ithBinHigh = chBins_Dialog.getDblRightBin();
            returnStatus = chBins_Dialog.getReturnStatus();
            chBins_Dialog.close();
            
            if (!returnStatus.equals("Cancel")) {
                constructBinInformation();
                doTheGraph();
            }
        });
        
        btn_RadiusReset.setOnAction(e -> {
            chRadius_Dialog = new Change_Radius_Dialog(this);
            chRadius_Dialog.showAndWait();
            returnStatus = chRadius_Dialog.getReturnStatus();            
            if (!returnStatus.equals("Cancel")) {
                relRad = chRadius_Dialog.getRelativeRadius();
                chRadius_Dialog.close();
                doTheGraph();
            }
        });

        hBox_BinReset = new HBox();
        hBox_BinReset.getChildren().addAll(btn_BinReset, btn_RadiusReset);
        makeTheCheckBoxes();
        makeItHappen();        
    }

    private void makeItHappen() {   
        theContainingPane = new Pane();
        gc = graphCanvas.getGraphicsContext2D();
        gc.setFont(Font.font("Times New Roman", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 16));
        graphCanvas.heightProperty().addListener(ov-> {doTheGraph();});
        graphCanvas.widthProperty().addListener(ov-> {doTheGraph();});
    }
        
    public void completeTheDeal() { 
        if (printTheStuff) {
            System.out.println("160 *** MultUni_DotPlotView, completeTheDeal()");
        }
        initializeGraphParameters();
        setUpUI();       
        setUpAnchorPane();
        setHandlers();
        doTheGraph();   
        theContainingPane = dragableAnchorPane.getTheContainingPane();       
    }
    
    public void initializeGraphParameters() {  
        initial_xMin = multUni_DotPlotModel.getQDV().getMinValue();
        initial_xMax = multUni_DotPlotModel.getQDV().getMaxValue();
        initial_xRange = initial_xMax - initial_xMin;
        // Adjust to better bring the histogram into the window
        xMin = initial_xMin - 0.1 * initial_xRange;   
        xMax = initial_xMax + 0.1 * initial_xRange;   
        xRange = xMax - xMin;
        // deltaX controls the rate of scale change when dragging
        deltaX = 0.005 * xRange;
        setUpUI();
        constructBinInformation();
        setUpTheAxes(); 
    }

    
    public void setUpUI() {
        txtTitle1 = new Text(50, 25, " Dot Plot ");
        String subTitle = "Frequency  vs.  " + multUni_DotPlotModel.getDescrOfVar();
        txtTitle2 = new Text (60, 45, subTitle);
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15));     
    }
    
    public void setUpAnchorPane() {
        dragableAnchorPane = new DragableAnchorPane();
        graphCanvas.heightProperty().bind(dragableAnchorPane.heightProperty().multiply(.70));
        graphCanvas.widthProperty().bind(dragableAnchorPane.widthProperty().multiply(.90));
        anchorPane = dragableAnchorPane.getTheAP();
        dragableAnchorPane.makeDragable();
        dragableAnchorPane.getStylesheets().add(graphsCSS);    
        dragableAnchorPane.getTheAP()
                           .getChildren()
                           .addAll(hBox_BinReset, txtTitle1, txtTitle2, xAxis, yAxis, graphCanvas);
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }
  
    public void doTheGraph() {
        double binFrequency;  
        double text1Width = txtTitle1.getLayoutBounds().getWidth();
        double text2Width = txtTitle2.getLayoutBounds().getWidth();
        double paneWidth = dragableAnchorPane.getWidth();
        double txt1Edge = (paneWidth - text1Width) / (2 * paneWidth);
        double txt2Edge = (paneWidth - text2Width) / (2 * paneWidth);
        
        double tempHeight = dragableAnchorPane.getHeight();
        double tempWidth = dragableAnchorPane.getWidth();
        
        AnchorPane.setTopAnchor(hBox_BinReset, 0.01 * tempHeight);
        AnchorPane.setLeftAnchor(hBox_BinReset, 0.0 * txt1Edge * tempWidth);
        AnchorPane.setRightAnchor(hBox_BinReset, txt1Edge * tempWidth);
        AnchorPane.setBottomAnchor(hBox_BinReset, 0.95 * tempHeight);

        AnchorPane.setTopAnchor(txtTitle1, 0.07 * tempHeight);
        AnchorPane.setLeftAnchor(txtTitle1, txt1Edge * tempWidth);
        AnchorPane.setRightAnchor(txtTitle1, txt1Edge * tempWidth);
        AnchorPane.setBottomAnchor(txtTitle1, 0.10 * tempHeight);
                
        AnchorPane.setTopAnchor(txtTitle2, 0.12 * tempHeight);
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
            AnchorPane.setLeftAnchor(scatterPlotCheckBoxes[chex], (chex) * tempWidth / 5.0);
        }

        gc.clearRect(0, 0 , graphCanvas.getWidth(), graphCanvas.getHeight()); 
        
        for (int ithCat = 0; ithCat < nLevels; ithCat++) {
            gc.setStroke(Color.BLACK);
            gc.setFill(Color.BLACK);
            double xText = 0.75 * graphCanvas.getWidth();
            double yText = ( 0.035  + 0.06 * ithCat) * graphCanvas.getHeight();
            gc.fillText(categoryLevels.get(ithCat), xText, yText);
            gc.setStroke(dotColors[ithCat]);
            gc.setFill(dotColors[ithCat]);
            gc.fillRect(xText + 100, ( 0.0055  + 0.06 * ithCat) * graphCanvas.getHeight(), 49, 16);
        } 
        
        binWidth = xAxis.getDisplayPosition(leftBinEnd[1]) - xAxis.getDisplayPosition(leftBinEnd[0]);  
        diameter = relRad * binWidth;
        radius = 0.5 * diameter;
        
        for (int ithBin = 0; ithBin < nBinsTotal; ithBin++ ) {
            double preXCenter = (leftBinEnd[ithBin] + rightBinEnd[ithBin]) / 2.0;
            xCenter = xAxis.getDisplayPosition(preXCenter);
            binFrequency = freqAllLevels[ithBin];
                        
            if (binFrequency > 0.) {
                for (int jthLevel = 0; jthLevel < nLevels; jthLevel++) { 
                    gc.setFill(dotColors[jthLevel]);    // Set color for that level
                        // Level 0 has no cum below
                        if (jthLevel == 0) {
                            for (int ithCircle = 0; ithCircle < cumFreqByLevel[ithBin][jthLevel]; ithCircle++) {
                                yCenter = yAxis.getDisplayPosition((double)(ithCircle + 1.));
                                gc.fillOval(xCenter - radius, yCenter - radius , diameter, diameter);    
                            } 
                        } else {    // jthLevel > 0
                            gc.setFill(dotColors[jthLevel]);    // Set color for that level
                            int startCircle = cumFreqByLevel[ithBin][jthLevel - 1];
                            int stopCircle = cumFreqByLevel[ithBin][jthLevel - 1]+ freqByLevel[ithBin][jthLevel];
                            for (int ithCircle = startCircle; ithCircle < stopCircle; ithCircle++) {
                                yCenter = yAxis.getDisplayPosition((double)(ithCircle + 1.));
                                gc.fillOval(xCenter - radius, yCenter - radius , diameter, diameter);                               
                        }
                    }
                }
     
            }
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
    
    
    public void constructBinInformation() { 
        binWidth = ithBinHigh - ithBinLow;
        nBinsToLeft = (int)Math.floor((ithBinLow - xMin) / binWidth) + 1;
        nBinsToRight = (int)Math.floor((xMax - ithBinHigh) / binWidth) + 1;
        
        //  Create some space on the left and right
        if (nBinsToLeft < 3) {nBinsToLeft = 3;}
        if (nBinsToRight < 3) {nBinsToRight = 3;}       
        
        nBinsTotal = nBinsToLeft + 1 + nBinsToRight;
        freqAllLevels = new int[nBinsTotal];
        freqByLevel = new int[nBinsTotal][nLevels];
        cumFreqByLevel = new int[nBinsTotal][nLevels];
        leftBinEnd = new double[nBinsTotal];
        rightBinEnd = new double[nBinsTotal];
        minScale = ithBinLow - nBinsToLeft * binWidth;
        
        // Zero out the counts    
        for (int ithBin = 0; ithBin < nBinsTotal; ithBin++) {
            leftBinEnd[ithBin] = minScale + ithBin * binWidth;
            rightBinEnd[ithBin] = leftBinEnd[ithBin] + binWidth;
            // Reinitialize the frequencies and freqByLevels
            freqAllLevels[ithBin] = 0;
            for (int jthLevel = 0; jthLevel < nLevels; jthLevel++) {
                freqByLevel[ithBin][jthLevel] = 0;
                cumFreqByLevel[ithBin][jthLevel] = 0;
            }
        }
 
        for (int ithData = 0; ithData < nLegalDataPoints; ithData++) {            
            CatQuantPair tempPair = cqdv.getIthCatQuantPair(ithData);
            double ithDataPoint = tempPair.getQuantValueDouble(); 
            String ithCatValue = tempPair.getCatValue();

            for (int ithBin = 0; ithBin < nBinsTotal; ithBin++) {
                if ((leftBinEnd[ithBin] <= ithDataPoint) && (ithDataPoint < rightBinEnd[ithBin])) {                    
                    for (int jthLevel = 0; jthLevel < nLevels; jthLevel++) {
                        if (ithCatValue.equals(categoryLevels.get(jthLevel))) {
                            freqByLevel[ithBin][jthLevel]++;
                            cumFreqByLevel[ithBin][jthLevel]++;
                        }
                    }     
                    freqAllLevels[ithBin] = freqAllLevels[ithBin] + 1;
                    break;
                }
            }

            maximumFreq = 0.;
            for (int ithFreq = 0; ithFreq < nBinsTotal; ithFreq++) {            
                if (freqAllLevels[ithFreq] > maximumFreq) {
                    maximumFreq = freqAllLevels[ithFreq];
                }
            }
            maximumFreq = maximumFreq + 1.0;    // Create some space at the top
        }
        
        //  Cumulate the color counts for each bin
        for (int ithBin = 0; ithBin < nBinsTotal; ithBin++) {
            for (int jthLevel = 0; jthLevel < nLevels; jthLevel++) {
            }
        }         
               
        // Cumulate the frequencies
        for (int ithBin = 0; ithBin < nBinsTotal; ithBin++) {
            for (int jthLevel = 1; jthLevel < nLevels; jthLevel++) {
                cumFreqByLevel[ithBin][jthLevel] = cumFreqByLevel[ithBin][jthLevel - 1] + cumFreqByLevel[ithBin][jthLevel];
            }
        }  
    }
    
    public void setUpTheAxes() {
        xAxis = new JustAnAxis(xMin, xMax);
        xAxis.setSide(Side.BOTTOM);
        binWidth = xAxis.getDisplayPosition(leftBinEnd[1]) - xAxis.getDisplayPosition(leftBinEnd[0]);
        newX_Lower = xMin; 
        newX_Upper = xMax;
        xAxis.setLowerBound(newX_Lower ); 
        xAxis.setUpperBound(newX_Upper );
        
        yMin = -0.5;
        yMax = 1.2 * maximumFreq;
        newY_Lower = yMin;
        newY_Upper = yMax;
        yAxis = new JustAnAxis(yMin, yMax); 
        yAxis.forceLowScaleEndToBe(0.0);
        yAxis.setVisible(true);
        yAxis.setSide(Side.LEFT); 
        yAxis.setLowerBound(newY_Lower ); 
        yAxis.setUpperBound(newY_Upper );
        yRange = yMax - yMin;      
        deltaY = 0.005 * yRange; 
    }
    
    public void setRelRad(double theNewRad) { relRad = theNewRad; }
    public Pane getTheContainingPane() { return theContainingPane; }
    public Canvas getTheCanvas() { return graphCanvas; }
    public DragableAnchorPane getTheDragableAnchorPane() { return dragableAnchorPane; }
}
