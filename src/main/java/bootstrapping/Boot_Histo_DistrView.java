/**************************************************
 *              Boot_Histo_DistrView              *
 *                    01/08/25                    *
 *                     15:00                      *
 *************************************************/
package bootstrapping;

import dialogs.Change_Bins_Dialog;
import genericClasses.DragableAnchorPane;
import genericClasses.Point_2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class Boot_Histo_DistrView extends Super_OneStat_DistrView {
    
    // POOs
    //boolean printTheStuff = true;
    boolean printTheStuff = false;
    
    double displayThisMuchIsRed, displayThisMuchIsGreen, binHeight, 
           binFrequency, leftPercentile, rightPercentile;
    
    String strBootedStat, strBootOrRandomize;
    String theTitles[];
    
    //  POJOs / FX   
    Button btn_BinReset;
    HBox hBox_BinReset;   
    Point_2D ithBinLimits;

    public Boot_Histo_DistrView(NonGenericBootstrap_Info nonGen,
            Boot_DistrModel boot_DistrModel,
                        double placeHoriz, double placeVert,
                        double withThisWidth, double withThisHeight) {
        super(nonGen, boot_DistrModel, placeHoriz, placeVert, withThisWidth, withThisHeight); 
        if (printTheStuff) {
            System.out.println("\n51 *** Boot_Histo_DistrView, Constructing");
        }
        initHoriz = placeHoriz; initVert = placeVert;
        initWidth = withThisWidth; initHeight = withThisHeight;
        this.boot_DistrModel = boot_DistrModel;
        ithBinLimits = this.boot_DistrModel.getBinLimits();
        ithBinLow = ithBinLimits.getFirstValue();
        ithBinHigh = ithBinLimits.getSecondValue();
        bootstrap_Dashboard = nonGen.getTheDashboard(); 
        nLegalDataPoints = this.boot_DistrModel.getTheQDV().getLegalN();
        sortedData = new double[nLegalDataPoints];
        sortedData = this.boot_DistrModel.getUCDO().getTheDataSorted();
        strBootedStat = nonGen.getBootedStatText();
        
        theTitles = nonGen.getTheTitles();
        txtTitle1 = new Text(50, 25, theTitles[0]);
        txtTitle2 = new Text (60, 45, theTitles[01]);
        txtTitle1.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,20));
        txtTitle2.setFont(Font.font("Times New Roman", FontWeight.BOLD, FontPosture.REGULAR,15));
    }

    public void continueConstruction() {    
        if (printTheStuff) {
            System.out.println("74 --- Boot_Histo_DistrView, continueConstruction()");
        }
        nonGen.set_Original_Histo_DistrView(this);
        xPrintPosLeft = 0.05;
        xPrintPosCenter = 0.325;
        xPrintPosRight = 0.5;
        yPrintPosLeftRight = 0.9;
        yPrintPosCenter = 0.95;   
        maxOfYScale = 0.45; 

        newX_Lower = nonGen.getOriginalXLower();
        newX_Upper =  nonGen.getOriginalXUpper();
        
        initializeGraphParameters();

        binWidth = ithBinHigh - ithBinLow;

        graphCanvas = new Canvas(600, 600);
        gc = graphCanvas.getGraphicsContext2D();  
        btn_BinReset = new Button("Change bin width");
        minDataRange = boot_DistrModel.getUCDO().getMinValue();
        maxDataRange = boot_DistrModel.getUCDO().getMaxValue();
        
        btn_BinReset.setOnAction(e -> {
            chBins_Dialog = new Change_Bins_Dialog(minDataRange, maxDataRange);
            chBins_Dialog.showAndWait();
            ithBinLow = chBins_Dialog.getDblLeftBin();
            ithBinHigh = chBins_Dialog.getDblRightBin();
            binWidth = ithBinHigh - ithBinLow;
            returnStatus = chBins_Dialog.getReturnStatus();
            chBins_Dialog.close();
            
            if (!returnStatus.equals("Cancel")) {
                constructBinInformation();
                doTheGraph();
            }
        });

        hBox_BinReset = new HBox();
        hBox_BinReset.getChildren().add(btn_BinReset);  
    
        //  There are no check boxes, but superclass constructs a CheckBoxRow
        nCheckBoxes = 0;
        initializing = true;
        oneStat_DialogView = bootstrap_Dashboard.get_Boot_DialogView();
        oneStat_DialogView.getBootstrapOneStat_DialogView();
        oneStat_Controller = bootstrap_Dashboard.get_Boot_Controller();
        tailChoice = "NotEqual";
        respondToChanges();
        graphCanvas = new Canvas(initWidth, initHeight);
        makeTheCheckBoxes();
        makeItHappen();
    }

    public void doTheGraph() {
        shadeLeft = boot_DistrModel.get_ShadeLeft();
        shadeRight = boot_DistrModel.get_ShadeRight();
        
        if (boot_DistrModel.get_TwoTail_IsChecked()) {
            shadeLeft = true;
            shadeRight = true;
        }
        
        leftPercentile = boot_DistrModel.get_LeftPercentile();
        rightPercentile = boot_DistrModel.get_RightPercentile();   
        text1Width = txtTitle1.getLayoutBounds().getWidth();
        text2Width = txtTitle2.getLayoutBounds().getWidth();
        paneWidth = dragableAnchorPane.getWidth();
        txt1Edge = (paneWidth - text1Width) / (2 * paneWidth);
        txt2Edge = (paneWidth - text2Width) / (2 * paneWidth);
        
        tempHeight = dragableAnchorPane.getHeight();
        tempWidth = dragableAnchorPane.getWidth();
         
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
        
        binWidth = leftBinEnd[1] - leftBinEnd[0];
        displayBinWidth = xAxis.getDisplayPosition(leftBinEnd[1]) - xAxis.getDisplayPosition(leftBinEnd[0]);  
    
        for (int ithBin = 0; ithBin < nBinsTotal; ithBin++ ) {
            gc.setFill(Color.GREEN);
            binFrequency = frequencies[ithBin];
            
            if (binFrequency > 0.) { 
                double leftEndOfBin = leftBinEnd[ithBin];
                double rightEndOfBin = rightBinEnd[ithBin];
                double displayLeftEndOfBin = xAxis.getDisplayPosition(leftEndOfBin);
                double displayRightEndOfBin = xAxis.getDisplayPosition(rightEndOfBin);
                double displayLeftPercentile = xAxis.getDisplayPosition(leftPercentile);
                double displayRightPercentile = xAxis.getDisplayPosition(rightPercentile);
 
                bILT = (rightEndOfBin < leftPercentile);
                bSLP = ((leftEndOfBin <= leftPercentile) && (leftPercentile < rightEndOfBin));
                bITM = ((leftPercentile <= leftEndOfBin) && (rightEndOfBin < rightPercentile));
                bSRP = ((leftEndOfBin <= rightPercentile) && (rightPercentile < rightEndOfBin));
                bIRT = (rightPercentile <= leftEndOfBin);
 
                gc.setFill(Color.GREEN);
                //  No tails are selected
                if (!shadeLeft && !shadeRight) { 
                    binHeight = yAxis.getDisplayPosition(0.0) - yAxis.getDisplayPosition(binFrequency);
                    gc.fillRect(displayLeftEndOfBin, yAxis.getDisplayPosition(binFrequency), displayBinWidth, binHeight);                      
                }
                else 
                {
                //  Bin is totally in the left tail
                    if (bILT) {
                        gc.setFill(Color.GREEN);
                        if (shadeLeft) { gc.setFill(Color.RED);}
                        binHeight = yAxis.getDisplayPosition(0.0) - yAxis.getDisplayPosition(binFrequency);
                        gc.fillRect(displayLeftEndOfBin, yAxis.getDisplayPosition(binFrequency), displayBinWidth, binHeight);                       
                    }

                    //  Bin contains the left percentile
                    if (bSLP && shadeLeft) {
                        binHeight = yAxis.getDisplayPosition(0.0) - yAxis.getDisplayPosition(binFrequency);

                        displayThisMuchIsRed = displayLeftPercentile - displayLeftEndOfBin;
                        displayThisMuchIsGreen = displayRightEndOfBin - displayLeftPercentile;    
 
                        gc.setFill(Color.BLACK);
                        gc.fillRect(displayLeftEndOfBin, yAxis.getDisplayPosition(binFrequency), displayThisMuchIsRed, binHeight); 
                        gc.setFill(Color.BLUE);
                        gc.fillRect(displayLeftPercentile, yAxis.getDisplayPosition(binFrequency), displayThisMuchIsGreen, binHeight);
                    }
                    
                    if (bSLP && !shadeLeft) {
                        binHeight = yAxis.getDisplayPosition(0.0) - yAxis.getDisplayPosition(binFrequency);
                        gc.fillRect(displayLeftEndOfBin, yAxis.getDisplayPosition(binFrequency), displayBinWidth, binHeight);   
                    }
                    
                    //  Bin is totally in the middle
                    if (bITM) {
                        gc.setFill(Color.GREEN);
                        binHeight = yAxis.getDisplayPosition(0.0) - yAxis.getDisplayPosition(binFrequency);
                        gc.fillRect(displayLeftEndOfBin, yAxis.getDisplayPosition(binFrequency), displayBinWidth, binHeight); 
                    }

                    //  Bin contains the right percentile
                    if (bSRP && shadeRight) {
                        binHeight = yAxis.getDisplayPosition(0.0) - yAxis.getDisplayPosition(binFrequency);

                        displayThisMuchIsGreen = displayRightPercentile - displayLeftEndOfBin;
                        displayThisMuchIsRed = displayRightEndOfBin - displayRightPercentile;                       
                                               
                        gc.setFill(Color.BLUE);
                        gc.fillRect(displayLeftEndOfBin, yAxis.getDisplayPosition(binFrequency), displayThisMuchIsGreen, binHeight); 
                        gc.setFill(Color.BLACK);
                        gc.fillRect(displayRightPercentile, yAxis.getDisplayPosition(binFrequency), displayThisMuchIsRed, binHeight);                          
                    }
                    
                    if (bSRP && !shadeRight) {
                        binHeight = yAxis.getDisplayPosition(0.0) - yAxis.getDisplayPosition(binFrequency);
                        gc.fillRect(displayLeftEndOfBin, yAxis.getDisplayPosition(binFrequency), displayBinWidth, binHeight);   
                    }
                    
                    //  Bin is totally in the right tail
                    if (bIRT) {
                        gc.setFill(Color.GREEN);
                        if (shadeRight) { gc.setFill(Color.RED);}
                        binHeight = yAxis.getDisplayPosition(0.0) - yAxis.getDisplayPosition(binFrequency);
                        gc.fillRect(displayLeftEndOfBin, yAxis.getDisplayPosition(binFrequency), displayBinWidth, binHeight);                          
                    }
                }
            }   // end else shading somewhere                
        }   // for ithBin   
        
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
        
    }   //  end doThePlot   
    
    public void initializeGraphParameters() {  
        initial_xMin = boot_DistrModel.getTheQDV().getMinValue();
        initial_xMax = boot_DistrModel.getTheQDV().getMaxValue();
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
    
    public void constructBinInformation() { 
        binWidth = ithBinHigh - ithBinLow;
        nBinsToLeft = (int)Math.floor((ithBinLow - xMin) / binWidth);
        nBinsToRight = (int)Math.floor((xMax - ithBinHigh) / binWidth);
        if (nBinsToLeft < 2) {nBinsToLeft = 2;}
        if (nBinsToRight < 2) {nBinsToRight = 2;}
        
        nBinsTotal = nBinsToLeft + 2 + nBinsToRight;

        leftBinEnd = new double[nBinsTotal];
        rightBinEnd = new double[nBinsTotal];
        minScale = ithBinLow - nBinsToLeft * binWidth;
        
        for (int ithBin = 0; ithBin < nBinsTotal; ithBin++) {
            leftBinEnd[ithBin] = minScale + ithBin * binWidth;
            rightBinEnd[ithBin] = leftBinEnd[ithBin] + binWidth;
        }
        
        frequencies = new double[nBinsTotal];
        for (int ithData = 0; ithData < nLegalDataPoints; ithData++) {
            double ithDataPoint = sortedData[ithData];
            for (int jthBin = 0; jthBin < nBinsTotal; jthBin++) {
                if ((leftBinEnd[jthBin] <= ithDataPoint) && (ithDataPoint < rightBinEnd[jthBin])) {
                    frequencies[jthBin] = frequencies[jthBin] + 1.0;
                    break;
                }
            }
        }
        
        maximumFreq = 0.;
        
        for (int ithFreq = 0; ithFreq < nBinsTotal; ithFreq++) {
            if (frequencies[ithFreq] > maximumFreq) {
                maximumFreq = frequencies[ithFreq];
            }
        }
        
        if (yAxis != null) {    //  If changing existing bins
            yAxis.setUpperBound(maximumFreq);
        }
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
    
    public OneStat_Controller get_Bootstrap_Controller() {
        return oneStat_Controller;
    }
    
    public void setBootstrapOneStat_DialogView(BootedStat_DialogView bootstrap_1Stat_DialogView) {
        this.oneStat_DialogView = bootstrap_1Stat_DialogView;
    }
    
    public void setBootStrapOrRandomized(String toThis) {
        strBootOrRandomize = toThis; 
    }

    public void setInitializingToTrue() { initializing = true; }
}
