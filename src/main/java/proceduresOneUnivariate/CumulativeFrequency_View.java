/**************************************************
 *             CumulativeFrequency_View           *
 *                     08/20/24                   *
 *                      09:00                     *
 *************************************************/
package proceduresOneUnivariate;

import genericClasses.JustAnAxis;
import genericClasses.DragableAnchorPane;
import dialogs.Change_Bins_Dialog;
import genericClasses.Point_2D;
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

public class CumulativeFrequency_View extends BivariateScale_W_CheckBoxes_View {
    // POJOs
    int nLegalDataPoints, nBinsToLeft, nBinsToRight, nBinsTotal;
    
    double xMin, xMax, minScale, yMin, yMax, initial_xMin, 
           initial_xMax, initial_xRange,  ithBinLow, ithBinHigh, binWidth,
           sqrRootOfTwoOverTwo, cumFreqLeft, cumFreqRight, actualDataMax,
           ithCumFrequency, maximumCumFreq, minDataRange, maxDataRange;
    
    double[] sortedData, univDataArray, leftBinEnd, rightBinEnd, 
             frequencies, cumFrequencies;

    String returnStatus;
    
    // My classes
    Change_Bins_Dialog chBins_Dialog;
    CumulativeFrequency_Model cumFreq_Model;
    Point_2D ithBinLimits;
    Button btn_BinReset;
    HBox hBox_BinReset;
    AnchorPane anchorPane;
    Pane theContainingPane;

    public CumulativeFrequency_View(CumulativeFrequency_Model cumFreq_Model, Exploration_Dashboard exploration_Dashboard,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {    
        super(placeHoriz, placeVert, withThisWidth, withThisHeight);
        //System.out.println("\n59 CumulativeFrequency_View, Constructing");
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight; 
        this.cumFreq_Model = cumFreq_Model;
        univDataArray = cumFreq_Model.getQDV().getLegalDataAsDoubles();
        nLegalDataPoints = cumFreq_Model.getQDV().getLegalN();
        ithBinLimits = this.cumFreq_Model.getBinLimits();
        ithBinLow = ithBinLimits.getFirstValue();
        ithBinHigh = ithBinLimits.getSecondValue();
        sortedData = new double[nLegalDataPoints];
        sortedData = cumFreq_Model.getUCDO().getTheDataSorted();
        actualDataMax = cumFreq_Model.getUCDO().getMaxValue();
        graphCanvas = new Canvas(600, 600);
        gc = graphCanvas.getGraphicsContext2D();  
        btn_BinReset = new Button("Change bin width");
        minDataRange = cumFreq_Model.getUCDO().getMinValue();
        maxDataRange = cumFreq_Model.getUCDO().getMaxValue();
        
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

        hBox_BinReset = new HBox();
        hBox_BinReset.getChildren().add(btn_BinReset);
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
        initializeGraphParameters();
        setUpUI();       
        setUpAnchorPane();
        setHandlers();
        doTheGraph();   
        theContainingPane = dragableAnchorPane.getTheContainingPane();       
    }
    
    public void initializeGraphParameters() {  
        sqrRootOfTwoOverTwo = Math.sqrt(2.0) / 2.0;
        radius = 3.0; diameter = 6.0;
        initial_xMin = cumFreq_Model.getQDV().getMinValue();
        initial_xMax = cumFreq_Model.getQDV().getMaxValue();
        initial_xRange = initial_xMax - initial_xMin;
        // Adjust to better bring the histogram into the window
        xMin = initial_xMin - 0.05 * initial_xRange;   
        xMax = initial_xMax + 0.05 * initial_xRange;   
        xRange = xMax - xMin;
        // This constant controls the rate of scale change when dragging
        deltaX = 0.005 * xRange;
        constructBinInformation();
        setUpTheAxes();
    }

    
    public void setUpUI() {
        txtTitle1 = new Text(50, 25, " Cumulative Frequency ");
        String subTitle = "Cumulative Frequency  vs.  " + cumFreq_Model.getDescrOfVar();
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
                           .addAll(hBox_BinReset,txtTitle1, txtTitle2, xAxis, yAxis, graphCanvas);
        dragableAnchorPane.setInitialEventCoordinates(initHoriz, initVert, initHeight, initWidth);
    }
  
    public void doTheGraph() {
        double binHeight; 
        double text1Width = txtTitle1.getLayoutBounds().getWidth();
        double text2Width = txtTitle2.getLayoutBounds().getWidth();
        double paneWidth = dragableAnchorPane.getWidth();
        double txt1Edge = (paneWidth - text1Width) / (2 * paneWidth);
        double txt2Edge = (paneWidth - text2Width) / (2 * paneWidth);
        
        double tempHeight = dragableAnchorPane.getHeight();
        double tempWidth = dragableAnchorPane.getWidth();
        
        // On the left
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
        
        gc.setFill(Color.GREEN);
        binWidth = xAxis.getDisplayPosition(leftBinEnd[1]) - xAxis.getDisplayPosition(leftBinEnd[0]);  
        
        for (int ithBin = 0; ithBin < nBinsTotal; ithBin++ ) {
            
            ithCumFrequency = cumFrequencies[ithBin];            
            if (ithCumFrequency > 0.) {
                binHeight = yAxis.getDisplayPosition(0.0) - yAxis.getDisplayPosition(ithCumFrequency);
                double leftEnd = xAxis.getDisplayPosition(leftBinEnd[ithBin]);
                double rightEnd = xAxis.getDisplayPosition(rightBinEnd[ithBin]);
                
                if (ithBin == 0) {
                    cumFreqLeft = yAxis.getDisplayPosition(0.0);
                    cumFreqRight = yAxis.getDisplayPosition(ithCumFrequency);
                    gc.strokeLine(leftEnd, cumFreqLeft, rightEnd, cumFreqRight);
                    gc.fillOval(leftEnd - sqrRootOfTwoOverTwo * radius , cumFreqLeft - sqrRootOfTwoOverTwo * radius , diameter, diameter);
                }
                else if ((ithBin <= nBinsTotal - 1) && (leftBinEnd[ithBin] < actualDataMax)) {
                    cumFreqLeft = yAxis.getDisplayPosition(cumFrequencies[ithBin - 1]);
                    cumFreqRight = yAxis.getDisplayPosition(cumFrequencies[ithBin]);
                    gc.strokeLine(leftEnd, cumFreqLeft, rightEnd, cumFreqRight);
                    gc.fillOval(leftEnd - sqrRootOfTwoOverTwo * radius , cumFreqLeft - sqrRootOfTwoOverTwo * radius , diameter, diameter);
                    gc.fillOval(rightEnd - sqrRootOfTwoOverTwo * radius , cumFreqRight - sqrRootOfTwoOverTwo * radius , diameter, diameter);
                }
            }
        }   // end ithBin
        
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
    
    public void constructBinInformation() { 
        binWidth = ithBinHigh - ithBinLow;
        nBinsToLeft = (int)Math.floor((ithBinLow - xMin) / binWidth);
        nBinsToRight = (int)Math.floor((xMax - ithBinHigh) / binWidth);
        
        if (nBinsToLeft < 2) {nBinsToLeft = 2;}
        if (nBinsToRight < 2) {nBinsToRight = 2;}
        
        nBinsTotal = nBinsToLeft + 1 + nBinsToRight;
        leftBinEnd = new double[nBinsTotal];
        rightBinEnd = new double[nBinsTotal];
        minScale = ithBinLow - nBinsToLeft * binWidth;
        
        for (int ithBin = 0; ithBin < nBinsTotal; ithBin++) {
            leftBinEnd[ithBin] = minScale + ithBin * binWidth;
            rightBinEnd[ithBin] = leftBinEnd[ithBin] + binWidth;
        }
        
        frequencies = new double[nBinsTotal];
        cumFrequencies = new double[nBinsTotal];
        
        for (int ithData = 0; ithData < nLegalDataPoints; ithData++) {              
            double ithDataPoint = sortedData[ithData];            
            for (int ithBin = 0; ithBin < nBinsTotal; ithBin++) {
                if ((leftBinEnd[ithBin] <= ithDataPoint) && (ithDataPoint < rightBinEnd[ithBin])) {
                    frequencies[ithBin] = frequencies[ithBin] + 1.0;
                    break;
                }
            }
        }
        
        cumFrequencies[0] = frequencies[0];
        
        for (int ithFreq = 1; ithFreq < nBinsTotal; ithFreq++) {
            cumFrequencies[ithFreq] = cumFrequencies[ithFreq - 1] + frequencies[ithFreq];
            
            if (cumFrequencies[ithFreq] <  cumFrequencies[ithFreq - 1]) {
                cumFrequencies[ithFreq] = cumFrequencies[ithFreq - 1];
            }
        }    
        
        maximumCumFreq = 0.;
        for (int ithFreq = 0; ithFreq < nBinsTotal; ithFreq++) {
            if (cumFrequencies[ithFreq] > maximumCumFreq) {
                maximumCumFreq = cumFrequencies[ithFreq];
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
        
        yMin = 0;
        yMax = 1.05 * maximumCumFreq;
        newY_Lower = yMin;
        newY_Upper = yMax;
        yAxis = new JustAnAxis(yMin, yMax); 
        yAxis.forceLowScaleEndToBe(0.0);
        yAxis.setVisible(true);

        yAxis.setLowerBound(newY_Lower ); 
        yAxis.setUpperBound(newY_Upper );
        yRange = yMax - yMin;      
        deltaY = 0.005 * yRange;
        yAxis.setSide(Side.LEFT);      
    }
    
   public Pane getTheContainingPane() { return theContainingPane; }
}